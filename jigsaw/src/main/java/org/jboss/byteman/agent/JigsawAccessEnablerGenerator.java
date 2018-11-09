/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat and individual contributors
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

package org.jboss.byteman.agent;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * class which provides a bytecode definition for class JigsawAccessEnabler
 */
public class JigsawAccessEnablerGenerator
{
    private static final String BYTEMAN_JIGSAW_PACKAGE_NAME = "org/jboss/byteman/jigsaw/";
    private static final int BYTEMAN_JIGSAW_PACKAGE_NAME_LEN = BYTEMAN_JIGSAW_PACKAGE_NAME.length();
    private static final boolean DEBUG = false;

    public static byte[] getJigsawClassBytes(String s)
    {
        byte[] bytes = null;
        if(s.startsWith(BYTEMAN_JIGSAW_PACKAGE_NAME)) {
            String name = s.substring(BYTEMAN_JIGSAW_PACKAGE_NAME_LEN);
            switch (name) {
                case "JigsawAccessEnabler.class":
                    bytes = getJigsawAccessEnablerClassBytes();
                    break;
                case "JigsawAccessibleConstructorInvoker.class":
                    bytes =  getJigsawAccessibleConstructorInvokerClassBytes();
                    break;
                case "JigsawAccessibleMethodInvoker.class":
                    bytes =  getJigsawAccessibleMethodInvokerClassBytes();
                    break;
                case "JigsawAccessibleFieldGetter.class":
                    bytes =  getJigsawAccessibleFieldGetterClassBytes();
                    break;
                case "JigsawAccessibleFieldSetter.class":
                    bytes =  getJigsawAccessibleFieldSetterClassBytes();
                    break;
            }
        }

        if (bytes != null) {
            String name = s.substring(0, s.length() - ".class".length()).replace('/', '.');
            Transformer.maybeDumpClass(name, bytes);
        }

        return bytes;
    }


    public static byte[] getJigsawAccessEnablerClassBytes()
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;


