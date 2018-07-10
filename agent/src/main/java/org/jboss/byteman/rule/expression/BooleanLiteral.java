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

import java.io.StringWriter;

/**
 * A binary logical operator expression
 */
public class BooleanLiteral extends Expression
{
    private boolean value;
    
    public BooleanLiteral(Rule rule, ParseNode token)
    {
        super(rule, Type.Z, token);
        this.value = (Boolean)token.getChild(0) ;
    }


    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     */
    public void bind()
    {
    }

    public Type typeCheck(Type expected) throws TypeException {
        type = Type.Z;
        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("BooleanLiteral.typeCheck : invalid expected result type " + expected.getName() + getPos());
        }
        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException {
        return value;
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        // load a boolean constant
        mv.visitLdcInsn(value);
        
        // increment stack height and update maximmum if necessary
        compileContext.addStackCount(1);
    }

    public void writeTo(StringWriter stringWriter) {
        if (value) {
            stringWriter.write("TRUE");
        } else {
            stringWriter.write("FALSE");
        }
    }
}
