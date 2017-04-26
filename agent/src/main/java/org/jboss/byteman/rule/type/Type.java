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
package org.jboss.byteman.rule.type;

import org.jboss.byteman.rule.exception.TypeException;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * models the type of a rule binding or expression
 */
public class Type {
    /**
     * create a type with a given name and optionally an associated class
     *
     * @param typeName the name of the type which may or may not be fully qualified
     * @param clazz the class associated with this name if it is know otherwise null
     */
    public Type(String typeName, Class clazz)
    {
        this(typeName, clazz, F_OBJECT, 4, null);
    }

    /**
     * create a type with a given name and no associated class
     *
     * @param typeName the name of the type which may or may not be fully qualified
     */
    public Type(String typeName)
    {
        this(typeName, null);
    }

    /**
     * create an array type from this base type
     * @return an array type
     */
    public Type arrayType()
    {
        return arrayType(null);
    }
    
    /**
     * create an array type from this base type
     * @param clazz the class for the array type derived from the class of base type or
     * null if the base type is undefined
     * @return an array type
     */
    public Type arrayType(Class clazz)
    {
        if (this.arrayType ==  null) {
            arrayType = new Type(typeName + "[]", clazz, F_ARRAY, 4, this);
        }
        return arrayType;
    }

    /**
     * retrieve the base type for an array type or null if this is not an array type
     * @return an array type
     */                                    
    public Type getBaseType()
    {
        if (isArray()) {
            return baseType;
        } else {
            return null;
        }
    }

    /**
     * get the possibly unqualified name with which this type was created
     * @return the type name
     */
    public String getName()
    {
        return typeName;
    }

    /**
     * get the internal name for this type used by the class loader. this is only valid for
     * defined types, defined array types or primitive types
     * @return the type name
     */
    public String getInternalName()
    {
        return getInternalName(false, true);
    }

    /**
     * get the internal name for this type used by the class
     * loader. this is only valid for defined types, defined array
     * types or primitive types
     * @param forDescriptor true if we need the name to appear in a
     * decriptor false if not
     * @param slashSeparate true if the package separator should be
     * slash false if it should be dot
     * @return the type name
     */
    public String getInternalName(boolean forDescriptor, boolean slashSeparate)
    {
        if (isArray()) {
            // the base name has to be L...; bracketed
            return "[" + baseType.getInternalName(true, slashSeparate);
        } else if (isPrimitive()) {
            return internalNames.get(typeName);
        } else if (isVoid()) {
            return internalNames.get(typeName);
        } else {
            Class targetClass = aliasFor.getTargetClass();
            Class enclosingClass = targetClass.getEnclosingClass();
            String name;
            if (enclosingClass != null && !forDescriptor) {
                // retain the $ separator for inner classes and local/anon classes
                name = targetClass.getName();
            } else {
                name = targetClass.getCanonicalName();
                if (name == null) {
                    // local or anonymous class
                    name = targetClass.getName();
                }
            }
            if (slashSeparate) {
                name = name.replace('.', '/');
            }
            if (forDescriptor) {
                name = "L" + name + ";";
            }
            return name;
        }
    }

    /**
     * get the class associated with this type if it has one or a special undefined class if
     * the type is not defined or null if there is no associated class
     * @return the associated class
     */
    public Class getTargetClass()
    {
        return clazz;
    }

    /**
     * get the package component of the name associated with this type or the empty String
     * if it has no package or is was defiend with an unqualified name or is a builtin type
     * @return the package component or an empty string
     */

    public String getPackageName()
    {
            return packageName;
    }

    /**
     * dereference an object type to the fully qualified named type to which it is aliased where
     * such an alias has been found to exist or return the supplied type if no alias exists or the
     * type is a non-objecttype or was originally specified using a fully qualified type name.
     *
     * @param target the type to be dereferenced
     * @return the alias where it exists or the supplied type where there is no alias or null
     * if a null value is supplied
     */
    public static Type dereference(Type target)
    {
        if (target == null) {
            return null;
        }

        while (target.aliasFor != target) {
            target = target.aliasFor;
        }

        return target;
    }

