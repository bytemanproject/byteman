package org.jboss.byteman.tests.auxiliary;

/**
 * Auxiliary class for BYTEMAN-156 which tests throw injection when an implementing class does
 * not throw an exception declared by a super class or super interface
 */
public interface TestThrowInterface
{
    public void throwMethod() throws Exception;
    public void throwMethod2() throws Exception;
}
