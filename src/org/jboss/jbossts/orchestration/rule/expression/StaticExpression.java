package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * an expression which identifies aa static field reference
 */
public class StaticExpression extends Expression
{
    public StaticExpression(Type type, Token token, String[] path) {
        // type is the type of static field
        super(type, token);
        this.path = path;
        this.fieldNames = null;
        this.ownerTypes = null;
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
        // nothing to verify

        return true;
    }

    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected) throws TypeException {
        // look for a class whose name matches some initial segment of path
        Type rootType = typegroup.match(path);
        if (rootType == null) {
            throw new TypeException("StaticExpression.typeCheck : invalid path to static field " + getPath(path.length) + getPos());
        }
        // find out which path elements constitute the type name
        int length = rootType.getName().length();
        int pathLength = path.length;
        int idx = 0;
        int count;

        for (count = 0; count < length;) {
            if (idx != 0) {
                count += 1;
            }
            count += path[idx++].length();
        }

        // we need at least one left over element for the field name

        if (idx < pathLength - 1) {
            throw new TypeException("StaticExpression.typeCheck : invalid static field name " + getPath(path.length) + getPos());
        }

        int fieldCount = pathLength - idx;
        fieldNames = new String[fieldCount];
        fields = new Field[fieldCount];
        ownerTypes = new Type[fieldCount];
        int fieldIdx;
        for (fieldIdx = 0; fieldIdx < fieldCount; fieldIdx++) {
            fieldNames[fieldIdx] = path[idx++];
        }
        Type valueType = rootType;
        for (idx = 0; idx < fieldCount; idx++)
        {
            ownerTypes[idx] = valueType;
            Class clazz = valueType.getTargetClass();
            try {
                fields[idx] = clazz.getField(fieldNames[idx]);
            } catch (NoSuchFieldException e) {
                // oops
                throw new TypeException("StaticExpression.typeCheck : invalid field name " + fieldNames[idx] + getPos());
            }
            clazz = fields[idx].getType();
            valueType = typegroup.ensureType(clazz);
        }
        type = valueType;
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("StaticExpression.typeCheck : invalid expected return type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        try {
            Object value = fields[0].get(null);

            for (int i = 1; i < ownerTypes.length; i++) {
                if (value == null) {
                    throw new ExecuteException("StaticExpression.interpret : attempted field indirection through null value " + token.getText() + getPos());
                }
                value = fields[i].get(value);
            }
            return value;
        } catch (ExecuteException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new ExecuteException("StaticExpression.interpret : error accessing field " + token.getText() + getPos(), e);
        } catch (Exception e) {
            throw new ExecuteException("StaticExpression.interpret : unexpected exception accessing field " + token.getText() + getPos(), e);
        }
    }

    public String getPath(int len)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(path[0]);

        for (int i = 1; i < len; i++) {
            buffer.append(".");
            buffer.append(path[i]);
        }
        return buffer.toString();
    }

    public void writeTo(StringWriter stringWriter) {
        String sepr = "";
        for (String element : path) {
            stringWriter.write(sepr);
            stringWriter.write(element);
            sepr = ".";
        }
    }

    /**
     * the list of path components which may include package qualifiers, the class name, the
     * field name and subordinate field references
     */

    private String path[];
    private String fieldNames[];
    private Field fields[];
    private Type ownerTypes[];
}