    public void resolve(ClassLoader loader)
    {
        if (this.isDefined() || this == Type.UNDEFINED || this == Type.N) {
            return;
        }
        if (aliasFor != this) {
            aliasFor.resolve(loader);
            clazz = aliasFor.clazz;
            if (clazz != null) {
                flags &= ~F_UNKNOWN;
            }
        } else {
            try {
                clazz = loader.loadClass(getName());
                flags &= ~F_UNKNOWN;
            } catch (ClassNotFoundException e) {
                // ok give up here -- we should get a type error later
            }
        }
        // ensure any classes which might have been created via this one are also resolved
        if (baseType != null) {
            baseType.resolve(loader);
        }
        if (arrayType != null) {
            arrayType.resolve(loader);
        }
    }

    /**
     * attempt to establish an alias from an package unqualified named object type to a package
     * qualified named object type whose unqualified name equals this type's name
     * @param target the package qualified named type for which this type should become an alias
     * @return true if the alias link can be established or already exsits or false if an alias
     * to a different target already exists or this type or the target are not object types
     */
    public boolean aliasTo(Type target)
    {
        // we can only legitimately alias an object type without a package to a type with a package
        // ???caller should already have checked this???
        if (!isObject() || !target.isObject()) {
            return false;
        }
        // ???caller should already have checked this???
        if (packageName.length() != 0 || target.packageName.length() == 0) {
            return false;
        }
        // if there is already an alias for this type then it has to be the same as the supplied type

        if (aliasFor != this) {
            return (aliasFor == target);
        } else {
            // we assume the caller has already checked that the names match so . . .
            // update the alias
            aliasFor = target;
            // propagate type-class binding up or down alias link if available
            if (clazz != null) {
                if (target.clazz != null) {
                    // oops class mismatch!
                    return false;
                } else {
                    target.clazz = clazz;
                    target.flags &= ~F_UNKNOWN;
                }
            } else if (target.clazz != null) {
                clazz = target.clazz;
                flags &= ~F_UNKNOWN;
            }
            if (arrayType != null) {
                // ensure array types are also aliased
                if (target.arrayType == null) {
                    target.arrayType(target.clazz);
                }
                arrayType.aliasTo(target.arrayType);
            } else if (target.arrayType != null) {
                // point the array type at the target array type
                arrayType = arrayType(this.clazz);
                arrayType.aliasTo(target.arrayType);
            }
            return true;
        }
    }

    /**
     * check whether this type can be assigned with values of the supplied type including
     * the case where numeric conversion from known or unknown numeric types but excluding
     * any other cases where this type is undefined
     *
     * n.b. the caller must dereference the recipient and argument types before calling
     * this method
     * @param type the type poviding RHS values
     * @return true if it is known that the assignment is valid, false if it is not known to be valid or
     * is known not to be valid
     */

    public boolean isAssignableFrom(Type type)
    {
        if (this.aliasFor != this) {
            return Type.dereference(this).isAssignableFrom(type);
        }
        type = Type.dereference(type);
        
        // check for unknown cases first
        if (isNumeric()) {
            // can always coerce numerics even if it involves boxing
            return type.isNumeric();
        } else if (isUndefined() || type.isUndefined()) {
            // cannot answer this question yet
            return false;
        } else if (this == type) {
            return true;
        } else if (isString()) {
            // can always convert anything to a string via boxing and Object.toString();
            return true;
        } else if (isVoid()) {
            // can always assign to void
            return true;
        } else if (isPrimitive()) {
           if (!type.isPrimitive()) {
               // see if we can arrive at the correct type by unboxing
               Type unboxedType = boxedTypes.get(type);
               return (unboxedType == this);
           } else {
               return false;
           }
        } else if (type.isPrimitive()) {
            // see if we can arrive at the correct type by boxing
            Type boxedType = boxedTypes.get(type);
            if (boxedType == this) {
                return true;
            } else {
                // we only get here if we have a known type i.e. both clazz values are non-null
                // see if the supplied type is assignable from this type's class
                return (this.clazz.isAssignableFrom(boxedType.clazz));
            }
        } else if (isObject() || isArray()) {
            // we only get here if we have a known type i.e. both clazz values are non-null
            // see if the supplied type is assignable from this type's class
            return (this.clazz.isAssignableFrom(type.clazz));
        } else {
            return false;
        }
    }

