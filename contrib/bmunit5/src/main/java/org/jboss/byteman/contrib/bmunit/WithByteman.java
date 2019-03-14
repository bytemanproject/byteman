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

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta Annotation to wire all the BMUnit annotations to JUnit5 tests.
 * When updating from JUnit4, @WithByteman replaces @RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({BMUnit5ConfigHandler.class, BMUnit5MultiScriptHandler.class, BMUnit5SingleScriptHandler.class, BMUnit5MultiRuleHandler.class, BMUnit5SingleRuleHandler.class})
public @interface WithByteman {
}
