/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
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
package org.jboss.byteman.rule;

import org.jboss.byteman.agent.HelperManager;
import org.jboss.byteman.rule.type.TypeGroup;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.exception.*;
import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.binding.Binding;
import org.jboss.byteman.rule.grammar.ECATokenLexer;
import org.jboss.byteman.rule.grammar.ECAGrammarParser;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.helper.Helper;
import org.jboss.byteman.rule.helper.InterpretedHelper;
import org.jboss.byteman.agent.Location;
import org.jboss.byteman.agent.Transformer;
import org.jboss.byteman.agent.RuleScript;
import org.objectweb.asm.Opcodes;

import org.jboss.byteman.rule.compiler.Compiler;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java_cup.runtime.Symbol;

/**
 * A rule ties together an event, condition and action. It also maintains a TypeGroup
 * identifying type information derived from these components.
 */
public class Rule
{
    /**
     * the script defining this rule
     */
    private RuleScript ruleScript;
    /**
     * the name of this rule supplied in the rule script
     */
    private String name;
    /**
     * the class loader for the target class
     */
    private ClassLoader loader;
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

    /**
     * return type of the rule's trigger method
     */

    private Type returnType;

    /**
     * the key under which this rule is indexed in the rule key map.
     */

    private String key;

    /**
     * lifecycle event manager for rule helpers
     */
    private HelperManager helperManager;
    /**
     * a list of field objects used by compiled code to enable rule code to access non-public fields
     */
    private List<Field> accessibleFields;

    /**
     * a list of method objects used by compiled code to enable rule code to access non-public methods
     */
    private List<Method> accessibleMethods;

    private Rule(RuleScript ruleScript, ClassLoader loader, HelperManager helperManager)
            throws ParseException, TypeException, CompileException
    {
        ParseNode ruleTree;

        this.ruleScript = ruleScript;
        this.helperClass = null;
        this.loader = loader;

        typeGroup = new TypeGroup(loader);
        bindings = new Bindings();
        checked = false;
        triggerClass = null;
        triggerMethod = null;
        triggerDescriptor = null;
        triggerAccess = 0;
        returnType = null;
        accessibleFields =  null;
        accessibleMethods = null;
        // this is only set when the rule is created via a real installed transformer
        this.helperManager =  helperManager;
        ECAGrammarParser parser = null;
        try {
            String file = getFile();
            ECATokenLexer lexer = new ECATokenLexer(new StringReader(ruleScript.getRuleText()));
            lexer.setStartLine(getLine());
            lexer.setFile(file);
            parser = new ECAGrammarParser(lexer);
            parser.setFile(file);
            Symbol parse = (debugParse ? parser.debug_parse() : parser.parse());
            if (parser.getErrorCount() != 0) {
                String message = "rule " + ruleScript.getName();
                message += parser.getErrors();
                throw new ParseException(message);
            }
            ruleTree = (ParseNode) parse.value;
        } catch (ParseException pe) {
            throw pe;
        } catch (Throwable th) {
            String message = "rule " + ruleScript.getName();
            if (parser != null && parser.getErrorCount() != 0) {
                message += parser.getErrors();
            }
            message += "\n" + th.getMessage();
            throw new ParseException(message);
        }

        ParseNode eventTree = (ParseNode)ruleTree.getChild(0);
        ParseNode conditionTree = (ParseNode)ruleTree.getChild(1);
        ParseNode actionTree = (ParseNode)ruleTree.getChild(2);

        event = Event.create(this, eventTree);
        condition = Condition.create(this, conditionTree);
        action = Action.create(this, actionTree);
        key = null;
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
        return ruleScript.getName();
    }

    public String getTargetClass() {
        return ruleScript.getTargetClass();
    }

    public String getTargetMethod() {
        return ruleScript.getTargetMethod();
    }

    public Location getTargetLocation() {
        return ruleScript.getTargetLocation();
    }

    public boolean isOverride()
    {
        return ruleScript.isOverride();
    }

    public boolean isInterface()
    {
        return ruleScript.isInterface();
    }

