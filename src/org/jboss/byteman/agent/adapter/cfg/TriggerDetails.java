/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
* @authors Andrew Dinn
*/
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