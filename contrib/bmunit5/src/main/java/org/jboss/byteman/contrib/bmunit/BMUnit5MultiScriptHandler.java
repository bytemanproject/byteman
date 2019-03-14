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

import java.lang.reflect.Method;

public class BMUnit5MultiScriptHandler extends BMUnit5AbstractHandler<BMScripts> {

    public BMUnit5MultiScriptHandler() {
        super(BMScripts.class);
    }

    @Override
    protected void install(Class<?> testClass, Method testMethod, BMScripts bmScripts) throws Exception {

        final BMScript[] scriptAnnotations = bmScripts.scripts();

        for (int i = 0; i < scriptAnnotations.length; i++) {
            final BMScript scriptAnnotation = scriptAnnotations[i];
            String name = BMUnit5SingleScriptHandler.computeName(testMethod, scriptAnnotation);
            final String loadDirectory = BMRunnerUtil.normaliseLoadDirectory(scriptAnnotation);
            BMUnit.loadScriptFile(testClass, name, loadDirectory);
        }
    }

    @Override
    protected void uninstall(Class<?> testClass, Method testMethod, BMScripts bmScripts) throws Exception {

        final BMScript[] scriptAnnotations = bmScripts.scripts();

        for (int i = 0; i < scriptAnnotations.length; i++) {
            final BMScript scriptAnnotation = scriptAnnotations[i];
            String name = BMUnit5SingleScriptHandler.computeName(testMethod, scriptAnnotation);
            BMUnit.unloadScriptFile(testClass, name);
        }
    }
}
