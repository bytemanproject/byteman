package org.jboss.jbossts.orchestration.rule.exception;

/**
 * Created by IntelliJ IDEA.
 * User: adinn
 * Date: 30-Sep-2008
 * Time: 13:37:55
 * To change this template use File | Settings | File Templates.
 */
public class ThrowException extends ExecuteException
{
    private Throwable throwable;

    public ThrowException(Throwable throwable) {
        super("wrapper for exception created in throw expression", throwable);
        this.throwable = throwable;
    }

    public Throwable getThrowable()
    {
        return throwable;
    }
}
