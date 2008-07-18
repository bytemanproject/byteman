package org.jboss.jbossts.orchestration.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to tag classes which implement event handler code
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
    String targetClass();
    String targetMethod() default "";
    int targetLine() default -1;
    String event() default "";
    String condition() default "";
    String action() default "";
}