/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
* @authors Andrew Dinn
*/
package org.jboss.byteman.agent.adapter;

import org.jboss.byteman.agent.TransformContext;
import org.objectweb.asm.*;

/**
 * generic asm Adapter class specialised by both check adapters (RuleCheckAdapter) and trigger
 * adapters (RuleTriggerAdapter)
 */
public class RuleAdapter extends ClassAdapter
{
    protected RuleAdapter(ClassVisitor cv, TransformContext transformContext)
    {
        super(cv);
        this.transformContext =  transformContext;
    }

    protected TransformContext transformContext;

    protected boolean matchTargetMethod(int access, String name, String desc)
    {
        return transformContext.matchTargetMethod(access, name, desc);
    }
    
    protected TransformContext getTransformContext()
    {
        return transformContext;
    }
}