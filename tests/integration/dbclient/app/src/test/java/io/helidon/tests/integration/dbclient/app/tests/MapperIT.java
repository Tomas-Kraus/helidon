/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates.
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
package io.helidon.tests.integration.dbclient.app.tests;

import java.lang.System.Logger.Level;

import io.helidon.tests.integration.dbclient.app.LogData;
import io.helidon.tests.integration.dbclient.app.VerifyData;
import io.helidon.tests.integration.dbclient.common.model.Pokemon;
import io.helidon.tests.integration.dbclient.app.tools.QueryParams;
import io.helidon.tests.integration.harness.JsonValues;
import io.helidon.tests.integration.harness.TestClient;
import io.helidon.tests.integration.harness.TestServiceClient;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Verify mapping interface.
 */
class MapperIT {

    private static final System.Logger LOGGER = System.getLogger(MapperIT.class.getName());

    private final TestServiceClient testClient;

    MapperIT(int serverPort) {
        this.testClient = TestClient.builder()
                .port(serverPort)
                .service("Mapper")
                .build();
    }

    /**
     * Verify insertion using indexed mapping.
     */
    @Test
    void testInsertWithOrderMapping() {
        executeInsertTest("testInsertWithOrderMapping", 103);
    }

    /**
     * Verify insertion using named mapping.
     */
    @Test
    void testInsertWithNamedMapping() {
        executeInsertTest("testInsertWithNamedMapping", 104);
    }

    /**
     * Verify update using indexed mapping.
     */
    @Test
    void testUpdateWithOrderMapping() {
        executeUpdateTest("testUpdateWithOrderMapping", 99, "Masquerain");
    }

    /**
     * Verify update using named mapping.
     */
    @Test
    void testUpdateWithNamedMapping() {
        executeUpdateTest("testUpdateWithNamedMapping", 100, "Moltres");
    }

    /**
     * Verify delete using indexed mapping.
     */
    @Test
    void testDeleteWithOrderMapping() {
        executeDeleteTest("testDeleteWithOrderMapping", 101);
    }

    /**
     * Verify delete using named mapping.
     */
    @Test
    void testDeleteWithNamedMapping() {
        executeDeleteTest("testDeleteWithNamedMapping", 102);
    }

    // Query and Get calls are here just once so no common executor code is needed.

    /**
     * Verify query as a result using mapping.
     */
    @Test
    void testQueryWithMapping() {
        Pokemon pokemon = Pokemon.POKEMONS.get(1);
        JsonArray data = testClient
                .callServiceAndGetData(
                        "testQueryWithMapping",
                        QueryParams.single(QueryParams.NAME, pokemon.getName()))
                .asJsonArray();
        LogData.logJsonArray(Level.DEBUG, data);
        assertThat(data.size(), equalTo(1));
        VerifyData.verifyPokemon(data.getJsonObject(0), pokemon);
    }

    /**
     * Verify get as a result using mapping.
     */
    @Test
    void testGetWithMapping() {
        Pokemon pokemon = Pokemon.POKEMONS.get(2);
        JsonObject data = testClient
                .callServiceAndGetData(
                        "testGetWithMapping",
                        QueryParams.single(QueryParams.NAME, pokemon.getName()))
                .asJsonObject();
        LogData.logJsonObject(Level.DEBUG, data);
        VerifyData.verifyPokemon(data, pokemon);
    }

    private void executeInsertTest(String testName, int id) {
        LOGGER.log(Level.DEBUG, () -> String.format("Running %s.%s on client", getClass().getSimpleName(), testName));
        JsonObject data = testClient
                .callServiceAndGetData(
                        testName,
                        QueryParams.single(QueryParams.ID, String.valueOf(id)))
                .asJsonObject();
        LogData.logJsonObject(Level.DEBUG, data);
        JsonObject pokemonData = VerifyData.getPokemon(testClient, id);
        LogData.logJsonObject(Level.DEBUG, pokemonData);
        VerifyData.verifyPokemon(pokemonData, data);
    }

    private void executeUpdateTest(String testName, int id, String newName) {
        LOGGER.log(Level.DEBUG, () -> String.format("Running MapperIT.%s on client", testName));
        Pokemon pokemon = Pokemon.POKEMONS.get(id);
        Pokemon updatedPokemon = new Pokemon(pokemon.getId(), newName, pokemon.getTypes());
        JsonValue data = testClient
                .callServiceAndGetData(
                        testName,
                        QueryParams.builder()
                                .add(QueryParams.NAME, newName)
                                .add(QueryParams.ID, String.valueOf(id))
                                .build());
        Long count = JsonValues.asLong(data);
        LOGGER.log(Level.DEBUG, () -> String.format("Rows updated: %d", count));
        JsonObject pokemonData = VerifyData.getPokemon(testClient, pokemon.getId());
        LogData.logJsonObject(Level.DEBUG, pokemonData);
        assertThat(count, equalTo(1L));
        VerifyData.verifyPokemon(pokemonData, updatedPokemon);
    }

    private void executeDeleteTest(String testName, int id) {
        LOGGER.log(Level.DEBUG, () -> String.format("Running MapperIT.%s on client", testName));
        JsonValue data = testClient
                .callServiceAndGetData(
                        testName,
                        QueryParams.single(QueryParams.ID, String.valueOf(id)));
        Long count = JsonValues.asLong(data);
        LOGGER.log(Level.DEBUG, () -> String.format("Rows deleted: %d", count));
        JsonObject pokemonData = VerifyData.getPokemon(testClient, id);
        LogData.logJsonObject(Level.DEBUG, pokemonData);
        assertThat(count, equalTo(1L));
        assertThat(pokemonData.isEmpty(), equalTo(true));
    }
}
