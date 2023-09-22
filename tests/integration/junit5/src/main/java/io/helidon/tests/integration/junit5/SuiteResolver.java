package io.helidon.tests.integration.junit5;

import java.lang.reflect.Type;

/**
 * {@link SuiteResolver} defines API for suite extension providers
 * to dynamically resolve arguments for constructors and methods at runtime.
 */
public interface SuiteResolver {

    /**
     * Determine if this resolver supports resolving of provided parameter {@link Type}.
     *
     * @param type parameter {@link Type} to check
     * @return value of {@code true} if this resolver supports provided type or {@code false} otherwise
     */
    boolean supportsParameter(Type type);

    /**
     * Resolve parameter of provided parameter {@link Type}.
     * This method is only called if {@link #supportsParameter(Type)} previously returned {@code true}
     * for the same {@link Type}.
     * @param type {@link Type} of the parameter to resolve
     * @return resolved parameter value
     */
    Object resolveParameter(Type type);

}
