/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat and individual contributors
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
* @authors Gary Brown
*/
package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * Auxiliary class used by entry and exit location test classes
 */
public class TestExceptionExitAuxiliary implements TestInterface {
    protected Test test;

    public TestExceptionExitAuxiliary(Test test)
    {
        this.test = test;
        test.log("inside TestExceptionExitAuxiliary(Test)");
    }

    public void testMethod()
    {
        test.log("inside TestExceptionExitAuxiliary.testMethod");
        
        try {
        	testVoidMethod(true);
        } catch (Throwable t) {
        	test.log("caught: "+t.getMessage());
        }

        try {
        	testStringMethod(true);
        } catch (Throwable t) {
        	test.log("caught: "+t.getMessage());
        }

        try {
        	testMethodTryMultiCatch(true);
        } catch (Throwable t) {
        	test.log("caught: "+t.getMessage());
        }

        try {
        	testMethodNestedTryCatch(true);
        } catch (Throwable t) {
        	test.log("caught: "+t.getMessage());
        }

        test.log("exiting TestExceptionExitAuxiliary.testMethod");
    }
    
    public void testVoidMethod(boolean fail) {
        test.log("inside TestExceptionExitAuxiliary.testVoidMethod");
        if (fail) {
        	throw new RuntimeException("testVoidMethod exception");
        }
        test.log("exit TestExceptionExitAuxiliary.testVoidMethod");
    }

    public String testStringMethod(boolean fail) {
    	String ret="Hello";
        test.log("inside TestExceptionExitAuxiliary.testStringMethod");
        if (fail) {
        	throw new RuntimeException("testStringMethod exception");
        }
        test.log("exit TestExceptionExitAuxiliary.testStringMethod");
        return ret;
    }

    public void testMethodTryMultiCatch(boolean fail) throws ExcA {
        test.log("inside TestExceptionExitAuxiliary.testMethodTryMultiCatch");
        try {
	        if (fail) {
	        	throw new ExcA("testMethodTryMultiCatch exception");
	        }
        } catch (ExcB eb) {
        	test.log("should not catch ExcB");
        } catch (ExcC ec) {
        	test.log("should not catch ExcC");
        }
        test.log("exit TestExceptionExitAuxiliary.testMethodTryMultiCatch");
    }

    public void testMethodNestedTryCatch(boolean fail) throws ExcA {
        test.log("inside TestExceptionExitAuxiliary.testMethodNestedTryCatch");
        try {
	        if (fail) {
	        	throw new ExcC("testMethodNestedTryCatch exception");
	        }
        } catch (ExcC ec) {
        	try {        		
        		throw new ExcB(ec.getMessage());
        	} finally {
        		test.log("finally testMethodNestedTryCatch");
        	}
        }
        test.log("exit TestExceptionExitAuxiliary.testMethodNestedTryCatch");
    }

    public Test getTest()
    {
        return test;
    }
    
    public static class ExcA extends Exception {
		private static final long serialVersionUID = 1L;
		public ExcA(String mesg) {
    		super(mesg);
    	}
    }
    
    public static class ExcB extends ExcA {
		private static final long serialVersionUID = 1L;
		public ExcB(String mesg) {
    		super(mesg);
    	}
    }
    
    public static class ExcC extends ExcA {
		private static final long serialVersionUID = 1L;
		public ExcC(String mesg) {
    		super(mesg);
    	}
    }
}
