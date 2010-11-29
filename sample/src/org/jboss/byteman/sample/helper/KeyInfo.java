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
package org.jboss.byteman.sample.helper;

/**
 * Auxiliary class used by the rule set to provide information about which counters the rule system
 * will be updating and how it wants them to be presented in the JMX bean display
 */

public class KeyInfo
{
    /**
     * type value identifying a counter whose value should be treated as a running total. the
     * value is displayed as an int.
     */
    public final static int KEY_TYPE_CUMULATIVE = 0;
    /**
     * type value identifying a counter whose value should be used to compute a rate by dividing
     * the sampled count by the time in seconds over which the sample was obtained. the value is
     * displayed as a float.
     */
    public final static int KEY_TYPE_RATE = 1;
    /**
     * type value identifying a counter whose value should be used to compute a running average by
     * dividing the running total for the last N samples by N where N is 5. the value is displayed
     * as a float.
     */
    public final static int KEY_TYPE_MEAN = 2;

    public KeyInfo()
    {
        this(null, null, null, null);
    }

    public KeyInfo(String label)
    {
        this(label, null, null, null);
    }

    public KeyInfo(String label, String[] keyNames)
    {
        this(label, keyNames, null, null);
    }

    public KeyInfo(String label, String[] keyNames, String[] keyLabels)
    {
        this(label, keyNames, null, keyLabels);
    }

    public KeyInfo(String label, String[] keyNames, int[] keyTypes)
    {
        this(label, keyNames, keyTypes, null);
    }

    public KeyInfo(String label, String[] keyNames, int keyTypes[], String keyLabels[])
    {
        if (label == null) {
            label = "Byteman Periodic Statistics";
        }
        this.label = label;

        if (keyNames == null) {
            this.keyNames = new String[0];
        } else {
            this.keyNames = keyNames.clone();
        }

        keyCount = this.keyNames.length;

        if (keyTypes != null && keyTypes.length != keyCount) {
            throw new IllegalArgumentException("KeyInfo.KeyInfo() : Key types size must match key names size");
        } else if (keyTypes == null) {
            this.keyTypes = new int[keyCount];
            for (int i = 0; i < keyCount; i++) {
                // by default we just sum values
                this.keyTypes[i] = KEY_TYPE_CUMULATIVE;
            }
        } else {
            this.keyTypes = keyTypes.clone();
        }
        
        if (keyLabels != null && keyLabels.length != keyCount) {
            throw new IllegalArgumentException("KeyInfo.KeyInfo() : Key labels size must match key names size");
        } else if (keyLabels == null) {
            this.keyLabels = new String[keyCount];
            for (int i = 0; i < keyCount; i++) {
                // we use the key name as the label
                this.keyLabels[i] = keyNames[i];
            }
        } else {
            this.keyLabels = keyLabels.clone();
        }
    }

    public String getLabel() {
        return label;
    }

    public int getKeyCount() {
        return keyCount;
    }

    public String[] getKeyNames() {
        return keyNames;
    }

    public int[] getKeyTypes() {
        return keyTypes;
    }

    public String[] getKeyLabels() {
        return keyLabels;
    }

    public void addKey(String keyName)
    {
        addKey(keyName, KEY_TYPE_CUMULATIVE, null);
    }

    public KeyInfo addKey(String keyName, int keyType)
    {
        return addKey(keyName, keyType, null);
    }
    
    public KeyInfo addKey(String keyName, int keyType, String keyLabel)
    {
        // we cannot allow a null name
        if (keyName == null) {
            throw new IllegalArgumentException("KeyInfo.addKey() : Key name must no tbe null");
        }

        if (keyType < KEY_TYPE_CUMULATIVE || keyType > KEY_TYPE_MEAN) {
            throw new IllegalArgumentException("KeyInfo.addKey() : Key type out of range");
        }

        // if the label is not provided use the key name as the label

        if (keyLabel == null) {
            keyLabel = keyName;
        }

        String[] newKeyNames = new String[keyCount + 1];
        int[] newKeyTypes = new int[keyCount + 1];
        String[] newKeyLabels = new String[keyCount + 1];
        System.arraycopy(keyNames, 0, newKeyNames, 0, keyCount);
        System.arraycopy(keyTypes, 0, newKeyTypes, 0, keyCount);
        System.arraycopy(keyLabels, 0, newKeyLabels, 0, keyCount);
        keyNames = newKeyNames;
        keyTypes = newKeyTypes;
        keyLabels = newKeyLabels;
        keyNames[keyCount] = keyName;
        keyTypes[keyCount] = keyType;
        keyLabels[keyCount] = keyLabel;
        keyCount++;

        return this;
    }

    /**
     * A label for the collection of stats to use in the JMX bean display
     */
    private String label;
    /**
     * how many keys there are
     */
    private int keyCount;
    /**
     * the keys for each of the counters being sampled
     */
    private String keyNames[];
    /**
     * the type of information each sampled counter is being used to collect
     */
    private int keyTypes[];

    /**
     * the keys for each of the counters being sampled
     */
    private String keyLabels[];
}
