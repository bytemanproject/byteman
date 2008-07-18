package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 */
public class NotExpression extends UnaryOperExpression
{
    public NotExpression(Token token, Expression operand)
    {
        super(NOT, Type.BOOLEAN, token, operand);
    }
}