package org.jboss.jbossts.orchestration.agent;

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
    // this is tricky so we exclude it for now
    // EXIT,
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
    SYNCHRONIZE_COMPLETED;

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
            "LINE", // for compatibility
            "AT CALL", // for ambiguity :-)
            "AFTER CALL" // for ambiguity :-)
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
            LINE,
            INVOKE,
            INVOKE_COMPLETED
    };
}
