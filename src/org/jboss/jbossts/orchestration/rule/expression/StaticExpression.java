package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * an expression which identifies aa static field reference
 */
public class StaticExpression extends Expression
{
    public StaticExpression(Type type, Token token, String clazzName, String fieldName) {
        // type is the type of static field
        super(type, token);
        this.clazzName = clazzName;
        this.fieldName = fieldName;
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
        // nothing to verify

        return true;
    }

    private String fieldName;
    private String clazzName;
}