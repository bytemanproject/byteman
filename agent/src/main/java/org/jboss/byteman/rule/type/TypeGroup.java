/*
* JBoss, Home of Professional Open Source
* Copyright 200810 Red Hat and individual contributors
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
package org.jboss.byteman.rule.type;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * the collection of types associated with an ECA rule
 */
public class TypeGroup {
    protected HashMap<String, Type> typeTable;
    private ClassLoader loader;
    private boolean resolve;
    private List<Type> exceptionTypes;

    /**
     * create a type group for a rule containing all the basic types
     * @param loader the class loader to use for type resolution
     */

    public TypeGroup(ClassLoader loader)
    {
        // ensure default types are all in table

        typeTable = new HashMap<String, Type>();

        typeTable.put("boolean", Type.Z);
        typeTable.put("java.lang.Boolean", Type.BOOLEAN);
        typeTable.put("Boolean", Type.BOOLEAN);

        typeTable.put("byte", Type.B);
        typeTable.put("java.lang.Byte", Type.BYTE);
        typeTable.put("Byte", Type.BYTE);
        typeTable.put("short", Type.S);
        typeTable.put("java.lang.Short", Type.SHORT);
        typeTable.put("Short", Type.SHORT);
        typeTable.put("char", Type.C);
        typeTable.put("java.lang.Char", Type.CHARACTER);
        typeTable.put("Char", Type.CHARACTER);
        typeTable.put("int", Type.I);
        typeTable.put("java.lang.Integer", Type.INTEGER);
        typeTable.put("Integer", Type.INTEGER);
        typeTable.put("long", Type.J);
        typeTable.put("java.lang.Long", Type.LONG);
        typeTable.put("Long", Type.LONG);

        typeTable.put("float", Type.F);
        typeTable.put("java.lang.Float", Type.FLOAT);
        typeTable.put("Float", Type.FLOAT);
        typeTable.put("double", Type.D);
        typeTable.put("java.lang.Double", Type.DOUBLE);
        typeTable.put("Double", Type.DOUBLE);

        typeTable.put("java.lang.String", Type.STRING);
        typeTable.put("String", Type.STRING);

        typeTable.put("java.lang.Object", Type.OBJECT);
        typeTable.put("Object", Type.OBJECT);

        typeTable.put("java.lang.Number", Type.NUMBER);
        typeTable.put("Number", Type.NUMBER);

        typeTable.put("void", Type.VOID);

        this.loader = loader;
        this.resolve = false;
        exceptionTypes = new ArrayList<Type>();
    }

    /**
     * lookup a type by name dereferencing it to its fully qualified type if that exists
     * @param name the type name
     * @return the type
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
     * @param name the type name
     * @return the type if created or matched or null if there is an alias mismatch
     */
    public Type create(String name)
    {
        // check for array types and create the base types first
        if (name.endsWith("[]")) {
            String baseName = name.substring(0, name.length() - 2);
            Type baseType = create(baseName);
            return createArray(baseType);
        }
        
        return create(name, null);
    }

    /**
     * create a type with a given name and class or return an existing type if the supplied
     * name and  class can be matched. if the type name is qualified ensure that any existing
     * type with an unqualified name matching this entry is not already aliased to another
     * type.
     * @param name the type name
     * @param clazz the associated class
     * @return the type if created or matched or null if there is a class or alias mismatch
     */
    public Type create(String name, Class clazz)
    {
        Type existing = typeTable.get(name);

        if (existing != null) {
            // use the existing type assuming we can match or upgrade the class
            if (clazz != null) {
                if (clazz == Type.dereference(existing).getTargetClass()) {
                    return existing;
                } else {
                    return null;
                }
            } else {
                return existing;
            }
        } else {
            if (clazz == null && resolve) {
                // try to find a class for this type using the class loader
                try {
                    clazz = loader.loadClass(name);
                } catch (ClassNotFoundException cfe) {
                    // ignore this for now as we may resolve it later
                }
            }
            // if the name is not package qualified and matches a class in package java.lang then
            // use the java.lang class

            if (clazz == null && name.indexOf('.') < 0) {
                try {
                    String newName = "java.lang." + name;
                    clazz = loader.loadClass(newName);
                    name = newName;
                } catch (ClassNotFoundException cfe) {
                    // ignore this for now as we may resolve it later
                }
            }

            Type newType = new Type(name, clazz);
            checkAlias(newType);
            // add the package qualified name
            typeTable.put(name, newType);
            return newType;
        }
    }

