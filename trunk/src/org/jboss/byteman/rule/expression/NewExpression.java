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
package org.jboss.byteman.rule.expression;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.compiler.StackHeights;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.ThrowException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.type.TypeGroup;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Expression which implements a new operation.
 */
public class NewExpression extends Expression
{
    private String typeName;
    private List<Expression> arguments;
    private List<Type> argumentTypes;
    private List<Type> paramTypes;
    private Constructor constructor;

    public NewExpression(Rule rule, ParseNode token, List<Expression> arguments) {
        super(rule, Type.UNDEFINED, token);
        this.typeName = token.getText();
        this.arguments = arguments;
        this.argumentTypes = null;
        this.constructor = null;
    }
    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public void bind() throws TypeException
    {
        // check that the recipient and argument expressions have valid bindings

        Iterator<Expression> iterator = arguments.iterator();

        while (iterator.hasNext()) {
            iterator.next().bind();
        }
    }

    /**
     * ensure that all type references in the expression and its component expressions
     * can be resolved, that the type of the expression is well-defined and that it is
     * compatible with the type expected in the context in which it occurs.
     *
     * @param expected  the type expected for the expression in the contxt in which it occurs. this
     *                  may be void but shoudl not be undefined at the point where type checking is performed.
     * @return
     * @throws org.jboss.byteman.rule.exception.TypeException
     *
     */
    public Type typeCheck(Type expected) throws TypeException {
        // check the new instance type is defined and then look for a relevant constructor

        TypeGroup typeGroup = getTypeGroup();

        type = Type.dereference(typeGroup.create(typeName));

        if (type == null || type.isUndefined()) {
            throw new TypeException("ThrowExpression.typeCheck : unknown exception type " + typeName + getPos());
        }

        Class clazz = type.getTargetClass();
        // if we can find a unique method then we can use it to type the parameters
        // otherwise we do it the hard way
        int arity = arguments.size();
        Constructor[] constructors = clazz.getConstructors();
        List<Constructor> candidates = new ArrayList<Constructor>();
        boolean duplicates = false;

        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == arity) {
                candidates.add(constructor);
            }
        }

        argumentTypes = new ArrayList<Type>();

        // check each argument in turn -- if all candidates have the same argument type then
        // use that as the type to check against
        for (int i = 0; i < arguments.size() ; i++) {
            if (candidates.isEmpty()) {
                throw new TypeException("ThrowExpression.typeCheck : invalid constructor for target class " + typeName + getPos());
            }

            // TODO get and prune operations do not allow for coercion but type check does!
            // e.g. the parameter type may be int and the arg type float
            // or the parameter type may be String and the arg type class Foo
            // reimplement this using type inter-assignability to do the pruning

            Class candidateClass = getCandidateArgClass(candidates, i);
            Type candidateType;
            if (candidateClass != null) {
                candidateType = typeGroup.ensureType(candidateClass);
            } else {
                candidateType = Type.UNDEFINED;
            }
            Type argType = arguments.get(i).typeCheck(candidateType);
            argumentTypes.add(argType);
            if (candidateType == Type.UNDEFINED) {
                // we had several constructors to choose from
                candidates = pruneCandidates(candidates, i, argType.getTargetClass());
            }
        }

        if (candidates.isEmpty()) {
            throw new TypeException("ThrowExpression.typeCheck : invalid constructor for target class " + typeName + getPos());
        }

        if (candidates.size() > 1) {
            throw new TypeException("ThrowExpression.typeCheck : ambiguous constructor signature for target class " + typeName + getPos());
        }

        constructor = candidates.get(0);

        // make sure we know the formal parameter types and have included them in the typegroup

        paramTypes = new ArrayList<Type>();
        Class<?>[] paramClasses = constructor.getParameterTypes();

        for (int i = 0; i < arguments.size() ; i++) {
            paramTypes.add(typeGroup.ensureType(paramClasses[i]));
        }

        // if the expected type is defined then ensure we can assign this type to it

        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("NewExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Class getCandidateArgClass(List<Constructor> candidates, int argIdx)
    {
        Class argClazz = null;

        for (Constructor c : candidates) {
            Class nextClazz = c.getParameterTypes()[argIdx];
            if (argClazz == null) {
                argClazz = nextClazz;
            } else if (argClazz != nextClazz) {
                return null;
            }
        }

        return argClazz;
    }

    public List<Constructor> pruneCandidates(List<Constructor> candidates, int argIdx, Class argClazz)
    {
        for (int i = 0; i < candidates.size();) {
            Constructor c = candidates.get(i);
            Class nextClazz = c.getParameterTypes()[argIdx];
            if (nextClazz != argClazz) {
                candidates.remove(i);
            } else {
                i++;
            }
        }
        return candidates;
    }

    /**
     * evaluate the expression by interpreting the expression tree
     *
     * @param helper an execution context associated with the rule whcih contains a map of
     *               current bindings for rule variables and another map of their declared types both of which
     *               are indexed by varoable name. This includes entries for the helper (name "-1"), the
     *               recipient if the trigger method is not static (name "0") and the trigger method arguments
     *               (names "1", ...)
     * @return the result of evaluation as an Object
     * @throws org.jboss.byteman.rule.exception.ExecuteException
     *
     */
    public Object interpret(HelperAdapter helper) throws ExecuteException {
        int l = arguments.size();
        int i;
        Object[] callArgs = new Object[l];
        for (i =0; i < l; i++) {
            callArgs[i] = arguments.get(i).interpret(helper);
        }
        try {
            Object result = constructor.newInstance(callArgs);
            return result;
        } catch (InstantiationException e) {
            throw new ExecuteException("ThrowExpression.interpret : unable to instantiate exception class " + typeName + getPos(), e);
        } catch (IllegalAccessException e) {
            throw new ExecuteException("ThrowExpression.interpret : unable to access exception class " + typeName + getPos(), e);
        } catch (InvocationTargetException e) {
            throw new ExecuteException("ThrowExpression.interpret : unable to invoke exception class constructor for " + typeName + getPos(), e);
        }
    }

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
        int currentStack = currentStackHeights.stackCount;
        int expected = 1;
        int extraParams = 0;

        // ok, we need to create the new instance and then initialise it.

        // create the new instance -- adds 1 to stack
        String exceptionClassName = type.getInternalName();
        mv.visitTypeInsn(Opcodes.NEW, exceptionClassName);
        currentStackHeights.addStackCount(1);
        // copy the exception so we can init it
        mv.visitInsn(Opcodes.DUP);
        currentStackHeights.addStackCount(1);

        int argCount = arguments.size();

        // stack each of the arguments to the constructor
        for (int i = 0; i < argCount; i++) {
            Type argType = argumentTypes.get(i);
            Type paramType = paramTypes.get(i);
            int paramCount = (paramType.getNBytes() > 4 ? 2 : 1);

            // track extra storage used after type conversion
            extraParams += (paramCount);
            arguments.get(i).compile(mv, currentStackHeights, maxStackHeights);
            compileTypeConversion(argType, paramType, mv, currentStackHeights, maxStackHeights);
        }

        // construct the exception
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, exceptionClassName, "<init>", getDescriptor());

        // modify the stack height to account for the removed exception and params
        currentStackHeights.addStackCount(-(extraParams+1));

        if (currentStackHeights.stackCount != currentStack + expected) {
            throw new CompileException("ThrowExpression.compile : invalid stack height " + currentStackHeights.stackCount + " expecting " + (currentStack + expected));
        }

        // no need to update max stack unless extraParams is zero in which case the exception
        // instance may have thrown us over the limit

        if (extraParams < 1) {
            int overflow = ((currentStack + 1) - maxStackHeights.stackCount);
            if (overflow > 0) {
                maxStackHeights.addStackCount(overflow);
            }
        }
    }

    private String getDescriptor()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        int nParams = paramTypes.size();
        for (int i = 0; i < nParams; i++) {
            buffer.append(paramTypes.get(i).getInternalName(true, true));
        }
        buffer.append(")V");
        return buffer.toString();
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write("throw ");
        if (type == null || Type.UNDEFINED == type) {
            stringWriter.write(typeName);
        } else {
            stringWriter.write(type.getName());
        }
        String separator = "";
        stringWriter.write("(");
        for (Expression argument : arguments) {
            stringWriter.write(separator);
            argument.writeTo(stringWriter);
            separator = ",";
        }
        stringWriter.write(")");

    }
}