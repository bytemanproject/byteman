package org.jboss.byteman.agent.adapter;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * a modified version of JSRInliner which uses a slightly modified version of JSRInlinerAdapter
 * to ensure that local variable scopes are notified during code visits
 */
public class BMJSRInliner extends ClassAdapter
{
    public BMJSRInliner(ClassVisitor cv)
    {
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new BMJSRInlinerAdapter(mv, access, name, desc, signature, exceptions);
    }
}
