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
package org.jboss.byteman.rule;

import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.expression.ExpressionHelper;
import org.jboss.byteman.rule.expression.Expression;
import org.jboss.byteman.rule.grammar.ECATokenLexer;
import org.jboss.byteman.rule.grammar.ECAGrammarParser;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.jboss.byteman.rule.exception.ParseException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.compiler.StackHeights;
import org.objectweb.asm.MethodVisitor;

import java.io.StringWriter;
import java.io.StringReader;

import java_cup.runtime.Symbol;

/**
 * class which represents a rule condition comprising a boolean expression
 */
public class Condition extends RuleElement
{
    public static Condition create(Rule rule, ParseNode conditionTree) throws TypeException
    {
        Condition condition = new Condition(rule, conditionTree);
        return condition;
    }

    public static Condition create(Rule rule, String text) throws ParseException, TypeException
    {
        if ("".equals(text)) {
            return new Condition(rule);
        }
        String fulltext = "BIND NOTHING IF \n" + text + "\n DO NOTHING";
        try {
            ECATokenLexer lexer = new ECATokenLexer(new StringReader(text));
            ECAGrammarParser parser = new ECAGrammarParser(lexer);
            Symbol condition_parse = parser.parse();
            ParseNode conditionTree = (ParseNode) condition_parse.value;
            Condition condition = new Condition(rule, conditionTree);
            return condition;
        } catch (Exception e) {
            throw new ParseException("org.jboss.byteman.rule.Condition : error parsing condition\n" + text, e);
        }
    }

    protected Condition(Rule rule, ParseNode conditionTree)
            throws TypeException
    {
        super(rule);
        int tag = conditionTree.getTag();
        condition = ExpressionHelper.createExpression(rule, rule.getBindings(), conditionTree, Type.BOOLEAN);
        if (!condition.bind()) {
            throw new TypeException("ExpressionHelper.createExpression : unknown reference in expression" + condition.getPos());
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

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException {
        int currentStack = currentStackHeights.stackCount;
        // get the condition to compile itself -- it adds 1 to stack height
        condition.compile(mv, currentStackHeights, maxStackHeights);
        // unbox if necessary
        if (condition.getType() == Type.BOOLEAN) {
            compileUnbox(Type.BOOLEAN, Type.Z, mv, currentStackHeights, maxStackHeights);
        }

        // check stack heights
        if (currentStackHeights.stackCount != currentStack + 1) {
            throw new CompileException("Condition.compile : invalid stack height " + currentStackHeights.stackCount + " expecting " + currentStack);
        }

        // we needed room for 1 more values on the stack -- make sure we got it
        int maxStack = maxStackHeights.stackCount;
        int overflow = (currentStack + 1) - maxStack;

        if (overflow > 0) {
            maxStackHeights.addStackCount(overflow);
        }
    }

    public Object interpret(HelperAdapter helper)
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
