/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.byteman.rule.compiler;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.agent.Transformer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * A class which compiles a rule by generating a subclass of the rule's helperClass which implements
 * the HelperAdapter interface
 */
public class Compiler implements Opcodes
{
    public static Class getHelperAdapter(Rule rule, Class helperClass, boolean compileToBytecode) throws CompileException
    {
        Class adapterClass;
        // for each rule with the same helper we can use the same interpreted adapter
        // we have to use a different compiled helper per rule since it encodes the
        // details of the rule event, condition and action into its execute method

        if (!compileToBytecode) {
            // try to reuse any existing adaoter
            adapterClass = interpretedAdapterMap.get(helperClass);
        } else {
            adapterClass = null;
        }

        if (adapterClass == null) {
            // ok we have to create the adapter class

            // n.b. we don't bother synchronizing here -- if another rule is racing to create an adapter
            // in parallel we don't really care about generating two of them -- we can use whichever
            // one gets intalled last

            try {
                String helperName = Type.getInternalName(helperClass);
                String compiledHelperName;

                // we put the helper in the 
                if (compileToBytecode) {
                    compiledHelperName = helperName + "_HelperAdapter_Compiled_" + nextId();
                } else {
                    compiledHelperName = helperName + "_HelperAdapter_Interpreted";
                }

                byte[] classBytes = compileBytes(rule, helperClass, helperName, compiledHelperName, compileToBytecode);
                String externalName = compiledHelperName.replaceAll("/", ".");
                // dump the compiled class bytes if required
                Transformer.getTheTransformer().maybeDumpClass(externalName, classBytes);
                // ensure the class is loaded
                adapterClass = Transformer.getTheTransformer().loadHelperAdapter(helperClass, externalName, classBytes);
            } catch(CompileException ce) {
                throw ce;
            } catch (Throwable th) {
                if (compileToBytecode) {
                    throw new CompileException("Compiler.createHelperAdapter : exception creating compiled helper adapter for " + helperClass.getName(), th);
                } else {
                    throw new CompileException("Compiler.createHelperAdapter : exception creating interpreted helper adapter for " + helperClass.getName(), th);
                }
            }

            // if this is an interpreted adapter then stash it for later reuse

            if(!compileToBytecode) {
                interpretedAdapterMap.put(helperClass, adapterClass);
            }
        }
        
        return adapterClass;
    }

    /**
     * hashmap used to retrieve previously generated interpred adapters
     *
     * TODO strictly this should be a weak hash map so we don't hang on to adapters after the helper
     * has been dropped. but to make that work we probably need to worry about dropping references to
     * rules too.
     */
    private static HashMap<Class<?>, Class<?>> interpretedAdapterMap = new HashMap<Class<?>, Class<?>>();

