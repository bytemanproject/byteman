/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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
package org.jboss.byteman.agent.adapter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;
import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.binding.Binding;
import org.jboss.byteman.agent.adapter.cfg.*;
import org.jboss.byteman.agent.Transformer;
import org.jboss.byteman.agent.RuleScript;
import org.jboss.byteman.agent.TransformContext;

import java.util.*;
import java.io.PrintStream;

/**
 * class which provides base functionality extended by all the location-specific method trigger adapters
 */
public class RuleTriggerMethodAdapter extends RuleGeneratorAdapter
{
    RuleTriggerMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
    {
        super(mv, transformContext, access, name, descriptor, transformContext.lookupRule(name, descriptor));
        this.signature = signature;
        this.exceptions = exceptions;
        this.callArrayBindings = new ArrayList<Binding>();
        this.returnBindingType = Type.getReturnType(descriptor);
        this.argumentTypes = Type.getArgumentTypes(descriptor);
        this.argLocalIndices = new int[argumentTypes.length];
        this.bindReturnOrThrowableValue = false;
        this.bindInvokeParams = false;
        this.bindingIndicesSet =  false;
    }

    /**
     * method overridden by AT INVOKE method adapter allowing types for the invoked method owner,
     * parameters and return value to be identified. this default version should never get invoked
     * @return an array containing the types of the invoked method owner, parameters and return value
     */
    public Type[] getInvokedTypes()    {
        throw new RuntimeException("RuleTriggerMethodAdapter.getInvokedTypes() : should never get called!");
    }

    /**
     * method overridden by AT INVOKE method adapter allowing the type of the $! binding to be identified.
     * this default version should only get invoked for an AT EXIT rule where it returns the trigger method
     * return type. the overridden version should only get invoked for an AFTER INVOKE rule where it returns the
     * invoked method return type
     * @return the appropriate return type
     */
    public Type getReturnBindingType()
    {
        return returnBindingType;
    }

