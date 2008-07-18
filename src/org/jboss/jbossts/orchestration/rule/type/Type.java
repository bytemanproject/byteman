package org.jboss.jbossts.orchestration.rule.type;

import java.util.HashMap;

/**
 * models the type of a rule binding or expression
 */
public class Type {
    // TODO we need eventually to be able to create array types
    /**
     * create a type with a given name and optionally an associated class
     *
     * @param clazzName the name of the type which may or may not be fully qualified
     * @param clazz the class associated with this name if it is know otherwise null
     */
    public Type(String clazzName, Class clazz)
    {
        this(clazzName, clazz, F_OBJECT);
    }

    /**
     * create a type with a given name and no associated class
     *
     * @param clazzName the name of the type which may or may not be fully qualified
     */
    public Type(String clazzName)
    {
        this(clazzName, null);
    }

    /**
     * get the possibly unqualified name with which this type was created
     * @return
     */
    public String getName()
    {
        return clazzName;
    }

    /**
     * get the class associated with this type or a special undefined class if the type is not defined
     * @return
     */
    public Class getTargetClass()
    {
        return clazz;
    }

    /**
     * get the package component of the name associated with this type or the empty String
     * if it has no package or is was defiend with an unqualified name or is a builtin type
     * @return
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
            return true;
        }
    }

    /**
     * check whether this type can be assigned with values of the supplied type including
     * the case where numeric conversion from known or unknown numeric types but excluding any other
     * cases where this type is undefined
     * @param type the type poviding RHS values
     * @return true if it is known that the assignment is valid, false if it is not known to be valid or
     * is known not to be valid
     */

    public boolean isAssignableFrom(Type type)
    {
        return isAssignableFrom(type.clazz);
    }

    /**
     * check whether this type can be assigned with values of the supplied class including
     * the case where numeric conversion from known or unknown numeric types but excluding any other
     * cases where this type is undefined
     * @param clazz the class poviding RHS values
     * @return true if it is known that the assignment is valid, false if it is not known to be valid or
     * is known not to be valid
     */
    public boolean isAssignableFrom(Class clazz)
    {
        if (isUndefined()) {
            // we can only say yes if this is void or if it is an undefined numeric type
            if (isVoid()) {
                return true;
            } else if (isNumeric()) {
                // we can coerce any numeric type to another numeric type
                Type type = primitiveType(clazz);
                if (type != null && type.isNumeric()) {
                    return true;
                }
            }
            // we don't know if we can coerce here so return false to play safe
            return false;
        } else if (isObject()) {
            // we can always convert something to a string unless it is void
            if (this == STRING) {
                return (clazz != void.class);
            }
            // see if the supplied type is assignable from this type's class
            if (this.clazz.isAssignableFrom(clazz)) {
                return true;
            }
            return false;
        } else {

            // ok this is a defined primitive type check that clazz is primitive

            Type type = primitiveType(clazz);

            if (type == null) {
                return false;
            }
            // ok we can proceed if the two types have overlapping flags
            return ((this.flags & type.flags) != 0);
        }
    }

    public boolean isUndefined()
    {
        return ((flags & F_UNKNOWN) != 0);
    }

    public boolean isDefined()
    {
        return !isUndefined();
    }

    public boolean isPrimitive()
    {
        return ((flags & F_PRIMITIVE) != 0);
    }

    public boolean isVoid()
    {
        return this == VOID;
    }

    public boolean isNumeric()
    {
        return (flags & F_NUMERIC) != 0;
    }

    public boolean isIntegral()
    {
        return (flags & F_INTEGRAL) == F_INTEGRAL;
    }

    public boolean isFloating()
    {
        return (flags & F_FLOATING) == F_FLOATING;
    }

    public boolean isBoolean()
    {
        return (flags & F_BOOLEAN) != 0;
    }

    public boolean isObject()
    {
        return (flags & F_OBJECT) != 0;
    }

    public boolean isArray()
    {
        return (flags & F_ARRAY) != 0;
    }

    public Type builtinType(Class clazz)
    {
        return builtinTypes.get(clazz.getName());
    }

    public Type primitiveType(Class clazz)
    {
        Type type = builtinType(clazz);

        if (type != null && type.isDefined() && type.isPrimitive())
        {
            return type;
        }

        return null;
    }

    public Type numericType(Class clazz) {
        Type type = builtinType(clazz);

        if (type != null && type.isNumeric())
        {
            return type;
        }

        return null;
    }

    private String clazzName;
    private Class clazz;
    private String packageName;
    private int flags;
    private Type aliasFor;

    private Type(String clazzName, Class clazz, int flags)
    {
        this.clazzName = clazzName;

        if (clazz != null) {
            this.clazz = clazz;
        } else {
            this.clazz = Undefined.class;
            flags |= F_UNKNOWN; 
        }

        this.flags = flags;

        // types dereference to themselves until they are aliased

        aliasFor = this;

        packageName = packagePart(clazzName);
    }

