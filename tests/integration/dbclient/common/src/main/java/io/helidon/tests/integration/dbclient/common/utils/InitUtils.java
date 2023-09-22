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
package io.helidon.tests.integration.dbclient.common.utils;

import java.sql.DriverManager;
import java.util.Map;

import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbExecute;
import io.helidon.tests.integration.dbclient.common.model.Pokemon;
import io.helidon.tests.integration.dbclient.common.model.Type;

/**
 * Test initialization utilities.
 */
public class InitUtils {

    private static final System.Logger LOGGER = System.getLogger(InitUtils.class.getName());

    /**
     * Check code to be executed periodically while waiting for database container to come up.
     */
    @FunctionalInterface
    public interface StartCheck {
        /**
         * Check whether database is already up and accepts connections.
         *
         * @throws Exception when check failed
         */
        void check() throws Exception;
    }

    /**
     * Wait for database container to come up.
     *
     * @param check container check to be executed periodically until no exception is thrown
     * @param timeout container start up timeout in seconds
     */
    @SuppressWarnings("SleepWhileInLoop")
    public static void waitForStart(StartCheck check, int timeout) {
        System.out.println("Waiting for database server to come up");
        long endTm = 1000L * timeout + System.currentTimeMillis();
        while (true) {
            try {
                check.check();
                break;
            } catch (Throwable th) {
                if (System.currentTimeMillis() > endTm) {
                    throw new IllegalStateException("Database startup failed!", th);
                }
                LOGGER.log(System.Logger.Level.DEBUG, () -> String.format("Exception: %s", th.getMessage()), th);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * Wait for database container to come up.
     * With single check timeout modified.
     *
     * @param check container check to be executed periodically until no exception is thrown
     * @param timeout container start up timeout in seconds
     * @param checkTimeout single check timeout in seconds
     */
    public static void waitForStart(StartCheck check, int timeout, int checkTimeout) {
        int currentLoginTimeout = DriverManager.getLoginTimeout();
        DriverManager.setLoginTimeout(checkTimeout);
        InitUtils.waitForStart(check, timeout);
        DriverManager.setLoginTimeout(currentLoginTimeout);
    }

    /**
     * Initialize database schema.
     *
     * @param dbClient database client instance
     */
    public static void initSchema(DbClient dbClient) {
        DbExecute exec = dbClient.execute();
        exec.namedDml("create-types");
        exec.namedDml("create-pokemons");
        exec.namedDml("create-poketypes");
    }

    /**
     * Initialize database data.
     *
     * @param dbClient database client instance
     */
    public static void initData(DbClient dbClient) {
        DbExecute exec = dbClient.execute();
        long count = 0;
        for (Map.Entry<Integer, Type> entry : Type.TYPES.entrySet()) {
            count += exec.namedInsert("insert-type", entry.getKey(), entry.getValue().name());
        }

        for (Map.Entry<Integer, Pokemon> entry : Pokemon.POKEMONS.entrySet()) {
            count += exec.namedInsert("insert-pokemon", entry.getKey(), entry.getValue().getName());
        }

        for (Map.Entry<Integer, Pokemon> entry : Pokemon.POKEMONS.entrySet()) {
            Pokemon pokemon = entry.getValue();
            for (Type type : pokemon.getTypes()) {
                count += exec.namedInsert("insert-poketype", pokemon.getId(), type.id());
            }
        }
        LOGGER.log(System.Logger.Level.INFO, String.format("executed %s statements", count));
    }

    /**
     * Destroy database data.
     *
     * @param dbClient database client instance
     */
    public static void dropSchema(DbClient dbClient) {
        DbExecute exec = dbClient.execute();
        exec.namedDml("drop-poketypes");
        exec.namedDml("drop-pokemons");
        exec.namedDml("drop-types");
    }

}
