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
import org.objectweb.asm.Label;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;

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

        if (oper !=  EQ && oper != NE && !comparable) {
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
                }  else { // comparisonType == Type.C
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
            } else if (comparable) {
                Comparable value1 = (Comparable)getOperand(0).interpret(helper);
                Comparable value2 = (Comparable)getOperand(1).interpret(helper);
                int cmp = value1.compareTo(value2);
                boolean result;
                switch (oper)
                {
                    case LT:
                        result = (cmp < 0);
                        break;
                    case LE:
                        result = (cmp <= 0);
                        break;
                    case GT:
                        result = (cmp > 0);
                        break;
                    case GE:
                        result = (cmp >= 0);
                        break;
                    case EQ:
                        result = (cmp == 0);
                        break;
                    case NE:
                        result = (cmp != 0);
                        break;
                    default:
                        result = false;
                        break;
                }
                return result;
            } else  {
                Object value1 = getOperand(0).interpret(helper);
                Object value2 = getOperand(1).interpret(helper);
                boolean result;
                if (oper == EQ) {
                    result = (value1 == value2);
                } else {
                    result = (value1 !=  value2);
                }
                return result;
            }
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("ComparisonExpression.interpret : unexpected exception for operation " + token + getPos() + " in rule " + helper.getName(), e);
        }
    }

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        Expression oper0 = getOperand(0);
        Expression oper1 = getOperand(1);

        int currentStack = currentStackHeights.stackCount;
        int max = 0;

        // evaluate the operands and ensure the reuslt is of the correct type for comparison adds 2
        oper0.compile(mv, currentStackHeights, maxStackHeights);
        compileTypeConversion(oper0.getType(), comparisonType, mv, currentStackHeights, maxStackHeights);
        oper1.compile(mv, currentStackHeights, maxStackHeights);
        compileTypeConversion(oper1.getType(), comparisonType, mv, currentStackHeights, maxStackHeights);

        // now do the appropriate type of comparison
        if (comparisonType.isNumeric()) {
            if (comparisonType == type.B || comparisonType == type.S || comparisonType == type.S || comparisonType == type.I) {
                Label elsetarget = new Label();
                Label endtarget = new Label();
                // we needed to stack two words for the operands
                max = 2;
                switch (oper)
                {
                    case LT:
                        mv.visitJumpInsn(Opcodes.IF_ICMPGE, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case LE:
                        mv.visitJumpInsn(Opcodes.IF_ICMPGT, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case GT:
                        mv.visitJumpInsn(Opcodes.IF_ICMPLE, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case GE:
                        mv.visitJumpInsn(Opcodes.IF_ICMPLT, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case EQ:
                        mv.visitJumpInsn(Opcodes.IF_ICMPNE, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case NE:
                        mv.visitJumpInsn(Opcodes.IF_ICMPEQ, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                }
            } else if (comparisonType == type.J || comparisonType == type.F || comparisonType == type.D || comparable) {
                if (comparisonType == type.J) {
                    mv.visitInsn(Opcodes.LCMP);
                    // we needed to stack four words for the operands
                    max = 4;
                } else if (comparisonType == type.F) {
                    // we needed to stack two words for the operands
                    max = 2;
                    mv.visitInsn(Opcodes.FCMPG);
                } else if (comparisonType == type.D) {
                    // we needed to stack four words for the operands
                    max = 4;
                    mv.visitInsn(Opcodes.DCMPG);
                } else {
                    // we needed to stack two words for the operands
                    max = 2;
                    // comparable -- call compare to to leave an int
                    mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/Comparable", "compareTo", "(Ljava/lang/Object;)I");
                }
                Label elsetarget = new Label();
                Label endtarget = new Label();
                switch (oper)
                {
                    case LT:
                        mv.visitJumpInsn(Opcodes.IFGE, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case LE:
                        mv.visitJumpInsn(Opcodes.IFGT, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case GT:
                        mv.visitJumpInsn(Opcodes.IFLE, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case GE:
                        mv.visitJumpInsn(Opcodes.IFLT, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case EQ:
                        mv.visitJumpInsn(Opcodes.IFNE, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                    case NE:
                        mv.visitJumpInsn(Opcodes.IFEQ, elsetarget);
                        mv.visitLdcInsn(true); // or should we BIPUSH 1
                        mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                        mv.visitLabel(elsetarget);
                        mv.visitLdcInsn(false);
                        mv.visitLabel(endtarget);
                        break;
                }
            }
        } else {
            // we needed to stack two words for the operands
            max = 2;
            Label elsetarget = new Label();
            Label endtarget = new Label();
            if (oper == EQ) {
                mv.visitJumpInsn(Opcodes.IF_ACMPNE, elsetarget);
                mv.visitLdcInsn(true); // or should we BIPUSH 1
                mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                mv.visitLabel(elsetarget);
                mv.visitLdcInsn(false);
                mv.visitLabel(endtarget);
            } else {
                mv.visitJumpInsn(Opcodes.IF_ACMPEQ, elsetarget);
                mv.visitLdcInsn(true); // or should we BIPUSH 1
                mv.visitJumpInsn(Opcodes.GOTO, endtarget);
                mv.visitLabel(elsetarget);
                mv.visitLdcInsn(false);
                mv.visitLabel(endtarget);
            }
        }
        // we remove max words and add a single boolean to the stack
        currentStackHeights.addStackCount(1 - max);
        // no need to check the max heights as we stacked one or two words per operand and
        // the compile and compileConvert calls will have already bumped the maximum stack
    }

    private Type comparisonType;
    private boolean comparable;
}