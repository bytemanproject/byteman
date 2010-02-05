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

import org.jboss.byteman.rule.binding.Binding;
import org.jboss.byteman.rule.type.Type;
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

/**
 * an expression which identifies a variable occurring either as an LVALUE on the LHS of an event
 * binding in the rule's event or as an RVALUE mentioned in the RHS of an event binding or in thre
 * rule's conditon or action.
 */
public class Variable extends AssignableExpression
{
    public Variable(Rule rule, Type type, ParseNode token) {
        super(rule, type, token);
        this.name = token.getText();
    }

    public Variable(Rule rule, Type type, ParseNode token, String name) {
        super(rule, type, token);
        this.name = name;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public boolean bind() {
        // ensure that there is a binding with this name

        Binding binding = getBindings().lookup(name);

        if (binding == null) {
            System.err.println("Variable.bind : unbound variable " + name + getPos());
            return false;
        }
        // adopt the binding type

        this.type = binding.getType();

        return true;
    }

    public Type typeCheck(Type expected) throws TypeException {
        // type must be defined by now or we are in trouble

        Binding binding = getBindings().lookup(name);

        type = Type.dereference(binding.getType());

        if (type.isUndefined()) {
            throw new TypeException("Variable.typeCheck : unable to derive type for variable " + name +  getPos());
        }
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("Variable.typeCheck() : invalid result type : " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        return helper.getBinding(name);
    }

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        int currentStack = currentStackHeights.stackCount;

        // stack the current helper
        // stack the name for the variable
        // call the getBinding method
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.internalName(HelperAdapter.class), "getBinding", "(Ljava/lang/String;)Ljava/lang/Object;");
        // ok, we added 2 to the stack and then popped them leaving 1
        currentStackHeights.addStackCount(1);
        // perform any necessary type conversion
        if (type.isPrimitive()) {
            // cast down to the boxed type then do an unbox
            Type boxType = Type.boxType(type);
            compileObjectConversion(Type.OBJECT, boxType, mv, currentStackHeights, maxStackHeights);
            compileUnbox(boxType, type,  mv, currentStackHeights, maxStackHeights);
        } else {
            // cast down to the required type
            compileObjectConversion(Type.OBJECT, type, mv, currentStackHeights, maxStackHeights);
        }
        // make sure we have room for 2 working slots
        int overflow = (currentStack + 2 - maxStackHeights.stackCount);
        if (overflow > 0) {
            maxStackHeights.addStackCount(overflow);
        }
    }

    @Override
    public Object interpretAssign(HelperAdapter helperAdapter, Object value) throws ExecuteException
    {
        helperAdapter.setBinding(name, value);
        return value;
    }

    @Override
    public void compileAssign(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        int currentStack = currentStackHeights.stackCount;
        int size = ((type.getNBytes() > 4) ? 2 : 1);
        int max;

        // value to be assigned is TOS and will already be coerced to the correct value type
        // copy it so we leave it as a a return value on the stack
        if (size == 2) {
            mv.visitInsn(Opcodes.DUP2);
        } else {
            mv.visitInsn(Opcodes.DUP);
        }
        // stack the current helper then insert it below the value
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        if (size == 2) {
            // use a DUP_X2 to push a copy below the value then pop the redundant value
            mv.visitInsn(Opcodes.DUP_X2);
            mv.visitInsn(Opcodes.POP);
        } else {
            // we can just swap the two values
            mv.visitInsn(Opcodes.SWAP);
        }
        // stack the name for the variable and swap below the value
        mv.visitLdcInsn(name);
        if (size == 2) {
            // use a DUP_X2 to push a copy below the value then pop the redundant value
            mv.visitInsn(Opcodes.DUP_X2);
            // this is the high water mark
            // at this point the stack has gone from [ .. val1 val2]  to [.. val1 val2 helper name val1 val2 name]
            max = 3 + size;
            mv.visitInsn(Opcodes.POP);
            // and now we have the desired arrangement for the call[.. val1 val2 helper name val]
        } else {
            // this is the high water mark
            // at this point the stack has gone from [ .. val]  to [.. val helper val name]
            max = 2 + size;
            // we can just swap the two values
            mv.visitInsn(Opcodes.SWAP);
            // and now we have the desired arrangement for the call[.. val helper name val]
        }
        // update the stack count for the value and two extra words before we attempt a type conversion
        currentStackHeights.addStackCount(2 + size);
        // ensure we have an object
        compileObjectConversion(type, Type.OBJECT, mv, currentStackHeights, maxStackHeights);

        // call the setBinding method
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.internalName(HelperAdapter.class), "setBinding", "(Ljava/lang/String;Ljava/lang/Object;)V");

        // the call will remove 3 from the stack height
        currentStackHeights.addStackCount(-3);

        // ok, the stack height should be as it was
        if (currentStackHeights.stackCount != currentStack) {
            throw new CompileException("variable.compileAssignment : invalid stack height " + currentStackHeights.stackCount + " expecting " + currentStack);
        }
        // make sure we left room for the right number of working slots at our maximum
        // at the high water mark we had
        int overflow = (currentStack + max - maxStackHeights.stackCount);
        if (overflow > 0) {
            maxStackHeights.addStackCount(overflow);
        }
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(name);
    }

    private String name;
}
