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
import org.objectweb.asm.Label;

/**
 * A binary comparison operator expression
 */
public class ComparisonExpression extends BooleanExpression
{
    public ComparisonExpression(Rule rule, int oper, ParseNode token, Expression left, Expression right)
    {
        super(rule, oper, token, left, right);
        comparisonType = Type.UNDEFINED;
        comparable = false;
    }

    public Type typeCheck(Type expected) throws TypeException {
        // TODO allow comparison of non-numeric values
        Type type1 = getOperand(0).typeCheck(Type.UNDEFINED);
        Type type2 = getOperand(1).typeCheck(Type.UNDEFINED);
        if (type1.isNumeric() || type2.isNumeric()) {
            comparisonType = Type.promote(type1,  type2);
            comparable = true;
        } else if (type1.isAssignableFrom(type2)) {
            comparisonType = type1;
            comparable = Comparable.class.isAssignableFrom(comparisonType.getTargetClass());
        } else if (type2.isAssignableFrom(type1)) {
            comparisonType = type2;
            comparable = Comparable.class.isAssignableFrom(comparisonType.getTargetClass());
        } else {
            throw new TypeException("ComparisonExpression.typeCheck : incomparable argument types " + type1.getName() + " and " + type2.getName() + " for comparison expression"  + getPos());
        }

        // we have to implement anything other than EQ or NE using Comparable
        
        if (oper != EQ && oper != NE && !comparable) {
            throw new TypeException("ComparisonExpression.typeCheck : cannot compare instances of class " + comparisonType.getName() + getPos());
        }
        
        type = Type.Z;
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("ComparisonExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException
    {
        try {
            if (comparisonType.isNumeric()) {
// n.b. be careful with characters here
                Number value1 = (Number)getOperand(0).interpret(helper);
                Number value2 = (Number)getOperand(1).interpret(helper);
                // type is the result of promoting one or other or both of the operands
                // and they should be converted to this type before doing the compare operation
                if (comparisonType == type.B || comparisonType == type.S || comparisonType == type.I) {
                    int i1 = value1.intValue();
                    int i2 = value2.intValue();
                    boolean result;
                    switch (oper)
                    {
                        case LT:
                            result = (i1 < i2);
                            break;
                        case LE:
                            result = (i1 <= i2);
                            break;
                        case GT:
                            result = (i1 > i2);
                            break;
                        case GE:
                            result = (i1 >= i2);
                            break;
                        case EQ:
                            result = (i1 == i2);
                            break;
                        case NE:
                            result = (i1 != i2);
                            break;
                        default:
                            result = false;
                            break;
                    }
                    return result;
                }  else if (comparisonType == type.J) {
                    long l1 = value1.longValue();
                    long l2 = value2.longValue();
                    boolean result;
                    switch (oper)
                    {
                        case LT:
                            result = (l1 < l2);
                            break;
                        case LE:
                            result = (l1 <= l2);
                            break;
                        case GT:
                            result = (l1 > l2);
                            break;
                        case GE:
                            result = (l1 >= l2);
                            break;
                        case EQ:
                            result = (l1 == l2);
                            break;
                        case NE:
                            result = (l1 != l2);
                            break;
                        default:
                            result = false;
                            break;
                    }
                    return result;
                }  else if (comparisonType == type.F) {
                    float f1 = value1.floatValue();
                    float f2 = value2.floatValue();
                    boolean result;
                    switch (oper)
                    {
                        case LT:
                            result = (f1 < f2);
                            break;
                        case LE:
                            result = (f1 <= f2);
                            break;
                        case GT:
                            result = (f1 > f2);
                            break;
                        case GE:
                            result = (f1 >= f2);
                            break;
                        case EQ:
                            result = (f1 == f2);
                            break;
                        case NE:
                            result = (f1 != f2);
                            break;
                        default:
                            result = false;
                            break;
                    }
                    return result;
                }  else if (comparisonType == type.D) {
                    double d1 = value1.doubleValue();
                    double d2 = value2.doubleValue();
                    boolean result;
                    switch (oper)
                    {
                        case LT:
                            result = (d1 < d2);
                            break;
                        case LE:
                            result = (d1 <= d2);
                            break;
                        case GT:
                            result = (d1 > d2);
                            break;
                        case GE:
                            result = (d1 >= d2);
                            break;
                        case EQ:
                            result = (d1 == d2);
                            break;
                        case NE:
                            result = (d1 != d2);
                            break;
                        default:
                            result = false;
                            break;
                    }
                    return result;
                }  else if (comparisonType == Type.C) {
                    char c1 = (char)value1.intValue();
                    char c2 = (char)value2.intValue();
                    boolean result;
                    switch (oper)
                    {
                        case LT:
                            result = (c1 < c2);
                            break;
                        case LE:
                            result = (c1 <= c2);
                            break;
                        case GT:
                            result = (c1 > c2);
                            break;
                        case GE:
                            result = (c1 >= c2);
                            break;
                        case EQ:
                            result = (c1 == c2);
                            break;
                        case NE:
                            result = (c1 != c2);
                            break;
                        default:
                            result = false;
                            break;
                    }
                    return result;
                }
            }
            // we implement compares via comparable but eq and neq via .equals

            if (comparable) {
                Comparable value1 = (Comparable)getOperand(0).interpret(helper);
                Comparable value2 = (Comparable)getOperand(1).interpret(helper);
                if (value1 == null || value2 == null) {
                    if (oper == EQ) {
                        return value1 == value2;
                    } else if (oper == NE) {
                        return value1 != value2;
                    }
                    return false;
                } else {
                    int cmp;
                    cmp = value1.compareTo(value2);
                    boolean result;
                    switch (oper)
                    {
                        case LT:
                            cmp = value1.compareTo(value2);
                            result = (cmp < 0);
                            break;
                        case LE:
                            cmp = value1.compareTo(value2);
                            result = (cmp <= 0);
                            break;
                        case GT:
                            cmp = value1.compareTo(value2);
                            result = (cmp > 0);
                            break;
                        case GE:
                            cmp = value1.compareTo(value2);
                            result = (cmp >= 0);
                            break;
                        case EQ:
                            result = value1.equals(value2);
                            break;
                        case NE:
                            result = !value1.equals(value2);
                            break;
                        default:
                            result = false;
                            break;
                    }
                    return result;
                }
            } else if (comparisonType == Type.Z || comparisonType == Type.BOOLEAN) {
                // boxed booleans need special treatment
                Boolean value1 = (Boolean)getOperand(0).interpret(helper);
                Boolean value2 = (Boolean)getOperand(1).interpret(helper);
                boolean result;
                if (oper == EQ) {
                    result = value1.equals(value2);
                } else {
                    result = !value1.equals(value2);
                }
                return result;
            } else  {
                Object value1 = getOperand(0).interpret(helper);
                Object value2 = getOperand(1).interpret(helper);
                boolean result;
                if (oper == EQ) {
                    result = (value1 == value2);
                } else {
                    result = (value1 != value2);
                }
                return result;
            }
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("ComparisonExpression.interpret : unexpected exception for operation " + token + getPos() + " in rule " + helper.getName(), e);
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Expression oper0 = getOperand(0);
        Expression oper1 = getOperand(1);

        int removed = 0;

        // evaluate the operands and ensure the reuslt is of the correct type for comparison adds 2
        oper0.compile(mv, compileContext);
        compileTypeConversion(oper0.getType(), comparisonType, mv, compileContext);
        oper1.compile(mv, compileContext);
        compileTypeConversion(oper1.getType(), comparisonType, mv, compileContext);

        // now do the appropriate type of comparison
        if (comparisonType == type.B || comparisonType == type.S || comparisonType == type.S || comparisonType == type.I) {
            Label elsetarget = new Label();
            Label endtarget = new Label();
            // we remove 2 words from the stack and then add 1 back
            removed = 2;
            switch (oper)
            {
                case LT:
                    mv.visitJumpInsn(Opcodes.IF_ICMPGE, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case LE:
                    mv.visitJumpInsn(Opcodes.IF_ICMPGT, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case GT:
                    mv.visitJumpInsn(Opcodes.IF_ICMPLE, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case GE:
                    mv.visitJumpInsn(Opcodes.IF_ICMPLT, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case EQ:
                    mv.visitJumpInsn(Opcodes.IF_ICMPNE, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case NE:
                    mv.visitJumpInsn(Opcodes.IF_ICMPEQ, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
            }
        } else if (comparisonType == type.J || comparisonType == type.F || comparisonType == type.D) {
            if (comparisonType == type.J) {
                mv.visitInsn(Opcodes.LCMP);
                // we remove four words from the stack and add 1 back
                removed = 4;
            } else if (comparisonType == type.F) {
                // we remove two words from the stack and add 1 back
                removed = 2;
                mv.visitInsn(Opcodes.FCMPG);
            } else if (comparisonType == type.D) {
                // we remove four words from the stack and add 1 back
                removed = 4;
                mv.visitInsn(Opcodes.DCMPG);
            }
            Label elsetarget = new Label();
            Label endtarget = new Label();
            switch (oper)
            {
                case LT:
                    mv.visitJumpInsn(Opcodes.IFGE, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case LE:
                    mv.visitJumpInsn(Opcodes.IFGT, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case GT:
                    mv.visitJumpInsn(Opcodes.IFLE, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case GE:
                    mv.visitJumpInsn(Opcodes.IFLT, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case EQ:
                    mv.visitJumpInsn(Opcodes.IFNE, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case NE:
                    mv.visitJumpInsn(Opcodes.IFEQ, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
            }
        } else if (comparable) {
            // we add a further two words setting up the relevant test then remove them
            // and also remove the original two words replacing them with a single word
            removed = 4;
            compileContext.addStackCount(2);
            // we need to deal with null values correctly
            // if op1 == null || op2 == null
            // then
            //   EQ:
            //   push value1 == value2
            //   NE:
            //   push value1 != value2
            //   ow:
            //   push false
            // else
            //   execute compareTo or equals and test for the desired outcome
            // end if
            Label splittarget = new Label(); // else
            Label jointarget = new Label(); // end if
            mv.visitInsn(Opcodes.DUP2); // [... op1, op2 ] ==> [... op1, op2, op1,  op2]
            mv.visitInsn(Opcodes.POP); // [... op1, op2, op1, op2 ] ==> [... op1, op2, op1]
            // if op1 == null
            mv.visitJumpInsn(Opcodes.IFNULL, splittarget); // [... op1, op2, op1] ==> [... op1, op2]
            mv.visitInsn(Opcodes.DUP); // [... op1, op2 ] ==> [... op1, op2, op2]
            // || op2 == null
            mv.visitJumpInsn(Opcodes.IFNULL, splittarget); // [... op1, op2, op2] ==> [... op1, op2]
            // so, it is ok to call compareTo leaving an int or equals leaving a boolean
            if (oper != EQ && oper != NE) {
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/Comparable", "compareTo", "(Ljava/lang/Object;)I");
            } else {
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");
            }
            // now if we did a compareTo we need to generate the required boolean
            Label elsetarget = new Label();
            Label endtarget = new Label();
            // if needed the convert the compareTo result to the required boolean outcome
            switch (oper)
            {
                case LT:
                    mv.visitJumpInsn(Opcodes.IFGE, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case LE:
                    mv.visitJumpInsn(Opcodes.IFGT, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case GT:
                    mv.visitJumpInsn(Opcodes.IFLE, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case GE:
                    mv.visitJumpInsn(Opcodes.IFLT, elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitLabel(endtarget);
                    break;
                case NE:
                    mv.visitJumpInsn(Opcodes.IFEQ, elsetarget);
                    mv.visitLdcInsn(false);
                    mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                    mv.visitLabel(elsetarget);
                    mv.visitLdcInsn(true);
                    mv.visitLabel(endtarget);
                    break;
            }
            // skip to the join point
            mv.visitJumpInsn(Opcodes.GOTO, jointarget);
            // label the split point
            mv.visitLabel(splittarget);
            if (oper == EQ) {
                elsetarget = new Label();
                endtarget = new Label();
                mv.visitJumpInsn(Opcodes.IF_ACMPEQ, elsetarget);
                mv.visitLdcInsn(false);
                mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                mv.visitLabel(elsetarget);
                mv.visitLdcInsn(true);
                mv.visitLabel(endtarget);
            } else if (oper == NE) {
                elsetarget = new Label();
                endtarget = new Label();
                mv.visitJumpInsn(Opcodes.IF_ACMPNE, elsetarget);
                mv.visitLdcInsn(false);
                mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                mv.visitLabel(elsetarget);
                mv.visitLdcInsn(true);
                mv.visitLabel(endtarget);
            } else {
                // pop the operands and stack false
                mv.visitInsn(Opcodes.POP2);
                mv.visitLdcInsn(false);
            }
            // label the join point
            mv.visitLabel(jointarget);
        } else if (comparisonType == Type.Z) {
            // unboxed booleans need special treatment
            // we remove two words replacing them with a single word
            removed = 2;
            Label elsetarget = new Label();
            Label endtarget = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, elsetarget);
            // on this branch for EQ the stacked value is what we need and for NE
            // the stacked value needs flipping
            if (oper == NE) {
                Label elsetarget2 = new Label();
                mv.visitJumpInsn(Opcodes.IFEQ, elsetarget2);
                mv.visitLdcInsn(false);
                mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                mv.visitLabel(elsetarget2);
                mv.visitLdcInsn(true);
            }
            mv.visitJumpInsn(Opcodes.GOTO, endtarget);
            mv.visitLabel(elsetarget);
            // on this branch for NE the stacked value is what we need and for EQ
            // the stacked value needs flipping
            if (oper == EQ) {
                Label elsetarget2 = new Label();
                mv.visitJumpInsn(Opcodes.IFEQ, elsetarget2);
                mv.visitLdcInsn(false);
                mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                mv.visitLabel(elsetarget2);
                mv.visitLdcInsn(true);
            }
            mv.visitLabel(endtarget);

        } else if (comparisonType == Type.BOOLEAN) {
            // boxed booleans need special treatment
            // we remove two words replacing them with a single word
            removed = 2;
            Label elsetarget = new Label();
            Label endtarget = new Label();
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java.lang.Boolean", "equals", "(Ljava/lang/Boolean;)Z");
            if (oper == NE) {
                mv.visitJumpInsn(Opcodes.IFEQ, elsetarget);
                mv.visitLdcInsn(true);
                mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                mv.visitLabel(elsetarget);
                mv.visitLdcInsn(false);
                mv.visitLabel(endtarget);
            }
        } else {
            // we remove two words replacing them with a single word
            removed = 2;
            Label elsetarget = new Label();
            Label endtarget = new Label();
            if (oper == EQ) {
                mv.visitJumpInsn(Opcodes.IF_ACMPNE, elsetarget);
                mv.visitLdcInsn(true);
                mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                mv.visitLabel(elsetarget);
                mv.visitLdcInsn(false);
                mv.visitLabel(endtarget);
            } else {
                mv.visitJumpInsn(Opcodes.IF_ACMPEQ, elsetarget);
                mv.visitLdcInsn(true);
                mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                mv.visitLabel(elsetarget);
                mv.visitLdcInsn(false);
                mv.visitLabel(endtarget);
            }
        }
        compileContext.addStackCount(1 - removed);
    }

    private Type comparisonType;
    private boolean comparable;
}