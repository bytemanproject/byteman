package org.jboss.byteman.agent.adapter;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.LocalVariableNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * a subclass of JSRInlinerAdapter which pushes local variable info through to the next
 * adapter inline during code generation if it wants it
 */
public class BMJSRInlinerAdapter extends JSRInlinerAdapter
{
    public BMJSRInlinerAdapter(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
        super(mv, access, name, desc, signature, exceptions);
        if (mv instanceof LocalScopeMethodVisitor) {
            // replace the instruction list so that it generates the required start and end local scope calls
            instructions = new BMInsnList(localVariables);
        }
    }
}
