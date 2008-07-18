package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 */
public class TwiddleExpression extends UnaryOperExpression
{
    public TwiddleExpression(Token token, Expression operand)
    {
        super(TWIDDLE, operand.getType(), token, operand);
    }
}
