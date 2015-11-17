package byteman.tests.modules.jbossmodules;

import byteman.tests.Test;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

public class TestTrampoline extends Test
{
    private static final String TEST_MODULE = System.getProperty("modulartest.module");
    private static final String TEST_CLASS = System.getProperty("modulartest.class");

    public TestTrampoline()
    {
        super(TestTrampoline.class.getCanonicalName());
    }

    public void test() throws Throwable {
        ModuleLoader bootModuleLoader = Module.getBootModuleLoader();

        Module module = bootModuleLoader.loadModule(ModuleIdentifier.create(TEST_MODULE));
        Class/*<Test>*/ klass = module.getClassLoader().loadClass(TEST_CLASS);
        Object/*Test*/ test = klass.newInstance();
        klass.getMethod("setName", String.class).invoke(test,  "test");
        Object/*TestResult*/ result = klass.getMethod("runBare").invoke(test);
        // TODO: reports the results properly
    }
}
