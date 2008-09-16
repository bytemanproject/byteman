package org.jboss.jbossts.orchestration.rule.type;

import org.jboss.jbossts.orchestration.annotation.EventHandler;

/**
 * Helpoer class providing static methods for manipulating type and class names,
 * field and method descriptor names etc
 */
public class TypeHelper {

    public static boolean equalDescriptors(String desc1, String desc2)
    {
        int idx1 = 0, idx2 = 0;
        int len1 = desc1.length(), len2 = desc2.length();
        while (idx1 < len1) {
            // check the other has not dropped off the end
            if (idx2 == len2) {
                if ((idx1 == (len1 - 1)) && (desc1.charAt(idx1) == '$')) {
                    return true;
                }
                return false;
            }
            // check type is the same
            char char1 = desc1.charAt(idx1);
            char char2 = desc2.charAt(idx2);
            // if we have a $ at the end of the descriptor then this means any return
            // type so special case this
            if ((char1 == '$' && idx1 == len1 - 1) || (char2 == '$' && idx2 == len2 - 1)) {
                return true;
            }
            // otherwise the chars must match
            if (char1 != char2) {
                return false;
            }
            // however an L indicates a class name and we allow a classname without a package
            // to match a class name with a package
            if (char1 == 'L') {
                // ok, ensure the names must match modulo a missing package
                int end1 = idx1 + 1;
                int end2 = idx2 + 1;
                while (end1 < len1 && desc1.charAt(end1) != ';') {
                    end1++;
                }
                while (end2 < len2 && desc2.charAt(end2) != ';') {
                    end2++;
                }
                if (end1 == len1 || end2 == len2) {
                    // bad format for desc!!
                    return false;
                }
                String typeName1 = desc1.substring(idx1 + 1, end1);
                String typeName2 = desc2.substring(idx2 + 1, end2);
                if (!typeName1.equals(typeName2)) {
                    int tailIdx1 = typeName1.lastIndexOf('/');
                    int tailIdx2 = typeName2.lastIndexOf('/');
                    if (tailIdx1 > 0) {
                        if (tailIdx2 > 0) {
                            // both specify packages so they must be different types
                            return false;
                        } else {
                            // only type 1 specifies a package so type 2 should match the tail
                            if (!typeName2.equals(typeName1.substring(tailIdx1 + 1))) {
                                return false;
                            }
                        }
                    } else {
                        if (tailIdx2 > 0) {
                            // only type 2 specifies a package so type 1 should match the tail
                            if (!typeName1.equals(typeName2.substring(tailIdx2 + 1))) {
                                return false;
                            }
                        } else {
                            // neither specify packages so they must be different types
                            return false;
                        }
                    }
                }
                // skp past ';'s
                idx1 = end1;
                idx2 = end2;
            }
            idx1++;
            idx2++;
        }

        // check the other has not reached the end
        if (idx2 != len2) {
            return false;
        }

        return true;
    }
    /**
     * convert a classname from canonical form to the form used to represent it externally i.e. replace
     * all dots with slashes
     *
     * @param className
     * @return
     */
    public static String externalizeClass(String className)
    {
        return className.replaceAll("\\.", "/");
    }

    /**
     * convert a classname from external form to canonical form i.e. replace
     * all slashes with dots
     *
     * @param className
     * @return
     */
    public static String internalizeClass(String className)
    {
        String result = className;
        int length = result.length();
        if (result.charAt(length - 1) == ';') {
            result = result.substring(1, length - 2);
        }
        result = result.replaceAll("/", "\\.");
        return result;
    }

    /**
     * convert a type name from canonical form to the form used to represent it externally i.e.
     * replace primitive type names by the appropriate single letter types, class names
     * by the externalized class name bracketed by 'L' and ';' and array names by the
     * base type name preceded by '['.
     *
     * @param typeName
     * @return
     */
    public static String externalizeType(String typeName)
    {
        String externalName = "";
        String[] typeAndArrayIndices = typeName.split("\\[");
        String baseType = typeAndArrayIndices[0].trim();
        for (int i = 1; i< typeAndArrayIndices.length; i++) {
            String arrayIdx = typeAndArrayIndices[i];
            if (arrayIdx.indexOf("\\]") != 0) {
                externalName += '[';
            }
        }
        for (int i = 0; i < internalNames.length; i++) {
            if (internalNames[i].equals(baseType)) {
                externalName += externalNames[i];
                return externalName;
            }
        }

        externalName += "L" + externalizeClass(baseType) + ";";

        return externalName;
    }

