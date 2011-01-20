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
    BMScript classSingleScriptAnnotation;
    BMScripts classMultiScriptAnnotation;
    BMRules classMultiRuleAnnotation;
    BMRule classSingleRuleAnnotation;
    Class<?> testKlazz;
    private final BMRunnerUtil BMRunnerUtil = new BMRunnerUtil();

    /**
     * Creates a BMUnitRunner to run test in {@code klass}
     *
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed.
     */
    public BMUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        testKlazz = getTestClass().getJavaClass();
        classSingleScriptAnnotation = testKlazz.getAnnotation(BMScript.class);
        classMultiScriptAnnotation = testKlazz.getAnnotation(BMScripts.class);
        classMultiRuleAnnotation = testKlazz.getAnnotation(BMRules.class);
        classSingleRuleAnnotation = testKlazz.getAnnotation((BMRule.class));
        if (classMultiRuleAnnotation != null && classSingleRuleAnnotation != null) {
            throw new InitializationError("Use either BMRule or BMRules annotation but not both");
        }
        if (classMultiScriptAnnotation != null && classSingleScriptAnnotation != null) {
            throw new InitializationError("Use either BMScript or BMScripts annotation but not both");
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
        if (classSingleScriptAnnotation !=null) {
            final String name = computeBMScriptName(classSingleScriptAnnotation.value());
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
        statement = addClassSingleScriptLoader(statement, notifier);
        statement = addClassMultiScriptLoader(statement, notifier);
        return statement;
    }

    protected Statement addClassSingleScriptLoader(final Statement statement, RunNotifier notifier)
    {
        if (classSingleScriptAnnotation == null) {
            return statement;
        } else {
            final String name = BMRunnerUtil.computeBMScriptName(classSingleScriptAnnotation.value());
            final RunNotifier fnotifier = notifier;
            final Description description = Description.createTestDescription(testKlazz, getName(), classSingleScriptAnnotation);
            final String loadDirectory = BMRunnerUtil.normaliseLoadDirectory(classSingleScriptAnnotation);
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

    protected Statement addClassMultiScriptLoader(final Statement statement, RunNotifier notifier)
    {
        if (classMultiScriptAnnotation == null) {
            return statement;
         } else {
            BMScript[] scriptAnnotations = classMultiScriptAnnotation.scripts();
            Statement result = statement;
            // note we iterate down here because we generate statements by wraparound
            // which means the the outer statement gets executed first
            for (int i = scriptAnnotations.length; i> 0; i--) {
                BMScript scriptAnnotation= scriptAnnotations[i - 1];
                final String name = BMRunnerUtil.computeBMScriptName(scriptAnnotation.value());
                final RunNotifier fnotifier = notifier;
                final Description description = Description.createTestDescription(testKlazz, getName(), scriptAnnotation);
                final String loadDirectory = BMRunnerUtil.normaliseLoadDirectory(scriptAnnotation);
                final Statement nextStatement = result;
                result = new Statement() {
                    public void evaluate() throws Throwable {
                        try {
                            BMUnit.loadScriptFile(testKlazz, name, loadDirectory);
                            try {
                                nextStatement.evaluate();
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
            return result;
        }
    }

    protected Statement addClassMultiRuleLoader(final Statement statement, RunNotifier notifier)
    {
        if (classMultiRuleAnnotation == null) {
            return statement;
        } else {
            final String scriptText = BMRunnerUtil.constructScriptText(classMultiRuleAnnotation.rules());
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
            final String scriptText = BMRunnerUtil.constructScriptText(new BMRule[]{classSingleRuleAnnotation});
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
        statement = addMethodSingleScriptLoader(statement, method);
        statement = addMethodMultiScriptLoader(statement, method);
        return statement;
    }

    /**
     * wrap the test method execution statement with the necessary load and unload calls if it has
     * a BMScript annotation
     * @param statement
     * @param method
     * @return
     */
    protected Statement addMethodSingleScriptLoader(final Statement statement, FrameworkMethod method)
    {
        BMScript annotation = method.getAnnotation(BMScript.class);
        if (annotation == null) {
            return statement;
        } else {
            // ensure we always have an actual name here instead of null because using
            // null will clash with the name used for looking up rules when the clas
            // has a BMRules annotation
            final String name = BMRunnerUtil.computeBMScriptName(annotation.value(), method.getMethod());
            final String loadDirectory = BMRunnerUtil.normaliseLoadDirectory(annotation);
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
     * a BMScripts annotation
     * @param statement
     * @param method
     * @return
     */
    protected Statement addMethodMultiScriptLoader(final Statement statement, FrameworkMethod method)
    {
        BMScripts scriptsAnnotation = method.getAnnotation(BMScripts.class);
        if (scriptsAnnotation == null) {
            return statement;
        } else {
            BMScript[] scriptAnnotations = scriptsAnnotation.scripts();
            Statement result = statement;
            // note we iterate down here because we generate statements by wraparound
            // which means the the outer statement gets executed first
            for (int i = scriptAnnotations.length; i> 0; i--) {
                BMScript scriptAnnotation = scriptAnnotations[i - 1];
                final Statement nextStatement = result;
                // ensure we always have an actual name here instead of null because using
                // null will clash with the name used for looking up rules when the clas
                // has a BMRules annotation
                final String name = BMRunnerUtil.computeBMScriptName(scriptAnnotation.value(), method.getMethod());
                final String loadDirectory = BMRunnerUtil.normaliseLoadDirectory(scriptAnnotation);
                result = new Statement() {
                    public void evaluate() throws Throwable {
                        BMUnit.loadScriptFile(testKlazz, name, loadDirectory);
                        try {
                            nextStatement.evaluate();
                        } finally {
                            BMUnit.unloadScriptFile(testKlazz, name);
                        }
                    }
                };
            }
            return result;
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
            final String script = BMRunnerUtil.constructScriptText(annotation.rules());
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
            final String script = BMRunnerUtil.constructScriptText(new BMRule[]{annotation});
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
}