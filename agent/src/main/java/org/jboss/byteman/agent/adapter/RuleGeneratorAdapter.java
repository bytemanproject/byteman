/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2005 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.byteman.agent.TransformContext;
import org.jboss.byteman.rule.Rule;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.commons.TableSwitchGenerator;

/**
 * A modified version of the asm 3.0 GeneratorAdapter class which dispatches calls to methods of
 * MethodVisitor to this rather than to the encapsulated MethodVisitor instance in field mv.
 * Doing so gives the current instance a chance to observe all visit operations. Without it
 * the current instance only sees visit operations invoked directly by previous visitors in
 * the chain. This is necessary in order for the RuleTriggerAdapter to build a complete CFG
 * for the method being visited.
 *
 * As a consequence of the above change this class cannot inherit the methods from LocalVariableSorter
 * which allow introduction of new local variables. That's not actually much of a loss since the
 * functionality provided by that class is of limited utility -- it only allows local variables to
 * be introduced via a prior pipeline stage. Instead this class provides methods to track the number
 * of locals employed so far and supports temporary introduction and removal of locals inside injected
 * trigger or handler code. See methods {@link #newLocal(org.objectweb.asm.Type)}, {@link #popLocal(int)},
 * {@link #loadLocal(int)} and {@link #storeLocal(int)}.
 *
 * Another reason to transplant code to this class is because it inherits functionality from
 * RuleMethodAdapter which is used by RuleCheckAdapter and RuleTriggerAdapter to identify
 * and classify local variables but provides functionality to modify bytecode which is only
 * needed by RuleTriggerAdapter. So, the original class would have needed reparenting anyway.
 *
 * @author Andrew Dinn
 * @author Juozas Baliuka
 * @author Chris Nokleberg
 * @author Eric Bruneton
 */
public class RuleGeneratorAdapter extends RuleMethodAdapter {

    private final static Type BYTE_TYPE = Type.getObjectType("java/lang/Byte");

    private final static Type BOOLEAN_TYPE = Type.getObjectType("java/lang/Boolean");

    private final static Type SHORT_TYPE = Type.getObjectType("java/lang/Short");

    private final static Type CHARACTER_TYPE = Type.getObjectType("java/lang/Character");

    private final static Type INTEGER_TYPE = Type.getObjectType("java/lang/Integer");

    private final static Type FLOAT_TYPE = Type.getObjectType("java/lang/Float");

    private final static Type LONG_TYPE = Type.getObjectType("java/lang/Long");

    private final static Type DOUBLE_TYPE = Type.getObjectType("java/lang/Double");

    private final static Type NUMBER_TYPE = Type.getObjectType("java/lang/Number");

    private final static Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");

    private final static Method BOOLEAN_VALUE = Method.getMethod("boolean booleanValue()");

    private final static Method CHAR_VALUE = Method.getMethod("char charValue()");

    private final static Method INT_VALUE = Method.getMethod("int intValue()");

    private final static Method FLOAT_VALUE = Method.getMethod("float floatValue()");

    private final static Method LONG_VALUE = Method.getMethod("long longValue()");

    private final static Method DOUBLE_VALUE = Method.getMethod("double doubleValue()");

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int ADD = Opcodes.IADD;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int SUB = Opcodes.ISUB;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int MUL = Opcodes.IMUL;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int DIV = Opcodes.IDIV;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int REM = Opcodes.IREM;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int NEG = Opcodes.INEG;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int SHL = Opcodes.ISHL;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int SHR = Opcodes.ISHR;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int USHR = Opcodes.IUSHR;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int AND = Opcodes.IAND;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int OR = Opcodes.IOR;

    /**
     * Constant for the {@link #math math} method.
     */
    public final static int XOR = Opcodes.IXOR;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public final static int EQ = Opcodes.IFEQ;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public final static int NE = Opcodes.IFNE;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public final static int LT = Opcodes.IFLT;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public final static int GE = Opcodes.IFGE;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public final static int GT = Opcodes.IFGT;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public final static int LE = Opcodes.IFLE;

    /**
     * Argument types of the method visited by this adapter.
     */
    private final Type[] argumentTypes;

    /**
     * Return type of the method visited by this adapter.
     */
    private final Type returnType;

    /**
     * Types of the local variables of the method visited by this adapter.
     */
    private final List localTypes;

    /**
     * used to track active local variable slots
     */
    private int nextLocal;

    /**
     * used to track maximum number of local variable slots
     */
    private int localHighWater;

    /**
     * Creates a new {@link RuleGeneratorAdapter}.
     *
     * @param mv the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link org.objectweb.asm.Opcodes}).
     * @param name the method's name.
     * @param desc the method's descriptor (see {@link org.objectweb.asm.Type Type}).
     */
    public RuleGeneratorAdapter(
        final MethodVisitor mv,
        final TransformContext transformContext,
        final int access,
        final String name,
        final String desc,
        final Rule rule)
    {
        super(mv, transformContext, access, name, desc, rule);
        this.argumentTypes = Type.getArgumentTypes(desc);
        this.returnType = Type.getReturnType(desc);
        localTypes = new ArrayList();
        initLocalTypes();
    }

