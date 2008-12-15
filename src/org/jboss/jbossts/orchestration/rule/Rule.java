/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jbossts.orchestration.rule;

import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.exception.ParseException;
import org.jboss.jbossts.orchestration.rule.exception.CompileException;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import org.jboss.jbossts.orchestration.rule.grammar.ECATokenLexer;
import org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser;
import org.jboss.jbossts.orchestration.rule.grammar.ParseNode;
import org.jboss.jbossts.orchestration.synchronization.CountDown;
import org.jboss.jbossts.orchestration.synchronization.Waiter;
import org.jboss.jbossts.orchestration.agent.Location;
import org.jboss.jbossts.orchestration.agent.LocationType;
import org.objectweb.asm.Opcodes;

import java.io.StringWriter;
import java.io.StringReader;
import java.util.*;

import java_cup.runtime.Symbol;

/**
 * A rule ties together an event, condition and action. It also maintains a TypeGroup
 * identifying type information derived from these components.
 */
public class Rule
{
    /**
     * the name of this rule supplied in the rule script
     */
    private String name;
    /**
     * the name of the target class for this rule supplied in the rule script
     */
    private String targetClass;
    /**
     * the name of the triggering method on the target class for this rule supplied in the
     * rule script
     */
    private String targetMethod;
    /**
     * the location at which the rule trigger point is attached
     */
    private Location targetLocation;
    /**
     * the parsed event derived from the script for this rule
     */
    private Event event;
    /**
     * the parsed condition derived from the script for this rule
     */
    private Condition condition;
    /**
     * the parsed condition derived from the script for this rule
     */
    private Action action;
    /**
     * the set of bindings derived from the event supplemented, post type checking, with bindings
     * derived from the trigger method. we may eventually also be able to install bindings for
     * method local variables.
     *
     * Note that use of the name bindings is slightly misleading since this instance identifies the
     * name and type of each of the available bound variables and, in the case of an event binding,
     * the expression to be evaluated in order to initialise the variable. It does not identify
     * any bound values for the variable. These are stored <em>per rule-firing</em> in a set
     * attached to the Helper instance used to implement the execute method for the rule.
     */
    private Bindings bindings;
    /**
     * the fully qualified name of the class to which this rule has been attached by the code
     * transformation package. note that this may not be the same as targetClass since the
     * latter may not specify a package.
     */
    private String triggerClass;
    /**
     * the name of the trigger method in which a trigger call for this rule has been inserted by
     * the code transformation package, not including the descriptor component. note that this
     * may not be the same as the targetMethod since the latter may include an argument list.
     */
    private String triggerMethod;
    /**
     * the descriptor of the trigger method in which a trigger call for this rule has been inserted
     * by the code transformation package. note that this will be in encoded format e.g. "(IZ)V"
     * rather than declaration format e.g. "void (int, boolean)"
     */
    private String triggerDescriptor;
    /**
     * the name sof all the exceptions declared by the trigger method in which a trigger call for
     * this rule has been inserted by the code transformation package.
     */
    private String[] triggerExceptions;
    /**
     * the access mode for the target method defined using flag bits defined in the asm Opcodes
     * class.
     */
    private int triggerAccess;
    /**
     * the set of types employed by the rule, inlcuding types referenced by abbreviated name (without
     * mentioning the package), array type sand/or their base types and standard builtin types.
     */
    private TypeGroup typeGroup;
    /**
     * flag set to true only after the rule has been type checked
     */
    private boolean checked;
    /**
     * flag set to true only after the rule has been type checked successfully
     */
    private boolean checkFailed;

    private Rule(String name, String targetClass, String targetMethod, Location targetLocation, String ruleSpec, ClassLoader loader)
            throws ParseException, TypeException, CompileException
    {
        ParseNode ruleTree;

        this.name = name;
        typeGroup = new TypeGroup(loader);
        bindings = new Bindings();
        if (ruleSpec != null) {
            // ensure line numbers start at 1
            String fullSpec = "\n" + ruleSpec;
            try {
                ECATokenLexer lexer = new ECATokenLexer(new StringReader(fullSpec));
                ECAGrammarParser parser = new ECAGrammarParser(lexer);
                Symbol parse = (debugParse ? parser.debug_parse() : parser.parse());
                ruleTree = (ParseNode) parse.value;
            } catch (Exception e) {
                throw new ParseException("org.jboss.jbossts.orchestration.rule.Rule : error parsing rule " + ruleSpec, e);
            }
            ParseNode eventTree = (ParseNode)ruleTree.getChild(0);
            ParseNode conditionTree = (ParseNode)ruleTree.getChild(1);
            ParseNode actionTree = (ParseNode)ruleTree.getChild(2);
            event = Event.create(this, eventTree);
            condition = Condition.create(this, conditionTree);
            action = Action.create(this, actionTree);
        }
        checked = false;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.targetLocation = (targetLocation != null ? targetLocation : Location.create(LocationType.ENTRY, ""));
        triggerClass = null;
        triggerMethod = null;
        triggerDescriptor = null;
        triggerAccess = 0;
    }

