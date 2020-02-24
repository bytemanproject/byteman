/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat and individual contributors
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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InstanceOfExpression  extends BooleanExpression {
    Class<?> testType;
    public InstanceOfExpression(Rule rule, int oper, ParseNode token, Expression left, ClassLiteralExpression right) {
        super(rule, oper, token, left, right);
        this.testType = null;
    }
    @Override
    public Type typeCheck(Type expected) throws TypeException {
        Type type1 = getOperand(0).typeCheck(Type.UNDEFINED);
        Type type2 = getOperand(1).typeCheck(Type.UNDEFINED);
        if (type1 == null || !type1.isObject()) {
            throw new TypeException("InstanceExpression.typeCheck : left hand side of instanceof must be of object type " + getPos());
        }
        if (type2 == null || !type2.isObject() || !(type2.getTargetClass() == Class.class)) {
            throw new TypeException("InstanceExpression.typeCheck : right hand side of instanceof must be a class literal " + getPos());
        }
        testType = (Class<?>)getOperand(1).interpret(null);
        return Type.B;
    }

    @Override
    public Object interpret(HelperAdapter helper) throws ExecuteException {
        Object lhs = getOperand(0).interpret(helper);
        return testType.isInstance(lhs);
    }

    @Override
    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        int currentStack = compileContext.getStackCount();
        int expected = 1;

        // this pushes the instance
        getOperand(0).compile(mv, compileContext);
        // pop the instance and replace with a boolean
        // so no overall change
        mv.visitTypeInsn(Opcodes.INSTANCEOF, Type.internalName(testType));

        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("InstanceOfExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expected));
        }
    }
}
