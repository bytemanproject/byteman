package org.jboss.byteman.modules;

public interface ModuleSystem <CL extends ClassLoader>
{
    void initialize(String args);
    CL createLoader(ClassLoader triggerLoader, String[] imports);
    void destroyLoader(CL helperLoader);

    /**
     * dynamically load and return a generated helper adapter classes using a custom classloader derived from the
     * trigger class's loader
     * @param triggerClassLoader the class loader of the trigger class which has been matched with this
     * helper class's rule
     * @param helperAdapterName the name of the helper adapter class to be loaded
     * @param classBytes the byte array defining the class
     * @return the new helper class
     */
    Class<?> loadHelperAdapter(CL helperLoader, String helperName, byte[] helperBytes);
}
