package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * A binary arithmetic operator expression
 */
public class ArithmeticExpression extends BinaryOperExpression
{
    public ArithmeticExpression(int oper, Token token, Expression left, Expression right)
    {
        super(oper, Type.promote(left.getType(), right.getType()), token, left, right);
    }
}
