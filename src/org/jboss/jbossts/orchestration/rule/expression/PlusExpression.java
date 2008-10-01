package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

/**
 * A plus operator expression which handles the case where we do not know the type of the first
 * operand. this expression must be replaced by an expression with a known type during type
 * checking
 */
public class PlusExpression extends BinaryOperExpression
{
    public PlusExpression(Rule rule, Token token, Expression left, Expression right)
    {
        super(rule, PLUS, Type.UNDEFINED, token, left, right);
    }

    public Type typeCheck(Type expected) throws TypeException {
        // if the expected type is numeric then we know this must be an arithmetic plus
        // if it is string then this could still be an arithmetic plus so we will
        // have to rely on the type of the first argument to guide us

        Type type1 = getOperand(0).typeCheck((expected.isNumeric() ? expected : Type.UNDEFINED));
        Type type2;

        if (type1.isNumeric()) {
            type2 = getOperand(1).typeCheck(Type.N);
            type = Type.promote(type1, type2);
        } else if (type1.isString()) {
            type2 = getOperand(1).typeCheck(Type.STRING);
        } else {
            throw new TypeException("PlusExpression.typeCheck : invalid argument type " + type1.getName() + getPos());
        }

        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        Object value1 = getOperand(0).interpret(helper);
        Object value2 = getOperand(1).interpret(helper);
        if (type == Type.S) {
            String s1 = (String)value1;
            String s2 = (String)value2;
            return s1 + s2;
        } else {
            Number n1 = (Number)value1;
            Number n2 = (Number)value2;
            if (type == Type.B) {
                byte b1 = n1.byteValue();
                byte b2 = n2.byteValue();
                byte result = (byte)(b1 + b2);
                return new Byte(result);
            } else if (type == Type.S) {
                short s1 = n1.shortValue();
                short s2 = n2.shortValue();
                short result = (short)(s1 + s2);
                return new Short(result);
            }  else if (type == Type.I) {
                int i1 = n1.intValue();
                int i2 = n2.intValue();
                int result = (i1 + i2);
                return new Integer(result);
            } else if (type == Type.F) {
                float f1 = n1.floatValue();
                float f2 = n2.floatValue();
                float result = (f1 + f2);
                return new Float(result);
            } else if (type == Type.D) {
                double d1 = n1.doubleValue();
                double d2 = n2.doubleValue();
                double result = (d1 + d2);
                return new Double(result);
            } else { // type == Type.C
                char c1 = (char)n1.intValue();
                char c2 = (char)n2.intValue();
                char result = (char)(c1 + c2);
                return new Integer(result);
            }
        }
    }
}