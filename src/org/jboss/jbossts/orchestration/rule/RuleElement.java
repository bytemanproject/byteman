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
package org.jboss.jbossts.orchestration.rule;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;

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

    public String toString()
    {
        StringWriter stringWriter = new StringWriter();
        writeTo(stringWriter);
        return stringWriter.toString();
    }

    public abstract void writeTo(StringWriter stringWriter);
}
