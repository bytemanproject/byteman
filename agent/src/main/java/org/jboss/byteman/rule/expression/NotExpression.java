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
public class NotExpression extends UnaryOperExpression
{
    public NotExpression(Rule rule, ParseNode token, Expression operand)
    {
        super(rule, NOT, Type.BOOLEAN, token, operand);
    }

    public Type typeCheck(Type expected)
    throws TypeException {
        type = getOperand(0).typeCheck(Type.Z);
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("NotExpression.typeCheck() : invalid result type : " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        Boolean result = (Boolean) getOperand(0).interpret(helper);
        return !result;
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Expression oper = getOperand(0);
        Type operType = oper.getType();

        int currentStack = compileContext.getStackCount();
        int expected = 1;

        // compile code to execute the operand -- adds 1
        oper.compile(mv, compileContext);
        compileTypeConversion(operType, type, mv, compileContext);

        // the boolean expression will leave 0 or 1 on the stack so we can negate negate this to get
        // 0 or -1 and then add 1 to get 1 or 0

        mv.visitInsn(Opcodes.INEG);
        // adds 1
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitInsn(Opcodes.IADD);

        // check stack height
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("NotExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack + expected);
        }
    }
}