/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
            this.action = ExpressionHelper.createExpressionList(rule, this.getBindings(), actionTree, Type.VOID);
        }
    }

    protected Action(Rule rule)
    {
        super(rule);
        this.action = null;
    }

    public Type typeCheck(Type expected) throws TypeException {
        // expected must be Type.VOID
        if (action != null) {
            for (Expression expr : action) {
                expr.typeCheck(Type.VOID);
            }
        }
        return Type.VOID;
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