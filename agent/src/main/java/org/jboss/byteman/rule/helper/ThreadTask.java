package org.jboss.byteman.rule.helper;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTask implements Runnable{
    public static boolean stop = false;
    private static ReentrantLock lock = new ReentrantLock();

    int interval;
    public ThreadTask() {
        
    }

    public static void setStop(boolean stop) {
        ThreadTask.lock.lock();
        ThreadTask.stop = stop;
        ThreadTask.lock.unlock();
    }

    public static boolean getStop() {
        ThreadTask.lock.lock();
        stop = ThreadTask.stop;
        ThreadTask.lock.unlock();

        return stop;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    public void run() {
        System.out.println("chaos thread: " + Thread.currentThread().getName());
        
        if (interval > 0) {
            for (int i = 0; i < interval; i++) {
                try {
                    Thread.sleep(1000);
                    if (this.getStop()) {
                        System.out.println("exit the chaos thread");
                        return;
                    }
                } catch(Exception e) {
                    System.out.println("get exception when execute new chaos thread:" + e);
                }         
            }
        }
    }
}