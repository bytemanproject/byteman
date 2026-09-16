/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019 Red Hat and individual contributors
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
 */
package org.jboss.byteman.contrib.bmunit;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.jboss.byteman.contrib.bmunit.BMUnit.isBMUnitVerbose;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public class BMUnit5ConfigHandler extends BMUnit5AbstractHandler<BMUnitConfig> {

    /**
     * namespace used to remember, per test container, the configuration state
     * that container installed. the configuration state is global, so teardown
     * has to be able to tell its own state from one belonging to an enclosing
     * or preceding container.
     */
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(BMUnit5ConfigHandler.class);

    public BMUnit5ConfigHandler() {
        super(BMUnitConfig.class);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (isBMUnitVerbose()) {
            System.out.println(this.getClass().getName() + ".beforeAll");
        }

        final Class<?> testClass = context.getRequiredTestClass();

        final Optional<BMUnitConfig> optionalAnnotation = findAnnotation(testClass, annotationClass);
        System.out.println(this.getClass().getName() + " installing " + testClass.getCanonicalName());
        final BMUnitConfigState preexisting = BMUnitConfigState.getCurrentConfigState();
        try {
            install(testClass, null, optionalAnnotation.orElse(null));
        } finally {
            // a failed install can still have replaced the configuration state, so
            // record whatever this container installed rather than only recording
            // it on success. if there already was a configuration then it belongs
            // to another container and this one must not claim it.
            final BMUnitConfigState installed = BMUnitConfigState.getCurrentConfigState();
            if (preexisting == null && installed != null) {
                context.getStore(NAMESPACE).put(context.getUniqueId(), installed);
            }
        }
    }

    /**
     * removes the configuration installed by this container. the inherited
     * implementation only uninstalls when the test class carries a
     * {@link BMUnitConfig} annotation, but beforeAll installs a configuration
     * either way, which left the state of an unannotated class in place for the
     * next class to trip over.
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (isBMUnitVerbose()) {
            System.out.println(this.getClass().getName() + ".afterAll");
        }

        final BMUnitConfigState installed =
                context.getStore(NAMESPACE).remove(context.getUniqueId(), BMUnitConfigState.class);
        if (installed == null) {
            // this container installed nothing, so whatever is configured now
            // belongs to another container and has to be left alone
            return;
        }

        final BMUnitConfigState current = BMUnitConfigState.getCurrentConfigState();
        if (current == null) {
            // the state installed here has already been removed
            return;
        }
        if (current != installed) {
            throw new Exception("BMUnit test class configuration for "
                    + context.getRequiredTestClass().getName()
                    + " was replaced before it could be popped!");
        }

        uninstall(context.getRequiredTestClass(), null, null);
    }


    @Override
    protected void install(Class<?> testClass, Method testMethod, BMUnitConfig bmUnitConfig) throws Exception {
        if(testMethod != null) {
            BMUnitConfigState.pushConfigurationState(bmUnitConfig, testMethod);
        } else {
            BMUnitConfigState.pushConfigurationState(bmUnitConfig, testClass);
        }
    }

    @Override
    protected void uninstall(Class<?> testClass, Method testMethod, BMUnitConfig bmUnitConfig) throws Exception {
        if(testMethod != null) {
            BMUnitConfigState.popConfigurationState(testMethod);
        } else {
            BMUnitConfigState.popConfigurationState(testClass);
        }
    }
}
