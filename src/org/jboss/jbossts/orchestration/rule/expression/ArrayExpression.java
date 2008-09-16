package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import java.util.List;
import java.util.Iterator;
import java.io.StringWriter;
import java.lang.reflect.Array;

/**
 * an expression which identifies an array reference.
 */

public class ArrayExpression extends Expression
{

    public ArrayExpression(Type type, Token token, Expression arrayRef, List<Expression> idxList)
    {
        super(type, token);
        this.arrayRef = arrayRef;
        this.idxList = idxList;
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
        // we  have to make sure that any names occuring in the array reference are bound
        // and that the index expressions contain valid bindings
        boolean valid = true;

        valid = arrayRef.bind(bindings);

        Iterator<Expression> iterator = idxList.iterator();

        while (iterator.hasNext()) {
            valid &= iterator.next().bind(bindings);
        }

        return valid;
    }

    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected) throws TypeException {
        Type arrayType = arrayRef.typeCheck(bindings, typegroup, Type.UNDEFINED);
        Type nextType = arrayType;
        for (Expression expr : idxList) {
            if (!nextType.isArray()) {
                throw new TypeException("ArrayExpression.typeCheck : invalid type for array dereference " + nextType.getName() + getPos());
            }
            nextType = nextType.getBaseType();
            expr.typeCheck(bindings, typegroup, Type.N);
        }
        type = nextType;
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("ArrayExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        // evaluate the array expression then evaluate each index expression in turn and
        // dereference to access the array element

        try {
            Object value = arrayRef.interpret(helper);
            Type nextType = arrayRef.getType();
            for (Expression expr : idxList) {
                int idx = ((Number) expr.interpret(helper)).intValue();
                if (value == null) {
                    throw new ExecuteException("ArrayExpression.interpret : attempted array indirection through null value " + arrayRef.token.getText() + getPos());
                }
                value = Array.get(value, idx);
                nextType = nextType.getBaseType();
            }

            return value;
        } catch (ExecuteException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new ExecuteException("ArrayExpression.interpret : failed to evaluate expression " + arrayRef.token.getText() + getPos(), e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ExecuteException("ArrayExpression.interpret : invalid index for array " + arrayRef.token.getText() + getPos(), e);
        } catch (ClassCastException e) {
            throw new ExecuteException("ArrayExpression.interpret : invalid index dereferencing array " + arrayRef.token.getText() + getPos(), e);
        } catch (Exception e) {
            throw new ExecuteException("ArrayExpression.interpret : unexpected exception dereferencing array " + arrayRef.token.getText() + getPos(), e);
        }
    }

    public void writeTo(StringWriter stringWriter) {
        arrayRef.writeTo(stringWriter);
        for (Expression expr : idxList) {
            stringWriter.write("[");
            expr.writeTo(stringWriter);
            stringWriter.write("]");
        }
    }

    Expression arrayRef;
    List<Expression> idxList;
}
