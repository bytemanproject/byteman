package org.jboss.jbossts.orchestration.rule.exception;

/**
 * used to notify an exception during rule compilation
 */
public class CompileException extends Exception
{
    public CompileException(String message)
    {
        super(message);
    }

    public CompileException(String message, Throwable th)
    {
        super(message, th);
    }
}