    /**
     * test if this type is an unknown type. a type may be unknown either because it is one
     * of the pseudo types used as type variables or because it represents an object type
     * mentioned in a rule but not yet bound to a specific class
     * @return true if the type is unknown otherwise false
     */
    public boolean isUndefined()
    {
        return ((flags & F_UNKNOWN) != 0);
    }

    /**
     * check if this type is a known type. this is just teh oppositeof isUndefined
     * @return false if the type is unknown otherwise true
     */
    public boolean isDefined()
    {
        return !isUndefined();
    }

    /**
     * return true if this is a type mentioned in a rule but not yet bound to a specific class
     * @return true if the type is not yet bound to a specific class
     */
    public boolean isUnbound()
    {
        // this only happens where we have an object type marked with the UNKNOWN marker

        return (flags & (F_OBJECT | F_UNKNOWN)) == (F_OBJECT | F_UNKNOWN);
    }

    /**
     * return true if this is a primitive value type
     * @return true if this is a primitive value type
     */
    public boolean isPrimitive()
    {
        return (flags & F_PRIMITIVE) != 0;
    }

    /**
     * return true if this is a value type, which includes the boxed versions of primitive types
     * @return true if this is a value type
     */
    public boolean isValue()
    {
        return (flags & F_VALUE) != 0;
    }

    /**
     * return true if this is the void type
     * @return true if this is void type
     */
    public boolean isVoid()
    {
        return (flags & F_VOID) != 0;
    }

    /**
     * return true if this is the string type
     * @return true if this is string type
     */
    public boolean isString()
    {
        return (flags & F_STRING) != 0;
    }

    /**
     * return true if this is a numeric type, including the unknown primitive numeric type
     * @return true if this is a numeric type
     */
    public boolean isNumeric()
    {
        return (flags & F_NUMERIC) != 0;
    }

    /**
     * return true if this is an integral type of whatever size, including the unknown
     * primitive numeric type
     * @return true if this is an integral type
     */
    public boolean isIntegral()
    {
        return (flags & F_INTEGRAL) == F_INTEGRAL;
    }

    /**
     * return true if this is a floating type of whatever size, including the unknown
     * primitive numeric type
     * @return true if this is a floating type
     */
    public boolean isFloating()
    {
        return (flags & F_FLOATING) == F_FLOATING;
    }

    /**
     * return true if this is a boolean type
     * @return true if this is a boolean type
     */
    public boolean isBoolean()
    {
        return (flags & F_BOOLEAN) != 0;
    }

    /**
     * return true if this is an object type, including unbound types mentioned in rules
     * @return true if this is an object type
     */
    public boolean isObject()
    {
        return (flags & F_OBJECT) != 0;
    }

    /**
     * return true if this is an array type
     * @return true if this is an array type
     */
    public boolean isArray()
    {
        return (flags & F_ARRAY) != 0;
    }

    /**
     * return the number of stack words occupied by instances of this type
     * @return true if this is an array type
     */
    public int getNBytes()
    {
        return nBytes;
    }

    /**
     * return the builtin type associated with a given class
     * @param clazz the class for the builtin type
     * @return the corresponding builtin type
     */
    public static Type builtinType(Class clazz)
    {
        return builtinTypes.get(clazz.getName());
    }

    /**
     * return the primitive type whose boxed equivalent is associated with a given class
     * @param clazz the class for the primitivebuiltin type
     * @return the corresponding primitive type
     */
    public static Type boxType(Class clazz)
    {
        Type type = builtinType(clazz);

        return boxedTypes.get(type);
    }

    /**
     * return the primitive type for a boxed type or vice versa
     * @param type the boxed type
     * @return the corresponding primitive type
     */
    public static Type boxType(Type type)
    {
        return boxedTypes.get(type);
    }

    private String typeName;
    private Class clazz;
    private String packageName;
    private int flags;
    private int nBytes;
    private Type aliasFor;
    private Type baseType;
    private Type arrayType;

    protected Type(String typeName, Class clazz, int flags, int nBytes)
    {
        this(typeName, clazz, flags, nBytes, null);
    }

