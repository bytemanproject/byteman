/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat and individual contributors
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

package org.jboss.byteman.contrib.bmunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation to allow configuration of BMUnit operation directly from
 * test classes rather than via use of system variables
 *
 * a BMUnitConfig annotation may be attached to a test method (or test class)
 * to specify the BMUnit and Byteman configuration which should be employed
 * when the associated test(s) is (are) run.
 *
 * Certain configuration options only take effect when the first
 * BMUnitConfig annotation encounterd during the run is encountered
 * as the associated configuration values may not be reset once set. If
 * multiple annotations are encountered in a given test run then it
 * may not always be possible to honour all the specified settings. For
 * example, if the agent has been autoloaded into the current VM using a
 * given hostname (e.g. "192.168.0.254") then it will not be possible to
 * upload rules to the autoloaded agent using the configured hostname
 * "localhost". In such cases tests will normally proceed using the
 * existing configuration. It is possible to force an error and fail a
 * test in these cases through use of the enforce attribute.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface BMUnitConfig
{
    /**
     * if enforce is true then every element of the configuration
     * specified in the annotation must be configured as specified
     * and if it cannot be so configured (or reconfigured) then an
     * exception will be thrown by the test runner. By default
     * unavailable configuration settings are ignored.
     * @return enforce
     */
    boolean enforce() default false;

    /**
     * agentHost specifies the hostname which will be used when
     * opening a client connection to the Byteman agent listener.
     * if BMUnit needs to load the agent into the local JVM then
     * the same hostname will be passed to the agent for it to use
     * when opening the agent listener server socket. An empty
     * String  (the default) means use the Byteman default (i.e.
     * "localhost").
     * @return agentHost
     */
    String agentHost() default "";
    /**
     * agentPort specifies the port which will be used when
     * opening a client connection to the Byteman agent listener.
     * if BMUnit needs to load the agent into the local JVM then
     * the same port will be passed to the agent for it to use
     * when opening the agent listener server socket. An empty
     * String (the default) means use the Byteman default (i.e.
     * 9999).
     * @return agentPort
     */
    String agentPort() default "";

    /**
     * inhibitAgentLoad requests that the Byteman agent not be
     * auto-loaded into the current JVM on behalf of this test.
     * @return inhibitAgentLoad
     */
    boolean inhibitAgentLoad() default false;

    /**
     * loadDirectory identifies a directory relative to which rule
     * rule script files should be loaded. it is only effective when a
     * BMScript annotation fails to specify a dir="..." value. An empty
     * String (the default) means use the current working directory.
     * @return loadDirectory
     */
    String loadDirectory() default "";
    /**
     * resourceLoadDirectory identifies a resource path relative to which
     * rule script resources should be loaded. it is only effective when
     * a BMScript annotation fails to specify a dir="..." value. An empty
     * String (the default) means use the value of loadDirectory or, if
     * that is not set, no resource path
     * @return resourceLoadDirectory
     */
    String resourceLoadDirectory() default "";

    /**
     * allowAgentConfigUpdate determines whether or not the various Byteman property
     * settings specified when the agent is uploaded can be reconfigured while
     * the agent is running. In particular, this flag allows Byetman's verbose
     * tracing mode to be reset. Byteman normally disables this setting for
     * performance reasons. In particular, by disabling configuration of the
     * verbose setting Byteman avoids the many frequent tests which occur during
     * rule injection and rule execution to determine whether or not to print
     * trace output. The BMUnitConfig annotation default setting for this
     * attribute enables configuration update. The assumption is that most
     * tests are not performance critical while the ability to reset Byteman's
     * behaviour for individual tests is highly desirable. If your test really
     * needs to run as close as possible to normal conditions then you should
     * specify this attribute as false and set attribute enforce to true.
     *
     * @return allowAgentConfigUpdate
     */
    boolean allowAgentConfigUpdate() default true;

    /**
     * verbose configures the Byteman verbose setting which controls
     * printing of trace related to the operation of Byteman
     * @return verbose
     */
    boolean verbose() default false;
    /**
     * debug configures the Byteman debug setting which controls
     * printing of debug trace statements embedded in Byteman rules
     * @return debug
     */
    boolean debug() default false;
    /**
     * bmunitVerbose configures the BMUnit verbose setting which controls
     * printing of trace related to the operation of the BMUnit package
     * @return bmunitVerbose
     */
    boolean bmunitVerbose() default false;

    /**
     * policy configures whether or not to set a security policy
     * when loading the agent.
     * @return policy
     */
    boolean policy() default false;

    /**
     * dumpGeneratedClasses configures whether or not the Byteman agent
     * dumps transformed bytecode to a class file.
     * @return dumpGeneratedClasses
     */
    boolean dumpGeneratedClasses() default false;
    /**
     * dumpGeneratedClassesDirectory identifies a directory path
     * to locate the root directory for class files created when the
     * Byteman agent dumps transformed bytecode to a class file.
     * Each class file sits below this root directory in a
     * subdirectory tree corresponding to the class's package.
     * This attribute is only taken into account when attribute
     * dumpGeneratedClasses has value true. An empty String means
     * use the current working directory.
     * @return dumpGeneratedClassesDirectory
     */
    String dumpGeneratedClassesDirectory() default "";

    /**
     * dumpGeneratedClassesIntermediate configures whether or not
     * the Byteman agent dumps intermediate versions of transformed
     * bytecode to a class file. It is only of relevance where
     * multiple rules target the same class. In such cases Byteman
     * will inject rules one after the other. This attribute is only
     * taken into account when attribute dumpGeneratedClasses has
     * value true. Successive intermediate class files are written
     * with the same name as the final transformed file except that
     * the base name is modified by adding suffixes _1, _2, ..., etc.
     * @return dumpGeneratedClassesIntermediate
     */
    boolean dumpGeneratedClassesIntermediate() default false;
}
