/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat and individual contributors
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
import org.jboss.byteman.rule.helper.Helper;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * Class used to construct an AccessEnabler appropriate to the
 * JDK Byteman is running in i.e. whether or not it includes
 * modules.
 */
public class AccessManager
{
    /**
     * Create and return an AccessEnabler to manage enabling
     * reflective access.
     *
     * For JDK8 and lower releases return a DefaultAccessEnabler
     * which does not now about module encapsulation.
     *
     * For JDK9 and higher releases return  a JigsawAccessEnabler
     * which is capable of enabling access to members of classes which
     * are normally inaccessible because of module restrictions.
     *
     * caveat: during testing class JigsawAccessEnabler may fail to
     * load even thought the JDK is modular. That happens when testing
     * that the JDK8- core classes work without the JDK9 code present.
     * In this specific  situation a DefaultAccessEnabler is returned.
     * A helper trace message is logged just in case.
     *
     * @param inst an Instrumentation instance which may be needed
     * enable access to members of unexported module classes
     * @return  an AccessEnabler to manage enabling reflective access
     */
    public static AccessEnabler init(Instrumentation inst)
    {
        // if 1) we have a module system and 2) we can load an access manager
        // for the module system then we delegate to it otherwise we use a
        // default access enabler

        ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
        try {
            systemLoader.loadClass("java.lang.reflect.Module");
        } catch (ClassNotFoundException ce) {
            //  we are not in a Jigsaw runtime so use
            // a default access enabler
            return initDefault();
        }

        // ok let's look for a Jigsaw access enabler
        try {
            Class<?> jigsawAccessManagerClazz = systemLoader.loadClass("org.jboss.byteman.agent.JigsawAccessManager");
            try {
                Method initMethod = jigsawAccessManagerClazz.getDeclaredMethod("init", Instrumentation.class);
                AccessEnabler accessEnabler = (AccessEnabler) initMethod.invoke(null, inst);
                return accessEnabler;
            } catch (Exception e) {
                // this should not happen
                Helper.err("AccessManager:init unexpected error initialising JigsawAccessManager");
                Helper.errTraceException(e);
                // continue with a default accessor
                // TODO - maybe revise this decision
                return initDefault();
            }
        } catch (ClassNotFoundException e) {
            // this can happen legitimately when we
            // testthe base agent jar or even if someone
            // decides to use it without JDK9 support
            // generate verbose trace for now but ...
            // TODO replace wirth noisy trace when that gets implemented
            Helper.verbose("AccessManager:init hmm, JigsawAccessManager not present in Jigsaw runtime!");
            return initDefault();
        }
    }

    private static AccessEnabler initDefault()
    {
        Helper.verbose("AccessManager:init Initialising default AccessManager");
        return new DefaultAccessEnabler();
    }
}
