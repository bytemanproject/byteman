package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.antlr.runtime.Token;

/**
 * unary operators includes boolean NOT and arithmetic TWIDDLE
 * n.b. unary MINUS is not currently supported except as part of number
 * parsing
 */
public class UnaryOperExpression extends OperExpression
{
    public UnaryOperExpression(int oper, Type type, Token token, Expression operand)
    {
        super(oper, type, token);
        this.operand = operand;
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
        // we just need to ensure that the operand can find its bindings
        return operand.bind(bindings);
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
