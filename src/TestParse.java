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
