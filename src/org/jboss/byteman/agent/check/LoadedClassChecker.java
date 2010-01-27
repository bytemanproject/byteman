package org.jboss.byteman.agent.check;

public class LoadedClassChecker implements ClassChecker {
    final static Class[] EMPTY = new Class[0];
    boolean isInterface;
    String superName;
    boolean hasOuterClass;
    Class[] interfaces;

    public LoadedClassChecker(Class<?> clazz) {
        isInterface = clazz.isInterface();
        Class superClazz = clazz.getSuperclass();
        superName = (superClazz == null ? null : superClazz.getName());
        // this is not foolproof and probably implies some false positives
        // but if we call getEnclosingClass or getDeclaringClass we get risk tripping a ClassCircularityException
        hasOuterClass = (clazz.getName().contains("$"));
        if (!hasOuterClass) {
            interfaces = clazz.getInterfaces();
        } else {
            interfaces = EMPTY;
        }
    }

    public boolean isInterface() {
        return isInterface;
    }

    public String getSuper() {
        return superName;
    }

    public boolean hasOuterClass() {
        return hasOuterClass;
    }

    public int getInterfaceCount() {
        return interfaces.length;
    }

    public String getInterface(int idx) {
        return interfaces[idx].getName();
    }
}
