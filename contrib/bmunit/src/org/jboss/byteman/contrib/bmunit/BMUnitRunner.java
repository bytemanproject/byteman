package org.jboss.byteman.contrib.bmunit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Specialisation of the BlockJUnit4ClassRunner Runner class which can be attached to a text class
 * using the @RunWith annotation. It ensures that Byteman rules are loaded and unloaded for tests
 * which are annotated with an @Byteman annotation
 */
public class BMUnitRunner extends BlockJUnit4ClassRunner
{
    BMScript classAnnotation;
    Class<?> testKlazz;

    /**
     * Creates a BMUnitRunner to run test in {@code klass}
     *
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed.
     */
    public BMUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        testKlazz = getTestClass().getJavaClass();
        classAnnotation = testKlazz.getAnnotation(BMScript.class);
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        // if we have a BMRules annotation on the test class then surround the method block
        // with calls to load and unload the per class rules
        final Statement original =  super.methodBlock(method);
        if (classAnnotation !=null) {
            final String name = computeBMRulesName(classAnnotation.value(), testKlazz);
            return new Statement() {
                public void evaluate() throws Throwable {
                    BMUnit.loadTestScript(testKlazz, name);
                    try {
                        original.evaluate();
                    } finally {
                        BMUnit.unloadTestScript(testKlazz, name);
                    }
                }
            };
        } else {
            return original;
        }
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        final Statement original = super.methodInvoker(method, test);
        BMScript annotation = method.getAnnotation(BMScript.class);
        if (annotation != null) {
            // ensure we always have an actual name here instead of null because using
            // null will clash with the name used for looking up rules when the clas
            // has a BMRules annotation
            final String name = computeBMRulesName(annotation.value(), method);
            return new Statement() {
                public void evaluate() throws Throwable {
                    BMUnit.loadTestScript(testKlazz, name);
                    try {
                        original.evaluate();
                    } finally {
                        BMUnit.unloadTestScript(testKlazz, name);
                    }
                }
            };
        } else {
            return original;
        }
    }

    /**
     * method which computes the name of the BMRules file for a method test if it is not supplied in the
     * method annotation
     * @param name the value supplied in the annotation or "" if it has been defaulted
     * @param method the Framework method annotated with an @BMRules annotation
     * @return by default this returns the annotation value or the the bare method name if the annotation
     * value is null or empty
     */
    protected String computeBMRulesName(String name, FrameworkMethod method)
    {
        // if the annotation has a real name  then use  it
        if (name != null && name.length() > 0) {
            return name;
        }
        // use the method name

        return method.getName();
    }

    /**
     * method which computes the name of the BMRules file for a test class if it is not supplied in the
     * class annotation
     * @param name the value supplied in the annotation or "" if it has been defaulted
     * @param testClass the test class annotated with an @BMRules annotation
     * @return by default this returns the annotation value or null if the annotation value is null or empty.
     */
    protected String computeBMRulesName(String name, Class<?> testClass)
    {
        if (name != null && name.length() > 0) {
            return name;
        }

        return null;
    }
}