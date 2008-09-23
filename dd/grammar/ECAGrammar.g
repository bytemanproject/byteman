parser grammar ECAGrammar;
options {
 // use AST output
 output = AST;
 // We're going to use the tokens defined in our ECALexer grammar.
 tokenVocab = ECAToken;
 // enable backtracking
 backtrack = true;
}

// we need some synthetic tokens for the AST
tokens {
	UNOP;
	BINOP;
	TERNOP;
	METH;
	ARRAY;
	NUM_LIT;
	STRING_LIT;
}

@header {
package org.jboss.jbossts.orchestration.rule.grammar;
}

eca_script_rule :	rule=eca_script_rule_one EOF -> ^($rule)
	;
eca_script_rule_one
	:	RULE n=SYMBOL
		CLASS cl=SYMBOL
		METHOD m=SYMBOL
		LINE l=NUMBER
		BIND e=event
		IF c=condition
		DO a=action
		ENDRULE  -> ^(RULE $n $cl $m $l $e $c $a)
	;
eca_rule	:	eca EOF -> ^(eca)
	;
	
eca_event	:	event EOF -> ^(event)
	;

eca_condition	:	condition EOF -> ^(condition)
	;

eca_action	:	action EOF -> ^(action)
	;

eca	:	BIND e=event
		IF c=condition
		DO a=action	-> ^(BIND $e $c $a)
	;

// event specifications -- for now events are just a list of bindings

event	:	bindings
	;

// zero event bindings is specified by an empty event string so we always expect at least one binding

bindings	:	binding SEPR bindings	-> ^(SEPR binding bindings)
	|	binding
	;

binding	:	bind_sym ASSIGN expr	-> ^(ASSIGN bind_sym expr)
	;

// a bound symbol can be specified to be of a particular type
bind_sym	:	v=SYMBOL COLON t=SYMBOL	-> ^(COLON $v $t) 
	|	SYMBOL
	;

// a condition is simply an expression. it not type-constrained by the grammar -- it's easier to do
// the type checking after parsing  n.b. we always have at least one condition as an empty (i.e.
// vacuously true) condition is defined by an empty input string.

condition	:	TRUE		-> ^(TRUE)
	|	FALSE		-> ^(FALSE)
	|	expr
	;

// actions area defined as a sequence of expressions.it not type-constrained by the grammar -- it's
// easier to do the type checking after parsing  n.b. we always have at least one action as
// an  empty (i.e. do nothing) action is defined by an empty action string

action	:	NOTHING		-> ^(NOTHING)
	|	action_expr_list
	;

action_expr_list
	:	action_expr SEPR action_expr_list	-> ^(SEPR action_expr action_expr_list)
	|	action_expr
	;

action_expr	:	RETURN		-> ^(RETURN)
	|	RETURN expr		-> ^(RETURN expr)
	|	expr
	;

expr	:	simple_expr infix_oper expr		-> ^(BINOP infix_oper simple_expr expr)
	|	simple_expr
	|	unary_oper expr 		-> ^(UNOP unary_oper expr)
	|	cond=simple_expr TERN_IF iftrue=expr COLON iffalse=expr -> ^(TERNOP $cond $iftrue $iffalse)
	;

simple_expr	:	v=DOLLARSYM
	|	v=SYMBOL idx=array_idx			-> ^(ARRAY $v $idx)
	|	v=SYMBOL LPAREN RPAREN			-> ^(METH $v)
	|	v=SYMBOL
	|	v=SYMBOL LPAREN args=expr_list RPAREN		-> ^(METH $v $args) 
	|	NUMBER
	|	STRING
	|	LPAREN expr RPAREN			-> ^(expr)
	;

expr_list
	:	expr SEPR expr_list			-> ^(SEPR expr expr_list)
	|	expr
	;

array_idx_list
	:	array_idx array_idx_list			-> ^(SEPR array_idx array_idx_list)
	|	array_idx
	;

array_idx
	:	LSQUARE expr RSQUARE			-> ^(expr)
	;

infix_oper	:	infix_bit_oper
	|	infix_arith_oper
	|	infix_bool_oper
	|	infix_cmp_oper
	;

infix_bit_oper
	:	BAND
	|	BOR
	|	BXOR
	;
	
infix_arith_oper
	:	MUL
	|	DIV
	|	PLUS
	|	MINUS
	;

infix_bool_oper
	:	AND
	|	OR
	;

infix_cmp_oper
	:	EQ
	|	NEQ
	|	GT
	|	LT
	|	GEQ
	|	LEQ
	;

unary_oper	:	NOT
	|	TWIDDLE
	;
