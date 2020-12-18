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

import javax.json.JsonObject;

import io.helidon.tests.integration.dbclient.appl.tools.AppResponse;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import static io.helidon.tests.integration.dbclient.appl.ApplMain.DB_CLIENT;
import static io.helidon.tests.integration.dbclient.appl.model.Pokemon.POKEMONS;
import static io.helidon.tests.integration.dbclient.appl.tools.AppConfig.CONFIG;

/**
 *
 */
public class SimpleGetResource implements Service {

    private static final Logger LOGGER = Logger.getLogger(SimpleGetResource.class.getName());

    public static final String SELECT_POKEMON_NAMED_ARG
            = CONFIG.get("db.statements.select-pokemon-named-arg").asString().get();

    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/testCreateNamedGetStrStrNamedArgs", this::testCreateNamedGetStrStrNamedArgs);
    }

    /**
     * Test invocation.
     *
     * @param request  the server request
     * @param response the server response
     */
    public JsonObject testCreateNamedGetStrStrNamedArgs(final ServerRequest request, final ServerResponse response) {
        LOGGER.fine(() -> String.format("Running SimpleGetResource.testCreateNamedGetStrStrNamedArgs on server"));
        AppResponse.sendResponse(response,
                () -> DB_CLIENT.execute(
                        exec -> exec
                                .createNamedGet("select-pikachu", SELECT_POKEMON_NAMED_ARG)
                                .addParam("name", POKEMONS.get(1).getName()).execute()
                ).toCompletableFuture());
        return null;
    }

}
