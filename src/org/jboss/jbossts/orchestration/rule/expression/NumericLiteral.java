package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

import java.io.StringWriter;

/**
 */
public class NumericLiteral extends Expression
{

    public NumericLiteral(Token token) {
        super(check(token.getText()), token);

        this.text = token.getText();
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @param bindings the set of bindings in place at the point of evaluation of this expression
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public boolean bind(Bindings bindings) {
        // no bindings to check but stash a valid value to help with type checking
        if (type == Type.FLOAT) {
            value = Float.valueOf(text);
        } else if (type == Type.DOUBLE) {
            value = Float.valueOf(text);
        } else if (type == Type.INTEGER) {
            value = Integer.valueOf(text);
        } else if (type == Type.LONG) {
            value = Long.valueOf(text);            
        } else {
            // should not happen!
            value = Integer.valueOf("0");
            System.err.println("NumericLiteral.bind : invalid number format " + text + getPos());
            return false;
        }
        return true;
    }

    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected) throws TypeException {
        if (!expected.isNumeric()) {
            throw new TypeException("NumericLiteral.typeCheck : invalid expected type " + expected.getName() + getPos());            
        }
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        return value;
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(text);
    }

    private static Type check(String text)
    {
        if (text.contains("e") | text.contains("E") | text.contains(".")) {
            return checkFloat(text);
        } else {
            return checkInt(text);
        }
    }

    private static Type checkFloat(String text)
    {
        try {
            Float.valueOf(text);
            return Type.FLOAT;
        } catch (NumberFormatException e) {
            // ok retry as Double
            try {
                Double.valueOf(text);
                return Type.DOUBLE;
            } catch (NumberFormatException e1) {
                // should not happen!
                System.err.println("NumericLiteral.checkFloat : invalid float format " + text);
                return Type.NUMBER;
            }
        }
    }

    private static Type checkInt(String text)
    {
        try {
            Integer.decode(text);
            return Type.INTEGER;
        } catch (NumberFormatException e) {
            // ok retry as Double
            try {
                Long.decode(text);
                return Type.LONG;
            } catch (NumberFormatException e1) {
                // should not happen!
                System.err.println("NumericLiteral.checkInt : invalid integer format " + text);
                return Type.NUMBER;
            }
        }
    }

    private String text;
    private Object value;
}
