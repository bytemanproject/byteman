/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10, Red Hat and individual contributors
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
package org.jboss.byteman.rule.binding;

import org.jboss.byteman.agent.Transformer;
import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.expression.ArrayInitExpression;
import org.jboss.byteman.rule.expression.DollarExpression;
import org.jboss.byteman.rule.expression.NullLiteral;
import org.jboss.byteman.rule.helper.Helper;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.expression.Expression;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.RuleElement;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class used to store a binding of a named variable to a value of some given type
 */

public class Binding extends RuleElement
{

    public Binding(Rule rule, String name)
    {
        this(rule, name, Type.UNDEFINED, null);
    }

    public Binding(Rule rule, String name, Type type)
    {
        this(rule, name, type, null);
    }

    public Binding(Rule rule, String name, Type type, Expression value)
    {
        super(rule);
        this.name = name;
        this.type = (type != null ? type : Type.UNDEFINED);
        this.value = value;
        this.alias = null;
        // ok, check the name to see what type of binding we have
        if (name.matches("\\$[0-9].*")) {
            // $NNN references the method target or a parameter from 0 upwards
            index = Integer.valueOf(name.substring(1));
        } else if (name.equals("$$")) {
            // $$ references the helper implicitly associated with a builtin call
            index = DollarExpression.HELPER_IDX;
        } else if (name.equals("$!")) {
            // $! refers to the current return value for the trigger method and is only valid when
            // the rule is triggered AT EXIT
            index = DollarExpression.RETURN_VALUE_IDX;
        } else if (name.equals("$^")) {
            // $^ refers to the current throwable value for the trigger method and is only valid when
            // the rule is triggered AT THROW
            index = DollarExpression.THROWABLE_VALUE_IDX;
        } else if (name.equals("$#")) {
            // $# refers to the parameter count for the trigger method
            index = DollarExpression.PARAM_COUNT_IDX;
        } else if (name.equals("$*")) {
            // $* refers to the parameters for the trigger method supplied as an Object array
            index = DollarExpression.PARAM_ARRAY_IDX;
        } else if (name.equals("$@")) {
            // $* refers to the parameters for the trigger method supplied as an Object array
            index = DollarExpression.INVOKE_PARAM_ARRAY_IDX;
        } else if (name.equals("$CLASS")) {
            // $* refers to the parameters for the trigger method supplied as an Object array
            index = DollarExpression.TRIGGER_CLASS_IDX;
        } else if (name.equals("$METHOD")) {
            // $* refers to the parameters for the trigger method supplied as an Object array
            index = DollarExpression.TRIGGER_METHOD_IDX;
        } else if (name.matches("\\$[A-Za-z].*")) {
           // $AAAAA refers  to a local variable in the trigger method
            index = DollarExpression.LOCAL_IDX;
        } else {
            // anything else must be a variable introduced in the BINDS clause
            index = DollarExpression.BIND_IDX;
        }
        this.callArrayIndex = 0;

        this.updated = false;
        this.doCheckCast = false;
    }

