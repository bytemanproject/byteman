package org.jboss.jbossts.orchestration.rule.exception;

/**
 * used to notify an exception during rule parsing
 */
public class ParseException extends Exception
{
    public ParseException(String message)
    {
        super(message);
    }

    public ParseException(String message, Throwable th)
    {
        super(message, th);
    }
}
