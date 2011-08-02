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
package org.jboss.byteman.rule.helper;

import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.binding.Binding;
import org.jboss.byteman.agent.Transformer;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Implementation of RuleHelper which extends the functionality of the standard helper class,
 * Helper, by adding the methods required to implement interface RuleHelper. It provides an
 * implementation which executes rules by interpreting the rule tree. Any rule which employs the
 * standard helper type checks built in method calls against class Helper. However, the interpreter
 * assumes that the helper object implements InterpretedHelper.
 *
 * When a rule is compiled class Helper is extended with a generated class CompiledHelper<NNN>
 * which also implements interface RuleHelper. The implementation of the execute method is generated
 * by translating the parse tree to bytecode. Builtin calls are translated to calls of methods
 * defined by class Helper.
 *
 * A rule can also specify its own helper class in order to provide its own set of builtin
 * operations. The helper class does not implement interface RuleHelper. Instead the
 * compilation process will generate a subclass of the user-defined helper class which
 * provides an appropriate implementation for the RuleHelper methods, including an implementation
 * of the execute method dreived from the rule parse tree. As in the default case, builtin calls
 * are translated to calls of methods defined by the helper class. 
 */
public class InterpretedHelper extends Helper implements HelperAdapter
{
    protected HashMap<String, Object> bindingMap;

    public InterpretedHelper(Rule rule)
    {
        super(rule);
        bindingMap = new HashMap<String, Object>();
    }

    /**
     * install values into the bindings map and then call the execute0 method
     * to actually execute the rule
     * @param recipient
     * @param args
     */
    public void execute(Object recipient, Object[] args)
            throws ExecuteException
    {
        if (Transformer.isVerbose()) {
            System.out.println(rule.getName() + " execute");
        }
        Bindings bindings = rule.getBindings();
        Iterator<Binding> iterator = bindings.iterator();
        while (iterator.hasNext()) {
            Binding binding = iterator.next();
            String name = binding.getName();
            if (binding.isAlias()) {
                // this is a local var used to refer to a method recipient or parameter
                // so use the value and type associated with the alias
                binding = binding.getAlias();
            }
            if (binding.isHelper()) {
                bindingMap.put(name, this);
            } else if (binding.isRecipient()) {
                bindingMap.put(name, recipient);
            // } else if (!binding.isParam() || binding.isLocalVar() || binding.isReturn() ||
            //         binding.isThrowable() || binding.isParamCount() || binding.isParamArray()) {
            } else if (!binding.isBindVar()) {
                bindingMap.put(name, args[binding.getCallArrayIndex()]);
            }
        }

        // now do the actual execution

        execute0();

        // now restore update bindings

        iterator = bindings.iterator();

        while (iterator.hasNext()) {
            Binding binding = iterator.next();
            String name = binding.getName();
            if (binding.isAlias()) {
                continue;
            }

            if (binding.isUpdated()) {
                // if (binding.isParam() || binding.isLocalVar() || binding.isReturn()) {
                if (!binding.isBindVar() && !binding.isAlias()) {
                    Object value = bindingMap.get(name);
                    int idx = binding.getCallArrayIndex();
                    args[idx] = value;
                }
            }
        }

    }

    /**
     * basic implementation of rule execution
     *
     * @throws ExecuteException
     */
        
    protected void execute0()
            throws ExecuteException
    {
        // System.out.println(rule.getName() + " execute0");
        bind();
        if (test()) {
            fire();
        }
    }

    public void setBinding(String name, Object value)
    {
        bindingMap.put(name, value);
    }

    public Object getBinding(String name)
    {
        return bindingMap.get(name);
    }

    private void bind()
            throws ExecuteException
    {
        // System.out.println(rule.getName() + " bind");
        rule.getEvent().interpret(this);
    }

    private boolean test()
            throws ExecuteException
    {
        // System.out.println(rule.getName() + " test");
        return (Boolean)rule.getCondition().interpret(this);
    }
        
    private void fire()
            throws ExecuteException
    {
        // System.out.println(rule.getName() + " fire");
        rule.getAction().interpret(this);
    }

    public String getName() {
        return rule.getName();
    }

    public Object getAccessibleField(Object owner, int fieldIndex)
    {
        return rule.getAccessibleField(owner, fieldIndex);
    }

    public void setAccessibleField(Object owner, Object value, int fieldIndex)
    {
        rule.setAccessibleField(owner, value, fieldIndex);
    }
    public Object invokeAccessibleMethod(Object target, Object[] args, int methodIndex)
    {
        return rule.invokeAccessibleMethod(target, args, methodIndex);
    }
}
