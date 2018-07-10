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

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.compiler.CompileContext;
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

    public void bind() throws TypeException
    {
        // we need to ensure that the assignable expression gets told to bindAssign()

        lhs.bindAssign();
        getOperand(1).bind();
    }

    public Type typeCheck(Type expected) throws TypeException {
        // we accept any type we are given and check the rhs expression
        // then we type check the lhs ensuring that it can be assigned
        // with a value of this type. the resulting type has to be that
        // of the lhs.
        // if either operand cannot type check then it will throw an error

        // if we are assigning a variable and we have no expected type
        // or it is void then type check the bind var against undefined
        // first and then push the bind var type down as the expected
        // type for the assigned value
        //
        // otherwise type check the assigned value using the expected
        // type
        Expression rhs = getOperand(1);
        if ((expected.isUndefined() || expected.isVoid()) && lhs instanceof Variable) {
            Type type1 = lhs.typeCheckAssign(Type.UNDEFINED);
            Type type2 = rhs.typeCheck(type1);
            type = type1;
        } else {
            Type type2 = rhs.typeCheck(expected);
            Type type1 = lhs.typeCheckAssign(type2);
            type = type1;
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        Object value = getOperand(1).interpret(helper);
        value = lhs.interpretAssign(helper, value);
        return value;
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Expression oper1 = getOperand(1);

        int currentStack = compileContext.getStackCount();
        int expected = (type.getNBytes() > 4 ? 2 : 1);

        // compile and type convert the rhs operand . it will ensure the max stack height is updated
        // accordingly and we don't add anything more so there is no need to check the stack heights here

        oper1.compile(mv, compileContext);

        // one or other of the lhs and rhs may be inaccessible
        // and hence treated simply as an Object. if not then
        // we may need to perform a type conversion.
        // if the rhs is inaccessible it can be treated as an Object
        // if the rhs is inaccessible then it can also be treated as an object
        Type rhsType = oper1.getType();
        Type lhsType = type;
        if (!rule.requiresAccess(lhsType)) {
            // we are assigning into a non-generic slot
            // if the rhs has been treated generically
            // then it needs casting from OBJECT
            // otherwise convert using its type
            if(rule.requiresAccess(rhsType)) {
                rhsType = Type.OBJECT;
            }
            compileContext.compileTypeConversion(rhsType, lhsType);
        }

        // now get the LHS expression to compile in the appropriate assignment code

        lhs.compileAssign(mv, compileContext);

        // ok, the stack height should be increased by the expected bytecount
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("AssignExpression.compileAssignment : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expected));
        }
    }
}