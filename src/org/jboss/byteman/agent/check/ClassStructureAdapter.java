/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010 Red Hat and individual contributors
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
 * (C) 2010,
 * @authors Andrew Dinn
 */

package org.jboss.byteman.agent.check;

import org.objectweb.asm.*;

/**
 * a simple and <em>quick</em> adapter used to scan a class's bytecode definition for the name of its superclass,
 * its enclosing class and the interfaces it implements directly
 */

public class ClassStructureAdapter implements ClassVisitor {
    private boolean isInterface = false;
    private String[] interfaces = null;
    private String superName = null;
    private String outerClass = null;

    public boolean isInterface() {
        return isInterface;
    }

    public String getSuper() {
        return superName;
    }

    public String getOuterClass() {
        return outerClass;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        this.interfaces = interfaces;
        this.superName = superName;
    }

    public void visitSource(String source, String debug) {
        // do nothimg
    }

    public void visitOuterClass(String owner, String name, String desc) {
        outerClass = owner;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return null;
    }

    public void visitAttribute(Attribute attr) {
        // do nothimg
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        // do nothimg
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return null;
    }

    public void visitEnd() {
        // do nothimg
    }
}
