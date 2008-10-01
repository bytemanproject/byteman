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
package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.exception.ThrowException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;

import java.io.StringWriter;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ListIterator;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: adinn
 * Date: 30-Sep-2008
 * Time: 11:12:41
 * To change this template use File | Settings | File Templates.
 */
public class ThrowExpression extends Expression
{
    private String typeName;
    private List<Expression> arguments;
    private List<Type> argumentTypes;
    private Constructor constructor;

    public ThrowExpression(Rule rule, String typeName, Token token, List<Expression> arguments) {
        super(rule, Type.UNDEFINED, token);
        this.typeName = typeName;
        this.arguments = arguments;
        this.argumentTypes = null;
        this.constructor = null;
    }
    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public boolean bind() {
        // check that the recipient and argument expressions have valid bindings

        boolean valid = true;
        Iterator<Expression> iterator = arguments.iterator();

        while (valid && iterator.hasNext()) {
            valid &= iterator.next().bind();
        }

        return valid;
    }

    /**
     * ensure that all type references in the expression and its component expressions
     * can be resolved, that the type of the expression is well-defined and that it is
     * compatible with the type expected in the context in which it occurs.
     *
     * @param expected  the type expected for the expression in the contxt in which it occurs. this
     *                  may be void but shoudl not be undefined at the point where type checking is performed.
     * @return
     * @throws org.jboss.jbossts.orchestration.rule.exception.TypeException
     *
     */
    public Type typeCheck(Type expected) throws TypeException {
        // check the exception type is defined and then look for a relevant constructor

        TypeGroup typeGroup = getTypeGroup();

        type = Type.dereference(typeGroup.create(typeName));

        if (type.isUndefined()) {
            throw new TypeException("ThrowExpression.typeCheck : unknown exception type " + typeName + getPos());
        }

        if (!Throwable.class.isAssignableFrom(type.getTargetClass())) {
            throw new TypeException("ThrowExpression.typeCheck : not an exception type " + typeName  + getPos());
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
            throw new TypeException("ThrowExpression.typeCheck : invalid method for target class " + typeName + getPos());
        }

        if (candidates.size() > 1) {
            throw new TypeException("ThrowExpression.typeCheck : ambiguous method signature for target class " + typeName + getPos());
        }

        constructor = candidates.get(0);

        // expected type should always be void since throw can only occur as a top level action
        // however, we need to be sure that the trigering method throws this exception type or
        // else that it is a subtype of runtime exception

        if (RuntimeException.class.isAssignableFrom(type.getTargetClass())) {
            return type;
        } else {
            Iterator<Type> iterator = typeGroup.getExceptionTypes().iterator();
            while (iterator.hasNext()) {
                Type exceptionType = iterator.next();
                if (Type.dereference(exceptionType).isAssignableFrom(type)) {
                    // ok we foudn a suitable declaration for the exception
                    return type;
                }
            }
            // didn't find a suitable type in the method type list
            throw new TypeException("ThrowExpression.typeCheck : exception type not declared by trigger method "  + typeName + getPos());
        }
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
     * @throws org.jboss.jbossts.orchestration.rule.exception.ExecuteException
     *
     */
    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        int l = arguments.size();
        int i;
        Object[] callArgs = new Object[l];
        for (i =0; i < l; i++) {
            callArgs[i] = arguments.get(i).interpret(helper);
        }
        try {
            Throwable th = (Throwable) constructor.newInstance(callArgs);
            ThrowException thex = new ThrowException(th);
            throw thex;
        } catch (InstantiationException e) {
            throw new ExecuteException("ThrowExpression.interpret : unable to instantiate exception class " + typeName + getPos(), e);
        } catch (IllegalAccessException e) {
            throw new ExecuteException("ThrowExpression.interpret : unable to access exception class " + typeName + getPos(), e);
        } catch (InvocationTargetException e) {
            throw new ExecuteException("ThrowExpression.interpret : unable to invoke exception class constructor for " + typeName + getPos(), e);
        }
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write("throw " + type.getName() + "(");
        for (Expression argument : arguments) {
            argument.writeTo(stringWriter);
        }
        stringWriter.write(")");

    }
}
