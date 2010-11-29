/*
* JBoss, Home of Professional Open Source
* Copyright 2010 Red Hat and individual contributors
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
package org.jboss.byteman.sample.helper;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

/**
 * Helper class used by ThreadMonitorHelper script to trace thread operations
 */
public class ThreadMonitorHelper extends Helper
{
    protected ThreadMonitorHelper(Rule rule) {
        super(rule);
    }

    /**
     * trace creation of the supplied thread to System.out
     *
     * this should only be triggered from the constructor for class java.lang.Thread"
     *
     * @param thread the newly created thread
     */
    public void traceCreate(Thread thread)
    {
        traceCreate(thread, "out");
    }
    /**
     * trace creation of the supplied thread to the trace stream identified by key
     *
     * @param thread the newly created thread
     * @param key an object identifying the trace stream to which output should be generated
     */
    public void traceCreate(Thread thread, Object key)
    {
        StringBuffer buffer = new StringBuffer();
        StackTraceElement[] stack = getStack();
        int l = stack.length;
        int t = triggerIndex(stack);
        if (t < 0) {
            return;
        }

        int i = matchIndex(stack, "java.lang.Thread.<init>", false, true, true, t, l);
        if (i < 0) {
            // illegal usage
            traceStack("ThreadMonitorHelper.traceCreate : should only be triggered below Thread.<init>\n", key);
            return;
        }
        buffer.append("*** Thread create ");
        buffer.append(thread.getName());
        buffer.append(" ");
        buffer.append(thread.getClass().getCanonicalName());
        buffer.append('\n');

        // find bottommost constructor invocation
        i++;
        while (i < l && matchIndex(stack, "<init>", false, false, false, i, i+1) >= 0) {
            i++;
        }
        if (i == l) {
            // happens when a system thread is created by the runtime
            buffer.append("    from VM runtime\n");
        } else {
            buffer.append("    from ");
            printlnFrame(buffer, stack[i]);
        }
        trace(key, buffer.toString());
    }

    /**
     * trace start of the supplied thread to System.out
     *
     * this should only be triggered from the call to java.lang.Thread.start"
     *
     * @param thread the newly starting thread
     */
    public void traceStart(Thread thread)
    {
        traceStart(thread, "out");
    }

    /**
     * trace start of the supplied thread to the trace stream identified by key
     *
     * this should only be triggered from the call to java.lang.Thread.start"
     *
     * @param thread the newly starting thread
     * @param key an object identifying the trace stream to which output should be generated
     */
    public void traceStart(Thread thread, Object key)
    {
        StringBuffer buffer = new StringBuffer();
        StackTraceElement[] stack = getStack();
        int l = stack.length;
        int t = triggerIndex(stack);
        if (t < 0) {
            return;
        }

        int i = matchIndex(stack, "java.lang.Thread.start", false, true, true, t, l);
        if (i < 0) {
            // illegal usage
            traceStack("ThreadMonitorHelper.traceStart : should only be triggered below Thread.start\n", key);
            return;
        }

        buffer.append("*** Thread start ");
        buffer.append(thread.getName());
        buffer.append(" ");
        buffer.append(thread.getClass().getCanonicalName());
        buffer.append('\n');

        if (++i == l) {
            // should not happen
            traceStack("ThreadMonitorHelper.traceStart : failed to find frame for caller of start\n", key);
            return;
        }

        buffer.append("    from ");
        printlnFrame(buffer, stack[i]);
        trace(key, buffer.toString());
    }

    /**
     * trace exit of the supplied thread to System.out
     *
     * this should only be triggered from the call to java.lang.Thread.exit"
     *
     * @param thread the exiting thread
     */
    public void traceExit(Thread thread)
    {
        traceExit(thread, "out");
    }

    /**
     * trace exit of the supplied thread to the trace stream identified by key
     *
     * this should only be triggered from the call to java.lang.Thread.exit"
     *
     * @param thread the exiting thread
     * @param key an object identifying the trace stream to which output should be generated
     */
    public void traceExit(Thread thread, Object key)
    {
        StringBuffer buffer = new StringBuffer();
        StackTraceElement[] stack = getStack();
        int l = stack.length;
        int t = triggerIndex(stack);
        if (triggerIndex(stack) < 0) {
            return;
        }
        int i = matchIndex(stack, "java.lang.Thread.exit", false, true, true, t, l);
        if (i < 0) {
            // illegal usage
            traceStack("ThreadMonitorHelper.traceExit : should only be triggered below Thread.exit\n", key);
            return;
        }

        buffer.append("*** Thread exit ");
        buffer.append(thread.getName());
        buffer.append(" ");
        buffer.append(thread.getClass().getCanonicalName());
        buffer.append('\n');

        trace(key, buffer.toString());
    }

    /**
     * trace run of the supplied Runnable to System.out
     *
     * this should only be triggered from a call to an implementation of java.lang.Runnable.run"
     *
     * @param runnable the runnable being run
     */
    public void traceRun(Runnable runnable)
    {
        traceRun(runnable, "out");
    }

    /**
     * trace start of the supplied thread to the trace stream identified by key
     *
     * this should only be triggered from the call an implementation of java.lang.Runnable.run"
     *
     * @param runnable the runnable being run
     * @param key an object identifying the trace stream to which output should be generated
     */
    public void traceRun(Runnable runnable, Object key)
    {
        StringBuffer buffer = new StringBuffer();
        StackTraceElement[] stack = getStack();
        int l = stack.length;
        int t = triggerIndex(stack);
        if (t < 0) {
            return;
        }

        int i = matchIndex(stack, "run", false, false, false, t, l);
        if (i < 0) {
            // illegal usage
            traceStack("ThreadMonitorHelper.traceRun : should only be triggered below Runnable.run\n", key);
            return;
        }

        if (runnable instanceof Thread) {
            Thread thread = (Thread) runnable;
            buffer.append("*** Thread run ");
            buffer.append(thread.getName());
            buffer.append(" ");
        } else {
            buffer.append("*** Runnable run ");            
            buffer.append(runnable.toString());
            buffer.append(" ");
        }
        buffer.append(runnable.getClass().getCanonicalName());
        buffer.append('\n');

        buffer.append("    from ");
        if (i < l - 1) {
            printlnFrame(buffer, stack[i + 1]);
        } else {
            buffer.append(" VM runtime\n");
        }
        trace(key, buffer.toString());
    }
}
