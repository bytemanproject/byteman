/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
* @authors Andrew Dinn
*/
package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.exception.CompileException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.compiler.StackHeights;
import org.jboss.jbossts.orchestration.rule.helper.HelperAdapter;
import org.jboss.jbossts.orchestration.rule.grammar.ParseNode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A binary arithmetic operator expression
 */
public class BitExpression extends BinaryOperExpression
{
    public BitExpression(Rule rule, int oper, ParseNode token, Expression left, Expression right)
    {
        // n.b. left and right must be of integral type

        super(rule, oper, Type.promote(left.getType(), right.getType()), token, left, right);
    }

    public Type typeCheck(Type expected) throws TypeException {
        Type type1 = getOperand(0).typeCheck(Type.N);
        Type type2 = getOperand(1).typeCheck(Type.N);
        type = Type.promote(type1, type2);
        // if either arg is float or double we will convert it to long and generate a long
        // result so correct the promotion here
        if (type.isFloating()) {
            type = type.J;
        }
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("BitExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        try {
// n.b. be careful with characters here
            Number value1 = (Number)getOperand(0).interpret(helper);
            Number value2 = (Number)getOperand(1).interpret(helper);
            // type is the result of promoting one or other or both of the operands
            // and they should be converted to this type before doing the arithmetic operation
            if (type == type.B) {
                byte b1 = value1.byteValue();
                byte b2 = value2.byteValue();
                byte result;
                // TODO we should probably only respect the byte, short and char types for + and -
                // TODO also need to decide how to handle divide by zero

                switch (oper)
                {
                    case BOR:
                        result = (byte)(b1 | b2);
                        break;
                    case BAND:
                        result = (byte)(b1 & b2);
                        break;
                    case BXOR:
                        result = (byte)(b1 ^ b2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Byte(result);
            } else if (type == type.S) {
                short s1 = value1.shortValue();
                short s2 = value2.shortValue();
                short result;
                switch (oper)
                {
                    case BOR:
                        result = (short)(s1 | s2);
                        break;
                    case BAND:
                        result = (short)(s1 & s2);
                        break;
                    case BXOR:
                        result = (short)(s1 ^ s2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Short(result);
            }  else if (type == type.I) {
                int i1 = value1.intValue();
                int i2 = value2.intValue();
                int result;
                switch (oper)
                {
                    case BOR:
                        result = (i1 | i2);
                        break;
                    case BAND:
                        result = (i1 & i2);
                        break;
                    case BXOR:
                        result = (i1 ^ i2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Integer(result);
            }  else if (type == type.J || type == type.F || type == type.D) {
                long l1 = value1.longValue();
                long l2 = value2.longValue();
                long result;
                switch (oper)
                {
                    case BOR:
                        result = (l1 | l2);
                        break;
                    case BAND:
                        result = (l1 & l2);
                        break;
                    case BXOR:
                        result = (l1 ^ l2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Long(result);
            }  else { // (type == type.C)
                // use integers here but be careful about conversions
                int s1 = value1.intValue();
                int s2 = value2.intValue();
                char result;
                switch (oper)
                {
                    case BOR:
                        result = (char)(s1 | s2);
                        break;
                    case BAND:
                        result = (char)(s1 & s2);
                        break;
                    case BXOR:
                        result = (char)(s1 ^ s2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Integer(result);
            }
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("BitExpression.interpret : unexpected exception for operation " + token + getPos() + " in rule " + helper.getName(), e);
        }
    }

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        int currentStack = currentStackHeights.stackCount;
        int expected = 0;
        Expression oper0 = getOperand(0);
        Expression oper1 = getOperand(1);
        // compile the operands and make sure the result is our target type
        oper0.compile(mv, currentStackHeights, maxStackHeights);
        compileTypeConversion(oper0.getType(), type, mv, currentStackHeights, maxStackHeights);
        oper1.compile(mv, currentStackHeights, maxStackHeights);
        compileTypeConversion(oper1.getType(), type, mv, currentStackHeights, maxStackHeights);

        if (type == Type.B || type == Type.S || type == Type.C || type == Type.I) {
            switch (oper)
            {
                case BOR:
                    mv.visitInsn(Opcodes.IOR);
                    break;
                case BAND:
                    mv.visitInsn(Opcodes.IAND);
                    break;
                case BXOR:
                    mv.visitInsn(Opcodes.IXOR);
                    break;
            }
            if (type ==  Type.B) {
                mv.visitInsn(Opcodes.I2B);
            } else if (type == Type.S) {
                mv.visitInsn(Opcodes.I2S);
            } else if (type == Type.C) {
                mv.visitInsn(Opcodes.I2C);
            }
            expected =  1;
        } else if (type == Type.J) {
            switch (oper)
            {
                case BOR:
                    mv.visitInsn(Opcodes.LOR);
                    break;
                case BAND:
                    mv.visitInsn(Opcodes.LAND);
                    break;
                case BXOR:
                    mv.visitInsn(Opcodes.LXOR);
                    break;
            }
            expected =  2;
        } else if (type == Type.F) {
            mv.visitInsn(Opcodes.F2L);
            switch (oper)
            {
                case BOR:
                    mv.visitInsn(Opcodes.LOR);
                    break;
                case BAND:
                    mv.visitInsn(Opcodes.LAND);
                    break;
                case BXOR:
                    mv.visitInsn(Opcodes.LXOR);
                    break;
            }
            expected =  2;
        } else if (type == Type.D) {
            mv.visitInsn(Opcodes.D2L);
            switch (oper)
            {
                case BOR:
                    mv.visitInsn(Opcodes.LOR);
                    break;
                case BAND:
                    mv.visitInsn(Opcodes.LAND);
                    break;
                case BXOR:
                    mv.visitInsn(Opcodes.LXOR);
                    break;
            }
            expected =  2;
        }
        // we have either a 1 byte or a 2 byte result
        // check that the stack height is what we expect

        currentStackHeights.addStackCount(expected);
        
        if (currentStackHeights.stackCount != currentStack + expected) {
            throw new CompileException("BitExpression.compile : invalid stack height " + currentStackHeights.stackCount + " expecting " + currentStack + expected);
        }

        // we needed room for 2 * expected stack values at the highest point
        int maxStack = maxStackHeights.stackCount;
        int overflow = (currentStack + (2 * expected)) - maxStack;

        if (overflow > 0) {
            maxStackHeights.addStackCount(overflow);
        }
    }
}