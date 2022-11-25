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
package io.helidon.data;


import io.helidon.data.repository.GenericRepository;

public class HelidonData {

    /**
     * Creates an instance of data repository.
     *
     * @param repositoryClass data repository interface or abstract class to create
     * @return new data repository instance
     * @param <E> type of the entity
     * @param <ID> type of the ID
     * @param <T> target data repository type
     */
    public static <E, ID, T extends GenericRepository<E, ID>> T createRepository(Class <? super T> repositoryClass) {
        // TODO: Real imlpementation, this is just a placeholder
        T repository = null;
        return repository;
    }

}
