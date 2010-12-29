package org.jboss.byteman.contrib.bmunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to attach a Byteman rule to a class or method
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface BMRule
{
    String name();
    boolean isInterface() default false;
    boolean isOverriding() default false;
    String targetClass();
    String targetMethod();
    String targetLocation() default "";
    String helper() default "";
    String binding() default "";
    String condition() default "TRUE";
    String  action() default "NOTHING";
}
