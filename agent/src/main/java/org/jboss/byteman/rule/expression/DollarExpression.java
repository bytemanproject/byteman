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
     * @param rule
     * @param type
     * @param token
     * @param index
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
        } else {
            name = "$" + Integer.toString(index);
        }
        this.index = index;
        this.binding = null;
    }

    /**
     * constructor for local var bindings 
     * @param rule
     * @param type
     * @param token
     * @param name
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

     * @return true if all variables in this expression are bound and no type mismatches have
     * been detected during inference/validation.
     */

    public void  bind() throws TypeException
    {
        bind(false);
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list. infer/validate the type of this expression or its subexpressions
     * where possible

     * @return true if all variables in this expression are bound and non-final and no type mismatches have
     * been detected during inference/validation.
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
        // if the associated binding is an alias then dereference it

        if (binding.isAlias()) {
            binding = binding.getAlias();
        }

        type = binding.getType();
        
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("DollarExpression.typeCheck : invalid expected type " + expected.getName() + " for bound parameter " + name + getPos());            
        }
        return type;
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
            // stack the current helper
            // stack the name for the variable
            // call the getBinding method
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitLdcInsn(targetName);
            compileContext.addStackCount(2);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.internalName(HelperAdapter.class), "getBinding", "(Ljava/lang/String;)Ljava/lang/Object;");
            compileContext.addStackCount(-1);
            // perform any necessary type conversion
            if (type.isPrimitive()) {
                // cast down to the boxed type then do an unbox
                Type boxType = Type.boxType(type);
                compileObjectConversion(Type.OBJECT, boxType, mv, compileContext);
                compileUnbox(boxType, type,  mv, compileContext);
            } else {
                // cast down to the required type
                compileObjectConversion(Type.OBJECT, type, mv, compileContext);
            }
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

        String targetName = binding.getName();

        int currentStack = compileContext.getStackCount();
        int size = ((type.getNBytes() > 4) ? 2 : 1);

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
            mv.visitLdcInsn(targetName);
            if (size == 2) {
                // use a DUP_X2 to push a copy below the value then pop the redundant value
                mv.visitInsn(Opcodes.DUP_X2);
                // this is the high water mark
                // at this point the stack has gone from [ .. val1 val2]  to [.. val1 val2 helper name val1 val2 name]
                compileContext.addStackCount(5);
                mv.visitInsn(Opcodes.POP);
                compileContext.addStackCount(-1);
            } else {
                // this is the high water mark
                // at this point the stack has gone from [ .. val]  to [.. val helper val name]
                compileContext.addStackCount(3);
                // we can just swap the two values
                mv.visitInsn(Opcodes.SWAP);
            }
            // ensure we have an object
            compileObjectConversion(type, Type.OBJECT, mv, compileContext);

            // call the setBinding method
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.internalName(HelperAdapter.class), "setBinding", "(Ljava/lang/String;Ljava/lang/Object;)V");

            // the call will remove 3 from the stack height
            compileContext.addStackCount(-3);

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
}
