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

package io.helidon.tests.integration.dbclient.appl.it.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.helidon.common.LogConfig;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.tests.integration.dbclient.appl.ApplMain;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientResponse;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 *
 */
public class LifeCycleExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static final Logger LOGGER = Logger.getLogger(LifeCycleExtension.class.getName());

    private static final String STORE_KEY = LifeCycleExtension.class.getName();

    /* Startup timeout in seconds for both database and web server. */
    private static final int TIMEOUT = 60;

    /* Thread sleep time in miliseconds while waiting for database or appserver to come up. */
    private static final int SLEEP_MILIS = 250;

    private static HelidonProcessRunner runner;
 
    /**
     * Test setup.
     *
     * @param ec current extension context
     * @throws Exception when test setup fails
     */
    @Override
    public void beforeAll(ExtensionContext ec) throws Exception {
        final Object resource = ec.getRoot().getStore(GLOBAL).get(STORE_KEY);
        if (resource == null) {
            LogConfig.configureRuntime();
            LOGGER.finest("Running beforeAll lifecycle method for the first time, invoking setup()");
            ec.getRoot().getStore(GLOBAL).put(STORE_KEY, this);
            setup();
        } else {
            LOGGER.finest("Running beforeAll lifecycle method next time, skipping setup()");
        }
    }

    /**
     * Setup JPA application tests.
     */
    private void setup() {
        LOGGER.fine("Running JPA application test setup()");
        waitForDatabase();
        startTheServer();
        init();
    }

    /**
     * Cleanup JPA application tests.
     */
    @Override
    public void close() throws Throwable {
        LOGGER.fine("Running JPA application test close()");
        shutdown();
    }

    protected static void startTheServer() {
        runner = HelidonProcessRunner
                .create(HelidonProcessRunner.ExecType.CLASS_PATH,
                        "io.helidon.tests.integration.dbclient.appl",
                        ApplMain.class.getName(),
                        "heldion-tests-integration-dbclient-appl",
                        null,
                        null);

        runner.startApplication();
        ApplClient.init(runner.port());
    }

    @SuppressWarnings("SleepWhileInLoop")
    public static void waitForDatabase() {
        final String dbUser = System.getProperty("db.user");
        final String dbPassword = System.getProperty("db.password");
        final String dbUrl = System.getProperty("db.url");
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
                LOGGER.finest(() -> String.format("Connection check: user=%s password=%s url=%s", dbUser, dbPassword, dbUrl));
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                closeConnection(conn);
                return;
            } catch (SQLException ex) {
                LOGGER.finest(() -> String.format("Connection check: %s", ex.getMessage()));
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
        long tmEnd = System.currentTimeMillis() + (TIMEOUT * 1000);
        boolean retry = true;
        while (retry) {
            try {
                WebClientResponse response = ApplClient.root().get().path("/status").submit().get();
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

    public void init() {
    }

    public void shutdown() {
        WebClientResponse response;
        try {
            response = ApplClient.root().get().path("/exit").submit().get();
            LOGGER.info(() -> String.format("Status: %s", response.status()));
        } catch (InterruptedException ie) {
            LOGGER.log(Level.WARNING, ie, () -> String.format("Thread was interrupted: %s", ie.getMessage()));
        } catch (ExecutionException ex) {
            LOGGER.log(Level.WARNING, ex, () -> String.format("Web client exception: %s", ex.getMessage()));
        }
    }

    /**
     * Close database connection.
     *
     * @param connection database connection
     */
    public static void closeConnection(final Connection connection) {
        try {
            connection.close();
        } catch (SQLException ex) {
            LOGGER.warning(() -> String.format("Could not close database connection: %s", ex.getMessage()));
        }
    }

}
