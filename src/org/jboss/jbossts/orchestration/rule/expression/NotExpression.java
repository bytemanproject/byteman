package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

/**
 */
public class NotExpression extends UnaryOperExpression
{
    public NotExpression(Token token, Expression operand)
    {
        super(NOT, Type.BOOLEAN, token, operand);
    }

    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected)
    throws TypeException {
        type = getOperand(0).typeCheck(bindings, typegroup, Type.Z);
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("NotExpression.typeCheck() : invalid result type : " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        Boolean result = (Boolean) getOperand(0).interpret(helper);
        return !result;
    }
}