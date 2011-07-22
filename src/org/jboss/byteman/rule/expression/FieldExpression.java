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

import org.jboss.byteman.rule.binding.Binding;
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
 * an expression which identifies an instance field reference
 */
public class FieldExpression extends AssignableExpression
{
    public FieldExpression(Rule rule, Type type, ParseNode fieldTree, String fieldName, Expression owner, String[] pathList) {
        // we cannot process the pathlist until typecheck time
        super(rule, type, fieldTree);
        this.fieldName = fieldName;
        this.owner = owner;
        this.pathList = pathList;
        this.ownerType = null;
        this.indirectStatic = null;
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
    public void bind() throws TypeException
    {

        if (owner != null) {
            // ensure the owner is bound
            owner.bind();
        } else {
            // see if the path starts with a bound variable and, if so, treat the path as a series
            // of field references and construct a owner expression from it. if not we will have to
            // wait until runtime in order to resolve this as a static field reference
            String leading = pathList[0];
            Binding binding = getBindings().lookup(leading);
            if (binding != null) {
                // create a sequence of field expressions and make it the owner

                int l = pathList.length;
                Expression owner =  new Variable(rule, binding.getType(), token, binding.getName());
                for (int idx = 1; idx < l; idx++) {
                    owner = new FieldExpression(rule, Type.UNDEFINED, token, pathList[idx], owner, null);
                }
                this.owner = owner;
                this.pathList = null;
                // not strictly necessary?
                this.owner.bind();
            }
        }
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
        if (owner == null && pathList != null) {
            // factor off a typename from the path
            TypeGroup typeGroup = getTypeGroup();
            Type rootType = typeGroup.match(pathList);
            if (rootType == null) {
                throw new TypeException("FieldExpression.typeCheck : invalid path " + getPath(pathList.length) + " to static field " + fieldName + getPos());
            }

            // find out how many of the path elements are included in the type name

            String rootTypeName = rootType.getName();

            int idx = getPathCount(rootTypeName);

            if (idx < pathList.length) {
                // create a static field reference using the type name and the first field name and wrap it with
                // enough field references to use up all the path
                String fieldName = pathList[idx++];
                Expression owner = new StaticExpression(rule, Type.UNDEFINED, token, fieldName, rootTypeName);
                while (idx < pathList.length) {
                    owner = new FieldExpression(rule, Type.UNDEFINED, token, pathList[idx++], owner, null);
                }
                this.owner = owner;
                // not strictly necessary?
                this.owner.bind();
            } else {
                // ok this field reference is actually a static reference -- install the one we just created as
                // owner and mark this one so it sidesteps any further requests to the owner
                this.indirectStatic = new StaticExpression(rule, Type.UNDEFINED, token, this.fieldName, rootTypeName);
                // not strictly necessary?
                this.indirectStatic.bind();
            }
            // get rid of the path list now
            this.pathList = null;
        }

        if (indirectStatic  != null) {
            // this is really a static field reference pointed to by owner so get it to type check
            type = Type.dereference(indirectStatic.typeCheck(expected));
            return type;
        } else {

            // ok, type check the owner and then use it to derive the field type

            ownerType = Type.dereference(owner.typeCheck(Type.UNDEFINED));
            
            if (ownerType.isUndefined()) {
                throw new TypeException("FieldExpresssion.typeCheck : unbound owner type for field " + fieldName + getPos());
            }

            Class ownerClazz = ownerType.getTargetClass();
            Class valueClass = null;

            try {
                field  = lookupField(ownerClazz);
            } catch (NoSuchFieldException e) {
                throw new TypeException("FieldExpresssion.typeCheck : invalid field reference " + fieldName + getPos());
            }

            if ((field.getModifiers() & Modifier.STATIC) != 0) {
                throw new TypeException("FieldExpresssion.typeCheck : field is static " + fieldName + getPos());
            }

            valueClass = field.getType();
            type = getTypeGroup().ensureType(valueClass);

            if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
                throw new TypeException("FieldExpresssion.typeCheck : invalid expected type " + expected.getName() + getPos());
            }

            return type;
        }
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException
    {
        if (indirectStatic != null) {
            return indirectStatic.interpret(helper);
        } else {
            try {
                // TODO the reference should really be an expression?
                Object value = owner.interpret(helper);

                if (value == null) {
                    throw new ExecuteException("FieldExpression.interpret : attempted field indirection through null value " + token.getText() + getPos());
                }

                return field.get(value);
            } catch (ExecuteException e) {
                throw e;
            } catch (IllegalAccessException e) {
                throw new ExecuteException("FieldExpression.interpret : error accessing field " + fieldName + getPos(), e);
            } catch (Exception e) {
                throw new ExecuteException("FieldExpression.interpret : unexpected exception accessing field " + fieldName + getPos(), e);
            }
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        int currentStack = compileContext.getStackCount();
        int expected = (type.getNBytes() > 4 ? 2 : 1);

        if (indirectStatic != null) {
            // this is just wrapping a static field expression so compile it
            indirectStatic.compile(mv, compileContext);
        } else {
            if (isPublicField) {
                // we can use GETFIELD to access a public field
                String ownerType = Type.internalName(field.getDeclaringClass());
                String fieldName = field.getName();
                String fieldType = Type.internalName(field.getType(), true);
                // compile the owner expression
                owner.compile(mv, compileContext);
                mv.visitFieldInsn(Opcodes.GETFIELD, ownerType, fieldName, fieldType);
                // we removed the owner and replaced with expected words
                compileContext.addStackCount(expected - 1);
            } else {
                // since this is a private field we need to do the access using reflection
                // stack the helper, owner and the field index
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                compileContext.addStackCount(1);
                owner.compile(mv, compileContext);
                mv.visitLdcInsn(fieldIndex);
                compileContext.addStackCount(1);
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
        // check the stack height is ok
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("FieldExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expected));
        }
    }

    public String getPath(int len)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(pathList[0]);

        for (int i = 1; i < len; i++) {
            buffer.append(".");
            buffer.append(pathList[i]);
        }
        return buffer.toString();
    }

    public int getPathCount(String name)
    {
        // name will be package qualified so check whether the path list also includes the package
        if (name.startsWith(pathList[0])) {
            int charMax = name.length();
            int charCount = 0;
            int dotExtra = 0;
            int idx;
            for (idx = 0; idx < pathList.length; idx++) {
                charCount += (dotExtra + pathList[idx].length());
                if (charCount > charMax) {
                    break;
                }
            }
            return idx;
        } else {
            // name must have been obtained by globalizing an unqualified type name so the typename
            // is the first element in the path list
            return 1;
        }
    }

    public void writeTo(StringWriter stringWriter) {
        // we normally have a owner expression but before binding we have a path
        if (owner != null) {
            owner.writeTo(stringWriter);
        } else {
            String sepr = "";
            for (String field : pathList) {
                stringWriter.write(sepr);
                stringWriter.write(field);
                sepr =".";
            }
        }
        stringWriter.write(".");
        stringWriter.write(fieldName);
    }

    private Expression owner;
    private String[] pathList;
    private String fieldName;
    private Type ownerType;
    private Field field;
    private AssignableExpression indirectStatic;
    /**
     * true if this is a public field otherwise false
     */
    private boolean isPublicField;
    /**
     * index used compiled code when reading or writing a non-public field to obtain the field descriptor
     * from the helper's list of accessible field descriptors.
     */
    private int fieldIndex;

    @Override
    public Object interpretAssign(HelperAdapter helperAdapter, Object value) throws ExecuteException
    {
        if (indirectStatic != null) {
            return indirectStatic.interpretAssign(helperAdapter, value);
        } else {
            try {
                Object ownerInstance = owner.interpret(helperAdapter);

                if (ownerInstance == null) {
                    throw new ExecuteException("FieldExpression.interpret : attempted field indirection through null value " + token.getText() + getPos());
                }

                field.set(ownerInstance, value);
                return value;
            } catch (ExecuteException e) {
                throw e;
            } catch (IllegalAccessException e) {
                throw new ExecuteException("FieldExpression.interpretAssign : error accessing field " + fieldName + getPos(), e);
            } catch (IllegalArgumentException e) {
                throw new ExecuteException("FieldExpression.interpretAssign : invalid value assigning field " + fieldName + getPos(), e);
            } catch (Exception e) {
                throw new ExecuteException("FieldExpression.interpretAssign : unexpected exception accessing field " + fieldName + getPos(), e);
            }
        }
    }

    @Override
    public void compileAssign(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        if (indirectStatic != null) {
            // this is just wrapping a static field expression so compile it
            indirectStatic.compileAssign(mv, compileContext);
        } else {
            // make sure we are at the right source line
            compileContext.notifySourceLine(line);

            int currentStack = compileContext.getStackCount();
            int size = (type.getNBytes() > 4 ? 2 : 1);

            // copy the value so we leave it as a result
            if (size == 1) {
                // this means at the maximum we add 1 to the current stack
                // [.. val] ==> [.. val val]
                mv.visitInsn(Opcodes.DUP);
            } else {
                // [.. val1 val2] ==> [.. val1 val2 val1 val2]
                mv.visitInsn(Opcodes.DUP2);
            }
            compileContext.addStackCount(size);
            // compile the owner expression and swap with the value
            owner.compile(mv, compileContext);
            if (size == 1) {
                // [.. val val owner] ==> [.. val owner val]
                mv.visitInsn(Opcodes.SWAP);
            } else {
                // we have to use a DUP_X2 and a POP to insert the owner below the two word value
                // i.e. [.. val1 val2 val1 val2] ==> [.. val1 val2 val1 val2 owner] ==>
                //              [.. val1 val2 owner val1 val2 owner] ==> [.. val1 val2 owner val1 val2]
                mv.visitInsn(Opcodes.DUP_X2);
                compileContext.addStackCount(1);
                mv.visitInsn(Opcodes.POP);
                compileContext.addStackCount(-1);
            }
            if (isPublicField) {
                // now compile a field update
                String ownerType = Type.internalName(field.getDeclaringClass());
                String fieldName = field.getName();
                String fieldType = Type.internalName(field.getType(), true);
                mv.visitFieldInsn(Opcodes.PUTFIELD, ownerType, fieldName, fieldType);
                // we removed the owner and the value
                compileContext.addStackCount(- (1 + size));
            } else {
                // since this is a private field we need to do the update using reflection
                // box the value to an object if necessary
                if (type.isPrimitive()) {
                    compileBox(Type.boxType(type),  mv, compileContext);
                }
                // stack the helper and then dupx2 it so it goes under the owner and value
                // [.. val(s) owner  valObj ==> val(s) owner valObj helper ]
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                // [.. val(s) owner  valObj helper ==> val(s) helper owner valObj helper ]
                mv.visitInsn(Opcodes.DUP_X2);
                // stack now has 2 more words so count them
                compileContext.addStackCount(2);
                // now pop the redundant top word and stack the field index instead
                // [.. val(s) helper owner valObj helper ==> val(s) helper owner valObj index ]
                mv.visitInsn(Opcodes.POP);
                mv.visitLdcInsn(fieldIndex);
                // use the HelperAdapter method setAccessibleField to set the field value
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                        Type.internalName(HelperAdapter.class),
                        "setAccessibleField",
                        "(Ljava/lang/Object;Ljava/lang/Object;I)V");
                // we popped four args
                compileContext.addStackCount(-4);
            }

            // check the stack height is ok
            if (compileContext.getStackCount() != currentStack) {
                throw new CompileException("FieldExpression.compileAssign : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack));
            }
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