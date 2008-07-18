package org.jboss.jbossts.orchestration.rule.binding;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.expression.Expression;

/**
 * Class used to store a binding of a named variable to a value of some given type
 */

public class Binding {

    public Binding(String name)
    {
        this(name, null, null);
    }

    public Binding(String name, Type type)
    {
        this(name, type, null);
    }

    public Binding(String name, Type type, Expression value)
    {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public boolean typeCheck(ClassLoader loader, Type type)
    {
        if (this.type.isUndefined()) {
            // accept the candidate type
            if (type.isUndefined()) {
                // !!should never happen
                System.err.println("Binding.typecheck : shouldn't happen! variable " + getName() + " has undefined tyep and undefined derived type " + type.getName());
                return false;
            } else {
                this.type = type;
                return true;
            }
        } else {
            // TODO check is this the right way round???
            if (type.isAssignableFrom(this.type)) {
                return true;
            } else {
                System.err.println("Binding.typecheck : current type " + this.type.getName() + " for variable " + getName() + " is incompatible with derived type " + type.getName());
                return false;
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public Object getValue()
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

    private String name;
    private Type type;
    private Expression value;
}
