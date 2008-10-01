// $ANTLR 3.0.1 dd/grammar/ECAToken.g 2008-10-01 15:50:04

package org.jboss.jbossts.orchestration.rule.grammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ECATokenLexer extends Lexer {
    public static final int MINUS=51;
    public static final int NUMBER=12;
    public static final int METHOD=18;
    public static final int FLOAT=11;
    public static final int FALSE=23;
    public static final int POSDIGIT=5;
    public static final int TWIDDLE=47;
    public static final int LEQ=43;
    public static final int RULE=16;
    public static final int MOD=52;
    public static final int GEQ=42;
    public static final int DQUOTE=58;
    public static final int BOR=44;
    public static final int OR=35;
    public static final int BAREINT=7;
    public static final int LBRACE=30;
    public static final int NEWLINE=60;
    public static final int DOT=33;
    public static final int RBRACE=31;
    public static final int INTEGER=8;
    public static final int AND=36;
    public static final int ASSIGN=34;
    public static final int SYMBOL=66;
    public static final int RPAREN=27;
    public static final int SIGN=6;
    public static final int LPAREN=26;
    public static final int PLUS=50;
    public static final int DIGIT=4;
    public static final int LINE=19;
    public static final int BAND=45;
    public static final int NEQ=39;
    public static final int SPACE=59;
    public static final int LETTER=55;
    public static final int LSQUARE=28;
    public static final int DO=15;
    public static final int POINT=9;
    public static final int BARESYM=63;
    public static final int NOTHING=21;
    public static final int SEPR=32;
    public static final int WS=69;
    public static final int STRING=62;
    public static final int EQ=38;
    public static final int QUOTSYM=64;
    public static final int LT=41;
    public static final int GT=40;
    public static final int DOLLAR=67;
    public static final int RSQUARE=29;
    public static final int QUOTE=57;
    public static final int TERN_IF=53;
    public static final int MUL=48;
    public static final int CLASS=17;
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
    public static final int UNDERSCORE=56;
    public static final int THROW=25;
    public static final int DOLLARSYM=68;
    public ECATokenLexer() {;} 
    public ECATokenLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "dd/grammar/ECAToken.g"; }

    // $ANTLR start DIGIT
    public final void mDIGIT() throws RecognitionException {
        try {
            // dd/grammar/ECAToken.g:33:7: ( '0' .. '9' )
            // dd/grammar/ECAToken.g:33:9: '0' .. '9'
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
            // dd/grammar/ECAToken.g:37:10: ( '1' .. '9' )
            // dd/grammar/ECAToken.g:37:12: '1' .. '9'
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
            // dd/grammar/ECAToken.g:41:6: ( '+' | '-' )
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
            // dd/grammar/ECAToken.g:45:9: ( '0' | ( POSDIGIT ( DIGIT )* ) )
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
                    new NoViableAltException("44:1: fragment BAREINT : ( '0' | ( POSDIGIT ( DIGIT )* ) );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // dd/grammar/ECAToken.g:45:11: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:45:17: ( POSDIGIT ( DIGIT )* )
                    {
                    // dd/grammar/ECAToken.g:45:17: ( POSDIGIT ( DIGIT )* )
                    // dd/grammar/ECAToken.g:45:18: POSDIGIT ( DIGIT )*
                    {
                    mPOSDIGIT(); 
                    // dd/grammar/ECAToken.g:45:27: ( DIGIT )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // dd/grammar/ECAToken.g:45:28: DIGIT
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
            // dd/grammar/ECAToken.g:48:9: ( ( SIGN )? BAREINT )
            // dd/grammar/ECAToken.g:48:11: ( SIGN )? BAREINT
            {
            // dd/grammar/ECAToken.g:48:11: ( SIGN )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='+'||LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // dd/grammar/ECAToken.g:48:11: SIGN
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
            // dd/grammar/ECAToken.g:52:7: ( '.' )
            // dd/grammar/ECAToken.g:52:9: '.'
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
            // dd/grammar/ECAToken.g:56:9: ( ( 'e' | 'E' ) INTEGER )
            // dd/grammar/ECAToken.g:56:12: ( 'e' | 'E' ) INTEGER
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
            // dd/grammar/ECAToken.g:61:7: ( INTEGER POINT ( BAREINT )? ( EXPPART )? )
            // dd/grammar/ECAToken.g:61:9: INTEGER POINT ( BAREINT )? ( EXPPART )?
            {
            mINTEGER(); 
            mPOINT(); 
            // dd/grammar/ECAToken.g:61:23: ( BAREINT )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // dd/grammar/ECAToken.g:61:23: BAREINT
                    {
                    mBAREINT(); 

                    }
                    break;

            }

            // dd/grammar/ECAToken.g:61:32: ( EXPPART )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='E'||LA5_0=='e') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // dd/grammar/ECAToken.g:61:32: EXPPART
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
            // dd/grammar/ECAToken.g:64:8: ( INTEGER | FLOAT )
            int alt6=2;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // dd/grammar/ECAToken.g:64:10: INTEGER
                    {
                    mINTEGER(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:64:20: FLOAT
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
            // dd/grammar/ECAToken.g:69:6: ( 'BIND' )
            // dd/grammar/ECAToken.g:69:8: 'BIND'
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
            // dd/grammar/ECAToken.g:72:4: ( 'IF' )
            // dd/grammar/ECAToken.g:72:6: 'IF'
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
            // dd/grammar/ECAToken.g:75:4: ( 'DO' )
            // dd/grammar/ECAToken.g:75:6: 'DO'
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
            // dd/grammar/ECAToken.g:78:6: ( 'RULE' )
            // dd/grammar/ECAToken.g:78:8: 'RULE'
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
            // dd/grammar/ECAToken.g:81:7: ( 'CLASS' )
            // dd/grammar/ECAToken.g:81:9: 'CLASS'
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
            // dd/grammar/ECAToken.g:84:8: ( 'METHOD' )
            // dd/grammar/ECAToken.g:84:10: 'METHOD'
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
            // dd/grammar/ECAToken.g:87:6: ( 'LINE' )
            // dd/grammar/ECAToken.g:87:8: 'LINE'
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
            // dd/grammar/ECAToken.g:90:9: ( 'ENDRULE' )
            // dd/grammar/ECAToken.g:90:11: 'ENDRULE'
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
            // dd/grammar/ECAToken.g:93:9: ( 'NOTHING' )
            // dd/grammar/ECAToken.g:93:11: 'NOTHING'
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
            // dd/grammar/ECAToken.g:96:6: ( 'TRUE' | 'true' )
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
                    new NoViableAltException("96:1: TRUE : ( 'TRUE' | 'true' );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // dd/grammar/ECAToken.g:96:9: 'TRUE'
                    {
                    match("TRUE"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:96:18: 'true'
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
            // dd/grammar/ECAToken.g:99:7: ( 'FALSE' | 'false' )
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
                    new NoViableAltException("99:1: FALSE : ( 'FALSE' | 'false' );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // dd/grammar/ECAToken.g:99:9: 'FALSE'
                    {
                    match("FALSE"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:99:17: 'false'
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
            // dd/grammar/ECAToken.g:102:8: ( 'RETURN' | 'return' )
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
                    new NoViableAltException("102:1: RETURN : ( 'RETURN' | 'return' );", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // dd/grammar/ECAToken.g:102:10: 'RETURN'
                    {
                    match("RETURN"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:102:19: 'return'
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

    // $ANTLR start THROW
    public final void mTHROW() throws RecognitionException {
        try {
            int _type = THROW;
            // dd/grammar/ECAToken.g:105:7: ( 'THROW' | 'throw' )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='T') ) {
                alt10=1;
            }
            else if ( (LA10_0=='t') ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("105:1: THROW : ( 'THROW' | 'throw' );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // dd/grammar/ECAToken.g:105:9: 'THROW'
                    {
                    match("THROW"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:105:17: 'throw'
                    {
                    match("throw"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THROW

    // $ANTLR start LPAREN
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            // dd/grammar/ECAToken.g:110:8: ( '(' )
            // dd/grammar/ECAToken.g:110:10: '('
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
            // dd/grammar/ECAToken.g:113:8: ( ')' )
            // dd/grammar/ECAToken.g:113:10: ')'
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
            // dd/grammar/ECAToken.g:116:9: ( '\\[' )
            // dd/grammar/ECAToken.g:116:11: '\\['
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
            // dd/grammar/ECAToken.g:119:9: ( '\\]' )
            // dd/grammar/ECAToken.g:119:11: '\\]'
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
            // dd/grammar/ECAToken.g:122:8: ( '{' )
            // dd/grammar/ECAToken.g:122:10: '{'
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
            // dd/grammar/ECAToken.g:125:8: ( '}' )
            // dd/grammar/ECAToken.g:125:10: '}'
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
            // dd/grammar/ECAToken.g:130:6: ( ';' | ',' )
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
            // dd/grammar/ECAToken.g:136:5: ( '.' )
            // dd/grammar/ECAToken.g:136:7: '.'
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
            // dd/grammar/ECAToken.g:141:8: ( '=' | '<--' )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='=') ) {
                alt11=1;
            }
            else if ( (LA11_0=='<') ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("141:1: ASSIGN : ( '=' | '<--' );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // dd/grammar/ECAToken.g:141:10: '='
                    {
                    match('='); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:142:4: '<--'
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
            // dd/grammar/ECAToken.g:147:4: ( '||' | 'OR' | 'or' )
            int alt12=3;
            switch ( input.LA(1) ) {
            case '|':
                {
                alt12=1;
                }
                break;
            case 'O':
                {
                alt12=2;
                }
                break;
            case 'o':
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("147:1: OR : ( '||' | 'OR' | 'or' );", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // dd/grammar/ECAToken.g:147:6: '||'
                    {
                    match("||"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:148:4: 'OR'
                    {
                    match("OR"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:149:4: 'or'
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
            // dd/grammar/ECAToken.g:152:5: ( '&&' | 'AND' | 'and' )
            int alt13=3;
            switch ( input.LA(1) ) {
            case '&':
                {
                alt13=1;
                }
                break;
            case 'A':
                {
                alt13=2;
                }
                break;
            case 'a':
                {
                alt13=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("152:1: AND : ( '&&' | 'AND' | 'and' );", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // dd/grammar/ECAToken.g:152:7: '&&'
                    {
                    match("&&"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:153:4: 'AND'
                    {
                    match("AND"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:154:4: 'and'
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
            // dd/grammar/ECAToken.g:157:5: ( '!' | 'NOT' | 'not' )
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
                    new NoViableAltException("157:1: NOT : ( '!' | 'NOT' | 'not' );", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // dd/grammar/ECAToken.g:157:7: '!'
                    {
                    match('!'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:158:4: 'NOT'
                    {
                    match("NOT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:159:4: 'not'
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
            // dd/grammar/ECAToken.g:164:4: ( '==' | 'EQ' | 'eq' )
            int alt15=3;
            switch ( input.LA(1) ) {
            case '=':
                {
                alt15=1;
                }
                break;
            case 'E':
                {
                alt15=2;
                }
                break;
            case 'e':
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("164:1: EQ : ( '==' | 'EQ' | 'eq' );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // dd/grammar/ECAToken.g:164:6: '=='
                    {
                    match("=="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:165:4: 'EQ'
                    {
                    match("EQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:166:4: 'eq'
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
            // dd/grammar/ECAToken.g:169:5: ( '!=' | 'NEQ' | 'neq' )
            int alt16=3;
            switch ( input.LA(1) ) {
            case '!':
                {
                alt16=1;
                }
                break;
            case 'N':
                {
                alt16=2;
                }
                break;
            case 'n':
                {
                alt16=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("169:1: NEQ : ( '!=' | 'NEQ' | 'neq' );", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // dd/grammar/ECAToken.g:169:7: '!='
                    {
                    match("!="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:170:4: 'NEQ'
                    {
                    match("NEQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:171:4: 'neq'
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
            // dd/grammar/ECAToken.g:174:4: ( '>' | 'GT' | 'gt' )
            int alt17=3;
            switch ( input.LA(1) ) {
            case '>':
                {
                alt17=1;
                }
                break;
            case 'G':
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
                    new NoViableAltException("174:1: GT : ( '>' | 'GT' | 'gt' );", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // dd/grammar/ECAToken.g:174:6: '>'
                    {
                    match('>'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:175:4: 'GT'
                    {
                    match("GT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:176:4: 'gt'
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
            // dd/grammar/ECAToken.g:179:4: ( '<' | 'LT' | 'lt' )
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
                    new NoViableAltException("179:1: LT : ( '<' | 'LT' | 'lt' );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // dd/grammar/ECAToken.g:179:6: '<'
                    {
                    match('<'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:180:4: 'LT'
                    {
                    match("LT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:181:4: 'lt'
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
            // dd/grammar/ECAToken.g:184:5: ( '>=' | 'EQ' | 'geq' )
            int alt19=3;
            switch ( input.LA(1) ) {
            case '>':
                {
                alt19=1;
                }
                break;
            case 'E':
                {
                alt19=2;
                }
                break;
            case 'g':
                {
                alt19=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("184:1: GEQ : ( '>=' | 'EQ' | 'geq' );", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // dd/grammar/ECAToken.g:184:7: '>='
                    {
                    match(">="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:185:4: 'EQ'
                    {
                    match("EQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:186:4: 'geq'
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
            // dd/grammar/ECAToken.g:189:5: ( '<=' | 'LEQ' | 'leq' )
            int alt20=3;
            switch ( input.LA(1) ) {
            case '<':
                {
                alt20=1;
                }
                break;
            case 'L':
                {
                alt20=2;
                }
                break;
            case 'l':
                {
                alt20=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("189:1: LEQ : ( '<=' | 'LEQ' | 'leq' );", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // dd/grammar/ECAToken.g:189:7: '<='
                    {
                    match("<="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:190:4: 'LEQ'
                    {
                    match("LEQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:191:4: 'leq'
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
            // dd/grammar/ECAToken.g:196:5: ( '|' )
            // dd/grammar/ECAToken.g:196:7: '|'
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
            // dd/grammar/ECAToken.g:199:6: ( '&' )
            // dd/grammar/ECAToken.g:199:8: '&'
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
            // dd/grammar/ECAToken.g:202:6: ( '^' )
            // dd/grammar/ECAToken.g:202:8: '^'
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
            // dd/grammar/ECAToken.g:205:9: ( '~' )
            // dd/grammar/ECAToken.g:205:11: '~'
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
            // dd/grammar/ECAToken.g:210:5: ( '*' | 'TIMES' | 'times' )
            int alt21=3;
            switch ( input.LA(1) ) {
            case '*':
                {
                alt21=1;
                }
                break;
            case 'T':
                {
                alt21=2;
                }
                break;
            case 't':
                {
                alt21=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("210:1: MUL : ( '*' | 'TIMES' | 'times' );", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // dd/grammar/ECAToken.g:210:7: '*'
                    {
                    match('*'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:211:4: 'TIMES'
                    {
                    match("TIMES"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:212:4: 'times'
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
            // dd/grammar/ECAToken.g:215:5: ( '/' | 'DIVIDE' | 'divide' )
            int alt22=3;
            switch ( input.LA(1) ) {
            case '/':
                {
                alt22=1;
                }
                break;
            case 'D':
                {
                alt22=2;
                }
                break;
            case 'd':
                {
                alt22=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("215:1: DIV : ( '/' | 'DIVIDE' | 'divide' );", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // dd/grammar/ECAToken.g:215:7: '/'
                    {
                    match('/'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:216:4: 'DIVIDE'
                    {
                    match("DIVIDE"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:217:4: 'divide'
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
            // dd/grammar/ECAToken.g:220:6: ( '+' | 'PLUS' | 'plus' )
            int alt23=3;
            switch ( input.LA(1) ) {
            case '+':
                {
                alt23=1;
                }
                break;
            case 'P':
                {
                alt23=2;
                }
                break;
            case 'p':
                {
                alt23=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("220:1: PLUS : ( '+' | 'PLUS' | 'plus' );", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // dd/grammar/ECAToken.g:220:8: '+'
                    {
                    match('+'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:221:4: 'PLUS'
                    {
                    match("PLUS"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:222:4: 'plus'
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
            // dd/grammar/ECAToken.g:225:7: ( '-' | 'MINUS' | 'minus' )
            int alt24=3;
            switch ( input.LA(1) ) {
            case '-':
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
                    new NoViableAltException("225:1: MINUS : ( '-' | 'MINUS' | 'minus' );", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // dd/grammar/ECAToken.g:225:9: '-'
                    {
                    match('-'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:226:4: 'MINUS'
                    {
                    match("MINUS"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:227:4: 'minus'
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
            // dd/grammar/ECAToken.g:230:5: ( '%' | 'MOD' | 'mod' )
            int alt25=3;
            switch ( input.LA(1) ) {
            case '%':
                {
                alt25=1;
                }
                break;
            case 'M':
                {
                alt25=2;
                }
                break;
            case 'm':
                {
                alt25=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("230:1: MOD : ( '%' | 'MOD' | 'mod' );", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // dd/grammar/ECAToken.g:230:7: '%'
                    {
                    match('%'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:231:4: 'MOD'
                    {
                    match("MOD"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:232:4: 'mod'
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
            // dd/grammar/ECAToken.g:237:9: ( '?' )
            // dd/grammar/ECAToken.g:237:11: '?'
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
            // dd/grammar/ECAToken.g:240:7: ( ':' )
            // dd/grammar/ECAToken.g:240:9: ':'
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
            // dd/grammar/ECAToken.g:246:8: ( 'a' .. 'z' | 'A' .. 'Z' )
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
            // dd/grammar/ECAToken.g:250:12: ( '_' )
            // dd/grammar/ECAToken.g:250:14: '_'
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
            // dd/grammar/ECAToken.g:253:7: ( '\\'' )
            // dd/grammar/ECAToken.g:253:9: '\\''
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
            // dd/grammar/ECAToken.g:256:8: ( '\"' )
            // dd/grammar/ECAToken.g:256:10: '\"'
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
            // dd/grammar/ECAToken.g:260:7: ( ' ' | '\\t' | '\\r' )
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
            // dd/grammar/ECAToken.g:264:9: ( '\\n' )
            // dd/grammar/ECAToken.g:264:11: '\\n'
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
            // dd/grammar/ECAToken.g:268:7: ( '!' | '$' | '%' | '^' | '&' | '*' | '(' | ')' | '-' | '+' | '=' | '{' | '}' | '[' | ']' | ':' | ';' | '@' | '~' | '#' | '|' | '\\\\' | '`' | ',' | '<' | '.' | '>' | '/' | '?' )
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
            // dd/grammar/ECAToken.g:271:8: ( DQUOTE ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )* DQUOTE )
            // dd/grammar/ECAToken.g:271:10: DQUOTE ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )* DQUOTE
            {
            mDQUOTE(); 
            // dd/grammar/ECAToken.g:271:17: ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0=='\t'||LA26_0=='\r'||(LA26_0>=' ' && LA26_0<='!')||(LA26_0>='#' && LA26_0<='~')) ) {
                    alt26=1;
                }


                switch (alt26) {
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
            	    break loop26;
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
            // dd/grammar/ECAToken.g:275:9: ( ( LETTER | UNDERSCORE ) ( LETTER | DIGIT | UNDERSCORE )* )
            // dd/grammar/ECAToken.g:275:11: ( LETTER | UNDERSCORE ) ( LETTER | DIGIT | UNDERSCORE )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // dd/grammar/ECAToken.g:275:33: ( LETTER | DIGIT | UNDERSCORE )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( ((LA27_0>='0' && LA27_0<='9')||(LA27_0>='A' && LA27_0<='Z')||LA27_0=='_'||(LA27_0>='a' && LA27_0<='z')) ) {
                    alt27=1;
                }


                switch (alt27) {
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
            	    break loop27;
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
            // dd/grammar/ECAToken.g:278:9: ( QUOTE ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE | SPACE )* QUOTE )
            // dd/grammar/ECAToken.g:278:11: QUOTE ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE | SPACE )* QUOTE
            {
            mQUOTE(); 
            // dd/grammar/ECAToken.g:278:17: ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE | SPACE )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0=='\t'||LA28_0=='\r'||(LA28_0>=' ' && LA28_0<='&')||(LA28_0>='(' && LA28_0<='~')) ) {
                    alt28=1;
                }


                switch (alt28) {
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
            	    break loop28;
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
            // dd/grammar/ECAToken.g:283:8: ( BARESYM DOT DOTSYM | BARESYM )
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // dd/grammar/ECAToken.g:283:10: BARESYM DOT DOTSYM
                    {
                    mBARESYM(); 
                    mDOT(); 
                    mDOTSYM(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:284:4: BARESYM
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
            // dd/grammar/ECAToken.g:287:8: ( DOTSYM | QUOTSYM )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( ((LA30_0>='A' && LA30_0<='Z')||LA30_0=='_'||(LA30_0>='a' && LA30_0<='z')) ) {
                alt30=1;
            }
            else if ( (LA30_0=='\'') ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("287:1: SYMBOL : ( DOTSYM | QUOTSYM );", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // dd/grammar/ECAToken.g:287:10: DOTSYM
                    {
                    mDOTSYM(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:288:4: QUOTSYM
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
            // dd/grammar/ECAToken.g:293:8: ( '$' )
            // dd/grammar/ECAToken.g:293:10: '$'
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
            // dd/grammar/ECAToken.g:298:11: ( DOLLAR ( BAREINT | BARESYM ) )
            // dd/grammar/ECAToken.g:298:13: DOLLAR ( BAREINT | BARESYM )
            {
            mDOLLAR(); 
            // dd/grammar/ECAToken.g:298:20: ( BAREINT | BARESYM )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( ((LA31_0>='0' && LA31_0<='9')) ) {
                alt31=1;
            }
            else if ( ((LA31_0>='A' && LA31_0<='Z')||LA31_0=='_'||(LA31_0>='a' && LA31_0<='z')) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("298:20: ( BAREINT | BARESYM )", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // dd/grammar/ECAToken.g:298:21: BAREINT
                    {
                    mBAREINT(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:298:31: BARESYM
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
            // dd/grammar/ECAToken.g:304:4: ( ( SPACE | NEWLINE ) )
            // dd/grammar/ECAToken.g:304:6: ( SPACE | NEWLINE )
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
        // dd/grammar/ECAToken.g:1:8: ( NUMBER | BIND | IF | DO | RULE | CLASS | METHOD | LINE | ENDRULE | NOTHING | TRUE | FALSE | RETURN | THROW | LPAREN | RPAREN | LSQUARE | RSQUARE | LBRACE | RBRACE | SEPR | DOT | ASSIGN | OR | AND | NOT | EQ | NEQ | GT | LT | GEQ | LEQ | BOR | BAND | BXOR | TWIDDLE | MUL | DIV | PLUS | MINUS | MOD | TERN_IF | COLON | QUOTE | DQUOTE | STRING | SYMBOL | DOLLARSYM | WS )
        int alt32=49;
        switch ( input.LA(1) ) {
        case '+':
            {
            int LA32_1 = input.LA(2);

            if ( ((LA32_1>='0' && LA32_1<='9')) ) {
                alt32=1;
            }
            else {
                alt32=39;}
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
            alt32=1;
            }
            break;
        case 'B':
            {
            int LA32_3 = input.LA(2);

            if ( (LA32_3=='I') ) {
                int LA32_58 = input.LA(3);

                if ( (LA32_58=='N') ) {
                    int LA32_117 = input.LA(4);

                    if ( (LA32_117=='D') ) {
                        int LA32_152 = input.LA(5);

                        if ( (LA32_152=='.'||(LA32_152>='0' && LA32_152<='9')||(LA32_152>='A' && LA32_152<='Z')||LA32_152=='_'||(LA32_152>='a' && LA32_152<='z')) ) {
                            alt32=47;
                        }
                        else {
                            alt32=2;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'I':
            {
            int LA32_4 = input.LA(2);

            if ( (LA32_4=='F') ) {
                int LA32_59 = input.LA(3);

                if ( (LA32_59=='.'||(LA32_59>='0' && LA32_59<='9')||(LA32_59>='A' && LA32_59<='Z')||LA32_59=='_'||(LA32_59>='a' && LA32_59<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=3;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'D':
            {
            switch ( input.LA(2) ) {
            case 'O':
                {
                int LA32_60 = input.LA(3);

                if ( (LA32_60=='.'||(LA32_60>='0' && LA32_60<='9')||(LA32_60>='A' && LA32_60<='Z')||LA32_60=='_'||(LA32_60>='a' && LA32_60<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=4;}
                }
                break;
            case 'I':
                {
                int LA32_61 = input.LA(3);

                if ( (LA32_61=='V') ) {
                    int LA32_120 = input.LA(4);

                    if ( (LA32_120=='I') ) {
                        int LA32_153 = input.LA(5);

                        if ( (LA32_153=='D') ) {
                            int LA32_176 = input.LA(6);

                            if ( (LA32_176=='E') ) {
                                int LA32_195 = input.LA(7);

                                if ( (LA32_195=='.'||(LA32_195>='0' && LA32_195<='9')||(LA32_195>='A' && LA32_195<='Z')||LA32_195=='_'||(LA32_195>='a' && LA32_195<='z')) ) {
                                    alt32=47;
                                }
                                else {
                                    alt32=38;}
                            }
                            else {
                                alt32=47;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 'R':
            {
            switch ( input.LA(2) ) {
            case 'U':
                {
                int LA32_62 = input.LA(3);

                if ( (LA32_62=='L') ) {
                    int LA32_121 = input.LA(4);

                    if ( (LA32_121=='E') ) {
                        int LA32_154 = input.LA(5);

                        if ( (LA32_154=='.'||(LA32_154>='0' && LA32_154<='9')||(LA32_154>='A' && LA32_154<='Z')||LA32_154=='_'||(LA32_154>='a' && LA32_154<='z')) ) {
                            alt32=47;
                        }
                        else {
                            alt32=5;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'E':
                {
                int LA32_63 = input.LA(3);

                if ( (LA32_63=='T') ) {
                    int LA32_122 = input.LA(4);

                    if ( (LA32_122=='U') ) {
                        int LA32_155 = input.LA(5);

                        if ( (LA32_155=='R') ) {
                            int LA32_178 = input.LA(6);

                            if ( (LA32_178=='N') ) {
                                int LA32_196 = input.LA(7);

                                if ( (LA32_196=='.'||(LA32_196>='0' && LA32_196<='9')||(LA32_196>='A' && LA32_196<='Z')||LA32_196=='_'||(LA32_196>='a' && LA32_196<='z')) ) {
                                    alt32=47;
                                }
                                else {
                                    alt32=13;}
                            }
                            else {
                                alt32=47;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 'C':
            {
            int LA32_7 = input.LA(2);

            if ( (LA32_7=='L') ) {
                int LA32_64 = input.LA(3);

                if ( (LA32_64=='A') ) {
                    int LA32_123 = input.LA(4);

                    if ( (LA32_123=='S') ) {
                        int LA32_156 = input.LA(5);

                        if ( (LA32_156=='S') ) {
                            int LA32_179 = input.LA(6);

                            if ( (LA32_179=='.'||(LA32_179>='0' && LA32_179<='9')||(LA32_179>='A' && LA32_179<='Z')||LA32_179=='_'||(LA32_179>='a' && LA32_179<='z')) ) {
                                alt32=47;
                            }
                            else {
                                alt32=6;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'M':
            {
            switch ( input.LA(2) ) {
            case 'I':
                {
                int LA32_65 = input.LA(3);

                if ( (LA32_65=='N') ) {
                    int LA32_124 = input.LA(4);

                    if ( (LA32_124=='U') ) {
                        int LA32_157 = input.LA(5);

                        if ( (LA32_157=='S') ) {
                            int LA32_180 = input.LA(6);

                            if ( (LA32_180=='.'||(LA32_180>='0' && LA32_180<='9')||(LA32_180>='A' && LA32_180<='Z')||LA32_180=='_'||(LA32_180>='a' && LA32_180<='z')) ) {
                                alt32=47;
                            }
                            else {
                                alt32=40;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'O':
                {
                int LA32_66 = input.LA(3);

                if ( (LA32_66=='D') ) {
                    int LA32_125 = input.LA(4);

                    if ( (LA32_125=='.'||(LA32_125>='0' && LA32_125<='9')||(LA32_125>='A' && LA32_125<='Z')||LA32_125=='_'||(LA32_125>='a' && LA32_125<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=41;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'E':
                {
                int LA32_67 = input.LA(3);

                if ( (LA32_67=='T') ) {
                    int LA32_126 = input.LA(4);

                    if ( (LA32_126=='H') ) {
                        int LA32_158 = input.LA(5);

                        if ( (LA32_158=='O') ) {
                            int LA32_181 = input.LA(6);

                            if ( (LA32_181=='D') ) {
                                int LA32_198 = input.LA(7);

                                if ( (LA32_198=='.'||(LA32_198>='0' && LA32_198<='9')||(LA32_198>='A' && LA32_198<='Z')||LA32_198=='_'||(LA32_198>='a' && LA32_198<='z')) ) {
                                    alt32=47;
                                }
                                else {
                                    alt32=7;}
                            }
                            else {
                                alt32=47;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 'L':
            {
            switch ( input.LA(2) ) {
            case 'T':
                {
                int LA32_68 = input.LA(3);

                if ( (LA32_68=='.'||(LA32_68>='0' && LA32_68<='9')||(LA32_68>='A' && LA32_68<='Z')||LA32_68=='_'||(LA32_68>='a' && LA32_68<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=30;}
                }
                break;
            case 'E':
                {
                int LA32_69 = input.LA(3);

                if ( (LA32_69=='Q') ) {
                    int LA32_127 = input.LA(4);

                    if ( (LA32_127=='.'||(LA32_127>='0' && LA32_127<='9')||(LA32_127>='A' && LA32_127<='Z')||LA32_127=='_'||(LA32_127>='a' && LA32_127<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=32;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'I':
                {
                int LA32_70 = input.LA(3);

                if ( (LA32_70=='N') ) {
                    int LA32_128 = input.LA(4);

                    if ( (LA32_128=='E') ) {
                        int LA32_159 = input.LA(5);

                        if ( (LA32_159=='.'||(LA32_159>='0' && LA32_159<='9')||(LA32_159>='A' && LA32_159<='Z')||LA32_159=='_'||(LA32_159>='a' && LA32_159<='z')) ) {
                            alt32=47;
                        }
                        else {
                            alt32=8;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 'E':
            {
            switch ( input.LA(2) ) {
            case 'N':
                {
                int LA32_71 = input.LA(3);

                if ( (LA32_71=='D') ) {
                    int LA32_129 = input.LA(4);

                    if ( (LA32_129=='R') ) {
                        int LA32_160 = input.LA(5);

                        if ( (LA32_160=='U') ) {
                            int LA32_183 = input.LA(6);

                            if ( (LA32_183=='L') ) {
                                int LA32_199 = input.LA(7);

                                if ( (LA32_199=='E') ) {
                                    int LA32_207 = input.LA(8);

                                    if ( (LA32_207=='.'||(LA32_207>='0' && LA32_207<='9')||(LA32_207>='A' && LA32_207<='Z')||LA32_207=='_'||(LA32_207>='a' && LA32_207<='z')) ) {
                                        alt32=47;
                                    }
                                    else {
                                        alt32=9;}
                                }
                                else {
                                    alt32=47;}
                            }
                            else {
                                alt32=47;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'Q':
                {
                int LA32_72 = input.LA(3);

                if ( (LA32_72=='.'||(LA32_72>='0' && LA32_72<='9')||(LA32_72>='A' && LA32_72<='Z')||LA32_72=='_'||(LA32_72>='a' && LA32_72<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=27;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 'N':
            {
            switch ( input.LA(2) ) {
            case 'O':
                {
                int LA32_73 = input.LA(3);

                if ( (LA32_73=='T') ) {
                    switch ( input.LA(4) ) {
                    case 'H':
                        {
                        int LA32_161 = input.LA(5);

                        if ( (LA32_161=='I') ) {
                            int LA32_184 = input.LA(6);

                            if ( (LA32_184=='N') ) {
                                int LA32_200 = input.LA(7);

                                if ( (LA32_200=='G') ) {
                                    int LA32_208 = input.LA(8);

                                    if ( (LA32_208=='.'||(LA32_208>='0' && LA32_208<='9')||(LA32_208>='A' && LA32_208<='Z')||LA32_208=='_'||(LA32_208>='a' && LA32_208<='z')) ) {
                                        alt32=47;
                                    }
                                    else {
                                        alt32=10;}
                                }
                                else {
                                    alt32=47;}
                            }
                            else {
                                alt32=47;}
                        }
                        else {
                            alt32=47;}
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
                        alt32=47;
                        }
                        break;
                    default:
                        alt32=26;}

                }
                else {
                    alt32=47;}
                }
                break;
            case 'E':
                {
                int LA32_74 = input.LA(3);

                if ( (LA32_74=='Q') ) {
                    int LA32_131 = input.LA(4);

                    if ( (LA32_131=='.'||(LA32_131>='0' && LA32_131<='9')||(LA32_131>='A' && LA32_131<='Z')||LA32_131=='_'||(LA32_131>='a' && LA32_131<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=28;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 'T':
            {
            switch ( input.LA(2) ) {
            case 'R':
                {
                int LA32_75 = input.LA(3);

                if ( (LA32_75=='U') ) {
                    int LA32_132 = input.LA(4);

                    if ( (LA32_132=='E') ) {
                        int LA32_162 = input.LA(5);

                        if ( (LA32_162=='.'||(LA32_162>='0' && LA32_162<='9')||(LA32_162>='A' && LA32_162<='Z')||LA32_162=='_'||(LA32_162>='a' && LA32_162<='z')) ) {
                            alt32=47;
                        }
                        else {
                            alt32=11;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'H':
                {
                int LA32_76 = input.LA(3);

                if ( (LA32_76=='R') ) {
                    int LA32_133 = input.LA(4);

                    if ( (LA32_133=='O') ) {
                        int LA32_163 = input.LA(5);

                        if ( (LA32_163=='W') ) {
                            int LA32_186 = input.LA(6);

                            if ( (LA32_186=='.'||(LA32_186>='0' && LA32_186<='9')||(LA32_186>='A' && LA32_186<='Z')||LA32_186=='_'||(LA32_186>='a' && LA32_186<='z')) ) {
                                alt32=47;
                            }
                            else {
                                alt32=14;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'I':
                {
                int LA32_77 = input.LA(3);

                if ( (LA32_77=='M') ) {
                    int LA32_134 = input.LA(4);

                    if ( (LA32_134=='E') ) {
                        int LA32_164 = input.LA(5);

                        if ( (LA32_164=='S') ) {
                            int LA32_187 = input.LA(6);

                            if ( (LA32_187=='.'||(LA32_187>='0' && LA32_187<='9')||(LA32_187>='A' && LA32_187<='Z')||LA32_187=='_'||(LA32_187>='a' && LA32_187<='z')) ) {
                                alt32=47;
                            }
                            else {
                                alt32=37;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 't':
            {
            switch ( input.LA(2) ) {
            case 'r':
                {
                int LA32_78 = input.LA(3);

                if ( (LA32_78=='u') ) {
                    int LA32_135 = input.LA(4);

                    if ( (LA32_135=='e') ) {
                        int LA32_165 = input.LA(5);

                        if ( (LA32_165=='.'||(LA32_165>='0' && LA32_165<='9')||(LA32_165>='A' && LA32_165<='Z')||LA32_165=='_'||(LA32_165>='a' && LA32_165<='z')) ) {
                            alt32=47;
                        }
                        else {
                            alt32=11;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'i':
                {
                int LA32_79 = input.LA(3);

                if ( (LA32_79=='m') ) {
                    int LA32_136 = input.LA(4);

                    if ( (LA32_136=='e') ) {
                        int LA32_166 = input.LA(5);

                        if ( (LA32_166=='s') ) {
                            int LA32_188 = input.LA(6);

                            if ( (LA32_188=='.'||(LA32_188>='0' && LA32_188<='9')||(LA32_188>='A' && LA32_188<='Z')||LA32_188=='_'||(LA32_188>='a' && LA32_188<='z')) ) {
                                alt32=47;
                            }
                            else {
                                alt32=37;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'h':
                {
                int LA32_80 = input.LA(3);

                if ( (LA32_80=='r') ) {
                    int LA32_137 = input.LA(4);

                    if ( (LA32_137=='o') ) {
                        int LA32_167 = input.LA(5);

                        if ( (LA32_167=='w') ) {
                            int LA32_189 = input.LA(6);

                            if ( (LA32_189=='.'||(LA32_189>='0' && LA32_189<='9')||(LA32_189>='A' && LA32_189<='Z')||LA32_189=='_'||(LA32_189>='a' && LA32_189<='z')) ) {
                                alt32=47;
                            }
                            else {
                                alt32=14;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 'F':
            {
            int LA32_14 = input.LA(2);

            if ( (LA32_14=='A') ) {
                int LA32_81 = input.LA(3);

                if ( (LA32_81=='L') ) {
                    int LA32_138 = input.LA(4);

                    if ( (LA32_138=='S') ) {
                        int LA32_168 = input.LA(5);

                        if ( (LA32_168=='E') ) {
                            int LA32_190 = input.LA(6);

                            if ( (LA32_190=='.'||(LA32_190>='0' && LA32_190<='9')||(LA32_190>='A' && LA32_190<='Z')||LA32_190=='_'||(LA32_190>='a' && LA32_190<='z')) ) {
                                alt32=47;
                            }
                            else {
                                alt32=12;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'f':
            {
            int LA32_15 = input.LA(2);

            if ( (LA32_15=='a') ) {
                int LA32_82 = input.LA(3);

                if ( (LA32_82=='l') ) {
                    int LA32_139 = input.LA(4);

                    if ( (LA32_139=='s') ) {
                        int LA32_169 = input.LA(5);

                        if ( (LA32_169=='e') ) {
                            int LA32_191 = input.LA(6);

                            if ( (LA32_191=='.'||(LA32_191>='0' && LA32_191<='9')||(LA32_191>='A' && LA32_191<='Z')||LA32_191=='_'||(LA32_191>='a' && LA32_191<='z')) ) {
                                alt32=47;
                            }
                            else {
                                alt32=12;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'r':
            {
            int LA32_16 = input.LA(2);

            if ( (LA32_16=='e') ) {
                int LA32_83 = input.LA(3);

                if ( (LA32_83=='t') ) {
                    int LA32_140 = input.LA(4);

                    if ( (LA32_140=='u') ) {
                        int LA32_170 = input.LA(5);

                        if ( (LA32_170=='r') ) {
                            int LA32_192 = input.LA(6);

                            if ( (LA32_192=='n') ) {
                                int LA32_203 = input.LA(7);

                                if ( (LA32_203=='.'||(LA32_203>='0' && LA32_203<='9')||(LA32_203>='A' && LA32_203<='Z')||LA32_203=='_'||(LA32_203>='a' && LA32_203<='z')) ) {
                                    alt32=47;
                                }
                                else {
                                    alt32=13;}
                            }
                            else {
                                alt32=47;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case '(':
            {
            alt32=15;
            }
            break;
        case ')':
            {
            alt32=16;
            }
            break;
        case '[':
            {
            alt32=17;
            }
            break;
        case ']':
            {
            alt32=18;
            }
            break;
        case '{':
            {
            alt32=19;
            }
            break;
        case '}':
            {
            alt32=20;
            }
            break;
        case ',':
        case ';':
            {
            alt32=21;
            }
            break;
        case '.':
            {
            alt32=22;
            }
            break;
        case '=':
            {
            int LA32_25 = input.LA(2);

            if ( (LA32_25=='=') ) {
                alt32=27;
            }
            else {
                alt32=23;}
            }
            break;
        case '<':
            {
            switch ( input.LA(2) ) {
            case '-':
                {
                alt32=23;
                }
                break;
            case '=':
                {
                alt32=32;
                }
                break;
            default:
                alt32=30;}

            }
            break;
        case '|':
            {
            int LA32_27 = input.LA(2);

            if ( (LA32_27=='|') ) {
                alt32=24;
            }
            else {
                alt32=33;}
            }
            break;
        case 'O':
            {
            int LA32_28 = input.LA(2);

            if ( (LA32_28=='R') ) {
                int LA32_90 = input.LA(3);

                if ( (LA32_90=='.'||(LA32_90>='0' && LA32_90<='9')||(LA32_90>='A' && LA32_90<='Z')||LA32_90=='_'||(LA32_90>='a' && LA32_90<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=24;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'o':
            {
            int LA32_29 = input.LA(2);

            if ( (LA32_29=='r') ) {
                int LA32_91 = input.LA(3);

                if ( (LA32_91=='.'||(LA32_91>='0' && LA32_91<='9')||(LA32_91>='A' && LA32_91<='Z')||LA32_91=='_'||(LA32_91>='a' && LA32_91<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=24;}
            }
            else {
                alt32=47;}
            }
            break;
        case '&':
            {
            int LA32_30 = input.LA(2);

            if ( (LA32_30=='&') ) {
                alt32=25;
            }
            else {
                alt32=34;}
            }
            break;
        case 'A':
            {
            int LA32_31 = input.LA(2);

            if ( (LA32_31=='N') ) {
                int LA32_94 = input.LA(3);

                if ( (LA32_94=='D') ) {
                    int LA32_141 = input.LA(4);

                    if ( (LA32_141=='.'||(LA32_141>='0' && LA32_141<='9')||(LA32_141>='A' && LA32_141<='Z')||LA32_141=='_'||(LA32_141>='a' && LA32_141<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=25;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'a':
            {
            int LA32_32 = input.LA(2);

            if ( (LA32_32=='n') ) {
                int LA32_95 = input.LA(3);

                if ( (LA32_95=='d') ) {
                    int LA32_142 = input.LA(4);

                    if ( (LA32_142=='.'||(LA32_142>='0' && LA32_142<='9')||(LA32_142>='A' && LA32_142<='Z')||LA32_142=='_'||(LA32_142>='a' && LA32_142<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=25;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case '!':
            {
            int LA32_33 = input.LA(2);

            if ( (LA32_33=='=') ) {
                alt32=28;
            }
            else {
                alt32=26;}
            }
            break;
        case 'n':
            {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA32_98 = input.LA(3);

                if ( (LA32_98=='t') ) {
                    int LA32_143 = input.LA(4);

                    if ( (LA32_143=='.'||(LA32_143>='0' && LA32_143<='9')||(LA32_143>='A' && LA32_143<='Z')||LA32_143=='_'||(LA32_143>='a' && LA32_143<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=26;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'e':
                {
                int LA32_99 = input.LA(3);

                if ( (LA32_99=='q') ) {
                    int LA32_144 = input.LA(4);

                    if ( (LA32_144=='.'||(LA32_144>='0' && LA32_144<='9')||(LA32_144>='A' && LA32_144<='Z')||LA32_144=='_'||(LA32_144>='a' && LA32_144<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=28;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 'e':
            {
            int LA32_35 = input.LA(2);

            if ( (LA32_35=='q') ) {
                int LA32_100 = input.LA(3);

                if ( (LA32_100=='.'||(LA32_100>='0' && LA32_100<='9')||(LA32_100>='A' && LA32_100<='Z')||LA32_100=='_'||(LA32_100>='a' && LA32_100<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=27;}
            }
            else {
                alt32=47;}
            }
            break;
        case '>':
            {
            int LA32_36 = input.LA(2);

            if ( (LA32_36=='=') ) {
                alt32=31;
            }
            else {
                alt32=29;}
            }
            break;
        case 'G':
            {
            int LA32_37 = input.LA(2);

            if ( (LA32_37=='T') ) {
                int LA32_103 = input.LA(3);

                if ( (LA32_103=='.'||(LA32_103>='0' && LA32_103<='9')||(LA32_103>='A' && LA32_103<='Z')||LA32_103=='_'||(LA32_103>='a' && LA32_103<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=29;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'g':
            {
            switch ( input.LA(2) ) {
            case 't':
                {
                int LA32_104 = input.LA(3);

                if ( (LA32_104=='.'||(LA32_104>='0' && LA32_104<='9')||(LA32_104>='A' && LA32_104<='Z')||LA32_104=='_'||(LA32_104>='a' && LA32_104<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=29;}
                }
                break;
            case 'e':
                {
                int LA32_105 = input.LA(3);

                if ( (LA32_105=='q') ) {
                    int LA32_145 = input.LA(4);

                    if ( (LA32_145=='.'||(LA32_145>='0' && LA32_145<='9')||(LA32_145>='A' && LA32_145<='Z')||LA32_145=='_'||(LA32_145>='a' && LA32_145<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=31;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case 'l':
            {
            switch ( input.LA(2) ) {
            case 't':
                {
                int LA32_106 = input.LA(3);

                if ( (LA32_106=='.'||(LA32_106>='0' && LA32_106<='9')||(LA32_106>='A' && LA32_106<='Z')||LA32_106=='_'||(LA32_106>='a' && LA32_106<='z')) ) {
                    alt32=47;
                }
                else {
                    alt32=30;}
                }
                break;
            case 'e':
                {
                int LA32_107 = input.LA(3);

                if ( (LA32_107=='q') ) {
                    int LA32_146 = input.LA(4);

                    if ( (LA32_146=='.'||(LA32_146>='0' && LA32_146<='9')||(LA32_146>='A' && LA32_146<='Z')||LA32_146=='_'||(LA32_146>='a' && LA32_146<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=32;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case '^':
            {
            alt32=35;
            }
            break;
        case '~':
            {
            alt32=36;
            }
            break;
        case '*':
            {
            alt32=37;
            }
            break;
        case '/':
            {
            alt32=38;
            }
            break;
        case 'd':
            {
            int LA32_44 = input.LA(2);

            if ( (LA32_44=='i') ) {
                int LA32_108 = input.LA(3);

                if ( (LA32_108=='v') ) {
                    int LA32_147 = input.LA(4);

                    if ( (LA32_147=='i') ) {
                        int LA32_171 = input.LA(5);

                        if ( (LA32_171=='d') ) {
                            int LA32_193 = input.LA(6);

                            if ( (LA32_193=='e') ) {
                                int LA32_204 = input.LA(7);

                                if ( (LA32_204=='.'||(LA32_204>='0' && LA32_204<='9')||(LA32_204>='A' && LA32_204<='Z')||LA32_204=='_'||(LA32_204>='a' && LA32_204<='z')) ) {
                                    alt32=47;
                                }
                                else {
                                    alt32=38;}
                            }
                            else {
                                alt32=47;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case '-':
            {
            int LA32_45 = input.LA(2);

            if ( ((LA32_45>='0' && LA32_45<='9')) ) {
                alt32=1;
            }
            else {
                alt32=40;}
            }
            break;
        case 'P':
            {
            int LA32_46 = input.LA(2);

            if ( (LA32_46=='L') ) {
                int LA32_110 = input.LA(3);

                if ( (LA32_110=='U') ) {
                    int LA32_148 = input.LA(4);

                    if ( (LA32_148=='S') ) {
                        int LA32_172 = input.LA(5);

                        if ( (LA32_172=='.'||(LA32_172>='0' && LA32_172<='9')||(LA32_172>='A' && LA32_172<='Z')||LA32_172=='_'||(LA32_172>='a' && LA32_172<='z')) ) {
                            alt32=47;
                        }
                        else {
                            alt32=39;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'p':
            {
            int LA32_47 = input.LA(2);

            if ( (LA32_47=='l') ) {
                int LA32_111 = input.LA(3);

                if ( (LA32_111=='u') ) {
                    int LA32_149 = input.LA(4);

                    if ( (LA32_149=='s') ) {
                        int LA32_173 = input.LA(5);

                        if ( (LA32_173=='.'||(LA32_173>='0' && LA32_173<='9')||(LA32_173>='A' && LA32_173<='Z')||LA32_173=='_'||(LA32_173>='a' && LA32_173<='z')) ) {
                            alt32=47;
                        }
                        else {
                            alt32=39;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
            }
            else {
                alt32=47;}
            }
            break;
        case 'm':
            {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA32_112 = input.LA(3);

                if ( (LA32_112=='d') ) {
                    int LA32_150 = input.LA(4);

                    if ( (LA32_150=='.'||(LA32_150>='0' && LA32_150<='9')||(LA32_150>='A' && LA32_150<='Z')||LA32_150=='_'||(LA32_150>='a' && LA32_150<='z')) ) {
                        alt32=47;
                    }
                    else {
                        alt32=41;}
                }
                else {
                    alt32=47;}
                }
                break;
            case 'i':
                {
                int LA32_113 = input.LA(3);

                if ( (LA32_113=='n') ) {
                    int LA32_151 = input.LA(4);

                    if ( (LA32_151=='u') ) {
                        int LA32_174 = input.LA(5);

                        if ( (LA32_174=='s') ) {
                            int LA32_194 = input.LA(6);

                            if ( (LA32_194=='.'||(LA32_194>='0' && LA32_194<='9')||(LA32_194>='A' && LA32_194<='Z')||LA32_194=='_'||(LA32_194>='a' && LA32_194<='z')) ) {
                                alt32=47;
                            }
                            else {
                                alt32=40;}
                        }
                        else {
                            alt32=47;}
                    }
                    else {
                        alt32=47;}
                }
                else {
                    alt32=47;}
                }
                break;
            default:
                alt32=47;}

            }
            break;
        case '%':
            {
            alt32=41;
            }
            break;
        case '?':
            {
            alt32=42;
            }
            break;
        case ':':
            {
            alt32=43;
            }
            break;
        case '\'':
            {
            int LA32_52 = input.LA(2);

            if ( (LA32_52=='\t'||LA32_52=='\r'||(LA32_52>=' ' && LA32_52<='~')) ) {
                alt32=47;
            }
            else {
                alt32=44;}
            }
            break;
        case '\"':
            {
            int LA32_53 = input.LA(2);

            if ( (LA32_53=='\t'||LA32_53=='\r'||(LA32_53>=' ' && LA32_53<='~')) ) {
                alt32=46;
            }
            else {
                alt32=45;}
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
            alt32=47;
            }
            break;
        case '$':
            {
            alt32=48;
            }
            break;
        case '\t':
        case '\n':
        case '\r':
        case ' ':
            {
            alt32=49;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( NUMBER | BIND | IF | DO | RULE | CLASS | METHOD | LINE | ENDRULE | NOTHING | TRUE | FALSE | RETURN | THROW | LPAREN | RPAREN | LSQUARE | RSQUARE | LBRACE | RBRACE | SEPR | DOT | ASSIGN | OR | AND | NOT | EQ | NEQ | GT | LT | GEQ | LEQ | BOR | BAND | BXOR | TWIDDLE | MUL | DIV | PLUS | MINUS | MOD | TERN_IF | COLON | QUOTE | DQUOTE | STRING | SYMBOL | DOLLARSYM | WS );", 32, 0, input);

            throw nvae;
        }

        switch (alt32) {
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
                // dd/grammar/ECAToken.g:1:85: THROW
                {
                mTHROW(); 

                }
                break;
            case 15 :
                // dd/grammar/ECAToken.g:1:91: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 16 :
                // dd/grammar/ECAToken.g:1:98: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 17 :
                // dd/grammar/ECAToken.g:1:105: LSQUARE
                {
                mLSQUARE(); 

                }
                break;
            case 18 :
                // dd/grammar/ECAToken.g:1:113: RSQUARE
                {
                mRSQUARE(); 

                }
                break;
            case 19 :
                // dd/grammar/ECAToken.g:1:121: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 20 :
                // dd/grammar/ECAToken.g:1:128: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 21 :
                // dd/grammar/ECAToken.g:1:135: SEPR
                {
                mSEPR(); 

                }
                break;
            case 22 :
                // dd/grammar/ECAToken.g:1:140: DOT
                {
                mDOT(); 

                }
                break;
            case 23 :
                // dd/grammar/ECAToken.g:1:144: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 24 :
                // dd/grammar/ECAToken.g:1:151: OR
                {
                mOR(); 

                }
                break;
            case 25 :
                // dd/grammar/ECAToken.g:1:154: AND
                {
                mAND(); 

                }
                break;
            case 26 :
                // dd/grammar/ECAToken.g:1:158: NOT
                {
                mNOT(); 

                }
                break;
            case 27 :
                // dd/grammar/ECAToken.g:1:162: EQ
                {
                mEQ(); 

                }
                break;
            case 28 :
                // dd/grammar/ECAToken.g:1:165: NEQ
                {
                mNEQ(); 

                }
                break;
            case 29 :
                // dd/grammar/ECAToken.g:1:169: GT
                {
                mGT(); 

                }
                break;
            case 30 :
                // dd/grammar/ECAToken.g:1:172: LT
                {
                mLT(); 

                }
                break;
            case 31 :
                // dd/grammar/ECAToken.g:1:175: GEQ
                {
                mGEQ(); 

                }
                break;
            case 32 :
                // dd/grammar/ECAToken.g:1:179: LEQ
                {
                mLEQ(); 

                }
                break;
            case 33 :
                // dd/grammar/ECAToken.g:1:183: BOR
                {
                mBOR(); 

                }
                break;
            case 34 :
                // dd/grammar/ECAToken.g:1:187: BAND
                {
                mBAND(); 

                }
                break;
            case 35 :
                // dd/grammar/ECAToken.g:1:192: BXOR
                {
                mBXOR(); 

                }
                break;
            case 36 :
                // dd/grammar/ECAToken.g:1:197: TWIDDLE
                {
                mTWIDDLE(); 

                }
                break;
            case 37 :
                // dd/grammar/ECAToken.g:1:205: MUL
                {
                mMUL(); 

                }
                break;
            case 38 :
                // dd/grammar/ECAToken.g:1:209: DIV
                {
                mDIV(); 

                }
                break;
            case 39 :
                // dd/grammar/ECAToken.g:1:213: PLUS
                {
                mPLUS(); 

                }
                break;
            case 40 :
                // dd/grammar/ECAToken.g:1:218: MINUS
                {
                mMINUS(); 

                }
                break;
            case 41 :
                // dd/grammar/ECAToken.g:1:224: MOD
                {
                mMOD(); 

                }
                break;
            case 42 :
                // dd/grammar/ECAToken.g:1:228: TERN_IF
                {
                mTERN_IF(); 

                }
                break;
            case 43 :
                // dd/grammar/ECAToken.g:1:236: COLON
                {
                mCOLON(); 

                }
                break;
            case 44 :
                // dd/grammar/ECAToken.g:1:242: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 45 :
                // dd/grammar/ECAToken.g:1:248: DQUOTE
                {
                mDQUOTE(); 

                }
                break;
            case 46 :
                // dd/grammar/ECAToken.g:1:255: STRING
                {
                mSTRING(); 

                }
                break;
            case 47 :
                // dd/grammar/ECAToken.g:1:262: SYMBOL
                {
                mSYMBOL(); 

                }
                break;
            case 48 :
                // dd/grammar/ECAToken.g:1:269: DOLLARSYM
                {
                mDOLLARSYM(); 

                }
                break;
            case 49 :
                // dd/grammar/ECAToken.g:1:279: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA29 dfa29 = new DFA29(this);
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
            return "64:1: NUMBER : ( INTEGER | FLOAT );";
        }
    }
    static final String DFA29_eotS =
        "\1\uffff\2\3\2\uffff";
    static final String DFA29_eofS =
        "\5\uffff";
    static final String DFA29_minS =
        "\1\101\2\56\2\uffff";
    static final String DFA29_maxS =
        "\3\172\2\uffff";
    static final String DFA29_acceptS =
        "\3\uffff\1\2\1\1";
    static final String DFA29_specialS =
        "\5\uffff}>";
    static final String[] DFA29_transitionS = {
            "\32\1\4\uffff\1\1\1\uffff\32\1",
            "\1\4\1\uffff\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
            "\1\4\1\uffff\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
            "",
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "282:1: fragment DOTSYM : ( BARESYM DOT DOTSYM | BARESYM );";
        }
    }
 

}