    protected Type(String typeName, Class clazz, int flags, int nBytes, Type baseType)
    {
        this.typeName = typeName;

        if (clazz == null) {
            flags |= F_UNKNOWN;
        }

        this.clazz = clazz;

        this.flags = flags;

        this.nBytes = nBytes;

        if ((flags & F_ARRAY) != 0) {
            this.baseType = baseType;
            baseType.arrayType = this;
        } else {
            this.baseType = null;
        }
        this.arrayType = null;

        // types dereference to themselves until they are aliased

        aliasFor = this;

        packageName = packagePart(typeName);
    }

    /**
     * compute the type to which a binary arithmetic operator should promote its operands
     * before combination based on the two operand types which is also the type to be
     * used for the result of the operation
     * @param type1 the type of the left operand  which must be numeric but may be undefined
     * @param type2 the type of the right operand which must be numeric but may be undefined
     * @return the corresponding promotion/result type which may be undefined numeric
     * @throws TypeException if types are undefined or promotion is invalid
     */
    public static Type promote(Type type1, Type type2) throws TypeException {
        if (type1.isUndefined() || type2.isUndefined()) {
                // don't know for sure which is which so return undefined numeric
                return N;
        } else if (!type1.isNumeric() || !type2.isNumeric()) {
                // should not happen!
            throw new TypeException("Type.promote : unexpected non-numeric type argument");
        } else if (type1.isFloating() || type2.isFloating()) {
            if (type1 == DOUBLE || type2 == DOUBLE || type1 == D || type2 == D) {
                return D;
            } else {
                return F;
            }
        } else {
            // integral types -- ok lets invent^H^H^H declare some rules here :-)
            // either arg long forces a long result
            // either arg integer forces an int result
            // a matched pair of short, char or byte arguments retains the same type in the result
            // otherwise the result is an int and args will be coerced to int
            if (type1 == LONG || type2 == LONG || type1 == J || type2 == J) {
                return J;
            } else if (type1 == INTEGER || type2 == INTEGER || type1 == I || type2 == I) {
                return I;
            } else if ((type1 == SHORT || type1 == S) && (type2 == SHORT || type2 == S)) {
                return S;
            } else if ((type1 == CHARACTER || type1 == C) && (type2 == CHARACTER || type2 == C)) {
                return C;
            } else if ((type1 == BYTE || type1 == B) && (type2 == BYTE || type2 == B)) {
                return B;
            } else {
                return I;
            }
        }
    }
    /* TODO we don't seem to need this?
    private static String classPart(String className)
    {
        int dotIdx = className.lastIndexOf('.');

        if (dotIdx < 0) {
            return className;
        } else {
            return className.substring(dotIdx);
        }
    }
    */
    private static String packagePart(String className)
    {
        int dotIdx = className.lastIndexOf('.');

        if (dotIdx < 0) {
            return "";
        } else {
            return className.substring(0, dotIdx);
        }
    }

