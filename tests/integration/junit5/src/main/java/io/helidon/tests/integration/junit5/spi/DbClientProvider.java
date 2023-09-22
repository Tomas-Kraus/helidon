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
package io.helidon.tests.integration.junit5.spi;

import java.util.Optional;

import io.helidon.dbclient.DbClient;
import io.helidon.tests.integration.junit5.Junit5ExtensionProvider;

public interface DbClientProvider extends Junit5ExtensionProvider {

    /**
     * Shared suite context storage key to store configuration if defined.
     *
     * @param configKey suite context storage key to store configuration
     */
    void configKey(Optional<String> configKey);

    /**
     * Shared suite context storage key to store mapped database container port.
     *
     * @param portKey suite context storage key to store mapped database container port
     */
    void portKey(Optional<String> portKey);

    /**
     * Build configuration builder with default initial values.
     */
    void setup();

    /**
     * Provide config builder to be used in setup hook.
     *
     * @return configuration builder with values from provided file.
     */
    DbClient.Builder builder();

    /**
     * Start the existence of {@link DbClient}.
     */
    void start();

    /**
     * Provide root {@link DbClient} instance for the tests.
     */
    DbClient dbClient();

    /**
     * Cast {@link Junit5ExtensionProvider} as {@link DbClientProvider}.
     * Implementing class should override this method and add itself.
     *
     * @param cls {@link Junit5ExtensionProvider} child interface or implementing class
     * @return this instance cast to {@link DbClientProvider} and optionally implementing class
     * @param <T> target casting type
     */
    @Override
    default <T extends Junit5ExtensionProvider> T as(Class<T> cls) {
        if (cls == DbClientProvider.class) {
            return cls.cast(this);
        }
        throw new IllegalArgumentException(
                String.format("Cannot cast this DbClientProvider implementation as %s", cls.getName()));
    }

}
