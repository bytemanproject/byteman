/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat and individual contributors
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

import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * Clas modelling a class literal of the form foo.bar.baz.Mumble.class
 */
public class ClassLiteralExpression extends Expression
{
    public ClassLiteralExpression(Rule rule, Type type, ParseNode pathTree, String[] pathList) {
        // we cannot process the pathlist until typecheck time
        super(rule, type, pathTree);
        this.pathList = pathList;
        this.ownerType = null;
        this.classIndex = -1;
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
        // nothing to bind
    }

    public Type typeCheck(Type expected) throws TypeException {
        // we have a putative reference to a class of the form
        // foo.bar.baz.Mumble.class
        TypeGroup typeGroup = getTypeGroup();
        ownerType = typeGroup.create(getPath(pathList.length));
        if (ownerType == null || ownerType.getTargetClass() == null) {
            throw new TypeException("FieldExpression.typeCheck : invalid class literal " + getPath(pathList.length) + ".class" + getPos());
        }
        type = typeGroup.ensureType(Class.class);

        return type;
    }

    public Object interpret(HelperAdapter helper) throws ExecuteException
    {
        return ownerType.getTargetClass();
    }

    public void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException
    {
        // make sure we are at the right source line
        compileContext.notifySourceLine(line);

        int currentStack = compileContext.getStackCount();
        int expected = 1;

        // TODO -- this is only ok if the class is accessible
        // we will need to use a helper method for non-accessible classes
        mv.visitLdcInsn(org.objectweb.asm.Type.getType(ownerType.getTargetClass()));
        // we added one object
        compileContext.addStackCount(1);
        // check the stack height is ok
        if (compileContext.getStackCount() != currentStack + expected) {
            throw new CompileException("ClassLiteralExpression.compile : invalid stack height " + compileContext.getStackCount() + " expecting " + (currentStack + expected));
        }
    }

    public String getPath(int len)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(pathList[0]);

        for (int i = 1; i < len; i++) {
            buffer.append(".");
            buffer.append(pathList[i]);
        }
        return buffer.toString();
    }

    public void writeTo(StringWriter stringWriter) {
        // we normally have a owner expression but before binding we have a path
        String sepr = "";
        for (String field : pathList) {
            stringWriter.write(sepr);
            stringWriter.write(field);
            sepr =".";
        }
    }

    private String[] pathList;
    private Type ownerType;
    private Field field;

    /**
     * true if this is a public class otherwise false
     */
    private boolean isPublicClass;
    /**
     * index used by compiled code when referring to a non-public class.
     */
    private int classIndex;

}