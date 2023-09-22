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
package io.helidon.tests.integration.junit5;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.helidon.config.Config;
import io.helidon.tests.integration.junit5.spi.ContainerProvider;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class MySqlContainer implements ContainerProvider {

    private static final String DEFAULT_IMAGE = "mysql:latest";
    private static final String ROOT_PASSWORD = "R00t_P4ssw0rd";
    private static final String USER = "user";
    private static final String PASSWORD = "p4ssw0rd";
    private static final String DATABASE = "dbclient";
    private static final int PORT = 3306;

    /** @noinspection OptionalUsedAsFieldOrParameterType*/
    private Optional<String> image;
    private Optional<String> configKey;
    private Optional<String> portKey;
    private ContainerConfig.Builder builder;
    private GenericContainer<?> container;
    private SuiteContext suiteContext;

    public MySqlContainer() {
        image = Optional.empty();
        configKey = Optional.empty();
        portKey = Optional.empty();
        builder = null;
        container = null;
        suiteContext = null;
    }

    @Override
    public void suiteContext(SuiteContext suiteContext) {
        this.suiteContext = suiteContext;
    }

    @Override
    public void image(Optional<String> image) {
        this.image = image;
    }

    @Override
    public void configKey(Optional<String> configKey) {
        this.configKey = configKey;
    }

    @Override
    public void portKey(Optional<String> portKey) {
        this.portKey = portKey;
    }


    @Override
    public void setup() {
        Map<String, String> environment = new HashMap<>();
        builder = ContainerConfig.builder()
                .image(image.orElse(DEFAULT_IMAGE));
        // Use DbClient configuration node if present in the config with default values as fallback
        if (configKey.isPresent()) {
            Config config = suiteContext.storage().get(configKey.get(), Config.class);
            if (config != null) {
                config.get("db.connection").asNode().ifPresentOrElse(
                        connectionConfig -> {
                            System.out.println("DbClient setup node \"db.connection\" was found in config, using it for MySQL setup");
                            environment.put("MYSQL_ROOT_PASSWORD", ROOT_PASSWORD);
                            connectionConfig.get("username").asString().ifPresentOrElse(
                                    username -> environment.put("MYSQL_USER", username),
                                    () -> environment.put("MYSQL_USER", USER));
                            connectionConfig.get("password").asString().ifPresentOrElse(
                                    password -> environment.put("MYSQL_PASSWORD", password),
                                    () -> environment.put("MYSQL_PASSWORD", PASSWORD));
                            connectionConfig.get("url").asString().ifPresentOrElse(
                                    url -> builder = parseUrlToMySqlContainerBuilder(url, environment, builder),
                                    () -> {
                                        environment.put("MYSQL_DATABASE", DATABASE);
                                        builder = builder.exposedPorts(new int[] {PORT});
                                    });
                        },
                        () -> {
                            System.out.println("DbClient setup node \"db.connection\" was not found in config, using default values");
                            defaultConfig(environment);
                        }
                );
            } else {
                throw new IllegalStateException(
                        String.format("Configuration was not found in the suite context storage with key %s",
                                      configKey.get()));
            }
        } else {
            System.out.println("Using default values for MySQL database");
            defaultConfig(environment);
        }
        builder = builder.environment(environment);
    }

    @Override
    public ContainerConfig.Builder builder() {
        return builder;
    }

    @Override
    public void start() {
        ContainerConfig config = builder.build();
        System.out.printf("Starting MySQL database from image %s%n", config.image());
        System.out.printf("Container configuration: %s%n", config.toString());
        container = new GenericContainer<>(DockerImageName.parse(config.image()));
        config.environment().forEach(container::withEnv);
        container.addExposedPorts(config.exposedPorts());
        container.start();
        int dbPort = container.getMappedPort(config.exposedPorts()[0]);
        System.out.printf("MySQL database is listening on localhost:%d%n", dbPort);
        if (portKey.isPresent()) {
            suiteContext.storage().put(portKey.get(), dbPort);
            System.out.printf("MySQL database port stored as \"%s\"%n", portKey.get());
        }
    }

    @Override
    public void stop() {
        System.out.println("Stopping MySQL database");
        container.stop();
    }

    @Override
    public <T extends Junit5ExtensionProvider> T as(Class<T> cls) {
        if (cls == ContainerProvider.class || cls == MySqlContainer.class) {
            return cls.cast(this);
        }
        throw new IllegalArgumentException(
                String.format("Cannot cast this ContainerProvider implementation as %s", cls.getName()));
    }

    // Set default MySQL configuration
    private void defaultConfig(Map<String, String> environment) {
        environment.put("MYSQL_ROOT_PASSWORD", ROOT_PASSWORD);
        environment.put("MYSQL_USER", USER);
        environment.put("MYSQL_PASSWORD", PASSWORD);
        environment.put("MYSQL_DATABASE", DATABASE);
        builder = builder.exposedPorts(new int[] {PORT});
    }

    private static ContainerConfig.Builder parseUrlToMySqlContainerBuilder(String url,
                                                                           Map<String, String> environment,
                                                                           ContainerConfig.Builder builder) {
        URI uri = uriFromDbUrl(url);
        String dbName = dbNameFromUri(uri);
        int port = uri.getPort();
        environment.put("MYSQL_DATABASE", dbName);
        return builder.exposedPorts(new int[] {port});
    }

    private static URI uriFromDbUrl(String url) {
        int separator = url.indexOf(':'); // 4
        if (separator == -1) {
            throw new IllegalArgumentException("Missing ':' character to separate leading jdbc prefix in database URL");
        }
        if (url.length() < separator + 2) {
            throw new IllegalArgumentException("Missing characters after \"jdbc:\"prefix");
        }
        return URI.create(url.substring(separator + 1));
    }

    private static String dbNameFromUri(URI dbUri) {
        String dbPath =  dbUri.getPath();
        if (dbPath.length() == 0) {
            throw new IllegalArgumentException("Database name is empty");
        }
        String dbName = dbPath.charAt(0) == '/' ? dbPath.substring(1, dbPath.length()) : dbPath;
        if (dbName.length() == 0) {
            throw new IllegalArgumentException("Database name is empty");
        }
        return dbName;
    }

}
