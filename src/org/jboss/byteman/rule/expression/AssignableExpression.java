package org.jboss.byteman.rule.expression;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.compiler.StackHeights;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.jboss.byteman.rule.helper.Helper;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.type.Type;
import org.objectweb.asm.MethodVisitor;

import java.io.StringWriter;

/**
 * an expression which can appear on the left hand side of an assignment expression as well as in any
 * other expression context. assignable expressions provide extra methods which support assignment,
 * either interpreted or compiled, on top of the usual evaluation methods.
 */
public abstract class AssignableExpression extends Expression
{
    /**
     * Create a new expression.
     *
     * @param type the current type for this expression.
     */
    protected AssignableExpression(Rule rule, Type type, ParseNode token) {
        super(rule, type, token);
    }

    /**
     * execute an assignment to the referenced location by interpretation of the expression,
     * using the object passed in this call
     * @param helperAdapter an execution context associated with the rule which contains a map of
     * current bindings for rule variables and another map of their declared types both of which
     * are indexed by variable name. This includes entries for the helper (name "-1"), the
     * recipient if the trigger method is not static (name "0") and the trigger method arguments
     * (names "1", ...)
     * @return  the result of evaluation as an Object
     * @throws org.jboss.byteman.rule.exception.ExecuteException
     */
    public abstract Object interpretAssign(HelperAdapter helperAdapter, Object value) throws ExecuteException;

    /**
     * compile an assignment to the referenced location using the value on the top of the
     * Java stack.

     * @param mv
     * @param currentStackHeights
     * @param maxStackHeights
     * @throws CompileException
     */
    public abstract void compileAssign(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException;
}