    /**
     * compute the type to which a binary arithmetic operator should promote its operands
     * before combination based on the two operand types which is also the type to be
     * used for the result of the operation
     * @param type1 the type of the left operand  which must be numeric but may be undefined
     * @param type2 the type of the right operand which must be numeric but may be undefined
     * @return the corresponding promotion/result type which may be undefined numeric
     */
    public static Type promote(Type type1, Type type2) {
        if (!type1.isNumeric() || !type2.isNumeric()) {
            // should not happen!
            System.err.println("Type.promote : unexpected non-numeric type argument");
            return Type.NUMBER;
        } else if (type1.isUndefined() || type2.isUndefined()) {
                // don't know for sure which is which so return undefined numeric
                return Type.NUMBER;
        } else if (type1.isFloating() || type2.isFloating()) {
            if (type1 == DOUBLE || type2 == DOUBLE) {
                return DOUBLE;
            } else {
                return FLOAT;
            }
        } else {
            // integral types -- ok lets invent^H^H^H declare some rules here :-)
            // either arg long forces a long result
            // either arg integer forces an integer result
            // a matched pair of short, char or byte arguments retains the same type in the result
            // otherwise the result is an integer and args wil be coerced to integer
            if (type1 == LONG || type2 == LONG) {
                return LONG;
            } else if (type1 == INTEGER || type2 == INTEGER) {
                return INTEGER;
            } else if (type1 == SHORT && type2 == SHORT) {
                return SHORT;
            } else if (type1 == CHARACTER && type2 == CHARACTER) {
                return CHARACTER;
            } else if (type1 == BYTE && type2 == BYTE) {
                return BYTE;
            } else {
                return INTEGER;
            }
        }
    }

    private static String classPart(String className)
    {
        int dotIdx = className.lastIndexOf('.');

        if (dotIdx < 0) {
            return className;
        } else {
            return className.substring(dotIdx);
        }
    }

    private static String packagePart(String className)
    {
        int dotIdx = className.lastIndexOf('.');

        if (dotIdx < 0) {
            return "";
        } else {
            return className.substring(0, dotIdx);
        }
    }

    // private class used to type unknown types
    private static class Undefined {
    };

    final public static int F_UNKNOWN       = 0x1000;
    final public static int F_BOOLEAN       = 0x0001;
    final public static int F_INTEGRAL      = 0x0002;
    final public static int F_FLOATING      = 0x0004;
    final public static int F_NUMERIC       = F_UNKNOWN | F_INTEGRAL | F_FLOATING;
    final public static int F_PRIMITIVE     = F_UNKNOWN | F_BOOLEAN | F_NUMERIC;
    final public static int F_OBJECT        = 0x0008;
    final public static int F_ARRAY         = 0x0010;
    final public static int F_ANY           = F_UNKNOWN | F_PRIMITIVE | F_OBJECT | F_ARRAY;

    // we need to cope with array types
    final public static Type BOOLEAN = new Type("Boolean", Boolean.class, F_BOOLEAN);
    final public static Type BYTE = new Type("Byte", Byte.class, F_INTEGRAL);
    final public static Type SHORT = new Type("Short", Short.class, F_INTEGRAL);
    final public static Type CHARACTER = new Type("Character", Character.class, F_INTEGRAL);
    final public static Type INTEGER = new Type("Integer", Integer.class, F_INTEGRAL);
    final public static Type LONG = new Type("Long", Long.class, F_INTEGRAL);
    final public static Type FLOAT = new Type("Float", Float.class, F_FLOATING);
    final public static Type DOUBLE = new Type("Double", Double.class, F_FLOATING);
    final public static Type STRING = new Type("String", String.class, F_OBJECT);
    final public static Type VOID = new Type("void", void.class, F_ANY);
    final public static Type NUMBER = new Type("Number", Number.class, F_NUMERIC);
    final public static Type UNDEFINED = new Type("", Undefined.class, F_ANY);

    final private static HashMap<String, Type> builtinTypes;

    static {
        builtinTypes = new HashMap<String, Type>();
        // canonical names
        builtinTypes.put(BOOLEAN.getTargetClass().getName(), BOOLEAN);
        builtinTypes.put(BYTE.getTargetClass().getName(), BYTE);
        builtinTypes.put(SHORT.getTargetClass().getName(), SHORT);
        builtinTypes.put(CHARACTER.getTargetClass().getName(), CHARACTER);
        builtinTypes.put(INTEGER.getTargetClass().getName(), INTEGER);
        builtinTypes.put(LONG.getTargetClass().getName(), LONG);
        builtinTypes.put(FLOAT.getTargetClass().getName(), FLOAT);
        builtinTypes.put(STRING.getTargetClass().getName(), STRING);
        builtinTypes.put(VOID.getTargetClass().getName(), VOID);
        builtinTypes.put(NUMBER.getTargetClass().getName(), NUMBER);
        builtinTypes.put(UNDEFINED.getTargetClass().getName(), UNDEFINED);
        // nicknames
        builtinTypes.put(BOOLEAN.getName(), BOOLEAN);
        builtinTypes.put(BYTE.getName(), BYTE);
        builtinTypes.put(SHORT.getName(), SHORT);
        builtinTypes.put(CHARACTER.getName(), CHARACTER);
        builtinTypes.put(INTEGER.getName(), INTEGER);
        builtinTypes.put(LONG.getName(), LONG);
        builtinTypes.put(FLOAT.getName(), FLOAT);
        builtinTypes.put(STRING.getName(), STRING);
        builtinTypes.put(VOID.getName(), VOID);
        builtinTypes.put(NUMBER.getName(), NUMBER);
        builtinTypes.put(UNDEFINED.getName(), UNDEFINED);
        // allow undefined to be spelled out
        builtinTypes.put("Undefined", UNDEFINED);
    }
}
