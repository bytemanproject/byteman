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

package org.jboss.byteman.jigsaw;

import org.jboss.byteman.agent.AccessEnabler;
import org.jboss.byteman.agent.AccessibleConstructorInvoker;
import org.jboss.byteman.agent.AccessibleFieldGetter;
import org.jboss.byteman.agent.AccessibleFieldSetter;
import org.jboss.byteman.agent.AccessibleMethodInvoker;
import org.jboss.byteman.agent.DefaultAccessEnabler;
import org.jboss.byteman.rule.exception.ExecuteException;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Module;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * Implementation of AccessEnabler for use in a
 * Jigsaw enabled JDK runtime
 * n.b. this class is only exemplary and must not
 * be compiled into the Byteman jar. It is supposed
 * to be generated at runtime and installed by the
 * classloader for the org.jboss.byteman.jigsaw module
 * created by the LayerFactory.
 */
public class JigsawAccessEnabler implements AccessEnabler
{
    /**
     * flag to allow debug trace to be generated
     */
    public boolean DEBUG = false;

    /**
     * the single Byteman module to which reflective access
     * is granted by exporting packages as necessary
     */
    private Module THIS_MODULE = this.getClass().getModule();
    /**
     * the module to which the rest of the Byteman code belongs
     * which should be the system or bootstrap unnamed module
     */
    private Module UNPRIVILEGED_MODULE = AccessEnabler.class.getModule();
    /**
     * singleton set passed to specify the single target module for an addExports call
     */
    private Set<Module> THIS_MODULE_SET = Set.of(THIS_MODULE);
    /**
     * empty reads set passed to an addExports call
     */
    private Set<Module> EMPTY_READS_SET = Set.of();
    /**
     * empty exports set passed to an addExports call
     */
    private Map<String, Set<Module>> EMPTY_EXPORTS_MAP = Map.of();
    /**
     * empty opens set passed to an addExports call
     */
    private Map<String, Set<Module>> EMPTY_OPENS_MAP = Map.of();
    /**
     * empty uses set passed to an addExports call
     */
    private Set<Class<?>> EMPTY_USES_SET = Set.of();
    /**
     * empty provides map passed to an addExports call
     */
    private Map<Class<?>, List<Class<?>>> EMPTY_PROVIDES_MAP = Map.of();

    /**
     * the Instrumentation instance that allows addExports to be called
     */
    private Instrumentation inst;

    private Lookup theLookup;

    /**
     * create an AccessEnabler that is capable of ensuring access when
     * running inside a Jigsaw enabled JDK.
     *
     * This constructor throws an exception if it is not provided with a
     * non-null Instrumentation instance as argument, effectively limiting
     * the use of this class to JVMTI agents.
     *
     * @param inst the instrumentation instance we need to use to enable access
     */
    public JigsawAccessEnabler(Instrumentation inst) {

        // don't play ball unless this class is in a named module
        // since we don't want to provide access to the unnamed module

        if (!THIS_MODULE.isNamed()) {
            throw new RuntimeException("JigsawAccessEnabler : can only enable Jigsaw access from a named module " + THIS_MODULE);
        }

        // effectively this means only an agent can create one of these
        // which is all we need to ensure that access is restricted
        // to code which ought to be able to use setAccessible

        if (inst == null) {
            throw new RuntimeException("JigsawAccessEnabler : can only be created if passed a real Instrumentation handle");
        }

        this.inst = inst;

        try {
            this.theLookup = MethodHandles.lookup();
        } catch (Exception e) {
            throw new RuntimeException("JigsawAccessEnabler : cannot obtain lookup from Byteman module", e);
        }
        debug("created JigsawAccessEnabler");
    }
    /**
     * test whether reference to the class from a classpath
     * class requires the use of reflection or a method handle
     * and possibly also module jiggery-pokery.
     *
     * @param klazz the clas to be checked
     * @return  true if reference to the class from a classpath
     * class requires the use of reflection or a method handle
     * and possibly module jiggery-pokery otherwise false.
     */
    public boolean requiresAccess(Class<?> klazz)
    {
        debug("JigsawAccessEnabler.requiresAccess( klazz == ", klazz.getName(), ")");

        // we need to handle private or protected classes with kid gloves.
        // public classes in an unexported package also need special access.
        // public classes in an exported package are fine so long as they
        // are not embedded in inaccessible packages.
        while (Modifier.isPublic(klazz.getModifiers())) {
            Module module = klazz.getModule();
            if (module.isNamed()) {
                debug(" module == ", module.getName());
                Package pkg = klazz.getPackage();
                if (pkg == null) {
                    debug ("  (pkg == null) ==> false");
                    return false;
                }
                if (!module.isExported(pkg.getName())) {
                    debug (" !module.isExported(pkg.getName()) ==> true");
                    return true;
                }
            }
            if (!klazz.isMemberClass()) {
                debug(" !klazz.isMemberClass() ==> false") ;
                return false;
            }
            try {
                klazz = klazz.getDeclaringClass();
                debug(" klazz == ", klazz.getName());
            } catch (SecurityException se) {
                debug ("SecurityException ==> true");
                return true;
            }
        }
        debug ("  ==> true" );
        return true;
    }
    /*
     * test whether access to the accessible from a classpath
     * class requires the use of reflection or a method handle
     * and possibly also module jiggery-pokery.
     *
     * @param accessible this must be a Member
     * @return  true if access requires reflection or a method handle and
     * possibly also module jiggery-pokery otherwise false.
     */
    public boolean requiresAccess(AccessibleObject accessible)
    {
        // member specific checks
        // the accessible has to be a field, method or constructor
        Member member = (Member)accessible;

        debug("JigsawAccessEnabler.requiresAccess( accessible == ", member.getDeclaringClass().getName(), ".", member.getName(), ")");

        // we need to use reflection to access non-public members
        if (!Modifier.isPublic(member.getModifiers())) {
            debug ("!Modifier.isPublic(member.getModifiers()) ==> true");
            return true;
        }

        // class level checks
        Class<?> clazz = member.getDeclaringClass();

        // we need to use reflection to access non-public classes
        // (n.b. it won't be in the Byteman package space)
        if (!Modifier.isPublic(clazz.getModifiers())) {
            debug("!Modifier.isPublic(clazz.getModifiers()) ==> true");
            return true;
        }

        // we need to repeat the same check for outer classes
        while (clazz.isMemberClass()) {
            clazz = clazz.getEnclosingClass();
            debug("klazz == ", clazz.getName());
            if (!Modifier.isPublic(clazz.getModifiers())) {
                debug("!Modifier.isPublic(clazz.getModifiers()) ==> true");
                return true;
            }
        }

        // module system-specific checks
        Module module = clazz.getModule();

        // we can always access classes in the unnamed module
        if (!module.isNamed()) {
            debug ("!module.isNamed() ==> false");
            return false;
        }

        debug("module == ", module.getName());
        // check the exports for the owner class's package
        String pkg = clazz.getPackageName();

        debug("pkg == ", pkg);

        // if the package is already exported to normal Byteman code
        // then we don't need to enable access

        if (module.isExported(pkg, UNPRIVILEGED_MODULE)) {
            debug ("module.isExported(pkg, UNPRIVILEGED_MODULE) ==> false");
            return false;
        }

        debug(" ==> true");
        return true;
    }

