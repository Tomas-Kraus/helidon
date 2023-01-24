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

package io.helidon.data.jpa.runtime;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import io.helidon.data.DataException;
import io.helidon.data.DataTransaction;
import io.helidon.data.HelidonData;
import io.helidon.data.repository.GenericRepository;

import jakarta.persistence.EntityTransaction;

/**
 * JPA specific Helidon Data Repository.
 */
class JpaHelidonData implements HelidonData {

    // All JPA specific context
    private final JpaContext jpaContext;

    JpaHelidonData(JpaContext jpaContext) {
        this.jpaContext = jpaContext;
    }

    @Override
    public <E, ID, T extends GenericRepository<E, ID>> T repository(Class<? super T> repositoryClass) {
        return null;
    }

    @Override
    public <T> T transaction(Callable<T> task) {
        EntityTransaction et = jpaContext.entityManager().getTransaction();
        et.begin();
        try {
            T result = task.call();
            et.commit();
            return result;
        } catch (Throwable t) {
            et.rollback();
            throw new DataException("Helidon Data transaction failed", t);
        }
    }

    @Override
    public void transaction(VoidCallable task) {
        EntityTransaction et = jpaContext.entityManager().getTransaction();
        et.begin();
        try {
            task.call();
            et.commit();
        } catch (Throwable t) {
            et.rollback();
            throw new DataException("Helidon Data transaction failed", t);
        }
    }

    @Override
    public <T> T transaction(Function<DataTransaction, T> task) {
        JpaTransaction t = new JpaTransaction(jpaContext.entityManager().getTransaction());
        t.begin();
        return task.apply(t);
    }

    @Override
    public  void transaction(Consumer<DataTransaction> task) {
        JpaTransaction t = new JpaTransaction(jpaContext.entityManager().getTransaction());
        t.begin();
        task.accept(t);
    }

}
