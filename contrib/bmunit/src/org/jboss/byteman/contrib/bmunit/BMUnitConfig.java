/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.byteman.contrib.bmunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Unit test level configuration of byteman properties
 *
 * @author Scott stark (sstark@redhat.com) (C) 2011 Red Hat Inc.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BMUnitConfig {

   /**
    * The port to be used by the agent listener or 0 for the default port. This can only be set once
    * per load of the agent. If #isAgentLoadEnabled is false, this give the port of the remote agent
    * to connect to.
    * Same as the org.jboss.byteman.contrib.bmunit.agent.port System property.
    */
   int agentPort() default -1;
   /**
    * The hostname to be used by the agent listener or "" for localhost. This can only be set once
    * per load of the agent. If #isAgentLoadEnabled is false, this give the port of the remote agent
    * to connect to.
    * Same as the org.jboss.byteman.contrib.bmunit.agent.host System property.
    */
   String agentHost() default "";

   /**
    * Idenitfy the location of the installed byteman release. This is the parent directory of the
    * byteman lib directory.
    * Same as the org.jboss.byteman.home System property.
    */
   abstract String bytemanHome() default "";
   /**
    * Identifies the directory from which to start searching
    * for rule script. If unset the current working directory of the test is used.
    * Same as the org.jboss.byteman.contrib.bmunit.script.directory System property.
    */
   abstract String scriptDirectory() default "";

   /**
    * Enable tracing of Byteman Unit test manager class activity
    * Same as the org.jboss.byteman.contrib.bmunit.verbose System property.
    */
   abstract boolean isBmunitVerbose() default false;
   /**
    * Enable tracing of the org.jboss.byteman.agent.Transformer behavior.
    * Same as the org.jboss.byteman.verbose System property.
    */
   abstract boolean isBytemanVerbose() default false;
   /**
    * Enable output of the debug statements in rules
    * Same as the org.jboss.byteman.debug System property.
    */
   abstract boolean isBytemanDebug() default false;

   /**
    * Enable mutability of the agent behavior with respect to all the properties explicitly labeled abstract.
    * This can be set to false in tests where performance really matters, in which case this can only
    * be set once.
    * @return true if the abstract properties can change values between unit tests.
    */
   boolean isReconfigurationEnabled() default true;

   /**
    * Setting this to false disables BMUnit loading the agent into the current JVM. This is useful if e.g. the
    * test is driving a remote app server and the rules need to be uploaded to the app server JVM.
    * Same as org.jboss.byteman.contrib.bmunit.agent.inhibit System property.
    * @return true if the bytename agent is loaded into the unit test JVM.
    */
   boolean isAgentLoadEnabled() default true;

   /**
    * A flag to determine if the corresponding system properties for this annotation's elements should
    * be checked before using the annotation value. This is necessary when system properties are being
    * used because a default instance of BMUnitConfig is used if a test case does not specify
    * @return true if the corresponding system property should be checked before the BMUnitConfig
    * element, false if the BMUnitConfig element takes precedence.
    */
   boolean isPreferSystemProperties() default false;


}
