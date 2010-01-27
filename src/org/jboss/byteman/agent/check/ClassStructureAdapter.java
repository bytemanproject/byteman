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
