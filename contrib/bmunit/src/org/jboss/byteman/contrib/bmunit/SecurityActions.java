/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
 */
package org.jboss.byteman.contrib.bmunit;


import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Privileged actions for accessing system properties.
 *
 * @author Scott stark (sstark@redhat.com) (C) 2011 Red Hat Inc.
 * @version $Revision:$
 */
public class SecurityActions {


   static String getSystemProperty(final String key, final String defaultValue) {
      if (System.getSecurityManager() == null) {
         return System.getProperty(key, defaultValue);
      }

      return AccessController.doPrivileged(new PrivilegedAction<String>() {

         @Override
         public String run() {
            return System.getProperty(key);
         }
      });
   }

   static void setSystemProperty(final String key, final String value) {
      if (System.getSecurityManager() == null) {
         System.setProperty(key, value);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {

            @Override
            public Void run() {
               System.setProperty(key, value);
               return null;
            }
         });
      }
   }

   static void clearSystemProperty(final String key) {
      if (System.getSecurityManager() == null) {
         System.clearProperty(key);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {

            @Override
            public Void run() {
               System.clearProperty(key);
               return null;
            }
         });
      }
   }
}
