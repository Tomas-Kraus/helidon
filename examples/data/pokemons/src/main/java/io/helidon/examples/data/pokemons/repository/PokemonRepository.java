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
package io.helidon.examples.data.pokemons.repository;

import io.helidon.data.annotation.NativeQuery;
import io.helidon.data.annotation.Query;
import io.helidon.data.annotation.Repository;
import io.helidon.data.annotation.Transactional;
import io.helidon.data.repository.CrudRepository;
import io.helidon.data.repository.RepositoryFilter;
import io.helidon.examples.data.pokemons.model.Pokemon;
import io.helidon.examples.data.pokemons.repository.generated.PokemonCriteria;
import io.helidon.examples.data.pokemons.repository.generated.PokemonFilter;
import io.helidon.examples.data.pokemons.repository.generated.PokemonOrder;

import java.util.List;
import java.util.Optional;

// Micronaut marks those interfaces/abstract classes with annotation. It may help with processing.
// But it's not mandatory - all have GenericRepository as parent interface.
@Repository
public interface PokemonRepository extends CrudRepository<Pokemon, Integer> {

    // Query defined by method name: Find pokemon by provided name attribute
    Optional<Pokemon> getByName(String name);

    // Query defined by method name: List all pokemons with provided type name attribute
    List<Pokemon> findByTypeName(String typeName);

    // Query defined by annotation: Find pokemon by provided type name and name attributes
    @Query(value="SELECT p FROM Pokemon p WHERE p.type.name = :typeName AND p.name = :pokemonName")
    Optional<Pokemon> pokemonByTypeAndName(String typeName, String pokemonName);

    // Query defined by annotation: Find all pokemons by provided trainer's name
    @Query(value="SELECT p FROM Pokemon p WHERE p.trainer.name = :trainerName")
    List<Pokemon> pokemonsByTrainerName(String trainerName);

    // Query defined by annotation: Find pokemon by provided type name and name attributes
    @Query(key="pokemons.jpql.by-type-and-name")
    Optional<Pokemon> pokemonByTypeAndName2(String typeName, String pokemonName);

    // Query defined by annotation: Find pokemon by provided type name and name attributes
    // ResultSet to Pokemon/Type mapping is defined by JPA @SqlResultSetMapping on entity.
    @NativeQuery(
            value = "SELECT p.ID, p.NAME, p.ID_TYPE, t.ID, t.NAME " +
                            "FROM POKEMON p INNER JOIN TYPE t ON p.ID_TYPE = t.ID " +
                            "WHERE t.NAME = :typeName AND p.NAME = :pokemonName",
            resultSetMapping = "PokemonByTypeAndNameRSMapping")
    Optional<Pokemon> pokemonByTypeAndName3(String typeName, String pokemonName);

    @NativeQuery(key="pokemons.native.by-type-and-name", resultSetMapping = "PokemonByTypeAndNameRSMapping")
    Optional<Pokemon> pokemonByTypeAndName4(String typeName, String pokemonName);

    // "Filter" is newly introduced keyword which allows to pass dynamic filtering rules to repository methods.

    // Query with custom filtering: Dynamic criteria
    // Filter can access method parameters by name.
    // @Filter annotation links method prototype with filtering class (generated). This may be used
    //         for linking filtering classes with methods. Another option is to use child class instead
    //         of RepositoryFilter in method prototype filter argument.
    // TODO: How to specify projection part of the query?
    //       - use method name prefix? -> findMaxAgeByFilter :: parse everything up to 'By' delimiter
    List<Pokemon> findByFilter(PokemonCriteria filter);

    // Query with custom filtering: Dynamic ordering
    // Projection and criteria are built based on method name
    List<Pokemon> findByNameOrderByFilter(PokemonOrder filter);

    // There must be a chance to pass both criteria and ordering rules together.
    // Unfortunately using 'By' keyword to separate projection and rest of the query (criteria and ordring)
    // needs another information to be passed to the parser and code generator
    // - type of the filter attribute to distinguish whether just criteria or both criteria and ordring
    //   are passed
    List<Pokemon> findAvgHpByFilterOrderByFilter(PokemonCriteria criteria, PokemonOrder order);

    // Another example of passing both criteria and ordering rules together.
    List<Pokemon> findNameByFilter(PokemonFilter filter);

    @Transactional
    default void decrementPokemonHp(String trainer, int hp) {
        List<Pokemon> pokemons = pokemonsByTrainerName(trainer);
        pokemons.forEach(pokemon -> pokemon.setHp(pokemon.getHp() - hp));
        updateAll(pokemons);
    }

}
