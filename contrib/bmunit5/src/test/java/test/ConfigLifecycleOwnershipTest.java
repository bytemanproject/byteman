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
package test;

import org.jboss.byteman.contrib.bmunit.BMUnit5ConfigHandler;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitConfigState;
import org.jboss.byteman.contrib.bmunit.WithByteman;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Checks that a test container only removes the BMUnit configuration it
 * installed itself. The configuration state is a single global, so the
 * sequential chain in the other ConfigLifecycle classes cannot distinguish a
 * teardown that removes the right state from one that removes whatever happens
 * to be installed. These cases drive nested engine runs so that a container can
 * be started while another container's configuration is already in force, and
 * so that containers which fail part way through setup can be observed.
 *
 * This class deliberately carries no BMUnit annotations of its own, so it
 * starts from an uninstalled configuration.
 */
public class ConfigLifecycleOwnershipTest
{
    /**
     * message thrown by {@link ThrowingInstallHandler} once the configuration
     * has genuinely been installed, standing in for any later failure in setup
     */
    private static final String INSTALL_FAILED = "install failed after the configuration was assigned";

    /**
     * message thrown by a class whose own setup fails after the BMUnit
     * configuration has been installed for it
     */
    private static final String SETUP_FAILED = "class setup failed after the configuration was installed";

    /**
     * the message the configuration state rejects an enforcing configuration
     * with, raised before anything is installed
     */
    private static final String REJECTED =
            "BMUnit configuration specifies incompatible settings for allowAgentConfigUpdate";

    /**
     * the message a class level push reports when a configuration is already in
     * force
     */
    private static final String ALREADY_PUSHED = "BMUnit test class configuration pushed without prior pop!";

    /**
     * the configuration seen by the enclosing class once its nested container
     * has finished but before the BMUnit extension tears the class down
     */
    static BMUnitConfigState observedBeforeEnclosingTeardown;

    /**
     * the configuration a scenario puts in place of its own, so that the test
     * can check the extension left it alone
     */
    static BMUnitConfigState replacementInstalledByScenario;

    @WithByteman
    public static class NestingScenario
    {
        @Test
        public void enclosingTest()
        {
        }

        // the nested container inherits the BMUnit extensions and runs its own
        // beforeAll and afterAll while the enclosing configuration is installed.
        // it cannot install a configuration of its own, so what matters is that
        // its teardown leaves the enclosing one alone either way
        @Nested
        public class NestedScenario
        {
            @Test
            public void nestedTest()
            {
            }
        }

        @AfterAll
        public static void recordConfiguration()
        {
            observedBeforeEnclosingTeardown = BMUnitConfigState.getCurrentConfigState();
        }
    }

    @WithByteman
    public static class PlainScenario
    {
        @Test
        public void plainTest()
        {
        }
    }

    /**
     * carries a configuration annotation, so the inherited teardown applies to it
     * unconditionally. a container whose install failed must still not pop a
     * configuration it never owned.
     */
    @WithByteman
    @BMUnitConfig
    public static class AnnotatedScenario
    {
        @Test
        public void annotatedTest()
        {
        }
    }

    /**
     * the enforcing configuration conflicts with the default configuration
     * already established in this JVM, so the configuration state constructor
     * rejects it before anything is installed
     */
    @WithByteman
    @BMUnitConfig(enforce = true, allowAgentConfigUpdate = false, verbose = true)
    public static class RejectedConfigScenario
    {
        @Test
        public void rejectedTest()
        {
        }
    }

    /**
     * installs the configuration exactly as the real handler does and then
     * fails, injecting a fault after the configuration state has genuinely
     * been assigned. that is what the handler's ownership tracking has to
     * cope with: a setup that never returns from install but has already
     * replaced the global state. it says nothing about whether the
     * configuration state rolls back correctly when the agent transport
     * itself fails, which is a separate question about BMUnitConfigState.
     */
    public static class ThrowingInstallHandler extends BMUnit5ConfigHandler
    {
        @Override
        protected void install(Class<?> testClass, Method testMethod, BMUnitConfig bmUnitConfig) throws Exception
        {
            super.install(testClass, testMethod, bmUnitConfig);
            throw new IllegalStateException(INSTALL_FAILED);
        }
    }

    /**
     * registers the failing handler on its own rather than through
     * {@code @WithByteman}, so that exactly one configuration handler is in
     * play and the failure is the one this test provokes
     */
    @ExtendWith(ThrowingInstallHandler.class)
    public static class PartialInstallScenario
    {
        @Test
        public void neverRuns()
        {
        }
    }

