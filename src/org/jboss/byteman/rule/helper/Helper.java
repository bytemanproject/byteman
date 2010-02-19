/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-9, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.byteman.rule.helper;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.synchronization.*;
import org.jboss.byteman.agent.Transformer;

import java.io.*;
import java.util.*;

/**
 * This is the default helper class which is used to define builtin operations for rules.
 * Methods provided on this class are automatically made available as builtin operations in
 * expressions appearing in rule event bindings, conditions and actions. Although Helper
 * methods are all instance methods the message recipient for the method call is implicit
 * and does not appear in the builtin call. It does, however, appear in the runtime
 * invocation, giving the builtin operation access to the helper and thence the rule being
 * fired.
 */
public class Helper
{
    protected Rule rule;

    protected Helper(Rule rule)
    {
        this.rule = rule;
    }
    // tracing support
    /**
     * builtin to print a message during rule execution. n.b. this always returns true which
     * means it can be invoked during condition execution
     * @param text the message to be printed as trace output
     * @return true
     */
    public boolean debug(String text)
    {
        if (Transformer.isDebug()) {
            System.out.println("rule.debug{" + rule.getName() + "} : " + text);
        }
        return true;
    }

    // file based trace support
    /**
     * open a trace output stream identified by identifier to a file located in the current working
     * directory using a unique generated name
     * @param identifier an identifier used subsequently to identify the trace output stream
     * @return true if new file and stream was created, false if a stream identified by identifier
     * already existed or the identifer is null, "out" or "err"
     */
    public boolean openTrace(Object identifier)
    {
        return openTrace(identifier, null);
    }

    /**
     * open a trace output stream identified by identifier to a file located in the current working
     * directory using the given file name or a generated name if the supplied name is null
     * @param identifier an identifier used subsequently to identify the trace output stream
     * @return true if new file and stream was created, false if a stream identified by identifier
     * already existed or if a file of the same name already exists or the identifer is null, "out"
     * or "err"
     */
    public boolean openTrace(Object identifier, String fileName)
    {
        if (identifier == null) {
            return false;
        }

        synchronized(traceMap) {
            PrintStream stream = traceMap.get(identifier);
            String name = fileName;
            if (stream != null) {
                return false;
            }
            if (fileName == null) {
                name = nextFileName();
            }
            File file = new File(name);

            if (file.exists() && !file.canWrite()) {
                if (fileName == null) {
                    // keep trying new names until we hit an unused one
                    do {
                        name = nextFileName();
                        file = new File(name);
                    } while (file.exists() && !file.canWrite());
                } else {
                    // can't open file as requested
                    return false;
                }
            }
                
            FileOutputStream fos;

            try {
                if (file.exists()) {
                    fos = new FileOutputStream(file, true);
                } else {
                    fos = new FileOutputStream(file, true);
                }
            } catch (FileNotFoundException e) {
                // oops, just return false
                return false;
            }

            PrintStream ps = new PrintStream(fos, true);

            traceMap.put(identifier, ps);

            return true;
        }
    }

    /**
     * close the trace output stream identified by identifier flushing any pending output
     * @param identifier an identifier used subsequently to identify the trace output stream
     * @return true if the stream was flushed and closed, false if no stream is identified by identifier
     * or the identifer is null, "out" or "err"
     */
    public boolean closeTrace(Object identifier)
    {
        if (identifier == null ||
                identifier.equals("out") ||
                identifier.equals("err")) {
            return false;
        }

        synchronized(traceMap) {
            PrintStream ps = traceMap.get(identifier);
            if (ps != null) {
                // need to do the close while synchornized so we ensure an open cannot
                // proceed until we have flushed all changes to disk
                ps.close();
                traceMap.remove(identifier);
                return true;
            }
        }

        return false;
    }

    /**
     * call trace("out, message").
     * @param message
     * @return true
     */
    public boolean trace(String message)
    {
        return trace("out", message);
    }

    /**
     * write the supplied message to the trace stream identified by identifier, creating a new stream
     * if none exists
     * @param identifier an identifier used subsequently to identify the trace output stream
     * @param message
     * @return true
     * @caveat if identifier is the string "out" or null the message will be written to System.out.
     * if identifier is the string "err" the message will be written to System.err.
     */
    public boolean trace(Object identifier, String message)
    {
        synchronized(traceMap) {
            PrintStream ps = traceMap.get(identifier);
            if (ps == null) {
                if (openTrace(identifier)) {
                    ps = traceMap.get(identifier);
                } else {
                    ps = System.out;
                }
            }
            ps.print(message);
            ps.flush();
        }
        return true;
    }

