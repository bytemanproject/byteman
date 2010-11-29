/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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
package org.jboss.byteman.agent;

import org.jboss.byteman.rule.Rule;

import java.io.PrintWriter;

/**
 * record of a specific bytecode transformation applied by the byteman agent for a given trigger class
 */
public class Transform
{
    private ClassLoader loader;
    private String internalClassName;
    private String triggerMethodName;
    private Rule rule;
    private Throwable throwable;
    private boolean compiled;
    private boolean successful;
    private String detail;

    public Transform(ClassLoader loader, String internalClassName, Rule rule) {
        this(loader, internalClassName, null, rule, null);
    }

    public Transform(ClassLoader loader, String internalClassName, String triggerMethodName, Rule rule, Throwable th) {
        this.loader = loader;
        this.internalClassName = internalClassName;
        this.triggerMethodName = triggerMethodName;
        this.rule = rule;
        this.compiled = false;
        this.throwable = th;
        this.successful = false;
        this.detail = "";
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public String getInternalClassName() {
        return internalClassName;
    }

    public String getTriggerMethodName() {
        return triggerMethodName;
    }

    public Rule getRule() {
        return rule;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getDetail() {
        return detail;
    }

    public void setCompiled(boolean successful, String detail) {
        this.compiled = true;
        this.successful = successful;
        this.detail = detail;
    }

    public boolean isTransformed()
    {
        return (throwable == null);
    }
    
    public boolean isCompiledOk() {
        return compiled && successful;
    }

    public void writeTo(PrintWriter writer)
    {
        writer.print("Transformed in:\n");
        writer.print("loader: ");
        writer.println(loader);
        if (triggerMethodName == null) {
            writer.print("trigger class: ");
            writer.println(internalClassName);
        } else {
            writer.print("trigger method: ");
            writer.print(internalClassName);
            writer.print('.');
            writer.println(triggerMethodName);
        }
        if (throwable != null) {
            writer.print("threw ");
            writer.println(throwable);
            throwable.printStackTrace(writer);
        } else if (compiled) {
            if (successful) {
                writer.println("compiled successfully");
            } else {
                writer.println("failed to compile");
                writer.println(detail);
            }
        }
    }
}
