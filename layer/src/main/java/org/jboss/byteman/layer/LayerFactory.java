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

package org.jboss.byteman.layer;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Layer;
import java.lang.module.Configuration;
import java.lang.reflect.Module;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Factory class allowing a JVMTI Java agent to
 * define a Jigsaw Module Layer containing a
 * Jigsaw module for the agent to populate
 * with classes provided as byte arrays in
 * class file format
 */

public class LayerFactory
{
    /**
     * Create z module Layer above the boot Layer and install a module into it returning
     * a classloader which can be used to drive installation of classes into the module.
     * The class laoder does not install classes from a jar file located on disk. Instead
     * the caller supplies a class mapper which is used to map class names which appear
     * as Strings in the format "x/y/z/MyClass.class" to a corresponding class file
     * format byte array.
     *
     * @param moduleName the name of the one module to be installed in the layer
     * @param exportsNames an array of names of packages to be exported by the module
     * @param requiresNames an array of names of modules to be imported by the module
     * @param classMapper a mapper provided by the caller to populate the module with
     * classes which accepts a class name and returns the corresponding class file format
     * byte array. The name will be presented in the format "x/y/z/MyClass.class".
     * </p>Ideally this parameter should be a Function<String, byte[]> but that precludes use
     * of this class from JDK6 clients so instead a Map is accepted. If the classes required
     * are capable of being predefined then a simple HasMap can be supplied populated with
     * the required class definitions. If not then a class implementing Map can be supplied
     * whose get method can construct the required classes on demand.
     *
     * @return the module classloader.
     */
    public static Module installModule(String moduleName, String[] exportsNames, String[] requiresNames, Function<String, byte[]> classMapper)
    {
        ModuleFinder finder = new LayerModuleFinder(moduleName, exportsNames, requiresNames, classMapper);

        Layer parent =  Layer.boot();
        Configuration parentConfig = parent.configuration();

        Configuration childConfig = parentConfig.resolveRequires(ModuleFinder.of(), finder, Set.of("org.jboss.byteman.jigsaw"));
        ClassLoader scl = ClassLoader.getSystemClassLoader();

        Layer layer = parent.defineModulesWithOneLoader(childConfig, scl);
        Optional<Module> optModule = layer.findModule(moduleName);
        Module module = (optModule.isPresent() ? optModule.get() : null);

        return module;
}}
