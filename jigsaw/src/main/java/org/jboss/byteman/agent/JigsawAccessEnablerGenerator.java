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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * class which provides a bytecode definition for class JigsawAccessEnabler
 */
public class JigsawAccessEnablerGenerator
{
    public static byte[] getJigsawAccessEnablerClassBytes()
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;


        // package org/jboss/byteman/jigsaw;
        // public class JigsawAccessEnabler impldements AccessEnabler { ... }
        cw.visit(Opcodes.V1_9, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "org/jboss/byteman/jigsaw/JigsawAccessEnabler",
                 null, "java/lang/Object", new String[] { "org/jboss/byteman/agent/AccessEnabler"});

        // this is because we use String + String
        cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC);

        {
            // private Module THIS_MODULE = this.getClass().getModule();
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "THIS_MODULE", "Ljava/lang/reflect/Module;", null, null);
            fv.visitEnd();
        }
        {
            // private Set<Module> THIS_MODULE_SET = Set.of(THIS_MODULE);
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "THIS_MODULE_SET", "Ljava/util/Set;", "Ljava/util/Set<Ljava/lang/reflect/Module;>;", null);
            fv.visitEnd();
        }
        {
            // private Set<Module> EMPTY_READS_SET = Set.of();
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "EMPTY_READS_SET", "Ljava/util/Set;", "Ljava/util/Set<Ljava/lang/reflect/Module;>;", null);
            fv.visitEnd();
        }
        {
            //  private Map<String,<Set<Module>> EMPTY_EXPORTS_MAP = Map.of();
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "EMPTY_EXPORTS_MAP", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/reflect/Module;>;", null);
            fv.visitEnd();
        }
        {
            // private Set<Class<?>> EMPTY_USES_SET = Set.of()
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "EMPTY_USES_SET", "Ljava/util/Set;", "Ljava/util/Set<Ljava/lang/Class<*>;>;", null);
            fv.visitEnd();
        }
        {
            // private Map<Class<?>,Set<Class?>>> EMPTY_PROVIDES_MAP = Map.of();
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "EMPTY_PROVIDES_MAP", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/Class<*>;Ljava/util/List<Ljava/lang/Class<*>;>;>;", null);
            fv.visitEnd();
        }
        {
            //  private Instrumentation inst;
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "inst", "Ljava/lang/instrument/Instrumentation;", null, null);
            fv.visitEnd();
        }
        {
            // public JigsawAccessEnabler(Instrumentation inst)   {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/instrument/Instrumentation;)V", null, null);
            mv.visitCode();
            // super()
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            // this.THIS_MODULE = this.getClass().getModule();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getModule", "()Ljava/lang/reflect/Module;", false);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/reflect/Module;");
            // this.THIS_MODULE_SET = Set.of(this.THIS_MODULE);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/reflect/Module;");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Set", "of", "(Ljava/lang/Object;)Ljava/util/Set;", true);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE_SET", "Ljava/util/Set;");
            // this.EMPTY_READS_SET = Set.of();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Set", "of", "()Ljava/util/Set;", true);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_READS_SET", "Ljava/util/Set;");
            // this.EMPTY_EXPORTS_MAP = Map.of();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Map", "of", "()Ljava/util/Map;", true);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_EXPORTS_MAP", "Ljava/util/Map;");
            // this.EMPTY_USES_SET = Set.of()
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Set", "of", "()Ljava/util/Set;", true);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_USES_SET", "Ljava/util/Set;");
            // this.EMPTY_PROVIDES_MAP = Map.of();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Map", "of", "()Ljava/util/Map;", true);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_PROVIDES_MAP", "Ljava/util/Map;");
            // if this.THIS_MODULE.isnamed()
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/reflect/Module;");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Module", "isNamed", "()Z", false);
            Label l0 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l0);
            // then
            // throw Opcodes.NEW RuntimeException("JigsawAccessEnabler : can only enable Jigsaw access from a named module not " + THIS_MODULE);
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/reflect/Module;");
            mv.visitInvokeDynamicInsn("makeConcatWithConstants", "(Ljava/lang/reflect/Module;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;"), new Object[]{"JigsawAccessEnabler : can only enable Jigsaw access from a named module not \u0001"});
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
            // endif
            mv.visitLabel(l0);
            // if (inst == null)
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            Label l1 = new Label();
            mv.visitJumpInsn(Opcodes.IFNONNULL, l1);
            // then
            // throw New RuntimeException("JigsawAccessEnabler : can only be created if passed a real Instrumentation handle");
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn("JigsawAccessEnabler : can only be created if passed a real Instrumentation handle");
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
            // endif
            mv.visitLabel(l1);
            // this.inst = inst;
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitFieldInsn(Opcodes.PUTFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "inst", "Ljava/lang/instrument/Instrumentation;");
            // return
            mv.visitInsn(Opcodes.RETURN);
            // }
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
            // public boolean requiresAccess(AccessibleObject) {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "requiresAccess", "(Ljava/lang/reflect/AccessibleObject;)Z", null, null);
            mv.visitCode();
            // Member member = (Member)accessible;
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/reflect/Member");
            mv.visitVarInsn(Opcodes.ASTORE, 2);
            // if (!Modifier.isPublic(member.getModifiers())
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/reflect/Member", "getModifiers", "()I", true);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/reflect/Modifier", "isPublic", "(I)Z", false);
            Label l0 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l0);
            // then
            // return true
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IRETURN);
            // endif
            mv.visitLabel(l0);
            // Class<?> clazz = member.getDeclaringClass();
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/reflect/Member", "getDeclaringClass", "()Ljava/lang/Class;", true);
            mv.visitVarInsn(Opcodes.ASTORE, 3);
            // if (!Modifier.isPublic(clazz.getModifiers()))
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getModifiers", "()I", false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/reflect/Modifier", "isPublic", "(I)Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l1);
            // then
            // return true
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IRETURN);
            // endif
            mv.visitLabel(l1);
            // while (clazz.isMemberClass())
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "isMemberClass", "()Z", false);
            Label l2 = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, l2);
            // do {
            // clazz = clazz.getEnclosingClass();
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getEnclosingClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 3);
            // if (!Modifier.isPublic(clazz.getModifiers()))
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getModifiers", "()I", false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/reflect/Modifier", "isPublic", "(I)Z", false);
            mv.visitJumpInsn(Opcodes.IFNE, l1);
            // then
            // return true
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IRETURN);
            // endif
            // } done
            mv.visitLabel(l2);
            // Module module = clazz.getModule();
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getModule", "()Ljava/lang/reflect/Module;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 4);
            // if (!module.isNamed())
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Module", "isNamed", "()Z", false);
            Label l3 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l3);
            // then
            // return false
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitInsn(Opcodes.IRETURN);
            // endif
            mv.visitLabel(l3);
            // String pkg = clazz.getPackageName();
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getPackageName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 5);
            // if (module.isOpen(pkg, this.THIS_MODULE))
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitVarInsn(Opcodes.ALOAD, 5);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/reflect/Module;");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Module", "isOpen", "(Ljava/lang/String;Ljava/lang/reflect/Module;)Z", false);
            Label l4 = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, l4);
            // then
            // return false
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitInsn(Opcodes.IRETURN);
            // endif
            mv.visitLabel(l4);
            // return true
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IRETURN);
            // }
            mv.visitMaxs(3, 6);
            mv.visitEnd();
        }
        {
            // public void ensureAccess(AccessibleObject) {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "ensureAccess", "(Ljava/lang/reflect/AccessibleObject;)V", null, null);
            mv.visitCode();
            //  ensureModuleAccess(accessible);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "ensureModuleAccess", "(Ljava/lang/reflect/AccessibleObject;)V", false);//
            // accessible.setAccessible(true);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/AccessibleObject", "setAccessible", "(Z)V", false);
            mv.visitInsn(Opcodes.RETURN);
            // }
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            // public void ensureModuleAccess(AccessibleObject) {
            mv = cw.visitMethod(Opcodes.ACC_PRIVATE, "ensureModuleAccess", "(Ljava/lang/reflect/AccessibleObject;)V", null, null);
            mv.visitCode();
            // Member member = (Member)accessible;
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/reflect/Member");
            mv.visitVarInsn(Opcodes.ASTORE, 2);
            // Class<?> clazz = member.getDeclaringClass();
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/reflect/Member", "getDeclaringClass", "()Ljava/lang/Class;", true);
            mv.visitVarInsn(Opcodes.ASTORE, 3);
            // Module module = clazz.getModule();
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getModule", "()Ljava/lang/reflect/Module;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 4);
            // if (!module.isNamed())
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Module", "isNamed", "()Z", false);
            Label l0 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l0);
            // then
            // return
            mv.visitInsn(Opcodes.RETURN);
            // endif
            mv.visitLabel(l0);
            // String pkg = clazz.getPackageName();
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getPackageName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 5);
            // if (!module.isOpen(pkg, this.THIS_MODULE))
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitVarInsn(Opcodes.ALOAD, 5);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE", "Ljava/lang/reflect/Module;");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Module", "isOpen", "(Ljava/lang/String;Ljava/lang/reflect/Module;)Z", false);
            Label l2 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l2);
            // then
            // Map<String, Set<Module>> extraExports = Map.of(pkg, this.THIS_MODULE_SET);
            mv.visitVarInsn(Opcodes.ALOAD, 5);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "THIS_MODULE_SET", "Ljava/util/Set;");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Map", "of", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;", true);
            mv.visitVarInsn(Opcodes.ASTORE, 6);
            // this.inst.redefineModule(module, this.EMPTY_READS_SET, extraExports, this.EMPTY_USES_SET, this.EMPTY_PROVIDES_MAP);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "inst", "Ljava/lang/instrument/Instrumentation;");
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_READS_SET", "Ljava/util/Set;");
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_EXPORTS_MAP", "Ljava/util/Map;");
            mv.visitVarInsn(Opcodes.ALOAD, 6);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_USES_SET", "Ljava/util/Set;");
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/jboss/byteman/jigsaw/JigsawAccessEnabler", "EMPTY_PROVIDES_MAP", "Ljava/util/Map;");
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/instrument/Instrumentation", "redefineModule", "(Ljava/lang/reflect/Module;Ljava/util/Set;Ljava/util/Map;Ljava/util/Map;Ljava/util/Set;Ljava/util/Map;)V", true);
            // endif
            mv.visitLabel(l2);
            // return
            mv.visitInsn(Opcodes.RETURN);
            // }
            mv.visitMaxs(6, 7);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
