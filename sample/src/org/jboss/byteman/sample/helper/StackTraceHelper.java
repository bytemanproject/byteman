package org.jboss.byteman.sample.helper;

import org.jboss.byteman.rule.helper.Helper;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.exception.ExecuteException;

import java.util.Map;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Helper class providing support for stack scanning and tracing
 */
public class StackTraceHelper extends Helper
{
    private static String RULE_CLASS_NAME = Rule.class.getCanonicalName();
    private static String RULE_EXECUTE_METHOD_NAME = "execute";

    protected StackTraceElement[] stack;

    protected StackTraceHelper(Rule rule) {
        super(rule);
        stack = Thread.currentThread().getStackTrace();
    }

    /**
     * return the index of the frame for the trigger method below which the rule system created
     * this helper or -1 if it cannot be found
     * @return the index of the frame for the trigger method or -1 if it cannot be found
     */
    protected int triggerIndex()
    {
        return triggerIndex(stack);
    }

    /**
     * return the index of the frame in stack for the trigger method below which the rule system
     * was entered or -1 if it cannot be found
     * @return the index of the frame for the trigger method or -1 if it cannot be found
     */
    protected int triggerIndex(StackTraceElement[] stack)
    {
        int l= stack.length;
        int i;
        // find the trigger method frame above the rule engine entry point
        // we should see two calls to rule.execute()
        for (i = 0; i < l; i++) {
            if (RULE_CLASS_NAME.equals(stack[i].getClassName()) &&
                    RULE_EXECUTE_METHOD_NAME.equals(stack[i].getMethodName())) {
                break;
            }
        }

        if (i >= l - 1 ||
                !RULE_CLASS_NAME.equals(stack[i].getClassName()) ||
                !RULE_EXECUTE_METHOD_NAME.equals(stack[i].getMethodName())) {
            // illegal usage
            new ExecuteException("StacktraceHelper.traceStack : can only be called below Rule.execute()").printStackTrace();
            return -1;
        }

        return  i + 2;
    }

