/*
* JBoss, Home of Professional Open Source
* Copyright 2011 Red Hat and individual contributors
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
* @author Andrew Dinn
* @author Scott.Stark
*/
package org.jboss.byteman.sample.helper;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Helper class used by ThreadHistoryMonitorHelper script to trace thread operations. This
 * is essentially an extension of the ThreadMonitorHelper which uses maps to store the thread
 * history rather than writing it out.
 * <p>
 * The helper also implements ThreadHistoryMonitorHelperMXBean to allow this class to be
 * registered as an mbean @see #registerHelperMBean(String).
 * 
 */
public class ThreadHistoryMonitorHelper extends Helper
    implements ThreadHistoryMonitorHelperMXBean
{
    private static Map<ThreadMonitored, ThreadMonitored> monitoredThreads =
            new ConcurrentHashMap<ThreadMonitored, ThreadMonitored>();
    private static Queue<ThreadMonitorEvent> createList      = new ConcurrentLinkedQueue<ThreadMonitorEvent>();
    private static Queue<ThreadMonitorEvent> startList       = new ConcurrentLinkedQueue<ThreadMonitorEvent>();
    private static Queue<ThreadMonitorEvent> exitList        = new ConcurrentLinkedQueue<ThreadMonitorEvent>();
    private static Queue<ThreadMonitorEvent> runList         = new ConcurrentLinkedQueue<ThreadMonitorEvent>();
    private static Queue<ThreadMonitorEvent> interruptedList = new ConcurrentLinkedQueue<ThreadMonitorEvent>();

    /** The first instance of ThreadHistoryMonitorHelper that will be used as the mbean
     * by {@link #registerHelperMBean(String)}.
     */
    private static ThreadHistoryMonitorHelper INSTANCE;
    /** org.jboss.byteman.sample.helper.debug system property debug mode flag */
    private static boolean DEBUG;

    /**
     * Looks to the org.jboss.byteman.sample.helper.debug system property to
     * set the class DEBUG mode flag.
     */
    public static void activated() {
        DEBUG = Boolean.getBoolean("org.jboss.byteman.sample.helper.debug");
        if(DEBUG)
            System.err.println("ThreadHistoryMonitorHelper.activated, ");
    }
    public static void installed(Rule rule) {
        if(DEBUG)
            System.err.println("ThreadHistoryMonitorHelper.installed, "+rule);
    }
    protected ThreadHistoryMonitorHelper(Rule rule) {
        super(rule);
        INSTANCE = this;
    }

    /**
     * Register the INSTANCE as an mbean under the given name.
     * @param name - the object name string to register the INSTANCE under
     */
    public void registerHelperMBean(String name) {
        synchronized (createList) {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                ObjectName oname = new ObjectName(name);
                if(mbs.isRegistered(oname) == false)
                    mbs.registerMBean(INSTANCE, oname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * trace creation of the supplied thread to System.out
     *
     * this should only be triggered from the {@link Thread} constructor
     *
     * @param thread the newly created thread
     * @param depth unused
     */
    public void traceCreate(Thread thread, int depth)
    {
        ThreadMonitored monitoredThread = getMonitoredThread(thread);
        ThreadMonitored createdByMonitoredThread = getMonitoredThread(Thread.currentThread());
        monitoredThread.setCreatedBy(createdByMonitoredThread);

        ThreadMonitorEvent event = newThreadEvent(monitoredThread, thread, ThreadMonitorEventType.CREATE);
        createList.add(event);

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
        ThreadMonitored monitoredThread = getMonitoredThread(thread);
        ThreadMonitorEvent event = newThreadEvent(monitoredThread, thread, ThreadMonitorEventType.START);
        startList.add(event);
    }

    /**
     * trace exit of the supplied thread to System.out
     *
     * this should only be triggered from the call to {@link Thread} exit method
     *
     * @param thread the exiting thread
     */
    public void traceExit(Thread thread)
    {
        ThreadMonitored monitoredThread = getMonitoredThread(thread);
        ThreadMonitorEvent event = newThreadEvent(monitoredThread, thread, ThreadMonitorEventType.EXIT);
        exitList.add(event);
    }
    
    /**
     * trace interrupted of the supplied thread to System.out
     *
     * this should only be triggered from the call to {@link Thread#interrupt()}
     *
     * @param thread  the interrupting thread
     */
    public void traceInterrupt(Thread thread)
    {
        ThreadMonitored monitoredThread = getMonitoredThread(thread);
        ThreadMonitorEvent event = newThreadEvent(monitoredThread, thread, ThreadMonitorEventType.INTERRUPT);
        interruptedList.add(event);
    }

    /**
     * trace run of the supplied Runnable to System.out
     *
     * this should only be triggered from a call to an implementation of {@link Runnable#run()}
     *
     * @param runnable the runnable being run
     */
    public void traceRun(Runnable runnable)
    {
        Thread thread = Thread.currentThread();
        ThreadMonitored monitoredThread = getMonitoredThread(thread);
        monitoredThread.setRunnableClass(runnable.getClass());
        ThreadMonitorEvent event = newThreadEvent(monitoredThread, thread, ThreadMonitorEventType.RUN);
        runList.add(event);
    }

    public ThreadMonitorEvent[] getCreateEvents() {
        ThreadMonitorEvent[] events = new ThreadMonitorEvent[createList.size()];
        return createList.toArray(events);
    }

    public ThreadMonitorEvent[] getStartEvents() {
        ThreadMonitorEvent[] events = new ThreadMonitorEvent[startList.size()];
        return startList.toArray(events);
    }

    public ThreadMonitorEvent[] getExitEvents() {
        ThreadMonitorEvent[] events = new ThreadMonitorEvent[exitList.size()];
        return exitList.toArray(events);
    }

    public ThreadMonitorEvent[] getRunEvents() {
        ThreadMonitorEvent[] events = new ThreadMonitorEvent[runList.size()];
        return runList.toArray(events);
    }

    public String getEventReport() throws IOException {
        StringWriter sw = new StringWriter();
        Formatter format = new Formatter(sw);
        writeFullEvents(format, "Thread.create", createList);
        writeFullEvents(format, "Thread.start", startList);
        writeFullEvents(format, "Thread.exit", exitList);
        writeFullEvents(format, "Runable.run", runList);
        sw.close();
        return sw.toString();
    }

    public void writeEventsToFile(String type, String path) throws IOException {
        FileWriter fw = new FileWriter(path);
        Formatter format = new Formatter(fw);
        if(type == null || type.length() == 0 || type.equalsIgnoreCase("create"))
            writeFullEvents(format, "Thread.create Events", createList);
        if(type == null || type.length() == 0 || type.equalsIgnoreCase("start"))
            writeFullEvents(format, "Thread.start Events", startList);
        if(type == null || type.length() == 0 || type.equalsIgnoreCase("exit"))
            writeFullEvents(format, "Thread.exit Events", exitList);
        if(type == null || type.length() == 0 || type.equalsIgnoreCase("run"))
            writeFullEvents(format, "Runable.run Events", runList);
        fw.close();
    }

    /**
     * Write all events to the file given by path
     *
     * @param path path to file
     * @throws IOException if an io error occurs
     */
    public void writeAllEventsToFile(String path) throws IOException {
        System.err.println("writeAllEventsToFile: "+path);
        writeAllEventsToFile(path, 0);
    }

    /**
     * Write all events to the file given by path, repeating sampleCount times
     * at 5 second intervals. The actual filename of each sample report will be either
     *  path-n where n = [0,sampleCount] if path does not contain a suffix, for example:
     *  /tmp/report-0
     *  /tmp/report-1
     *  /tmp/report-3
     * or
     *  pathbase-n.suffix if there is a '.' delimited suffix (.txt), for example:
     *  /tmp/report-0.txt
     *  /tmp/report-1.txt
     *  /tmp/report-3.txt
     * @param path - the path to the event report file
     * @param sampleCount - the number of samples to take
     * @throws IOException - thrown on any IO failure
     */
    public synchronized void writeAllEventsToFile(String path, int sampleCount) throws IOException {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ArrayList<ScheduledFuture> tasks = new ArrayList<ScheduledFuture>();
        // Look for path suffix
        String suffix = null;
        String base = path;
        int lastDot = path.lastIndexOf('.');
        if(lastDot > 0) {
            suffix = path.substring(lastDot);
            base = path.substring(0, lastDot);
        }
        for(int n = 0; n <= sampleCount; n ++) {
            final String samplePath = base + "-" + n + (suffix != null ? suffix : "");
            int delay = 5*n;
            ScheduledFuture future = ses.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        doWriteAllEvents(samplePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, delay, TimeUnit.SECONDS);
            tasks.add(future);
        }
        // Wait for tasks to complete
        for(ScheduledFuture future : tasks) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            ses.shutdown();
            ses.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Waiting on finishing scheduled writer executor "
                + ses + " failed", e);
        }
    }
    private void doWriteAllEvents(String path) throws IOException {
        FileWriter fw = new FileWriter(path);
        Formatter format = new Formatter(fw);
        writeFullEvents(format, "Thread.create", createList);
        writeFullEvents(format, "Thread.start", startList);
        writeFullEvents(format, "Thread.exit", exitList);
        writeFullEvents(format, "Runable.run", runList);
        
        // thread which were started but not exited yet
        Map<ThreadMonitored, ThreadMonitorEvent> runningThreads = getThreadEventMap(startList);
        for(ThreadMonitorEvent exitEvent: exitList) {
            runningThreads.remove(exitEvent.getMonitoredThread());
        }
        writeThreadNames(format, "Thread.start but not exit", runningThreads.values());

        fw.close();
        System.err.println("Wrote events to: " + path);
    }

    private void writeFullEvents(Formatter fw, String title, Collection<ThreadMonitorEvent> events) {
        int count = 0;
        List<String> threadNames = new ArrayList<String>();
        fw.format("+++ Begin %s Events, count=%d +++\n", title, events.size());
        for(ThreadMonitorEvent event : events) {
            ThreadMonitored thread = event.getMonitoredThread();
            if(thread.getRunnableClass() != null) {
                fw.format("#%d [%s], %s:%s(runnable=%s, by=%s)\n%s\n", count++, title, thread.getThreadName(), thread.getThreadId(),
                        thread.getRunnableClass(), thread.getCreatedBy(), event.getFullStack());
            } else {
                fw.format("#%d [%s], %s:%s(by=%s)\n%s\n", count++, title, thread.getThreadName(), thread.getThreadId(), thread.getCreatedBy(), event.getFullStack());
            }
            threadNames.add(thread.toString());
        }
        fw.format("+++ End %s Events +++\n", title);
        writeThreadNames(fw, title, events);;
    }
    
    private void writeThreadNames(Formatter fw, String title, Collection<ThreadMonitorEvent> events) {
        List<String> threadNames = new ArrayList<String>();
        for(ThreadMonitorEvent event : events) {
            threadNames.add(event.getMonitoredThread().toString());
        }
        fw.format("+++ Begin %s Thread Names (count=%s) +++\n", title, threadNames.size());
        for(String name : threadNames) {
            fw.format("%s\n", name);
        }
        fw.format("+++ End %s Thread Names +++\n", title);
    }

    /**
     * Common ThreadMonitorEvent creation method.
     *
     * @param thread - the thread associated with the event
     * @param eventType - the type of the event.
     * @return the ThreadMonitorEvent instance for the event.
     */
    public ThreadMonitorEvent newThreadEvent(ThreadMonitored threadMonitored, Thread thread, ThreadMonitorEventType eventType) {
        StringBuffer line = new StringBuffer();
        StringBuilder buffer = new StringBuilder();
        StackTraceElement[] stack = getStack();
        ArrayList<String> stackInfo = new ArrayList<String>();
        int l = stack.length;
        int t = super.triggerIndex(stack);

        line.append("*** Thread ");
        line.append(eventType);
        line.append(" ");
        line.append(thread.getName());
        line.append(" ");
        line.append(thread.getClass().getCanonicalName());
        line.append('\n');
        stackInfo.add(line.toString());
        line.setLength(0);

        for(int n = t; n < l; n ++) {
            StackTraceElement frame = stack[n];
            super.printlnFrame(line, frame);
            buffer.append(line);
            stackInfo.add(line.toString());
            line.setLength(0);
        }
        //
        String fullStack = buffer.toString();
        ThreadMonitorEvent threadEvent = new ThreadMonitorEvent(threadMonitored, eventType, stackInfo, fullStack);
        return threadEvent;
    }

    /**
     * Returning monitored thread belonging to the provided thread object.
     * If such monitored thread does not exist (is not known to {@link ThreadHistoryMonitorHelper})
     * then brand new {@link ThreadMonitored} object is created and is added to the list
     * checked by the helper class.
     *
     * @param thread  thread that belonging  ThreadMonitorThread is search for
     * @return  ThreadMonitorThread which belongs to provided thread or null in no such known
     */
    private ThreadMonitored getMonitoredThread(Thread thread) {
        ThreadMonitored mt = ThreadMonitored.newMonitoredThread(thread);
        if(monitoredThreads.containsKey(mt)) {
            return monitoredThreads.get(mt);
        } else {
            monitoredThreads.put(mt, mt);
            return mt;
        }
    }


    private Map<ThreadMonitored, ThreadMonitorEvent> getThreadEventMap(Iterable<ThreadMonitorEvent> events) {
        Map<ThreadMonitored, ThreadMonitorEvent> threadEventMap = new HashMap<ThreadMonitored, ThreadMonitorEvent>();
        for(ThreadMonitorEvent event: events) {
            threadEventMap.put(event.getMonitoredThread(), event);
        }
        return threadEventMap;
    }
}
