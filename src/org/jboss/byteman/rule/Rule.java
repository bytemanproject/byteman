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
package org.jboss.byteman.rule;

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
import org.jboss.byteman.agent.LocationType;
import org.jboss.byteman.agent.Transformer;
import org.objectweb.asm.Opcodes;

import org.jboss.byteman.rule.compiler.Compiler;

import java.io.*;
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
     * the line number for the start of the parseable rule text (the BIND clause)
     */
    private int line;
    /**
     * the name of the file which contains this rule
     */
    private String file;
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

    private Rule(String name, String targetClass, String targetMethod,Class<?> helperClass, Location targetLocation, String ruleSpec, int line, String file, ClassLoader loader)
            throws ParseException, TypeException, CompileException
    {
        ParseNode ruleTree;

        this.name = name;
        this.line = line;
        this.file = file;
        typeGroup = new TypeGroup(loader);
        bindings = new Bindings();
        if (ruleSpec != null) {
            // ensure line numbers start at 1
            String fullSpec = "\n" + ruleSpec;
            try {
                ECATokenLexer lexer = new ECATokenLexer(new StringReader(fullSpec));
                lexer.setStartLine(line);
                lexer.setFile(file);
                ECAGrammarParser parser = new ECAGrammarParser(lexer);
                parser.setFile(file);
                Symbol parse = (debugParse ? parser.debug_parse() : parser.parse());
                ruleTree = (ParseNode) parse.value;
            } catch (Exception e) {
                throw new ParseException("org.jboss.byteman.rule.Rule : error parsing rule\n" + ruleSpec, e);
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
        this.helperClass = (helperClass != null ? helperClass : Helper.class);
        this.targetLocation = (targetLocation != null ? targetLocation : Location.create(LocationType.ENTRY, ""));
        triggerClass = null;
        triggerMethod = null;
        triggerDescriptor = null;
        triggerAccess = 0;
        returnType = null;
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

    public String getTriggerClass() {
        return triggerClass;
    }

    public Type getReturnType()
    {
        return returnType;
    }
    
    public static Rule create(String name, String targetClass, String targetMethod, Class<?> helperClass, Location targetLocation, String ruleSpec, int line, String file, ClassLoader loader)
            throws ParseException, TypeException, CompileException
    {
            return new Rule(name, targetClass, targetMethod, helperClass, targetLocation, ruleSpec, line, file, loader);
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
                compile();
            } catch (TypeException te) {
                System.out.println("Rule.ensureTypeCheckedCompiled : error typechecking rule " + getName());
                te.printStackTrace(System.out);
                checkFailed = true;
                return false;
            } catch (CompileException ce) {
                System.out.println("Rule.ensureTypeCheckedCompiled : error compiling rule " + getName());
                ce.printStackTrace(System.out);
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

        // use the descriptor to derive the method argument types and type any bindings for them
        // that are located in the bindings list

        installParameters((triggerAccess & Opcodes.ACC_STATIC) != 0, triggerClass);

        event.typeCheck(Type.VOID);
        condition.typeCheck(Type.Z);
        action.typeCheck(Type.VOID);
        checked = true;
    }

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
        // add a binding for the rule so we can call builtin static methods
        type = typeGroup.create(helperClass.getCanonicalName());
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

        Iterator<Binding> iterator = bindings.iterator();

        while (iterator.hasNext()) {
            Binding binding = iterator.next();
            if (binding.isParam() || binding.isLocalVar()) {
                String typeName = binding.getDescriptor();
                String[] typeAndArrayBounds = typeName.split("\\[");
                Type baseType = typeGroup.create(typeAndArrayBounds[0]);
                Type paramType = baseType;
                if (baseType.isUndefined()) {
                    throw new TypeException("Rule.installParameters : Rule " + name + " unable to load class " + baseType);
                }
                for (int i = 1; i < typeAndArrayBounds.length ; i++) {
                    paramType = typeGroup.createArray(paramType);
                }
                binding.setType(paramType);
            }
        }

        String returnTypeName = Type.parseMethodReturnType(triggerDescriptor);

        returnType = typeGroup.create(returnTypeName);
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
        if (Transformer.isVerbose()) {
            System.out.println("Rule.execute called for " + key);
        }

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
                helper.execute(bindings, recipient, args);
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
                throw new ExecuteException(getName() + " unknnown error : " + throwable, throwable);
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
        String key = getName() + "_" + nextId();
        ruleKeyMap.put(key, this);
        return key;
    }

    /**
     * retrieve the start line for the ruel's parseable text
     * @return the start line for the ruel's parseable text
     */
    public int getLine()
    {
        return line;
    }

    /**
     * retrieve the name of the file containing this rule
     * @return the name of the file containing this rule
     */
    public String getFile()
    {
        return file;
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
    
    private static boolean debugParse = (System.getProperty("org.jboss.byteman.rule.debug") != null ? true : false);
}
