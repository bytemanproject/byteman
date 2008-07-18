package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.Token;

/**
 * an expression which identifies an instance field reference
 */
public class FieldExpression extends Expression
{
    public FieldExpression(Type type, Token token, String ref, String[] fields) {
        // type is the type of last field
        // ownerType[i] is the type of the owner of field[i]
        // so ownerType[0] is the type of ref;
        super(type, token);
        this.ref = ref;
        this.fields = fields;
        int len = fields.length;
        this.ownerType = new Type[len];
        for (int i = 0; i < len; i++) {
            ownerType[i] = Type.UNDEFINED;
        }
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

        Binding binding = bindings.lookup(ref);

        if (binding == null) {
            System.err.println("FieldExpresssion.bind : unbound instance " + ref + getPos());
            return false;
        }

        // use the binding type to type ref

        if (ownerType[0].isUndefined()) {
            Type bindingType = binding.getType();
            ownerType[0] = bindingType;
        }

        return true;
    }

    private String ref;
    private String[] fields;
    private Type[] ownerType;
}