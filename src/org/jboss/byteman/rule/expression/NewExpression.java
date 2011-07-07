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
package org.jboss.byteman.rule.expression;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.type.TypeGroup;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.StringWriter;
import java.lang.reflect.Array;
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
    private List<Expression> arrayDims;
    private List<Type> argumentTypes;
    private List<Type> paramTypes;
    private Constructor constructor;
    // if the new value is an array it will have this many dimensions
    private int arrayDimCount;
    // if the new value is an array this many of its dimensions are specified and are to be instantiated
    private int arrayDimDefinedCount;

    public NewExpression(Rule rule, ParseNode token, List<Expression> arguments, List<Expression> arraySizes) {
        super(rule, Type.UNDEFINED, token);
        this.typeName = token.getText();
        this.arguments = arguments;
        this.arrayDims = arraySizes;
        this.arrayDimCount = arraySizes.size();
        // we check this at bind time and throw a TypeError if it is invalid
        this.arrayDimDefinedCount = 0;
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

        // repeat for the array size expressions

        iterator = arrayDims.iterator();

        while (iterator.hasNext()) {
            Expression expr = iterator.next();
            if (expr !=  null)  {
                expr.bind();
                arrayDimDefinedCount++;
            }
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
            throw new TypeException("NewExpression.typeCheck : unknown type " + typeName + getPos());
        }

        if (type.isObject() && arrayDimCount == 0) {
            // we need to look for a suitable constructor
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
                    throw new TypeException("NewExpression.typeCheck : invalid constructor for target class " + typeName + getPos());
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
                throw new TypeException("NewExpression.typeCheck : invalid constructor for target class " + typeName + getPos());
            }

            if (candidates.size() > 1) {
                throw new TypeException("NewExpression.typeCheck : ambiguous constructor signature for target class " + typeName + getPos());
            }

            constructor = candidates.get(0);

            // make sure we know the formal parameter types and have included them in the typegroup

            paramTypes = new ArrayList<Type>();
            Class<?>[] paramClasses = constructor.getParameterTypes();

            for (int i = 0; i < arguments.size() ; i++) {
                paramTypes.add(typeGroup.ensureType(paramClasses[i]));
            }
        } else if (arrayDimCount == 0) {
            // if we have a primitive type then have to have some array dimensions
            throw new TypeException("NewExpression.typeCheck : invalid type for new operation " + getPos());
        }
        // if this is a new array operation we must have at least one defined dimension and we cannot have
        // more dimensions than we can fit into a byte

        if (arrayDimCount > 0 && arrayDimDefinedCount == 0) {
            throw new TypeException("NewExpression.typeCheck : array dimension missing " + getPos());
        }

        if (arrayDimCount > Byte.MAX_VALUE) {
            throw new TypeException("NewExpression.typeCheck : too many array dimensions " + getPos());
        }
        // if we have any array dimension sizings then ensure they all type check as integer expressions

        for (int i = 0; i < arrayDimCount ; i++) {
            if (i < arrayDimDefinedCount) {
                Expression expr = arrayDims.get(i);
                expr.typeCheck(Type.I);
            }
            // replace the current type with the corresponding array type
            type = typeGroup.createArray(type);
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
        if (arrayDimCount == 0) {
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
                throw new ExecuteException("NewExpression.interpret : unable to instantiate class " + typeName + getPos(), e);
            } catch (IllegalAccessException e) {
                throw new ExecuteException("NewExpression.interpret : unable to access class " + typeName + getPos(), e);
            } catch (InvocationTargetException e) {
                throw new ExecuteException("NewExpression.interpret : unable to invoke constructor for class " + typeName + getPos(), e);
            }
        } else {
            int[] dims = new int[arrayDimDefinedCount];
            Type componentType = type;
            for (int i = 0; i < arrayDimDefinedCount; i++) {
                Expression dim = arrayDims.get(i);
                int dimValue = (Integer)dim.interpret(helper);
                dims[i] = dimValue;
                componentType = componentType.getBaseType();
            }
            try {
                Object result = Array.newInstance(componentType.getTargetClass(), dims);
                return result;
            } catch (IllegalArgumentException e) {
                throw new ExecuteException("NewExpression.interpret : unable to instantiate array " + typeName + getPos(), e);
            } catch (NegativeArraySizeException e) {
                // should never happen
                throw new ExecuteException("NewExpression.interpret : unable to instantiate array " + typeName + getPos(), e);
            }
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        int currentStack = compileContext.getStackCount();
        int expected = 1;
        int extraParams = 0;

        if (arrayDimCount ==  0) {
            // ok, we need to create the new instance and then initialise it.

            // create the new instance -- adds 1 to stack
            String instantiatedClassName = type.getInternalName();
            mv.visitTypeInsn(Opcodes.NEW, instantiatedClassName);
            compileContext.addStackCount(1);
            // copy the exception so we can init it
            mv.visitInsn(Opcodes.DUP);
            compileContext.addStackCount(1);

            int argCount = arguments.size();

            // stack each of the arguments to the constructor
            for (int i = 0; i < argCount; i++) {
                Type argType = argumentTypes.get(i);
                Type paramType = paramTypes.get(i);
                int paramCount = (paramType.getNBytes() > 4 ? 2 : 1);

                // track extra storage used after type conversion
                extraParams += (paramCount);
                arguments.get(i).compile(mv, compileContext);
                compileTypeConversion(argType, paramType, mv, compileContext);
            }

            // construct the exception
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, instantiatedClassName, "<init>", getDescriptor());

            // modify the stack height to account for the removed exception and params
            compileContext.addStackCount(-(extraParams+1));
        } else {
            // TODO !!! implement compilation for array types !!!
            if (arrayDimCount == 1) {
                // we can use a NEWARRAY or ANEWARRAY
                Type baseType = type.getBaseType();
                // compile first array dimension adds 1 to stack
                arrayDims.get(0).compile(mv, compileContext);
                // compile new array op -- pops 1 and adds 1 to stack
                if (baseType.isObject()) {
                    mv.visitTypeInsn(Opcodes.ANEWARRAY, baseType.getInternalName());
                // } else if (baseType.isArray()) {  // cannot happen!!!
                } else {
                    int operand = 0;
                    if (baseType.equals(Type.Z)) {
                        operand = Opcodes.T_BOOLEAN;
                    } else if (baseType.equals(Type.B)) {
                        operand = Opcodes.T_BYTE;
                    } else if (baseType.equals(Type.S)) {
                        operand = Opcodes.T_SHORT;
                    } else if (baseType.equals(Type.C)) {
                        operand = Opcodes.T_CHAR;
                    } else if (baseType.equals(Type.I)) {
                        operand = Opcodes.T_INT;
                    } else if (baseType.equals(Type.J)) {
                        operand = Opcodes.T_LONG;
                    } else if (baseType.equals(Type.F)) {
                        operand = Opcodes.T_FLOAT;
                    } else if (baseType.equals(Type.D)) {
                        operand = Opcodes.T_DOUBLE;
                    }
                    mv.visitIntInsn(Opcodes.NEWARRAY, operand);
                }
            } else {
                // we need to use MULTIANEWARRAY

                for (int i = 0; i < arrayDimDefinedCount; i++) {
                    // compile next array dimension adds 1 to stack
                    arrayDims.get(i).compile(mv, compileContext);
                }
                // execute the MULTIANEWARRAY operation -- pops arrayDims operands and pushes 1
                mv.visitMultiANewArrayInsn(type.getInternalName(), arrayDimDefinedCount);
                compileContext.addStackCount(1 - arrayDimDefinedCount);
            }
        }

        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("NewExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expected));
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