    private void setBindingIndices()
    {
        if (bindingIndicesSet) {
            return;
        }

        Bindings bindings = rule.getBindings();
        Iterator<Binding> iterator = bindings.iterator();
        List<Binding> aliasBindings = new ArrayList<Binding>();
        int argLocalIndex = 0;

        // if this is an instance method then the local 0 holds this and args start at local index 1

        if ((access & Opcodes.ACC_STATIC) == 0) {
            argLocalIndex += 1;
        }

        // compute the local indices of each method parameter

        for (int i = 0; i < argumentTypes.length; i++) {
            argLocalIndices[i] = argLocalIndex;
            argLocalIndex += argumentTypes[i].getSize();
        }

        // check the local var bindings and, if any of them are actually refereces to method params
        // alias them to the corresponding param binding

        while (iterator.hasNext()) {
            Binding binding = iterator.next();
            if  (binding.isLocalVar()){
                int localIdx = binding.getLocalIndex();
                if (localIdx < argLocalIndex) {
                    binding = alias(binding, bindings, localIdx);
                    if (binding != null) {
                        // the aliased param binding was not present so ensure it gets added to the
                        // binding set once we have finished iterating. there is no need to add it
                        // to the call bindings since we pass the aliased method param instead
                        aliasBindings.add(binding);
                    }
                } else {
                    callArrayBindings.add(binding);
                }
            }
        }

        bindings.addBindings(aliasBindings);
        
        // now iterate over the param vars and ensure they go into the call bindings list

        iterator = bindings.iterator();
        
        while (iterator.hasNext()) {
            Binding binding = iterator.next();
            if (binding.isParam()) {
                callArrayBindings.add(binding);
            } else if (binding.isReturn()) {
                // in order to be able to add the return value to the args array
                // we have to add a local var to store the value so track that requirement
                bindReturnOrThrowableValue = true;
                saveValueType = getReturnBindingType();
                binding.setDescriptor(saveValueType.getClassName());
                callArrayBindings.add(binding);
            } else if (binding.isThrowable()) {
                // in order to be able to add the return value or throwable value to the args array
                // we have to add a local var to store the value so track that requirement
                bindReturnOrThrowableValue = true;
                // TODO -- allow type to be more accurately identified than this
                saveValueType = Type.getType("Ljava/lang/Throwable;");
                callArrayBindings.add(binding);
            } else if (binding.isParamCount() || binding.isParamArray()) {
                callArrayBindings.add(binding);
            } else if (binding.isInvokeParamArray()) {
                // in order to be able to ad th einvoke parameters to the args array we
                // have to add a local var to store the value so track that requirement
                callArrayBindings.add(binding);
                bindInvokeParams = true;
            }
        }
        // we don't have to do this but it makes debugging easier
        
        if (true) {
            // ok now sort the callArrayBindings for later use
            // we sort param bindings before local bindings before the param count
            // and param array before the throwable or return binding
            // within param bindings we sort by param idx
            // within local bindings we sort by local var idx

            Comparator<Binding> comparator = new Comparator<Binding>() {
                public int compare(Binding b1, Binding b2)
                {
                    if (b1.isParam()) {
                        if (b2.isParam()) {
                            // sort by param index
                            int i1 = b1.getIndex();
                            int i2 = b2.getIndex();
                            return (i1 < i2 ? -1 : (i1 > i2 ? 1 : 0));
                        } else {
                            // param bindings precede all other types
                            return -1;
                        }
                    } else if (b1.isLocalVar()){
                        if (b2.isParam()) {
                            // param bindings precede all other types
                            return 1;
                        } else if (b2.isLocalVar()) {
                            // sort by local index
                            int i1 = b1.getLocalIndex();
                            int i2 = b2.getLocalIndex();
                            return (i1 < i2 ? -1 : (i1 > i2 ? 1 : 0));
                        } else {
                            // local vars precede all remaining types
                            return -1;
                        }
                    } else if (b1.isParamCount()){
                        if (b2.isParam() || b2.isLocalVar()) {
                            // param and local bindings precede param count
                            return 1;
                        } else if (b2.isParamCount()) {
                            return 0;
                        } else {
                            // param count vars precede all remaining types
                            return -1;
                        }
                    } else if (b1.isParamArray()){
                        if (b2.isParam() || b2.isLocalVar() || b2.isParamCount()) {
                            // param, local and param count bindings precede param array
                            return 1;
                        } else if (b2.isParamArray()) {
                            return 0;
                        } else {
                            // param array vars precede all remaining types
                            return -1;
                        }
                    } else if (b1.isInvokeParamArray()){
                        if (b2.isParam() || b2.isLocalVar() || b2.isParamCount() || b2.isParamArray()) {
                            // param, local and param count and param array bindings precede invoke param array
                            return 1;
                        } else if (b2.isInvokeParamArray()) {
                            return 0;
                        } else {
                            // invoke param array vars precede all remaining types
                            return -1;
                        }
                    } else {
                        // return var always sorts last
                        return 1;
                    }
                }
            };

            Collections.sort(callArrayBindings, comparator);
        }

        // now give each binding a unique index in the object array
        int n = callArrayBindings.size();
        for (int i = 0; i < n; i++) {
            callArrayBindings.get(i).setCallArrayIndex(i);
        }

        bindingIndicesSet = true;
    }

    private Binding alias(Binding binding, Bindings bindings, int localIdx)
    {
        if (((access & Opcodes.ACC_STATIC) == 0) && (localIdx == 0)) {
            String name = "$0";
            Binding alias = bindings.lookup(name);
            if (alias == null) {
                alias = new Binding(rule, name);
                alias.setDescriptor(binding.getDescriptor());
                alias.setLocalIndex(binding.getLocalIndex());
                binding.aliasTo(alias);
                // alias var was new so return it for addition to the binding set
                return alias;
            } else {
                // just alias the binding
                binding.aliasTo(alias);
                return null;
            }
        }

        for (int i = 0; i < argLocalIndices.length; i++) {
            if (argLocalIndices[i] == localIdx) {
                String name = "$" + (i + 1);
                Binding alias = bindings.lookup(name);
                if (alias == null) {
                    alias = new Binding(rule, name);
                    alias.setDescriptor(binding.getDescriptor());
                    alias.setLocalIndex(binding.getLocalIndex());
                    binding.aliasTo(alias);
                    // alias var was new so return it for addition to the binding set
                    return alias;
                } else {
                    // just alias the binding
                    binding.aliasTo(alias);
                    return null;
                }
            }
        }

        return null;
    }

