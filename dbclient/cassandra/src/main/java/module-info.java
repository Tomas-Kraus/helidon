/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates.
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

/**
 * Helidon DB Client Cassandra.
 */
module io.helidon.dbclient.cassandra {
    requires java.logging;
    requires java.sql;

    requires transitive io.helidon.common;
    requires transitive io.helidon.common.configurable;
    requires transitive io.helidon.dbclient;
    requires transitive io.helidon.dbclient.common;

    exports io.helidon.dbclient.cassandra;

    //provides io.helidon.dbclient.spi.DbClientProvider with io.helidon.dbclient.jdbc.JdbcDbClientProvider;
}