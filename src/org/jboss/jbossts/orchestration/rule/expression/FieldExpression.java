package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * an expression which identifies an instance field reference
 */
public class FieldExpression extends Expression
{
    public FieldExpression(Type type, Token token, String ref, String[] fieldNames) {
        // type is the type of last field
        // ownerType[i] is the type of the owner of field[i]
        // so ownerType[0] is the type of ref;
        super(type, token);
        this.ref = ref;
        this.fieldNames = fieldNames;
        int len = fieldNames.length;
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

    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected) throws TypeException {
        // check the owner type is defined and then start searching for
        // the types of each field referenced from it

        ownerType[0] = Type.dereference(ownerType[0]);
        if (ownerType[0].isUndefined()) {
            throw new TypeException("FieldExpresssion.typeCheck : unbound instance " + ref + getPos());
        }

        Class ownerClazz = ownerType[0].getTargetClass();
        Class valueClass = null;
        Type valueType = null;
        int fieldCount = fieldNames.length;

        fields = new Field[fieldCount];

        for (int i = 0; i < fieldCount; i++) {
            if (i != 0) {
                ownerType[i] = valueType;
                ownerClazz = valueType.getTargetClass();
            }
            String fieldName = fieldNames[i];
            try {
                fields[i]  = ownerClazz.getField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new TypeException("FieldExpresssion.typeCheck : invalid field reference " + fieldName + getPos());
            }

            valueClass = fields[i].getType();
            valueType = typegroup.ensureType(valueClass);
        }

        type = valueType;

        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("FieldExpresssion.typeCheck : invalid expected type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException
    {
        try {
            // TODO the reference should really be an expression?
            Object value = helper.getBinding(ref);

            int fieldCount = fields.length;
            for (int i = 0; i < fieldCount; i++) {
                if (value == null) {
                    throw new ExecuteException("FieldExpression.interpret : attempted field indirection through null value " + token.getText() + getPos());
                }
                value = fields[i].get(value);
            }

            return value;
        } catch (ExecuteException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new ExecuteException("FieldExpression.interpret : error accessing field " + token.getText() + getPos(), e);
        } catch (Exception e) {
            throw new ExecuteException("FieldExpression.interpret : unexpected exception accessing field " + token.getText() + getPos(), e);
        }
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(ref);
        for (String field : fieldNames) {
            stringWriter.write(".");
            stringWriter.write(field);
        }
    }

    private String ref;
    private String[] fieldNames;
    private Type[] ownerType;
    private Field[] fields;
}