    private int doReturnOrThrowSave()
    {
        // allocate a slot for the return or throwable on top of the stack
        // stash the value into the slot and return the slot idx

        int saveValueSlot = newLocal(saveValueType);
        if (saveValueType.getSize() == 2) {
            visitInsn(Opcodes.DUP2);
        } else {
            visitInsn(Opcodes.DUP);
        }
        storeLocal(saveValueSlot);

        return saveValueSlot;
    }

    private int doInvokeBindingsSave()
    {
        // allocate a slot for the invoke parameters array,
        // construct the array
        // stash it into the slot and return the slot idx

        Type objectType = Type.getType(Object.class);
        Type objectArrayType = Type.getType("[Ljava/lang/Object;");
        Type[] invokeParamTypes = getInvokedTypes();
        int savedValueCount = invokeParamTypes.length;

        // create the array and save it in a local var slot

        int arrayValueSlot = newLocal(objectArrayType);
        push(savedValueCount);
        newArray(objectType);
        storeLocal(arrayValueSlot);

        // pop the arguments off the stack into the invoke parameters array
        // n.b. the top one is the last parameter

        for (int i = savedValueCount - 1; i >= 0; i--) {
            Type type = invokeParamTypes[i];
            if (type != null) {
                // convert value to object if needed
                box(type);
                // load array and  swap under value
                loadLocal(arrayValueSlot);
                swap(objectArrayType, objectType);
                // load index and swap under value
                push(i);
                swap(Type.INT_TYPE, objectType);
            } else {
                // this is a static method and index is 0 so we install null in the array
                // load array index and then null
                loadLocal(arrayValueSlot);
                push(i);
                push((Type)null);
            }
            // store the value in the array as an object
            arrayStore(objectType);
        }

        // now restore the arguments from the array in increasing order

        for (int i = 0; i < savedValueCount; i++) {
            Type type = invokeParamTypes[i];
            if (type != null) {
                // load the array, retrieve the object and unbox if needed
                loadLocal(arrayValueSlot);
                push(i);
                arrayLoad(objectType);
                //if (type.getSort() != Type.OBJECT && type.getSort() != Type.ARRAY) {
                    unbox(type);
                //}
            } else {
                // this is a static method so no target instance to stack
            }
        }

        return arrayValueSlot;
    }

