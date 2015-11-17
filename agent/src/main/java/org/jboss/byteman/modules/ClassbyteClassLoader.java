package org.jboss.byteman.modules;

/**
 * this is a classloader used to define classes from bytecode
 */
public class ClassbyteClassLoader extends ClassLoader
{
    public ClassbyteClassLoader(ClassLoader cl)
    {
        super(cl);
    }

    public Class<?> addClass(String name, byte[] bytes)
            throws ClassFormatError
    {
        Class<?> cl = defineClass(name, bytes, 0, bytes.length);
        resolveClass(cl);

        return cl;
    }
}