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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    private static final Client CLIENT = ClientBuilder.newClient();
    private static final WebTarget TARGET = CLIENT.target("http://localhost:7001/test");

    @SuppressWarnings("SleepWhileInLoop")
    public static void waitForDatabase() {
        final String dbUser = System.getProperty("db.user");
        final String dbPassword = System.getProperty("db.password");
        final String dbUrl = System.getProperty("db.url");
        boolean connected = false;
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
                connected = true;
                Utils.closeConnection(conn);
                return;
            } catch (SQLException ex) {
                LOGGER.info(() -> String.format("Connection check: %s", ex.getMessage()));
                if (System.currentTimeMillis() > endTm) {
                    throw new IllegalStateException(String.format("Database is not ready within %d seconds", TIMEOUT));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    System.out.println("STATUS: " + ie.getMessage());
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
                System.out.println("STATUS: " + response.readEntity(String.class));
                retry = false;
            } catch (Exception ex) {
                System.out.println("STATUS: " + ex.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    System.out.println("STATUS: " + ie.getMessage());
                }
                if (System.currentTimeMillis() > tmEnd) {
                    System.out.println("STATUS: Startup timeout");
                    retry = false;
                }
            }
        }
    }

    @BeforeAll
    public static void init() {
        waitForDatabase();
        waitForServer();
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
            LOGGER.severe(() -> String.format("Response is not JSON: %s", t.getMessage()));
            LOGGER.info(() -> String.format("Message: %s", responseStr));
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
            LOGGER.severe(() -> String.format("Response is not JSON: %s", t.getMessage()));
            LOGGER.info(() -> String.format("Message: %s", responseStr));
        }
    }

}
