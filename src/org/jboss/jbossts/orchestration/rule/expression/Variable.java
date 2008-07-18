package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * an expression which identifies a variable occurring either as an LVALUE on the LHS of an event
 * binding in the rule's event or as an RVALUE mentioned in the RHS of an event binding or in thre
 * rule's conditon or action.
 */
public class Variable extends Expression
{
    public Variable(Type type, Token token) {
        super(type, token);
        this.name = token.getText();
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
        // ensure that there is a binding with this name

        Binding binding = bindings.lookup(name);

        if (binding == null) {
            System.err.println("VarExpresssion.bind : unbound variable " + name + getPos());                
            return false;
        }
        // if the binding has a defined type and this has an undefined one then adopt it

        if (type.isUndefined()) {
            Type bindingType = binding.getType();
            if (!bindingType.isUndefined()) {
                this.type = bindingType;
            }
        }

        return true;
    }

    private String name;
}
