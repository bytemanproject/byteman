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
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.Rule;
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
        this.binding = null;
    }

    public Variable(Rule rule, Type type, ParseNode token, String name) {
        super(rule, type, token);
        this.name = name;
        this.binding = null;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @throws TypeException if any variable is missing or has the wrong type
     */
    public void bind() throws TypeException
    {
        bind(false);
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list. infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @throws TypeException if any variable is missing or has the wrong type
     */

    public void bindAssign() throws TypeException
    {
        bind(true);
    }

    private boolean bind(boolean isUpdateable)throws TypeException
    {
        // ensure that there is a binding with this name

        binding = getBindings().lookup(name);

        if (binding == null) {
            throw new TypeException("Variable.bind : unbound variable " + name + getPos());
        }

        // if necessary tag it as updateable
        if (isUpdateable) {
            binding.setUpdated();
        }
        
        // adopt the binding type

        this.type = binding.getType();

        return true;
    }

    public Type typeCheck(Type expected) throws TypeException {

        typeCheckAny();

        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("Variable.typeCheck() : invalid result type : " + expected.getName() + getPos());
        }
        return type;
    }

    public Type typeCheckAssign(Type expected) throws TypeException {

        typeCheckAny();

        if (Type.dereference(expected).isDefined() && !type.isAssignableFrom(expected)) {
            throw new TypeException("Variable.typeCheck() : invalid value type : " + expected.getName() + " for assignment " + getPos());
        }
        return type;
    }

    public void typeCheckAny() throws TypeException {
        // type must be defined by now or we are in trouble

        Binding binding = getBindings().lookup(name);

        type = Type.dereference(binding.getType());

        if (type.isUndefined()) {
            throw new TypeException("Variable.typeCheck : unable to derive type for variable " + name +  getPos());
        }
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        return helper.getBinding(name);
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        String ivarName = binding.getIVarName();
        Type type = this.type;
        if (rule.requiresAccess(type)) {
            type = Type.OBJECT;
        }
        int expected = ((type.getNBytes() > 4) ? 2 : 1);
        // plant a getfield
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, rule.getHelperImplementationClassName(), ivarName, type.getInternalName(true, true));
        compileContext.addStackCount(expected);
    }

    @Override
    public Object interpretAssign(HelperAdapter helperAdapter, Object value) throws ExecuteException
    {
        helperAdapter.setBinding(name, value);
        return value;
    }

    @Override
    public void compileAssign(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        String ivarName = binding.getIVarName();
        Type type = this.type;
        if (rule.requiresAccess(type)) {
            type = Type.OBJECT;
        }
        int size = ((type.getNBytes() > 4) ? 2 : 1);
        int currentStack = compileContext.getStackCount();
        int max;
        // plant a putfield but leave a copy of the original value on the stack
        // value to be assigned is TOS and will already be coerced to the correct value type
        // copy it so we leave it as a a return value on the stack
        if (size == 2) {
            // [... val1 val2 ==> ... val1 val2 val1 val2]
            mv.visitInsn(Opcodes.DUP2);
        } else {
            // [... val ==> ... val val]
            mv.visitInsn(Opcodes.DUP);
        }
        compileContext.addStackCount(size);

        // stack the current helper then insert it below the value
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        compileContext.addStackCount(1);
        if (size == 2) {
            // use a DUP_X2 to push a copy below the value then pop the redundant value
            // [... val1 val2 val1 val2 helper ==> ... val1 val2 helper val1 val2 helper]
            mv.visitInsn(Opcodes.DUP_X2);
            compileContext.addStackCount(1);
            // [... val1 val2 helper val1 val2 helper ==> ... val1 val2 helper val1 val2]
            mv.visitInsn(Opcodes.POP);
            compileContext.addStackCount(-1);
        } else {
            // we can just swap the two values
            // [... val val helper ==> ... val helper val]
            mv.visitInsn(Opcodes.SWAP);
        }
        mv.visitFieldInsn(Opcodes.PUTFIELD, rule.getHelperImplementationClassName(), ivarName, type.getInternalName(true, true));

        // the call will remove 3 from the stack height
        compileContext.addStackCount(-1 - size);

        // ok, the stack height should be as it was
        if (compileContext.getStackCount() != currentStack) {
            throw new CompileException("variable.compileAssignment : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack);
        }
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(name);
    }

    private String name;

    private Binding binding;
}
