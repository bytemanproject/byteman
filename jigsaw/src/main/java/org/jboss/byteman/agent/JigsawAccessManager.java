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
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Module;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.jboss.byteman.layer.LayerFactory;
import org.jboss.byteman.rule.helper.Helper;

/**
 * Class used to construct a JigsawAccessEnabler needed when Byteman
 * is running on JDK9+.
 */
public class JigsawAccessManager
{
    /**
     * Create and return a JigsawAccessEnabler to manage enabling
     * reflective access including accessing nonmembers of
     * unexported module classes.
     *
     * @param inst an Instrumentation instance which is needed
     * enable access to members of non-exported module classes
     * @return  a JigsawAccessEnabler to manage enabling reflective access
     */
    public static AccessEnabler init(Instrumentation inst)
    {
        System.out.println("AccessManager:init Initialising JDK9 AccessManager");
        if(inst == null) {
            Helper.verbose("AccessManager:init No instrumentation provided -- using default AccessEnabler");
            return new DefaultAccessEnabler();
        }

        // create a layer populated with the org.jboss.byteman.jigsaw module

        Module module = LayerFactory.installModule("org.jboss.byteman.jigsaw",
                                                   new String[]{"org.jboss.byteman.jigsaw"},
                                                   new String[]{"java.instrument"},
                                                   new Function<String, byte[]>()
                                                        {

                                                            @Override
                                                            public byte[] apply(String s)
                                                            {
                                                                if(s.equals("org/jboss/byteman/jigsaw/JigsawAccessEnabler.class")) {
                                                                    return JigsawAccessEnablerGenerator.getJigsawAccessEnablerClassBytes();
                                                                }
                                                                return null;
                                                            }
                                                        });

        Helper.verbose("AccessManager:init created module");

        if (module == null) {
            return new DefaultAccessEnabler();
        }

        // the new module needs to read this (default) module so it can implement interface AccessEnabler
        /*
        Set<Module> extraReads = Set.of(AccessEnabler.class.getModule());
        Map<String, Set<Module>> extraExports = Map.of();
        Map<String, Set<Module>> extraExportsPrivate = extraExports;
        Set<Class<?>> extraUses = Set.of();
        Map<Class<?>, Set<Class<?>>> extraProvides = Map.of();
        */
        Set<Module> extraReads = new HashSet<Module>();
        extraReads.add(AccessEnabler.class.getModule());
        Map<String, Set<Module>> extraExports = new HashMap<String, Set<Module>>();
        Map<String, Set<Module>> extraExportsPrivate = extraExports;
        Set<Class<?>> extraUses = new HashSet<Class<?>>();
        Map<Class<?>, Set<Class<?>>> extraProvides = new HashMap<Class<?>, Set<Class<?>>>();

        inst.redefineModule(module, extraReads, extraExports, extraExportsPrivate, extraUses, extraProvides);

        Helper.verbose("AccessManager:init added extraReads");

        // now we can inject class JigsawAccessEnabler into the module
        // allowing it to add module access permissions ad lib
        try {
            ClassLoader loader = module.getClassLoader();
            Class<?> enablerClazz = loader.loadClass("org.jboss.byteman.jigsaw.JigsawAccessEnabler");
            Constructor<?> constructor = enablerClazz.getConstructor(Instrumentation.class);
            AccessEnabler accessEnabler = (AccessEnabler) constructor.newInstance(inst);
            Helper.verbose("AccessManager:init returning JigsawAccessEnabler");
            return accessEnabler;
        } catch (Exception e) {
            Helper.err("AccessManager:init oops! returning DefaultAccessEnabler ");
            Helper.errTraceException(e);
            // continue with a default accessor
            // TODO - maybe revise this decision
            return new DefaultAccessEnabler();
        }
    }
}