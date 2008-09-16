package org.jboss.jbossts.orchestration.rule.exception;

/**
 * used to notify an exception during rule type checking
 */
public class TypeException extends Exception
{
    public TypeException(String message)
    {
        super(message);
    }

    public TypeException(String message, Throwable th)
    {
        super(message, th);
    }
}