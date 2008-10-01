// $ANTLR 3.0.1 dd/grammar/ECAGrammar.g 2008-10-01 15:50:08

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DIGIT", "POSDIGIT", "SIGN", "BAREINT", "INTEGER", "POINT", "EXPPART", "FLOAT", "NUMBER", "BIND", "IF", "DO", "RULE", "CLASS", "METHOD", "LINE", "ENDRULE", "NOTHING", "TRUE", "FALSE", "RETURN", "THROW", "LPAREN", "RPAREN", "LSQUARE", "RSQUARE", "LBRACE", "RBRACE", "SEPR", "DOT", "ASSIGN", "OR", "AND", "NOT", "EQ", "NEQ", "GT", "LT", "GEQ", "LEQ", "BOR", "BAND", "BXOR", "TWIDDLE", "MUL", "DIV", "PLUS", "MINUS", "MOD", "TERN_IF", "COLON", "LETTER", "UNDERSCORE", "QUOTE", "DQUOTE", "SPACE", "NEWLINE", "PUNCT", "STRING", "BARESYM", "QUOTSYM", "DOTSYM", "SYMBOL", "DOLLAR", "DOLLARSYM", "WS", "Tokens", "UNOP", "BINOP", "TERNOP", "METH", "ARRAY", "NUM_LIT", "STRING_LIT"
    };
    public static final int MINUS=51;
    public static final int ARRAY=75;
    public static final int NUMBER=12;
    public static final int FALSE=23;
    public static final int METHOD=18;
    public static final int FLOAT=11;
    public static final int POSDIGIT=5;
    public static final int LEQ=43;
    public static final int TWIDDLE=47;
    public static final int RULE=16;
    public static final int MOD=52;
    public static final int GEQ=42;
    public static final int DQUOTE=58;
    public static final int OR=35;
    public static final int BOR=44;
    public static final int STRING_LIT=77;
    public static final int BAREINT=7;
    public static final int LBRACE=30;
    public static final int NEWLINE=60;
    public static final int DOT=33;
    public static final int RBRACE=31;
    public static final int INTEGER=8;
    public static final int AND=36;
    public static final int NUM_LIT=76;
    public static final int ASSIGN=34;
    public static final int SYMBOL=66;
    public static final int RPAREN=27;
    public static final int SIGN=6;
    public static final int LPAREN=26;
    public static final int METH=74;
    public static final int PLUS=50;
    public static final int DIGIT=4;
    public static final int LINE=19;
    public static final int BINOP=72;
    public static final int BAND=45;
    public static final int NEQ=39;
    public static final int TERNOP=73;
    public static final int SPACE=59;
    public static final int LETTER=55;
    public static final int LSQUARE=28;
    public static final int DO=15;
    public static final int POINT=9;
    public static final int BARESYM=63;
    public static final int NOTHING=21;
    public static final int SEPR=32;
    public static final int WS=69;
    public static final int EQ=38;
    public static final int STRING=62;
    public static final int QUOTSYM=64;
    public static final int LT=41;
    public static final int GT=40;
    public static final int DOLLAR=67;
    public static final int RSQUARE=29;
    public static final int TERN_IF=53;
    public static final int QUOTE=57;
    public static final int UNOP=71;
    public static final int CLASS=17;
    public static final int MUL=48;
    public static final int EXPPART=10;
    public static final int PUNCT=61;
    public static final int RETURN=24;
    public static final int IF=14;
    public static final int EOF=-1;
    public static final int Tokens=70;
    public static final int COLON=54;
    public static final int DIV=49;
    public static final int DOTSYM=65;
    public static final int BXOR=46;
    public static final int ENDRULE=20;
    public static final int BIND=13;
    public static final int NOT=37;
    public static final int TRUE=22;
    public static final int THROW=25;
    public static final int UNDERSCORE=56;
    public static final int DOLLARSYM=68;

        public ECAGrammarParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[63+1];
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
    // dd/grammar/ECAGrammar.g:49:1: eca_script_rule : rule= eca_script_rule_one EOF -> ^( $rule) ;
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
            // dd/grammar/ECAGrammar.g:49:17: (rule= eca_script_rule_one EOF -> ^( $rule) )
            // dd/grammar/ECAGrammar.g:49:19: rule= eca_script_rule_one EOF
            {
            pushFollow(FOLLOW_eca_script_rule_one_in_eca_script_rule90);
            rule=eca_script_rule_one();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_eca_script_rule_one.add(rule.getTree());
            EOF1=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_script_rule92); if (failed) return retval;
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
            // 49:48: -> ^( $rule)
            {
                // dd/grammar/ECAGrammar.g:49:51: ^( $rule)
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
    // dd/grammar/ECAGrammar.g:51:1: eca_script_rule_one : RULE n= SYMBOL CLASS cl= SYMBOL METHOD m= SYMBOL LINE l= NUMBER BIND e= event IF c= condition DO a= action ENDRULE -> ^( RULE $n $cl $m $l $e $c $a) ;
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
            // dd/grammar/ECAGrammar.g:52:2: ( RULE n= SYMBOL CLASS cl= SYMBOL METHOD m= SYMBOL LINE l= NUMBER BIND e= event IF c= condition DO a= action ENDRULE -> ^( RULE $n $cl $m $l $e $c $a) )
            // dd/grammar/ECAGrammar.g:52:4: RULE n= SYMBOL CLASS cl= SYMBOL METHOD m= SYMBOL LINE l= NUMBER BIND e= event IF c= condition DO a= action ENDRULE
            {
            RULE2=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_eca_script_rule_one109); if (failed) return retval;
            if ( backtracking==0 ) stream_RULE.add(RULE2);

            n=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_eca_script_rule_one113); if (failed) return retval;
            if ( backtracking==0 ) stream_SYMBOL.add(n);

            CLASS3=(Token)input.LT(1);
            match(input,CLASS,FOLLOW_CLASS_in_eca_script_rule_one117); if (failed) return retval;
            if ( backtracking==0 ) stream_CLASS.add(CLASS3);

            cl=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_eca_script_rule_one121); if (failed) return retval;
            if ( backtracking==0 ) stream_SYMBOL.add(cl);

            METHOD4=(Token)input.LT(1);
            match(input,METHOD,FOLLOW_METHOD_in_eca_script_rule_one125); if (failed) return retval;
            if ( backtracking==0 ) stream_METHOD.add(METHOD4);

            m=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_eca_script_rule_one129); if (failed) return retval;
            if ( backtracking==0 ) stream_SYMBOL.add(m);

            LINE5=(Token)input.LT(1);
            match(input,LINE,FOLLOW_LINE_in_eca_script_rule_one133); if (failed) return retval;
            if ( backtracking==0 ) stream_LINE.add(LINE5);

            l=(Token)input.LT(1);
            match(input,NUMBER,FOLLOW_NUMBER_in_eca_script_rule_one137); if (failed) return retval;
            if ( backtracking==0 ) stream_NUMBER.add(l);

            BIND6=(Token)input.LT(1);
            match(input,BIND,FOLLOW_BIND_in_eca_script_rule_one141); if (failed) return retval;
            if ( backtracking==0 ) stream_BIND.add(BIND6);

            pushFollow(FOLLOW_event_in_eca_script_rule_one145);
            e=event();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_event.add(e.getTree());
            IF7=(Token)input.LT(1);
            match(input,IF,FOLLOW_IF_in_eca_script_rule_one149); if (failed) return retval;
            if ( backtracking==0 ) stream_IF.add(IF7);

            pushFollow(FOLLOW_condition_in_eca_script_rule_one153);
            c=condition();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_condition.add(c.getTree());
            DO8=(Token)input.LT(1);
            match(input,DO,FOLLOW_DO_in_eca_script_rule_one157); if (failed) return retval;
            if ( backtracking==0 ) stream_DO.add(DO8);

            pushFollow(FOLLOW_action_in_eca_script_rule_one161);
            a=action();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action.add(a.getTree());
            ENDRULE9=(Token)input.LT(1);
            match(input,ENDRULE,FOLLOW_ENDRULE_in_eca_script_rule_one165); if (failed) return retval;
            if ( backtracking==0 ) stream_ENDRULE.add(ENDRULE9);


            // AST REWRITE
            // elements: n, a, l, RULE, c, e, m, cl
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
            // 59:12: -> ^( RULE $n $cl $m $l $e $c $a)
            {
                // dd/grammar/ECAGrammar.g:59:15: ^( RULE $n $cl $m $l $e $c $a)
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
    // dd/grammar/ECAGrammar.g:61:1: eca_rule : eca EOF -> ^( eca ) ;
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
            // dd/grammar/ECAGrammar.g:61:10: ( eca EOF -> ^( eca ) )
            // dd/grammar/ECAGrammar.g:61:12: eca EOF
            {
            pushFollow(FOLLOW_eca_in_eca_rule202);
            eca10=eca();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_eca.add(eca10.getTree());
            EOF11=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_rule204); if (failed) return retval;
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
            // 61:20: -> ^( eca )
            {
                // dd/grammar/ECAGrammar.g:61:23: ^( eca )
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
    // dd/grammar/ECAGrammar.g:64:1: eca_event : event EOF -> ^( event ) ;
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
            // dd/grammar/ECAGrammar.g:64:11: ( event EOF -> ^( event ) )
            // dd/grammar/ECAGrammar.g:64:13: event EOF
            {
            pushFollow(FOLLOW_event_in_eca_event221);
            event12=event();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_event.add(event12.getTree());
            EOF13=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_event223); if (failed) return retval;
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
            // 64:23: -> ^( event )
            {
                // dd/grammar/ECAGrammar.g:64:26: ^( event )
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
    // dd/grammar/ECAGrammar.g:67:1: eca_condition : condition EOF -> ^( condition ) ;
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
            // dd/grammar/ECAGrammar.g:67:15: ( condition EOF -> ^( condition ) )
            // dd/grammar/ECAGrammar.g:67:17: condition EOF
            {
            pushFollow(FOLLOW_condition_in_eca_condition239);
            condition14=condition();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_condition.add(condition14.getTree());
            EOF15=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_condition241); if (failed) return retval;
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
            // 67:31: -> ^( condition )
            {
                // dd/grammar/ECAGrammar.g:67:34: ^( condition )
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
    // dd/grammar/ECAGrammar.g:70:1: eca_action : action EOF -> ^( action ) ;
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
            // dd/grammar/ECAGrammar.g:70:12: ( action EOF -> ^( action ) )
            // dd/grammar/ECAGrammar.g:70:14: action EOF
            {
            pushFollow(FOLLOW_action_in_eca_action257);
            action16=action();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action.add(action16.getTree());
            EOF17=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_action259); if (failed) return retval;
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
            // 70:25: -> ^( action )
            {
                // dd/grammar/ECAGrammar.g:70:28: ^( action )
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
    // dd/grammar/ECAGrammar.g:73:1: eca : BIND e= event IF c= condition DO a= action -> ^( BIND $e $c $a) ;
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
            // dd/grammar/ECAGrammar.g:73:5: ( BIND e= event IF c= condition DO a= action -> ^( BIND $e $c $a) )
            // dd/grammar/ECAGrammar.g:73:7: BIND e= event IF c= condition DO a= action
            {
            BIND18=(Token)input.LT(1);
            match(input,BIND,FOLLOW_BIND_in_eca275); if (failed) return retval;
            if ( backtracking==0 ) stream_BIND.add(BIND18);

            pushFollow(FOLLOW_event_in_eca279);
            e=event();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_event.add(e.getTree());
            IF19=(Token)input.LT(1);
            match(input,IF,FOLLOW_IF_in_eca283); if (failed) return retval;
            if ( backtracking==0 ) stream_IF.add(IF19);

            pushFollow(FOLLOW_condition_in_eca287);
            c=condition();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_condition.add(c.getTree());
            DO20=(Token)input.LT(1);
            match(input,DO,FOLLOW_DO_in_eca291); if (failed) return retval;
            if ( backtracking==0 ) stream_DO.add(DO20);

            pushFollow(FOLLOW_action_in_eca295);
            a=action();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action.add(a.getTree());

            // AST REWRITE
            // elements: e, a, c, BIND
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
            // 75:15: -> ^( BIND $e $c $a)
            {
                // dd/grammar/ECAGrammar.g:75:18: ^( BIND $e $c $a)
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
    // dd/grammar/ECAGrammar.g:80:1: event : bindings ;
    public final event_return event() throws RecognitionException {
        event_return retval = new event_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        bindings_return bindings21 = null;



        try {
            // dd/grammar/ECAGrammar.g:80:7: ( bindings )
            // dd/grammar/ECAGrammar.g:80:9: bindings
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_bindings_in_event322);
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
    // dd/grammar/ECAGrammar.g:85:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );
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
            // dd/grammar/ECAGrammar.g:85:10: ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding )
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
                        new NoViableAltException("85:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );", 1, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("85:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:85:12: binding SEPR bindings
                    {
                    pushFollow(FOLLOW_binding_in_bindings334);
                    binding22=binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_binding.add(binding22.getTree());
                    SEPR23=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_bindings336); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR23);

                    pushFollow(FOLLOW_bindings_in_bindings338);
                    bindings24=bindings();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_bindings.add(bindings24.getTree());

                    // AST REWRITE
                    // elements: bindings, SEPR, binding
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 85:34: -> ^( SEPR binding bindings )
                    {
                        // dd/grammar/ECAGrammar.g:85:37: ^( SEPR binding bindings )
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
                    // dd/grammar/ECAGrammar.g:86:4: binding
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_binding_in_bindings353);
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
    // dd/grammar/ECAGrammar.g:89:1: binding : bind_sym ASSIGN expr -> ^( ASSIGN bind_sym expr ) ;
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
            // dd/grammar/ECAGrammar.g:89:9: ( bind_sym ASSIGN expr -> ^( ASSIGN bind_sym expr ) )
            // dd/grammar/ECAGrammar.g:89:11: bind_sym ASSIGN expr
            {
            pushFollow(FOLLOW_bind_sym_in_binding363);
            bind_sym26=bind_sym();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_bind_sym.add(bind_sym26.getTree());
            ASSIGN27=(Token)input.LT(1);
            match(input,ASSIGN,FOLLOW_ASSIGN_in_binding365); if (failed) return retval;
            if ( backtracking==0 ) stream_ASSIGN.add(ASSIGN27);

            pushFollow(FOLLOW_expr_in_binding367);
            expr28=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_expr.add(expr28.getTree());

            // AST REWRITE
            // elements: bind_sym, ASSIGN, expr
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            if ( backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 89:32: -> ^( ASSIGN bind_sym expr )
            {
                // dd/grammar/ECAGrammar.g:89:35: ^( ASSIGN bind_sym expr )
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
    // dd/grammar/ECAGrammar.g:93:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );
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
            // dd/grammar/ECAGrammar.g:93:10: (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL )
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
                        new NoViableAltException("93:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );", 2, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("93:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:93:12: v= SYMBOL COLON t= SYMBOL
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym390); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    COLON29=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_bind_sym392); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON29);

                    t=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym396); if (failed) return retval;
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
                    // 93:36: -> ^( COLON $v $t)
                    {
                        // dd/grammar/ECAGrammar.g:93:39: ^( COLON $v $t)
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
                    // dd/grammar/ECAGrammar.g:94:4: SYMBOL
                    {
                    root_0 = (Object)adaptor.nil();

                    SYMBOL30=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym414); if (failed) return retval;
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
    // dd/grammar/ECAGrammar.g:101:1: condition : ( TRUE -> ^( TRUE ) | FALSE -> ^( FALSE ) | expr );
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
            // dd/grammar/ECAGrammar.g:101:11: ( TRUE -> ^( TRUE ) | FALSE -> ^( FALSE ) | expr )
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
                    new NoViableAltException("101:1: condition : ( TRUE -> ^( TRUE ) | FALSE -> ^( FALSE ) | expr );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:101:13: TRUE
                    {
                    TRUE31=(Token)input.LT(1);
                    match(input,TRUE,FOLLOW_TRUE_in_condition428); if (failed) return retval;
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
                    // 101:19: -> ^( TRUE )
                    {
                        // dd/grammar/ECAGrammar.g:101:22: ^( TRUE )
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
                    // dd/grammar/ECAGrammar.g:102:4: FALSE
                    {
                    FALSE32=(Token)input.LT(1);
                    match(input,FALSE,FOLLOW_FALSE_in_condition440); if (failed) return retval;
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
                    // 102:11: -> ^( FALSE )
                    {
                        // dd/grammar/ECAGrammar.g:102:14: ^( FALSE )
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
                    // dd/grammar/ECAGrammar.g:103:4: expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_condition452);
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
    // dd/grammar/ECAGrammar.g:110:1: action : ( NOTHING -> ^( NOTHING ) | action_expr_list );
    public final action_return action() throws RecognitionException {
        action_return retval = new action_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token NOTHING34=null;
        action_expr_list_return action_expr_list35 = null;


        Object NOTHING34_tree=null;
        RewriteRuleTokenStream stream_NOTHING=new RewriteRuleTokenStream(adaptor,"token NOTHING");

        try {
            // dd/grammar/ECAGrammar.g:110:8: ( NOTHING -> ^( NOTHING ) | action_expr_list )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==NOTHING) ) {
                alt4=1;
            }
            else if ( (LA4_0==NUMBER||(LA4_0>=RETURN && LA4_0<=LPAREN)||LA4_0==NOT||LA4_0==TWIDDLE||LA4_0==STRING||LA4_0==SYMBOL||LA4_0==DOLLARSYM) ) {
                alt4=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("110:1: action : ( NOTHING -> ^( NOTHING ) | action_expr_list );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:110:10: NOTHING
                    {
                    NOTHING34=(Token)input.LT(1);
                    match(input,NOTHING,FOLLOW_NOTHING_in_action466); if (failed) return retval;
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
                    // 110:19: -> ^( NOTHING )
                    {
                        // dd/grammar/ECAGrammar.g:110:22: ^( NOTHING )
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
                    // dd/grammar/ECAGrammar.g:111:4: action_expr_list
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_action_expr_list_in_action478);
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
    // dd/grammar/ECAGrammar.g:114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );
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
            // dd/grammar/ECAGrammar.g:115:2: ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr )
            int alt5=2;
            switch ( input.LA(1) ) {
            case RETURN:
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
                        new NoViableAltException("114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 1, input);

                    throw nvae;
                }
                }
                break;
            case THROW:
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
                        new NoViableAltException("114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 2, input);

                    throw nvae;
                }
                }
                break;
            case DOLLARSYM:
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
                        new NoViableAltException("114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 3, input);

                    throw nvae;
                }
                }
                break;
            case SYMBOL:
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
                        new NoViableAltException("114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 4, input);

                    throw nvae;
                }
                }
                break;
            case NUMBER:
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
                        new NoViableAltException("114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 5, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
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
                        new NoViableAltException("114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 6, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                int LA5_7 = input.LA(2);

                if ( (synpred6()) ) {
                    alt5=1;
                }
                else if ( (true) ) {
                    alt5=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 7, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                int LA5_8 = input.LA(2);

                if ( (synpred6()) ) {
                    alt5=1;
                }
                else if ( (true) ) {
                    alt5=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 8, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("114:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:115:4: action_expr SEPR action_expr_list
                    {
                    pushFollow(FOLLOW_action_expr_in_action_expr_list489);
                    action_expr36=action_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_action_expr.add(action_expr36.getTree());
                    SEPR37=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_action_expr_list491); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR37);

                    pushFollow(FOLLOW_action_expr_list_in_action_expr_list493);
                    action_expr_list38=action_expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_action_expr_list.add(action_expr_list38.getTree());

                    // AST REWRITE
                    // elements: SEPR, action_expr, action_expr_list
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 115:38: -> ^( SEPR action_expr action_expr_list )
                    {
                        // dd/grammar/ECAGrammar.g:115:41: ^( SEPR action_expr action_expr_list )
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
                    // dd/grammar/ECAGrammar.g:116:4: action_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_action_expr_in_action_expr_list508);
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
    // dd/grammar/ECAGrammar.g:119:1: action_expr : ( RETURN -> ^( RETURN ) | RETURN expr -> ^( RETURN expr ) | THROW e= SYMBOL LPAREN RPAREN -> ^( THROW $e) | THROW e= SYMBOL LPAREN args= expr_list RPAREN -> ^( THROW $e $args) | expr );
    public final action_expr_return action_expr() throws RecognitionException {
        action_expr_return retval = new action_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token e=null;
        Token RETURN40=null;
        Token RETURN41=null;
        Token THROW43=null;
        Token LPAREN44=null;
        Token RPAREN45=null;
        Token THROW46=null;
        Token LPAREN47=null;
        Token RPAREN48=null;
        expr_list_return args = null;

        expr_return expr42 = null;

        expr_return expr49 = null;


        Object e_tree=null;
        Object RETURN40_tree=null;
        Object RETURN41_tree=null;
        Object THROW43_tree=null;
        Object LPAREN44_tree=null;
        Object RPAREN45_tree=null;
        Object THROW46_tree=null;
        Object LPAREN47_tree=null;
        Object RPAREN48_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RETURN=new RewriteRuleTokenStream(adaptor,"token RETURN");
        RewriteRuleTokenStream stream_THROW=new RewriteRuleTokenStream(adaptor,"token THROW");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");
        RewriteRuleSubtreeStream stream_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule expr_list");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // dd/grammar/ECAGrammar.g:119:13: ( RETURN -> ^( RETURN ) | RETURN expr -> ^( RETURN expr ) | THROW e= SYMBOL LPAREN RPAREN -> ^( THROW $e) | THROW e= SYMBOL LPAREN args= expr_list RPAREN -> ^( THROW $e $args) | expr )
            int alt6=5;
            switch ( input.LA(1) ) {
            case RETURN:
                {
                int LA6_1 = input.LA(2);

                if ( (LA6_1==NUMBER||LA6_1==LPAREN||LA6_1==NOT||LA6_1==TWIDDLE||LA6_1==STRING||LA6_1==SYMBOL||LA6_1==DOLLARSYM) ) {
                    alt6=2;
                }
                else if ( (LA6_1==EOF||LA6_1==ENDRULE||LA6_1==SEPR) ) {
                    alt6=1;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("119:1: action_expr : ( RETURN -> ^( RETURN ) | RETURN expr -> ^( RETURN expr ) | THROW e= SYMBOL LPAREN RPAREN -> ^( THROW $e) | THROW e= SYMBOL LPAREN args= expr_list RPAREN -> ^( THROW $e $args) | expr );", 6, 1, input);

                    throw nvae;
                }
                }
                break;
            case THROW:
                {
                int LA6_2 = input.LA(2);

                if ( (LA6_2==SYMBOL) ) {
                    int LA6_6 = input.LA(3);

                    if ( (LA6_6==LPAREN) ) {
                        int LA6_7 = input.LA(4);

                        if ( (LA6_7==RPAREN) ) {
                            alt6=3;
                        }
                        else if ( (LA6_7==NUMBER||LA6_7==LPAREN||LA6_7==NOT||LA6_7==TWIDDLE||LA6_7==STRING||LA6_7==SYMBOL||LA6_7==DOLLARSYM) ) {
                            alt6=4;
                        }
                        else {
                            if (backtracking>0) {failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("119:1: action_expr : ( RETURN -> ^( RETURN ) | RETURN expr -> ^( RETURN expr ) | THROW e= SYMBOL LPAREN RPAREN -> ^( THROW $e) | THROW e= SYMBOL LPAREN args= expr_list RPAREN -> ^( THROW $e $args) | expr );", 6, 7, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("119:1: action_expr : ( RETURN -> ^( RETURN ) | RETURN expr -> ^( RETURN expr ) | THROW e= SYMBOL LPAREN RPAREN -> ^( THROW $e) | THROW e= SYMBOL LPAREN args= expr_list RPAREN -> ^( THROW $e $args) | expr );", 6, 6, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("119:1: action_expr : ( RETURN -> ^( RETURN ) | RETURN expr -> ^( RETURN expr ) | THROW e= SYMBOL LPAREN RPAREN -> ^( THROW $e) | THROW e= SYMBOL LPAREN args= expr_list RPAREN -> ^( THROW $e $args) | expr );", 6, 2, input);

                    throw nvae;
                }
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
                alt6=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("119:1: action_expr : ( RETURN -> ^( RETURN ) | RETURN expr -> ^( RETURN expr ) | THROW e= SYMBOL LPAREN RPAREN -> ^( THROW $e) | THROW e= SYMBOL LPAREN args= expr_list RPAREN -> ^( THROW $e $args) | expr );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:119:15: RETURN
                    {
                    RETURN40=(Token)input.LT(1);
                    match(input,RETURN,FOLLOW_RETURN_in_action_expr518); if (failed) return retval;
                    if ( backtracking==0 ) stream_RETURN.add(RETURN40);


                    // AST REWRITE
                    // elements: RETURN
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 119:25: -> ^( RETURN )
                    {
                        // dd/grammar/ECAGrammar.g:119:28: ^( RETURN )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_RETURN.next(), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:120:4: RETURN expr
                    {
                    RETURN41=(Token)input.LT(1);
                    match(input,RETURN,FOLLOW_RETURN_in_action_expr532); if (failed) return retval;
                    if ( backtracking==0 ) stream_RETURN.add(RETURN41);

                    pushFollow(FOLLOW_expr_in_action_expr534);
                    expr42=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr42.getTree());

                    // AST REWRITE
                    // elements: expr, RETURN
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 120:19: -> ^( RETURN expr )
                    {
                        // dd/grammar/ECAGrammar.g:120:22: ^( RETURN expr )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_RETURN.next(), root_1);

                        adaptor.addChild(root_1, stream_expr.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:121:4: THROW e= SYMBOL LPAREN RPAREN
                    {
                    THROW43=(Token)input.LT(1);
                    match(input,THROW,FOLLOW_THROW_in_action_expr550); if (failed) return retval;
                    if ( backtracking==0 ) stream_THROW.add(THROW43);

                    e=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_action_expr554); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(e);

                    LPAREN44=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_action_expr556); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN44);

                    RPAREN45=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_action_expr558); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN45);


                    // AST REWRITE
                    // elements: THROW, e
                    // token labels: e
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_e=new RewriteRuleTokenStream(adaptor,"token e",e);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 121:34: -> ^( THROW $e)
                    {
                        // dd/grammar/ECAGrammar.g:121:37: ^( THROW $e)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_THROW.next(), root_1);

                        adaptor.addChild(root_1, stream_e.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 4 :
                    // dd/grammar/ECAGrammar.g:122:4: THROW e= SYMBOL LPAREN args= expr_list RPAREN
                    {
                    THROW46=(Token)input.LT(1);
                    match(input,THROW,FOLLOW_THROW_in_action_expr573); if (failed) return retval;
                    if ( backtracking==0 ) stream_THROW.add(THROW46);

                    e=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_action_expr577); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(e);

                    LPAREN47=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_action_expr579); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN47);

                    pushFollow(FOLLOW_expr_list_in_action_expr583);
                    args=expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr_list.add(args.getTree());
                    RPAREN48=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_action_expr585); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN48);


                    // AST REWRITE
                    // elements: args, e, THROW
                    // token labels: e
                    // rule labels: args, retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_e=new RewriteRuleTokenStream(adaptor,"token e",e);
                    RewriteRuleSubtreeStream stream_args=new RewriteRuleSubtreeStream(adaptor,"token args",args!=null?args.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 122:48: -> ^( THROW $e $args)
                    {
                        // dd/grammar/ECAGrammar.g:122:51: ^( THROW $e $args)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_THROW.next(), root_1);

                        adaptor.addChild(root_1, stream_e.next());
                        adaptor.addChild(root_1, stream_args.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 5 :
                    // dd/grammar/ECAGrammar.g:123:4: expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_action_expr602);
                    expr49=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expr49.getTree());

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
    // $ANTLR end action_expr

    public static class expr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expr
    // dd/grammar/ECAGrammar.g:126:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );
    public final expr_return expr() throws RecognitionException {
        expr_return retval = new expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TERN_IF56=null;
        Token COLON57=null;
        simple_expr_return cond = null;

        expr_return iftrue = null;

        expr_return iffalse = null;

        simple_expr_return simple_expr50 = null;

        infix_oper_return infix_oper51 = null;

        expr_return expr52 = null;

        simple_expr_return simple_expr53 = null;

        unary_oper_return unary_oper54 = null;

        expr_return expr55 = null;


        Object TERN_IF56_tree=null;
        Object COLON57_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_TERN_IF=new RewriteRuleTokenStream(adaptor,"token TERN_IF");
        RewriteRuleSubtreeStream stream_unary_oper=new RewriteRuleSubtreeStream(adaptor,"rule unary_oper");
        RewriteRuleSubtreeStream stream_infix_oper=new RewriteRuleSubtreeStream(adaptor,"rule infix_oper");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_simple_expr=new RewriteRuleSubtreeStream(adaptor,"rule simple_expr");
        try {
            // dd/grammar/ECAGrammar.g:126:6: ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) )
            int alt7=4;
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
                    alt7=1;
                    }
                    break;
                case TERN_IF:
                    {
                    alt7=4;
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
                    alt7=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("126:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 7, 1, input);

                    throw nvae;
                }

                }
                break;
            case SYMBOL:
                {
                switch ( input.LA(2) ) {
                case LPAREN:
                    {
                    int LA7_10 = input.LA(3);

                    if ( (synpred11()) ) {
                        alt7=1;
                    }
                    else if ( (synpred12()) ) {
                        alt7=2;
                    }
                    else if ( (true) ) {
                        alt7=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("126:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 7, 10, input);

                        throw nvae;
                    }
                    }
                    break;
                case TERN_IF:
                    {
                    alt7=4;
                    }
                    break;
                case LSQUARE:
                    {
                    int LA7_11 = input.LA(3);

                    if ( (synpred11()) ) {
                        alt7=1;
                    }
                    else if ( (synpred12()) ) {
                        alt7=2;
                    }
                    else if ( (true) ) {
                        alt7=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("126:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 7, 11, input);

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
                    alt7=2;
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
                    alt7=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("126:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 7, 2, input);

                    throw nvae;
                }

                }
                break;
            case NUMBER:
                {
                switch ( input.LA(2) ) {
                case TERN_IF:
                    {
                    alt7=4;
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
                    alt7=2;
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
                    alt7=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("126:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 7, 3, input);

                    throw nvae;
                }

                }
                break;
            case STRING:
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
                    alt7=1;
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
                    alt7=2;
                    }
                    break;
                case TERN_IF:
                    {
                    alt7=4;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("126:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 7, 4, input);

                    throw nvae;
                }

                }
                break;
            case LPAREN:
                {
                int LA7_5 = input.LA(2);

                if ( (synpred11()) ) {
                    alt7=1;
                }
                else if ( (synpred12()) ) {
                    alt7=2;
                }
                else if ( (true) ) {
                    alt7=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("126:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 7, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                alt7=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("126:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:126:8: simple_expr infix_oper expr
                    {
                    pushFollow(FOLLOW_simple_expr_in_expr612);
                    simple_expr50=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_simple_expr.add(simple_expr50.getTree());
                    pushFollow(FOLLOW_infix_oper_in_expr614);
                    infix_oper51=infix_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_infix_oper.add(infix_oper51.getTree());
                    pushFollow(FOLLOW_expr_in_expr616);
                    expr52=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr52.getTree());

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
                    // 126:37: -> ^( BINOP infix_oper simple_expr expr )
                    {
                        // dd/grammar/ECAGrammar.g:126:40: ^( BINOP infix_oper simple_expr expr )
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
                    // dd/grammar/ECAGrammar.g:127:4: simple_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_expr_in_expr634);
                    simple_expr53=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, simple_expr53.getTree());

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:128:4: unary_oper expr
                    {
                    pushFollow(FOLLOW_unary_oper_in_expr639);
                    unary_oper54=unary_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_unary_oper.add(unary_oper54.getTree());
                    pushFollow(FOLLOW_expr_in_expr641);
                    expr55=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr55.getTree());

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
                    // 128:22: -> ^( UNOP unary_oper expr )
                    {
                        // dd/grammar/ECAGrammar.g:128:25: ^( UNOP unary_oper expr )
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
                    // dd/grammar/ECAGrammar.g:129:4: cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr
                    {
                    pushFollow(FOLLOW_simple_expr_in_expr660);
                    cond=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_simple_expr.add(cond.getTree());
                    TERN_IF56=(Token)input.LT(1);
                    match(input,TERN_IF,FOLLOW_TERN_IF_in_expr662); if (failed) return retval;
                    if ( backtracking==0 ) stream_TERN_IF.add(TERN_IF56);

                    pushFollow(FOLLOW_expr_in_expr666);
                    iftrue=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(iftrue.getTree());
                    COLON57=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_expr668); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON57);

                    pushFollow(FOLLOW_expr_in_expr672);
                    iffalse=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(iffalse.getTree());

                    // AST REWRITE
                    // elements: cond, iftrue, iffalse
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
                    // 129:60: -> ^( TERNOP $cond $iftrue $iffalse)
                    {
                        // dd/grammar/ECAGrammar.g:129:63: ^( TERNOP $cond $iftrue $iffalse)
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
    // dd/grammar/ECAGrammar.g:132:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );
    public final simple_expr_return simple_expr() throws RecognitionException {
        simple_expr_return retval = new simple_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token v=null;
        Token LPAREN58=null;
        Token RPAREN59=null;
        Token LPAREN60=null;
        Token RPAREN61=null;
        Token NUMBER62=null;
        Token STRING63=null;
        Token LPAREN64=null;
        Token RPAREN66=null;
        array_idx_return idx = null;

        expr_list_return args = null;

        expr_return expr65 = null;


        Object v_tree=null;
        Object LPAREN58_tree=null;
        Object RPAREN59_tree=null;
        Object LPAREN60_tree=null;
        Object RPAREN61_tree=null;
        Object NUMBER62_tree=null;
        Object STRING63_tree=null;
        Object LPAREN64_tree=null;
        Object RPAREN66_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");
        RewriteRuleSubtreeStream stream_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule expr_list");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_array_idx=new RewriteRuleSubtreeStream(adaptor,"rule array_idx");
        try {
            // dd/grammar/ECAGrammar.g:132:13: (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) )
            int alt8=8;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                alt8=1;
                }
                break;
            case SYMBOL:
                {
                switch ( input.LA(2) ) {
                case LPAREN:
                    {
                    int LA8_6 = input.LA(3);

                    if ( (LA8_6==RPAREN) ) {
                        alt8=3;
                    }
                    else if ( (LA8_6==NUMBER||LA8_6==LPAREN||LA8_6==NOT||LA8_6==TWIDDLE||LA8_6==STRING||LA8_6==SYMBOL||LA8_6==DOLLARSYM) ) {
                        alt8=5;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("132:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 8, 6, input);

                        throw nvae;
                    }
                    }
                    break;
                case LSQUARE:
                    {
                    alt8=2;
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
                    alt8=4;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("132:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 8, 2, input);

                    throw nvae;
                }

                }
                break;
            case NUMBER:
                {
                alt8=6;
                }
                break;
            case STRING:
                {
                alt8=7;
                }
                break;
            case LPAREN:
                {
                alt8=8;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("132:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:132:15: v= DOLLARSYM
                    {
                    root_0 = (Object)adaptor.nil();

                    v=(Token)input.LT(1);
                    match(input,DOLLARSYM,FOLLOW_DOLLARSYM_in_simple_expr699); if (failed) return retval;
                    if ( backtracking==0 ) {
                    v_tree = (Object)adaptor.create(v);
                    adaptor.addChild(root_0, v_tree);
                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:133:4: v= SYMBOL idx= array_idx
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr706); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    pushFollow(FOLLOW_array_idx_in_simple_expr710);
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
                    // 133:29: -> ^( ARRAY $v $idx)
                    {
                        // dd/grammar/ECAGrammar.g:133:32: ^( ARRAY $v $idx)
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
                    // dd/grammar/ECAGrammar.g:134:4: v= SYMBOL LPAREN RPAREN
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr731); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    LPAREN58=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr733); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN58);

                    RPAREN59=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr735); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN59);


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
                    // 134:29: -> ^( METH $v)
                    {
                        // dd/grammar/ECAGrammar.g:134:32: ^( METH $v)
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
                    // dd/grammar/ECAGrammar.g:135:4: v= SYMBOL
                    {
                    root_0 = (Object)adaptor.nil();

                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr753); if (failed) return retval;
                    if ( backtracking==0 ) {
                    v_tree = (Object)adaptor.create(v);
                    adaptor.addChild(root_0, v_tree);
                    }

                    }
                    break;
                case 5 :
                    // dd/grammar/ECAGrammar.g:136:4: v= SYMBOL LPAREN args= expr_list RPAREN
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr760); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    LPAREN60=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr762); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN60);

                    pushFollow(FOLLOW_expr_list_in_simple_expr766);
                    args=expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr_list.add(args.getTree());
                    RPAREN61=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr768); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN61);


                    // AST REWRITE
                    // elements: args, v
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
                    // 136:43: -> ^( METH $v $args)
                    {
                        // dd/grammar/ECAGrammar.g:136:46: ^( METH $v $args)
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
                    // dd/grammar/ECAGrammar.g:137:4: NUMBER
                    {
                    root_0 = (Object)adaptor.nil();

                    NUMBER62=(Token)input.LT(1);
                    match(input,NUMBER,FOLLOW_NUMBER_in_simple_expr787); if (failed) return retval;
                    if ( backtracking==0 ) {
                    NUMBER62_tree = (Object)adaptor.create(NUMBER62);
                    adaptor.addChild(root_0, NUMBER62_tree);
                    }

                    }
                    break;
                case 7 :
                    // dd/grammar/ECAGrammar.g:138:4: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING63=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_simple_expr792); if (failed) return retval;
                    if ( backtracking==0 ) {
                    STRING63_tree = (Object)adaptor.create(STRING63);
                    adaptor.addChild(root_0, STRING63_tree);
                    }

                    }
                    break;
                case 8 :
                    // dd/grammar/ECAGrammar.g:139:4: LPAREN expr RPAREN
                    {
                    LPAREN64=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr797); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN64);

                    pushFollow(FOLLOW_expr_in_simple_expr799);
                    expr65=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr65.getTree());
                    RPAREN66=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr801); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN66);


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
                    // 139:25: -> ^( expr )
                    {
                        // dd/grammar/ECAGrammar.g:139:28: ^( expr )
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
    // dd/grammar/ECAGrammar.g:142:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );
    public final expr_list_return expr_list() throws RecognitionException {
        expr_list_return retval = new expr_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR68=null;
        expr_return expr67 = null;

        expr_list_return expr_list69 = null;

        expr_return expr70 = null;


        Object SEPR68_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule expr_list");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // dd/grammar/ECAGrammar.g:143:2: ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr )
            int alt9=2;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                int LA9_1 = input.LA(2);

                if ( (synpred21()) ) {
                    alt9=1;
                }
                else if ( (true) ) {
                    alt9=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("142:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 9, 1, input);

                    throw nvae;
                }
                }
                break;
            case SYMBOL:
                {
                int LA9_2 = input.LA(2);

                if ( (synpred21()) ) {
                    alt9=1;
                }
                else if ( (true) ) {
                    alt9=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("142:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 9, 2, input);

                    throw nvae;
                }
                }
                break;
            case NUMBER:
                {
                int LA9_3 = input.LA(2);

                if ( (synpred21()) ) {
                    alt9=1;
                }
                else if ( (true) ) {
                    alt9=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("142:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 9, 3, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
                {
                int LA9_4 = input.LA(2);

                if ( (synpred21()) ) {
                    alt9=1;
                }
                else if ( (true) ) {
                    alt9=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("142:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 9, 4, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                int LA9_5 = input.LA(2);

                if ( (synpred21()) ) {
                    alt9=1;
                }
                else if ( (true) ) {
                    alt9=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("142:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 9, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                int LA9_6 = input.LA(2);

                if ( (synpred21()) ) {
                    alt9=1;
                }
                else if ( (true) ) {
                    alt9=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("142:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 9, 6, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("142:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:143:4: expr SEPR expr_list
                    {
                    pushFollow(FOLLOW_expr_in_expr_list820);
                    expr67=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr67.getTree());
                    SEPR68=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_expr_list822); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR68);

                    pushFollow(FOLLOW_expr_list_in_expr_list824);
                    expr_list69=expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr_list.add(expr_list69.getTree());

                    // AST REWRITE
                    // elements: SEPR, expr, expr_list
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 143:26: -> ^( SEPR expr expr_list )
                    {
                        // dd/grammar/ECAGrammar.g:143:29: ^( SEPR expr expr_list )
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
                    // dd/grammar/ECAGrammar.g:144:4: expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_expr_list841);
                    expr70=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expr70.getTree());

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
    // dd/grammar/ECAGrammar.g:147:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );
    public final array_idx_list_return array_idx_list() throws RecognitionException {
        array_idx_list_return retval = new array_idx_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        array_idx_return array_idx71 = null;

        array_idx_list_return array_idx_list72 = null;

        array_idx_return array_idx73 = null;


        RewriteRuleSubtreeStream stream_array_idx=new RewriteRuleSubtreeStream(adaptor,"rule array_idx");
        RewriteRuleSubtreeStream stream_array_idx_list=new RewriteRuleSubtreeStream(adaptor,"rule array_idx_list");
        try {
            // dd/grammar/ECAGrammar.g:148:2: ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==LSQUARE) ) {
                int LA10_1 = input.LA(2);

                if ( (synpred22()) ) {
                    alt10=1;
                }
                else if ( (true) ) {
                    alt10=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("147:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("147:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:148:4: array_idx array_idx_list
                    {
                    pushFollow(FOLLOW_array_idx_in_array_idx_list852);
                    array_idx71=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx.add(array_idx71.getTree());
                    pushFollow(FOLLOW_array_idx_list_in_array_idx_list854);
                    array_idx_list72=array_idx_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx_list.add(array_idx_list72.getTree());

                    // AST REWRITE
                    // elements: array_idx, array_idx_list
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 148:31: -> ^( SEPR array_idx array_idx_list )
                    {
                        // dd/grammar/ECAGrammar.g:148:34: ^( SEPR array_idx array_idx_list )
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
                    // dd/grammar/ECAGrammar.g:149:4: array_idx
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_array_idx_in_array_idx_list871);
                    array_idx73=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, array_idx73.getTree());

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
    // dd/grammar/ECAGrammar.g:152:1: array_idx : LSQUARE expr RSQUARE -> ^( expr ) ;
    public final array_idx_return array_idx() throws RecognitionException {
        array_idx_return retval = new array_idx_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LSQUARE74=null;
        Token RSQUARE76=null;
        expr_return expr75 = null;


        Object LSQUARE74_tree=null;
        Object RSQUARE76_tree=null;
        RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");
        RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // dd/grammar/ECAGrammar.g:153:2: ( LSQUARE expr RSQUARE -> ^( expr ) )
            // dd/grammar/ECAGrammar.g:153:4: LSQUARE expr RSQUARE
            {
            LSQUARE74=(Token)input.LT(1);
            match(input,LSQUARE,FOLLOW_LSQUARE_in_array_idx882); if (failed) return retval;
            if ( backtracking==0 ) stream_LSQUARE.add(LSQUARE74);

            pushFollow(FOLLOW_expr_in_array_idx884);
            expr75=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_expr.add(expr75.getTree());
            RSQUARE76=(Token)input.LT(1);
            match(input,RSQUARE,FOLLOW_RSQUARE_in_array_idx886); if (failed) return retval;
            if ( backtracking==0 ) stream_RSQUARE.add(RSQUARE76);


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
            // 153:27: -> ^( expr )
            {
                // dd/grammar/ECAGrammar.g:153:30: ^( expr )
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
    // dd/grammar/ECAGrammar.g:156:1: infix_oper : ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper );
    public final infix_oper_return infix_oper() throws RecognitionException {
        infix_oper_return retval = new infix_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        infix_bit_oper_return infix_bit_oper77 = null;

        infix_arith_oper_return infix_arith_oper78 = null;

        infix_bool_oper_return infix_bool_oper79 = null;

        infix_cmp_oper_return infix_cmp_oper80 = null;



        try {
            // dd/grammar/ECAGrammar.g:156:12: ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper )
            int alt11=4;
            switch ( input.LA(1) ) {
            case BOR:
            case BAND:
            case BXOR:
                {
                alt11=1;
                }
                break;
            case MUL:
            case DIV:
            case PLUS:
            case MINUS:
                {
                alt11=2;
                }
                break;
            case OR:
            case AND:
                {
                alt11=3;
                }
                break;
            case EQ:
            case NEQ:
            case GT:
            case LT:
            case GEQ:
            case LEQ:
                {
                alt11=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("156:1: infix_oper : ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper );", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:156:14: infix_bit_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_bit_oper_in_infix_oper904);
                    infix_bit_oper77=infix_bit_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_bit_oper77.getTree());

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:157:4: infix_arith_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_arith_oper_in_infix_oper909);
                    infix_arith_oper78=infix_arith_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_arith_oper78.getTree());

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:158:4: infix_bool_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_bool_oper_in_infix_oper914);
                    infix_bool_oper79=infix_bool_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_bool_oper79.getTree());

                    }
                    break;
                case 4 :
                    // dd/grammar/ECAGrammar.g:159:4: infix_cmp_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_cmp_oper_in_infix_oper919);
                    infix_cmp_oper80=infix_cmp_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_cmp_oper80.getTree());

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
    // dd/grammar/ECAGrammar.g:162:1: infix_bit_oper : ( BAND | BOR | BXOR );
    public final infix_bit_oper_return infix_bit_oper() throws RecognitionException {
        infix_bit_oper_return retval = new infix_bit_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set81=null;

        Object set81_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:163:2: ( BAND | BOR | BXOR )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set81=(Token)input.LT(1);
            if ( (input.LA(1)>=BOR && input.LA(1)<=BXOR) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set81));
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
    // dd/grammar/ECAGrammar.g:168:1: infix_arith_oper : ( MUL | DIV | PLUS | MINUS );
    public final infix_arith_oper_return infix_arith_oper() throws RecognitionException {
        infix_arith_oper_return retval = new infix_arith_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set82=null;

        Object set82_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:169:2: ( MUL | DIV | PLUS | MINUS )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set82=(Token)input.LT(1);
            if ( (input.LA(1)>=MUL && input.LA(1)<=MINUS) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set82));
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
    // dd/grammar/ECAGrammar.g:175:1: infix_bool_oper : ( AND | OR );
    public final infix_bool_oper_return infix_bool_oper() throws RecognitionException {
        infix_bool_oper_return retval = new infix_bool_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set83=null;

        Object set83_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:176:2: ( AND | OR )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set83=(Token)input.LT(1);
            if ( (input.LA(1)>=OR && input.LA(1)<=AND) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set83));
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
    // dd/grammar/ECAGrammar.g:180:1: infix_cmp_oper : ( EQ | NEQ | GT | LT | GEQ | LEQ );
    public final infix_cmp_oper_return infix_cmp_oper() throws RecognitionException {
        infix_cmp_oper_return retval = new infix_cmp_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set84=null;

        Object set84_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:181:2: ( EQ | NEQ | GT | LT | GEQ | LEQ )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set84=(Token)input.LT(1);
            if ( (input.LA(1)>=EQ && input.LA(1)<=LEQ) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set84));
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
    // dd/grammar/ECAGrammar.g:189:1: unary_oper : ( NOT | TWIDDLE );
    public final unary_oper_return unary_oper() throws RecognitionException {
        unary_oper_return retval = new unary_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set85=null;

        Object set85_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:189:12: ( NOT | TWIDDLE )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set85=(Token)input.LT(1);
            if ( input.LA(1)==NOT||input.LA(1)==TWIDDLE ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set85));
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
        // dd/grammar/ECAGrammar.g:85:12: ( binding SEPR bindings )
        // dd/grammar/ECAGrammar.g:85:12: binding SEPR bindings
        {
        pushFollow(FOLLOW_binding_in_synpred1334);
        binding();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred1336); if (failed) return ;
        pushFollow(FOLLOW_bindings_in_synpred1338);
        bindings();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:115:4: ( action_expr SEPR action_expr_list )
        // dd/grammar/ECAGrammar.g:115:4: action_expr SEPR action_expr_list
        {
        pushFollow(FOLLOW_action_expr_in_synpred6489);
        action_expr();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred6491); if (failed) return ;
        pushFollow(FOLLOW_action_expr_list_in_synpred6493);
        action_expr_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred11
    public final void synpred11_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:126:8: ( simple_expr infix_oper expr )
        // dd/grammar/ECAGrammar.g:126:8: simple_expr infix_oper expr
        {
        pushFollow(FOLLOW_simple_expr_in_synpred11612);
        simple_expr();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_infix_oper_in_synpred11614);
        infix_oper();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_expr_in_synpred11616);
        expr();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred11

    // $ANTLR start synpred12
    public final void synpred12_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:127:4: ( simple_expr )
        // dd/grammar/ECAGrammar.g:127:4: simple_expr
        {
        pushFollow(FOLLOW_simple_expr_in_synpred12634);
        simple_expr();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred12

    // $ANTLR start synpred21
    public final void synpred21_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:143:4: ( expr SEPR expr_list )
        // dd/grammar/ECAGrammar.g:143:4: expr SEPR expr_list
        {
        pushFollow(FOLLOW_expr_in_synpred21820);
        expr();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred21822); if (failed) return ;
        pushFollow(FOLLOW_expr_list_in_synpred21824);
        expr_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred21

    // $ANTLR start synpred22
    public final void synpred22_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:148:4: ( array_idx array_idx_list )
        // dd/grammar/ECAGrammar.g:148:4: array_idx array_idx_list
        {
        pushFollow(FOLLOW_array_idx_in_synpred22852);
        array_idx();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_array_idx_list_in_synpred22854);
        array_idx_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred22

    public final boolean synpred12() {
        backtracking++;
        int start = input.mark();
        try {
            synpred12_fragment(); // can never throw exception
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
    public final boolean synpred11() {
        backtracking++;
        int start = input.mark();
        try {
            synpred11_fragment(); // can never throw exception
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
    public final boolean synpred22() {
        backtracking++;
        int start = input.mark();
        try {
            synpred22_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred21() {
        backtracking++;
        int start = input.mark();
        try {
            synpred21_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_eca_script_rule_one_in_eca_script_rule90 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_script_rule92 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_eca_script_rule_one109 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_SYMBOL_in_eca_script_rule_one113 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLASS_in_eca_script_rule_one117 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_SYMBOL_in_eca_script_rule_one121 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_METHOD_in_eca_script_rule_one125 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_SYMBOL_in_eca_script_rule_one129 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_LINE_in_eca_script_rule_one133 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_NUMBER_in_eca_script_rule_one137 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BIND_in_eca_script_rule_one141 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_event_in_eca_script_rule_one145 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_IF_in_eca_script_rule_one149 = new BitSet(new long[]{0x4000802004C01000L,0x0000000000000014L});
    public static final BitSet FOLLOW_condition_in_eca_script_rule_one153 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_DO_in_eca_script_rule_one157 = new BitSet(new long[]{0x4000802007201000L,0x0000000000000014L});
    public static final BitSet FOLLOW_action_in_eca_script_rule_one161 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_ENDRULE_in_eca_script_rule_one165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eca_in_eca_rule202 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_rule204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_event_in_eca_event221 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_event223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_condition_in_eca_condition239 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_condition241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_eca_action257 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_action259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIND_in_eca275 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_event_in_eca279 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_IF_in_eca283 = new BitSet(new long[]{0x4000802004C01000L,0x0000000000000014L});
    public static final BitSet FOLLOW_condition_in_eca287 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_DO_in_eca291 = new BitSet(new long[]{0x4000802007201000L,0x0000000000000014L});
    public static final BitSet FOLLOW_action_in_eca295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bindings_in_event322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_bindings334 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEPR_in_bindings336 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_bindings_in_bindings338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_bindings353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_sym_in_binding363 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_ASSIGN_in_binding365 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_in_binding367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym390 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_COLON_in_bind_sym392 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_condition428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_condition440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_condition452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOTHING_in_action466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_list_in_action478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_action_expr_list489 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEPR_in_action_expr_list491 = new BitSet(new long[]{0x4000802007001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_action_expr_list_in_action_expr_list493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_action_expr_list508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_action_expr518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_action_expr532 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_in_action_expr534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROW_in_action_expr550 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_SYMBOL_in_action_expr554 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAREN_in_action_expr556 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_action_expr558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROW_in_action_expr573 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_SYMBOL_in_action_expr577 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAREN_in_action_expr579 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_list_in_action_expr583 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_action_expr585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_action_expr602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr612 = new BitSet(new long[]{0x000F7FD800000000L});
    public static final BitSet FOLLOW_infix_oper_in_expr614 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_in_expr616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_oper_in_expr639 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_in_expr641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr660 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_TERN_IF_in_expr662 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_in_expr666 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_COLON_in_expr668 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_in_expr672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLARSYM_in_simple_expr699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr706 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_array_idx_in_simple_expr710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr731 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr733 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr760 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr762 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_list_in_simple_expr766 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_simple_expr787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_simple_expr792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr797 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_in_simple_expr799 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_expr_list820 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEPR_in_expr_list822 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_list_in_expr_list824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_expr_list841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_array_idx_list852 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_array_idx_list_in_array_idx_list854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_array_idx_list871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LSQUARE_in_array_idx882 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_in_array_idx884 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RSQUARE_in_array_idx886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_bit_oper_in_infix_oper904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_arith_oper_in_infix_oper909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_bool_oper_in_infix_oper914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_cmp_oper_in_infix_oper919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_bit_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_arith_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_bool_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_cmp_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_unary_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_synpred1334 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEPR_in_synpred1336 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_bindings_in_synpred1338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_synpred6489 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEPR_in_synpred6491 = new BitSet(new long[]{0x4000802007001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_action_expr_list_in_synpred6493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_synpred11612 = new BitSet(new long[]{0x000F7FD800000000L});
    public static final BitSet FOLLOW_infix_oper_in_synpred11614 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_in_synpred11616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_synpred12634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_synpred21820 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_SEPR_in_synpred21822 = new BitSet(new long[]{0x4000802004001000L,0x0000000000000014L});
    public static final BitSet FOLLOW_expr_list_in_synpred21824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_synpred22852 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_array_idx_list_in_synpred22854 = new BitSet(new long[]{0x0000000000000002L});

}