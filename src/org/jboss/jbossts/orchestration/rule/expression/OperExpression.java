package org.jboss.jbossts.orchestration.rule.expression;

import org.antlr.runtime.Token;
import org.jboss.jbossts.orchestration.rule.type.Type;

import java.util.Iterator;

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
}
