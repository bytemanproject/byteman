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

import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.exception.EarlyReturnException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.StringWriter;

/**
 *  A return expression which is used in a rule action to cause a return from the rule trigger
 * method, supplying a return value where appropriate.
 */

public class ReturnExpression extends Expression
{
    private Expression returnValue;

    public ReturnExpression(Rule rule, ParseNode token, Expression returnValue)
    {
        // the trigger method may return any old type but the return expression can only occur
        // at the top level in a rule action seuqence so it is actually a VOID expression

        super(rule, Type.VOID, token);

        this.returnValue = returnValue;
    }
    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public void bind() throws TypeException
    {
        if (returnValue != null) {
            // ensure the return value expression has all its bindings
            returnValue.bind();
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
        // we need to check the returnValue expression against the type of the trigger method
        type = rule.getReturnType();
        if (returnValue == null && !type.isVoid()) {
            throw new TypeException("ReturnExpression.typeCheck : return expression must supply argument when triggered from method with return type " + type.getName() + getPos());
        } else if (returnValue != null) {
            if (type.isVoid()) {
                throw new TypeException("ReturnExpression.typeCheck : return expression must not supply argument when triggered from void method" + getPos());
            }
            returnValue.typeCheck(type);
        }
        return type;
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
    public Object interpret(HelperAdapter helper) throws ExecuteException
    {
        // time to take an early bath -- the code compield into the trigger method should
        // catch this and return as appropriate
        if (returnValue != null) {
            Object value = returnValue.interpret(helper);
            Type subtype = returnValue.type;
            if (type.isNumeric()) {
                // make sure we produce the expected type of numeric
                if (type == Type.C && subtype != Type.C) {
                    // ok, transform Number to a Character
                    int number = ((Number)value).intValue();
                    value = new Character((char)number);
                } else if (subtype == Type.C) {
                    // ok, transform Character to a boxed Numeric if necessary
                    char c = ((Character)value).charValue();
                    if (type == Type.B) {
                        value = new Byte((byte)c);
                    } else if (type == Type.S) {
                        value = new Short((short)c);
                    } else if (type == Type.I) {
                        value = new Integer((int)c);
                    } else if (type == Type.J) {
                        value = new Long((int)c);
                    } else if (type == Type.F) {
                        value = new Float((int)c);
                    } else if (type == Type.D) {
                        value = new Double((int)c);
                    }
                } else {
                    if (type == Type.B && subtype != Type.B) {
                        Number number = (Number)value;
                        value = new Byte(number.byteValue());
                    } else if (type == Type.S && subtype != Type.S) {
                        Number number = (Number)value;
                        value = new Short(number.shortValue());
                    } else if (type == Type.I && subtype != Type.I) {
                        Number number = (Number)value;
                        value = new Integer(number.intValue());
                    } else if (type == Type.J && subtype != Type.J) {
                        Number number = (Number)value;
                        value = new Long(number.longValue());
                    } else if (type == Type.F && subtype != Type.F) {
                        Number number = (Number)value;
                        value = new Float(number.floatValue());
                    } else if (type == Type.D && subtype != Type.D) {
                        Number number = (Number)value;
                        value = new Double(number.doubleValue());
                    }
                }
            }
            throw new EarlyReturnException("return from " + helper.getName(), value);
        } else {
            throw new EarlyReturnException("return from " + helper.getName());
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Type valueType = (returnValue == null ? Type.VOID : returnValue.getType());
        int currentStack = compileContext.getStackCount();
        int expected = 1;

        // ok, we need to create the EarlyReturnException instance and then
        // initialise it using the appropriate return value or null if no
        // return value is needed. strictly we should maybe delay the
        // new until after computing the return expression so we avoid a new
        // if the expression throws an error. but that means we end up doing
        // stack manipulations so lets do it the easy way.

        // create am EarlyReturnException -- adds 1 to stack
        String exceptionClassName = Type.internalName(EarlyReturnException.class);
        mv.visitTypeInsn(Opcodes.NEW, exceptionClassName);
        compileContext.addStackCount(1);
        // copy the exception so we can initialise it -- adds 1 to stack
        mv.visitInsn(Opcodes.DUP);
        compileContext.addStackCount(1);
        // stack a string constant to initialise the exception with -- adds 1 to stack
        mv.visitLdcInsn("return from " + rule.getName());
        compileContext.addStackCount(1);
        // stack any required return value or null -- adds 1 to stack but may use 2 slots
        if (returnValue != null) {
            returnValue.compile(mv, compileContext);
            // we may need to convert from the value type to the return type
            if (valueType != type) {
                compileTypeConversion(valueType, type,  mv, compileContext);
            }
            if (type.isPrimitive()) {
                // we need an object not a primitive
                compileBox(Type.boxType(type), mv, compileContext);
            }
        } else {
            // just push null
            mv.visitInsn(Opcodes.ACONST_NULL);
            compileContext.addStackCount(1);
        }
        // construct the exception -- pops 3
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, exceptionClassName, "<init>", "(Ljava/lang/String;Ljava/lang/Object;)V");
        compileContext.addStackCount(-3);

        // check current stack and increment max stack if necessary
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("ReturnExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expected));
        }

        // now insert the throw instruction and decrement the stack height accordingly
        
        mv.visitInsn(Opcodes.ATHROW);
        compileContext.addStackCount(-1);
    }

    public void writeTo(StringWriter stringWriter) {
        if (returnValue != null) {
            stringWriter.write("RETURN ");
            returnValue.writeTo(stringWriter);
        } else {
            stringWriter.write("RETURN");
        }
    }
}
