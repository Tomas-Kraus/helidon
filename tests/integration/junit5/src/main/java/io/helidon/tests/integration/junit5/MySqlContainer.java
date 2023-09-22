/*
 * Copyright (c) 2023, 2024 Oracle and/or its affiliates.
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

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.helidon.config.Config;
import io.helidon.config.ConfigValue;

public class MySqlContainer extends DatabaseContainer {

    private static final Logger LOGGER = System.getLogger(MySqlContainer.class.getName());

    private static final String DEFAULT_IMAGE = "mysql:latest";
    private static final String ROOT_PASSWORD = "R00t_P4ssw0rd";
    private static final String USER = "user";
    private static final String PASSWORD = "p4ssw0rd";
    private static final String DATABASE = "dbclient";
    private static final int PORT = 3306;

    public MySqlContainer() {
        super();
    }

    @Override
    public void setup() {
        super.setup();
        // Set default image as fallback when no image was configured.
        if (image().isEmpty()) {
            builder().image(DEFAULT_IMAGE);
        }
    }

    @Override
    public void start() {
        createDbConfig();
        // Can't be null
        Map<String, String> environment = builder().environment();
        // Copy container configuration from DbClient "connection" Config node.
        // Only replace values that are not already present (added by @SetUpContainer method)
        dbConfig().dbClient().asNode().ifPresent(config -> {
            config.get("url").asString().ifPresent(url -> {
                URI uri = ConfigUtils.uriFromDbUrl(url);
                if (!environment.containsKey("MYSQL_DATABASE")) {
                    environment.put("MYSQL_DATABASE", ConfigUtils.dbNameFromUri(uri));
                }
                if (builder().exposedPorts() == null || builder().exposedPorts().length == 0) {
                    builder().exposedPorts(new int[] {uri.getPort()});
                }
            });
            config.get("username").asString().ifPresent(user -> {
                if (!environment.containsKey("MYSQL_USER")) {
                    environment.put("MYSQL_USER", user);
                }
            });
            config.get("password").asString().ifPresent(password -> {
                if (!environment.containsKey("MYSQL_PASSWORD")) {
                    environment.put("MYSQL_PASSWORD", password);
                }
            });
        });
        // Provide default configuration values for all that are missing
        if (!environment.containsKey("MYSQL_ROOT_PASSWORD")) {
            environment.put("MYSQL_ROOT_PASSWORD", ROOT_PASSWORD);
        }
        if (!environment.containsKey("MYSQL_DATABASE")) {
            environment.put("MYSQL_DATABASE", DATABASE);
        }
        if (!environment.containsKey("MYSQL_USER")) {
            environment.put("MYSQL_USER", USER);
        }
        if (!environment.containsKey("MYSQL_PASSWORD")) {
            environment.put("MYSQL_PASSWORD", PASSWORD);
        }
        if (builder().exposedPorts() == null || builder().exposedPorts().length == 0) {
            builder().exposedPorts(new int[] {PORT});
        }
        // Create container config and container from internal builder and start the container.
        super.start();

        LOGGER.log(Level.TRACE,
                   () -> String.format("MySQL database from image %s is running", config().image()));
        LOGGER.log(Level.TRACE,
                   () -> String.format("Container configuration: %s", config().toString()));
        // Store container info
        Map<Integer, Integer> portMappings = new HashMap<>(config().exposedPorts().length);
        for (int port : config().exposedPorts()) {
            Integer mappedPort = container().getMappedPort(port);
            if (mappedPort != null) {
                portMappings.put(port, mappedPort);
            }
        }
        Map<String, String> properties = new HashMap<>(3);
        ConfigValue<Config> dbConfig = dbConfig().dbClient().asNode();
        String url = String.format("jdbc:mysql://127.0.0.1:3306/%s?useSSL=false&allowPublicKeyRetrieval=true", DATABASE);
        if (dbConfig.isPresent()) {
            ConfigValue<String> urlValue = dbConfig.get().get("url").asString();
            if (urlValue.isPresent()) {
                url = urlValue.get();
            }
        }
        properties.put("url", replacePortInUrl(url, portMappings.get(config().exposedPorts()[0])));
        properties.put("username", environment.get("MYSQL_USER"));
        properties.put("password", environment.get("MYSQL_PASSWORD"));
        properties.put("adminpw", environment.get("MYSQL_ROOT_PASSWORD"));
        containerInfo(new ContainerInfo(config(), Map.copyOf(portMappings), Map.copyOf(properties)));
    }

}
