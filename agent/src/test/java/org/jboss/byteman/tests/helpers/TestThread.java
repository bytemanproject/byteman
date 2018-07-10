/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-2018 Red Hat and individual contributors
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
 */

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