    /**
     * return the index of the first frame at or below index start which matches pattern
     * @param pattern a pattern to be matched against the concatenated stack classname and methodname using
     * String.matches()
     * @param start the index of the first frame which should be tested for a match. this must be greater than
     * or equal to the trigger index.
     * @param limit the index of the first frame which should not be tested for a match. this must be less than
     * or equal to the stack length
     * @return the index of the matching frame between start and limit - 1 or -1 if it no match found
     */
    protected int matchIndex(String pattern, int start, int limit)
    {
        int l= stack.length;
        int i = start;
        // find the trigger method frame above the rule engine entry point
        // we should see two calls to rule.execute()
        for (; i < limit; i++) {
            String fullName = stack[i].getClassName() + "." + stack[i].getMethodName();
            if (fullName.matches(pattern)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * print the details of the current stack frame at index idx followed by a newline to buffer by calling
     * printlnFrame(buffer, stack[idx])
     * @param buffer
     * @param idx
     */
    protected void printlnFrame(StringBuffer buffer, int idx)
    {
        printlnFrame(buffer, stack[idx]);
    }

    /**
     * print the details of stack frame at index idx to buffer
     * print the details of stack frame to buffer
     * @param buffer
     * @param idx
     */
    protected void printFrame(StringBuffer buffer, int idx)
    {
        printFrame(buffer, stack[idx]);
    }

    /**
     * print the details of stack frame followed by a newline to buffer by calling
     * printlnFrame(buffer, frame) then buffer.append('\n')
     * @param buffer
     * @param frame
     */
    protected void printlnFrame(StringBuffer buffer, StackTraceElement frame)
    {
        printFrame(buffer, frame);
        buffer.append('\n');
    }

    /**
     * print the details of stack frame to buffer
     * @param buffer
     * @param frame
     */
    protected void printFrame(StringBuffer buffer, StackTraceElement frame)
    {
        buffer.append(frame.getClassName());
        buffer.append(".");
        buffer.append(frame.getMethodName());
        buffer.append(" at ");
        buffer.append(frame.getFileName());
        buffer.append(":");
        buffer.append(frame.getLineNumber());
    }

    /**
     * print a stack trace to System.out by calling traceStack(null)
     */
    public void traceStack()
    {
        traceStack(null);
    }

    /**
     * print a stack trace to System.out by calling traceStack(prefix, "out")
     */
    public void traceStack(String prefix)
    {
        traceStack(prefix, "out");
    }

    /**
     * print a stack trace to System.out by calling traceStack(prefix, key, 0)
     */
    public void traceStack(String prefix, Object key)
    {
        traceStack(prefix, key, 0);
    }

    /**
     * print a stack trace to System.out by calling traceStack(null, maxFrames)
     */
    public void traceStack(int maxFrames)
    {
        traceStack(null, maxFrames);
    }

    /**
     * print a stack trace to System.out by calling traceStack(prefix, "out", maxFrames)
     */
    public void traceStack(String prefix, int maxFrames)
    {
        traceStack(prefix, "out", maxFrames);
    }

    /**
     * print a stack trace to the trace stream identified by key
     *
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace for thread " + Thread.currentThread().getName() + "\n" is used
     * @param key an object identifying the trace stream to which output should be generated
     * @param maxFrames the maximum number of frames to print or 0 if no limit should apply
     */
    public void traceStack(String prefix, Object key, int maxFrames)
    {
        StringBuffer buffer = new StringBuffer();
        int l = stack.length;
        int i = triggerIndex();

        if (i < 0) {
            return;
        }

        if (prefix != null) {
            buffer.append(prefix);
        } else {
            buffer.append("Stack trace for thread ");
            buffer.append(Thread.currentThread().getName());
            buffer.append('\n');
        }
        boolean dotdotdot = false;

        if (maxFrames > 0 && (i + maxFrames) < l) {
            l = i + maxFrames;
            dotdotdot = true;
        }
        
        for (; i < l; i++) {
            printlnFrame(buffer, i);
        }
        if (dotdotdot) {
            buffer.append("  . . .\n");
        }

        trace(key, buffer.toString());
    }

    /**
     * print all stack frames which match pattern to System.out by calling traceStackMatching(pattern, null)
     */

    public void traceStackMatching(String pattern)
    {
        traceStackMatching(pattern, null);
    }

    /**
     * print all stack frames which match pattern to System.out preceded by prefix by calling
     * traceStackMatching(pattern, null, "out")
     */

    public void traceStackMatching(String pattern, String prefix)
    {
        traceStackMatching(pattern, null, "out");
    }

    /**
     * print all stack frames which match pattern to the trace stream identified by key preceded by prefix.
     *
     * @param pattern a pattern which will be matched against the concatenated classname and
     * method name of the stack frame by calling String.matches()
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace for thread " + Thread.currentThread().getName() + " matching " + pattern + "\n" is used
     * @param key an object identifying the trace stream to which output should be generated
     */

    public void traceStackMatching(String pattern, String prefix, Object key)
    {
        StringBuffer buffer = new StringBuffer();
        int l = stack.length;
        int i = triggerIndex();

        if (i < 0) {
            return;
        }

        if (prefix != null) {
            buffer.append(prefix);
        } else {
            buffer.append("Stack trace for thread ");
            buffer.append(Thread.currentThread().getName());
            buffer.append(" matching ");
            buffer.append(pattern);
            buffer.append('\n');
        }
        for (; i < l; i++) {
            String fullName = stack[i].getClassName() + "." +  stack[i].getMethodName();
            if (fullName.matches(pattern)) {
                printlnFrame(buffer, i);
            }
        }

        trace(key, buffer.toString());
    }

    /**
     * print all stack frames between the frames which match start and end to System.out by calling
     * traceStackMatching(from, to, null)
     */

    public void traceStackBetween(String from, String to)
    {
        traceStackBetween(from, to, null);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out preceded by prefix
     * by calling traceStackMatching(from, to, null, "out")
     */

    public void traceStackBetween(String from, String to, String prefix)
    {
        traceStackBetween(from, to, null, "out");
    }

    /**
     * print all stack frames between the frames which match start and end to the trace stream identified by key
     * preceded by prefix.
     *
     * @param from a pattern which identifies the first frame which should be printed. from will be matched against
     * the concatenated classname and method name of each successive stack frame by calling String.matches().
     * If null is supplied then the trigger frame will be used as the first frame to print. If a non-null value
     * is supplied and no match is foudn then no farmes will be printed.
     * @param to a pattern which identifies the last frame which should be printed. to will be matched against
     * the concatenated classname and method name of each successive stack frame by calling String.matches().
     * If null is supplied or no match is found then the bottom frame will be used as the last frame to print.
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace (restricted) for " + Thread.currentThread().getName() + "\n" is used
     * @param key an object identifying the trace stream to which output should be generated
     */

    public void traceStackBetween(String from, String to, String prefix, Object key)
    {
        StringBuffer buffer = new StringBuffer();
        int l = stack.length;
        int i = triggerIndex();
        if (i < 0) {
            return;
        }

        int first;
        if (from != null) {
            first = matchIndex(from, i, l);
            if (first < 0) {
                return;
            }
        } else {
            first = i;
        }

        int last;
        if (to != null) {
            last = matchIndex(to, first + 1, l);
            if (last < 0) {
                last = l - 1;
            }
        } else {
            last = l - 1;
        }

        if (prefix != null) {
            buffer.append(prefix);
        } else {
            buffer.append("Stack trace (restricted) for ");
            buffer.append(Thread.currentThread().getName());
            buffer.append('\n');
        }
        for (i = first; i < last; i++) {
            printlnFrame(buffer, i);
        }

        trace(key, buffer.toString());
    }

    /**
     * print a stack trace of all threads in the system to System.out by calling traceStacks("out")
     */
    public void traceAllStacks()
    {
        traceAllStacks("out");
    }

    /**
     * print a stack trace of all threads in the system to the trace stream keyed by key
     */
    public void traceAllStacks(String key)
    {
        StringBuffer buffer = new StringBuffer();
        if (threadMXBean != null) {
            ThreadInfo[] threadInfo = threadMXBean.dumpAllThreads(threadMXBean.isObjectMonitorUsageSupported(), threadMXBean.isSynchronizerUsageSupported());
            for (int i = 0; i < threadInfo.length; i++) {
                buffer.append(threadInfo[i].toString());
            }
        } else {
            Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
            for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
                Thread thread = entry.getKey();
                StackTraceElement[] stack = entry.getValue();
                int l = stack.length;
                int i = 0;
                buffer.append("Stack trace for thread ");
                buffer.append(Thread.currentThread().getName());
                buffer.append('\n');
                for (; i < l; i++) {
                    printlnFrame(buffer, i);
                }
                buffer.append('\n');
            }
        }
        trace(key, buffer.toString());
    }

    private static ThreadMXBean threadMXBean = initThreadMXBean();

    private static ThreadMXBean initThreadMXBean() {
        try {
            return AccessController.doPrivileged(
                new PrivilegedExceptionAction<ThreadMXBean>() {
                    public ThreadMXBean run() throws Exception {
                        return ManagementFactory.getThreadMXBean();
                    }
                });
        } catch (Exception exp) {
            throw new UnsupportedOperationException(exp);
        }
    }

}