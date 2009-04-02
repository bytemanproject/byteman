package org.jboss.jbossts.orchestration.agent.adapter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;

import java.util.*;

/**
 * class which provides base functionality extended by all the location-specific method trigger adapters
 */
public class RuleTriggerMethodAdapter extends RuleMethodAdapter
{
    RuleTriggerMethodAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor)
    {
        super(mv, rule, access, name, descriptor);
        this.access = access;
        this.descriptor = descriptor;
        this.paramBindings = new ArrayList<Binding>();
        this.returnType = Type.getReturnType(descriptor);
        this.argumentTypes = Type.getArgumentTypes(descriptor);
        this.bindingsDone = false;
    }

    private void setBindingIndices()
    {
        Bindings bindings = rule.getBindings();
        Iterator<Binding> iterator = bindings.iterator();

        // make sure all entries are valid
        while (iterator.hasNext()) {
            Binding binding = iterator.next();
            if (binding.isParam()) {
                paramBindings.add(binding);
            } else if (binding.isReturn()) {
                // at some point we will allow reference to the current return value so we need
                // to be sure that the method has a non-void return type bit for now we do nothing
            } else if (binding.isLocalVar()){
                paramBindings.add(binding);
            }
        }
        // we don't have to do this but it makes debgging easier
        
        if (true) {
            // ok now sort the paramBindings for later use

            Comparator<Binding> comparator = new Comparator<Binding>() {
                public int compare(Binding b1, Binding b2)
                {
                    if (b1.isParam()) {
                        if (b2.isParam()) {
                            int i1 = b1.getIndex();
                            int i2 = b2.getIndex();
                            return (i1 < i2 ? -1 : (i1 > i2 ? 1 : 0));
                        } else {
                            return -1;
                        }
                    } else {
                        if (b2.isParam()) {
                            return 1;
                        } else {
                            int i1 = b1.getLocalIndex();
                            int i2 = b2.getLocalIndex();
                            return (i1 < i2 ? -1 : (i1 > i2 ? 1 : 0));
                        }
                    }
                }
            };

            Collections.sort(paramBindings, comparator);
        }

        // now give each binding a unique index in the object array
        int n = paramBindings.size();
        for (int i = 0; i < n; i++) {
            paramBindings.get(i).setObjectArrayIndex(i);
        }
    }

    public void doArgLoad()
    {
        if (!bindingsDone) {
            setBindingIndices();
            bindingsDone = true;
        }
        
        if (paramBindings.size() ==  0) {
            super.push((org.objectweb.asm.Type)null);
            return;
        }

        int arraySize = paramBindings.size();

        push(arraySize);
        Type objectType = Type.getType(Object.class);
        newArray(objectType);

        for (int i = 0; i < arraySize; i++) {
            Binding binding = paramBindings.get(i);
            dup();
            push(i);
            if (binding.isParam()) {
                int idx = binding.getIndex() - 1;
                loadArg(idx);
                box(argumentTypes[idx]);
            } else {
                int idx = binding.getLocalIndex();
                loadLocal(idx);
                box(getLocalType(idx));
            }
            arrayStore(objectType);
        }
    }

    private int access;
    private String descriptor;
    private Type returnType;
    private Type[] argumentTypes;
    private List<Binding> paramBindings;
    private boolean bindingsDone;
}
