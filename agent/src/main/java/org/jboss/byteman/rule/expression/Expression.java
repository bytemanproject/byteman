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

import org.jboss.byteman.rule.grammar.ParseNode;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.RuleElement;
import org.jboss.byteman.rule.helper.HelperAdapter;

import java.io.StringWriter;

/**
 * abstract class representing an evaluable expression. this is used in all 3 elements of ECA rules:
 * as the value part of each of the event bindings in the binding list comprising an ECA rule event;
 * as the condition expression of an ECA rule condition;and as an element of the actions list in an
 * ECA rule action.
 */
public abstract class Expression extends RuleElement
{
    /**
     * Create a new expression.
     * @param type the current type for this expression.
     */
    protected Expression(Rule rule, Type type, ParseNode token)
    {
        super(rule);
        this.rule = rule;
        this.type = type;
        this.token = token;
        if (token != null) {
            this.charPos = token.getColumn();
            this.line = token.getLine();
        } else {
            this.charPos = 0;
            this.line = 0;
        }
    }

    /**
     * verify that variables mentioned in this expression are actually available in the rule
     * bindings list
     * @return true if all variables in this expression are bound and no type mismatches have
     * been detected during validation.
     */
    public abstract void bind() throws TypeException;

    public String getPos()
    {
        return " file " + rule.getFile() + " line " + line;
    }

    public Type getType()
    {
        return type;
    }

    /**
     * ensure that all type references in the expression and its component expressions
     * can be resolved, that the type of the expression is well-defined and that it is
     * compatible with the type expected in the context in which it occurs.
     * @param expected the type expected for the expression in the contxet in which it occurs. this
     * may be void but should not be undefined at the point where type checking is performed.
     * @return
     * @throws TypeException
     */
    public abstract Type typeCheck(Type expected)
            throws TypeException;

    /**
     * evaluate the expression by interpreting the expression tree
     * @param helper an execution context associated with the rule which contains a map of
     * current bindings for rule variables and another map of their declared types both of which
     * are indexed by variable name. This includes entries for the helper (name "-1"), the
     * recipient if the trigger method is not static (name "0") and the trigger method arguments
     * (names "1", ...)
     * @return  the result of evaluation as an Object
     * @throws org.jboss.byteman.rule.exception.ExecuteException
     */
    public abstract Object interpret(HelperAdapter helper) throws ExecuteException;

    public abstract void writeTo(StringWriter stringWriter);

    protected Rule  rule;
    protected Type type;
    protected int charPos;
    protected int line;
    protected ParseNode token;
}
