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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.StringWriter;

/**
 * an expression which identifies a null value
 */
public class NullLiteral extends Expression
{
    public NullLiteral(Rule rule, ParseNode token)
    {
        super(rule, Type.UNDEFINED, token);
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public void bind()
    {
        // nothing to do
    }

    public Type typeCheck(Type expected) throws TypeException {
        if (expected.isPrimitive()) {
            type = Type.boxType(expected);
        } else {
            type = expected;
            if (type.isUndefined()) {
                throw new TypeException("NullLiteral.typeCheck : unable to derive type for null from context");
            }
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        return null;
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        // load null or zero

        mv.visitInsn(Opcodes.ACONST_NULL);

        compileContext.addStackCount(1);
    }

    public void writeTo(StringWriter stringWriter) {
        stringWriter.write("null");
    }
}