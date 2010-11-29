/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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
package org.jboss.byteman.agent.adapter;

import org.objectweb.asm.Opcodes;

public class OpcodesHelper implements Opcodes
{
/**
 * instruction type for visitInsn opcodes
 */
    public final static int INSN_NONE = 0;
/**
 * instruction type for visitIntInsn opcodes
 */
    public final static int INSN_INT = 1;
/**
 * instruction type for visitLdcInsn opcodes
 */
    public final static int INSN_LDC = 2;
/**
 * instruction type for visitVarInsn opcodes
 */
    public final static int INSN_VAR = 3;
/**
 * instruction type for visitIIncInsn opcodes
 */
    public final static int INSN_IINC = 4;
/**
 * instruction type for visitJumpInsn opcodes
 */
    public final static int INSN_JUMP = 5;
/**
 * instruction type for visitTableSwitchInsn opcodes
 */
    public final static int INSN_TSWITCH = 6;
/**
 * instruction type for visitTableLookupInsn opcodes
 */
    public final static int INSN_LOOKUP = 7;
/**
 * instruction type for visitFieldInsn opcodes
 */
    public final static int INSN_FIELD = 8;
/**
 * instruction type for visitMethodInsn opcodes
 */
    public final static int INSN_METHOD = 9;
/**
 * instruction type for visitInsn opcodes
 */
    public final static int INSN_TYPE = 10;
/**
 * instruction type for visitMultiANewArrayInsn opcodes
 */
    public final static int INSN_MULTIANEWARRAY = 11;
/**
 * instruction type for unused opcodes
 */
    public final static int INSN_UNUSED = 99;

/**
 * lookup table to derive instruction type from instruction
 */
    private static int[] insnType = new int[256];

/**
 * lookup table to derive instruction name from instruction
 */
    private static String[] insnName = new String[256];

/**
 * initializer block for insnType and insnName lookup tables
 */

