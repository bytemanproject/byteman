// $ANTLR 3.0.1 dd/grammar/ECAToken.g 2008-09-25 12:22:32

package org.jboss.jbossts.orchestration.rule.grammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ECATokenLexer extends Lexer {
    public static final int MINUS=50;
    public static final int NUMBER=12;
    public static final int METHOD=18;
    public static final int FLOAT=11;
    public static final int FALSE=23;
    public static final int POSDIGIT=5;
    public static final int TWIDDLE=46;
    public static final int LEQ=42;
    public static final int RULE=16;
    public static final int MOD=51;
    public static final int GEQ=41;
    public static final int DQUOTE=57;
    public static final int OR=34;
    public static final int BOR=43;
    public static final int BAREINT=7;
    public static final int LBRACE=29;
    public static final int NEWLINE=59;
    public static final int DOT=32;
    public static final int RBRACE=30;
    public static final int INTEGER=8;
    public static final int AND=35;
    public static final int ASSIGN=33;
    public static final int SYMBOL=65;
    public static final int RPAREN=26;
    public static final int SIGN=6;
    public static final int LPAREN=25;
    public static final int PLUS=49;
    public static final int DIGIT=4;
    public static final int LINE=19;
    public static final int BAND=44;
    public static final int NEQ=38;
    public static final int SPACE=58;
    public static final int LETTER=54;
    public static final int LSQUARE=27;
    public static final int DO=15;
    public static final int POINT=9;
    public static final int BARESYM=62;
    public static final int NOTHING=21;
    public static final int SEPR=31;
    public static final int WS=68;
    public static final int STRING=61;
    public static final int EQ=37;
    public static final int QUOTSYM=63;
    public static final int LT=40;
    public static final int GT=39;
    public static final int DOLLAR=66;
    public static final int RSQUARE=28;
    public static final int QUOTE=56;
    public static final int TERN_IF=52;
    public static final int MUL=47;
    public static final int CLASS=17;
    public static final int EXPPART=10;
    public static final int PUNCT=60;
    public static final int RETURN=24;
    public static final int IF=14;
    public static final int EOF=-1;
    public static final int Tokens=69;
    public static final int COLON=53;
    public static final int DIV=48;
    public static final int DOTSYM=64;
    public static final int BXOR=45;
    public static final int ENDRULE=20;
    public static final int BIND=13;
    public static final int NOT=36;
    public static final int TRUE=22;
    public static final int UNDERSCORE=55;
    public static final int DOLLARSYM=67;
    public ECATokenLexer() {;} 
    public ECATokenLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "dd/grammar/ECAToken.g"; }

    // $ANTLR start DIGIT
    public final void mDIGIT() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:10:7: ( '0' .. '9' )
            // dd/grammar/ECAToken.g:10:9: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end DIGIT

    // $ANTLR start POSDIGIT
    public final void mPOSDIGIT() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:14:10: ( '1' .. '9' )
            // dd/grammar/ECAToken.g:14:12: '1' .. '9'
            {
            matchRange('1','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end POSDIGIT

    // $ANTLR start SIGN
    public final void mSIGN() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:18:6: ( '+' | '-' )
            // dd/grammar/ECAToken.g:
            {
            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end SIGN

    // $ANTLR start BAREINT
    public final void mBAREINT() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:22:9: ( '0' | ( POSDIGIT ( DIGIT )* ) )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='0') ) {
                alt2=1;
            }
            else if ( ((LA2_0>='1' && LA2_0<='9')) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("21:1: fragment BAREINT : ( '0' | ( POSDIGIT ( DIGIT )* ) );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // dd/grammar/ECAToken.g:22:11: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:22:17: ( POSDIGIT ( DIGIT )* )
                    {
                    // dd/grammar/ECAToken.g:22:17: ( POSDIGIT ( DIGIT )* )
                    // dd/grammar/ECAToken.g:22:18: POSDIGIT ( DIGIT )*
                    {
                    mPOSDIGIT(); 
                    // dd/grammar/ECAToken.g:22:27: ( DIGIT )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // dd/grammar/ECAToken.g:22:28: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);


                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end BAREINT

    // $ANTLR start INTEGER
    public final void mINTEGER() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:25:9: ( ( SIGN )? BAREINT )
            // dd/grammar/ECAToken.g:25:11: ( SIGN )? BAREINT
            {
            // dd/grammar/ECAToken.g:25:11: ( SIGN )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='+'||LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // dd/grammar/ECAToken.g:25:11: SIGN
                    {
                    mSIGN(); 

                    }
                    break;

            }

            mBAREINT(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end INTEGER

    // $ANTLR start POINT
    public final void mPOINT() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:29:7: ( '.' )
            // dd/grammar/ECAToken.g:29:9: '.'
            {
            match('.'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end POINT

    // $ANTLR start EXPPART
    public final void mEXPPART() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:33:9: ( ( 'e' | 'E' ) INTEGER )
            // dd/grammar/ECAToken.g:33:12: ( 'e' | 'E' ) INTEGER
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            mINTEGER(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end EXPPART

    // $ANTLR start FLOAT
    public final void mFLOAT() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:38:7: ( INTEGER POINT ( BAREINT )? ( EXPPART )? )
            // dd/grammar/ECAToken.g:38:9: INTEGER POINT ( BAREINT )? ( EXPPART )?
            {
            mINTEGER(); 
            mPOINT(); 
            // dd/grammar/ECAToken.g:38:23: ( BAREINT )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // dd/grammar/ECAToken.g:38:23: BAREINT
                    {
                    mBAREINT(); 

                    }
                    break;

            }

            // dd/grammar/ECAToken.g:38:32: ( EXPPART )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='E'||LA5_0=='e') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // dd/grammar/ECAToken.g:38:32: EXPPART
                    {
                    mEXPPART(); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end FLOAT

    // $ANTLR start NUMBER
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            // dd/grammar/ECAToken.g:41:8: ( INTEGER | FLOAT )
            int alt6=2;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // dd/grammar/ECAToken.g:41:10: INTEGER
                    {
                    mINTEGER(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:41:20: FLOAT
                    {
                    mFLOAT(); 

                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NUMBER

    // $ANTLR start BIND
    public final void mBIND() throws RecognitionException {
        try {
            int _type = BIND;
            // dd/grammar/ECAToken.g:46:6: ( 'BIND' )
            // dd/grammar/ECAToken.g:46:8: 'BIND'
            {
            match("BIND"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BIND

    // $ANTLR start IF
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            // dd/grammar/ECAToken.g:49:4: ( 'IF' )
            // dd/grammar/ECAToken.g:49:6: 'IF'
            {
            match("IF"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IF

    // $ANTLR start DO
    public final void mDO() throws RecognitionException {
        try {
            int _type = DO;
            // dd/grammar/ECAToken.g:52:4: ( 'DO' )
            // dd/grammar/ECAToken.g:52:6: 'DO'
            {
            match("DO"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DO

    // $ANTLR start RULE
    public final void mRULE() throws RecognitionException {
        try {
            int _type = RULE;
            // dd/grammar/ECAToken.g:55:6: ( 'RULE' )
            // dd/grammar/ECAToken.g:55:8: 'RULE'
            {
            match("RULE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE

    // $ANTLR start CLASS
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            // dd/grammar/ECAToken.g:58:7: ( 'CLASS' )
            // dd/grammar/ECAToken.g:58:9: 'CLASS'
            {
            match("CLASS"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CLASS

    // $ANTLR start METHOD
    public final void mMETHOD() throws RecognitionException {
        try {
            int _type = METHOD;
            // dd/grammar/ECAToken.g:61:8: ( 'METHOD' )
            // dd/grammar/ECAToken.g:61:10: 'METHOD'
            {
            match("METHOD"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end METHOD

    // $ANTLR start LINE
    public final void mLINE() throws RecognitionException {
        try {
            int _type = LINE;
            // dd/grammar/ECAToken.g:64:6: ( 'LINE' )
            // dd/grammar/ECAToken.g:64:8: 'LINE'
            {
            match("LINE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LINE

    // $ANTLR start ENDRULE
    public final void mENDRULE() throws RecognitionException {
        try {
            int _type = ENDRULE;
            // dd/grammar/ECAToken.g:67:9: ( 'ENDRULE' )
            // dd/grammar/ECAToken.g:67:11: 'ENDRULE'
            {
            match("ENDRULE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ENDRULE

    // $ANTLR start NOTHING
    public final void mNOTHING() throws RecognitionException {
        try {
            int _type = NOTHING;
            // dd/grammar/ECAToken.g:70:9: ( 'NOTHING' )
            // dd/grammar/ECAToken.g:70:11: 'NOTHING'
            {
            match("NOTHING"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOTHING

    // $ANTLR start TRUE
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            // dd/grammar/ECAToken.g:73:6: ( 'TRUE' | 'true' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='T') ) {
                alt7=1;
            }
            else if ( (LA7_0=='t') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("73:1: TRUE : ( 'TRUE' | 'true' );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // dd/grammar/ECAToken.g:73:9: 'TRUE'
                    {
                    match("TRUE"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:73:18: 'true'
                    {
                    match("true"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRUE

    // $ANTLR start FALSE
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            // dd/grammar/ECAToken.g:76:7: ( 'FALSE' | 'false' )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0=='F') ) {
                alt8=1;
            }
            else if ( (LA8_0=='f') ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("76:1: FALSE : ( 'FALSE' | 'false' );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // dd/grammar/ECAToken.g:76:9: 'FALSE'
                    {
                    match("FALSE"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:76:17: 'false'
                    {
                    match("false"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FALSE

    // $ANTLR start RETURN
    public final void mRETURN() throws RecognitionException {
        try {
            int _type = RETURN;
            // dd/grammar/ECAToken.g:79:8: ( 'RETURN' | 'return' )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='R') ) {
                alt9=1;
            }
            else if ( (LA9_0=='r') ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("79:1: RETURN : ( 'RETURN' | 'return' );", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // dd/grammar/ECAToken.g:79:10: 'RETURN'
                    {
                    match("RETURN"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:79:19: 'return'
                    {
                    match("return"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RETURN

    // $ANTLR start LPAREN
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            // dd/grammar/ECAToken.g:84:8: ( '(' )
            // dd/grammar/ECAToken.g:84:10: '('
            {
            match('('); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LPAREN

    // $ANTLR start RPAREN
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            // dd/grammar/ECAToken.g:87:8: ( ')' )
            // dd/grammar/ECAToken.g:87:10: ')'
            {
            match(')'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RPAREN

    // $ANTLR start LSQUARE
    public final void mLSQUARE() throws RecognitionException {
        try {
            int _type = LSQUARE;
            // dd/grammar/ECAToken.g:90:9: ( '\\[' )
            // dd/grammar/ECAToken.g:90:11: '\\['
            {
            match('['); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LSQUARE

    // $ANTLR start RSQUARE
    public final void mRSQUARE() throws RecognitionException {
        try {
            int _type = RSQUARE;
            // dd/grammar/ECAToken.g:93:9: ( '\\]' )
            // dd/grammar/ECAToken.g:93:11: '\\]'
            {
            match(']'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RSQUARE

    // $ANTLR start LBRACE
    public final void mLBRACE() throws RecognitionException {
        try {
            int _type = LBRACE;
            // dd/grammar/ECAToken.g:96:8: ( '{' )
            // dd/grammar/ECAToken.g:96:10: '{'
            {
            match('{'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LBRACE

    // $ANTLR start RBRACE
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            // dd/grammar/ECAToken.g:99:8: ( '}' )
            // dd/grammar/ECAToken.g:99:10: '}'
            {
            match('}'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RBRACE

    // $ANTLR start SEPR
    public final void mSEPR() throws RecognitionException {
        try {
            int _type = SEPR;
            // dd/grammar/ECAToken.g:104:6: ( ';' | ',' )
            // dd/grammar/ECAToken.g:
            {
            if ( input.LA(1)==','||input.LA(1)==';' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SEPR

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // dd/grammar/ECAToken.g:110:5: ( '.' )
            // dd/grammar/ECAToken.g:110:7: '.'
            {
            match('.'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start ASSIGN
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            // dd/grammar/ECAToken.g:115:8: ( '=' | '<--' )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='=') ) {
                alt10=1;
            }
            else if ( (LA10_0=='<') ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("115:1: ASSIGN : ( '=' | '<--' );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // dd/grammar/ECAToken.g:115:10: '='
                    {
                    match('='); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:116:4: '<--'
                    {
                    match("<--"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ASSIGN

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            // dd/grammar/ECAToken.g:121:4: ( '||' | 'OR' | 'or' )
            int alt11=3;
            switch ( input.LA(1) ) {
            case '|':
                {
                alt11=1;
                }
                break;
            case 'O':
                {
                alt11=2;
                }
                break;
            case 'o':
                {
                alt11=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("121:1: OR : ( '||' | 'OR' | 'or' );", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // dd/grammar/ECAToken.g:121:6: '||'
                    {
                    match("||"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:122:4: 'OR'
                    {
                    match("OR"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:123:4: 'or'
                    {
                    match("or"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OR

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            // dd/grammar/ECAToken.g:126:5: ( '&&' | 'AND' | 'and' )
            int alt12=3;
            switch ( input.LA(1) ) {
            case '&':
                {
                alt12=1;
                }
                break;
            case 'A':
                {
                alt12=2;
                }
                break;
            case 'a':
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("126:1: AND : ( '&&' | 'AND' | 'and' );", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // dd/grammar/ECAToken.g:126:7: '&&'
                    {
                    match("&&"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:127:4: 'AND'
                    {
                    match("AND"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:128:4: 'and'
                    {
                    match("and"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            // dd/grammar/ECAToken.g:131:5: ( '!' | 'NOT' | 'not' )
            int alt13=3;
            switch ( input.LA(1) ) {
            case '!':
                {
                alt13=1;
                }
                break;
            case 'N':
                {
                alt13=2;
                }
                break;
            case 'n':
                {
                alt13=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("131:1: NOT : ( '!' | 'NOT' | 'not' );", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // dd/grammar/ECAToken.g:131:7: '!'
                    {
                    match('!'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:132:4: 'NOT'
                    {
                    match("NOT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:133:4: 'not'
                    {
                    match("not"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start EQ
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            // dd/grammar/ECAToken.g:138:4: ( '==' | 'EQ' | 'eq' )
            int alt14=3;
            switch ( input.LA(1) ) {
            case '=':
                {
                alt14=1;
                }
                break;
            case 'E':
                {
                alt14=2;
                }
                break;
            case 'e':
                {
                alt14=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("138:1: EQ : ( '==' | 'EQ' | 'eq' );", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // dd/grammar/ECAToken.g:138:6: '=='
                    {
                    match("=="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:139:4: 'EQ'
                    {
                    match("EQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:140:4: 'eq'
                    {
                    match("eq"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQ

    // $ANTLR start NEQ
    public final void mNEQ() throws RecognitionException {
        try {
            int _type = NEQ;
            // dd/grammar/ECAToken.g:143:5: ( '!=' | 'NEQ' | 'neq' )
            int alt15=3;
            switch ( input.LA(1) ) {
            case '!':
                {
                alt15=1;
                }
                break;
            case 'N':
                {
                alt15=2;
                }
                break;
            case 'n':
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("143:1: NEQ : ( '!=' | 'NEQ' | 'neq' );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // dd/grammar/ECAToken.g:143:7: '!='
                    {
                    match("!="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:144:4: 'NEQ'
                    {
                    match("NEQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:145:4: 'neq'
                    {
                    match("neq"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NEQ

    // $ANTLR start GT
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            // dd/grammar/ECAToken.g:148:4: ( '>' | 'GT' | 'gt' )
            int alt16=3;
            switch ( input.LA(1) ) {
            case '>':
                {
                alt16=1;
                }
                break;
            case 'G':
                {
                alt16=2;
                }
                break;
            case 'g':
                {
                alt16=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("148:1: GT : ( '>' | 'GT' | 'gt' );", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // dd/grammar/ECAToken.g:148:6: '>'
                    {
                    match('>'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:149:4: 'GT'
                    {
                    match("GT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:150:4: 'gt'
                    {
                    match("gt"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GT

    // $ANTLR start LT
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            // dd/grammar/ECAToken.g:153:4: ( '<' | 'LT' | 'lt' )
            int alt17=3;
            switch ( input.LA(1) ) {
            case '<':
                {
                alt17=1;
                }
                break;
            case 'L':
                {
                alt17=2;
                }
                break;
            case 'l':
                {
                alt17=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("153:1: LT : ( '<' | 'LT' | 'lt' );", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // dd/grammar/ECAToken.g:153:6: '<'
                    {
                    match('<'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:154:4: 'LT'
                    {
                    match("LT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:155:4: 'lt'
                    {
                    match("lt"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LT

    // $ANTLR start GEQ
    public final void mGEQ() throws RecognitionException {
        try {
            int _type = GEQ;
            // dd/grammar/ECAToken.g:158:5: ( '>=' | 'EQ' | 'geq' )
            int alt18=3;
            switch ( input.LA(1) ) {
            case '>':
                {
                alt18=1;
                }
                break;
            case 'E':
                {
                alt18=2;
                }
                break;
            case 'g':
                {
                alt18=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("158:1: GEQ : ( '>=' | 'EQ' | 'geq' );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // dd/grammar/ECAToken.g:158:7: '>='
                    {
                    match(">="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:159:4: 'EQ'
                    {
                    match("EQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:160:4: 'geq'
                    {
                    match("geq"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GEQ

    // $ANTLR start LEQ
    public final void mLEQ() throws RecognitionException {
        try {
            int _type = LEQ;
            // dd/grammar/ECAToken.g:163:5: ( '<=' | 'LEQ' | 'leq' )
            int alt19=3;
            switch ( input.LA(1) ) {
            case '<':
                {
                alt19=1;
                }
                break;
            case 'L':
                {
                alt19=2;
                }
                break;
            case 'l':
                {
                alt19=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("163:1: LEQ : ( '<=' | 'LEQ' | 'leq' );", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // dd/grammar/ECAToken.g:163:7: '<='
                    {
                    match("<="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:164:4: 'LEQ'
                    {
                    match("LEQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:165:4: 'leq'
                    {
                    match("leq"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEQ

    // $ANTLR start BOR
    public final void mBOR() throws RecognitionException {
        try {
            int _type = BOR;
            // dd/grammar/ECAToken.g:170:5: ( '|' )
            // dd/grammar/ECAToken.g:170:7: '|'
            {
            match('|'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BOR

    // $ANTLR start BAND
    public final void mBAND() throws RecognitionException {
        try {
            int _type = BAND;
            // dd/grammar/ECAToken.g:173:6: ( '&' )
            // dd/grammar/ECAToken.g:173:8: '&'
            {
            match('&'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BAND

    // $ANTLR start BXOR
    public final void mBXOR() throws RecognitionException {
        try {
            int _type = BXOR;
            // dd/grammar/ECAToken.g:176:6: ( '^' )
            // dd/grammar/ECAToken.g:176:8: '^'
            {
            match('^'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BXOR

    // $ANTLR start TWIDDLE
    public final void mTWIDDLE() throws RecognitionException {
        try {
            int _type = TWIDDLE;
            // dd/grammar/ECAToken.g:179:9: ( '~' )
            // dd/grammar/ECAToken.g:179:11: '~'
            {
            match('~'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TWIDDLE

    // $ANTLR start MUL
    public final void mMUL() throws RecognitionException {
        try {
            int _type = MUL;
            // dd/grammar/ECAToken.g:184:5: ( '*' | 'TIMES' | 'times' )
            int alt20=3;
            switch ( input.LA(1) ) {
            case '*':
                {
                alt20=1;
                }
                break;
            case 'T':
                {
                alt20=2;
                }
                break;
            case 't':
                {
                alt20=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("184:1: MUL : ( '*' | 'TIMES' | 'times' );", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // dd/grammar/ECAToken.g:184:7: '*'
                    {
                    match('*'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:185:4: 'TIMES'
                    {
                    match("TIMES"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:186:4: 'times'
                    {
                    match("times"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MUL

    // $ANTLR start DIV
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            // dd/grammar/ECAToken.g:189:5: ( '/' | 'DIVIDE' | 'divide' )
            int alt21=3;
            switch ( input.LA(1) ) {
            case '/':
                {
                alt21=1;
                }
                break;
            case 'D':
                {
                alt21=2;
                }
                break;
            case 'd':
                {
                alt21=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("189:1: DIV : ( '/' | 'DIVIDE' | 'divide' );", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // dd/grammar/ECAToken.g:189:7: '/'
                    {
                    match('/'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:190:4: 'DIVIDE'
                    {
                    match("DIVIDE"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:191:4: 'divide'
                    {
                    match("divide"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DIV

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            // dd/grammar/ECAToken.g:194:6: ( '+' | 'PLUS' | 'plus' )
            int alt22=3;
            switch ( input.LA(1) ) {
            case '+':
                {
                alt22=1;
                }
                break;
            case 'P':
                {
                alt22=2;
                }
                break;
            case 'p':
                {
                alt22=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("194:1: PLUS : ( '+' | 'PLUS' | 'plus' );", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // dd/grammar/ECAToken.g:194:8: '+'
                    {
                    match('+'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:195:4: 'PLUS'
                    {
                    match("PLUS"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:196:4: 'plus'
                    {
                    match("plus"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start MINUS
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            // dd/grammar/ECAToken.g:199:7: ( '-' | 'MINUS' | 'minus' )
            int alt23=3;
            switch ( input.LA(1) ) {
            case '-':
                {
                alt23=1;
                }
                break;
            case 'M':
                {
                alt23=2;
                }
                break;
            case 'm':
                {
                alt23=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("199:1: MINUS : ( '-' | 'MINUS' | 'minus' );", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // dd/grammar/ECAToken.g:199:9: '-'
                    {
                    match('-'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:200:4: 'MINUS'
                    {
                    match("MINUS"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:201:4: 'minus'
                    {
                    match("minus"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MINUS

    // $ANTLR start MOD
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            // dd/grammar/ECAToken.g:204:5: ( '%' | 'MOD' | 'mod' )
            int alt24=3;
            switch ( input.LA(1) ) {
            case '%':
                {
                alt24=1;
                }
                break;
            case 'M':
                {
                alt24=2;
                }
                break;
            case 'm':
                {
                alt24=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("204:1: MOD : ( '%' | 'MOD' | 'mod' );", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // dd/grammar/ECAToken.g:204:7: '%'
                    {
                    match('%'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:205:4: 'MOD'
                    {
                    match("MOD"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:206:4: 'mod'
                    {
                    match("mod"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MOD

    // $ANTLR start TERN_IF
    public final void mTERN_IF() throws RecognitionException {
        try {
            int _type = TERN_IF;
            // dd/grammar/ECAToken.g:211:9: ( '?' )
            // dd/grammar/ECAToken.g:211:11: '?'
            {
            match('?'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TERN_IF

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            // dd/grammar/ECAToken.g:214:7: ( ':' )
            // dd/grammar/ECAToken.g:214:9: ':'
            {
            match(':'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start LETTER
    public final void mLETTER() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:220:8: ( 'a' .. 'z' | 'A' .. 'Z' )
            // dd/grammar/ECAToken.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end LETTER

    // $ANTLR start UNDERSCORE
    public final void mUNDERSCORE() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:224:12: ( '_' )
            // dd/grammar/ECAToken.g:224:14: '_'
            {
            match('_'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end UNDERSCORE

    // $ANTLR start QUOTE
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            // dd/grammar/ECAToken.g:227:7: ( '\\'' )
            // dd/grammar/ECAToken.g:227:9: '\\''
            {
            match('\''); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end QUOTE

    // $ANTLR start DQUOTE
    public final void mDQUOTE() throws RecognitionException {
        try {
            int _type = DQUOTE;
            // dd/grammar/ECAToken.g:230:8: ( '\"' )
            // dd/grammar/ECAToken.g:230:10: '\"'
            {
            match('\"'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DQUOTE

    // $ANTLR start SPACE
    public final void mSPACE() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:234:7: ( ' ' | '\\t' | '\\r' )
            // dd/grammar/ECAToken.g:
            {
            if ( input.LA(1)=='\t'||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end SPACE

    // $ANTLR start NEWLINE
    public final void mNEWLINE() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:238:9: ( '\\n' )
            // dd/grammar/ECAToken.g:238:11: '\\n'
            {
            match('\n'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end NEWLINE

    // $ANTLR start PUNCT
    public final void mPUNCT() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:242:7: ( '!' | '$' | '%' | '^' | '&' | '*' | '(' | ')' | '-' | '+' | '=' | '{' | '}' | '[' | ']' | ':' | ';' | '@' | '~' | '#' | '|' | '\\\\' | '`' | ',' | '<' | '.' | '>' | '/' | '?' )
            // dd/grammar/ECAToken.g:
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='/')||(input.LA(1)>=':' && input.LA(1)<='@')||(input.LA(1)>='[' && input.LA(1)<='^')||input.LA(1)=='`'||(input.LA(1)>='{' && input.LA(1)<='~') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end PUNCT

    // $ANTLR start STRING
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            // dd/grammar/ECAToken.g:245:8: ( DQUOTE ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )* DQUOTE )
            // dd/grammar/ECAToken.g:245:10: DQUOTE ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )* DQUOTE
            {
            mDQUOTE(); 
            // dd/grammar/ECAToken.g:245:17: ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0=='\t'||LA25_0=='\r'||(LA25_0>=' ' && LA25_0<='!')||(LA25_0>='#' && LA25_0<='~')) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // dd/grammar/ECAToken.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)=='\r'||(input.LA(1)>=' ' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='~') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            mDQUOTE(); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRING

    // $ANTLR start BARESYM
    public final void mBARESYM() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:249:9: ( ( LETTER | UNDERSCORE ) ( LETTER | DIGIT | UNDERSCORE )* )
            // dd/grammar/ECAToken.g:249:11: ( LETTER | UNDERSCORE ) ( LETTER | DIGIT | UNDERSCORE )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // dd/grammar/ECAToken.g:249:33: ( LETTER | DIGIT | UNDERSCORE )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( ((LA26_0>='0' && LA26_0<='9')||(LA26_0>='A' && LA26_0<='Z')||LA26_0=='_'||(LA26_0>='a' && LA26_0<='z')) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // dd/grammar/ECAToken.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end BARESYM

    // $ANTLR start QUOTSYM
    public final void mQUOTSYM() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:252:9: ( QUOTE ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE | SPACE )* QUOTE )
            // dd/grammar/ECAToken.g:252:11: QUOTE ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE | SPACE )* QUOTE
            {
            mQUOTE(); 
            // dd/grammar/ECAToken.g:252:17: ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE | SPACE )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0=='\t'||LA27_0=='\r'||(LA27_0>=' ' && LA27_0<='&')||(LA27_0>='(' && LA27_0<='~')) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // dd/grammar/ECAToken.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)=='\r'||(input.LA(1)>=' ' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='~') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

            mQUOTE(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end QUOTSYM

    // $ANTLR start DOTSYM
    public final void mDOTSYM() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:257:8: ( BARESYM DOT DOTSYM | BARESYM )
            int alt28=2;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // dd/grammar/ECAToken.g:257:10: BARESYM DOT DOTSYM
                    {
                    mBARESYM(); 
                    mDOT(); 
                    mDOTSYM(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:258:4: BARESYM
                    {
                    mBARESYM(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end DOTSYM

    // $ANTLR start SYMBOL
    public final void mSYMBOL() throws RecognitionException {
        try {
            int _type = SYMBOL;
            // dd/grammar/ECAToken.g:261:8: ( DOTSYM | QUOTSYM )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( ((LA29_0>='A' && LA29_0<='Z')||LA29_0=='_'||(LA29_0>='a' && LA29_0<='z')) ) {
                alt29=1;
            }
            else if ( (LA29_0=='\'') ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("261:1: SYMBOL : ( DOTSYM | QUOTSYM );", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // dd/grammar/ECAToken.g:261:10: DOTSYM
                    {
                    mDOTSYM(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:262:4: QUOTSYM
                    {
                    mQUOTSYM(); 

                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SYMBOL

    // $ANTLR start DOLLAR
    public final void mDOLLAR() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:267:8: ( '$' )
            // dd/grammar/ECAToken.g:267:10: '$'
            {
            match('$'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end DOLLAR

    // $ANTLR start DOLLARSYM
    public final void mDOLLARSYM() throws RecognitionException {
        try {
            int _type = DOLLARSYM;
            // dd/grammar/ECAToken.g:272:11: ( DOLLAR ( BAREINT | BARESYM ) )
            // dd/grammar/ECAToken.g:272:13: DOLLAR ( BAREINT | BARESYM )
            {
            mDOLLAR(); 
            // dd/grammar/ECAToken.g:272:20: ( BAREINT | BARESYM )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( ((LA30_0>='0' && LA30_0<='9')) ) {
                alt30=1;
            }
            else if ( ((LA30_0>='A' && LA30_0<='Z')||LA30_0=='_'||(LA30_0>='a' && LA30_0<='z')) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("272:20: ( BAREINT | BARESYM )", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // dd/grammar/ECAToken.g:272:21: BAREINT
                    {
                    mBAREINT(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:272:31: BARESYM
                    {
                    mBARESYM(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOLLARSYM

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // dd/grammar/ECAToken.g:278:4: ( ( SPACE | NEWLINE ) )
            // dd/grammar/ECAToken.g:278:6: ( SPACE | NEWLINE )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

             skip(); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    public void mTokens() throws RecognitionException {
        // dd/grammar/ECAToken.g:1:8: ( NUMBER | BIND | IF | DO | RULE | CLASS | METHOD | LINE | ENDRULE | NOTHING | TRUE | FALSE | RETURN | LPAREN | RPAREN | LSQUARE | RSQUARE | LBRACE | RBRACE | SEPR | DOT | ASSIGN | OR | AND | NOT | EQ | NEQ | GT | LT | GEQ | LEQ | BOR | BAND | BXOR | TWIDDLE | MUL | DIV | PLUS | MINUS | MOD | TERN_IF | COLON | QUOTE | DQUOTE | STRING | SYMBOL | DOLLARSYM | WS )
        int alt31=48;
        switch ( input.LA(1) ) {
        case '+':
            {
            int LA31_1 = input.LA(2);

            if ( ((LA31_1>='0' && LA31_1<='9')) ) {
                alt31=1;
            }
            else {
                alt31=38;}
            }
            break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            {
            alt31=1;
            }
            break;
        case 'B':
            {
            int LA31_3 = input.LA(2);

            if ( (LA31_3=='I') ) {
                int LA31_58 = input.LA(3);

                if ( (LA31_58=='N') ) {
                    int LA31_115 = input.LA(4);

                    if ( (LA31_115=='D') ) {
                        int LA31_148 = input.LA(5);

                        if ( (LA31_148=='.'||(LA31_148>='0' && LA31_148<='9')||(LA31_148>='A' && LA31_148<='Z')||LA31_148=='_'||(LA31_148>='a' && LA31_148<='z')) ) {
                            alt31=46;
                        }
                        else {
                            alt31=2;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'I':
            {
            int LA31_4 = input.LA(2);

            if ( (LA31_4=='F') ) {
                int LA31_59 = input.LA(3);

                if ( (LA31_59=='.'||(LA31_59>='0' && LA31_59<='9')||(LA31_59>='A' && LA31_59<='Z')||LA31_59=='_'||(LA31_59>='a' && LA31_59<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=3;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'D':
            {
            switch ( input.LA(2) ) {
            case 'O':
                {
                int LA31_60 = input.LA(3);

                if ( (LA31_60=='.'||(LA31_60>='0' && LA31_60<='9')||(LA31_60>='A' && LA31_60<='Z')||LA31_60=='_'||(LA31_60>='a' && LA31_60<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=4;}
                }
                break;
            case 'I':
                {
                int LA31_61 = input.LA(3);

                if ( (LA31_61=='V') ) {
                    int LA31_118 = input.LA(4);

                    if ( (LA31_118=='I') ) {
                        int LA31_149 = input.LA(5);

                        if ( (LA31_149=='D') ) {
                            int LA31_170 = input.LA(6);

                            if ( (LA31_170=='E') ) {
                                int LA31_187 = input.LA(7);

                                if ( (LA31_187=='.'||(LA31_187>='0' && LA31_187<='9')||(LA31_187>='A' && LA31_187<='Z')||LA31_187=='_'||(LA31_187>='a' && LA31_187<='z')) ) {
                                    alt31=46;
                                }
                                else {
                                    alt31=37;}
                            }
                            else {
                                alt31=46;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 'R':
            {
            switch ( input.LA(2) ) {
            case 'U':
                {
                int LA31_62 = input.LA(3);

                if ( (LA31_62=='L') ) {
                    int LA31_119 = input.LA(4);

                    if ( (LA31_119=='E') ) {
                        int LA31_150 = input.LA(5);

                        if ( (LA31_150=='.'||(LA31_150>='0' && LA31_150<='9')||(LA31_150>='A' && LA31_150<='Z')||LA31_150=='_'||(LA31_150>='a' && LA31_150<='z')) ) {
                            alt31=46;
                        }
                        else {
                            alt31=5;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 'E':
                {
                int LA31_63 = input.LA(3);

                if ( (LA31_63=='T') ) {
                    int LA31_120 = input.LA(4);

                    if ( (LA31_120=='U') ) {
                        int LA31_151 = input.LA(5);

                        if ( (LA31_151=='R') ) {
                            int LA31_172 = input.LA(6);

                            if ( (LA31_172=='N') ) {
                                int LA31_188 = input.LA(7);

                                if ( (LA31_188=='.'||(LA31_188>='0' && LA31_188<='9')||(LA31_188>='A' && LA31_188<='Z')||LA31_188=='_'||(LA31_188>='a' && LA31_188<='z')) ) {
                                    alt31=46;
                                }
                                else {
                                    alt31=13;}
                            }
                            else {
                                alt31=46;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 'C':
            {
            int LA31_7 = input.LA(2);

            if ( (LA31_7=='L') ) {
                int LA31_64 = input.LA(3);

                if ( (LA31_64=='A') ) {
                    int LA31_121 = input.LA(4);

                    if ( (LA31_121=='S') ) {
                        int LA31_152 = input.LA(5);

                        if ( (LA31_152=='S') ) {
                            int LA31_173 = input.LA(6);

                            if ( (LA31_173=='.'||(LA31_173>='0' && LA31_173<='9')||(LA31_173>='A' && LA31_173<='Z')||LA31_173=='_'||(LA31_173>='a' && LA31_173<='z')) ) {
                                alt31=46;
                            }
                            else {
                                alt31=6;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'M':
            {
            switch ( input.LA(2) ) {
            case 'E':
                {
                int LA31_65 = input.LA(3);

                if ( (LA31_65=='T') ) {
                    int LA31_122 = input.LA(4);

                    if ( (LA31_122=='H') ) {
                        int LA31_153 = input.LA(5);

                        if ( (LA31_153=='O') ) {
                            int LA31_174 = input.LA(6);

                            if ( (LA31_174=='D') ) {
                                int LA31_190 = input.LA(7);

                                if ( (LA31_190=='.'||(LA31_190>='0' && LA31_190<='9')||(LA31_190>='A' && LA31_190<='Z')||LA31_190=='_'||(LA31_190>='a' && LA31_190<='z')) ) {
                                    alt31=46;
                                }
                                else {
                                    alt31=7;}
                            }
                            else {
                                alt31=46;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 'O':
                {
                int LA31_66 = input.LA(3);

                if ( (LA31_66=='D') ) {
                    int LA31_123 = input.LA(4);

                    if ( (LA31_123=='.'||(LA31_123>='0' && LA31_123<='9')||(LA31_123>='A' && LA31_123<='Z')||LA31_123=='_'||(LA31_123>='a' && LA31_123<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=40;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 'I':
                {
                int LA31_67 = input.LA(3);

                if ( (LA31_67=='N') ) {
                    int LA31_124 = input.LA(4);

                    if ( (LA31_124=='U') ) {
                        int LA31_154 = input.LA(5);

                        if ( (LA31_154=='S') ) {
                            int LA31_175 = input.LA(6);

                            if ( (LA31_175=='.'||(LA31_175>='0' && LA31_175<='9')||(LA31_175>='A' && LA31_175<='Z')||LA31_175=='_'||(LA31_175>='a' && LA31_175<='z')) ) {
                                alt31=46;
                            }
                            else {
                                alt31=39;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 'L':
            {
            switch ( input.LA(2) ) {
            case 'E':
                {
                int LA31_68 = input.LA(3);

                if ( (LA31_68=='Q') ) {
                    int LA31_125 = input.LA(4);

                    if ( (LA31_125=='.'||(LA31_125>='0' && LA31_125<='9')||(LA31_125>='A' && LA31_125<='Z')||LA31_125=='_'||(LA31_125>='a' && LA31_125<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=31;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 'T':
                {
                int LA31_69 = input.LA(3);

                if ( (LA31_69=='.'||(LA31_69>='0' && LA31_69<='9')||(LA31_69>='A' && LA31_69<='Z')||LA31_69=='_'||(LA31_69>='a' && LA31_69<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=29;}
                }
                break;
            case 'I':
                {
                int LA31_70 = input.LA(3);

                if ( (LA31_70=='N') ) {
                    int LA31_126 = input.LA(4);

                    if ( (LA31_126=='E') ) {
                        int LA31_155 = input.LA(5);

                        if ( (LA31_155=='.'||(LA31_155>='0' && LA31_155<='9')||(LA31_155>='A' && LA31_155<='Z')||LA31_155=='_'||(LA31_155>='a' && LA31_155<='z')) ) {
                            alt31=46;
                        }
                        else {
                            alt31=8;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 'E':
            {
            switch ( input.LA(2) ) {
            case 'Q':
                {
                int LA31_71 = input.LA(3);

                if ( (LA31_71=='.'||(LA31_71>='0' && LA31_71<='9')||(LA31_71>='A' && LA31_71<='Z')||LA31_71=='_'||(LA31_71>='a' && LA31_71<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=26;}
                }
                break;
            case 'N':
                {
                int LA31_72 = input.LA(3);

                if ( (LA31_72=='D') ) {
                    int LA31_127 = input.LA(4);

                    if ( (LA31_127=='R') ) {
                        int LA31_156 = input.LA(5);

                        if ( (LA31_156=='U') ) {
                            int LA31_177 = input.LA(6);

                            if ( (LA31_177=='L') ) {
                                int LA31_191 = input.LA(7);

                                if ( (LA31_191=='E') ) {
                                    int LA31_198 = input.LA(8);

                                    if ( (LA31_198=='.'||(LA31_198>='0' && LA31_198<='9')||(LA31_198>='A' && LA31_198<='Z')||LA31_198=='_'||(LA31_198>='a' && LA31_198<='z')) ) {
                                        alt31=46;
                                    }
                                    else {
                                        alt31=9;}
                                }
                                else {
                                    alt31=46;}
                            }
                            else {
                                alt31=46;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 'N':
            {
            switch ( input.LA(2) ) {
            case 'E':
                {
                int LA31_73 = input.LA(3);

                if ( (LA31_73=='Q') ) {
                    int LA31_128 = input.LA(4);

                    if ( (LA31_128=='.'||(LA31_128>='0' && LA31_128<='9')||(LA31_128>='A' && LA31_128<='Z')||LA31_128=='_'||(LA31_128>='a' && LA31_128<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=27;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 'O':
                {
                int LA31_74 = input.LA(3);

                if ( (LA31_74=='T') ) {
                    switch ( input.LA(4) ) {
                    case 'H':
                        {
                        int LA31_157 = input.LA(5);

                        if ( (LA31_157=='I') ) {
                            int LA31_178 = input.LA(6);

                            if ( (LA31_178=='N') ) {
                                int LA31_192 = input.LA(7);

                                if ( (LA31_192=='G') ) {
                                    int LA31_199 = input.LA(8);

                                    if ( (LA31_199=='.'||(LA31_199>='0' && LA31_199<='9')||(LA31_199>='A' && LA31_199<='Z')||LA31_199=='_'||(LA31_199>='a' && LA31_199<='z')) ) {
                                        alt31=46;
                                    }
                                    else {
                                        alt31=10;}
                                }
                                else {
                                    alt31=46;}
                            }
                            else {
                                alt31=46;}
                        }
                        else {
                            alt31=46;}
                        }
                        break;
                    case '.':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'S':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    case 'Z':
                    case '_':
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'm':
                    case 'n':
                    case 'o':
                    case 'p':
                    case 'q':
                    case 'r':
                    case 's':
                    case 't':
                    case 'u':
                    case 'v':
                    case 'w':
                    case 'x':
                    case 'y':
                    case 'z':
                        {
                        alt31=46;
                        }
                        break;
                    default:
                        alt31=25;}

                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 'T':
            {
            switch ( input.LA(2) ) {
            case 'R':
                {
                int LA31_75 = input.LA(3);

                if ( (LA31_75=='U') ) {
                    int LA31_130 = input.LA(4);

                    if ( (LA31_130=='E') ) {
                        int LA31_158 = input.LA(5);

                        if ( (LA31_158=='.'||(LA31_158>='0' && LA31_158<='9')||(LA31_158>='A' && LA31_158<='Z')||LA31_158=='_'||(LA31_158>='a' && LA31_158<='z')) ) {
                            alt31=46;
                        }
                        else {
                            alt31=11;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 'I':
                {
                int LA31_76 = input.LA(3);

                if ( (LA31_76=='M') ) {
                    int LA31_131 = input.LA(4);

                    if ( (LA31_131=='E') ) {
                        int LA31_159 = input.LA(5);

                        if ( (LA31_159=='S') ) {
                            int LA31_180 = input.LA(6);

                            if ( (LA31_180=='.'||(LA31_180>='0' && LA31_180<='9')||(LA31_180>='A' && LA31_180<='Z')||LA31_180=='_'||(LA31_180>='a' && LA31_180<='z')) ) {
                                alt31=46;
                            }
                            else {
                                alt31=36;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 't':
            {
            switch ( input.LA(2) ) {
            case 'i':
                {
                int LA31_77 = input.LA(3);

                if ( (LA31_77=='m') ) {
                    int LA31_132 = input.LA(4);

                    if ( (LA31_132=='e') ) {
                        int LA31_160 = input.LA(5);

                        if ( (LA31_160=='s') ) {
                            int LA31_181 = input.LA(6);

                            if ( (LA31_181=='.'||(LA31_181>='0' && LA31_181<='9')||(LA31_181>='A' && LA31_181<='Z')||LA31_181=='_'||(LA31_181>='a' && LA31_181<='z')) ) {
                                alt31=46;
                            }
                            else {
                                alt31=36;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 'r':
                {
                int LA31_78 = input.LA(3);

                if ( (LA31_78=='u') ) {
                    int LA31_133 = input.LA(4);

                    if ( (LA31_133=='e') ) {
                        int LA31_161 = input.LA(5);

                        if ( (LA31_161=='.'||(LA31_161>='0' && LA31_161<='9')||(LA31_161>='A' && LA31_161<='Z')||LA31_161=='_'||(LA31_161>='a' && LA31_161<='z')) ) {
                            alt31=46;
                        }
                        else {
                            alt31=11;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 'F':
            {
            int LA31_14 = input.LA(2);

            if ( (LA31_14=='A') ) {
                int LA31_79 = input.LA(3);

                if ( (LA31_79=='L') ) {
                    int LA31_134 = input.LA(4);

                    if ( (LA31_134=='S') ) {
                        int LA31_162 = input.LA(5);

                        if ( (LA31_162=='E') ) {
                            int LA31_182 = input.LA(6);

                            if ( (LA31_182=='.'||(LA31_182>='0' && LA31_182<='9')||(LA31_182>='A' && LA31_182<='Z')||LA31_182=='_'||(LA31_182>='a' && LA31_182<='z')) ) {
                                alt31=46;
                            }
                            else {
                                alt31=12;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'f':
            {
            int LA31_15 = input.LA(2);

            if ( (LA31_15=='a') ) {
                int LA31_80 = input.LA(3);

                if ( (LA31_80=='l') ) {
                    int LA31_135 = input.LA(4);

                    if ( (LA31_135=='s') ) {
                        int LA31_163 = input.LA(5);

                        if ( (LA31_163=='e') ) {
                            int LA31_183 = input.LA(6);

                            if ( (LA31_183=='.'||(LA31_183>='0' && LA31_183<='9')||(LA31_183>='A' && LA31_183<='Z')||LA31_183=='_'||(LA31_183>='a' && LA31_183<='z')) ) {
                                alt31=46;
                            }
                            else {
                                alt31=12;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'r':
            {
            int LA31_16 = input.LA(2);

            if ( (LA31_16=='e') ) {
                int LA31_81 = input.LA(3);

                if ( (LA31_81=='t') ) {
                    int LA31_136 = input.LA(4);

                    if ( (LA31_136=='u') ) {
                        int LA31_164 = input.LA(5);

                        if ( (LA31_164=='r') ) {
                            int LA31_184 = input.LA(6);

                            if ( (LA31_184=='n') ) {
                                int LA31_194 = input.LA(7);

                                if ( (LA31_194=='.'||(LA31_194>='0' && LA31_194<='9')||(LA31_194>='A' && LA31_194<='Z')||LA31_194=='_'||(LA31_194>='a' && LA31_194<='z')) ) {
                                    alt31=46;
                                }
                                else {
                                    alt31=13;}
                            }
                            else {
                                alt31=46;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case '(':
            {
            alt31=14;
            }
            break;
        case ')':
            {
            alt31=15;
            }
            break;
        case '[':
            {
            alt31=16;
            }
            break;
        case ']':
            {
            alt31=17;
            }
            break;
        case '{':
            {
            alt31=18;
            }
            break;
        case '}':
            {
            alt31=19;
            }
            break;
        case ',':
        case ';':
            {
            alt31=20;
            }
            break;
        case '.':
            {
            alt31=21;
            }
            break;
        case '=':
            {
            int LA31_25 = input.LA(2);

            if ( (LA31_25=='=') ) {
                alt31=26;
            }
            else {
                alt31=22;}
            }
            break;
        case '<':
            {
            switch ( input.LA(2) ) {
            case '-':
                {
                alt31=22;
                }
                break;
            case '=':
                {
                alt31=31;
                }
                break;
            default:
                alt31=29;}

            }
            break;
        case '|':
            {
            int LA31_27 = input.LA(2);

            if ( (LA31_27=='|') ) {
                alt31=23;
            }
            else {
                alt31=32;}
            }
            break;
        case 'O':
            {
            int LA31_28 = input.LA(2);

            if ( (LA31_28=='R') ) {
                int LA31_88 = input.LA(3);

                if ( (LA31_88=='.'||(LA31_88>='0' && LA31_88<='9')||(LA31_88>='A' && LA31_88<='Z')||LA31_88=='_'||(LA31_88>='a' && LA31_88<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=23;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'o':
            {
            int LA31_29 = input.LA(2);

            if ( (LA31_29=='r') ) {
                int LA31_89 = input.LA(3);

                if ( (LA31_89=='.'||(LA31_89>='0' && LA31_89<='9')||(LA31_89>='A' && LA31_89<='Z')||LA31_89=='_'||(LA31_89>='a' && LA31_89<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=23;}
            }
            else {
                alt31=46;}
            }
            break;
        case '&':
            {
            int LA31_30 = input.LA(2);

            if ( (LA31_30=='&') ) {
                alt31=24;
            }
            else {
                alt31=33;}
            }
            break;
        case 'A':
            {
            int LA31_31 = input.LA(2);

            if ( (LA31_31=='N') ) {
                int LA31_92 = input.LA(3);

                if ( (LA31_92=='D') ) {
                    int LA31_137 = input.LA(4);

                    if ( (LA31_137=='.'||(LA31_137>='0' && LA31_137<='9')||(LA31_137>='A' && LA31_137<='Z')||LA31_137=='_'||(LA31_137>='a' && LA31_137<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=24;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'a':
            {
            int LA31_32 = input.LA(2);

            if ( (LA31_32=='n') ) {
                int LA31_93 = input.LA(3);

                if ( (LA31_93=='d') ) {
                    int LA31_138 = input.LA(4);

                    if ( (LA31_138=='.'||(LA31_138>='0' && LA31_138<='9')||(LA31_138>='A' && LA31_138<='Z')||LA31_138=='_'||(LA31_138>='a' && LA31_138<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=24;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case '!':
            {
            int LA31_33 = input.LA(2);

            if ( (LA31_33=='=') ) {
                alt31=27;
            }
            else {
                alt31=25;}
            }
            break;
        case 'n':
            {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA31_96 = input.LA(3);

                if ( (LA31_96=='t') ) {
                    int LA31_139 = input.LA(4);

                    if ( (LA31_139=='.'||(LA31_139>='0' && LA31_139<='9')||(LA31_139>='A' && LA31_139<='Z')||LA31_139=='_'||(LA31_139>='a' && LA31_139<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=25;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 'e':
                {
                int LA31_97 = input.LA(3);

                if ( (LA31_97=='q') ) {
                    int LA31_140 = input.LA(4);

                    if ( (LA31_140=='.'||(LA31_140>='0' && LA31_140<='9')||(LA31_140>='A' && LA31_140<='Z')||LA31_140=='_'||(LA31_140>='a' && LA31_140<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=27;}
                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 'e':
            {
            int LA31_35 = input.LA(2);

            if ( (LA31_35=='q') ) {
                int LA31_98 = input.LA(3);

                if ( (LA31_98=='.'||(LA31_98>='0' && LA31_98<='9')||(LA31_98>='A' && LA31_98<='Z')||LA31_98=='_'||(LA31_98>='a' && LA31_98<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=26;}
            }
            else {
                alt31=46;}
            }
            break;
        case '>':
            {
            int LA31_36 = input.LA(2);

            if ( (LA31_36=='=') ) {
                alt31=30;
            }
            else {
                alt31=28;}
            }
            break;
        case 'G':
            {
            int LA31_37 = input.LA(2);

            if ( (LA31_37=='T') ) {
                int LA31_101 = input.LA(3);

                if ( (LA31_101=='.'||(LA31_101>='0' && LA31_101<='9')||(LA31_101>='A' && LA31_101<='Z')||LA31_101=='_'||(LA31_101>='a' && LA31_101<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=28;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'g':
            {
            switch ( input.LA(2) ) {
            case 'e':
                {
                int LA31_102 = input.LA(3);

                if ( (LA31_102=='q') ) {
                    int LA31_141 = input.LA(4);

                    if ( (LA31_141=='.'||(LA31_141>='0' && LA31_141<='9')||(LA31_141>='A' && LA31_141<='Z')||LA31_141=='_'||(LA31_141>='a' && LA31_141<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=30;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 't':
                {
                int LA31_103 = input.LA(3);

                if ( (LA31_103=='.'||(LA31_103>='0' && LA31_103<='9')||(LA31_103>='A' && LA31_103<='Z')||LA31_103=='_'||(LA31_103>='a' && LA31_103<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=28;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case 'l':
            {
            switch ( input.LA(2) ) {
            case 'e':
                {
                int LA31_104 = input.LA(3);

                if ( (LA31_104=='q') ) {
                    int LA31_142 = input.LA(4);

                    if ( (LA31_142=='.'||(LA31_142>='0' && LA31_142<='9')||(LA31_142>='A' && LA31_142<='Z')||LA31_142=='_'||(LA31_142>='a' && LA31_142<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=31;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 't':
                {
                int LA31_105 = input.LA(3);

                if ( (LA31_105=='.'||(LA31_105>='0' && LA31_105<='9')||(LA31_105>='A' && LA31_105<='Z')||LA31_105=='_'||(LA31_105>='a' && LA31_105<='z')) ) {
                    alt31=46;
                }
                else {
                    alt31=29;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case '^':
            {
            alt31=34;
            }
            break;
        case '~':
            {
            alt31=35;
            }
            break;
        case '*':
            {
            alt31=36;
            }
            break;
        case '/':
            {
            alt31=37;
            }
            break;
        case 'd':
            {
            int LA31_44 = input.LA(2);

            if ( (LA31_44=='i') ) {
                int LA31_106 = input.LA(3);

                if ( (LA31_106=='v') ) {
                    int LA31_143 = input.LA(4);

                    if ( (LA31_143=='i') ) {
                        int LA31_165 = input.LA(5);

                        if ( (LA31_165=='d') ) {
                            int LA31_185 = input.LA(6);

                            if ( (LA31_185=='e') ) {
                                int LA31_195 = input.LA(7);

                                if ( (LA31_195=='.'||(LA31_195>='0' && LA31_195<='9')||(LA31_195>='A' && LA31_195<='Z')||LA31_195=='_'||(LA31_195>='a' && LA31_195<='z')) ) {
                                    alt31=46;
                                }
                                else {
                                    alt31=37;}
                            }
                            else {
                                alt31=46;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case '-':
            {
            int LA31_45 = input.LA(2);

            if ( ((LA31_45>='0' && LA31_45<='9')) ) {
                alt31=1;
            }
            else {
                alt31=39;}
            }
            break;
        case 'P':
            {
            int LA31_46 = input.LA(2);

            if ( (LA31_46=='L') ) {
                int LA31_108 = input.LA(3);

                if ( (LA31_108=='U') ) {
                    int LA31_144 = input.LA(4);

                    if ( (LA31_144=='S') ) {
                        int LA31_166 = input.LA(5);

                        if ( (LA31_166=='.'||(LA31_166>='0' && LA31_166<='9')||(LA31_166>='A' && LA31_166<='Z')||LA31_166=='_'||(LA31_166>='a' && LA31_166<='z')) ) {
                            alt31=46;
                        }
                        else {
                            alt31=38;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'p':
            {
            int LA31_47 = input.LA(2);

            if ( (LA31_47=='l') ) {
                int LA31_109 = input.LA(3);

                if ( (LA31_109=='u') ) {
                    int LA31_145 = input.LA(4);

                    if ( (LA31_145=='s') ) {
                        int LA31_167 = input.LA(5);

                        if ( (LA31_167=='.'||(LA31_167>='0' && LA31_167<='9')||(LA31_167>='A' && LA31_167<='Z')||LA31_167=='_'||(LA31_167>='a' && LA31_167<='z')) ) {
                            alt31=46;
                        }
                        else {
                            alt31=38;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
            }
            else {
                alt31=46;}
            }
            break;
        case 'm':
            {
            switch ( input.LA(2) ) {
            case 'i':
                {
                int LA31_110 = input.LA(3);

                if ( (LA31_110=='n') ) {
                    int LA31_146 = input.LA(4);

                    if ( (LA31_146=='u') ) {
                        int LA31_168 = input.LA(5);

                        if ( (LA31_168=='s') ) {
                            int LA31_186 = input.LA(6);

                            if ( (LA31_186=='.'||(LA31_186>='0' && LA31_186<='9')||(LA31_186>='A' && LA31_186<='Z')||LA31_186=='_'||(LA31_186>='a' && LA31_186<='z')) ) {
                                alt31=46;
                            }
                            else {
                                alt31=39;}
                        }
                        else {
                            alt31=46;}
                    }
                    else {
                        alt31=46;}
                }
                else {
                    alt31=46;}
                }
                break;
            case 'o':
                {
                int LA31_111 = input.LA(3);

                if ( (LA31_111=='d') ) {
                    int LA31_147 = input.LA(4);

                    if ( (LA31_147=='.'||(LA31_147>='0' && LA31_147<='9')||(LA31_147>='A' && LA31_147<='Z')||LA31_147=='_'||(LA31_147>='a' && LA31_147<='z')) ) {
                        alt31=46;
                    }
                    else {
                        alt31=40;}
                }
                else {
                    alt31=46;}
                }
                break;
            default:
                alt31=46;}

            }
            break;
        case '%':
            {
            alt31=40;
            }
            break;
        case '?':
            {
            alt31=41;
            }
            break;
        case ':':
            {
            alt31=42;
            }
            break;
        case '\'':
            {
            int LA31_52 = input.LA(2);

            if ( (LA31_52=='\t'||LA31_52=='\r'||(LA31_52>=' ' && LA31_52<='~')) ) {
                alt31=46;
            }
            else {
                alt31=43;}
            }
            break;
        case '\"':
            {
            int LA31_53 = input.LA(2);

            if ( (LA31_53=='\t'||LA31_53=='\r'||(LA31_53>=' ' && LA31_53<='~')) ) {
                alt31=45;
            }
            else {
                alt31=44;}
            }
            break;
        case 'H':
        case 'J':
        case 'K':
        case 'Q':
        case 'S':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case '_':
        case 'b':
        case 'c':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'q':
        case 's':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            {
            alt31=46;
            }
            break;
        case '$':
            {
            alt31=47;
            }
            break;
        case '\t':
        case '\n':
        case '\r':
        case ' ':
            {
            alt31=48;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( NUMBER | BIND | IF | DO | RULE | CLASS | METHOD | LINE | ENDRULE | NOTHING | TRUE | FALSE | RETURN | LPAREN | RPAREN | LSQUARE | RSQUARE | LBRACE | RBRACE | SEPR | DOT | ASSIGN | OR | AND | NOT | EQ | NEQ | GT | LT | GEQ | LEQ | BOR | BAND | BXOR | TWIDDLE | MUL | DIV | PLUS | MINUS | MOD | TERN_IF | COLON | QUOTE | DQUOTE | STRING | SYMBOL | DOLLARSYM | WS );", 31, 0, input);

            throw nvae;
        }

        switch (alt31) {
            case 1 :
                // dd/grammar/ECAToken.g:1:10: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 2 :
                // dd/grammar/ECAToken.g:1:17: BIND
                {
                mBIND(); 

                }
                break;
            case 3 :
                // dd/grammar/ECAToken.g:1:22: IF
                {
                mIF(); 

                }
                break;
            case 4 :
                // dd/grammar/ECAToken.g:1:25: DO
                {
                mDO(); 

                }
                break;
            case 5 :
                // dd/grammar/ECAToken.g:1:28: RULE
                {
                mRULE(); 

                }
                break;
            case 6 :
                // dd/grammar/ECAToken.g:1:33: CLASS
                {
                mCLASS(); 

                }
                break;
            case 7 :
                // dd/grammar/ECAToken.g:1:39: METHOD
                {
                mMETHOD(); 

                }
                break;
            case 8 :
                // dd/grammar/ECAToken.g:1:46: LINE
                {
                mLINE(); 

                }
                break;
            case 9 :
                // dd/grammar/ECAToken.g:1:51: ENDRULE
                {
                mENDRULE(); 

                }
                break;
            case 10 :
                // dd/grammar/ECAToken.g:1:59: NOTHING
                {
                mNOTHING(); 

                }
                break;
            case 11 :
                // dd/grammar/ECAToken.g:1:67: TRUE
                {
                mTRUE(); 

                }
                break;
            case 12 :
                // dd/grammar/ECAToken.g:1:72: FALSE
                {
                mFALSE(); 

                }
                break;
            case 13 :
                // dd/grammar/ECAToken.g:1:78: RETURN
                {
                mRETURN(); 

                }
                break;
            case 14 :
                // dd/grammar/ECAToken.g:1:85: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 15 :
                // dd/grammar/ECAToken.g:1:92: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 16 :
                // dd/grammar/ECAToken.g:1:99: LSQUARE
                {
                mLSQUARE(); 

                }
                break;
            case 17 :
                // dd/grammar/ECAToken.g:1:107: RSQUARE
                {
                mRSQUARE(); 

                }
                break;
            case 18 :
                // dd/grammar/ECAToken.g:1:115: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 19 :
                // dd/grammar/ECAToken.g:1:122: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 20 :
                // dd/grammar/ECAToken.g:1:129: SEPR
                {
                mSEPR(); 

                }
                break;
            case 21 :
                // dd/grammar/ECAToken.g:1:134: DOT
                {
                mDOT(); 

                }
                break;
            case 22 :
                // dd/grammar/ECAToken.g:1:138: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 23 :
                // dd/grammar/ECAToken.g:1:145: OR
                {
                mOR(); 

                }
                break;
            case 24 :
                // dd/grammar/ECAToken.g:1:148: AND
                {
                mAND(); 

                }
                break;
            case 25 :
                // dd/grammar/ECAToken.g:1:152: NOT
                {
                mNOT(); 

                }
                break;
            case 26 :
                // dd/grammar/ECAToken.g:1:156: EQ
                {
                mEQ(); 

                }
                break;
            case 27 :
                // dd/grammar/ECAToken.g:1:159: NEQ
                {
                mNEQ(); 

                }
                break;
            case 28 :
                // dd/grammar/ECAToken.g:1:163: GT
                {
                mGT(); 

                }
                break;
            case 29 :
                // dd/grammar/ECAToken.g:1:166: LT
                {
                mLT(); 

                }
                break;
            case 30 :
                // dd/grammar/ECAToken.g:1:169: GEQ
                {
                mGEQ(); 

                }
                break;
            case 31 :
                // dd/grammar/ECAToken.g:1:173: LEQ
                {
                mLEQ(); 

                }
                break;
            case 32 :
                // dd/grammar/ECAToken.g:1:177: BOR
                {
                mBOR(); 

                }
                break;
            case 33 :
                // dd/grammar/ECAToken.g:1:181: BAND
                {
                mBAND(); 

                }
                break;
            case 34 :
                // dd/grammar/ECAToken.g:1:186: BXOR
                {
                mBXOR(); 

                }
                break;
            case 35 :
                // dd/grammar/ECAToken.g:1:191: TWIDDLE
                {
                mTWIDDLE(); 

                }
                break;
            case 36 :
                // dd/grammar/ECAToken.g:1:199: MUL
                {
                mMUL(); 

                }
                break;
            case 37 :
                // dd/grammar/ECAToken.g:1:203: DIV
                {
                mDIV(); 

                }
                break;
            case 38 :
                // dd/grammar/ECAToken.g:1:207: PLUS
                {
                mPLUS(); 

                }
                break;
            case 39 :
                // dd/grammar/ECAToken.g:1:212: MINUS
                {
                mMINUS(); 

                }
                break;
            case 40 :
                // dd/grammar/ECAToken.g:1:218: MOD
                {
                mMOD(); 

                }
                break;
            case 41 :
                // dd/grammar/ECAToken.g:1:222: TERN_IF
                {
                mTERN_IF(); 

                }
                break;
            case 42 :
                // dd/grammar/ECAToken.g:1:230: COLON
                {
                mCOLON(); 

                }
                break;
            case 43 :
                // dd/grammar/ECAToken.g:1:236: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 44 :
                // dd/grammar/ECAToken.g:1:242: DQUOTE
                {
                mDQUOTE(); 

                }
                break;
            case 45 :
                // dd/grammar/ECAToken.g:1:249: STRING
                {
                mSTRING(); 

                }
                break;
            case 46 :
                // dd/grammar/ECAToken.g:1:256: SYMBOL
                {
                mSYMBOL(); 

                }
                break;
            case 47 :
                // dd/grammar/ECAToken.g:1:263: DOLLARSYM
                {
                mDOLLARSYM(); 

                }
                break;
            case 48 :
                // dd/grammar/ECAToken.g:1:273: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA28 dfa28 = new DFA28(this);
    static final String DFA6_eotS =
        "\2\uffff\2\4\2\uffff\1\4";
    static final String DFA6_eofS =
        "\7\uffff";
    static final String DFA6_minS =
        "\1\53\1\60\2\56\2\uffff\1\56";
    static final String DFA6_maxS =
        "\2\71\1\56\1\71\2\uffff\1\71";
    static final String DFA6_acceptS =
        "\4\uffff\1\1\1\2\1\uffff";
    static final String DFA6_specialS =
        "\7\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\1\uffff\1\1\2\uffff\1\2\11\3",
            "\1\2\11\3",
            "\1\5",
            "\1\5\1\uffff\12\6",
            "",
            "",
            "\1\5\1\uffff\12\6"
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "41:1: NUMBER : ( INTEGER | FLOAT );";
        }
    }
    static final String DFA28_eotS =
        "\1\uffff\2\3\2\uffff";
    static final String DFA28_eofS =
        "\5\uffff";
    static final String DFA28_minS =
        "\1\101\2\56\2\uffff";
    static final String DFA28_maxS =
        "\3\172\2\uffff";
    static final String DFA28_acceptS =
        "\3\uffff\1\2\1\1";
    static final String DFA28_specialS =
        "\5\uffff}>";
    static final String[] DFA28_transitionS = {
            "\32\1\4\uffff\1\1\1\uffff\32\1",
            "\1\4\1\uffff\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
            "\1\4\1\uffff\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
            "",
            ""
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "256:1: fragment DOTSYM : ( BARESYM DOT DOTSYM | BARESYM );";
        }
    }
 

}