package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.antlr.runtime.Token;

/**
 * binary operators includes arithmetic and comparison operators
 */
public abstract class BinaryOperExpression extends OperExpression
{
    public BinaryOperExpression(int oper, Type type, Token token, Expression operand1, Expression operand2)
    {
        super(oper, type, token);
        this.operand1 = operand1;
        this.operand2 = operand2;
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
        // we just need to ensure that the operands can find their bindings
        // run both so we get as many errors as possible

        boolean success = operand1.bind(bindings);
        success  &= operand2.bind(bindings);
        return success;
    }

    /**
     * return the operand with the given index or null if the index is out of range
     * @param index
     * @return the operand with the given index
     */
    public Expression getOperand(int index)
    {
        if (index == 0) {
            return operand1;
        } else if (index == 1) {
            return operand2;
        }

        return null;
    }

    private Expression operand1;
    private Expression operand2;
}