    /**
     * stack an argument array containing all the values which need to be bound to parameters or local variables
     * in the rule or a null pointer if no bindings are required. this method also inserts a copy of the array
     * below the initial two top entries if any of the bindings can potentially be updated by the rule, allowing
     * the updated values to be written back on return from the call to the rule engine.
     * @param saveSlot a local variable slot containing either the return value which the trigger method is about
     * to return or the Throwable the method is about to throw. this is only valid if, respectively, the rule
     * location is AT EXIT/AFTER INVOKE or AT THROW and the rule body contains a reference to the return value ($!)
     * or throwable value ($@).
     * @return true if the rule may update the argument array and hence if a copy of the array has been inserted
     * below the initial two top entries otherwise false.
     */
    private boolean doArgLoad(int saveSlot)
    {
        if (callArrayBindings.size() ==  0) {
            push((org.objectweb.asm.Type)null);
            return false;
        }

        // create the array used to pass the bindings

        int arraySize = callArrayBindings.size();

        push(arraySize);
        Type objectType = Type.getType(Object.class);
        newArray(objectType);

        // check if any of the bindings gets updated. if so we need to stash a copy of the bindings array
        // below the key and owner so we can pull out the updated values and update local var/param slots
        // once the call ahs completed

        boolean doUpdates = false;

        for (int i = 0; i < arraySize; i++) {
            Binding binding = callArrayBindings.get(i);
            if (binding.isUpdated()) {
                doUpdates = true;
                break;
            }
        }

        if (doUpdates) {
            // insert copy of array below first two call arguments
            // [.. key owner bindings ] ==> [.. bindings key owner bindings ]
            mv.visitInsn(Opcodes.DUP_X2);
        }

        // now install required values into bindings array
        for (int i = 0; i < arraySize; i++) {
            Binding binding = callArrayBindings.get(i);
            dup();
            push(i);
            if (binding.isParam()) {
                int idx = binding.getIndex() - 1;
                loadArg(idx);
                box(argumentTypes[idx]);
            } else if (binding.isLocalVar()){
                int idx = binding.getLocalIndex();
                loadLocal(idx);
                box(getLocalType(idx));
            } else if (binding.isParamCount()){
                int count = argumentTypes.length;
                push(count);
                box(Type.INT_TYPE);
            } else if (binding.isParamArray()){
                int count = argumentTypes.length;
                push(count + 1);
                newArray(objectType);
                dup();
                push(0);
                if ((access & Opcodes.ACC_STATIC) == 0) {
                    loadThis();
                } else {
                    push((Type)null);
                }
                arrayStore(objectType);
                for (int idx = 0; idx < count; idx++) {
                    dup();
                    push(idx + 1);
                    loadArg(idx);
                    box(argumentTypes[idx]);
                    arrayStore(objectType);
                }
            } else if (binding.isInvokeParamArray()){
                loadLocal(saveSlot);
            } else if (binding.isThrowable() | binding.isReturn()){
                loadLocal(saveSlot);
                box(saveValueType);
            }
            arrayStore(objectType);
        }

        return doUpdates;
    }

    /**
     * plant code to copy back any updated values from the argument array to the relevant local variable slots
     */
    private void doArgUpdate()
    {
        // at entry the top of the stack contains the object array
        // for an AT EXIT rule the entry below this is the return value

        Type objectType = Type.getType(Object.class);
        int arraySize = callArrayBindings.size();
        int lastUpdated = -1;
        int returnIdx = -1;


        // identify which is the last index we will need to update. n.b. if we find a return value we will
        // update that last

        for (int i = 0; i < arraySize; i++) {
            Binding binding = callArrayBindings.get(i);
            if (binding.isUpdated()) {
                lastUpdated = i;
                if (binding.isReturn()) {
                    returnIdx = i;
                }
            }
        }
        
        // if the return value is updated it gets done last
        if (returnIdx >= 0) {
            lastUpdated = returnIdx;
        }

        // write back all other args then stack new return value and drop old one
        for (int i = 0; i < arraySize; i++) {
            Binding binding = callArrayBindings.get(i);
            if (binding.isUpdated() && !binding.isReturn()) {
                // if this is the last update then we consume the arguments array
                // otherwise we need to copy it
                if (i != lastUpdated) {
                    dup();
                }
                push(i);
                arrayLoad(objectType);
                if (binding.isParam()) {
                    int idx = binding.getIndex() - 1;
                    unbox(argumentTypes[idx]);
                    storeArg(idx);
                } else if (binding.isLocalVar()) {
                    int idx = binding.getLocalIndex();
                    unbox(getLocalType(idx));
                    storeLocal(idx);
                }
            }
        }

        // if we had a return value to process then the args array will still be on top of the stack above
        // the old return value

        if (returnIdx >= 0) {
            // get rid of old return value remembering to use the correct type for $!
            // which varies depending if this is an AT EXIT or AFTER CALL rule
            if (saveValueType.getSize() == 2) {
                mv.visitInsn(Opcodes.DUP_X2);
                mv.visitInsn(Opcodes.POP);
                mv.visitInsn(Opcodes.POP2);
            } else {
                mv.visitInsn(Opcodes.SWAP);
                mv.visitInsn(Opcodes.POP);
            }

            push(returnIdx);
            arrayLoad(objectType);
            // use the correct type for $! which varies depending if this is an AT EXIT or AFTER CALL rule
            unbox(saveValueType);
        }
    }
    /**
     * return true if the current block is a handler which catches a byteman exception thrown by the byteman runtime
     * @return
     */
    protected boolean inBytemanHandler()
    {
        return cfg.inBytemanHandler();
    }

