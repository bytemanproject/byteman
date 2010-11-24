package org.jboss.byteman.agent.adapter;

/**
 */
public interface LocalScopeMethodVisitor
{
    public void visitLocalScopeStart(String name, String desc, String sig, int stackSlot, int startOffset);
    public void visitLocalScopeEnd(String name, String desc, String sig, int stackSlot, int endOffset);
}
