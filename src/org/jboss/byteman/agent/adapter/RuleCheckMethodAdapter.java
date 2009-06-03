package org.jboss.byteman.agent.adapter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.binding.Binding;
import org.jboss.byteman.agent.Transformer;

import java.util.*;

/**
 * class which provides base functionality extended by all the location-specific method check adapters
 */
public class RuleCheckMethodAdapter extends RuleMethodAdapter {
    RuleCheckMethodAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor)
    {
        super(mv, rule, access, name, descriptor);
        this.access = access;
        this.descriptor = descriptor;
        this.triggerPoints = null;
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

    protected boolean checkBindings()
    {
        if (!isTriggerPoint()) {
            return false;
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
                    return false;
                }
            } else if (binding.isParam()) {
                int idx = binding.getIndex();
                if (idx > parameterCount) {
                    // parameter out of range
                    if (Transformer.isVerbose()) {
                        System.out.println("RuleCheckMethodAdapter.checkBindings : found out of range parameter binding " + binding + " checking method " + name + descriptor);
                    }
                    return false;
                } else {
                    binding.setDescriptor(parameterTypenames.get(idx - 1));
                }
            } else if (binding.isReturn()) {
                // at some point we will allow reference to the current return value so we need
                // to be sure that the methdo has a non-void return type bit for now we do nothing
            } else if (binding.isLocalVar()){
                // make sure we have a local variable with the correct name
                String localVarName = binding.getName().substring(1);
                List<LocalVar> localVars = lookup(localVarName);

                if (localVars == null || localVars.isEmpty()) {
                    if (Transformer.isVerbose()) {
                        System.out.println("RuleCheckMethodAdapter.checkBindings : unknown local variable binding " + binding + " checking method " + name + descriptor);
                    }
                    return false;
                } else {
                    String descriptor = null;
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
                                if (descriptor == null && index == -1) {
                                    descriptor = localVar.desc;
                                    index = localVar.index;
                                    found = true;
                                } else if (descriptor.equals(localVar.desc) && index == localVar.index) {
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
                            return false;
                        }
                    }
                    // if we got here then we have a unique local var descriptor and index for
                    // all trigger points so update the binding

                    binding.setDescriptor(Type.parseFieldDescriptor(descriptor));
                    binding.setLocalIndex(index);
                }
            }
        }
        // ok all local vars and params are accounted for so return true

        return true;
    }

    private int access;
    private String descriptor;
    private List<Label> triggerPoints;
}