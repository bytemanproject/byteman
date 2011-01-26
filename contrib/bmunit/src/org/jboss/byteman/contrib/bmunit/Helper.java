/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
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
 */
package org.jboss.byteman.contrib.bmunit;

import java.util.ArrayList;

/**
 * Common helper methods used by the bmunit package
 * @author Scott stark (sstark@redhat.com) (C) 2011 Red Hat Inc.
 * @version $Revision:$
 */
public class Helper {
   /**
    * Build a property array of the system properties that should be passed to an agent
    * Install ctor.
    *
    * @param config - the unit test configuration annotation
    * @return an array of system property names and their values in the form of key=value pairs.
    */
   public static String[] getProperties(BMUnitConfig config) {
       // Get properties from the config
       ArrayList<String> configProperties = new ArrayList<String>();
       String host = getAgentHost(config);
       int port = getAgentPort(config);
      String home = getBytemanHome(config);
      boolean bmunitVerbose = isBmunitVerbose(config);
      boolean bytemanVerbose = isBytemanVerbose(config);

      if(host.length() > 0)
         configProperties.add("org.jboss.byteman.contrib.bmunit.agent.host="+host);
      if(port != -1)
         configProperties.add("org.jboss.byteman.contrib.bmunit.agent.port="+port);
      if(home.length() > 0)
         configProperties.add("org.jboss.byteman.home="+home);
      configProperties.add("org.jboss.byteman.contrib.bmunit.verbose="+bmunitVerbose);
      configProperties.add("org.jboss.byteman.verbose="+bytemanVerbose);

      String[] properties = new String[configProperties.size()];
      configProperties.toArray(properties);
      return properties;
   }

   /**
    * Determine the agent host value to use
    * @param config
    * @return the agent host value
    */
   static String getAgentHost(BMUnitConfig config) {
      String value = "";
      String sysPropValue = SecurityActions.getSystemProperty(BMUnit.AGENT_HOST, "");
      String configValue = config.agentHost();
      // First try system property if preferred
      if(config.preferSystemProperties())
         value = sysPropValue;
      // Next try config property
      if(value.length() == 0 && configValue.length() > 0)
         value = configValue;
      else if(value.length() == 0 && sysPropValue.length() > 0)
         value = sysPropValue;
      return value;
   }
   /**
    * Determine the agent host value to use
    * @param config
    * @return the agent port value
    */
   static int getAgentPort(BMUnitConfig config) {
      int value = -1;
      String sysPropValue = SecurityActions.getSystemProperty(BMUnit.AGENT_PORT, "-1");
      int configValue = config.agentPort();
      // First try system property if preferred
      if(config.preferSystemProperties())
         value = Integer.valueOf(sysPropValue);
      // Next try config property
      if(value == -1 && configValue > 0)
         value = configValue;
      else if(value == -1 && sysPropValue.length() > 0)
         value = Integer.valueOf(sysPropValue);
      return value;
   }
   /**
    * Determine the byteman agent jar home location
    * org.jboss.byteman.home
    * @param config
    * @return the agent host value
    */
   static String getBytemanHome(BMUnitConfig config) {
      String value = "";
      String sysPropValue = SecurityActions.getSystemProperty("org.jboss.byteman.home", "");
      String configValue = config.bytemanHome();
      // First try system property if preferred
      if(config.preferSystemProperties())
         value = sysPropValue;
      // Next try config property
      if(value.length() == 0 && configValue.length() > 0)
         value = configValue;
      else if(value.length() == 0 && sysPropValue.length() > 0)
         value = sysPropValue;
      return value;
   }
   static boolean isBmunitVerbose(BMUnitConfig config) {
      boolean value = false;
      String sysPropValue = SecurityActions.getSystemProperty(BMUnit.VERBOSE, "false");
      boolean configValue = config.isBmunitVerbose();
      // First try system property if preferred
      if(config.preferSystemProperties())
         value = Boolean.valueOf(sysPropValue);
      // Next use config property
      else
         value = configValue;
      return value;
   }

   /**
    * org.jboss.byteman.verbose
    * @param config
    * @return
    */
   static boolean isBytemanVerbose(BMUnitConfig config) {
      boolean value = false;
      String sysPropValue = SecurityActions.getSystemProperty("org.jboss.byteman.verbose", "false");
      boolean configValue = config.isBytemanVerbose();
      // First try system property if preferred
      if(config.preferSystemProperties())
         value = Boolean.valueOf(sysPropValue);
      // Next use config property
      else
         value = configValue;
      return value;
   }
}