    /**
     * list of well known typenames as written in Java code
     */
    final static private String[] internalNames = {
            "", /* equivalent to void */
            "void",
            "byte",
            "char",
            "short",
            "int",
            "long",
            "float",
            "double",
            "boolean",
            "Byte",
            "Character",
            "Short",
            "Integer",
            "Long",
            "Float",
            "Double",
            "String",
            "java.lang.Byte",
            "java.lang.Character",
            "java.lang.Short",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Float",
            "java.lang.Double",
            "java.lang.String"
    };

    /**
     * list of typenames in external form corresponding to entries ni previous list
     */
    final static private String[] externalNames = {
            "$",
            "V",
            "B",
            "C",
            "S",
            "I",
            "J",
            "F",
            "D",
            "Z",
            "Ljava/lang/Byte;",
            "Ljava/lang/Character;",
            "Ljava/lang/Short;",
            "Ljava/lang/Integer;",
            "Ljava/lang/Long;",
            "Ljava/lang/Float;",
            "Ljava/lang/Double;",
            "Ljava/lang/String;",
            "Ljava/lang/Byte;",
            "Ljava/lang/Character;",
            "Ljava/lang/Short;",
            "Ljava/lang/Integer;",
            "Ljava/lang/Long;",
            "Ljava/lang/Float;",
            "Ljava/lang/Double;",
            "Ljava/lang/String;"
    };

    /**
     * convert a method descriptor from canonical form to the form used to represent it externally
     *
     * @param desc the method descriptor which must be trimmed of any surrounding white space
     * @return an externalised form for the descriptor
     */
    public static String externalizeDescriptor(String desc)
    {
        // the descriptor will start with '(' and the arguments list should end with ')' and,
        // if it is not void be followed by a return type
        int openIdx = desc.indexOf('(');
        int closeIdx = desc.indexOf(')');
        int length = desc.length();
        if (openIdx != 0) {
            return "";
        }
        if (closeIdx < 0) {
            return "";
        }
        String retType = (closeIdx < length ? desc.substring(closeIdx + 1).trim() : "");
        String[] args = desc.substring(1, closeIdx).trim().split(",");
        String externalRetType = externalizeType(retType);
        String externalArgs = "";
        for (int i = 0; i < args.length ; i++) {
            externalArgs += externalizeType(args[i]);
        }

        return "(" + externalArgs + ")" + externalRetType;
    }

    /**
     * split off the method name preceding the signature and return it
     * @param targetMethod - the unqualified method name, possibly including signature
     * @return
     */
    public static String parseMethodName(String targetMethod) {
        int sigIdx = targetMethod.indexOf("(");
        if (sigIdx > 0) {
            return targetMethod.substring(0, sigIdx).trim();
        } else {
            return targetMethod;
        }
    }

    /**
     * split off the signature following the method name and return it
     * @param targetMethod - the unqualified method name, possibly including signature
     * @return
     */
    public static String parseMethodDescriptor(String targetMethod) {
        int descIdx = targetMethod.indexOf("(");
        if (descIdx >= 0) {
            String desc = targetMethod.substring(descIdx, targetMethod.length()).trim();
            return externalizeDescriptor(desc);
        } else {
            return "";
        }
    }

    /**
     * split off the signature following the method name and return it
     * @param targetName the unqualified method name, not including signature
     * @param targetSignature the method signature including brackets types and return type
     * @return
     */
    public static String generateFieldName(String targetName, String targetSignature) {
        String result = targetName;
        int startIdx = targetSignature.indexOf("(");
        int endIdx = targetSignature.indexOf(")");
        if (startIdx < 0) {
            startIdx = 0;
        }
        if (endIdx < 0) {
            endIdx = targetSignature.length() - 1;
        }

        String args = targetSignature.substring(startIdx, endIdx + 1);

        result = result.replaceAll("<", "\\$_");
        result = result.replaceAll(">", "_\\$");

        // remove any brackets, semi-colons and '[' characters
        args = args.replaceAll("\\(", "\\$_");
        args = args.replaceAll("\\)", "_\\$");
        args = args.replaceAll(";", "__");
        args = args.replaceAll("\\[", "\\$\\$_");
        args = args.replaceAll("/", "_\\$_");

        return result + args;
    }

    private static Class generateHandlerClass(EventHandler handler, ClassLoader loader, String targetClassName, Class targetClass)
    {
        // TODO -- write this but use Object for now
        return Object.class;
    }
}
