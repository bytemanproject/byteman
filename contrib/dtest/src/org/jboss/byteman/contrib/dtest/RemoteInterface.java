/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates,
 * and individual contributors as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2010,
 * @author JBoss, by Red Hat.
 */
package org.jboss.byteman.contrib.dtest;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI communication endpoint interface, exposed by the test runner for the
 *  purpose of receiving remote method trace information from code instrumented
 *  by Byteman Rules.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-05
 */
public interface RemoteInterface extends Remote
{
    public void trace(String methodName, Object[] args) throws RemoteException;
}
