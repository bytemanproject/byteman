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

/**
 * Created by IntelliJ IDEA.
 * User: adinn
 * Date: 17-Jul-2008
 * Time: 15:20:18
 * To change this template use File | Settings | File Templates.
 */
public class Condition
{
    public static Condition create(TypeGroup typeGroup, Bindings bindings, String text)
    {
        if ("".equals(text)) {
            return new Condition(typeGroup, bindings);
        }
        try {
            ECATokenLexer lexer = new ECATokenLexer(new ANTLRStringStream(text));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            ECAGrammarParser parser = new ECAGrammarParser(tokenStream);
            ECAGrammarParser.condition_return parse = parser.condition();
            CommonTree conditionTree = (CommonTree) parse.getTree();
            Condition condition = new Condition(typeGroup,  bindings, conditionTree);
            return condition;
        } catch (RecognitionException e) {
            System.err.println("org.jboss.jbossts.orchestration.rule.event : error parsing condition " + text);
            return new Condition(typeGroup, bindings);
        }
    }

    protected Condition(TypeGroup typeGroup, Bindings bindings, CommonTree conditionTree)
    {
        this.typeGroup = typeGroup;
        this.bindings = bindings;
        this.condition = ExpressionHelper.createExpression(this.bindings, conditionTree, Type.BOOLEAN);
    }

    protected Condition(TypeGroup typeGroup, Bindings bindings)
    {
        this.typeGroup = typeGroup;
        this.bindings = bindings;
        this.condition = null;
    }

    private Expression condition;
    private TypeGroup typeGroup;
    private Bindings bindings;
}