        // package org/jboss/byteman/jigsaw;
        // public class JigsawAccessEnabler implements AccessEnabler { ... }
        cw.visit(V9, ACC_PUBLIC + ACC_SUPER, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", null, "java/lang/Object", new String[] { "org/jboss/byteman/agent/AccessEnabler" });

        cw.visitSource("JigsawAccessEnabler.java", null);

        // this is here because we use String + String
        cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        {
            // private Module THIS_MODULE = this.getClass().getModule();
            fv = cw.visitField(ACC_PRIVATE, "THIS_MODULE", "Ljava/lang/Module;", null, null);
            fv.visitEnd();
        }
        {
            // private Module UNPRIVILEGED_MODULE = AccessEnabler.class.getModule();
            fv = cw.visitField(ACC_PRIVATE, "UNPRIVILEGED_MODULE", "Ljava/lang/Module;", null, null);
            fv.visitEnd();
        }        {
            // private Set<Module> THIS_MODULE_SET = Set.of(THIS_MODULE);
            fv = cw.visitField(ACC_PRIVATE, "THIS_MODULE_SET", "Ljava/util/Set;", "Ljava/util/Set<Ljava/lang/Module;>;", null);
            fv.visitEnd();
        }
        {
            // private Set<Module> EMPTY_READS_SET = Set.of();
            fv = cw.visitField(ACC_PRIVATE, "EMPTY_READS_SET", "Ljava/util/Set;", "Ljava/util/Set<Ljava/lang/Module;>;", null);
            fv.visitEnd();
        }
        {
            //  private Map<String,<Set<Module>> EMPTY_EXPORTS_MAP = Map.of();
            fv = cw.visitField(ACC_PRIVATE, "EMPTY_EXPORTS_MAP", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/Module;>;>;", null);
            fv.visitEnd();
        }
        {
            //  private Map<String,<Set<Module>> EMPTY_OPENS_MAP = Map.of();
            fv = cw.visitField(ACC_PRIVATE, "EMPTY_OPENS_MAP", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/Module;>;>;", null);
            fv.visitEnd();
        }
        {
            // private Set<Class<?>> EMPTY_USES_SET = Set.of()
            fv = cw.visitField(ACC_PRIVATE, "EMPTY_USES_SET", "Ljava/util/Set;", "Ljava/util/Set<Ljava/lang/Class<*>;>;", null);
            fv.visitEnd();
        }
        {
            // private Map<Class<?>,List<Class?>>> EMPTY_PROVIDES_MAP = Map.of();
            fv = cw.visitField(ACC_PRIVATE, "EMPTY_PROVIDES_MAP", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/Class<*>;Ljava/util/List<Ljava/lang/Class<*>;>;>;", null);
            fv.visitEnd();
        }
        {
            //  private Instrumentation inst;
            fv = cw.visitField(ACC_PRIVATE, "inst", "Ljava/lang/instrument/Instrumentation;", null, null);
            fv.visitEnd();
        }
        {
            // private Lookup theLookup;
            fv = cw.visitField(ACC_PRIVATE, "theLookup", "Ljava/lang/invoke/MethodHandles$Lookup;", null, null);
            fv.visitEnd();
        }
        {
            // private DefaultAccessEnabler defaultAccessEnabler;
            fv = cw.visitField(ACC_PRIVATE, "defaultAccessEnabler", "Lorg/jboss/byteman/agent/DefaultAccessEnabler;", null, null);
            fv.visitEnd();
        }
        {
            // public JigsawAccessEnabler(Instrumentation inst)   {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/instrument/Instrumentation;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            // super()
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            // this.THIS_MODULE = this.getClass().getModule();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getModule", "()Ljava/lang/Module;", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/Module;");
            // this.UNPRIVILEGED_MODULE = AccessEnabler.class.getModule();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(Type.getType("Lorg/jboss/byteman/agent/AccessEnabler;"));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getModule", "()Ljava/lang/Module;", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "UNPRIVILEGED_MODULE", "Ljava/lang/Module;");
            // this.THIS_MODULE_SET = Set.of(this.THIS_MODULE);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/Module;");
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Set", "of", "(Ljava/lang/Object;)Ljava/util/Set;", true);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE_SET", "Ljava/util/Set;");
            // this.EMPTY_READS_SET = Set.of();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Set", "of", "()Ljava/util/Set;", true);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_READS_SET", "Ljava/util/Set;");
            // this.EMPTY_EXPORTS_MAP = Map.of();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Map", "of", "()Ljava/util/Map;", true);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_EXPORTS_MAP", "Ljava/util/Map;");
            // this.EMPTY_OPENS_MAP = Map.of();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Map", "of", "()Ljava/util/Map;", true);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_OPENS_MAP", "Ljava/util/Map;");
            // this.EMPTY_USES_SET = Set.of()
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Set", "of", "()Ljava/util/Set;", true);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_USES_SET", "Ljava/util/Set;");
            // this.EMPTY_PROVIDES_MAP = Map.of();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Map", "of", "()Ljava/util/Map;", true);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_PROVIDES_MAP", "Ljava/util/Map;");
            // if this.THIS_MODULE.isnamed()
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/Module;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "isNamed", "()Z", false);
            Label l3 = new Label();
            mv.visitJumpInsn(IFNE, l3);
            // {
            //   throw NEW RuntimeException("JigsawAccessEnabler : can only enable Jigsaw access from a named module not " + THIS_MODULE);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/Module;");
            mv.visitInvokeDynamicInsn("makeConcatWithConstants", "(Ljava/lang/Module;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;"), new Object[]{"JigsawAccessEnabler : can only enable Jigsaw access from a named module not \u0001"});
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);
            // }
            mv.visitLabel(l3);
            // if (inst == null)
            mv.visitVarInsn(ALOAD, 1);
            Label l4 = new Label();
            mv.visitJumpInsn(IFNONNULL, l4);
            // {
            //   throw New RuntimeException("JigsawAccessEnabler : can only be created if passed a real Instrumentation handle");
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessEnabler : can only be created if passed a real Instrumentation handle");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);
            // }
            mv.visitLabel(l4);
            // this.inst = inst;
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "inst", "Ljava/lang/instrument/Instrumentation;");
            // try {
            mv.visitLabel(l0);
            // this.theLookup = MethodHandles.lookup();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "theLookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
            mv.visitLabel(l1);
            Label l5 = new Label();
            mv.visitJumpInsn(GOTO, l5);
            // } (catch Exception) {
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 2);
            // throw new RuntimeException("JigsawAccessEnabler : cannot access Lookup.IMPL_LOOKUP", e);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessEnabler : cannot obtain lookup from Byteman module");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            // }
            mv.visitLabel(l5);
            // defaultAccessEnabler = new DefaultAccessEnabler();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/agent/DefaultAccessEnabler");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/agent/DefaultAccessEnabler", "<init>", "()V", false);
            mv.visitFieldInsn(PUTFIELD,"org/jboss/byteman/jigsaw/JigsawAccessEnabler", "defaultAccessEnabler", "Lorg/jboss/byteman/agent/DefaultAccessEnabler;");
            if (DEBUG) {
            // debug("created JigsawAccessEnabler")
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("created JigsawAccessEnabler");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            // return
            mv.visitInsn(RETURN);
            // }
            mv.visitMaxs(5, 3);
            mv.visitEnd();
        }
        {
            // public boolean requiresAccess(Class<?> klazz)
            mv = cw.visitMethod(ACC_PUBLIC, "requiresAccess", "(Ljava/lang/Class;)Z", "(Ljava/lang/Class<*>;)Z", null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/SecurityException");
            // debug("JigsawAccessEnabler.requiresAccess( klazz == " + klazz.getName() + ")");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_3);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("JigsawAccessEnabler.requiresAccess( klazz == ");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_2);
            mv.visitLdcInsn(")");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            Label l3 = new Label();
            mv.visitLabel(l3);
            // while (Modifier.isPublic(klazz.getModifiers()))
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getModifiers", "()I", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/reflect/Modifier", "isPublic", "(I)Z", false);
            Label l4 = new Label();
            mv.visitJumpInsn(IFEQ, l4);
            // {
            //   Module module = klazz.getModule();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getModule", "()Ljava/lang/Module;", false);
            mv.visitVarInsn(ASTORE, 2);
            //   if (module.isNamed())
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "isNamed", "()Z", false);
            Label l5 = new Label();
            mv.visitJumpInsn(IFEQ, l5);
            //   {
            //     debug(" module == " + module.getName());
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(" module == ");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "getName", "()Ljava/lang/String;", false);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            //     Package pkg = klazz.getPackage();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getPackage", "()Ljava/lang/Package;", false);
            mv.visitVarInsn(ASTORE, 3);
            //     if (pkg == null)
            mv.visitVarInsn(ALOAD, 3);
            Label l6 = new Label();
            mv.visitJumpInsn(IFNONNULL, l6);
            //     {
            //        debug ("  (pkg == null) ==> false");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("  (pkg == null) ==> false");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            //     }
            mv.visitLabel(l6);
            //     if (!module.isExported(pkg.getName()))
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Package", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "isExported", "(Ljava/lang/String;)Z", false);
            mv.visitJumpInsn(IFNE, l5);
            //     {
            //       debug (" !module.isExported(pkg.getName()) ==> true");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(" !module.isExported(pkg.getName()) ==> true");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            //       return true;
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            //     }
            //   }
            mv.visitLabel(l5);
            //   if (!klazz.isMemberClass())
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isMemberClass", "()Z", false);
            mv.visitJumpInsn(IFNE, l0);
            //   {
            //     debug(" !klazz.isMemberClass() ==> false") ;
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(" !klazz.isMemberClass() ==> false");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            //   }
            //   try {
            mv.visitLabel(l0);
            //     klazz = klazz.getDeclaringClass();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 1);
            //     debug(" klazz == " + klazz.getName());
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(" klazz == ");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            mv.visitLabel(l1);
            Label l7 = new Label();
            mv.visitJumpInsn(GOTO, l7);
            //   } catch (SecurityException se) {
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            //     debug ("SecurityException ==> true");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("SecurityException ==> true");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            //   }
            mv.visitLabel(l7);
            // }
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l4);
            //     debug ("  ==> true");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("  ==> true");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(5, 4);
            mv.visitEnd();
        }
        {
            // public boolean requiresAccess(AccessibleObject) {
            mv = cw.visitMethod(ACC_PUBLIC, "requiresAccess", "(Ljava/lang/reflect/AccessibleObject;)Z", null, null);
            mv.visitCode();
            // Member member = (Member)accessible;
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "java/lang/reflect/Member");
            mv.visitVarInsn(ASTORE, 2);
            // debug("JigsawAccessEnabler.requiresAccess( accessible == " + member.getDeclaringClass().getName() + member.getName() + ")");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_5);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("JigsawAccessEnabler.requiresAccess( accessible == ");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/reflect/Member", "getDeclaringClass", "()Ljava/lang/Class;", true);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_2);
            mv.visitLdcInsn(".");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_3);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/reflect/Member", "getName", "()Ljava/lang/String;", true);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_4);
            mv.visitLdcInsn(")");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            // if (!Modifier.isPublic(member.getModifiers())
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/reflect/Member", "getModifiers", "()I", true);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/reflect/Modifier", "isPublic", "(I)Z", false);
            Label l0 = new Label();
            mv.visitJumpInsn(IFNE, l0);
            // {
            //   debug ("!Modifier.isPublic(member.getModifiers()) ==> true");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("!Modifier.isPublic(member.getModifiers()) ==> true");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            //   return true
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            // }
            mv.visitLabel(l0);
            // Class<?> clazz = member.getDeclaringClass();
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/reflect/Member", "getDeclaringClass", "()Ljava/lang/Class;", true);
            mv.visitVarInsn(ASTORE, 3);
            // if (!Modifier.isPublic(clazz.getModifiers()))
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getModifiers", "()I", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/reflect/Modifier", "isPublic", "(I)Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(IFNE, l1);
            // {
            //   debug("!Modifier.isPublic(clazz.getModifiers()) ==> true");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("!Modifier.isPublic(clazz.getModifiers()) ==> true");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            // }
            mv.visitLabel(l1);
            // while (clazz.isMemberClass())
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isMemberClass", "()Z", false);
            Label l2 = new Label();
            mv.visitJumpInsn(IFEQ, l2);
            // do {
            //   clazz = clazz.getEnclosingClass();
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getEnclosingClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 3);
            //   debug("klazz == " + clazz.getName());
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("klazz == ");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            //   if (!Modifier.isPublic(clazz.getModifiers()))
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getModifiers", "()I", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/reflect/Modifier", "isPublic", "(I)Z", false);
            mv.visitJumpInsn(IFNE, l1);
            //   {
            //     debug("!Modifier.isPublic(clazz.getModifiers()) ==> true");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("!Modifier.isPublic(clazz.getModifiers()) ==> true");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            //     return true
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            //   }
            // } done
            mv.visitLabel(l2);
            // Module module = clazz.getModule();
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getModule", "()Ljava/lang/Module;", false);
            mv.visitVarInsn(ASTORE, 4);
            // if (!module.isNamed())
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "isNamed", "()Z", false);
            Label l3 = new Label();
            mv.visitJumpInsn(IFNE, l3);
            // {
            //   debug ("!module.isNamed() ==> false");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("!module.isNamed() ==> false");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            //   return false
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            // }
            mv.visitLabel(l3);
            if (DEBUG) {
            // debug("module == " + module.getName());
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("module == ");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "getName", "()Ljava/lang/String;", false);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            // String pkg = clazz.getPackageName();
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getPackageName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 5);
            if (DEBUG) {
            // debug("pkg == " + pkg);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("pkg == ");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            // if (module.isExported(pkg, UNPRIVILEGED_MODULE))
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "UNPRIVILEGED_MODULE", "Ljava/lang/Module;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "isExported", "(Ljava/lang/String;Ljava/lang/Module;)Z", false);
            Label l4 = new Label();
            mv.visitJumpInsn(IFEQ, l4);
            // {
            //   debug ("module.isExported(pkg, UNPRIVILEGED_MODULE) ==> false");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("module.isExported(pkg, UNPRIVILEGED_MODULE) ==> false");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            //   return false
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            // }
            mv.visitLabel(l4);
            // debug(" ==> true");
            if (DEBUG) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(" ==> true");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "debug", "([Ljava/lang/String;)V", false);
            }
            // return true
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            // }
            mv.visitMaxs(5, 6);
            mv.visitEnd();
        }
        {
            // public void ensureAccess(AccessibleObject) {
            mv = cw.visitMethod(ACC_PUBLIC, "ensureAccess", "(Ljava/lang/reflect/AccessibleObject;)V", null, null);
            mv.visitCode();
            //  ensureModuleAccess(accessible);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "ensureModuleAccess", "(Ljava/lang/reflect/AccessibleObject;)V", false);
            // accessible.setAccessible(true);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/AccessibleObject", "setAccessible", "(Z)V", false);
            mv.visitInsn(RETURN);
            // }
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "createMethodInvoker", "(Ljava/lang/reflect/Method;)Lorg/jboss/byteman/agent/AccessibleMethodInvoker;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/IllegalArgumentException");
            Label l3 = new Label();
            mv.visitTryCatchBlock(l0, l1, l3, "java/lang/IllegalAccessException");
            // ensureModuleAccess(method);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "ensureModuleAccess", "(Ljava/lang/reflect/AccessibleObject;)V", false);
            //  Lookup privateLookup = null;
            mv.visitInsn(ACONST_NULL);
            mv.visitVarInsn(ASTORE, 2);
            //   try {
            mv.visitLabel(l0);
            // privateLookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), theLookup);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "theLookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitLabel(l1);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            // } catch (IllegalArgumentException e) {
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            // method.setAccessible(true);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "setAccessible", "(Z)V", false);
            // return defaultAccessEnabler.createMethodInvoker(method, true);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "defaultAccessEnabler", "Lorg/jboss/byteman/agent/DefaultAccessEnabler;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/agent/DefaultAccessEnabler", "createMethodInvoker", "(Ljava/lang/reflect/Method;Z)Lorg/jboss/byteman/agent/AccessibleMethodInvoker;", false);
            mv.visitInsn(ARETURN);
            // } catch (IllegalAccessException e) {
            mv.visitLabel(l3);
            mv.visitVarInsn(ASTORE, 3);
            // }
            mv.visitLabel(l4);
            // return new JigsawAccessibleMethodInvoker(privateLookup, method);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/jigsaw/JigsawAccessibleMethodInvoker");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessibleMethodInvoker", "<init>", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/reflect/Method;)V", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "createConstructorInvoker", "(Ljava/lang/reflect/Constructor;)Lorg/jboss/byteman/agent/AccessibleConstructorInvoker;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/IllegalArgumentException");
            Label l3 = new Label();
            mv.visitTryCatchBlock(l0, l1, l3, "java/lang/IllegalAccessException");
            // ensureModuleAccess(constructor);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "ensureModuleAccess", "(Ljava/lang/reflect/AccessibleObject;)V", false);
            //  Lookup privateLookup = null;
            mv.visitInsn(ACONST_NULL);
            mv.visitVarInsn(ASTORE, 2);
            // try {
            mv.visitLabel(l0);
            // privateLookup = MethodHandles.privateLookupIn(constructor.getDeclaringClass(), theLookup)
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Constructor", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "theLookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitLabel(l1);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            // } catch (IllegalArgumentException e) {
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            // constructor.setAccessible(true);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Constructor", "setAccessible", "(Z)V", false);
            // return defaultAccessEnabler.createConstructorInvoker(constructor);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "defaultAccessEnabler", "Lorg/jboss/byteman/agent/DefaultAccessEnabler;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/agent/DefaultAccessEnabler", "createConstructorInvoker", "(Ljava/lang/reflect/Constructor;Z)Lorg/jboss/byteman/agent/AccessibleConstructorInvoker;", false);
            mv.visitInsn(ARETURN);
            // } catch (IllegalAccessException e) {
            mv.visitLabel(l3);
            mv.visitVarInsn(ASTORE, 3);
            // }
            mv.visitLabel(l4);
            // return new JigsawAccessibleConstructorInvoker(privateLookup, constructor);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/jigsaw/JigsawAccessibleConstructorInvoker");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessibleConstructorInvoker", "<init>", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/reflect/Constructor;)V", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "createFieldGetter", "(Ljava/lang/reflect/Field;)Lorg/jboss/byteman/agent/AccessibleFieldGetter;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/IllegalArgumentException");
            Label l3 = new Label();
            mv.visitTryCatchBlock(l0, l1, l3, "java/lang/IllegalAccessException");
            // ensureModuleAccess(field);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "ensureModuleAccess", "(Ljava/lang/reflect/AccessibleObject;)V", false);
            // Lookup privateLookup = null;
            mv.visitInsn(ACONST_NULL);
            mv.visitVarInsn(ASTORE, 2);
            // try {
            mv.visitLabel(l0);
            // privateLookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), theLookup)
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "theLookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitLabel(l1);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            // } catch (IllegalArgumentException e) {
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            // field.setAccessible(true);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "setAccessible", "(Z)V", false);
            // return defaultAccessEnabler.createFieldGetter(method);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "defaultAccessEnabler", "Lorg/jboss/byteman/agent/DefaultAccessEnabler;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/agent/DefaultAccessEnabler", "createFieldGetter", "(Ljava/lang/reflect/Field;Z)Lorg/jboss/byteman/agent/AccessibleFieldGetter;", false);
            mv.visitInsn(ARETURN);
            // } catch (IllegalAccessException e) {
            mv.visitLabel(l3);
            mv.visitVarInsn(ASTORE, 3);
            // }
            mv.visitLabel(l4);
            // return new JigsawAccessibleFieldGetter(privateLookup, field);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter", "<init>", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/reflect/Field;)V", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "createFieldSetter", "(Ljava/lang/reflect/Field;)Lorg/jboss/byteman/agent/AccessibleFieldSetter;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/IllegalArgumentException");
            Label l3 = new Label();
            mv.visitTryCatchBlock(l0, l1, l3, "java/lang/IllegalAccessException");
            // ensureModuleAccess(field);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "ensureModuleAccess", "(Ljava/lang/reflect/AccessibleObject;)V", false);
            // Lookup privateLookup = null;
            mv.visitInsn(ACONST_NULL);
            mv.visitVarInsn(ASTORE, 2);
            // try {
            mv.visitLabel(l0);
            // privateLookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), theLookup)
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "theLookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "privateLookupIn", "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitLabel(l1);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            // } catch (IllegalArgumentException e) {
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            // field.setAccessible(true);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "setAccessible", "(Z)V", false);
            // return defaultAccessEnabler.createFieldSetter(method);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "defaultAccessEnabler", "Lorg/jboss/byteman/agent/DefaultAccessEnabler;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/jboss/byteman/agent/DefaultAccessEnabler", "createFieldSetter", "(Ljava/lang/reflect/Field;Z)Lorg/jboss/byteman/agent/AccessibleFieldSetter;", false);
            mv.visitInsn(ARETURN);
            // } catch (IllegalAccessException e) {
            mv.visitLabel(l3);
            mv.visitVarInsn(ASTORE, 3);
            // }
            mv.visitLabel(l4);
            // return new JigsawAccessibleFieldSetter(privateLookup, field);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter", "<init>", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/reflect/Field;)V", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE, "ensureModuleAccess", "(Ljava/lang/reflect/AccessibleObject;)V", null, null);
            mv.visitCode();
            // Member member = (Member)accessible;
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "java/lang/reflect/Member");
            mv.visitVarInsn(ASTORE, 2);
            // Class<?> clazz = member.getDeclaringClass();
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/reflect/Member", "getDeclaringClass", "()Ljava/lang/Class;", true);
            mv.visitVarInsn(ASTORE, 3);
            // Module module = clazz.getModule();
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getModule", "()Ljava/lang/Module;", false);
            mv.visitVarInsn(ASTORE, 4);
            // if (!module.isNamed()) {
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "isNamed", "()Z", false);
            Label l0 = new Label();
            mv.visitJumpInsn(IFNE, l0);
            // if (!THIS_MODULE.canRead(module)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/Module;");
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "canRead", "(Ljava/lang/Module;)Z", false);
            Label ltmp0 = new Label();
            mv.visitJumpInsn(IFNE, ltmp0);
            // inst.redefineModule(THIS_MODULE, Set.of(module), EMPTY_EXPORTS_MAP, EMPTY_OPENS_MAP, EMPTY_USES_SET, EMPTY_PROVIDES_MAP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "inst", "Ljava/lang/instrument/Instrumentation;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/Module;");
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Set", "of", "(Ljava/lang/Object;)Ljava/util/Set;", true);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_EXPORTS_MAP", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_OPENS_MAP", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_USES_SET", "Ljava/util/Set;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_PROVIDES_MAP", "Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/instrument/Instrumentation", "redefineModule", "(Ljava/lang/Module;Ljava/util/Set;Ljava/util/Map;Ljava/util/Map;Ljava/util/Set;Ljava/util/Map;)V", true);
            // }
            mv.visitLabel(ltmp0);
            // return;
            mv.visitInsn(RETURN);
            // }
            mv.visitLabel(l0);
            // String pkg = clazz.getPackageName();
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getPackageName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 5);
            // if (!module.isOpen(pkg, THIS_MODULE)) {
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/Module;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Module", "isOpen", "(Ljava/lang/String;Ljava/lang/Module;)Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(IFNE, l1);
            // Map<String, Set<Module>> extraOpens = Map.of(pkg, THIS_MODULE_SET);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE_SET", "Ljava/util/Set;");
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Map", "of", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;", true);
            mv.visitVarInsn(ASTORE, 6);
            // inst.redefineModule(module, EMPTY_READS_SET, EMPTY_EXPORTS_MAP, extraOpens, EMPTY_USES_SET, EMPTY_PROVIDES_MAP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "inst", "Ljava/lang/instrument/Instrumentation;");
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_READS_SET", "Ljava/util/Set;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_EXPORTS_MAP", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 6);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_USES_SET", "Ljava/util/Set;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_PROVIDES_MAP", "Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/instrument/Instrumentation", "redefineModule", "(Ljava/lang/Module;Ljava/util/Set;Ljava/util/Map;Ljava/util/Map;Ljava/util/Set;Ljava/util/Map;)V", true);
            // inst.redefineModule(THIS_MODULE, Set.of(module), EMPTY_EXPORTS_MAP, extraOpens, EMPTY_USES_SET, EMPTY_PROVIDES_MAP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "inst", "Ljava/lang/instrument/Instrumentation;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/Module;");
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Set", "of", "(Ljava/lang/Object;)Ljava/util/Set;", true);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_EXPORTS_MAP", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_OPENS_MAP", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_USES_SET", "Ljava/util/Set;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_PROVIDES_MAP", "Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/instrument/Instrumentation", "redefineModule", "(Ljava/lang/Module;Ljava/util/Set;Ljava/util/Map;Ljava/util/Map;Ljava/util/Set;Ljava/util/Map;)V", true);
            mv.visitLabel(l1);
            // }
            mv.visitInsn(RETURN);
            mv.visitMaxs(7, 7);
            mv.visitEnd();
        }
        {
            if (DEBUG) {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_VARARGS, "debug", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            // StringBuilder builder = new StringBuilder();
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 2);
            // for (String s : args)
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitVarInsn(ISTORE, 4);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 5);
            Label l0 = new Label();
            mv.visitLabel(l0);
            // {
            //
            mv.visitVarInsn(ILOAD, 5);
            mv.visitVarInsn(ILOAD, 4);
            Label l1 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l1);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(AALOAD);
            //    builder.append(s);
            mv.visitVarInsn(ASTORE, 6);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            mv.visitIincInsn(5, 1);
            mv.visitJumpInsn(GOTO, l0);
            // }
            mv.visitLabel(l1);
            // System.out.println(builder.toString());
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 7);
            mv.visitEnd();
            }
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    private static byte[] getJigsawAccessibleConstructorInvokerClassBytes()
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V9, ACC_PUBLIC + ACC_SUPER, "org/jboss/byteman/jigsaw/JigsawAccessibleConstructorInvoker", null, "java/lang/Object", new String[] { "org/jboss/byteman/agent/AccessibleConstructorInvoker" });

        cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        {
            fv = cw.visitField(ACC_PRIVATE, "handle", "Ljava/lang/invoke/MethodHandle;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/reflect/Constructor;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Constructor", "getParameterTypes", "()[Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Constructor", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findConstructor", "(Ljava/lang/Class;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Constructor", "isVarArgs", "()Z", false);
            Label l3 = new Label();
            mv.visitJumpInsn(IFEQ, l3);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asFixedArity", "()Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 4);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleConstructorInvoker", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitLabel(l1);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleConstructorInvoker : exception creating method handle for constructor ");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l4);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleConstructorInvoker", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitLabel(l1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/rule/exception/ExecuteException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleConstructorInvoker.invoke : exception invoking methodhandle ");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/rule/exception/ExecuteException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(4, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    private static byte[] getJigsawAccessibleMethodInvokerClassBytes()
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V9, ACC_PUBLIC + ACC_SUPER, "org/jboss/byteman/jigsaw/JigsawAccessibleMethodInvoker", null, "java/lang/Object", new String[]{"org/jboss/byteman/agent/AccessibleMethodInvoker"});

        cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        {
            fv = cw.visitField(ACC_PRIVATE, "handle", "Ljava/lang/invoke/MethodHandle;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "isStatic", "Z", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/reflect/Method;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getReturnType", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getModifiers", "()I", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/reflect/Modifier", "isStatic", "(I)Z", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleMethodInvoker", "isStatic", "Z");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleMethodInvoker", "isStatic", "Z");
            Label l3 = new Label();
            mv.visitJumpInsn(IFEQ, l3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 4);
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "isVarArgs", "()Z", false);
            Label l5 = new Label();
            mv.visitJumpInsn(IFEQ, l5);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asFixedArity", "()Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 4);
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleMethodInvoker", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitLabel(l1);
            Label l6 = new Label();
            mv.visitJumpInsn(GOTO, l6);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleMethodInvoker : exception creating methodhandle for method ");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l6);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
            Label l3 = new Label();
            Label l4 = new Label();
            mv.visitTryCatchBlock(l3, l4, l2, "java/lang/Throwable");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleMethodInvoker", "isStatic", "Z");
            mv.visitJumpInsn(IFEQ, l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleMethodInvoker", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitLabel(l1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleMethodInvoker", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "bindTo", "(Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitLabel(l4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/rule/exception/ExecuteException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleMethodInvoker.invoke : exception invoking methodhandle ");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/rule/exception/ExecuteException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    private static byte[] getJigsawAccessibleFieldGetterClassBytes()
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V9, ACC_PUBLIC + ACC_SUPER, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter", null, "java/lang/Object", new String[] { "org/jboss/byteman/agent/AccessibleFieldGetter" });

        cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        {
            fv = cw.visitField(ACC_PRIVATE, "handle", "Ljava/lang/invoke/MethodHandle;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(0, "isStatic", "Z", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/reflect/Field;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getModifiers", "()I", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/reflect/Modifier", "isStatic", "(I)Z", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter", "isStatic", "Z");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter", "isStatic", "Z");
            Label l3 = new Label();
            mv.visitJumpInsn(IFEQ, l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStaticGetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitJumpInsn(GOTO, l1);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findGetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitLabel(l1);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleFieldGetter : exception creating getter method handle for field ");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l4);
            mv.visitInsn(RETURN);
            mv.visitMaxs(5, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
            Label l3 = new Label();
            Label l4 = new Label();
            mv.visitTryCatchBlock(l3, l4, l2, "java/lang/Throwable");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter", "isStatic", "Z");
            mv.visitJumpInsn(IFEQ, l3);
            mv.visitVarInsn(ALOAD, 1);
            Label l5 = new Label();
            mv.visitJumpInsn(IFNULL, l5);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/rule/exception/ExecuteException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleFieldGetter.get : expecting null owner for static get!");
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/rule/exception/ExecuteException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitLabel(l1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldGetter", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitLabel(l4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/rule/exception/ExecuteException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleFieldGetter.invoke : exception invoking getter methodhandle ");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/rule/exception/ExecuteException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(5, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }


    private static byte[] getJigsawAccessibleFieldSetterClassBytes()
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V9, ACC_PUBLIC + ACC_SUPER, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter", null, "java/lang/Object", new String[] { "org/jboss/byteman/agent/AccessibleFieldSetter" });

        cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        {
            fv = cw.visitField(ACC_PRIVATE, "handle", "Ljava/lang/invoke/MethodHandle;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(0, "isStatic", "Z", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "field", "Ljava/lang/reflect/Field;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/reflect/Field;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getModifiers", "()I", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/reflect/Modifier", "isStatic", "(I)Z", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter", "isStatic", "Z");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter", "isStatic", "Z");
            Label l3 = new Label();
            mv.visitJumpInsn(IFEQ, l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStaticSetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitJumpInsn(GOTO, l1);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getDeclaringClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findSetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitFieldInsn(PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitLabel(l1);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleFieldSetter : exception creating etter method handle for field ");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l4);
            mv.visitInsn(RETURN);
            mv.visitMaxs(5, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter", "isStatic", "Z");
            Label l3 = new Label();
            mv.visitJumpInsn(IFEQ, l3);
            mv.visitVarInsn(ALOAD, 1);
            Label l4 = new Label();
            mv.visitJumpInsn(IFNULL, l4);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/rule/exception/ExecuteException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleFieldSetter.set : expecting null owner for static set!");
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/rule/exception/ExecuteException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(POP);
            mv.visitJumpInsn(GOTO, l1);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessibleFieldSetter", "handle", "Ljava/lang/invoke/MethodHandle;");
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(POP);
            mv.visitLabel(l1);
            Label l5 = new Label();
            mv.visitJumpInsn(GOTO, l5);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitTypeInsn(NEW, "org/jboss/byteman/rule/exception/ExecuteException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("JigsawAccessibleFieldSetter.set : exception invoking setter methodhandle ");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, "org/jboss/byteman/rule/exception/ExecuteException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l5);
            mv.visitInsn(RETURN);
            mv.visitMaxs(5, 4);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }


}
