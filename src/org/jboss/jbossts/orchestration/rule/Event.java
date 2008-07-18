package org.jboss.jbossts.orchestration.rule;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.Token;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import static org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.*;
import org.jboss.jbossts.orchestration.rule.grammar.ECATokenLexer;
import org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser;
import org.jboss.jbossts.orchestration.rule.expression.Expression;
import org.jboss.jbossts.orchestration.rule.expression.ExpressionHelper;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;

/**
 * class which represents a rule event comprising of a set of abstract bindings of event variables to
 * evaluable expressions.
 */
public class Event {

    public static Event create(String text)
    {
        if ("".equals(text)) {
            return new Event();
        }

        try {
            ECATokenLexer lexer = new ECATokenLexer(new ANTLRStringStream(text));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            ECAGrammarParser parser = new ECAGrammarParser(tokenStream);
            ECAGrammarParser.event_return event_parse = parser.event();
            CommonTree eventTree = (CommonTree) event_parse.getTree();
            Event event = new Event(eventTree);
            return event;
        } catch (RecognitionException e) {
            System.err.println("org.jboss.jbossts.orchestration.rule.event : error parsing event " + text);
            return new Event();
        }
    }

    protected Event(CommonTree eventTree)
    {
        this.typeGroup = new TypeGroup();
        createBindings(eventTree);
    }

    protected Event()
    {
        this.typeGroup = new TypeGroup();
        this.bindings = new Bindings();
    }

    public boolean compile()
    {
        return true;
    }

    public Bindings getBindings()
    {
        return bindings;
    }

    private void createBindings(CommonTree eventTree)
    {
        Bindings bindings = new Bindings();
        boolean success = true;

        // we expect BINDINGS = (SEPR BINDING BINDINGS) | BINDING
        // where BINDING = (BIND BINDSYM EXPR)

        while (eventTree != null) {
            Token token = eventTree.getToken();
            int tokenType = token.getType();
            switch (tokenType) {
                case SEPR:
                {
                    success |= addBinding(bindings, (CommonTree)eventTree.getChild(0));
                    eventTree = (CommonTree)eventTree.getChild(1);
                }
                break;
                case ASSIGN:
                {
                    success |= addBinding(bindings, eventTree);
                    eventTree = null;
                }
                break;
                default:
                    System.err.println("Event.createBindings : unexpected token Type in binding list " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                    break;
            }
        }

        // if we had any errors we leave bindings null to ensure that we don't try to
        // compile this rule any further

        if (success) {
            this.bindings = bindings;
        }
    }

    private boolean addBinding(Bindings bindings, CommonTree bindingTree)
    {
        Token token = bindingTree.getToken();
        int tokenType = token.getType();

        if (tokenType != ASSIGN) {
            System.err.println("Event.createBindings : unexpected token Type in binding " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
            return false;
        }

        CommonTree varTree = (CommonTree)bindingTree.getChild(0);
        CommonTree exprTree = (CommonTree)bindingTree.getChild(1);

        Binding binding = createBinding(varTree);

        // don't allow current binding to be used when parsing the expression
        // but do use any type supplied for the binding

        Expression expr;

        if (binding == null) {
            // try expression anyway so we get as manyh errors as possible
            expr = ExpressionHelper.createExpression(bindings, exprTree);
        } else {
            expr = ExpressionHelper.createExpression(bindings, exprTree, binding.getType());
        }

        if (binding == null || expr == null) {
            // errors wil have been notified already
            return false;
        }

        if (bindings.lookup(binding.getName()) != null) {
            // oops rebinding not allowed
            System.err.println("Event.createBindings : rebinding disallowed for variable " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
            return false;
        }

        binding.setValue(expr);
        bindings.append(binding);

        return true;
    }

    public Binding createBinding(CommonTree varTree)
    {
        Token token = varTree.getToken();
        int tokenType = token.getType();

        // we expect either (COLON SYMBOL SYMBOL) or SYMBOL
        switch (tokenType) {
            case SYMBOL:
            {
                return new Binding(token.getText());
            }
            case COLON:
            {
                CommonTree child0 = (CommonTree)varTree.getChild(0);
                CommonTree child1 = (CommonTree)varTree.getChild(1);
                if (child0.getToken().getType() != SYMBOL) {
                    System.err.println("Event.createBindings : unexpected token Type in variable declaration" + child0.getToken().getType() + " for token " + child0.getToken().getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                    return null;
                } else if (child1.getToken().getType() != SYMBOL) {
                    System.err.println("Event.createBindings : unexpected token Type in variable type declaration" + child1.getToken().getType()  + " for token " + child1.getToken().getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                    return null;
                }
                String typeName = child1.getText();
                Type type = typeGroup.lookup(typeName);
                if (type == null) {
                    type = typeGroup.create(typeName);
                    if (type == null) {
                        System.err.println("Event.createBindings : incompatible type in declaration of variable " + child1.getToken().getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                        return null;
                    }
                }
                return new Binding(child0.getText(), type);
            }
            default:
            {
                System.err.println("Event.createBindings : unexpected token Type in binding variable declaration" + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                return null;
            }
        }
    }

    public TypeGroup getTypeGroup()
    {
        return typeGroup;
    }

    private TypeGroup typeGroup;
    private Bindings bindings;
}
