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
package org.jboss.byteman.rule.expression;

import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.grammar.ParseNode;

import java.io.StringWriter;

/**
 * generic operator expression subsumes unary, binary and ternary operators
 */
public abstract class OperExpression extends Expression
{
    OperExpression(Rule rule, int oper, Type type, ParseNode token)
    {
        super(rule, type, token);

        this.oper = oper;
    }

    protected int oper;

    /**
     * return the operand with the given index or null if the index is out of range
     * @param index
     * @return the operand with the given index
     */
    public abstract Expression getOperand(int index);

    public void writeTo(StringWriter stringWriter) {
        if ((oper & UNARY) != 0) {
            stringWriter.write(getOperandString());
            stringWriter.write("(");
            getOperand(0).writeTo(stringWriter);
            stringWriter.write(")");
        } else if ((oper & BINARY) != 0) {
            stringWriter.write("(");
            getOperand(0).writeTo(stringWriter);
            stringWriter.write(" ");
            stringWriter.write(getOperandString());
            stringWriter.write(" ");
            getOperand(1).writeTo(stringWriter);
            stringWriter.write(")");
        } else if (oper == COND) {
            // we only have one ternary operator
            stringWriter.write("(");
            getOperand(0).writeTo(stringWriter);
            stringWriter.write(" ? ");
            getOperand(1).writeTo(stringWriter);
            stringWriter.write(" : ");
            getOperand(2).writeTo(stringWriter);
            stringWriter.write(")");
        } else {
            stringWriter.write("*** error unknown operator *** " + oper);
        }
    }

    private String getOperandString()
    {
        for (int i = 0; i < operands.length; i++)
        {
            if (operands[i] == oper) {
                return operandNames[i];
            }
        }
        return "*** error unknown operator ***";
    }

    public static int convertOper(int parserOper)
    {
        for (int i = 0; i < parserOperands.length; i++) {
            if (parserOperands[i] == parserOper) {
                return operands[i];
            }
        }

        return -1;
    }

    final public static int UNARY       = 0x1000;
    final public static int BINARY      = 0x2000;
    final public static int TERNARY     = 0x4000;

    final public static int NOT         = 0x0010 | UNARY;

    final public static int TWIDDLE     = 0x0020 | UNARY;

    final public static int OR          = 0x0040 | BINARY;
    final public static int AND         = 0x0041 | BINARY;

    final public static int EQ          = 0x0080 | BINARY;
    final public static int NE          = 0x0081 | BINARY;
    final public static int GT          = 0x0082 | BINARY;
    final public static int LT          = 0x0083 | BINARY;
    final public static int GE          = 0x0084 | BINARY;
    final public static int LE          = 0x0085 | BINARY;

    final public static int BOR         = 0x0100 | BINARY;
    final public static int BAND        = 0x0101 | BINARY;
    final public static int BXOR        = 0x0102 | BINARY;
    final public static int URSH        = 0x0103 | BINARY;
    final public static int RSH         = 0x0104 | BINARY;
    final public static int LSH         = 0x0105 | BINARY;

    final public static int UMINUS      = 0x0200 | UNARY;

    final public static int MUL         = 0x0201 | BINARY;
    final public static int DIV         = 0x0202 | BINARY;
    final public static int PLUS        = 0x0203| BINARY;
    final public static int MINUS       = 0x0204 | BINARY;
    final public static int MOD         = 0x0205 | BINARY;

    final public static int ASSIGN      = 0x0401 | BINARY;

    final public static int COND        = 0x0800 | TERNARY;

    final private static int[] operands = {
            NOT,
            TWIDDLE,
            OR,
            AND,
            EQ,
            NE,
            GT,
            LT,
            GE,
            LE,
            BOR,
            BAND,
            BXOR,
            URSH,
            RSH,
            LSH,
            UMINUS,
            MUL,
            DIV,
            PLUS,
            MINUS,
            MOD,
            ASSIGN,
            COND,
    };

    /* parser operands are not allocated rationally so we convert using this table */

    final private static int[] parserOperands = {
            org.jboss.byteman.rule.grammar.ParseNode.NOT,
            org.jboss.byteman.rule.grammar.ParseNode.TWIDDLE,
            org.jboss.byteman.rule.grammar.ParseNode.OR,
            org.jboss.byteman.rule.grammar.ParseNode.AND,
            org.jboss.byteman.rule.grammar.ParseNode.EQ,
            org.jboss.byteman.rule.grammar.ParseNode.NE,
            org.jboss.byteman.rule.grammar.ParseNode.GT,
            org.jboss.byteman.rule.grammar.ParseNode.LT,
            org.jboss.byteman.rule.grammar.ParseNode.GE,
            org.jboss.byteman.rule.grammar.ParseNode.LE,
            org.jboss.byteman.rule.grammar.ParseNode.BOR,
            org.jboss.byteman.rule.grammar.ParseNode.BAND,
            org.jboss.byteman.rule.grammar.ParseNode.BXOR,
            org.jboss.byteman.rule.grammar.ParseNode.URSH,
            org.jboss.byteman.rule.grammar.ParseNode.RSH,
            org.jboss.byteman.rule.grammar.ParseNode.LSH,
            org.jboss.byteman.rule.grammar.ParseNode.UMINUS,
            org.jboss.byteman.rule.grammar.ParseNode.MUL,
            org.jboss.byteman.rule.grammar.ParseNode.DIV,
            org.jboss.byteman.rule.grammar.ParseNode.PLUS,
            org.jboss.byteman.rule.grammar.ParseNode.MINUS,
            org.jboss.byteman.rule.grammar.ParseNode.MOD,
            org.jboss.byteman.rule.grammar.ParseNode.ASSIGN,
            org.jboss.byteman.rule.grammar.ParseNode.TERNOP
    };

    final private static String[] operandNames = {
            "!",
            "~",
            "||",
            "&&",
            "==",
            "!=",
            ">",
            "<",
            ">=",
            "<=",
            "|",
            "&",
            "^",
            ">>>",
            ">>",
            "<<",
            "-",
            "*",
            "/",
            "+",
            "-",
            "%",
            "=",
            "? :"
    };
}