    public static List<String> parseMethodDescriptor(String descriptor, boolean includeReturnType)
    {
        List<String> argTypes = new ArrayList<String>();
        int length = descriptor.length();
        int idx = descriptor.indexOf("(");
        int arrayDepth = 0;
        if (idx < 0) {
            return null;
        }
        idx = idx + 1;
        while (idx < length) {
            char c = descriptor.charAt(idx);
            switch(descriptor.charAt(idx))
            {
                case 'Z':
                {
                    String baseType = "boolean";
                    argTypes.add(fixArrayType(baseType, arrayDepth));
                    arrayDepth = 0;
                    idx++;
                }
                break;
                case 'B':
                {
                    String baseType = "byte";
                    argTypes.add(fixArrayType(baseType, arrayDepth));
                    arrayDepth = 0;
                    idx++;
                }
                break;
                case 'S':
                {
                    String baseType = "short";
                    argTypes.add(fixArrayType(baseType, arrayDepth));
                    arrayDepth = 0;
                    idx++;
                }
                break;
                case 'C':
                {
                    String baseType = "char";
                    argTypes.add(fixArrayType(baseType, arrayDepth));
                    arrayDepth = 0;
                    idx++;
                }
                break;
                case 'I':
                {
                    String baseType = "int";
                    argTypes.add(fixArrayType(baseType, arrayDepth));
                    arrayDepth = 0;
                    idx++;
                }
                break;
                case 'J':
                {
                    String baseType = "long";
                    argTypes.add(fixArrayType(baseType, arrayDepth));
                    arrayDepth = 0;
                    idx++;
                }
                break;
                case 'F':
                {
                    String baseType = "float";
                    argTypes.add(fixArrayType(baseType, arrayDepth));
                    arrayDepth = 0;
                    idx++;
                }
                break;
                case 'D':
                {
                    String baseType = "double";
                    argTypes.add(fixArrayType(baseType, arrayDepth));
                    arrayDepth = 0;
                    idx++;
                }
                break;
                case 'V':
                {
                    if (arrayDepth != 0) {
                        // hmm void arrays are definitely not kosher
                        return null;
                    } else if (!includeReturnType) {
                        // hmm should not have got here
                        return null;
                    }
                    argTypes.add("void");
                    idx++;
                }
                break;
                case 'L':
                {
                    int endIdx = descriptor.indexOf(';', idx);
                    if (endIdx < 0) {
                        return null;
                    }
                    String baseType = descriptor.substring(idx+1, endIdx).replace('/', '.');
                    argTypes.add(fixArrayType(baseType, arrayDepth));
                    arrayDepth = 0;
                    idx = endIdx + 1;
                }
                break;
                case '[':
                {
                    arrayDepth++;
                    idx++;
                }
                break;
                case ')':
                {
                    if (arrayDepth != 0) {
                        return null;
                    } else if (!includeReturnType) {
                        // stop here
                        return argTypes;
                    } else {
                        // skip any trailing spaces before the return type
                        idx++;
                        while (idx < length && descriptor.charAt(idx) == ' ')
                        {
                            idx++;
                        }
                        if (idx == length) {
                            // ok, we need to add a void return type
                            argTypes.add("void");
                        }
                    }
                }
                break;
                default:
                    return null;
            }
        }

        return (arrayDepth == 0 ? argTypes : null);
    }

    public static String parseFieldDescriptor(String descriptor)
    {
        int length = descriptor.length();
        int idx = 0;
        int arrayDepth = 0;
        while (idx < length) {
            char c = descriptor.charAt(idx);
            switch(descriptor.charAt(idx))
            {
                case 'Z':
                {
                    String baseType = "boolean";
                    return(fixArrayType(baseType, arrayDepth));
                }
                case 'B':
                {
                    String baseType = "byte";
                    return(fixArrayType(baseType, arrayDepth));
                }
                case 'S':
                {
                    String baseType = "short";
                    return(fixArrayType(baseType, arrayDepth));
                }
                case 'C':
                {
                    String baseType = "char";
                    return(fixArrayType(baseType, arrayDepth));
                }
                case 'I':
                {
                    String baseType = "int";
                    return(fixArrayType(baseType, arrayDepth));
                }
                case 'J':
                {
                    String baseType = "long";
                    return(fixArrayType(baseType, arrayDepth));
                }
                case 'F':
                {
                    String baseType = "float";
                    return(fixArrayType(baseType, arrayDepth));
                }
                case 'D':
                {
                    String baseType = "double";
                    return(fixArrayType(baseType, arrayDepth));
                }
                case 'V':
                {
                    String baseType = "void";
                    return(fixArrayType(baseType, arrayDepth));
                }
                case 'L':
                {
                    int endIdx = descriptor.indexOf(';', idx);
                    if (endIdx < 0) {
                        return null;
                    }
                    String baseType = descriptor.substring(idx+1, endIdx).replace('/', '.');
                    return(fixArrayType(baseType, arrayDepth));
                }
                case '[':
                {
                    arrayDepth++;
                    idx++;
                }
                break;
                default:
                    return null;
            }
        }
        return null;
    }

