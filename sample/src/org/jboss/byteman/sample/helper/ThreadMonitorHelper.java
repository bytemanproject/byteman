package org.jboss.byteman.sample.helper;

import org.jboss.byteman.rule.Rule;

/**
 * Helper class used by ThreadMonitorHelper script to trace thread operations
 */
public class ThreadMonitorHelper extends StackTraceHelper
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
        int l = stack.length;
        int t = triggerIndex();
        if (t < 0) {
            return;
        }

        int i = matchIndex("java.lang.Thread.<init>", t, l);
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
        while (i < l && matchIndex(".*<init>", i, i) >= 0) {
            i++;
        }
        if (i == l) {
            // happens when a system thread is created by the runtime
            buffer.append("    from VM runtime\n");
        } else {
            buffer.append("    from ");
            printlnFrame(buffer, i);
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
        int l = stack.length;
        int t = triggerIndex();
        if (t < 0) {
            return;
        }

        int i = matchIndex("java.lang.Thread.start", t, l);
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
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        if (++i == l) {
            // should not happen
            traceStack("ThreadMonitorHelper.traceStart : failed to find frame for caller of start\n", key);
            return;
        }

        buffer.append("    from ");
        printlnFrame(buffer, i);
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
        int l = stack.length;
        int t = triggerIndex();
        if (triggerIndex() < 0) {
            return;
        }
        int i = matchIndex("java.lang.Thread.exit", t, l);
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
}
