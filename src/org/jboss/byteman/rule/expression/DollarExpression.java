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
import org.jboss.byteman.rule.binding.Bindings;
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
 * an expression which refers either to a builtin variable or to a bound parameter of the
 * triggering method for an ECA rule. builtin variables are written as a dollar sign followed
 * by a leading alpha-underscore, trailing alpha-numeric-underscore string. bound parameters are
 * written as a dollar sign followed by a non-negativeinteger parameter index
 *
 * e.g. if the rule applies to method foo.bar(int baz, Mumble mumble) then an occurrence of $2
 * appearing as an expression in a rule would have type Mumble and evaluate to the value of mumble
 * at the point when the rule was triggered.
 *
 * At present there are no special variables but we may need to add some later
 */
public class DollarExpression extends AssignableExpression
{
    public DollarExpression(Rule rule, Type type, ParseNode token, int index)
    {
        super(rule, type, token);
        if (index == HELPER_IDX) {
            name = "$$";
        } else if (index == RETURN_VALUE_IDX){
            name = "$!";
        } else {
            name = "$" + Integer.toString(index);
        }
        this.index = index;
    }

    public DollarExpression(Rule rule, Type type, ParseNode token, String name)
    {
        super(rule, type, token);
        this.index = BINDING_IDX;
        this.name = "$" + name;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible

     * @return true if all variables in this expression are bound and no type mismatches have
     * been detected during inference/validation.
     */

    public boolean bind() {
        // ensure that there is a binding in the bindings set for this parameter
        // we will type check the binding later

        Bindings bindings = getBindings();
        Binding binding;

        binding = bindings.lookup(name);

        if (binding == null) {
            binding = new Binding(rule, name, null);
            bindings.append(binding);
        }

        return true;
    }

    public Type typeCheck(Type expected) throws TypeException {
        // ensure there is a parameter with the relevant name in the bindings
        Binding binding;
        binding = getBindings().lookup(name);

        if (binding == null) {
            throw new TypeException("DollarExpression.typeCheck : invalid bound parameter " + name + getPos());
        }
        type = binding.getType();
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("DollarExpression.typeCheck : invalid expected type " + expected.getName() + " for bound parameter " + name + getPos());            
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException
    {
        return helper.getBinding(name);
    }

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        int currentStack = currentStackHeights.stackCount;

        if (index == HELPER_IDX) {
            // reference to the current helper so just stack this
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            currentStackHeights.addStackCount(1);
            // make sure we have room for this
            int overflow = (currentStack + 1 - maxStackHeights.stackCount);
            if (overflow > 0) {
                maxStackHeights.addStackCount(overflow);
            }
        } else {
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

        if (index == HELPER_IDX) {
            // not allowed to reassign the helper binding
            throw new CompileException("DollarExpression.compileAssign : invalid assignment to helper binding $$");
        } else {
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
            } else {
                // this is the high water mark
                // at this point the stack has gone from [ .. val]  to [.. val helper val name]
                max = 2 + size;
                // we can just swap the two values
                mv.visitInsn(Opcodes.SWAP);
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
            int overflow = (currentStack + max - maxStackHeights.stackCount);
            if (overflow > 0) {
                maxStackHeights.addStackCount(overflow);
            }
        }
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(name);
    }

    private String name;
    /**
     * index is positive or zero if this is a reference to a method param and negative if this is a reference to
     * the current helper, the return value on the stack in an AT EXIT rule or a local or BIND variable
     */
    private int index;

    public final static int HELPER_IDX = -1;
    public final static int BINDING_IDX = -2;
    public final static int RETURN_VALUE_IDX = -3;
}
