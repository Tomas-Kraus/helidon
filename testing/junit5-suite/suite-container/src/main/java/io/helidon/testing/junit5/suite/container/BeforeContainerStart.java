package io.helidon.testing.junit5.suite.container;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Before Docker container start method.
 * Allows additional container configuration before container is started.
 * This method is being executed before the methods annotated with {@link io.helidon.testing.junit5.suite.BeforeSuite}
 * are executed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface BeforeContainerStart {
}
