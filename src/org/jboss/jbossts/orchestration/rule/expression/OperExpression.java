package org.jboss.jbossts.orchestration.rule.expression;

import org.antlr.runtime.Token;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ParseException;

import java.util.Iterator;
import java.io.StringWriter;

/**
 * generic operator expression subsumes unary, binary and ternary operators
 */
public abstract class OperExpression extends Expression
{
    OperExpression(int oper, Type type, Token token)
    {
        super(type, token);

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
        } else if (oper == IF) {
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
    final public static int NEQ         = 0x0081 | BINARY;
    final public static int GT          = 0x0082 | BINARY;
    final public static int LT          = 0x0083 | BINARY;
    final public static int GEQ         = 0x0084 | BINARY;
    final public static int LEQ         = 0x0085 | BINARY;

    final public static int BOR         = 0x0100 | BINARY;
    final public static int BAND        = 0x0101 | BINARY;
    final public static int BXOR        = 0x0102 | BINARY;

    final public static int MUL         = 0x0201 | BINARY;
    final public static int DIV         = 0x0202 | BINARY;
    final public static int PLUS        = 0x0203| BINARY;
    final public static int MINUS       = 0x0204 | BINARY;
    final public static int MOD         = 0x0205 | BINARY;

    final public static int IF          = 0x0400 | TERNARY;

    final private static int[] operands = {
            NOT,
            TWIDDLE,
            OR,
            AND,
            EQ,
            NEQ,
            GT,
            LT,
            GEQ,
            LEQ,
            BOR,
            BAND,
            BXOR,
            MUL,
            DIV,
            PLUS,
            MINUS,
            MOD,
            IF
    };

    /* parser operands ar enot allocated rationally so we convert usingthis table */

    final private static int[] parserOperands = {
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.NOT,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.TWIDDLE,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.OR,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.AND,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.EQ,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.NEQ,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.GT,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.LT,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.GEQ,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.LEQ,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.BOR,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.BAND,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.BXOR,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.MUL,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.DIV,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.PLUS,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.MINUS,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.MOD,
            org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.IF
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
            "*",
            "/",
            "+",
            "-",
            "%",
            "? :"
    };
}
