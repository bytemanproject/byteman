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
 * an expression which identifies a character string.
 */
public class StringLiteral extends Expression
{
    public StringLiteral(Rule rule, Token token)
    {
        super(rule, Type.STRING, token);

        this.text = token.getText();
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public boolean bind() {
        return true;
    }

    public Type typeCheck(Type expected) throws TypeException {
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        return text;
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(text);
    }

    private String text;
}
