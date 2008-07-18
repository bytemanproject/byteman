package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * A binary arithmetic operator expression
 */
public class BitExpression extends BinaryOperExpression
{
    public BitExpression(int oper, Token token, Expression left, Expression right)
    {
        // n.b. left and right must be of integral type

        super(oper, Type.promote(left.getType(), right.getType()), token, left, right);
    }
}