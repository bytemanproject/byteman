package org.jboss.byteman.agent.adapter;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;

/**
 * Class visitor which uses a JSRInlinerAdpater to replace JSR/RET sequences in incoming method code
 * with embedded bytecode
 */
public class JSRInliner extends ClassAdapter
{
    public JSRInliner(ClassVisitor cv)
    {
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions);
    }
}
