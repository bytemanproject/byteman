package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * A plus operator expression which handles the case where we do not know the type of the first
 * operand. this expression must be replaced by an expression with a known type during type
 * checking
 */
public class PlusExpression extends BinaryOperExpression
{
    public PlusExpression(Token token, Expression left, Expression right)
    {
        super(PLUS, Type.UNDEFINED, token, left, right);
    }
}