/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.helidon.dbclient.cassandra;

/**
 * Cassandra Configuration parameters.
 * Cassandra connection string URI:
 * {@code jdbc:cassandra://[username:password@]host1[:port1][,...hostN[:portN]]][/[keyspace][?options]]}
 */
public class CassandraDbClientConfig {
    
    private final String url;
    private final String username;
    private final String password;

    CassandraDbClientConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    String url() {
        return url;
    }

    String username() {
        return username;
    }

    String password() {
        return password;
    }

}
