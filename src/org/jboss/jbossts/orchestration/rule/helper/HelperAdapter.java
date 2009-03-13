package org.jboss.jbossts.orchestration.rule.helper;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;

/**
 * This interface defines the methods which need to be added to a helper class in order for it
 * to plug in to the rule system. In the case of the default helper class, Helper, this interface
 * is implemented by a pre-defined subclass, InterpretedHelper which interprets the rule parse
 * tree. Given any user-supplied helper class the rule compiler can generate a HelperAdapter class
 * which interprets the rule tree and invokes builtin methods using reflection. The compiler can
 * also generate a HelperAdapter whose bind(), test() and fire() methods are compiled from bytecode
 * derived from the parse trees of, respectively, the rule's event, condition and action. Bytecode
 * compilation is applicable to rules which employ the default helper as well as rules which employ
 * user-defined helpers.
 */
public interface HelperAdapter
{
    public void execute(Bindings bindings, Object recipient, Object[] args)
            throws ExecuteException;
    public void bindVariable(String name, Object value);
    public Object getBinding(String name);
    public String getName();
}
