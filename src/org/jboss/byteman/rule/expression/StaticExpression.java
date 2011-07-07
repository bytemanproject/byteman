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
package org.jboss.byteman.rule.expression;

import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.type.TypeGroup;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * an expression which identifies a static field reference
 */
public class StaticExpression extends AssignableExpression
{
    public StaticExpression(Rule rule, Type type, ParseNode token, String fieldName, String ownerTypeName) {
        // type is the type of static field
        super(rule, type, token);
        this.ownerTypeName = ownerTypeName;
        this.fieldName = fieldName;
        this.ownerType = null;
        this.fieldIndex = -1;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public void bind() throws TypeException {
        // nothing to verify
    }

    /**
     * treat this as a normal bind because an update to a field reference does not update any bindings
     * @return whatever a normal bind call returns
     */
    public void bindAssign() throws TypeException
    {
        bind();
    }

    public Type typeCheck(Type expected) throws TypeException {
        // look for a class whose name matches some initial segment of pathList
        TypeGroup typeGroup = getTypeGroup();
        ownerType = Type.dereference(typeGroup.create(ownerTypeName));
        if (ownerType.isUndefined()) {
            throw new TypeException("StaticExpression.typeCheck : invalid path " + ownerTypeName + " to static field " + fieldName + getPos());
        }

        Class clazz = ownerType.getTargetClass();
        try {
            field  = lookupField(clazz);
        } catch (NoSuchFieldException e) {
                // oops
            throw new TypeException("StaticExpression.typeCheck : invalid field name " + fieldName + getPos());
        }

        if ((field.getModifiers() & Modifier.STATIC)== 0) {
            // oops
            throw new TypeException("StaticExpression.typeCheck : field is not static " + fieldName + getPos());
        }

        clazz = field.getType();
        type = typeGroup.ensureType(clazz);

        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("StaticExpression.typeCheck : invalid expected return type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        try {
            return field.get(null);
        } catch (ExecuteException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new ExecuteException("StaticExpression.interpret : error accessing field " + ownerTypeName + "." + fieldName + getPos(), e);
        } catch (Exception e) {
            throw new ExecuteException("StaticExpression.interpret : unexpected exception accessing field " + ownerTypeName + "." + fieldName + getPos(), e);
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        int currentStack = compileContext.getStackCount();
        int expected;

        // compile a field access

        if (isPublicField) {
            String ownerType = Type.internalName(field.getDeclaringClass());
            String fieldName = field.getName();
            String fieldType = Type.internalName(field.getType(), true);
            mv.visitFieldInsn(Opcodes.GETSTATIC, ownerType, fieldName, fieldType);
            expected = (type.getNBytes() > 4 ? 2 : 1);
            compileContext.addStackCount(expected);
        } else {
            // since this is a private field we need to do the access using reflection
            // stack the helper, a null owner and the field index
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitLdcInsn(fieldIndex);
            compileContext.addStackCount(3);
            // use the HelperAdapter method getAccessibleField to get the field value
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                    Type.internalName(HelperAdapter.class),
                    "getAccessibleField",
                    "(Ljava/lang/Object;I)Ljava/lang/Object;");
            // we popped three words and added one object as result
            compileContext.addStackCount(-2);
            // convert Object to primitive or cast to subtype if required
            compileTypeConversion(Type.OBJECT, type, mv, compileContext);
        }
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(ownerTypeName);
        stringWriter.write(".");
        stringWriter.write(fieldName);
    }

    /**
     * the list of path components which may include package qualifiers, the class name, the
     * field name and subordinate field references
     */

    private String ownerTypeName;
    private String fieldName;
    private Field field;
    private Type ownerType;
    private boolean isPublicField;
    private int fieldIndex;

    @Override
    public Object interpretAssign(HelperAdapter helperAdapter, Object value) throws ExecuteException
    {
        try {
            field.set(null, value);
            return value;
        } catch (ExecuteException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new ExecuteException("StaticExpression.interpretAssign : error accessing field " + ownerTypeName + "." + fieldName + getPos(), e);
        } catch (IllegalArgumentException e) {
            throw new ExecuteException("StaticExpression.interpretAssign : invalid value assigning field " + ownerTypeName + "." + fieldName + getPos(), e);
        } catch (Exception e) {
            throw new ExecuteException("StaticExpression.interpretAssign : unexpected exception accessing field " + ownerTypeName + "." + fieldName + getPos(), e);
        }
    }

    @Override
    public void compileAssign(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        int currentStack =compileContext.getStackCount();
        int size = (type.getNBytes() > 4 ? 2 : 1);

        // copy the value so we leave a result
        // increases stack height by size words
        if (size == 1) {
            mv.visitInsn(Opcodes.DUP);
        } else {
            mv.visitInsn(Opcodes.DUP2);
        }
        compileContext.addStackCount(size);
        // compile a static field update

        if (isPublicField) {
            String ownerType = Type.internalName(field.getDeclaringClass());
            String fieldName = field.getName();
            String fieldType = Type.internalName(field.getType(), true);
            compileContext.addStackCount(-size);
            mv.visitFieldInsn(Opcodes.PUTSTATIC, ownerType, fieldName, fieldType);
        } else {
            // since this is a private field we need to do the update using reflection
            // box the value to an object if necessary
            // [.. val(s) val(s) ==> val(s) valObj]
            if (type.isPrimitive()) {
                compileBox(Type.boxType(type), mv, compileContext);
            }
            // stack the helper and then swap it so it goes under the value
            // [.. val(s) valObj ==> val(s) valObj helper ==> val(s) helper valObj]
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.SWAP);
            // stack a null owner then swap it so it goes under the value
            // [val(s) helper valObj ==> val(s) helper valObj null ==> val(s) helper null valObj]
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitInsn(Opcodes.SWAP);
            // now stack the field index
            // [.. val(s) helper null valObj ==> val(s) helper null valObj index ]
            mv.visitLdcInsn(fieldIndex);
            // we added three more words
            compileContext.addStackCount(3);
            // use the HelperAdapter method setAccessibleField to set the field value
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                    Type.internalName(HelperAdapter.class),
                    "setAccessibleField",
                    "(Ljava/lang/Object;Ljava/lang/Object;I)V");
            // we popped four args
            compileContext.addStackCount(-4);
        }

        if (compileContext.getStackCount() !=  currentStack) {
            throw new CompileException("StaticExpression.compileAssign : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack);
        }
    }

    private Field lookupField(Class<?> ownerClazz) throws NoSuchFieldException
    {
        try {
            Field field = ownerClazz.getField(fieldName);
            isPublicField = true;
            return field;
        } catch (NoSuchFieldException nsfe) {
            // look for a protected or private field with the desired name
            Class<?> nextClass = ownerClazz;
            while (nextClass != null) {
                try {
                    field = nextClass.getDeclaredField(fieldName);
                    isPublicField = false;
                    field.setAccessible(true);
                    // register the field with the rule so we can access it later
                    fieldIndex = rule.addAccessibleField(field);
                    return field;
                } catch (NoSuchFieldException e) {
                    // continue
                } catch (SecurityException e) {
                    // continue
                }
                nextClass = nextClass.getSuperclass();
            }

            throw nsfe;
        }
    }
}