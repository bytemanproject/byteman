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
package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.exception.CompileException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.compiler.StackHeights;
import org.jboss.jbossts.orchestration.rule.helper.HelperAdapter;
import org.jboss.jbossts.orchestration.rule.grammar.ParseNode;
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
public class DollarExpression extends Expression
{
    public DollarExpression(Rule rule, Type type, ParseNode token, int index)
    {
        super(rule, type, token);
        if (index == -1) {
            name = "$$";
        } else {
            name = "$" + Integer.toString(index);
        }
        this.index = index;
    }

    public DollarExpression(Rule rule, Type type, ParseNode token, String name)
    {
        super(rule, type, token);
        this.index = -2;
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

        if (index == -1) {
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

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(name);
    }

    private String name;
    private int index;
}
