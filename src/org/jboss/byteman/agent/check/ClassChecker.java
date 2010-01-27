package org.jboss.byteman.agent.check;

/**
 * interface hiding how we check the names of a class's super, outer class and implemented interfaces.
 */
public interface ClassChecker {
    /**
     * see if the checked class is an interface or really a class
     * @return true if the checked class is an interface and false if it is really a class
     */
    public boolean isInterface();

    /**
     * identify the name of the super class for the checked class
     * @return the name of the super class for the checked class
     */
    public String getSuper();

    /**
     * identify if the checked class is embedded in an outer class
     * @return true if the checked class is embedded in an outer class otherwise false
     */
    public boolean hasOuterClass();

    /**
     * identify how many interfaces are in the implements list of this class
     * @return how many interfaces are in the implements list of this class
     */
    public int getInterfaceCount();

    /**
     * identify the name of a specific interface in the implements list of this class
     * @param idx the index of the interface in the list
     * @return the name of a specific interface in the implements list of this class
     */
    public String getInterface(int idx);
}