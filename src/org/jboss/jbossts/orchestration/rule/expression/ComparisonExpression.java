package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

/**
 * A binary comparison operator expression
 */
public class ComparisonExpression extends BooleanExpression
{
    public ComparisonExpression(Rule rule, int oper, Token token, Expression left, Expression right)
    {
        super(rule, oper, token, left, right);
        comparisonType = Type.UNDEFINED;
    }

    public Type typeCheck(Type expected) throws TypeException {
        // TODO allow comparison of non-numeric values
        Type type1 = getOperand(0).typeCheck(Type.N);
        Type type2 = getOperand(1).typeCheck(Type.N);
        comparisonType = Type.promote(type1,  type2);
        type = Type.Z;
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("ComparisonExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException
    {
        try {
// n.b. be careful with characters here
            Number value1 = (Number)getOperand(0).interpret(helper);
            Number value2 = (Number)getOperand(1).interpret(helper);
            // type is the result of promoting one or other or both of the operands
            // and they should be converted to this type before doing the compare operation
            if (comparisonType == type.B || comparisonType == type.S || comparisonType == type.I) {
                int i1 = value1.intValue();
                int i2 = value2.intValue();
                boolean result;
                switch (oper)
                {
                    case LT:
                        result = (i1 < i2);
                        break;
                    case LEQ:
                        result = (i1 <= i2);
                        break;
                    case GT:
                        result = (i1 > i2);
                        break;
                    case GEQ:
                        result = (i1 >= i2);
                        break;
                    case EQ:
                        result = (i1 == i2);
                        break;
                    case NEQ:
                        result = (i1 != i2);
                        break;
                    default:
                        result = false;
                        break;
                }
                return result;
            }  else if (comparisonType == type.J) {
                long l1 = value1.longValue();
                long l2 = value2.longValue();
                boolean result;
                switch (oper)
                {
                    case LT:
                        result = (l1 < l2);
                        break;
                    case LEQ:
                        result = (l1 <= l2);
                        break;
                    case GT:
                        result = (l1 > l2);
                        break;
                    case GEQ:
                        result = (l1 >= l2);
                        break;
                    case EQ:
                        result = (l1 == l2);
                        break;
                    case NEQ:
                        result = (l1 != l2);
                        break;
                    default:
                        result = false;
                        break;
                }
                return result;
            }  else if (comparisonType == type.F) {
                float f1 = value1.floatValue();
                float f2 = value2.floatValue();
                boolean result;
                switch (oper)
                {
                    case LT:
                        result = (f1 < f2);
                        break;
                    case LEQ:
                        result = (f1 <= f2);
                        break;
                    case GT:
                        result = (f1 > f2);
                        break;
                    case GEQ:
                        result = (f1 >= f2);
                        break;
                    case EQ:
                        result = (f1 == f2);
                        break;
                    case NEQ:
                        result = (f1 != f2);
                        break;
                    default:
                        result = false;
                        break;
                }
                return result;
            }  else if (comparisonType == type.D) {
                double d1 = value1.doubleValue();
                double d2 = value2.doubleValue();
                boolean result;
                switch (oper)
                {
                    case LT:
                        result = (d1 < d2);
                        break;
                    case LEQ:
                        result = (d1 <= d2);
                        break;
                    case GT:
                        result = (d1 > d2);
                        break;
                    case GEQ:
                        result = (d1 >= d2);
                        break;
                    case EQ:
                        result = (d1 == d2);
                        break;
                    case NEQ:
                        result = (d1 != d2);
                        break;
                    default:
                        result = false;
                        break;
                }
                return result;
            }  else { // (comparisonType == type.C)
                char c1 = (char)value1.intValue();
                char c2 = (char)value2.intValue();
                boolean result;
                switch (oper)
                {
                    case LT:
                        result = (c1 < c2);
                        break;
                    case LEQ:
                        result = (c1 <= c2);
                        break;
                    case GT:
                        result = (c1 > c2);
                        break;
                    case GEQ:
                        result = (c1 >= c2);
                        break;
                    case EQ:
                        result = (c1 == c2);
                        break;
                    case NEQ:
                        result = (c1 != c2);
                        break;
                    default:
                        result = false;
                        break;
                }
                return result;
            }
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("ComparisonExpression.interpret : unexpected exception for operation " + token + getPos() + " in rule " + helper.getName(), e);
        }
    }
    private Type comparisonType;
}