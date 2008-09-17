package org.jboss.jbossts.orchestration.rule.expression;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.Token;
import static org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.*;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.expression.*;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;

import java.io.StringWriter;

/**
 * abstract class representing an evaluable expression. this is used in all 3 elements of ECA rules:
 * as the value part of each of the event bindings in the binding list comprising an ECA rule event;
 * as the condition expression of an ECA rule condition;and as an element of the actions list in an
 * ECA rule action.
 */
public abstract class Expression
{
    /**
     * Create a new expression.
     * @param type the current type for this expression.
     */
    protected Expression(Type type, Token token)
    {
        this.type = type;
        this.token = token;
        if (token != null) {
            this.charPos = token.getCharPositionInLine();
            this.line = token.getLine();
        } else {
            this.charPos = 0;
            this.line = 0;
        }
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @param bindings the set of bindings in place at the point of evaluation of this expression
     * @return true if all variables in this expression are bound and no type mismatches have
     * been detected during inference/validation.
     */
    public abstract boolean bind(Bindings bindings);

    public String getPos()
    {
        return " @ line " + line + "." + charPos;
    }

    public Type getType()
    {
        return type;
    }

    /**
     * ensure that all type references in the expression and its component expressions
     * can be resolved, that the type of the expression is well-defined and that it is
     * compatible with the type expected in the context in which it occurs.
     * @param bindings the bound variable in scope at the point where the expression is
     * to be evaluate
     * @param typegroup the set of types employed by the rule
     * @param expected the type expected for the expression in the contxt in which it occurs. this
     * may be void but shoudl not be undefined at the point where type checking is performed.
     * @return
     * @throws TypeException
     */
    public abstract Type typeCheck(Bindings bindings, TypeGroup typegroup, Type expected)
            throws TypeException;

    /**
     * evaluate the expression by interpreting the expression tree
     * @param helper an execution context associated with the rule whcih contains a map of
     * current bindings for rule variables and another map of their declared types both of which
     * are indexed by varoable name. This includes entries for the helper (name "-1"), the
     * recipient if the trigger method is not static (name "0") and the trigger method arguments
     * (names "1", ...)
     * @return  the result of evaluation as an Object
     * @throws org.jboss.jbossts.orchestration.rule.exception.ExecuteException
     */
    public abstract Object interpret(Rule.BasicHelper helper) throws ExecuteException;

    public abstract void writeTo(StringWriter stringWriter);

    protected Type type;
    protected int charPos;
    protected int line;
    protected Token token;
}
