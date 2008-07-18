package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * A binary logical operator expression
 */
public class LogicalExpression extends BooleanExpression
{
    public LogicalExpression(int oper, Token token, Expression left, Expression right)
    {
        super(oper, token, left, right);
    }
}