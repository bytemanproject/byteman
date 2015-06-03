/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat and individual contributors
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

package org.jboss.byteman.rule.expression;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

/**
 * Created by adinn on 27/05/15.
 */
public class ArrayInitExpression extends Expression
{
    public ArrayInitExpression(Rule rule, Type type, ParseNode token, List<Expression> elements)
    {
        super(rule, type, token);
        this.elements = elements;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @throws org.jboss.byteman.rule.exception.TypeException if any variable is missing or has the wrong type
     */
    public void bind() throws TypeException
    {
        for (Expression element : elements) {
            element.bind();
        }
    }

    public Type typeCheck(Type expected) throws TypeException
    {
        Type baseType;
        if (type.isUndefined()) {
            baseType = Type.UNDEFINED;
        } else if (type.isArray()) {
            baseType = type.getBaseType();
        } else {
            throw new TypeException("ArrayInitExpression.typeCheck : cannot initialise non-array type from array list " + type.getName() + getPos());
        }

        for (Expression element : elements) {
            Type t =  element.typeCheck(baseType);
            if (baseType.isUndefined()) {
                baseType = t;
            }
        }
        if (type.isUndefined()) {
            type = baseType.arrayType();
        }
        return type;
    }


    public Object interpret(HelperAdapter helper) throws ExecuteException {
        // evaluate the array expression then evaluate each index expression in turn and
        // dereference to access the array element

        try {
            Class<?> clazz = type.getBaseType().getTargetClass();
            Object array = Array.newInstance(clazz, elements.size());
            int idx = 0;
            for (Expression element : elements) {
                Object value = element.interpret(helper);
                Array.set(array, idx++, value);
            }
            return array;
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("ArrayInitExpression.interpret : unexpected exception initialising array " + token.getText() + getPos(), e);
        }
    }


    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Type baseType = getType().getBaseType();
        int currentStack = compileContext.getStackCount();
        int expected = 1;
        int length = elements.size();

        // stack array size and then create the array
        mv.visitLdcInsn(length);
        compileContext.addStackCount(1);
        // new array pops count and pushes array so no change to stack size
        if (baseType.isObject()) {
            mv.visitTypeInsn(Opcodes.NEWARRAY, baseType.getInternalName());
        } else {
            int operand = 0;
            if (baseType.equals(Type.Z)) {
                operand = Opcodes.T_BOOLEAN;
            } else if (baseType.equals(Type.B)) {
                operand = Opcodes.T_BYTE;
            } else if (baseType.equals(Type.S)) {
                operand = Opcodes.T_SHORT;
            } else if (baseType.equals(Type.C)) {
                operand = Opcodes.T_CHAR;
            } else if (baseType.equals(Type.I)) {
                operand = Opcodes.T_INT;
            } else if (baseType.equals(Type.J)) {
                operand = Opcodes.T_LONG;
            } else if (baseType.equals(Type.F)) {
                operand = Opcodes.T_FLOAT;
            } else if (baseType.equals(Type.D)) {
                operand = Opcodes.T_DOUBLE;
            }
            mv.visitIntInsn(Opcodes.NEWARRAY, operand);
        }

        int idx = 0;
        boolean isTwoWords = (baseType.getNBytes() > 4);

        for (Expression element : elements) {
            int toPop = 0;
            // copy array so we can assign it -- adds one to height
            mv.visitInsn(Opcodes.DUP);
            // compile expression index -- adds 1 to height
            mv.visitLdcInsn(idx);
            compileContext.addStackCount(2);
            // compile value -- adds one or two words to height
            element.compile(mv, compileContext);
            // ensure we have the correct value type
            compileTypeConversion(element.type, baseType, mv, compileContext);
            // now we can do the array store
            if (baseType.isObject() || baseType.isArray()) {
                // compile load object - pops 3
                mv.visitInsn(Opcodes.AASTORE);
                toPop =- 3;
            } else if (baseType == Type.Z || baseType == Type.B) {
                // compile load byte - pops 3
                mv.visitInsn(Opcodes.BASTORE);
                toPop = -3;
            } else if (baseType == Type.S) {
                // compile load short - pops 3
                mv.visitInsn(Opcodes.SASTORE);
                toPop = -3;
            } else if (baseType == Type.C) {
                // compile load char - pops 3
                mv.visitInsn(Opcodes.CASTORE);
                toPop = -3;
            } else if (baseType == Type.I) {
                // compile load int - pops 3
                mv.visitInsn(Opcodes.IASTORE);
                toPop = -3;
            } else if (baseType == Type.J) {
                // compile load long - pops 4
                mv.visitInsn(Opcodes.LASTORE);
                toPop = -4;
            } else if (baseType == Type.F) {
                // compile load float - pops 3
                mv.visitInsn(Opcodes.FASTORE);
                toPop = -3;
            } else if (baseType == Type.D) {
                // compile load double - pops 4
                mv.visitInsn(Opcodes.DASTORE);
                toPop = -4;
            }
            // pop the appropriate number of elements off the stack
            compileContext.addStackCount(toPop);
        }

        // check stack height
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("ArrayInitExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expected));
        }

        // no need to update stack max
    }

    @Override
    public void writeTo(StringWriter stringWriter) {
        String prefix = "{ ";
        for (Expression expr : elements) {
            stringWriter.write(prefix);
            expr.writeTo(stringWriter);
            prefix = ",\n  ";
        }
        stringWriter.write("};\n");
    }

    List<Expression> elements;
}
