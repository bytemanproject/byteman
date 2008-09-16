package org.jboss.jbossts.orchestration.rule.exception;

/**
 * exception class thrown during rule execution. n.b. this extends RuntimeException to avoid
 * issues with method checking
 */
public class ExecuteException extends RuntimeException
{
    public ExecuteException(String message)
    {
        super(message);
    }

    public ExecuteException(String message, Throwable th)
    {
        super(message, th);
    }
}
