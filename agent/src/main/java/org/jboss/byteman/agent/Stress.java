package org.jboss.byteman.agent;

import org.jboss.byteman.rule.helper.Helper;

import java.util.*;
import java.nio.ByteBuffer;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

/*
    Stress is an interface used for inject stress on Java process, include Memory and CPU.
 */
interface Stress {
    // load the stress
    public void load();

    // quit stops the stress load
    public void quit();
}

class CPUStress implements Stress {
    private String name;
    private int cpuCount;
    private ArrayList<CPUStressThread> threads;

    CPUStress(String name, int cpuCount) {
        this.name = name;
        this.cpuCount = cpuCount;
        threads = new ArrayList<CPUStressThread>();
    }

    public void load() {
        for (int i = 0; i < cpuCount; i++) {
            CPUStressThread thread = new CPUStressThread(name + i);
            threads.add(thread);
            thread.start();
        }
    }

    public void quit() {
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).shutdown();
        }
    }
}

class MemoryStress implements Stress {
    private String name;
    private int size;
    private MemoryStressThread thread;

    MemoryStress(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public void load() {
        thread = new MemoryStressThread(name, size);
        thread.start();
    }

    public void quit() {
        thread.shutdown();
    }
}

interface StressRunnable extends Runnable {

    public void shutdown();
}

class CPUStressThread implements StressRunnable {
    private Thread t;
    private String threadName;
    private boolean flag;
   
    private ReentrantLock lock = new ReentrantLock();
    
    CPUStressThread( String name ) {
        threadName = name;
        flag = true;
        Helper.verbose("Creating " +  threadName );
    }
   
    public void run() {
        Helper.verbose("Running " +  threadName );
        
        while (true) {
            lock.lock();
            boolean exit = !flag; 
            lock.unlock();
            if (exit) {
                break;
            }
        }

        Helper.verbose("Exiting " +  threadName );
    }
   
    public void start () {
        Helper.verbose("Starting " +  threadName );
    
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
   }

    public void shutdown() {
        Helper.verbose("Shutdown " +  threadName );
        lock.lock();
        flag = false;
        lock.unlock();
    }
}

class MemoryStressThread implements StressRunnable {
    private Thread t;
    private String threadName;
    private int size;
    private boolean flag;

    private ReentrantLock lock = new ReentrantLock();

    MemoryStressThread(String name, int size) {
        threadName = name;
        this.size = size;
        flag = true;
        Helper.verbose("Creating " +  threadName );
    }

    public void run() {
        Helper.verbose("Running " +  threadName );
        ByteBuffer stableSizeData;
        ArrayList<String> increaseSizeData = new ArrayList<String>();
        boolean oom = false;
        
        if (size > 0) {
            stableSizeData = ByteBuffer.allocateDirect(size*1024*1024);
        }

        while (true) {
            lock.lock();
            boolean exit = !flag;
            lock.unlock();
            if (exit) {
                stableSizeData = null;
                increaseSizeData = null;

                System.gc(); 
                break;
            }
            if (size < 0 && !oom ) {
                try {
                    increaseSizeData.add("123456");
                } catch (OutOfMemoryError e) {
                    oom = true;
                    Helper.verbose("exception: " + e);
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    Helper.verbose("exception: " + e);
                }
            }
        }

        Helper.verbose("Exiting " +  threadName );
    }
   
    public void start () {
        Helper.verbose("Starting " +  threadName );
    
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
   }

    public void shutdown() {
        Helper.verbose("Shutdown " +  threadName );
        lock.lock();
        flag = false;
        lock.unlock();
    }
}