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
 * Specializaton of ExecuteException used to wrap a client exception generated via a rule THROW action.
 * A ThrowException is caught by the injected trigger code and unwrapped so that the client exception
 * can be rethrown from the trigger method.
 */
public class ThrowException extends ExecuteException
{
    private Throwable throwable;

    public ThrowException(Throwable throwable) {
        super("wrapper for exception created in throw expression", throwable);
        this.throwable = throwable;
    }

    public Throwable getThrowable()
    {
        return throwable;
    }
}
