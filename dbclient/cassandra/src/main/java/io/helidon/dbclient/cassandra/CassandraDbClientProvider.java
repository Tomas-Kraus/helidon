/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.helidon.dbclient.cassandra;

import io.helidon.dbclient.spi.DbClientProvider;

/**
 *
 * @author kratz
 */
public class CassandraDbClientProvider implements DbClientProvider {
    
    static final String DB_TYPE = "cassandra";

    @Override
    public String name() {
        return DB_TYPE;
    }

    @Override
    public CassandraDbClientProviderBuilder builder() {
        return new CassandraDbClientProviderBuilder();
    }

}