    public Type typeCheck(Type expected)
            throws TypeException
    {
        if (alias != null) {
            type = alias.typeCheck(expected);
            doCheckCast = alias.doCheckCast;
            return type;
        }
        
        // value can be null if this is a rule method parameter
        if (value != null) {
            if (type != Type.UNDEFINED) {
                // in most cases TypeGroup.resolve() should have found a
                // class for any types referenced from the rule. However,
                // the one special case is when we have a declaration type
                // for a BIND variable. A failure to resolve to a class is
                // only legitimate when the declaration type omits the
                // package qualifier.

                if (type.isUndefined() && type.getName().indexOf('.') > 0) {
                    throw new TypeException("Binding.typecheck unknown type for binding " + name);
                }

                // If the name is unqualified we may still be able to
                // infer the type from that derived for the initializer
                // expression either because type checking the expression
                // adds a suitably qualified alias to the type group or
                // because it produces a type whose supers or implemented
                // interfaces include a matching qualified type. There
                // are a few specific cases where we cannot use the
                // initializer which we need to handle separately.

                if (Transformer.disallowDowncast()) {
                    // compatibility behaviour -- use declared type to help infer expression type
                    Type valueType = value.typeCheck(type);
                    // if the type is still undefined try to resolve it from the derived type
                    if (type.isUndefined()) {
                        resolveUnknownAgainstDerived(valueType);
                    }
                } else {
                    // downcasts in the binding are allowed but . . .

                    if (type.isArray() && value instanceof ArrayInitExpression) {
                        // with an array init we could try to infer the type but that
                        // would be dangerous for two reasons.
                        //
                        // Firstly, it means the user has to ensure all elements of the
                        // initializer have types which match the first one. So, e.g.
                        // { 1, "foo" } is ok to initialize an Object[] but inference
                        // will infer int[] at element 1 and then give an error at "foo".
                        //
                        // Even more pernicious, { "foo", "bar" } would be inferred to
                        // type String[] which the type checker will accept as a valid
                        // value to assign to an Object[]. However, when the rule is
                        // executed this will initialise the Object[] bind var to a new
                        // String[] with potentially nasty consequences should an object
                        // be inserted into the array.
                        //
                        // Clearly, the latter can also happen with other initialization
                        // expressions but an array initializer is a special case because
                        // it is a form of array literal and so we ought to use the type
                        // info to ensure we get the right type result.
                        Type valueType = value.typeCheck(type);
                        // if this failed to resolve an already undefined array type
                        // call resolveUnknownAgainstDerived to throw a type error
                        if (type.isUndefined()) {
                            resolveUnknownAgainstDerived(valueType);
                        }
                    } else if (value instanceof NullLiteral) {
                        // a null literal is polysemous (put that in your Funk and Wagnell)
                        // i.e. null can stand for an instance of any Object type. so we
                        // cannot infer it's type and instead can only type it against a
                        // known declaration type. so first reject any undefined declaraton
                        // type
                        if (type.isUndefined()) {
                            throw new TypeException("Binding.typecheck unknown type for binding " + name);
                        }
                        // use declaration type to type the null literal
                        value.typeCheck(type);
                    } else {
                        // typecheck the value first and then check for assignability in either direction
                        // modulo assigning void
                        Type valueType = value.typeCheck(Type.UNDEFINED);
                        // if the type is still undefined try to resolve it from the derived type
                        if (type.isUndefined()) {
                            resolveUnknownAgainstDerived(valueType);
                        } else if (!type.isAssignableFrom(valueType)) {
                            // if this is a downcast we need to check whether downcasts are disabled
                            if(valueType == Type.VOID || !valueType.isAssignableFrom(type)) {
                                throw new TypeException("Binding.typecheck : incompatible type for binding expression " + valueType + value.getPos());
                            } else if(!rule.requiresAccess(type)) {
                                // we need an explicit downcast here
                                // n.b. we can omit the downcast for types
                                // needing access because they are stored
                                // generically and handled by reflection
                                doCheckCast = true;
                            }
                        } else if (rule.requiresAccess(valueType) && !rule.requiresAccess(type)) {
                            // the value will have been computed generically as
                            // an object but we need to use it via a supertype which is
                            // not treated generically
                            doCheckCast = true;
                        } else if (type == Type.STRING && valueType != Type.STRING) {
                            // special case -- we actually use a string conversion
                            doCheckCast = true;
                        }
                    }
                }
            } else {
                // type the BIND var using whatever type we can derive for the initializer
                Type valueType = value.typeCheck(expected);
                type = valueType;
            }
        } else if (type.isUndefined()) {
            // if we could not resolve this using the bare name then we have a problem
            throw new TypeException("Binding.typecheck unknown type for binding " + name);
        }
        return type;
    }