    /**
     * the configuration is installed successfully and the class's own setup
     * then fails, which is the ordinary way a container dies after the
     * extension has taken ownership
     */
    @WithByteman
    public static class FailingSetupScenario
    {
        @BeforeAll
        public static void failAfterConfiguration()
        {
            assertNotNull(BMUnitConfigState.getCurrentConfigState(),
                    "the configuration should already be installed when class setup runs");
            throw new IllegalStateException(SETUP_FAILED);
        }

        @Test
        public void neverRuns()
        {
        }
    }

    /**
     * replaces the configuration installed for it with a different one before
     * the extension gets to tear down. {@code @AfterAll} methods run before
     * afterAll callbacks, so this is observable by the handler.
     */
    @WithByteman
    public static class ReplacingScenario
    {
        @Test
        public void replacingTest()
        {
        }

        @AfterAll
        public static void replaceConfiguration() throws Exception
        {
            BMUnitConfigState.popConfigurationState(ReplacingScenario.class);
            BMUnitConfigState.pushConfigurationState(null, ConfigLifecycleOwnershipTest.class);
            replacementInstalledByScenario = BMUnitConfigState.getCurrentConfigState();
        }
    }

    /**
     * removes the configuration installed for it before the extension gets to
     * tear down, so that the handler finds nothing where its own state used to
     * be
     */
    @WithByteman
    public static class ClearedScenario
    {
        @Test
        public void clearedTest()
        {
        }

        @AfterAll
        public static void clearConfiguration() throws Exception
        {
            BMUnitConfigState.popConfigurationState(ClearedScenario.class);
        }
    }

    @Test
    public void nestedContainerLeavesTheEnclosingConfigurationInstalled()
    {
        observedBeforeEnclosingTeardown = null;

        final TestExecutionSummary summary = run(NestingScenario.class);

        // a nested container cannot install a configuration of its own while the
        // enclosing one is in force, so it is expected to fail. this pins current
        // behaviour rather than stating a requirement: making nested containers
        // work is a separate change, and it should have to update these lines
        assertEquals(ALREADY_PUSHED, soleFailureMessage(summary),
                "the nested container failed for an unexpected reason");
        assertNotNull(observedBeforeEnclosingTeardown,
                "the nested container removed the enclosing class configuration");
        assertNull(BMUnitConfigState.getCurrentConfigState(),
                "the enclosing class did not remove its own configuration");
    }

    @Test
    public void failedInstallLeavesAnotherContainersConfigurationInstalled() throws Exception
    {
        // the annotated case is the one that used to break: the inherited teardown
        // fires for an annotated class whether or not its own install succeeded
        assertForeignConfigurationSurvives(AnnotatedScenario.class);
        assertForeignConfigurationSurvives(PlainScenario.class);
    }

    private void assertForeignConfigurationSurvives(Class<?> scenario) throws Exception
    {
        BMUnitConfigState.pushConfigurationState(null, ConfigLifecycleOwnershipTest.class);
        final BMUnitConfigState foreign = BMUnitConfigState.getCurrentConfigState();
        try {
            // the scenario cannot install its own configuration while another one
            // is in force, and its teardown must not remove the one it found
            final TestExecutionSummary summary = run(scenario);
            assertEquals(ALREADY_PUSHED, soleFailureMessage(summary),
                    scenario.getSimpleName() + " failed for an unexpected reason");
            assertSame(foreign, BMUnitConfigState.getCurrentConfigState(),
                    "a failed install removed a configuration belonging to another container");
        } finally {
            // every assertion above has already fired by this point, so clearing
            // here cannot mask a failure. the reset variant is used because
            // popConfigurationState throws when nothing is installed, and an
            // exception raised from a finally block would replace an in-flight
            // assertion error with a misleading one
            BMUnitConfigState.resetConfigurationState(ConfigLifecycleOwnershipTest.class);
        }

        assertNull(BMUnitConfigState.getCurrentConfigState(),
                "the foreign configuration was not removed by its owner");
    }

    @Test
    public void configurationInstalledBeforeAFailingSetupIsStillRemoved() throws Exception
    {
        try {
            // the install itself succeeds here, so the handler owns a configuration
            // for a container that never reaches its tests
            final TestExecutionSummary summary = run(FailingSetupScenario.class);
            assertEquals(SETUP_FAILED, soleFailureMessage(summary),
                    "the scenario failed for an unexpected reason");
            assertNull(BMUnitConfigState.getCurrentConfigState(),
                    "a container that failed after a successful install left its configuration behind");
        } finally {
            BMUnitConfigState.resetConfigurationState(ConfigLifecycleOwnershipTest.class);
        }

        // the point of removing it is that the next class can still run
        assertPassed(run(PlainScenario.class), 1,
                "the following class could not install its own configuration");
        assertNull(BMUnitConfigState.getCurrentConfigState(),
                "the following class did not remove its own configuration");
    }

