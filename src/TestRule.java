import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jboss.jbossts.orchestration.rule.grammar.ECATokenLexer;
import org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser;
import org.jboss.jbossts.orchestration.rule.Condition;
import org.jboss.jbossts.orchestration.rule.Event;
import org.jboss.jbossts.orchestration.rule.Action;

public class TestRule
{
    public static void main(String[] args)
    {
        Event event = null;
        Condition condition = null;
        Action action = null;
        for (int i = 0; i < args.length ; i++) {
            if ("-event".equals(args[i])) {
                System.out.println("Creating event from " + args[++i]);
                try {
                    event = Event.create(args[i]);
                } catch (Throwable th) {
                    // bad rule event
                    System.err.println(": error " + th);
                }
            } else if ("-condition".equals(args[i])) {
                if (event != null) {
                    System.out.println("Creating condition from " + args[++i]);
                    try {
                        condition = Condition.create(event.getTypeGroup(), event.getBindings(), args[i]);
                    } catch (Throwable th) {
                        // bad rule event
                        System.err.println(": error " + th);
                    }
                } else {
                     System.out.println("No event to create condition from " + args[++i]);
                }
            } else if ("-action".equals(args[i])) {
                if (event != null) {
                    System.out.println("Creating action from " + args[++i]);
                    try {
                        action = Action.create(event.getTypeGroup(), event.getBindings(), args[i]);
                    } catch (Throwable th) {
                        // bad rule event
                        System.err.println(": error " + th);
                    }
                } else {
                     System.out.println("No event to create action from " + args[++i]);
                }
            }
        }
    }
}