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

import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.grammar.ParseNode;

/**
 * ternary operators includes conditional evaluation operator 'cond ? if_expr : else_expr'
 */
public abstract class TernaryOperExpression extends OperExpression
{
    public TernaryOperExpression(Rule rule, int oper, Type type, ParseNode token, Expression operand1,
                                 Expression operand2, Expression operand3)
    {
        super(rule, oper, type, token);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public void bind() throws TypeException
    {
        // we just need to ensure that the operands can find their bindings
        // run both so we get as many errors as possible

        operand1.bind();
        operand2.bind();
        operand3.bind();
    }

    /**
     * return the operand with the given index or null if the index is out of range
     * @param index
     * @return the operand with the given index
     */
    public Expression getOperand(int index)
    {
        if (index == 0) {
            return operand1;
        } else if (index == 1) {
            return operand2;
        } else if (index == 2) {
            return operand3;
        }

        return null;
    }

    private Expression operand1;
    private Expression operand2;
    private Expression operand3;
}