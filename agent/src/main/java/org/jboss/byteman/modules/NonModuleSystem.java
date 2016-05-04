package org.jboss.byteman.modules;

import org.jboss.byteman.rule.helper.Helper;

public class NonModuleSystem implements ModuleSystem<ClassbyteClassLoader>
{

    public void initialize(String args)
    {
        if (!args.isEmpty())
            Helper.err("Unexpcted module system arguments: " + args);
    }

    public ClassbyteClassLoader createLoader(ClassLoader triggerClassLoader, String[] imports)
    {
        if (imports.length > 0) {
            reportUnexpectedImports(imports);
        }

        // create the helper class in a classloader derived from the trigger class
        // this allows the injected code to refer to the triggger class type and related
        // application types. the default helper will be accessible because it is loaded by the
        // bootstrap loader. custom helpers need to be made available to the application either
        // by deployng them with it or by locating them in the JVM classpath.
        return new ClassbyteClassLoader(triggerClassLoader);
    }

    public void destroyLoader(ClassbyteClassLoader helperLoader)
    {
        // do nothing
    }

    public Class<?> loadHelperAdapter(ClassbyteClassLoader helperLoader, String helperAdapterName, byte[] classBytes)
    {
        return helperLoader.addClass(helperAdapterName, classBytes);
    }

    protected void reportUnexpectedImports(String[] imports)
    {
        throw new IllegalArgumentException("Using IMPORT requires a module system");
    }
}
