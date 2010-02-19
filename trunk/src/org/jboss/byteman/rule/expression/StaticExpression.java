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
package org.jboss.byteman.rule.expression;

import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.type.TypeGroup;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.compiler.StackHeights;
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
            field = clazz.getField(fieldName);
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

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        int currentStack = currentStackHeights.stackCount;
        int expected;

        // compile a field access

        String ownerType = Type.internalName(field.getDeclaringClass());
        String fieldName = field.getName();
        String fieldType = Type.internalName(field.getType(), true);
        mv.visitFieldInsn(Opcodes.GETSTATIC, ownerType, fieldName, fieldType);
        expected = (type.getNBytes() > 4 ? 2 : 1);

        currentStackHeights.addStackCount(expected);

        int overflow = ((currentStack + expected) - maxStackHeights.stackCount);
        
        if (overflow > 0) {
            maxStackHeights.addStackCount(overflow);
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
    public void compileAssign(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        int currentStack = currentStackHeights.stackCount;
        int size = (type.getNBytes() > 4 ? 2 : 1);

        // copy the value so we leave a result
        // increases stack height by size words
        if (size == 1) {
            mv.visitInsn(Opcodes.DUP);
        } else {
            mv.visitInsn(Opcodes.DUP2);
        }
        // compile a static field update

        String ownerType = Type.internalName(field.getDeclaringClass());
        String fieldName = field.getName();
        String fieldType = Type.internalName(field.getType(), true);
        mv.visitFieldInsn(Opcodes.PUTSTATIC, ownerType, fieldName, fieldType);
        // ensure that we had room for size extra words
        int overflow = (currentStack + size - maxStackHeights.stackCount);
        if (overflow > 0) {
            maxStackHeights.addStackCount(overflow);
        }
    }
}