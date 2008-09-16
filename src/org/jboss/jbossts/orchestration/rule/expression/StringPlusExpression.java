package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

/**
 * A binary string concatenation operator expression
 */
public class StringPlusExpression extends BinaryOperExpression
{
    public StringPlusExpression(Token token, Expression left, Expression right)
    {
        super(PLUS, Type.STRING, token, left, right);
    }

    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected) throws TypeException {
        // first type must be a string -- second may be anything but expect
        // a string to indicate that it must be assignable evn if only by conversion
        Type type1 = getOperand(0).typeCheck(bindings, typegroup,  Type.STRING);
        Type type2 = getOperand(1).typeCheck(bindings, typegroup,  Type.STRING);
        // result will always be a String
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(Type.STRING)) {
            throw new TypeException("StringPlusExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        Object value1 = getOperand(0).interpret(helper);
        Object value2 = getOperand(1).interpret(helper);
        String string1 = value1.toString();
        String string2 = value2.toString();
        return string1 + string2;
    }
}