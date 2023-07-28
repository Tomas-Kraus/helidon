/*
 * Copyright (c) 2019, 2023 Oracle and/or its affiliates.
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
import io.helidon.common.features.api.Aot;
import io.helidon.common.features.api.Feature;
import io.helidon.common.features.api.HelidonFlavor;
import io.helidon.dbclient.jdbc.JdbcClientProvider;
import io.helidon.dbclient.jdbc.spi.JdbcConnectionPoolProvider;
import io.helidon.dbclient.jdbc.spi.JdbcCpExtensionProvider;
import io.helidon.dbclient.spi.DbClientProvider;

/**
 * Helidon Database Client JDBC.
 */
@Feature(value = "JDBC Database Client",
         description = "Database client over JDBC",
         in = HelidonFlavor.SE,
         path = {"DbClient", "JDBC"}
)
@Aot(description = "Tested with Helidon Oracle and H2 drivers (see examples)")
module io.helidon.dbclient.jdbc {

    requires static io.helidon.common.features.api;

    requires java.sql;
    requires com.zaxxer.hikari;

    requires transitive io.helidon.common;
    requires transitive io.helidon.dbclient;
    requires transitive io.helidon.builder.api;
    requires transitive io.helidon.config.metadata;

    exports io.helidon.dbclient.jdbc;
    exports io.helidon.dbclient.jdbc.spi;

    uses JdbcCpExtensionProvider;
    uses JdbcConnectionPoolProvider;

    provides DbClientProvider with JdbcClientProvider;

}
