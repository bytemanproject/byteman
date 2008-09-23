package org.jboss.jbossts.orchestration.rule;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.expression.ExpressionHelper;
import org.jboss.jbossts.orchestration.rule.expression.Expression;
import org.jboss.jbossts.orchestration.rule.expression.BooleanExpression;
import org.jboss.jbossts.orchestration.rule.expression.BooleanLiteral;
import org.jboss.jbossts.orchestration.rule.grammar.ECATokenLexer;
import org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser;
import org.jboss.jbossts.orchestration.rule.exception.ParseException;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;

import java.io.StringWriter;

/**
 * class which represents a rule condition comprising a boolean expression
 */
public class Condition extends RuleElement
{
    public static Condition create(Rule rule, CommonTree conditionTree) throws TypeException
    {
        Condition condition = new Condition(rule, conditionTree);
        return condition;
    }

    public static Condition create(Rule rule, String text) throws ParseException, TypeException
    {
        if ("".equals(text)) {
            return new Condition(rule);
        }
        try {
            ECATokenLexer lexer = new ECATokenLexer(new ANTLRStringStream(text));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            ECAGrammarParser parser = new ECAGrammarParser(tokenStream);
            ECAGrammarParser.eca_condition_return parse = parser.eca_condition();
            CommonTree conditionTree = (CommonTree) parse.getTree();
            Condition condition = new Condition(rule, conditionTree);
            return condition;
        } catch (RecognitionException e) {
            throw new ParseException("org.jboss.jbossts.orchestration.rule.Condition : error parsing condition " + text, e);
        }
    }

    protected Condition(Rule rule, CommonTree conditionTree)
            throws TypeException
    {
        super(rule);
        Token token = conditionTree.getToken();
        if (token.getType() == ECAGrammarParser.TRUE) {
            this.condition = new BooleanLiteral(token, true);
        } else if (token.getType() == ECAGrammarParser.FALSE) {
            this.condition = new BooleanLiteral(token, false);
        } else {
            this.condition = ExpressionHelper.createExpression(rule.getBindings(), conditionTree, Type.BOOLEAN);
        }
    }
    
    protected Condition(Rule rule)
    {
        super(rule);
        this.condition = null;
    }

    public void typeCheck() throws TypeException {
        if (condition != null) {
            condition.typeCheck(getBindings(), getTypeGroup(), Type.Z);
        }
    }

    public boolean interpret(Rule.BasicHelper helper)
            throws ExecuteException
    {
        Boolean result = (Boolean)condition.interpret(helper);

        return result;
    }

    public void writeTo(StringWriter stringWriter)
    {
        if (condition == null) {
            stringWriter.write("IF   TRUE");
        } else {
            stringWriter.write("IF   ");
            condition.writeTo(stringWriter);
        }
        stringWriter.write("\n");
    }

    private Expression condition;
}
