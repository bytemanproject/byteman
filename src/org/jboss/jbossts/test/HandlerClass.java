package org.jboss.jbossts.test;

import org.jboss.jbossts.orchestration.annotation.EventHandler;
import org.jboss.jbossts.orchestration.annotation.EventHandlerClass;

/**
 * sample class to test event handling
 */
@EventHandlerClass
public class HandlerClass {
    @EventHandler(
            targetClass="com.arjuna.wst11.messaging.engines.CoordinatorEngine",
            targetMethod="commit",
            targetLine = 77,
            event = "engine:CoordinatorEngine = $1, recovered:boolean = engine.isRecovered(), identifier:String = engine.getInstanceidentifier()",
            condition = "recovered AND decrementCounter(identifier)"//,
            //action = ""
    ) public static void handleCommit1()
    {
        // decrements the counter identified by a recovered engine's identifier each time recovery is attempted
        // for that engine -- if the counter decrements to zero it will be deactivated
    }

    @EventHandler(
            targetClass="com.arjuna.wst11.messaging.engines.CoordinatorEngine",
            targetMethod="commit",
            targetLine = 77,
            event = "engine:CoordinatorEngine = $1, recovered:boolean = engine.isRecovered()",
            condition = "NOT recovered",
            action = "killJVM()"
    ) public static void handleCommit2()
    {
        // kills the JVM when a commit is attempted on a non-recovered engine
    }

    @EventHandler(
            targetClass="com.arjuna.wst11.messaging.engines.CoordinatorEngine",
            targetMethod="<init>(String, boolean, W3CEndpointReference)",
            targetLine = -1,
            event = "engine:CoordinatorEngine = $1, recovered:boolean = engine.isRecovered(), identifier:String = engine.getInstanceIdentifier()",
            condition = "recovered",
            action = "addCounter(identifier, 2)"
    ) public static void handleNewEngine()
    {
        // activates a counter identified by a recovered engine's identifier when the engine is recreated
        // from the logged data
    }

    @EventHandler(
            targetClass="com.arjuna.wst11.messaging.engines.CoordinatorProcessorImpl",
            targetMethod="committed(Notification, AddressingProperties, ArjunaContext)",
            targetLine = -1,
            event = "processor:CoordinatorProcessorImpl = $1, identifier:String = $3.getInstanceIdentifier(), engine:CoordinatorEngine = processor.getCoordinator(identifier), recovered:boolean = engine.isRecovered()",
            condition = "recovered AND getCounter(identifier)",
            action = "killThread()"
    ) public static void handleCommitted()
    {
        // kills the current thread when a committed message is received for an engine whose identifier identifies
        // an active counter
    }
}
