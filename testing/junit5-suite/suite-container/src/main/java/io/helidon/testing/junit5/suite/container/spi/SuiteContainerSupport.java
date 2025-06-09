package io.helidon.testing.junit5.suite.container.spi;

import org.testcontainers.containers.GenericContainer;

/**
 * Suite Docker container support for specific test containers class extending {@link GenericContainer}.
 */
public interface SuiteContainerSupport {

    /**
     * Check whether factory supports container initialization for specific {@link GenericContainer}
     * child class.

     * @param containerClass {@link GenericContainer} child class to check
     * @return value of {@code true} when factory supports container initialization for provided {@link GenericContainer} child
     *         class or {@code false} otherwise
     */
    @SuppressWarnings("rawtypes")
    boolean supports(Class<? extends GenericContainer> containerClass);

    /**
     * Create {@link GenericContainer} child class instance.
     */
    GenericContainer<?> create(String image);

}
