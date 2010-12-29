package org.jboss.byteman.contrib.bmunit;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
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
    BMScript classScriptAnnotation;
    BMRules classMultiRuleAnnotation;
    BMRule classSingleRuleAnnotation;
    Class<?> testKlazz;
    String loadDirectory;

    /**
     * Creates a BMUnitRunner to run test in {@code klass}
     *
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed.
     */
    public BMUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        testKlazz = getTestClass().getJavaClass();
        classScriptAnnotation = testKlazz.getAnnotation(BMScript.class);
        classMultiRuleAnnotation = testKlazz.getAnnotation(BMRules.class);
        classSingleRuleAnnotation = testKlazz.getAnnotation((BMRule.class));
        if (classMultiRuleAnnotation != null && classSingleRuleAnnotation != null) {
            throw new InitializationError("Use either BMRule and BMRules annotation but not both");
        }
        loadDirectory = classScriptAnnotation.dir();
        if (loadDirectory.length() == 0) {
            loadDirectory = null;
        }
    }

    /*
     * this loads and unloads the class rules around each test. if we override childrenInvoker then
     * we can load and unload the class rules once only around all tests
    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        // if we have a BMRules annotation on the test class then surround the method block
        // with calls to load and unload the per class rules
        final Statement original =  super.methodBlock(method);
        if (classScriptAnnotation !=null) {
            final String name = computeBMRulesName(classScriptAnnotation.value(), testKlazz);
            return new Statement() {
                public void evaluate() throws Throwable {
                    BMUnit.loadScriptFile(testKlazz, name, loadDirectory);
                    try {
                        original.evaluate();
                    } finally {
                        BMUnit.unloadScriptFile(testKlazz, name);
                    }
                }
            };
        } else {
            return original;
        }
    }
    */

    @Override
    protected Statement childrenInvoker(RunNotifier notifier) {
        Statement statement = super.childrenInvoker(notifier);
        // n.b. we add the wrapper code in reverse order to the preferred order of loading
        // as it works by wrapping around and so execution is  in reverse order to wrapping
        // i.e. this ensures that the class script rules get loaded before any rules specified
        // using BMRule(s) annotations
        statement = addClassSingleRuleLoader(statement, notifier);
        statement = addClassMultiRuleLoader(statement, notifier);
        statement = addClassScriptLoader(statement, notifier);
        return statement;
    }

    protected Statement addClassScriptLoader(final Statement statement, RunNotifier notifier)
    {
        if (classScriptAnnotation == null) {
            return statement;
        } else {
            final String name = computeBMRulesName(classScriptAnnotation.value(), testKlazz);
            final RunNotifier fnotifier = notifier;
            final Description description = Description.createTestDescription(testKlazz, getName(), classScriptAnnotation);
            return new Statement() {
                public void evaluate() throws Throwable {
                    try {
                        BMUnit.loadScriptFile(testKlazz, name, loadDirectory);
                        try {
                            statement.evaluate();
                        } finally {
                            try {
                                BMUnit.unloadScriptFile(testKlazz, name);
                            } catch (Exception e) {
                                fnotifier.fireTestFailure(new Failure(description, e));
                            }
                        }
                    } catch (Exception e) {
                        fnotifier.fireTestFailure(new Failure(description, e));
                    }
                }
            };
        }
    }

    protected Statement addClassMultiRuleLoader(final Statement statement, RunNotifier notifier)
    {
        if (classMultiRuleAnnotation == null) {
            return statement;
        } else {
            final String scriptText = constructScriptText(classMultiRuleAnnotation.rules());
            final RunNotifier fnotifier = notifier;
            final Description description = Description.createTestDescription(testKlazz, getName(), classMultiRuleAnnotation);
            return new Statement() {
                public void evaluate() throws Throwable {
                    try {
                        BMUnit.loadScriptText(testKlazz, null, scriptText);
                        try {
                            statement.evaluate();
                        } finally {
                            try {
                                BMUnit.unloadScriptText(testKlazz, null);
                            } catch (Exception e) {
                                fnotifier.fireTestFailure(new Failure(description, e));
                            }
                        }
                    } catch (Exception e) {
                        fnotifier.fireTestFailure(new Failure(description, e));
                    }
                }
            };
        }
    }

    protected Statement addClassSingleRuleLoader(final Statement statement, RunNotifier notifier)
    {
        if (classSingleRuleAnnotation == null) {
            return statement;
        } else {
            final String scriptText = constructScriptText(new BMRule[] {classSingleRuleAnnotation});
            final RunNotifier fnotifier = notifier;
            final Description description = Description.createTestDescription(testKlazz, getName(), classSingleRuleAnnotation);
            return new Statement() {
                public void evaluate() throws Throwable {
                    try {
                        BMUnit.loadScriptText(testKlazz, null, scriptText);
                        try {
                            statement.evaluate();
                        } finally {
                            try {
                                BMUnit.unloadScriptText(testKlazz, null);
                            } catch (Exception e) {
                                fnotifier.fireTestFailure(new Failure(description, e));
                            }
                        }
                    } catch (Exception e) {
                        fnotifier.fireTestFailure(new Failure(description, e));
                    }
                }
            };
        }
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test)
    {
        Statement statement = super.methodInvoker(method, test);
        // n.b. we add the wrapper code in reverse order to the preferred order of loading
        // as it works by wrapping around and so execution is in reverse order to wrapping
        // i.e. this ensures that the method script rules get loaded before any rules specified
        // using BMRule(s) annotations
        statement = addMethodSingleRuleLoader(statement, method);
        statement = addMethodMultiRuleLoader(statement, method);
        statement = addMethodScriptLoader(statement, method);
        return statement;
    }

    /**
     * wrap the test method execution statement with the necessary load and unload calls if it has
     * a BMScript annotation
     * @param statement
     * @param method
     * @return
     */
    protected Statement addMethodScriptLoader(final Statement statement, FrameworkMethod method)
    {
        BMScript annotation = method.getAnnotation(BMScript.class);
        if (annotation == null) {
            return statement;
        } else {
            // ensure we always have an actual name here instead of null because using
            // null will clash with the name used for looking up rules when the clas
            // has a BMRules annotation
            final String name = computeBMRulesName(annotation.value(), method);
            final String loadDirectory = computeLoadDirectory(annotation.dir(), this.loadDirectory);
            return new Statement() {
                public void evaluate() throws Throwable {
                    BMUnit.loadScriptFile(testKlazz, name, loadDirectory);
                    try {
                        statement.evaluate();
                    } finally {
                        BMUnit.unloadScriptFile(testKlazz, name);
                    }
                }
            };
        }
    }

    /**
     * wrap the test method execution statement with the necessary load and unload calls if it has
     * a BMRules annotation
     * @param statement
     * @param method
     * @return
     */
    protected Statement addMethodMultiRuleLoader(final Statement statement, FrameworkMethod method)
    {
        BMRules annotation = method.getAnnotation(BMRules.class);
        if (annotation == null) {
            return statement;
        } else {
            final String name = method.getName();
            final String script = constructScriptText(annotation.rules());
            return new Statement() {
                public void evaluate() throws Throwable {
                    BMUnit.loadScriptText(testKlazz, name, script);
                    try {
                        statement.evaluate();
                    } finally {
                        BMUnit.unloadScriptText(testKlazz, name);
                    }
                }
            };
        }
    }

    /**
     * wrap the test method execution statement with the necessary load and unload calls if it has
     * a BMRule annotation
     * @param statement
     * @param method
     * @return
     */
    protected Statement addMethodSingleRuleLoader(final Statement statement, FrameworkMethod method)
    {
        BMRule annotation = method.getAnnotation(BMRule.class);
        if (annotation == null) {
            return statement;
        } else {
            final String name = method.getName();
            final String script = constructScriptText(new BMRule[] {annotation });
            return new Statement() {
                public void evaluate() throws Throwable {
                    BMUnit.loadScriptText(testKlazz, name, script);
                    try {
                        statement.evaluate();
                    } finally {
                        BMUnit.unloadScriptText(testKlazz, name);
                    }
                }
            };
        }
    }

    /**
     * construct the text of a rule script from a  set of BMRule annotations
     * @param bmRules
     * @return
     */
    protected String constructScriptText(BMRule[] bmRules) {
        StringBuilder builder = new StringBuilder();
        builder.append("# BMUnit autogenerated script ");
        for (BMRule bmRule : bmRules) {
            builder.append("\nRULE ");
            builder.append(bmRule.name());
            if (bmRule.isInterface()) {
                builder.append("\nINTERFACE ");
            } else {
                builder.append("\nCLASS ");
            }
            if (bmRule.isOverriding()) {
                builder.append("^");
            }
            builder.append(bmRule.targetClass());
            builder.append("\nMETHOD ");
            builder.append(bmRule.targetMethod());
            String location = bmRule.targetLocation();
            if (location  !=  null && location.length() > 0) {
                builder.append("\nAT ");
                builder.append(location);
            }
            String helper = bmRule.helper();
            if (helper  !=  null && helper.length() > 0) {
                builder.append("\nHELPER ");
                builder.append(helper);
            }
            builder.append("\nIF ");
            builder.append(bmRule.condition());
            builder.append("\nDO ");
            builder.append(bmRule.action());
            builder.append("\nENDRULE\n");
        }
        return builder.toString();
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

    protected String computeLoadDirectory(String methodAnnotationDir, String classAnnotationDir)
    {
        if (methodAnnotationDir != null && methodAnnotationDir.length() > 0) {
            return methodAnnotationDir;
        }
        return classAnnotationDir;
    }
}