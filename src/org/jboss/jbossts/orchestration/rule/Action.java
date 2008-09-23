package org.jboss.jbossts.orchestration.rule;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.expression.ExpressionHelper;
import org.jboss.jbossts.orchestration.rule.expression.Expression;
import org.jboss.jbossts.orchestration.rule.grammar.ECATokenLexer;
import org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser;
import org.jboss.jbossts.orchestration.rule.exception.ParseException;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;

import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;

/**
 * class which represents a rule action comprising a void expression
 */
public class Action extends RuleElement
{
    public static Action create(Rule rule, CommonTree actionTree)
            throws TypeException
    {
        Action action = new Action(rule, actionTree);
        return action;
    }

    public static Action create(Rule rule, String text)
            throws ParseException, TypeException
    {
        if ("".equals(text)) {
            return new Action(rule);
        }
        try {
            ECATokenLexer lexer = new ECATokenLexer(new ANTLRStringStream(text));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            ECAGrammarParser parser = new ECAGrammarParser(tokenStream);
            ECAGrammarParser.eca_action_return parse = parser.eca_action();
            CommonTree actionTree = (CommonTree) parse.getTree();
            Action action = new Action(rule, actionTree);
            return action;
        } catch (RecognitionException e) {
            throw new ParseException("org.jboss.jbossts.orchestration.rule.Action : error parsing action " + text);
        }
    }
    protected Action(Rule rule, CommonTree actionTree) throws TypeException
    {
        super(rule);
        if (actionTree.getToken().getType() == ECAGrammarParser.NOTHING) {
            this.action = new ArrayList<Expression>();
        } else {
            this.action = ExpressionHelper.createExpressionList(this.getBindings(), actionTree, Type.VOID);
        }
    }

    protected Action(Rule rule)
    {
        super(rule);
        this.action = null;
    }

    public void typeCheck() throws TypeException {
        if (action != null) {
            for (Expression expr : action) {
                expr.typeCheck(getBindings(), getTypeGroup(), Type.VOID);
            }
        }
    }

    public void interpret(Rule.BasicHelper helper)
            throws ExecuteException
    {
        if (action != null) {
            for (Expression expr : action) {
                expr.interpret(helper);
            }
        }
    }

    public void writeTo(StringWriter stringWriter)
    {
        if (action == null) {
            stringWriter.write("DO   NOTHING");
        } else {
            String prefix = "DO   ";
            for (Expression expr : action) {
                stringWriter.write(prefix);
                expr.writeTo(stringWriter);
                prefix = ",\n     ";
            }
        }
        stringWriter.write("\n");
    }

    private List<Expression> action;
}