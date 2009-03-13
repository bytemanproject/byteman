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
import org.jboss.jbossts.orchestration.rule.helper.HelperAdapter;
import org.jboss.jbossts.orchestration.rule.grammar.ParseNode;

/**
 * A binary string concatenation operator expression
 */
public class StringPlusExpression extends BinaryOperExpression
{
    public StringPlusExpression(Rule rule, ParseNode token, Expression left, Expression right)
    {
        super(rule, PLUS, Type.STRING, token, left, right);
    }

    public Type typeCheck(Type expected) throws TypeException {
        // first type must be a string -- second may be anything but expect
        // a string to indicate that it must be assignable evn if only by conversion
        Type type1 = getOperand(0).typeCheck(Type.STRING);
        Type type2 = getOperand(1).typeCheck(Type.STRING);
        // result will always be a String
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(Type.STRING)) {
            throw new TypeException("StringPlusExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        Object value1 = getOperand(0).interpret(helper);
        Object value2 = getOperand(1).interpret(helper);
        String string1 = value1.toString();
        String string2 = value2.toString();
        return string1 + string2;
    }
}