    /**
     * return true if the current block is in a trigger block injected by Byteman
     * @return
     */
    protected boolean inBytemanTrigger()
    {
        return cfg.inBytemanTrigger();
    }
    /**
     * return true if the current block is handler which catches a thrown exception within the scope
     * of a monitor enter in order to be able exit the monitor and rethrow the exception
     * @return
     */
    protected boolean inRethrowHandler()
    {
        return cfg.inRethrowHandler();
    }

    protected String getMethodName()
    {
        return name + TypeHelper.internalizeDescriptor(descriptor);
    }

    private RuleScript ruleScript;
    private String signature;
    protected String[] exceptions;
    private Type[] argumentTypes;
    private Type saveValueType;
    private int[] argLocalIndices;
    private List<Binding> callArrayBindings;
    private boolean bindReturnOrThrowableValue;
    private boolean bindInvokeParams;
    private boolean bindingIndicesSet;
    private Type returnBindingType;

    private CFG cfg;

    // methdos copied from GeneratorAdapter but modified so they invoke local MethodVisitor
    // method implementations rather than delegating to the next MethodVisitor in line


    // overridden methods from MethodVisitor

    @Override
    public void visitCode()
    {
        super.visitCode();
        // create a control flow graph for the method
        String methodName = getTriggerClassName() + "." + this.name + this.descriptor;
        Label newStart = super.newLabel();
        this.cfg = new CFG(methodName, newStart);
        visitLabel(newStart);
    }

    @Override
    public void visitInsn(int opcode)
    {
        super.visitInsn(opcode);
        // look for interesting instructions which need inserting into the CFG
        switch(opcode)
        {
            case Opcodes.IRETURN:
            case Opcodes.LRETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.ARETURN:
            case Opcodes.RETURN:
            case Opcodes.ATHROW:
            {
                // add this instruction to the current block and then start a new current block
                cfg.add(opcode);
                Label newStart = super.newLabel();
                // must call split before visiting the label
                cfg.split(newStart);
                visitLabel(newStart);
            }
            break;
            case Opcodes.MONITORENTER:
            case Opcodes.MONITOREXIT:
            {
                // just add this instruction to the current block
                cfg.add(opcode);
            }
            break;
            default:
            {
                cfg.add(opcode);
            }
        }
    }

    @Override
    public void visitIincInsn(int var, int increment)
    {
        super.visitIincInsn(var, increment);
        cfg.add(Opcodes.IINC, var, increment);
    }

    @Override
    public void visitIntInsn(int opcode, int operand)
    {
        super.visitIntInsn(opcode, operand);
        cfg.add(opcode, operand);
    }

    @Override
    public void visitLdcInsn(Object cst)
    {
        super.visitLdcInsn(cst);
        cfg.add(Opcodes.LDC, cst.toString());
    }

