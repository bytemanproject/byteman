package org.jboss.byteman.agent.check;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * A cache used to allow the association between a class name and the corresponding class in the context
 * of any given class loader to be cached. caching is used to improve performance of the transformer
 * when checking the superclass hierarchy of a class which is a candidate for transformation to identify
 * if it is a target for rules which inject into overriding methods. the cache is a map keyed by classloader
 * whose value is itself a map. each nested map translates a fully qualified class name to an instance of
 * Class. The outer map is a WeakHashMap ensuring that it doe snot hold on to loaders once all references to
 * them have been dropped.
 *
 */
public class LoadCache
{

    public LoadCache(Instrumentation inst)
    {
        this.inst = inst;
    }

    private Instrumentation inst;
    private WeakHashMap<ClassLoader, HashMap<String, Class>> loaderMaps = new WeakHashMap<ClassLoader, HashMap<String, Class>>();
    private HashMap<String, Class> bootMap = new HashMap<String, Class>();

    public Class lookupClass(String name, ClassLoader baseLoader)
    {
        HashMap<String, Class> baseLoaderMap = null;
        Class clazz;

        // see if we have cached the cahe in the map associated with this loader

        if (baseLoader == null) {
            baseLoaderMap = bootMap;
        } else {
            synchronized (loaderMaps) {
                baseLoaderMap = loaderMaps.get(baseLoader);
                if (baseLoaderMap == null) {
                    baseLoaderMap = new HashMap<String, Class>();
                    loaderMaps.put(baseLoader, baseLoaderMap);
                }
            }
        }

        synchronized (baseLoaderMap) {
            clazz = baseLoaderMap.get(name);
        }

        if (clazz != null) {
            return clazz;
        }

        // ok, look it up the hard way

        HashMap<String, Class> loaderMap = baseLoaderMap;
        ClassLoader loader = baseLoader;

        // use a do while loop so we don't omit to look in the bootstrap classpath
        do
        {
            Class[] classes = inst.getInitiatedClasses(loader);
            for (int i = 0; i < classes.length; i++) {
                clazz = classes[i];
                if (clazz.getName().equals(name)) {
                    // ok, install this class in the base map and the map we found it in
                    synchronized (loaderMap) {
                        loaderMap.put(name, clazz);
                        if (loader != baseLoader) {
                            synchronized (baseLoaderMap) {
                                baseLoaderMap.put(name, clazz);
                            }
                        }
                        return clazz;
                    }
                }
            }

            if (loader != null) {
                loader = loader.getParent();
                if (loader == null) {
                    loaderMap = bootMap;
                } else {
                    synchronized (loaderMaps) {
                        loaderMap = loaderMaps.get(loader);
                        if (loaderMap == null) {
                            loaderMap = new HashMap<String, Class>();
                            loaderMaps.put(loader, loaderMap);
                        }
                    }
                }
            }
        } while (loader != null);

        return null;
    }
}
