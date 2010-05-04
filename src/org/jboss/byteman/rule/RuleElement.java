/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.byteman.rule;

import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.type.TypeGroup;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.compiler.StackHeights;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;

import java.io.StringWriter;

/**
 * generic class implemented by rule events, conditions and actions which gives them
 * access to the rule context and provides them with common behaviours
 */
public abstract class RuleElement {
    protected RuleElement(Rule rule)
    {
        this.rule = rule;
    }

    protected Rule rule;

    protected TypeGroup getTypeGroup()
    {
        return rule.getTypeGroup();
    }

    protected Bindings getBindings()
    {
        return rule.getBindings();
    }

    public abstract Type typeCheck(Type expected) throws TypeException;

    public abstract Object interpret(HelperAdapter helper) throws ExecuteException;

    public abstract void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException;

    protected void compileTypeConversion(Type fromType, Type toType, MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights)
            throws CompileException
    {
        // make sure we have some real work to do

        if (fromType.equals(toType)) {
            return;
        }

        if (toType.isNumeric()) {
            // do number conversion
            compileNumericConversion(fromType, toType, mv, currentStackHeights, maxStackHeights);
        } else if (toType.isString()) {
            // do toString conversion
            compileStringConversion(fromType, toType, mv, currentStackHeights, maxStackHeights);
        } else if (toType.isBoolean()) {
            // do toString conversion
            compileBooleanConversion(fromType, toType, mv, currentStackHeights, maxStackHeights);
        } else {
            compileObjectConversion(fromType, toType, mv, currentStackHeights, maxStackHeights);
        }
    }

    protected void compileNumericConversion(Type fromType, Type toType, MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights)
            throws CompileException
    {
        // fromType != toType
        boolean unbox = fromType.isObject();
        boolean box = toType.isObject();

        if (unbox) {
            if (box) {
                Type midType = Type.boxType(toType);
                compileUnbox(fromType, midType, mv, currentStackHeights, maxStackHeights);
                compileBox(midType, mv, currentStackHeights, maxStackHeights);
            } else {
                compileUnbox(fromType, toType, mv, currentStackHeights, maxStackHeights);
            }
        } else if (box) {
            Type midType = Type.boxType(toType);
            if (fromType != midType) {
                compilePrimitiveConversion(fromType, midType, mv, currentStackHeights, maxStackHeights);
            }
            compileBox(toType, mv, currentStackHeights, maxStackHeights);
        } else {
            compilePrimitiveConversion(fromType, toType, mv, currentStackHeights, maxStackHeights);
        }
    }

