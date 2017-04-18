/*
* JBoss, Home of Professional Open Source
* Copyright 2015 Red Hat and individual contributors
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
* @authors James Livingston
*/

package org.jboss.byteman.modules.jbossmodules;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.byteman.modules.ClassbyteClassLoader;
import org.jboss.byteman.modules.ModuleSystem;
import org.jboss.byteman.rule.helper.Helper;
import org.jboss.modules.DependencySpec;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleFinder;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.ModuleSpec;
import org.jboss.modules.ModuleSpec.Builder;

public class JBossModulesSystem implements ModuleSystem<ClassbyteClassLoader>
{
    private boolean lazyInited = false;
    private ModuleLoader ruleModuleLoader; // where the rule modules are stores

    private final AtomicBoolean warningEmitted = new AtomicBoolean(false);

    private final AtomicLong currentRuleNumber = new AtomicLong();
    private Map<ModuleIdentifier, ModuleSpec> ruleModules;

    private static final String RULE_MODULE_PREFIX = "byteman.rule";
    private static final Set<String> BYTEMAN_PACKAGE_PATHS;
    static
    {
        BYTEMAN_PACKAGE_PATHS = new HashSet<String>();
        BYTEMAN_PACKAGE_PATHS.add("org/jboss/byteman/rule/exception");
        BYTEMAN_PACKAGE_PATHS.add("org/jboss/byteman/rule/helper");
    }

    public void initialize(String args)
    {
        if (!args.isEmpty())
            Helper.err("Unexpcted module system arguments: " + args);

        // NOTE: this will be run at agent start, be careful
    }

    private synchronized void lazyInit()
    {
        if (lazyInited)
            return;

        // perform any initialization that can't be done at agent start time
        lazyInited = true;

        // store all the module specification in an instance map.
        ruleModules = new ConcurrentHashMap<ModuleIdentifier, ModuleSpec>();
        ModuleFinder[] finders = new ModuleFinder[] {
            new ModuleFinder() {
                public ModuleSpec findModule(ModuleIdentifier identifier,
                                ModuleLoader delegateLoader) throws ModuleLoadException {
                    return ruleModules.get(identifier);
                }
            }
        };
        ruleModuleLoader = new ModuleLoaderWrapper(finders);
    }
    
    /**
     * Utility class to allow instantiating {@link ModuleLoader} with jboss-modules version 1.2.0 and earlier (protected constructors)
     */
    public class ModuleLoaderWrapper extends ModuleLoader
    {
        public ModuleLoaderWrapper(ModuleFinder[] finders) {
            super(finders);
        }
    }

    public ClassbyteClassLoader createLoader(ClassLoader triggerClassLoader, String[] imports)
    {
        if (imports.length == 0) {
            // do the same thing as NonModuleSystem
            return new ClassbyteClassLoader(triggerClassLoader);
        } else {
            lazyInit();

            try
            {
                // Installing the real MBean server is the last thing that JBoss Modules (1.4.3) does before calling Module.run
                // if it's there, we should be safe to use JBoss Modules
                Field regRefField = ModuleLoader.class.getDeclaredField("REG_REF");
                regRefField.setAccessible(true);
                Object regRef = regRefField.get(null);
                if (regRef == null) {
                    return warnAndFallback(triggerClassLoader, "ModuleLoader.REG_REF is null, JBoss Modules internals may have changed. Assuming it is unsafe to use");
                }
                String regRefName = regRef.getClass().getName();
                if (regRefName.equals("org.jboss.modules.ModuleLoader$TempMBeanReg")) {
                    return warnAndFallback(triggerClassLoader, "ModuleLoader.REG_REF is TempMBeanReg, JBoss Modules is not fully loaded. Assuming it is unsafe to use");
                } else if (regRefName.equals("org.jboss.modules.ModuleLoader$RealMBeanReg")) {
                    // JBoss Modules appears to be loaded
                    return createModularLoader(triggerClassLoader, imports);
                } else {
                    return warnAndContinue(triggerClassLoader, imports, "Unknown ModuleLoader.REG_REF implementation " + regRefName + ", JBoss Modules internals may have changed. Assuming it is safe to use");
                }
            } catch (SecurityException e) {
                return warnAndContinue(triggerClassLoader, imports, e, "SecurityException accessing ModuleLoader.REG_REF, JBoss Modules internals may have changed. Assuming it is safe to use");
            } catch (NoSuchFieldException e) {
                return warnAndContinue(triggerClassLoader, imports, e, "Could not detect ModuleLoader.REG_REF, JBoss Modules internals may have changed. Assuming it is safe to use");
            } catch (IllegalAccessException e) {
                return warnAndContinue(triggerClassLoader, imports, e, "Could not access ModuleLoader.REG_REF, JBoss Modules internals may have changed. Assuming it is safe to use");
            }
        }
    }

