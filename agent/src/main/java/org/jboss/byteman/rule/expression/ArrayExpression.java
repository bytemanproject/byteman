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
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.jboss.byteman.rule.grammar.ParseNode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Iterator;
import java.io.StringWriter;
import java.lang.reflect.Array;

/**
 * an expression which identifies an array reference.
 */

public class ArrayExpression extends AssignableExpression
{

    public ArrayExpression(Rule rule, Type type, ParseNode token, Expression arrayRef, List<Expression> idxList)
    {
        super(rule, type, token);
        this.arrayRef = arrayRef;
        this.idxList = idxList;
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
        // we  have to make sure that any names occuring in the array reference are bound
        // and that the index expressions contain valid bindings
        arrayRef.bind();

        Iterator<Expression> iterator = idxList.iterator();

        while (iterator.hasNext()) {
            iterator.next().bind();
        }
    }

    public Type typeCheck(Type expected) throws TypeException {
        typeCheckAny();
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("ArrayExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }

        return type;
    }

    public Type typeCheckAssign(Type expected) throws TypeException
    {
        typeCheckAny();
        if (Type.dereference(expected).isDefined() && !type.isAssignableFrom(expected)) {
            throw new TypeException("ArrayExpression.typeCheckAssign : invalid value type " + expected.getName() + " for array assignment " + getPos());
        }

        return type;
    }

    private void typeCheckAny() throws TypeException
    {
        Type arrayType = arrayRef.typeCheck(Type.UNDEFINED);
        Type nextType = arrayType;
        for (Expression expr : idxList) {
            if (!nextType.isArray()) {
                throw new TypeException("ArrayExpression.typeCheck : invalid type for array dereference " + nextType.getName() + getPos());
            }
            nextType = nextType.getBaseType();
            expr.typeCheck(Type.N);
        }
        type = nextType;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        // evaluate the array expression then evaluate each index expression in turn and
        // dereference to access the array element

        try {
            Object value = arrayRef.interpret(helper);
            Type nextType = arrayRef.getType();
            for (Expression expr : idxList) {
                int idx = ((Number) expr.interpret(helper)).intValue();
                if (value == null) {
                    throw new ExecuteException("ArrayExpression.interpret : attempted array indirection through null value " + arrayRef.token.getText() + getPos());
                }
                value = Array.get(value, idx);
                nextType = nextType.getBaseType();
            }

            return value;
        } catch (ExecuteException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new ExecuteException("ArrayExpression.interpret : failed to evaluate expression " + arrayRef.token.getText() + getPos(), e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ExecuteException("ArrayExpression.interpret : invalid index for array " + arrayRef.token.getText() + getPos(), e);
        } catch (ClassCastException e) {
            throw new ExecuteException("ArrayExpression.interpret : invalid index dereferencing array " + arrayRef.token.getText() + getPos(), e);
        } catch (Exception e) {
            throw new ExecuteException("ArrayExpression.interpret : unexpected exception dereferencing array " + arrayRef.token.getText() + getPos(), e);
        }
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Type valueType = arrayRef.getType().getBaseType();
        int currentStack = compileContext.getStackCount();
        int expected = 0;

        // compile load of array reference -- adds 1 to stack height
        arrayRef.compile(mv, compileContext);
        // for each index expression compile the expression and the do an array load
        Iterator<Expression> iterator = idxList.iterator();

        while (iterator.hasNext()) {
            Expression idxExpr = iterator.next();
            // compile expression index -- adds 1 to height
            idxExpr.compile(mv, compileContext);
            // make sure the index is an integer
            compileTypeConversion(idxExpr.getType(), Type.I, mv, compileContext);

            if (valueType.isObject() || valueType.isArray()) {
                // compile load object - pops 2 and adds 1
                mv.visitInsn(Opcodes.AALOAD);
                expected = 1;
            } else if (valueType == Type.Z || valueType == Type.B) {
                // compile load byte - pops 2 and adds 1
                mv.visitInsn(Opcodes.BALOAD);
                expected = 1;
            } else if (valueType == Type.S) {
                // compile load short - pops 2 and adds 1
                mv.visitInsn(Opcodes.SALOAD);
                expected = 1;
            } else if (valueType == Type.C) {
                // compile load char - pops 2 and adds 1
                mv.visitInsn(Opcodes.CALOAD);
                expected = 1;
            } else if (valueType == Type.I) {
                // compile load int - pops 2 and adds 1
                mv.visitInsn(Opcodes.IALOAD);
                expected = 1;
            } else if (valueType == Type.J) {
                // compile load long - pops 2 and adds 2
                mv.visitInsn(Opcodes.LALOAD);
                expected = 2;
            } else if (valueType == Type.F) {
                // compile load float - pops 2 and adds 1
                mv.visitInsn(Opcodes.FALOAD);
                expected = 1;
            } else if (valueType == Type.D) {
                // compile load double - pops 2 and adds 2
                mv.visitInsn(Opcodes.DALOAD);
                expected = 2;
            }
            compileContext.addStackCount(expected - 2);
            if (iterator.hasNext()) {
                assert valueType.isArray();
                valueType =valueType.getBaseType();
            }
        }

        // check stack height
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("ArrayExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expected));
        }

        // we needed room for an aray and an index or for a one or two word result
        // but the recursive evaluations will have made sure the max stack is big enough
        // so there is no need to update the maximum stack height
    }

    public void writeTo(StringWriter stringWriter) {
        arrayRef.writeTo(stringWriter);
        for (Expression expr : idxList) {
            stringWriter.write("[");
            expr.writeTo(stringWriter);
            stringWriter.write("]");
        }
    }

    Expression arrayRef;
    List<Expression> idxList;

    @Override
    public Object interpretAssign(HelperAdapter helper, Object value) throws ExecuteException {
        // evaluate the array expression then evaluate each index expression in turn up to the last one
        // and dereference to access the array at that index. finally evaluate the final index expression
        // and use it as the position at which to install the supplied value in the dereferenced array

        try {
            Object array = arrayRef.interpret(helper);
            Type nextType = arrayRef.getType();
            int count = idxList.size() - 1;
            for (Expression expr : idxList) {
                int idx = ((Number) expr.interpret(helper)).intValue();
                if (array == null) {
                    throw new ExecuteException("ArrayExpression.interpret : attempted array indirection through null value " + arrayRef.token.getText() + getPos());
                }
                if (count-- >  0)  {
                    array = Array.get(array, idx);
                    nextType = nextType.getBaseType();
                } else {
                    Array.set(array, idx, value);
                }
            }
            return value;
        } catch (ExecuteException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new ExecuteException("ArrayExpression.interpret : failed to evaluate expression " + arrayRef.token.getText() + getPos(), e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ExecuteException("ArrayExpression.interpret : invalid index for array " + arrayRef.token.getText() + getPos(), e);
        } catch (ClassCastException e) {
            throw new ExecuteException("ArrayExpression.interpret : invalid index dereferencing array " + arrayRef.token.getText() + getPos(), e);
        } catch (Exception e) {
            throw new ExecuteException("ArrayExpression.interpret : unexpected exception dereferencing array " + arrayRef.token.getText() + getPos(), e);
        }
    }

    @Override
    public void compileAssign(MethodVisitor mv, CompileContext compileContext) throws CompileException {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Type valueType = arrayRef.getType().getBaseType();
        int currentStack = compileContext.getStackCount();
        boolean isTwoWords = (valueType.getNBytes() > 4);
        int toPop = 0;
        int size = (isTwoWords ? 2 : 1);

        // value to be assigned is TOS and will already be coerced to the correct value type
        // copy it so we can install the copy and leave the original as a a return value on the stack
        if (isTwoWords) {
            // [... val1 val2 ==> ... val1 val2 val1 val2]
            mv.visitInsn(Opcodes.DUP2);
        } else {
            // [... val ==> ... val val]
            mv.visitInsn(Opcodes.DUP);
        }
        compileContext.addStackCount(size);

        // compile load of array reference -- adds 1 to stack height
        arrayRef.compile(mv, compileContext);
        // for each index expression compile the expression and the do an array load
        Iterator<Expression> iterator = idxList.iterator();

        while (iterator.hasNext()) {
            Expression idxExpr = iterator.next();
            if (iterator.hasNext()) {
                // dereference the array to get an embedded array
                // compile expression index -- adds 1 to height
                idxExpr.compile(mv, compileContext);
                // make sure the index is an integer
                compileTypeConversion(idxExpr.getType(), Type.I, mv, compileContext);
                // fetch embedded array pop 2 and add 1
                mv.visitInsn(Opcodes.AALOAD);
                compileContext.addStackCount(-1);
                valueType = valueType.getBaseType();
            } else {
                if (isTwoWords) {
                    // stack is [..., val1, val2, val1, val2, aref ] and we want [..., val1, val2, aref, val1, val2 ]
                    mv.visitInsn(Opcodes.DUP_X2);     // ==>  [..., val1, val2, aref. val1, val2, aref ]
                    compileContext.addStackCount(1);
                    mv.visitInsn(Opcodes.POP);        // ==> [..., val1, val2, aref. val1, val2 ]
                    compileContext.addStackCount(-1);
                } else {
                    // stack is [..., val, val, aref ] and we want [..., val, aref, val ]
                    mv.visitInsn(Opcodes.SWAP);
                }
                // compile expression index -- adds 1 to height
                idxExpr.compile(mv, compileContext);
                // make sure the index is an integer
                compileTypeConversion(idxExpr.getType(), Type.I, mv, compileContext);
                if (isTwoWords) {
                    // stack is [..., val1, val2, aref, val1, val2, idx] and we want [..., val1, val2, aref, idx, val1, val2 ]
                    mv.visitInsn(Opcodes.DUP_X2);     // ==> [..., val1, val2, aref, idx, val1, val2, idx]
                    compileContext.addStackCount(1);
                    mv.visitInsn(Opcodes.POP);        // ==> [..., val1, val2, aref, idx, val1, val2 ]
                    compileContext.addStackCount(-1);
                } else {
                    // stack is [..., val, aref, val, idx] and we want [..., val, aref, idx, val ]
                    mv.visitInsn(Opcodes.SWAP);
                }
                // now we can do the array store
                if (valueType.isObject() || valueType.isArray()) {
                    // compile load object - pops 3
                    mv.visitInsn(Opcodes.AASTORE);
                    toPop =- 3;
                } else if (valueType == Type.Z || valueType == Type.B) {
                    // compile load byte - pops 3
                    mv.visitInsn(Opcodes.BASTORE);
                    toPop = -3;
                } else if (valueType == Type.S) {
                    // compile load short - pops 3
                    mv.visitInsn(Opcodes.SASTORE);
                    toPop = -3;
                } else if (valueType == Type.C) {
                    // compile load char - pops 3
                    mv.visitInsn(Opcodes.CASTORE);
                    toPop = -3;
                } else if (valueType == Type.I) {
                    // compile load int - pops 3
                    mv.visitInsn(Opcodes.IASTORE);
                    toPop = -3;
                } else if (valueType == Type.J) {
                    // compile load long - pops 4
                    mv.visitInsn(Opcodes.LASTORE);
                    toPop = -4;
                } else if (valueType == Type.F) {
                    // compile load float - pops 3
                    mv.visitInsn(Opcodes.FASTORE);
                    toPop = -3;
                } else if (valueType == Type.D) {
                    // compile load double - pops 4
                    mv.visitInsn(Opcodes.DASTORE);
                    toPop = -4;
                }
                compileContext.addStackCount(toPop);
                if (iterator.hasNext()) {
                    assert valueType.isArray();
                    valueType =valueType.getBaseType();
                }
            }
        }

        // check stack height
        if (compileContext.getStackCount() != currentStack) {
            throw new CompileException("ArrayExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack);
        }

        // we needed room for an aray and an index or for a one or two word result
        // but the recursive evaluations will have made sure the max stack is big enough
        // so there is no need to update the maximum stack height
    }

    @Override
    public void bindAssign() throws TypeException {
        bind();
    }
}
