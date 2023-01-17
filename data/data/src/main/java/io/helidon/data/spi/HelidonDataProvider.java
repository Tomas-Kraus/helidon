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

package io.helidon.data.spi;

import io.helidon.config.Config;
import io.helidon.data.HelidonData;

/**
 * Helidon Data provider for specific platform.
 * Implementation of this class serves as factory class to build platform specific implementation
 * of {@link io.helidon.data.HelidonData} interface.
 * <p>
 * Methods of this interface will be called in following order:<ol>
 *     <li>{@code configKey}: to get platform specific configuration node key</li>
 *     <li>{@code config}: to provide platform specific configuration node</li>
 *     <li>{@code create}: to build platform specific instance of {@link io.helidon.data.HelidonData} interface</li>
 * </ol>
 */
public interface HelidonDataProvider {

    /**
     * Return platform specific configuration node key.
     * <p>
     * Helidon Data provider configuration node structure example:<pre>
     *     data-repository:
     *         providers:
     *             jpa:
     *                 persistence-unit:
     *                     name: "SamplePU"
     *                     properties:
     *                         eclipselink.ddl-generation: "drop-and-create-tables"
     *                         jakarta.persistence.jdbc.driver: "org.postgresql.Driver"
     *                         jakarta.persistence.jdbc.url: "jdbc:postgresql://localhost:5432/myDatabase"
     *                         jakarta.persistence.jdbc.user: "myUser"
     *                         jakarta.persistence.jdbc.password: "p4ssw0rd"
     * </pre>
     * Returned value of this method is {@code "jpa"} for JPA specific platform provider in the example
     *
     * @return name of the platform specific configuration node name (the last token of the fully-qualified key)
     */
    String configKey();

    /**
     * Process provider specific configuration node.
     * Received node will be the one marked by {@link #configKey()} method. This method will always be called before
     * {@link #create()}. May receive empty node when no provider specific configuration node is missing in the config file.
     *
     * @param config configuration node to process
     */
    void config(Config config);

    /**
     * Create platform specific instance of {@link io.helidon.data.HelidonData} interface.
     *
     * @return new instance of {@link io.helidon.data.HelidonData} interface.
     */
    HelidonData create();

}
