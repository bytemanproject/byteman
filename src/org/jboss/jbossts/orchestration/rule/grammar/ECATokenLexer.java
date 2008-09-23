// $ANTLR 3.0.1 dd/grammar/ECAToken.g 2008-09-22 16:26:17

package org.jboss.jbossts.orchestration.rule.grammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ECATokenLexer extends Lexer {
    public static final int MINUS=49;
    public static final int NUMBER=12;
    public static final int METHOD=18;
    public static final int FLOAT=11;
    public static final int FALSE=23;
    public static final int POSDIGIT=5;
    public static final int TWIDDLE=45;
    public static final int LEQ=41;
    public static final int RULE=16;
    public static final int MOD=50;
    public static final int GEQ=40;
    public static final int DQUOTE=56;
    public static final int OR=33;
    public static final int BOR=42;
    public static final int BAREINT=7;
    public static final int LBRACE=28;
    public static final int NEWLINE=58;
    public static final int DOT=31;
    public static final int RBRACE=29;
    public static final int INTEGER=8;
    public static final int AND=34;
    public static final int ASSIGN=32;
    public static final int SYMBOL=64;
    public static final int RPAREN=25;
    public static final int SIGN=6;
    public static final int LPAREN=24;
    public static final int PLUS=48;
    public static final int DIGIT=4;
    public static final int LINE=19;
    public static final int BAND=43;
    public static final int NEQ=37;
    public static final int SPACE=57;
    public static final int LETTER=53;
    public static final int LSQUARE=26;
    public static final int DO=15;
    public static final int POINT=9;
    public static final int BARESYM=61;
    public static final int NOTHING=21;
    public static final int SEPR=30;
    public static final int WS=67;
    public static final int STRING=60;
    public static final int EQ=36;
    public static final int QUOTSYM=62;
    public static final int LT=39;
    public static final int GT=38;
    public static final int DOLLAR=65;
    public static final int RSQUARE=27;
    public static final int QUOTE=55;
    public static final int TERN_IF=51;
    public static final int MUL=46;
    public static final int CLASS=17;
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

    // $ANTLR start LPAREN
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            // dd/grammar/ECAToken.g:81:8: ( '(' )
            // dd/grammar/ECAToken.g:81:10: '('
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
            // dd/grammar/ECAToken.g:84:8: ( ')' )
            // dd/grammar/ECAToken.g:84:10: ')'
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
            // dd/grammar/ECAToken.g:87:9: ( '\\[' )
            // dd/grammar/ECAToken.g:87:11: '\\['
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
            // dd/grammar/ECAToken.g:90:9: ( '\\]' )
            // dd/grammar/ECAToken.g:90:11: '\\]'
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
            // dd/grammar/ECAToken.g:93:8: ( '{' )
            // dd/grammar/ECAToken.g:93:10: '{'
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
            // dd/grammar/ECAToken.g:96:8: ( '}' )
            // dd/grammar/ECAToken.g:96:10: '}'
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
            // dd/grammar/ECAToken.g:101:6: ( ';' | ',' )
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
            // dd/grammar/ECAToken.g:107:5: ( '.' )
            // dd/grammar/ECAToken.g:107:7: '.'
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
            // dd/grammar/ECAToken.g:112:8: ( '=' | '<--' )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='=') ) {
                alt9=1;
            }
            else if ( (LA9_0=='<') ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("112:1: ASSIGN : ( '=' | '<--' );", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // dd/grammar/ECAToken.g:112:10: '='
                    {
                    match('='); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:113:4: '<--'
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
            // dd/grammar/ECAToken.g:118:4: ( '||' | 'OR' | 'or' )
            int alt10=3;
            switch ( input.LA(1) ) {
            case '|':
                {
                alt10=1;
                }
                break;
            case 'O':
                {
                alt10=2;
                }
                break;
            case 'o':
                {
                alt10=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("118:1: OR : ( '||' | 'OR' | 'or' );", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // dd/grammar/ECAToken.g:118:6: '||'
                    {
                    match("||"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:119:4: 'OR'
                    {
                    match("OR"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:120:4: 'or'
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
            // dd/grammar/ECAToken.g:123:5: ( '&&' | 'AND' | 'and' )
            int alt11=3;
            switch ( input.LA(1) ) {
            case '&':
                {
                alt11=1;
                }
                break;
            case 'A':
                {
                alt11=2;
                }
                break;
            case 'a':
                {
                alt11=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("123:1: AND : ( '&&' | 'AND' | 'and' );", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // dd/grammar/ECAToken.g:123:7: '&&'
                    {
                    match("&&"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:124:4: 'AND'
                    {
                    match("AND"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:125:4: 'and'
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
            // dd/grammar/ECAToken.g:128:5: ( '!' | 'NOT' | 'not' )
            int alt12=3;
            switch ( input.LA(1) ) {
            case '!':
                {
                alt12=1;
                }
                break;
            case 'N':
                {
                alt12=2;
                }
                break;
            case 'n':
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("128:1: NOT : ( '!' | 'NOT' | 'not' );", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // dd/grammar/ECAToken.g:128:7: '!'
                    {
                    match('!'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:129:4: 'NOT'
                    {
                    match("NOT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:130:4: 'not'
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
            // dd/grammar/ECAToken.g:135:4: ( '==' | 'EQ' | 'eq' )
            int alt13=3;
            switch ( input.LA(1) ) {
            case '=':
                {
                alt13=1;
                }
                break;
            case 'E':
                {
                alt13=2;
                }
                break;
            case 'e':
                {
                alt13=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("135:1: EQ : ( '==' | 'EQ' | 'eq' );", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // dd/grammar/ECAToken.g:135:6: '=='
                    {
                    match("=="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:136:4: 'EQ'
                    {
                    match("EQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:137:4: 'eq'
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
            // dd/grammar/ECAToken.g:140:5: ( '!=' | 'NEQ' | 'neq' )
            int alt14=3;
            switch ( input.LA(1) ) {
            case '!':
                {
                alt14=1;
                }
                break;
            case 'N':
                {
                alt14=2;
                }
                break;
            case 'n':
                {
                alt14=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("140:1: NEQ : ( '!=' | 'NEQ' | 'neq' );", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // dd/grammar/ECAToken.g:140:7: '!='
                    {
                    match("!="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:141:4: 'NEQ'
                    {
                    match("NEQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:142:4: 'neq'
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
            // dd/grammar/ECAToken.g:145:4: ( '>' | 'GT' | 'gt' )
            int alt15=3;
            switch ( input.LA(1) ) {
            case '>':
                {
                alt15=1;
                }
                break;
            case 'G':
                {
                alt15=2;
                }
                break;
            case 'g':
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("145:1: GT : ( '>' | 'GT' | 'gt' );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // dd/grammar/ECAToken.g:145:6: '>'
                    {
                    match('>'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:146:4: 'GT'
                    {
                    match("GT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:147:4: 'gt'
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
            // dd/grammar/ECAToken.g:150:4: ( '<' | 'LT' | 'lt' )
            int alt16=3;
            switch ( input.LA(1) ) {
            case '<':
                {
                alt16=1;
                }
                break;
            case 'L':
                {
                alt16=2;
                }
                break;
            case 'l':
                {
                alt16=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("150:1: LT : ( '<' | 'LT' | 'lt' );", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // dd/grammar/ECAToken.g:150:6: '<'
                    {
                    match('<'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:151:4: 'LT'
                    {
                    match("LT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:152:4: 'lt'
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
            // dd/grammar/ECAToken.g:155:5: ( '>=' | 'EQ' | 'geq' )
            int alt17=3;
            switch ( input.LA(1) ) {
            case '>':
                {
                alt17=1;
                }
                break;
            case 'E':
                {
                alt17=2;
                }
                break;
            case 'g':
                {
                alt17=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("155:1: GEQ : ( '>=' | 'EQ' | 'geq' );", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // dd/grammar/ECAToken.g:155:7: '>='
                    {
                    match(">="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:156:4: 'EQ'
                    {
                    match("EQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:157:4: 'geq'
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
            // dd/grammar/ECAToken.g:160:5: ( '<=' | 'LEQ' | 'leq' )
            int alt18=3;
            switch ( input.LA(1) ) {
            case '<':
                {
                alt18=1;
                }
                break;
            case 'L':
                {
                alt18=2;
                }
                break;
            case 'l':
                {
                alt18=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("160:1: LEQ : ( '<=' | 'LEQ' | 'leq' );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // dd/grammar/ECAToken.g:160:7: '<='
                    {
                    match("<="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:161:4: 'LEQ'
                    {
                    match("LEQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:162:4: 'leq'
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
            // dd/grammar/ECAToken.g:167:5: ( '|' )
            // dd/grammar/ECAToken.g:167:7: '|'
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
            // dd/grammar/ECAToken.g:170:6: ( '&' )
            // dd/grammar/ECAToken.g:170:8: '&'
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
            // dd/grammar/ECAToken.g:173:6: ( '^' )
            // dd/grammar/ECAToken.g:173:8: '^'
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
            // dd/grammar/ECAToken.g:176:9: ( '~' )
            // dd/grammar/ECAToken.g:176:11: '~'
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
            // dd/grammar/ECAToken.g:181:5: ( '*' | 'TIMES' | 'times' )
            int alt19=3;
            switch ( input.LA(1) ) {
            case '*':
                {
                alt19=1;
                }
                break;
            case 'T':
                {
                alt19=2;
                }
                break;
            case 't':
                {
                alt19=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("181:1: MUL : ( '*' | 'TIMES' | 'times' );", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // dd/grammar/ECAToken.g:181:7: '*'
                    {
                    match('*'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:182:4: 'TIMES'
                    {
                    match("TIMES"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:183:4: 'times'
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
            // dd/grammar/ECAToken.g:186:5: ( '/' | 'DIVIDE' | 'divide' )
            int alt20=3;
            switch ( input.LA(1) ) {
            case '/':
                {
                alt20=1;
                }
                break;
            case 'D':
                {
                alt20=2;
                }
                break;
            case 'd':
                {
                alt20=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("186:1: DIV : ( '/' | 'DIVIDE' | 'divide' );", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // dd/grammar/ECAToken.g:186:7: '/'
                    {
                    match('/'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:187:4: 'DIVIDE'
                    {
                    match("DIVIDE"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:188:4: 'divide'
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
            // dd/grammar/ECAToken.g:191:6: ( '+' | 'PLUS' | 'plus' )
            int alt21=3;
            switch ( input.LA(1) ) {
            case '+':
                {
                alt21=1;
                }
                break;
            case 'P':
                {
                alt21=2;
                }
                break;
            case 'p':
                {
                alt21=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("191:1: PLUS : ( '+' | 'PLUS' | 'plus' );", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // dd/grammar/ECAToken.g:191:8: '+'
                    {
                    match('+'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:192:4: 'PLUS'
                    {
                    match("PLUS"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:193:4: 'plus'
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
            // dd/grammar/ECAToken.g:196:7: ( '-' | 'MINUS' | 'minus' )
            int alt22=3;
            switch ( input.LA(1) ) {
            case '-':
                {
                alt22=1;
                }
                break;
            case 'M':
                {
                alt22=2;
                }
                break;
            case 'm':
                {
                alt22=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("196:1: MINUS : ( '-' | 'MINUS' | 'minus' );", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // dd/grammar/ECAToken.g:196:9: '-'
                    {
                    match('-'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:197:4: 'MINUS'
                    {
                    match("MINUS"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:198:4: 'minus'
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
            // dd/grammar/ECAToken.g:201:5: ( '%' | 'MOD' | 'mod' )
            int alt23=3;
            switch ( input.LA(1) ) {
            case '%':
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
                    new NoViableAltException("201:1: MOD : ( '%' | 'MOD' | 'mod' );", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // dd/grammar/ECAToken.g:201:7: '%'
                    {
                    match('%'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:202:4: 'MOD'
                    {
                    match("MOD"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:203:4: 'mod'
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
            // dd/grammar/ECAToken.g:208:9: ( '?' )
            // dd/grammar/ECAToken.g:208:11: '?'
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
            // dd/grammar/ECAToken.g:211:7: ( ':' )
            // dd/grammar/ECAToken.g:211:9: ':'
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
            // dd/grammar/ECAToken.g:217:8: ( 'a' .. 'z' | 'A' .. 'Z' )
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
            // dd/grammar/ECAToken.g:221:12: ( '_' )
            // dd/grammar/ECAToken.g:221:14: '_'
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
            // dd/grammar/ECAToken.g:224:7: ( '\\'' )
            // dd/grammar/ECAToken.g:224:9: '\\''
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
            // dd/grammar/ECAToken.g:227:8: ( '\"' )
            // dd/grammar/ECAToken.g:227:10: '\"'
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
            // dd/grammar/ECAToken.g:231:7: ( ' ' | '\\t' | '\\r' )
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
            // dd/grammar/ECAToken.g:235:9: ( '\\n' )
            // dd/grammar/ECAToken.g:235:11: '\\n'
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
            // dd/grammar/ECAToken.g:239:7: ( '!' | '$' | '%' | '^' | '&' | '*' | '(' | ')' | '-' | '+' | '=' | '{' | '}' | '[' | ']' | ':' | ';' | '@' | '~' | '#' | '|' | '\\\\' | '`' | ',' | '<' | '.' | '>' | '/' | '?' )
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
            // dd/grammar/ECAToken.g:242:8: ( DQUOTE ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )* DQUOTE )
            // dd/grammar/ECAToken.g:242:10: DQUOTE ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )* DQUOTE
            {
            mDQUOTE(); 
            // dd/grammar/ECAToken.g:242:17: ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0=='\t'||LA24_0=='\r'||(LA24_0>=' ' && LA24_0<='!')||(LA24_0>='#' && LA24_0<='~')) ) {
                    alt24=1;
                }


                switch (alt24) {
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
            	    break loop24;
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
            // dd/grammar/ECAToken.g:246:9: ( ( LETTER | UNDERSCORE ) ( LETTER | DIGIT | UNDERSCORE )* )
            // dd/grammar/ECAToken.g:246:11: ( LETTER | UNDERSCORE ) ( LETTER | DIGIT | UNDERSCORE )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // dd/grammar/ECAToken.g:246:33: ( LETTER | DIGIT | UNDERSCORE )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( ((LA25_0>='0' && LA25_0<='9')||(LA25_0>='A' && LA25_0<='Z')||LA25_0=='_'||(LA25_0>='a' && LA25_0<='z')) ) {
                    alt25=1;
                }


                switch (alt25) {
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
            	    break loop25;
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
            // dd/grammar/ECAToken.g:249:9: ( QUOTE ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE | SPACE )* QUOTE )
            // dd/grammar/ECAToken.g:249:11: QUOTE ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE | SPACE )* QUOTE
            {
            mQUOTE(); 
            // dd/grammar/ECAToken.g:249:17: ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE | SPACE )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0=='\t'||LA26_0=='\r'||(LA26_0>=' ' && LA26_0<='&')||(LA26_0>='(' && LA26_0<='~')) ) {
                    alt26=1;
                }


                switch (alt26) {
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
            	    break loop26;
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
            // dd/grammar/ECAToken.g:254:8: ( BARESYM DOT DOTSYM | BARESYM )
            int alt27=2;
            alt27 = dfa27.predict(input);
            switch (alt27) {
                case 1 :
                    // dd/grammar/ECAToken.g:254:10: BARESYM DOT DOTSYM
                    {
                    mBARESYM(); 
                    mDOT(); 
                    mDOTSYM(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:255:4: BARESYM
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
            // dd/grammar/ECAToken.g:258:8: ( DOTSYM | QUOTSYM )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( ((LA28_0>='A' && LA28_0<='Z')||LA28_0=='_'||(LA28_0>='a' && LA28_0<='z')) ) {
                alt28=1;
            }
            else if ( (LA28_0=='\'') ) {
                alt28=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("258:1: SYMBOL : ( DOTSYM | QUOTSYM );", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // dd/grammar/ECAToken.g:258:10: DOTSYM
                    {
                    mDOTSYM(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:259:4: QUOTSYM
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
            // dd/grammar/ECAToken.g:264:8: ( '$' )
            // dd/grammar/ECAToken.g:264:10: '$'
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
            // dd/grammar/ECAToken.g:269:11: ( DOLLAR ( BAREINT | BARESYM ) )
            // dd/grammar/ECAToken.g:269:13: DOLLAR ( BAREINT | BARESYM )
            {
            mDOLLAR(); 
            // dd/grammar/ECAToken.g:269:20: ( BAREINT | BARESYM )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( ((LA29_0>='0' && LA29_0<='9')) ) {
                alt29=1;
            }
            else if ( ((LA29_0>='A' && LA29_0<='Z')||LA29_0=='_'||(LA29_0>='a' && LA29_0<='z')) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("269:20: ( BAREINT | BARESYM )", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // dd/grammar/ECAToken.g:269:21: BAREINT
                    {
                    mBAREINT(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:269:31: BARESYM
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
            // dd/grammar/ECAToken.g:275:4: ( ( SPACE | NEWLINE ) )
            // dd/grammar/ECAToken.g:275:6: ( SPACE | NEWLINE )
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
        // dd/grammar/ECAToken.g:1:8: ( NUMBER | BIND | IF | DO | RULE | CLASS | METHOD | LINE | ENDRULE | NOTHING | TRUE | FALSE | LPAREN | RPAREN | LSQUARE | RSQUARE | LBRACE | RBRACE | SEPR | DOT | ASSIGN | OR | AND | NOT | EQ | NEQ | GT | LT | GEQ | LEQ | BOR | BAND | BXOR | TWIDDLE | MUL | DIV | PLUS | MINUS | MOD | TERN_IF | COLON | QUOTE | DQUOTE | STRING | SYMBOL | DOLLARSYM | WS )
        int alt30=47;
        switch ( input.LA(1) ) {
        case '+':
            {
            int LA30_1 = input.LA(2);

            if ( ((LA30_1>='0' && LA30_1<='9')) ) {
                alt30=1;
            }
            else {
                alt30=37;}
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
            alt30=1;
            }
            break;
        case 'B':
            {
            int LA30_3 = input.LA(2);

            if ( (LA30_3=='I') ) {
                int LA30_57 = input.LA(3);

                if ( (LA30_57=='N') ) {
                    int LA30_112 = input.LA(4);

                    if ( (LA30_112=='D') ) {
                        int LA30_143 = input.LA(5);

                        if ( (LA30_143=='.'||(LA30_143>='0' && LA30_143<='9')||(LA30_143>='A' && LA30_143<='Z')||LA30_143=='_'||(LA30_143>='a' && LA30_143<='z')) ) {
                            alt30=45;
                        }
                        else {
                            alt30=2;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'I':
            {
            int LA30_4 = input.LA(2);

            if ( (LA30_4=='F') ) {
                int LA30_58 = input.LA(3);

                if ( (LA30_58=='.'||(LA30_58>='0' && LA30_58<='9')||(LA30_58>='A' && LA30_58<='Z')||LA30_58=='_'||(LA30_58>='a' && LA30_58<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=3;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'D':
            {
            switch ( input.LA(2) ) {
            case 'O':
                {
                int LA30_59 = input.LA(3);

                if ( (LA30_59=='.'||(LA30_59>='0' && LA30_59<='9')||(LA30_59>='A' && LA30_59<='Z')||LA30_59=='_'||(LA30_59>='a' && LA30_59<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=4;}
                }
                break;
            case 'I':
                {
                int LA30_60 = input.LA(3);

                if ( (LA30_60=='V') ) {
                    int LA30_115 = input.LA(4);

                    if ( (LA30_115=='I') ) {
                        int LA30_144 = input.LA(5);

                        if ( (LA30_144=='D') ) {
                            int LA30_163 = input.LA(6);

                            if ( (LA30_163=='E') ) {
                                int LA30_178 = input.LA(7);

                                if ( (LA30_178=='.'||(LA30_178>='0' && LA30_178<='9')||(LA30_178>='A' && LA30_178<='Z')||LA30_178=='_'||(LA30_178>='a' && LA30_178<='z')) ) {
                                    alt30=45;
                                }
                                else {
                                    alt30=36;}
                            }
                            else {
                                alt30=45;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case 'R':
            {
            int LA30_6 = input.LA(2);

            if ( (LA30_6=='U') ) {
                int LA30_61 = input.LA(3);

                if ( (LA30_61=='L') ) {
                    int LA30_116 = input.LA(4);

                    if ( (LA30_116=='E') ) {
                        int LA30_145 = input.LA(5);

                        if ( (LA30_145=='.'||(LA30_145>='0' && LA30_145<='9')||(LA30_145>='A' && LA30_145<='Z')||LA30_145=='_'||(LA30_145>='a' && LA30_145<='z')) ) {
                            alt30=45;
                        }
                        else {
                            alt30=5;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'C':
            {
            int LA30_7 = input.LA(2);

            if ( (LA30_7=='L') ) {
                int LA30_62 = input.LA(3);

                if ( (LA30_62=='A') ) {
                    int LA30_117 = input.LA(4);

                    if ( (LA30_117=='S') ) {
                        int LA30_146 = input.LA(5);

                        if ( (LA30_146=='S') ) {
                            int LA30_165 = input.LA(6);

                            if ( (LA30_165=='.'||(LA30_165>='0' && LA30_165<='9')||(LA30_165>='A' && LA30_165<='Z')||LA30_165=='_'||(LA30_165>='a' && LA30_165<='z')) ) {
                                alt30=45;
                            }
                            else {
                                alt30=6;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'M':
            {
            switch ( input.LA(2) ) {
            case 'I':
                {
                int LA30_63 = input.LA(3);

                if ( (LA30_63=='N') ) {
                    int LA30_118 = input.LA(4);

                    if ( (LA30_118=='U') ) {
                        int LA30_147 = input.LA(5);

                        if ( (LA30_147=='S') ) {
                            int LA30_166 = input.LA(6);

                            if ( (LA30_166=='.'||(LA30_166>='0' && LA30_166<='9')||(LA30_166>='A' && LA30_166<='Z')||LA30_166=='_'||(LA30_166>='a' && LA30_166<='z')) ) {
                                alt30=45;
                            }
                            else {
                                alt30=38;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 'E':
                {
                int LA30_64 = input.LA(3);

                if ( (LA30_64=='T') ) {
                    int LA30_119 = input.LA(4);

                    if ( (LA30_119=='H') ) {
                        int LA30_148 = input.LA(5);

                        if ( (LA30_148=='O') ) {
                            int LA30_167 = input.LA(6);

                            if ( (LA30_167=='D') ) {
                                int LA30_180 = input.LA(7);

                                if ( (LA30_180=='.'||(LA30_180>='0' && LA30_180<='9')||(LA30_180>='A' && LA30_180<='Z')||LA30_180=='_'||(LA30_180>='a' && LA30_180<='z')) ) {
                                    alt30=45;
                                }
                                else {
                                    alt30=7;}
                            }
                            else {
                                alt30=45;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 'O':
                {
                int LA30_65 = input.LA(3);

                if ( (LA30_65=='D') ) {
                    int LA30_120 = input.LA(4);

                    if ( (LA30_120=='.'||(LA30_120>='0' && LA30_120<='9')||(LA30_120>='A' && LA30_120<='Z')||LA30_120=='_'||(LA30_120>='a' && LA30_120<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=39;}
                }
                else {
                    alt30=45;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case 'L':
            {
            switch ( input.LA(2) ) {
            case 'T':
                {
                int LA30_66 = input.LA(3);

                if ( (LA30_66=='.'||(LA30_66>='0' && LA30_66<='9')||(LA30_66>='A' && LA30_66<='Z')||LA30_66=='_'||(LA30_66>='a' && LA30_66<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=28;}
                }
                break;
            case 'I':
                {
                int LA30_67 = input.LA(3);

                if ( (LA30_67=='N') ) {
                    int LA30_121 = input.LA(4);

                    if ( (LA30_121=='E') ) {
                        int LA30_149 = input.LA(5);

                        if ( (LA30_149=='.'||(LA30_149>='0' && LA30_149<='9')||(LA30_149>='A' && LA30_149<='Z')||LA30_149=='_'||(LA30_149>='a' && LA30_149<='z')) ) {
                            alt30=45;
                        }
                        else {
                            alt30=8;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 'E':
                {
                int LA30_68 = input.LA(3);

                if ( (LA30_68=='Q') ) {
                    int LA30_122 = input.LA(4);

                    if ( (LA30_122=='.'||(LA30_122>='0' && LA30_122<='9')||(LA30_122>='A' && LA30_122<='Z')||LA30_122=='_'||(LA30_122>='a' && LA30_122<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=30;}
                }
                else {
                    alt30=45;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case 'E':
            {
            switch ( input.LA(2) ) {
            case 'N':
                {
                int LA30_69 = input.LA(3);

                if ( (LA30_69=='D') ) {
                    int LA30_123 = input.LA(4);

                    if ( (LA30_123=='R') ) {
                        int LA30_150 = input.LA(5);

                        if ( (LA30_150=='U') ) {
                            int LA30_169 = input.LA(6);

                            if ( (LA30_169=='L') ) {
                                int LA30_181 = input.LA(7);

                                if ( (LA30_181=='E') ) {
                                    int LA30_186 = input.LA(8);

                                    if ( (LA30_186=='.'||(LA30_186>='0' && LA30_186<='9')||(LA30_186>='A' && LA30_186<='Z')||LA30_186=='_'||(LA30_186>='a' && LA30_186<='z')) ) {
                                        alt30=45;
                                    }
                                    else {
                                        alt30=9;}
                                }
                                else {
                                    alt30=45;}
                            }
                            else {
                                alt30=45;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 'Q':
                {
                int LA30_70 = input.LA(3);

                if ( (LA30_70=='.'||(LA30_70>='0' && LA30_70<='9')||(LA30_70>='A' && LA30_70<='Z')||LA30_70=='_'||(LA30_70>='a' && LA30_70<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=25;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case 'N':
            {
            switch ( input.LA(2) ) {
            case 'E':
                {
                int LA30_71 = input.LA(3);

                if ( (LA30_71=='Q') ) {
                    int LA30_124 = input.LA(4);

                    if ( (LA30_124=='.'||(LA30_124>='0' && LA30_124<='9')||(LA30_124>='A' && LA30_124<='Z')||LA30_124=='_'||(LA30_124>='a' && LA30_124<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=26;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 'O':
                {
                int LA30_72 = input.LA(3);

                if ( (LA30_72=='T') ) {
                    switch ( input.LA(4) ) {
                    case 'H':
                        {
                        int LA30_151 = input.LA(5);

                        if ( (LA30_151=='I') ) {
                            int LA30_170 = input.LA(6);

                            if ( (LA30_170=='N') ) {
                                int LA30_182 = input.LA(7);

                                if ( (LA30_182=='G') ) {
                                    int LA30_187 = input.LA(8);

                                    if ( (LA30_187=='.'||(LA30_187>='0' && LA30_187<='9')||(LA30_187>='A' && LA30_187<='Z')||LA30_187=='_'||(LA30_187>='a' && LA30_187<='z')) ) {
                                        alt30=45;
                                    }
                                    else {
                                        alt30=10;}
                                }
                                else {
                                    alt30=45;}
                            }
                            else {
                                alt30=45;}
                        }
                        else {
                            alt30=45;}
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
                        alt30=45;
                        }
                        break;
                    default:
                        alt30=24;}

                }
                else {
                    alt30=45;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case 'T':
            {
            switch ( input.LA(2) ) {
            case 'I':
                {
                int LA30_73 = input.LA(3);

                if ( (LA30_73=='M') ) {
                    int LA30_126 = input.LA(4);

                    if ( (LA30_126=='E') ) {
                        int LA30_152 = input.LA(5);

                        if ( (LA30_152=='S') ) {
                            int LA30_171 = input.LA(6);

                            if ( (LA30_171=='.'||(LA30_171>='0' && LA30_171<='9')||(LA30_171>='A' && LA30_171<='Z')||LA30_171=='_'||(LA30_171>='a' && LA30_171<='z')) ) {
                                alt30=45;
                            }
                            else {
                                alt30=35;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 'R':
                {
                int LA30_74 = input.LA(3);

                if ( (LA30_74=='U') ) {
                    int LA30_127 = input.LA(4);

                    if ( (LA30_127=='E') ) {
                        int LA30_153 = input.LA(5);

                        if ( (LA30_153=='.'||(LA30_153>='0' && LA30_153<='9')||(LA30_153>='A' && LA30_153<='Z')||LA30_153=='_'||(LA30_153>='a' && LA30_153<='z')) ) {
                            alt30=45;
                        }
                        else {
                            alt30=11;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case 't':
            {
            switch ( input.LA(2) ) {
            case 'i':
                {
                int LA30_75 = input.LA(3);

                if ( (LA30_75=='m') ) {
                    int LA30_128 = input.LA(4);

                    if ( (LA30_128=='e') ) {
                        int LA30_154 = input.LA(5);

                        if ( (LA30_154=='s') ) {
                            int LA30_173 = input.LA(6);

                            if ( (LA30_173=='.'||(LA30_173>='0' && LA30_173<='9')||(LA30_173>='A' && LA30_173<='Z')||LA30_173=='_'||(LA30_173>='a' && LA30_173<='z')) ) {
                                alt30=45;
                            }
                            else {
                                alt30=35;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 'r':
                {
                int LA30_76 = input.LA(3);

                if ( (LA30_76=='u') ) {
                    int LA30_129 = input.LA(4);

                    if ( (LA30_129=='e') ) {
                        int LA30_155 = input.LA(5);

                        if ( (LA30_155=='.'||(LA30_155>='0' && LA30_155<='9')||(LA30_155>='A' && LA30_155<='Z')||LA30_155=='_'||(LA30_155>='a' && LA30_155<='z')) ) {
                            alt30=45;
                        }
                        else {
                            alt30=11;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case 'F':
            {
            int LA30_14 = input.LA(2);

            if ( (LA30_14=='A') ) {
                int LA30_77 = input.LA(3);

                if ( (LA30_77=='L') ) {
                    int LA30_130 = input.LA(4);

                    if ( (LA30_130=='S') ) {
                        int LA30_156 = input.LA(5);

                        if ( (LA30_156=='E') ) {
                            int LA30_174 = input.LA(6);

                            if ( (LA30_174=='.'||(LA30_174>='0' && LA30_174<='9')||(LA30_174>='A' && LA30_174<='Z')||LA30_174=='_'||(LA30_174>='a' && LA30_174<='z')) ) {
                                alt30=45;
                            }
                            else {
                                alt30=12;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'f':
            {
            int LA30_15 = input.LA(2);

            if ( (LA30_15=='a') ) {
                int LA30_78 = input.LA(3);

                if ( (LA30_78=='l') ) {
                    int LA30_131 = input.LA(4);

                    if ( (LA30_131=='s') ) {
                        int LA30_157 = input.LA(5);

                        if ( (LA30_157=='e') ) {
                            int LA30_175 = input.LA(6);

                            if ( (LA30_175=='.'||(LA30_175>='0' && LA30_175<='9')||(LA30_175>='A' && LA30_175<='Z')||LA30_175=='_'||(LA30_175>='a' && LA30_175<='z')) ) {
                                alt30=45;
                            }
                            else {
                                alt30=12;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case '(':
            {
            alt30=13;
            }
            break;
        case ')':
            {
            alt30=14;
            }
            break;
        case '[':
            {
            alt30=15;
            }
            break;
        case ']':
            {
            alt30=16;
            }
            break;
        case '{':
            {
            alt30=17;
            }
            break;
        case '}':
            {
            alt30=18;
            }
            break;
        case ',':
        case ';':
            {
            alt30=19;
            }
            break;
        case '.':
            {
            alt30=20;
            }
            break;
        case '=':
            {
            int LA30_24 = input.LA(2);

            if ( (LA30_24=='=') ) {
                alt30=25;
            }
            else {
                alt30=21;}
            }
            break;
        case '<':
            {
            switch ( input.LA(2) ) {
            case '-':
                {
                alt30=21;
                }
                break;
            case '=':
                {
                alt30=30;
                }
                break;
            default:
                alt30=28;}

            }
            break;
        case '|':
            {
            int LA30_26 = input.LA(2);

            if ( (LA30_26=='|') ) {
                alt30=22;
            }
            else {
                alt30=31;}
            }
            break;
        case 'O':
            {
            int LA30_27 = input.LA(2);

            if ( (LA30_27=='R') ) {
                int LA30_85 = input.LA(3);

                if ( (LA30_85=='.'||(LA30_85>='0' && LA30_85<='9')||(LA30_85>='A' && LA30_85<='Z')||LA30_85=='_'||(LA30_85>='a' && LA30_85<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=22;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'o':
            {
            int LA30_28 = input.LA(2);

            if ( (LA30_28=='r') ) {
                int LA30_86 = input.LA(3);

                if ( (LA30_86=='.'||(LA30_86>='0' && LA30_86<='9')||(LA30_86>='A' && LA30_86<='Z')||LA30_86=='_'||(LA30_86>='a' && LA30_86<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=22;}
            }
            else {
                alt30=45;}
            }
            break;
        case '&':
            {
            int LA30_29 = input.LA(2);

            if ( (LA30_29=='&') ) {
                alt30=23;
            }
            else {
                alt30=32;}
            }
            break;
        case 'A':
            {
            int LA30_30 = input.LA(2);

            if ( (LA30_30=='N') ) {
                int LA30_89 = input.LA(3);

                if ( (LA30_89=='D') ) {
                    int LA30_132 = input.LA(4);

                    if ( (LA30_132=='.'||(LA30_132>='0' && LA30_132<='9')||(LA30_132>='A' && LA30_132<='Z')||LA30_132=='_'||(LA30_132>='a' && LA30_132<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=23;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'a':
            {
            int LA30_31 = input.LA(2);

            if ( (LA30_31=='n') ) {
                int LA30_90 = input.LA(3);

                if ( (LA30_90=='d') ) {
                    int LA30_133 = input.LA(4);

                    if ( (LA30_133=='.'||(LA30_133>='0' && LA30_133<='9')||(LA30_133>='A' && LA30_133<='Z')||LA30_133=='_'||(LA30_133>='a' && LA30_133<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=23;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case '!':
            {
            int LA30_32 = input.LA(2);

            if ( (LA30_32=='=') ) {
                alt30=26;
            }
            else {
                alt30=24;}
            }
            break;
        case 'n':
            {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA30_93 = input.LA(3);

                if ( (LA30_93=='t') ) {
                    int LA30_134 = input.LA(4);

                    if ( (LA30_134=='.'||(LA30_134>='0' && LA30_134<='9')||(LA30_134>='A' && LA30_134<='Z')||LA30_134=='_'||(LA30_134>='a' && LA30_134<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=24;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 'e':
                {
                int LA30_94 = input.LA(3);

                if ( (LA30_94=='q') ) {
                    int LA30_135 = input.LA(4);

                    if ( (LA30_135=='.'||(LA30_135>='0' && LA30_135<='9')||(LA30_135>='A' && LA30_135<='Z')||LA30_135=='_'||(LA30_135>='a' && LA30_135<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=26;}
                }
                else {
                    alt30=45;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case 'e':
            {
            int LA30_34 = input.LA(2);

            if ( (LA30_34=='q') ) {
                int LA30_95 = input.LA(3);

                if ( (LA30_95=='.'||(LA30_95>='0' && LA30_95<='9')||(LA30_95>='A' && LA30_95<='Z')||LA30_95=='_'||(LA30_95>='a' && LA30_95<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=25;}
            }
            else {
                alt30=45;}
            }
            break;
        case '>':
            {
            int LA30_35 = input.LA(2);

            if ( (LA30_35=='=') ) {
                alt30=29;
            }
            else {
                alt30=27;}
            }
            break;
        case 'G':
            {
            int LA30_36 = input.LA(2);

            if ( (LA30_36=='T') ) {
                int LA30_98 = input.LA(3);

                if ( (LA30_98=='.'||(LA30_98>='0' && LA30_98<='9')||(LA30_98>='A' && LA30_98<='Z')||LA30_98=='_'||(LA30_98>='a' && LA30_98<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=27;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'g':
            {
            switch ( input.LA(2) ) {
            case 'e':
                {
                int LA30_99 = input.LA(3);

                if ( (LA30_99=='q') ) {
                    int LA30_136 = input.LA(4);

                    if ( (LA30_136=='.'||(LA30_136>='0' && LA30_136<='9')||(LA30_136>='A' && LA30_136<='Z')||LA30_136=='_'||(LA30_136>='a' && LA30_136<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=29;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 't':
                {
                int LA30_100 = input.LA(3);

                if ( (LA30_100=='.'||(LA30_100>='0' && LA30_100<='9')||(LA30_100>='A' && LA30_100<='Z')||LA30_100=='_'||(LA30_100>='a' && LA30_100<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=27;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case 'l':
            {
            switch ( input.LA(2) ) {
            case 't':
                {
                int LA30_101 = input.LA(3);

                if ( (LA30_101=='.'||(LA30_101>='0' && LA30_101<='9')||(LA30_101>='A' && LA30_101<='Z')||LA30_101=='_'||(LA30_101>='a' && LA30_101<='z')) ) {
                    alt30=45;
                }
                else {
                    alt30=28;}
                }
                break;
            case 'e':
                {
                int LA30_102 = input.LA(3);

                if ( (LA30_102=='q') ) {
                    int LA30_137 = input.LA(4);

                    if ( (LA30_137=='.'||(LA30_137>='0' && LA30_137<='9')||(LA30_137>='A' && LA30_137<='Z')||LA30_137=='_'||(LA30_137>='a' && LA30_137<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=30;}
                }
                else {
                    alt30=45;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case '^':
            {
            alt30=33;
            }
            break;
        case '~':
            {
            alt30=34;
            }
            break;
        case '*':
            {
            alt30=35;
            }
            break;
        case '/':
            {
            alt30=36;
            }
            break;
        case 'd':
            {
            int LA30_43 = input.LA(2);

            if ( (LA30_43=='i') ) {
                int LA30_103 = input.LA(3);

                if ( (LA30_103=='v') ) {
                    int LA30_138 = input.LA(4);

                    if ( (LA30_138=='i') ) {
                        int LA30_158 = input.LA(5);

                        if ( (LA30_158=='d') ) {
                            int LA30_176 = input.LA(6);

                            if ( (LA30_176=='e') ) {
                                int LA30_184 = input.LA(7);

                                if ( (LA30_184=='.'||(LA30_184>='0' && LA30_184<='9')||(LA30_184>='A' && LA30_184<='Z')||LA30_184=='_'||(LA30_184>='a' && LA30_184<='z')) ) {
                                    alt30=45;
                                }
                                else {
                                    alt30=36;}
                            }
                            else {
                                alt30=45;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case '-':
            {
            int LA30_44 = input.LA(2);

            if ( ((LA30_44>='0' && LA30_44<='9')) ) {
                alt30=1;
            }
            else {
                alt30=38;}
            }
            break;
        case 'P':
            {
            int LA30_45 = input.LA(2);

            if ( (LA30_45=='L') ) {
                int LA30_105 = input.LA(3);

                if ( (LA30_105=='U') ) {
                    int LA30_139 = input.LA(4);

                    if ( (LA30_139=='S') ) {
                        int LA30_159 = input.LA(5);

                        if ( (LA30_159=='.'||(LA30_159>='0' && LA30_159<='9')||(LA30_159>='A' && LA30_159<='Z')||LA30_159=='_'||(LA30_159>='a' && LA30_159<='z')) ) {
                            alt30=45;
                        }
                        else {
                            alt30=37;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'p':
            {
            int LA30_46 = input.LA(2);

            if ( (LA30_46=='l') ) {
                int LA30_106 = input.LA(3);

                if ( (LA30_106=='u') ) {
                    int LA30_140 = input.LA(4);

                    if ( (LA30_140=='s') ) {
                        int LA30_160 = input.LA(5);

                        if ( (LA30_160=='.'||(LA30_160>='0' && LA30_160<='9')||(LA30_160>='A' && LA30_160<='Z')||LA30_160=='_'||(LA30_160>='a' && LA30_160<='z')) ) {
                            alt30=45;
                        }
                        else {
                            alt30=37;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
            }
            else {
                alt30=45;}
            }
            break;
        case 'm':
            {
            switch ( input.LA(2) ) {
            case 'i':
                {
                int LA30_107 = input.LA(3);

                if ( (LA30_107=='n') ) {
                    int LA30_141 = input.LA(4);

                    if ( (LA30_141=='u') ) {
                        int LA30_161 = input.LA(5);

                        if ( (LA30_161=='s') ) {
                            int LA30_177 = input.LA(6);

                            if ( (LA30_177=='.'||(LA30_177>='0' && LA30_177<='9')||(LA30_177>='A' && LA30_177<='Z')||LA30_177=='_'||(LA30_177>='a' && LA30_177<='z')) ) {
                                alt30=45;
                            }
                            else {
                                alt30=38;}
                        }
                        else {
                            alt30=45;}
                    }
                    else {
                        alt30=45;}
                }
                else {
                    alt30=45;}
                }
                break;
            case 'o':
                {
                int LA30_108 = input.LA(3);

                if ( (LA30_108=='d') ) {
                    int LA30_142 = input.LA(4);

                    if ( (LA30_142=='.'||(LA30_142>='0' && LA30_142<='9')||(LA30_142>='A' && LA30_142<='Z')||LA30_142=='_'||(LA30_142>='a' && LA30_142<='z')) ) {
                        alt30=45;
                    }
                    else {
                        alt30=39;}
                }
                else {
                    alt30=45;}
                }
                break;
            default:
                alt30=45;}

            }
            break;
        case '%':
            {
            alt30=39;
            }
            break;
        case '?':
            {
            alt30=40;
            }
            break;
        case ':':
            {
            alt30=41;
            }
            break;
        case '\'':
            {
            int LA30_51 = input.LA(2);

            if ( (LA30_51=='\t'||LA30_51=='\r'||(LA30_51>=' ' && LA30_51<='~')) ) {
                alt30=45;
            }
            else {
                alt30=42;}
            }
            break;
        case '\"':
            {
            int LA30_52 = input.LA(2);

            if ( (LA30_52=='\t'||LA30_52=='\r'||(LA30_52>=' ' && LA30_52<='~')) ) {
                alt30=44;
            }
            else {
                alt30=43;}
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
        case 'r':
        case 's':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            {
            alt30=45;
            }
            break;
        case '$':
            {
            alt30=46;
            }
            break;
        case '\t':
        case '\n':
        case '\r':
        case ' ':
            {
            alt30=47;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( NUMBER | BIND | IF | DO | RULE | CLASS | METHOD | LINE | ENDRULE | NOTHING | TRUE | FALSE | LPAREN | RPAREN | LSQUARE | RSQUARE | LBRACE | RBRACE | SEPR | DOT | ASSIGN | OR | AND | NOT | EQ | NEQ | GT | LT | GEQ | LEQ | BOR | BAND | BXOR | TWIDDLE | MUL | DIV | PLUS | MINUS | MOD | TERN_IF | COLON | QUOTE | DQUOTE | STRING | SYMBOL | DOLLARSYM | WS );", 30, 0, input);

            throw nvae;
        }

        switch (alt30) {
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
                // dd/grammar/ECAToken.g:1:78: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 14 :
                // dd/grammar/ECAToken.g:1:85: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 15 :
                // dd/grammar/ECAToken.g:1:92: LSQUARE
                {
                mLSQUARE(); 

                }
                break;
            case 16 :
                // dd/grammar/ECAToken.g:1:100: RSQUARE
                {
                mRSQUARE(); 

                }
                break;
            case 17 :
                // dd/grammar/ECAToken.g:1:108: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 18 :
                // dd/grammar/ECAToken.g:1:115: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 19 :
                // dd/grammar/ECAToken.g:1:122: SEPR
                {
                mSEPR(); 

                }
                break;
            case 20 :
                // dd/grammar/ECAToken.g:1:127: DOT
                {
                mDOT(); 

                }
                break;
            case 21 :
                // dd/grammar/ECAToken.g:1:131: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 22 :
                // dd/grammar/ECAToken.g:1:138: OR
                {
                mOR(); 

                }
                break;
            case 23 :
                // dd/grammar/ECAToken.g:1:141: AND
                {
                mAND(); 

                }
                break;
            case 24 :
                // dd/grammar/ECAToken.g:1:145: NOT
                {
                mNOT(); 

                }
                break;
            case 25 :
                // dd/grammar/ECAToken.g:1:149: EQ
                {
                mEQ(); 

                }
                break;
            case 26 :
                // dd/grammar/ECAToken.g:1:152: NEQ
                {
                mNEQ(); 

                }
                break;
            case 27 :
                // dd/grammar/ECAToken.g:1:156: GT
                {
                mGT(); 

                }
                break;
            case 28 :
                // dd/grammar/ECAToken.g:1:159: LT
                {
                mLT(); 

                }
                break;
            case 29 :
                // dd/grammar/ECAToken.g:1:162: GEQ
                {
                mGEQ(); 

                }
                break;
            case 30 :
                // dd/grammar/ECAToken.g:1:166: LEQ
                {
                mLEQ(); 

                }
                break;
            case 31 :
                // dd/grammar/ECAToken.g:1:170: BOR
                {
                mBOR(); 

                }
                break;
            case 32 :
                // dd/grammar/ECAToken.g:1:174: BAND
                {
                mBAND(); 

                }
                break;
            case 33 :
                // dd/grammar/ECAToken.g:1:179: BXOR
                {
                mBXOR(); 

                }
                break;
            case 34 :
                // dd/grammar/ECAToken.g:1:184: TWIDDLE
                {
                mTWIDDLE(); 

                }
                break;
            case 35 :
                // dd/grammar/ECAToken.g:1:192: MUL
                {
                mMUL(); 

                }
                break;
            case 36 :
                // dd/grammar/ECAToken.g:1:196: DIV
                {
                mDIV(); 

                }
                break;
            case 37 :
                // dd/grammar/ECAToken.g:1:200: PLUS
                {
                mPLUS(); 

                }
                break;
            case 38 :
                // dd/grammar/ECAToken.g:1:205: MINUS
                {
                mMINUS(); 

                }
                break;
            case 39 :
                // dd/grammar/ECAToken.g:1:211: MOD
                {
                mMOD(); 

                }
                break;
            case 40 :
                // dd/grammar/ECAToken.g:1:215: TERN_IF
                {
                mTERN_IF(); 

                }
                break;
            case 41 :
                // dd/grammar/ECAToken.g:1:223: COLON
                {
                mCOLON(); 

                }
                break;
            case 42 :
                // dd/grammar/ECAToken.g:1:229: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 43 :
                // dd/grammar/ECAToken.g:1:235: DQUOTE
                {
                mDQUOTE(); 

                }
                break;
            case 44 :
                // dd/grammar/ECAToken.g:1:242: STRING
                {
                mSTRING(); 

                }
                break;
            case 45 :
                // dd/grammar/ECAToken.g:1:249: SYMBOL
                {
                mSYMBOL(); 

                }
                break;
            case 46 :
                // dd/grammar/ECAToken.g:1:256: DOLLARSYM
                {
                mDOLLARSYM(); 

                }
                break;
            case 47 :
                // dd/grammar/ECAToken.g:1:266: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA27 dfa27 = new DFA27(this);
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
    static final String DFA27_eotS =
        "\1\uffff\2\3\2\uffff";
    static final String DFA27_eofS =
        "\5\uffff";
    static final String DFA27_minS =
        "\1\101\2\56\2\uffff";
    static final String DFA27_maxS =
        "\3\172\2\uffff";
    static final String DFA27_acceptS =
        "\3\uffff\1\2\1\1";
    static final String DFA27_specialS =
        "\5\uffff}>";
    static final String[] DFA27_transitionS = {
            "\32\1\4\uffff\1\1\1\uffff\32\1",
            "\1\4\1\uffff\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
            "\1\4\1\uffff\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
            "",
            ""
    };

    static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
    static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
    static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
    static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
    static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
    static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
    static final short[][] DFA27_transition;

    static {
        int numStates = DFA27_transitionS.length;
        DFA27_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
        }
    }

    class DFA27 extends DFA {

        public DFA27(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 27;
            this.eot = DFA27_eot;
            this.eof = DFA27_eof;
            this.min = DFA27_min;
            this.max = DFA27_max;
            this.accept = DFA27_accept;
            this.special = DFA27_special;
            this.transition = DFA27_transition;
        }
        public String getDescription() {
            return "253:1: fragment DOTSYM : ( BARESYM DOT DOTSYM | BARESYM );";
        }
    }
 

}