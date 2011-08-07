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
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Helper class used by ThreadHistoryMonitorHelper script to trace thread operations. This
 * is essentially an extension of the ThreadMonitorHelper which uses maps to store the thread
 * history rather than writing it out.
 *
 * The helper also implements ThreadHistoryMonitorHelperMXBean to allow this class to be
 * registered as an mbean @see #registerHelperMBean(String).
 * 
 */
public class ThreadHistoryMonitorHelper extends Helper
    implements ThreadHistoryMonitorHelperMXBean
{
    private static ConcurrentHashMap<String, ThreadMonitorEvent> createMap = new ConcurrentHashMap<String, ThreadMonitorEvent>();
    private static ConcurrentHashMap<String, ThreadMonitorEvent> startMap = new ConcurrentHashMap<String, ThreadMonitorEvent>();
    private static ConcurrentHashMap<String, ThreadMonitorEvent> exitMap = new ConcurrentHashMap<String, ThreadMonitorEvent>();
    private static ConcurrentHashMap<String, ThreadMonitorEvent> runMap = new ConcurrentHashMap<String, ThreadMonitorEvent>();
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
        synchronized (createMap) {
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

    @Override
    public ThreadMonitorEvent[] getCreateEvents() {
        ThreadMonitorEvent[] events = new ThreadMonitorEvent[createMap.size()];
        return createMap.values().toArray(events);
    }

    @Override
    public ThreadMonitorEvent[] getStartEvents() {
        ThreadMonitorEvent[] events = new ThreadMonitorEvent[startMap.size()];
        return startMap.values().toArray(events);
    }

    @Override
    public ThreadMonitorEvent[] getExitEvents() {
        ThreadMonitorEvent[] events = new ThreadMonitorEvent[exitMap.size()];
        return exitMap.values().toArray(events);
    }

    @Override
    public ThreadMonitorEvent[] getRunEvents() {
        ThreadMonitorEvent[] events = new ThreadMonitorEvent[runMap.size()];
        return runMap.values().toArray(events);
    }

    @Override
    public String getEventReport() throws IOException {
        StringWriter sw = new StringWriter();
        Formatter format = new Formatter(sw);
        writeEvents(format, "Thread.create", createMap.values());
        writeEvents(format, "Thread.start", startMap.values());
        writeEvents(format, "Thread.exit", exitMap.values());
        writeEvents(format, "Runable.run", runMap.values());
        sw.close();
        return sw.toString();
    }
    @Override
    public void writeEventsToFile(String type, String path) throws IOException {
        FileWriter fw = new FileWriter(path);
        Formatter format = new Formatter(fw);
        if(type == null || type.length() == 0 || type.equalsIgnoreCase("create"))
            writeEvents(format, "Thread.create Events", createMap.values());
        if(type == null || type.length() == 0 || type.equalsIgnoreCase("start"))
            writeEvents(format, "Thread.start Events", startMap.values());
        if(type == null || type.length() == 0 || type.equalsIgnoreCase("exit"))
            writeEvents(format, "Thread.exit Events", exitMap.values());
        if(type == null || type.length() == 0 || type.equalsIgnoreCase("run"))
            writeEvents(format, "Runable.run Events", runMap.values());
        fw.close();
    }

    /**
     * Write all events to the file given by path
     *
     * @param path
     * @throws IOException
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
    }
    private void doWriteAllEvents(String path) throws IOException {
        FileWriter fw = new FileWriter(path);
        Formatter format = new Formatter(fw);
        writeEvents(format, "Thread.create", createMap.values());
        writeEvents(format, "Thread.start", startMap.values());
        writeEvents(format, "Thread.exit", exitMap.values());
        writeEvents(format, "Runable.run", runMap.values());
        fw.close();
        System.err.println("Wrote events to: "+path);
    }
    private void writeEvents(Formatter fw, String title, Collection<ThreadMonitorEvent> events) {
        int count = 0;
        TreeSet<String> threadNames = new TreeSet<String>();
        fw.format("+++ Begin %s Events, count=%d +++\n", title, events.size());
        for(ThreadMonitorEvent event : events) {
            if(event.getRunnableClass() != null) {
                fw.format("#%d, %s(runnable=%s)\n%s\n", count++, event.getThreadName(), event.getRunnableClass(), event.getFullStack());
            } else {
                fw.format("#%d, %s\n%s\n", count++, event.getThreadName(), event.getFullStack());
            }
            threadNames.add(event.getThreadName());
        }
        fw.format("+++ End %s Events +++\n", title);
        fw.format("+++ Begin %s Thread Names +++\n", title);
        for(String name : threadNames) {
            fw.format("%s\n", name);
        }
        fw.format("+++ End %s Thread Names +++\n", title);
    }

    /**
     * trace creation of the supplied thread to System.out
     *
     * this should only be triggered from the constructor for class java.lang.Thread"
     *
     * @param thread the newly created thread
     */
    public void traceCreate(Thread thread, int depth)
    {
        ThreadMonitorEvent event = newThreadEvent(thread, "create");
        createMap.put(event.getThreadName(), event);

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
        ThreadMonitorEvent event = newThreadEvent(thread, "start");
        startMap.put(event.getThreadName(), event);
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
        ThreadMonitorEvent event = newThreadEvent(thread, "exit");
        exitMap.put(event.getThreadName(), event);
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
        Thread thread = Thread.currentThread();
        ThreadMonitorEvent event = newThreadEvent(thread, "run");
        event.setRunnableClass(runnable.getClass().toString());
        runMap.put(event.getThreadName(), event);
    }

    /**
     * Common ThreadMonitorEvent creation method.
     *
     * @param thread - the thread associated with the event
     * @param type - the type of the event.
     * @return the ThreadMonitorEvent instance for the event.
     */
    private ThreadMonitorEvent newThreadEvent(Thread thread, String type) {
        StringBuffer line = new StringBuffer();
        StringBuilder buffer = new StringBuilder();
        StackTraceElement[] stack = getStack();
        ArrayList<String> stackInfo = new ArrayList<String>();
        int l = stack.length;
        int t = super.triggerIndex(stack);

        line.append("*** Thread ");
        line.append(type);
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
        String name = thread.getName();
        String fullStack = buffer.toString();
        ThreadMonitorEvent event = new ThreadMonitorEvent(type, name, stackInfo, fullStack);
        return event;
    }

}
