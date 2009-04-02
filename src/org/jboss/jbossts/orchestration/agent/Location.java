package org.jboss.jbossts.orchestration.agent;

import org.objectweb.asm.ClassVisitor;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.jboss.jbossts.orchestration.agent.adapter.*;

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
    public abstract RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod);

    /**
     * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
     * position matches this location
     * @return the required adapter
     */
    public abstract RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod);

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
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            // a line check adapter with line -1 will do the job

            return new LineCheckAdapter(cv, rule, targetClass, targetMethod, -1);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            // a line adapter with line -1 will do the job

            return new LineTriggerAdapter(cv, rule, targetClass, targetMethod, -1);
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
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new LineCheckAdapter(cv, rule, targetClass, targetMethod, targetLine);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new LineTriggerAdapter(cv, rule, targetClass,targetMethod, targetLine);
        }

        public String toString() {
            return "AT LINE " + targetLine;
        }
    }

    /**
     * location identifying a method field access trigger point
     */
    private static class AccessLocation extends Location
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
         * count identifying which access should be taken as the trigger point. if not specified
         * as a parameter this defaults to the first access.
         */
        private int count;

        /**
         * flags identifying which type of access should be used to identify the trigger. this is either
         * ACCESS_READ, ACCESS_WRITE or an OR of these two values
         */
        private int flags;

        /**
         * flag which is false if the trigger should be inserted before the field access is performed
         * and true if it should be inserted after
         */
        private boolean whenComplete;

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
        private AccessLocation(String typeName, String fieldName, int count, int flags, boolean whenComplete)
        {
            this.typeName = typeName;
            this.fieldName = fieldName;
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
            String typeName;
            String fieldName;
            int count;

            // check for trailing count
            if (text.contains(" ")) {
                int spaceIdx = text.lastIndexOf(" ");
                String countText = text.substring(spaceIdx).trim();
                try {
                    count = Integer.valueOf(countText);
                } catch (NumberFormatException nfe) {
                    return null;
                }
                text = text.substring(0, spaceIdx).trim();
            } else {
                count = 1;
            }
            if (text.equals("")) {
                return null;
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
                
            return new AccessLocation(typeName, fieldName, count, flags, whenComplete);
        }

        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new AccessCheckAdapter(cv, rule, targetClass, targetMethod, typeName, fieldName, flags, count);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new AccessTriggerAdapter(cv, rule, targetClass, targetMethod, typeName, fieldName, flags, count, whenComplete);
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
                text += " " + count;
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
                try {
                    count = Integer.valueOf(countText);
                } catch (NumberFormatException nfe) {
                    return null;
                }
                text = text.substring(0, tailIdx).trim();
            } else if (text.contains(" ")) {
                int tailIdx = text.lastIndexOf(" ");
                String countText = text.substring(tailIdx + 1).trim();
                try {
                    count = Integer.valueOf(countText);
                } catch (NumberFormatException nfe) {
                    return null;
                }
                text = text.substring(0, tailIdx).trim();
            } else {
                count = 1;
            }
            // check for argument list
            if (text.contains("(")) {
                text=TypeHelper.parseMethodName(text);
                signature = TypeHelper.parseMethodDescriptor(text);
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
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new InvokeCheckAdapter(cv, rule, targetClass, targetMethod, typeName, methodName, signature, count);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new InvokeTriggerAdapter(cv, rule, targetClass, targetMethod, typeName, methodName, signature, count, whenComplete);
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
                text += " " + count;
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
                try {
                    count = Integer.valueOf(text);
                } catch (NumberFormatException nfe) {
                    return null;
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
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new SynchronizeCheckAdapter(cv, rule, targetClass, targetMethod, count);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new SynchronizeTriggerAdapter(cv, rule, targetClass, targetMethod, count, whenComplete);
        }

        public String toString() {
            String text;

            if (whenComplete) {
                text= "AFTER SYNCHRONIZE";
            } else {
                text= "AT SYNCHRONIZE";
            }

            if (count != 1) {
                text += " " + count;
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
            String typeName;
            String signature;
            int count;

            // check for trailing count
            if (text.contains(" ")) {
                int tailIdx = text.lastIndexOf(" ");
                String countText = text.substring(tailIdx + 1).trim();
                try {
                    count = Integer.valueOf(countText);
                } catch (NumberFormatException nfe) {
                    return null;
                }
                text = text.substring(0, tailIdx).trim();
            } else {
                count = 1;
            }

            // text may be either a count or a type name
            if (text.equals("") || !Character.isDigit(text.charAt(0))) {
                typeName = text;
            } else {
                try {
                    count = Integer.valueOf(text);
                } catch (NumberFormatException nfe) {
                    return null;
                }
                typeName="";
            }

            // TODO sanity check type name

            return new ThrowLocation(count, typeName);
        }

        /**
         * return an adapter which can be used to check whether a method contains a trigger point whose position
         * matches this location
         * @return the required adapter
         */
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new ThrowCheckAdapter(cv, rule, targetClass, targetMethod, typeName, count);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            return new ThrowTriggerAdapter(cv, rule, targetClass, targetMethod, typeName, count);
        }

        public String toString() {
            String text = "AT THROW";

            if (count != 1) {
                text += " " + count;
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
        public RuleCheckAdapter getRuleCheckAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            // a line check adapter with line -1 will do the job

            return new ExitCheckAdapter(cv, rule, targetClass, targetMethod);
        }

        /**
         * return an adapter which can be used to insert a trigger call in a method containing a trigger point whose
         * position matches this location
         * @return the required adapter
         */
        public RuleTriggerAdapter getRuleAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
            // a line adapter with line -1 will do the job

            return new ExitTriggerAdapter(cv, rule, targetClass, targetMethod);
        }

        public String toString() {
            return "AT EXIT";
        }
    }

}
