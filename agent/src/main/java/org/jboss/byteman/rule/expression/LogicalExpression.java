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
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

/**
 * A binary logical operator expression
 */
public class LogicalExpression extends BooleanExpression
{
    public LogicalExpression(Rule rule, int oper, ParseNode token, Expression left, Expression right)
    {
        super(rule, oper, token, left, right);
    }

    public Type typeCheck(Type expected) throws TypeException {
        Type type1 = getOperand(0).typeCheck(Type.Z);
        Type type2 = getOperand(1).typeCheck(Type.Z);
        type = Type.Z;
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("LogicalExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        Boolean value = (Boolean)getOperand(0).interpret(helper);

        if (oper == AND) {
            return (value && (Boolean)getOperand(1).interpret(helper));
        } else { // oper == OR
            return (value || (Boolean)getOperand(1).interpret(helper));
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Expression oper0 = getOperand(0);
        Expression oper1 = getOperand(1);

        int currentStack = compileContext.getStackCount();

        // compile the first expression and make sure it is a boolean -- adds 1 to stack height
        oper0.compile(mv, compileContext);
        if (oper0.getType() == Type.BOOLEAN) {
            compileBooleanConversion(Type.BOOLEAN, type.Z, mv, compileContext);
        }
        // plant a test and branch
        Label nextLabel = new Label();
        Label endLabel = new Label();
        if (oper == AND) {
            // only try next if we got true here
            mv.visitJumpInsn(Opcodes.IFNE, nextLabel);
            // ok, the first branch was false so stack a false for the result and skip to the end
            mv.visitLdcInsn(false);
            mv.visitJumpInsn(Opcodes.GOTO, endLabel);
        } else {
            // only try next if we got false here
            mv.visitJumpInsn(Opcodes.IFEQ, nextLabel);
            // ok, the first branch was true so stack a true for the result and skip to the end
            mv.visitLdcInsn(true);
            mv.visitJumpInsn(Opcodes.GOTO, endLabel);
        }
        // in either case if we get here the if test removed 1 from the stack
        compileContext.addStackCount(-1);
        // the else branch -- adds 1 to stack height
        mv.visitLabel(nextLabel);
        oper1.compile(mv, compileContext);
        if (oper0.getType() == Type.BOOLEAN) {
            compileBooleanConversion(Type.BOOLEAN, type.Z, mv, compileContext);
        }
        // the final result is the result of the second oper which is on the stack already
        // This is the end, my beau-tiful friend
        mv.visitLabel(endLabel);
        // in either case if we get here we should have one extra value on the stack
        // check stack height
        if (compileContext.getStackCount() != currentStack + 1) {
            throw new CompileException("LogicalExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + 1));
        }
    }
}