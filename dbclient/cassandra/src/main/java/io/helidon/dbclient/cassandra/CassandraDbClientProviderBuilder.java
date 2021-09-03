/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.helidon.dbclient.cassandra;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import io.helidon.common.GenericType;
import io.helidon.common.mapper.MapperManager;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbClientService;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbMapperManager;
import io.helidon.dbclient.DbStatements;
import io.helidon.dbclient.spi.DbClientProviderBuilder;
import io.helidon.dbclient.spi.DbMapperProvider;

/**
 *
 * @author kratz
 */
public class CassandraDbClientProviderBuilder implements DbClientProviderBuilder<CassandraDbClientProviderBuilder> {
    
    private final List<DbClientService> clientServices = new LinkedList<>();
    private final DbMapperManager.Builder dbMapperBuilder = DbMapperManager.builder();

    private String url;
    private String username;
    private String password;
    private DbStatements statements;
    private MapperManager mapperManager;
    private DbMapperManager dbMapperManager;
    private CassandraDbClientConfig dbConfig;

    @Override
    public CassandraDbClientProviderBuilder config(Config config) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CassandraDbClientProviderBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public CassandraDbClientProviderBuilder username(String username) {
        this.username = username;
        return this;
    }

    @Override
    public CassandraDbClientProviderBuilder password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public CassandraDbClientProviderBuilder statements(DbStatements statements) {
        this.statements = statements;
        return this;
    }

    @Override
    public CassandraDbClientProviderBuilder addMapperProvider(DbMapperProvider provider) {
        this.dbMapperBuilder.addMapperProvider(provider);
        return this;
    }

    @Override
    public <TYPE> CassandraDbClientProviderBuilder addMapper(DbMapper<TYPE> dbMapper, Class<TYPE> mappedClass) {
        this.dbMapperBuilder.addMapperProvider(new DbMapperProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
                if (type.equals(mappedClass)) {
                    return Optional.of((DbMapper<T>) dbMapper);
                }
                return Optional.empty();
            }
        });
        return this;
    }

    @Override
    public <TYPE> CassandraDbClientProviderBuilder addMapper(DbMapper<TYPE> dbMapper, GenericType<TYPE> mappedType) {
        this.dbMapperBuilder.addMapperProvider(new DbMapperProvider() {
            @Override
            public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> Optional<DbMapper<T>> mapper(GenericType<T> type) {
                if (type.equals(mappedType)) {
                    return Optional.of((DbMapper<T>) dbMapper);
                }
                return Optional.empty();
            }
        });
        return this;
    }

    @Override
    public CassandraDbClientProviderBuilder mapperManager(MapperManager manager) {
        this.mapperManager = manager;
        return this;
    }

    @Override
    public CassandraDbClientProviderBuilder addService(DbClientService clientService) {
        this.clientServices.add(clientService);
        return this;
    }

    @Override
    public DbClient build() {
        if (null == dbMapperManager) {
            this.dbMapperManager = dbMapperBuilder.build();
        }
        if (null == mapperManager) {
            this.mapperManager = MapperManager.create();
        }
        if (null == dbConfig) {
            dbConfig = new CassandraDbClientConfig(url, username, password);
        }

        return new CassandraDbClient(this);
    }

    List<DbClientService> clientServices() {
        return List.copyOf(clientServices);
    }

    DbMapperManager.Builder dbMapperBuilder() {
        return dbMapperBuilder;
    }

    DbStatements statements() {
        return statements;
    }

    MapperManager mapperManager() {
        return mapperManager;
    }

    DbMapperManager dbMapperManager() {
        return dbMapperManager;
    }

    CassandraDbClientConfig dbConfig() {
        return dbConfig;
    }

}
