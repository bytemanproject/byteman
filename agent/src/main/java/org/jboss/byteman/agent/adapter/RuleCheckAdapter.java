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

import org.objectweb.asm.*;
import org.jboss.byteman.agent.TransformContext;

/**
 * asm Adapter class used to check that the target method for a rule exists in a class
 */
public class RuleCheckAdapter extends RuleAdapter
{
    protected RuleCheckAdapter(ClassVisitor cv, TransformContext transformContext)
    {
        super(cv, transformContext);
        this.visited = false;
        this.visitOk = false;
    }

    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited()
    {
        this.visited = true;
    }

    public boolean isVisitOk()
    {
        return visitOk;
    }

    protected void setVisitOk()
    {
        visitOk = true;
    }

    private boolean visited;
    private boolean visitOk;
}