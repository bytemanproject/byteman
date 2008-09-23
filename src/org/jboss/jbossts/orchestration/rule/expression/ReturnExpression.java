package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.exception.EarlyReturnException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

import java.io.StringWriter;

/**
 *  A return expression which is used in a rule action to cause a return from the rule trigger
 * method, supplying a return value where appropriate.
 */

public class ReturnExpression extends Expression
{
    private Expression returnValue;

    public ReturnExpression(Token token, Expression returnValue)
    {
        // the trigger method may return any old tyep but the return expression can only occur
        // at the top level in a rule action seuqence so it is actually a VOID expression

        super(Type.VOID, token);

        this.returnValue = returnValue;
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
        if (returnValue != null) {
            // ensure the return value expression has all its bindings
            return returnValue.bind(bindings);
        }
        return true;
    }

    /**
     * ensure that all type references in the expression and its component expressions
     * can be resolved, that the type of the expression is well-defined and that it is
     * compatible with the type expected in the context in which it occurs.
     *
     * @param bindings  the bound variable in scope at the point where the expression is
     *                  to be evaluate
     * @param typegroup the set of types employed by the rule
     * @param expected  the type expected for the expression in the contxt in which it occurs. this
     *                  may be void but shoudl not be undefined at the point where type checking is performed.
     * @return
     * @throws org.jboss.jbossts.orchestration.rule.exception.TypeException
     *
     */
    public Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected) throws TypeException {
        // we need to check the returnValue expression against the type of the trigger method
        Binding returnBinding = bindings.lookup("$!");
        Type returnBindingType = (returnBinding != null ? returnBinding.getType() : Type.VOID);
        if (returnValue == null && !returnBindingType.isVoid()) {
            throw new TypeException("ReturnExpression.typeCheck : return expression must supply argument when triggered from method with return type " + returnBindingType.getName() + getPos());
        } else if (returnValue != null) {
            if (returnBindingType.isVoid()) {
                throw new TypeException("ReturnExpression.typeCheck : return expression must not supply argument when triggered from void method" + getPos());
            }
            returnValue.typeCheck(bindings, typegroup,  returnBindingType);
        }
        return type;
    }

    /**
     * evaluate the expression by interpreting the expression tree
     *
     * @param helper an execution context associated with the rule whcih contains a map of
     *               current bindings for rule variables and another map of their declared types both of which
     *               are indexed by varoable name. This includes entries for the helper (name "-1"), the
     *               recipient if the trigger method is not static (name "0") and the trigger method arguments
     *               (names "1", ...)
     * @return the result of evaluation as an Object
     * @throws org.jboss.jbossts.orchestration.rule.exception.ExecuteException
     *
     */
    public Object interpret(Rule.BasicHelper helper) throws ExecuteException
    {
        // time to take an early bath -- the code compield into the trigger method should
        // catch this and return as appropriate
        if (returnValue != null) {
            Object value = returnValue.interpret(helper);
            throw new EarlyReturnException("return from " + helper.getName(), value);
        } else {
            throw new EarlyReturnException("return from " + helper.getName());
        }
    }

    public void writeTo(StringWriter stringWriter) {
        if (returnValue != null) {
            stringWriter.write("RETURN ");
            returnValue.writeTo(stringWriter);
        } else {
            stringWriter.write("RETURN");
        }
    }
}
