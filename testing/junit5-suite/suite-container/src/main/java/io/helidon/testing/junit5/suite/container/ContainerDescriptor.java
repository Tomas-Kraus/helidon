/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.testing.junit5.suite.container;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.suite.container.spi.SuiteContainerSupport;
import io.helidon.testing.junit5.suite.spi.SuiteProvider;
import io.helidon.testing.junit5.suite.spi.SuiteSupport;

import org.testcontainers.containers.GenericContainer;

/**
 * Container descriptor for specific {@link SuiteProvider}.
 */
class ContainerDescriptor {

    // Container factories
    private static final List<SuiteContainerSupport> SUPPORTS = Services.all(SuiteContainerSupport.class);

    private final GenericContainer<?> container;
    private final SuiteSupport.SuiteCallSupport callSupport;

    static ContainerDescriptor create(Container container, SuiteSupport.SuiteCallSupport resolver) {
        GenericContainer<?> genericContainer = createContainer(container.containerClass(), container.image());
        return new ContainerDescriptor(genericContainer, resolver);
    }

    private ContainerDescriptor(GenericContainer<?> container, SuiteSupport.SuiteCallSupport callSupport) {
        this.container = container;
        this.callSupport = callSupport;
    }

    GenericContainer<?> container() {
        return container;
    }

    void startContainer() {
        container.start();
    }

    void stopContainer() {
        container.stop();
    }

    boolean supportsParameter(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        if (GenericContainer.class.isAssignableFrom((Class<?>) type)) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Class<? extends GenericContainer> containerClass = (Class<? extends GenericContainer>) type;
            for (SuiteContainerSupport factory : SUPPORTS) {
                if (factory.supports(containerClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    Object resolveParameter(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        if (GenericContainer.class.isAssignableFrom((Class<?>) type)) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Class<? extends GenericContainer> containerClass = (Class<? extends GenericContainer>) type;
            for (SuiteContainerSupport factory : SUPPORTS) {
                if (factory.supports(containerClass)) {
                    return container;
                }
            }
        }
        throw new IllegalArgumentException(String.format("Cannot resolve parameter Type %s", type.getTypeName()));
    }

    // Call method with resolved parameters.
    // Method invocation is delegated to suite invoke.
    void callMethod(Method method) {
        Type[] types = method.getGenericParameterTypes();
        int count = method.getParameterCount();
        Object[] parameters = new Object[count];
        for (int i = 0; i < count; i++) {
            parameters[i] = resolve(types[i]);
        }
        callSupport.invoke().accept(method, parameters);
    }

    // Resolve single method parameter.
    // Parameter resolution is delegated to suite resolver first.
    private Object resolve(Type type) {
        // Try to resolve parameter by the suite provided resolver
        if (callSupport.supports().apply(type)) {
            return callSupport.resolve().apply(type);
        }
        // Try to resolve parameter by local resolvers
        if (supportsParameter(type)) {
            return resolveParameter(type);
        }
        throw new IllegalArgumentException(String.format("Cannot resolve parameter Type %s", type.getTypeName()));
    }

    private static GenericContainer<?> createContainer(@SuppressWarnings("rawtypes")
                                                       Class<? extends GenericContainer> containerClass,
                                                       String image) {
        for (SuiteContainerSupport factory : SUPPORTS) {
            if (factory.supports(containerClass)) {
                return factory.create(image);
            }
        }
        throw new UnsupportedOperationException(
                "No testcontainers factory was found for " + containerClass.getSimpleName() + " class");
    }

}
