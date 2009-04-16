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

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        Expression oper = getOperand(0);
        Type operType = oper.getType();

        int currentStack = currentStackHeights.stackCount;
        int expected = 1;

        // compile code to execute the operand -- adds 1
        oper.compile(mv, currentStackHeights, maxStackHeights);
        compileTypeConversion(operType, type, mv, currentStackHeights, maxStackHeights);

        // the boolean expression will leave 0 or 1 on the stack so we can negate negate this to get
        // 0 or -1 and then add 1 to get 1 or 0

        mv.visitInsn(Opcodes.INEG);
        // adds 1
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitInsn(Opcodes.IADD);

        // check stack height
        if (currentStackHeights.stackCount != currentStack + expected) {
            throw new CompileException("NotExpression.compile : invalid stack height " + currentStackHeights.stackCount + " expecting " + currentStack + expected);
        }

        // ensure we have room for the two values we stacked

        int overflow = (currentStack + 2) - maxStackHeights.stackCount;

        if (overflow > 0) {
            maxStackHeights.addStackCount(overflow);
        }

    }
}