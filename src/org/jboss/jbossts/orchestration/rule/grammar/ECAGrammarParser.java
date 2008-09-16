// $ANTLR 3.0.1 dd/grammar/ECAGrammar.g 2008-09-15 14:07:44

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DIGIT", "POSDIGIT", "SIGN", "BAREINT", "INTEGER", "POINT", "EXPPART", "FLOAT", "NUMBER", "BIND", "IF", "DO", "LPAREN", "RPAREN", "LSQUARE", "RSQUARE", "LBRACE", "RBRACE", "SEPR", "DOT", "ASSIGN", "OR", "AND", "NOT", "EQ", "NEQ", "GT", "LT", "GEQ", "LEQ", "BOR", "BAND", "BXOR", "TWIDDLE", "MUL", "DIV", "PLUS", "MINUS", "MOD", "TERN_IF", "COLON", "LETTER", "UNDERSCORE", "QUOTE", "DQUOTE", "SPACE", "NEWLINE", "PUNCT", "STRING", "BARESYM", "QUOTSYM", "DOTSYM", "SYMBOL", "DOLLAR", "DOLLARSYM", "WS", "Tokens", "UNOP", "BINOP", "TERNOP", "METH", "ARRAY", "NUM_LIT", "STRING_LIT"
    };
    public static final int MINUS=41;
    public static final int ARRAY=65;
    public static final int NUMBER=12;
    public static final int FLOAT=11;
    public static final int POSDIGIT=5;
    public static final int TWIDDLE=37;
    public static final int LEQ=33;
    public static final int MOD=42;
    public static final int GEQ=32;
    public static final int DQUOTE=48;
    public static final int BOR=34;
    public static final int OR=25;
    public static final int STRING_LIT=67;
    public static final int BAREINT=7;
    public static final int LBRACE=20;
    public static final int DOT=23;
    public static final int NEWLINE=50;
    public static final int RBRACE=21;
    public static final int INTEGER=8;
    public static final int AND=26;
    public static final int NUM_LIT=66;
    public static final int ASSIGN=24;
    public static final int SYMBOL=56;
    public static final int RPAREN=17;
    public static final int LPAREN=16;
    public static final int SIGN=6;
    public static final int METH=64;
    public static final int DIGIT=4;
    public static final int PLUS=40;
    public static final int BINOP=62;
    public static final int BAND=35;
    public static final int NEQ=29;
    public static final int TERNOP=63;
    public static final int SPACE=49;
    public static final int LETTER=45;
    public static final int LSQUARE=18;
    public static final int DO=15;
    public static final int POINT=9;
    public static final int BARESYM=53;
    public static final int SEPR=22;
    public static final int WS=59;
    public static final int STRING=52;
    public static final int EQ=28;
    public static final int QUOTSYM=54;
    public static final int LT=31;
    public static final int GT=30;
    public static final int DOLLAR=57;
    public static final int RSQUARE=19;
    public static final int TERN_IF=43;
    public static final int QUOTE=47;
    public static final int UNOP=61;
    public static final int MUL=38;
    public static final int EXPPART=10;
    public static final int PUNCT=51;
    public static final int IF=14;
    public static final int EOF=-1;
    public static final int Tokens=60;
    public static final int COLON=44;
    public static final int DIV=39;
    public static final int DOTSYM=55;
    public static final int BXOR=36;
    public static final int BIND=13;
    public static final int NOT=27;
    public static final int UNDERSCORE=46;
    public static final int DOLLARSYM=58;

        public ECAGrammarParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[54+1];
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


    public static class eca_rule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eca_rule
    // dd/grammar/ECAGrammar.g:26:1: eca_rule : eca EOF -> ^( eca ) ;
    public final eca_rule_return eca_rule() throws RecognitionException {
        eca_rule_return retval = new eca_rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF2=null;
        eca_return eca1 = null;


        Object EOF2_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_eca=new RewriteRuleSubtreeStream(adaptor,"rule eca");
        try {
            // dd/grammar/ECAGrammar.g:26:10: ( eca EOF -> ^( eca ) )
            // dd/grammar/ECAGrammar.g:26:12: eca EOF
            {
            pushFollow(FOLLOW_eca_in_eca_rule86);
            eca1=eca();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_eca.add(eca1.getTree());
            EOF2=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_rule88); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF2);


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
            // 26:20: -> ^( eca )
            {
                // dd/grammar/ECAGrammar.g:26:23: ^( eca )
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
    // dd/grammar/ECAGrammar.g:29:1: eca_event : event EOF -> ^( event ) ;
    public final eca_event_return eca_event() throws RecognitionException {
        eca_event_return retval = new eca_event_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF4=null;
        event_return event3 = null;


        Object EOF4_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_event=new RewriteRuleSubtreeStream(adaptor,"rule event");
        try {
            // dd/grammar/ECAGrammar.g:29:11: ( event EOF -> ^( event ) )
            // dd/grammar/ECAGrammar.g:29:13: event EOF
            {
            pushFollow(FOLLOW_event_in_eca_event105);
            event3=event();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_event.add(event3.getTree());
            EOF4=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_event107); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF4);


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
            // 29:23: -> ^( event )
            {
                // dd/grammar/ECAGrammar.g:29:26: ^( event )
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
    // dd/grammar/ECAGrammar.g:32:1: eca_condition : condition EOF -> ^( condition ) ;
    public final eca_condition_return eca_condition() throws RecognitionException {
        eca_condition_return retval = new eca_condition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF6=null;
        condition_return condition5 = null;


        Object EOF6_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        try {
            // dd/grammar/ECAGrammar.g:32:15: ( condition EOF -> ^( condition ) )
            // dd/grammar/ECAGrammar.g:32:17: condition EOF
            {
            pushFollow(FOLLOW_condition_in_eca_condition123);
            condition5=condition();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_condition.add(condition5.getTree());
            EOF6=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_condition125); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF6);


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
            // 32:31: -> ^( condition )
            {
                // dd/grammar/ECAGrammar.g:32:34: ^( condition )
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
    // dd/grammar/ECAGrammar.g:35:1: eca_action : action EOF -> ^( action ) ;
    public final eca_action_return eca_action() throws RecognitionException {
        eca_action_return retval = new eca_action_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF8=null;
        action_return action7 = null;


        Object EOF8_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_action=new RewriteRuleSubtreeStream(adaptor,"rule action");
        try {
            // dd/grammar/ECAGrammar.g:35:12: ( action EOF -> ^( action ) )
            // dd/grammar/ECAGrammar.g:35:14: action EOF
            {
            pushFollow(FOLLOW_action_in_eca_action141);
            action7=action();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action.add(action7.getTree());
            EOF8=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_action143); if (failed) return retval;
            if ( backtracking==0 ) stream_EOF.add(EOF8);


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
            // 35:25: -> ^( action )
            {
                // dd/grammar/ECAGrammar.g:35:28: ^( action )
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
    // dd/grammar/ECAGrammar.g:38:1: eca : BIND e= event IF c= condition DO a= action -> ^( BIND $e $c $a) ;
    public final eca_return eca() throws RecognitionException {
        eca_return retval = new eca_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BIND9=null;
        Token IF10=null;
        Token DO11=null;
        event_return e = null;

        condition_return c = null;

        action_return a = null;


        Object BIND9_tree=null;
        Object IF10_tree=null;
        Object DO11_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_BIND=new RewriteRuleTokenStream(adaptor,"token BIND");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleSubtreeStream stream_action=new RewriteRuleSubtreeStream(adaptor,"rule action");
        RewriteRuleSubtreeStream stream_event=new RewriteRuleSubtreeStream(adaptor,"rule event");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        try {
            // dd/grammar/ECAGrammar.g:38:5: ( BIND e= event IF c= condition DO a= action -> ^( BIND $e $c $a) )
            // dd/grammar/ECAGrammar.g:38:7: BIND e= event IF c= condition DO a= action
            {
            BIND9=(Token)input.LT(1);
            match(input,BIND,FOLLOW_BIND_in_eca159); if (failed) return retval;
            if ( backtracking==0 ) stream_BIND.add(BIND9);

            pushFollow(FOLLOW_event_in_eca163);
            e=event();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_event.add(e.getTree());
            IF10=(Token)input.LT(1);
            match(input,IF,FOLLOW_IF_in_eca167); if (failed) return retval;
            if ( backtracking==0 ) stream_IF.add(IF10);

            pushFollow(FOLLOW_condition_in_eca171);
            c=condition();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_condition.add(c.getTree());
            DO11=(Token)input.LT(1);
            match(input,DO,FOLLOW_DO_in_eca175); if (failed) return retval;
            if ( backtracking==0 ) stream_DO.add(DO11);

            pushFollow(FOLLOW_action_in_eca179);
            a=action();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_action.add(a.getTree());

            // AST REWRITE
            // elements: e, BIND, c, a
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
            // 40:15: -> ^( BIND $e $c $a)
            {
                // dd/grammar/ECAGrammar.g:40:18: ^( BIND $e $c $a)
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
    // dd/grammar/ECAGrammar.g:45:1: event : bindings ;
    public final event_return event() throws RecognitionException {
        event_return retval = new event_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        bindings_return bindings12 = null;



        try {
            // dd/grammar/ECAGrammar.g:45:7: ( bindings )
            // dd/grammar/ECAGrammar.g:45:9: bindings
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_bindings_in_event206);
            bindings12=bindings();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, bindings12.getTree());

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
    // dd/grammar/ECAGrammar.g:50:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );
    public final bindings_return bindings() throws RecognitionException {
        bindings_return retval = new bindings_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR14=null;
        binding_return binding13 = null;

        bindings_return bindings15 = null;

        binding_return binding16 = null;


        Object SEPR14_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_bindings=new RewriteRuleSubtreeStream(adaptor,"rule bindings");
        RewriteRuleSubtreeStream stream_binding=new RewriteRuleSubtreeStream(adaptor,"rule binding");
        try {
            // dd/grammar/ECAGrammar.g:50:10: ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding )
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
                        new NoViableAltException("50:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );", 1, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("50:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:50:12: binding SEPR bindings
                    {
                    pushFollow(FOLLOW_binding_in_bindings218);
                    binding13=binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_binding.add(binding13.getTree());
                    SEPR14=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_bindings220); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR14);

                    pushFollow(FOLLOW_bindings_in_bindings222);
                    bindings15=bindings();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_bindings.add(bindings15.getTree());

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
                    // 50:34: -> ^( SEPR binding bindings )
                    {
                        // dd/grammar/ECAGrammar.g:50:37: ^( SEPR binding bindings )
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
                    // dd/grammar/ECAGrammar.g:51:4: binding
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_binding_in_bindings237);
                    binding16=binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, binding16.getTree());

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
    // dd/grammar/ECAGrammar.g:54:1: binding : bind_sym ASSIGN expr -> ^( ASSIGN bind_sym expr ) ;
    public final binding_return binding() throws RecognitionException {
        binding_return retval = new binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASSIGN18=null;
        bind_sym_return bind_sym17 = null;

        expr_return expr19 = null;


        Object ASSIGN18_tree=null;
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_bind_sym=new RewriteRuleSubtreeStream(adaptor,"rule bind_sym");
        try {
            // dd/grammar/ECAGrammar.g:54:9: ( bind_sym ASSIGN expr -> ^( ASSIGN bind_sym expr ) )
            // dd/grammar/ECAGrammar.g:54:11: bind_sym ASSIGN expr
            {
            pushFollow(FOLLOW_bind_sym_in_binding247);
            bind_sym17=bind_sym();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_bind_sym.add(bind_sym17.getTree());
            ASSIGN18=(Token)input.LT(1);
            match(input,ASSIGN,FOLLOW_ASSIGN_in_binding249); if (failed) return retval;
            if ( backtracking==0 ) stream_ASSIGN.add(ASSIGN18);

            pushFollow(FOLLOW_expr_in_binding251);
            expr19=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_expr.add(expr19.getTree());

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
            // 54:32: -> ^( ASSIGN bind_sym expr )
            {
                // dd/grammar/ECAGrammar.g:54:35: ^( ASSIGN bind_sym expr )
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
    // dd/grammar/ECAGrammar.g:58:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );
    public final bind_sym_return bind_sym() throws RecognitionException {
        bind_sym_return retval = new bind_sym_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token v=null;
        Token t=null;
        Token COLON20=null;
        Token SYMBOL21=null;

        Object v_tree=null;
        Object t_tree=null;
        Object COLON20_tree=null;
        Object SYMBOL21_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");

        try {
            // dd/grammar/ECAGrammar.g:58:10: (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL )
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
                        new NoViableAltException("58:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );", 2, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("58:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:58:12: v= SYMBOL COLON t= SYMBOL
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym274); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    COLON20=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_bind_sym276); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON20);

                    t=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym280); if (failed) return retval;
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
                    // 58:36: -> ^( COLON $v $t)
                    {
                        // dd/grammar/ECAGrammar.g:58:39: ^( COLON $v $t)
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
                    // dd/grammar/ECAGrammar.g:59:4: SYMBOL
                    {
                    root_0 = (Object)adaptor.nil();

                    SYMBOL21=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym298); if (failed) return retval;
                    if ( backtracking==0 ) {
                    SYMBOL21_tree = (Object)adaptor.create(SYMBOL21);
                    adaptor.addChild(root_0, SYMBOL21_tree);
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
    // dd/grammar/ECAGrammar.g:66:1: condition : expr ;
    public final condition_return condition() throws RecognitionException {
        condition_return retval = new condition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        expr_return expr22 = null;



        try {
            // dd/grammar/ECAGrammar.g:66:11: ( expr )
            // dd/grammar/ECAGrammar.g:66:13: expr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expr_in_condition312);
            expr22=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expr22.getTree());

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
    // dd/grammar/ECAGrammar.g:73:1: action : action_expr_list ;
    public final action_return action() throws RecognitionException {
        action_return retval = new action_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        action_expr_list_return action_expr_list23 = null;



        try {
            // dd/grammar/ECAGrammar.g:73:8: ( action_expr_list )
            // dd/grammar/ECAGrammar.g:73:10: action_expr_list
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_action_expr_list_in_action326);
            action_expr_list23=action_expr_list();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, action_expr_list23.getTree());

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
    // dd/grammar/ECAGrammar.g:76:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );
    public final action_expr_list_return action_expr_list() throws RecognitionException {
        action_expr_list_return retval = new action_expr_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR25=null;
        action_expr_return action_expr24 = null;

        action_expr_list_return action_expr_list26 = null;

        action_expr_return action_expr27 = null;


        Object SEPR25_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_action_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule action_expr_list");
        RewriteRuleSubtreeStream stream_action_expr=new RewriteRuleSubtreeStream(adaptor,"rule action_expr");
        try {
            // dd/grammar/ECAGrammar.g:77:2: ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr )
            int alt3=2;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                int LA3_1 = input.LA(2);

                if ( (synpred3()) ) {
                    alt3=1;
                }
                else if ( (true) ) {
                    alt3=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("76:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 3, 1, input);

                    throw nvae;
                }
                }
                break;
            case SYMBOL:
                {
                int LA3_2 = input.LA(2);

                if ( (synpred3()) ) {
                    alt3=1;
                }
                else if ( (true) ) {
                    alt3=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("76:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 3, 2, input);

                    throw nvae;
                }
                }
                break;
            case NUMBER:
                {
                int LA3_3 = input.LA(2);

                if ( (synpred3()) ) {
                    alt3=1;
                }
                else if ( (true) ) {
                    alt3=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("76:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 3, 3, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
                {
                int LA3_4 = input.LA(2);

                if ( (synpred3()) ) {
                    alt3=1;
                }
                else if ( (true) ) {
                    alt3=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("76:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 3, 4, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                int LA3_5 = input.LA(2);

                if ( (synpred3()) ) {
                    alt3=1;
                }
                else if ( (true) ) {
                    alt3=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("76:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 3, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                int LA3_6 = input.LA(2);

                if ( (synpred3()) ) {
                    alt3=1;
                }
                else if ( (true) ) {
                    alt3=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("76:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 3, 6, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("76:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:77:4: action_expr SEPR action_expr_list
                    {
                    pushFollow(FOLLOW_action_expr_in_action_expr_list337);
                    action_expr24=action_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_action_expr.add(action_expr24.getTree());
                    SEPR25=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_action_expr_list339); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR25);

                    pushFollow(FOLLOW_action_expr_list_in_action_expr_list341);
                    action_expr_list26=action_expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_action_expr_list.add(action_expr_list26.getTree());

                    // AST REWRITE
                    // elements: action_expr_list, action_expr, SEPR
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 77:38: -> ^( SEPR action_expr action_expr_list )
                    {
                        // dd/grammar/ECAGrammar.g:77:41: ^( SEPR action_expr action_expr_list )
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
                    // dd/grammar/ECAGrammar.g:78:4: action_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_action_expr_in_action_expr_list356);
                    action_expr27=action_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, action_expr27.getTree());

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
    // dd/grammar/ECAGrammar.g:81:1: action_expr : expr ;
    public final action_expr_return action_expr() throws RecognitionException {
        action_expr_return retval = new action_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        expr_return expr28 = null;



        try {
            // dd/grammar/ECAGrammar.g:81:13: ( expr )
            // dd/grammar/ECAGrammar.g:81:15: expr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expr_in_action_expr366);
            expr28=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expr28.getTree());

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
    // dd/grammar/ECAGrammar.g:84:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );
    public final expr_return expr() throws RecognitionException {
        expr_return retval = new expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TERN_IF35=null;
        Token COLON36=null;
        simple_expr_return cond = null;

        expr_return iftrue = null;

        expr_return iffalse = null;

        simple_expr_return simple_expr29 = null;

        infix_oper_return infix_oper30 = null;

        expr_return expr31 = null;

        simple_expr_return simple_expr32 = null;

        unary_oper_return unary_oper33 = null;

        expr_return expr34 = null;


        Object TERN_IF35_tree=null;
        Object COLON36_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_TERN_IF=new RewriteRuleTokenStream(adaptor,"token TERN_IF");
        RewriteRuleSubtreeStream stream_unary_oper=new RewriteRuleSubtreeStream(adaptor,"rule unary_oper");
        RewriteRuleSubtreeStream stream_infix_oper=new RewriteRuleSubtreeStream(adaptor,"rule infix_oper");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_simple_expr=new RewriteRuleSubtreeStream(adaptor,"rule simple_expr");
        try {
            // dd/grammar/ECAGrammar.g:84:6: ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) )
            int alt4=4;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                switch ( input.LA(2) ) {
                case EOF:
                case IF:
                case DO:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt4=2;
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
                    alt4=1;
                    }
                    break;
                case TERN_IF:
                    {
                    alt4=4;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("84:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 4, 1, input);

                    throw nvae;
                }

                }
                break;
            case SYMBOL:
                {
                switch ( input.LA(2) ) {
                case LPAREN:
                    {
                    int LA4_10 = input.LA(3);

                    if ( (synpred4()) ) {
                        alt4=1;
                    }
                    else if ( (synpred5()) ) {
                        alt4=2;
                    }
                    else if ( (true) ) {
                        alt4=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("84:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 4, 10, input);

                        throw nvae;
                    }
                    }
                    break;
                case LSQUARE:
                    {
                    int LA4_11 = input.LA(3);

                    if ( (synpred4()) ) {
                        alt4=1;
                    }
                    else if ( (synpred5()) ) {
                        alt4=2;
                    }
                    else if ( (true) ) {
                        alt4=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("84:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 4, 11, input);

                        throw nvae;
                    }
                    }
                    break;
                case TERN_IF:
                    {
                    alt4=4;
                    }
                    break;
                case EOF:
                case IF:
                case DO:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt4=2;
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
                    alt4=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("84:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 4, 2, input);

                    throw nvae;
                }

                }
                break;
            case NUMBER:
                {
                switch ( input.LA(2) ) {
                case TERN_IF:
                    {
                    alt4=4;
                    }
                    break;
                case EOF:
                case IF:
                case DO:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt4=2;
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
                    alt4=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("84:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 4, 3, input);

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
                    alt4=1;
                    }
                    break;
                case EOF:
                case IF:
                case DO:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt4=2;
                    }
                    break;
                case TERN_IF:
                    {
                    alt4=4;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("84:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 4, 4, input);

                    throw nvae;
                }

                }
                break;
            case LPAREN:
                {
                int LA4_5 = input.LA(2);

                if ( (synpred4()) ) {
                    alt4=1;
                }
                else if ( (synpred5()) ) {
                    alt4=2;
                }
                else if ( (true) ) {
                    alt4=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("84:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 4, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                alt4=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("84:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:84:8: simple_expr infix_oper expr
                    {
                    pushFollow(FOLLOW_simple_expr_in_expr376);
                    simple_expr29=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_simple_expr.add(simple_expr29.getTree());
                    pushFollow(FOLLOW_infix_oper_in_expr378);
                    infix_oper30=infix_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_infix_oper.add(infix_oper30.getTree());
                    pushFollow(FOLLOW_expr_in_expr380);
                    expr31=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr31.getTree());

                    // AST REWRITE
                    // elements: infix_oper, simple_expr, expr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 84:37: -> ^( BINOP infix_oper simple_expr expr )
                    {
                        // dd/grammar/ECAGrammar.g:84:40: ^( BINOP infix_oper simple_expr expr )
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
                    // dd/grammar/ECAGrammar.g:85:4: simple_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_expr_in_expr398);
                    simple_expr32=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, simple_expr32.getTree());

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:86:4: unary_oper expr
                    {
                    pushFollow(FOLLOW_unary_oper_in_expr403);
                    unary_oper33=unary_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_unary_oper.add(unary_oper33.getTree());
                    pushFollow(FOLLOW_expr_in_expr405);
                    expr34=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr34.getTree());

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
                    // 86:22: -> ^( UNOP unary_oper expr )
                    {
                        // dd/grammar/ECAGrammar.g:86:25: ^( UNOP unary_oper expr )
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
                    // dd/grammar/ECAGrammar.g:87:4: cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr
                    {
                    pushFollow(FOLLOW_simple_expr_in_expr424);
                    cond=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_simple_expr.add(cond.getTree());
                    TERN_IF35=(Token)input.LT(1);
                    match(input,TERN_IF,FOLLOW_TERN_IF_in_expr426); if (failed) return retval;
                    if ( backtracking==0 ) stream_TERN_IF.add(TERN_IF35);

                    pushFollow(FOLLOW_expr_in_expr430);
                    iftrue=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(iftrue.getTree());
                    COLON36=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_expr432); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON36);

                    pushFollow(FOLLOW_expr_in_expr436);
                    iffalse=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(iffalse.getTree());

                    // AST REWRITE
                    // elements: iftrue, iffalse, cond
                    // token labels: 
                    // rule labels: iftrue, cond, iffalse, retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_iftrue=new RewriteRuleSubtreeStream(adaptor,"token iftrue",iftrue!=null?iftrue.tree:null);
                    RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"token cond",cond!=null?cond.tree:null);
                    RewriteRuleSubtreeStream stream_iffalse=new RewriteRuleSubtreeStream(adaptor,"token iffalse",iffalse!=null?iffalse.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 87:60: -> ^( TERNOP $cond $iftrue $iffalse)
                    {
                        // dd/grammar/ECAGrammar.g:87:63: ^( TERNOP $cond $iftrue $iffalse)
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
    // dd/grammar/ECAGrammar.g:90:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );
    public final simple_expr_return simple_expr() throws RecognitionException {
        simple_expr_return retval = new simple_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token v=null;
        Token LPAREN37=null;
        Token RPAREN38=null;
        Token LPAREN39=null;
        Token RPAREN40=null;
        Token NUMBER41=null;
        Token STRING42=null;
        Token LPAREN43=null;
        Token RPAREN45=null;
        array_idx_return idx = null;

        expr_list_return args = null;

        expr_return expr44 = null;


        Object v_tree=null;
        Object LPAREN37_tree=null;
        Object RPAREN38_tree=null;
        Object LPAREN39_tree=null;
        Object RPAREN40_tree=null;
        Object NUMBER41_tree=null;
        Object STRING42_tree=null;
        Object LPAREN43_tree=null;
        Object RPAREN45_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");
        RewriteRuleSubtreeStream stream_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule expr_list");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_array_idx=new RewriteRuleSubtreeStream(adaptor,"rule array_idx");
        try {
            // dd/grammar/ECAGrammar.g:90:13: (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) )
            int alt5=8;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                alt5=1;
                }
                break;
            case SYMBOL:
                {
                switch ( input.LA(2) ) {
                case LPAREN:
                    {
                    int LA5_6 = input.LA(3);

                    if ( (LA5_6==RPAREN) ) {
                        alt5=3;
                    }
                    else if ( (LA5_6==NUMBER||LA5_6==LPAREN||LA5_6==NOT||LA5_6==TWIDDLE||LA5_6==STRING||LA5_6==SYMBOL||LA5_6==DOLLARSYM) ) {
                        alt5=5;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("90:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 5, 6, input);

                        throw nvae;
                    }
                    }
                    break;
                case LSQUARE:
                    {
                    alt5=2;
                    }
                    break;
                case EOF:
                case IF:
                case DO:
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
                    alt5=4;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("90:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 5, 2, input);

                    throw nvae;
                }

                }
                break;
            case NUMBER:
                {
                alt5=6;
                }
                break;
            case STRING:
                {
                alt5=7;
                }
                break;
            case LPAREN:
                {
                alt5=8;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("90:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:90:15: v= DOLLARSYM
                    {
                    root_0 = (Object)adaptor.nil();

                    v=(Token)input.LT(1);
                    match(input,DOLLARSYM,FOLLOW_DOLLARSYM_in_simple_expr463); if (failed) return retval;
                    if ( backtracking==0 ) {
                    v_tree = (Object)adaptor.create(v);
                    adaptor.addChild(root_0, v_tree);
                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:91:4: v= SYMBOL idx= array_idx
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr470); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    pushFollow(FOLLOW_array_idx_in_simple_expr474);
                    idx=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx.add(idx.getTree());

                    // AST REWRITE
                    // elements: v, idx
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
                    // 91:29: -> ^( ARRAY $v $idx)
                    {
                        // dd/grammar/ECAGrammar.g:91:32: ^( ARRAY $v $idx)
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
                    // dd/grammar/ECAGrammar.g:92:4: v= SYMBOL LPAREN RPAREN
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr495); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    LPAREN37=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr497); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN37);

                    RPAREN38=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr499); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN38);


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
                    // 92:29: -> ^( METH $v)
                    {
                        // dd/grammar/ECAGrammar.g:92:32: ^( METH $v)
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
                    // dd/grammar/ECAGrammar.g:93:4: v= SYMBOL
                    {
                    root_0 = (Object)adaptor.nil();

                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr517); if (failed) return retval;
                    if ( backtracking==0 ) {
                    v_tree = (Object)adaptor.create(v);
                    adaptor.addChild(root_0, v_tree);
                    }

                    }
                    break;
                case 5 :
                    // dd/grammar/ECAGrammar.g:94:4: v= SYMBOL LPAREN args= expr_list RPAREN
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr524); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    LPAREN39=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr526); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN39);

                    pushFollow(FOLLOW_expr_list_in_simple_expr530);
                    args=expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr_list.add(args.getTree());
                    RPAREN40=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr532); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN40);


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
                    // 94:43: -> ^( METH $v $args)
                    {
                        // dd/grammar/ECAGrammar.g:94:46: ^( METH $v $args)
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
                    // dd/grammar/ECAGrammar.g:95:4: NUMBER
                    {
                    root_0 = (Object)adaptor.nil();

                    NUMBER41=(Token)input.LT(1);
                    match(input,NUMBER,FOLLOW_NUMBER_in_simple_expr551); if (failed) return retval;
                    if ( backtracking==0 ) {
                    NUMBER41_tree = (Object)adaptor.create(NUMBER41);
                    adaptor.addChild(root_0, NUMBER41_tree);
                    }

                    }
                    break;
                case 7 :
                    // dd/grammar/ECAGrammar.g:96:4: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING42=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_simple_expr556); if (failed) return retval;
                    if ( backtracking==0 ) {
                    STRING42_tree = (Object)adaptor.create(STRING42);
                    adaptor.addChild(root_0, STRING42_tree);
                    }

                    }
                    break;
                case 8 :
                    // dd/grammar/ECAGrammar.g:97:4: LPAREN expr RPAREN
                    {
                    LPAREN43=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr561); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN43);

                    pushFollow(FOLLOW_expr_in_simple_expr563);
                    expr44=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr44.getTree());
                    RPAREN45=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr565); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN45);


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
                    // 97:25: -> ^( expr )
                    {
                        // dd/grammar/ECAGrammar.g:97:28: ^( expr )
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
    // dd/grammar/ECAGrammar.g:100:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );
    public final expr_list_return expr_list() throws RecognitionException {
        expr_list_return retval = new expr_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR47=null;
        expr_return expr46 = null;

        expr_list_return expr_list48 = null;

        expr_return expr49 = null;


        Object SEPR47_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule expr_list");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // dd/grammar/ECAGrammar.g:101:2: ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr )
            int alt6=2;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                int LA6_1 = input.LA(2);

                if ( (synpred14()) ) {
                    alt6=1;
                }
                else if ( (true) ) {
                    alt6=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("100:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 6, 1, input);

                    throw nvae;
                }
                }
                break;
            case SYMBOL:
                {
                int LA6_2 = input.LA(2);

                if ( (synpred14()) ) {
                    alt6=1;
                }
                else if ( (true) ) {
                    alt6=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("100:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 6, 2, input);

                    throw nvae;
                }
                }
                break;
            case NUMBER:
                {
                int LA6_3 = input.LA(2);

                if ( (synpred14()) ) {
                    alt6=1;
                }
                else if ( (true) ) {
                    alt6=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("100:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 6, 3, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
                {
                int LA6_4 = input.LA(2);

                if ( (synpred14()) ) {
                    alt6=1;
                }
                else if ( (true) ) {
                    alt6=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("100:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 6, 4, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                int LA6_5 = input.LA(2);

                if ( (synpred14()) ) {
                    alt6=1;
                }
                else if ( (true) ) {
                    alt6=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("100:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 6, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                int LA6_6 = input.LA(2);

                if ( (synpred14()) ) {
                    alt6=1;
                }
                else if ( (true) ) {
                    alt6=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("100:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 6, 6, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("100:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:101:4: expr SEPR expr_list
                    {
                    pushFollow(FOLLOW_expr_in_expr_list584);
                    expr46=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr46.getTree());
                    SEPR47=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_expr_list586); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR47);

                    pushFollow(FOLLOW_expr_list_in_expr_list588);
                    expr_list48=expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr_list.add(expr_list48.getTree());

                    // AST REWRITE
                    // elements: expr, SEPR, expr_list
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 101:26: -> ^( SEPR expr expr_list )
                    {
                        // dd/grammar/ECAGrammar.g:101:29: ^( SEPR expr expr_list )
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
                    // dd/grammar/ECAGrammar.g:102:4: expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_expr_list605);
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
    // $ANTLR end expr_list

    public static class array_idx_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start array_idx_list
    // dd/grammar/ECAGrammar.g:105:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );
    public final array_idx_list_return array_idx_list() throws RecognitionException {
        array_idx_list_return retval = new array_idx_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        array_idx_return array_idx50 = null;

        array_idx_list_return array_idx_list51 = null;

        array_idx_return array_idx52 = null;


        RewriteRuleSubtreeStream stream_array_idx=new RewriteRuleSubtreeStream(adaptor,"rule array_idx");
        RewriteRuleSubtreeStream stream_array_idx_list=new RewriteRuleSubtreeStream(adaptor,"rule array_idx_list");
        try {
            // dd/grammar/ECAGrammar.g:106:2: ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==LSQUARE) ) {
                int LA7_1 = input.LA(2);

                if ( (synpred15()) ) {
                    alt7=1;
                }
                else if ( (true) ) {
                    alt7=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("105:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );", 7, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("105:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:106:4: array_idx array_idx_list
                    {
                    pushFollow(FOLLOW_array_idx_in_array_idx_list616);
                    array_idx50=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx.add(array_idx50.getTree());
                    pushFollow(FOLLOW_array_idx_list_in_array_idx_list618);
                    array_idx_list51=array_idx_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx_list.add(array_idx_list51.getTree());

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
                    // 106:31: -> ^( SEPR array_idx array_idx_list )
                    {
                        // dd/grammar/ECAGrammar.g:106:34: ^( SEPR array_idx array_idx_list )
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
                    // dd/grammar/ECAGrammar.g:107:4: array_idx
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_array_idx_in_array_idx_list635);
                    array_idx52=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, array_idx52.getTree());

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
    // dd/grammar/ECAGrammar.g:110:1: array_idx : LSQUARE expr RSQUARE -> ^( expr ) ;
    public final array_idx_return array_idx() throws RecognitionException {
        array_idx_return retval = new array_idx_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LSQUARE53=null;
        Token RSQUARE55=null;
        expr_return expr54 = null;


        Object LSQUARE53_tree=null;
        Object RSQUARE55_tree=null;
        RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");
        RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // dd/grammar/ECAGrammar.g:111:2: ( LSQUARE expr RSQUARE -> ^( expr ) )
            // dd/grammar/ECAGrammar.g:111:4: LSQUARE expr RSQUARE
            {
            LSQUARE53=(Token)input.LT(1);
            match(input,LSQUARE,FOLLOW_LSQUARE_in_array_idx646); if (failed) return retval;
            if ( backtracking==0 ) stream_LSQUARE.add(LSQUARE53);

            pushFollow(FOLLOW_expr_in_array_idx648);
            expr54=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_expr.add(expr54.getTree());
            RSQUARE55=(Token)input.LT(1);
            match(input,RSQUARE,FOLLOW_RSQUARE_in_array_idx650); if (failed) return retval;
            if ( backtracking==0 ) stream_RSQUARE.add(RSQUARE55);


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
            // 111:27: -> ^( expr )
            {
                // dd/grammar/ECAGrammar.g:111:30: ^( expr )
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
    // dd/grammar/ECAGrammar.g:114:1: infix_oper : ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper );
    public final infix_oper_return infix_oper() throws RecognitionException {
        infix_oper_return retval = new infix_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        infix_bit_oper_return infix_bit_oper56 = null;

        infix_arith_oper_return infix_arith_oper57 = null;

        infix_bool_oper_return infix_bool_oper58 = null;

        infix_cmp_oper_return infix_cmp_oper59 = null;



        try {
            // dd/grammar/ECAGrammar.g:114:12: ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper )
            int alt8=4;
            switch ( input.LA(1) ) {
            case BOR:
            case BAND:
            case BXOR:
                {
                alt8=1;
                }
                break;
            case MUL:
            case DIV:
            case PLUS:
            case MINUS:
                {
                alt8=2;
                }
                break;
            case OR:
            case AND:
                {
                alt8=3;
                }
                break;
            case EQ:
            case NEQ:
            case GT:
            case LT:
            case GEQ:
            case LEQ:
                {
                alt8=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("114:1: infix_oper : ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:114:14: infix_bit_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_bit_oper_in_infix_oper668);
                    infix_bit_oper56=infix_bit_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_bit_oper56.getTree());

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:115:4: infix_arith_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_arith_oper_in_infix_oper673);
                    infix_arith_oper57=infix_arith_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_arith_oper57.getTree());

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:116:4: infix_bool_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_bool_oper_in_infix_oper678);
                    infix_bool_oper58=infix_bool_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_bool_oper58.getTree());

                    }
                    break;
                case 4 :
                    // dd/grammar/ECAGrammar.g:117:4: infix_cmp_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_cmp_oper_in_infix_oper683);
                    infix_cmp_oper59=infix_cmp_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_cmp_oper59.getTree());

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
    // dd/grammar/ECAGrammar.g:120:1: infix_bit_oper : ( BAND | BOR | BXOR );
    public final infix_bit_oper_return infix_bit_oper() throws RecognitionException {
        infix_bit_oper_return retval = new infix_bit_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set60=null;

        Object set60_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:121:2: ( BAND | BOR | BXOR )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set60=(Token)input.LT(1);
            if ( (input.LA(1)>=BOR && input.LA(1)<=BXOR) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set60));
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
    // dd/grammar/ECAGrammar.g:126:1: infix_arith_oper : ( MUL | DIV | PLUS | MINUS );
    public final infix_arith_oper_return infix_arith_oper() throws RecognitionException {
        infix_arith_oper_return retval = new infix_arith_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set61=null;

        Object set61_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:127:2: ( MUL | DIV | PLUS | MINUS )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set61=(Token)input.LT(1);
            if ( (input.LA(1)>=MUL && input.LA(1)<=MINUS) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set61));
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
    // dd/grammar/ECAGrammar.g:133:1: infix_bool_oper : ( AND | OR );
    public final infix_bool_oper_return infix_bool_oper() throws RecognitionException {
        infix_bool_oper_return retval = new infix_bool_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set62=null;

        Object set62_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:134:2: ( AND | OR )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set62=(Token)input.LT(1);
            if ( (input.LA(1)>=OR && input.LA(1)<=AND) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set62));
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
    // dd/grammar/ECAGrammar.g:138:1: infix_cmp_oper : ( EQ | NEQ | GT | LT | GEQ | LEQ );
    public final infix_cmp_oper_return infix_cmp_oper() throws RecognitionException {
        infix_cmp_oper_return retval = new infix_cmp_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set63=null;

        Object set63_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:139:2: ( EQ | NEQ | GT | LT | GEQ | LEQ )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set63=(Token)input.LT(1);
            if ( (input.LA(1)>=EQ && input.LA(1)<=LEQ) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set63));
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
    // dd/grammar/ECAGrammar.g:147:1: unary_oper : ( NOT | TWIDDLE );
    public final unary_oper_return unary_oper() throws RecognitionException {
        unary_oper_return retval = new unary_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set64=null;

        Object set64_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:147:12: ( NOT | TWIDDLE )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set64=(Token)input.LT(1);
            if ( input.LA(1)==NOT||input.LA(1)==TWIDDLE ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set64));
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
        // dd/grammar/ECAGrammar.g:50:12: ( binding SEPR bindings )
        // dd/grammar/ECAGrammar.g:50:12: binding SEPR bindings
        {
        pushFollow(FOLLOW_binding_in_synpred1218);
        binding();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred1220); if (failed) return ;
        pushFollow(FOLLOW_bindings_in_synpred1222);
        bindings();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:77:4: ( action_expr SEPR action_expr_list )
        // dd/grammar/ECAGrammar.g:77:4: action_expr SEPR action_expr_list
        {
        pushFollow(FOLLOW_action_expr_in_synpred3337);
        action_expr();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred3339); if (failed) return ;
        pushFollow(FOLLOW_action_expr_list_in_synpred3341);
        action_expr_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:84:8: ( simple_expr infix_oper expr )
        // dd/grammar/ECAGrammar.g:84:8: simple_expr infix_oper expr
        {
        pushFollow(FOLLOW_simple_expr_in_synpred4376);
        simple_expr();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_infix_oper_in_synpred4378);
        infix_oper();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_expr_in_synpred4380);
        expr();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:85:4: ( simple_expr )
        // dd/grammar/ECAGrammar.g:85:4: simple_expr
        {
        pushFollow(FOLLOW_simple_expr_in_synpred5398);
        simple_expr();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred14
    public final void synpred14_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:101:4: ( expr SEPR expr_list )
        // dd/grammar/ECAGrammar.g:101:4: expr SEPR expr_list
        {
        pushFollow(FOLLOW_expr_in_synpred14584);
        expr();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred14586); if (failed) return ;
        pushFollow(FOLLOW_expr_list_in_synpred14588);
        expr_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred14

    // $ANTLR start synpred15
    public final void synpred15_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:106:4: ( array_idx array_idx_list )
        // dd/grammar/ECAGrammar.g:106:4: array_idx array_idx_list
        {
        pushFollow(FOLLOW_array_idx_in_synpred15616);
        array_idx();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_array_idx_list_in_synpred15618);
        array_idx_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred15

    public final boolean synpred4() {
        backtracking++;
        int start = input.mark();
        try {
            synpred4_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred14() {
        backtracking++;
        int start = input.mark();
        try {
            synpred14_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred3() {
        backtracking++;
        int start = input.mark();
        try {
            synpred3_fragment(); // can never throw exception
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
    public final boolean synpred5() {
        backtracking++;
        int start = input.mark();
        try {
            synpred5_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred15() {
        backtracking++;
        int start = input.mark();
        try {
            synpred15_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_eca_in_eca_rule86 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_rule88 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_event_in_eca_event105 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_event107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_condition_in_eca_condition123 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_condition125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_eca_action141 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_action143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIND_in_eca159 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_event_in_eca163 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_IF_in_eca167 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_condition_in_eca171 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_DO_in_eca175 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_action_in_eca179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bindings_in_event206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_bindings218 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_bindings220 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_bindings_in_bindings222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_bindings237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_sym_in_binding247 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_binding249 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_binding251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym274 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_COLON_in_bind_sym276 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_condition312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_list_in_action326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_action_expr_list337 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_action_expr_list339 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_action_expr_list_in_action_expr_list341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_action_expr_list356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_action_expr366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr376 = new BitSet(new long[]{0x000003DFF6000000L});
    public static final BitSet FOLLOW_infix_oper_in_expr378 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_expr380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_oper_in_expr403 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_expr405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr424 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_TERN_IF_in_expr426 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_expr430 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_COLON_in_expr432 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_expr436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLARSYM_in_simple_expr463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr470 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_array_idx_in_simple_expr474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr495 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr497 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr524 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr526 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_list_in_simple_expr530 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_simple_expr551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_simple_expr556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr561 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_simple_expr563 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_expr_list584 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_expr_list586 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_list_in_expr_list588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_expr_list605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_array_idx_list616 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_array_idx_list_in_array_idx_list618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_array_idx_list635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LSQUARE_in_array_idx646 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_array_idx648 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_RSQUARE_in_array_idx650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_bit_oper_in_infix_oper668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_arith_oper_in_infix_oper673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_bool_oper_in_infix_oper678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_cmp_oper_in_infix_oper683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_bit_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_arith_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_bool_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_cmp_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_unary_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_synpred1218 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_synpred1220 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_bindings_in_synpred1222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_synpred3337 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_synpred3339 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_action_expr_list_in_synpred3341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_synpred4376 = new BitSet(new long[]{0x000003DFF6000000L});
    public static final BitSet FOLLOW_infix_oper_in_synpred4378 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_synpred4380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_synpred5398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_synpred14584 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_synpred14586 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_list_in_synpred14588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_synpred15616 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_array_idx_list_in_synpred15618 = new BitSet(new long[]{0x0000000000000002L});

}