    public void destroyLoader(ClassbyteClassLoader loader)
    {
        if (loader instanceof ModuleUsingClassbyteClassLoader) {
            //TODO: is removing this safe?
            ModuleUsingClassbyteClassLoader moduleLoader = (ModuleUsingClassbyteClassLoader) loader;
            ModuleIdentifier moduleIdentifier = moduleLoader.getModule().getIdentifier();
            ruleModules.remove(moduleIdentifier);
        } else {
            // do nothing
        }
    }

    public Class<?> loadHelperAdapter(ClassbyteClassLoader helperLoader, String helperAdapterName, byte[] classBytes)
    {
        return helperLoader.addClass(helperAdapterName, classBytes);
    }



    protected ClassbyteClassLoader createModularLoader(ClassLoader triggerClassLoader, String[] imports)
    {
        try {
            long ruleNumber = currentRuleNumber.incrementAndGet();
            ModuleIdentifier ruleModuleIdentifier = ModuleIdentifier.create(RULE_MODULE_PREFIX + "." + ruleNumber);
            Builder builder = ModuleSpec.build(ruleModuleIdentifier);

            // add the package for Byteman bits that the classes need to see
            builder.addDependency(DependencySpec.createSystemDependencySpec(BYTEMAN_PACKAGE_PATHS));

            // add the imports
            // TODO: should we use the module loader of 'triggerClassLoader' if it has one?
            //   ModuleLoader.forClassLoader(triggerClassLoader);
            // FIXME: if JBoss modules has not yet been initialized, this will break things
            ModuleLoader bootModuleLoader = Module.getBootModuleLoader();
            for (int i = 0; i < imports.length; i++) {
                ModuleIdentifier importIdentifier = ModuleIdentifier.fromString(imports[i]);
                DependencySpec importDepSpec = DependencySpec.createModuleDependencySpec(bootModuleLoader, importIdentifier, false);
                builder.addDependency(importDepSpec);
            }

            ruleModules.put(ruleModuleIdentifier, builder.create());
            return new ModuleUsingClassbyteClassLoader(ruleModuleLoader.loadModule(ruleModuleIdentifier), triggerClassLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ClassbyteClassLoader warnAndFallback(ClassLoader triggerClassLoader, String message)
    {
        if (!warningEmitted.getAndSet(true)) {
            Helper.err(message);
        }
        return new ClassbyteClassLoader(triggerClassLoader);
    }

    protected ClassbyteClassLoader warnAndContinue(ClassLoader triggerClassLoader, String[] imports, String message)
    {
        if (!warningEmitted.getAndSet(true)) {
            Helper.err(message);
        }
        return createModularLoader(triggerClassLoader, imports);
    }

    protected ClassbyteClassLoader warnAndContinue(ClassLoader triggerClassLoader, String[] imports, Exception e, String message)
    {
        if (!warningEmitted.getAndSet(true)) {
            Helper.err(message);
            Helper.errTraceException(e);
        }
        return createModularLoader(triggerClassLoader, imports);
    }
}
