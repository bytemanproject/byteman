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
package org.jboss.byteman.rule;

import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.compiler.CompileContext;
import org.jboss.byteman.rule.type.TypeGroup;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.helper.HelperAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;

import java.io.StringWriter;

/**
 * generic class implemented by rule events, conditions and actions which gives them
 * access to the rule context and provides them with common behaviours
 */
public abstract class RuleElement {
    protected RuleElement(Rule rule)
    {
        this.rule = rule;
    }

    protected Rule rule;

    protected TypeGroup getTypeGroup()
    {
        return rule.getTypeGroup();
    }

    protected Bindings getBindings()
    {
        return rule.getBindings();
    }

    public abstract Type typeCheck(Type expected) throws TypeException;

    public abstract Object interpret(HelperAdapter helper) throws ExecuteException;

    public abstract void compile(MethodVisitor mv, CompileContext compileContext) throws CompileException;

    // auxiliary for use by the interpreter when we evaluated an expression
    // to a boxed numeric but need it to be presented as some other boxed numeric
    protected Object rebox(Type fromType, Type toType, Object value)
    {
        // we only ever need to interconvert numerics
        if (!fromType.isNumeric()) {
            return value;
        }
        if (!toType.isNumeric()) {
            return value;
        }
        // replace primitive types with their boxed
        // equivalents since the interpreter always
        // operates in the boxed realm
        if (fromType.isPrimitive()) {
            fromType = Type.boxType(fromType);
        }
        if (toType.isPrimitive()) {
            toType = Type.boxType(toType);
        }
        // no conversion needed if the types are the same
        if (fromType.equals(toType)) {
            return value;
        }
        // characters get special cased because they are not a Number
        if (fromType == Type.CHARACTER) {
            int ival = (int)((Character)value).charValue();
            if (toType == Type.BYTE) {
                return Byte.valueOf((byte)ival);
            } else if (toType == Type.SHORT) {
                return Short.valueOf((short)ival);
            } else if (toType == Type.INTEGER) {
                return Integer.valueOf(ival);
            } else if (toType == Type.LONG) {
                return Long.valueOf((long)ival);
            } else if (toType == Type.FLOAT) {
                return Float.valueOf((float)ival);
            } else if (toType == Type.DOUBLE) {
                return Double.valueOf((double)ival);
            }
            // should not reach here
            return value;
        } else {
            Number num = (Number) value;
            if (toType == Type.BYTE) {
                return Byte.valueOf(num.byteValue());
            } else if (toType == Type.CHARACTER) {
                return Character.valueOf((char)num.intValue());
            } else if (toType == Type.SHORT) {
                return Short.valueOf(num.shortValue());
            } else if (toType == Type.INTEGER) {
                return Integer.valueOf(num.intValue());
            } else if (toType == Type.LONG) {
                return Long.valueOf(num.longValue());
            } else if (toType == Type.FLOAT) {
                return Float.valueOf(num.floatValue());
            } else if (toType == Type.DOUBLE) {
                return Double.valueOf(num.doubleValue());
            }
            // should not reach here
            return value;
        }
    }

    public String toString()
    {
        StringWriter stringWriter = new StringWriter();
        writeTo(stringWriter);
        return stringWriter.toString();
    }

    public abstract void writeTo(StringWriter stringWriter);
}
