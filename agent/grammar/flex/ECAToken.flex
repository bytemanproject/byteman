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

package org.jboss.byteman.rule.grammar;

import java_cup.runtime.*;
import org.jboss.byteman.rule.grammar.PrintableSymbol;

%%

%class ECATokenLexer
%unicode
%cup
%line
%column
%public
// %debug

%{
  StringBuffer string = new StringBuffer();

  private int startLine = 0;

  private String file = "";

  public void setStartLine(int startLine)
  {
    this.startLine = startLine;
  }

  public void setFile(String file)
  {
    this.file = file;
  }

  private Symbol symbol(int type) {
    return new PrintableSymbol(type, file, yyline + startLine, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new PrintableSymbol(type, file, yyline + startLine, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n

WhiteSpace     = {LineTerminator} | [ \t\f]

Identifier = ([A-Za-z_]) ([A-Za-z0-9$_])*

PosInteger = 0 | [1-9][0-9]*

Sign = [+-]

Exp = [Ee]

Dot = "."

DotTrailing = {Dot} {PosInteger}?
ExpTrailing = {Exp} {Sign}? {PosInteger}
FloatTrailing = {ExpTrailing} | {DotTrailing} {ExpTrailing}?
PosFloat = {PosInteger} {FloatTrailing}

Integer = {Sign}? {PosInteger}

Float = {Sign}? {PosFloat}

%state STRING

%state QUOTEDIDENT

%state COMMENT

%%

/* keywords */

<YYINITIAL> {

"BIND"|"bind"	{ return symbol(sym.BIND); }

"IF"|"if"	{ return symbol(sym.IF); }

"DO"|"do"	{ return symbol(sym.DO); }

/* rule and rule header keywords are not required

"RULE"		{ return symbol(sym.RULE); }

"CLASS"		{ return symbol(sym.CLASS); }

"class"		{ return symbol(sym.CLASS); }

"METHOD"	{ return symbol(sym.METHOD); }
	
"LINE"		{ return symbol(sym.LINE); }
	
"ENDRULE"	{ return symbol(sym.ENDRULE); }

*/

"NOTHING"|"nothing"
		{ return symbol(sym.NOTHING); }

"TRUE"|"true" 	{ return symbol(sym.BOOLEAN_LITERAL, Boolean.TRUE); }

"FALSE"|"false"	{ return symbol(sym.BOOLEAN_LITERAL, Boolean.FALSE); }
	
"RETURN"|"return"
		{ return symbol(sym.RETURN); }
	
"THROW"|"throw"	{ return symbol(sym.THROW); }

"NEW"|"new"	{ return symbol(sym.NEW); }

"class"		{ return symbol(sym.CLASS); }

/* various bracket pairs */
	
"("		{ return symbol(sym.LPAREN); }

")"		{ return symbol(sym.RPAREN); }

"["		{ return symbol(sym.LSQUARE); }

"]"		{ return symbol(sym.RSQUARE); }

/* braces are not required

"{"		{ return symbol(sym.LBRACE); }

"}"		{ return symbol(sym.RBRACE); }

*/

/* expression separator */

";"		{ return symbol(sym.SEMI); }

/* bindings separator */

","		{ return symbol(sym.COMMA); }


/* identifier punctuator */

"."		{ return symbol(sym.DOT); }

/* binding for events */

"=" | "<--"	{ return symbol(sym.ASSIGN); }

/* logical operators */

"||" | "OR" | "or"	{ return symbol(sym.OR); }

"&&" | "AND" | "and"	{ return symbol(sym.AND); }

"!" | "NOT" | "not"	{ return symbol(sym.NOT); }

/* bitwise operators */

">>>"        { return symbol(sym.URSH); }

">>"        { return symbol(sym.RSH); }

"<<"        { return symbol(sym.LSH); }

"|"			{ return symbol(sym.BOR); }

"&"			{ return symbol(sym.BAND); }

"^"			{ return symbol(sym.BXOR); }

"~"			{ return symbol(sym.TWIDDLE); }

/* comparison operators */

"<" | "LT" | "lt"	{ return symbol(sym.LT); }

"<=" | "LE" | "le"	{ return symbol(sym.LE); }

"==" | "EQ" | "eq"	{ return symbol(sym.EQ); }

"!=" | "NE" | "ne"	{ return symbol(sym.NE); }

">=" | "GE" | "ge"	{ return symbol(sym.GE); }

">" | "GT" | "gt"	{ return symbol(sym.GT); }

/* arithmetic operators */


"*" | "TIMES" | "times"	{ return symbol(sym.MUL); }

"/" | "DIVIDE" | "divide"	{ return symbol(sym.DIV); }

"+" | "PLUS" | "plus"	{ return symbol(sym.PLUS); }

"-" | "MINUS" | "minus"	{ return symbol(sym.MINUS); }

"%" | "MOD" | "mod"	{ return symbol(sym.MOD); }

/* ternary condition operator -- also sepr for var and type in decl */

"?"			{ return symbol(sym.TERN_IF); }

":"			{ return symbol(sym.COLON); }

/* dollar prefixed symbols */

/* trigger method recipient and params */

"$" {Integer} { return symbol(sym.DOLLAR, yytext()); }

/* trigger method local variable */

"$" {Identifier} { return symbol(sym.DOLLAR, yytext()); }

/* return value on stack in AT EXIT rule */

"$" "!" { return symbol(sym.DOLLAR, yytext()); }

/* throwable on stack in AT THROW rule */

"$" "^" { return symbol(sym.DOLLAR, yytext()); }

/* trigger method parameter count */

"$" "#" { return symbol(sym.DOLLAR, yytext()); }

/* trigger method parameter array */

"$" "*" { return symbol(sym.DOLLAR, yytext()); }

/* invoked method parameter array -- for use in AT INVOKE rules */

"$" "@" { return symbol(sym.DOLLAR, yytext()); }

/* identifiers */

"NULL" | "null" { return symbol(sym.NULL_LITERAL); }

{Identifier}		{ return symbol(sym.IDENTIFIER, yytext()); }

/* numbers */

{Integer}		{ return symbol(sym.INTEGER_LITERAL, Integer.valueOf(yytext())); }

{Float}		{ return symbol(sym.FLOAT_LITERAL, Float.valueOf(yytext())); }

/* strings */

\"			{ string.setLength(0); yybegin(STRING); }

\'			{ string.setLength(0);  yybegin(QUOTEDIDENT); }

#			{ yybegin(COMMENT); }

/* whitespace */

{WhiteSpace}		{ /* ignore */ }

/* anything else is an error! */

.			{ throw new Error("Illegal character <"+ yytext()+">"); }

}

<STRING> {
\"			{ yybegin(YYINITIAL);
			  return symbol(sym.STRING_LITERAL,
					string.toString()); }

[^\n\r\"\\]+		{ string.append( yytext() ); }
\\t			{ string.append('\t'); }
\\n			{ string.append('\n'); }
\\r			{ string.append('\r'); }
\\\"			{ string.append('\"'); }
\\			{ string.append('\\'); }

/* anything else is an error! */
\n			{ throw new Error("File " + file + " line " + (yyline + startLine) + " : newline in string"); }
.			{ throw new Error("File " + file + " line " + (yyline + startLine) + " : illegal character in string <"+ yytext()+">"); }
}

<QUOTEDIDENT> {
[^\n\r']+		{ string.append( yytext() ); }
'			{ yybegin(YYINITIAL);
			  return symbol(sym.IDENTIFIER,
					string.toString()); }
/* anything else is an error! */
{LineTerminator}			{ throw new Error("File " + file + " line " + (yyline + startLine) + " : newline in quoted identifier"); }
}

<COMMENT> {
[^\n\r]			{ /*ignore */ }
{LineTerminator}	{ yybegin(YYINITIAL); }
}
