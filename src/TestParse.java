import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jboss.jbossts.orchestration.rule.grammar.ECATokenLexer;
import org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser;

public class TestParse
{
    public static void main(String[] args)
    {
        CommonTree result = null;
        for (String arg : args) {
            System.out.println("Parsing : " + arg);
            try {
                ECATokenLexer lexer = new ECATokenLexer(new ANTLRStringStream(arg));
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                ECAGrammarParser parser = new ECAGrammarParser(tokenStream);
                ECAGrammarParser.eca_rule_return eca_rule = parser.eca_rule();
                result = (CommonTree) eca_rule.getTree();
            } catch (RecognitionException e) {
                // bad rule event
                System.err.println(": error parsing arg");
            }
        }
    }
}
