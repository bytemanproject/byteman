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

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.exception.CompileException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.compiler.StackHeights;
import org.jboss.jbossts.orchestration.rule.helper.HelperAdapter;
import org.jboss.jbossts.orchestration.rule.grammar.ParseNode;
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
    public boolean bind() {
        // no bindings to check
        return true;
    }

    public Type typeCheck(Type expected) throws TypeException {
        if (!expected.isNumeric() && !expected.isUndefined() && !expected.isVoid()) {
            throw new TypeException("NumericLiteral.typeCheck : invalid expected type " + expected.getName() + getPos());            
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        return value;
    }

    public void compile(MethodVisitor mv, StackHeights currentStackHeights, StackHeights maxStackHeights) throws CompileException
    {
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

            currentStackHeights.addStackCount(1);
            if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                maxStackHeights.stackCount = currentStackHeights.stackCount;
            }
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

            currentStackHeights.addStackCount(1);
            if (currentStackHeights.stackCount > maxStackHeights.stackCount) {
                maxStackHeights.stackCount = currentStackHeights.stackCount;
            }
        }
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write(value.toString());
    }

    private Number value;
}
