/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.tests.integration.dbclient.appl;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.helidon.common.LogConfig;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.health.DbClientHealthCheck;
import io.helidon.health.HealthSupport;
import io.helidon.media.jsonb.JsonbSupport;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.tests.integration.dbclient.appl.tools.AppConfig;
import io.helidon.tests.integration.dbclient.appl.tools.AppResource;
import io.helidon.tests.integration.dbclient.appl.tools.Dispatcher;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;

import static io.helidon.tests.integration.dbclient.appl.tools.AppConfig.CONFIG;

/**
 * Main class.
 * This is present for modularized java to work correctly,
 * as using a main class from a different module resulted
 * in build errors.
 */
public class ApplMain {

    private static final Logger LOGGER = Logger.getLogger(ApplMain.class.getName());

    public static final DbClient DB_CLIENT = initDbClient();

    /**
     * Initialize database client.
     *
     * @return database client instance
     */
    private static DbClient initDbClient() {
        Config dbConfig = AppConfig.CONFIG.get("db");
        return DbClient.builder(dbConfig).build();
    }

    private static int getPortFromProperty() {
        final String portStr = System.getProperty("server.port");
        if (portStr != null && portStr.length() > 0) {
            try {
                return Integer.parseInt(portStr);
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.WARNING, ex, () -> String.format("Could not parse port number: %s", ex.getMessage()));
                return -1;
            }
        } else {
            return -1;
        }
    }

    static WebServer startServer() {
        int port = getPortFromProperty();
        // Prepare routing for the server
        WebServer.Builder serverBuilder = WebServer.builder()
                .routing(createRouting(CONFIG))
                // Get webserver config from the "server" section of application.yaml
                .config(CONFIG.get("server"));
        if (port > -1) {
            serverBuilder = serverBuilder.port(port);
        }
        WebServer server = 
                serverBuilder.addMediaSupport(JsonpSupport.create())
                .addMediaSupport(JsonbSupport.create())
                .build();

        // Start the server and print some info.
        server.start().thenAccept(ws -> {
            System.out.println(
                    "WEB server is up! http://localhost:" + ws.port() + "/");
        });

        // Server threads are not daemon. NO need to block. Just react.
        server.whenShutdown().thenRun(() -> System.out.println("WEB server is DOWN. Good bye!"));

        return server;
    }


    /**
     * Creates new {@link io.helidon.webserver.Routing}.
     *
     * @param config configuration of this server
     * @return routing configured with JSON support, a health check, and a service
     */
    private static Routing createRouting(Config config) {
        Config dbConfig = config.get("db");

        // Client services are added through a service loader - see mongoDB example for explicit services
        DbClient dbClient = DbClient.builder(dbConfig)
                .build();

        HealthSupport health = HealthSupport.builder()
                .addLiveness(DbClientHealthCheck.create(dbClient))
                .build();

        return Routing.builder()
                .register(health)                   // Health at "/health"
                .register("/test", new AppResource())
                .register("/Init", new InitResource())
                .register("/SimpleGet", new SimpleGetResource())
                .build();
    }

    /**
     * Main method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        LogConfig.configureRuntime();
        Dispatcher.add(SimpleGetIT.class);
        startServer();
    }

}