    /**
     * retrieve the start line for the rule
     * @return the start line for the rule
     */
    public int getLine()
    {
        return ruleScript.getLine();
    }

    /**
     * retrieve the name of the file containing this rule
     * @return the name of the file containing this rule
     */
    public String getFile()
    {
        return ruleScript.getFile();
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

    public String getTriggerClass() {
        return triggerClass;
    }

    public String getTriggerMethod() {
        return triggerMethod;
    }

    public String getTriggerDescriptor() {
        return triggerDescriptor;
    }

    public Type getReturnType()
    {
        return returnType;
    }                                                                                     

    /**
     * get the class loader of the target class for the rule
     * @return
     */
    public ClassLoader getLoader()
    {
        return loader;
    }

    public static Rule create(RuleScript ruleScript, ClassLoader loader, HelperManager helperManager)
            throws ParseException, TypeException, CompileException
    {
            return new Rule(ruleScript, loader, helperManager);
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

    /**
     * has this rule been typechecked and/or compiled
     * @return true if this rule has been typechecked and/or compiled otherwise false
     */
    public boolean isChecked()
    {
        return checked;
    }

    /**
     * has this rule failed to typecheck or compile
     * @return true if this rule has failed to typecheck or compile otherwise false
     */
    public boolean isCheckFailed()
    {
        return checkFailed;
    }

    /**
     * has this rule been typechecked and compiled without error.
     * @return true if this rule has been typechecked and compiled without error otherwise false
     */
    public boolean isCheckedOk()
    {
        return (checked && !checkFailed);
    }

    /**
     * disable triggering of rules inside the current thread. this is the version called internally
     * after returning from a method call in a rule binding, condition or action.
     * @return true if triggering was previously enabled and false if it was already disabled
     */
    public static boolean disableTriggersInternal()
    {
        return Transformer.disableTriggers(false);
    }

    /**
     * enable triggering of rules inside the current thread n.b. this is called internally by the rule
     * engine before it executes a method call in a rule binding, condition or action. it will not
     * enable triggers if they have been switched off by an earlier call to userDisableTriggers.
     * @return true if triggering was previously enabled and false if it was already disabled
     */
    public static boolean enableTriggersInternal()
    {
        return Transformer.enableTriggers(false);
    }

    /**
     * disable triggering of rules inside the current thread. this is the version which should be
     * called from a Helper class to ensure that subsequent method invocatiosn during execution of
     * the current rule bindings, condition or action do not recursively trigger rules. It ensures
     * that subsequent calls to enableTriggers have no effect. The effect lasts until the end of
     * processing for the current rule when resetTriggers is called.
     * @return true if triggering was previously enabled and false if it was already disabled
     */
    public static boolean disableTriggers()
    {
        return Transformer.disableTriggers(true);
    }

    /**
     * enable triggering of rules inside the current thread. this is called internally by the rule
     * engine after rule execution has completed. it will re-enable triggers even if they have been
     * switched off by an earlier call to userDisableTriggers. It is also called by the default helper
     * to reverse the effect of calling userDisableTriggers.
     * @return true if triggering was previously enabled and false if it was already disabled
     */
    public static boolean enableTriggers()
    {
        return Transformer.enableTriggers(true);
    }

    /**
     * check if triggering of rules is enabled inside the current thread
     * @return true if triggering is enabled and false if it is disabled
     */
    public static boolean isTriggeringEnabled()
    {
        return Transformer.isTriggeringEnabled();
    }

    /**
     * typecheck and then compile this rule unless either action has been tried before
     * @return true if the rule successfully type checks and then compiles under this call or a previous
     * call or false if either operation has previously failed or fails under this call.
     */
    private synchronized boolean ensureTypeCheckedCompiled()
    {
        if (checkFailed) {
            return false;
        }

        if (!checked) {
            // ensure we don't trigger any code inside the type check or compile
            // n.b. we may still allow recursive triggering while executing
            boolean triggerEnabled = false;
            String detail = "";
            try {
                typeCheck();
                compile();
                checked = true;
                installed();
            } catch (TypeWarningException te) {
                checkFailed = true;
                if (Transformer.isVerbose()) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter writer = new PrintWriter(stringWriter);
                    writer.println("Rule.ensureTypeCheckedCompiled : warning type checking rule " + getName());
                    te.printStackTrace(writer);
                    detail = stringWriter.toString();
                    System.out.println(detail);
                }
            } catch (TypeException te) {
                checkFailed = true;
                StringWriter stringWriter = new StringWriter();
                PrintWriter writer = new PrintWriter(stringWriter);
                writer.println("Rule.ensureTypeCheckedCompiled : error type checking rule " + getName());
                te.printStackTrace(writer);
                detail = stringWriter.toString();
                System.out.println(detail);
            } catch (CompileException ce) {
                checkFailed = true;
                StringWriter stringWriter = new StringWriter();
                PrintWriter writer = new PrintWriter(stringWriter);
                writer.println("Rule.ensureTypeCheckedCompiled : error compiling rule " + getName());
                ce.printStackTrace(writer);
                detail = stringWriter.toString();
                System.out.println(detail);
            }

            ruleScript.recordCompile(triggerClass, loader, !checkFailed, detail);
            return !checkFailed;
        }

        return true;
    }

    /**
     * type check this rule
     * @throws TypeException if the ruele contains type errors
     */
    public void typeCheck()
            throws TypeException
    {
        String helperName = ruleScript.getTargetHelper();
        
        // ensure that we have a valid helper class
        if (helperName != null) {
            try {
                helperClass = loader.loadClass(helperName);
            } catch (ClassNotFoundException e) {
                throw new TypeException("Rule.typecheck : unknown helper class " + helperName + " for rule " + getName());
            }
        } else {
            helperClass = Helper.class;
        }
        if (triggerExceptions != null) {
            // ensure that the type group includes the exception types
            typeGroup.addExceptionTypes(triggerExceptions);
        }
        
        // try to resolve all types in the type group to classes

        typeGroup.resolveTypes();

        // use the descriptor to derive the method argument types and type any bindings for them
        // that are located in the bindings list

        installParameters((triggerAccess & Opcodes.ACC_STATIC) != 0, triggerClass);

        event.typeCheck(Type.VOID);
        condition.typeCheck(Type.Z);
        action.typeCheck(Type.VOID);
    }

    /**
     * install helper class used to execute this rule. this may involve generating a compiled helper class
     * for the rule and, if compilation to bytecode is enabled, generating bytecode for a method of this class
     * used to execute the rule binding, condition and action expressions. If the rule employ sthe default helper
     * without enabling compilation to bytecode then no class need be generated. the installed helper class will
     * be the predefined class InterpretedHelper.
     * @throws CompileException if the rule cannot be compiled
     */
    public void compile()
            throws CompileException
    {
        boolean compileToBytecode = isCompileToBytecode();

        if (helperClass == Helper.class && !compileToBytecode) {
            // we can use the builtin interpreted helper adapter for class Helper
           helperImplementationClass = InterpretedHelper.class;
        } else {
            // we need to generate a helper adapter class which either interprets or compiles

            helperImplementationClass = Compiler.getHelperAdapter(this, helperClass, compileToBytecode);
        }
    }

    /**
     * should rules be compiled to bytecode
     * @return true if rules should be compiled to bytecode otherwise false
     */
    private boolean isCompileToBytecode()
    {
        return Transformer.isCompileToBytecode();
    }

    private void installParameters(boolean isStatic, String className)
            throws TypeException
    {
        Type type;
        // add a binding for the helper so we can call builtin static methods
        type = typeGroup.create(helperClass.getName());
        Binding ruleBinding = bindings.lookup("$$");
        if (ruleBinding != null) {
            ruleBinding.setType(type);
        } else {
            bindings.append(new Binding(this, "$$", type));
        }

        if (!isStatic) {
            Binding recipientBinding = bindings.lookup("$0");
            if (recipientBinding != null) {
                type = typeGroup.create(className);
                if (type.isUndefined()) {
                    throw new TypeException("Rule.installParameters : Rule " + name + " unable to load class " + className);
                }
                recipientBinding.setType(type);
            }
        }

        String returnTypeName = Type.parseMethodReturnType(triggerDescriptor);

        returnType = typeGroup.create(returnTypeName);

        Iterator<Binding> iterator = bindings.iterator();

        while (iterator.hasNext()) {
            Binding binding = iterator.next();
            // these bindings are typed via the descriptor installed during trigger injection
            // note that the return type has to be done this way because it may represent the
            // trigger method return type or the invoke method return type
            if (binding.isParam() || binding.isLocalVar() || binding.isReturn()) {
                String typeName = binding.getDescriptor();
                String[] typeAndArrayBounds = typeName.split("\\[");
                Type baseType = typeGroup.create(typeAndArrayBounds[0]);
                Type fullType = baseType;
                if (baseType.isUndefined()) {
                    throw new TypeException("Rule.installParameters : Rule " + name + " unable to load class " + baseType);
                }
                for (int i = 1; i < typeAndArrayBounds.length ; i++) {
                    fullType = typeGroup.createArray(fullType);
                }
                binding.setType(fullType);
            } else if (binding.isThrowable()) {
                // TODO -- enable a more precise specification of the throwable type
                // we need to be able to obtain the type descriptor for the throw operation
                binding.setType(typeGroup.ensureType(Throwable.class));
            } else if (binding.isParamCount()) {
                binding.setType(Type.I);
            } else if (binding.isParamArray() || binding.isInvokeParamArray()) {
                binding.setType(Type.OBJECT.arrayType());
            } else if (binding.isTriggerClass() || binding.isTriggerMethod()) {
                binding.setType(Type.STRING);
            }
        }
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
        boolean enabled = isTriggeringEnabled();
        if (!enabled) {
            // we don't trigger code while we are doing rule housekeeping
            return;
        }

        // disable triggering until we get into actual rule code
        
        disableTriggersInternal();

        try {
        Rule rule = ruleKeyMap.get(key);
        if (Transformer.isVerbose()) {
            System.out.println("Rule.execute called for " + key);
        }

        // if the key is no longer present it just means the rule has been decommissioned so return
        if (rule == null) {
            if (Transformer.isVerbose()) {
                System.out.println("Rule.execute for decommissioned key " + key);
            }
            return;
        }

        rule.execute(recipient, args);
        } finally {
            // restore the status quo -- we must have been enabled if we got to this method
            enableTriggers();
        }            
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
        // type check and createHelperAdapter the rule now if it has not already been done

        if (ensureTypeCheckedCompiled()) {

            // create a helper and get it to execute the rule
            // eventually we will create a subclass of helper for each rule and createHelperAdapter
            // an implementation of execute from the rule source. for now we create a generic
            // helper and call the generic execute method which interprets the rule
            HelperAdapter helper;
            try {
                Constructor constructor = helperImplementationClass.getConstructor(Rule.class);
                helper = (HelperAdapter)constructor.newInstance(this);
                //helper = (RuleHelper)helperClass.newInstance();
                //helper.setRule(this);
                helper.execute(recipient, args);
            } catch (NoSuchMethodException e) {
                // should not happen!!!
                System.out.println("cannot find constructor " + helperImplementationClass.getCanonicalName() + "(Rule) for helper class");
                e.printStackTrace(System.out);
                return;
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InstantiationException e) {
                // should not happen
                System.out.println("cannot create instance of " + helperImplementationClass.getCanonicalName());
                e.printStackTrace(System.out);
                return;
            } catch (IllegalAccessException e) {
                // should not happen
                System.out.println("cannot access " + helperImplementationClass.getCanonicalName());
                e.printStackTrace(System.out);
                return;
            } catch (ClassCastException e) {
                // should not happen
                System.out.println("cast exception " + helperImplementationClass.getCanonicalName());
                e.printStackTrace(System.out);
                return;
            } catch (EarlyReturnException e) {
                throw e;
            } catch (ThrowException e) {
                throw e;
            } catch (ExecuteException e) {
                System.out.println(getName() + " : " + e);
                throw e;
            } catch (Throwable throwable) {
                System.out.println(getName() + " : " + throwable);
                throw new ExecuteException(getName() + "  : caught " + throwable, throwable);
            }
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
        if (key != null) {
            return key;
        }
        
        String key = getName() + "_" + nextId();
        this.key = key;
        ruleKeyMap.put(key, this);
        return key;
    }

    /**
     * return the key under which this rule has been indexed in the rule key map
     * @return
     */
    public String lookupKey()
    {
        return key;
    }


    /**
     * delete any reference to the rule from the rule map
     */
    public void purge()
    {
        // nothing to do unless we actually allocated a key
        if (key != null) {
            ruleKeyMap.remove(key);
            if (checked) {
                uninstalled();
            }
        }
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

    private static boolean compileRules()
    {
        return Transformer.isCompileToBytecode();
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
        stringWriter.write(getName());
        stringWriter.write("\n");
        if (isInterface()) {
            stringWriter.write("INTERFACE ");
        } else {
            stringWriter.write("CLASS ");
        }
        if (isOverride()) {
            stringWriter.write("^");
        }
        stringWriter.write(getTargetClass());
        stringWriter.write('\n');
        stringWriter.write("METHOD ");
        stringWriter.write(getTargetMethod());
        stringWriter.write('\n');
        stringWriter.write(getTargetLocation().toString());
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
     * a helper class which defines the builtin methods available to this rule -- by default Helper
     */
    private Class helperClass;

    /**
     * an extension of the helper class which implements the methods of interface RuleHelper -- by default
     * InterpretedHelper. This is the class which is instantiated and used as the target for an execute
     * operation.
     */

    private Class helperImplementationClass;

    /**
     * a getter allowing the helper class for the rule to be identified
     * 
     * @return
     */
    public Class getHelperClass()
    {
        return helperClass;
    }

    /**
     * flag true if debugging of rule parsing is desired and false if it should not be performed
     */
    private static boolean debugParse = (System.getProperty("org.jboss.byteman.rule.debug") != null ? true : false);

    /**
     * method called when the rule has been successfully injected into a class, type checked and compiled. it passes
     * the message on to the Transformer so it can perform helper lifecycle management.
     */
    private void installed()
    {
        helperManager.installed(this);
    }

    /**
     * method called when the rule has been uninstalled after previously being successfully injected into a class,
     * type checked and compiled. it passes the message on to the Transformer so it can perform helper lifecycle
     * management.
     */
    private void uninstalled()
    {
        helperManager.uninstalled(this);
    }

    public int addAccessibleField(Field field) {
        if (accessibleFields == null) {
            accessibleFields = new ArrayList<Field>();
        }
        int index = accessibleFields.size();
        accessibleFields.add(field);
        return index;
    }
    
    public int addAccessibleMethod(Method method) {
        if (accessibleMethods == null) {
            accessibleMethods = new ArrayList<Method>();
        }
        int index = accessibleMethods.size();
        accessibleMethods.add(method);
        return index;
    }

    public Object getAccessibleField(Object owner, int fieldIndex) throws ExecuteException
    {
        try {
            Field field = accessibleFields.get(fieldIndex);
            return field.get(owner);
        } catch (Exception e) {
            throw new  ExecuteException("Rule.getAccessibleField : unexpected error getting non-public field in rule " + getName(), e);
        }
    }

    public void setAccessibleField(Object owner, Object value, int fieldIndex) throws ExecuteException
    {
        try {
            Field field = accessibleFields.get(fieldIndex);
            field.set(owner, value);
        } catch (Exception e) {
            throw new  ExecuteException("Rule.setAccessibleField : unexpected error setting non-public field in rule " + getName(), e);
        }
    }

    public Object invokeAccessibleMethod(Object target, Object[] args, int methodIndex)
    {
        try {
            Method method = accessibleMethods.get(methodIndex);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new  ExecuteException("Rule.invokeAccessibleMethod : unexpected error invoking non-public method in rule " + getName(), e);
        }
    }

    public long getObjectSize(Object o)
    {
        return helperManager.getObjectSize(o);
    }
}
