package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * A binary comparison operator expression
 */
public class ComparisonExpression extends BooleanExpression
{
    public ComparisonExpression(int oper, Token token, Expression left, Expression right)
    {
        super(oper, token, left, right);
    }
}