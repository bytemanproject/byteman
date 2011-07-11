package org.jboss.byteman.rule.exception;

/**
 * A sepcialization of TypeException which can be thrown during type checking to indicate that
 * a rule has failed to type check for a legitimate reason. It indicates that rule execution
 * should be inhibited even though the rule has already been injected.
 */
public class TypeWarningException extends TypeException
{
    public TypeWarningException(String message)
    {
        super(message);
    }

    public TypeWarningException(String message, Throwable th)
    {
        super(message, th);
    }
}
