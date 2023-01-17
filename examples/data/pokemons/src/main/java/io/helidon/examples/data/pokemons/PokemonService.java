/*
 * Copyright (c) 2019, 2022 Oracle and/or its affiliates.
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
package io.helidon.examples.data.pokemons;

import java.util.Collections;
import java.util.List;

import io.helidon.common.http.Http;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.data.HelidonData;
import io.helidon.data.runtime.DynamicFinderOrder;
import io.helidon.examples.data.pokemons.model.Pokemon;
import io.helidon.examples.data.pokemons.repository.PokemonRepository;
import io.helidon.examples.data.pokemons.repository.TypeRepository;
import io.helidon.examples.data.pokemons.repository.generated.PokemonCriteria;
import io.helidon.examples.data.pokemons.repository.generated.PokemonFilter;
import io.helidon.examples.data.pokemons.repository.generated.PokemonOrder;
import io.helidon.examples.data.pokemons.repository.generated.TypeCriteria;
import io.helidon.examples.data.pokemons.repository.generated.TypeOrder;
import io.helidon.nima.webserver.http.Handler;
import io.helidon.nima.webserver.http.HttpRules;
import io.helidon.nima.webserver.http.HttpService;
import io.helidon.nima.webserver.http.ServerRequest;
import io.helidon.nima.webserver.http.ServerResponse;

/**
 * Example Nima service using blocking CRUD data repoository.
 */
public class PokemonService implements HttpService {

    // Helidon data repository
    private final HelidonData data;
    // Pokemon entity data repository
    private final PokemonRepository pokemonRepository;
    // Type entity data repository
    private final TypeRepository typeRepository;

    PokemonService(HelidonData data) {
        this.data = data;
        // Initialize data repositories
        this.pokemonRepository = data.repository(PokemonRepository.class);
        this.typeRepository = data.repository(TypeRepository.class);
    }

    @Override
    public void routing(HttpRules rules) {
        rules
                .get("/", this::index)
                // List all types
                .get("/type", this::listTypes)
                // List all pokemons
                .get("/pokemon", this::listPokemons)
                // Get pokemon by ID
                .get("/pokemon/{id}", this::getPokemonById)
                // Get pokemon by name
                .get("/pokemon/name/{name}", this::getPokemonByName)
                // Get pokemons by Type name
                .get("/pokemon/type/{name}", this::getPokemonsByType)
                // Get pokemon by filtering keyword
                .get("/pokemon/filter", this::getPokemonsByFilter)
                // Get pokemon by name and sort depending on request argument
                .get("/pokemon/sort", this::getPokemonsByNameInVariableOrder)
                // Create new pokemon. FIXME: Handler factory with Class<T> type and exception handling would be helpful
                .post("/pokemon", (req, res) -> insertPokemon(req.content().as(Pokemon.class), res))
                // Update name of existing pokemon
                .put("/pokemon", Handler.create(Pokemon.class, this::updatePokemon))
                // Delete pokemon by ID including type relation
                .delete(this::deletePokemonById);
    }

    /**
     * Return index page.
     *
     * @param request  the server request
     * @param response the server response
     */
    private void index(ServerRequest request, ServerResponse response) {
        response.headers().contentType(MediaTypes.TEXT_PLAIN);
        response.send("Pokemon JDBC Example:\n"
                              + "     GET /type                - List all pokemon types\n"
                              + "     GET /pokemon             - List all pokemons\n"
                              + "     GET /pokemon/{id}        - Get pokemon by id\n"
                              + "     GET /pokemon/name/{name} - Get pokemon by name\n"
                              + "     GET /pokemon/type/{name} - List all pokemons of given type\n"
                              + "    POST /pokemon             - Insert new pokemon:\n"
                              + "                                {\"id\":<id>,\"name\":<name>,\"type\":<type>}\n"
                              + "     PUT /pokemon             - Update pokemon\n"
                              + "                                {\"id\":<id>,\"name\":<name>,\"type\":<type>}\n"
                              + "  DELETE /pokemon/{id}        - Delete pokemon with specified id\n");
    }

    /**
     * Return all stored Pokemon types.
     *
     * @param request  the server request
     * @param response the server response
     */
    private void listTypes(ServerRequest request, ServerResponse response) {
        // Iterable<E> findAll() is method added from CrudRepository interface
        response.send(typeRepository.findAll());
    }

    /**
     * Return all stored pokemons.
     *
     * @param request  the server request
     * @param response the server response
     */
    private void listPokemons(ServerRequest request, ServerResponse response) {
        // Iterable<E> findAll() is method added from CrudRepository interface
        response.send(pokemonRepository.findAll());
    }

    /**
     * Get a single pokemon by id.
     *
     * @param request  server request
     * @param response server response
     */
    private void getPokemonById(ServerRequest request, ServerResponse response) {
        int pokemonId = Integer.parseInt(request.path().pathParameters().value("id"));
        // Optional<E> findById(ID id) is method added from CrudRepository interface
        pokemonRepository.findById(pokemonId)
                .ifPresentOrElse(
                        it -> response.send(it),
                        () -> response.status(Http.Status.NOT_FOUND_404).send()
                );
    }

