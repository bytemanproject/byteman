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
import org.jboss.byteman.rule.helper.Helper;

import java.util.ArrayList;
import java.util.List;
/**
 * A TransformSet groups together a set of Transform records which
 * share a common classloader, trigger class name (and RuleScript).
 * The set includes details of successful or failed transforms.
 *
 * This grouping ensures that all transforms arising from a specific
 * retransform operation for a new, modified or deleted script can
 * be managed as a unit. In particular this is needed in order to
 * allow installation and uninstallation of a rule to be performed
 * consistently.
 *
 * Note that although the loader and trigger class name uniquely
 * identify a single trigger class a transform set may still
 * contain more than one successful transform. That is possible
 * because the RuleScript may omit a descriptor and hence may match
 * multiple overloaded variants of the method named in the rule's
 * METHOD clause.
 */
public class TransformSet
{
    private ClassLoader loader;
    private String triggerClass;
    private List<Transform> transforms;
    private Rule rule;

    public TransformSet(ClassLoader loader, String triggerClass)
    {
        this.loader = loader;
        this.triggerClass = triggerClass;
        this.transforms = new ArrayList<Transform>();
        this.rule = null;
    }

    public boolean isFor(ClassLoader loader, String triggerClass)
    {
        return this.loader == loader && this.triggerClass.equals(triggerClass);
    }

    public void add(Transform transform)
    {
        if (transform.getThrowable() == null) {
            // transform was successful. see if we have another
            // transform for the same method and if so just stick
            // with the existing one as both are equivalent
            String key = transform.getRule().getKey();
            for (Transform current : transforms) {
                if (current.getRule() != null && key.equals(current.getRule().getKey())) {
                    // the other transform should not have resulted in an error
                    if (transform.getThrowable() != null && Transformer.isVerbose()) {
                        Helper.verbose("TransformSet.add : mismatch between successful and failed transforms with key " + key);
                        Helper.verboseTraceException(transform.getThrowable());
                    }
                    return;
                }
            }
        }
        // add the new transform to the list
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
        return getRule() != null;
    }

    public void setInstalled(Rule key)
    {
        this.rule = key;
    }

    public Rule getRule()
    {
        return rule;
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
