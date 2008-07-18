// $ANTLR 3.0.1 dd/grammar/ECAGrammar.g 2008-07-18 14:16:03

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DIGIT", "POSDIGIT", "SIGN", "BAREINT", "INTEGER", "POINT", "EXPPART", "FLOAT", "NUMBER", "WHEN", "IF", "DO", "LPAREN", "RPAREN", "LSQUARE", "RSQUARE", "LBRACE", "RBRACE", "SEPR", "DOT", "ASSIGN", "OR", "AND", "NOT", "EQ", "NEQ", "GT", "LT", "GEQ", "LEQ", "BOR", "BAND", "BXOR", "TWIDDLE", "MUL", "DIV", "PLUS", "MINUS", "MOD", "TERN_IF", "COLON", "LETTER", "UNDERSCORE", "QUOTE", "DQUOTE", "SPACE", "NEWLINE", "PUNCT", "STRING", "BARESYM", "QUOTSYM", "DOTSYM", "SYMBOL", "DOLLAR", "DOLLARSYM", "WS", "Tokens", "UNOP", "BINOP", "TERNOP", "METH", "ARRAY", "NUM_LIT", "STRING_LIT"
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
    public static final int WHEN=13;
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
    public static final int NOT=27;
    public static final int UNDERSCORE=46;
    public static final int DOLLARSYM=58;

        public ECAGrammarParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[53+1];
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
    // dd/grammar/ECAGrammar.g:26:1: eca_rule : eca EOF ;
    public final eca_rule_return eca_rule() throws RecognitionException {
        eca_rule_return retval = new eca_rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF2=null;
        eca_return eca1 = null;


        Object EOF2_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:26:10: ( eca EOF )
            // dd/grammar/ECAGrammar.g:26:12: eca EOF
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_eca_in_eca_rule86);
            eca1=eca();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, eca1.getTree());
            EOF2=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_eca_rule88); if (failed) return retval;
            if ( backtracking==0 ) {
            EOF2_tree = (Object)adaptor.create(EOF2);
            adaptor.addChild(root_0, EOF2_tree);
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

    public static class eca_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start eca
    // dd/grammar/ECAGrammar.g:28:1: eca : ( WHEN event -> ^( WHEN event ) | IF condition -> ^( IF condition ) | DO action -> ^( DO action ) );
    public final eca_return eca() throws RecognitionException {
        eca_return retval = new eca_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token WHEN3=null;
        Token IF5=null;
        Token DO7=null;
        event_return event4 = null;

        condition_return condition6 = null;

        action_return action8 = null;


        Object WHEN3_tree=null;
        Object IF5_tree=null;
        Object DO7_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_WHEN=new RewriteRuleTokenStream(adaptor,"token WHEN");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleSubtreeStream stream_action=new RewriteRuleSubtreeStream(adaptor,"rule action");
        RewriteRuleSubtreeStream stream_event=new RewriteRuleSubtreeStream(adaptor,"rule event");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        try {
            // dd/grammar/ECAGrammar.g:28:5: ( WHEN event -> ^( WHEN event ) | IF condition -> ^( IF condition ) | DO action -> ^( DO action ) )
            int alt1=3;
            switch ( input.LA(1) ) {
            case WHEN:
                {
                alt1=1;
                }
                break;
            case IF:
                {
                alt1=2;
                }
                break;
            case DO:
                {
                alt1=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("28:1: eca : ( WHEN event -> ^( WHEN event ) | IF condition -> ^( IF condition ) | DO action -> ^( DO action ) );", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:28:7: WHEN event
                    {
                    WHEN3=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_eca97); if (failed) return retval;
                    if ( backtracking==0 ) stream_WHEN.add(WHEN3);

                    pushFollow(FOLLOW_event_in_eca99);
                    event4=event();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_event.add(event4.getTree());

                    // AST REWRITE
                    // elements: event, WHEN
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 28:18: -> ^( WHEN event )
                    {
                        // dd/grammar/ECAGrammar.g:28:21: ^( WHEN event )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_WHEN.next(), root_1);

                        adaptor.addChild(root_1, stream_event.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:29:4: IF condition
                    {
                    IF5=(Token)input.LT(1);
                    match(input,IF,FOLLOW_IF_in_eca112); if (failed) return retval;
                    if ( backtracking==0 ) stream_IF.add(IF5);

                    pushFollow(FOLLOW_condition_in_eca114);
                    condition6=condition();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_condition.add(condition6.getTree());

                    // AST REWRITE
                    // elements: IF, condition
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 29:18: -> ^( IF condition )
                    {
                        // dd/grammar/ECAGrammar.g:29:21: ^( IF condition )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_IF.next(), root_1);

                        adaptor.addChild(root_1, stream_condition.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    }

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:30:4: DO action
                    {
                    DO7=(Token)input.LT(1);
                    match(input,DO,FOLLOW_DO_in_eca128); if (failed) return retval;
                    if ( backtracking==0 ) stream_DO.add(DO7);

                    pushFollow(FOLLOW_action_in_eca130);
                    action8=action();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_action.add(action8.getTree());

                    // AST REWRITE
                    // elements: DO, action
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 30:14: -> ^( DO action )
                    {
                        // dd/grammar/ECAGrammar.g:30:17: ^( DO action )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_DO.next(), root_1);

                        adaptor.addChild(root_1, stream_action.next());

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
    // $ANTLR end eca

    public static class event_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start event
    // dd/grammar/ECAGrammar.g:35:1: event : bindings ;
    public final event_return event() throws RecognitionException {
        event_return retval = new event_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        bindings_return bindings9 = null;



        try {
            // dd/grammar/ECAGrammar.g:35:7: ( bindings )
            // dd/grammar/ECAGrammar.g:35:9: bindings
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_bindings_in_event150);
            bindings9=bindings();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, bindings9.getTree());

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
    // dd/grammar/ECAGrammar.g:40:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );
    public final bindings_return bindings() throws RecognitionException {
        bindings_return retval = new bindings_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR11=null;
        binding_return binding10 = null;

        bindings_return bindings12 = null;

        binding_return binding13 = null;


        Object SEPR11_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_bindings=new RewriteRuleSubtreeStream(adaptor,"rule bindings");
        RewriteRuleSubtreeStream stream_binding=new RewriteRuleSubtreeStream(adaptor,"rule binding");
        try {
            // dd/grammar/ECAGrammar.g:40:10: ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==SYMBOL) ) {
                int LA2_1 = input.LA(2);

                if ( (synpred3()) ) {
                    alt2=1;
                }
                else if ( (true) ) {
                    alt2=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("40:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );", 2, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("40:1: bindings : ( binding SEPR bindings -> ^( SEPR binding bindings ) | binding );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:40:12: binding SEPR bindings
                    {
                    pushFollow(FOLLOW_binding_in_bindings162);
                    binding10=binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_binding.add(binding10.getTree());
                    SEPR11=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_bindings164); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR11);

                    pushFollow(FOLLOW_bindings_in_bindings166);
                    bindings12=bindings();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_bindings.add(bindings12.getTree());

                    // AST REWRITE
                    // elements: SEPR, binding, bindings
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 40:34: -> ^( SEPR binding bindings )
                    {
                        // dd/grammar/ECAGrammar.g:40:37: ^( SEPR binding bindings )
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
                    // dd/grammar/ECAGrammar.g:41:4: binding
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_binding_in_bindings181);
                    binding13=binding();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, binding13.getTree());

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
    // dd/grammar/ECAGrammar.g:44:1: binding : bind_sym ASSIGN expr -> ^( ASSIGN bind_sym expr ) ;
    public final binding_return binding() throws RecognitionException {
        binding_return retval = new binding_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASSIGN15=null;
        bind_sym_return bind_sym14 = null;

        expr_return expr16 = null;


        Object ASSIGN15_tree=null;
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_bind_sym=new RewriteRuleSubtreeStream(adaptor,"rule bind_sym");
        try {
            // dd/grammar/ECAGrammar.g:44:9: ( bind_sym ASSIGN expr -> ^( ASSIGN bind_sym expr ) )
            // dd/grammar/ECAGrammar.g:44:11: bind_sym ASSIGN expr
            {
            pushFollow(FOLLOW_bind_sym_in_binding191);
            bind_sym14=bind_sym();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_bind_sym.add(bind_sym14.getTree());
            ASSIGN15=(Token)input.LT(1);
            match(input,ASSIGN,FOLLOW_ASSIGN_in_binding193); if (failed) return retval;
            if ( backtracking==0 ) stream_ASSIGN.add(ASSIGN15);

            pushFollow(FOLLOW_expr_in_binding195);
            expr16=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_expr.add(expr16.getTree());

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
            // 44:32: -> ^( ASSIGN bind_sym expr )
            {
                // dd/grammar/ECAGrammar.g:44:35: ^( ASSIGN bind_sym expr )
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
    // dd/grammar/ECAGrammar.g:48:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );
    public final bind_sym_return bind_sym() throws RecognitionException {
        bind_sym_return retval = new bind_sym_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token v=null;
        Token t=null;
        Token COLON17=null;
        Token SYMBOL18=null;

        Object v_tree=null;
        Object t_tree=null;
        Object COLON17_tree=null;
        Object SYMBOL18_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");

        try {
            // dd/grammar/ECAGrammar.g:48:10: (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==SYMBOL) ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1==COLON) ) {
                    alt3=1;
                }
                else if ( (LA3_1==ASSIGN) ) {
                    alt3=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("48:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );", 3, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("48:1: bind_sym : (v= SYMBOL COLON t= SYMBOL -> ^( COLON $v $t) | SYMBOL );", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:48:12: v= SYMBOL COLON t= SYMBOL
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym218); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    COLON17=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_bind_sym220); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON17);

                    t=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym224); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(t);


                    // AST REWRITE
                    // elements: t, v, COLON
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
                    // 48:36: -> ^( COLON $v $t)
                    {
                        // dd/grammar/ECAGrammar.g:48:39: ^( COLON $v $t)
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
                    // dd/grammar/ECAGrammar.g:49:4: SYMBOL
                    {
                    root_0 = (Object)adaptor.nil();

                    SYMBOL18=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_bind_sym242); if (failed) return retval;
                    if ( backtracking==0 ) {
                    SYMBOL18_tree = (Object)adaptor.create(SYMBOL18);
                    adaptor.addChild(root_0, SYMBOL18_tree);
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
    // dd/grammar/ECAGrammar.g:56:1: condition : expr ;
    public final condition_return condition() throws RecognitionException {
        condition_return retval = new condition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        expr_return expr19 = null;



        try {
            // dd/grammar/ECAGrammar.g:56:11: ( expr )
            // dd/grammar/ECAGrammar.g:56:13: expr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expr_in_condition256);
            expr19=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expr19.getTree());

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
    // dd/grammar/ECAGrammar.g:63:1: action : action_expr_list ;
    public final action_return action() throws RecognitionException {
        action_return retval = new action_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        action_expr_list_return action_expr_list20 = null;



        try {
            // dd/grammar/ECAGrammar.g:63:8: ( action_expr_list )
            // dd/grammar/ECAGrammar.g:63:10: action_expr_list
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_action_expr_list_in_action270);
            action_expr_list20=action_expr_list();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, action_expr_list20.getTree());

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
    // dd/grammar/ECAGrammar.g:66:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );
    public final action_expr_list_return action_expr_list() throws RecognitionException {
        action_expr_list_return retval = new action_expr_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR22=null;
        action_expr_return action_expr21 = null;

        action_expr_list_return action_expr_list23 = null;

        action_expr_return action_expr24 = null;


        Object SEPR22_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_action_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule action_expr_list");
        RewriteRuleSubtreeStream stream_action_expr=new RewriteRuleSubtreeStream(adaptor,"rule action_expr");
        try {
            // dd/grammar/ECAGrammar.g:67:2: ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr )
            int alt4=2;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                int LA4_1 = input.LA(2);

                if ( (synpred5()) ) {
                    alt4=1;
                }
                else if ( (true) ) {
                    alt4=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("66:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 4, 1, input);

                    throw nvae;
                }
                }
                break;
            case SYMBOL:
                {
                int LA4_2 = input.LA(2);

                if ( (synpred5()) ) {
                    alt4=1;
                }
                else if ( (true) ) {
                    alt4=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("66:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 4, 2, input);

                    throw nvae;
                }
                }
                break;
            case NUMBER:
                {
                int LA4_3 = input.LA(2);

                if ( (synpred5()) ) {
                    alt4=1;
                }
                else if ( (true) ) {
                    alt4=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("66:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 4, 3, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
                {
                int LA4_4 = input.LA(2);

                if ( (synpred5()) ) {
                    alt4=1;
                }
                else if ( (true) ) {
                    alt4=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("66:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 4, 4, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                int LA4_5 = input.LA(2);

                if ( (synpred5()) ) {
                    alt4=1;
                }
                else if ( (true) ) {
                    alt4=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("66:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 4, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                int LA4_6 = input.LA(2);

                if ( (synpred5()) ) {
                    alt4=1;
                }
                else if ( (true) ) {
                    alt4=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("66:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 4, 6, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("66:1: action_expr_list : ( action_expr SEPR action_expr_list -> ^( SEPR action_expr action_expr_list ) | action_expr );", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:67:4: action_expr SEPR action_expr_list
                    {
                    pushFollow(FOLLOW_action_expr_in_action_expr_list281);
                    action_expr21=action_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_action_expr.add(action_expr21.getTree());
                    SEPR22=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_action_expr_list283); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR22);

                    pushFollow(FOLLOW_action_expr_list_in_action_expr_list285);
                    action_expr_list23=action_expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_action_expr_list.add(action_expr_list23.getTree());

                    // AST REWRITE
                    // elements: action_expr_list, SEPR, action_expr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 67:38: -> ^( SEPR action_expr action_expr_list )
                    {
                        // dd/grammar/ECAGrammar.g:67:41: ^( SEPR action_expr action_expr_list )
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
                    // dd/grammar/ECAGrammar.g:68:4: action_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_action_expr_in_action_expr_list300);
                    action_expr24=action_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, action_expr24.getTree());

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
    // dd/grammar/ECAGrammar.g:71:1: action_expr : expr ;
    public final action_expr_return action_expr() throws RecognitionException {
        action_expr_return retval = new action_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        expr_return expr25 = null;



        try {
            // dd/grammar/ECAGrammar.g:71:13: ( expr )
            // dd/grammar/ECAGrammar.g:71:15: expr
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expr_in_action_expr310);
            expr25=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) adaptor.addChild(root_0, expr25.getTree());

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
    // dd/grammar/ECAGrammar.g:74:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );
    public final expr_return expr() throws RecognitionException {
        expr_return retval = new expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TERN_IF32=null;
        Token COLON33=null;
        simple_expr_return cond = null;

        expr_return iftrue = null;

        expr_return iffalse = null;

        simple_expr_return simple_expr26 = null;

        infix_oper_return infix_oper27 = null;

        expr_return expr28 = null;

        simple_expr_return simple_expr29 = null;

        unary_oper_return unary_oper30 = null;

        expr_return expr31 = null;


        Object TERN_IF32_tree=null;
        Object COLON33_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_TERN_IF=new RewriteRuleTokenStream(adaptor,"token TERN_IF");
        RewriteRuleSubtreeStream stream_unary_oper=new RewriteRuleSubtreeStream(adaptor,"rule unary_oper");
        RewriteRuleSubtreeStream stream_infix_oper=new RewriteRuleSubtreeStream(adaptor,"rule infix_oper");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_simple_expr=new RewriteRuleSubtreeStream(adaptor,"rule simple_expr");
        try {
            // dd/grammar/ECAGrammar.g:74:6: ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) )
            int alt5=4;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                switch ( input.LA(2) ) {
                case EOF:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt5=2;
                    }
                    break;
                case TERN_IF:
                    {
                    alt5=4;
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
                    alt5=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("74:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 5, 1, input);

                    throw nvae;
                }

                }
                break;
            case SYMBOL:
                {
                switch ( input.LA(2) ) {
                case LPAREN:
                    {
                    int LA5_10 = input.LA(3);

                    if ( (synpred6()) ) {
                        alt5=1;
                    }
                    else if ( (synpred7()) ) {
                        alt5=2;
                    }
                    else if ( (true) ) {
                        alt5=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("74:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 5, 10, input);

                        throw nvae;
                    }
                    }
                    break;
                case LSQUARE:
                    {
                    int LA5_11 = input.LA(3);

                    if ( (synpred6()) ) {
                        alt5=1;
                    }
                    else if ( (synpred7()) ) {
                        alt5=2;
                    }
                    else if ( (true) ) {
                        alt5=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("74:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 5, 11, input);

                        throw nvae;
                    }
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
                    alt5=1;
                    }
                    break;
                case TERN_IF:
                    {
                    alt5=4;
                    }
                    break;
                case EOF:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt5=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("74:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 5, 2, input);

                    throw nvae;
                }

                }
                break;
            case NUMBER:
                {
                switch ( input.LA(2) ) {
                case TERN_IF:
                    {
                    alt5=4;
                    }
                    break;
                case EOF:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt5=2;
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
                    alt5=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("74:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 5, 3, input);

                    throw nvae;
                }

                }
                break;
            case STRING:
                {
                switch ( input.LA(2) ) {
                case TERN_IF:
                    {
                    alt5=4;
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
                    alt5=1;
                    }
                    break;
                case EOF:
                case RPAREN:
                case RSQUARE:
                case SEPR:
                case COLON:
                    {
                    alt5=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("74:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 5, 4, input);

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
                else if ( (synpred7()) ) {
                    alt5=2;
                }
                else if ( (true) ) {
                    alt5=4;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("74:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 5, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                alt5=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("74:1: expr : ( simple_expr infix_oper expr -> ^( BINOP infix_oper simple_expr expr ) | simple_expr | unary_oper expr -> ^( UNOP unary_oper expr ) | cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr -> ^( TERNOP $cond $iftrue $iffalse) );", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:74:8: simple_expr infix_oper expr
                    {
                    pushFollow(FOLLOW_simple_expr_in_expr320);
                    simple_expr26=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_simple_expr.add(simple_expr26.getTree());
                    pushFollow(FOLLOW_infix_oper_in_expr322);
                    infix_oper27=infix_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_infix_oper.add(infix_oper27.getTree());
                    pushFollow(FOLLOW_expr_in_expr324);
                    expr28=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr28.getTree());

                    // AST REWRITE
                    // elements: expr, infix_oper, simple_expr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 74:37: -> ^( BINOP infix_oper simple_expr expr )
                    {
                        // dd/grammar/ECAGrammar.g:74:40: ^( BINOP infix_oper simple_expr expr )
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
                    // dd/grammar/ECAGrammar.g:75:4: simple_expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_simple_expr_in_expr342);
                    simple_expr29=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, simple_expr29.getTree());

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:76:4: unary_oper expr
                    {
                    pushFollow(FOLLOW_unary_oper_in_expr347);
                    unary_oper30=unary_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_unary_oper.add(unary_oper30.getTree());
                    pushFollow(FOLLOW_expr_in_expr349);
                    expr31=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr31.getTree());

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
                    // 76:22: -> ^( UNOP unary_oper expr )
                    {
                        // dd/grammar/ECAGrammar.g:76:25: ^( UNOP unary_oper expr )
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
                    // dd/grammar/ECAGrammar.g:77:4: cond= simple_expr TERN_IF iftrue= expr COLON iffalse= expr
                    {
                    pushFollow(FOLLOW_simple_expr_in_expr368);
                    cond=simple_expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_simple_expr.add(cond.getTree());
                    TERN_IF32=(Token)input.LT(1);
                    match(input,TERN_IF,FOLLOW_TERN_IF_in_expr370); if (failed) return retval;
                    if ( backtracking==0 ) stream_TERN_IF.add(TERN_IF32);

                    pushFollow(FOLLOW_expr_in_expr374);
                    iftrue=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(iftrue.getTree());
                    COLON33=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_expr376); if (failed) return retval;
                    if ( backtracking==0 ) stream_COLON.add(COLON33);

                    pushFollow(FOLLOW_expr_in_expr380);
                    iffalse=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(iffalse.getTree());

                    // AST REWRITE
                    // elements: iffalse, cond, iftrue
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
                    // 77:60: -> ^( TERNOP $cond $iftrue $iffalse)
                    {
                        // dd/grammar/ECAGrammar.g:77:63: ^( TERNOP $cond $iftrue $iffalse)
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
    // dd/grammar/ECAGrammar.g:80:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );
    public final simple_expr_return simple_expr() throws RecognitionException {
        simple_expr_return retval = new simple_expr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token v=null;
        Token LPAREN34=null;
        Token RPAREN35=null;
        Token LPAREN36=null;
        Token RPAREN37=null;
        Token NUMBER38=null;
        Token STRING39=null;
        Token LPAREN40=null;
        Token RPAREN42=null;
        array_idx_return idx = null;

        expr_list_return args = null;

        expr_return expr41 = null;


        Object v_tree=null;
        Object LPAREN34_tree=null;
        Object RPAREN35_tree=null;
        Object LPAREN36_tree=null;
        Object RPAREN37_tree=null;
        Object NUMBER38_tree=null;
        Object STRING39_tree=null;
        Object LPAREN40_tree=null;
        Object RPAREN42_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");
        RewriteRuleSubtreeStream stream_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule expr_list");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        RewriteRuleSubtreeStream stream_array_idx=new RewriteRuleSubtreeStream(adaptor,"rule array_idx");
        try {
            // dd/grammar/ECAGrammar.g:80:13: (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) )
            int alt6=8;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                alt6=1;
                }
                break;
            case SYMBOL:
                {
                switch ( input.LA(2) ) {
                case LPAREN:
                    {
                    int LA6_6 = input.LA(3);

                    if ( (LA6_6==RPAREN) ) {
                        alt6=3;
                    }
                    else if ( (LA6_6==NUMBER||LA6_6==LPAREN||LA6_6==NOT||LA6_6==TWIDDLE||LA6_6==STRING||LA6_6==SYMBOL||LA6_6==DOLLARSYM) ) {
                        alt6=5;
                    }
                    else {
                        if (backtracking>0) {failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("80:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 6, 6, input);

                        throw nvae;
                    }
                    }
                    break;
                case LSQUARE:
                    {
                    alt6=2;
                    }
                    break;
                case EOF:
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
                    alt6=4;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("80:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 6, 2, input);

                    throw nvae;
                }

                }
                break;
            case NUMBER:
                {
                alt6=6;
                }
                break;
            case STRING:
                {
                alt6=7;
                }
                break;
            case LPAREN:
                {
                alt6=8;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("80:1: simple_expr : (v= DOLLARSYM | v= SYMBOL idx= array_idx -> ^( ARRAY $v $idx) | v= SYMBOL LPAREN RPAREN -> ^( METH $v) | v= SYMBOL | v= SYMBOL LPAREN args= expr_list RPAREN -> ^( METH $v $args) | NUMBER | STRING | LPAREN expr RPAREN -> ^( expr ) );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:80:15: v= DOLLARSYM
                    {
                    root_0 = (Object)adaptor.nil();

                    v=(Token)input.LT(1);
                    match(input,DOLLARSYM,FOLLOW_DOLLARSYM_in_simple_expr407); if (failed) return retval;
                    if ( backtracking==0 ) {
                    v_tree = (Object)adaptor.create(v);
                    adaptor.addChild(root_0, v_tree);
                    }

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:81:4: v= SYMBOL idx= array_idx
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr414); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    pushFollow(FOLLOW_array_idx_in_simple_expr418);
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
                    // 81:29: -> ^( ARRAY $v $idx)
                    {
                        // dd/grammar/ECAGrammar.g:81:32: ^( ARRAY $v $idx)
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
                    // dd/grammar/ECAGrammar.g:82:4: v= SYMBOL LPAREN RPAREN
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr439); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    LPAREN34=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr441); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN34);

                    RPAREN35=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr443); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN35);


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
                    // 82:29: -> ^( METH $v)
                    {
                        // dd/grammar/ECAGrammar.g:82:32: ^( METH $v)
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
                    // dd/grammar/ECAGrammar.g:83:4: v= SYMBOL
                    {
                    root_0 = (Object)adaptor.nil();

                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr461); if (failed) return retval;
                    if ( backtracking==0 ) {
                    v_tree = (Object)adaptor.create(v);
                    adaptor.addChild(root_0, v_tree);
                    }

                    }
                    break;
                case 5 :
                    // dd/grammar/ECAGrammar.g:84:4: v= SYMBOL LPAREN args= expr_list RPAREN
                    {
                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_simple_expr468); if (failed) return retval;
                    if ( backtracking==0 ) stream_SYMBOL.add(v);

                    LPAREN36=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr470); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN36);

                    pushFollow(FOLLOW_expr_list_in_simple_expr474);
                    args=expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr_list.add(args.getTree());
                    RPAREN37=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr476); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN37);


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
                    // 84:43: -> ^( METH $v $args)
                    {
                        // dd/grammar/ECAGrammar.g:84:46: ^( METH $v $args)
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
                    // dd/grammar/ECAGrammar.g:85:4: NUMBER
                    {
                    root_0 = (Object)adaptor.nil();

                    NUMBER38=(Token)input.LT(1);
                    match(input,NUMBER,FOLLOW_NUMBER_in_simple_expr495); if (failed) return retval;
                    if ( backtracking==0 ) {
                    NUMBER38_tree = (Object)adaptor.create(NUMBER38);
                    adaptor.addChild(root_0, NUMBER38_tree);
                    }

                    }
                    break;
                case 7 :
                    // dd/grammar/ECAGrammar.g:86:4: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING39=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_simple_expr500); if (failed) return retval;
                    if ( backtracking==0 ) {
                    STRING39_tree = (Object)adaptor.create(STRING39);
                    adaptor.addChild(root_0, STRING39_tree);
                    }

                    }
                    break;
                case 8 :
                    // dd/grammar/ECAGrammar.g:87:4: LPAREN expr RPAREN
                    {
                    LPAREN40=(Token)input.LT(1);
                    match(input,LPAREN,FOLLOW_LPAREN_in_simple_expr505); if (failed) return retval;
                    if ( backtracking==0 ) stream_LPAREN.add(LPAREN40);

                    pushFollow(FOLLOW_expr_in_simple_expr507);
                    expr41=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr41.getTree());
                    RPAREN42=(Token)input.LT(1);
                    match(input,RPAREN,FOLLOW_RPAREN_in_simple_expr509); if (failed) return retval;
                    if ( backtracking==0 ) stream_RPAREN.add(RPAREN42);


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
                    // 87:25: -> ^( expr )
                    {
                        // dd/grammar/ECAGrammar.g:87:28: ^( expr )
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
    // dd/grammar/ECAGrammar.g:90:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );
    public final expr_list_return expr_list() throws RecognitionException {
        expr_list_return retval = new expr_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPR44=null;
        expr_return expr43 = null;

        expr_list_return expr_list45 = null;

        expr_return expr46 = null;


        Object SEPR44_tree=null;
        RewriteRuleTokenStream stream_SEPR=new RewriteRuleTokenStream(adaptor,"token SEPR");
        RewriteRuleSubtreeStream stream_expr_list=new RewriteRuleSubtreeStream(adaptor,"rule expr_list");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // dd/grammar/ECAGrammar.g:91:2: ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr )
            int alt7=2;
            switch ( input.LA(1) ) {
            case DOLLARSYM:
                {
                int LA7_1 = input.LA(2);

                if ( (synpred16()) ) {
                    alt7=1;
                }
                else if ( (true) ) {
                    alt7=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("90:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 7, 1, input);

                    throw nvae;
                }
                }
                break;
            case SYMBOL:
                {
                int LA7_2 = input.LA(2);

                if ( (synpred16()) ) {
                    alt7=1;
                }
                else if ( (true) ) {
                    alt7=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("90:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 7, 2, input);

                    throw nvae;
                }
                }
                break;
            case NUMBER:
                {
                int LA7_3 = input.LA(2);

                if ( (synpred16()) ) {
                    alt7=1;
                }
                else if ( (true) ) {
                    alt7=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("90:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 7, 3, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
                {
                int LA7_4 = input.LA(2);

                if ( (synpred16()) ) {
                    alt7=1;
                }
                else if ( (true) ) {
                    alt7=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("90:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 7, 4, input);

                    throw nvae;
                }
                }
                break;
            case LPAREN:
                {
                int LA7_5 = input.LA(2);

                if ( (synpred16()) ) {
                    alt7=1;
                }
                else if ( (true) ) {
                    alt7=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("90:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 7, 5, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case TWIDDLE:
                {
                int LA7_6 = input.LA(2);

                if ( (synpred16()) ) {
                    alt7=1;
                }
                else if ( (true) ) {
                    alt7=2;
                }
                else {
                    if (backtracking>0) {failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("90:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 7, 6, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("90:1: expr_list : ( expr SEPR expr_list -> ^( SEPR expr expr_list ) | expr );", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:91:4: expr SEPR expr_list
                    {
                    pushFollow(FOLLOW_expr_in_expr_list528);
                    expr43=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr.add(expr43.getTree());
                    SEPR44=(Token)input.LT(1);
                    match(input,SEPR,FOLLOW_SEPR_in_expr_list530); if (failed) return retval;
                    if ( backtracking==0 ) stream_SEPR.add(SEPR44);

                    pushFollow(FOLLOW_expr_list_in_expr_list532);
                    expr_list45=expr_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_expr_list.add(expr_list45.getTree());

                    // AST REWRITE
                    // elements: expr, expr_list, SEPR
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    if ( backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 91:26: -> ^( SEPR expr expr_list )
                    {
                        // dd/grammar/ECAGrammar.g:91:29: ^( SEPR expr expr_list )
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
                    // dd/grammar/ECAGrammar.g:92:4: expr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_expr_list549);
                    expr46=expr();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, expr46.getTree());

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
    // dd/grammar/ECAGrammar.g:95:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );
    public final array_idx_list_return array_idx_list() throws RecognitionException {
        array_idx_list_return retval = new array_idx_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        array_idx_return array_idx47 = null;

        array_idx_list_return array_idx_list48 = null;

        array_idx_return array_idx49 = null;


        RewriteRuleSubtreeStream stream_array_idx=new RewriteRuleSubtreeStream(adaptor,"rule array_idx");
        RewriteRuleSubtreeStream stream_array_idx_list=new RewriteRuleSubtreeStream(adaptor,"rule array_idx_list");
        try {
            // dd/grammar/ECAGrammar.g:96:2: ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==LSQUARE) ) {
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
                        new NoViableAltException("95:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );", 8, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("95:1: array_idx_list : ( array_idx array_idx_list -> ^( SEPR array_idx array_idx_list ) | array_idx );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:96:4: array_idx array_idx_list
                    {
                    pushFollow(FOLLOW_array_idx_in_array_idx_list560);
                    array_idx47=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx.add(array_idx47.getTree());
                    pushFollow(FOLLOW_array_idx_list_in_array_idx_list562);
                    array_idx_list48=array_idx_list();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) stream_array_idx_list.add(array_idx_list48.getTree());

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
                    // 96:31: -> ^( SEPR array_idx array_idx_list )
                    {
                        // dd/grammar/ECAGrammar.g:96:34: ^( SEPR array_idx array_idx_list )
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
                    // dd/grammar/ECAGrammar.g:97:4: array_idx
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_array_idx_in_array_idx_list579);
                    array_idx49=array_idx();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, array_idx49.getTree());

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
    // dd/grammar/ECAGrammar.g:100:1: array_idx : LSQUARE expr RSQUARE -> ^( expr ) ;
    public final array_idx_return array_idx() throws RecognitionException {
        array_idx_return retval = new array_idx_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LSQUARE50=null;
        Token RSQUARE52=null;
        expr_return expr51 = null;


        Object LSQUARE50_tree=null;
        Object RSQUARE52_tree=null;
        RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");
        RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // dd/grammar/ECAGrammar.g:101:2: ( LSQUARE expr RSQUARE -> ^( expr ) )
            // dd/grammar/ECAGrammar.g:101:4: LSQUARE expr RSQUARE
            {
            LSQUARE50=(Token)input.LT(1);
            match(input,LSQUARE,FOLLOW_LSQUARE_in_array_idx590); if (failed) return retval;
            if ( backtracking==0 ) stream_LSQUARE.add(LSQUARE50);

            pushFollow(FOLLOW_expr_in_array_idx592);
            expr51=expr();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) stream_expr.add(expr51.getTree());
            RSQUARE52=(Token)input.LT(1);
            match(input,RSQUARE,FOLLOW_RSQUARE_in_array_idx594); if (failed) return retval;
            if ( backtracking==0 ) stream_RSQUARE.add(RSQUARE52);


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
            // 101:27: -> ^( expr )
            {
                // dd/grammar/ECAGrammar.g:101:30: ^( expr )
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
    // dd/grammar/ECAGrammar.g:104:1: infix_oper : ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper );
    public final infix_oper_return infix_oper() throws RecognitionException {
        infix_oper_return retval = new infix_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        infix_bit_oper_return infix_bit_oper53 = null;

        infix_arith_oper_return infix_arith_oper54 = null;

        infix_bool_oper_return infix_bool_oper55 = null;

        infix_cmp_oper_return infix_cmp_oper56 = null;



        try {
            // dd/grammar/ECAGrammar.g:104:12: ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper )
            int alt9=4;
            switch ( input.LA(1) ) {
            case BOR:
            case BAND:
            case BXOR:
                {
                alt9=1;
                }
                break;
            case MUL:
            case DIV:
            case PLUS:
            case MINUS:
                {
                alt9=2;
                }
                break;
            case OR:
            case AND:
                {
                alt9=3;
                }
                break;
            case EQ:
            case NEQ:
            case GT:
            case LT:
            case GEQ:
            case LEQ:
                {
                alt9=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("104:1: infix_oper : ( infix_bit_oper | infix_arith_oper | infix_bool_oper | infix_cmp_oper );", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // dd/grammar/ECAGrammar.g:104:14: infix_bit_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_bit_oper_in_infix_oper612);
                    infix_bit_oper53=infix_bit_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_bit_oper53.getTree());

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAGrammar.g:105:4: infix_arith_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_arith_oper_in_infix_oper617);
                    infix_arith_oper54=infix_arith_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_arith_oper54.getTree());

                    }
                    break;
                case 3 :
                    // dd/grammar/ECAGrammar.g:106:4: infix_bool_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_bool_oper_in_infix_oper622);
                    infix_bool_oper55=infix_bool_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_bool_oper55.getTree());

                    }
                    break;
                case 4 :
                    // dd/grammar/ECAGrammar.g:107:4: infix_cmp_oper
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_infix_cmp_oper_in_infix_oper627);
                    infix_cmp_oper56=infix_cmp_oper();
                    _fsp--;
                    if (failed) return retval;
                    if ( backtracking==0 ) adaptor.addChild(root_0, infix_cmp_oper56.getTree());

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
    // dd/grammar/ECAGrammar.g:110:1: infix_bit_oper : ( BAND | BOR | BXOR );
    public final infix_bit_oper_return infix_bit_oper() throws RecognitionException {
        infix_bit_oper_return retval = new infix_bit_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set57=null;

        Object set57_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:111:2: ( BAND | BOR | BXOR )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set57=(Token)input.LT(1);
            if ( (input.LA(1)>=BOR && input.LA(1)<=BXOR) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set57));
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
    // dd/grammar/ECAGrammar.g:116:1: infix_arith_oper : ( MUL | DIV | PLUS | MINUS );
    public final infix_arith_oper_return infix_arith_oper() throws RecognitionException {
        infix_arith_oper_return retval = new infix_arith_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set58=null;

        Object set58_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:117:2: ( MUL | DIV | PLUS | MINUS )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set58=(Token)input.LT(1);
            if ( (input.LA(1)>=MUL && input.LA(1)<=MINUS) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set58));
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
    // dd/grammar/ECAGrammar.g:123:1: infix_bool_oper : ( AND | OR );
    public final infix_bool_oper_return infix_bool_oper() throws RecognitionException {
        infix_bool_oper_return retval = new infix_bool_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set59=null;

        Object set59_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:124:2: ( AND | OR )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set59=(Token)input.LT(1);
            if ( (input.LA(1)>=OR && input.LA(1)<=AND) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set59));
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
    // dd/grammar/ECAGrammar.g:128:1: infix_cmp_oper : ( EQ | NEQ | GT | LT | GEQ | LEQ );
    public final infix_cmp_oper_return infix_cmp_oper() throws RecognitionException {
        infix_cmp_oper_return retval = new infix_cmp_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set60=null;

        Object set60_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:129:2: ( EQ | NEQ | GT | LT | GEQ | LEQ )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set60=(Token)input.LT(1);
            if ( (input.LA(1)>=EQ && input.LA(1)<=LEQ) ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set60));
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
    // dd/grammar/ECAGrammar.g:137:1: unary_oper : ( NOT | TWIDDLE );
    public final unary_oper_return unary_oper() throws RecognitionException {
        unary_oper_return retval = new unary_oper_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set61=null;

        Object set61_tree=null;

        try {
            // dd/grammar/ECAGrammar.g:137:12: ( NOT | TWIDDLE )
            // dd/grammar/ECAGrammar.g:
            {
            root_0 = (Object)adaptor.nil();

            set61=(Token)input.LT(1);
            if ( input.LA(1)==NOT||input.LA(1)==TWIDDLE ) {
                input.consume();
                if ( backtracking==0 ) adaptor.addChild(root_0, adaptor.create(set61));
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

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:40:12: ( binding SEPR bindings )
        // dd/grammar/ECAGrammar.g:40:12: binding SEPR bindings
        {
        pushFollow(FOLLOW_binding_in_synpred3162);
        binding();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred3164); if (failed) return ;
        pushFollow(FOLLOW_bindings_in_synpred3166);
        bindings();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:67:4: ( action_expr SEPR action_expr_list )
        // dd/grammar/ECAGrammar.g:67:4: action_expr SEPR action_expr_list
        {
        pushFollow(FOLLOW_action_expr_in_synpred5281);
        action_expr();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred5283); if (failed) return ;
        pushFollow(FOLLOW_action_expr_list_in_synpred5285);
        action_expr_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:74:8: ( simple_expr infix_oper expr )
        // dd/grammar/ECAGrammar.g:74:8: simple_expr infix_oper expr
        {
        pushFollow(FOLLOW_simple_expr_in_synpred6320);
        simple_expr();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_infix_oper_in_synpred6322);
        infix_oper();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_expr_in_synpred6324);
        expr();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred7
    public final void synpred7_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:75:4: ( simple_expr )
        // dd/grammar/ECAGrammar.g:75:4: simple_expr
        {
        pushFollow(FOLLOW_simple_expr_in_synpred7342);
        simple_expr();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred7

    // $ANTLR start synpred16
    public final void synpred16_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:91:4: ( expr SEPR expr_list )
        // dd/grammar/ECAGrammar.g:91:4: expr SEPR expr_list
        {
        pushFollow(FOLLOW_expr_in_synpred16528);
        expr();
        _fsp--;
        if (failed) return ;
        match(input,SEPR,FOLLOW_SEPR_in_synpred16530); if (failed) return ;
        pushFollow(FOLLOW_expr_list_in_synpred16532);
        expr_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred16

    // $ANTLR start synpred17
    public final void synpred17_fragment() throws RecognitionException {   
        // dd/grammar/ECAGrammar.g:96:4: ( array_idx array_idx_list )
        // dd/grammar/ECAGrammar.g:96:4: array_idx array_idx_list
        {
        pushFollow(FOLLOW_array_idx_in_synpred17560);
        array_idx();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_array_idx_list_in_synpred17562);
        array_idx_list();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred17

    public final boolean synpred16() {
        backtracking++;
        int start = input.mark();
        try {
            synpred16_fragment(); // can never throw exception
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


 

    public static final BitSet FOLLOW_eca_in_eca_rule86 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_eca_rule88 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_eca97 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_event_in_eca99 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_eca112 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_condition_in_eca114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_eca128 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_action_in_eca130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bindings_in_event150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_bindings162 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_bindings164 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_bindings_in_bindings166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_bindings181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_sym_in_binding191 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_binding193 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_binding195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym218 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_COLON_in_bind_sym220 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_bind_sym242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_condition256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_list_in_action270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_action_expr_list281 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_action_expr_list283 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_action_expr_list_in_action_expr_list285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_action_expr_list300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_action_expr310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr320 = new BitSet(new long[]{0x000003DFF6000000L});
    public static final BitSet FOLLOW_infix_oper_in_expr322 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_expr324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_oper_in_expr347 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_expr349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_expr368 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_TERN_IF_in_expr370 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_expr374 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_COLON_in_expr376 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_expr380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLARSYM_in_simple_expr407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr414 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_array_idx_in_simple_expr418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr439 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr441 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_simple_expr468 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr470 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_list_in_simple_expr474 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_simple_expr495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_simple_expr500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_simple_expr505 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_simple_expr507 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RPAREN_in_simple_expr509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_expr_list528 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_expr_list530 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_list_in_expr_list532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_expr_list549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_array_idx_list560 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_array_idx_list_in_array_idx_list562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_array_idx_list579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LSQUARE_in_array_idx590 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_array_idx592 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_RSQUARE_in_array_idx594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_bit_oper_in_infix_oper612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_arith_oper_in_infix_oper617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_bool_oper_in_infix_oper622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_infix_cmp_oper_in_infix_oper627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_bit_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_arith_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_bool_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_infix_cmp_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_unary_oper0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binding_in_synpred3162 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_synpred3164 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_bindings_in_synpred3166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_expr_in_synpred5281 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_synpred5283 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_action_expr_list_in_synpred5285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_synpred6320 = new BitSet(new long[]{0x000003DFF6000000L});
    public static final BitSet FOLLOW_infix_oper_in_synpred6322 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_in_synpred6324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_synpred7342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_synpred16528 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_SEPR_in_synpred16530 = new BitSet(new long[]{0x0510002008011000L});
    public static final BitSet FOLLOW_expr_list_in_synpred16532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_idx_in_synpred17560 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_array_idx_list_in_synpred17562 = new BitSet(new long[]{0x0000000000000002L});

}