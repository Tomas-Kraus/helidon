/*
 * Copyright (c) 2019, 2023 Oracle and/or its affiliates.
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

package io.helidon.examples.dbclient.jdbc;

import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.logging.common.LogConfig;
import io.helidon.nima.observe.ObserveFeature;
import io.helidon.nima.webserver.WebServer;
import io.helidon.nima.webserver.http.HttpRouting;
import io.helidon.nima.webserver.tracing.TracingFeature;
import io.helidon.tracing.TracerBuilder;

/**
 * Simple Hello World rest application.
 */
public final class JdbcExampleMain {

    /**
     * Cannot be instantiated.
     */
    private JdbcExampleMain() {
    }

    /**
     * Application main entry point.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        // load logging configuration
        LogConfig.configureRuntime();

        // By default, this will pick up application.yaml from the classpath
        Config config = Config.create();

        // Prepare routing for the server
        WebServer server = WebServer.builder()
                .routing(routing -> routing(routing, config))
                .config(config.get("server"))
                .build()
                .start();

        System.out.println("WEB server is up! http://localhost:" + server.port() + "/");
    }

    static void routing(HttpRouting.Builder routing, Config config) {
        routing.addFeature(ObserveFeature.create(config))
                .addFeature(TracingFeature.create(TracerBuilder.create(config.get("tracing")).build()))
                .register("/db", new PokemonService(DbClient.create(config.get("db"))))
                .build();
    }
}