    /**
     * call traceln("out", message).
     * @param message
     * @return true
     */
    public boolean traceln(String message)
    {
        return traceln("out", message);
    }
    
    /**
     * write the supplied message to the trace stream identified by identifier, creating a new stream
     * if none exists, and append a new line
     * @param identifier an identifier used subsequently to identify the trace output stream
     * @param message
     * @return true
     * @caveat if identifier is the string "out" or null the message will be written to System.out.
     * if identifier is the string "err" the message will be written to System.err.
     */
    public boolean traceln(Object identifier, String message)
    {
        synchronized(traceMap) {
            PrintStream ps = traceMap.get(identifier);
            if (ps == null) {
                if (openTrace(identifier)) {
                    ps = traceMap.get(identifier);
                } else {
                    ps = System.out;
                }
            }
            ps.println(message);
            ps.flush();
        }
        return true;
    }

    // flag support
    /**
     * set a flag keyed by the supplied object if it is not already set
     * @param identifier the object identifying the relevant flag
     * @return true if the flag was clear before this call otherwise false
     */
    public boolean flag(Object identifier)
    {
        synchronized (flagSet) {
            return flagSet.add(identifier);
        }
    }

    /**
     * test the state of the flag keyed by the supplied object
     * @param identifier the object identifying the relevant flag
     * @return true if the flag is set otherwise false
     */
    public boolean flagged(Object identifier)
    {
        synchronized (flagSet) {
            return flagSet.contains(identifier);
        }
    }

    /**
     * clear the flag keyed by the supplied object if it is not already clear
     * @param identifier the object identifying the relevant flag
     * @return true if the flag was clear before this call otherwise false
     */
    public boolean clear(Object identifier)
    {
        synchronized (flagSet) {
            return flagSet.remove(identifier);
        }
    }

    // countdown support
    /**
     * for backwards compatibility
     */
    public boolean getCountDown(Object identifier)
    {
        return isCountDown(identifier);
    }

    /**
     * builtin to test test if a countdown has been installed
     * @param identifier an object which uniquely identifies the countdown in question
     * @return true if the countdown is currently installed
     */
    public boolean isCountDown(Object identifier)
    {
        synchronized (countDownMap) {
            return (countDownMap.get(identifier) != null);
        }
    }

    /**
     * alias for createCountDown provided for backwards compatibility
     */
    public boolean addCountDown(Object identifier, int count)
    {
        return createCountDown(identifier, count);
    }

    /**
     * builtin to test create a countdown identified by a specific object and with the specified
     * count. n.b. this builtin checks if a countdown identified by the supplied object is
     * currently installed, returning false if so, otherwise atomically adds the countdown
     * and returns true. This allows the builtin to be used safely in conditions where concurrent
     * rule firings (including firings of multiple rules) might otherwise lead to a race condition.
     * @param identifier an object which uniquely identifies the countdown in question
     * @param count the number of times the countdown needs to be counted down before the
     * countdown operation returns true. e.g. if count is supplied as 2 then the first two
     * calls to {@link #countDown(Object)} will return false and the third call will return true.
     * @return true if a new countdown is installed, false if one already exists.
     */
    public boolean createCountDown(Object identifier, int count)
    {
        synchronized (countDownMap) {
            if (countDownMap.get(identifier) == null) {
                countDownMap.put(identifier, new CountDown(count));
                return true;
            }
        }

        return false;
    }

    /**
     * builtin to decrement the countdown identified by a specific object, uninstalling it and
     * returning true only when the count is zero.
     * @param identifier an object which uniquely identifies the countdown in question
     * @return true if the countdown is installed and its count is zero, otherwise false
     */
    public boolean countDown(Object identifier)
    {
        synchronized (countDownMap) {
            CountDown countDown = countDownMap.get(identifier);

            if (countDown != null) {
                boolean result = countDown.decrement();
                if (result) {
                    countDownMap.remove(identifier);
                }
                return result;
            }
        }

        // we must only fire a decrement event once for a given counter

        return false;
    }

