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
package io.helidon.tests.integration.junit5;

import java.util.Map;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.dbclient.DbClient;
import io.helidon.tests.integration.junit5.spi.SuiteProvider;

@TestConfig(file = "mysql.yaml",
            key  = ContainerSuite.CONFIG_KEY)
@ContainerTest(provider  = MySqlContainer.class,
               image     = "mysql:8.0",
               configKey = ContainerSuite.CONFIG_KEY,
               portKey   = ContainerSuite.PORT_KEY)
@DbClientTest(configKey = ContainerSuite.CONFIG_KEY,
              portKey = ContainerSuite.PORT_KEY)
public class ContainerSuite implements SuiteProvider {

    static final String CONFIG_KEY = "ContainerSuite.config.key";
    static final String PORT_KEY = "MySqlContainer.port.key";

    static final String BEFORE_KEY = "ContainerSuite.before";
    static final String AFTER_KEY = "ContainerSuite.after";
    static final String SETUP_CONFIG_KEY = "ContainerSuite.config";
    static final String SETUP_CONTAINER_KEY = "ContainerSuite.container";
    static final String SETUP_DBCLIENT_KEY = "ContainerSuite.dbClient";

    private SuiteContext suiteContext;
    private int counter;

    public ContainerSuite() {
        suiteContext = null;
        counter = 1;
    }

    // Store shared suite context when passed from suite initialization
    @Override
    public void suiteContext(SuiteContext suiteContext) {
        this.suiteContext = suiteContext;
    }

    @SetUpConfig
    public void setupConfig(Config.Builder builder) {
        System.out.println(String.format("Running setupConfig of ContainerSuite test class, order %d", counter));
        // Modify target Config content with additional node
        builder.addSource(ConfigSources.create(Map.of("id", "TEST")));
        suiteContext.storage().put(SETUP_CONFIG_KEY, counter++);
    }

    @SetUpContainer
    public void setupContainer(ContainerConfig.Builder builder) {
        System.out.println(String.format("Running setupContainer of ContainerSuite test class, order %d", counter));
        // Modify target ContainerConfig content with additional system variable
        builder.environment().put("MY_VARIABLE", "myValue");
        suiteContext.storage().put(SETUP_CONTAINER_KEY, counter++);
    }

    @SetUpDbClient
    public void setupDbClient(DbClient.Builder builder) {
        System.out.println(String.format("Running setupDbClient of ContainerSuite test class, order %d", counter));
        // Validate order of provider's methods execution
        suiteContext.storage().put(SETUP_DBCLIENT_KEY, counter++);
    }

    // Initialize database schema and data
    // Validate that @BeforeSuite is executed
    @BeforeSuite
    public void beforeSuite(DbClient dbClient) {
        System.out.println(String.format("Running beforeSuite of ContainerSuite test class, order %d", counter));
        // Database initialization goes here
        // Validate order of provider's methods execution
        suiteContext.storage().put(BEFORE_KEY, counter++);
    }

    // Validate that @AfterSuite is executed
    @AfterSuite
    public void afterSuite() {
        System.out.println(String.format("Running afterSuite of ContainerSuite test class, order %d", counter));
        suiteContext.storage().put(AFTER_KEY, counter);
    }

}
