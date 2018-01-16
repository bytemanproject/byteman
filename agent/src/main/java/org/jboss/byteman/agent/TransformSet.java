/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat and individual contributors
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

package org.jboss.byteman.agent;

import org.jboss.byteman.rule.Rule;

import java.util.ArrayList;
import java.util.List;
/**
 * A TransformSet groups together a set of Transform records which
 * share a common classloader and trigger class name. This grouping
 * ensures that all transforms arising from a specific retransform
 * operation can be managed as a unit. In particular this is needed
 * in order to allow installation and uninstallation of a rule to
 * be performed consistently.
 */
public class TransformSet
{
    private ClassLoader loader;
    private String triggerClass;
    private List<Transform> transforms;
    private Rule installedRule;

    public TransformSet(ClassLoader loader, String triggerClass)
    {
        this.loader = loader;
        this.triggerClass = triggerClass;
        this.transforms = new ArrayList<Transform>();
        this.installedRule = null;
    }

    public boolean isFor(ClassLoader loader, String triggerClass)
    {
        return this.loader == loader && this.triggerClass.equals(triggerClass);
    }

    public void add(Transform transform)
    {
        transforms.add(transform);
    }

    public ClassLoader getLoader()
    {
        return loader;
    }

    public String getTriggerClass() {
        return triggerClass;
    }

    /*
     * check whether a rule has been installed for this transform
     */
    public boolean isInstalled()
    {
        return getInstalledRule() != null;
    }

    public void setInstalled(Rule rule)
    {
        installedRule = rule;
    }

    public Rule getInstalledRule()
    {
        return installedRule;
    }

    public List<Transform> getTransforms()
    {
        return transforms;
    }
    
    public boolean isEmpty()
    {
        return transforms.isEmpty();
    }

    public void clearTransforms()
    {
        transforms.clear();
    }
}
