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
 * expression representing a ternary conditional evaluation (cond ? if_expr : else_expr)
 */
public class ConditionalEvalExpression extends TernaryOperExpression
{
    public ConditionalEvalExpression(Rule rule, Type type, ParseNode token, Expression cond, Expression if_expr, Expression else_expr)
    {
        super(rule, COND, type, token, cond, if_expr, else_expr);
    }

    public Type typeCheck(Type expected) throws TypeException {
        Type condType = getOperand(0).typeCheck(Type.Z);
        Type type1 = getOperand(1).typeCheck(expected);
        Type type2 = getOperand(2).typeCheck(expected);
        // type1 must be defined and type2 must be the same as type 1 or assignable
        // to/from it.
        if (type2 != type1) {
            // ok check that the types are interassignable in at least one direction
            // but we have to treat numerics as special cases because we can assign in
            // many directions
            if (type1.isNumeric() && type2.isNumeric()) {
                type = Type.promote(type1,  type2);
            } else if (type2.isAssignableFrom(type1)) {
                type = type2;
            } else if (type1.isAssignableFrom(type2)) {
                type = type1;
            } else {
                throw new TypeException("ConditionalEvalExpression.typeCheck : incompatible argument types " + type1.getName() + " and " + type2.getName() + getPos());
            }
        } else {
            // use either type
            type = type1;
        }
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("ConditionalEvalExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException
    {
        Boolean executeFirstBranch = (Boolean)getOperand(0).interpret(helper);
        if (executeFirstBranch) {
            return getOperand(1).interpret(helper);
        } else {
            return getOperand(2).interpret(helper);
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Expression oper0 = getOperand(0);
        Expression oper1 = getOperand(1);
        Expression oper2 = getOperand(2);

        int currentStack = compileContext.getStackCount();
        int expected = (type.getNBytes() > 4 ? 2 : 1);

        // compile the first operand to a boolean and ensure it is primitive -- adds 1 to stack
        oper0.compile(mv, compileContext);
        if (oper0.getType() == Type.BOOLEAN) {
            compileBooleanConversion(Type.BOOLEAN, Type.Z, mv, compileContext);
        }
        // plant the test -- consumes 1 word
        Label elseLabel = new Label();
        Label endLabel = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, elseLabel);
        compileContext.addStackCount(-1);
        // compile the if branch
        oper1.compile(mv, compileContext);
        // make sure we type convert to our result type so that either branch stacks the same thing
        compileTypeConversion(oper1.getType(), type,  mv, compileContext);
        // plant a goto skipping over the else expression
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);
        // check the stack height is what we expect, either 1 or 2 words depending upon the result type
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("ConditionalEvalExpression.compile : invalid true branch stack height " + compileContext.getStackCount() + " expecting " + currentStack + expected);
        }
        // ok, now reset stack height for false branch
        compileContext.addStackCount(-expected);
        // else starts here
        mv.visitLabel(elseLabel);
        // compile the else branch
        oper2.compile(mv, compileContext);
        // make sure we type convert to our result type so that either branch stacks the same thing
        compileTypeConversion(oper2.getType(), type,  mv, compileContext);
        // the end is nigh
        mv.visitLabel(endLabel);

        // check the stack height is what we expect, either 1 or 2 words depending upon the result type
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("ConditionalEvalExpression.compile : invalid false branch stack height " + compileContext.getStackCount() + " expecting " + currentStack + expected);
        }

        // no need to check max stack height as teh left and right expressions will have exceeded anything
        // we stacked inside this call
    }
}
