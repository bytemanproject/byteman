package org.jboss.jbossts.orchestration.rule.expression;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.Token;
import static org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.*;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.expression.*;
import org.jboss.jbossts.orchestration.rule.type.Type;

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
        this.charPos = token.getCharPositionInLine();
        this.line = token.getLine();
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
        return " @ " + line + "." + charPos;
    }

    public Type getType()
    {
        return type;
    }

    /**
     * the name of the
     */
    protected Type type;
    protected int charPos;
    protected int line;
}
