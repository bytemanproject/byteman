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
package org.jboss.byteman.rule.helper;

import org.jboss.byteman.rule.exception.ExecuteException;

/**
 * This interface defines the methods which need to be added to a helper class in order for it
 * to plug in to the rule system. In the case of the default helper class, Helper, this interface
 * is implemented by a pre-defined subclass, InterpretedHelper which interprets the rule parse
 * tree. Given any user-supplied helper class the rule compiler can generate a HelperAdapter class
 * which interprets the rule tree and invokes builtin methods using reflection. The compiler can
 * also generate a HelperAdapter whose bind(), test() and fire() methods are compiled from bytecode
 * derived from the parse trees of, respectively, the rule's event, condition and action. Bytecode
 * compilation is applicable to rules which employ the default helper as well as rules which employ
 * user-defined helpers.
 */
public interface HelperAdapter
{
    public void execute(Object recipient, Object[] args)
            throws ExecuteException;
    public void setBinding(String name, Object value);
    public Object getBinding(String name);
    public String getName();
    public Object getAccessibleField(Object owner, int fieldIndex);
    public void setAccessibleField(Object owner, Object value, int fieldIndex);
    public Object invokeAccessibleMethod(Object target, Object[] args, int fieldIndex);
}