    private static byte[] compileBytes(Rule rule, Class helperClass, String helperName, String compiledHelperName, boolean compileToBytecode) throws Exception
    {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;
        // create the class as a subclass of the rule helper class, appending Compiled to the front
        // of the class name and a unique number to the end of the class helperName
        // also ensure it implements the HelperAdapter interface
        //
        // public class foo.bar.Compiled_<helper>_<NNN> extends foo.bar.<helper> implements HelperAdapter

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, compiledHelperName, null, helperName, new String[] { "org/jboss/byteman/rule/helper/HelperAdapter" });
        {
        // we need a Hashmap field to hold the bindings
        //
        // private HashMap<String, Object> bindingMap;

        fv = cw.visitField(ACC_PRIVATE, "bindingMap", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Object;>;", null);
        fv.visitEnd();
        }
        {
        // and another Hashmap field to hold the binding types
        //
        // private HashMap<String, Type> bindingTypeMap;

        fv = cw.visitField(ACC_PRIVATE, "bindingTypeMap", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Lorg/jboss/byteman/rule/type/Type;>;", null);
        fv.visitEnd();
        }
        {
        // and a rule field to hold the rule
        //
        // private Rule rule;

        fv = cw.visitField(ACC_PRIVATE, "rule", "Lorg/jboss/byteman/rule/Rule;", "Lorg/jboss/byteman/rule/Rule;", null);
        fv.visitEnd();
        }
        {
        // we need a constructor which takes a Rule as argument
        // if the helper implements a constructor which takes a Rule as argument then we invoke it
        // otherwise we invoke the empty helper constructor

        Constructor superConstructor = null;
        try {
            superConstructor = helperClass.getDeclaredConstructor(Rule.class);
        } catch (NoSuchMethodException e) {
            // hmm, ok see if there is an empty constructor
        } catch (SecurityException e) {
            throw new CompileException("Compiler.compileBytes : unable to access constructor for helper class " + helperClass.getCanonicalName());
        }
        boolean superWantsRule = (superConstructor != null);
        if (!superWantsRule) {
            try {
                superConstructor = helperClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new CompileException("Compiler.compileBytes : no valid constructor found for helper class " + helperClass.getCanonicalName());
            } catch (SecurityException e) {
                throw new CompileException("Compiler.compileBytes : unable to access constructor for helper class " + helperClass.getCanonicalName());
            }
        }
        //
        //  public Compiled<helper>_<NNN>()Rule rule)
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/jboss/byteman/rule/Rule;)V", null, null);
        mv.visitCode();
        // super();
        //
        // or
        //
        // super(Rule);
        if (superWantsRule) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, helperName, "<init>", "(Lorg/jboss/byteman/rule/Rule;)V");
        } else {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, helperName, "<init>", "()V");
        }
        // bindingMap = new HashMap<String, Object);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, "java/util/HashMap");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
        mv.visitFieldInsn(PUTFIELD, compiledHelperName, "bindingMap", "Ljava/util/HashMap;");
        // bindingTypeMap = new HashMap<String, Type);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, "java/util/HashMap");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
        mv.visitFieldInsn(PUTFIELD, compiledHelperName, "bindingTypeMap", "Ljava/util/HashMap;");
        // this.rule = rule
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, compiledHelperName, "rule", "Lorg/jboss/byteman/rule/Rule;");
        // return;
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
        }
        {
        // create the execute method
        //
        // public void execute(Bindings bindings, Object recipient, Object[] args) throws ExecuteException
        mv = cw.visitMethod(ACC_PUBLIC, "execute", "(Lorg/jboss/byteman/rule/binding/Bindings;Ljava/lang/Object;[Ljava/lang/Object;)V", null, new String[] { "org/jboss/byteman/rule/exception/ExecuteException" });
        mv.visitCode();
        // if (Transformer.isVerbose())
        mv.visitMethodInsn(INVOKESTATIC, "org/jboss/byteman/agent/Transformer", "isVerbose", "()Z");
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);
        // then
        // System.out.println(rule.getName() + " execute");
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "rule", "Lorg/jboss/byteman/rule/Rule;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/Rule", "getName", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitLdcInsn(" execute()");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        // end if
        mv.visitLabel(l0);
        // Iterator<Binding> iterator = bindings.iterator();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/binding/Bindings", "iterator", "()Ljava/util/Iterator;");
        mv.visitVarInsn(ASTORE, 4);
        // while 
        Label l1 = new Label();
        mv.visitLabel(l1);
        // iterator.hasNext()
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
        Label l2 = new Label();
        mv.visitJumpInsn(IFEQ, l2);
        // do
        // binding = (Binding)iterator.next();
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, "org/jboss/byteman/rule/binding/Binding");
        mv.visitVarInsn(ASTORE, 5);
        // String name = binding.getName();
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/binding/Binding", "getName", "()Ljava/lang/String;");
        mv.visitVarInsn(ASTORE, 6);
        // Type type = binding.getType();
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/binding/Binding", "getType", "()Lorg/jboss/byteman/rule/type/Type;");
        mv.visitVarInsn(ASTORE, 7);
        // if (binding.isHelper())
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/binding/Binding", "isHelper", "()Z");
        Label l3 = new Label();
        mv.visitJumpInsn(IFEQ, l3);
        // then
        // bindingMap.put(name, this);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "bindingMap", "Ljava/util/HashMap;");
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(POP);
        // bindingTypeMap.put(name, type);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "bindingTypeMap", "Ljava/util/HashMap;");
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(POP);
        Label l4 = new Label();
        mv.visitJumpInsn(GOTO, l4);
        // else if (binding.isRecipient())
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/binding/Binding", "isRecipient", "()Z");
        Label l5 = new Label();
        mv.visitJumpInsn(IFEQ, l5);
        // then
        // bindingMap.put(name, recipient);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "bindingMap", "Ljava/util/HashMap;");
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(POP);
        // bindingTypeMap.put(name, type);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "bindingTypeMap", "Ljava/util/HashMap;");
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(POP);
        mv.visitJumpInsn(GOTO, l4);
        // else if (binding.isParam() || binding.isLocalVar())
        mv.visitLabel(l5);
        Label l6 = new Label();
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/binding/Binding", "isParam", "()Z");
        mv.visitJumpInsn(IFNE, l6); // skip to then if true or drop throuogh to || branch if false
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/binding/Binding", "isLocalVar", "()Z");
        mv.visitJumpInsn(IFEQ, l4); // bypass this branch
        mv.visitLabel(l6);
        // then
        // bindingMap.put(name, args[binding.getObjectArrayIndex]);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "bindingMap", "Ljava/util/HashMap;");
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/binding/Binding", "getObjectArrayIndex", "()I");
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(POP);
        // bindingTypeMap.put(name, type);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "bindingTypeMap", "Ljava/util/HashMap;");
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(POP);
        // end if
        mv.visitLabel(l4);
        mv.visitJumpInsn(GOTO, l1);
        // end do while
        mv.visitLabel(l2);
        // execute0()
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, compiledHelperName, "execute0", "()V");
        // return
        mv.visitInsn(RETURN);
        mv.visitMaxs(5, 8);
        mv.visitEnd();
        }
        {
        // create the bindVariable method
        //
        // public void bindVariable(String name, Object value)
        mv = cw.visitMethod(ACC_PUBLIC, "bindVariable", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
        mv.visitCode();
        //  bindingMap.put(name, value);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "bindingMap", "Ljava/util/HashMap;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(POP);
        // return
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
        }
        {
        // create the getBinding method
        //
        // public Object getBinding(String name)
        mv = cw.visitMethod(ACC_PUBLIC, "getBinding", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);
        mv.visitCode();
        // {TOS} <== bindingMap.get(name);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "bindingMap", "Ljava/util/HashMap;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        // return {TOS}
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        }
        {
        // create the getName method
        //
        // public String getName()
        mv = cw.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
        mv.visitCode();
        // {TOS} <== rule.getName()
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, compiledHelperName, "rule", "Lorg/jboss/byteman/rule/Rule;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/Rule", "getName", "()Ljava/lang/String;");
        // return {TOS}
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        }
        if (compileToBytecode) {
            // we generate a single execute0 method if we want to run compiled and get
            // the event, condiiton and action to insert the relevant bytecode to implement
            // bind(), test() and fire()

            StackHeights maxStackHeights = new StackHeights();
            StackHeights currentStackHeights;
            {
            // create the execute0() method
            //
            // private void execute0()
            mv = cw.visitMethod(ACC_PRIVATE, "execute0", "()V", null, new String[] { "org/jboss/byteman/rule/exception/ExecuteException" });
            mv.visitCode();
            maxStackHeights.addLocalCount(3); // for this and 2 object args
            // bind();
            currentStackHeights = new StackHeights();
            rule.getEvent().compile(mv, currentStackHeights, maxStackHeights);
            // if (test())
            currentStackHeights = new StackHeights();
            rule.getCondition().compile(mv, currentStackHeights, maxStackHeights);
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            // then
            currentStackHeights = new StackHeights();
            rule.getAction().compile(mv, currentStackHeights, maxStackHeights);
            // fire();
            // end if
            mv.visitLabel(l0);
            // return
            mv.visitInsn(RETURN);
            // need to specify correct Maxs values
            mv.visitMaxs(maxStackHeights.stackCount, maxStackHeights.localCount);
            mv.visitEnd();
            }
        } else {
            // we generate the following methods if we want to run interpreted
            {
            // create the execute0() method
            //
            // private void execute0()
            mv = cw.visitMethod(ACC_PRIVATE, "execute0", "()V", null, new String[] { "org/jboss/byteman/rule/exception/ExecuteException" });
            mv.visitCode();
            // bind();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, compiledHelperName, "bind", "()V");
            // if (test())
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, compiledHelperName, "test", "()Z");
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            // then
            // fire();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, compiledHelperName, "fire", "()V");
            // end if
            mv.visitLabel(l0);
            // return
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
            }
            {
            // create the bind method
            //
            // private void bind()
            mv = cw.visitMethod(ACC_PRIVATE, "bind", "()V", null, new String[] { "org/jboss/byteman/rule/exception/ExecuteException" });
            mv.visitCode();
            // rule.getEvent().interpret(this);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, compiledHelperName, "rule", "Lorg/jboss/byteman/rule/Rule;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/Rule", "getEvent", "()Lorg/jboss/byteman/rule/Event;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/Event", "interpret", "(Lorg/jboss/byteman/rule/helper/HelperAdapter;)Ljava/lang/Object;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
            }
            {
            // create the test method
            //
            // private boolean test()
            mv = cw.visitMethod(ACC_PRIVATE, "test", "()Z", null, new String[] { "org/jboss/byteman/rule/exception/ExecuteException" });
            mv.visitCode();
            // {TOS} <== rule.getCondition().interpret(this);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, compiledHelperName, "rule", "Lorg/jboss/byteman/rule/Rule;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/Rule", "getCondition", "()Lorg/jboss/byteman/rule/Condition;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/Condition", "interpret", "(Lorg/jboss/byteman/rule/helper/HelperAdapter;)Ljava/lang/Object;");
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            // unbox the returned Boolean
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
            // return {TOS}
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
            }
            {
            // create the fire method
            //
            // private void fire()
            mv = cw.visitMethod(ACC_PRIVATE, "fire", "()V", null, new String[] { "org/jboss/byteman/rule/exception/ExecuteException" });
            mv.visitCode();
            // rule.getAction().interpret(this);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, compiledHelperName, "rule", "Lorg/jboss/byteman/rule/Rule;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/Rule", "getAction", "()Lorg/jboss/byteman/rule/Action;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/rule/Action", "interpret", "(Lorg/jboss/byteman/rule/helper/HelperAdapter;)Ljava/lang/Object;");
            // return
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
            }
        }

        cw.visitEnd();

        return cw.toByteArray();
    }

    private static int nextId = 0;

    private static synchronized int nextId()
    {
        return ++nextId;
    }
}
