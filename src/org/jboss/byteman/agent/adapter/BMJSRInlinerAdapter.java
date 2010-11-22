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
    /*
    private LocalScopeMethodVisitor localScopeMethodVisitor;
    private HashMap<Label, LinkedList<LocalVariableNode>> localVarStarts;
    private HashMap<Label, LinkedList<LocalVariableNode>> localVarEnds;
    */

    public BMJSRInlinerAdapter(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
        super(mv, access, name, desc, signature, exceptions);
        if (mv instanceof LocalScopeMethodVisitor) {
            // we need to feed the next node
            /*
            localScopeMethodVisitor = (LocalScopeMethodVisitor)mv;
            localVarStarts = new HashMap<Label, LinkedList<LocalVariableNode>>();
            localVarEnds= new HashMap<Label, LinkedList<LocalVariableNode>>();
            */
            // replace the instruction list so that it generates the required start and end local scope calls
            instructions = new BMInsnList(localVariables);
        } else {
            // no need to feed  the next node
            /*
            localScopeMethodVisitor = null;
            localVarStarts = null;
            localVarEnds= null;
            */
        }
    }

    /*
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index);
        LocalVariableNode localVar = (LocalVariableNode)localVariables.get(0);
        // if necessary index this local var under the start and end labels
        if (localScopeMethodVisitor != null) {
            LinkedList<LocalVariableNode> starts = localVarStarts.get(start);
            if (starts == null) {
                starts = new LinkedList<LocalVariableNode>();
                localVarStarts.put(start, starts);
            }
            starts.add(localVar);

            LinkedList<LocalVariableNode> ends = localVarStarts.get(start);
            if (ends == null) {
                ends = new LinkedList<LocalVariableNode>();
                localVarEnds.put(end, ends);
            }
            ends.add(localVar);

        }
    }

    // TODO !!! this is wrong -- we need to intercept the label visit in the next method visitor not this one
    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        // if this is a local variable start or end label then visit the relevant local scope
        if (localScopeMethodVisitor != null) {
            LinkedList<LocalVariableNode> starts = localVarStarts.get(label);
            if (starts != null) {
                Iterator<LocalVariableNode> iterator = starts.iterator();
                while (iterator.hasNext()) {
                    LocalVariableNode localVariableNode = iterator.next();
                    String name =localVariableNode.name;
                    String desc =localVariableNode.desc;
                    String sig =localVariableNode.signature;
                    int stackSlot = localVariableNode.index;
                    localScopeMethodVisitor.visitLocalScopeStart(name, desc, sig, stackSlot);
                }
            }

            LinkedList<LocalVariableNode> ends = localVarEnds.get(label);
            if (ends != null) {
                Iterator<LocalVariableNode> iterator = ends.iterator();
                while (iterator.hasNext()) {
                    LocalVariableNode localVariableNode = iterator.next();
                    String name =localVariableNode.name;
                    String desc =localVariableNode.desc;
                    String sig =localVariableNode.signature;
                    int stackSlot = localVariableNode.index;
                    localScopeMethodVisitor.visitLocalScopeEnd(name, desc, sig, stackSlot);
                }
            }
        }
    }
    */
}
