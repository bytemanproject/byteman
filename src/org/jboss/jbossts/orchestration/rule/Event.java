package org.jboss.jbossts.orchestration.rule;

import org.antlr.runtime.tree.CommonTree;
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
import org.jboss.jbossts.orchestration.rule.exception.ParseException;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.StringWriter;

/**
 * class which represents a rule event comprising of a set of abstract bindings of event variables to
 * evaluable expressions.
 */
public class Event extends RuleElement {

    public static Event create(Rule rule, CommonTree eventTree)
            throws TypeException
    {
        Event event = new Event(rule, eventTree);
        return event;
    }

    public static Event create(Rule rule, String text)
            throws ParseException, TypeException
    {
        if ("".equals(text)) {
            return new Event(rule);
        }

        try {
            ECATokenLexer lexer = new ECATokenLexer(new ANTLRStringStream(text));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            ECAGrammarParser parser = new ECAGrammarParser(tokenStream);
            ECAGrammarParser.eca_event_return event_parse = parser.eca_event();
            CommonTree eventTree = (CommonTree) event_parse.getTree();
            Event event = new Event(rule, eventTree);
            return event;
        } catch (RecognitionException e) {
            throw new ParseException("org.jboss.jbossts.orchestration.rule.Event : error parsing event " + text, e);
        }
    }

    protected Event(Rule rule, CommonTree eventTree) throws TypeException
    {
        super(rule);
        createBindings(eventTree);
    }

    protected Event(Rule rule)
    {
        super(rule);
    }

    public boolean compile()
    {
        return true;
    }

    public Bindings getBindings()
    {
        return rule.getBindings();
    }

    public void typeCheck() throws TypeException {
        Iterator<Binding> iterator = getBindings().iterator();
        while (iterator.hasNext()) {
            Binding binding = iterator.next();

            typeCheck(binding);
        }
    }

    private void typeCheck(Binding binding)
            throws TypeException
    {
        binding.typeCheck(getBindings(), getTypeGroup());
    }

    private void createBindings(CommonTree eventTree) throws TypeException
    {
        Bindings bindings = getBindings();

        // we bundle exceptions from each binding to report more than just the first error

        List<TypeException> exceptions = new ArrayList<TypeException>();

        // we expect BINDINGS = (SEPR BINDING BINDINGS) | BINDING
        // where BINDING = (BIND BINDSYM EXPR)

        while (eventTree != null) {
            try {
                Token token = eventTree.getToken();
                int tokenType = token.getType();
                switch (tokenType) {
                    case SEPR:
                    {
                        // update before we risk an exception
                        CommonTree child0 = (CommonTree)eventTree.getChild(0);
                        eventTree = (CommonTree)eventTree.getChild(1);
                        addBinding(bindings, child0);
                    }
                    break;
                    case ASSIGN:
                    {
                        // update before we risk an exception
                        CommonTree saveTree = eventTree;
                        eventTree = null;
                        addBinding(bindings, saveTree);
                    }
                    break;
                    default:
                    {
                        eventTree = null;
                        String message = "Event.createBindings : unexpected token Type in binding list " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine();
                        throw new TypeException(message);
                    }
                }
            } catch (TypeException te) {
                exceptions.add(te);
            }
        }

        if (!exceptions.isEmpty()) {
            if (exceptions.size() == 1) {
                throw exceptions.get(0);
            } else {
                StringBuffer buffer = new StringBuffer();
                buffer.append("Event.createBindings : invalid event bindings");
                for (TypeException exception : exceptions) {
                    buffer.append("\n\t");
                    buffer.append(exception.getMessage());
                }
                throw new TypeException(buffer.toString());
            }
        }
    }

    private void addBinding(Bindings bindings, CommonTree bindingTree) throws TypeException
    {
        Token token = bindingTree.getToken();
        int tokenType = token.getType();

        if (tokenType != ASSIGN) {
            String message = "Event.createBindings : unexpected token Type in binding " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine();
            throw new TypeException(message);
        }

        CommonTree varTree = (CommonTree)bindingTree.getChild(0);
        CommonTree exprTree = (CommonTree)bindingTree.getChild(1);
        Binding binding;

        binding = createBinding(varTree);

        // don't allow current binding to be used when parsing the expression
        // but do use any type supplied for the binding

        Expression expr;

        expr = ExpressionHelper.createExpression(bindings, exprTree, binding.getType());

        if (bindings.lookup(binding.getName()) != null) {
            // oops rebinding not allowed
            String message = "Event.createBindings : rebinding disallowed for variable " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine();
            throw new TypeException(message);
        }

        binding.setValue(expr);
        bindings.append(binding);
    }

    public Binding createBinding(CommonTree varTree) throws TypeException
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
                    throw new TypeException("Event.createBindings : unexpected token Type in variable declaration" + child0.getToken().getType() + " for token " + child0.getToken().getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                } else if (child1.getToken().getType() != SYMBOL) {
                    throw new TypeException("Event.createBindings : unexpected token Type in variable type declaration" + child1.getToken().getType()  + " for token " + child1.getToken().getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                }
                String typeName = child1.getText();
                Type type = getTypeGroup().create(typeName);
                if (type == null) {
                    throw new TypeException("Event.createBindings : incompatible type in declaration of variable " + child1.getToken().getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                }
                return new Binding(child0.getText(), type);
            }
            default:
            {
                throw new TypeException("Event.createBindings : unexpected token Type in binding variable declaration" + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
            }
        }
    }

    public void interpret(Rule.BasicHelper helper)
            throws ExecuteException
    {
        Iterator<Binding> iterator = getBindings().iterator();

        while (iterator.hasNext()) {
            Binding binding = iterator.next();

            if (binding.isVar()) {
                Object value = binding.getValue().interpret(helper);
                helper.bind(binding.getName(), value);
            }
        }

    }

    public void writeTo(StringWriter stringWriter)
    {
        String prefix = "BIND ";
        Iterator<Binding> iter = getBindings().iterator();
        while (iter.hasNext()) {
            Binding binding = iter.next();
            stringWriter.write(prefix);
            binding.writeTo(stringWriter);
            prefix = ",\n     ";
        }
        stringWriter.write("\n");
    }
}
