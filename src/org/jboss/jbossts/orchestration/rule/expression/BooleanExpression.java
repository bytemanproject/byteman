package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

/**
 * A binary arithmetic operator expression
 */
public abstract class BooleanExpression extends BinaryOperExpression
{
    public BooleanExpression(Rule rule, int oper, Token token, Expression left, Expression right)
    {
        super(rule, oper, Type.Z, token, left, right);
    }
}