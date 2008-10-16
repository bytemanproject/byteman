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
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.grammar.ParseNode;

import java.io.StringWriter;

/**
 * an expression which identifies a variable occurring either as an LVALUE on the LHS of an event
 * binding in the rule's event or as an RVALUE mentioned in the RHS of an event binding or in thre
 * rule's conditon or action.
 */
public class Variable extends Expression
{
    public Variable(Rule rule, Type type, ParseNode token) {
        super(rule, type, token);
        this.name = token.getText();
    }

    public Variable(Rule rule, Type type, ParseNode token, String name) {
        super(rule, type, token);
        this.name = name;
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
        // ensure that there is a binding with this name

        Binding binding = getBindings().lookup(name);

        if (binding == null) {
            System.err.println("Variable.bind : unbound variable " + name + getPos());
            return false;
        }
        // adopt the binding type

        this.type = binding.getType();

        return true;
    }

    public Type typeCheck(Type expected) throws TypeException {
        // type must be defined by now or we are in trouble

        Binding binding = getBindings().lookup(name);

        type = Type.dereference(binding.getType());

        if (type.isUndefined()) {
            throw new TypeException("Variable.typeCheck : unable to derive type for variable " + name +  getPos());
        }
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("Variable.typeCheck() : invalid result type : " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        return helper.getBinding(name);
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(name);
    }

    private String name;
}
