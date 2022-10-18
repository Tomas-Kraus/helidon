/*
 * Copyright (c) 2022 Oracle and/or its affiliates.
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
package io.helidon.data.builder.query.transform;

import io.helidon.common.HelidonServiceLoader;
import io.helidon.data.builder.RepositoryBuilderException;

import java.util.List;
import java.util.ServiceLoader;

/**
 * Manage registered implementations of Query Transformation API.
 */
class QueryTransformManager {

    // Static reference holder.
    private static class Instance {

        private static final List<QueryTransformationProvider> PROVIDERS = initProviders();

        private static List<QueryTransformationProvider> initProviders() {
            final HelidonServiceLoader<QueryTransformationProvider> serviceLoader = HelidonServiceLoader
                    .builder(ServiceLoader.load(QueryTransformationProvider.class))
                    .build();
            return serviceLoader.asList();
        }

    }

    /**
     * Returns an instance of Query Transformation API implementations provider.
     * Without name, it just grabs first available provider.
     *
     * @return an instance of Query Transformation API implementations provider
     */
    static final QueryTransformationProvider provider() {
        if (Instance.PROVIDERS.isEmpty()) {
            throw new RepositoryBuilderException("No Query Transformation API implementation was found.");
        }
        return Instance.PROVIDERS.get(0);
    }

    /**
     * Returns an instance of Query Transformation API implementations provider with specified name.
     *
     * @param name name of the Query Transformation API implementation to return
     * @return an instance of Query Transformation API implementations provider
     */
    static final QueryTransformationProvider provider(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name of Query Transformation API implementations provider is null.");
        }
        for (QueryTransformationProvider provider : Instance.PROVIDERS) {
            if (name.equals(provider.name())) {
                return provider;
            }
        }
        throw new RepositoryBuilderException(
                String.format("No Query Transformation API implementation with \"%s\" name was found.", name));
    }

}
