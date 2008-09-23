// $ANTLR 3.0.1 dd/grammar/ECAGrammar.g 2008-09-22 16:26:20

package org.jboss.jbossts.orchestration.rule.grammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class ECAGrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DIGIT", "POSDIGIT", "SIGN", "BAREINT", "INTEGER", "POINT", "EXPPART", "FLOAT", "NUMBER", "BIND", "IF", "DO", "RULE", "CLASS", "METHOD", "LINE", "ENDRULE", "NOTHING", "TRUE", "FALSE", "LPAREN", "RPAREN", "LSQUARE", "RSQUARE", "LBRACE", "RBRACE", "SEPR", "DOT", "ASSIGN", "OR", "AND", "NOT", "EQ", "NEQ", "GT", "LT", "GEQ", "LEQ", "BOR", "BAND", "BXOR", "TWIDDLE", "MUL", "DIV", "PLUS", "MINUS", "MOD", "TERN_IF", "COLON", "LETTER", "UNDERSCORE", "QUOTE", "DQUOTE", "SPACE", "NEWLINE", "PUNCT", "STRING", "BARESYM", "QUOTSYM", "DOTSYM", "SYMBOL", "DOLLAR", "DOLLARSYM", "WS", "Tokens", "UNOP", "BINOP", "TERNOP", "METH", "ARRAY", "NUM_LIT", "STRING_LIT"
    };
    public static final int MINUS=49;
    public static final int ARRAY=73;
    public static final int NUMBER=12;
    public static final int FALSE=23;
    public static final int METHOD=18;
    public static final int FLOAT=11;
    public static final int POSDIGIT=5;
    public static final int LEQ=41;
    public static final int TWIDDLE=45;
    public static final int RULE=16;
    public static final int MOD=50;
    public static final int GEQ=40;
    public static final int DQUOTE=56;
    public static final int BOR=42;
    public static final int OR=33;
    public static final int STRING_LIT=75;
    public static final int BAREINT=7;
    public static final int LBRACE=28;
    public static final int NEWLINE=58;
    public static final int DOT=31;
    public static final int RBRACE=29;
    public static final int INTEGER=8;
    public static final int AND=34;
    public static final int NUM_LIT=74;
    public static final int ASSIGN=32;
    public static final int SYMBOL=64;
    public static final int RPAREN=25;
    public static final int SIGN=6;
    public static final int LPAREN=24;
    public static final int METH=72;
    public static final int PLUS=48;
    public static final int DIGIT=4;
    public static final int LINE=19;
    public static final int BINOP=70;
    public static final int BAND=43;
    public static final int NEQ=37;
    public static final int TERNOP=71;
    public static final int SPACE=57;
    public static final int LETTER=53;
    public static final int LSQUARE=26;
    public static final int DO=15;
    public static final int POINT=9;
    public static final int BARESYM=61;
    public static final int NOTHING=21;
    public static final int SEPR=30;
    public static final int WS=67;
    public static final int EQ=36;
    public static final int STRING=60;
    public static final int QUOTSYM=62;
    public static final int LT=39;
    public static final int GT=38;
    public static final int DOLLAR=65;
    public static final int RSQUARE=27;
    public static final int TERN_IF=51;
    public static final int QUOTE=55;
    public static final int UNOP=69;
    public static final int CLASS=17;
    public static final int MUL=46;
    public static final int EXPPART=10;
    public static final int PUNCT=59;
    public static final int IF=14;
    public static final int EOF=-1;
    public static final int Tokens=68;
    public static final int COLON=52;
    public static final int DIV=47;
    public static final int DOTSYM=63;
    public static final int BXOR=44;
    public static final int ENDRULE=20;
    public static final int BIND=13;
    public static final int NOT=35;
    public static final int TRUE=22;
    public static final int UNDERSCORE=54;
    public static final int DOLLARSYM=66;

        public ECAGrammarParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[59+1];
         }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "dd/grammar/ECAGrammar.g"; }


    public static class eca_script_rule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eca_script_rule
    // dd/grammar/ECAGrammar.g:26:1: eca_script_rule : rule= eca_script_rule_one EOF -> ^( $rule) ;
    public final eca_script_rule_return eca_script_rule() throws RecognitionException {
        eca_script_rule_return retval = new eca_script_rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF1=null;
        eca_script_rule_one_return rule = null;


        Object EOF1_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_eca_script_rule_one=new RewriteRuleSubtreeStream(adaptor,"rule eca_script_rule_one");
        try {
            // dd/grammar/ECAGrammar.g:26:17: (rule= eca_script_rule_one EOF -> ^( $rule) )
            // dd/grammar/ECAGrammar.g:26:19: rule= eca_script_rule_one EOF
            {
            pushFollow(FOLLOW_eca_script_rule_one_in_eca_script_rule88);
            rule=eca_script_rule_one();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_eca_script_rule_one.add(rule.getTree());
            EOF1=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_script_rule90); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF1);


            // AST REWRITE
            // elements: rule
            // token labels: 
            // rule labels: rule, retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"token rule",rule!=null?rule.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 26:48: -> ^( $rule)
            {
                // dd/grammar/ECAGrammar.g:26:51: ^( $rule)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_rule.nextNode(), root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end eca_script_rule

    public static class eca_script_rule_one_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eca_script_rule_one
    // dd/grammar/ECAGrammar.g:28:1: eca_script_rule_one : RULE n= SYMBOL CLASS cl= SYMBOL METHOD m= SYMBOL LINE l= NUMBER BIND e= event IF c= condition DO a= action ENDRULE -> ^( RULE $n $cl $m $l $e $c $a) ;
    public final eca_script_rule_one_return eca_script_rule_one() throws RecognitionException {
        eca_script_rule_one_return retval = new eca_script_rule_one_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token n=null;
        Token cl=null;
        Token m=null;
        Token l=null;
        Token RULE2=null;
        Token CLASS3=null;
        Token METHOD4=null;
        Token LINE5=null;
        Token BIND6=null;
        Token IF7=null;
        Token DO8=null;
        Token ENDRULE9=null;
        event_return e = null;

        condition_return c = null;

        action_return a = null;


        Object n_tree=null;
        Object cl_tree=null;
        Object m_tree=null;
        Object l_tree=null;
        Object RULE2_tree=null;
        Object CLASS3_tree=null;
        Object METHOD4_tree=null;
        Object LINE5_tree=null;
        Object BIND6_tree=null;
        Object IF7_tree=null;
        Object DO8_tree=null;
        Object ENDRULE9_tree=null;
        RewriteRuleTokenStream stream_CLASS=new RewriteRuleTokenStream(adaptor,"token CLASS");
        RewriteRuleTokenStream stream_RULE=new RewriteRuleTokenStream(adaptor,"token RULE");
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_LINE=new RewriteRuleTokenStream(adaptor,"token LINE");
        RewriteRuleTokenStream stream_ENDRULE=new RewriteRuleTokenStream(adaptor,"token ENDRULE");
        RewriteRuleTokenStream stream_BIND=new RewriteRuleTokenStream(adaptor,"token BIND");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");
        RewriteRuleTokenStream stream_METHOD=new RewriteRuleTokenStream(adaptor,"token METHOD");
        RewriteRuleSubtreeStream stream_action=new RewriteRuleSubtreeStream(adaptor,"rule action");
        RewriteRuleSubtreeStream stream_event=new RewriteRuleSubtreeStream(adaptor,"rule event");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        try {
            // dd/grammar/ECAGrammar.g:29:2: ( RULE n= SYMBOL CLASS cl= SYMBOL METHOD m= SYMBOL LINE l= NUMBER BIND e= event IF c= condition DO a= action ENDRULE -> ^( RULE $n $cl $m $l $e $c $a) )
            // dd/grammar/ECAGrammar.g:29:4: RULE n= SYMBOL CLASS cl= SYMBOL METHOD m= SYMBOL LINE l= NUMBER BIND e= event IF c= condition DO a= action ENDRULE
            {
            RULE2=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_eca_script_rule_one107); if (failed) return retval;
            if ( backtracking==0 ) stream_RULE.add(RULE2);

            n=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_eca_script_rule_one111); if (failed) return retval;
            if ( backtracking==0 ) stream_SYMBOL.add(n);

            CLASS3=(Token)input.LT(1);
            match(input,CLASS,FOLLOW_CLASS_in_eca_script_rule_one115); if (failed) return retval;
            if ( backtracking==0 ) stream_CLASS.add(CLASS3);

            cl=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_eca_script_rule_one119); if (failed) return retval;
            if ( backtracking==0 ) stream_SYMBOL.add(cl);

            METHOD4=(Token)input.LT(1);
            match(input,METHOD,FOLLOW_METHOD_in_eca_script_rule_one123); if (failed) return retval;
            if ( backtracking==0 ) stream_METHOD.add(METHOD4);

            m=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_eca_script_rule_one127); if (failed) return retval;
            if ( backtracking==0 ) stream_SYMBOL.add(m);

            LINE5=(Token)input.LT(1);
            match(input,LINE,FOLLOW_LINE_in_eca_script_rule_one131); if (failed) return retval;
            if ( backtracking==0 ) stream_LINE.add(LINE5);

            l=(Token)input.LT(1);
            match(input,NUMBER,FOLLOW_NUMBER_in_eca_script_rule_one135); if (failed) return retval;
            if ( backtracking==0 ) stream_NUMBER.add(l);

            BIND6=(Token)input.LT(1);
            match(input,BIND,FOLLOW_BIND_in_eca_script_rule_one139); if (failed) return retval;
            if ( backtracking==0 ) stream_BIND.add(BIND6);

            pushFollow(FOLLOW_event_in_eca_script_rule_one143);
            e=event();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_event.add(e.getTree());
            IF7=(Token)input.LT(1);
            match(input,IF,FOLLOW_IF_in_eca_script_rule_one147); if (failed) return retval;
            if ( backtracking==0 ) stream_IF.add(IF7);

            pushFollow(FOLLOW_condition_in_eca_script_rule_one151);
            c=condition();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_condition.add(c.getTree());
            DO8=(Token)input.LT(1);
            match(input,DO,FOLLOW_DO_in_eca_script_rule_one155); if (failed) return retval;
            if ( backtracking==0 ) stream_DO.add(DO8);

            pushFollow(FOLLOW_action_in_eca_script_rule_one159);
            a=action();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action.add(a.getTree());
            ENDRULE9=(Token)input.LT(1);
            match(input,ENDRULE,FOLLOW_ENDRULE_in_eca_script_rule_one163); if (failed) return retval;
            if ( backtracking==0 ) stream_ENDRULE.add(ENDRULE9);


            // AST REWRITE
            // elements: e, n, c, l, a, RULE, m, cl
            // token labels: cl, m, n, l
            // rule labels: a, c, retval, e
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_cl=new RewriteRuleTokenStream(adaptor,"token cl",cl);
            RewriteRuleTokenStream stream_m=new RewriteRuleTokenStream(adaptor,"token m",m);
            RewriteRuleTokenStream stream_n=new RewriteRuleTokenStream(adaptor,"token n",n);
            RewriteRuleTokenStream stream_l=new RewriteRuleTokenStream(adaptor,"token l",l);
            RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"token a",a!=null?a.tree:null);
            RewriteRuleSubtreeStream stream_c=new RewriteRuleSubtreeStream(adaptor,"token c",c!=null?c.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"token e",e!=null?e.tree:null);

            root_0 = (Object)adaptor.nil();
            // 36:12: -> ^( RULE $n $cl $m $l $e $c $a)
            {
                // dd/grammar/ECAGrammar.g:36:15: ^( RULE $n $cl $m $l $e $c $a)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_RULE.next(), root_1);

                adaptor.addChild(root_1, stream_n.next());
                adaptor.addChild(root_1, stream_cl.next());
                adaptor.addChild(root_1, stream_m.next());
                adaptor.addChild(root_1, stream_l.next());
                adaptor.addChild(root_1, stream_e.next());
                adaptor.addChild(root_1, stream_c.next());
                adaptor.addChild(root_1, stream_a.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end eca_script_rule_one

    public static class eca_rule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eca_rule
    // dd/grammar/ECAGrammar.g:38:1: eca_rule : eca EOF -> ^( eca ) ;
    public final eca_rule_return eca_rule() throws RecognitionException {
        eca_rule_return retval = new eca_rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF11=null;
        eca_return eca10 = null;


        Object EOF11_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_eca=new RewriteRuleSubtreeStream(adaptor,"rule eca");
        try {
            // dd/grammar/ECAGrammar.g:38:10: ( eca EOF -> ^( eca ) )
            // dd/grammar/ECAGrammar.g:38:12: eca EOF
            {
            pushFollow(FOLLOW_eca_in_eca_rule200);
            eca10=eca();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_eca.add(eca10.getTree());
            EOF11=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_rule202); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF11);


            // AST REWRITE
            // elements: eca
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 38:20: -> ^( eca )
            {
                // dd/grammar/ECAGrammar.g:38:23: ^( eca )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_eca.nextNode(), root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end eca_rule

    public static class eca_event_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eca_event
    // dd/grammar/ECAGrammar.g:41:1: eca_event : event EOF -> ^( event ) ;
    public final eca_event_return eca_event() throws RecognitionException {
        eca_event_return retval = new eca_event_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF13=null;
        event_return event12 = null;


        Object EOF13_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_event=new RewriteRuleSubtreeStream(adaptor,"rule event");
        try {
            // dd/grammar/ECAGrammar.g:41:11: ( event EOF -> ^( event ) )
            // dd/grammar/ECAGrammar.g:41:13: event EOF
            {
            pushFollow(FOLLOW_event_in_eca_event219);
            event12=event();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_event.add(event12.getTree());
            EOF13=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_event221); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF13);


            // AST REWRITE
            // elements: event
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 41:23: -> ^( event )
            {
                // dd/grammar/ECAGrammar.g:41:26: ^( event )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_event.nextNode(), root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end eca_event

    public static class eca_condition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eca_condition
    // dd/grammar/ECAGrammar.g:44:1: eca_condition : condition EOF -> ^( condition ) ;
    public final eca_condition_return eca_condition() throws RecognitionException {
        eca_condition_return retval = new eca_condition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF15=null;
        condition_return condition14 = null;


        Object EOF15_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        try {
            // dd/grammar/ECAGrammar.g:44:15: ( condition EOF -> ^( condition ) )
            // dd/grammar/ECAGrammar.g:44:17: condition EOF
            {
            pushFollow(FOLLOW_condition_in_eca_condition237);
            condition14=condition();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_condition.add(condition14.getTree());
            EOF15=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_condition239); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF15);


            // AST REWRITE
            // elements: condition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 44:31: -> ^( condition )
            {
                // dd/grammar/ECAGrammar.g:44:34: ^( condition )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_condition.nextNode(), root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end eca_condition

    public static class eca_action_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eca_action
    // dd/grammar/ECAGrammar.g:47:1: eca_action : action EOF -> ^( action ) ;
    public final eca_action_return eca_action() throws RecognitionException {
        eca_action_return retval = new eca_action_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF17=null;
        action_return action16 = null;


        Object EOF17_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_action=new RewriteRuleSubtreeStream(adaptor,"rule action");
        try {
            // dd/grammar/ECAGrammar.g:47:12: ( action EOF -> ^( action ) )
            // dd/grammar/ECAGrammar.g:47:14: action EOF
            {
            pushFollow(FOLLOW_action_in_eca_action255);
            action16=action();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action.add(action16.getTree());
            EOF17=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_action257); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF17);


            // AST REWRITE
            // elements: action
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 47:25: -> ^( action )
            {
                // dd/grammar/ECAGrammar.g:47:28: ^( action )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_action.nextNode(), root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end eca_action

    public static class eca_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eca
    // dd/grammar/ECAGrammar.g:50:1: eca : BIND e= event IF c= condition DO a= action -> ^( BIND $e $c $a) ;
    public final eca_return eca() throws RecognitionException {
        eca_return retval = new eca_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BIND18=null;
        Token IF19=null;
        Token DO20=null;
        event_return e = null;

        condition_return c = null;

        action_return a = null;


        Object BIND18_tree=null;
        Object IF19_tree=null;
        Object DO20_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_BIND=new RewriteRuleTokenStream(adaptor,"token BIND");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleSubtreeStream stream_action=new RewriteRuleSubtreeStream(adaptor,"rule action");
        RewriteRuleSubtreeStream stream_event=new RewriteRuleSubtreeStream(adaptor,"rule event");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        try {
            // dd/grammar/ECAGrammar.g:50:5: ( BIND e= event IF c= condition DO a= action -> ^( BIND $e $c $a) )
            // dd/grammar/ECAGrammar.g:50:7: BIND e= event IF c= condition DO a= action
            {
            BIND18=(Token)input.LT(1);
            match(input,BIND,FOLLOW_BIND_in_eca273); if (failed) return retval;
            if ( backtracking==0 ) stream_BIND.add(BIND18);

            pushFollow(FOLLOW_event_in_eca277);
            e=event();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_event.add(e.getTree());
            IF19=(Token)input.LT(1);
            match(input,IF,FOLLOW_IF_in_eca281); if (failed) return retval;
            if ( backtracking==0 ) stream_IF.add(IF19);

            pushFollow(FOLLOW_condition_in_eca285);
            c=condition();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_condition.add(c.getTree());
            DO20=(Token)input.LT(1);
            match(input,DO,FOLLOW_DO_in_eca289); if (failed) return retval;
            if ( backtracking==0 ) stream_DO.add(DO20);

            pushFollow(FOLLOW_action_in_eca293);
            a=action();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action.add(a.getTree());

            // AST REWRITE
            // elements: c, e, a, BIND
            // token labels: 
            // rule labels: a, c, retval, e
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"token a",a!=null?a.tree:null);
            RewriteRuleSubtreeStream stream_c=new RewriteRuleSubtreeStream(adaptor,"token c",c!=null?c.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"token e",e!=null?e.tree:null);

            root_0 = (Object)adaptor.nil();
            // 52:15: -> ^( BIND $e $c $a)
            {
                // dd/grammar/ECAGrammar.g:52:18: ^( BIND $e $c $a)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_BIND.next(), root_1);

                adaptor.addChild(root_1, stream_e.next());
                adaptor.addChild(root_1, stream_c.next());
                adaptor.addChild(root_1, stream_a.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end eca

    public static class event_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start event
    // dd/grammar/ECAGrammar.g:57:1: event : bindings ;
    public final event_return event() throws RecognitionException {
        event_return retval = new event_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        bindings_return bindings21 = null;



        try {
            // dd/grammar/ECAGrammar.g:57:7: ( bindings )
            // dd/grammar/ECAGrammar.g:57:9: bindings
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_bindings_in_event320);
            bindings21=bindings();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, bindings21.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end event

    public static class bindings_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start bindings
    // dd/grammar/ECAGrammar.g:62:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );
    public final bindings_return bindings() throws RecognitionException {
        bindings_return retval = new bindings_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR23=null;
        binding_return binding22 = null;

        bindings_return bindings24 = null;

        binding_return binding25 = null;


        Object SEPR23_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_bindings=new RewriteRuleSubtreeStream(adaptor,"rule bindings");
        RewriteRuleSubtreeStream stream_binding=new RewriteRuleSubtreeStream(adaptor,"rule binding");
        try {
            // dd/grammar/ECAGrammar.g:62:10: ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==SYMBOL) ) {
                int LA1_1 = input.LA(2);

                if ( (synpred1()) ) {
                    alt1=1;
                }
                else if ( (true) ) {
                    alt1=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("62:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );", 1, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("62:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:62:12: binding SEPR bindings
                    {
                    pushFollow(FOLLOW_binding_in_bindings332);
                    binding22=binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_binding.add(binding22.getTree());
                    SEPR23=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_bindings334); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR23);

                    pushFollow(FOLLOW_bindings_in_bindings336);
                    bindings24=bindings();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_bindings.add(bindings24.getTree());

                    // AST REWRITE
                    // elements: binding, SEPR, bindings
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 62:34: -> ^( SEPR binding bindings )
                    {
                        // dd/grammar/ECAGrammar.g:62:37: ^( SEPR binding bindings )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_SEPR.next(), root_1);

                        adaptor.addChild(root_1, stream_binding.next());
                        adaptor.addChild(root_1, stream_bindings.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:63:4: binding
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_binding_in_bindings351);
                    binding25=binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, binding25.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end bindings

    public static class binding_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start binding
    // dd/grammar/ECAGrammar.g:66:1: binding : bind_sym ASSIGN expr -> ^( ASSIGN bind_sym expr ) ;
    public final binding_return binding() throws RecognitionException {
        binding_return retval = new binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASSIGN27=null;
        bind_sym_return bind_sym26 = null;

        expr_return expr28 = null;


        Object ASSIGN27_tree=null;
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_bind_sym=new RewriteRuleSubtreeStream(adaptor,"rule bind_sym");
        try {
            // dd/grammar/ECAGrammar.g:66:9: ( bind_sym ASSIGN expr -> ^( ASSIGN bind_sym expr ) )
            // dd/grammar/ECAGrammar.g:66:11: bind_sym ASSIGN expr
            {
            pushFollow(FOLLOW_bind_sym_in_binding361);
            bind_sym26=bind_sym();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_bind_sym.add(bind_sym26.getTree());
            ASSIGN27=(Token)input.LT(1);
            match(input,ASSIGN,FOLLOW_ASSIGN_in_binding363); if (failed) return retval;
            if ( backtracking==0 ) stream_ASSIGN.add(ASSIGN27);

            pushFollow(FOLLOW_expr_in_binding365);
            expr28=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_expr.add(expr28.getTree());

            // AST REWRITE
            // elements: expr, bind_sym, ASSIGN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 66:32: -> ^( ASSIGN bind_sym expr )
            {
                // dd/grammar/ECAGrammar.g:66:35: ^( ASSIGN bind_sym expr )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ASSIGN.next(), root_1);

                adaptor.addChild(root_1, stream_bind_sym.next());
                adaptor.addChild(root_1, stream_expr.next());

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end binding

    public static class bind_sym_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start bind_sym
    // dd/grammar/ECAGrammar.g:70:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );
    public final bind_sym_return bind_sym() throws RecognitionException {
        bind_sym_return retval = new bind_sym_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token v=null;
        Token t=null;
        Token COLON29=null;
        Token SYMBOL30=null;

        Object v_tree=null;
        Object t_tree=null;
        Object COLON29_tree=null;
        Object SYMBOL30_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");

        try {
            // dd/grammar/ECAGrammar.g:70:10: (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==SYMBOL) ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1==COLON) ) {
                    alt2=1;
                }
                else if ( (LA2_1==ASSIGN) ) {
                    alt2=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("70:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );", 2, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("70:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:70:12: v= SYMBOL COLON t= SYMBOL
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym388); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    COLON29=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_bind_sym390); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON29);

                    t=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym394); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(t);


                    // AST REWRITE
                    // elements: t, COLON, v
                    // token labels: t, v
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_t=new RewriteRuleTokenStream(adaptor,"token t",t);
                    RewriteRuleTokenStream stream_v=new RewriteRuleTokenStream(adaptor,"token v",v);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 70:36: -> ^( COLON $v $t)
                    {
                        // dd/grammar/ECAGrammar.g:70:39: ^( COLON $v $t)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_COLON.next(), root_1);

                        adaptor.addChild(root_1, stream_v.next());
                        adaptor.addChild(root_1, stream_t.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:71:4: SYMBOL
                    {
                    root_0 = (Object)adaptor.nil();

                    SYMBOL30=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym412); if (failed) return retval;
                    if ( backtracking==0 ) {
                    SYMBOL30_tree = (Object)adaptor.create(SYMBOL30);
                    adaptor.addChild(root_0, SYMBOL30_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end bind_sym

    public static class condition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start condition
    // dd/grammar/ECAGrammar.g:78:1: condition : ( TRUE -> ^( TRUE ) | FALSE -> ^( FALSE ) | expr );
    public final condition_return condition() throws RecognitionException {
        condition_return retval = new condition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TRUE31=null;
        Token FALSE32=null;
        expr_return expr33 = null;


        Object TRUE31_tree=null;
        Object FALSE32_tree=null;
        RewriteRuleTokenStream stream_TRUE=new RewriteRuleTokenStream(adaptor,"token TRUE");
        RewriteRuleTokenStream stream_FALSE=new RewriteRuleTokenStream(adaptor,"token FALSE");

        try {
            // dd/grammar/ECAGrammar.g:78:11: ( TRUE -> ^( TRUE ) | FALSE -> ^( FALSE ) | expr )
            int alt3=3;
            switch ( input.LA(1) ) {
            case TRUE:
                {
                alt3=1;
                }
                break;
            case FALSE:
                {
                alt3=2;
                }
                break;
            case NUMBER:
            case LPAREN:
            case NOT:
            case TWIDDLE:
            case STRING:
            case SYMBOL:
            case DOLLARSYM:
                {
                alt3=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("78:1: condition : ( TRUE -> ^( TRUE ) | FALSE -> ^( FALSE ) | expr );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:78:13: TRUE
                    {
                    TRUE31=(Token)input.LT(1);
                    match(input,TRUE,FOLLOW_TRUE_in_condition426); if (failed) return retval;
                    if ( backtracking==0 ) stream_TRUE.add(TRUE31);


                    // AST REWRITE
                    // elements: TRUE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 78:19: -> ^( TRUE )
                    {
                        // dd/grammar/ECAGrammar.g:78:22: ^( TRUE )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_TRUE.next(), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:79:4: FALSE
                    {
                    FALSE32=(Token)input.LT(1);
                    match(input,FALSE,FOLLOW_FALSE_in_condition438); if (failed) return retval;
                    if ( backtracking==0 ) stream_FALSE.add(FALSE32);


                    // AST REWRITE
                    // elements: FALSE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 79:11: -> ^( FALSE )
                    {
                        // dd/grammar/ECAGrammar.g:79:14: ^( FALSE )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_FALSE.next(), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:80:4: expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_condition450);
                    expr33=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expr33.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end condition

    public static class action_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start action
    // dd/grammar/ECAGrammar.g:87:1: action : ( NOTHING -> ^( NOTHING ) | action_expr_list );
    public final action_return action() throws RecognitionException {
        action_return retval = new action_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token NOTHING34=null;
        action_expr_list_return action_expr_list35 = null;


        Object NOTHING34_tree=null;
        RewriteRuleTokenStream stream_NOTHING=new RewriteRuleTokenStream(adaptor,"token NOTHING");

        try {
            // dd/grammar/ECAGrammar.g:87:8: ( NOTHING -> ^( NOTHING ) | action_expr_list )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==NOTHING) ) {
                alt4=1;
            }
            else if ( (LA4_0==NUMBER||LA4_0==LPAREN||LA4_0==NOT||LA4_0==TWIDDLE||LA4_0==STRING||LA4_0==SYMBOL||LA4_0==DOLLARSYM) ) {
                alt4=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("87:1: action : ( NOTHING -> ^( NOTHING ) | action_expr_list );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:87:10: NOTHING
                    {
                    NOTHING34=(Token)input.LT(1);
                    match(input,NOTHING,FOLLOW_NOTHING_in_action464); if (failed) return retval;
                    if ( backtracking==0 ) stream_NOTHING.add(NOTHING34);


                    // AST REWRITE
                    // elements: NOTHING
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 87:19: -> ^( NOTHING )
                    {
                        // dd/grammar/ECAGrammar.g:87:22: ^( NOTHING )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_NOTHING.next(), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:88:4: action_expr_list
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_action_expr_list_in_action476);
                    action_expr_list35=action_expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, action_expr_list35.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end action

    public static class action_expr_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start action_expr_list
    // dd/grammar/ECAGrammar.g:91:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );
    public final action_expr_list_return action_expr_list() throws RecognitionException {
        action_expr_list_return retval = new action_expr_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR37=null;
        action_expr_return action_expr36 = null;

        action_expr_list_return action_expr_list38 = null;

        action_expr_return action_expr39 = null;


        Object SEPR37_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_action_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule action_expr_list");
        RewriteRuleSubtreeStream stream_action_expr=new RewriteRuleSubtreeStream(adaptor,"rule action_expr");
        try {
            // dd/grammar/ECAGrammar.g:92:2: ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr )
            int alt5=2;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                int LA5_1 = input.LA(2);

                if ( (synpred6()) ) {
                    alt5=1;
                }
                else if ( (true) ) {
                    alt5=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("91:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 1, input);

                    throw nvae;
                }
                }
                break;
            case SYMBOL:
                {
                int LA5_2 = input.LA(2);

                if ( (synpred6()) ) {
                    alt5=1;
                }
                else if ( (true) ) {
                    alt5=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("91:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 2, input);

                    throw nvae;
                }
                }
                break;
            case NUMBER:
                {
                int LA5_3 = input.LA(2);

                if ( (synpred6()) ) {
                    alt5=1;
                }
                else if ( (true) ) {
                    alt5=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("91:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 3, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
                {
                int LA5_4 = input.LA(2);

                if ( (synpred6()) ) {
                    alt5=1;
                }
                else if ( (true) ) {
                    alt5=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("91:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 4, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                int LA5_5 = input.LA(2);

                if ( (synpred6()) ) {
                    alt5=1;
                }
                else if ( (true) ) {
                    alt5=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("91:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                int LA5_6 = input.LA(2);

                if ( (synpred6()) ) {
                    alt5=1;
                }
                else if ( (true) ) {
                    alt5=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("91:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 6, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("91:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:92:4: action_expr SEPR action_expr_list
                    {
                    pushFollow(FOLLOW_action_expr_in_action_expr_list487);
                    action_expr36=action_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_action_expr.add(action_expr36.getTree());
                    SEPR37=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_action_expr_list489); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR37);

                    pushFollow(FOLLOW_action_expr_list_in_action_expr_list491);
                    action_expr_list38=action_expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_action_expr_list.add(action_expr_list38.getTree());

                    // AST REWRITE
                    // elements: action_expr, action_expr_list, SEPR
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 92:38: -> ^( SEPR action_expr action_expr_list )
                    {
                        // dd/grammar/ECAGrammar.g:92:41: ^( SEPR action_expr action_expr_list )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_SEPR.next(), root_1);

                        adaptor.addChild(root_1, stream_action_expr.next());
                        adaptor.addChild(root_1, stream_action_expr_list.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:93:4: action_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_action_expr_in_action_expr_list506);
                    action_expr39=action_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, action_expr39.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end action_expr_list

    public static class action_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start action_expr
    // dd/grammar/ECAGrammar.g:96:1: action_expr : expr ;
    public final action_expr_return action_expr() throws RecognitionException {
        action_expr_return retval = new action_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        expr_return expr40 = null;



        try {
            // dd/grammar/ECAGrammar.g:96:13: ( expr )
            // dd/grammar/ECAGrammar.g:96:15: expr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expr_in_action_expr516);
            expr40=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expr40.getTree());

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end action_expr

    public static class expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expr
    // dd/grammar/ECAGrammar.g:99:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );
    public final expr_return expr() throws RecognitionException {
        expr_return retval = new expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TERN_IF47=null;
        Token COLON48=null;
        simple_expr_return cond = null;

        expr_return iftrue = null;

        expr_return iffalse = null;

        simple_expr_return simple_expr41 = null;

        infix_oper_return infix_oper42 = null;

        expr_return expr43 = null;

        simple_expr_return simple_expr44 = null;

        unary_oper_return unary_oper45 = null;

        expr_return expr46 = null;


        Object TERN_IF47_tree=null;
        Object COLON48_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_TERN_IF=new RewriteRuleTokenStream(adaptor,"token TERN_IF");
        RewriteRuleSubtreeStream stream_unary_oper=new RewriteRuleSubtreeStream(adaptor,"rule unary_oper");
        RewriteRuleSubtreeStream stream_infix_oper=new RewriteRuleSubtreeStream(adaptor,"rule infix_oper");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_simple_expr=new RewriteRuleSubtreeStream(adaptor,"rule simple_expr");
        try {
            // dd/grammar/ECAGrammar.g:99:6: ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) )
            int alt6=4;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                switch ( input.LA(2) ) {
                case OR:
                case AND:
                case EQ:
                case NEQ:
                case GT:
                case LT:
                case GEQ:
                case LEQ:
                case BOR:
                case BAND:
                case BXOR:
                case MUL:
                case DIV:
                case PLUS:
                case MINUS:
                    {
                    alt6=1;
                    }
                    break;
                case TERN_IF:
                    {
                    alt6=4;
                    }
                    break;
                case EOF:
                case IF:
                case DO:
                case ENDRULE:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt6=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("99:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 6, 1, input);

                    throw nvae;
                }

                }
                break;
            case SYMBOL:
                {
                switch ( input.LA(2) ) {
                case LPAREN:
                    {
                    int LA6_10 = input.LA(3);

                    if ( (synpred7()) ) {
                        alt6=1;
                    }
                    else if ( (synpred8()) ) {
                        alt6=2;
                    }
                    else if ( (true) ) {
                        alt6=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("99:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 6, 10, input);

                        throw nvae;
                    }
                    }
                    break;
                case TERN_IF:
                    {
                    alt6=4;
                    }
                    break;
                case LSQUARE:
                    {
                    int LA6_11 = input.LA(3);

                    if ( (synpred7()) ) {
                        alt6=1;
                    }
                    else if ( (synpred8()) ) {
                        alt6=2;
                    }
                    else if ( (true) ) {
                        alt6=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("99:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 6, 11, input);

                        throw nvae;
                    }
                    }
                    break;
                case EOF:
                case IF:
                case DO:
                case ENDRULE:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt6=2;
                    }
                    break;
                case OR:
                case AND:
                case EQ:
                case NEQ:
                case GT:
                case LT:
                case GEQ:
                case LEQ:
                case BOR:
                case BAND:
                case BXOR:
                case MUL:
                case DIV:
                case PLUS:
                case MINUS:
                    {
                    alt6=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("99:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 6, 2, input);

                    throw nvae;
                }

                }
                break;
            case NUMBER:
                {
                switch ( input.LA(2) ) {
                case OR:
                case AND:
                case EQ:
                case NEQ:
                case GT:
                case LT:
                case GEQ:
                case LEQ:
                case BOR:
                case BAND:
                case BXOR:
                case MUL:
                case DIV:
                case PLUS:
                case MINUS:
                    {
                    alt6=1;
                    }
                    break;
                case TERN_IF:
                    {
                    alt6=4;
                    }
                    break;
                case EOF:
                case IF:
                case DO:
                case ENDRULE:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt6=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("99:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 6, 3, input);

                    throw nvae;
                }

                }
                break;
            case STRING:
                {
                switch ( input.LA(2) ) {
                case EOF:
                case IF:
                case DO:
                case ENDRULE:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt6=2;
                    }
                    break;
                case TERN_IF:
                    {
                    alt6=4;
                    }
                    break;
                case OR:
                case AND:
                case EQ:
                case NEQ:
                case GT:
                case LT:
                case GEQ:
                case LEQ:
                case BOR:
                case BAND:
                case BXOR:
                case MUL:
                case DIV:
                case PLUS:
                case MINUS:
                    {
                    alt6=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("99:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 6, 4, input);

                    throw nvae;
                }

                }
                break;
            case LPAREN:
                {
                int LA6_5 = input.LA(2);

                if ( (synpred7()) ) {
                    alt6=1;
                }
                else if ( (synpred8()) ) {
                    alt6=2;
                }
                else if ( (true) ) {
                    alt6=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("99:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 6, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                alt6=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("99:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:99:8: simple_expr infix_oper expr
                    {
                    pushFollow(FOLLOW_simple_expr_in_expr526);
                    simple_expr41=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_simple_expr.add(simple_expr41.getTree());
                    pushFollow(FOLLOW_infix_oper_in_expr528);
                    infix_oper42=infix_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_infix_oper.add(infix_oper42.getTree());
                    pushFollow(FOLLOW_expr_in_expr530);
                    expr43=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr43.getTree());

                    // AST REWRITE
                    // elements: infix_oper, expr, simple_expr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 99:37: -> ^( BINOP infix_oper simple_expr expr )
                    {
                        // dd/grammar/ECAGrammar.g:99:40: ^( BINOP infix_oper simple_expr expr )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(BINOP, "BINOP"), root_1);

                        adaptor.addChild(root_1, stream_infix_oper.next());
                        adaptor.addChild(root_1, stream_simple_expr.next());
                        adaptor.addChild(root_1, stream_expr.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:100:4: simple_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_expr_in_expr548);
                    simple_expr44=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, simple_expr44.getTree());

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:101:4: unary_oper expr
                    {
                    pushFollow(FOLLOW_unary_oper_in_expr553);
                    unary_oper45=unary_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_unary_oper.add(unary_oper45.getTree());
                    pushFollow(FOLLOW_expr_in_expr555);
                    expr46=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr46.getTree());

                    // AST REWRITE
                    // elements: expr, unary_oper
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 101:22: -> ^( UNOP unary_oper expr )
                    {
                        // dd/grammar/ECAGrammar.g:101:25: ^( UNOP unary_oper expr )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(UNOP, "UNOP"), root_1);

                        adaptor.addChild(root_1, stream_unary_oper.next());
                        adaptor.addChild(root_1, stream_expr.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 4 :
                    // dd/grammar/ECAGrammar.g:102:4: cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr
                    {
                    pushFollow(FOLLOW_simple_expr_in_expr574);
                    cond=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_simple_expr.add(cond.getTree());
                    TERN_IF47=(Token)input.LT(1);
                    match(input,TERN_IF,FOLLOW_TERN_IF_in_expr576); if (failed) return retval;
                    if ( backtracking==0 ) stream_TERN_IF.add(TERN_IF47);

                    pushFollow(FOLLOW_expr_in_expr580);
                    iftrue=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(iftrue.getTree());
                    COLON48=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_expr582); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON48);

                    pushFollow(FOLLOW_expr_in_expr586);
                    iffalse=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(iffalse.getTree());

                    // AST REWRITE
                    // elements: iftrue, cond, iffalse
                    // token labels: 
                    // rule labels: iftrue, iffalse, cond, retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_iftrue=new RewriteRuleSubtreeStream(adaptor,"token iftrue",iftrue!=null?iftrue.tree:null);
                    RewriteRuleSubtreeStream stream_iffalse=new RewriteRuleSubtreeStream(adaptor,"token iffalse",iffalse!=null?iffalse.tree:null);
                    RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"token cond",cond!=null?cond.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 102:60: -> ^( TERNOP $cond $iftrue $iffalse)
                    {
                        // dd/grammar/ECAGrammar.g:102:63: ^( TERNOP $cond $iftrue $iffalse)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(TERNOP, "TERNOP"), root_1);

                        adaptor.addChild(root_1, stream_cond.next());
                        adaptor.addChild(root_1, stream_iftrue.next());
                        adaptor.addChild(root_1, stream_iffalse.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expr

    public static class simple_expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start simple_expr
    // dd/grammar/ECAGrammar.g:105:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );
    public final simple_expr_return simple_expr() throws RecognitionException {
        simple_expr_return retval = new simple_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token v=null;
        Token LPAREN49=null;
        Token RPAREN50=null;
        Token LPAREN51=null;
        Token RPAREN52=null;
        Token NUMBER53=null;
        Token STRING54=null;
        Token LPAREN55=null;
        Token RPAREN57=null;
        array_idx_return idx = null;

        expr_list_return args = null;

        expr_return expr56 = null;


        Object v_tree=null;
        Object LPAREN49_tree=null;
        Object RPAREN50_tree=null;
        Object LPAREN51_tree=null;
        Object RPAREN52_tree=null;
        Object NUMBER53_tree=null;
        Object STRING54_tree=null;
        Object LPAREN55_tree=null;
        Object RPAREN57_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");
        RewriteRuleSubtreeStream stream_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule expr_list");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_array_idx=new RewriteRuleSubtreeStream(adaptor,"rule array_idx");
        try {
            // dd/grammar/ECAGrammar.g:105:13: (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) )
            int alt7=8;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                alt7=1;
                }
                break;
            case SYMBOL:
                {
                switch ( input.LA(2) ) {
                case LPAREN:
                    {
                    int LA7_6 = input.LA(3);

                    if ( (LA7_6==RPAREN) ) {
                        alt7=3;
                    }
                    else if ( (LA7_6==NUMBER||LA7_6==LPAREN||LA7_6==NOT||LA7_6==TWIDDLE||LA7_6==STRING||LA7_6==SYMBOL||LA7_6==DOLLARSYM) ) {
                        alt7=5;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("105:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 7, 6, input);

                        throw nvae;
                    }
                    }
                    break;
                case LSQUARE:
                    {
                    alt7=2;
                    }
                    break;
                case EOF:
                case IF:
                case DO:
                case ENDRULE:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case OR:
                case AND:
                case EQ:
                case NEQ:
                case GT:
                case LT:
                case GEQ:
                case LEQ:
                case BOR:
                case BAND:
                case BXOR:
                case MUL:
                case DIV:
                case PLUS:
                case MINUS:
                case TERN_IF:
                case COLON:
                    {
                    alt7=4;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("105:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 7, 2, input);

                    throw nvae;
                }

                }
                break;
            case NUMBER:
                {
                alt7=6;
                }
                break;
            case STRING:
                {
                alt7=7;
                }
                break;
            case LPAREN:
                {
                alt7=8;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("105:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:105:15: v= DOLLARSYM
                    {
                    root_0 = (Object)adaptor.nil();

                    v=(Token)input.LT(1);
                    match(input,DOLLARSYM,FOLLOW_DOLLARSYM_in_simple_expr613); if (failed) return retval;
                    if ( backtracking==0 ) {
                    v_tree = (Object)adaptor.create(v);
                    adaptor.addChild(root_0, v_tree);
                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:106:4: v= SYMBOL idx= array_idx
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr620); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    pushFollow(FOLLOW_array_idx_in_simple_expr624);
                    idx=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx.add(idx.getTree());

                    // AST REWRITE
                    // elements: idx, v
                    // token labels: v
                    // rule labels: idx, retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_v=new RewriteRuleTokenStream(adaptor,"token v",v);
                    RewriteRuleSubtreeStream stream_idx=new RewriteRuleSubtreeStream(adaptor,"token idx",idx!=null?idx.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 106:29: -> ^( ARRAY $v $idx)
                    {
                        // dd/grammar/ECAGrammar.g:106:32: ^( ARRAY $v $idx)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(ARRAY, "ARRAY"), root_1);

                        adaptor.addChild(root_1, stream_v.next());
                        adaptor.addChild(root_1, stream_idx.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:107:4: v= SYMBOL LPAREN RPAREN
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr645); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    LPAREN49=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr647); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN49);

                    RPAREN50=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr649); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN50);


                    // AST REWRITE
                    // elements: v
                    // token labels: v
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_v=new RewriteRuleTokenStream(adaptor,"token v",v);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 107:29: -> ^( METH $v)
                    {
                        // dd/grammar/ECAGrammar.g:107:32: ^( METH $v)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(METH, "METH"), root_1);

                        adaptor.addChild(root_1, stream_v.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 4 :
                    // dd/grammar/ECAGrammar.g:108:4: v= SYMBOL
                    {
                    root_0 = (Object)adaptor.nil();

                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr667); if (failed) return retval;
                    if ( backtracking==0 ) {
                    v_tree = (Object)adaptor.create(v);
                    adaptor.addChild(root_0, v_tree);
                    }

                    }
                    break;
                case 5 :
                    // dd/grammar/ECAGrammar.g:109:4: v= SYMBOL LPAREN args= expr_list RPAREN
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr674); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    LPAREN51=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr676); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN51);

                    pushFollow(FOLLOW_expr_list_in_simple_expr680);
                    args=expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr_list.add(args.getTree());
                    RPAREN52=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr682); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN52);


                    // AST REWRITE
                    // elements: v, args
                    // token labels: v
                    // rule labels: args, retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_v=new RewriteRuleTokenStream(adaptor,"token v",v);
                    RewriteRuleSubtreeStream stream_args=new RewriteRuleSubtreeStream(adaptor,"token args",args!=null?args.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 109:43: -> ^( METH $v $args)
                    {
                        // dd/grammar/ECAGrammar.g:109:46: ^( METH $v $args)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(METH, "METH"), root_1);

                        adaptor.addChild(root_1, stream_v.next());
                        adaptor.addChild(root_1, stream_args.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 6 :
                    // dd/grammar/ECAGrammar.g:110:4: NUMBER
                    {
                    root_0 = (Object)adaptor.nil();

                    NUMBER53=(Token)input.LT(1);
                    match(input,NUMBER,FOLLOW_NUMBER_in_simple_expr701); if (failed) return retval;
                    if ( backtracking==0 ) {
                    NUMBER53_tree = (Object)adaptor.create(NUMBER53);
                    adaptor.addChild(root_0, NUMBER53_tree);
                    }

                    }
                    break;
                case 7 :
                    // dd/grammar/ECAGrammar.g:111:4: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING54=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_simple_expr706); if (failed) return retval;
                    if ( backtracking==0 ) {
                    STRING54_tree = (Object)adaptor.create(STRING54);
                    adaptor.addChild(root_0, STRING54_tree);
                    }

                    }
                    break;
                case 8 :
                    // dd/grammar/ECAGrammar.g:112:4: LPAREN expr RPAREN
                    {
                    LPAREN55=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr711); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN55);

                    pushFollow(FOLLOW_expr_in_simple_expr713);
                    expr56=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr56.getTree());
                    RPAREN57=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr715); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN57);


                    // AST REWRITE
                    // elements: expr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 112:25: -> ^( expr )
                    {
                        // dd/grammar/ECAGrammar.g:112:28: ^( expr )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_expr.nextNode(), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end simple_expr

    public static class expr_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expr_list
    // dd/grammar/ECAGrammar.g:115:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );
    public final expr_list_return expr_list() throws RecognitionException {
        expr_list_return retval = new expr_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR59=null;
        expr_return expr58 = null;

        expr_list_return expr_list60 = null;

        expr_return expr61 = null;


        Object SEPR59_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule expr_list");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // dd/grammar/ECAGrammar.g:116:2: ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr )
            int alt8=2;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                int LA8_1 = input.LA(2);

                if ( (synpred17()) ) {
                    alt8=1;
                }
                else if ( (true) ) {
                    alt8=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("115:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 8, 1, input);

                    throw nvae;
                }
                }
                break;
            case SYMBOL:
                {
                int LA8_2 = input.LA(2);

                if ( (synpred17()) ) {
                    alt8=1;
                }
                else if ( (true) ) {
                    alt8=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("115:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 8, 2, input);

                    throw nvae;
                }
                }
                break;
            case NUMBER:
                {
                int LA8_3 = input.LA(2);

                if ( (synpred17()) ) {
                    alt8=1;
                }
                else if ( (true) ) {
                    alt8=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("115:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 8, 3, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
                {
                int LA8_4 = input.LA(2);

                if ( (synpred17()) ) {
                    alt8=1;
                }
                else if ( (true) ) {
                    alt8=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("115:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 8, 4, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                int LA8_5 = input.LA(2);

                if ( (synpred17()) ) {
                    alt8=1;
                }
                else if ( (true) ) {
                    alt8=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("115:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 8, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                int LA8_6 = input.LA(2);

                if ( (synpred17()) ) {
                    alt8=1;
                }
                else if ( (true) ) {
                    alt8=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("115:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 8, 6, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("115:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:116:4: expr SEPR expr_list
                    {
                    pushFollow(FOLLOW_expr_in_expr_list734);
                    expr58=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr58.getTree());
                    SEPR59=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_expr_list736); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR59);

                    pushFollow(FOLLOW_expr_list_in_expr_list738);
                    expr_list60=expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr_list.add(expr_list60.getTree());

                    // AST REWRITE
                    // elements: expr_list, SEPR, expr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 116:26: -> ^( SEPR expr expr_list )
                    {
                        // dd/grammar/ECAGrammar.g:116:29: ^( SEPR expr expr_list )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_SEPR.next(), root_1);

                        adaptor.addChild(root_1, stream_expr.next());
                        adaptor.addChild(root_1, stream_expr_list.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:117:4: expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_expr_list755);
                    expr61=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expr61.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expr_list

    public static class array_idx_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start array_idx_list
    // dd/grammar/ECAGrammar.g:120:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );
    public final array_idx_list_return array_idx_list() throws RecognitionException {
        array_idx_list_return retval = new array_idx_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        array_idx_return array_idx62 = null;

        array_idx_list_return array_idx_list63 = null;

        array_idx_return array_idx64 = null;


        RewriteRuleSubtreeStream stream_array_idx=new RewriteRuleSubtreeStream(adaptor,"rule array_idx");
        RewriteRuleSubtreeStream stream_array_idx_list=new RewriteRuleSubtreeStream(adaptor,"rule array_idx_list");
        try {
            // dd/grammar/ECAGrammar.g:121:2: ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==LSQUARE) ) {
                int LA9_1 = input.LA(2);

                if ( (synpred18()) ) {
                    alt9=1;
                }
                else if ( (true) ) {
                    alt9=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("120:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );", 9, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("120:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:121:4: array_idx array_idx_list
                    {
                    pushFollow(FOLLOW_array_idx_in_array_idx_list766);
                    array_idx62=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx.add(array_idx62.getTree());
                    pushFollow(FOLLOW_array_idx_list_in_array_idx_list768);
                    array_idx_list63=array_idx_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx_list.add(array_idx_list63.getTree());

                    // AST REWRITE
                    // elements: array_idx_list, array_idx
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 121:31: -> ^( SEPR array_idx array_idx_list )
                    {
                        // dd/grammar/ECAGrammar.g:121:34: ^( SEPR array_idx array_idx_list )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(adaptor.create(SEPR, "SEPR"), root_1);

                        adaptor.addChild(root_1, stream_array_idx.next());
                        adaptor.addChild(root_1, stream_array_idx_list.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:122:4: array_idx
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_array_idx_in_array_idx_list785);
                    array_idx64=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, array_idx64.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end array_idx_list

    public static class array_idx_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start array_idx
    // dd/grammar/ECAGrammar.g:125:1: array_idx : LSQUARE expr RSQUARE -> ^( expr ) ;
    public final array_idx_return array_idx() throws RecognitionException {
        array_idx_return retval = new array_idx_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LSQUARE65=null;
        Token RSQUARE67=null;
        expr_return expr66 = null;


        Object LSQUARE65_tree=null;
        Object RSQUARE67_tree=null;
        RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");
        RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // dd/grammar/ECAGrammar.g:126:2: ( LSQUARE expr RSQUARE -> ^( expr ) )
            // dd/grammar/ECAGrammar.g:126:4: LSQUARE expr RSQUARE
            {
            LSQUARE65=(Token)input.LT(1);
            match(input,LSQUARE,FOLLOW_LSQUARE_in_array_idx796); if (failed) return retval;
            if ( backtracking==0 ) stream_LSQUARE.add(LSQUARE65);

            pushFollow(FOLLOW_expr_in_array_idx798);
            expr66=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_expr.add(expr66.getTree());
            RSQUARE67=(Token)input.LT(1);
            match(input,RSQUARE,FOLLOW_RSQUARE_in_array_idx800); if (failed) return retval;
            if ( backtracking==0 ) stream_RSQUARE.add(RSQUARE67);


            // AST REWRITE
            // elements: expr
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 126:27: -> ^( expr )
            {
                // dd/grammar/ECAGrammar.g:126:30: ^( expr )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_expr.nextNode(), root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            }

            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end array_idx

    public static class infix_oper_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start infix_oper
    // dd/grammar/ECAGrammar.g:129:1: infix_oper : ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper );
    public final infix_oper_return infix_oper() throws RecognitionException {
        infix_oper_return retval = new infix_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        infix_bit_oper_return infix_bit_oper68 = null;

        infix_arith_oper_return infix_arith_oper69 = null;

        infix_bool_oper_return infix_bool_oper70 = null;

        infix_cmp_oper_return infix_cmp_oper71 = null;



        try {
            // dd/grammar/ECAGrammar.g:129:12: ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper )
            int alt10=4;
            switch ( input.LA(1) ) {
            case BOR:
            case BAND:
            case BXOR:
                {
                alt10=1;
                }
                break;
            case MUL:
            case DIV:
            case PLUS:
            case MINUS:
                {
                alt10=2;
                }
                break;
            case OR:
            case AND:
                {
                alt10=3;
                }
                break;
            case EQ:
            case NEQ:
            case GT:
            case LT:
            case GEQ:
            case LEQ:
                {
                alt10=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("129:1: infix_oper : ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper );", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:129:14: infix_bit_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_bit_oper_in_infix_oper818);
                    infix_bit_oper68=infix_bit_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_bit_oper68.getTree());

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:130:4: infix_arith_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_arith_oper_in_infix_oper823);
                    infix_arith_oper69=infix_arith_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_arith_oper69.getTree());

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:131:4: infix_bool_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_bool_oper_in_infix_oper828);
                    infix_bool_oper70=infix_bool_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_bool_oper70.getTree());

                    }
                    break;
                case 4 :
                    // dd/grammar/ECAGrammar.g:132:4: infix_cmp_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_cmp_oper_in_infix_oper833);
                    infix_cmp_oper71=infix_cmp_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_cmp_oper71.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end infix_oper

    public static class infix_bit_oper_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start infix_bit_oper
    // dd/grammar/ECAGrammar.g:135:1: infix_bit_oper : ( BAND | BOR | BXOR );
    public final infix_bit_oper_return infix_bit_oper() throws RecognitionException {
        infix_bit_oper_return retval = new infix_bit_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set72=null;

        Object set72_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:136:2: ( BAND | BOR | BXOR )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set72=(Token)input.LT(1);
            if ( (input.LA(1)>=BOR && input.LA(1)<=BXOR) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set72));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_infix_bit_oper0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end infix_bit_oper

    public static class infix_arith_oper_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start infix_arith_oper
    // dd/grammar/ECAGrammar.g:141:1: infix_arith_oper : ( MUL | DIV | PLUS | MINUS );
    public final infix_arith_oper_return infix_arith_oper() throws RecognitionException {
        infix_arith_oper_return retval = new infix_arith_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set73=null;

        Object set73_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:142:2: ( MUL | DIV | PLUS | MINUS )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set73=(Token)input.LT(1);
            if ( (input.LA(1)>=MUL && input.LA(1)<=MINUS) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set73));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_infix_arith_oper0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end infix_arith_oper

    public static class infix_bool_oper_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start infix_bool_oper
    // dd/grammar/ECAGrammar.g:148:1: infix_bool_oper : ( AND | OR );
    public final infix_bool_oper_return infix_bool_oper() throws RecognitionException {
        infix_bool_oper_return retval = new infix_bool_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set74=null;

        Object set74_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:149:2: ( AND | OR )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set74=(Token)input.LT(1);
            if ( (input.LA(1)>=OR && input.LA(1)<=AND) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set74));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_infix_bool_oper0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end infix_bool_oper

    public static class infix_cmp_oper_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start infix_cmp_oper
    // dd/grammar/ECAGrammar.g:153:1: infix_cmp_oper : ( EQ | NEQ | GT | LT | GEQ | LEQ );
    public final infix_cmp_oper_return infix_cmp_oper() throws RecognitionException {
        infix_cmp_oper_return retval = new infix_cmp_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set75=null;

        Object set75_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:154:2: ( EQ | NEQ | GT | LT | GEQ | LEQ )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set75=(Token)input.LT(1);
            if ( (input.LA(1)>=EQ && input.LA(1)<=LEQ) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set75));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_infix_cmp_oper0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end infix_cmp_oper

    public static class unary_oper_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start unary_oper
    // dd/grammar/ECAGrammar.g:162:1: unary_oper : ( NOT | TWIDDLE );
    public final unary_oper_return unary_oper() throws RecognitionException {
        unary_oper_return retval = new unary_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set76=null;

        Object set76_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:162:12: ( NOT | TWIDDLE )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set76=(Token)input.LT(1);
            if ( input.LA(1)==NOT||input.LA(1)==TWIDDLE ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set76));
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_unary_oper0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {
                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end unary_oper

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:62:12: ( binding SEPR bindings )
        // dd/grammar/ECAGrammar.g:62:12: binding SEPR bindings
        {
        pushFollow(FOLLOW_binding_in_synpred1332);
        binding();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred1334); if (failed) return ;
        pushFollow(FOLLOW_bindings_in_synpred1336);
        bindings();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:92:4: ( action_expr SEPR action_expr_list )
        // dd/grammar/ECAGrammar.g:92:4: action_expr SEPR action_expr_list
        {
        pushFollow(FOLLOW_action_expr_in_synpred6487);
        action_expr();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred6489); if (failed) return ;
        pushFollow(FOLLOW_action_expr_list_in_synpred6491);
        action_expr_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred7
    public final void synpred7_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:99:8: ( simple_expr infix_oper expr )
        // dd/grammar/ECAGrammar.g:99:8: simple_expr infix_oper expr
        {
        pushFollow(FOLLOW_simple_expr_in_synpred7526);
        simple_expr();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_infix_oper_in_synpred7528);
        infix_oper();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_expr_in_synpred7530);
        expr();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred7

    // $ANTLR start synpred8
    public final void synpred8_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:100:4: ( simple_expr )
        // dd/grammar/ECAGrammar.g:100:4: simple_expr
        {
        pushFollow(FOLLOW_simple_expr_in_synpred8548);
        simple_expr();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred17
    public final void synpred17_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:116:4: ( expr SEPR expr_list )
        // dd/grammar/ECAGrammar.g:116:4: expr SEPR expr_list
        {
        pushFollow(FOLLOW_expr_in_synpred17734);
        expr();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred17736); if (failed) return ;
        pushFollow(FOLLOW_expr_list_in_synpred17738);
        expr_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred17

    // $ANTLR start synpred18
    public final void synpred18_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:121:4: ( array_idx array_idx_list )
        // dd/grammar/ECAGrammar.g:121:4: array_idx array_idx_list
        {
        pushFollow(FOLLOW_array_idx_in_synpred18766);
        array_idx();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_array_idx_list_in_synpred18768);
        array_idx_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred18

    public final boolean synpred18() {
        backtracking++;
        int start = input.mark();
        try {
            synpred18_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred7() {
        backtracking++;
        int start = input.mark();
        try {
            synpred7_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred1() {
        backtracking++;
        int start = input.mark();
        try {
            synpred1_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred17() {
        backtracking++;
        int start = input.mark();
        try {
            synpred17_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred6() {
        backtracking++;
        int start = input.mark();
        try {
            synpred6_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred8() {
        backtracking++;
        int start = input.mark();
        try {
            synpred8_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_eca_script_rule_one_in_eca_script_rule88 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_script_rule90 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_eca_script_rule_one107 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_SYMBOL_in_eca_script_rule_one111 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLASS_in_eca_script_rule_one115 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_SYMBOL_in_eca_script_rule_one119 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_METHOD_in_eca_script_rule_one123 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_SYMBOL_in_eca_script_rule_one127 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_LINE_in_eca_script_rule_one131 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_NUMBER_in_eca_script_rule_one135 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BIND_in_eca_script_rule_one139 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_event_in_eca_script_rule_one143 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_IF_in_eca_script_rule_one147 = new BitSet(new long[]{0x1000200801C01000L,0x0000000000000005L});
    public static final BitSet FOLLOW_condition_in_eca_script_rule_one151 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_DO_in_eca_script_rule_one155 = new BitSet(new long[]{0x1000200801201000L,0x0000000000000005L});
    public static final BitSet FOLLOW_action_in_eca_script_rule_one159 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_ENDRULE_in_eca_script_rule_one163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eca_in_eca_rule200 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_rule202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_event_in_eca_event219 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_event221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_condition_in_eca_condition237 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_condition239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_eca_action255 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_action257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIND_in_eca273 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_event_in_eca277 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_IF_in_eca281 = new BitSet(new long[]{0x1000200801C01000L,0x0000000000000005L});
    public static final BitSet FOLLOW_condition_in_eca285 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_DO_in_eca289 = new BitSet(new long[]{0x1000200801201000L,0x0000000000000005L});
    public static final BitSet FOLLOW_action_in_eca293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bindings_in_event320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_bindings332 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_SEPR_in_bindings334 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_bindings_in_bindings336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_bindings351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_sym_in_binding361 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGN_in_binding363 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_in_binding365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym388 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_COLON_in_bind_sym390 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_condition426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_condition438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_condition450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOTHING_in_action464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_list_in_action476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_action_expr_list487 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_SEPR_in_action_expr_list489 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_action_expr_list_in_action_expr_list491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_action_expr_list506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_action_expr516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr526 = new BitSet(new long[]{0x0003DFF600000000L});
    public static final BitSet FOLLOW_infix_oper_in_expr528 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_in_expr530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_oper_in_expr553 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_in_expr555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr574 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_TERN_IF_in_expr576 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_in_expr580 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_COLON_in_expr582 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_in_expr586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLARSYM_in_simple_expr613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr620 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_array_idx_in_simple_expr624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr645 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr647 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr674 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr676 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_list_in_simple_expr680 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_simple_expr701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_simple_expr706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr711 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_in_simple_expr713 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_expr_list734 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_SEPR_in_expr_list736 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_list_in_expr_list738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_expr_list755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_array_idx_list766 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_array_idx_list_in_array_idx_list768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_array_idx_list785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LSQUARE_in_array_idx796 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_in_array_idx798 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RSQUARE_in_array_idx800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_bit_oper_in_infix_oper818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_arith_oper_in_infix_oper823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_bool_oper_in_infix_oper828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_cmp_oper_in_infix_oper833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_bit_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_arith_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_bool_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_cmp_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_unary_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_synpred1332 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_SEPR_in_synpred1334 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_bindings_in_synpred1336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_synpred6487 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_SEPR_in_synpred6489 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_action_expr_list_in_synpred6491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_synpred7526 = new BitSet(new long[]{0x0003DFF600000000L});
    public static final BitSet FOLLOW_infix_oper_in_synpred7528 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_in_synpred7530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_synpred8548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_synpred17734 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_SEPR_in_synpred17736 = new BitSet(new long[]{0x1000200801001000L,0x0000000000000005L});
    public static final BitSet FOLLOW_expr_list_in_synpred17738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_synpred18766 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_array_idx_list_in_synpred18768 = new BitSet(new long[]{0x0000000000000002L});

}