    /**
     * try to associate each type in the typegroup with a class 
     */

    public void resolveTypes() {
        for (Type type : typeTable.values()) {
            if (type.isUndefined()) {
                type.resolve(loader);
            }
        }
        resolve = true;
    }

    /**
     * if the supplied type has a package qualified name ensure that any existing
     * entry with the unqualified name is aliased to it or else add an entry with an
     * unqualified name as an alias for it. do nothing if the type name is unqualified
     * @param type the type to be checked for an alias
     * @return true if the alias type is now or was already in the table or false if no such
     * type can be installed because there is an existing alias to some other type
     */
    private boolean checkAlias(Type type)
    {
        String name = type.getName();
        int dotIdx = name.lastIndexOf('.');

        if (dotIdx >= 0) {
            // we are inserting a qualified type -- ensure it does not clash with any
            // unqualified name

            name = name.substring(dotIdx + 1);

            Type alias = typeTable.get(name);

            if (alias != null) {
                if (!alias.aliasTo(type)) {
                    return false;
                } else {
                    return true;
                }
            } else {
                // bag the unqualified name as an alias for this type

                alias = new Type(name);
                alias.aliasTo(type);

                typeTable.put(name, alias);
            }
        }

        return true;
    }

    public Type createArray(Type baseType)
    {
        String arrayTypeName = baseType.getName() + "[]";
        Type arrayType = typeTable.get(arrayTypeName);
        if (arrayType == null) {
            Class arrayClazz = null;
            if (baseType.isPrimitive() || baseType.isDefined()) {
                try {
                    // aaarrghh array base type names use dots not slashes bt still need  L...; bracketing
                    String internalName ="[" + baseType.getInternalName(true, false);
                    // need to do this via Class.forname after chnage from 1.5 to 1.6 snookered
                    // creation of array classes via loader.findClass.
                    // arrayClazz = loader.loadClass(internalName);
                    arrayClazz = Class.forName(internalName, false, loader);
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
            arrayType = baseType.arrayType(arrayClazz);
        }

        return arrayType;
    }

    public void addExceptionTypes(String[] exceptionTypeNames) {
        for (String exceptionTypeName : exceptionTypeNames) {
            exceptionTypes.add(create(TypeHelper.internalizeClass(exceptionTypeName)));
        }
    }

    public List<Type> getExceptionTypes()
    {
        return exceptionTypes;
    }

    public Type ensureType(Class clazz)
    {
        if (clazz.isArray()) {
            Class baseClazz = clazz.getComponentType();
            Type baseType = ensureType(baseClazz);
            return createArray(baseType);
        } else if (clazz.isPrimitive()) {
            return typeTable.get(clazz.getName());
        } else {
            String name = clazz.getCanonicalName();
            Type type = typeTable.get(name);
            if (type == null) {
                type = create(name, clazz);
            }
            return type;
        }
    }

    public Type match(String[] path)
    {
        // check to see if the first element of path is a known type or an alias for a known type
        String name = path[0];

        Type type = typeTable.get(name);
        if (type != null) {
            return Type.dereference(type);
        }

        // also check to see if the first element of path is a non-primitive type in java.lang e.g. System

        name = "java.lang." + name;
        type = typeTable.get(name);
        if (type != null) {
            return Type.dereference(type);
        } else {
            try {
                Class clazz = loader.loadClass(name);
                return ensureType(clazz);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }


        // ok, see if we can find a type using some initial segment of the path
        
        String fullName = "";
        String sepr = "";
        int length = path.length;

        for (int i = 0; i < length; i++) {
            fullName += sepr + path[i];
            sepr = ".";
            try {
                Class clazz = loader.loadClass(fullName);
                return ensureType(clazz);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        
        return null;
    }
}