    private void resolveUnknownAgainstDerived(Type derived) throws TypeException
    {
        // derived will be resolved to a class while type
        // is a type name which we have not yet resolved
        // to a class. that also implies that it has no
        // package qualifier

        // if we have an unresolved array type then we have a problem
        // because we can only accept an array built from the same
        // base type as derived. which means type and derived should
        // both be the same type.

        if (type.isArray()) {
            throw new TypeException("Binding.typecheck unknown type for binding " + name);
        }

        String typename = type.getName();
        Class<?> derivedClazz = Type.dereference(derived).getTargetClass();

        // if we have a name without package we need to look
        // through the super class/interface tree of the derived
        // class to find a match
        Class<?> nextClazz = derivedClazz;
        while (nextClazz != null) {
            String clazzName = nextClazz.getCanonicalName();
            if (clazzName.endsWith(typename)) {
                // use this class to resolve typename
                getTypeGroup().create(clazzName, nextClazz);
                return;
            }
            nextClazz = nextClazz.getSuperclass();
        }
        // ok maybe we need to look for an interface
        List<Class<?>> allInterfaces = new ArrayList<Class<?>>();
        LinkedList<Class<?>> toCheck = new LinkedList<Class<?>>();
        toCheck.addLast(derivedClazz);
        while (!toCheck.isEmpty() && (nextClazz = toCheck.pop()) != null) {
            // if we are looking at a class the include the super for further checking
            if (!nextClazz.isInterface()) {
                Class<?> nextSuper = nextClazz.getSuperclass();
                if (nextSuper != null) {
                    toCheck.addLast(nextSuper);
                }
            }
            // check for new interfaces
            Class[] interfaces = nextClazz.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Class<?> nextInterface = interfaces[i];
                // only process new interfaces
                if (!allInterfaces.contains(nextInterface)) {
                    // this might be the one
                    String interfaceName = nextInterface.getCanonicalName();
                    if (interfaceName.endsWith(typename)) {
                        // use this class to resolve typename
                        getTypeGroup().create(interfaceName, nextInterface);
                        return;
                    }
                    // remember that we have see this interface
                    allInterfaces.add(nextInterface);
                    // add it for recursive checking of its implemented interfaces
                    toCheck.addLast(nextInterface);
                }
            }
        }

        // failed to resolve type so throw a wobbly
        throw new TypeException("Binding.typecheck unknown type for binding " + name);
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException
    {
        if (isBindVar()) {
            Object result = value.interpret(helper);
            if (type.isPrimitive()) {
                // if the assigment involves a type conversion then we need to rebox the value
                result = rebox(value.getType(), type, result);
            } else if (doCheckCast) {
                if (type == Type.STRING) {
                    // force conversion to String
                    result = result.toString();
                } else if (!type.getTargetClass().isInstance(result)) {
                    throw new ClassCastException("Cannot cast " + result + " to class " + type);
                }
            }
            helper.setBinding(getName(), result);
            return result;
        }
        return null;
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        if (alias != null) {
            alias.compile(mv, compileContext);
        } else if (isBindVar()) {
            // push the current helper instance i.e. this -- adds 1 to stack height
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            // increment stack count
            compileContext.addStackCount(1);
            // compile the rhs expression for the binding -- adds 1 to stack height
            value.compile(mv, compileContext);
            // plant check cast if required
            if (doCheckCast) {
                // we schedule a direct checkcast here rather than calling
                // compileTypeConversion(value.type, type). the latter
                // is fine when we are doing a downcast but does nothing
                // if this is an 'upcast' i.e.when value.type is a subtype
                // of type. We may still find doCheckCast set in that case
                // because value.type requires access i.e. the value has
                // been handled generically as an Object.
                compileContext.compileCheckCast(type);
            }
            Type type = this.type;
            if (rule.requiresAccess(type)) {
                type = Type.OBJECT;
            }
            int size = (type.getNBytes() > 4 ? 2 : 1);
            String ivarName = getIVarName();
            mv.visitFieldInsn(Opcodes.PUTFIELD, rule.getHelperImplementationClassName(), ivarName, type.getInternalName(true, true));
            compileContext.addStackCount(-1 - size);
        }
    }

    public String getName()
    {
        return name;
    }

    public String getIVarName()
    {
        if (isLocalVar()) {
            return "$local_" + name;
        } else if (isParam()) {
            return "$param_" + name;
        } else if (isBindVar()) {
            return "$bind_" + name;
        } else if (isRecipient()) {
            return "$param_" + name;
        } else {
            return name;
        }
    }

    public Expression getValue()
    {
        if (alias != null) {
            return alias.getValue();
        }
        return value;
    }

    public Expression setValue(Expression value)
    {
        Expression oldValue = this.value;
        this.value = value;

        return oldValue;
    }

