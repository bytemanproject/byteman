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
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.grammar.ParseNode;

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

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        Boolean result = (Boolean) getOperand(0).interpret(helper);
        return !result;
    }
}