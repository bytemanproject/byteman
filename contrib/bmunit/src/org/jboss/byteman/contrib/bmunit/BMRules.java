package org.jboss.byteman.contrib.bmunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation attached to a test class or a test method identifying a Byteman rule file to be loaded
 * before running tests and unloaded after running tests.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface BMRules
{
    String value() default "";
}