    public TypeGroup getTypeGroup()
    {
        return typeGroup;
    }

    public Bindings getBindings()
    {
        return bindings;
    }

    public String getName() {
        return name;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public Event getEvent()
    {
        return event;
    }

    public Condition getCondition() {
        return condition;
    }

    public Action getAction() {
        return action;
    }

    public static Rule create(String name, String targetClass, String targetMethod, Location targetLocation, String ruleSpec, ClassLoader loader)
            throws ParseException, TypeException, CompileException
    {
            return new Rule(name, targetClass, targetMethod, targetLocation, ruleSpec, loader);
    }

    public void setEvent(String eventSpec) throws ParseException, TypeException
    {
        if (event == null) {
            event = Event.create(this, eventSpec);
        }
    }

    public void setCondition(String conditionSpec) throws ParseException, TypeException
    {
        if (event != null & condition == null) {
            condition = Condition.create(this, conditionSpec);
        }
    }

    public void setAction(String actionSpec) throws ParseException, TypeException
    {
        if (event != null & condition != null && action == null) {
            action = Action.create(this, actionSpec);
        }
    }

    public void setTypeInfo(final String className, final int access, final String methodName, final String desc, String[] exceptions)
    {
        triggerClass = className;
        triggerAccess = access;
        triggerMethod = methodName;
        triggerDescriptor = desc;
        triggerExceptions = exceptions;
    }

    private synchronized boolean ensureTypeCheckedCompiled()
    {
        if (checkFailed) {
            return false;
        }

        if (!checked) {
            try {
                typeCheck();
                helperClass = BasicHelper.class;
            } catch (TypeException te) {
                System.out.println("Rule.typecheck : error typechecking rule " + getName());
                te.printStackTrace(System.out);
                checkFailed = true;
                return false;
            }
        }
        
        return true;
    }


    public void typeCheck()
            throws TypeException
    {
        if (triggerExceptions != null) {
            // ensure that the type group includes the exception types
            typeGroup.addExceptionTypes(triggerExceptions);
        }
        
        // try to resolve all types in the type group to classes

        typeGroup.resolveTypes();

        // use the descriptor to derive the method argument types and install bindings for them
        // in the bindings list

        installParameters((triggerAccess & Opcodes.ACC_STATIC) != 0, triggerClass, triggerDescriptor);
        event.typeCheck(Type.VOID);
        condition.typeCheck(Type.Z);
        action.typeCheck(Type.VOID);
        checked = true;
    }

    private void installParameters(boolean isStatic, String className, String descriptor)
            throws TypeException
    {
        List<Binding> parameterBindings = new ArrayList<Binding>();
        Type type;
        // add a binding for the rule so we can call builting static methods
        type = typeGroup.create("org.jboss.jbossts.orchestration.rule.Rule$Helper");
        Binding ruleBinding = new Binding(this, "$", type);
        parameterBindings.add(ruleBinding);

        if (!isStatic) {
            type = typeGroup.create(className);
            if (type.isUndefined()) {
                throw new TypeException("Rule.installParameters : Rule " + name + " unable to load class " + className);
            }
            Binding binding =  new Binding(this, "0", type);
            parameterBindings.add(binding);
        }
        List<String> parameterTypenames = Type.parseDescriptor(descriptor, true);
        int paramIdx = 1;
        int last = parameterTypenames.size();
        if (parameterTypenames != null) {
            for (String typeName : parameterTypenames) {
                String[] typeAndArrayBounds = typeName.split("\\[");
                Type baseType = typeGroup.create(typeAndArrayBounds[0]);
                Type paramType = baseType;
                Binding binding;
                if (baseType.isUndefined()) {
                    throw new TypeException("Rule.installParameters : Rule " + name + " unable to load class " + baseType);
                }
                for (int i = 1; i < typeAndArrayBounds.length ; i++) {
                    paramType = typeGroup.createArray(baseType);
                }
                if (paramIdx == last) {
                    // we also add a special binding to allow us to identify the return type
                    binding = new Binding(this, "$!", paramType);
                } else {
                    binding = new Binding(this, Integer.toString(paramIdx++), paramType);
                }
                parameterBindings.add(binding);
            }
        }

        bindings.addBindings(parameterBindings);
    }

    /**
     * forward an execute request a rule identified by its unique key
     * @param key a string key identifying the rule instance to be fired
     * @param recipient the recipient of the method from which execution of the rule was
     * triggered or null if it was a static method
     * @param args the arguments of the method from which execution of the rule was
     * triggered
     */
    public static void execute(String key, Object recipient, Object[] args) throws ExecuteException
    {
        Rule rule = ruleKeyMap.get(key);
        System.out.println("Rule.execute called for " + key);

        if (rule == null) {
            throw new ExecuteException("Rule.execute : unable to find rule with key " + key);
        }

        rule.execute(recipient, args);
    }

    /**
     * forward an execute request to a helper instance associated with the rule
     * @param recipient the recipient of the method from which execution of this rule was
     * triggered or null if it was a static method
     * @param args the arguments of the method from which execution of this rule was
     * triggered
     */

    private void execute(Object recipient, Object[] args) throws ExecuteException
    {
        // type check and compile the rule now if it has not already been done

        if (ensureTypeCheckedCompiled()) {

            // create a helper and get it to execute the rule
            // eventually we will create a subclass of helper for each rule and compile
            // an implementation of execute from the rule source. for now we create a generic
            // helper and call the generic execute method which interprets the rule
            BasicHelper helper;
            try {
                helper = (BasicHelper)helperClass.newInstance();
                helper.setRule(this);
            } catch (InstantiationException e) {
                // should not happen
                System.out.println("cannot create instance of " + helperClass.getCanonicalName());
                e.printStackTrace(System.out);
                return;
            } catch (IllegalAccessException e) {
                // should not happen
                System.out.println("cannot access " + helperClass.getCanonicalName());
                e.printStackTrace(System.out);
                return;
            } catch (ClassCastException e) {
                // should not happen
                System.out.println("cast exception " + helperClass.getCanonicalName());
                e.printStackTrace(System.out);
                return;
            }
            helper.execute(bindings, recipient, args);
        }
    }

    /**
     * called when a trigger is compiled for the rule to provide a String key which can be used
     * at execution time to obtain a handle on the rule instance
     *
     * @return a key which can be used later to obtain a reference to the rule
     */

    public String getKey()
    {
        String key = getName() + "_" + nextId();
        ruleKeyMap.put(key, this);
        return key;
    }

    /**
     * a hash map used to identify rules from their keys
     */
    private static HashMap<String, Rule> ruleKeyMap = new HashMap<String, Rule>();

    /**
     * a counter used to ensure rule identifiers are unique
     */
    private static int nextId = 0;

    /**
     * a method to return the next available counter for use in constructing a key for the rule
     * @return
     */
    private synchronized static int nextId()
    {
        return nextId++;
    }

    /**
     * generate a string representation of the rule
     *
     * @return a string representation of the rule
     */

    public String toString()
    {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write("RULE ");
        stringWriter.write(name);
        stringWriter.write("\n");
        stringWriter.write("CLASS ");
        stringWriter.write(targetClass);
        stringWriter.write('\n');
        stringWriter.write("METHOD ");
        stringWriter.write(targetMethod);
        stringWriter.write('\n');
        stringWriter.write(targetLocation.toString());
        stringWriter.write('\n');
        if (event != null) {
            event.writeTo(stringWriter);
        } else {
            stringWriter.write("BIND NOTHING\n");
        }
        if (condition != null) {
            condition.writeTo(stringWriter);
        } else {
            stringWriter.write("COND   TRUE\n");
        }
        if (action != null) {
            action.writeTo(stringWriter);
        } else {
            stringWriter.write("DO   NOTHING\n");
        }
        
        return stringWriter.toString();
    }

    /**
     * a helper class instantiated when the rule is triggered which provides an
     * implementation of execute for the rule and enables the execution context
     * to be accessed from the executed code
     */

    private Class helperClass;

    /**
     * Methods provided on this class are automatically made available as builtin operations in
     * expressions appearing in rule event bindings, conditions and actions. Although Helper
     * methods are all instance methods the message recipient for the method call is implicit
     * and does not appear in the builtin call. It does, however, appear in the runtime
     * invocation, giving the builtin operation access to the rule being fired (the Helper
     * instance has implicit access to the enclosing rule object since class Helper is
     * not static).
     */
    public static class Helper
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
            System.out.println("rule.debug{" + rule.getName() + "} : " + text);
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
         * builtin to test test if a countdown has been installed
         * @param identifier an object which uniquely identifies the countdown in question
         * @return true if the countdown is currently installed
         */
        public boolean getCountDown(Object identifier)
        {
            synchronized (countDownMap) {
                return (countDownMap.get(identifier) != null);
            }
        }

        /**
         * builtin to test add a countdown identified by a specific object and with the specified
         * count. n.b. this builtin checks if a countdown identified by the supplied object is
         * currently installed, returning false if so, otherwise atomically adds the countdown
         * and returns true. This allows the builtin to be used safely in conditions where concurrent
         * rule firings (including firings of multiple rules) might otherwise lead to a race condition.
         * @param identifier an object which uniquely identifies the countdown in question
         * @param count the number of times the countdown needs to be counted down before the
         * countdown operation returns true. e.g. if count is supplied as 2 then the first two
         * calls to @link{#countdown(Object)} will return false and the third call will return true.
         * @return true if a new countdown is installed, false if one already exists.
         */
        public boolean addCountDown(Object identifier, int count)
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
         * @link{#waitFor(Object, long)} for details and caveats regarding calling this builtin.
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
         * signal an event identified by the supplied object, causing all waiting threads to resume
         * rule processing and clearing the event. if there are no threads waiting either because
         * there has been no call to @link{#waitFor} or because some other thread has sent the signal
         * then this call returns false, otherwise it returns true. This operation is atomic,
         * allowing the builtin to be used in rule conditions.
         * @param identifier an object used to identify the which waiting threads the signal should
         * be delivered to. n.b. the operation is not performed using a notify on the supplied object.
         * this argument is used as a key to identify a synchronization object private to the rule
         * system.
         */
        public boolean signalWake(Object identifier)
        {
            Waiter waiter = removeWaiter(identifier);

            if (waiter != null) {
                return waiter.signalWake();
            }
            
            return false;
        }

        /**
         * signal an event identified by the suppied object, causing all waiting threads to throw an
         * exception and clearing the event. if there are no objects waiting, either because there has been
         * no call to @link{#waitFor} or because some other thread has already sent the signal, then this
         * call returns false, otherwise it returns true. This operation is atomic, allowing the builtin
         * to be used safely in rule conditions.
         * @param identifier an object used to identify the which waiting threads the signal should
         * be delivered to. n.b. the operation is not performed using a notify on the supplied object.
         * this argument is used as a key to identify a synchronization object private to the rule
         * system.
         */
        public boolean signalKill(Object identifier)
        {
            Waiter waiter = removeWaiter(identifier);

            if (waiter != null) {
                return waiter.signalKill();
            }

            return false;
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
    }

    /**
     * implementation of helper class which executes a compiled rule. it
     * can be overridden by a generated subclass in order to enable compiled
     * rule execution. this version inerprets the rule
     */
    public static class BasicHelper extends Helper
    {
        protected HashMap<String, Object> bindingMap;
        private HashMap<String, Type> bindingTypeMap;

        public BasicHelper()
        {
            super(null);
            bindingMap = new HashMap<String, Object>();
            bindingTypeMap = new HashMap<String, Type>();
        }

        public void setRule(Rule rule)
        {
            this.rule = rule;
        }

        /**
         * install values into the bindings map and then call the execute0 method
         * to actually execute the rule
         * @param bindings
         * @param recipient
         * @param args
         */
        public void execute(Bindings bindings, Object recipient, Object[] args)
                throws ExecuteException
        {
            System.out.println(rule.getName() + " execute");
            Iterator<Binding> iterator = bindings.iterator();
            while (iterator.hasNext()) {
                Binding binding = iterator.next();
                String name = binding.getName();
                Type type = binding.getType();
                if (binding.isHelper()) {
                    bindingMap.put(name, this);
                    bindingTypeMap.put(name, type);
                } else if (binding.isRecipient()) {
                    bindingMap.put(name, recipient);
                    bindingTypeMap.put(name, type);
                } else if (binding.isParam()) {
                    bindingMap.put(name, args[binding.getIndex() - 1]);
                    bindingTypeMap.put(name, type);
                }
            }

            // now do the actual execution

            execute0();
        }

        /**
         * basic implementation of rule execution
         *
         * @throws ExecuteException
         */
        
        protected void execute0()
                throws ExecuteException
        {
            // System.out.println(rule.getName() + " execute0");
            bind();
            if (test()) {
                fire();
            }
        }

        public void bindVariable(String name, Object value)
        {
            bindingMap.put(name, value);
        }

        public Object getBinding(String name)
        {
            return bindingMap.get(name);
        }

        private void bind()
                throws ExecuteException
        {
            // System.out.println(rule.getName() + " bind");
            rule.getEvent().interpret(this);
        }

        private boolean test()
                throws ExecuteException
        {
            // System.out.println(rule.getName() + " test");
            return rule.getCondition().interpret(this);
        }
        
        private void fire()
                throws ExecuteException
        {
            // System.out.println(rule.getName() + " fire");
            rule.getAction().interpret(this);
        }

        public String getName() {
            return rule.getName();
        }
    }

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
     * a hash map used to identify waiters from their identifying objects
     */
    private static HashMap<Object, Waiter> waitMap = new HashMap<Object, Waiter>();

    private static boolean debugParse = (System.getProperty("org.jboss.jbossts.orchestration.rule.debug") != null ? true : false);
}
