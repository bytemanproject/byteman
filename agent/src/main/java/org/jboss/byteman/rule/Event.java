/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
* @authors Andrew Dinn
*/
package org.jboss.byteman.rule;

import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.binding.Binding;
import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.grammar.ParseNode;
import static org.jboss.byteman.rule.grammar.ParseNode.*;
import org.jboss.byteman.rule.grammar.ECATokenLexer;
import org.jboss.byteman.rule.grammar.ECAGrammarParser;
import org.jboss.byteman.rule.expression.Expression;
import org.jboss.byteman.rule.expression.ExpressionHelper;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.exception.ParseException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.objectweb.asm.MethodVisitor;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.StringWriter;
import java.io.StringReader;

import java_cup.runtime.Symbol;

/**
 * class which represents a rule event comprising of a set of abstract bindings of event variables to
 * evaluable expressions.
 */
public class Event extends RuleElement {

    public static Event create(Rule rule, ParseNode eventTree)
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

        String fullText = "BIND\n" + text + "\nIF TRUE DO NOTHING";
        try {
            ECATokenLexer lexer = new ECATokenLexer(new StringReader(fullText));
            ECAGrammarParser parser = new ECAGrammarParser(lexer);
            Symbol event_parse = parser.parse();
            ParseNode eventTree = (ParseNode)event_parse.value;
            Event event = new Event(rule, eventTree);
            return event;
        } catch (Exception e) {
            throw new ParseException("org.jboss.byteman.rule.Event : error parsing event\n" + text, e);
        }
    }

    protected Event(Rule rule, ParseNode eventTree) throws TypeException
    {
        super(rule);
        createBindings(eventTree);
    }

    protected Event(Rule rule)
    {
        super(rule);
    }

    public Bindings getBindings()
    {
        return rule.getBindings();
    }

    public Type typeCheck(Type expected) throws TypeException {
        // expected must be Type.VOID
        Iterator<Binding> iterator = getBindings().iterator();
        while (iterator.hasNext()) {
            Binding binding = iterator.next();

            typeCheck(binding);
        }
        return Type.VOID;
    }

    private void typeCheck(Binding binding)
            throws TypeException
    {
        binding.typeCheck(Type.UNDEFINED);
    }

    private void createBindings(ParseNode eventTree) throws TypeException
    {
        // we expect BINDINGS = NOTHING | BINDING | (COMMA BINDING BINDINGS)
        // where BINDING = (BIND BINDSYM EXPR)

        if (eventTree == null || eventTree.getTag() == NOTHING) {
            return;
        }
        
        Bindings bindings = getBindings();

        // we bundle exceptions from each binding to report more than just the first error

        List<TypeException> exceptions = new ArrayList<TypeException>();

        while (eventTree != null) {
            try {
                int tag = eventTree.getTag();
                switch (tag) {
                    case COMMA:
                    {
                        // update before we risk an exception
                        ParseNode child0 = (ParseNode)eventTree.getChild(0);
                        eventTree = (ParseNode)eventTree.getChild(1);
                        addBinding(bindings, child0);
                    }
                    break;
                    case ASSIGN:
                    {
                        // update before we risk an exception
                        ParseNode saveTree = eventTree;
                        eventTree = null;
                        addBinding(bindings, saveTree);
                    }
                    break;
                    default:
                    {
                        String message = "Event.createBindings : unexpected token Type in binding list " + tag + " for token " + eventTree.getText() + eventTree.getPos();
                        eventTree = null;
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

    private void addBinding(Bindings bindings, ParseNode bindingTree) throws TypeException
    {
        int tag = bindingTree.getTag();

        if (tag != ASSIGN) {
            String message = "Event.createBindings : unexpected token Type in binding " + tag + " for token " + bindingTree.getText() + bindingTree.getPos();
            throw new TypeException(message);
        }

        ParseNode varTree = (ParseNode)bindingTree.getChild(0);
        ParseNode exprTree = (ParseNode)bindingTree.getChild(1);
        Binding binding;

        binding = createBinding(varTree);

        // don't allow current binding to be used when parsing the expression
        // but do use any type supplied for the binding

        Expression expr;

        expr = ExpressionHelper.createExpression(rule, bindings, exprTree, binding.getType());

        // check bindings
        expr.bind();

        String name = binding.getName();

        if (bindings.lookup(name) != null) {
            // oops rebinding not allowed
            String message = "Event.createBindings : rebinding disallowed for variable " + name + varTree.getPos();
            throw new TypeException(message);
        }
        // if the binding type is undefined and the expression type is defined propagate the
        // expression type to the binding
        if (binding.getType() == Type.UNDEFINED && expr.getType() != Type.UNDEFINED) {
            binding.setType(expr.getType());
        }
        binding.setValue(expr);
        bindings.append(binding);
    }

    public Binding createBinding(ParseNode varTree) throws TypeException
    {
        int tag = varTree.getTag();

        // we expect either (COLON IDENTIFIER TYPE) or IDENTIFIER
        switch (tag) {
            case IDENTIFIER:
            {
                return new Binding(rule, varTree.getText());
            }
            case COLON:
            {
                ParseNode child0 = (ParseNode)varTree.getChild(0);
                ParseNode child1 = (ParseNode)varTree.getChild(1);
                if (child0.getTag() != IDENTIFIER) {
                    throw new TypeException("Event.createBindings : unexpected token type in variable declaration" + child0.getTag() + " for token " + child0.getText() + child0.getPos());
                } else if (child1.getTag() != IDENTIFIER && child1.getTag() != ARRAY) {
                    throw new TypeException("Event.createBindings : unexpected token Type in variable type declaration" + child1.getTag()  + " for token " + child1.getText() + child1.getPos());
                }
                Type type = getBindingType(child1);
                if (type == null) {
                    throw new TypeException("Event.createBindings : incompatible type in declaration of variable " + child1.getText() + child1.getPos());
                }
                return new Binding(rule, child0.getText(), type);
            }
            default:
            {
                throw new TypeException("Event.createBindings : unexpected token type in binding variable declaration" + tag + " for token " + varTree.getText() + varTree.getPos());
            }
        }
    }

    /**
     * create and return a type for a binding or return null if the type cannot be created
     * @param typeTree
     * @return the binding type or null
     */
    private Type getBindingType(ParseNode typeTree)
    {
        int tag = typeTree.getTag();
        // we expect either TYPE = (IDENTIFIER) or (ARRAY TYPE)
        switch (tag) {
            case IDENTIFIER:
            {
                String typeName = typeTree.getText();
                return getTypeGroup().create(typeName);
            }
            case ARRAY:
            {
                ParseNode child0 = (ParseNode)typeTree.getChild(0);
                Type baseType = getBindingType(child0);
                if (baseType != null) {
                    return getTypeGroup().createArray(baseType);
                } else {
                    return null;
                }
            }
            default:
            {
                return null;
            }
        }
    }

    public Object interpret(HelperAdapter helper)
            throws ExecuteException
    {
        Iterator<Binding> iterator = getBindings().iterator();

        while (iterator.hasNext()) {
            Binding binding = iterator.next();

            binding.interpret(helper);
        }
        
        return null;
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        int currentStack = compileContext.getStackCount();

        Iterator<Binding> iterator = getBindings().iterator();
        while (iterator.hasNext()) {
            Binding binding = iterator.next();

            binding.compile(mv, compileContext);
        }

        // check stack heights
        if (compileContext.getStackCount() != currentStack) {
            throw new CompileException("Event.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack);
        }
    }

    public void writeTo(StringWriter stringWriter)
    {
        Iterator<Binding> iter = getBindings().iterator();
        if (!iter.hasNext()) {
            stringWriter.write("BIND NOTHING");
        } else {
            String prefix = "BIND ";
            while (iter.hasNext()) {
                Binding binding = iter.next();
                stringWriter.write(prefix);
                binding.writeTo(stringWriter);
                prefix = ",\n     ";
            }
        }
        stringWriter.write("\n");
    }
}