    public Type getType()
    {
        if (alias != null) {
            return alias.getType();
        }
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public int getCallArrayIndex()
    {
        if (alias != null) {
            return alias.getCallArrayIndex();
        }
        return callArrayIndex;
    }

    public void setCallArrayIndex(int callArrayIndex)
    {
        this.callArrayIndex = callArrayIndex;
    }

    public int getLocalIndex()
    {
        if (alias != null) {
            return alias.getLocalIndex();
        }
        return localIndex;
    }

    public void setLocalIndex(int localIndex)
    {
        this.localIndex = localIndex;
    }

    public boolean isParam()
    {
        return index > 0;
    }

    public boolean isRecipient()
    {
        return index == 0;
    }

    public boolean isHelper()
    {
        return index == DollarExpression.HELPER_IDX;
    }

    public boolean isBindVar()
    {
        return index == DollarExpression.BIND_IDX;
    }

    public boolean isLocalVar()
    {
        return index == DollarExpression.LOCAL_IDX;
    }

    public boolean isReturn()
    {
        return index == DollarExpression.RETURN_VALUE_IDX;
    }

    public boolean isThrowable()
    {
        return index == DollarExpression.THROWABLE_VALUE_IDX;
    }

    public boolean isParamCount()
    {
        return index == DollarExpression.PARAM_COUNT_IDX;
    }

    public boolean isParamArray()
    {
        return index == DollarExpression.PARAM_ARRAY_IDX;
    }

    public boolean isInvokeParamArray()
    {
        return index == DollarExpression.INVOKE_PARAM_ARRAY_IDX;
    }

    public boolean isTriggerClass()
    {
        return index == DollarExpression.TRIGGER_CLASS_IDX;
    }

    public boolean isTriggerMethod()
    {
        return index == DollarExpression.TRIGGER_METHOD_IDX;
    }

    public int getIndex()
    {
        return index;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String desc) {
        this.descriptor = desc;
    }

    /**
     * record that this binding occurs on the LHS of an assignment
     */
    public void setUpdated()
    {
        updated = true;
        if (alias != null) {
            alias.setUpdated();
        }
    }

    /**
     * record that this binding occurs on the LHS of an assignment
     * @return true if this binding occurs on the LHS
     */
    public boolean isUpdated()
    {
        return updated;
    }

    public void writeTo(StringWriter stringWriter)
    {
        if (isHelper()) {
            stringWriter.write(name);
        } else if (isParam() || isRecipient()) {
            stringWriter.write(name);
            if (type != null && (type.isDefined() || type.isObject())) {
                stringWriter.write(" : ");
                stringWriter.write(type.getName());
            }
        } else {
            stringWriter.write(name);
            if (type != null && (type.isDefined() || type.isObject())) {
                stringWriter.write(" : ");
                stringWriter.write(type.getName());
            }
        }
        if (value != null) {
            stringWriter.write(" = ");
            value.writeTo(stringWriter);
        }
    }


    public void aliasTo(Binding alias)
    {
        if (this.isLocalVar()) {
            this.alias = alias;
            if (this.updated) {
                alias.updated = true;
            }
        } else {
            System.out.println("Binding : attempt to alias non-local var " + getName() + " to " + alias.getName());
        }
    }

    public boolean isAlias()
    {
        return (alias != null);
    }

    public Binding getAlias()
    {
        return alias;
    }

    // special index values for non-positional parameters

    private final static int HELPER = -1;
    private final static int BIND_VAR = -2;
    private final static int LOCAL_VAR = -3;
    private final static int RETURN_VAR = -4;
    private final static int THROWABLE_VAR = -5;
    private final static int PARAM_COUNT_VAR = -6;
    private final static int PARAM_ARRAY_VAR = -7;
    private final static int INVOKE_PARAM_ARRAY_VAR = -8;

    private String name;
    private String descriptor; // supplied when the binding is for a local var
    private Type type;
    private Expression value;
    // the position index of the trigger method recipient or a trigger method parameter or one of the special index
    // values for other types  of parameters.
    private int index;
    // the offset into the trigger method Object array of the initial value for this parameter
    private int callArrayIndex;
    // the offset into the stack at which a local var is located
    private int localIndex;
    private Binding alias; // aliases $x to $n where x is a method parameter name and n its index in the parameter list
    boolean updated; // records whether this binding occurs on the lhs of an assignment
    boolean doCheckCast;
}
