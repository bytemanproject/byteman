package org.jboss.jbossts.orchestration.rule;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;

import java.io.StringWriter;

/**
 * generic class implemented by rule events, conditions and actions which gives them
 * access to the rule context and provides them with common behaviours
 */
public abstract class RuleElement {
    protected RuleElement(Rule rule)
    {
        this.rule = rule;
    }

    protected Rule rule;

    protected TypeGroup getTypeGroup()
    {
        return rule.getTypeGroup();
    }

    protected Bindings getBindings()
    {
        return rule.getBindings();
    }

    public abstract Type typeCheck(Type expected) throws TypeException;

    public String toString()
    {
        StringWriter stringWriter = new StringWriter();
        writeTo(stringWriter);
        return stringWriter.toString();
    }

    public abstract void writeTo(StringWriter stringWriter);
}
