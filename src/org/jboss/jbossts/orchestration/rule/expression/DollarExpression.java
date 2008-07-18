package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

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
            // reference to special symbol
            int l = dollarSymbols.length;
            int i;
            for (i = 0; i < l; i++) {
                if (dollarSymbols[i].equals(name)) {
                    return true;
                }
            }
            System.err.println("DollarExpression.bind : invalid builtin symbol " + name + getPos());
            return false;
        } else {
            // reference to positional parameter -- name must be a non-signed integer
            // we will do type checking later
            return true;
        }
    }

    private String name;
    private int index;

    private static String[] dollarSymbols = {
    };
}