    public static String parseMethodReturnType(String descriptor)
    {
        int length = descriptor.length();
        int idx = descriptor.indexOf(")");
        int arrayDepth = 0;
        
        if (idx < 0) {
            return "void";
        }
        idx = idx + 1;
        while (idx < length) {
            char c = descriptor.charAt(idx);
            switch(c)
            {
                case 'Z':
                {
                    String baseType = "boolean";
                    return fixArrayType(baseType, arrayDepth);
                }
                case 'B':
                {
                    String baseType = "byte";
                    return fixArrayType(baseType, arrayDepth);
                }
                case 'S':
                {
                    String baseType = "short";
                    return fixArrayType(baseType, arrayDepth);
                }
                case 'C':
                {
                    String baseType = "char";
                    return fixArrayType(baseType, arrayDepth);
                }
                case 'I':
                {
                    String baseType = "int";
                    return fixArrayType(baseType, arrayDepth);
                }
                case 'J':
                {
                    String baseType = "long";
                    return fixArrayType(baseType, arrayDepth);
                }
                case 'F':
                {
                    String baseType = "float";
                    return fixArrayType(baseType, arrayDepth);
                }
                case 'D':
                {
                    String baseType = "double";
                    return fixArrayType(baseType, arrayDepth);
                }
                case 'V':
                {
                    return "void";
                }
                case 'L':
                {
                    int endIdx = descriptor.indexOf(';', idx);
                    if (endIdx < 0) {
                        return "void";
                    }
                    String baseType = descriptor.substring(idx+1, endIdx).replace('/', '.');
                    return fixArrayType(baseType, arrayDepth);
                }
                case '[':
                {
                    arrayDepth++;
                    idx++;
                }
                break;
                default:
                    return "void";
            }
        }
        return "void";
    }

    /**
     * identify the local var slot used to store a method parameter identified by parameter index
     * @param access the access flags for the method including whether or not it is static
     * @param desc the intrenal form descriptor for the maethod
     * @param paramIdx the index of the parameter in the parameter lost starting with 0 for this or 1 for
     * actual parameters
     * @return the corresponding local var slot or -1 if there is no such parameter
     */
    public static int paramSlotIdx(int access, String desc, int paramIdx)
    {
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        if (paramIdx ==  0) {
            if (isStatic) {
                return -1;
            } else {
                return 0;
            }
        } else {
            int slotIdx =  (isStatic ? 0 : 1);
            int descIdx = 0;
            char[] chars = desc.toCharArray();
            char next = chars[descIdx++];
            // skip leading '('
            if (next == '(') {
                next = chars[descIdx++];
            }
            //  skip  all slots preceding the desired one
            for (int i = 1;  i < paramIdx;  i++) {
                switch (next) {
                    case 'Z':
                    case 'B':
                    case 'S':
                    case 'C':
                    case 'I':
                    case 'F':
                    {
                        slotIdx++;
                    }
                    break;
                    case 'J':
                    case 'D':
                    {
                        slotIdx += 2;
                    }
                    break;
                    case 'L':
                    {
                        slotIdx++;
                        // skip forward in descriptor up to ';'

                        while (next != ';') {
                            next = chars[descIdx++];
                        }
                    }
                    break;
                    case '[':
                    {
                        slotIdx++;
                        // skip any extra '[' chars to get to the base array type
                        while (next == '[') {
                            next = chars[descIdx++];
                        }
                        if (next == 'L') {
                            // skip forward in descriptor up to ';'

                            while (next != ';') {
                                next = chars[descIdx++];
                            }
                        }
                    }
                    break;
                    case ')':
                    default:
                    {
                        // invalid param index or invalid descriptor --either way this is a problem
                        return -1;
                    }
                }
                next = chars[descIdx++];
            }
            return slotIdx;
        }
    }

    public static String fixArrayType(String baseType, int dimension)
    {
        String result = baseType;

        for (int i = 0; i < dimension; i++) {
            result  += "[]";
        }
        
        return result;
    }

    public static String internalName(Class<?> clazz)
    {
        return internalName(clazz, false);
    }

    public static String internalName(Class<?> clazz, boolean forField)
    {
        if (clazz.isPrimitive()) {
            if (forField) {
                return internalNames.get(clazz.getName());
            } else {
                return clazz.getName();
            }
        } else if (clazz.isArray()) {
            Class base = clazz.getComponentType();
            return "[" + internalName(base, true);
        } else if (forField) {
            return "L" + clazz.getName().replace('.', '/') + ";";
        } else {
            return clazz.getName().replace('.', '/');
        }
    }

