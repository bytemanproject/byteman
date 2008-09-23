lexer grammar ECAToken;

@header {
package org.jboss.jbossts.orchestration.rule.grammar;
}

// integers or floats
	
fragment
DIGIT	:	'0'..'9'
	;

fragment
POSDIGIT	:	'1'..'9'
	;

fragment
SIGN	:	'+'|'-'
	;

fragment
BAREINT	:	'0' | (POSDIGIT (DIGIT)*)
	;
fragment
INTEGER	:	SIGN? BAREINT
	;

fragment	
POINT	:	'.'
	;

fragment	
EXPPART	:	 ('e'|'E') INTEGER
	;


fragment
FLOAT	:	INTEGER POINT BAREINT? EXPPART?
	;

NUMBER	:	INTEGER | FLOAT
	;

// builtin symbols -- need to add these before addign the rules for adding any old symbol

BIND	:	'BIND'
	;

IF	:	'IF'
	;

DO	:	'DO'
	;

RULE	:	'RULE'
	;
	
CLASS	:	'CLASS'
	;
	
METHOD	:	'METHOD'
	;
	
LINE	:	'LINE'
	;
	
ENDRULE	:	'ENDRULE'
	;

NOTHING	:	'NOTHING'
	;

TRUE	: 	'TRUE' | 'true'
	;

FALSE	:	'FALSE'|'false'
	;
	
// various bracket pairs

LPAREN	:	'('
	;

RPAREN	:	')'
	;

LSQUARE	:	'\['
	;

RSQUARE	:	'\]'
	;

LBRACE	:	'{'
	;

RBRACE	:	'}'
	;

// statement or expression separator -- we don't care

SEPR	:	';'
	|	','
	;

// symbol punctuator

DOT	:	'.'
	;

// binding for events and assignment for exprs

ASSIGN	:	'='
	|	'<--'
	;

// logical operators
	
OR	:	'||'
	|	'OR'
	|	'or'
	;

AND	:	'&&'
	|	'AND'
	|	'and'
	;

NOT	:	'!'
	|	'NOT'
	|	'not'
	;

// comparison operators

EQ	:	'=='
	|	'EQ'
	|	'eq'
	;

NEQ	:	'!='
	|	'NEQ'
	|	'neq'
	;

GT	:	'>'
	|	'GT'
	|	'gt'
	;

LT	:	'<'
	|	'LT'
	|	'lt'
	;

GEQ	:	'>='
	|	'EQ'
	|	'geq'
	;

LEQ	:	'<='
	|	'LEQ'
	|	'leq'
	;

// bitwise operators

BOR	:	'|'
	;

BAND	:	'&'
	;

BXOR	:	'^'
	;

TWIDDLE	:	'~'
	;

// arithmetic operators

MUL	:	'*'
	|	'TIMES'
	|	'times'
	;

DIV	:	'/'
	|	'DIVIDE'
	|	'divide'
	;

PLUS	:	'+'
	|	'PLUS'
	|	'plus'
	;

MINUS	:	'-'
	|	'MINUS'
	|	'minus'
	;

MOD	:	'%'
	|	'MOD'
	|	'mod'
	;

// ternary condition operator

TERN_IF	:	'?'
	;

COLON	:	':'
	;

// "strings" and symbols, the latter possibly with leading or embedded '_' and possibly 'quoted_inlcuding_punctuation_@#$_!!!' 

fragment
LETTER	:	'a'..'z' | 'A'..'Z'
	;

fragment
UNDERSCORE	:	'_'
	;
	
QUOTE	:	'\''
	;

DQUOTE	:	'"'
	;

fragment
SPACE	:	' '|'\t'|'\r'
	;

fragment
NEWLINE	:	'\n'
	;

fragment
PUNCT	:	'!'|'$'|'%'|'^'|'&'|'*'|'('|')'|'-'|'+'|'='|'{'|'}'|'['|']'|':'|';'|'@'|'~'|'#'|'|'|'\\'|'`'|','|'<'|'.'|'>'|'/'|'?'
	;

STRING	:	DQUOTE (SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )* DQUOTE
	;

fragment
BARESYM	:	(LETTER | UNDERSCORE) (LETTER | DIGIT | UNDERSCORE)*
	;
fragment
QUOTSYM	:	QUOTE (PUNCT |LETTER | UNDERSCORE | DIGIT | DQUOTE |SPACE)* QUOTE
	;

// n.b. dot separated symbol can contain zero or more dot separators
fragment
DOTSYM	:	BARESYM DOT DOTSYM
	|	BARESYM
	;

SYMBOL	:	DOTSYM
	|	QUOTSYM
	;

// dollar is not allowed except in fromt of number or id or in quotes
fragment
DOLLAR	:	'$'
	;

// dollar symbols have dollar followed by a trailing non-signed integer or unquoted string

DOLLARSYM	:	DOLLAR (BAREINT | BARESYM)
	;

// ignore any other white space

// WS	:	(' ' | '\t' | '\n' | '\r') { setType(Token.SKIP); }
WS	:	(SPACE | NEWLINE) { skip(); }
	;
