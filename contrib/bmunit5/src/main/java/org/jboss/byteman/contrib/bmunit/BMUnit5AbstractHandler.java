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

import org.junit.jupiter.api.extension.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.jboss.byteman.contrib.bmunit.BMUnit.isBMUnitVerbose;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

/**
 * Base class for wiring Byteman BMUnit annotations to the test engine's lifecycle hooks.
 *
 * @param <A> The BMUnit annotation class.
 */
public abstract class BMUnit5AbstractHandler<A extends Annotation>
        implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    protected final Class<A> annotationClass;

    protected BMUnit5AbstractHandler(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public Class<A> getAnnotationClass() {
        return annotationClass;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (isBMUnitVerbose()) {
            System.out.println(this.getClass().getName() + ".beforeAll");
        }

        final Class<?> testClass = context.getRequiredTestClass();

        final Optional<A> optionalAnnotation = findAnnotation(testClass, annotationClass);
        if (optionalAnnotation.isPresent()) {
            if (isBMUnitVerbose()) {
                System.out.println(this.getClass().getName() + " installing " + testClass.getCanonicalName());
            }
            install(testClass, null, optionalAnnotation.get());
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (isBMUnitVerbose()) {
            System.out.println(this.getClass().getName() + ".beforeEach");
        }

        final Class<?> testClass = context.getRequiredTestClass();

        final Optional<Method> testMethod = context.getTestMethod();
        if (testMethod.isEmpty()) {
            return;
        }

        final Optional<A> optionalAnnotation = findAnnotation(testMethod.get(), annotationClass);
        if (optionalAnnotation.isPresent()) {
            if (isBMUnitVerbose()) {
                System.out.println(this.getClass().getName() + " installing " + testClass.getCanonicalName() + "::" + testMethod.get().getName());
            }
            install(testClass, testMethod.get(), optionalAnnotation.get());
        }
    }

    protected abstract void install(Class<?> testClass, Method testMethod, A annotation) throws Exception;

    ///////////////////////

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (isBMUnitVerbose()) {
            System.out.println(this.getClass().getName() + ".afterAll");
        }

        final Class<?> testClass = context.getRequiredTestClass();

        final Optional<A> optionalAnnotation = findAnnotation(testClass, annotationClass);
        if (optionalAnnotation.isPresent()) {
            uninstall(testClass, null, optionalAnnotation.get());
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (isBMUnitVerbose()) {
            System.out.println(this.getClass().getName() + ".afterEach");
        }

        final Class<?> testClass = context.getRequiredTestClass();

        final Optional<Method> testMethod = context.getTestMethod();
        if (testMethod.isEmpty()) {
            return;
        }

        final Optional<A> optionalAnnotation = findAnnotation(testMethod.get(), annotationClass);
        if (optionalAnnotation.isPresent()) {
            if (isBMUnitVerbose()) {
                System.out.println(this.getClass().getName() + " uninstalling " + testClass.getCanonicalName() + "::" + testMethod.get().getName());
            }
            uninstall(testClass, testMethod.get(), optionalAnnotation.get());
        }
    }

    protected abstract void uninstall(Class<?> testClass, Method testMethod, A annotation) throws Exception;
}
