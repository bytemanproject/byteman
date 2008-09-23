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
 * A binary logical operator expression
 */
public class BooleanLiteral extends Expression
{
    private boolean value;
    
    public BooleanLiteral(Token token, Boolean value)
    {
        super(Type.Z, token);
        this.value = value;
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
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected) throws TypeException {
        type = Type.Z;
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("BooleanLiteral.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        return value;
    }

    public void writeTo(StringWriter stringWriter) {
        if (value) {
            stringWriter.write("TRUE");
        } else {
            stringWriter.write("FALSE");
        }
    }
}