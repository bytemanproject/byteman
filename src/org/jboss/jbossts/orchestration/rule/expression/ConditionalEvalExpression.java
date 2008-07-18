package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * expression representing a ternary conditional evaluation (cond ? if_expr : else_expr)
 */
public class ConditionalEvalExpression extends TernaryOperExpression
{
    public ConditionalEvalExpression(Type type, Token token, Expression cond, Expression if_expr, Expression else_expr)
    {
        super(TERNARY, type, token, cond, if_expr, else_expr);
    }
}