    // wait/notify support
    /**
     * test if there are threads waiting for an event identified by the supplied object to
     * be signalled
     * @param identifier an object identifying the event to be signalled
     * @return true if threads are waiting for the associated event to be signalled
     */
    public boolean waiting(Object identifier)
    {
        return (getWaiter(identifier, false) != null);
    }
    /**
     * wait for another thread to signal an event with no timeout. see
     * {@link #waitFor(Object, long)} for details and caveats regarding calling this builtin.
     * @param identifier an object used to identify the signal that is to be waited on.
     */
    public void waitFor(Object identifier)
    {
        waitFor(identifier, 0);
    }

    /**
     * wait for another thread to signal an event with a specific timeout or no timeout if zero
     * is supplied as the second argument. this may be called in a rule event, condition or action.
     * it will suspend the current thread pending signalling of the event at which point rule
     * processing will either continue or abort depending upon the type of signal. if an exception
     * is thrown it will be an instance of runtime exception which, in normal circumstances, will
     * cause the thread to exit. The exception may not kill the thread f the trigger method or
     * calling code contains a catch-all handler so care must be used to ensure that an abort of
     * waiting threads has the desired effect. n.b. care must also be employed if the current
     * thread is inside a synchronized block since there is a potential for the waitFor call to
     * cause deadlock.
     * @param identifier an object used to identify the signal that is to be waited on. n.b. the
     * wait operation is not performed using synchronization on the supplied object as the rule
     * system cannot safely release and reobtain locks on application data. this argument is used
     * as a key to identify a synchronization object private to the rule system.
     */
    public void waitFor(Object identifier, long millisecs)
    {
        Waiter waiter = getWaiter(identifier, true);

        waiter.waitFor(millisecs);
    }

    /**
     * call signalWake(Object, boolean) defaulting the second argument to
     * false
     */
    public boolean signalWake(Object identifier)
    {
        return signalWake(identifier, false);
    }

