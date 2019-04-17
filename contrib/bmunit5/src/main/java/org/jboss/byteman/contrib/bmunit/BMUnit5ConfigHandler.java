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
        if(optionalAnnotation.isPresent()) {
            install(testClass, null, null);
        } else {
            install(testClass, null, optionalAnnotation.get());
        }
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
