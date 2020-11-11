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
package io.helidon.tests.integration.jpa.appl.test;

import java.net.MalformedURLException;
import java.net.URL;
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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Initialize tests.
 */
public class CheckIT {

    /* Local logger instance. */
    private static final Logger LOGGER = Logger.getLogger(CheckIT.class.getName());

    /* Startup timeout in seconds. */
    private static final int TIMEOUT = 60;

    /* Thread sleep time in miliseconds while waiting for database or appserver to come up. */
    private static final int SLEEP_MILIS = 250;

    private static final Client CLIENT = ClientBuilder.newClient();
    // FIXME: Use random port.
    private static final WebTarget TARGET = CLIENT.target("http://localhost:7001/test");

    @SuppressWarnings("SleepWhileInLoop")
    public static void waitForDatabase(final String dbUrl, final String dbUser, final String dbPassword) {
        if (dbUser == null) {
            throw new IllegalStateException("Database user name was not set!");
        }
        if (dbPassword == null) {
            throw new IllegalStateException("Database user password was not set!");
        }
        if (dbUrl == null) {
            throw new IllegalStateException("Database URL was not set!");
        }
        long endTm = 1000 * TIMEOUT + System.currentTimeMillis();
        while (true) {
            try {
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                Utils.closeConnection(conn);
                return;
            } catch (SQLException ex) {
                LOGGER.fine(() -> String.format("Connection check: %s", ex.getMessage()));
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
     * Check whether database URL contains MsSQL connection information.
     *
     * @param dbUrl database URL
     * @return value of {@code true} when URL contains MsSQL connection information
     *         or {@code false} otherwise
     */
    private static boolean isMsSQL(final String dbUrl) {
        return dbUrl != null && dbUrl.startsWith("jdbc:sqlserver");
    }

    /**
     * Strip query parameters from MsSQL URL to get SA connection URL.
     *
     * @param dbUrl database URL
     * @return database URL without query parameters
     */
    private static String saUrlOfMsSQL(final String dbUrl) {
        final int semiColonPos = dbUrl.indexOf(';');
        return semiColonPos > 0 ? dbUrl.substring(0, semiColonPos) : dbUrl;
    }

    /**
     * Initialize MsSQL database.
     * Database name is retrieved from connection URL.
     *
     * @param dbUrl MsSQL database connection URL with database name
     * @param dbUser MsSQL database connection user name
     * @param dbPassword MsSQL database connection user password
     */
    private static void initMsSQL(final String dbUrl, final String dbUser, final String dbPassword) {
        String database = null;
        final int semiColonPos = dbUrl.indexOf(';');
        if (semiColonPos < 0) {
            throw new IllegalArgumentException("MsSQL URL Query does not contain query parameters");
        }
        final String urlQuery = dbUrl.substring(semiColonPos + 1);
        LOGGER.fine(() -> String.format("URL %s has query part %s", dbUrl, urlQuery));
        final int pos = urlQuery.indexOf("databaseName=");
        if (pos < 0) {
            throw new IllegalArgumentException("MsSQL URL Query does not contain databaseName parameter");
        }
        if (urlQuery.length() < (pos + 14)) {
            throw new IllegalArgumentException("MsSQL URL Query dose not contain databaseName parameter value");
        }
        final int end = urlQuery.indexOf(dbUser, pos + 13);
        database = end > 0 ? urlQuery.substring(pos + 13, end) : urlQuery.substring(pos + 13);
        if (database == null) {
            throw new IllegalStateException("Missing database name!");
        }
        final String dbSaPassword = System.getProperty("db.sa.password");
        try (Connection conn = DriverManager.getConnection(saUrlOfMsSQL(dbUrl), "sa", dbSaPassword)) {    
            try {
                Statement stmt = conn.createStatement();
                final int dbCount = stmt.executeUpdate(String.format("EXEC sp_configure 'CONTAINED DATABASE AUTHENTICATION', 1", database));
                LOGGER.log(Level.INFO, () -> String.format("Executed EXEC statement. %d records modified.", dbCount));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not configure database:", ex);
            } try {
                Statement stmt = conn.createStatement();
                final int dbCount = stmt.executeUpdate(String.format("RECONFIGURE", database));
                LOGGER.log(Level.INFO, () -> String.format("Executed RECONFIGURE statement. %d records modified.", dbCount));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not reconfigure database:", ex);
            } try {
                Statement stmt = conn.createStatement();
                final int dbCount = stmt.executeUpdate(String.format("CREATE DATABASE %s CONTAINMENT = PARTIAL", database));
                LOGGER.log(Level.INFO, () -> String.format("Executed CREATE DATABASE statement. %d records modified.", dbCount));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not create database:", ex);
            } try {
                Statement stmt = conn.createStatement();
                final int useCount = stmt.executeUpdate(String.format("USE %s", database));
                LOGGER.log(Level.INFO, () -> String.format("Executed USE statement. %d records modified.", useCount));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not use database:", ex);
            } try {
                Statement stmt = conn.createStatement();//"CREATE USER ? WITH PASSWORD = ?");
                final int userCount = stmt.executeUpdate(String.format("CREATE USER %s WITH PASSWORD = '%s'", dbUser, dbPassword));
                LOGGER.log(Level.INFO, () -> String.format("Executed CREATE USER statement. %d records modified.", userCount));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not create database user:", ex);
            } try {
                Statement stmt = conn.createStatement();//"CREATE USER ? WITH PASSWORD = ?");
                final int userCount = stmt.executeUpdate(String.format("GRANT ALL TO %s", dbUser));
                LOGGER.log(Level.INFO, () -> String.format("Executed GRANT statement. %d records modified.", userCount));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not grant database privilegs to user:", ex);
            } try {
                Statement stmt = conn.createStatement();//"CREATE USER ? WITH PASSWORD = ?");
                final int userCount = stmt.executeUpdate(String.format("GRANT CONTROL ON SCHEMA::dbo TO %s", dbUser));
                LOGGER.log(Level.INFO, () -> String.format("Executed GRANT statement. %d records modified.", userCount));
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not grant database privilegs to user:", ex);
            }
        } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not open database connection:", ex);
        }
    }

    @BeforeAll
    public static void init() {
        final String dbUser = System.getProperty("db.user");
        final String dbPassword = System.getProperty("db.password");
        final String dbUrl = System.getProperty("db.url");
        if (isMsSQL(dbUrl)) {
            final String dbSaPassword = System.getProperty("db.sa.password");
            waitForDatabase(saUrlOfMsSQL(dbUrl), "sa", dbSaPassword);
        } else {
            waitForDatabase(dbUrl, dbUser, dbPassword);
        }
        waitForServer();
        if (isMsSQL(dbUrl)) {
            initMsSQL(dbUrl, dbUser, dbPassword);
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
        ClientUtils.callJdbcTest("/test/JdbcApiIT.ping");
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
