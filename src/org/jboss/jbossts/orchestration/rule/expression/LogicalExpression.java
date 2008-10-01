package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

/**
 * A binary logical operator expression
 */
public class LogicalExpression extends BooleanExpression
{
    public LogicalExpression(Rule rule, int oper, Token token, Expression left, Expression right)
    {
        super(rule, oper, token, left, right);
    }

    public Type typeCheck(Type expected) throws TypeException {
        Type type1 = getOperand(0).typeCheck(Type.Z);
        Type type2 = getOperand(1).typeCheck(Type.Z);
        type = Type.Z;
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("LogicalExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        Boolean value = (Boolean)getOperand(0).interpret(helper);

        if (oper == AND) {
            return (value && (Boolean)getOperand(1).interpret(helper));
        } else { // oper == OR
            return (value || (Boolean)getOperand(1).interpret(helper));
        }
    }
}