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
package io.helidon.tests.integration.jpa.appl.start;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.stream.JsonParsingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import io.helidon.tests.integration.jpa.appl.Utils;
import io.helidon.tests.integration.jpa.appl.test.ClientUtils;
import io.helidon.tests.integration.jpa.appl.test.Validate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Initialize tests.
 */
public class CheckIT {

    private static enum DbType {

        DEFAULT,
        ORADB;

        /**
         * Get database type based on provided URL.
         *
         * @param dbUrl database URL to check
         * @return database type retrieved from URL
         */
        private static DbType get(final String dbUrl) {
            if (dbUrl == null) {
                throw new IllegalStateException("Database URL is null!");
            }
            if (dbUrl.startsWith("jdbc:oracle:thin")) {
                return ORADB;
            }
            return DEFAULT;
        }

    }

    /* Local logger instance. */
    private static final Logger LOGGER = Logger.getLogger(CheckIT.class.getName());

    /* Startup timeout in seconds. */
    private static final int TIMEOUT = 300;

    /* Thread sleep time in miliseconds while waiting for database or appserver to come up. */
    private static final int SLEEP_MILIS = 250;

    private static final Client CLIENT = ClientBuilder.newClient();
    // FIXME: Use random port.
    private static final WebTarget TARGET = CLIENT.target("http://localhost:7001/test");
    
    private static DbType DB_TYPE = DbType.DEFAULT;

    @SuppressWarnings("SleepWhileInLoop")
    public static void waitForDatabase(final String dbUrl, final String dbUser, final String dbPassword) {
        if (dbUrl == null) {
            throw new IllegalStateException("Database URL was not set!");
        }
        if (dbUser == null) {
            throw new IllegalStateException("Database user name was not set!");
        }
        if (dbPassword == null) {
            throw new IllegalStateException("Database user password was not set!");
        }
        long endTm = 1000 * TIMEOUT + System.currentTimeMillis();
        while (true) {
            try {
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                Utils.closeConnection(conn);
                return;
            } catch (SQLException ex) {
                LOGGER.info(() -> String.format("Connection check: %s", ex.getMessage()));
                if (System.currentTimeMillis() > endTm) {
                    throw new IllegalStateException(String.format("Database is not ready within %d seconds", TIMEOUT));
                }
                try {
                    Thread.sleep(SLEEP_MILIS);
                } catch (InterruptedException ie) {
                    LOGGER.log(Level.WARNING, ie, () -> String.format("Thread was interrupted: %s", ie.getMessage()));
                }
            }
        }
    }

    @SuppressWarnings("SleepWhileInLoop")
    public static void waitForServer() {
        WebTarget status = TARGET.path("/status");

        long tmEnd = System.currentTimeMillis() + (TIMEOUT * 1000);
        boolean retry = true;
        while (retry) {
            try {
                Response response = status.request().get();
                retry = false;
            } catch (Exception ex) {
                LOGGER.fine(() -> String.format("Connection check: %s", ex.getMessage()));
                if (System.currentTimeMillis() > tmEnd) {
                    throw new IllegalStateException(String.format("Appserver is not ready within %d seconds", TIMEOUT));
                }
                try {
                    Thread.sleep(SLEEP_MILIS);
                } catch (InterruptedException ie) {
                    LOGGER.log(Level.WARNING, ie, () -> String.format("Thread was interrupted: %s", ie.getMessage()));
                }
            }
        }
    }

    
    /**
     * Initialize MsSQL database.
     * Database name is retrieved from connection URL.
     *
     * @param dbUrl MsSQL database connection URL with database name
     * @param dbUser MsSQL database connection user name
     * @param dbPassword MsSQL database connection user password
     */
    private static void initOraDB(final String dbUrl, final String dbSysUser, final String dbSysPassword) {
        final String dbUser = System.getProperty("db.user");
        final String dbPassword = System.getProperty("db.password");
        try (Connection conn = DriverManager.getConnection(dbUrl, dbSysUser, dbSysPassword)) {
            try {
                Statement stmt = conn.createStatement();
                final int dbCount = stmt.executeUpdate(String.format("CREATE USER %s IDENTIFIED BY %s", dbUser, dbPassword));
                LOGGER.log(Level.INFO, () -> String.format("Executed EXEC statement. %d records modified.", dbCount));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not create database user:", ex);
            }
            try {
                Statement stmt = conn.createStatement();
                final int dbCount = stmt.executeUpdate(String.format("GRANT ALL PRIVILEGES TO %s", dbUser));
                LOGGER.log(Level.INFO, () -> String.format("Executed EXEC statement. %d records modified.", dbCount));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not grant privileges to user:", ex);
            }
        } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not open database connection:", ex);
        }
    }

    @BeforeAll
    public static void init() {
        final String dbUrl = System.getProperty("db.url");
        String dbUser;
        String dbPassword;
        DB_TYPE = DbType.get(dbUrl);
        switch (DB_TYPE) {
            case ORADB:
                dbPassword = System.getProperty("db.syspw");
                dbUser = "sys as sysdba";
                break;
            default:
                dbUser = System.getProperty("db.user");
                dbPassword = System.getProperty("db.password");
        }
        waitForDatabase(dbUrl, dbUser, dbPassword);
        waitForServer();
        switch (DB_TYPE) {
            case ORADB:
                initOraDB(dbUrl, dbUser, dbPassword);
                break;
        }
        ClientUtils.callJdbcTest("/setup");
    }

    @AfterAll
    public static void destroy() {
        ClientUtils.callJdbcTest("/destroy");
    }

    @Test
    @Order(1)
    public void testJdbcPing() {
        switch (DB_TYPE) {
            case ORADB:
                ClientUtils.callJdbcTest("/test/JdbcApiIT.pingOraDb");
                break;
            default:
                ClientUtils.callJdbcTest("/test/JdbcApiIT.ping");
        }
    }

    @Test
    @Order(2)
    public void testInit() {
        WebTarget status = TARGET.path("/init");
        Response response = status.request().get();
        String responseStr = response.readEntity(String.class);
        try {
            Validate.check(responseStr);
        } catch (JsonParsingException t) {
            LOGGER.log(Level.SEVERE, t, () -> String.format("Response is not JSON: %s, message: %s", t.getMessage(), responseStr));
        }
    }

    @Test
    @Order(3)
    public void testBeans() {
        WebTarget status = TARGET.path("/beans");
        Response response = status.request().get();
        String responseStr = response.readEntity(String.class);
        try {
            Validate.check(responseStr);
        } catch (JsonParsingException t) {
            LOGGER.log(Level.SEVERE, t, () -> String.format("Response is not JSON: %s, message: %s", t.getMessage(), responseStr));
        }
    }

}
