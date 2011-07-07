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
import org.objectweb.asm.Label;

/**
 * A binary string concatenation operator expression
 */
public class StringPlusExpression extends BinaryOperExpression
{
    public StringPlusExpression(Rule rule, ParseNode token, Expression left, Expression right)
    {
        super(rule, PLUS, Type.STRING, token, left, right);
    }

    public Type typeCheck(Type expected) throws TypeException {
        // first type must be a string -- second may be anything but expect
        // a string to indicate that it must be assignable evn if only by conversion
        Type type1 = getOperand(0).typeCheck(Type.STRING);
        Type type2 = getOperand(1).typeCheck(Type.STRING);
        // result will always be a String
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(Type.STRING)) {
            throw new TypeException("StringPlusExpression.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        Object value1 = getOperand(0).interpret(helper);
        Object value2 = getOperand(1).interpret(helper);
        String string1 = value1.toString();
        String string2 = (value2 == null ? "null" : value2.toString());
        return string1 + string2;
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        Expression oper0 = getOperand(0);
        Expression oper1 = getOperand(1);

        int currentStack = compileContext.getStackCount();
        int expected = 1;

        // compile and type convert each operand n.b. the type conversion will ensure that
        // null operands are replaced with "null"
        
        oper0.compile(mv, compileContext);
        compileTypeConversion(oper0.getType(), type, mv, compileContext);
        oper1.compile(mv, compileContext);
        compileTypeConversion(oper1.getType(), type, mv, compileContext);

        // ok, we could optimize this for the case where the left or right operand is a String plus expression
        // by employing a StringBuffer but for now we will just evaluate the left and right operand and
        // then call concat to join them
        // add two strings leaving one string

        // if second operand is null replace it with "null"
        Label skiptarget = new Label();
        // this adds a word then removes it -- do so to ensure the max height gets updated if need be
        mv.visitInsn(Opcodes.DUP);
        compileContext.addStackCount(1);
        // if it is not null we skip to the concat operation
        mv.visitJumpInsn(Opcodes.IFNONNULL, skiptarget);
        compileContext.addStackCount(-1);
        // it's null so we have to swap it fdr "null"
        mv.visitInsn(Opcodes.POP);
        mv.visitLdcInsn("null");
        mv.visitLabel(skiptarget);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");

        compileContext.addStackCount(-1);
        
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("StringPlusExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + currentStack + expected);
        }
    }
}