    /**
     * compile code to convert a value of a boxed type to a primitive type, possibly not the immediately
     * related primitive type
     *
     * @param fromType
     * @param toType
     * @param mv
     * @param currentStackHeights
     * @param maxStackHeights
     * @throws CompileException
     */
   protected void compileUnbox(Type fromType, Type toType, MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights)
            throws CompileException
    {
        // we either have a Boolean, a Character or a Number for fromType
        if (fromType == Type.BOOLEAN) {
            assert toType == Type.Z;
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
        } else if (fromType == Type.CHARACTER) {
            // obtain the underlying char then massage it to the correct bit format
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
            compilePrimitiveConversion(Type.C, toType, mv, currentStackHeights, maxStackHeights);
        } else {
            // we have a numeric type so call the relevant conversion method
            if (toType == Type.B) {
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B");
            } else if (toType == Type.S){
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S");
            } else if (toType == Type.C){
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I");
                // now convert to char, dropping any sign extension
                mv.visitIntInsn(Opcodes.ISHL, 16);
                mv.visitIntInsn(Opcodes.LSHR, 16);
            } else if (toType == Type.I){
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I");
            } else if (toType == Type.J){
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J");
                currentStackHeights.addStackCount(1);
                if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                    maxStackHeights.stackCount = currentStackHeights.stackCount;
                }
            } else if (toType == Type.F){
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F");
            } else {
                assert toType == Type.D;
                currentStackHeights.addStackCount(1);
                if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                    maxStackHeights.stackCount = currentStackHeights.stackCount;
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D");
            }
        }
    }

    /**
     * box a value belonging to a primitive type
     * @param toType
     * @param mv
     * @param currentStackHeights
     * @param maxStackHeights
     * @throws CompileException
     */
    protected void compileBox(Type toType, MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights)
            throws CompileException
    {
        // use the statci methods on the class  to do conversions -- that means the class gets a chance
        // to reuse cached values
        if (toType == Type.BOOLEAN) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        } else if (toType == Type.BYTE) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
        } else if (toType == Type.SHORT){
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
        } else if (toType == Type.CHARACTER){
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
        } else if (toType == Type.INTEGER) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        } else if (toType == Type.LONG) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            currentStackHeights.addStackCount(-1);
        } else if (toType == Type.FLOAT) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
        } else if (toType == Type.DOUBLE) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            currentStackHeights.addStackCount(-1);
        }
    }

    protected void compileStringConversion(Type fromType, Type toType, MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights)
            throws CompileException
    {
        assert toType == Type.STRING;
        if (fromType.isObject() || fromType.isArray() || (fromType.isNumeric() && !fromType.isPrimitive())) {
            // use the toString method if the object is non null otherwise just replace it with null
            Label elseLabel = new Label();
            Label endLabel = new Label();
            // if (object == null)
            mv.visitInsn(Opcodes.DUP);
            mv.visitJumpInsn(Opcodes.IFNONNULL, elseLabel);
            // then string = "null"
            mv.visitInsn(Opcodes.POP);
            mv.visitLdcInsn("null");
            mv.visitJumpInsn(Opcodes.GOTO, endLabel);
            // else string = object.toString()
            mv.visitLabel(elseLabel);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
            mv.visitLabel(endLabel);
        } else if (fromType == Type.Z) {
            // use the toString method
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "toString", "(Z)Ljava/lang/String;");
        } else if (fromType == Type.B) {
            // use the toString method
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "toString", "(B)Ljava/lang/String;");
        } else if (fromType == Type.S) {
            // use the toString method
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "toString", "(S)Ljava/lang/String;");
        } else if (fromType == Type.C) {
            // use the toString method
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "toString", "(C)Ljava/lang/String;");
        } else if (fromType == Type.I) {
            // use the toString method
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "toString", "(I)Ljava/lang/String;");
        } else if (fromType == Type.J) {
            // use the toString method
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "toString", "(J)Ljava/lang/String;");
            currentStackHeights.addStackCount(-1);
        } else if (fromType == Type.F) {
            // use the toString method
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "toString", "(F)Ljava/lang/String;");
        } else if (fromType == Type.D) {
            // use the toString method
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "toString", "(D)Ljava/lang/String;");
            currentStackHeights.addStackCount(-1);
        }
    }

    /**
     * compile code to convert a numeric or character primitive to a numeric or character primitive
     * @param fromType
     * @param toType
     * @param mv
     * @param currentStackHeights
     * @param maxStackHeights
     * @throws CompileException
     */
    protected void compilePrimitiveConversion(Type fromType, Type toType, MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights)
            throws CompileException
    {
        if (fromType == Type.B || fromType == Type.S || fromType == Type.I) {
            if (toType == Type.B) {
                mv.visitInsn(Opcodes.I2B);
            } else if (toType == Type.S) {
                mv.visitInsn(Opcodes.I2S);
            } else if (toType == Type.C) {
                mv.visitInsn(Opcodes.I2C);
            } else if (toType == Type.I) {
                // nothing to do
            } else if (toType == Type.J) {
                mv.visitInsn(Opcodes.I2L);
                currentStackHeights.addStackCount(1);
                if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                    maxStackHeights.stackCount = currentStackHeights.stackCount;
                }
            } else if (toType == Type.F) {
                mv.visitInsn(Opcodes.I2F);
            } else if (toType == Type.D) {
                mv.visitInsn(Opcodes.I2D);
                currentStackHeights.addStackCount(1);
                if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                    maxStackHeights.stackCount = currentStackHeights.stackCount;
                }
            }
        } else if (fromType == Type.C) {
            // convert to the relevant numeric size
            if (toType == Type.B) {
                mv.visitInsn(Opcodes.I2B);
            } else if (toType == Type.S) {
                mv.visitInsn(Opcodes.I2S);
            } else if (toType == Type.C) {
                // nothing to do
            } else if (toType == Type.I) {
                // nothing to do
            } else  if (toType == Type.J) {
                mv.visitInsn(Opcodes.I2L);
                currentStackHeights.addStackCount(1);
                if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                    maxStackHeights.stackCount = currentStackHeights.stackCount;
                }
            } else if (toType == Type.F) {
                mv.visitInsn(Opcodes.I2F);
            } else if (toType == Type.D) {
                mv.visitInsn(Opcodes.I2D);
                currentStackHeights.addStackCount(1);
                if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                    maxStackHeights.stackCount = currentStackHeights.stackCount;
                }
            }
        } else if (fromType == Type.J) {
            if (toType == Type.B || toType ==  Type.S || toType == Type.I || toType == Type.C) {
                mv.visitInsn(Opcodes.L2I);
                currentStackHeights.addStackCount(-1);
            } else if (toType == Type.J) {
                // nothing to do
            } else if (toType == Type.F) {
                mv.visitInsn(Opcodes.L2F);
                currentStackHeights.addStackCount(-1);
            } else if (toType == Type.D) {
                mv.visitInsn(Opcodes.L2D);
            }
        } else if (fromType == Type.F) {
            if (toType == Type.B) {
                mv.visitInsn(Opcodes.F2I);
                mv.visitInsn(Opcodes.I2B);
            } else if (toType == Type.S) {
                mv.visitInsn(Opcodes.F2I);
                mv.visitInsn(Opcodes.I2S);
            } else if (toType == Type.C) {
                mv.visitInsn(Opcodes.F2I);
                mv.visitInsn(Opcodes.I2C);
            } else if (toType == Type.I) {
                mv.visitInsn(Opcodes.F2I);
            } else if (toType == Type.J) {
                mv.visitInsn(Opcodes.F2L);
                currentStackHeights.addStackCount(1);
                if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                    maxStackHeights.stackCount = currentStackHeights.stackCount;
                }
            } else if (toType == Type.F) {
                // nothing to do
            } else if (toType == Type.D) {
                mv.visitInsn(Opcodes.F2D);
                currentStackHeights.addStackCount(1);
                if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                    maxStackHeights.stackCount = currentStackHeights.stackCount;
                }
            }
        } else if (fromType == Type.D) {
            if (toType == Type.B) {
                mv.visitInsn(Opcodes.D2I);
                mv.visitInsn(Opcodes.I2B);
            } else if (toType == Type.S) {
                mv.visitInsn(Opcodes.D2I);
                mv.visitInsn(Opcodes.I2S);
            } else if (toType == Type.C) {
                mv.visitInsn(Opcodes.D2I);
                mv.visitInsn(Opcodes.I2C);
            } else if (toType == Type.I) {
                mv.visitInsn(Opcodes.D2I);
            } else if (toType == Type.J) {
                mv.visitInsn(Opcodes.D2L);
            } else if (toType == Type.F) {
                mv.visitInsn(Opcodes.D2F);
                currentStackHeights.addStackCount(-1);
            } else if (toType == Type.D) {
                // nothing to do
            }
        }
    }

    protected void compileBooleanConversion(Type fromType, Type toType, MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights)
            throws CompileException
    {
        if (toType == Type.Z) {
            assert fromType == Type.BOOLEAN;
            compileUnbox(fromType, toType, mv, currentStackHeights, maxStackHeights);
        } else {
            assert toType == Type.BOOLEAN;
            assert fromType == Type.Z;
            compileBox(toType, mv, currentStackHeights, maxStackHeights);
        }
    }

    protected void compileObjectConversion(Type fromType, Type toType, MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights)
            throws CompileException
    {
        // ensure any primitive type is boxed before we go any further

        if (fromType.isPrimitive()) {
            Type boxType = Type.boxType(fromType);
            compileBox(boxType, mv, currentStackHeights, maxStackHeights);
            fromType = boxType;
        }

        if (toType.isAssignableFrom(fromType)) {
            // special case -- isAssignableFrom says yes if we are trying to assign to a String but
            // we may still need to do a toString cobversion all the same
            if (toType == Type.STRING && fromType != Type.STRING) {
                compileStringConversion(fromType, toType, mv, currentStackHeights, maxStackHeights);
            } else {
                // nothing more to do
            }
        } else {
            // this happens when we downcast a bound variable from Object to the variable's type
            assert fromType.isAssignableFrom(toType);
            mv.visitTypeInsn(Opcodes.CHECKCAST, toType.getInternalName(false, true));
        }
    }

    public String toString()
    {
        StringWriter stringWriter = new StringWriter();
        writeTo(stringWriter);
        return stringWriter.toString();
    }

    public abstract void writeTo(StringWriter stringWriter);
}
