package org.jboss.byteman.agent;

import org.jboss.byteman.rule.helper.Helper;

import java.util.*;
import java.lang.reflect.Method;

class RunnableDemo implements Runnable {
    private Thread t;
    private String threadName;
   
    RunnableDemo( String name) {
        threadName = name;
        Helper.verbose("Creating " +  threadName );
    }
   
    boolean flag = true;
    public void run() {
        Helper.verbose("Running " +  threadName );
        while (flag) {            
        }
    }
   
    public void start () {
        Helper.verbose("Starting " +  threadName );
    
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
   }
}
