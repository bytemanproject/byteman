package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * A binary string concatenation operator expression
 */
public class StringPlusExpression extends BinaryOperExpression
{
    public StringPlusExpression(Token token, Expression left, Expression right)
    {
        super(PLUS, Type.STRING, token, left, right);
    }
}