    /**
     * initialise the local slot types array with the types of the method target and parameters.
     * this is needed because we are only sent an initial frame identifying the local slots
     * which belong to the method if a stackmap table  has been included in the bytecode and this
     * is nto always the case.
     */
    private void initLocalTypes()
    {
        // owner of this method is an object
        // localTypes.add(Type.getType(Object.class));
        String name = getTriggerClassName().replace('.', '/');
        if ((access & Opcodes.ACC_STATIC) == 0) {
            // an instance method so slot 0 will contain the target object
            localTypes.add(Type.getType("L" + name + ";"));
        }
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];
            int size = argumentType.getSize();
            localTypes.add(argumentType);
            if (size > 1) {
                localTypes.add(null);
            }
        }
        nextLocal = localHighWater = localTypes.size();
    }

    /**
     * Returns the internal names of the given types.
     *
     * @param types a set of types.
     * @return the internal names of the given types.
     */
    private static String[] getInternalNames(final Type[] types) {
        if (types == null) {
            return null;
        }
        String[] names = new String[types.length];
        for (int i = 0; i < names.length; ++i) {
            names[i] = types[i].getInternalName();
        }
        return names;
    }

    // ------------------------------------------------------------------------
    // Instructions to push constants on the stack
    // ------------------------------------------------------------------------

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public void push(final boolean value) {
        push(value ? 1 : 0);
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public void push(final int value) {
        if (value >= -1 && value <= 5) {
            visitInsn(Opcodes.ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            visitIntInsn(Opcodes.BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            visitIntInsn(Opcodes.SIPUSH, value);
        } else {
            visitLdcInsn(new Integer(value));
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public void push(final long value) {
        if (value == 0L || value == 1L) {
            visitInsn(Opcodes.LCONST_0 + (int) value);
        } else {
            visitLdcInsn(new Long(value));
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public void push(final float value) {
        int bits = Float.floatToIntBits(value);
        if (bits == 0L || bits == 0x3f800000 || bits == 0x40000000) { // 0..2
            visitInsn(Opcodes.FCONST_0 + (int) value);
        } else {
            visitLdcInsn(new Float(value));
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public void push(final double value) {
        long bits = Double.doubleToLongBits(value);
        if (bits == 0L || bits == 0x3ff0000000000000L) { // +0.0d and 1.0d
            visitInsn(Opcodes.DCONST_0 + (int) value);
        } else {
            visitLdcInsn(new Double(value));
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack. May be <tt>null</tt>.
     */
    public void push(final String value) {
        if (value == null) {
            visitInsn(Opcodes.ACONST_NULL);
        } else {
            visitLdcInsn(value);
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public void push(final Type value) {
        if (value == null) {
            visitInsn(Opcodes.ACONST_NULL);
        } else {
            visitLdcInsn(value);
        }
    }

    // ------------------------------------------------------------------------
    // Instructions to load and store method arguments
    // ------------------------------------------------------------------------

    /**
     * Returns the index of the given method argument in the frame's local
     * variables array.
     *
     * @param arg the index of a method argument.
     * @return the index of the given method argument in the frame's local
     *         variables array.
     */
    private int getArgIndex(final int arg) {
        int index = (access & Opcodes.ACC_STATIC) == 0 ? 1 : 0;
        for (int i = 0; i < arg; i++) {
            index += argumentTypes[i].getSize();
        }
        return index;
    }

    /**
     * Generates the instruction to push a local variable on the stack.
     *
     * @param type the type of the local variable to be loaded.
     * @param index an index in the frame's local variables array.
     */
    private void loadInsn(final Type type, final int index) {
        visitVarInsn(type.getOpcode(Opcodes.ILOAD), index);
    }

    /**
     * Generates the instruction to store the top stack value in a local
     * variable.
     *
     * @param type the type of the local variable to be stored.
     * @param index an index in the frame's local variables array.
     */
    private void storeInsn(final Type type, final int index) {
        visitVarInsn(type.getOpcode(Opcodes.ISTORE), index);
    }

    /**
     * Generates the instruction to load 'this' on the stack.
     */
    public void loadThis() {
        if ((access & Opcodes.ACC_STATIC) != 0) {
            throw new IllegalStateException("no 'this' pointer within static method");
        }
        visitVarInsn(Opcodes.ALOAD, 0);
    }

    /**
     * Generates the instruction to load the given method argument on the stack.
     *
     * @param arg the index of a method argument.
     */
    public void loadArg(final int arg) {
        loadInsn(argumentTypes[arg], getArgIndex(arg));
    }

    /**
     * Generates the instructions to load the given method arguments on the
     * stack.
     *
     * @param arg the index of the first method argument to be loaded.
     * @param count the number of method arguments to be loaded.
     */
    public void loadArgs(final int arg, final int count) {
        int index = getArgIndex(arg);
        for (int i = 0; i < count; ++i) {
            Type t = argumentTypes[arg + i];
            loadInsn(t, index);
            index += t.getSize();
        }
    }

    /**
     * Generates the instructions to load all the method arguments on the stack.
     */
    public void loadArgs() {
        loadArgs(0, argumentTypes.length);
    }

    /**
     * Generates the instructions to load all the method arguments on the stack,
     * as a single object array.
     */
    public void loadArgArray() {
        push(argumentTypes.length);
        newArray(OBJECT_TYPE);
        for (int i = 0; i < argumentTypes.length; i++) {
            dup();
            push(i);
            loadArg(i);
            box(argumentTypes[i]);
            arrayStore(OBJECT_TYPE);
        }
    }

    /**
     * Generates the instruction to store the top stack value in the given
     * method argument.
     *
     * @param arg the index of a method argument.
     */
    public void storeArg(final int arg) {
        storeInsn(argumentTypes[arg], getArgIndex(arg));
    }

    /**
     * Generates the instruction to load an element from an array.
     *
     * @param type the type of the array element to be loaded.
     */
    public void arrayLoad(final Type type) {
        visitInsn(type.getOpcode(Opcodes.IALOAD));
    }

    /**
     * Generates the instruction to store an element in an array.
     *
     * @param type the type of the array element to be stored.
     */
    public void arrayStore(final Type type) {
        visitInsn(type.getOpcode(Opcodes.IASTORE));
    }

    // ------------------------------------------------------------------------
    // Instructions to manage the stack
    // ------------------------------------------------------------------------

    /**
     * Generates a POP instruction.
     */
    public void pop() {
        visitInsn(Opcodes.POP);
    }

    /**
     * Generates a POP2 instruction.
     */
    public void pop2() {
        visitInsn(Opcodes.POP2);
    }

    /**
     * Generates a DUP instruction.
     */
    public void dup() {
        visitInsn(Opcodes.DUP);
    }

    /**
     * Generates a DUP2 instruction.
     */
    public void dup2() {
        visitInsn(Opcodes.DUP2);
    }

    /**
     * Generates a DUP_X1 instruction.
     */
    public void dupX1() {
        visitInsn(Opcodes.DUP_X1);
    }

    /**
     * Generates a DUP_X2 instruction.
     */
    public void dupX2() {
        visitInsn(Opcodes.DUP_X2);
    }

    /**
     * Generates a DUP2_X1 instruction.
     */
    public void dup2X1() {
        visitInsn(Opcodes.DUP2_X1);
    }

    /**
     * Generates a DUP2_X2 instruction.
     */
    public void dup2X2() {
        visitInsn(Opcodes.DUP2_X2);
    }

    /**
     * Generates a SWAP instruction.
     */
    public void swap() {
        visitInsn(Opcodes.SWAP);
    }

    /**
     * Generates the instructions to swap the top two stack values.
     *
     * @param prev type of the top - 1 stack value.
     * @param type type of the top stack value.
     */
    public void swap(final Type prev, final Type type) {
        if (type.getSize() == 1) {
            if (prev.getSize() == 1) {
                swap(); // same as dupX1(), pop();
            } else {
                dupX2();
                pop();
            }
        } else {
            if (prev.getSize() == 1) {
                dup2X1();
                pop2();
            } else {
                dup2X2();
                pop2();
            }
        }
    }

    // ------------------------------------------------------------------------
    // Instructions to do mathematical and logical operations
    // ------------------------------------------------------------------------

    /**
     * Generates the instruction to do the specified mathematical or logical
     * operation.
     *
     * @param op a mathematical or logical operation. Must be one of ADD, SUB,
     *        MUL, DIV, REM, NEG, SHL, SHR, USHR, AND, OR, XOR.
     * @param type the type of the operand(s) for this operation.
     */
    public void math(final int op, final Type type) {
        visitInsn(type.getOpcode(op));
    }

    /**
     * Generates the instructions to compute the bitwise negation of the top
     * stack value.
     */
    public void not() {
        visitInsn(Opcodes.ICONST_1);
        visitInsn(Opcodes.IXOR);
    }

    /**
     * Generates the instruction to increment the given local variable.
     *
     * @param local the local variable to be incremented.
     * @param amount the amount by which the local variable must be incremented.
     */
    public void iinc(final int local, final int amount) {
        visitIincInsn(local, amount);
    }

    /**
     * Generates the instructions to cast a numerical value from one type to
     * another.
     *
     * @param from the type of the top stack value
     * @param to the type into which this value must be cast.
     */
    public void cast(final Type from, final Type to) {
        if (from != to) {
            if (from == Type.DOUBLE_TYPE) {
                if (to == Type.FLOAT_TYPE) {
                    visitInsn(Opcodes.D2F);
                } else if (to == Type.LONG_TYPE) {
                    visitInsn(Opcodes.D2L);
                } else {
                    visitInsn(Opcodes.D2I);
                    cast(Type.INT_TYPE, to);
                }
            } else if (from == Type.FLOAT_TYPE) {
                if (to == Type.DOUBLE_TYPE) {
                    visitInsn(Opcodes.F2D);
                } else if (to == Type.LONG_TYPE) {
                    visitInsn(Opcodes.F2L);
                } else {
                    visitInsn(Opcodes.F2I);
                    cast(Type.INT_TYPE, to);
                }
            } else if (from == Type.LONG_TYPE) {
                if (to == Type.DOUBLE_TYPE) {
                    visitInsn(Opcodes.L2D);
                } else if (to == Type.FLOAT_TYPE) {
                    visitInsn(Opcodes.L2F);
                } else {
                    visitInsn(Opcodes.L2I);
                    cast(Type.INT_TYPE, to);
                }
            } else {
                if (to == Type.BYTE_TYPE) {
                    visitInsn(Opcodes.I2B);
                } else if (to == Type.CHAR_TYPE) {
                    visitInsn(Opcodes.I2C);
                } else if (to == Type.DOUBLE_TYPE) {
                    visitInsn(Opcodes.I2D);
                } else if (to == Type.FLOAT_TYPE) {
                    visitInsn(Opcodes.I2F);
                } else if (to == Type.LONG_TYPE) {
                    visitInsn(Opcodes.I2L);
                } else if (to == Type.SHORT_TYPE) {
                    visitInsn(Opcodes.I2S);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Instructions to do boxing and unboxing operations
    // ------------------------------------------------------------------------

    /**
     * Generates the instructions to box the top stack value. This value is
     * replaced by its boxed equivalent on top of the stack.
     *
     * @param type the type of the top stack value.
     */
    public void box(final Type type) {
        if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
            return;
        }
        if (type == Type.VOID_TYPE) {
            push((String) null);
        } else {
            Type boxed = type;
            switch (type.getSort()) {
                case Type.BYTE:
                    boxed = BYTE_TYPE;
                    break;
                case Type.BOOLEAN:
                    boxed = BOOLEAN_TYPE;
                    break;
                case Type.SHORT:
                    boxed = SHORT_TYPE;
                    break;
                case Type.CHAR:
                    boxed = CHARACTER_TYPE;
                    break;
                case Type.INT:
                    boxed = INTEGER_TYPE;
                    break;
                case Type.FLOAT:
                    boxed = FLOAT_TYPE;
                    break;
                case Type.LONG:
                    boxed = LONG_TYPE;
                    break;
                case Type.DOUBLE:
                    boxed = DOUBLE_TYPE;
                    break;
            }
            newInstance(boxed);
            if (type.getSize() == 2) {
                // Pp -> Ppo -> oPpo -> ooPpo -> ooPp -> o
                dupX2();
                dupX2();
                pop();
            } else {
                // p -> po -> opo -> oop -> o
                dupX1();
                swap();
            }
            invokeConstructor(boxed, new Method("<init>",
                    Type.VOID_TYPE,
                    new Type[] { type }));
        }
    }

    /**
     * Generates the instructions to unbox the top stack value. This value is
     * replaced by its unboxed equivalent on top of the stack.
     *
     * @param type the type of the top stack value.
     */
    public void unbox(final Type type) {
        Type t = NUMBER_TYPE;
        Method sig = null;
        switch (type.getSort()) {
            case Type.VOID:
                return;
            case Type.CHAR:
                t = CHARACTER_TYPE;
                sig = CHAR_VALUE;
                break;
            case Type.BOOLEAN:
                t = BOOLEAN_TYPE;
                sig = BOOLEAN_VALUE;
                break;
            case Type.DOUBLE:
                sig = DOUBLE_VALUE;
                break;
            case Type.FLOAT:
                sig = FLOAT_VALUE;
                break;
            case Type.LONG:
                sig = LONG_VALUE;
                break;
            case Type.INT:
            case Type.SHORT:
            case Type.BYTE:
                sig = INT_VALUE;
        }
        if (sig == null) {
            checkCast(type);
        } else {
            checkCast(t);
            invokeVirtual(t, sig);
        }
    }

    // ------------------------------------------------------------------------
    // Instructions to jump to other instructions
    // ------------------------------------------------------------------------

    /**
     * Creates a new {@link org.objectweb.asm.Label}.
     *
     * @return a new {@link org.objectweb.asm.Label}.
     */
    public Label newLabel() {
        return new Label();
    }

    /**
     * Marks the current code position with the given label.
     *
     * @param label a label.
     */
    public void mark(final Label label) {
        visitLabel(label);
    }

    /**
     * Marks the current code position with a new label.
     *
     * @return the label that was created to mark the current code position.
     */
    public Label mark() {
        Label label = new Label();
        visitLabel(label);
        return label;
    }

    /**
     * Generates the instructions to jump to a label based on the comparison of
     * the top two stack values.
     *
     * @param type the type of the top two stack values.
     * @param mode how these values must be compared. One of EQ, NE, LT, GE, GT,
     *        LE.
     * @param label where to jump if the comparison result is <tt>true</tt>.
     */
    public void ifCmp(final Type type, final int mode, final Label label) {
        int intOp = -1;
        switch (type.getSort()) {
            case Type.LONG:
                visitInsn(Opcodes.LCMP);
                break;
            case Type.DOUBLE:
                visitInsn(Opcodes.DCMPG);
                break;
            case Type.FLOAT:
                visitInsn(Opcodes.FCMPG);
                break;
            case Type.ARRAY:
            case Type.OBJECT:
                switch (mode) {
                    case EQ:
                        visitJumpInsn(Opcodes.IF_ACMPEQ, label);
                        return;
                    case NE:
                        visitJumpInsn(Opcodes.IF_ACMPNE, label);
                        return;
                }
                throw new IllegalArgumentException("Bad comparison for type "
                        + type);
            default:
                switch (mode) {
                    case EQ:
                        intOp = Opcodes.IF_ICMPEQ;
                        break;
                    case NE:
                        intOp = Opcodes.IF_ICMPNE;
                        break;
                    case GE:
                        intOp = Opcodes.IF_ICMPGE;
                        break;
                    case LT:
                        intOp = Opcodes.IF_ICMPLT;
                        break;
                    case LE:
                        intOp = Opcodes.IF_ICMPLE;
                        break;
                    case GT:
                        intOp = Opcodes.IF_ICMPGT;
                        break;
                }
                visitJumpInsn(intOp, label);
                return;
        }
        int jumpMode = mode;
        switch (mode) {
            case GE:
                jumpMode = LT;
                break;
            case LE:
                jumpMode = GT;
                break;
        }
        visitJumpInsn(jumpMode, label);
    }

    /**
     * Generates the instructions to jump to a label based on the comparison of
     * the top two integer stack values.
     *
     * @param mode how these values must be compared. One of EQ, NE, LT, GE, GT,
     *        LE.
     * @param label where to jump if the comparison result is <tt>true</tt>.
     */
    public void ifICmp(final int mode, final Label label) {
        ifCmp(Type.INT_TYPE, mode, label);
    }

    /**
     * Generates the instructions to jump to a label based on the comparison of
     * the top integer stack value with zero.
     *
     * @param mode how these values must be compared. One of EQ, NE, LT, GE, GT,
     *        LE.
     * @param label where to jump if the comparison result is <tt>true</tt>.
     */
    public void ifZCmp(final int mode, final Label label) {
        visitJumpInsn(mode, label);
    }

    /**
     * Generates the instruction to jump to the given label if the top stack
     * value is null.
     *
     * @param label where to jump if the condition is <tt>true</tt>.
     */
    public void ifNull(final Label label) {
        visitJumpInsn(Opcodes.IFNULL, label);
    }

    /**
     * Generates the instruction to jump to the given label if the top stack
     * value is not null.
     *
     * @param label where to jump if the condition is <tt>true</tt>.
     */
    public void ifNonNull(final Label label) {
        visitJumpInsn(Opcodes.IFNONNULL, label);
    }

    /**
     * Generates the instruction to jump to the given label.
     *
     * @param label where to jump if the condition is <tt>true</tt>.
     */
    public void goTo(final Label label) {
        visitJumpInsn(Opcodes.GOTO, label);
    }

    /**
     * Generates a RET instruction.
     *
     * @param local a local variable identifier, as returned by
     *        {@link org.objectweb.asm.commons.LocalVariablesSorter#newLocal(org.objectweb.asm.Type) newLocal()}.
     */
    public void ret(final int local) {
        visitVarInsn(Opcodes.RET, local);
    }

    /**
     * Generates the instructions for a switch statement.
     *
     * @param keys the switch case keys.
     * @param generator a generator to generate the code for the switch cases.
     */
    public void tableSwitch(
        final int[] keys,
        final TableSwitchGenerator generator)
    {
        float density;
        if (keys.length == 0) {
            density = 0;
        } else {
            density = (float) keys.length
                    / (keys[keys.length - 1] - keys[0] + 1);
        }
        tableSwitch(keys, generator, density >= 0.5f);
    }

    /**
     * Generates the instructions for a switch statement.
     *
     * @param keys the switch case keys.
     * @param generator a generator to generate the code for the switch cases.
     * @param useTable <tt>true</tt> to use a TABLESWITCH instruction, or
     *        <tt>false</tt> to use a LOOKUPSWITCH instruction.
     */
    public void tableSwitch(
        final int[] keys,
        final TableSwitchGenerator generator,
        final boolean useTable)
    {
        for (int i = 1; i < keys.length; ++i) {
            if (keys[i] < keys[i - 1]) {
                throw new IllegalArgumentException("keys must be sorted ascending");
            }
        }
        Label def = newLabel();
        Label end = newLabel();
        if (keys.length > 0) {
            int len = keys.length;
            int min = keys[0];
            int max = keys[len - 1];
            int range = max - min + 1;
            if (useTable) {
                Label[] labels = new Label[range];
                Arrays.fill(labels, def);
                for (int i = 0; i < len; ++i) {
                    labels[keys[i] - min] = newLabel();
                }
                visitTableSwitchInsn(min, max, def, labels);
                for (int i = 0; i < range; ++i) {
                    Label label = labels[i];
                    if (label != def) {
                        mark(label);
                        generator.generateCase(i + min, end);
                    }
                }
            } else {
                Label[] labels = new Label[len];
                for (int i = 0; i < len; ++i) {
                    labels[i] = newLabel();
                }
                visitLookupSwitchInsn(def, keys, labels);
                for (int i = 0; i < len; ++i) {
                    mark(labels[i]);
                    generator.generateCase(keys[i], end);
                }
            }
        }
        mark(def);
        generator.generateDefault();
        mark(end);
    }

    /**
     * Generates the instruction to return the top stack value to the caller.
     */
    public void returnValue() {
        visitInsn(returnType.getOpcode(Opcodes.IRETURN));
    }

    // ------------------------------------------------------------------------
    // Instructions to load and store fields
    // ------------------------------------------------------------------------

    /**
     * Generates a get field or set field instruction.
     *
     * @param opcode the instruction's opcode.
     * @param ownerType the class in which the field is defined.
     * @param name the name of the field.
     * @param fieldType the type of the field.
     */
    private void fieldInsn(
        final int opcode,
        final Type ownerType,
        final String name,
        final Type fieldType)
    {
        visitFieldInsn(opcode,
                ownerType.getInternalName(),
                name,
                fieldType.getDescriptor());
    }

    /**
     * Generates the instruction to push the value of a static field on the
     * stack.
     *
     * @param owner the class in which the field is defined.
     * @param name the name of the field.
     * @param type the type of the field.
     */
    public void getStatic(final Type owner, final String name, final Type type)
    {
        fieldInsn(Opcodes.GETSTATIC, owner, name, type);
    }

    /**
     * Generates the instruction to store the top stack value in a static field.
     *
     * @param owner the class in which the field is defined.
     * @param name the name of the field.
     * @param type the type of the field.
     */
    public void putStatic(final Type owner, final String name, final Type type)
    {
        fieldInsn(Opcodes.PUTSTATIC, owner, name, type);
    }

    /**
     * Generates the instruction to push the value of a non static field on the
     * stack.
     *
     * @param owner the class in which the field is defined.
     * @param name the name of the field.
     * @param type the type of the field.
     */
    public void getField(final Type owner, final String name, final Type type) {
        fieldInsn(Opcodes.GETFIELD, owner, name, type);
    }

    /**
     * Generates the instruction to store the top stack value in a non static
     * field.
     *
     * @param owner the class in which the field is defined.
     * @param name the name of the field.
     * @param type the type of the field.
     */
    public void putField(final Type owner, final String name, final Type type) {
        fieldInsn(Opcodes.PUTFIELD, owner, name, type);
    }

    // ------------------------------------------------------------------------
    // Instructions to invoke methods
    // ------------------------------------------------------------------------

    /**
     * Generates an invoke method instruction.
     *
     * @param opcode the instruction's opcode.
     * @param type the class in which the method is defined.
     * @param method the method to be invoked.
     */
    private void invokeInsn(
        final int opcode,
        final Type type,
        final Method method)
    {
        String owner = type.getSort() == Type.ARRAY
                ? type.getDescriptor()
                : type.getInternalName();
        visitMethodInsn(opcode,
                owner,
                method.getName(),
                method.getDescriptor());
    }

    /**
     * Generates the instruction to invoke a normal method.
     *
     * @param owner the class in which the method is defined.
     * @param method the method to be invoked.
     */
    public void invokeVirtual(final Type owner, final Method method) {
        invokeInsn(Opcodes.INVOKEVIRTUAL, owner, method);
    }

    /**
     * Generates the instruction to invoke a constructor.
     *
     * @param type the class in which the constructor is defined.
     * @param method the constructor to be invoked.
     */
    public void invokeConstructor(final Type type, final Method method) {
        invokeInsn(Opcodes.INVOKESPECIAL, type, method);
    }

    /**
     * Generates the instruction to invoke a static method.
     *
     * @param owner the class in which the method is defined.
     * @param method the method to be invoked.
     */
    public void invokeStatic(final Type owner, final Method method) {
        invokeInsn(Opcodes.INVOKESTATIC, owner, method);
    }

    /**
     * Generates the instruction to invoke an interface method.
     *
     * @param owner the class in which the method is defined.
     * @param method the method to be invoked.
     */
    public void invokeInterface(final Type owner, final Method method) {
        invokeInsn(Opcodes.INVOKEINTERFACE, owner, method);
    }

    // ------------------------------------------------------------------------
    // Instructions to create objects and arrays
    // ------------------------------------------------------------------------

    /**
     * Generates a type dependent instruction.
     *
     * @param opcode the instruction's opcode.
     * @param type the instruction's operand.
     */
    private void typeInsn(final int opcode, final Type type) {
        String desc;
        if (type.getSort() == Type.ARRAY) {
            desc = type.getDescriptor();
        } else {
            desc = type.getInternalName();
        }
        visitTypeInsn(opcode, desc);
    }

    /**
     * Generates the instruction to create a new object.
     *
     * @param type the class of the object to be created.
     */
    public void newInstance(final Type type) {
        typeInsn(Opcodes.NEW, type);
    }

    /**
     * Generates the instruction to create a new array.
     *
     * @param type the type of the array elements.
     */
    public void newArray(final Type type) {
        int typ;
        switch (type.getSort()) {
            case Type.BOOLEAN:
                typ = Opcodes.T_BOOLEAN;
                break;
            case Type.CHAR:
                typ = Opcodes.T_CHAR;
                break;
            case Type.BYTE:
                typ = Opcodes.T_BYTE;
                break;
            case Type.SHORT:
                typ = Opcodes.T_SHORT;
                break;
            case Type.INT:
                typ = Opcodes.T_INT;
                break;
            case Type.FLOAT:
                typ = Opcodes.T_FLOAT;
                break;
            case Type.LONG:
                typ = Opcodes.T_LONG;
                break;
            case Type.DOUBLE:
                typ = Opcodes.T_DOUBLE;
                break;
            default:
                typeInsn(Opcodes.ANEWARRAY, type);
                return;
        }
        visitIntInsn(Opcodes.NEWARRAY, typ);
    }

    // ------------------------------------------------------------------------
    // Miscelaneous instructions
    // ------------------------------------------------------------------------

    /**
     * Generates the instruction to compute the length of an array.
     */
    public void arrayLength() {
        visitInsn(Opcodes.ARRAYLENGTH);
    }

    /**
     * Generates the instruction to throw an exception.
     */
    public void throwException() {
        visitInsn(Opcodes.ATHROW);
    }

    /**
     * Generates the instructions to create and throw an exception. The
     * exception class must have a constructor with a single String argument.
     *
     * @param type the class of the exception to be thrown.
     * @param msg the detailed message of the exception.
     */
    public void throwException(final Type type, final String msg) {
        newInstance(type);
        dup();
        push(msg);
        invokeConstructor(type, Method.getMethod("void <init> (String)"));
        throwException();
    }

    /**
     * Generates the instruction to check that the top stack value is of the
     * given type.
     *
     * @param type a class or interface type.
     */
    public void checkCast(final Type type) {
        if (!type.equals(OBJECT_TYPE)) {
            typeInsn(Opcodes.CHECKCAST, type);
        }
    }

    /**
     * Generates the instruction to test if the top stack value is of the given
     * type.
     *
     * @param type a class or interface type.
     */
    public void instanceOf(final Type type) {
        typeInsn(Opcodes.INSTANCEOF, type);
    }

    /**
     * Generates the instruction to get the monitor of the top stack value.
     */
    public void monitorEnter() {
        visitInsn(Opcodes.MONITORENTER);
    }

    /**
     * Generates the instruction to release the monitor of the top stack value.
     */
    public void monitorExit() {
        visitInsn(Opcodes.MONITOREXIT);
    }

    // ------------------------------------------------------------------------
    // Non instructions
    // ------------------------------------------------------------------------

    /**
     * Marks the end of the visited method.
     */
    public void endMethod() {
        if ((access & Opcodes.ACC_ABSTRACT) == 0) {
            visitMaxs(0, 0);
        }
        visitEnd();
    }

    /**
     * Marks the start of an exception handler.
     *
     * @param start beginning of the exception handler's scope (inclusive).
     * @param end end of the exception handler's scope (exclusive).
     * @param exception internal name of the type of exceptions handled by the
     *        handler.
     */
    public void catchException(
        final Label start,
        final Label end,
        final Type exception)
    {
        visitTryCatchBlock(start, end, mark(), exception.getInternalName());
    }

    // local variable handling

    /**
     * override this so we can see track which local var slots are in use and avoid overwriting them
     * @param opcode
     * @param var
     */
    public void visitVarInsn(final int opcode, final int var)
    {
        if (var >= nextLocal || localTypes.get(var) == null) {
            int size = 1;
            Type type = null;
            switch(opcode) {
                case Opcodes.ISTORE:
                    type = Type.INT_TYPE;
                break;
                case Opcodes.LSTORE:
                    type = Type.LONG_TYPE;
                    size = 2;
                break;
                case Opcodes.FSTORE:
                    type = Type.FLOAT_TYPE;
                break;
                case Opcodes.DSTORE:
                    type = Type.DOUBLE_TYPE;
                    size = 2;
                break;
                case Opcodes.ASTORE:
                // we don't know exactly what type this is but at least we know it is an object
                // we need to correct for this when injecting trigger code to update the local
                // luckily we can use the binding type derived during the check phase to idenitfy
                // the actual local type
                type = Type.getType(Object.class);
                break;
            }
            if (var <  nextLocal) {
                // just fill in the missing type
                localTypes.set(var, type);
            } else {

                // we may not have seen some of the locals so leave a blank spot for them in the types array
                for (int i = nextLocal; i < var; i++) {
                    localTypes.add(null);
                }
                // now add entry for var
                
                localTypes.add(type);
                if (size > 1) {
                    localTypes.add(null);
                }
                nextLocal = var + size;

                if (nextLocal > localHighWater) {
                    localHighWater = nextLocal;
                }
            }
        }
        super.visitVarInsn(opcode, var);
    }

    /**
     * return a new local slot index for a local var not currently in use. this must be released
     * using popLocal before a new frame can be notified which means that the slot should only be
     * allocated inside a generated trigger section and should be released before the trigger
     * end of the trigger section by calling popLocal.
     * @param valueType the type of the value to be stored in the local slot
     * @return the index for the new slot
     */
    public int newLocal(Type valueType)
    {
        int localIndex = nextLocal++;
        localTypes.add(valueType);

        if (valueType.getSize() > 1) {
            nextLocal++;
            localTypes.add(null);
        }

        if (nextLocal > localHighWater) {
            localHighWater = nextLocal;
        }

        return localIndex;
    }

    /**
     * free a previously allocated local slot
     * @param local the slot to be released
     */
    public void popLocal(int local)
    {
        Type type = (Type)localTypes.get(local);
        int size = type.getSize();
        if (nextLocal != local + size) {
            throw new IndexOutOfBoundsException("popLocal was expecting " + (nextLocal - size ) + " but got " + local + " instead!");
        }
        
        if (size > 1) {
            nextLocal--;
            localTypes.remove(nextLocal);
        }

        nextLocal--;
        localTypes.remove(nextLocal);
    }

    /**
     * load a value onto the stack from a local var slot which can obtained from a call to newLocal or
     * from a lcoal variable table entry.
     * @param local the slot to load from
     */

    public void loadLocal(int local)
    {
        Type type = (Type)localTypes.get(local);
        visitVarInsn(type.getOpcode(Opcodes.ILOAD), local);
    }
    /**
     * save a value on the stack to a local var slot
     * @param local the slot to save to
     */

    public void storeLocal(int local)
    {
        Type type = (Type)localTypes.get(local);
        visitVarInsn(type.getOpcode(Opcodes.ISTORE), local);
    }

    public Type getLocalType(int local)
    {
        return (Type)localTypes.get(local);
    }

    /**
     * ensure we allow enough room for any extra locals on the stack
     * 
     * @param maxStack
     * @param maxLocals
     */
    public void visitMaxs(final int maxStack, final int maxLocals) {
        if (localHighWater < maxLocals) {
            localHighWater = maxLocals;
        }
        
        mv.visitMaxs(maxStack, localHighWater);
    }

    private void dumpFrame(int nLocal, Object[] local, int nStack, Object[] stack)
    {
        StringBuffer buffer = new StringBuffer();
        String sepr;
        Label l = new Label();
        visitLabel(l);
        buffer.append("Frame ");
        buffer.append(l.getOffset());
        buffer.append("\n");
        buffer.append("  locals ");
        buffer.append(nLocal);
        buffer.append("\n    ");
        sepr = "";
        for (int i = 0; i < nLocal; i++) {
            buffer.append(sepr);
            dumpType(buffer, local[i]);
            sepr=",\n    ";
        }
        buffer.append("\n  stack ");
        buffer.append(nStack);
        buffer.append("\n    ");
        sepr = "";
        for (int i = 0; i < nStack; i++) {
            buffer.append(sepr);
            dumpType(buffer, stack[i]);
            sepr=",\n    ";
        }
        System.out.println(buffer.toString());
    }

    private void dumpType(StringBuffer buffer, Object t)
    {

        if (t == Opcodes.TOP) {
            buffer.append("TOP");
        } else if (t == null) {
            buffer.append("null");
        } else if (t == Opcodes.INTEGER) {
            buffer.append("int");
        } else if (t == Opcodes.FLOAT) {
            buffer.append("float");
        } else if (t == Opcodes.DOUBLE) {
            buffer.append("double");
        } else if (t == Opcodes.LONG) {
            buffer.append("long");
        } else if (t == Opcodes.NULL) {
            buffer.append("null");
        } else if (t == Opcodes.UNINITIALIZED_THIS) {
            buffer.append("uninit_this");
        } else if (t instanceof String) {
            buffer.append((String)t);
        } else {
            buffer.append(((Label)t).getOffset());
        }
    }

    public void visitFrame(
        final int type,
        final int nLocal,
        final Object[] local,
        final int nStack,
        final Object[] stack)
    {
        if (type != Opcodes.F_NEW) { // uncompressed frame
            throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
        }

        // dumpFrame(nLocal, local, nStack, stack);

        // adjust the local types array

        int toRemove = localTypes.size();

        for (int i = toRemove; i > 0; i--) {
            localTypes.remove(i - 1);
        }

        int nextLocal = 0;

        for (int i = 0; i < nLocal; i++) {
            Object t = local[i];
            if (t == Opcodes.TOP) {
                localTypes.add(null);
            } else if (t == null) {
                localTypes.add(null);
            } else if (t == Opcodes.INTEGER) {
                localTypes.add(Type.INT_TYPE);
            } else if (t == Opcodes.FLOAT) {
                localTypes.add(Type.FLOAT_TYPE);
            } else if (t == Opcodes.DOUBLE) {
                localTypes.add(Type.DOUBLE_TYPE);
                nextLocal++;
                localTypes.add(null);
            } else if (t == Opcodes.LONG) {
                localTypes.add(Type.LONG_TYPE);
                nextLocal++;
                localTypes.add(null);
            } else if (t == Opcodes.NULL) {
                localTypes.add(null);
            } else if (t == Opcodes.UNINITIALIZED_THIS) {
                localTypes.add(null);
            } else if (t instanceof String) {
                localTypes.add(Type.getObjectType((String)t));
            } else {
                localTypes.add(null);
            }
            nextLocal++;
        }
        
        this.nextLocal = nextLocal;

        mv.visitFrame(type, nLocal, local, nStack, stack);
    }
}