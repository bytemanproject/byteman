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

import java.io.StringWriter;

/**
 */
public class NumericLiteral extends Expression
{

    public NumericLiteral(Rule rule, Type type, ParseNode token) {
        super(rule, type, token);

        this.value = (Number)token.getChild(0);
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
        // no bindings to check
    }

    public Type typeCheck(Type expected) throws TypeException {
        if (!expected.isNumeric() && !expected.isUndefined() && !expected.isVoid() && expected != Type.OBJECT && expected != Type.STRING) {
            throw new TypeException("NumericLiteral.typeCheck : invalid expected type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        return value;
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        if (type == Type.I) {
            int val = value.intValue();
            // compile code to stack int value
            if (val >= -1 && val <= 5) {
                // we can use an iconst instruction
                mv.visitInsn(Opcodes.ICONST_0 + val);
            } else {
                // we have to add an integer constant to the constants pool
                mv.visitLdcInsn(value);
            }
            // we have only added 1 to the stack height

            compileContext.addStackCount(1);
        } else { // type = type.F
            float val = value.floatValue();
            if (val == 0.0) {
                // we can use an fconst instruction
                mv.visitInsn(Opcodes.FCONST_0);
            } else if (val == 1.0) {
                    // we can use an fconst instruction
                    mv.visitInsn(Opcodes.FCONST_1);
            } else if (val == 2.0) {
                    // we can use an fconst instruction
                    mv.visitInsn(Opcodes.FCONST_2);
            } else {
                // we have to add a float constant to the constants pool
                mv.visitLdcInsn(value);
            }

            // we have only added 1 to the stack height

            compileContext.addStackCount(1);
        }
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(value.toString());
    }

    private Number value;
}
