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
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.service.registry.Service;
import io.helidon.testing.junit5.suite.Storage;
import io.helidon.testing.junit5.suite.spi.SuiteProvider;
import io.helidon.testing.junit5.suite.spi.SuiteSupport;

@Weight(Weighted.DEFAULT_WEIGHT + 100)
@Service.Singleton
class ContainerSuiteSupport implements SuiteSupport {

    private static final System.Logger LOGGER = System.getLogger(ContainerSuiteSupport.class.getName());

    // There may exist multiple SuiteProvider instances.
    // Container life-cycle is always bound to a specific SuiteProvider instance.
    private final Map<SuiteProvider, ContainerDescriptor> descriptors;

    ContainerSuiteSupport() {
        this.descriptors = new HashMap<>();
    }

    @Override
    public void init(SuiteProvider provider, Storage storage, SuiteCallSupport callSupport) {
        Container container = provider.getClass().getAnnotation(Container.class);
        // Handle only annotated SuiteProvider instances
        if (container != null) {
            ContainerDescriptor descriptor = ContainerDescriptor.create(container, callSupport);
            descriptors.put(provider, descriptor);
            // Run @BeforeContainerStart annotated methods
            for (Method method : provider.getClass().getMethods()) {
                if (method.isAnnotationPresent(BeforeContainerStart.class)) {
                    descriptor.callMethod(method);
                }
            }
            // Start the container
            descriptor.startContainer();
        }
    }

    @Override
    public void beforeTests(SuiteProvider provider) {
    }

    @Override
    public void afterTests(SuiteProvider provider) {
    }

    @Override
    public void close(SuiteProvider provider) {
        ContainerDescriptor descriptor = descriptors.remove(provider);
        if (descriptor != null) {
            // Stop the container
            descriptor.stopContainer();
        }
    }

    @Override
    public boolean supportsParameter(SuiteProvider provider, Type type) {
        ContainerDescriptor descriptor = descriptors.get(provider);
        return descriptor != null && descriptor.supportsParameter(type);
    }

    @Override
    public Object resolveParameter(SuiteProvider provider, Type type) {
        ContainerDescriptor descriptor = descriptors.get(provider);
        if (descriptor != null) {
            return descriptor.resolveParameter(type);
        }
        throw new IllegalArgumentException(String.format("Cannot resolve parameter Type %s", type.getTypeName()));
    }

}
