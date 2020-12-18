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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.helidon.dbclient.DbRow;
import io.helidon.tests.integration.dbclient.appl.tools.AppTestOptionalDbRow;
import io.helidon.webserver.ServerResponse;

import static io.helidon.tests.integration.dbclient.appl.ApplMain.DB_CLIENT;
import static io.helidon.tests.integration.dbclient.appl.tools.AppConfig.CONFIG;
import static io.helidon.tests.integration.dbclient.appl.model.Pokemon.POKEMONS;

/**
 *
 */
public class SimpleGetIT {


    private static final Logger LOGGER = Logger.getLogger(SimpleGetIT.class.getName());

    public static final String SELECT_POKEMON_NAMED_ARG
            = CONFIG.get("db.statements.select-pokemon-named-arg").asString().get();

    /**
     * Get a single pokemon by name.
     *
     * @param request  server request
     * @param response server response
     * @return get query future
     */
    @AppTestOptionalDbRow
    public CompletableFuture<Optional<DbRow>> testCreateNamedGetStrStrNamedArgs(ServerResponse response) {
        LOGGER.info(() -> String.format("Running testCreateNamedGetStrStrNamedArgs on server"));
        return DB_CLIENT.execute(exec -> exec
                .createNamedGet("select-pikachu", SELECT_POKEMON_NAMED_ARG)
                .addParam("name", POKEMONS.get(1).getName()).execute()).toCompletableFuture();
    }
    
    
}
