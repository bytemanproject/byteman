/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

/**
 * enum categorizing types of locations at which rule triggers can be inserted
 */
public enum LocationType
{
    /**
     * specifies the default location for trigger insertion which is either the first line of a method or
     * the first line of a constructor following any indirection via an alternative constructor or via
     * the super constructor.
     *
     * script syntax : 'AT' 'ENTRY'
     */
    ENTRY,
    /**
     * specifies a location for trigger insertion via a line number.
     *
     * script syntax : 'AT' 'LINE' <linenumber>
     */
    LINE,
    /**
     * specifies a location for trigger insertion by identifying a field read operation or the nth such field
     * read if a count is supplied.
     *
     * script syntax : 'AT' 'READ' [<typename> '.' ] <fieldname> [ <count> ]
     */
    READ,
    /**
     * specifies a location for trigger insertion by identifying a field read operation or the nth such field
     * read if a count is supplied.
     *
     * script syntax : 'AFTER' 'READ' [<typename> '.' ] <fieldname> [ <count> ]
     */
    READ_COMPLETED,
    /**
     * specifies a location for trigger insertion by identifying a field write operation or the nth such field
     * write if a count is supplied.
     *
     * script syntax : 'AT' 'WRITE' [<typename> '.' ] <fieldname> [ <count> ]
     */
    WRITE,
    /**
     * specifies a location for trigger insertion by identifying a field write operation or the nth such field
     * write if a count is supplied.
     *
     * script syntax : 'AFTER' 'WRITE' [<typename> '.' ] <fieldname> [ <count> ]
     */
    WRITE_COMPLETED,
    /**
     * specifies a location for trigger insertion by identifying a method invoke operation or the nth such
     * method invoke if a count is supplied.
     *
     * script syntax : 'AT' 'INVOKE' [<typename> '.' ] <methodname> ['(' <argtypes> ')' [ <count> ]
     */
    INVOKE,
    /**
     * specifies a location for trigger insertion by identifying return from a method invoke operation or the
     * nth such return if a count is supplied.
     *
     * script syntax : 'AFTER' 'INVOKE' [<typename> '.' ] <methodname> ['(' <argtypes> ')' [ <count> ]
     */
    INVOKE_COMPLETED,
    /**
     * specifies a location for trigger insertion by identifying a synchronize operation or the nth such
     * operation if a count is supplied.
     *
     * script syntax : 'AT' 'SYNCHRONIZE' [ <count> ]
     */
    SYNCHRONIZE,
    /**
     * specifies a location for trigger insertion by identifying completion of a synchronize operation or the
     * nth such operation if a count is supplied.
     *
     * script syntax : 'AFTER' 'SYNCHRONIZE' [ <count> ]
     */
    SYNCHRONIZE_COMPLETED,

    /**
     * specifies a location for trigger insertion by identifying throw of an exception of the nth such throw
     * if a count is supplied
     * script syntax : 'AT' 'THROW' [<typename>] [ <count> ]
     * n.b. exception typename parsed but not yet implemented
     */
    THROW,

    /**
     * specifies a location for trigger insertion at return from the trigger method
     * script syntax : 'AT' 'RETURN'
     */
    EXIT;

    public String specifierText()
    {
        for (int i = 0; i < specifiers.length; i++) {
            if (types[i] == this) {
                return specifiers[i];
            }
        }
        // hmm, well default to entry
        return specifiers[0];
    }

    public static LocationType type(String locationSpec)
    {
        locationSpec = locationSpec.trim();
        for (int i = 0; i < specifiers.length; i++) {
            String specifier = specifiers[i];
            if (locationSpec.startsWith(specifier)) {
                return types[i];
            }
        }
        // hmm, well default to entry
        return null;
    }

    public static String parameterText(String locationSpec)
    {
        locationSpec = locationSpec.trim();
        for (int i = 0; i < specifiers.length; i++) {
            String specifier = specifiers[i];
            if (locationSpec.startsWith(specifier)) {
                return locationSpec.substring(specifier.length());
            }
        }
        // hmm, doesn't really matter but ENTRY has no parameters

        return "";
    }

    private static String[] specifiers = {
            "AT ENTRY",
            "AT LINE",
            "AT READ",
            "AFTER READ",
            "AT WRITE",
            "AFTER WRITE",
            "AT INVOKE",
            "AFTER INVOKE",
            "AT SYNCHRONIZE",
            "AFTER SYNCHRONIZE",
            "AT THROW",
            "AT EXIT",
            "LINE", // for compatibility
            "AT CALL", // for ambiguity :-)
            "AFTER CALL", // for ambiguity :-)
            "AT RETURN" // for ambiguity :-)
    };

    private static LocationType[] types = {
            ENTRY,
            LINE,
            READ,
            READ_COMPLETED,
            WRITE,
            WRITE_COMPLETED,
            INVOKE,
            INVOKE_COMPLETED,
            SYNCHRONIZE,
            SYNCHRONIZE_COMPLETED,
            THROW,
            EXIT,
            LINE,
            INVOKE,
            INVOKE_COMPLETED,
            EXIT
    };
}