    /**
     * signal an event identified by the supplied object, causing all waiting threads to resume
     * rule processing and clearing the event. if there are no threads waiting either because
     * there has been no call to {@link #waitFor} or because some other thread has sent the signal
     * then this call returns false, otherwise it returns true. This operation is atomic,
     * allowing the builtin to be used in rule conditions.
     * @param identifier an object used to identify the which waiting threads the signal should
     * be delivered to. n.b. the operation is not performed using a notify on the supplied object.
     * this argument is used as a key to identify a synchronization object private to the rule
     * system.
     * @param mustMeet if true then the signal operation must not be delivered until some other
     * thread is actually waiting on a waiter identified by identifier. if there is no such waiter
     * when this method is called then the calling thread will suspend until one arrives.
     */
    public boolean signalWake(Object identifier, boolean mustMeet)
    {
        if (mustMeet == false) {
            Waiter waiter = removeWaiter(identifier);

            if (waiter != null) {
                return waiter.signalWake();
            }

            return false;
        } else {
            Waiter waiter;
            // may need to do test and insert atomically
            synchronized (waitMap) {
                // see if we have a waiter
                 waiter = removeWaiter(identifier);

                if (waiter != null) {
                    return waiter.signalWake();
                } else {
                    // insert a pre-signalled waiter
                    waiter = new Waiter(identifier, true, false);
                    waitMap.put(identifier, waiter);
                }
            }

            // ok, so we need to wait until a wait has happened

            synchronized (waiter) {
                while (!waiter.waiting()) {
                    try {
                        waiter.wait();
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            }

            // remove the association between the waiter and the wait map
            synchronized (waitMap) {
                removeWaiter(identifier);
            }
            return true;
        }
    }

    /**
     * for backwards compatibility
     */
    public boolean signalKill(Object identifier)
    {
        return signalThrow(identifier);
    }

    /**
     * for backwards compatibility
     */
    public boolean signalKill(Object identifier, boolean mustMeet)
    {
        return signalThrow(identifier, mustMeet);
    }

    /**
     * call signalThrow(Object, boolean) defaulting the second argument to
     * false
     */
    public boolean signalThrow(Object identifier)
    {
        return signalThrow(identifier, false);
    }

    /**
     * signal an event identified by the suppied object, causing all waiting threads to throw an
     * exception and clearing the event. if there are no objects waiting, either because there has been
     * no call to {@link #waitFor} or because some other thread has already sent the signal, then this
     * call returns false, otherwise it returns true. This operation is atomic, allowing the builtin
     * to be used safely in rule conditions.
     * @param identifier an object used to identify the which waiting threads the signal should
     * be delivered to. n.b. the operation is not performed using a notify on the supplied object.
     * this argument is used as a key to identify a synchronization object private to the rule
     * system.
     * @param mustMeet if true then the signal operation must not be delivered until some other
     * thread is actually waiting on a waiter identified by identifier. if there is no such waiter
     * when this method is called then the calling thread will suspend until one arrives.
     */
    public boolean signalThrow(Object identifier, boolean mustMeet)
    {
        if (mustMeet == false) {
            Waiter waiter = removeWaiter(identifier);

            if (waiter != null) {
                return waiter.signalThrow();
            }

            return false;
        } else {
            Waiter waiter;
            // may need to do test and insert atomically
            synchronized (waitMap) {
                // see if we have a waiter
                 waiter = removeWaiter(identifier);

                if (waiter != null) {
                    return waiter.signalThrow();
                } else {
                    // insert a pre-signalled waiter
                    waiter = new Waiter(identifier, true, false);
                    waitMap.put(identifier, waiter);
                }
            }

            // ok, so we need to wait until a wait has happened

            synchronized (waiter) {
                while (!waiter.waiting()) {
                    try {
                        waiter.wait();
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            }
            // remove the association between the waiter and the wait map
            synchronized (waitMap) {
                removeWaiter(identifier);
            }
            return true;
        }
    }

    /**
     * delay execution of the current thread for a specified number of milliseconds
     * @param millisecs how many milliseconds to delay for
     */

    public void delay(long millisecs)
    {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            // ignore this
        }
    }

    // rendezvous support
    /**
     * call createRendezvous(Object, int, boolean) supplying false for the last parameter
     * @param identifier an identifier for the rendezvous
     * @param expected the number of threads expected to meet at the rendezvous
     * @return true if the rendezvous is created or false if a rendezvous identified by identifier already exists
     */
    public boolean createRendezvous(Object identifier, int expected)
    {
        return createRendezvous(identifier, expected, false);
    }

    /**
     * create a rendezvous for a given number of threads to join
     * @param identifier an identifier for the rendezvious in subsequent rendezvous operations
     * @param expected
     * @param restartable
     * @return
     */
    public boolean createRendezvous(Object identifier, int expected, boolean restartable)
    {
        // need to do this atomically
        synchronized (rendezvousMap) {
            Rendezvous rendezvous = rendezvousMap.get(identifier);
            if (rendezvous !=  null) {
                return false;
            }
            rendezvous = new Rendezvous(expected, restartable);
            rendezvousMap.put(identifier, rendezvous);
        }
        
        return true;
    }

    /**
     * test whether a rendezvous with a specific expected count is associated with identifier
     * @param identifier the identifier for the rendezvous
     * @param expected the number of threads expected to meet at the rendezvous
     * @return true if the endezvous exists and is active otherwise false
     */
    public boolean isRendezvous(Object identifier, int expected)
    {
        int arrived = getRendezvous(identifier, expected);

        return (arrived >= 0 && arrived < expected);
    }

    /**
     * test whether a rendezvous with a specific expected count is associated with identifier
     * @param identifier the identifier for the rendezvous
     * @param expected the number of threads expected to meet at the rendezvous
     * @return the numer of threads currently arrived at the rendezvous
     */
    public int getRendezvous(Object identifier, int expected)
    {
        Rendezvous rendezvous = rendezvousMap.get(identifier);
        if (rendezvous == null || rendezvous.getExpected() != expected) {
            return -1;
        }
        synchronized (rendezvous) {
            return rendezvous.getArrived();
        }
    }

    /**
     * meet other threads at a given rendezvous returning only when the expected number have arrived
     * @param identifier the identifier for the rendezvous
     * @return an ordinal which sorts all parties to the rendezvous in order of arrival from 0 to
     * (expected-1) or -1 if the rendezvous does not exist
     */
    public int rendezvous(Object identifier)
    {
        Rendezvous rendezvous = rendezvousMap.get(identifier);

        if (rendezvous !=  null) {
            synchronized(rendezvous) {
                int result = rendezvous.rendezvous();
                // make sure the rendezvous is removed from the map if required
                // n.b. this implementation makes sure the remove happens before any thread
                // successfully passes the rendezvous call
                if (rendezvous.needsRemove()) {
                    rendezvousMap.remove(identifier);
                    rendezvous.setRemoved();
                }

                return result;
            }
        }

        return -1;
    }

    /*
     * delete a rendezvous. All threads waiting inside a call to rendezvous return result -1;
     * @param identifier
     * @param expected
     * @return true if the rendezvous was active and deleted and false if it had already been deleted
    */
    public boolean deleteRendezvous(Object identifier, int expected)
    {
        Rendezvous rendezvous = rendezvousMap.get(identifier);
        if (rendezvous == null || rendezvous.getExpected() != expected) {
            return false;
        }
        synchronized (rendezvous) {
            if (rendezvous.delete()) {
                if (rendezvous.needsRemove()) {
                    rendezvousMap.remove(identifier);
                }
                return true;
            }
        }
        // hmm, completed before we got there
        return false;
    }

    public boolean createJoin(Object key, int max)
    {
        if (max <= 0) {
            return false;
        }

        synchronized(joinerMap) {
            if (joinerMap.get(key) != null) {
                return false;
            }
            joinerMap.put(key, new Joiner(max));
        }

        return true;
    }

    public boolean isJoin(Object key, int max)
    {
        synchronized(joinerMap) {
            Joiner joiner = joinerMap.get(key);

            if (joiner == null || joiner.getMax() != max) {
                return false;
            }
        }

        return true;
    }

    public boolean joinEnlist(Object key)
    {
        Joiner joiner;
        synchronized (joinerMap)
        {
            joiner = joinerMap.get(key);
        }

        if (joiner == null) {
            return false;
        }

        Thread current = Thread.currentThread();

        switch (joiner.addChild(current)) {
            case DUPLICATE:
            case EXCESS:
            {
                // failed to add  child
                return false;
            }
            case ADDED:
            case FILLED:
            {
                // added child but parent was not waiting so leave joiner in the map for parent to find
                return true;
            }
            case DONE:
            default:
            {
                // added child and parent was waiting so remove joiner from map now
                synchronized (joinerMap) {
                    joinerMap.remove(joiner);
                }
                return true;
            }
        }
    }

    public boolean joinWait(Object key, int count)
    {
        Joiner joiner;
        synchronized (joinerMap)
        {
            joiner = joinerMap.get(key);
        }

        if (joiner == null || joiner.getMax() != count) {
            return false;
        }

        Thread current = Thread.currentThread();

        if (joiner.joinChildren(current)) {
            // successfully joined all child threads so remove joiner form map
            synchronized (joinerMap) {
                joinerMap.remove(joiner);
            }
            return true;
        } else {
            // hmm, another thread must have done the join so leave it do the remove
            return true;
        }
    }

    private static HashMap<Object, Joiner> joinerMap = new HashMap<Object, Joiner>();

    // counter support
    /**
     * create a counter identified by the given object with count 0 as its initial count
     * @param o an identifier used to refer to the counter in future
     * @return true if a new counter was created and false if one already existed under the given identifier
     */
    public boolean createCounter(Object o)
    {
        return createCounter(o, 0);
    }

    /**
     * create a counter identified by the given object with the supplied value as its iniital count
     * @param o an identifier used to refer to the counter in future
     * @param value the initial value for the counter
     * @return true if a new counter was created and false if one already existed under the given identifier
     */
    public boolean createCounter(Object o, int value)
    {
        synchronized (counterMap) {
            Counter counter = counterMap.get(o);
            if  (counter != null) {
                return false;
            } else {
                counterMap.put(o, new Counter(value));
                return true;
            }
        }
    }

    /**
     * delete a counter identified by the given object with count 0 as its initial count
     * @param o the identifier for the coounter
     * @return true if a counter was deleted and false if no counter existed under the given identifier
     */
    public boolean deleteCounter(Object o)
    {
        synchronized (counterMap) {
            Counter counter = counterMap.get(o);
            if  (counter != null) {
                counterMap.remove(o);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * read the value of the counter associated with given identifier, creating a new one with count zero
     * if none exists
     * @param o the identifier for the coounter
     * @return the value of the counter
     */
    public int readCounter(Object o)
    {
        synchronized (counterMap) {
            Counter counter = counterMap.get(o);
            if (counter == null) {
                counter = new Counter();
                counterMap.put(o, counter);
            }
            return counter.count();
        }
    }

    /**
     * increment the value of the counter associated with given identifier, creating a new one with count zero
     * if none exists
     * @param o the identifier for the coounter
     * @return the value of the counter after the increment
     */
    public int incrementCounter(Object o)
    {
        synchronized (counterMap) {
            Counter counter = counterMap.get(o);
            if (counter == null) {
                counter = new Counter();
                counterMap.put(o, counter);
            }
            return counter.increment();
        }
    }

    /**
     * decrement the value of the counter associated with given identifier, creating a new one with count zero
     * if none exists
     * @param o the identifier for the coounter
     * @return the value of the counter after the decrement
     */
    public int decrementCounter(Object o)
    {
        synchronized (counterMap) {
            Counter counter = counterMap.get(o);
            if (counter == null) {
                counter = new Counter();
                counterMap.put(o, counter);
            }
            return counter.decrement();
        }
    }

    /**
     * cause the current thread to throw a runtime exception which will normally cause it to exit.
     * The exception may not kill the thread if the trigger method or calling code contains a
     * catch-all handler so care must be employed to ensure that a call to this builtin has the
     * desired effect.
     */
    public void killThread()
    {
        throw new ExecuteException("rule " + rule.getName() + " : killing thread " + Thread.currentThread().getName());
    }

    /**
     * cause the current JVM to halt immediately, simulating a crash as near as possible. exit code -1
     * is returned
     */

    public void killJVM()
    {
        killJVM(-1);
    }

    /**
     * cause the current JVM to halt immediately, simulating a crash as near as possible. exit code -1
     * is returned
     */

    public void killJVM(int exitCode)
    {
        java.lang.Runtime.getRuntime().halt(-1);
    }

    /**
     * return a unique name for the trigger point associated with this rule. n.b. a single rule may
     * give rise to more than one trigger point if the rule applies to several methods with the same
     * name or to several classes with the same (package unqualified) name, or even to several
     * versions of the same compiled class loaded into distinct class loaders.
     *
     * @return a unique name for the trigger point from which this rule was invoked
     */
    public String toString()
    {
        return rule.getName();
    }

    /**
     * lookup the waiter object used to target wait and signal requests associated with a
     * specific identifying object
     * @param object the identifer for the waiter
     * @param createIfAbsent true if the waiter should be (atomically) inserted if it is not present
     * @return the waiter if it was found or inserted or null if it was not found and createIfAbsent was false
     */
    private Waiter getWaiter(Object object, boolean createIfAbsent)
    {
        Waiter waiter;

        synchronized(waitMap) {
            waiter = waitMap.get(object);
            if (waiter == null && createIfAbsent) {
                waiter = new Waiter(object);
                waitMap.put(object, waiter);
            }
        }

        return waiter;
    }

    /**
     * remove the waiter object used to target wait and signal requests associated with a
     * specific identifying object
     * @param object the identifer for the waiter
     * @return the waiter if it was found or inserted or null if it was not found and createIfAbsent was false
     */
    private Waiter removeWaiter(Object object)
    {
        return waitMap.remove(object);
    }

    private static int nextFileIndex = 0;

    private static synchronized int nextFileIndex()
    {
        return nextFileIndex++;
    }

    private String nextFileName()
    {
        StringWriter writer = new StringWriter();
        String digits = Integer.toString(nextFileIndex());
        int numDigits = digits.length();
        int idx;

        writer.write("trace");

        // this pads up to 9 digits but we may get more if we open enough files!
        for (idx = 9; idx > numDigits; idx--) {
            writer.write('0');
        }

        writer.write(digits);

        return writer.toString();
    }
    /**
     * a hash map used to identify trace streams from their identifying objects
     */
    private static HashMap<Object, PrintStream> traceMap = new HashMap<Object, PrintStream>();

    /**
     * a set used to identify settings for boolean flags associated with arbitrary objects. if
     * an object is in the set then the flag associated with the object is set (true) otherwise
     * it is clear (false).
     */
    private static Set<Object> flagSet = new HashSet<Object>();

    /**
     * a hash map used to identify countdowns from their identifying objects
     */
    private static HashMap<Object, CountDown> countDownMap = new HashMap<Object, CountDown>();

    /**
     * a hash map used to identify counters from their identifying objects
     */
    private static HashMap<Object, Counter> counterMap = new HashMap<Object, Counter>();

    /**
     * a hash map used to identify waiters from their identifying objects
     */
    private static HashMap<Object, Waiter> waitMap = new HashMap<Object, Waiter>();

    /**
     * a hash map used to identify rendezvous from their identifying objects
     */
    private static HashMap<Object, Rendezvous> rendezvousMap = new HashMap<Object, Rendezvous>();

    // initialise the trace map so it contains the system  output and error keyed under "out" and "err"

    static {
        traceMap.put("out", System.out);
        traceMap.put("err", System.err);
    }
}
