package org.jboss.byteman.agent.adapter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

/**
 * a subclass of JSRInlinerAdapter which pushes local variable info through to the next
 * adapter inline during code generation if it wants it
 */
public class BMLocalScopeMethodAdapter extends MethodNode
{
    private MethodVisitor mv;

    /**
     * creates a method node with an instruction list which notifies local var scope start and end
     * events. should only be called with a method visitor which is an instance of LocalScopeMethodVisitor
     * @param mv
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     */
    public BMLocalScopeMethodAdapter(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
        super(access, name, desc, signature, exceptions);
        this.mv = mv;
        if (mv instanceof LocalScopeMethodVisitor) {
            // replace the instruction list so that it generates the required start and end local scope calls
            instructions = new BMInsnList(localVariables);
        }
    }

    /**
     * once we have seen all the opcodes we can push the stored  method tree through the next visitor in line
     */
    public void visitEnd()
    {
        accept(mv);
    }
}
