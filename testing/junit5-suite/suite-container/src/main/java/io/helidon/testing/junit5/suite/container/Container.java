package io.helidon.testing.junit5.suite.container;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.testcontainers.containers.GenericContainer;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Container {

    /**
     * Test containers class.
     *
     * @return the test containers class
     */
    @SuppressWarnings("rawtypes")
    Class<? extends GenericContainer> containerClass();

    /**
     * Docker image name.
     * Value is passed to {@link org.testcontainers.utility.DockerImageName#parse(String)} method.
     *
     * @return the Docker image name
     */
    String image();

}
