/* The following code was generated by JFlex 1.4.3 on 26/06/2023, 10:08 */

/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
* @authors Andrew Dinn
*/

package org.jboss.byteman.rule.grammar;

import java_cup.runtime.*;
import org.jboss.byteman.rule.grammar.PrintableSymbol;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 26/06/2023, 10:08 from the specification file
 * /home/adinn/jboss/byteman/git/byteman/agent/grammar/flex/ECAToken.flex
 */
public class ECATokenLexer implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int STRING = 2;
  public static final int QUOTEDIDENT = 4;
  public static final int YYINITIAL = 0;
  public static final int COMMENT = 6;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3, 3
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\3\1\2\1\0\1\3\1\1\22\0\1\3\1\70\1\113"+
    "\1\111\1\5\1\106\1\67\1\114\1\53\1\54\1\76\1\10\1\62"+
    "\1\65\1\12\1\101\1\6\11\7\1\110\1\61\1\64\1\63\1\71"+
    "\1\107\1\112\1\43\1\16\1\4\1\14\1\40\1\15\1\32\1\31"+
    "\1\17\2\4\1\13\1\77\1\20\1\26\1\104\1\74\1\36\1\44"+
    "\1\30\1\37\1\102\1\50\3\4\1\55\1\115\1\56\1\72\1\4"+
    "\1\0\1\45\1\21\1\52\1\24\1\11\1\25\1\35\1\34\1\22"+
    "\2\4\1\46\1\100\1\23\1\27\1\105\1\75\1\41\1\47\1\33"+
    "\1\42\1\103\1\51\3\4\1\57\1\66\1\60\1\73\uff81\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\4\0\1\1\2\2\1\3\1\1\2\4\1\5\1\3"+
    "\1\6\30\3\1\7\1\10\1\11\1\12\1\13\1\14"+
    "\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24"+
    "\1\25\1\26\1\27\1\30\2\3\1\31\2\3\1\32"+
    "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\1\41\1\40\2\44\1\45\1\46\2\47\3\50"+
    "\2\0\1\51\1\52\1\53\1\20\1\54\1\3\1\55"+
    "\2\3\1\56\2\3\1\57\2\3\1\57\4\3\1\60"+
    "\3\3\1\25\1\61\10\3\1\53\1\54\1\62\1\0"+
    "\1\60\1\63\1\57\1\61\1\64\6\3\1\65\1\66"+
    "\1\67\1\70\1\71\2\51\1\0\1\72\1\51\3\3"+
    "\1\24\1\3\1\73\2\3\1\24\13\3\1\63\1\3"+
    "\1\17\1\74\1\3\1\32\5\3\1\75\1\3\1\76"+
    "\6\3\1\77\7\3\1\5\1\3\1\100\4\3\1\30"+
    "\1\101\2\3\1\102\1\21\1\31\3\3\1\103\1\104"+
    "\3\3\1\105";

  private static int [] zzUnpackAction() {
    int [] result = new int[214];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\116\0\234\0\352\0\u0138\0\u0186\0\u0138\0\u01d4"+
    "\0\u0222\0\u0270\0\u02be\0\u030c\0\u035a\0\u0138\0\u03a8\0\u03f6"+
    "\0\u0444\0\u0492\0\u04e0\0\u052e\0\u057c\0\u05ca\0\u0618\0\u0666"+
    "\0\u06b4\0\u0702\0\u0750\0\u079e\0\u07ec\0\u083a\0\u0888\0\u08d6"+
    "\0\u0924\0\u0972\0\u09c0\0\u0a0e\0\u0a5c\0\u0aaa\0\u0138\0\u0138"+
    "\0\u0138\0\u0138\0\u0138\0\u0138\0\u0138\0\u0138\0\u0af8\0\u0b46"+
    "\0\u030c\0\u0b94\0\u0be2\0\u0c30\0\u0c7e\0\u0138\0\u0138\0\u0138"+
    "\0\u0ccc\0\u0d1a\0\u0138\0\u0d68\0\u0db6\0\u0138\0\u0138\0\u0138"+
    "\0\u0138\0\u0138\0\u0138\0\u0e04\0\u0138\0\u0138\0\u0138\0\u0e52"+
    "\0\u0ea0\0\u0eee\0\u0138\0\u0138\0\u0138\0\u0f3c\0\u0138\0\u0f8a"+
    "\0\u0138\0\u0fd8\0\u1026\0\u1074\0\u10c2\0\u0138\0\u01d4\0\u01d4"+
    "\0\u01d4\0\u1110\0\u01d4\0\u115e\0\u11ac\0\u01d4\0\u11fa\0\u1248"+
    "\0\u1296\0\u12e4\0\u1332\0\u1380\0\u13ce\0\u141c\0\u146a\0\u14b8"+
    "\0\u01d4\0\u1506\0\u1554\0\u15a2\0\u01d4\0\u01d4\0\u15f0\0\u163e"+
    "\0\u168c\0\u16da\0\u1728\0\u1776\0\u17c4\0\u1812\0\u0138\0\u0138"+
    "\0\u0138\0\u1860\0\u0138\0\u0138\0\u0138\0\u0138\0\u18ae\0\u18fc"+
    "\0\u194a\0\u1998\0\u19e6\0\u1a34\0\u1a82\0\u0138\0\u0138\0\u0138"+
    "\0\u0138\0\u0138\0\u1ad0\0\u1b1e\0\u1b6c\0\u0138\0\u0138\0\u1bba"+
    "\0\u1c08\0\u1c56\0\u1ca4\0\u1cf2\0\u01d4\0\u1d40\0\u1d8e\0\u1ddc"+
    "\0\u1e2a\0\u1e78\0\u1ec6\0\u1f14\0\u1f62\0\u1fb0\0\u1ffe\0\u204c"+
    "\0\u209a\0\u20e8\0\u2136\0\u01d4\0\u2184\0\u0138\0\u0138\0\u21d2"+
    "\0\u01d4\0\u2220\0\u226e\0\u22bc\0\u230a\0\u2358\0\u01d4\0\u23a6"+
    "\0\u01d4\0\u23f4\0\u2442\0\u2490\0\u24de\0\u252c\0\u257a\0\u01d4"+
    "\0\u25c8\0\u2616\0\u2664\0\u26b2\0\u2700\0\u274e\0\u279c\0\u01d4"+
    "\0\u27ea\0\u01d4\0\u2838\0\u2886\0\u28d4\0\u2922\0\u01d4\0\u01d4"+
    "\0\u2970\0\u29be\0\u01d4\0\u01d4\0\u01d4\0\u2a0c\0\u2a5a\0\u2aa8"+
    "\0\u01d4\0\u01d4\0\u2af6\0\u2b44\0\u2b92\0\u01d4";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[214];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\5\1\6\2\7\1\10\1\11\1\12\1\13\1\14"+
    "\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24"+
    "\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34"+
    "\1\10\1\35\1\36\1\10\1\37\1\40\1\10\1\41"+
    "\1\42\1\10\1\43\1\10\1\44\1\45\3\10\1\46"+
    "\1\47\1\50\1\51\1\52\1\53\1\54\1\55\1\56"+
    "\1\57\1\60\1\61\1\62\1\63\1\64\1\65\1\66"+
    "\1\67\2\10\1\70\1\71\1\72\1\73\2\10\1\74"+
    "\1\75\1\76\1\77\1\100\1\101\1\5\1\102\1\103"+
    "\1\5\1\104\1\105\1\106\110\104\1\107\1\104\1\110"+
    "\1\111\1\112\1\113\111\111\1\114\1\111\1\115\1\116"+
    "\1\117\113\115\120\0\1\7\117\0\4\10\1\0\1\10"+
    "\1\0\40\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\1\120\1\0\1\121\1\122\1\123\1\120\1\0"+
    "\40\120\12\0\1\123\2\0\1\121\1\0\1\121\1\0"+
    "\2\120\1\121\2\120\1\0\4\120\3\0\2\121\14\0"+
    "\1\124\1\125\1\126\24\0\1\124\63\0\2\13\1\0"+
    "\1\124\1\125\1\126\24\0\1\124\63\0\1\12\1\13"+
    "\112\0\4\10\1\0\1\10\1\0\40\10\21\0\1\10"+
    "\1\127\1\0\2\10\1\0\4\10\14\0\4\10\1\0"+
    "\1\10\1\0\15\10\1\130\7\10\1\131\12\10\21\0"+
    "\2\10\1\0\2\10\1\0\4\10\14\0\4\10\1\0"+
    "\1\10\1\0\4\10\1\132\6\10\1\133\24\10\21\0"+
    "\2\10\1\0\2\10\1\0\4\10\14\0\4\10\1\0"+
    "\1\10\1\0\30\10\1\134\7\10\21\0\2\10\1\0"+
    "\2\10\1\0\4\10\14\0\4\10\1\0\1\10\1\0"+
    "\4\10\1\135\33\10\21\0\2\10\1\0\2\10\1\0"+
    "\4\10\14\0\4\10\1\0\1\10\1\0\2\10\1\136"+
    "\35\10\21\0\2\10\1\0\2\10\1\0\4\10\14\0"+
    "\4\10\1\0\1\10\1\0\13\10\1\137\10\10\1\140"+
    "\1\141\12\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\7\10\1\142\30\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\10\10\1\143\1\10\1\136\25\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\144\1\0\14\10\1\145\12\10\1\146\10\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\7\10\1\147\4\10\1\133\23\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\32\10\1\150\5\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\23\10\1\151\14\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\26\10"+
    "\1\151\11\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\4\10\1\152\11\10"+
    "\1\153\4\10\1\154\14\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\15\10"+
    "\1\155\7\10\1\156\12\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\7\10"+
    "\1\157\11\10\1\160\4\10\1\161\11\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\156"+
    "\1\0\20\10\1\155\17\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\25\10"+
    "\1\162\12\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\40\10\21\0\1\127"+
    "\1\10\1\0\2\10\1\0\4\10\14\0\4\10\1\0"+
    "\1\163\1\0\40\10\21\0\2\10\1\0\2\10\1\0"+
    "\4\10\14\0\4\10\1\0\1\10\1\0\5\10\1\164"+
    "\32\10\21\0\2\10\1\0\2\10\1\0\4\10\14\0"+
    "\4\10\1\0\1\10\1\0\10\10\1\165\27\10\21\0"+
    "\2\10\1\0\2\10\1\0\4\10\14\0\4\10\1\0"+
    "\1\131\1\0\20\10\1\130\17\10\21\0\2\10\1\0"+
    "\2\10\1\0\4\10\14\0\4\10\1\0\1\10\1\0"+
    "\33\10\1\166\4\10\21\0\2\10\1\0\2\10\1\0"+
    "\4\10\73\0\1\167\115\0\1\170\1\171\1\172\116\0"+
    "\1\173\116\0\1\174\111\0\1\175\115\0\1\176\5\0"+
    "\1\177\30\0\4\10\1\0\1\10\1\0\4\10\1\200"+
    "\6\10\1\201\24\10\21\0\2\10\1\0\2\10\1\0"+
    "\4\10\14\0\4\10\1\0\1\10\1\0\7\10\1\202"+
    "\4\10\1\203\23\10\21\0\2\10\1\0\2\10\1\0"+
    "\4\10\14\0\4\10\1\0\1\10\1\0\1\204\37\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\33\10\1\205\4\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\10\0\1\104\2\0\110\104"+
    "\1\0\1\104\24\0\1\206\7\0\1\207\5\0\1\210"+
    "\51\0\1\211\1\0\1\212\1\111\2\0\111\111\1\0"+
    "\1\111\2\0\1\113\115\0\1\117\117\0\4\120\1\0"+
    "\1\120\1\0\40\120\21\0\2\120\1\0\2\120\1\0"+
    "\4\120\16\0\2\122\114\0\1\121\1\122\114\0\1\213"+
    "\1\214\1\215\54\0\1\215\36\0\2\125\1\0\1\124"+
    "\2\0\1\216\1\217\22\0\1\124\61\0\4\10\1\0"+
    "\1\10\1\0\40\10\21\0\2\10\1\0\2\10\1\0"+
    "\1\220\3\10\14\0\4\10\1\0\1\10\1\0\1\221"+
    "\37\10\21\0\2\10\1\0\2\10\1\0\4\10\14\0"+
    "\4\10\1\0\1\10\1\0\5\10\1\222\32\10\21\0"+
    "\2\10\1\0\2\10\1\0\4\10\14\0\4\10\1\0"+
    "\1\10\1\0\15\10\1\223\22\10\21\0\2\10\1\0"+
    "\2\10\1\0\4\10\14\0\4\10\1\0\1\10\1\0"+
    "\1\224\37\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\35\10\1\225\2\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\10\10\1\226\27\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\34\10\1\227\3\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\36\10"+
    "\1\225\1\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\20\10\1\230\17\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\33\10\1\231\4\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\40\10\21\0\2\10\1\0\2\10\1\0\1\10"+
    "\1\232\2\10\14\0\4\10\1\0\1\10\1\0\33\10"+
    "\1\233\4\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\40\10\21\0\2\10"+
    "\1\0\1\234\1\10\1\0\4\10\14\0\4\10\1\0"+
    "\1\10\1\0\23\10\1\235\14\10\21\0\2\10\1\0"+
    "\2\10\1\0\4\10\14\0\4\10\1\0\1\10\1\0"+
    "\24\10\1\236\13\10\21\0\2\10\1\0\2\10\1\0"+
    "\4\10\14\0\4\10\1\0\1\10\1\0\40\10\21\0"+
    "\2\10\1\0\1\10\1\237\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\26\10\1\240\11\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\27\10\1\241\10\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\15\10"+
    "\1\242\22\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\20\10\1\243\17\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\1\10\1\244\36\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\11\10\1\244\26\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\32\10"+
    "\1\245\5\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\75\0\1\246\121\0\1\247\30\0\4\10\1\0\1\10"+
    "\1\0\5\10\1\250\32\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\1\10"+
    "\1\251\36\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\10\10\1\252\27\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\11\10\1\251\26\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\24\10\1\253\13\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\27\10"+
    "\1\254\10\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\24\0\1\216\1\217\106\0\2\214\4\0\1\216\1\217"+
    "\106\0\1\213\1\214\112\0\4\10\1\0\1\10\1\0"+
    "\4\10\1\255\33\10\21\0\2\10\1\0\2\10\1\0"+
    "\4\10\14\0\4\10\1\0\1\10\1\0\31\10\1\256"+
    "\6\10\21\0\2\10\1\0\2\10\1\0\4\10\14\0"+
    "\4\10\1\0\1\10\1\0\1\10\1\257\36\10\21\0"+
    "\2\10\1\0\2\10\1\0\4\10\14\0\4\10\1\0"+
    "\1\10\1\0\16\10\1\260\21\10\21\0\2\10\1\0"+
    "\2\10\1\0\4\10\14\0\4\10\1\0\1\10\1\0"+
    "\1\261\37\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\11\10\1\257\26\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\20\10\1\262\17\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\21\10\1\263\16\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\33\10"+
    "\1\261\4\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\7\10\1\264\30\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\34\10\1\265\3\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\25\10\1\266\12\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\13\10"+
    "\1\267\24\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\25\10\1\270\12\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\271\1\0\40\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\14\10"+
    "\1\272\23\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\270\1\0\40\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\24\10\1\273\13\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\27\10"+
    "\1\274\10\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\34\10\1\275\3\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\24\10\1\276\13\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\27\10\1\277\10\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\31\10"+
    "\1\300\6\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\34\10\1\300\3\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\1\10\1\301\36\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\25\10\1\302\12\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\4\10"+
    "\1\303\33\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\32\10\1\304\5\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\7\10\1\305\30\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\11\10\1\306\26\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\302\1\0\40\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\31\10\1\307\6\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\35\10\1\310\2\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\34\10"+
    "\1\307\3\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\36\10\1\310\1\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\23\10\1\311\14\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\26\10\1\312\11\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\34\10"+
    "\1\313\3\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\31\10\1\314\6\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\34\10\1\314\3\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\25\10\1\315\12\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\5\10"+
    "\1\316\32\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\10\10\1\317\27\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\10\10\1\320\27\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\315"+
    "\1\0\40\10\21\0\2\10\1\0\2\10\1\0\4\10"+
    "\14\0\4\10\1\0\1\10\1\0\5\10\1\321\32\10"+
    "\21\0\2\10\1\0\2\10\1\0\4\10\14\0\4\10"+
    "\1\0\1\10\1\0\10\10\1\321\27\10\21\0\2\10"+
    "\1\0\2\10\1\0\4\10\14\0\4\10\1\0\1\10"+
    "\1\0\17\10\1\322\20\10\21\0\2\10\1\0\2\10"+
    "\1\0\4\10\14\0\4\10\1\0\1\10\1\0\37\10"+
    "\1\323\21\0\2\10\1\0\2\10\1\0\4\10\14\0"+
    "\4\10\1\0\1\10\1\0\22\10\1\322\15\10\21\0"+
    "\2\10\1\0\2\10\1\0\4\10\14\0\4\10\1\0"+
    "\1\324\1\0\40\10\21\0\2\10\1\0\2\10\1\0"+
    "\4\10\14\0\4\10\1\0\1\10\1\0\14\10\1\325"+
    "\23\10\21\0\2\10\1\0\2\10\1\0\4\10\14\0"+
    "\4\10\1\0\1\10\1\0\12\10\1\326\25\10\21\0"+
    "\2\10\1\0\2\10\1\0\4\10\10\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[11232];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\4\0\1\11\1\1\1\11\6\1\1\11\30\1\10\11"+
    "\7\1\3\11\2\1\1\11\2\1\6\11\1\1\3\11"+
    "\3\1\3\11\1\1\1\11\1\1\1\11\1\1\2\0"+
    "\1\1\1\11\40\1\3\11\1\0\4\11\7\1\5\11"+
    "\2\1\1\0\2\11\26\1\2\11\57\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[214];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
  StringBuffer string = new StringBuffer();

  private int startLine = 0;

  private String file = "";

  public void setStartLine(int startLine)
  {
    this.startLine = startLine;
  }

  public void setFile(String file)
  {
    this.file = file;
  }

  private Symbol symbol(int type) {
    return new PrintableSymbol(type, file, yyline + startLine, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new PrintableSymbol(type, file, yyline + startLine, yycolumn, value);
  }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public ECATokenLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public ECATokenLexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 178) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to ZZ_INITIAL.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position pos from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 2: 
          { /* ignore */
          }
        case 70: break;
        case 66: 
          { return symbol(sym.CLASS);
          }
        case 71: break;
        case 42: 
          { return symbol(sym.LONG_LITERAL, Long.valueOf(yytext().substring(0, yytext().length() - 1)));
          }
        case 72: break;
        case 1: 
          { throw new Error("Illegal character <"+ yytext()+">");
          }
        case 73: break;
        case 19: 
          { return symbol(sym.BAND);
          }
        case 74: break;
        case 8: 
          { return symbol(sym.RPAREN);
          }
        case 75: break;
        case 35: 
          { yybegin(YYINITIAL);
			  return symbol(sym.STRING_LITERAL,
					string.toString());
          }
        case 76: break;
        case 63: 
          { return symbol(sym.BOOLEAN_LITERAL, Boolean.TRUE);
          }
        case 77: break;
        case 21: 
          { return symbol(sym.GT);
          }
        case 78: break;
        case 54: 
          { string.append('\t');
          }
        case 79: break;
        case 9: 
          { return symbol(sym.LSQUARE);
          }
        case 80: break;
        case 16: 
          { return symbol(sym.LT);
          }
        case 81: break;
        case 52: 
          { return symbol(sym.RSH);
          }
        case 82: break;
        case 11: 
          { return symbol(sym.LBRACE);
          }
        case 83: break;
        case 30: 
          { string.setLength(0); yybegin(STRING);
          }
        case 84: break;
        case 41: 
          { return symbol(sym.FLOAT_LITERAL, Float.valueOf(yytext()));
          }
        case 85: break;
        case 65: 
          { return symbol(sym.THROW);
          }
        case 86: break;
        case 51: 
          { return symbol(sym.AND);
          }
        case 87: break;
        case 20: 
          { return symbol(sym.NOT);
          }
        case 88: break;
        case 15: 
          { return symbol(sym.ASSIGN);
          }
        case 89: break;
        case 25: 
          { return symbol(sym.DIV);
          }
        case 90: break;
        case 57: 
          { string.append('\\');
          }
        case 91: break;
        case 67: 
          { return symbol(sym.RETURN);
          }
        case 92: break;
        case 62: 
          { return symbol(sym.NULL_LITERAL);
          }
        case 93: break;
        case 55: 
          { string.append('\r');
          }
        case 94: break;
        case 39: 
          { yybegin(YYINITIAL);
          }
        case 95: break;
        case 5: 
          { return symbol(sym.PLUS);
          }
        case 96: break;
        case 26: 
          { return symbol(sym.MOD);
          }
        case 97: break;
        case 29: 
          { yybegin(COMMENT);
          }
        case 98: break;
        case 3: 
          { return symbol(sym.IDENTIFIER, yytext());
          }
        case 99: break;
        case 58: 
          { return symbol(sym.DOUBLE_LITERAL, Double.valueOf(yytext().substring(0, yytext().length() - 1)));
          }
        case 100: break;
        case 45: 
          { return symbol(sym.DO);
          }
        case 101: break;
        case 56: 
          { string.append('\"');
          }
        case 102: break;
        case 28: 
          { return symbol(sym.COLON);
          }
        case 103: break;
        case 22: 
          { return symbol(sym.BXOR);
          }
        case 104: break;
        case 34: 
          { throw new Error("File " + file + " line " + (yyline + startLine) + " : newline in string");
          }
        case 105: break;
        case 46: 
          { return symbol(sym.IF);
          }
        case 106: break;
        case 64: 
          { return symbol(sym.BOOLEAN_LITERAL, Boolean.FALSE);
          }
        case 107: break;
        case 7: 
          { return symbol(sym.LPAREN);
          }
        case 108: break;
        case 48: 
          { return symbol(sym.OR);
          }
        case 109: break;
        case 43: 
          { return symbol(sym.EQ);
          }
        case 110: break;
        case 12: 
          { return symbol(sym.RBRACE);
          }
        case 111: break;
        case 53: 
          { string.append('\n');
          }
        case 112: break;
        case 13: 
          { return symbol(sym.SEMI);
          }
        case 113: break;
        case 10: 
          { return symbol(sym.RSQUARE);
          }
        case 114: break;
        case 47: 
          { return symbol(sym.NE);
          }
        case 115: break;
        case 60: 
          { return symbol(sym.URSH);
          }
        case 116: break;
        case 17: 
          { return symbol(sym.MINUS);
          }
        case 117: break;
        case 36: 
          { throw new Error("File " + file + " line " + (yyline + startLine) + " : newline in quoted identifier");
          }
        case 118: break;
        case 18: 
          { return symbol(sym.BOR);
          }
        case 119: break;
        case 31: 
          { string.setLength(0);  yybegin(QUOTEDIDENT);
          }
        case 120: break;
        case 59: 
          { return symbol(sym.NEW);
          }
        case 121: break;
        case 40: 
          { return symbol(sym.DOLLAR, yytext());
          }
        case 122: break;
        case 23: 
          { return symbol(sym.TWIDDLE);
          }
        case 123: break;
        case 33: 
          { throw new Error("File " + file + " line " + (yyline + startLine) + " : illegal character in string <"+ yytext()+">");
          }
        case 124: break;
        case 61: 
          { return symbol(sym.BIND);
          }
        case 125: break;
        case 50: 
          { return symbol(sym.LSH);
          }
        case 126: break;
        case 27: 
          { return symbol(sym.TERN_IF);
          }
        case 127: break;
        case 49: 
          { return symbol(sym.GE);
          }
        case 128: break;
        case 37: 
          { yybegin(YYINITIAL);
			  return symbol(sym.IDENTIFIER,
					string.toString());
          }
        case 129: break;
        case 14: 
          { return symbol(sym.COMMA);
          }
        case 130: break;
        case 69: 
          { return symbol(sym.INSTANCEOF);
          }
        case 131: break;
        case 44: 
          { return symbol(sym.LE);
          }
        case 132: break;
        case 24: 
          { return symbol(sym.MUL);
          }
        case 133: break;
        case 68: 
          { return symbol(sym.NOTHING);
          }
        case 134: break;
        case 4: 
          { return symbol(sym.INTEGER_LITERAL, Integer.valueOf(yytext()));
          }
        case 135: break;
        case 32: 
          { string.append( yytext() );
          }
        case 136: break;
        case 38: 
          { /*ignore */
          }
        case 137: break;
        case 6: 
          { return symbol(sym.DOT);
          }
        case 138: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              { return new java_cup.runtime.Symbol(sym.EOF); }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