    /**
     * Get a single pokemon by name.
     *
     * @param request  server request
     * @param response server response
     */
    private void getPokemonByName(ServerRequest request, ServerResponse response) {
        String pokemonName = request.path().pathParameters().value("name");
        // Optional<Pokemon> getByName(String name) is method defined as query by method name
        pokemonRepository.getByName(pokemonName)
                .ifPresentOrElse(
                        it -> response.send(it),
                        () -> response.status(Http.Status.NOT_FOUND_404).send()
                );
    }

    /**
     * Get all pokemons of given type.
     *
     * @param request  server request
     * @param response server response
     */
    private void getPokemonsByType(ServerRequest request, ServerResponse response) {
        String typeName = request.path().pathParameters().value("name");
        // List<Pokemon> findByTypeName(String typeName) is method defined as query by method name
        try {
            response.send(
                    data.transaction(
                            () -> pokemonRepository.findByTypeName(typeName)
                    )
            );
        } catch (Exception e) {
            response.status(Http.Status.INTERNAL_SERVER_ERROR_500).send();
        }
    }

    /**
     * Get pokemon of given type and name.
     *
     * @param request  server request
     * @param response server response
     */
    private void getPokemonByTypeAndName(ServerRequest request, ServerResponse response) {
        String typeName = request.path().pathParameters().value("typeName");
        String pokemonName = request.path().pathParameters().value("pokemonName");
        // Optional<Pokemon> pokemonsByTypeAndName(String typeName, String pokemonName)
        // is method defined by custom query annotation
        try {
            data.transaction(
                    () -> pokemonRepository.pokemonByTypeAndName(typeName, pokemonName)
            ).ifPresentOrElse(
                    it -> response.send(it),
                    () -> response.status(Http.Status.NOT_FOUND_404).send()
            );
        } catch (Exception e) {
            response.status(Http.Status.INTERNAL_SERVER_ERROR_500).send();
        }
    }

    /**
     * Insert new pokemon.
     *
     * @param pokemon pokemon to insert
     */
    private void insertPokemon(Pokemon pokemon, ServerResponse response) {
        // <T extends E> T save(T entity) is method added from CrudRepository interface
        try {
            response.send(
                    data.transaction(
                            () -> pokemonRepository.save(pokemon)));
        } catch (Exception e) {
            response.status(Http.Status.INTERNAL_SERVER_ERROR_500).send();
        }
    }

    /**
     * Update a pokemon.
     *
     * @param pokemon pokemon to update
     */
    private Pokemon updatePokemon(Pokemon pokemon) {
        // <T extends E> T update(T entity) is method added from CrudRepository interface
        pokemonRepository.update(pokemon);
        return pokemon;
    }

    /**
     * Delete pokemon with specified id (key).
     *
     * @param request the server request
     */
    private void deletePokemonById(ServerRequest request, ServerResponse response) {
        int id = Integer.parseInt(request.path().pathParameters().value("id"));
        // void deleteById(ID id) is method added from CrudRepository interface
        try {
            data.transaction(
                    () -> pokemonRepository.deleteById(id)
            );
        } catch (Exception e) {
            response.status(Http.Status.INTERNAL_SERVER_ERROR_500);
        }
        response.send();
    }

    /**
     * Find pokemons using custom criteria filter.
     *
     * @param request  server request
     * @param response server response
     */
    private void getPokemonsByFilter(ServerRequest request, ServerResponse response) {
        response.send(pokemonRepository.findByFilter(
                PokemonCriteria.builder()
                        .name(request.query().all("name", List::of))
                        //.typeName(request.query().all("type", () -> Collections.EMPTY_LIST))
                        .type(TypeCriteria.builder()
                                      .name(request.query().all("type", List::of))
                                      .build())
                        .build()));
    }

    /**
     * Find pokemons using custom ordering filter.
     *
     * @param request  server request
     * @param response server response
     */
    private void getPokemonsByNameInVariableOrder(ServerRequest request, ServerResponse response) {
        response.send(pokemonRepository.findByNameOrderByFilter(
                PokemonOrder.builder()
                        .orderBy("name", request.query().first("order").orElse("asc"))
                        .orderByType(
                                TypeOrder.builder()
                                        .orderByName(DynamicFinderOrder.Order.Method.DESC)
                                        .build())
                        .build()));
    }

    /**
     * Find pokemons using both custom criteria and ordering filters.
     *
     * @param request  server request
     * @param response server response
     */
    private void getPokemonsAvgHpByFilter(ServerRequest request, ServerResponse response) {
        response.send(pokemonRepository.findAvgHpByFilterOrderByFilter(
                PokemonCriteria.builder()
                        .name(request.query().all("name", List::of))
                        .type(TypeCriteria.builder()
                                      .name(request.query().all("type", List::of))
                                      .build())
                        .build(),
                PokemonOrder.builder()
                        .orderBy("name", request.query().first("order").orElse("asc"))
                        .build()));
    }

    /**
     * Find pokemon names using both custom criteria and ordering filters.
     * Another API example with compound filter.
     *
     * @param request  server request
     * @param response server response
     */
    private void getPokemonNamesByFilter(ServerRequest request, ServerResponse response) {
        response.send(pokemonRepository.findNameByFilter(
                PokemonFilter.builder()
                        .name(request.query().all("name", List::of))
                        .type(TypeCriteria.builder()
                                      .name(request.query().all("type", List::of))
                                      .build())
                        .orderByName(request.query().first("order").orElse("asc"))
                        .build()));
    }

}
