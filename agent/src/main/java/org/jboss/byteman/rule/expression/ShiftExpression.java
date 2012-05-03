/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat and individual contributors
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
package org.jboss.byteman.rule.expression;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A binary arithmetic operator expression
 */
public class ShiftExpression extends BinaryOperExpression
{
    public ShiftExpression(Rule rule, int oper, ParseNode token, Expression left, Expression right)
            throws TypeException
    {
        // n.b. left and right must be of integral type but need not be the same type

        super(rule, oper, left.getType(), token, left, right);
    }

    public Type typeCheck(Type expected) throws TypeException {
        Type type1 = getOperand(0).typeCheck(Type.N);
        Type type2 = getOperand(1).typeCheck(Type.N);
        type = type1;
        // if the type of operand1 is float or double we will convert it to long and
        // generate a long result so correct the promotion here
        if (type.isFloating()) {
            type = type.J;
        } else if (type1 == type.C || type2 == type.C) {
            throw new TypeException("ShiftExpression.typeCheck : invalid operand type java.lang.character " + getPos());
        }
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("ShiftExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        try {
// n.b. be careful with characters here
            Number value1 = (Number)getOperand(0).interpret(helper);
            Number value2 = (Number)getOperand(1).interpret(helper);
            int shift = value2.intValue();
            // type is the result of promoting one or other or both of the operands
            // and they should be converted to this type before doing the arithmetic operation
            if (type == type.B) {
                byte b1 = value1.byteValue();
                byte result;
                // TODO we should probably only respect the byte, short and char types for + and -
                // TODO also need to decide how to handle divide by zero

                switch (oper)
                {
                    case URSH:
                        result = (byte)(b1 >>> shift);
                        break;
                    case RSH:
                        result = (byte)(b1 >> shift);
                        break;
                    case LSH:
                        result = (byte)(b1 << shift);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Byte(result);
            } else if (type == type.S) {
                short s1 = value1.shortValue();
                short result;
                switch (oper)
                {
                    case URSH:
                        result = (short)(s1 >>> shift);
                        break;
                    case RSH:
                        result = (short)(s1 >> shift);
                        break;
                    case LSH:
                        result = (short)(s1 << shift);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Short(result);
            }  else if (type == type.I) {
                int i1 = value1.intValue();
                int result;
                switch (oper)
                {
                    case URSH:
                        result = (i1 >>> shift);
                        break;
                    case RSH:
                        result = (i1 >> shift);
                        break;
                    case LSH:
                        result = (i1 << shift);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Integer(result);
            }  else { // if (type == type.J) {
                long l1 = value1.longValue();
                long result;
                switch (oper)
                {
                    case URSH:
                        result = (l1 >>> shift);
                        break;
                    case RSH:
                        result = (l1 >> shift);
                        break;
                    case LSH:
                        result = (l1 << shift);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Long(result);
            }
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("ShiftExpression.interpret : unexpected exception for operation " + token + getPos() + " in rule " + helper.getName(), e);
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        int currentStack = compileContext.getStackCount();
        int expected = 0;
        Expression oper0 = getOperand(0);
        Expression oper1 = getOperand(1);
        // compile the operands and make sure the first result is our target type and the second is int
        oper0.compile(mv, compileContext);
        compileTypeConversion(oper0.getType(), type, mv, compileContext);
        oper1.compile(mv, compileContext);
        compileTypeConversion(oper1.getType(), Type.I, mv, compileContext);

        if (type == Type.B || type == Type.S || type == Type.I) {
            switch (oper)
            {
                case LSH:
                    mv.visitInsn(Opcodes.ISHL);
                    break;
                case RSH:
                    if (type == Type.C) {
                        mv.visitInsn(Opcodes.IUSHR);
                    } else {
                        mv.visitInsn(Opcodes.ISHR);
                    }
                    break;
                case URSH:
                    mv.visitInsn(Opcodes.IUSHR);
                    break;
            }
            if (type ==  Type.B) {
                mv.visitInsn(Opcodes.I2B);
            } else if (type == Type.S) {
                mv.visitInsn(Opcodes.I2S);
            } else if (type == Type.C) {
                mv.visitInsn(Opcodes.I2C);
            }
            // ok, we popped two words but added one
            compileContext.addStackCount(-1);
            expected =  1;
        } else if (type == Type.J) {
            switch (oper)
            {
                case LSH:
                    mv.visitInsn(Opcodes.LSHL);
                    break;
                case RSH:
                    mv.visitInsn(Opcodes.LSHR);
                    break;
                case URSH:
                    mv.visitInsn(Opcodes.LUSHR);
                    break;
            }
            // ok, we popped three words but added two
            compileContext.addStackCount(-1);
            expected =  2;
        }
        // we have either a 1 words or a 2 words result
        // check that the stack height is what we expect

        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("ShiftExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack + expected);
        }
    }
}