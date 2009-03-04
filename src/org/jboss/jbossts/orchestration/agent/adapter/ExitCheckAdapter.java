package org.jboss.jbossts.orchestration.agent.adapter;

import org.objectweb.asm.*;

/**
 * asm Adapter class used to check that the target method for a rule exists in a class
 */
public class ExitCheckAdapter extends RuleCheckAdapter
{
    public ExitCheckAdapter(ClassVisitor cv, String targetClass, String targetMethod) {
        super(cv, targetClass, targetMethod);
        // all methods return at some point so we always insert a trigger somewhere
        setVisitOk();
    }
}
