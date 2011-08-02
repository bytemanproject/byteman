/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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

    public static String[] sym_name = new String[200];

    static {
        for (int i =  0; i < sym_name.length; i++) {
            sym_name[i] = "???";
	}
    sym_name[org.jboss.byteman.rule.grammar.sym.STRING_LITERAL] = "STRING_LITERAL";
    sym_name[org.jboss.byteman.rule.grammar.sym.GE] = "GE";
    sym_name[org.jboss.byteman.rule.grammar.sym.UMINUS] = "UMINUS";
    sym_name[org.jboss.byteman.rule.grammar.sym.LPAREN] = "LPAREN";
    sym_name[org.jboss.byteman.rule.grammar.sym.SEMI] = "SEMI";
    sym_name[org.jboss.byteman.rule.grammar.sym.MINUS] = "MINUS";
    sym_name[org.jboss.byteman.rule.grammar.sym.BXOR] = "BXOR";
    sym_name[org.jboss.byteman.rule.grammar.sym.RPAREN] = "RPAREN";
    sym_name[org.jboss.byteman.rule.grammar.sym.BOOLEAN_LITERAL] = "BOOLEAN_LITERAL";
    sym_name[org.jboss.byteman.rule.grammar.sym.NOT] = "NOT";
    sym_name[org.jboss.byteman.rule.grammar.sym.AND] = "AND";
    sym_name[org.jboss.byteman.rule.grammar.sym.LT] = "LT";
    sym_name[org.jboss.byteman.rule.grammar.sym.OR] = "OR";
    sym_name[org.jboss.byteman.rule.grammar.sym.COMMA] = "COMMA";
    sym_name[org.jboss.byteman.rule.grammar.sym.THROW] = "THROW";
    sym_name[org.jboss.byteman.rule.grammar.sym.BAND] = "BAND";
    sym_name[org.jboss.byteman.rule.grammar.sym.DIV] = "DIV";
    sym_name[org.jboss.byteman.rule.grammar.sym.PLUS] = "PLUS";
    sym_name[org.jboss.byteman.rule.grammar.sym.ASSIGN] = "ASSIGN";
    sym_name[org.jboss.byteman.rule.grammar.sym.IF] = "IF";
    sym_name[org.jboss.byteman.rule.grammar.sym.DOT] = "DOT";
    sym_name[org.jboss.byteman.rule.grammar.sym.LE] = "LE";
    sym_name[org.jboss.byteman.rule.grammar.sym.BIND] = "BIND";
    sym_name[org.jboss.byteman.rule.grammar.sym.EOF] = "EOF";
    sym_name[org.jboss.byteman.rule.grammar.sym.RETURN] = "RETURN";
    sym_name[org.jboss.byteman.rule.grammar.sym.error] = "error";
    sym_name[org.jboss.byteman.rule.grammar.sym.MUL] = "MUL";
    sym_name[org.jboss.byteman.rule.grammar.sym.MOD] = "MOD";
    sym_name[org.jboss.byteman.rule.grammar.sym.TWIDDLE] = "TWIDDLE";
    sym_name[org.jboss.byteman.rule.grammar.sym.EQ] = "EQ";
    sym_name[org.jboss.byteman.rule.grammar.sym.COLON] = "COLON";
    sym_name[org.jboss.byteman.rule.grammar.sym.BOR] = "BOR";
    sym_name[org.jboss.byteman.rule.grammar.sym.LSQUARE] = "LSQUARE";
    sym_name[org.jboss.byteman.rule.grammar.sym.RSQUARE] = "RSQUARE";
    sym_name[org.jboss.byteman.rule.grammar.sym.DOLLAR] = "DOLLAR";
    sym_name[org.jboss.byteman.rule.grammar.sym.NE] = "NE";
    sym_name[org.jboss.byteman.rule.grammar.sym.TERN_IF] = "TERN_IF";
    sym_name[org.jboss.byteman.rule.grammar.sym.NOTHING] = "NOTHING";
    sym_name[org.jboss.byteman.rule.grammar.sym.FLOAT_LITERAL] = "FLOAT_LITERAL";
    sym_name[org.jboss.byteman.rule.grammar.sym.GT] = "GT";
    sym_name[org.jboss.byteman.rule.grammar.sym.DO] = "DO";
    sym_name[org.jboss.byteman.rule.grammar.sym.IDENTIFIER] = "IDENTIFIER";
    sym_name[org.jboss.byteman.rule.grammar.sym.INTEGER_LITERAL] = "INTEGER_LITERAL";
    }
}
