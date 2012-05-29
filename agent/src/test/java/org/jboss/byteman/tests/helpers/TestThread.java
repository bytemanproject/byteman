package org.jboss.byteman.tests.helpers;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.jboss.byteman.tests.helpertests.TestStackTrace;


public class TestThread extends Thread {
	private static CyclicBarrier barrier = new CyclicBarrier(2);
	
	private final boolean trigger;
	private final TestStackTrace inner;

	public TestThread(TestStackTrace inner, boolean trigger) {
		super("TestThread-" + trigger);
		this.trigger = trigger;
		this.inner = inner;
	}
	
	public void run() {
		sync();
		if (trigger)
			fire();
		sync();
	}

	private void sync() {
		try {
			barrier.await();
		} catch (InterruptedException ex) { 
			return; 
		} catch (BrokenBarrierException ex) { 
			return; 
		}
	}

	private void fire() {
	} 
}