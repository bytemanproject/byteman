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

import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.expression.ExpressionHelper;
import org.jboss.byteman.rule.expression.Expression;
import org.jboss.byteman.rule.expression.ReturnExpression;
import org.jboss.byteman.rule.expression.ThrowExpression;
import org.jboss.byteman.rule.grammar.ECATokenLexer;
import org.jboss.byteman.rule.grammar.ECAGrammarParser;
import org.jboss.byteman.rule.grammar.ParseNode;
import static org.jboss.byteman.rule.grammar.ParseNode.*;
import org.jboss.byteman.rule.exception.ParseException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.StringReader;

import java_cup.runtime.Symbol;

/**
 * class which represents a rule action comprising a void expression
 */
public class Action extends RuleElement
{
    public static Action create(Rule rule, ParseNode actionTree)
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
        String fullText = "BIND NOTHING IF TRUE DO \n" + text;
        try {
            ECATokenLexer lexer = new ECATokenLexer(new StringReader(text));
            ECAGrammarParser parser = new ECAGrammarParser(lexer);
            Symbol parse = parser.parse();
            ParseNode parseTree = (ParseNode)parse.value;
            ParseNode actionTree = (ParseNode)parseTree.getChild(3);
            Action action = new Action(rule, actionTree);
            return action;
        } catch (Exception e) {
            throw new ParseException("org.jboss.byteman.rule.Action : error parsing action\n" + text);
        }
    }
    protected Action(Rule rule, ParseNode actionTree) throws TypeException
    {
        super(rule);
        if (actionTree.getTag() == NOTHING) {
            this.action = new ArrayList<Expression>();
        } else {
            this.action = ExpressionHelper.createExpressionList(rule, this.getBindings(), actionTree, Type.VOID);
            for (Expression expr : action) {
                // check bindings
                expr.bind();
            }
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

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException {
        int currentStack = compileContext.getStackCount();

        for (Expression expr : action) {
            expr.compile(mv, compileContext);
            Type resultType = expr.getType();
            // return and throw expressions don't actually leave a value on the stack even
            // though they may have a non-VOID value type
            boolean maybePop = !(expr instanceof ReturnExpression || expr instanceof ThrowExpression);
            if (maybePop && resultType != Type.VOID) {
                int expected = (resultType.getNBytes() > 4 ? 2 : 1);
                if (expected == 1) {
                    mv.visitInsn(Opcodes.POP);
                    compileContext.addStackCount(-1);
                } else if (expected == 2) {
                    mv.visitInsn(Opcodes.POP2);
                    compileContext.addStackCount(-2);
                }
            }
        }

        // check original stack height has been restored
        if (compileContext.getStackCount() != currentStack) {
            throw new CompileException("Action.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack);
        }
    }

    public Object interpret(HelperAdapter helper)
            throws ExecuteException
    {
        if (action != null) {
            for (Expression expr : action) {
                expr.interpret(helper);
            }
        }
        
        return null;
    }

    public void writeTo(StringWriter stringWriter)
    {
        if (action == null || action.size()  == 0) {
            stringWriter.write("DO   NOTHING");
        } else {
            String prefix = "DO   ";
            for (Expression expr : action) {
                stringWriter.write(prefix);
                expr.writeTo(stringWriter);
                prefix = ";\n     ";
            }
        }
        stringWriter.write("\n");
    }

    private List<Expression> action;
}