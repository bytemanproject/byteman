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
import org.jboss.byteman.rule.binding.Bindings;
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
    /**
     * constructor for param bindings or special bindings
     * @param rule the rule for this expression
     * @param type the type for this expression
     * @param token the token for this expression
     * @param index the type of DollarExpression this is
     */
    public DollarExpression(Rule rule, Type type, ParseNode token, int index)
    {
        super(rule, type, token);
        if (index == HELPER_IDX) {
            name = "$$";
        } else if (index == RETURN_VALUE_IDX){
            name = "$!";
        } else if (index == THROWABLE_VALUE_IDX){
            name = "$^";
        } else if (index == PARAM_COUNT_IDX){
            name = "$#";
        } else if (index == PARAM_ARRAY_IDX){
            name = "$*";
        } else if (index == INVOKE_PARAM_ARRAY_IDX){
            name = "$@";
        } else if (index == TRIGGER_CLASS_IDX){
            name = "$CLASS";
        } else if (index == TRIGGER_METHOD_IDX){
            name = "$METHOD";
        } else {
            name = "$" + Integer.toString(index);
        }
        this.index = index;
        this.binding = null;
    }

    /**
     * constructor for local var bindings 
     * @param rule the rule for this expression
     * @param type the type for this expression
     * @param token the token for this expression
     * @param name the name of the local var referenced by this DollarVariable
     */
    public DollarExpression(Rule rule, Type type, ParseNode token, String name)
    {
        super(rule, type, token);
        this.index = LOCAL_IDX;
        this.name = "$" + name;
        this.binding = null;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @throws TypeException if any variable is missing or has the wrong type
     */

    public void  bind() throws TypeException
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
        if (name.equals("$0") || name.equals("$this")){
            throw new TypeException("invalid assignment to final variable " + name + getPos());
        }
        if (name.equals("$^")){
            // TODO -- see if it is possible to allow update to the throwable variable
            throw new TypeException("invalid assignment to throwable variable " + name + getPos());
        }
        if (name.equals("$#")){
            throw new TypeException("invalid assignment to param count variable " + name + getPos());
        }
        if (name.equals("$*")){
            throw new TypeException("invalid assignment to param array variable " + name + getPos());
        }
        if (name.equals("$@")){
            throw new TypeException("invalid assignment to invoke param array variable " + name + getPos());
        }
        if (name.equals("$CLASS")){
            throw new TypeException("invalid assignment to invoke param array variable " + name + getPos());
        }
        if (name.equals("$METHOD")){
            throw new TypeException("invalid assignment to invoke param array variable " + name + getPos());
        }
        bind(true);
    }

    public void bind(boolean isUpdateable) throws TypeException
    {
        // ensure that there is a binding in the bindings set for this parameter
        // we will type check the binding later

        Bindings bindings = getBindings();

        binding = bindings.lookup(name);

        if (binding == null) {
            binding = new Binding(rule, name, null);
            bindings.append(binding);
        }
        
        if (isUpdateable) {
            binding.setUpdated();
        }
    }

    public Type typeCheck(Type expected) throws TypeException {

       typeCheckAny();

        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("DollarExpression.typeCheck : invalid expected type " + expected.getName() + " for bound parameter " + name + getPos());            
        }
        return type;
    }

    public Type typeCheckAssign(Type expected) throws TypeException {

        typeCheckAny();

        if (Type.dereference(expected).isDefined() && !type.isAssignableFrom(expected)) {
            throw new TypeException("DollarExpression.typeCheck : invalid value type " + expected.getName() + " for assignment to bound parameter " + name + getPos());
        }
        return type;
    }

    private void typeCheckAny()
    {
        // if the associated binding is an alias then dereference it

        if (binding.isAlias()) {
            binding = binding.getAlias();
        }

        type = binding.getType();
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException
    {
        return helper.getBinding(binding.getName());
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        String targetName = binding.getName();

        int currentStack = compileContext.getStackCount();
        int expected = (type.getNBytes() > 4 ? 2 : 1);

        if (index == HELPER_IDX) {
            // reference to the current helper so just stack this
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            compileContext.addStackCount(1);
        } else {
            // plant a getfield
            Type type = binding.getType();
            if (rule.requiresAccess(type)) {
                // leave inaccessible types as plain objects
                // either to be consumed via reflection or
                // cast to an accessible type by the consumer
                type = Type.OBJECT;
            }
            String iVarName = binding.getIVarName();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, rule.getHelperImplementationClassName(), iVarName, type.getInternalName(true, true));
            compileContext.addStackCount(expected);
        }

        // ensure we have only increased the stack by the return value size
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("DollarExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expected));
        }
    }

    @Override
    public Object interpretAssign(HelperAdapter helperAdapter, Object value) throws ExecuteException
    {
        helperAdapter.setBinding(binding.getName(), value);
        return value;
    }

    @Override
    public void compileAssign(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        String ivarName = binding.getIVarName();

        int currentStack = compileContext.getStackCount();
        int size = ((type.getNBytes() > 4) ? 2 : 1);

        if (index == HELPER_IDX) {
            // not allowed to reassign the helper binding
            throw new CompileException("DollarExpression.compileAssign : invalid assignment to helper binding $$");
        } else {
            // plant a putfield but leave a copy of the original value on the stack
            // value to be assigned is TOS and will already be coerced to the correct value type
            // copy it so we leave it as a a return value on the stack
            if (size == 2) {
                mv.visitInsn(Opcodes.DUP2);
            } else {
                mv.visitInsn(Opcodes.DUP);
            }
            compileContext.addStackCount(size);
            // stack the current helper then insert it below the value
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            compileContext.addStackCount(1);
            if (size == 2) {
                // use a DUP_X2 to push a copy below the value then pop the redundant value
                mv.visitInsn(Opcodes.DUP_X2);
                compileContext.addStackCount(1);
                mv.visitInsn(Opcodes.POP);
                compileContext.addStackCount(-1);
            } else {
                // we can just swap the two values
                mv.visitInsn(Opcodes.SWAP);
            }
            Type type = this.type;
            if (rule.requiresAccess(type)) {
                type = Type.OBJECT;
            }
            mv.visitFieldInsn(Opcodes.PUTFIELD, rule.getHelperImplementationClassName(), ivarName, type.getInternalName(true, true));
            compileContext.addStackCount(-1 - size);

            // ok, the stack height should be as it was
            if (compileContext.getStackCount() != currentStack) {
                throw new CompileException("variable.compileAssignment : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack);
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

    private Binding binding;

    /**
     * index of $$ variable which is bound to the current helper instance
     */
    public final static int HELPER_IDX = -1;
    /**
     * index for any variable introduced in a BINDS clause
     */
    public final static int BIND_IDX = -2;
    /**
     * index for any local variable which must be further identified via its name
     */
    public final static int LOCAL_IDX = -3;
    /**
     * index of $! variable which is bound to the current return value on stack in AT RETURN rule
     */
    public final static int RETURN_VALUE_IDX = -4;
    /**
     * index of $@ variable which is bound to the current throwable on stack in AT THROW rule
     */
    public final static int THROWABLE_VALUE_IDX = -5;
    /**
     * index of $# variable which is bound to the count of number of trigger method params
     */
    public final static int PARAM_COUNT_IDX = -6;
    /**
     * index of $* variable which is bound to an array of the trigger method params
     */
    public final static int PARAM_ARRAY_IDX = -7;
    /**
     * index of $@ variable which is bound to an array of the invoked method params in an AT INVOKE rule
     */
    public final static int INVOKE_PARAM_ARRAY_IDX = -8;
    /**
     * index of $CLASS variable which is bound to a String identifying the package-qualified trigger class
     */
    public final static int TRIGGER_CLASS_IDX = -9;
    /**
     * index of $METHOD variable which is bound to a String identifying the trigger method and signature
     */
    public final static int TRIGGER_METHOD_IDX = -10;
}
