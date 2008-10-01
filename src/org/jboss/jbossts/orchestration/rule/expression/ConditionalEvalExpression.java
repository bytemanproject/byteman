package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

/**
 * expression representing a ternary conditional evaluation (cond ? if_expr : else_expr)
 */
public class ConditionalEvalExpression extends TernaryOperExpression
{
    public ConditionalEvalExpression(Rule rule, Type type, Token token, Expression cond, Expression if_expr, Expression else_expr)
    {
        super(rule, TERNARY, type, token, cond, if_expr, else_expr);
    }

    public Type typeCheck(Type expected) throws TypeException {
        Type condType = getOperand(0).typeCheck(Type.Z);
        Type type1 = getOperand(1).typeCheck(expected);
        Type type2 = getOperand(2).typeCheck(expected);
        // type1 must be defined and type2 must be the same as type 1 or assignable
        // to/from it.
        if (type2 != type1) {
            // ok check that the types are interassignable in at least one direction
            // but we have to treat numerics as special cases because we can assign in
            // many directions
            if (type1.isNumeric() && type2.isNumeric()) {
                type = Type.promote(type1,  type2);
            } else if (type2.isAssignableFrom(type1)) {
                type = type2;
            } else if (type1.isAssignableFrom(type2)) {
                type = type1;
            } else {
                throw new TypeException("ConditionalEvalExpression.typeCheck : incompatible argument types " + type1.getName() + " and " + type2.getName() + getPos());
            }
        }
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("ConditionalEvalExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException
    {
        Boolean executeFirstBranch = (Boolean)getOperand(0).interpret(helper);
        if (executeFirstBranch) {
            return getOperand(1).interpret(helper);
        } else {
            return getOperand(2).interpret(helper);
        }
    }
}
