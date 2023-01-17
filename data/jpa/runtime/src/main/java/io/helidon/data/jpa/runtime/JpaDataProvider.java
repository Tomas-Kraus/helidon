/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
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

package io.helidon.data.jpa.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.helidon.config.Config;
import io.helidon.data.HelidonData;
import io.helidon.data.DataException;
import io.helidon.data.spi.HelidonDataProvider;

/**
 * JPA specific Helidon Data provider.
 */
public class JpaDataProvider implements HelidonDataProvider {

    // JPA specific Helidon Data provider configuration node name.
    private static final String CONFIG_KEY = "jpa";

    // Persistence unit name. It has to be set by the builder or config method.
    private String puName = null;

    // Persistence unit properties.
    private Map<String, String> puProperties;

    /**
     * Creates an instance of JPA specific Helidon Data provider.
     *
     * @deprecated to be used solely by {@link java.util.ServiceLoader}
     */
    @Deprecated
    public JpaDataProvider() {
        // Initial value is null, but it has to be set by the builder or config method.
        this.puName = null;
        // Persistence unit properties Map is initialized as empty.
        this.puProperties = new HashMap<>();
    }

    private JpaDataProvider(String puName, Map<String, String> puProperties) {
        this.puName = puName;
        this.puProperties = puProperties;
    }

    @Override
    public String configKey() {
        return CONFIG_KEY;
    }

    @Override
    public void config(Config config) {
        config.get("persistence-unit")
                .asNodeList()
                .orElseGet(List::of)
                .forEach(puConfig -> {
                    switch (puConfig.name()) {
                        // Persistence unit name.
                        case "name" -> puConfig.asString().ifPresent(this::puName);
                        // Persistence unit properties.
                        case "properties" -> puConfig
                                .asNodeList()
                                .orElseGet(List::of)
                                .forEach(propConfig -> puProperties.put(propConfig.name(), propConfig.asString().get()));
                    }
                });
    }

    @Override
    public HelidonData create() {
        // Persistence unit name is mandatory in JPA. Null value at this stage means it was not set by builder or from config.
        if (puName == null) {
            throw new DataException("Name of the JPA persistence unit was not set");
        }
        // Target instance shall contain immutable instances.
        return new JpaHelidonData(new JpaContext(puName, Map.copyOf(puProperties)));
    }

    // Persistence unit name setter.
    private void puName(String puName) {
        this.puName = puName;
    }

    /**
     * Fluent API builder for JPA specific Helidon Data provider.
     */
    public final class Builder implements io.helidon.common.Builder<Builder, JpaDataProvider> {

        // Persistence unit name.
        private String puName;

        // Persistence unit properties.
        private Map<String, String> puProperties;

        private Builder() {
            this.puName = null;
            this.puProperties = new HashMap<>();
        }

        /**
         * Configure name of the JPA persistence unit name.
         * This value is mandatory for the JPA specific Helidon Data provider. Value must be set by this builder
         * or in the configuration file.
         *
         * @param name name of the JPA persistence unit name
         * @return updated builder
         */
        public Builder puName(String name) {
            Objects.requireNonNull(name, "Value of the persistence unit is null.");
            puName = name;
            return this;
        }

        /**
         * Configure JPA persistence unit property.
         *
         * @param name name of the property
         * @param value value of the property
         * @return updated builder
         */
        public Builder puProperty(String name, String value) {
            Objects.requireNonNull(name, "Value of the persistence unit property name is null.");
            Objects.requireNonNull(value, "Value of the persistence unit property value is null.");
            puProperties.put(name, value);
            return this;
        }

        /**
         * Configure JPA persistence unit properties.
         *
         * @param properties {@link Map} with the properties
         * @return updated builder
         */
        public Builder puProperties(Map<String, String> properties) {
            Objects.requireNonNull(properties, "Value of the persistence unit properties Map is null.");
            puProperties.putAll(properties);
            return this;
        }

        @Override
        public JpaDataProvider build() {
            // Allow null value of JPA persistence unit name. It may come from config directly to the JpaDataProvider.
            // Properties map may be modified from config too, so it shall not be immutable yet.
            return new JpaDataProvider(puName, puProperties);
        }

    }

}