    // private class used to type unknown types
    private static class Undefined {
    }
    // tags divide types into exclusive categories
    // unknown types may be associated with a tag group such as numeric, or object
    // markers are used to identify type properties such as unknown, primitive

    // value type tags
    final public static int F_BOOLEAN       = 0x0001;
    final public static int F_INTEGRAL      = 0x0002;
    final public static int F_FLOATING      = 0x0004;
    // object type tag
    final public static int F_OBJECT        = 0x0008;
    // void type tag
    final public static int F_VOID          = 0x0010;
    // array type tag
    final public static int F_ARRAY         = 0x0020;

    // value type tag groups
    final public static int F_NUMERIC       = F_INTEGRAL | F_FLOATING;
    final public static int F_VALUE         = F_BOOLEAN | F_NUMERIC;

    // unknown type marker
    final public static int F_UNKNOWN       = 0x1000;
    // primitive type marker
    final public static int F_PRIMITIVE     = 0x2000;                
    // string type marker
    final public static int F_STRING        = 0x4000;

    // we need to cope with array types

    final public static Type Z = new Type("boolean", boolean.class, F_BOOLEAN|F_PRIMITIVE, 4);
    final public static Type B = new Type("byte", byte.class, F_INTEGRAL|F_PRIMITIVE, 1);
    final public static Type S = new Type("short", short.class, F_INTEGRAL|F_PRIMITIVE, 2);
    final public static Type C = new Type("char", char.class, F_INTEGRAL|F_PRIMITIVE, 2);
    final public static Type I = new Type("int", int.class, F_INTEGRAL|F_PRIMITIVE, 4);
    final public static Type J = new Type("long", long.class, F_INTEGRAL|F_PRIMITIVE, 8);
    final public static Type F = new Type("float", float.class, F_FLOATING|F_PRIMITIVE, 4);
    final public static Type D = new Type("double", double.class, F_FLOATING|F_PRIMITIVE, 8);
    // pseudo type representing an undefined numeric primitive type
    final public static Type N = new Type("", null, F_UNKNOWN|F_NUMERIC|F_PRIMITIVE, 0);

    final public static Type BOOLEAN = new Type("java.lang.Boolean", Boolean.class, F_OBJECT|F_BOOLEAN, 4);
    final public static Type BYTE = new Type("java.lang.Byte", Byte.class, F_OBJECT|F_INTEGRAL, 4);
    final public static Type SHORT = new Type("java.lang.Short", Short.class, F_OBJECT|F_INTEGRAL, 4);
    final public static Type CHARACTER = new Type("java.lang.Character", Character.class, F_OBJECT|F_INTEGRAL, 4);
    final public static Type INTEGER = new Type("java.lang.Integer", Integer.class, F_OBJECT|F_INTEGRAL, 4);
    final public static Type LONG = new Type("java.lang.Long", Long.class, F_OBJECT|F_INTEGRAL, 4);
    final public static Type FLOAT = new Type("java.lang.Float", Float.class, F_OBJECT|F_FLOATING, 4);
    final public static Type DOUBLE = new Type("java.lang.Double", Double.class, F_OBJECT|F_FLOATING, 4);
    final public static Type STRING = new Type("java.lang.String", String.class, F_OBJECT|F_STRING, 4);
    final public static Type VOID = new Type("void", void.class, F_VOID, 0);
    final public static Type NUMBER = new Type("java.lang.Number", Number.class, F_OBJECT|F_NUMERIC, 0);
    final public static Type OBJECT = new Type("java.lang.Object", Object.class, F_OBJECT, 0);
    // pseudo type representing an undefined primitive or object type
    final public static Type UNDEFINED = new Type("", Undefined.class, F_UNKNOWN, 0);

    final private static HashMap<String, Type> builtinTypes;
    final private static HashMap<String, Type> primitiveTypes;
    final private static HashMap<Type, Type> boxedTypes;
    final private static HashMap<String, String> internalNames;

