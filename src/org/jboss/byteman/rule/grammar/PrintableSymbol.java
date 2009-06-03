package org.jboss.byteman.rule.grammar;

import java_cup.runtime.Symbol;

/**
 * Subclass of Symbol which knows how to print itself symbolically rather than as a numeric symbol type
 */
public class PrintableSymbol extends Symbol
{
    private String file;

    public PrintableSymbol(int id, String file, int l, int r, Object o)
    {
        super(id, l, r, o);
        this.file= file;
    }

    public PrintableSymbol(int id, String file, int l, int r)
    {
        super(id, l, r);
        this.file= file;
    }

    public PrintableSymbol(int id, Object o)
    {
        super(id, o);
        this.file= "";
    }

    public String toString()
    {
	String name = sym_name[sym];

        if (name != null) {
            if (value != null) {
                return name + " " + value;
            }
            return name;
        } else {
            if (value != null) {
                return "#" + sym + " " + value;
            }
            return "#" + sym;
        }
    }

    public String getPos()
    {
        return file + " @ " + left + "." + right;
    }

    public static String[] sym_name = new String[100];

    static {
    sym_name[41] = "STRING_LITERAL";
    sym_name[24] = "GE";
    sym_name[36] = "UMINUS";
    sym_name[8] = "LPAREN";
    sym_name[12] = "SEMI";
    sym_name[32] = "MINUS";
    sym_name[28] = "BXOR";
    sym_name[9] = "RPAREN";
    sym_name[40] = "BOOLEAN_LITERAL";
    sym_name[34] = "NOT";
    sym_name[19] = "AND";
    sym_name[20] = "LT";
    sym_name[18] = "OR";
    sym_name[13] = "COMMA";
    sym_name[7] = "THROW";
    sym_name[27] = "BAND";
    sym_name[30] = "DIV";
    sym_name[31] = "PLUS";
    sym_name[15] = "ASSIGN";
    sym_name[3] = "IF";
    sym_name[14] = "DOT";
    sym_name[21] = "LE";
    sym_name[2] = "BIND";
    sym_name[0] = "EOF";
    sym_name[6] = "RETURN";
    sym_name[1] = "error";
    sym_name[29] = "MUL";
    sym_name[33] = "MOD";
    sym_name[35] = "TWIDDLE";
    sym_name[22] = "EQ";
    sym_name[17] = "COLON";
    sym_name[26] = "BOR";
    sym_name[10] = "LSQUARE";
    sym_name[11] = "RSQUARE";
    sym_name[37] = "DOLLAR";
    sym_name[23] = "NE";
    sym_name[16] = "TERN_IF";
    sym_name[5] = "NOTHING";
    sym_name[38] = "FLOAT_LITERAL";
    sym_name[25] = "GT";
    sym_name[4] = "DO";
    sym_name[42] = "IDENTIFIER";
    sym_name[39] = "INTEGER_LITERAL";

    /* non terminals */
    sym_name[10] = "action_expr";
    sym_name[22] = "array_idx_list";
    sym_name[12] = "expr";
    sym_name[17] = "field_expr";
    sym_name[25] = "simple_name";
    sym_name[0] = "$START";
    sym_name[21] = "simple_expr";
    sym_name[8] = "actions";
    sym_name[6] = "bind_sym";
    sym_name[2] = "eca";
    sym_name[9] = "action_expr_list";
    sym_name[14] = "binary_oper_expr";
    sym_name[18] = "expr_field_expr";
    sym_name[26] = "path";
    sym_name[13] = "ternary_oper_expr";
    sym_name[5] = "binding";
    sym_name[20] = "expr_meth_expr";
    sym_name[11] = "expr_list";
    sym_name[3] = "event";
    sym_name[7] = "condition";
    sym_name[1] = "eca_rule";
    sym_name[16] = "array_expr";
    sym_name[15] = "unary_oper_expr";
    sym_name[19] = "meth_expr";
    sym_name[23] = "array_idx";
    sym_name[4] = "bindings";
    sym_name[24] = "name";
    }
}
