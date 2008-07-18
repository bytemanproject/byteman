package org.jboss.jbossts.orchestration.rule.type;

import java.util.HashMap;

/**
 * the collection of types associated with an ECA rule
 */
public class TypeGroup {
    protected HashMap<String, Type> typeTable;

    /**
     * create a type group for a rule containing all the basic types
     */

    public TypeGroup()
    {
        // ensure default types are all in table

        typeTable = new HashMap<String, Type>();

        typeTable.put("boolean", Type.BOOLEAN);
        typeTable.put("Boolean", Type.BOOLEAN);

        typeTable.put("byte", Type.BYTE);
        typeTable.put("Byte", Type.BYTE);
        typeTable.put("short", Type.SHORT);
        typeTable.put("Short", Type.SHORT);
        typeTable.put("char", Type.CHARACTER);
        typeTable.put("Char", Type.CHARACTER);
        typeTable.put("int", Type.INTEGER);
        typeTable.put("Integer", Type.INTEGER);
        typeTable.put("long", Type.LONG);
        typeTable.put("Long", Type.LONG);

        typeTable.put("float", Type.FLOAT);
        typeTable.put("Float", Type.FLOAT);
        typeTable.put("double", Type.DOUBLE);
        typeTable.put("DOUBLE", Type.DOUBLE);

        typeTable.put("String", Type.STRING);
        typeTable.put("void", Type.VOID);
    }

    /**
     * lookup a type by name dereferencing it to its fully qualified type if that exists
     * @param name
     * @return
     */
    public Type lookup(String name)
    {
        return Type.dereference(typeTable.get(name));
    }

    /**
     * create a type with a given name or return an existing type if the supplied
     * name can be matched. if the type name is qualified ensure that any existing
     * type with an unqualified name matching this entry is not already aliased to
     * another type.
     * @param name
     * @return the type if created or matched or null if there is an alias mismatch
     */
    public Type create(String name)
    {
        return create(name, null);
    }

    /**
     * create a type with a given name and class or return an existing type if the supplied
     * name and  class can be matched. if the type name is qualified ensure that any existing
     * type with an unqualified name matching this entry is not already aliased to another
     * type.
     * @param name
     * @param clazz
     * @return the type if created or matched or null if there is a class or alias mismatch
     */
    public Type create(String name, Class clazz)
    {
        Type existing = typeTable.get(name);
        if (existing == null) {
            Type newType = new Type(name, clazz);
            if (checkAlias(newType)) {
                return Type.dereference(newType);
            } else {
                return null;
            }
        } else {
            if (existing.isAssignableFrom(clazz)) {
                return Type.dereference(existing);
            } else {
                return null;
            }
        }
    }

    private boolean checkAlias(Type type)
    {
        String name = type.getName();
        int dotIdx = name.lastIndexOf('.');

        if (dotIdx >= 0) {
            // we are inserrting a qualified type -- ensure it does not clash with any
            // unqualified name

            name = name.substring(dotIdx);

            Type alias = typeTable.get(name);

            if (alias != null && !alias.aliasTo(type)) {
                return false;
            }
        }

        return true;
    }
}

