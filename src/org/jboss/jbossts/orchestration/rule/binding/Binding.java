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
package org.jboss.jbossts.orchestration.rule.binding;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.expression.Expression;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.RuleElement;

import java.io.StringWriter;

/**
 * Class used to store a binding of a named variable to a value of some given type
 */

public class Binding extends RuleElement
{

    public Binding(Rule rule, String name)
    {
        this(rule, name, Type.UNDEFINED, null);
    }

    public Binding(Rule rule, String name, Type type)
    {
        this(rule, name, type, null);
    }

    public Binding(Rule rule, String name, Type type, Expression value)
    {
        super(rule);
        this.name = name;
        this.type = (type != null ? type : Type.UNDEFINED);
        this.value = value;
        if (name.equals("-1")) {
            index = -1;
        } else if (name.matches("[0-9].*")) {
            index = Integer.valueOf(name);
        } else if (name.equals("$!")) {
            index = -2;
        } else {
            index = -3;
        }
    }

    public Type typeCheck(Type expected)
            throws TypeException
    {
        // value can be null if this is a rule method parameter
        if (value != null) {
            // type check the binding expression, using the bound variable's expected if it is known

            Type valueType = value.typeCheck(expected);

            if (type.isUndefined()) {
                type = valueType;
            }
        } else if (type.isUndefined()) {
            // can we have no expected for a method parameter?
            throw new TypeException("Binding.typecheck unknown type for binding " + name);
        }
        return type;
    }

    public String getName()
    {
        return name;
    }

    public Expression getValue()
    {
        return value;
    }

    public Expression setValue(Expression value)
    {
        Expression oldValue = this.value;
        this.value = value;

        return oldValue;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
    }

    public boolean isHelper()
    {
        return index == -1;
    }

    public boolean isRecipient()
    {
        return index == 0;
    }

    public boolean isParam()
    {
        return index > 0;
    }

    public boolean isReturn()
    {
        return index == -2;
    }

    public boolean isVar()
    {
        return index < -2;
    }

    public int getIndex()
    {
        return index;
    }

    public void writeTo(StringWriter stringWriter)
    {
        if (isHelper()) {
            stringWriter.write("$$");
        } else if (isParam()) {
            stringWriter.write("$" + name);
            if (type != null && (type.isDefined() || type.isObject())) {
                stringWriter.write(" : ");
                stringWriter.write(type.getName());
            }
        } else {
            stringWriter.write(name);
            if (type != null && (type.isDefined() || type.isObject())) {
                stringWriter.write(" : ");
                stringWriter.write(type.getName());
            }
        }
        if (value != null) {
            stringWriter.write(" = ");
            value.writeTo(stringWriter);
        }
    }

    private String name;
    private Type type;
    private Expression value;
    private int index;
}
