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
package org.jboss.byteman.rule.exception;

/**
 * Specialization of ExecuteException which is used to cause a trigger method to return
 * early the trigger point, possibly supplying an object to be returned. This is used
 * to implement the RETURN action
 *
 */
public class EarlyReturnException extends ExecuteException
{
    public EarlyReturnException(String message) {
        super(message);
        this.returnValue = null;
    }

    public EarlyReturnException(String message, Throwable th) {
        super(message, th);
        this.returnValue = null;
    }

    public EarlyReturnException(String message, Object returnValue) {
        super(message);
        this.returnValue = returnValue;
    }

    public EarlyReturnException(String message, Throwable th, Object returnValue) {
        super(message, th);
        this.returnValue = returnValue;
    }

    public Object getReturnValue()
    {
        return returnValue;
    }

    private Object returnValue;
}
