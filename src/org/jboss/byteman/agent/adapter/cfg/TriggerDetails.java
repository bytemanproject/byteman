package org.jboss.byteman.agent.adapter.cfg;

import org.objectweb.asm.Label;

/**
 * auxiliary used by CFG to store details of a specific trigger insertion point
 */
public class TriggerDetails
{
    /**
     * back link to the flow graph
     */
    private CFG cfg;

    /**
     * the label identifying the start of the trigger sequence injected into the bytecode
     */
    private Label start;

    /**
     * the label identifying the end of the trigger sequence injected into the bytecode
     */
    private Label end;

    /**
     * the label identifying the start of the handler block for any ReturnException thrown by the trigger call
     */
    private Label earlyReturnHandler;

    /**
     * the label identifying the start of the handler block for any ThrowException thrown by the trigger call
     */
    private Label throwHandler;

    /**
     * the label identifying the start of the handler block for any ExecuteException thrown by the trigger call
     */
    private Label executeHandler;

    /**
     * construct a new trigger details instance
     * @param cfg
     * @param start
     */
    public TriggerDetails(CFG cfg, Label start)
    {
        this.cfg = cfg;
        this.start = start;
        this.end = null;
    }

    // accessors

    public Label getStart() {
        return start;
    }

    public void setStart(Label start) {
        this.start = start;
    }

    public Label getEnd() {
        return end;
    }

    public void setEnd(Label end) {
        this.end = end;
    }

    public Label getExecuteHandler() {
        return executeHandler;
    }

    public void setExecuteHandler(Label executeHandler) {
        this.executeHandler = executeHandler;
    }

    public Label getEarlyReturnHandler() {
        return earlyReturnHandler;
    }

    public void setEarlyReturnHandler(Label earlyReturnHandler) {
        this.earlyReturnHandler = earlyReturnHandler;
    }

    public Label getThrowHandler() {
        return throwHandler;
    }

    public void setThrowHandler(Label throwHandler) {
        this.throwHandler = throwHandler;
    }
}