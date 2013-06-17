/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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

import org.objectweb.asm.ClassVisitor;
import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.agent.adapter.*;

/**
 * Specifies a location in a method at which a rule trigger should be inserted
 */
public abstract class Location
{
    /**
     * create a location object of a given type
     * @param type the type of location being specified
     * @param parameters the text of the parameters appended to the location specifier
     * @return a location of the appropriate type or null if the parameters are incorrectly specified
     */
    public static Location create(LocationType type, String parameters)
    {
        switch (type)
        {
            case ENTRY:
                return EntryLocation.create(parameters);
            case LINE:
                return LineLocation.create(parameters);
            case READ:
                return AccessLocation.create(parameters, ACCESS_READ, false);
            case READ_COMPLETED:
                return AccessLocation.create(parameters, ACCESS_READ, true);
            case WRITE:
                return AccessLocation.create(parameters, ACCESS_WRITE, false);
            case WRITE_COMPLETED:
                return AccessLocation.create(parameters, ACCESS_WRITE, true);
            case INVOKE:
                return InvokeLocation.create(parameters, false);
            case INVOKE_COMPLETED:
                return InvokeLocation.create(parameters, true);
            case SYNCHRONIZE:
                return SynchronizeLocation.create(parameters, false);
            case SYNCHRONIZE_COMPLETED:
                return SynchronizeLocation.create(parameters, true);
            case THROW:
                return ThrowLocation.create(parameters);
            case EXIT:
                return ExitLocation.create(parameters);
        }

        return null;
    }

