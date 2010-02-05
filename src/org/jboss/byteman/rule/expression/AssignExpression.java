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
package org.jboss.byteman.rule.expression;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.compiler.StackHeights;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.type.Type;
import org.objectweb.asm.MethodVisitor;

/**
 * A binary string concatenation operator expression
 */
public class AssignExpression extends BinaryOperExpression
{
    private AssignableExpression lhs;
    public AssignExpression(Rule rule, ParseNode token, AssignableExpression left, Expression right)
    {
        super(rule, ASSIGN, Type.UNDEFINED, token, left, right);
        this.lhs = left;
    }

    public Type typeCheck(Type expected) throws TypeException {
        // we accept any type we are given and check the var type then we use its type to check the expression
        // if either operand cannot type check then it will throw an error

        Type type1 = lhs.typeCheck(expected);
        Type type2 = getOperand(1).typeCheck(type1);
        type = type1;
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        Object value = getOperand(1).interpret(helper);
        value = lhs.interpretAssign(helper, value);
        return value;
    }

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        Expression oper1 = getOperand(1);

        int currentStack = currentStackHeights.stackCount;
        int expected = (type.getNBytes() > 4 ? 2 : 1);

        // compile and type convert the rhs operand . it will ensure the max stack height is updated
        // accordingly and we don't add anything more so there is no need to check the stack heights here

        oper1.compile(mv, currentStackHeights, maxStackHeights);
        compileTypeConversion(oper1.getType(), type, mv, currentStackHeights, maxStackHeights);

        // now get the LHS expression to compile in the appropriate assignment code

        lhs.compileAssign(mv, currentStackHeights, maxStackHeights);

        // ok, the stack height should be increased by the expecdted bytecount
        if (currentStackHeights.stackCount != currentStack + expected) {
            throw new CompileException("AssignExpression.compileAssignment : invalid stack height " + currentStackHeights.stackCount + " expecting " + (currentStack + expected));
        }
    }
}