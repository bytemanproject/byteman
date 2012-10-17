/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-10, Red Hat and individual contributors
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
import org.jboss.byteman.synchronization.Timer;
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
    public boolean traceOpen(Object identifier)
    {
        return traceOpen(identifier, null);
    }

    /**
     * open a trace output stream identified by identifier to a file located in the current working
     * directory using the given file name or a generated name if the supplied name is null
     * @param identifier an identifier used subsequently to identify the trace output stream
     * @return true if new file and stream was created, false if a stream identified by identifier
     * already existed or if a file of the same name already exists or the identifer is null, "out"
     * or "err"
     */
    public boolean traceOpen(Object identifier, String fileName)
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
    public boolean traceClose(Object identifier)
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

    /**
     * version for backwards compatibility -- docs and original code were mismatched
     */
    public boolean openTrace(Object identifier)
    {
        return traceOpen(identifier);
    }
    /**
     * version for backwards compatibility -- docs and original code were mismatched
     */
    public boolean openTrace(Object identifier, String fileName)
    {
        return traceOpen(identifier, fileName);
    }
    /**
     * version for backwards compatibility -- docs and original code were mismatched
     */
    public boolean closeTrace(Object identifier)
    {
        return traceClose(identifier);
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
                    joinerMap.remove(key);
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
                joinerMap.remove(key);
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
     * @param o the identifier for the counter
     * @return the value of the counter
     */
    public int readCounter(Object o)
    {
        return readCounter(o, false);
    }

    /**
     * read and optionally reset to zero the value of the counter associated with given identifier, creating
     * a new one with count zero if none exists
     * @param o the identifier for the counter
     * @param zero if true then zero the counter
     * @return the value of the counter
     */
    public int readCounter(Object o, boolean zero)
    {
        synchronized (counterMap) {
            Counter counter = counterMap.get(o);
            if (counter == null) {
                counter = new Counter();
                counterMap.put(o, counter);
            }
            return counter.count(zero);
        }
    }

    /**
     * increment the value of the counter associated with given identifier, creating a new one with count zero
     * if none exists
     * @param o the identifier for the counter
     * @return the value of the counter after the increment
     */
    public int incrementCounter(Object o)
    {
        return incrementCounter(o, 1);
    }

    /**
     * decrement the value of the counter associated with given identifier, creating a new one with count zero
     * if none exists
     * @param o the identifier for the counter
     * @return the value of the counter after the decrement
     */
    public int decrementCounter(Object o)
    {
        return incrementCounter(o, -1);
    }

    /**
     * increment the value of the counter associated with given identifier by the given amount, creating a new one
     * with count zero if none exists
     * @param o the identifier for the counter
     * @param amount the amount to add to the counter
     * @return the value of the counter after the increment
     */
    public int incrementCounter(Object o, int amount)
    {
        synchronized (counterMap) {
            Counter counter = counterMap.get(o);
            if (counter == null) {
                counter = new Counter();
                counterMap.put(o, counter);
            }
            return counter.increment(amount);
        }
    }

    // timer support
    /**
     * create a timer identified by the given object
     * @param o an identifier used to refer to the timer in future
     * @return true if a new timer was created and false if one already existed under the given identifier
     */
    public boolean createTimer(Object o)
    {
        synchronized (timerMap) {
            Timer timer = timerMap.get(o);
            if  (timer != null) {
                return false;
            } else {
                timerMap.put(o, new Timer());
                return true;
            }
        }
    }

    /**
     * delete a timer identified by the given object
     * @param o the identifier for the timer
     * @return true if a timer was deleted and false if no timer existed under the given identifier
     */
    public boolean deleteTimer(Object o)
    {
        synchronized (timerMap) {
            Timer timer = timerMap.get(o);
            if  (timer != null) {
                timerMap.remove(o);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * get the elapsed time from the start (or last reset) of timer associated with given identifier,
     * creating a new one if none exists
     * @param o the identifier for the timer
     * @return the elapsed time since the start (or reset) of the timer
     */
    public long getElapsedTimeFromTimer(Object o)
    {
        synchronized (timerMap) {
            Timer timer = timerMap.get(o);
            if (timer == null) {
                timer = new Timer();
                timerMap.put(o, timer);
            }
            return timer.getElapsedTime();
        }
    }

    /**
     * reset the timer associated with given identifier, creating a new one
     * if none exists
     * @param o the identifier for the timer
     * @return the current elapsed value of the timer before the reset
     */
    public long resetTimer(Object o)
    {
        synchronized (timerMap) {
            Timer timer = timerMap.get(o);
            if (timer == null) {
                timer = new Timer();
                timerMap.put(o, timer);
            }
            return timer.reset();
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

    // call stack management support
    //
    // matching caller frames against exact name

    /**
     * test whether the name of the method which called the the trigger method matches the supplied name
     * by calling callerEquals(name, false)
     * @return true if the name of the method which called the the trigger method matches the supplied name
     * otherwise false
     */
    public boolean callerEquals(String name)
    {
        return callerEquals(name, false);
    }

    /**
     * test whether the name of any of the selected methods in the stack which called the trigger method
     * matches the supplied name by calling callerEquals(name, 1, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied name
     * otherwise false
     */
    public boolean callerEquals(String name, int frameCount)
    {
        return callerEquals(name, 1, frameCount);
    }

    /**
     * test whether the name of any of the selected methods in the stack which called the trigger method
     * matches the supplied name by calling callerEquals(name, false, startFrame, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied name
     * otherwise false
     */
    public boolean callerEquals(String name, int startFrame, int frameCount)
    {
        return callerEquals(name, false, startFrame, frameCount);
    }

    /**
     * test whether the name of method which called the the trigger method matches the supplied name
     * by calling callerEquals(name, includeClass, false)
     * @return true if the name of the method which called the the trigger method matches the supplied name
     * otherwise false
     */
    public boolean callerEquals(String name, boolean includeClass)
    {
        return callerEquals(name, includeClass, false);
    }

    /**
     * test whether the name of method which called the the trigger method matches the supplied name
     * by calling callerEquals(name, includeClass, false, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied name
     * otherwise false
     */
    public boolean callerEquals(String name, boolean includeClass, int frameCount)
    {
        return callerEquals(name, includeClass, false, frameCount);
    }

    /**
     * test whether the name of method which called the the trigger method matches the supplied name
     * by calling callerEquals(name, includeClass, false, startFrame, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied name
     * otherwise false
     */
    public boolean callerEquals(String name, boolean includeClass, int startFrame, int frameCount)
    {
        return callerEquals(name, includeClass, false, startFrame, frameCount);
    }

    /**
     * test whether the name of method which called the the trigger method matches the supplied name
     * by calling callerEquals(name, includeClass, includePackage, 1)
     * @return true if the name of the method which called the the trigger method matches the supplied name
     * otherwise false
     */
    public boolean callerEquals(String name, boolean includeClass, boolean includePackage)
    {
        return callerEquals(name, includeClass, includePackage, 1);
    }

    /**
     * test whether the name of any of the selected methods in the stack which called the trigger method
     * matches the supplied name by calling
     * callerCheck(name, false, includeClass, includePackage, 1, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied name
     * otherwise false
     */
    public boolean callerEquals(String name, boolean includeClass, boolean includePackage, int frameCount)
    {
        return callerCheck(name, false, includeClass, includePackage, 1, frameCount);
    }

    /**
     * test whether the name of any of the selected methods in the stack which called the trigger method
     * matches the supplied name by calling
     * callerCheck(name, false, includeClass, false, startFrame, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied name
     * otherwise false
     */
    public boolean callerEquals(String name, boolean includeClass, boolean includePackage, int startFrame, int frameCount)
    {
        return callerCheck(name, false, includeClass, includePackage, startFrame, frameCount);
    }


    // matching caller frames against reg exp

    /**
     * test whether the name of the method which called the the trigger method matches the supplied regular
     * by calling callerMatches(regExp, false)
     * @return true if the name of the method which called the the trigger method matches the supplied
     * regular expression otherwise false
     */
    public boolean callerMatches(String regExp)
    {
        return callerMatches(regExp, false);
    }

    /**
     * test whether the name of any of the selected methods in the stack which called the trigger method
     * matches the supplied regular expression by calling callerMatches(regExp, 1, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied
     * regular expression otherwise false
     */
    public boolean callerMatches(String regExp, int frameCount)
    {
        return callerMatches(regExp, 1, frameCount);
    }

    /**
     * test whether the name of any of the selected methods in the stack which called the trigger method
     * matches the supplied regular expression by calling callerMatches(regExp, false, startFrame, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied
     * regular expression otherwise false
     */
    public boolean callerMatches(String regExp, int startFrame, int frameCount)
    {
        return callerMatches(regExp, false, startFrame, frameCount);
    }

    /**
     * test whether the name of method which called the the trigger method matches the supplied regular
     * expression by calling callerMatches(regExp, includeClass, false)
     * @return true if the name of the method which called the the trigger method matches the supplied regular
     * expression otherwise false
     */
    public boolean callerMatches(String regExp, boolean includeClass)
    {
        return callerMatches(regExp, includeClass, false);
    }

    /**
     * test whether the name of method which called the the trigger method matches the supplied regular
     * expression by calling callerMatches(regExp, includeClass, false, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied regular
     * expression otherwise false
     */
    public boolean callerMatches(String regExp, boolean includeClass, int frameCount)
    {
        return callerMatches(regExp, includeClass, false, frameCount);
    }

    /**
     * test whether the name of method which called the the trigger method matches the supplied regular
     * expression by calling callerMatches(regExp, includeClass, false, startFrame, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied regular
     * expression otherwise false
     */
    public boolean callerMatches(String regExp, boolean includeClass, int startFrame, int frameCount)
    {
        return callerMatches(regExp, includeClass, false, startFrame, frameCount);
    }

    /**
     * test whether the name of method which called the the trigger method matches the supplied regular
     * expression by calling callerMatches(regExp, includeClass, includePackage, 1)
     * @return true if the name of the method which called the the trigger method matches the supplied regular
     * expression otherwise false
     */
    public boolean callerMatches(String regExp, boolean includeClass, boolean includePackage)
    {
        return callerMatches(regExp, includeClass, includePackage, 1);
    }

    /**
     * test whether the name of method which called the the trigger method matches the supplied regular
     * expression by calling callerMatches(regExp, includeClass, includePackage, 1, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied regular
     * expression otherwise false
     */
    public boolean callerMatches(String regExp, boolean includeClass, boolean includePackage, int frameCount)
    {
        return callerMatches(regExp, includeClass, includePackage, 1, frameCount);
    }

    /**
     * test whether the name of any of the selected methods in the stack which called the trigger method
     * matches the supplied regular expression by calling
     * callerCheck(regExp, true, includeClass, includePackage, 1, frameCount)
     * @return true if the name of the method which called the the trigger method matches the supplied
     * regular expression otherwise false
     */
    public boolean callerMatches(String regExp, boolean includeClass, boolean includePackage, int startFrame, int frameCount)
    {
        return callerCheck(regExp, true, includeClass, includePackage, startFrame, frameCount);
    }

    /**
     * test whether the name of any of the selected methods in the stack which called the trigger method
     * matches the supplied regular expression.
     * @param match an expression which will be matched against the name of the method which called
     * the trigger method
     * @param isRegExp true if match should be matched as a regular expression and false if it should be matched
     * using a String equals comparison.
     * @param includeClass true if the match should be against the class qualified method name
     * @param includePackage true if the match should be against the package and class qualified method name.
     * ignored if includeClass is  not also true.
     * @param startFrame identifies the first frame which frame which should be considered. 0 identifies
     * the trigger frame, 1 the frame for the caller of the trigger method etc. If startFrame is negative
     * false is returned.
     * @param frameCount counts the frames which should be checked starting from the first caller. if
     * this is non-positive or exceeds the actual number of callers above the start frame then all frames in
     * the stack are tested.
     * @return true if the name of one of the selected methods in the call stack starting from the trigger
     * method matches the supplied match value otherwise false
     */
    public boolean callerCheck(String match, boolean isRegExp,
                               boolean includeClass, boolean includePackage,
                               int startFrame, int frameCount)
    {
        StackTraceElement[] stack = getStack();
        int triggerIndex = triggerIndex(stack);
        if (startFrame < 0) {
            return false;
        }
        int lastIndex;
        if (frameCount <= 0) {
            lastIndex = Integer.MAX_VALUE;
        } else {
            lastIndex = startFrame + frameCount;
        }
        int matched = matchIndex(stack, match, isRegExp, includeClass, includePackage,
                triggerIndex + startFrame, triggerIndex + lastIndex);

        return (matched >= 0);
    }

    // call stack management support
    //
    // tracing caller frames

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
     * print a stack trace to the trace stream identified by key by calling traceStack(prefix, key, 0)
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
        String stackTrace = formatStack(prefix, maxFrames);
        trace(key, stackTrace);
    }

    // tracing frames of all stacks

    /**
     * print trace of all threads' stacks to System.out by calling traceAllStacks(null)
     */
    public void traceAllStacks()
    {
    	traceAllStacks(null);
    }

    /**
     * print trace of all threads' stacks to System.out by calling traceAllStacks(prefix, "out")
     */
    public void traceAllStacks(String prefix)
    {
    	traceAllStacks(prefix, "out");
    }

    /**
     * print trace of all threads' stacks to the trace stream identified by key by calling traceAllStacks(prefix, key, 0)
     */
    public void traceAllStacks(String prefix, Object key)
    {
    	traceAllStacks(prefix, key, 0);
    }

    /**
     * print trace of all threads' stacks to System.out by calling traceAllStacks(null, maxFrames)
     */
    public void traceAllStacks(int maxFrames)
    {
    	traceAllStacks(null, maxFrames);
    }

    /**
     * print trace of all threads' stacks to System.out by calling traceAllStacks(prefix, "out", maxFrames)
     */
    public void traceAllStacks(String prefix, int maxFrames)
    {
    	traceAllStacks(prefix, "out", maxFrames);
    }

    /**
     * print trace of all threads' stacks to the trace stream identified by key
     *
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace for thread " + Thread.currentThread().getName() + "\n" is used
     * @param key an object identifying the trace stream to which output should be generated
     * @param maxFrames the maximum number of frames to print or 0 if no limit should apply
     */
    public void traceAllStacks(String prefix, Object key, int maxFrames)
    {
    	trace(key, formatAllStacks(prefix, maxFrames));
    }
    
    
    // trace stack of a specific thread

    /**
     * print a stack trace of a specific thread to System.out by calling traceThreadStack(threadName, null)
     */
    public void traceThreadStack(String threadName)
    {
    	traceThreadStack(threadName, null);
    }

    /**
     * print a stack trace of a specific thread to System.out by calling traceThreadStack(threadName, prefix, "out")
     */
    public void traceThreadStack(String threadName, String prefix)
    {
    	traceThreadStack(threadName, prefix, "out");
    }

    /**
     * print a stack trace of a specific thread to the trace stream identified by key by calling traceThreadStack(threadName, prefix, key, 0)
     */
    public void traceThreadStack(String threadName, String prefix, Object key)
    {
    	traceThreadStack(threadName, prefix, key, 0);
    }

    /**
     * print a stack trace of a specific thread to System.out by calling traceThreadStack(threadName, null, maxFrames)
     */
    public void traceThreadStack(String threadName, int maxFrames)
    {
    	traceThreadStack(threadName, null, maxFrames);
    }

    /**
     * print a stack trace of a specific thread of a specific thread to System.out by calling traceThreadStack(threadName, prefix, "out", maxFrames)
     */
    public void traceThreadStack(String threadName, String prefix, int maxFrames)
    {
    	traceThreadStack(threadName, prefix, "out", maxFrames);
    }

    /**
     * print a stack trace to the trace stream identified by key
     *
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace for thread " + threadName + "\n" is used
     * @param key an object identifying the trace stream to which output should be generated
     * @param maxFrames the maximum number of frames to print or 0 if no limit should apply
     */
    public void traceThreadStack(String threadName, String prefix, Object key, int maxFrames)
    {
        String stackTrace = formatThreadStack(threadName, prefix, maxFrames);
        trace(key, stackTrace);
    }


    /**
     * print all stack frames which match pattern to System.out by calling traceStackMatching(pattern, null)
     */

    public void traceStackMatching(String regExp)
    {
        traceStackMatching(regExp, null);
    }

    /**
     * print all stack frames which match pattern to System.out preceded by prefix by calling
     * traceStackMatching(pattern, prefix, "out")
     */

    public void traceStackMatching(String regExp, String prefix)
    {
        traceStackMatching(regExp, prefix, "out");
    }

    /**
     * print all stack frames which match pattern to System.out preceded by prefix by calling
     * traceStackMatching(pattern, false, prefix, key)
     */

    public void traceStackMatching(String regExp, String prefix, Object key)
    {
        traceStackMatching(regExp, false, prefix, key);
    }

    /**
     * print all stack frames which match pattern to System.out by calling
     * traceStackMatching(pattern, includeClass, false)
     */

    public void traceStackMatching(String regExp, boolean includeClass)
    {
        traceStackMatching(regExp, includeClass, false);
    }

    /**
     * print all stack frames which match pattern to System.out preceded by prefix by calling
     * traceStackMatching(pattern, includeClass, false, prefix)
     */

    public void traceStackMatching(String regExp, boolean includeClass, String prefix)
    {
        traceStackMatching(regExp, includeClass, false, prefix);
    }

    /**
     * print all stack frames which match pattern to System.out preceded by prefix by calling
     * traceStackMatching(pattern, includeClass, false, prefix, key)
     */

    public void traceStackMatching(String regExp, boolean includeClass, String prefix, Object key)
    {
        traceStackMatching(regExp, includeClass, false, prefix, key);
    }

    /**
     * print all stack frames which match pattern to System.out by calling
     * traceStackMatching(pattern, includeClass, includePackage, null)
     */

    public void traceStackMatching(String regExp, boolean includeClass, boolean includePackage)
    {
        traceStackMatching(regExp, includeClass, includePackage, null);
    }

    /**
     * print all stack frames which match pattern to System.out preceded by prefix by calling
     * traceStackMatching(pattern, includeClass, , includePackage, prefix, "out")
     */

    public void traceStackMatching(String regExp, boolean includeClass, boolean includePackage, String prefix)
    {
        traceStackMatching(regExp, includeClass, includePackage, prefix, "out");
    }

    /**
     * print all stack frames which match pattern to the trace stream identified by key preceded by prefix.
     *
     * @param regExp a pattern which will be matched against the method name of the stack frame as a
     * regular expression by calling String.matches()
     * @param includeClass true if the match should be against the package and class qualified method name
     * @param includePackage true if the match should be against the package and class qualified method name.
     * ignored if includeClass is  not also true.
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace for thread " + Thread.currentThread().getName() + " matching " + pattern + "\n" is used
     * @param key an object identifying the trace stream to which output should be generated
     */

    public void traceStackMatching(String regExp, boolean includeClass, boolean includePackage, String prefix, Object key)
    {
        String stackTrace = formatStackMatching(regExp, includeClass, includePackage, prefix);
        trace(key, stackTrace);
    }

    // tracing stack range by exact match

    /**
     * print all stack frames between the frames which match start and end to System.out by calling
     * traceStackBetween(from, to, null)
     */

    public void traceStackBetween(String from, String to)
    {
        traceStackBetween(from, to, null);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out preceded by prefix
     * by calling traceStackBetween(from, to, prefix, "out")
     */

    public void traceStackBetween(String from, String to, String prefix)
    {
        traceStackBetween(from, to, prefix, "out");
    }

    /**
     * print all stack frames between the frames which match start and end preceded by prefix
     * by calling traceStackBetween(from, to, false, prefix, key)
     */

    public void traceStackBetween(String from, String to, String prefix, Object key)
    {
        traceStackBetween(from, to, false, prefix, key);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out by calling
     * traceStackBetween(from, to, includeClass, false)
     */

    public void traceStackBetween(String from, String to, boolean includeClass)
    {
        traceStackBetween(from, to, includeClass, false);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out by calling
     * traceStackBetween(from, to, includeClass, false, prefix)
     */

    public void traceStackBetween(String from, String to, boolean includeClass, String prefix)
    {
        traceStackBetween(from, to, includeClass, false, prefix);
    }

    /**
     * print all stack frames between the frames which match start and end preceded by prefix
     * by calling traceStackBetween(from, to, includeClass, false, prefix, key)
     */

    public void traceStackBetween(String from, String to, boolean includeClass, String prefix, Object key)
    {
        traceStackBetween(from, to, includeClass, false, prefix, key);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out by calling
     * traceStackBetween(from, to, includeClass, includePackage, null)
     */

    public void traceStackBetween(String from, String to, boolean includeClass, boolean includePackage)
    {
        traceStackBetween(from, to, includeClass, includePackage, null);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out preceded by prefix
     * by calling traceStackBetween(from, to, includeClass, includePackage, prefix, "out")
     */

    public void traceStackBetween(String from, String to, boolean includeClass, boolean includePackage, String prefix)
    {
        traceStackBetween(from, to, includeClass, includePackage, prefix, "out");
    }

    /**
     * print all stack frames between the frames which match start and end preceded by prefix
     * by calling traceStackBetween(from, to, false, includeClass, includePackage, prefix, key)
     */

    public void traceStackBetween(String from, String to, boolean includeClass, boolean includePackage, String prefix, Object key)
    {
        traceStackRange(from, to, false, includeClass, includePackage, prefix, key);
    }

    // tracing stack range by regular expression  match

    /**
     * print all stack frames between the frames which match start and end to System.out by calling
     * traceStackBetweenMatches(from, to, null)
     */

    public void traceStackBetweenMatches(String from, String to)
    {
        traceStackBetweenMatches(from, to, null);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out preceded by prefix
     * by calling traceStackBetweenMatches(from, to, prefix, "out")
     */

    public void traceStackBetweenMatches(String from, String to, String prefix)
    {
        traceStackBetweenMatches(from, to, prefix, "out");
    }

    /**
     * print all stack frames between the frames which match start and end to System.out preceded by prefix
     * by calling traceStackBetweenMatches(from, to, false, prefix, key)
     */

    public void traceStackBetweenMatches(String from, String to, String prefix, Object key)
    {
        traceStackBetweenMatches(from, to, false, prefix, key);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out by calling
     * traceStackBetweenMatches(from, to, includeClass, false)
     */

    public void traceStackBetweenMatches(String from, String to, boolean includeClass)
    {
        traceStackBetweenMatches(from, to, includeClass, false);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out by calling
     * traceStackBetweenMatches(from, to, includeClass, false, prefix)
     */

    public void traceStackBetweenMatches(String from, String to, boolean includeClass, String prefix)
    {
        traceStackBetweenMatches(from, to, includeClass, false, prefix);
    }

    /**
     * print all stack frames between the frames which match start and end preceded by prefix
     * by calling traceStackBetween(from, to, includeClass, false, prefix, key)
     */

    public void traceStackBetweenMatches(String from, String to, boolean includeClass, String prefix, Object key)
    {
        traceStackBetweenMatches(from, to, includeClass, false, prefix, key);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out by calling
     * traceStackBetweenMatches(from, to, includeClass, includePackage, null)
     */

    public void traceStackBetweenMatches(String from, String to, boolean includeClass, boolean includePackage)
    {
        traceStackBetweenMatches(from, to, includeClass, includePackage, null);
    }

    /**
     * print all stack frames between the frames which match start and end to System.out preceded by prefix
     * by calling traceStackBetweenMatches(from, to, true, includeClass, includePackage, prefix, "out");
     */

    public void traceStackBetweenMatches(String from, String to, boolean includeClass, boolean includePackage, String prefix)
    {
        traceStackBetweenMatches(from, to, includeClass, includePackage, prefix, "out");
    }

    /**
     * print all stack frames between the frames which match start and end preceded by prefix
     * by calling traceStackRange(from, to, true, includeClass, includePackage, prefix, key)
     */

    public void traceStackBetweenMatches(String from, String to, boolean includeClass, boolean includePackage, String prefix, Object key)
    {
        traceStackRange(from, to, true, includeClass, includePackage, prefix, key);
    }
    /**
     * print all stack frames between the frames which match start and end to the trace stream identified by key
     * preceded by prefix.
     *
     * @param from a pattern which identifies the first frame which should be printed. from will be matched against
     * the name of each successive stack frame from the trigger methdo frame until a matching frame is found. If null
     * is supplied then the trigger frame will be used as the first frame to print. If a non-null value is supplied
     * and no match is found then no frames will be printed.
     * @param to a pattern which identifies the last frame which should be printed. to will be matched against
     * the name of each successive stack frame following the first matched frame until a matching frame is found.
     * If null is supplied or no match is found then the bottom frame will be used as the last frame to print.
     * @param isRegExp true if from and true should be matched as regular expressions or false if they should be
     * matched using a String equals comparison.
     * @param includeClass true if the match should be against the package and class qualified method name
     * @param includePackage true if the match should be against the package and class qualified method name.
     * ignored if includeClass is  not also true.
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace (restricted) for " + Thread.currentThread().getName() + "\n" is used
     * @param key an object identifying the trace stream to which output should be generated
     */

    public void traceStackRange(String from, String to, boolean isRegExp, boolean includeClass, boolean includePackage, String prefix, Object key)
    {
        String stackTrace = formatStackRange(from, to, isRegExp, includeClass, includePackage, prefix);
        trace(key, stackTrace);
    }

    // call stack management support
    //
    // retrieving caller frames

    /**
     * return a stack trace by calling formatStack(null)
     */
    public String formatStack()
    {
        return formatStack(null);
    }

    /**
     * return a stack trace by calling formatStack(prefix, 0)
     */
    public String formatStack(String prefix)
    {
        return formatStack(prefix, 0);
    }

    /**
     * return a stack trace by calling formatStack(null, maxFrames)
     */
    public String formatStack(int maxFrames)
    {
        return formatStack(null, maxFrames);
    }

    /**
     * print a stack trace to the trace stream identified by key
     *
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace for thread " + Thread.currentThread().getName() + "\n" is used
     * @param maxFrames the maximum number of frames to print or 0 if no limit should apply
     */
    public String formatStack(String prefix, int maxFrames)
    {
        StringBuffer buffer = new StringBuffer();
        appendStack(buffer, prefix, maxFrames, Thread.currentThread(), getStack());
        return buffer.toString();
    }
    

    //
    // retrieving frames for all threads

    /**
     * return all stack traces by calling formatAllStacks(null)
     */
    public String formatAllStacks()
    {
        return formatAllStacks(null);
    }

    /**
     * return all stack traces by calling formatAllStacks(prefix, 0)
     */
    public String formatAllStacks(String prefix)
    {
        return formatAllStacks(prefix, 0);
    }

    /**
     * return all stack traces by calling formatAllStacks(null, maxFrames)
     */
    public String formatAllStacks(int maxFrames)
    {
        return formatAllStacks(null, maxFrames);
    }

    /**
     * return all stack traces
     *
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace for thread " + Thread.currentThread().getName() + "\n" is used
     * @param maxFrames the maximum number of frames to print or 0 if no limit should apply
     */

    public String formatAllStacks(String prefix, int maxFrames)
    {
    	StringBuffer buffer = new StringBuffer();

    	Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
    	for (Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
	    	appendStack(buffer, prefix, maxFrames, entry.getKey(), entry.getValue());
    		buffer.append('\n');
    	}

    	return buffer.toString();
    }

    //
    // retrieving frames for all threads

    /**
     * return stack traces of a specific thread by calling formatThreadStack(threadName, null)
     */
    public String formatThreadStack(String threadName)
    {
        return formatThreadStack(threadName, null);
    }

    /**
     * return all stack traces by calling formatThreadStack(threadName, prefix, 0)
     */
    public String formatThreadStack(String threadName, String prefix)
    {
        return formatThreadStack(threadName, prefix, 0);
    }

    /**
     * return all stack traces by calling formatThreadStack(threadName, null, maxFrames)
     */
    public String formatThreadStack(String threadName, int maxFrames)
    {
        return formatThreadStack(threadName, null, maxFrames);
    }

    /**
     * return all stack traces
     *
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace for thread " + threadName + "\n" is used
     * @param maxFrames the maximum number of frames to print or 0 if no limit should apply
     */

    public String formatThreadStack(String threadName, String prefix, int maxFrames)
    {
    	StringBuffer buffer = new StringBuffer();
    	Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();

    	boolean found = false;

    	for (Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
    		Thread thread = entry.getKey();
    		if (thread.getName().equals(threadName)) {
        		appendStack(buffer, prefix, maxFrames, thread, entry.getValue());
        		found = true;
    		}
    	}

    	if (!found) {
    		buffer.append("Thread ");
    		buffer.append(threadName);
    		buffer.append(" not found\n");
    	}

    	return buffer.toString();
    }

    private void appendStack(StringBuffer buffer, String prefix, int maxFrames, Thread thread, StackTraceElement[] stack) {
        int l = stack.length;
        int i;

    	if (thread == Thread.currentThread()) {
	    	// trim off the byteman trigger parts
            i = triggerIndex(stack);
            if (i < 0) {
                return;
            }
    	} else {
    		i = 0;
    	}

    	if (prefix != null) {
    		buffer.append(prefix);
    	} else {
    		buffer.append("Stack trace for thread ");
    		buffer.append(thread.getName());
    		buffer.append('\n');
    	}

        boolean dotdotdot = false;
        if (maxFrames > 0 && (i + maxFrames) < l) {
            l = i + maxFrames;
            dotdotdot = true;
        }

        for (; i < l; i++) {
            printlnFrame(buffer, stack[i]);
        }
        if (dotdotdot) {
            buffer.append("  . . .\n");
        }
    }

    // retrieving caller frames which match a regular expression

    /**
     * return a String tracing all stack frames which match pattern by calling formatStackMatching(pattern, null)
     */

    public String formatStackMatching(String regExp)
    {
        return formatStackMatching(regExp, null);
    }

    /**
     * return a String tracing all stack frames which match pattern by calling
     * formatStackMatching(pattern, false, prefix)
     */

    public String formatStackMatching(String regExp, String prefix)
    {
        return formatStackMatching(regExp, false, prefix);
    }

    /**
     * return a String tracing all stack frames which match pattern by calling
     * formatStackMatching(pattern, includeClass, false)
     */

    public String formatStackMatching(String regExp, boolean includeClass)
    {
        return formatStackMatching(regExp, includeClass, false);
    }

    /**
     * return a String tracing all stack frames which match pattern by calling
     * formatStackMatching(pattern, includeClass, false, prefix)
     */

    public String formatStackMatching(String regExp, boolean includeClass, String prefix)
    {
        return formatStackMatching(regExp, includeClass, false, prefix);
    }

    /**
     * return a String tracing all stack frames which match pattern by calling
     * formatStackMatching(pattern, includeClass, includePackage, null)
     */

    public String formatStackMatching(String regExp, boolean includeClass, boolean includePackage)
    {
        return formatStackMatching(regExp, includeClass, includePackage, null);
    }

    /**
     * return a String tracing all stack frames which match pattern.
     *
     * @param regExp a pattern which will be matched against the method name of the stack frame as a
     * regular expression by calling String.matches()
     * @param includeClass true if the match should be against the package and class qualified method name
     * @param includePackage true if the match should be against the package and class qualified method name.
     * ignored if includeClass is  not also true.
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace for thread " + Thread.currentThread().getName() + " matching " + pattern + "\n" is used
     */

    public String formatStackMatching(String regExp, boolean includeClass, boolean includePackage, String prefix)
    {
        StringBuffer buffer = new StringBuffer();
        StackTraceElement[] stack = getStack();
        int l = stack.length;
        int i = triggerIndex(stack);

        if (i < 0) {
            return "";
        }

        if (prefix != null) {
            buffer.append(prefix);
        } else {
            buffer.append("Stack trace for thread ");
            buffer.append(Thread.currentThread().getName());
            buffer.append(" matching ");
            buffer.append(regExp);
            buffer.append('\n');
        }
        for (; i < l; i++) {
            String fullName;
            if (includeClass) {
                String className = stack[i].getClassName();
                if (!includePackage) {
                    int dotIdx = className.lastIndexOf('.');
                    if (dotIdx >= 0) {
                     className  = className.substring(dotIdx + 1);
                    }
                }
                fullName = className + "." + stack[i].getMethodName();
            } else {
                fullName = stack[i].getMethodName();
            }

            if (fullName.matches(regExp)) {
                printlnFrame(buffer, stack[i]);
            }
        }

        return buffer.toString();
    }

    // retrieving caller frames between exact matched start and end frame

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackBetween(from, to, null)
     */

    public String formatStackBetween(String from, String to)
    {
        return formatStackBetween(from, to, null);
    }

    /**
     * return a String tracing the stack between the frames which match start and end
     * by calling formatStackBetween(from, to, false, false, false, prefix)
     */

    public String formatStackBetween(String from, String to, String prefix)
    {
        return formatStackBetween(from, to, false, false, prefix);
    }

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackBetween(from, to, includeClass, false)
     */

    public String formatStackBetween(String from, String to, boolean includeClass)
    {
        return formatStackBetween(from, to, includeClass, false);
    }

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackBetween(from, to, includeClass, false, prefix)
     */

    public String formatStackBetween(String from, String to, boolean includeClass, String prefix)
    {
        return formatStackBetween(from, to, includeClass, false, prefix);
    }

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackBetween(from, to, includeClass, includePackage, null)
     */

    public String formatStackBetween(String from, String to, boolean includeClass, boolean includePackage)
    {
        return formatStackBetween(from, to, includeClass, includePackage, null);
    }

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackRange(from, to, false, includeClass, includePackage, prefix)
     */

    public String formatStackBetween(String from, String to, boolean includeClass, boolean includePackage, String prefix)
    {
        return formatStackRange(from, to, false, includeClass, includePackage, prefix);
    }

    // retrieving caller frames between regular expression  matched start and end frame

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackBetweenMatches(from, to, null)
     */

    public String formatStackBetweenMatches(String from, String to)
    {
        return formatStackBetweenMatches(from, to, null);
    }

    /**
     * return a String tracing the stack between the frames which match start and end
     * by calling formatStackBetweenMatches(from, to, false, false, false, prefix)
     */

    public String formatStackBetweenMatches(String from, String to, String prefix)
    {
        return formatStackBetweenMatches(from, to, false, false, prefix);
    }

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackBetweenMatches(from, to, includeClass, false)
     */

    public String formatStackBetweenMatches(String from, String to, boolean includeClass)
    {
        return formatStackBetweenMatches(from, to, includeClass, false);
    }

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackBetweenMatches(from, to, includeClass, false, prefix)
     */

    public String formatStackBetweenMatches(String from, String to, boolean includeClass, String prefix)
    {
        return formatStackBetweenMatches(from, to, includeClass, false, prefix);
    }

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackBetweenMatches(from, to, includeClass, includePackage, null)
     */

    public String formatStackBetweenMatches(String from, String to, boolean includeClass, boolean includePackage)
    {
        return formatStackBetweenMatches(from, to, includeClass, includePackage, null);
    }

    /**
     * return a String tracing the stack between the frames which match start and end by calling
     * formatStackRange(from, to, true, includeClass, includePackage, prefix)
     */

    public String formatStackBetweenMatches(String from, String to, boolean includeClass, boolean includePackage, String prefix)
    {
        return formatStackRange(from, to, true, includeClass, includePackage, prefix);
    }

    /**
     * return a String tracing the stack between the frames which match start and end.
     *
     * @param from a pattern which identifies the first frame which should be printed. from will be matched against
     * the name of each successive stack frame from the trigger methdo frame until a matching frame is found. If null
     * is supplied then the trigger frame will be used as the first frame to print. If a non-null value is supplied
     * and no match is found then no frames will be printed.
     * @param to a pattern which identifies the last frame which should be printed. to will be matched against
     * the name of each successive stack frame following the first matched frame until a matching frame is found.
     * If null is supplied or no match is found then the bottom frame will be used as the last frame to print.
     * @param isRegExp true if from and true should be matched as regular expressions or false if they should be
     * matched using a String equals comparison.
     * @param includeClass true if the match should be against the package and class qualified method name
     * @param includePackage true if the match should be against the package and class qualified method name.
     * ignored if includeClass is  not also true.
     * @param prefix a String to be printed once before printing each line of stack trace. if supplied as null
     * then the prefix "Stack trace (restricted) for " + Thread.currentThread().getName() + "\n" is used
     */

    public String formatStackRange(String from, String to, boolean isRegExp,
                                   boolean includeClass, boolean includePackage, String prefix)
    {
        StringBuffer buffer = new StringBuffer();
        StackTraceElement[] stack = getStack();
        int l = stack.length;
        int i = triggerIndex(stack);
        if (i < 0) {
            return "";
        }

        int first;
        if (from != null) {
            first = matchIndex(stack, from, isRegExp, includeClass, includePackage, i, l);
            if (first < 0) {
                return "";
            }
        } else {
            first = i;
        }

        int last;
        if (to != null) {
            last = matchIndex(stack, to, isRegExp, includeClass, includePackage, first + 1, l);
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
        // n.b. the range includes the last matched frame
        
        for (i = first; i <= last; i++) {
            printlnFrame(buffer, stack[i]);
        }

        return buffer.toString();
    }

    // trigger management
    
    /**
     * enable or disable recursive triggering of rules by subsequent operations performed during binding,
     * testing or firing of the current rule in the current thread.
     * @param enabled true if triggering should be enabled or false if it should be disabled
     */
    public void setTriggering(boolean enabled)
    {
        if (enabled) {
            Rule.enableTriggers();
        } else {
            Rule.disableTriggers();
        }
    }

    // exposed functionality of the instrumentation instance

    /**
     * provide an estimate of an object's size
     *
     * return -1 if not running in a real agent
     */
    public long getObjectSize(Object o)
    {
        return rule.getObjectSize(o);
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

    // lifecycle management

    public static void activated()
    {
        if (Transformer.isDebug()) {
            System.out.println("Default helper activated");
        }
    }

    public static void deactivated()
    {
        if (Transformer.isDebug()) {
            System.out.println("Default helper deactivated");
        }
    }

    public static void installed(Rule rule)
    {
        if (Transformer.isDebug()) {
            System.out.println("Installed rule using default helper : " + rule.getName());
        }
    }

    public static void uninstalled(Rule rule)
    {
        if (Transformer.isDebug()) {
            System.out.println("Uninstalled rule using default helper : " + rule.getName());
        }
    }

    //  private and protected implementation

    private StackTraceElement[] stack = null;

    /**
     * access to the current stack frames
     *
     * @return
     */
    protected StackTraceElement[] getStack()
    {
        if (stack == null) {
            synchronized (this) {
                stack = Thread.currentThread().getStackTrace();
            }
        }
        return stack;
    }

    private static String RULE_CLASS_NAME = Rule.class.getCanonicalName();
    private static String RULE_EXECUTE_METHOD_NAME = "execute";

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
            new ExecuteException("Helper.formatStack : can only be called below Rule.execute()").printStackTrace();
            return -1;
        }

        return  i + 2;
    }

    /**
     * return the index of the first frame at or below index start which matches pattern
     * @param pattern a pattern to be matched against the concatenated frame method name using String.matches()
     * @param isRegExp true if the pattern should be matched as a regular expression or false if it should
     * be matched using a String equals comparison
     * @param includeClass true if the method name should be qualified with the package and class name
     * @param start the index of the first frame which should be tested for a match. this must be greater than
     * or equal to the trigger index.
     * @param limit the index of the first frame which should not be tested for a match. this must be less than
     * or equal to the stack length
     * @return the index of the matching frame between start and limit - 1 or -1 if it no match found
     */
    protected int matchIndex(StackTraceElement[] stack, String pattern, boolean isRegExp,
                             boolean includeClass, boolean includePackage, int start, int limit)
    {
        int l= stack.length;
        int i = start;
        if (limit > l) {
            limit = l;
        }
        // find the trigger method frame above the rule engine entry point
        // we should see two calls to rule.execute()
        for (; i < limit; i++) {
            String fullName;
            if (includeClass) {
                String className = stack[i].getClassName();
                if (!includePackage) {
                    int dotIdx = className.lastIndexOf('.');
                    if (dotIdx >= 0) {
                     className  = className.substring(dotIdx + 1);
                    }
                }
                fullName = className + "." + stack[i].getMethodName();
            } else {
                fullName = stack[i].getMethodName();
            }

            if (isRegExp) {
                if (fullName.matches(pattern)) {
                    return i;
                }
            } else {
                if (fullName.equals(pattern)) {
                    return i;
                }
            }
        }

        return -1;
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
        String fileName = frame.getFileName();
        if (fileName != null) {
            buffer.append("(");
            buffer.append(fileName);
            buffer.append(":");
            buffer.append(frame.getLineNumber());
            buffer.append(")");
        } else {
            buffer.append(" (Unknown Source)");
        }
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

    /**
     * a hash map used to identify timer from their identifying objects
     */
    private static HashMap<Object, Timer> timerMap = new HashMap<Object, Timer>();
    
    // initialise the trace map so it contains the system  output and error keyed under "out" and "err"

    static {
        traceMap.put("out", System.out);
        traceMap.put("err", System.err);
    }
}
