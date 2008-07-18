package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * A binary arithmetic operator expression
 */
public class BooleanExpression extends BinaryOperExpression
{
    public BooleanExpression(int oper, Token token, Expression left, Expression right)
    {
        super(oper, Type.BOOLEAN, token, left, right);
    }
}