    @Test
    public void configurationInstalledByAFailingInstallIsStillRemoved() throws Exception
    {
        try {
            // the configuration is assigned and the install then throws, so the
            // handler has to have recorded ownership before it knew the outcome
            final TestExecutionSummary summary = run(PartialInstallScenario.class);
            assertEquals(INSTALL_FAILED, soleFailureMessage(summary),
                    "the scenario failed for an unexpected reason");
            assertNull(BMUnitConfigState.getCurrentConfigState(),
                    "a partly completed install left its configuration behind");
        } finally {
            BMUnitConfigState.resetConfigurationState(ConfigLifecycleOwnershipTest.class);
        }

        assertPassed(run(PlainScenario.class), 1,
                "the following class could not install its own configuration");
        assertNull(BMUnitConfigState.getCurrentConfigState(),
                "the following class did not remove its own configuration");
    }

    @Test
    public void aReplacedConfigurationIsReportedAndLeftAlone() throws Exception
    {
        replacementInstalledByScenario = null;

        try {
            final TestExecutionSummary summary = run(ReplacingScenario.class);
            assertEquals("BMUnit test class configuration for " + ReplacingScenario.class.getName()
                            + " was replaced before it could be popped!",
                    soleFailureMessage(summary),
                    "the replacement was not reported");
            assertNotNull(replacementInstalledByScenario, "the scenario installed no replacement");
            assertSame(replacementInstalledByScenario, BMUnitConfigState.getCurrentConfigState(),
                    "the replacement configuration was popped by a container that did not own it");
        } finally {
            BMUnitConfigState.resetConfigurationState(ConfigLifecycleOwnershipTest.class);
        }

        assertNull(BMUnitConfigState.getCurrentConfigState(),
                "the replacement configuration was not removed");
    }

    /**
     * pins the branch taken when the configuration this container installed has
     * already gone. this passes with or without the ownership fix, because the
     * inherited teardown also does nothing for an unannotated class; it is here
     * to record the intended behaviour of the branch, not to catch the original
     * defect.
     */
    @Test
    public void anAlreadyRemovedConfigurationIsNotReportedAsAnError()
    {
        assertPassed(run(ClearedScenario.class), 1,
                "removing the configuration early was treated as a failure");
        assertNull(BMUnitConfigState.getCurrentConfigState(),
                "a configuration was reinstated by teardown");
    }

    @Test
    public void rejectedConfigurationLeavesNothingInstalled()
    {
        // the enforcing configuration is only rejected once a default configuration
        // exists to conflict with, and the first class to install one in this JVM
        // defines it, so establish it from a scenario with default settings
        assertPassed(run(PlainScenario.class), 1,
                "the seeding scenario could not install a default configuration");

        final TestExecutionSummary summary = run(RejectedConfigScenario.class);
        assertEquals(REJECTED, soleFailureMessage(summary),
                "the enforcing configuration was rejected for an unexpected reason");
        assertNull(BMUnitConfigState.getCurrentConfigState(),
                "a rejected configuration left state installed");

        // a later class must still be able to install and remove its own configuration
        assertPassed(run(PlainScenario.class), 1,
                "the following class could not install its own configuration");
        assertNull(BMUnitConfigState.getCurrentConfigState(),
                "the following class did not remove its own configuration");
    }

    /**
     * asserts that a scenario ran the tests it was expected to run. a failure
     * count of zero is also what an empty or undiscovered container reports, so
     * the succeeded count is what makes the run evidence of anything.
     */
    private static void assertPassed(TestExecutionSummary summary, int expectedTests, String message)
    {
        assertEquals(0, summary.getTotalFailureCount(), message);
        assertEquals(expectedTests, summary.getTestsSucceededCount(),
                message + " (expected " + expectedTests + " tests to run)");
    }

    /**
     * returns the message of the single failure a scenario was expected to
     * produce. asserting the message rather than the count keeps these cases
     * from passing on an unrelated failure.
     */
    private static String soleFailureMessage(TestExecutionSummary summary)
    {
        assertEquals(1, summary.getFailures().size(),
                "expected exactly one failure, got " + describeFailures(summary));
        return summary.getFailures().get(0).getException().getMessage();
    }

    private static String describeFailures(TestExecutionSummary summary)
    {
        final StringBuilder builder = new StringBuilder();
        for (TestExecutionSummary.Failure failure : summary.getFailures()) {
            builder.append('[').append(failure.getException()).append(']');
        }
        return builder.length() == 0 ? "none" : builder.toString();
    }

    private static TestExecutionSummary run(Class<?> scenario)
    {
        final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(scenario))
                .build();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherFactory.create().execute(request, listener);
        return listener.getSummary();
    }
}
