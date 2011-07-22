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
 */
public class TwiddleExpression extends UnaryOperExpression
{
    public TwiddleExpression(Rule rule, ParseNode token, Expression operand)
    {
        super(rule, TWIDDLE, operand.getType(), token, operand);
    }

    public Type typeCheck(Type expected)
    throws TypeException {
        type = getOperand(0).typeCheck(Type.N);
        if (type == Type.F || type == Type.D) {
            type = Type.J;
        }
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("TwiddleExpression.typeCheck() : invalid result type : " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        try {
            Number value = (Number)getOperand(0).interpret(helper);

            if (type == Type.B) {
                return (byte)~value.intValue();
            } else if (type == Type.S) {
                return (short)~value.intValue();
            } else if (type == Type.I) {
                return ~value.intValue();
            } else if (type == Type.J) {
                return ~value.longValue();
            } else { // (type == Type.C)
                return ~value.intValue();
            }
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("TwiddleExpression.typeCheck() : unexpected exception : " + token.getText() + getPos(), e);
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        // compile the operand and then bit twiddle it
        Expression oper = getOperand(0);
        Type operType = oper.getType();

        int currentStack = compileContext.getStackCount();
        int expected = 0;

        oper.compile(mv, compileContext);
        compileContext.addStackCount((operType.getNBytes() > 4 ? 2 : 1));
        compileTypeConversion(operType, type, mv, compileContext);
        if (type == Type.B) {
            expected = 1;
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IXOR);
            mv.visitInsn(Opcodes.I2B);
        } else if (type == Type.S) {
            expected = 1;
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IXOR);
            mv.visitInsn(Opcodes.I2S);
        } else if (type == Type.C) {
            expected = 1;
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IXOR);
            mv.visitInsn(Opcodes.I2C);
        } else if (type == Type.I) {
            expected = 1;
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IXOR);
        } else if (type == Type.J) {
            expected = 2;
            mv.visitInsn(Opcodes.LCONST_1);
            mv.visitInsn(Opcodes.LXOR);
        }

        // check the stack height is what we expect
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("MinusExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack + expected);
        }
    }
}
