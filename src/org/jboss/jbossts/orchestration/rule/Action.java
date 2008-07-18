package org.jboss.jbossts.orchestration.rule;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.expression.ExpressionHelper;
import org.jboss.jbossts.orchestration.rule.expression.Expression;
import org.jboss.jbossts.orchestration.rule.grammar.ECATokenLexer;
import org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: adinn
 * Date: 17-Jul-2008
 * Time: 15:20:18
 * To change this template use File | Settings | File Templates.
 */
public class Action
{
    public static Action create(TypeGroup typeGroup, Bindings bindings, String text)
    {
        if ("".equals(text)) {
            return new Action(typeGroup, bindings);
        }
        try {
            ECATokenLexer lexer = new ECATokenLexer(new ANTLRStringStream(text));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            ECAGrammarParser parser = new ECAGrammarParser(tokenStream);
            ECAGrammarParser.action_return parse = parser.action();
            CommonTree actionTree = (CommonTree) parse.getTree();
            Action action = new Action(typeGroup, bindings, actionTree);
            return action;
        } catch (RecognitionException e) {
            System.err.println("org.jboss.jbossts.orchestration.rule.event : error parsing action " + text);
            return new Action(typeGroup, bindings);
        }
    }
    protected Action(TypeGroup typeGroup, Bindings bindings, CommonTree actionTree)
    {
        this.typeGroup = typeGroup;
        this.bindings = bindings;
        this.action = ExpressionHelper.createExpressionList(this.bindings, actionTree, Type.VOID);
    }

    protected Action(TypeGroup typeGroup, Bindings bindings)
    {
        this.typeGroup = typeGroup;
        this.bindings = bindings;
    }

    private List<Expression> action;
    private TypeGroup typeGroup;
    private Bindings bindings;
}