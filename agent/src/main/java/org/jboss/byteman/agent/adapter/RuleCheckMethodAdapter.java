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
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.binding.Binding;
import org.jboss.byteman.agent.Transformer;
import org.jboss.byteman.agent.TransformContext;
import org.jboss.byteman.agent.LocationType;

import java.util.*;

/**
 * class which provides base functionality extended by all the location-specific method check adapters
 */
public class RuleCheckMethodAdapter extends RuleMethodAdapter {
    RuleCheckMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor)
    {
        super(mv, transformContext, access, name, descriptor, transformContext.createRule(name, descriptor));
        this.triggerPoints = null;
        this.returnBindingType = Type.parseMethodReturnType(descriptor);
    }

    protected String getReturnBindingType()
    {
        return returnBindingType;
    }

    protected void setTriggerPoint()
    {
        if (triggerPoints == null) {
            triggerPoints = new ArrayList<Label>();
        }
        Label triggerLabel = new Label();
        triggerPoints.add(triggerLabel);
        this.visitLabel(triggerLabel);
    }
    boolean isTriggerPoint()
    {
        return (triggerPoints != null);
    }

    protected void checkBindings()
    {
        if (!isTriggerPoint()) {
            transformContext.warn(name, descriptor, "no matching injection point");
            return;
        }
        
        Bindings bindings = rule.getBindings();
        Iterator<Binding> bindingIter = bindings.iterator();
        List<String> parameterTypenames = Type.parseMethodDescriptor(descriptor, true);
        int parameterCount = parameterTypenames.size() - 1; // allows for return type

        // make sure all entries are valid
        while (bindingIter.hasNext()) {
            Binding binding = bindingIter.next();
            if (binding.isRecipient()) {
                if ((access & Opcodes.ACC_STATIC) != 0) {
                    if (Transformer.isVerbose()) {
                        System.out.println("RuleCheckMethodAdapter.checkBindings : found invalid recipient binding " + binding + " checking static method " + name + descriptor);
                    }
                    transformContext.warn(name, descriptor, "found invalid recipient binding " + binding + " injecting into static method");
                }
            } else if (binding.isParam()) {
                int idx = binding.getIndex();
                if (idx > parameterCount) {
                    // parameter out of range
                    if (Transformer.isVerbose()) {
                        System.out.println("RuleCheckMethodAdapter.checkBindings : found out of range parameter binding " + binding + " checking method " + name + descriptor);
                    }
                    transformContext.warn(name, descriptor, "found out of range parameter binding " + binding);
                } else {
                    binding.setDescriptor(parameterTypenames.get(idx - 1));
                }
            } else if (binding.isReturn()) {
                // this is a valid reference in an AT EXIT rule and in an AFTER INVOKE
                // but only if the corresponding returning or called method is non-void
                LocationType locationType = rule.getTargetLocation().getLocationType();
                if (locationType == LocationType.EXIT) {
                    if ("void".equals(getReturnBindingType())) {
                        if (Transformer.isVerbose()) {
                            System.out.println("RuleCheckMethodAdapter.checkBindings : found return value binding " + binding + " checking void trigger method " + name + descriptor + " in AT EXIT rule " + rule);
                        }
                        transformContext.warn(name, descriptor, "found return value binding " + binding + " checking void trigger method in AT EXIT rule");
                    }
                } else if (locationType == LocationType.INVOKE_COMPLETED) {
                    if ("void".equals(getReturnBindingType())) {
                        if (Transformer.isVerbose()) {
                            System.out.println("RuleCheckMethodAdapter.checkBindings : found return value binding " + binding + " checking void called method in AFTER INVOKE rule  " + rule.getName());
                        }
                        transformContext.warn(name, descriptor, "found return value binding " + binding + " checking void called method in AFTER INVOKE rule");
                    }
                } else {
                    if (Transformer.isVerbose()) {
                        System.out.println("RuleCheckMethodAdapter.checkBindings : found return value binding " + binding + " in rule which is neither AT EXIT nor AFTER INVOKE " + rule.getName());
                    }
                    transformContext.warn(name, descriptor, "found return value binding " + binding + " in rule which is neither AT EXIT nor AFTER INVOKE");
                }
            } else if (binding.isThrowable()) {
                // we can only allow reference to the current throwable in an AT THROW rule
                if (rule.getTargetLocation().getLocationType() != LocationType.THROW) {
                    if (Transformer.isVerbose()) {
                        System.out.println("RuleCheckMethodAdapter.checkBindings : found throwable value binding " + binding + " in rule which is not AT THROW " + rule.getName());
                    }
                    transformContext.warn(name, descriptor, "found throwable value binding " + binding + " in rule which is not AT THROW");
                }
                // we will need to set the descriptor at some point
            } else if (binding.isParamArray()) {
                // this is ok
            } else if (binding.isParamCount()) {
                // this is ok
            } else if (binding.isInvokeParamArray()) {
                // we can only allow reference to the invoked method parameters in an AT INVOKE rule
                if (rule.getTargetLocation().getLocationType() != LocationType.INVOKE) {
                    if (Transformer.isVerbose()) {
                        System.out.println("RuleCheckMethodAdapter.checkBindings : found invoke parameter array binding $@ in non-AT INVOKE rule " + rule.getName());
                    }
                    transformContext.warn(name, descriptor, "found throwable value binding " + binding + " in rule which is not AT THROW");
                }
            } else if (binding.isLocalVar()){
                // make sure we have a local variable with the correct name
                String localVarName = binding.getName().substring(1);
                List<LocalVar> localVars = lookup(localVarName);

                if (localVars == null || localVars.isEmpty()) {
                    if (Transformer.isVerbose()) {
                        System.out.println("RuleCheckMethodAdapter.checkBindings : unsatisfiable local variable binding " + binding + " checking method " + name + descriptor);
                    }
                    transformContext.warn(name, descriptor, "unknown local variable " + binding);
                } else {
                    String localDescriptor = null;
                    int index = -1;
                    Iterator<Label> labelIter = triggerPoints.iterator();
                    while (labelIter.hasNext()) {
                        int triggerPos = labelIter.next().getOffset();
                        boolean found = false;
                        Iterator<LocalVar> localVarIter = localVars.iterator();
                        while (localVarIter.hasNext()) {
                            LocalVar localVar = localVarIter.next();
                            int start = localVar.start.getOffset();
                            int end = localVar.end.getOffset();
                            if (start <= triggerPos && triggerPos < end) {
                                // only accept if the descriptor and index are the same or are not yet set
                                if (localDescriptor == null) {
                                    localDescriptor = localVar.desc;
                                    index = localVar.index;
                                    found = true;
                                } else if (localDescriptor.equals(localVar.desc) && index == localVar.index) {
                                    found = true;
                                }
                                // terminate the loop here
                                break;
                            }
                        }
                        // if there was no variable for this trigger point then fail
                        if (!found) {
                            if (Transformer.isVerbose()) {
                                System.out.println("RuleCheckMethodAdapter.checkBindings : invalid local variable binding " + binding + " checking method " + name + descriptor);
                            }
                            transformContext.warn(name, descriptor, "invalid local variable binding " + binding);
                            // ok no point checking any further
                            break;
                        }
                    }
                    // if we got here with a non-null localDescriptor then we have a unique
                    // local var descriptor and index for all trigger points so update the binding
                    // if not then we have notified a fail for the transform so there is no
                    // need to do the update  anyway
                    if (localDescriptor != null) {
                        binding.setDescriptor(Type.parseFieldDescriptor(localDescriptor));
                        binding.setLocalIndex(index);
                    }
                }
            }
        }
    }

    public void visitEnd()
    {
        // ensure that all bindings are valid - if this fails then it will call either transformContext.warn()
        // inhibiting trigger injection or transformContext.fail() throwing an exception and causing the whole
        // transform process to be abandoned.

        checkBindings();

        super.visitEnd();
    }

    private List<Label> triggerPoints;
    private String returnBindingType;
}