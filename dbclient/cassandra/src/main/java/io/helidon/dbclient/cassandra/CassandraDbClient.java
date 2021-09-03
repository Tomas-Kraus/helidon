/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
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
package io.helidon.dbclient.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import java.util.function.Function;

import io.helidon.common.reactive.Single;
import io.helidon.common.reactive.Subscribable;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbExecute;
import io.helidon.dbclient.DbTransaction;

/**
 * Cassandra driver handler.
 */
public class CassandraDbClient implements DbClient {

    private final CqlSession session;
    /**
     * Creates an instance of Cassandra driver handler.
     *
     * @param builder builder for Cassandra database
     */
    CassandraDbClient(CassandraDbClientProviderBuilder builder) {
        session = null;
    }
    
    @Override
    public <U, T extends Subscribable<U>> T inTransaction(Function<DbTransaction, T> executor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <U, T extends Subscribable<U>> T execute(Function<DbExecute, T> executor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String dbType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <C> Single<C> unwrap(Class<C> cls) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}