    /**
     * ensure that accessible can be accessed using reflection
     * or a method handle
     *
     * @param accessible this must be a Member
     */
    public void ensureAccess(AccessibleObject accessible)
    {
        // make sure the setAccessible call will actually work
        ensureModuleAccess(accessible);
        // make the accessor usable
        accessible.setAccessible(true);
    }

    @Override
    public AccessibleMethodInvoker createMethodInvoker(Method method)
    {
        ensureModuleAccess(method);
        Lookup privateLookup = null;
        try {
            privateLookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), theLookup);
        } catch (IllegalAccessException e) {
            // this should never happen
        }
        return new JigsawAccessibleMethodInvoker(privateLookup, method);
    }

    @Override
    public AccessibleConstructorInvoker createConstructorInvoker(Constructor constructor)
    {
        ensureModuleAccess(constructor);
        Lookup privateLookup = null;
        try {
            privateLookup = MethodHandles.privateLookupIn(constructor.getDeclaringClass(), theLookup);
        } catch (IllegalAccessException e) {
            // this should never happen
        }
        return new JigsawAccessibleConstructorInvoker(privateLookup, constructor);
    }

    @Override
    public AccessibleFieldGetter createFieldGetter(Field field)
    {
        ensureModuleAccess(field);
        Lookup privateLookup = null;
        try {
            privateLookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), theLookup);
        } catch (IllegalAccessException e) {
            // this should never happen
        }
        return new JigsawAccessibleFieldGetter(privateLookup, field);
    }

    @Override
    public AccessibleFieldSetter createFieldSetter(Field field)
    {
        ensureModuleAccess(field);
        Lookup privateLookup = null;
        try {
            privateLookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), theLookup);
        } catch (IllegalAccessException e) {
            // this should never happen
        }
        return new JigsawAccessibleFieldSetter(privateLookup, field);
    }

    /**
     * check whether the accessible's owning class resides in a non-default module and
     * if so ensure that the package is exported to the Byteman Jigsaw package
     *
     * @param accessible
     */
    private void ensureModuleAccess(AccessibleObject accessible)
    {
        Member member = (Member)accessible;
        Class<?> clazz = member.getDeclaringClass();
        Module module = clazz.getModule();

        // we can always access classes in the unnamed module
        if (!module.isNamed()) {
            return;
        }

        // check the exports for the owner class's package
        String pkg = clazz.getPackageName();

        // if the package is already exported to this module
        // then we don't need to enable access otherwise
        // we arrange for it to be exported

        if (!module.isOpen(pkg, THIS_MODULE)) {
            // ok, export it then
            Map<String, Set<Module>> extraOpens = Map.of(pkg, THIS_MODULE_SET);
            inst.redefineModule(module, EMPTY_READS_SET, EMPTY_EXPORTS_MAP, extraOpens, EMPTY_USES_SET, EMPTY_PROVIDES_MAP);
            inst.redefineModule(THIS_MODULE, Set.of(module), EMPTY_EXPORTS_MAP, EMPTY_OPENS_MAP, EMPTY_USES_SET, EMPTY_PROVIDES_MAP);
        }
    }

    private void debug(String ... args)
    {
        if (!DEBUG) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s);
        }
        System.out.println(builder.toString());
    }
}