    static {
        builtinTypes = new HashMap<String, Type>();
        // primitive type names
        builtinTypes.put(Z.getName(), Z);
        builtinTypes.put(B.getName(), B);
        builtinTypes.put(S.getName(), S);
        builtinTypes.put(C.getName(), C);
        builtinTypes.put(I.getName(), I);
        builtinTypes.put(J.getName(), J);
        builtinTypes.put(F.getName(), F);
        builtinTypes.put(D.getName(), D);
        builtinTypes.put("$number$", N);
        // canonical names
        builtinTypes.put(BOOLEAN.getTargetClass().getName(), BOOLEAN);
        builtinTypes.put(BYTE.getTargetClass().getName(), BYTE);
        builtinTypes.put(SHORT.getTargetClass().getName(), SHORT);
        builtinTypes.put(CHARACTER.getTargetClass().getName(), CHARACTER);
        builtinTypes.put(INTEGER.getTargetClass().getName(), INTEGER);
        builtinTypes.put(LONG.getTargetClass().getName(), LONG);
        builtinTypes.put(FLOAT.getTargetClass().getName(), FLOAT);
        builtinTypes.put(DOUBLE.getTargetClass().getName(), DOUBLE);
        builtinTypes.put(STRING.getTargetClass().getName(), STRING);
        builtinTypes.put(VOID.getTargetClass().getName(), VOID);
        builtinTypes.put(NUMBER.getTargetClass().getName(), NUMBER);
        builtinTypes.put(UNDEFINED.getTargetClass().getName(), UNDEFINED);
        builtinTypes.put(OBJECT.getTargetClass().getName(), OBJECT);
        // nicknames
        builtinTypes.put("Boolean", BOOLEAN);
        builtinTypes.put("Byte", BYTE);
        builtinTypes.put("Short", SHORT);
        builtinTypes.put("Character", CHARACTER);
        builtinTypes.put("Integer", INTEGER);
        builtinTypes.put("Long", LONG);
        builtinTypes.put("Float", FLOAT);
        builtinTypes.put("Double", DOUBLE);
        builtinTypes.put("String", STRING);
        builtinTypes.put("Number", NUMBER);
        builtinTypes.put("Object", OBJECT);
        builtinTypes.put("", UNDEFINED);
        // allow undefined to be spelled out
        builtinTypes.put("Undefined", UNDEFINED);

        primitiveTypes = new HashMap<String, Type>();
        primitiveTypes.put(Z.getName(), Z);
        primitiveTypes.put(B.getName(), B);
        primitiveTypes.put(S.getName(), S);
        primitiveTypes.put(C.getName(), C);
        primitiveTypes.put(I.getName(), I);
        primitiveTypes.put(J.getName(), J);
        primitiveTypes.put(F.getName(), F);
        primitiveTypes.put(D.getName(), D);
        primitiveTypes.put("$number$", N);

        // allow for boxing
        boxedTypes = new HashMap<Type, Type>();
        boxedTypes.put(Z, BOOLEAN);
        boxedTypes.put(B, BYTE);
        boxedTypes.put(S, SHORT);
        boxedTypes.put(C, CHARACTER);
        boxedTypes.put(I, INTEGER);
        boxedTypes.put(J, LONG);
        boxedTypes.put(F, FLOAT);
        boxedTypes.put(D, DOUBLE);
        // also allow for unboxing
        boxedTypes.put(BOOLEAN, Z);
        boxedTypes.put(BYTE, B);
        boxedTypes.put(SHORT, S);
        boxedTypes.put(CHARACTER, C);
        boxedTypes.put(INTEGER, I);
        boxedTypes.put(LONG, J);
        boxedTypes.put(FLOAT, F);
        boxedTypes.put(DOUBLE, D);

        internalNames = new HashMap<String, String>();
        // add translations from primitive names to internal tag
        internalNames.put("Z", "boolean");
        internalNames.put("B", "byte");
        internalNames.put("S", "short");
        internalNames.put("C", "char");
        internalNames.put("I", "int");
        internalNames.put("J", "long");
        internalNames.put("F", "float");
        internalNames.put("D", "double");
        internalNames.put("V", "void");
        // also add reverse translations
        internalNames.put("boolean", "Z");
        internalNames.put("byte", "B");
        internalNames.put("short", "S");
        internalNames.put("char", "C");
        internalNames.put("int", "I");
        internalNames.put("long", "J");
        internalNames.put("float", "F");
        internalNames.put("double", "D");
        internalNames.put("void", "V");
    }
    public String toString()
    {
        return getName();
    }
}
