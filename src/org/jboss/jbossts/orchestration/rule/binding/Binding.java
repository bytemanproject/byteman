package org.jboss.jbossts.orchestration.rule.binding;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.expression.Expression;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;

import java.io.StringWriter;

/**
 * Class used to store a binding of a named variable to a value of some given type
 */

public class Binding {

    public Binding(String name)
    {
        this(name, Type.UNDEFINED, null);
    }

    public Binding(String name, Type type)
    {
        this(name, type, null);
    }

    public Binding(String name, Type type, Expression value)
    {
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

    public void typeCheck(Bindings bindings, TypeGroup typeGroup)
            throws TypeException
    {
        // value can be null if this is a rule method parameter
        if (value != null) {
            // type check the binding expression, using the bound variable's type if it is known

            Type valueType = value.typeCheck(bindings, typeGroup, type);

            if (type.isUndefined()) {
                type = valueType;
            }
        } else if (type.isUndefined()) {
            // can we have no type for a method parameter?
            throw new TypeException("Binding.typecheck unknown type for binding " + name);
        }
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
