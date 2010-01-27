package org.jboss.byteman.agent.check;

import org.jboss.byteman.rule.type.TypeHelper;
import org.objectweb.asm.ClassReader;

/**
 * a private class which can be used to derive the super and interfaces of a class from its defining bytecode
 */
public class BytecodeChecker implements ClassChecker {
    ClassStructureAdapter adapter;

    public BytecodeChecker(byte[] buffer) {
        // run a pass over the bytecode to identify the interfaces
        ClassReader cr = new ClassReader(buffer);
        adapter = new ClassStructureAdapter();
        cr.accept(adapter, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }

    public boolean isInterface() {
        return adapter.isInterface();
    }

    public String getSuper() {
        return TypeHelper.internalizeClass(adapter.getSuper());
    }

    public boolean hasOuterClass() {
        return adapter.getOuterClass() != null;
    }

    public int getInterfaceCount() {
        return adapter.getInterfaces().length;
    }

    public String getInterface(int idx) {
        return TypeHelper.internalizeClass(adapter.getInterfaces()[idx]);
    }
}
