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
            this.condition = new BooleanLiteral(rule, token, true);
        } else if (token.getType() == ECAGrammarParser.FALSE) {
            this.condition = new BooleanLiteral(rule, token, false);
        } else {
            this.condition = ExpressionHelper.createExpression(rule, rule.getBindings(), conditionTree, Type.BOOLEAN);
        }
    }
    
    protected Condition(Rule rule)
    {
        super(rule);
        this.condition = null;
    }

    public Type typeCheck(Type expected) throws TypeException {
        // expected must be Type.Z
        if (condition != null) {
            condition.typeCheck(Type.Z);
        }
        return Type.Z;
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
