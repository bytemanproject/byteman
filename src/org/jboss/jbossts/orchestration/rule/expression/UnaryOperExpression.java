package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

import java.io.StringWriter;

/**
 * unary operators includes boolean NOT and arithmetic TWIDDLE
 * n.b. unary MINUS is not currently supported except as part of number
 * parsing
 */
public abstract class UnaryOperExpression extends OperExpression
{
    public UnaryOperExpression(Rule rule, int oper, Type type, Token token, Expression operand)
    {
        super(rule, oper, type, token);
        this.operand = operand;
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
        // we just need to ensure that the operand can find its bindings
        return operand.bind();
    }

    /**
     * return the operand with the given index or null if the index is out of range
     * @param index
     * @return the operand with the given index
     */
    public Expression getOperand(int index)
    {
        if (index != 0) {
            return null;
        }

        return operand;
    }

    private Expression operand;
}
