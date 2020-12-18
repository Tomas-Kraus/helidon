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

package io.helidon.tests.integration.dbclient.appl.tools;

import java.util.logging.Logger;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;

/**
 * Configuration utilities.
 */
public class AppConfig {

    /** Local logger instance. */
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());

    private static final String CONFIG_PROPERTY_NAME="app.config";

    private static final String DEFAULT_CONFIG_FILE="test.yaml";

    /** Application configuration. */
    public static final Config CONFIG = Config.create(ConfigSources.classpath(configFile()));

    /**
     * Retrieve configuration file from {@code io.helidon.tests.integration.dbclient.config}
     * property if exists.
     * Default {@code test.yaml} value is used when no property is set.
     *
     * @return tests configuration file name
     */
    public static String configFile() {
        String configFile = System.getProperty(CONFIG_PROPERTY_NAME, DEFAULT_CONFIG_FILE);
        LOGGER.info(() -> String.format("Configuration file: %s", configFile));
        return configFile;
    }


}