    static {
        insnType[Opcodes.NOP] = INSN_NONE;
        insnType[Opcodes.ACONST_NULL] = INSN_NONE;
        insnType[Opcodes.ICONST_M1] = INSN_NONE;
        insnType[Opcodes.ICONST_0] = INSN_NONE;
        insnType[Opcodes.ICONST_1] = INSN_NONE;
        insnType[Opcodes.ICONST_2] = INSN_NONE;
        insnType[Opcodes.ICONST_3] = INSN_NONE;
        insnType[Opcodes.ICONST_4] = INSN_NONE;
        insnType[Opcodes.ICONST_5] = INSN_NONE;
        insnType[Opcodes.LCONST_0] = INSN_NONE;
        insnType[Opcodes.LCONST_1] = INSN_NONE;
        insnType[Opcodes.FCONST_0] = INSN_NONE;
        insnType[Opcodes.FCONST_1] = INSN_NONE;
        insnType[Opcodes.FCONST_2] = INSN_NONE;
        insnType[Opcodes.DCONST_0] = INSN_NONE;
        insnType[Opcodes.DCONST_1] = INSN_NONE;
        insnType[Opcodes.BIPUSH] = INSN_INT;
        insnType[Opcodes.SIPUSH] = INSN_INT;
        for (int i = 0; i < Opcodes.ILOAD - Opcodes.LDC; i++) {
            insnType[Opcodes.LDC + i] = INSN_LDC;
        }
        // insnType[Opcodes.LDC_W] = XXX;
        // insnType[Opcodes.LDC2_W] = XXX;
        insnType[Opcodes.ILOAD] = INSN_VAR;
        insnType[Opcodes.LLOAD] = INSN_VAR;
        insnType[Opcodes.FLOAD] = INSN_VAR;
        insnType[Opcodes.DLOAD] = INSN_VAR;
        for (int i = 0; i < Opcodes.IALOAD - Opcodes.ALOAD; i++) {
            insnType[Opcodes.ALOAD + i] = INSN_VAR;
        }
        // insnType[Opcodes.ILOAD_0] = INSN_VAR;
        // insnType[Opcodes.ILOAD_1] = INSN_VAR;
        // insnType[Opcodes.ILOAD_2] = INSN_VAR;
        // insnType[Opcodes.ILOAD_3] = INSN_VAR;
        // insnType[Opcodes.LLOAD_0] = INSN_VAR;
        // insnType[Opcodes.LLOAD_1] = INSN_VAR;
        // insnType[Opcodes.LLOAD_2] = INSN_VAR;
        // insnType[Opcodes.LLOAD_3] = INSN_VAR;
        // insnType[Opcodes.FLOAD_0] = INSN_VAR;
        // insnType[Opcodes.FLOAD_1] = INSN_VAR;
        // insnType[Opcodes.FLOAD_2] = INSN_VAR;
        // insnType[Opcodes.FLOAD_3] = INSN_VAR;
        // insnType[Opcodes.DLOAD_0] = INSN_VAR;
        // insnType[Opcodes.DLOAD_1] = INSN_VAR;
        // insnType[Opcodes.DLOAD_2] = INSN_VAR;
        // insnType[Opcodes.DLOAD_3] = INSN_VAR;
        // insnType[Opcodes.ALOAD_0] = INSN_VAR;
        // insnType[Opcodes.ALOAD_1] = INSN_VAR;
        // insnType[Opcodes.ALOAD_2] = INSN_VAR;
        // insnType[Opcodes.ALOAD_3] = INSN_VAR;
        insnType[Opcodes.IALOAD] = INSN_NONE;
        insnType[Opcodes.LALOAD] = INSN_NONE;
        insnType[Opcodes.FALOAD] = INSN_NONE;
        insnType[Opcodes.DALOAD] = INSN_NONE;
        insnType[Opcodes.AALOAD] = INSN_NONE;
        insnType[Opcodes.BALOAD] = INSN_NONE;
        insnType[Opcodes.CALOAD] = INSN_NONE;
        insnType[Opcodes.SALOAD] = INSN_NONE;
        insnType[Opcodes.ISTORE] = INSN_VAR;
        insnType[Opcodes.LSTORE] = INSN_VAR;
        insnType[Opcodes.FSTORE] = INSN_VAR;
        insnType[Opcodes.DSTORE] = INSN_VAR;
        for (int i = 0; i < Opcodes.IASTORE - Opcodes.ASTORE; i++) {
            insnType[Opcodes.ASTORE + i] = INSN_VAR;
        }
        // insnType[Opcodes.ISTORE_0] = INSN_VAR;
        // insnType[Opcodes.ISTORE_1] = INSN_VAR;
        // insnType[Opcodes.ISTORE_2] = INSN_VAR;
        // insnType[Opcodes.ISTORE_3] = INSN_VAR;
        // insnType[Opcodes.LSTORE_0] = INSN_VAR;
        // insnType[Opcodes.LSTORE_1] = INSN_VAR;
        // insnType[Opcodes.LSTORE_2] = INSN_VAR;
        // insnType[Opcodes.LSTORE_3] = INSN_VAR;
        // insnType[Opcodes.FSTORE_0] = INSN_VAR;
        // insnType[Opcodes.FSTORE_1] = INSN_VAR;
        // insnType[Opcodes.FSTORE_2] = INSN_VAR;
        // insnType[Opcodes.FSTORE_3] = INSN_VAR;
        // insnType[Opcodes.DSTORE_0] = INSN_VAR;
        // insnType[Opcodes.DSTORE_1] = INSN_VAR;
        // insnType[Opcodes.DSTORE_2] = INSN_VAR;
        // insnType[Opcodes.DSTORE_3] = INSN_VAR;
        // insnType[Opcodes.ASTORE_0] = INSN_VAR;
        // insnType[Opcodes.ASTORE_1] = INSN_VAR;
        // insnType[Opcodes.ASTORE_2] = INSN_VAR;
        // insnType[Opcodes.ASTORE_3] = INSN_VAR;
        insnType[Opcodes.IASTORE] = INSN_NONE;
        insnType[Opcodes.LASTORE] = INSN_NONE;
        insnType[Opcodes.FASTORE] = INSN_NONE;
        insnType[Opcodes.DASTORE] = INSN_NONE;
        insnType[Opcodes.AASTORE] = INSN_NONE;
        insnType[Opcodes.BASTORE] = INSN_NONE;
        insnType[Opcodes.CASTORE] = INSN_NONE;
        insnType[Opcodes.SASTORE] = INSN_NONE;
        insnType[Opcodes.POP] = INSN_NONE;
        insnType[Opcodes.POP2] = INSN_NONE;
        insnType[Opcodes.DUP] = INSN_NONE;
        insnType[Opcodes.DUP_X1] = INSN_NONE;
        insnType[Opcodes.DUP_X2] = INSN_NONE;
        insnType[Opcodes.DUP2] = INSN_NONE;
        insnType[Opcodes.DUP2_X1] = INSN_NONE;
        insnType[Opcodes.DUP2_X2] = INSN_NONE;
        insnType[Opcodes.SWAP] = INSN_NONE;
        insnType[Opcodes.IADD] = INSN_NONE;
        insnType[Opcodes.LADD] = INSN_NONE;
        insnType[Opcodes.FADD] = INSN_NONE;
        insnType[Opcodes.DADD] = INSN_NONE;
        insnType[Opcodes.ISUB] = INSN_NONE;
        insnType[Opcodes.LSUB] = INSN_NONE;
        insnType[Opcodes.FSUB] = INSN_NONE;
        insnType[Opcodes.DSUB] = INSN_NONE;
        insnType[Opcodes.IMUL] = INSN_NONE;
        insnType[Opcodes.LMUL] = INSN_NONE;
        insnType[Opcodes.FMUL] = INSN_NONE;
        insnType[Opcodes.DMUL] = INSN_NONE;
        insnType[Opcodes.IDIV] = INSN_NONE;
        insnType[Opcodes.LDIV] = INSN_NONE;
        insnType[Opcodes.FDIV] = INSN_NONE;
        insnType[Opcodes.DDIV] = INSN_NONE;
        insnType[Opcodes.IREM] = INSN_NONE;
        insnType[Opcodes.LREM] = INSN_NONE;
        insnType[Opcodes.FREM] = INSN_NONE;
        insnType[Opcodes.DREM] = INSN_NONE;
        insnType[Opcodes.INEG] = INSN_NONE;
        insnType[Opcodes.LNEG] = INSN_NONE;
        insnType[Opcodes.FNEG] = INSN_NONE;
        insnType[Opcodes.DNEG] = INSN_NONE;
        insnType[Opcodes.ISHL] = INSN_NONE;
        insnType[Opcodes.LSHL] = INSN_NONE;
        insnType[Opcodes.ISHR] = INSN_NONE;
        insnType[Opcodes.LSHR] = INSN_NONE;
        insnType[Opcodes.IUSHR] = INSN_NONE;
        insnType[Opcodes.LUSHR] = INSN_NONE;
        insnType[Opcodes.IAND] = INSN_NONE;
        insnType[Opcodes.LAND] = INSN_NONE;
        insnType[Opcodes.IOR] = INSN_NONE;
        insnType[Opcodes.LOR] = INSN_NONE;
        insnType[Opcodes.IXOR] = INSN_NONE;
        insnType[Opcodes.LXOR] = INSN_NONE;
        insnType[Opcodes.IINC] = INSN_IINC;
        insnType[Opcodes.I2L] = INSN_NONE;
        insnType[Opcodes.I2F] = INSN_NONE;
        insnType[Opcodes.I2D] = INSN_NONE;
        insnType[Opcodes.L2I] = INSN_NONE;
        insnType[Opcodes.L2F] = INSN_NONE;
        insnType[Opcodes.L2D] = INSN_NONE;
        insnType[Opcodes.F2I] = INSN_NONE;
        insnType[Opcodes.F2L] = INSN_NONE;
        insnType[Opcodes.F2D] = INSN_NONE;
        insnType[Opcodes.D2I] = INSN_NONE;
        insnType[Opcodes.D2L] = INSN_NONE;
        insnType[Opcodes.D2F] = INSN_NONE;
        insnType[Opcodes.I2B] = INSN_NONE;
        insnType[Opcodes.I2C] = INSN_NONE;
        insnType[Opcodes.I2S] = INSN_NONE;
        insnType[Opcodes.LCMP] = INSN_NONE;
        insnType[Opcodes.FCMPL] = INSN_NONE;
        insnType[Opcodes.FCMPG] = INSN_NONE;
        insnType[Opcodes.DCMPL] = INSN_NONE;
        insnType[Opcodes.DCMPG] = INSN_NONE;
        insnType[Opcodes.IFEQ] = INSN_JUMP;
        insnType[Opcodes.IFNE] = INSN_JUMP;
        insnType[Opcodes.IFLT] = INSN_JUMP;
        insnType[Opcodes.IFGE] = INSN_JUMP;
        insnType[Opcodes.IFGT] = INSN_JUMP;
        insnType[Opcodes.IFLE] = INSN_JUMP;
        insnType[Opcodes.IF_ICMPEQ] = INSN_JUMP;
        insnType[Opcodes.IF_ICMPNE] = INSN_JUMP;
        insnType[Opcodes.IF_ICMPLT] = INSN_JUMP;
        insnType[Opcodes.IF_ICMPGE] = INSN_JUMP;
        insnType[Opcodes.IF_ICMPGT] = INSN_JUMP;
        insnType[Opcodes.IF_ICMPLE] = INSN_JUMP;
        insnType[Opcodes.IF_ACMPEQ] = INSN_JUMP;
        insnType[Opcodes.IF_ACMPNE] = INSN_JUMP;
        insnType[Opcodes.GOTO] = INSN_JUMP;
        insnType[Opcodes.JSR] = INSN_JUMP;
        insnType[Opcodes.RET] = INSN_VAR;
        insnType[Opcodes.TABLESWITCH] = INSN_TSWITCH;
        insnType[Opcodes.LOOKUPSWITCH] = INSN_LOOKUP;
        insnType[Opcodes.IRETURN] = INSN_NONE;
        insnType[Opcodes.LRETURN] = INSN_NONE;
        insnType[Opcodes.FRETURN] = INSN_NONE;
        insnType[Opcodes.DRETURN] = INSN_NONE;
        insnType[Opcodes.ARETURN] = INSN_NONE;
        insnType[Opcodes.RETURN] = INSN_NONE;
        insnType[Opcodes.GETSTATIC] = INSN_FIELD;
        insnType[Opcodes.PUTSTATIC] = INSN_FIELD;
        insnType[Opcodes.GETFIELD] = INSN_FIELD;
        insnType[Opcodes.PUTFIELD] = INSN_FIELD;
        insnType[Opcodes.INVOKEVIRTUAL] = INSN_METHOD;
        insnType[Opcodes.INVOKESPECIAL] = INSN_METHOD;
        insnType[Opcodes.INVOKESTATIC] = INSN_METHOD;
        insnType[Opcodes.INVOKEINTERFACE] = INSN_METHOD;
        insnType[Opcodes.INVOKEINTERFACE  + 1] = INSN_UNUSED;
        // insnType[Opcodes.UNUSED] = INSN_UNUSED;
        insnType[Opcodes.NEW] = INSN_TYPE;
        insnType[Opcodes.NEWARRAY] = INSN_INT;
        insnType[Opcodes.ANEWARRAY] = INSN_TYPE;
        insnType[Opcodes.ARRAYLENGTH] = INSN_NONE;
        insnType[Opcodes.ATHROW] = INSN_NONE;
        insnType[Opcodes.CHECKCAST] = INSN_TYPE;
        insnType[Opcodes.INSTANCEOF] = INSN_TYPE;
        insnType[Opcodes.MONITORENTER] = INSN_NONE;
        insnType[Opcodes.MONITOREXIT] = INSN_NONE;
        insnType[Opcodes.MONITOREXIT + 1] = INSN_UNUSED;
        // insnType[Opcodes.WIDE] = INSN_UNUSED;
        insnType[Opcodes.MULTIANEWARRAY] = INSN_MULTIANEWARRAY;
        insnType[Opcodes.IFNULL] = INSN_JUMP;
        for (int i = 0; i < 3; i++) {
            insnType[Opcodes.IFNONNULL + i] = INSN_JUMP;
        }
        // insnType[Opcodes.GOTO_W] = INSN_JUMP;
        // insnType[Opcodes.JSR_W] = INSN_JUMP;


        insnName[Opcodes.NOP] = "NOP";
        insnName[Opcodes.ACONST_NULL] = "aconst_null";
        insnName[Opcodes.ICONST_M1] = "iconst_m1";
        insnName[Opcodes.ICONST_0] = "iconst_0";
        insnName[Opcodes.ICONST_1] = "iconst_1";
        insnName[Opcodes.ICONST_2] = "iconst_2";
        insnName[Opcodes.ICONST_3] = "iconst_3";
        insnName[Opcodes.ICONST_4] = "iconst_4";
        insnName[Opcodes.ICONST_5] = "iconst_5";
        insnName[Opcodes.LCONST_0] = "lconst_0";
        insnName[Opcodes.LCONST_1] = "lconst_1";
        insnName[Opcodes.FCONST_0] = "fconst_0";
        insnName[Opcodes.FCONST_1] = "fconst_1";
        insnName[Opcodes.FCONST_2] = "fconst_2";
        insnName[Opcodes.DCONST_0] = "dconst_0";
        insnName[Opcodes.DCONST_1] = "dconst_1";
        insnName[Opcodes.BIPUSH] = "bipush";
        insnName[Opcodes.SIPUSH] = "sipush";
        insnName[Opcodes.LDC] = "ldc";
        insnName[Opcodes.LDC + 1] = "ldc_w";
        insnName[Opcodes.LDC + 2] = "ldc2_w";
        insnName[Opcodes.ILOAD] = "iload";
        insnName[Opcodes.LLOAD] = "lload";
        insnName[Opcodes.FLOAD] = "fload";
        insnName[Opcodes.DLOAD] = "dload";
        insnName[Opcodes.ALOAD] = "aload";
        insnName[Opcodes.ALOAD + 1] = "iload_0";
        insnName[Opcodes.ALOAD + 2] = "iload_1";
        insnName[Opcodes.ALOAD + 3] = "iload_2";
        insnName[Opcodes.ALOAD + 4] = "iload_3";
        insnName[Opcodes.ALOAD + 5] = "lload_0";
        insnName[Opcodes.ALOAD + 6] = "lload_1";
        insnName[Opcodes.ALOAD + 7] = "lload_2";
        insnName[Opcodes.ALOAD + 8] = "lload_3";
        insnName[Opcodes.ALOAD + 9] = "fload_0";
        insnName[Opcodes.ALOAD + 10] = "fload_1";
        insnName[Opcodes.ALOAD + 11] = "fload_2";
        insnName[Opcodes.ALOAD + 12] = "fload_3";
        insnName[Opcodes.ALOAD + 13] = "dload_0";
        insnName[Opcodes.ALOAD + 14] = "dload_1";
        insnName[Opcodes.ALOAD + 15] = "dload_2";
        insnName[Opcodes.ALOAD + 16] = "dload_3";
        insnName[Opcodes.ALOAD + 17] = "aload_0";
        insnName[Opcodes.ALOAD + 18] = "aload_1";
        insnName[Opcodes.ALOAD + 19] = "aload_2";
        insnName[Opcodes.ALOAD + 20] = "aload_3";
        insnName[Opcodes.IALOAD] = "iaload";
        insnName[Opcodes.LALOAD] = "laload";
        insnName[Opcodes.FALOAD] = "faload";
        insnName[Opcodes.DALOAD] = "daload";
        insnName[Opcodes.AALOAD] = "aaload";
        insnName[Opcodes.BALOAD] = "baload";
        insnName[Opcodes.CALOAD] = "caload";
        insnName[Opcodes.SALOAD] = "saload";
        insnName[Opcodes.ISTORE] = "istore";
        insnName[Opcodes.LSTORE] = "lstore";
        insnName[Opcodes.FSTORE] = "fstore";
        insnName[Opcodes.DSTORE] = "dstore";
        insnName[Opcodes.ASTORE] = "astore";
        insnName[Opcodes.ASTORE + 1] = "istore_0";
        insnName[Opcodes.ASTORE + 2] = "istore_1";
        insnName[Opcodes.ASTORE + 3] = "istore_2";
        insnName[Opcodes.ASTORE + 4] = "istore_3";
        insnName[Opcodes.ASTORE + 5] = "lstore_0";
        insnName[Opcodes.ASTORE + 6] = "lstore_1";
        insnName[Opcodes.ASTORE + 7] = "lstore_2";
        insnName[Opcodes.ASTORE + 8] = "lstore_3";
        insnName[Opcodes.ASTORE + 9] = "fstore_0";
        insnName[Opcodes.ASTORE + 10] = "fstore_1";
        insnName[Opcodes.ASTORE + 11] = "fstore_2";
        insnName[Opcodes.ASTORE + 12] = "fstore_3";
        insnName[Opcodes.ASTORE + 13] = "dstore_0";
        insnName[Opcodes.ASTORE + 14] = "dstore_1";
        insnName[Opcodes.ASTORE + 15] = "dstore_2";
        insnName[Opcodes.ASTORE + 16] = "dstore_3";
        insnName[Opcodes.ASTORE + 17] = "astore_0";
        insnName[Opcodes.ASTORE + 18] = "astore_1";
        insnName[Opcodes.ASTORE + 19] = "astore_2";
        insnName[Opcodes.ASTORE + 20] = "astore_3";
        insnName[Opcodes.IASTORE] = "iastore";
        insnName[Opcodes.LASTORE] = "lastore";
        insnName[Opcodes.FASTORE] = "fastore";
        insnName[Opcodes.DASTORE] = "dastore";
        insnName[Opcodes.AASTORE] = "aastore";
        insnName[Opcodes.BASTORE] = "bastore";
        insnName[Opcodes.CASTORE] = "castore";
        insnName[Opcodes.SASTORE] = "sastore";
        insnName[Opcodes.POP] = "pop";
        insnName[Opcodes.POP2] = "pop2";
        insnName[Opcodes.DUP] = "dup";
        insnName[Opcodes.DUP_X1] = "dup_x1";
        insnName[Opcodes.DUP_X2] = "dup_x2";
        insnName[Opcodes.DUP2] = "dup2";
        insnName[Opcodes.DUP2_X1] = "dup2_x1";
        insnName[Opcodes.DUP2_X2] = "dup2_X2";
        insnName[Opcodes.SWAP] = "swap";
        insnName[Opcodes.IADD] = "iadd";
        insnName[Opcodes.LADD] = "ladd";
        insnName[Opcodes.FADD] = "fadd";
        insnName[Opcodes.DADD] = "dadd";
        insnName[Opcodes.ISUB] = "isub";
        insnName[Opcodes.LSUB] = "lsub";
        insnName[Opcodes.FSUB] = "fsub";
        insnName[Opcodes.DSUB] = "dsub";
        insnName[Opcodes.IMUL] = "imul";
        insnName[Opcodes.LMUL] = "lmul";
        insnName[Opcodes.FMUL] = "fmul";
        insnName[Opcodes.DMUL] = "dmul";
        insnName[Opcodes.IDIV] = "idiv";
        insnName[Opcodes.LDIV] = "ldiv";
        insnName[Opcodes.FDIV] = "fdiv";
        insnName[Opcodes.DDIV] = "ddiv";
        insnName[Opcodes.IREM] = "irem";
        insnName[Opcodes.LREM] = "lrem";
        insnName[Opcodes.FREM] = "frem";
        insnName[Opcodes.DREM] = "drem";
        insnName[Opcodes.INEG] = "ineg";
        insnName[Opcodes.LNEG] = "lneg";
        insnName[Opcodes.FNEG] = "fneg";
        insnName[Opcodes.DNEG] = "dneg";
        insnName[Opcodes.ISHL] = "ishl";
        insnName[Opcodes.LSHL] = "lshl";
        insnName[Opcodes.ISHR] = "ishr";
        insnName[Opcodes.LSHR] = "lshr";
        insnName[Opcodes.IUSHR] = "iushr";
        insnName[Opcodes.LUSHR] = "lushr";
        insnName[Opcodes.IAND] = "iand";
        insnName[Opcodes.LAND] = "land";
        insnName[Opcodes.IOR] = "ior";
        insnName[Opcodes.LOR] = "lor";
        insnName[Opcodes.IXOR] = "ixor";
        insnName[Opcodes.LXOR] = "lxor";
        insnName[Opcodes.IINC] = "iinc";
        insnName[Opcodes.I2L] = "i2l";
        insnName[Opcodes.I2F] = "i2f";
        insnName[Opcodes.I2D] = "i2d";
        insnName[Opcodes.L2I] = "l2i";
        insnName[Opcodes.L2F] = "l2f";
        insnName[Opcodes.L2D] = "l2d";
        insnName[Opcodes.F2I] = "f2i";
        insnName[Opcodes.F2L] = "f2l";
        insnName[Opcodes.F2D] = "f2d";
        insnName[Opcodes.D2I] = "d2i";
        insnName[Opcodes.D2L] = "d2l";
        insnName[Opcodes.D2F] = "d2f";
        insnName[Opcodes.I2B] = "i2b";
        insnName[Opcodes.I2C] = "i2c";
        insnName[Opcodes.I2S] = "i2s";
        insnName[Opcodes.LCMP] = "lcmp";
        insnName[Opcodes.FCMPL] = "fcmpl";
        insnName[Opcodes.FCMPG] = "fcmpg";
        insnName[Opcodes.DCMPL] = "dcmpl";
        insnName[Opcodes.DCMPG] = "dcmpg";
        insnName[Opcodes.IFEQ] = "ifeq";
        insnName[Opcodes.IFNE] = "ifne";
        insnName[Opcodes.IFLT] = "iflt";
        insnName[Opcodes.IFGE] = "ifge";
        insnName[Opcodes.IFGT] = "ifgt";
        insnName[Opcodes.IFLE] = "ifle";
        insnName[Opcodes.IF_ICMPEQ] = "if_icmpeq";
        insnName[Opcodes.IF_ICMPNE] = "if_icmpne";
        insnName[Opcodes.IF_ICMPLT] = "if_icmplt";
        insnName[Opcodes.IF_ICMPGE] = "if_icmpge";
        insnName[Opcodes.IF_ICMPGT] = "if_icmpgt";
        insnName[Opcodes.IF_ICMPLE] = "if_icmple";
        insnName[Opcodes.IF_ACMPEQ] = "if_acmpeq";
        insnName[Opcodes.IF_ACMPNE] = "if_acmpne";
        insnName[Opcodes.GOTO] = "goto";
        insnName[Opcodes.JSR] = "jsr";
        insnName[Opcodes.RET] = "ret";
        insnName[Opcodes.TABLESWITCH] = "tableswitch";
        insnName[Opcodes.LOOKUPSWITCH] = "lookupswitch";
        insnName[Opcodes.IRETURN] = "ireturn";
        insnName[Opcodes.LRETURN] = "lreturn";
        insnName[Opcodes.FRETURN] = "freturn";
        insnName[Opcodes.DRETURN] = "dreturn";
        insnName[Opcodes.ARETURN] = "areturn";
        insnName[Opcodes.RETURN] = "return";
        insnName[Opcodes.GETSTATIC] = "getstatic";
        insnName[Opcodes.PUTSTATIC] = "putstatic";
        insnName[Opcodes.GETFIELD] = "getfield";
        insnName[Opcodes.PUTFIELD] = "putfield";
        insnName[Opcodes.INVOKEVIRTUAL] = "invokevirtual";
        insnName[Opcodes.INVOKESPECIAL] = "invokespecial";
        insnName[Opcodes.INVOKESTATIC] = "invokestatic";
        insnName[Opcodes.INVOKEINTERFACE] = "invokeinterface";
        insnName[Opcodes.INVOKEINTERFACE  + 1] = "unused";
        // insnName[Opcodes.UNUSED] = "unused";
        insnName[Opcodes.NEW] = "new";
        insnName[Opcodes.NEWARRAY] = "newarray";
        insnName[Opcodes.ANEWARRAY] = "anewarray";
        insnName[Opcodes.ARRAYLENGTH] = "arraylength";
        insnName[Opcodes.ATHROW] = "athrow";
        insnName[Opcodes.CHECKCAST] = "checkcast";
        insnName[Opcodes.INSTANCEOF] = "instanceof";
        insnName[Opcodes.MONITORENTER] = "monitorenter";
        insnName[Opcodes.MONITOREXIT] = "monitorexit";
        insnName[Opcodes.MONITOREXIT + 1] = "wide";
        // insnName[Opcodes.WIDE] = "wide";
        insnName[Opcodes.MULTIANEWARRAY] = "multianewarray";
        insnName[Opcodes.IFNULL] = "ifnull";
        insnName[Opcodes.IFNONNULL] = "ifnonnull";
        insnName[Opcodes.IFNONNULL + 1] = "goto_w";
        insnName[Opcodes.IFNONNULL + 2] = "jsr_w";
    }

    static public int insnType(int opcode)
    {
        return insnType[opcode];
    }

    static public String insnName(int opcode)
    {
        return insnName[opcode];
    }
}
