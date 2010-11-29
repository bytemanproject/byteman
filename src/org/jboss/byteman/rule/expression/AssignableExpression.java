/*
* JBoss, Home of Professional Open Source
* Copyright 2010 Red Hat and individual contributors
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

/**
 * an expression which can appear on the left hand side of an assignment expression as well as in any
 * other expression context. assignable expressions provide extra methods which support assignment,
 * either interpreted or compiled, on top of the usual evaluation methods.
 */
public abstract class AssignableExpression extends Expression
{
    /**
     * Create a new expression.
     *
     * @param type the current type for this expression.
     */
    protected AssignableExpression(Rule rule, Type type, ParseNode token) {
        super(rule, type, token);
    }

    /**
     * execute an assignment to the referenced location by interpretation of the expression,
     * using the object passed in this call
     * @param helperAdapter an execution context associated with the rule which contains a map of
     * current bindings for rule variables and another map of their declared types both of which
     * are indexed by variable name. This includes entries for the helper (name "-1"), the
     * recipient if the trigger method is not static (name "0") and the trigger method arguments
     * (names "1", ...)
     * @return  the result of evaluation as an Object
     * @throws org.jboss.byteman.rule.exception.ExecuteException
     */
    public abstract Object interpretAssign(HelperAdapter helperAdapter, Object value) throws ExecuteException;

    /**
     * compile an assignment to the referenced location using the value on the top of the
     * Java stack.

     * @param mv
     * @param compileContext
     * @throws CompileException
     */
    public abstract void compileAssign(MethodVisitor mv, CompileContext compileContext) throws CompileException;

    /**
     * bind as an assignable expression. for variables and dollar expressions this will ensure that a binding exists
     * and that it is marked as potentially updateable.
     * @return true if all bindings are valid and false if the expression contains an invalid or
     * unassignable reference
     */
    public abstract void bindAssign() throws TypeException;
}
