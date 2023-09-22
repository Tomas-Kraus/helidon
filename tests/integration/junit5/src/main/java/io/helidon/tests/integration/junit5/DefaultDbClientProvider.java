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

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.config.ConfigValue;
import io.helidon.dbclient.DbClient;
import io.helidon.tests.integration.junit5.spi.DbClientProvider;

public class DefaultDbClientProvider implements DbClientProvider, SuiteResolver {

    private SuiteContext suiteContext;
    private DbClient dbClient;
    private DbClient.Builder builder;
    private Optional<String> configKey;
    private Optional<String> portKey;

    public DefaultDbClientProvider() {
        suiteContext = null;
        dbClient = null;
        builder = null;
        configKey = Optional.empty();
        portKey = Optional.empty();
    }

    public void suiteContext(SuiteContext suiteContext) {
        this.suiteContext = suiteContext;
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
        builder = DbClient.builder();
        // Update database URL in DbClient config using stored container port number
        // This happens only when configKey is set in @DbClientTest annotation
        if (configKey.isPresent()) {
            Config config = suiteContext.storage().get(configKey.get(), Config.class);
            if (config != null) {
                if (portKey.isPresent()) {
                    Integer port = suiteContext.storage().get(portKey.get(), Integer.class);
                    if (port != null) {
                        ConfigValue<String> urlConfig = config.get("db.connection.url").asString();
                        if (urlConfig.isPresent()) {
                            // Parse old URL and replace found port token with port from container
                            String url = urlConfig.get();
                            int begin = url.indexOf("://");
                            if (begin >= 0) {
                                int end = url.indexOf('/', begin + 3);
                                int portBeg = url.indexOf(':', begin + 3);
                                // Found port position in URL
                                if (end > 0 && portBeg < end) {
                                    String frontPart = url.substring(0, portBeg + 1);
                                    String endPart = url.substring(end);
                                    String newUrl = frontPart + port.toString() + endPart;
                                    System.out.println(String.format("Updated DbClient URL based on container port: %s", newUrl));
                                    config = Config.create(ConfigSources.create(Map.of("db.connection.url", newUrl)),
                                                  ConfigSources.create(config));
                                    // Update stored configuration to contain updated URL
                                    suiteContext.storage().putOrReplace(configKey.get(), config);
                                } else {
                                    throw new IllegalStateException(
                                            String.format("URL %s does not contain host and port part \"://host:port/\"", url));
                                }
                            } else {
                                throw new IllegalStateException(
                                        String.format("Could not find host separator \"://\" in URL %s", url));
                            }
                        }
                    } else {
                        throw new IllegalStateException(
                                String.format("Mapped database container port was not found in the suite context storage with key %s",
                                              portKey.get()));
                    }
                }
               builder.config(config.get("db"));
            } else {
                throw new IllegalStateException(
                        String.format("DbClient config was not found in the suite context storage with key %s",
                                      configKey.get()));
            }
        }
    }

    @Override
    public DbClient.Builder builder() {
        return builder;
    }

    @Override
    public void start() {
        dbClient = builder.build();
    }

    @Override
    public DbClient dbClient() {
        return dbClient;
    }

    @Override
    public <T extends Junit5ExtensionProvider> T as(Class<T> cls) {
        if (cls == DbClientProvider.class || cls == DefaultDbClientProvider.class) {
            return cls.cast(this);
        }
        throw new IllegalArgumentException(
                String.format("Cannot cast this DbClientProvider implementation as %s", cls.getName()));
    }

    @Override
    public boolean supportsParameter(Type type) {
        return DbClient.class.isAssignableFrom((Class<?>) type);
    }

    @Override
    public Object resolveParameter(Type type) {
        if (DbClient.class.isAssignableFrom((Class<?>)type)) {
            return dbClient();
        }
        throw new IllegalArgumentException(String.format("Cannot resolve parameter Type %s", type.getTypeName()));
    }

}
