package org.jboss.byteman.agent.adapter;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * a class adapter which uses
 * to ensure that local variable scopes are notified during code visits
 */
public class BMLocalScopeAdapter extends ClassAdapter
{
    public BMLocalScopeAdapter(ClassVisitor cv)
    {
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        // only use the local scope adapter if we need to -- avoids creating unnecessary method nodes
        if (mv instanceof LocalScopeMethodVisitor) {
            return new BMLocalScopeMethodAdapter(mv, access, name, desc, signature, exceptions);
        } else {
            return mv;
        }
    }
}
