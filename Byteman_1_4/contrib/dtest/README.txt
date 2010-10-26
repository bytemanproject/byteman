#  JBoss, Home of Professional Open Source
#  Copyright 2010, Red Hat, Inc. and/or its affiliates,
#  and individual contributors as indicated by the @author tags.
#  See the copyright.txt in the distribution for a
#  full listing of individual contributors.
#  This copyrighted material is made available to anyone wishing to use,
#  modify, copy, or redistribute it subject to the terms and conditions
#  of the GNU Lesser General Public License, v. 2.1.
#  This program is distributed in the hope that it will be useful, but WITHOUT A
#  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
#  PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
#  You should have received a copy of the GNU Lesser General Public License,
#  v.2.1 along with this distribution; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#  MA  02110-1301, USA.
#
# (C) 2010,
#  @author JBoss, by Red Hat.


README.txt for Byteman contrib dtest, a distributed test helper.

@author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-05


When writing distributed systems, unit testing is necessary but not sufficient.
Despite the ability to Mock distributed components, run embedded containers,
simulate remote communication and so on, sooner or later you have to actually run
a test that involves more than one JVM. This is a pain. dtest is the analgesic.

In distributed tests, a significant amount of code must be dedicated to the basic plumbing
needed to gather information from multiple JVMs into one place and analyse it to determine
the test outcome. For example, we may need to know that system A received a message sent from
system B. This typically involves writing code to record the event in A and to allow querying
of that log by the test logic. dtest aims to reduce the amount of boilerplate code needed to
achieve this.

Assume that we have a test case that will start a JBossAS server, invoke an application within it
and verify that the application takes the expected action in response to that client request.
Specifically, we expect that MyBusinessClass.doSomeBusinessLogic will be invoked in the server.
Ordinarily we've have to either insert test code into that method to log its invocation,
use AOP to intercept and log the invocation, or test for some observable side effect
of the method's execution, such as a state change or db update. Using dtest we can instrument
MyBusinessClass to provide remote tracing capability, then state assertions against that trace:

  @Test
  public void myJUnitTestMethod()
  {
    Server remoteJBossServer = ServerManager.startServer("default");

    Instrumentor dtestInstrumentor = new Instrumentor(host, port);

    InstrumentedClass myRemoteBusinessLogic = dtestInstrumentor.instrumentClass(MyBusinessClass.class);

    makeClientInvocation();

    myRemoteBusinessLogic.assertMethodCalled("doSomeBusinessLogic");

    remoteJBossServer.stop();
  }

This allows for expressing the logic for distributed tests in much the same manner as unit tests.
It reduces the amount of boilerplate code needed for test distribution and makes tests easier to
both write and read.

How it works:
-------------

Remote JVMs must be started with the Byteman agent installed and configured to listen on a TCP/IP socket.
The dtest Instrumentor connects to this listener and dynamically injects Byteman Rules for each point of interest.
Upon triggering, these Rules pass information back to the Instrumentor via. an RMI connection. The log is
stored in the InstrumentedClass instance and can be queried by the test code to verify expected behaviour.


In addition to allowing tracing of remote execution, dtest can be used to modify that execution. For example,
we can test error handling in the remote system by injecting a fault (i.e. Exception). Let's assume we have a

  public DataObject parseMessage(Sting xml) throws StructureException { ... }

method in our business logic, which deserializes some business data from an xml representation. Callers of this
method are expected to do something reasonable if they get a StructureException, but how to test that exception
handling capability? We could construct a malformed xml String, but there is a quicker way:

  dtestInstrumentor.injectFault(MessageParser.class, "parseMesage", StructureException.class);

Now when the application calls parseMessage, instead of running the method body the Rule will cause a
StructureException to be thrown to the caller.

Many variations on this theme are feasible, such as altering method parameter or retun values,
crashing the JVM to test failure recovery, etc.

Status, as of 2010-05:
----------------------

For now dtest is largely a proof of concept and contains only the functionality needed for testing the
transaction bridge - for example usage see

http://anonsvn.jboss.org/repos/labs/labs/jbosstm/trunk/txbridge/tests/src/org/jboss/jbossts/txbridge/tests/

Over time more methods may be added to the framework as required. For example, the tracing currently
encompasses only method names but in future may allow for method parameters to be verified also, e.g.

  myRemoteBusinessLogic.assertMethodCalled("doSomeBusinessLogic(5)")

Likewise tracking of distinct remote object instances may be possible:

  InstrumentedInstance remoteInstance = myRemoveBusinesLogic.getInstance(id);
  remoteInstance.assertMethodCalled("doSomeBusinessLogic");

With bytecode manipulation techniques the client API may be made cleaner and typesafe, resulting in
a Mockito like syntax for distributed tests e.g:

  MyBusinessLogic mockLogic = myRemoveBusinesLogic.getInstance(id);
  assert( mockLogic.doSomeBusinessLogic().wasCalled() );

  mockLogic.doSomeBusinessLogic().thenReturn(someValue);

  mockMessageParser.parseMessage(xml).thenThrowException(StructureException.class);

Clearly the need for remote communication places some limitation on the capabilities that can be
achieved, particularly for instrumentation of code that uses non-serializable parameters ans such.

Nevertheless, dtest has the possibility to significantly ease the pain of writing distributed tests.