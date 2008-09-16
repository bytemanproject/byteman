package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

import java.io.StringWriter;

/**
 * an expression which refers either to a builtin variable or to a bound parameter of the
 * triggering method for an ECA rule. builtin variables are written as a dollar sign followed
 * by a leading alpha-underscore, trailing alpha-numeric-underscore string. bound parameters are
 * written as a dollar sign followed by a non-negativeinteger parameter index
 *
 * e.g. if the rule applies to method foo.bar(int baz, Mumble mumble) then an occurrence of $2
 * appearing as an expression in a rule would have type Mumble and evaluate to the value of mumble
 * at the point when the rule was triggered.
 *
 * At present there are no special variables but we may need to add some later
 */
public class DollarExpression extends Expression
{
    public DollarExpression(Type type, Token token)
    {
        super(type, token);
        String text = token.getText();
        this.name = text.substring(1, text.length());
        char first = name.charAt(0);
        if ('0' <= first && first <= '9') {
            try {
                index = Integer.decode(name);
            } catch (NumberFormatException nfe) {
                // oops should not be possible according to tokenizer rules
                index = -1;
            }
        }
    }

    public DollarExpression(Type type, Token token, String text)
    {
        super(type, token);
        this.name = text.substring(1, text.length());
        try {
            index = Integer.decode(name);
        } catch (NumberFormatException nfe) {
            // oops should not be possible according to tokenizer rules
            index = -1;
        }
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible

     * @param bindings the set of bindings in place at the point of evaluation of this expression
     * @return true if all variables in this expression are bound and no type mismatches have
     * been detected during inference/validation.
     */

    public boolean bind(Bindings bindings) {
        if (index < 0) {
            System.err.println("DollarExpression.bind : invalid bound parameter $" + name + getPos());
            return false;
        } else {
            // reference to positional parameter -- name must be a non-signed integer
            // we will do type checking later
            return true;
        }
    }

    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected) throws TypeException {
        // ensure there is a parameter with the relevant name in the bindings
        Binding binding = bindings.lookup(Integer.toString(index));
        if (binding == null) {
            throw new TypeException("DollarExpression.typeCheck : invalid bound parameter $" + name + getPos());
        }
        type = binding.getType();
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("DollarExpression.typeCheck : invalid expected type " + expected.getName() + " for bound parameter " + name + getPos());            
        }
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException
    {
        return helper.getBinding(name);
    }

    public void writeTo(StringWriter stringWriter) {
        if (name.equals("-1")) {
            stringWriter.write("$$");
        } else {
            stringWriter.write("$" + name);
        }
    }

    private String name;
    private int index;
}
