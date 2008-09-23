package org.jboss.jbossts.orchestration.rule.exception;

/**
 * Specialization of ExecuteException which is used to cause a trigger method to return
 * early the trigger point, possibly supplying an object to be returned. This is used
 * to implement the RETURN action
 *
 */
public class EarlyReturnException extends ExecuteException
{
    public EarlyReturnException(String message) {
        super(message);
        this.returnValue = null;
    }

    public EarlyReturnException(String message, Throwable th) {
        super(message, th);
        this.returnValue = null;
    }

    public EarlyReturnException(String message, Object returnValue) {
        super(message);
        this.returnValue = returnValue;
    }

    public EarlyReturnException(String message, Throwable th, Object returnValue) {
        super(message, th);
        this.returnValue = returnValue;
    }

    public Object getReturnValue()
    {
        return returnValue;
    }

    private Object returnValue;
}