    @Override
    public void visitVarInsn(int opcode, int var)
    {
        super.visitVarInsn(opcode, var);
        cfg.add(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String desc)
    {
        super.visitTypeInsn(opcode, desc);
        cfg.add(opcode, desc);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc)
    {
        super.visitFieldInsn(opcode, owner, name, desc);
        cfg.add(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc)
    {
        super.visitMethodInsn(opcode, owner, name, desc);
        cfg.add(opcode, owner, name, desc);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label)
    {
        super.visitJumpInsn(opcode, label);
        switch (opcode)
        {
            case Opcodes.IFEQ:
            case Opcodes.IFNE:
            case Opcodes.IFLT:
            case Opcodes.IFGE:
            case Opcodes.IFGT:
            case Opcodes.IFLE:
            case Opcodes.IF_ICMPEQ:
            case Opcodes.IF_ICMPNE:
            case Opcodes.IF_ICMPLT:
            case Opcodes.IF_ICMPGE:
            case Opcodes.IF_ICMPGT:
            case Opcodes.IF_ICMPLE:
            case Opcodes.IF_ACMPEQ:
            case Opcodes.IF_ACMPNE:
            {
                // create a new current block and add the label supplied in the call as the
                // first out of the old current block and the label of the new current block as
                // the second out
                cfg.add(opcode);
                Label newStart = super.newLabel();
                // must call split before visiting the label
                cfg.split(newStart, label, newStart);
                visitLabel(newStart);
            }
            break;
            case Opcodes.GOTO:
            {
                // create a new current block and  add the label supplied in the call as the
                // first out of the old current block
                cfg.add(opcode);
                Label newStart = super.newLabel();
                // must call split before visiting the label
                cfg.split(newStart, label);
                visitLabel(newStart);
            }
            break;
            case Opcodes.JSR:
            {
                // create a new current block and add the label supplied in the call as the first out
                // of the current block -- the new current block is a potential return point from the
                // JSR but we cannot represent that statically
                cfg.add(opcode);
                Label newStart = super.newLabel();
                // must call split before visiting the label
                cfg.split(newStart, label, newStart);
                visitLabel(newStart);
            }
            break;
            case Opcodes.IFNULL:
            case Opcodes.IFNONNULL:
            {
                // create a new current block and add the label supplied in the call as the
                // first out of the old current block and the label of the new current block as
                // the second out
                cfg.add(opcode);
                Label newStart = super.newLabel();
                // must call split before visiting the label
                cfg.split(newStart, label, newStart);
                visitLabel(newStart);
            }
            break;
        }
    }

    @Override
    public void visitLabel(Label label)
    {
        super.visitLabel(label);

        // tell the CFG to visit this label

        cfg.visitLabel(label);

        // if this is a try catch block end then we need to visit the try catch block now
        // this ensures that try catch blocks which enclose trigger blocks are listed after the
        //try catch blocks

        if (cfg.tryCatchEnd(label)) {
            for (TryCatchDetails details : cfg.tryCatchEndDetails(label)) {
                super.visitTryCatchBlock(details.getStart(), details.getEnd(), details.getHandler(), details.getType());
            }
        }
    }

    public void visitTriggerStart(Label label)
    {
        // tell the CFG to visit this label

        cfg.visitTriggerStart(label);

        visitLabel(label);
    }

    public void visitTriggerEnd(Label label)
    {
        // tell the CFG to visit this label

        cfg.visitTriggerEnd(label);

        visitLabel(label);

        // ok, update the trigger details with labels for the handler blocks we are going to generate

        TriggerDetails details = cfg.triggerEndDetails(label);

        // we always add an execute handler
        // we don't need a return or throw handler if we are in the scope of a monitor enter/exit pair
        // all the handler regions end here so we use the same end label for all of them
        Label end = new Label();
        Label executeHandler = newLabel();

        if (!cfg.inOpenMonitor()) {
            // ok this one needs the full set of handlers and the subtype handlers need to come first
            Label returnHandler = newLabel();
            details.setEarlyReturnHandler(returnHandler);
            visitTryCatchBlock(details.getStart(), end, returnHandler, CFG.EARLY_RETURN_EXCEPTION_TYPE_NAME);
            Label throwHandler = newLabel();
            details.setThrowHandler(throwHandler);
            visitTryCatchBlock(details.getStart(), end, throwHandler, CFG.THROW_EXCEPTION_TYPE_NAME);
        }

        details.setExecuteHandler(executeHandler);
        visitTryCatchBlock(details.getStart(), end, executeHandler, CFG.EXECUTE_EXCEPTION_TYPE_NAME);

        // ok this fixes the  handler end label at the same pont as the trigger end label
        
        visitLabel(end);
    }

    /*
    @Override
    public void visitLdcInsn(Object cst) {
        super.visitLdcInsn(cst);
    }
    @Override
    public void visitIincInsn(int var, int increment) {
        super.visitIincInsn(var, increment);
    }
    */

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels)
    {
        super.visitTableSwitchInsn(min, max, dflt, labels);
        cfg.add(Opcodes.TABLESWITCH, min, max);
        // create a new current block and then add the default lable and each of the switch labels as an
        // outgoing path from the current block
        Label newStart = super.newLabel();
        // must call split before visiting the label
        cfg.split(newStart, dflt, labels);
        visitLabel(newStart);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
    {
        super.visitLookupSwitchInsn(dflt, keys, labels);
        cfg.add(Opcodes.TABLESWITCH, keys);
        // create a new current block and then add the default lable and each of the switch labels as an
        // outgoing path from the current block
        Label newStart = super.newLabel();
        // must call split before visiting the label
        cfg.split(newStart, dflt, labels);
        visitLabel(newStart);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims)
    {
        super.visitMultiANewArrayInsn(desc, dims);
        cfg.add(Opcodes.MULTIANEWARRAY, desc, dims);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
    {
        // don't forward details to the next visitor in line until we reach the end label
        //this ensures we can insert trigger catch blocks in between the existing handlers
        // super.visitTryCatchBlock(start, end, handler, type);

        // now tell the cfg to visit this block

        cfg.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals)
    {
        Type returnType =  Type.getReturnType(descriptor);

        // check whether there are outstanding monitor opens at the start of the trigger
        // block and, if so, insert a handler which unlocks the monitor and then rethrows
        // the exception.

        Iterator<TriggerDetails> iterator = cfg.triggerDetails();
        boolean noneLeft = true;

        while (iterator.hasNext()) {
            TriggerDetails details = iterator.next();
            // see if this trigger has any open monitor enters which will need closing
            Iterator<CodeLocation> openEnters = cfg.getOpenMonitors(details);
            if (openEnters.hasNext()) {
                // add a handler here which unlocks each object and rethrows the
                // saved exception then protect it with a try catch block and update
                // the trigger details so that it is the target of this block

                // generate a rethrow handler for each exception type

                Label newStart = newLabel();
                Label newEnd = newLabel();

                // if we get here the return and throw handlers labels should be null
                if (details.getEarlyReturnHandler() != null || details.getThrowHandler() != null) {
                    System.out.println("unexpected : trigger region with open monitorenters has subtype handler!");
                }

                // generate rethrow code and mark the handler as a try catch block for all
                // three exception types
                newStart = newLabel();
                newEnd = newLabel();
                Label newEarlyReturn = newLabel();
                Label newThrow = newLabel();
                visitLabel(details.getExecuteHandler());
                Label newExecute = newLabel();
                visitLabel(newStart);
                while (openEnters.hasNext()) {
                    CodeLocation enterLocation = openEnters.next();
                    int varIdx = cfg.getSavedMonitorIdx(enterLocation);
                    // call super method to avoid indexing these instructions
                    visitVarInsn(Opcodes.ALOAD, varIdx);
                    visitInsn(Opcodes.MONITOREXIT);
                }
                visitInsn(Opcodes.ATHROW);
                // add try catch blocks for the 3 exception types
                visitTryCatchBlock(newStart, newEnd, newEarlyReturn, CFG.EARLY_RETURN_EXCEPTION_TYPE_NAME);
                visitTryCatchBlock(newStart, newEnd, newThrow, CFG.THROW_EXCEPTION_TYPE_NAME);
                visitTryCatchBlock(newStart, newEnd, newExecute, CFG.EXECUTE_EXCEPTION_TYPE_NAME);
                // visit the end of the handler blocks so they gets processed
                visitLabel(newEnd);
                // update the details so it tracks these new ecetpion regions
                details.setThrowHandler(newThrow);
                details.setEarlyReturnHandler(newEarlyReturn);
                details.setExecuteHandler(newExecute);
            }
        }

        // ok, so now we have to add the handler code for trigger block try catch handlers
        // we only need to add the handler code once but we need to make sure it is the target of
        //  all the try catch blocks by visiting their handler label before we insert the code

        iterator = cfg.triggerDetails();

        while (iterator.hasNext()) {
            TriggerDetails details = iterator.next();
            visitLabel(details.getEarlyReturnHandler());
        }

        if (Transformer.isVerbose()) {
            getStatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
            visitLdcInsn("caught ReturnException");
            invokeVirtual(Type.getType(PrintStream.class), Method.getMethod("void println(String)"));
        }
        // add exception handling code subclass first
        if (returnType == Type.VOID_TYPE) {
            // drop exception and just return
            pop();
            visitInsn(Opcodes.RETURN);
        } else {
            // fetch value from exception, unbox if needed and return value
            Method getReturnValueMethod = Method.getMethod("Object getReturnValue()");
            invokeVirtual(CFG.EARLY_RETURN_EXCEPTION_TYPE, getReturnValueMethod);
            unbox(returnType);
            returnValue();
        }

        iterator = cfg.triggerDetails();

        while (iterator.hasNext()) {
            TriggerDetails details = iterator.next();
            visitLabel(details.getThrowHandler());
        }

        if (Transformer.isVerbose()) {
            getStatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
            visitLdcInsn("caught ThrowException");
            invokeVirtual(Type.getType(PrintStream.class), Method.getMethod("void println(String)"));
        }
        // fetch value from exception, unbox if needed and return value
        Method getThrowableMethod = Method.getMethod("Throwable getThrowable()");
        invokeVirtual(CFG.THROW_EXCEPTION_TYPE, getThrowableMethod);
        throwException();

        // execute exception  comes last because it is the super of the othher two classes
        
        iterator = cfg.triggerDetails();

        while (iterator.hasNext()) {
            TriggerDetails details = iterator.next();
            visitLabel(details.getExecuteHandler());
        }

        if (Transformer.isVerbose()) {
            getStatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
            visitLdcInsn("caught ExecuteException");
            invokeVirtual(Type.getType(PrintStream.class), Method.getMethod("void println(String)"));
        }
        // rethrow an execute exception
        throwException();

        super.visitMaxs(maxStack, maxLocals);

        // hmm, don't think we need this
        cfg.visitMaxs();
    }

    @Override
    public void visitEnd()
    {
        super.visitEnd();
        // trash the current label
        cfg.visitEnd();
    }

    /**
         * inject the rule trigger code
     */
    protected void injectTriggerPoint()
    {
        // we need to set this here to avoid recursive re-entry into inject routine

        rule.setTypeInfo(getTriggerClassName(), access, name, descriptor, exceptions);
        String key = rule.getKey();
        Type ruleType = Type.getType(TypeHelper.externalizeType("org.jboss.byteman.rule.Rule"));
        Method method = Method.getMethod("void execute(String, Object, Object[])");
        // we are at the relevant line in the method -- so add a trigger call here
        if (Transformer.isVerbose()) {
            System.out.println("RuleTriggerMethodAdapter.injectTriggerPoint : inserting trigger into " + getTriggerClassName() + "." + getMethodName() + " for rule " + rule.getName());
        }
        Label startLabel = newLabel();
        Label endLabel = newLabel();
        visitTriggerStart(startLabel);

        // ensure binding indices have been installed
        
        setBindingIndices();

        // a local var slot to store a value for $! (AT RETURN), $^ (AT THROW) or $@ (AT INVOKE)
        // note that only one of these can appear in any given rule so we only need one slot

        int saveValueSlot;

        if (bindReturnOrThrowableValue) {
            saveValueSlot = doReturnOrThrowSave();
        } else if (bindInvokeParams) {
            saveValueSlot = doInvokeBindingsSave();
        } else {
            saveValueSlot = -1;
        }
        push(key);
        if ((access & Opcodes.ACC_STATIC) == 0) {
            loadThis();
        } else {
            push((Type)null);
        }
        boolean handleUpdates;
        handleUpdates = doArgLoad(saveValueSlot);
        // free the local slot if we need to
        if (saveValueSlot >= 0) {
            popLocal(saveValueSlot);
        }
        invokeStatic(ruleType, method);
        // if the rule can modify local variables then generate code to perform the update
        if (handleUpdates) {
            doArgUpdate();
        }
        visitTriggerEnd(endLabel);
    }
}