    /**
     * return an adapter which can be used to check whether a method contains a trigger point whose position
     * matches this location
     * @return the required adapter
     */
    public abstract RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, TransformContext transformContext);

    /**
     * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
     * position matches this location
     * @return the required adapter
     */
    public abstract RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, TransformContext transformContext);

    /**
     * identify the type of this location
     * @return the type of this location
     */
    public abstract LocationType getLocationType();

    /**
     * flag indicating that a field access location refers to field READ operations
     */
    public static final int ACCESS_READ = 1;

    /**
     * flag indicating that a field access location refers to field WRITE operations
     */
    public static final int ACCESS_WRITE = 2;

    /**
     * location identifying a method entry trigger point
     */
    private static class EntryLocation extends Location
    {
        /**
         * create a location identifying a method entry trigger point
         * @param parameters the text of the parameters appended to the location specifier
         * @return a method entry location or null if the parameters is not a blank String
         */
        protected static Location create(String parameters) {
            if (!parameters.trim().equals("")) {
                // hmm, not expecting any parameters here
                return null;
            }
            return new EntryLocation();
        }

        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new EntryCheckAdapter(cv, transformContext);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new EntryTriggerAdapter(cv, transformContext);
        }

        public LocationType getLocationType() {
            return LocationType.ENTRY;
        }

        public String toString() {
            return "AT ENTRY";
        }
    }

    /**
     * location identifying a method line trigger point
     */
    private static class LineLocation extends Location
    {
        /**
         * the line at which the trigger point should be inserted
         */
        private int targetLine;

        /**
         * construct a location identifying a method line trigger point
         * @param targetLine the line at which the trigger point should be inserted
         */
        private LineLocation(int targetLine)
        {
            this.targetLine = targetLine;
        }

        /**
         * create a location identifying a method entry trigger point
         * @param parameters the text of the parameters appended to the location specifier
         * @return a method entry location or null if the parameters is not a blank String
         */
        protected static Location create(String parameters)
        {
            try {
                int targetLine = Integer.decode(parameters.trim());
                return new LineLocation(targetLine);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }

        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new LineCheckAdapter(cv, transformContext, targetLine);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new LineTriggerAdapter(cv, transformContext, targetLine);
        }

        public LocationType getLocationType() {
            return LocationType.LINE;
        }

        public String toString() {
            return "AT LINE " + targetLine;
        }
    }

    /**
     * location identifying a generic access trigger point
     */
    private static abstract class AccessLocation extends Location
    {
        /**
         * count identifying which access should be taken as the trigger point. if not specified
         * as a parameter this defaults to the first access.
         */
        protected int count;

        /**
         * flags identifying which type of access should be used to identify the trigger. this is either
         * ACCESS_READ, ACCESS_WRITE or an OR of these two values
         */
        protected int flags;

        /**
         * flag which is false if the trigger should be inserted before the field access is performed
         * and true if it should be inserted after
         */
        protected boolean whenComplete;

        protected AccessLocation(int count, int flags, boolean whenComplete) {
            this.count = count;
            this.flags = flags;
            this.whenComplete = whenComplete;
        }

        /**
         * create a location identifying a method entry trigger point
         * @param parameters the text of the parameters appended to the location specifier
         * @param whenComplete false if the trigger should be inserted before the access is performed
         * and true if it should be inserted after
         * @return a method entry location or null if the parameters is not a blank String
         */
        protected static Location create(String parameters, int flags, boolean whenComplete)
        {
            String text = parameters.trim();
            int count;

            // check for trailing count
            if (text.contains(" ")) {
                int spaceIdx = text.lastIndexOf(" ");
                String countText = text.substring(spaceIdx).trim();
                if (countText.equals("ALL")) {
                    // a zero count means match all
                    count = 0;
                } else {
                    try {
                        count = Integer.valueOf(countText);
                    } catch (NumberFormatException nfe) {
                        return null;
                    }
                }
                text = text.substring(0, spaceIdx).trim();
            } else {
                count = 1;
            }
            if (text.equals("")) {
                return null;
            }

            // check for a local or parameter var name identified by a leading $
            if (text.startsWith("$")) {
                String varname = text.substring(1).trim();
                return new VariableAccessLocation(varname, count, flags, whenComplete);
            } else {
                String typeName;
                String fieldName;
                // check for leading type name
                if (text.contains(".")) {
                    int dotIdx = text.lastIndexOf(".");
                    typeName = text.substring(0, dotIdx).trim();
                    fieldName=text.substring(dotIdx + 1).trim();
                } else {
                    typeName = null;
                    fieldName = text;
                }
                // TODO sanity check type and field name

                return new FieldAccessLocation(typeName, fieldName, count, flags, whenComplete);
            }
        }

        public LocationType getLocationType() {
            if ((flags & ACCESS_WRITE) != 0) {
                if (whenComplete) {
                    return LocationType.WRITE_COMPLETED;
                } else {
                    return LocationType.WRITE;
                }
            } else {
                if (whenComplete) {
                    return LocationType.READ_COMPLETED;
                } else {
                    return LocationType.READ;
                }
            }
        }
    }

    /**
     * location identifying a field access trigger point
     */
    private static class FieldAccessLocation extends AccessLocation
    {
        /**
         * the name of the field being accessed at the point where the trigger point should be inserted
         */
        private String fieldName;

        /**
         * the name of the type to which the field belongs or null if any type will do
         */
        private String typeName;

        /**
         * construct a location identifying a field read trigger point
         * @param typeName the name of the class owning the field
         * @param fieldName the name of the field being read
         * @param count count identifying which access should be taken as the trigger point
         * @param flags bit field comprising one or other of flags ACCESS_READ and ACCESS_WRITE identifying
         * whether this specifies field READ or WRITE operations
         * @param whenComplete false if the trigger should be inserted before the access is performed
         * and true if it should be inserted after
         */
        private FieldAccessLocation(String typeName, String fieldName, int count, int flags, boolean whenComplete)
        {
            super(count, flags, whenComplete);
            this.typeName = typeName;
            this.fieldName = fieldName;
        }

        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new FieldAccessCheckAdapter(cv, transformContext, typeName, fieldName, flags, count);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new FieldAccessTriggerAdapter(cv, transformContext, typeName, fieldName, flags, count, whenComplete);
        }

        public String toString() {
            String text;

            if (whenComplete) {
                text = "AFTER ";
            } else {
                text = "AT ";
            }
            if (flags == ACCESS_READ) {
                text += "READ ";
            } else if (flags == ACCESS_WRITE) {
                text += "WRITE ";
            } else {
                text += "ACCESS ";
            }

            if (typeName != null) {
                text += typeName + ".";
            }
            text += fieldName;

            if (count != 1) {
                if (count == 0) {
                    text += " ALL";
                } else {
                    text += " " + count;
                }
            }

            return text;
        }
    }

    /**
     * location identifying a variable access trigger point
     */
    private static class VariableAccessLocation extends AccessLocation
    {
        /**
         * the name of the variable being accessed at the point where the trigger point should be inserted
         */
        private String variableName;

        /**
         * flag which is true if the name is a method parameter index such as $0, $1 etc otherwise false
         */
        private boolean isIndex;

        /**
         * construct a location identifying a variable read trigger point
         * @param typeName the name of the class owning the field
         * @param variablename the name of the variable being read
         * @param count count identifying which access should be taken as the trigger point
         * @param flags bit field comprising one or other of flags ACCESS_READ and ACCESS_WRITE identifying
         * whether this specifies field READ or WRITE operations
         * @param whenComplete false if the trigger should be inserted before the access is performed
         * and true if it should be inserted after
         */
        protected VariableAccessLocation(String variablename, int count, int flags, boolean whenComplete)
        {
            super(count, flags, whenComplete);
            this.variableName = variablename;
            isIndex = variablename.matches("[0-9]+");
        }


        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, TransformContext transformContext) {
            if (isIndex) {
                int paramIdx = Integer.valueOf(variableName);
                return new IndexParamAccessCheckAdapter(cv, transformContext, paramIdx, flags, count);
            } else {
                // we need to insert a BMJSRInliner into the pipeline so that the check adapter gets
                // notified when local vars go in and out of scope
                return new VariableAccessCheckAdapter(cv, transformContext, variableName, flags, count);
            }
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, TransformContext transformContext) {
            if (isIndex) {
                int paramIdx = Integer.valueOf(variableName);
                return new IndexParamAccessTriggerAdapter(cv, transformContext, paramIdx, flags, count, whenComplete);
            } else {
                return new VariableAccessTriggerAdapter(cv, transformContext, variableName, flags, count, whenComplete);
            }
        }

        public LocationType getLocationType() {
            if ((flags & ACCESS_WRITE) != 0) {
                if (whenComplete) {
                    return LocationType.WRITE_COMPLETED;
                } else {
                    return LocationType.WRITE;
                }
            } else {
                if (whenComplete) {
                    return LocationType.READ_COMPLETED;
                } else {
                    return LocationType.READ;
                }
            }
        }

        public String toString() {
            String text;

            if (whenComplete) {
                text = "AFTER ";
            } else {
                text = "AT ";
            }
            if (flags == ACCESS_READ) {
                text += "READ ";
            } else if (flags == ACCESS_WRITE) {
                text += "WRITE ";
            } else {
                text += "ACCESS ";
            }

            text += "$";
            
            text += variableName;

            if (count != 1) {
                if (count == 0) {
                    text += " ALL";
                } else {
                    text += " " + count;
                }
            }

            return text;
        }
    }

    /**
     * location identifying a method invocation trigger point
     */
    private static class InvokeLocation extends Location
    {
        /**
         * the name of the method being invoked at the point where the trigger point should be inserted
         */
        private String methodName;

        /**
         * the name of the type to which the method belongs or null if any type will do
         */
        private String typeName;

        /**
         * the method signature in externalised form
         */
        private String signature;

        /**
         * count identifying which invocation should be taken as the trigger point. if not specified
         * as a parameter this defaults to the first invocation.
         */
        private int count;

        /**
         * flag which is false if the trigger should be inserted before the method invocation is performed
         * and true if it should be inserted after
         */
        private boolean whenComplete;

        /**
         * construct a location identifying a method invocation trigger point
         * @param typeName the name of the class owning the method
         * @param methodName the name of the method being called
         * @param signature the method signature in externalised form
         * @param count count identifying which invocation should be taken as the trigger point
         * @param whenComplete false if the trigger should be inserted before the method invocation is
         * performed and true if it should be inserted after
         */
        private InvokeLocation(String typeName, String methodName, String signature, int count, boolean whenComplete)
        {
            this.typeName = typeName;
            this.methodName = methodName;
            this.signature = signature;
            this.count = count;
            this.whenComplete = whenComplete;
        }

        /**
         * create a location identifying a method entry trigger point
         * @param parameters the text of the parameters appended to the location specifier
         * @param whenComplete false if the trigger should be inserted before the access is performed
         * and true if ti shoudl be inserted after
         * @return a method entry location or null if the parameters is not a blank String
         */
        protected static Location create(String parameters, boolean whenComplete)
        {
            String text = parameters.trim();
            String typeName;
            String fieldName;
            String signature;
            int count;

            // check for trailing count
            if (text.contains(")")) {
                int tailIdx = text.lastIndexOf(")");
                String countText = text.substring(tailIdx + 1).trim();
                if (!countText.equals("")) {
                    if (countText.equals("ALL")) {
                        // a zero count means all
                        count = 0;
                    } else {
                        try {
                            count = Integer.valueOf(countText);
                        } catch (NumberFormatException nfe) {
                            return null;
                        }
                    }
                } else {
                    count = 1;
                }
                text = text.substring(0, tailIdx + 1).trim();
            } else if (text.contains(" ")) {
                int tailIdx = text.lastIndexOf(" ");
                String countText = text.substring(tailIdx + 1).trim();
                if (!countText.equals("")) {
                    if (countText.equals("ALL")) {
                        // a zero count means all
                        count = 0;
                    } else {
                        try {
                            count = Integer.valueOf(countText);
                        } catch (NumberFormatException nfe) {
                            return null;
                        }
                    }
                } else {
                    count = 1;
                }
                text = text.substring(0, tailIdx).trim();
            } else {
                count = 1;
            }
            // check for argument list
            if (text.contains("(")) {
                signature = TypeHelper.parseMethodDescriptor(text);
                text=TypeHelper.parseMethodName(text);
            } else {
                signature = "";
            }
            // check for leading type name
            if (text.contains(".")) {
                int dotIdx = text.lastIndexOf(".");
                typeName = text.substring(0, dotIdx).trim();
                fieldName=text.substring(dotIdx + 1).trim();
            } else {
                typeName = null;
                fieldName = text;
            }
            // TODO sanity check type and field name

            return new InvokeLocation(typeName, fieldName, signature, count, whenComplete);
        }

        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new InvokeCheckAdapter(cv, transformContext, typeName, methodName, signature, count);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new InvokeTriggerAdapter(cv, transformContext, typeName, methodName, signature, count, whenComplete);
        }

        public LocationType getLocationType() {
            if (whenComplete) {
                return LocationType.INVOKE_COMPLETED;
            } else {
                return LocationType.INVOKE;
            }
        }

        public String toString() {
            String text;

            if (whenComplete) {
                text = "AFTER INVOKE ";
            } else {
                text = "AT INVOKE ";
            }

            if (typeName != null) {
                text += typeName + ".";
            }
            text += methodName;

            if (signature.length() > 0) {
                text = text + TypeHelper.internalizeDescriptor(signature);
            }
            
            if (count != 1) {
                if (count == 0) {
                    text += " ALL";
                } else {
                    text += " " + count;
                }
            }

            return text;
        }
    }

    /**
     * location identifying a synchronization trigger point
     */
    private static class SynchronizeLocation extends Location
    {
        /**
         * count identifying which synchronization should be taken as the trigger point. if not specified
         * as a parameter this defaults to the first synchronization.
         */
        private int count;

        /**
         * flag which is false if the trigger should be inserted before the synchronization is performed
         * and true if it should be inserted after
         */
        private boolean whenComplete;

        /**
         * construct a location identifying a synchronization trigger point
         * @param count count identifying which synchronization should be taken as the trigger point
         * @param whenComplete false if the trigger should be inserted before the synchronization is
         * performed and true if it should be inserted after
         */
        private SynchronizeLocation(int count, boolean whenComplete)
        {
            this.count = count;
            this.whenComplete = whenComplete;
        }

        /**
         * create a location identifying a synchronization trigger point
         * @param parameters the text of the parameters appended to the location specifier
         * @param whenComplete false if the trigger should be inserted before the synchronization is
         * performed and true if it should be inserted after
         * @return a method entry location or null if the parameters is not a blank String
         */
        protected static Location create(String parameters, boolean whenComplete)
        {
            String text = parameters.trim();
            int count;

            // check for count
            if (text.length() != 0) {
                if (text.equals("ALL")) {
                    // a zero count means all
                    count = 0;
                } else {
                    try {
                        count = Integer.valueOf(text);
                    } catch (NumberFormatException nfe) {
                        return null;
                    }
                }
            } else {
                count = 1;
            }

            return new SynchronizeLocation(count, whenComplete);
        }

        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new SynchronizeCheckAdapter(cv, transformContext, count);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new SynchronizeTriggerAdapter(cv, transformContext, count, whenComplete);
        }

        public LocationType getLocationType() {
            if (whenComplete) {
                return LocationType.SYNCHRONIZE_COMPLETED;
            } else {
                return LocationType.SYNCHRONIZE;
            }
        }

        public String toString() {
            String text;

            if (whenComplete) {
                text= "AFTER SYNCHRONIZE";
            } else {
                text= "AT SYNCHRONIZE";
            }

            if (count != 1) {
                if (count == 0) {
                    text += " ALL";
                } else {
                    text += " " + count;
                }
            }

            return text;
        }
    }
    /**
     * location identifying a throw trigger point
     */
    private static class ThrowLocation extends Location
    {
        /**
         * count identifying which throw operation should be taken as the trigger point. if not specified
         * as a parameter this defaults to the first throw.
         */
        private int count;

        /**
         * the name of the exception type to which the method belongs or null if any type will do
         */
        private String typeName;

        /**
         * construct a location identifying a throw trigger point
         * @param count count identifying which throw should be taken as the trigger point
         * @param typeName the name of the exception type associated with the throw operation
         */
        private ThrowLocation(int count, String typeName)
        {
            this.count = count;
            this.typeName = typeName;
        }

        /**
         * create a location identifying a throw trigger point
         * @param parameters the text of the parameters appended to the location specifier
         * @return a throw location or null if the parameters does not contain a valid type name
         */
        protected static Location create(String parameters)
        {
            String text = parameters.trim();
            String typeName = "";
            int count;

            // text may be either blank, a count or ALL
            if (text.equals("")) {
                // count defaults to 1
                count = 1;
            } else if (text.equals("ALL")) {
                // a zero count means all
                count = 0;
            } else {
                try {
                    count = Integer.valueOf(text);
                } catch (NumberFormatException nfe) {
                    return null;
                }
            }

            // TODO sanity check type name

            return new ThrowLocation(count, typeName);
        }

        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new ThrowCheckAdapter(cv, transformContext, typeName, count);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, TransformContext transformContext) {
            return new ThrowTriggerAdapter(cv, transformContext, typeName, count);
        }

        public LocationType getLocationType() {
            return LocationType.THROW;
        }

        public String toString() {
            String text = "AT THROW";

            if (count != 1) {
                if (count == 0) {
                    text += " ALL";
                } else {
                    text += " " + count;
                }
            }

            return text;
        }
    }
    /**
     * location identifying a method exit trigger point
     */
    private static class ExitLocation extends Location
    {
        /**
         * create a location identifying a method entry trigger point
         * @param parameters the text of the parameters appended to the location specifier
         * @return a method entry location or null if the parameters is not a blank String
         */
        protected static Location create(String parameters) {
            if (!parameters.trim().equals("")) {
                // hmm, not expecting any parameters here
                return null;
            }
            return new ExitLocation();
        }

        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, TransformContext transformContext) {
            // a line check adapter with line -1 will do the job

            return new ExitCheckAdapter(cv, transformContext);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, TransformContext transformContext) {
            // a line adapter with line -1 will do the job

            return new ExitTriggerAdapter(cv, transformContext);
        }

        public LocationType getLocationType() {
            return LocationType.EXIT;
        }

        public String toString() {
            return "AT EXIT";
        }
    }

}
