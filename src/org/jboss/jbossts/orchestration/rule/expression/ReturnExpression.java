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

import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.exception.EarlyReturnException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.grammar.ParseNode;

import java.io.StringWriter;

/**
 *  A return expression which is used in a rule action to cause a return from the rule trigger
 * method, supplying a return value where appropriate.
 */

public class ReturnExpression extends Expression
{
    private Expression returnValue;

    public ReturnExpression(Rule rule, ParseNode token, Expression returnValue)
    {
        // the trigger method may return any old tyep but the return expression can only occur
        // at the top level in a rule action seuqence so it is actually a VOID expression

        super(rule, Type.VOID, token);

        this.returnValue = returnValue;
    }
    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public boolean bind() {
        if (returnValue != null) {
            // ensure the return value expression has all its bindings
            return returnValue.bind();
        }
        return true;
    }

    /**
     * ensure that all type references in the expression and its component expressions
     * can be resolved, that the type of the expression is well-defined and that it is
     * compatible with the type expected in the context in which it occurs.
     *
     * @param expected  the type expected for the expression in the contxt in which it occurs. this
     *                  may be void but shoudl not be undefined at the point where type checking is performed.
     * @return
     * @throws org.jboss.jbossts.orchestration.rule.exception.TypeException
     *
     */
    public Type typeCheck(Type expected) throws TypeException {
        // we need to check the returnValue expression against the type of the trigger method
        Binding returnBinding = getBindings().lookup("$!");
        Type returnBindingType = (returnBinding != null ? returnBinding.getType() : Type.VOID);
        if (returnValue == null && !returnBindingType.isVoid()) {
            throw new TypeException("ReturnExpression.typeCheck : return expression must supply argument when triggered from method with return type " + returnBindingType.getName() + getPos());
        } else if (returnValue != null) {
            if (returnBindingType.isVoid()) {
                throw new TypeException("ReturnExpression.typeCheck : return expression must not supply argument when triggered from void method" + getPos());
            }
            returnValue.typeCheck(returnBindingType);
        }
        return type;
    }

    /**
     * evaluate the expression by interpreting the expression tree
     *
     * @param helper an execution context associated with the rule whcih contains a map of
     *               current bindings for rule variables and another map of their declared types both of which
     *               are indexed by varoable name. This includes entries for the helper (name "-1"), the
     *               recipient if the trigger method is not static (name "0") and the trigger method arguments
     *               (names "1", ...)
     * @return the result of evaluation as an Object
     * @throws org.jboss.jbossts.orchestration.rule.exception.ExecuteException
     *
     */
    public Object interpret(Rule.BasicHelper helper) throws ExecuteException
    {
        // time to take an early bath -- the code compield into the trigger method should
        // catch this and return as appropriate
        if (returnValue != null) {
            Object value = returnValue.interpret(helper);
            throw new EarlyReturnException("return from " + helper.getName(), value);
        } else {
            throw new EarlyReturnException("return from " + helper.getName());
        }
    }

    public void writeTo(StringWriter stringWriter) {
        if (returnValue != null) {
            stringWriter.write("RETURN ");
            returnValue.writeTo(stringWriter);
        } else {
            stringWriter.write("RETURN");
        }
    }
}
