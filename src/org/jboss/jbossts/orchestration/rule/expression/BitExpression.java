package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
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

    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected) throws TypeException {
        Type type1 = getOperand(0).typeCheck(bindings, typegroup, Type.N);
        Type type2 = getOperand(1).typeCheck(bindings, typegroup, Type.N);
        type = Type.promote(type1, type2);
        // if either arg is float or double we will convert it to long and generate a long
        // result so correct the promotion here
        if (type.isFloating()) {
            type = type.J;
        }
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("BitExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        try {
// n.b. be careful with characters here
            Number value1 = (Number)getOperand(0).interpret(helper);
            Number value2 = (Number)getOperand(1).interpret(helper);
            // type is the result of promoting one or other or both of the operands
            // and they should be converted to this type before doing the arithmetic operation
            if (type == type.B) {
                byte b1 = value1.byteValue();
                byte b2 = value2.byteValue();
                byte result;
                // TODO we should probably only respect the byte, short and char types for + and -
                // TODO also need to decide how to handle divide by zero

                switch (oper)
                {
                    case BOR:
                        result = (byte)(b1 | b2);
                        break;
                    case BAND:
                        result = (byte)(b1 & b2);
                        break;
                    case BXOR:
                        result = (byte)(b1 ^ b2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Byte(result);
            } else if (type == type.S) {
                short s1 = value1.shortValue();
                short s2 = value2.shortValue();
                short result;
                switch (oper)
                {
                    case BOR:
                        result = (short)(s1 | s2);
                        break;
                    case BAND:
                        result = (short)(s1 & s2);
                        break;
                    case BXOR:
                        result = (short)(s1 ^ s2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Short(result);
            }  else if (type == type.I) {
                int i1 = value1.intValue();
                int i2 = value2.intValue();
                int result;
                switch (oper)
                {
                    case BOR:
                        result = (i1 | i2);
                        break;
                    case BAND:
                        result = (i1 & i2);
                        break;
                    case BXOR:
                        result = (i1 ^ i2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Integer(result);
            }  else if (type == type.J || type == type.F || type == type.D) {
                long l1 = value1.longValue();
                long l2 = value2.longValue();
                long result;
                switch (oper)
                {
                    case BOR:
                        result = (l1 | l2);
                        break;
                    case BAND:
                        result = (l1 & l2);
                        break;
                    case BXOR:
                        result = (l1 ^ l2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Long(result);
            }  else { // (type == type.C)
                // use integers here but be careful about conversions
                int s1 = value1.intValue();
                int s2 = value2.intValue();
                char result;
                switch (oper)
                {
                    case BOR:
                        result = (char)(s1 | s2);
                        break;
                    case BAND:
                        result = (char)(s1 & s2);
                        break;
                    case BXOR:
                        result = (char)(s1 ^ s2);
                        break;
                    default:
                        result = 0;
                        break;
                }
                return new Integer(result);
            }
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("BitExpression.interpret : unexpected exception for operation " + token + getPos() + " in rule " + helper.getName(), e);
        }
    }
}