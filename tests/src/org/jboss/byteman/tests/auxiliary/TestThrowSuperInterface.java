package org.jboss.byteman.tests.auxiliary;

/**
 * Auxiliary class for BYTEMAN-156 which tests throw injection when an implementing class does
 * not throw an exception declared by a super class or super interface
 */
public interface TestThrowSuperInterface
{
    public void throwMethod3() throws Exception;
}
