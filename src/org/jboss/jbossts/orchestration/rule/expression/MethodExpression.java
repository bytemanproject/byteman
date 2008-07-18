package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.antlr.runtime.Token;
import java.util.List;
import java.util.Iterator;

/**
 * an expression which identifies a method invocation
 */
public class MethodExpression extends Expression
{
    public MethodExpression(Type type, Token token, List<Expression> arguments) {
        super(type, token);
        this.name = token.getText();
        this.arguments = arguments;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @param bindings the set of bindings in place at the point of evaluation of this expression
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public boolean bind(Bindings bindings) {
        // we just have to check that the arguemnt expressions ahve valid bindings

        boolean valid = true;

        Iterator<Expression> iterator = arguments.iterator();

        while (iterator.hasNext()) {
            valid &= iterator.next().bind(bindings);
        }

        return valid;
    }

    private String name;
    private List<Expression> arguments;
}
