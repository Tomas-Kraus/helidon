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

import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import io.helidon.config.Config;
import io.helidon.dbclient.health.DbClientHealthCheck;
import io.helidon.tests.integration.dbclient.appl.tools.AppResponse;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import static io.helidon.tests.integration.dbclient.appl.ApplMain.DB_CLIENT;
import static io.helidon.tests.integration.dbclient.appl.tools.AppConfig.CONFIG;

/**
 *
 */
public class InitResource implements Service {

    private static final Logger LOGGER = Logger.getLogger(InitResource.class.getName());

    private static boolean pingDml = true;

    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/setup", this::setup)
                .get("/testPing", this::testHealthCheck)
                .get("/testInitSchema", this::testInitSchema);
    }

    public JsonObject setup(final ServerRequest request, final ServerResponse response) {
        LOGGER.fine(() -> String.format("Running InitResource.setup on server"));
        Config cfgPingDml = CONFIG.get("test.ping-dml");
        pingDml = cfgPingDml.exists() ? cfgPingDml.asBoolean().get() : true;
        final JsonObjectBuilder data = Json.createObjectBuilder();
        data.add("ping-dml", pingDml);
        response.send(AppResponse.okStatus(data.build()));
        return null;
    }

    public JsonObject testHealthCheck(final ServerRequest request, final ServerResponse response) {
        LOGGER.fine(() -> String.format("Running InitResource.testHealthCheck on server"));
        HealthCheck check;
        if (!pingDml) {
            LOGGER.finer(() -> String.format("Database %s does not support DML ping, using query", DB_CLIENT.dbType()));
            check = DbClientHealthCheck.builder(DB_CLIENT).query().build();
        } else {
            LOGGER.finer(() -> String.format("Database %s supports DML ping, using default method", DB_CLIENT.dbType()));
            check = DbClientHealthCheck.create(DB_CLIENT);
        }
        HealthCheckResponse checkResponse = check.call();
        HealthCheckResponse.State checkState = checkResponse.getState();
        final JsonObjectBuilder data = Json.createObjectBuilder();
        data.add("state", checkState.name());
        response.send(AppResponse.okStatus(data.build()));
        return null;
    }

    public JsonObject testInitSchema(final ServerRequest request, final ServerResponse response) {
        LOGGER.fine(() -> String.format("Running InitResource.testInitSchema on server"));
        AppResponse.sendDmlResponse(response,
                () -> DB_CLIENT.execute(
                        exec -> exec
                                .namedDml("create-types")
                                .flatMapSingle(result -> exec.namedDml("create-pokemons"))
                                .flatMapSingle(result -> exec.namedDml("create-poketypes"))
                ).toCompletableFuture());
        return null;
    }

}
