/*
 * Copyright (c) 2019, 2022 Oracle and/or its affiliates.
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
package io.helidon.data;


import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

import io.helidon.common.HelidonServiceLoader;
import io.helidon.config.Config;
import io.helidon.data.repository.GenericRepository;
import io.helidon.data.spi.HelidonDataProvider;

/**
 * Helidon Data Repository.
 */
public interface HelidonData {

    /**
     * Creates an instance of data repository.
     *
     * @param repositoryClass data repository interface or abstract class to create
     * @return new data repository instance
     * @param <E> type of the entity
     * @param <ID> type of the ID
     * @param <T> target data repository type
     */
    <E, ID, T extends GenericRepository<E, ID>> T repository(Class <? super T> repositoryClass);

    /**
     * Execute provided task as database transaction.
     * Task computes and returns result.
     *
     * @param task task to run in transaction
     * @return computed task result
     * @param <T> the result type of the task
     * @throws Exception when result computation failed
     */
    <T> T transaction(Callable<T> task) throws Exception;

    /**
     * Task that does not return a result but may throw an exception.
     * Implementors define a single method with no arguments called {@link #call()}.
     */
    @FunctionalInterface
    interface VoidCallable {
        /**
         * Executes the task. Throws an exception if unable to do so.
         *
         * @throws Exception when unable to compute a result
         */
        void call() throws Exception;
    }

    /**
     * Execute provided task as database transaction.
     * Task does not return any result.
     *
     * @param task task to run in transaction
     * @throws Exception when task computation failed
     */
    void transaction(VoidCallable task) throws Exception;

    /**
     * Create new instance of Helidon Data Repository builder.
     *
     * @return Helidon Data Repository builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Create new instance of Helidon Data Repository.
     *
     * @param config Helidon Data Repository specific configuration node.
     * @return an instance of Helidon Data Repository.
     */
    static HelidonData create(Config config) {
        return builder().config(config).build();
    }

    class Builder implements io.helidon.common.Builder<Builder, HelidonData> {

        private final HelidonServiceLoader.Builder<HelidonDataProvider> dataProviders;
        private Config providersConfig;

        private Builder() {
            this.dataProviders = HelidonServiceLoader.builder(ServiceLoader.load(HelidonDataProvider.class));
            this.providersConfig = Config.empty();
        }

        /**
         * Update this builder from Helidon Data Repository specific configuration node.
         *
         * @param config configuration node to use
         * @return updated builder
         */
        public Builder config(Config config) {
            // Store providers config node for later usage.
            providersConfig = config.get("providers");
            return this;
        }

        @Override
        public HelidonData build() {
            // Evaluate providers from service loader
            List<HelidonDataProvider> providers = dataProviders.build().asList();
            if (providers.isEmpty()) {
                throw new DataException("No Helidon Data Repository providers found.");
            }
            // Grab 1st provider and build HelidonData instance
            HelidonDataProvider provider = providers.get(0);
            provider.config(providersConfig.get(provider.configKey()));
            return provider.create();
        }

    }

}
