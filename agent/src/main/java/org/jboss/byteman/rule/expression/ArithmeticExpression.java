/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
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

import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A binary arithmetic operator expression
 */
public class ArithmeticExpression extends BinaryOperExpression
{
    public ArithmeticExpression(Rule rule, int oper, ParseNode token, Expression left, Expression right)
            throws TypeException
    {
        super(rule, oper, Type.promote(left.getType(), right.getType()), token, left, right);
    }

    public Type typeCheck(Type expected) throws TypeException {
        Type type1 = getOperand(0).typeCheck(Type.N);
        Type type2 = getOperand(1).typeCheck(Type.N);
        type = Type.promote(type1, type2);
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type) ) {
            throw new TypeException("ArithmenticExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(HelperAdapter helper)
            throws ExecuteException
    {
        try {
// n.b. be careful with characters here
            Object objValue1 = getOperand(0).interpret(helper);
            Object objValue2 = getOperand(1).interpret(helper);
            Number value1;
            Number value2;
            if (objValue1 instanceof Character) {
                value1 = new Integer((Character)objValue1);
            } else {
                value1 = (Number)objValue1;
            }
            if (objValue2 instanceof Character) {
                value2 = new Integer((Character)objValue2);
            } else {
                value2 = (Number)objValue2;
            }
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
                    case MUL:
                        result = (byte)(b1 * b2);
                        break;
                    case DIV:
                        result = (byte)(b1 / b2);
                        break;
                    case PLUS:
                        result = (byte)(b1 + b2);
                        break;
                    case MINUS:
                        result = (byte)(b1 - b2);
                        break;
                    case MOD:
                        result = (byte)(b1 % b2);
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
                    case MUL:
                        result = (short)(s1 * s2);
                        break;
                    case DIV:
                        result = (short)(s1 / s2);
                        break;
                    case PLUS:
                        result = (short)(s1 + s2);
                        break;
                    case MINUS:
                        result = (short)(s1 - s2);
                        break;
                    case MOD:
                        result = (short)(s1 % s2);
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
                    case MUL:
                        result = (i1 * i2);
                        break;
                    case DIV:
                        result = (i1 / i2);
                        break;
                    case PLUS:
                        result = (i1 + i2);
                        break;
                    case MINUS:
                        result = (i1 - i2);
                        break;
                    case MOD:
                        result = (i1 % i2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Integer(result);
            }  else if (type == type.J) {
                long l1 = value1.longValue();
                long l2 = value2.longValue();
                long result;
                switch (oper)
                {
                    case MUL:
                        result = (l1 * l2);
                        break;
                    case DIV:
                        result = (l1 / l2);
                        break;
                    case PLUS:
                        result = (l1 + l2);
                        break;
                    case MINUS:
                        result = (l1 - l2);
                        break;
                    case MOD:
                        result = (l1 % l2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Long(result);
            }  else if (type == type.F) {
                float f1 = value1.floatValue();
                float f2 = value2.floatValue();
                float result;
                switch (oper)
                {
                    case MUL:
                        result = (f1 * f2);
                        break;
                    case DIV:
                        result = (f1 / f2);
                        break;
                    case PLUS:
                        result = (f1 + f2);
                        break;
                    case MINUS:
                        result = (f1 - f2);
                        break;
                    case MOD:
                        result = (f1 % f2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Float(result);
            }  else if (type == type.D) {
                double d1 = value1.doubleValue();
                double d2 = value2.doubleValue();
                double result;
                switch (oper)
                {
                    case MUL:
                        result = (d1 * d2);
                        break;
                    case DIV:
                        result = (d1 / d2);
                        break;
                    case PLUS:
                        result = (d1 + d2);
                        break;
                    case MINUS:
                        result = (d1 - d2);
                        break;
                    case MOD:
                        result = (d1 % d2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Double(result);
            }  else { // (type == type.C)
                // use integers here but be careful about conversions
                int s1 = value1.intValue();
                int s2 = value2.intValue();
                char result;
                switch (oper)
                {
                    case MUL:
                        result = (char)(s1 * s2);
                        break;
                    case DIV:
                        result = (char)(s1 / s2);
                        break;
                    case PLUS:
                        result = (char)(s1 + s2);
                        break;
                    case MINUS:
                        result = (char)(s1 - s2);
                        break;
                    case MOD:
                        result = (char)(s1 % s2);
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
            throw new ExecuteException("ArithmeticExpression.interpret : unexpected exception for operation " + token + getPos() + " in rule " + helper.getName(), e);
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        int currentStack = compileContext.getStackCount();
        int expectedStack = 0;
        Expression operand0 = getOperand(0);
        Expression operand1 = getOperand(1);
        Type type0 = operand0.getType();
        Type type1 = operand1.getType();
        // compile lhs -- it adds 1 or 2 to the stack height
        operand0.compile(mv, compileContext);
        // do any required type conversion
        compileTypeConversion(type0, type, mv, compileContext);
        // compile rhs -- it adds 1 or 2 to the stack height
        operand1.compile(mv, compileContext);
        // do any required type conversion
        compileTypeConversion(type1, type, mv, compileContext);

        try {
// n.b. be careful with characters here
            if (type == type.B || type == type.S || type == type.C || type == type.I) {
                // TODO we should probably only respect the byte, short and char types for + and -
                // TODO also need to decide how to handle divide by zero

                expectedStack = 1;

                switch (oper)
                {
                    case MUL:
                        mv.visitInsn(Opcodes.IMUL);
                        break;
                    case DIV:
                        mv.visitInsn(Opcodes.IDIV);
                        break;
                    case PLUS:
                        mv.visitInsn(Opcodes.IADD);
                        break;
                    case MINUS:
                        mv.visitInsn(Opcodes.ISUB);
                        break;
                    case MOD:
                        mv.visitInsn(Opcodes.IREM);
                        break;
                    default:
                        // should never happen
                        throw new CompileException("ArithmeticExpression.compile : unexpected operator " + oper);
                }
                // now coerce back to appropriate type
                if (type == type.B) {
                    mv.visitInsn(Opcodes.I2B);
                } else if (type == type.S) {
                    mv.visitInsn(Opcodes.I2S);
                } else if (type == type.C) {
                    mv.visitInsn(Opcodes.I2C);
                } // else if (type == type.I) { do nothing }
                // ok, we popped two bytes but added one
                compileContext.addStackCount(-1);
            }  else if (type == type.J) {

                expectedStack = 2;

                switch (oper)
                {
                    case MUL:
                        mv.visitInsn(Opcodes.LMUL);
                        break;
                    case DIV:
                        mv.visitInsn(Opcodes.LDIV);
                        break;
                    case PLUS:
                        mv.visitInsn(Opcodes.LADD);
                        break;
                    case MINUS:
                        mv.visitInsn(Opcodes.LSUB);
                        break;
                    case MOD:
                        mv.visitInsn(Opcodes.LREM);
                        break;
                    default:
                        // should never happen
                        throw new CompileException("ArithmeticExpression.compile : unexpected operator " + oper);
                }
                // ok, we popped four bytes but added two
                compileContext.addStackCount(-2);
            }  else if (type == type.F) {

                expectedStack = 1;

                switch (oper)
                {
                    case MUL:
                        mv.visitInsn(Opcodes.FMUL);
                        break;
                    case DIV:
                        mv.visitInsn(Opcodes.FDIV);
                        break;
                    case PLUS:
                        mv.visitInsn(Opcodes.FADD);
                        break;
                    case MINUS:
                        mv.visitInsn(Opcodes.FSUB);
                        break;
                    case MOD:
                        mv.visitInsn(Opcodes.FREM);
                        break;
                    default:
                        // should never happen
                        throw new CompileException("ArithmeticExpression.compile : unexpected operator " + oper);
                }
                // ok, we popped two bytes but added one
                compileContext.addStackCount(-1);
            }  else if (type == type.D) {

                expectedStack = 2;

                switch (oper)
                {
                    case MUL:
                        mv.visitInsn(Opcodes.DMUL);
                        break;
                    case DIV:
                        mv.visitInsn(Opcodes.DDIV);
                        break;
                    case PLUS:
                        mv.visitInsn(Opcodes.DADD);
                        break;
                    case MINUS:
                        mv.visitInsn(Opcodes.DSUB);
                        break;
                    case MOD:
                        mv.visitInsn(Opcodes.DREM);
                        break;
                    default:
                        // should never happen
                        throw new CompileException("ArithmeticExpression.compile : unexpected operator " + oper);
                }
                // ok, we popped four bytes but added two
                compileContext.addStackCount(-2);
            } else {
                throw new CompileException("ArithmeticExpression.compile : unexpected result type " + type.getName());
            }
        } catch (CompileException e) {
            throw e;
        } catch (Exception e) {
            throw new CompileException("ArithmeticExpression.compile : unexpected exception for operation " + token + getPos() + " in rule " + rule.getName(), e);
        }

        // check stack heights
        if (compileContext.getStackCount() != currentStack + expectedStack) {
            throw new CompileException("ArithmeticExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expectedStack));
        }
    }

}
