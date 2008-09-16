// $ANTLR 3.0.1 dd/grammar/ECAToken.g 2008-09-12 18:02:58

package org.jboss.jbossts.orchestration.rule.grammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ECATokenLexer extends Lexer {
    public static final int MINUS=41;
    public static final int NUMBER=12;
    public static final int FLOAT=11;
    public static final int POSDIGIT=5;
    public static final int LEQ=33;
    public static final int TWIDDLE=37;
    public static final int MOD=42;
    public static final int GEQ=32;
    public static final int DQUOTE=48;
    public static final int OR=25;
    public static final int BOR=34;
    public static final int BAREINT=7;
    public static final int LBRACE=20;
    public static final int NEWLINE=50;
    public static final int DOT=23;
    public static final int RBRACE=21;
    public static final int INTEGER=8;
    public static final int AND=26;
    public static final int ASSIGN=24;
    public static final int SYMBOL=56;
    public static final int RPAREN=17;
    public static final int LPAREN=16;
    public static final int SIGN=6;
    public static final int DIGIT=4;
    public static final int PLUS=40;
    public static final int BAND=35;
    public static final int NEQ=29;
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
    public static final int QUOTE=47;
    public static final int TERN_IF=43;
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
            // dd/grammar/ECAToken.g:26:9: ( ( SIGN )? BAREINT )
            // dd/grammar/ECAToken.g:26:11: ( SIGN )? BAREINT
            {
            // dd/grammar/ECAToken.g:26:11: ( SIGN )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='+'||LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // dd/grammar/ECAToken.g:26:11: SIGN
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
            // dd/grammar/ECAToken.g:30:7: ( '.' )
            // dd/grammar/ECAToken.g:30:9: '.'
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
            // dd/grammar/ECAToken.g:34:9: ( ( 'e' | 'E' ) INTEGER )
            // dd/grammar/ECAToken.g:34:12: ( 'e' | 'E' ) INTEGER
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
            // dd/grammar/ECAToken.g:39:7: ( INTEGER POINT ( BAREINT )? ( EXPPART )? )
            // dd/grammar/ECAToken.g:39:9: INTEGER POINT ( BAREINT )? ( EXPPART )?
            {
            mINTEGER(); 
            mPOINT(); 
            // dd/grammar/ECAToken.g:39:23: ( BAREINT )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // dd/grammar/ECAToken.g:39:23: BAREINT
                    {
                    mBAREINT(); 

                    }
                    break;

            }

            // dd/grammar/ECAToken.g:39:32: ( EXPPART )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='E'||LA5_0=='e') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // dd/grammar/ECAToken.g:39:32: EXPPART
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
            // dd/grammar/ECAToken.g:42:8: ( INTEGER | FLOAT )
            int alt6=2;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // dd/grammar/ECAToken.g:42:10: INTEGER
                    {
                    mINTEGER(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:42:20: FLOAT
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
            // dd/grammar/ECAToken.g:47:6: ( 'BIND' )
            // dd/grammar/ECAToken.g:47:8: 'BIND'
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
            // dd/grammar/ECAToken.g:50:4: ( 'IF' )
            // dd/grammar/ECAToken.g:50:6: 'IF'
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
            // dd/grammar/ECAToken.g:53:4: ( 'DO' )
            // dd/grammar/ECAToken.g:53:6: 'DO'
            {
            match("DO"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DO

    // $ANTLR start LPAREN
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            // dd/grammar/ECAToken.g:58:8: ( '(' )
            // dd/grammar/ECAToken.g:58:10: '('
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
            // dd/grammar/ECAToken.g:61:8: ( ')' )
            // dd/grammar/ECAToken.g:61:10: ')'
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
            // dd/grammar/ECAToken.g:64:9: ( '\\[' )
            // dd/grammar/ECAToken.g:64:11: '\\['
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
            // dd/grammar/ECAToken.g:67:9: ( '\\]' )
            // dd/grammar/ECAToken.g:67:11: '\\]'
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
            // dd/grammar/ECAToken.g:70:8: ( '{' )
            // dd/grammar/ECAToken.g:70:10: '{'
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
            // dd/grammar/ECAToken.g:73:8: ( '}' )
            // dd/grammar/ECAToken.g:73:10: '}'
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
            // dd/grammar/ECAToken.g:78:6: ( ';' | ',' )
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
            // dd/grammar/ECAToken.g:84:5: ( '.' )
            // dd/grammar/ECAToken.g:84:7: '.'
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
            // dd/grammar/ECAToken.g:89:8: ( '=' | '<--' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='=') ) {
                alt7=1;
            }
            else if ( (LA7_0=='<') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("89:1: ASSIGN : ( '=' | '<--' );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // dd/grammar/ECAToken.g:89:10: '='
                    {
                    match('='); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:90:4: '<--'
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
            // dd/grammar/ECAToken.g:95:4: ( '||' | 'OR' | 'or' )
            int alt8=3;
            switch ( input.LA(1) ) {
            case '|':
                {
                alt8=1;
                }
                break;
            case 'O':
                {
                alt8=2;
                }
                break;
            case 'o':
                {
                alt8=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("95:1: OR : ( '||' | 'OR' | 'or' );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // dd/grammar/ECAToken.g:95:6: '||'
                    {
                    match("||"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:96:4: 'OR'
                    {
                    match("OR"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:97:4: 'or'
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
            // dd/grammar/ECAToken.g:100:5: ( '&&' | 'AND' | 'and' )
            int alt9=3;
            switch ( input.LA(1) ) {
            case '&':
                {
                alt9=1;
                }
                break;
            case 'A':
                {
                alt9=2;
                }
                break;
            case 'a':
                {
                alt9=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("100:1: AND : ( '&&' | 'AND' | 'and' );", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // dd/grammar/ECAToken.g:100:7: '&&'
                    {
                    match("&&"); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:101:4: 'AND'
                    {
                    match("AND"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:102:4: 'and'
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
            // dd/grammar/ECAToken.g:105:5: ( '!' | 'NOT' | 'not' )
            int alt10=3;
            switch ( input.LA(1) ) {
            case '!':
                {
                alt10=1;
                }
                break;
            case 'N':
                {
                alt10=2;
                }
                break;
            case 'n':
                {
                alt10=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("105:1: NOT : ( '!' | 'NOT' | 'not' );", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // dd/grammar/ECAToken.g:105:7: '!'
                    {
                    match('!'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:106:4: 'NOT'
                    {
                    match("NOT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:107:4: 'not'
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
            // dd/grammar/ECAToken.g:112:4: ( '==' | 'EQ' | 'eq' )
            int alt11=3;
            switch ( input.LA(1) ) {
            case '=':
                {
                alt11=1;
                }
                break;
            case 'E':
                {
                alt11=2;
                }
                break;
            case 'e':
                {
                alt11=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("112:1: EQ : ( '==' | 'EQ' | 'eq' );", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // dd/grammar/ECAToken.g:112:6: '=='
                    {
                    match("=="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:113:4: 'EQ'
                    {
                    match("EQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:114:4: 'eq'
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
            // dd/grammar/ECAToken.g:117:5: ( '!=' | 'NEQ' | 'neq' )
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
                    new NoViableAltException("117:1: NEQ : ( '!=' | 'NEQ' | 'neq' );", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // dd/grammar/ECAToken.g:117:7: '!='
                    {
                    match("!="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:118:4: 'NEQ'
                    {
                    match("NEQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:119:4: 'neq'
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
            // dd/grammar/ECAToken.g:122:4: ( '>' | 'GT' | 'gt' )
            int alt13=3;
            switch ( input.LA(1) ) {
            case '>':
                {
                alt13=1;
                }
                break;
            case 'G':
                {
                alt13=2;
                }
                break;
            case 'g':
                {
                alt13=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("122:1: GT : ( '>' | 'GT' | 'gt' );", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // dd/grammar/ECAToken.g:122:6: '>'
                    {
                    match('>'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:123:4: 'GT'
                    {
                    match("GT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:124:4: 'gt'
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
            // dd/grammar/ECAToken.g:127:4: ( '<' | 'LT' | 'lt' )
            int alt14=3;
            switch ( input.LA(1) ) {
            case '<':
                {
                alt14=1;
                }
                break;
            case 'L':
                {
                alt14=2;
                }
                break;
            case 'l':
                {
                alt14=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("127:1: LT : ( '<' | 'LT' | 'lt' );", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // dd/grammar/ECAToken.g:127:6: '<'
                    {
                    match('<'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:128:4: 'LT'
                    {
                    match("LT"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:129:4: 'lt'
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
            // dd/grammar/ECAToken.g:132:5: ( '>=' | 'GEQ' | 'geq' )
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
                    new NoViableAltException("132:1: GEQ : ( '>=' | 'GEQ' | 'geq' );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // dd/grammar/ECAToken.g:132:7: '>='
                    {
                    match(">="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:133:4: 'GEQ'
                    {
                    match("GEQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:134:4: 'geq'
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
            // dd/grammar/ECAToken.g:137:5: ( '<=' | 'LEQ' | 'leq' )
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
                    new NoViableAltException("137:1: LEQ : ( '<=' | 'LEQ' | 'leq' );", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // dd/grammar/ECAToken.g:137:7: '<='
                    {
                    match("<="); 


                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:138:4: 'LEQ'
                    {
                    match("LEQ"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:139:4: 'leq'
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
            // dd/grammar/ECAToken.g:144:5: ( '|' )
            // dd/grammar/ECAToken.g:144:7: '|'
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
            // dd/grammar/ECAToken.g:147:6: ( '&' )
            // dd/grammar/ECAToken.g:147:8: '&'
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
            // dd/grammar/ECAToken.g:150:6: ( '^' )
            // dd/grammar/ECAToken.g:150:8: '^'
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
            // dd/grammar/ECAToken.g:153:9: ( '~' )
            // dd/grammar/ECAToken.g:153:11: '~'
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
            // dd/grammar/ECAToken.g:158:5: ( '*' | 'TIMES' | 'times' )
            int alt17=3;
            switch ( input.LA(1) ) {
            case '*':
                {
                alt17=1;
                }
                break;
            case 'T':
                {
                alt17=2;
                }
                break;
            case 't':
                {
                alt17=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("158:1: MUL : ( '*' | 'TIMES' | 'times' );", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // dd/grammar/ECAToken.g:158:7: '*'
                    {
                    match('*'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:159:4: 'TIMES'
                    {
                    match("TIMES"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:160:4: 'times'
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
            // dd/grammar/ECAToken.g:163:5: ( '/' | 'DIVIDE' | 'divide' )
            int alt18=3;
            switch ( input.LA(1) ) {
            case '/':
                {
                alt18=1;
                }
                break;
            case 'D':
                {
                alt18=2;
                }
                break;
            case 'd':
                {
                alt18=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("163:1: DIV : ( '/' | 'DIVIDE' | 'divide' );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // dd/grammar/ECAToken.g:163:7: '/'
                    {
                    match('/'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:164:4: 'DIVIDE'
                    {
                    match("DIVIDE"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:165:4: 'divide'
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
            // dd/grammar/ECAToken.g:168:6: ( '+' | 'PLUS' | 'plus' )
            int alt19=3;
            switch ( input.LA(1) ) {
            case '+':
                {
                alt19=1;
                }
                break;
            case 'P':
                {
                alt19=2;
                }
                break;
            case 'p':
                {
                alt19=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("168:1: PLUS : ( '+' | 'PLUS' | 'plus' );", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // dd/grammar/ECAToken.g:168:8: '+'
                    {
                    match('+'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:169:4: 'PLUS'
                    {
                    match("PLUS"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:170:4: 'plus'
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
            // dd/grammar/ECAToken.g:173:7: ( '-' | 'MINUS' | 'minus' )
            int alt20=3;
            switch ( input.LA(1) ) {
            case '-':
                {
                alt20=1;
                }
                break;
            case 'M':
                {
                alt20=2;
                }
                break;
            case 'm':
                {
                alt20=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("173:1: MINUS : ( '-' | 'MINUS' | 'minus' );", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // dd/grammar/ECAToken.g:173:9: '-'
                    {
                    match('-'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:174:4: 'MINUS'
                    {
                    match("MINUS"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:175:4: 'minus'
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
            // dd/grammar/ECAToken.g:178:5: ( '%' | 'MOD' | 'mod' )
            int alt21=3;
            switch ( input.LA(1) ) {
            case '%':
                {
                alt21=1;
                }
                break;
            case 'M':
                {
                alt21=2;
                }
                break;
            case 'm':
                {
                alt21=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("178:1: MOD : ( '%' | 'MOD' | 'mod' );", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // dd/grammar/ECAToken.g:178:7: '%'
                    {
                    match('%'); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:179:4: 'MOD'
                    {
                    match("MOD"); 


                    }
                    break;
                case 3 :
                    // dd/grammar/ECAToken.g:180:4: 'mod'
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
            // dd/grammar/ECAToken.g:185:9: ( '?' )
            // dd/grammar/ECAToken.g:185:11: '?'
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
            // dd/grammar/ECAToken.g:188:7: ( ':' )
            // dd/grammar/ECAToken.g:188:9: ':'
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
            // dd/grammar/ECAToken.g:194:8: ( 'a' .. 'z' | 'A' .. 'Z' )
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
            // dd/grammar/ECAToken.g:198:12: ( '_' )
            // dd/grammar/ECAToken.g:198:14: '_'
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
            // dd/grammar/ECAToken.g:201:7: ( '\\'' )
            // dd/grammar/ECAToken.g:201:9: '\\''
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
            // dd/grammar/ECAToken.g:204:8: ( '\"' )
            // dd/grammar/ECAToken.g:204:10: '\"'
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
            // dd/grammar/ECAToken.g:208:7: ( ' ' | '\\t' | '\\r' )
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
            // dd/grammar/ECAToken.g:212:9: ( '\\n' )
            // dd/grammar/ECAToken.g:212:11: '\\n'
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
            // dd/grammar/ECAToken.g:216:7: ( '!' | '$' | '%' | '^' | '&' | '*' | '(' | ')' | '-' | '+' | '=' | '{' | '}' | '[' | ']' | ':' | ';' | '@' | '~' | '#' | '|' | '\\\\' | '`' | ',' | '<' | '.' | '>' | '/' | '?' )
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
            // dd/grammar/ECAToken.g:219:8: ( DQUOTE ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )* DQUOTE )
            // dd/grammar/ECAToken.g:219:10: DQUOTE ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )* DQUOTE
            {
            mDQUOTE(); 
            // dd/grammar/ECAToken.g:219:17: ( SPACE | PUNCT | LETTER | UNDERSCORE | DIGIT | QUOTE )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0=='\t'||LA22_0=='\r'||(LA22_0>=' ' && LA22_0<='!')||(LA22_0>='#' && LA22_0<='~')) ) {
                    alt22=1;
                }


                switch (alt22) {
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
            	    break loop22;
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
            // dd/grammar/ECAToken.g:223:9: ( ( LETTER | UNDERSCORE ) ( LETTER | DIGIT | UNDERSCORE )* )
            // dd/grammar/ECAToken.g:223:11: ( LETTER | UNDERSCORE ) ( LETTER | DIGIT | UNDERSCORE )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // dd/grammar/ECAToken.g:223:33: ( LETTER | DIGIT | UNDERSCORE )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>='0' && LA23_0<='9')||(LA23_0>='A' && LA23_0<='Z')||LA23_0=='_'||(LA23_0>='a' && LA23_0<='z')) ) {
                    alt23=1;
                }


                switch (alt23) {
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
            	    break loop23;
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
            // dd/grammar/ECAToken.g:226:9: ( QUOTE ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE )* QUOTE )
            // dd/grammar/ECAToken.g:226:11: QUOTE ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE )* QUOTE
            {
            mQUOTE(); 
            // dd/grammar/ECAToken.g:226:17: ( PUNCT | LETTER | UNDERSCORE | DIGIT | DQUOTE )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>='!' && LA24_0<='&')||(LA24_0>='(' && LA24_0<='~')) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // dd/grammar/ECAToken.g:
            	    {
            	    if ( (input.LA(1)>='!' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='~') ) {
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
            // dd/grammar/ECAToken.g:231:8: ( BARESYM DOT DOTSYM | BARESYM )
            int alt25=2;
            alt25 = dfa25.predict(input);
            switch (alt25) {
                case 1 :
                    // dd/grammar/ECAToken.g:231:10: BARESYM DOT DOTSYM
                    {
                    mBARESYM(); 
                    mDOT(); 
                    mDOTSYM(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:232:4: BARESYM
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
            // dd/grammar/ECAToken.g:235:8: ( DOTSYM | QUOTSYM )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( ((LA26_0>='A' && LA26_0<='Z')||LA26_0=='_'||(LA26_0>='a' && LA26_0<='z')) ) {
                alt26=1;
            }
            else if ( (LA26_0=='\'') ) {
                alt26=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("235:1: SYMBOL : ( DOTSYM | QUOTSYM );", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // dd/grammar/ECAToken.g:235:10: DOTSYM
                    {
                    mDOTSYM(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:236:4: QUOTSYM
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
            // dd/grammar/ECAToken.g:241:8: ( '$' )
            // dd/grammar/ECAToken.g:241:10: '$'
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
            // dd/grammar/ECAToken.g:246:11: ( DOLLAR ( BAREINT | BARESYM ) )
            // dd/grammar/ECAToken.g:246:13: DOLLAR ( BAREINT | BARESYM )
            {
            mDOLLAR(); 
            // dd/grammar/ECAToken.g:246:20: ( BAREINT | BARESYM )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( ((LA27_0>='0' && LA27_0<='9')) ) {
                alt27=1;
            }
            else if ( ((LA27_0>='A' && LA27_0<='Z')||LA27_0=='_'||(LA27_0>='a' && LA27_0<='z')) ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("246:20: ( BAREINT | BARESYM )", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // dd/grammar/ECAToken.g:246:21: BAREINT
                    {
                    mBAREINT(); 

                    }
                    break;
                case 2 :
                    // dd/grammar/ECAToken.g:246:31: BARESYM
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
            // dd/grammar/ECAToken.g:252:4: ( ( SPACE | NEWLINE ) )
            // dd/grammar/ECAToken.g:252:6: ( SPACE | NEWLINE )
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
        // dd/grammar/ECAToken.g:1:8: ( NUMBER | BIND | IF | DO | LPAREN | RPAREN | LSQUARE | RSQUARE | LBRACE | RBRACE | SEPR | DOT | ASSIGN | OR | AND | NOT | EQ | NEQ | GT | LT | GEQ | LEQ | BOR | BAND | BXOR | TWIDDLE | MUL | DIV | PLUS | MINUS | MOD | TERN_IF | COLON | QUOTE | DQUOTE | STRING | SYMBOL | DOLLARSYM | WS )
        int alt28=39;
        switch ( input.LA(1) ) {
        case '+':
            {
            int LA28_1 = input.LA(2);

            if ( ((LA28_1>='0' && LA28_1<='9')) ) {
                alt28=1;
            }
            else {
                alt28=29;}
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
            alt28=1;
            }
            break;
        case 'B':
            {
            int LA28_3 = input.LA(2);

            if ( (LA28_3=='I') ) {
                int LA28_53 = input.LA(3);

                if ( (LA28_53=='N') ) {
                    int LA28_100 = input.LA(4);

                    if ( (LA28_100=='D') ) {
                        int LA28_123 = input.LA(5);

                        if ( (LA28_123=='.'||(LA28_123>='0' && LA28_123<='9')||(LA28_123>='A' && LA28_123<='Z')||LA28_123=='_'||(LA28_123>='a' && LA28_123<='z')) ) {
                            alt28=37;
                        }
                        else {
                            alt28=2;}
                    }
                    else {
                        alt28=37;}
                }
                else {
                    alt28=37;}
            }
            else {
                alt28=37;}
            }
            break;
        case 'I':
            {
            int LA28_4 = input.LA(2);

            if ( (LA28_4=='F') ) {
                int LA28_54 = input.LA(3);

                if ( (LA28_54=='.'||(LA28_54>='0' && LA28_54<='9')||(LA28_54>='A' && LA28_54<='Z')||LA28_54=='_'||(LA28_54>='a' && LA28_54<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=3;}
            }
            else {
                alt28=37;}
            }
            break;
        case 'D':
            {
            switch ( input.LA(2) ) {
            case 'O':
                {
                int LA28_55 = input.LA(3);

                if ( (LA28_55=='.'||(LA28_55>='0' && LA28_55<='9')||(LA28_55>='A' && LA28_55<='Z')||LA28_55=='_'||(LA28_55>='a' && LA28_55<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=4;}
                }
                break;
            case 'I':
                {
                int LA28_56 = input.LA(3);

                if ( (LA28_56=='V') ) {
                    int LA28_103 = input.LA(4);

                    if ( (LA28_103=='I') ) {
                        int LA28_124 = input.LA(5);

                        if ( (LA28_124=='D') ) {
                            int LA28_133 = input.LA(6);

                            if ( (LA28_133=='E') ) {
                                int LA28_139 = input.LA(7);

                                if ( (LA28_139=='.'||(LA28_139>='0' && LA28_139<='9')||(LA28_139>='A' && LA28_139<='Z')||LA28_139=='_'||(LA28_139>='a' && LA28_139<='z')) ) {
                                    alt28=37;
                                }
                                else {
                                    alt28=28;}
                            }
                            else {
                                alt28=37;}
                        }
                        else {
                            alt28=37;}
                    }
                    else {
                        alt28=37;}
                }
                else {
                    alt28=37;}
                }
                break;
            default:
                alt28=37;}

            }
            break;
        case '(':
            {
            alt28=5;
            }
            break;
        case ')':
            {
            alt28=6;
            }
            break;
        case '[':
            {
            alt28=7;
            }
            break;
        case ']':
            {
            alt28=8;
            }
            break;
        case '{':
            {
            alt28=9;
            }
            break;
        case '}':
            {
            alt28=10;
            }
            break;
        case ',':
        case ';':
            {
            alt28=11;
            }
            break;
        case '.':
            {
            alt28=12;
            }
            break;
        case '=':
            {
            int LA28_14 = input.LA(2);

            if ( (LA28_14=='=') ) {
                alt28=17;
            }
            else {
                alt28=13;}
            }
            break;
        case '<':
            {
            switch ( input.LA(2) ) {
            case '=':
                {
                alt28=22;
                }
                break;
            case '-':
                {
                alt28=13;
                }
                break;
            default:
                alt28=20;}

            }
            break;
        case '|':
            {
            int LA28_16 = input.LA(2);

            if ( (LA28_16=='|') ) {
                alt28=14;
            }
            else {
                alt28=23;}
            }
            break;
        case 'O':
            {
            int LA28_17 = input.LA(2);

            if ( (LA28_17=='R') ) {
                int LA28_63 = input.LA(3);

                if ( (LA28_63=='.'||(LA28_63>='0' && LA28_63<='9')||(LA28_63>='A' && LA28_63<='Z')||LA28_63=='_'||(LA28_63>='a' && LA28_63<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=14;}
            }
            else {
                alt28=37;}
            }
            break;
        case 'o':
            {
            int LA28_18 = input.LA(2);

            if ( (LA28_18=='r') ) {
                int LA28_64 = input.LA(3);

                if ( (LA28_64=='.'||(LA28_64>='0' && LA28_64<='9')||(LA28_64>='A' && LA28_64<='Z')||LA28_64=='_'||(LA28_64>='a' && LA28_64<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=14;}
            }
            else {
                alt28=37;}
            }
            break;
        case '&':
            {
            int LA28_19 = input.LA(2);

            if ( (LA28_19=='&') ) {
                alt28=15;
            }
            else {
                alt28=24;}
            }
            break;
        case 'A':
            {
            int LA28_20 = input.LA(2);

            if ( (LA28_20=='N') ) {
                int LA28_67 = input.LA(3);

                if ( (LA28_67=='D') ) {
                    int LA28_104 = input.LA(4);

                    if ( (LA28_104=='.'||(LA28_104>='0' && LA28_104<='9')||(LA28_104>='A' && LA28_104<='Z')||LA28_104=='_'||(LA28_104>='a' && LA28_104<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=15;}
                }
                else {
                    alt28=37;}
            }
            else {
                alt28=37;}
            }
            break;
        case 'a':
            {
            int LA28_21 = input.LA(2);

            if ( (LA28_21=='n') ) {
                int LA28_68 = input.LA(3);

                if ( (LA28_68=='d') ) {
                    int LA28_105 = input.LA(4);

                    if ( (LA28_105=='.'||(LA28_105>='0' && LA28_105<='9')||(LA28_105>='A' && LA28_105<='Z')||LA28_105=='_'||(LA28_105>='a' && LA28_105<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=15;}
                }
                else {
                    alt28=37;}
            }
            else {
                alt28=37;}
            }
            break;
        case '!':
            {
            int LA28_22 = input.LA(2);

            if ( (LA28_22=='=') ) {
                alt28=18;
            }
            else {
                alt28=16;}
            }
            break;
        case 'N':
            {
            switch ( input.LA(2) ) {
            case 'E':
                {
                int LA28_71 = input.LA(3);

                if ( (LA28_71=='Q') ) {
                    int LA28_106 = input.LA(4);

                    if ( (LA28_106=='.'||(LA28_106>='0' && LA28_106<='9')||(LA28_106>='A' && LA28_106<='Z')||LA28_106=='_'||(LA28_106>='a' && LA28_106<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=18;}
                }
                else {
                    alt28=37;}
                }
                break;
            case 'O':
                {
                int LA28_72 = input.LA(3);

                if ( (LA28_72=='T') ) {
                    int LA28_107 = input.LA(4);

                    if ( (LA28_107=='.'||(LA28_107>='0' && LA28_107<='9')||(LA28_107>='A' && LA28_107<='Z')||LA28_107=='_'||(LA28_107>='a' && LA28_107<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=16;}
                }
                else {
                    alt28=37;}
                }
                break;
            default:
                alt28=37;}

            }
            break;
        case 'n':
            {
            switch ( input.LA(2) ) {
            case 'e':
                {
                int LA28_73 = input.LA(3);

                if ( (LA28_73=='q') ) {
                    int LA28_108 = input.LA(4);

                    if ( (LA28_108=='.'||(LA28_108>='0' && LA28_108<='9')||(LA28_108>='A' && LA28_108<='Z')||LA28_108=='_'||(LA28_108>='a' && LA28_108<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=18;}
                }
                else {
                    alt28=37;}
                }
                break;
            case 'o':
                {
                int LA28_74 = input.LA(3);

                if ( (LA28_74=='t') ) {
                    int LA28_109 = input.LA(4);

                    if ( (LA28_109=='.'||(LA28_109>='0' && LA28_109<='9')||(LA28_109>='A' && LA28_109<='Z')||LA28_109=='_'||(LA28_109>='a' && LA28_109<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=16;}
                }
                else {
                    alt28=37;}
                }
                break;
            default:
                alt28=37;}

            }
            break;
        case 'E':
            {
            int LA28_25 = input.LA(2);

            if ( (LA28_25=='Q') ) {
                int LA28_75 = input.LA(3);

                if ( (LA28_75=='.'||(LA28_75>='0' && LA28_75<='9')||(LA28_75>='A' && LA28_75<='Z')||LA28_75=='_'||(LA28_75>='a' && LA28_75<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=17;}
            }
            else {
                alt28=37;}
            }
            break;
        case 'e':
            {
            int LA28_26 = input.LA(2);

            if ( (LA28_26=='q') ) {
                int LA28_76 = input.LA(3);

                if ( (LA28_76=='.'||(LA28_76>='0' && LA28_76<='9')||(LA28_76>='A' && LA28_76<='Z')||LA28_76=='_'||(LA28_76>='a' && LA28_76<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=17;}
            }
            else {
                alt28=37;}
            }
            break;
        case '>':
            {
            int LA28_27 = input.LA(2);

            if ( (LA28_27=='=') ) {
                alt28=21;
            }
            else {
                alt28=19;}
            }
            break;
        case 'G':
            {
            switch ( input.LA(2) ) {
            case 'T':
                {
                int LA28_79 = input.LA(3);

                if ( (LA28_79=='.'||(LA28_79>='0' && LA28_79<='9')||(LA28_79>='A' && LA28_79<='Z')||LA28_79=='_'||(LA28_79>='a' && LA28_79<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=19;}
                }
                break;
            case 'E':
                {
                int LA28_80 = input.LA(3);

                if ( (LA28_80=='Q') ) {
                    int LA28_110 = input.LA(4);

                    if ( (LA28_110=='.'||(LA28_110>='0' && LA28_110<='9')||(LA28_110>='A' && LA28_110<='Z')||LA28_110=='_'||(LA28_110>='a' && LA28_110<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=21;}
                }
                else {
                    alt28=37;}
                }
                break;
            default:
                alt28=37;}

            }
            break;
        case 'g':
            {
            switch ( input.LA(2) ) {
            case 't':
                {
                int LA28_81 = input.LA(3);

                if ( (LA28_81=='.'||(LA28_81>='0' && LA28_81<='9')||(LA28_81>='A' && LA28_81<='Z')||LA28_81=='_'||(LA28_81>='a' && LA28_81<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=19;}
                }
                break;
            case 'e':
                {
                int LA28_82 = input.LA(3);

                if ( (LA28_82=='q') ) {
                    int LA28_111 = input.LA(4);

                    if ( (LA28_111=='.'||(LA28_111>='0' && LA28_111<='9')||(LA28_111>='A' && LA28_111<='Z')||LA28_111=='_'||(LA28_111>='a' && LA28_111<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=21;}
                }
                else {
                    alt28=37;}
                }
                break;
            default:
                alt28=37;}

            }
            break;
        case 'L':
            {
            switch ( input.LA(2) ) {
            case 'E':
                {
                int LA28_83 = input.LA(3);

                if ( (LA28_83=='Q') ) {
                    int LA28_112 = input.LA(4);

                    if ( (LA28_112=='.'||(LA28_112>='0' && LA28_112<='9')||(LA28_112>='A' && LA28_112<='Z')||LA28_112=='_'||(LA28_112>='a' && LA28_112<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=22;}
                }
                else {
                    alt28=37;}
                }
                break;
            case 'T':
                {
                int LA28_84 = input.LA(3);

                if ( (LA28_84=='.'||(LA28_84>='0' && LA28_84<='9')||(LA28_84>='A' && LA28_84<='Z')||LA28_84=='_'||(LA28_84>='a' && LA28_84<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=20;}
                }
                break;
            default:
                alt28=37;}

            }
            break;
        case 'l':
            {
            switch ( input.LA(2) ) {
            case 'e':
                {
                int LA28_85 = input.LA(3);

                if ( (LA28_85=='q') ) {
                    int LA28_113 = input.LA(4);

                    if ( (LA28_113=='.'||(LA28_113>='0' && LA28_113<='9')||(LA28_113>='A' && LA28_113<='Z')||LA28_113=='_'||(LA28_113>='a' && LA28_113<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=22;}
                }
                else {
                    alt28=37;}
                }
                break;
            case 't':
                {
                int LA28_86 = input.LA(3);

                if ( (LA28_86=='.'||(LA28_86>='0' && LA28_86<='9')||(LA28_86>='A' && LA28_86<='Z')||LA28_86=='_'||(LA28_86>='a' && LA28_86<='z')) ) {
                    alt28=37;
                }
                else {
                    alt28=20;}
                }
                break;
            default:
                alt28=37;}

            }
            break;
        case '^':
            {
            alt28=25;
            }
            break;
        case '~':
            {
            alt28=26;
            }
            break;
        case '*':
            {
            alt28=27;
            }
            break;
        case 'T':
            {
            int LA28_35 = input.LA(2);

            if ( (LA28_35=='I') ) {
                int LA28_87 = input.LA(3);

                if ( (LA28_87=='M') ) {
                    int LA28_114 = input.LA(4);

                    if ( (LA28_114=='E') ) {
                        int LA28_125 = input.LA(5);

                        if ( (LA28_125=='S') ) {
                            int LA28_134 = input.LA(6);

                            if ( (LA28_134=='.'||(LA28_134>='0' && LA28_134<='9')||(LA28_134>='A' && LA28_134<='Z')||LA28_134=='_'||(LA28_134>='a' && LA28_134<='z')) ) {
                                alt28=37;
                            }
                            else {
                                alt28=27;}
                        }
                        else {
                            alt28=37;}
                    }
                    else {
                        alt28=37;}
                }
                else {
                    alt28=37;}
            }
            else {
                alt28=37;}
            }
            break;
        case 't':
            {
            int LA28_36 = input.LA(2);

            if ( (LA28_36=='i') ) {
                int LA28_88 = input.LA(3);

                if ( (LA28_88=='m') ) {
                    int LA28_115 = input.LA(4);

                    if ( (LA28_115=='e') ) {
                        int LA28_126 = input.LA(5);

                        if ( (LA28_126=='s') ) {
                            int LA28_135 = input.LA(6);

                            if ( (LA28_135=='.'||(LA28_135>='0' && LA28_135<='9')||(LA28_135>='A' && LA28_135<='Z')||LA28_135=='_'||(LA28_135>='a' && LA28_135<='z')) ) {
                                alt28=37;
                            }
                            else {
                                alt28=27;}
                        }
                        else {
                            alt28=37;}
                    }
                    else {
                        alt28=37;}
                }
                else {
                    alt28=37;}
            }
            else {
                alt28=37;}
            }
            break;
        case '/':
            {
            alt28=28;
            }
            break;
        case 'd':
            {
            int LA28_38 = input.LA(2);

            if ( (LA28_38=='i') ) {
                int LA28_89 = input.LA(3);

                if ( (LA28_89=='v') ) {
                    int LA28_116 = input.LA(4);

                    if ( (LA28_116=='i') ) {
                        int LA28_127 = input.LA(5);

                        if ( (LA28_127=='d') ) {
                            int LA28_136 = input.LA(6);

                            if ( (LA28_136=='e') ) {
                                int LA28_140 = input.LA(7);

                                if ( (LA28_140=='.'||(LA28_140>='0' && LA28_140<='9')||(LA28_140>='A' && LA28_140<='Z')||LA28_140=='_'||(LA28_140>='a' && LA28_140<='z')) ) {
                                    alt28=37;
                                }
                                else {
                                    alt28=28;}
                            }
                            else {
                                alt28=37;}
                        }
                        else {
                            alt28=37;}
                    }
                    else {
                        alt28=37;}
                }
                else {
                    alt28=37;}
            }
            else {
                alt28=37;}
            }
            break;
        case '-':
            {
            int LA28_39 = input.LA(2);

            if ( ((LA28_39>='0' && LA28_39<='9')) ) {
                alt28=1;
            }
            else {
                alt28=30;}
            }
            break;
        case 'P':
            {
            int LA28_40 = input.LA(2);

            if ( (LA28_40=='L') ) {
                int LA28_91 = input.LA(3);

                if ( (LA28_91=='U') ) {
                    int LA28_117 = input.LA(4);

                    if ( (LA28_117=='S') ) {
                        int LA28_128 = input.LA(5);

                        if ( (LA28_128=='.'||(LA28_128>='0' && LA28_128<='9')||(LA28_128>='A' && LA28_128<='Z')||LA28_128=='_'||(LA28_128>='a' && LA28_128<='z')) ) {
                            alt28=37;
                        }
                        else {
                            alt28=29;}
                    }
                    else {
                        alt28=37;}
                }
                else {
                    alt28=37;}
            }
            else {
                alt28=37;}
            }
            break;
        case 'p':
            {
            int LA28_41 = input.LA(2);

            if ( (LA28_41=='l') ) {
                int LA28_92 = input.LA(3);

                if ( (LA28_92=='u') ) {
                    int LA28_118 = input.LA(4);

                    if ( (LA28_118=='s') ) {
                        int LA28_129 = input.LA(5);

                        if ( (LA28_129=='.'||(LA28_129>='0' && LA28_129<='9')||(LA28_129>='A' && LA28_129<='Z')||LA28_129=='_'||(LA28_129>='a' && LA28_129<='z')) ) {
                            alt28=37;
                        }
                        else {
                            alt28=29;}
                    }
                    else {
                        alt28=37;}
                }
                else {
                    alt28=37;}
            }
            else {
                alt28=37;}
            }
            break;
        case 'M':
            {
            switch ( input.LA(2) ) {
            case 'O':
                {
                int LA28_93 = input.LA(3);

                if ( (LA28_93=='D') ) {
                    int LA28_119 = input.LA(4);

                    if ( (LA28_119=='.'||(LA28_119>='0' && LA28_119<='9')||(LA28_119>='A' && LA28_119<='Z')||LA28_119=='_'||(LA28_119>='a' && LA28_119<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=31;}
                }
                else {
                    alt28=37;}
                }
                break;
            case 'I':
                {
                int LA28_94 = input.LA(3);

                if ( (LA28_94=='N') ) {
                    int LA28_120 = input.LA(4);

                    if ( (LA28_120=='U') ) {
                        int LA28_130 = input.LA(5);

                        if ( (LA28_130=='S') ) {
                            int LA28_137 = input.LA(6);

                            if ( (LA28_137=='.'||(LA28_137>='0' && LA28_137<='9')||(LA28_137>='A' && LA28_137<='Z')||LA28_137=='_'||(LA28_137>='a' && LA28_137<='z')) ) {
                                alt28=37;
                            }
                            else {
                                alt28=30;}
                        }
                        else {
                            alt28=37;}
                    }
                    else {
                        alt28=37;}
                }
                else {
                    alt28=37;}
                }
                break;
            default:
                alt28=37;}

            }
            break;
        case 'm':
            {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA28_95 = input.LA(3);

                if ( (LA28_95=='d') ) {
                    int LA28_121 = input.LA(4);

                    if ( (LA28_121=='.'||(LA28_121>='0' && LA28_121<='9')||(LA28_121>='A' && LA28_121<='Z')||LA28_121=='_'||(LA28_121>='a' && LA28_121<='z')) ) {
                        alt28=37;
                    }
                    else {
                        alt28=31;}
                }
                else {
                    alt28=37;}
                }
                break;
            case 'i':
                {
                int LA28_96 = input.LA(3);

                if ( (LA28_96=='n') ) {
                    int LA28_122 = input.LA(4);

                    if ( (LA28_122=='u') ) {
                        int LA28_131 = input.LA(5);

                        if ( (LA28_131=='s') ) {
                            int LA28_138 = input.LA(6);

                            if ( (LA28_138=='.'||(LA28_138>='0' && LA28_138<='9')||(LA28_138>='A' && LA28_138<='Z')||LA28_138=='_'||(LA28_138>='a' && LA28_138<='z')) ) {
                                alt28=37;
                            }
                            else {
                                alt28=30;}
                        }
                        else {
                            alt28=37;}
                    }
                    else {
                        alt28=37;}
                }
                else {
                    alt28=37;}
                }
                break;
            default:
                alt28=37;}

            }
            break;
        case '%':
            {
            alt28=31;
            }
            break;
        case '?':
            {
            alt28=32;
            }
            break;
        case ':':
            {
            alt28=33;
            }
            break;
        case '\'':
            {
            int LA28_47 = input.LA(2);

            if ( ((LA28_47>='!' && LA28_47<='~')) ) {
                alt28=37;
            }
            else {
                alt28=34;}
            }
            break;
        case '\"':
            {
            int LA28_48 = input.LA(2);

            if ( (LA28_48=='\t'||LA28_48=='\r'||(LA28_48>=' ' && LA28_48<='~')) ) {
                alt28=36;
            }
            else {
                alt28=35;}
            }
            break;
        case 'C':
        case 'F':
        case 'H':
        case 'J':
        case 'K':
        case 'Q':
        case 'R':
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
        case 'f':
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
            alt28=37;
            }
            break;
        case '$':
            {
            alt28=38;
            }
            break;
        case '\t':
        case '\n':
        case '\r':
        case ' ':
            {
            alt28=39;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( NUMBER | BIND | IF | DO | LPAREN | RPAREN | LSQUARE | RSQUARE | LBRACE | RBRACE | SEPR | DOT | ASSIGN | OR | AND | NOT | EQ | NEQ | GT | LT | GEQ | LEQ | BOR | BAND | BXOR | TWIDDLE | MUL | DIV | PLUS | MINUS | MOD | TERN_IF | COLON | QUOTE | DQUOTE | STRING | SYMBOL | DOLLARSYM | WS );", 28, 0, input);

            throw nvae;
        }

        switch (alt28) {
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
                // dd/grammar/ECAToken.g:1:28: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 6 :
                // dd/grammar/ECAToken.g:1:35: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 7 :
                // dd/grammar/ECAToken.g:1:42: LSQUARE
                {
                mLSQUARE(); 

                }
                break;
            case 8 :
                // dd/grammar/ECAToken.g:1:50: RSQUARE
                {
                mRSQUARE(); 

                }
                break;
            case 9 :
                // dd/grammar/ECAToken.g:1:58: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 10 :
                // dd/grammar/ECAToken.g:1:65: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 11 :
                // dd/grammar/ECAToken.g:1:72: SEPR
                {
                mSEPR(); 

                }
                break;
            case 12 :
                // dd/grammar/ECAToken.g:1:77: DOT
                {
                mDOT(); 

                }
                break;
            case 13 :
                // dd/grammar/ECAToken.g:1:81: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 14 :
                // dd/grammar/ECAToken.g:1:88: OR
                {
                mOR(); 

                }
                break;
            case 15 :
                // dd/grammar/ECAToken.g:1:91: AND
                {
                mAND(); 

                }
                break;
            case 16 :
                // dd/grammar/ECAToken.g:1:95: NOT
                {
                mNOT(); 

                }
                break;
            case 17 :
                // dd/grammar/ECAToken.g:1:99: EQ
                {
                mEQ(); 

                }
                break;
            case 18 :
                // dd/grammar/ECAToken.g:1:102: NEQ
                {
                mNEQ(); 

                }
                break;
            case 19 :
                // dd/grammar/ECAToken.g:1:106: GT
                {
                mGT(); 

                }
                break;
            case 20 :
                // dd/grammar/ECAToken.g:1:109: LT
                {
                mLT(); 

                }
                break;
            case 21 :
                // dd/grammar/ECAToken.g:1:112: GEQ
                {
                mGEQ(); 

                }
                break;
            case 22 :
                // dd/grammar/ECAToken.g:1:116: LEQ
                {
                mLEQ(); 

                }
                break;
            case 23 :
                // dd/grammar/ECAToken.g:1:120: BOR
                {
                mBOR(); 

                }
                break;
            case 24 :
                // dd/grammar/ECAToken.g:1:124: BAND
                {
                mBAND(); 

                }
                break;
            case 25 :
                // dd/grammar/ECAToken.g:1:129: BXOR
                {
                mBXOR(); 

                }
                break;
            case 26 :
                // dd/grammar/ECAToken.g:1:134: TWIDDLE
                {
                mTWIDDLE(); 

                }
                break;
            case 27 :
                // dd/grammar/ECAToken.g:1:142: MUL
                {
                mMUL(); 

                }
                break;
            case 28 :
                // dd/grammar/ECAToken.g:1:146: DIV
                {
                mDIV(); 

                }
                break;
            case 29 :
                // dd/grammar/ECAToken.g:1:150: PLUS
                {
                mPLUS(); 

                }
                break;
            case 30 :
                // dd/grammar/ECAToken.g:1:155: MINUS
                {
                mMINUS(); 

                }
                break;
            case 31 :
                // dd/grammar/ECAToken.g:1:161: MOD
                {
                mMOD(); 

                }
                break;
            case 32 :
                // dd/grammar/ECAToken.g:1:165: TERN_IF
                {
                mTERN_IF(); 

                }
                break;
            case 33 :
                // dd/grammar/ECAToken.g:1:173: COLON
                {
                mCOLON(); 

                }
                break;
            case 34 :
                // dd/grammar/ECAToken.g:1:179: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 35 :
                // dd/grammar/ECAToken.g:1:185: DQUOTE
                {
                mDQUOTE(); 

                }
                break;
            case 36 :
                // dd/grammar/ECAToken.g:1:192: STRING
                {
                mSTRING(); 

                }
                break;
            case 37 :
                // dd/grammar/ECAToken.g:1:199: SYMBOL
                {
                mSYMBOL(); 

                }
                break;
            case 38 :
                // dd/grammar/ECAToken.g:1:206: DOLLARSYM
                {
                mDOLLARSYM(); 

                }
                break;
            case 39 :
                // dd/grammar/ECAToken.g:1:216: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA25 dfa25 = new DFA25(this);
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
            return "42:1: NUMBER : ( INTEGER | FLOAT );";
        }
    }
    static final String DFA25_eotS =
        "\1\uffff\2\3\2\uffff";
    static final String DFA25_eofS =
        "\5\uffff";
    static final String DFA25_minS =
        "\1\101\2\56\2\uffff";
    static final String DFA25_maxS =
        "\3\172\2\uffff";
    static final String DFA25_acceptS =
        "\3\uffff\1\2\1\1";
    static final String DFA25_specialS =
        "\5\uffff}>";
    static final String[] DFA25_transitionS = {
            "\32\1\4\uffff\1\1\1\uffff\32\1",
            "\1\4\1\uffff\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
            "\1\4\1\uffff\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
            "",
            ""
    };

    static final short[] DFA25_eot = DFA.unpackEncodedString(DFA25_eotS);
    static final short[] DFA25_eof = DFA.unpackEncodedString(DFA25_eofS);
    static final char[] DFA25_min = DFA.unpackEncodedStringToUnsignedChars(DFA25_minS);
    static final char[] DFA25_max = DFA.unpackEncodedStringToUnsignedChars(DFA25_maxS);
    static final short[] DFA25_accept = DFA.unpackEncodedString(DFA25_acceptS);
    static final short[] DFA25_special = DFA.unpackEncodedString(DFA25_specialS);
    static final short[][] DFA25_transition;

    static {
        int numStates = DFA25_transitionS.length;
        DFA25_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA25_transition[i] = DFA.unpackEncodedString(DFA25_transitionS[i]);
        }
    }

    class DFA25 extends DFA {

        public DFA25(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 25;
            this.eot = DFA25_eot;
            this.eof = DFA25_eof;
            this.min = DFA25_min;
            this.max = DFA25_max;
            this.accept = DFA25_accept;
            this.special = DFA25_special;
            this.transition = DFA25_transition;
        }
        public String getDescription() {
            return "230:1: fragment DOTSYM : ( BARESYM DOT DOTSYM | BARESYM );";
        }
    }
 

}