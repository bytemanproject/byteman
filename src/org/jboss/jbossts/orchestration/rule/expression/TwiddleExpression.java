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
public class TwiddleExpression extends UnaryOperExpression
{
    public TwiddleExpression(Rule rule, Token token, Expression operand)
    {
        super(rule, TWIDDLE, operand.getType(), token, operand);
    }

    public Type typeCheck(Type expected)
    throws TypeException {
        type = getOperand(0).typeCheck(Type.N);
        if (type == Type.F || type == Type.D) {
            type = Type.J;
        }
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("TwiddleExpression.typeCheck() : invalid result type : " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        try {
            Number value = (Number)getOperand(0).interpret(helper);

            if (type == Type.B) {
                return (byte)~value.intValue();
            } else if (type == Type.S) {
                return (short)~value.intValue();
            } else if (type == Type.I) {
                return ~value.intValue();
            } else if (type == Type.J) {
                return ~value.longValue();
            } else if (type == Type.F) {
                return ~value.longValue();
            } else if (type == Type.D) {
                return ~value.longValue();
            } else { // (type == Type.C)
                return ~value.intValue();
            }
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("TwiddleExpression.typeCheck() : unexpected exception : " + token.getText() + getPos(), e);
        }
    }
}
