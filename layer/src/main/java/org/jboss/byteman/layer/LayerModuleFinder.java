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

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
/**
 * ModuleFinder implementation used by the module installed by LayerFactory
 * to locate class definitions when an attempt is made to load a class into
 * the module. The finder uses a ModuelReader which passes the request back
 * to the installer of the module, allowing it either to generate or load
 * the class definition as it sees fit.
 */
public class LayerModuleFinder implements ModuleFinder
{
    /**
     * the name of the single module this module finder finds classes for
     */
    private final String moduleName;
    /**
     * a module reference describing this finder's module
     */
    private ModuleReference reference;

    /**
     * construct a finder for a single module populated with classes
     * by the supplier classmapper
     *
     * @param moduleName the name of the one module to be installed in the layer
     * @param exportsNames an array of names of packages to be exported by the module
     * @param requiresNames an array of names of modules to be imported by the module
     * @param classMapper a function provided by the caller to populate the module with
     * classes which accepts a class name and returns the corresponding class file format
     * byte array. The name will be presented in the format "x/y/z/MyClass.class".
     */
    public LayerModuleFinder(String moduleName, String[] exportsNames, String[] requiresNames, Function<String, byte[]> classMapper)
    {
        // this URI may be used by the security manager to
        // associate security permissions with classes loaded
        // by the class loader
        //
        // n.b. we have to provide a scheme for the URI because
        // of a bug which leads to an attempt to convert t to a URL
        // that needs fixing but for now we use the fake scheme
        // "byteman" -- that's ok so long as nothing tries to use
        // it in a URL
        URI uri = null;
        try {
            uri = new URI("byteman", moduleName, null);
        } catch (URISyntaxException e) {
        }
        ModuleDescriptor.Builder builder =  ModuleDescriptor.module(moduleName);
        for(String required : requiresNames) {
            builder.requires(required);
        }
        for(String exported : exportsNames) {
            builder.exports(exported);
        }
        ModuleDescriptor desc = builder.build();

        final ModuleReader reader = new LayerModuleReader(classMapper);
        ModuleReference reference = new ModuleReference(desc, uri, new Supplier<ModuleReader>() {
            @Override
            public ModuleReader get()
            {
                return reader;
            }
        });

        this.moduleName = moduleName;
        this.reference = reference;
    }

    @Override
    public Optional<ModuleReference> find(String name)
    {
        if (moduleName.equals(name)) {
            return Optional.of(reference);
        };

        return null;
    }

    @Override
    public Set<ModuleReference> findAll()
    {
        return Set.of(reference);
    }

}