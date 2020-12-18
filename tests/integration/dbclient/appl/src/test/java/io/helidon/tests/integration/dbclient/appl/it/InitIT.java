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

package io.helidon.tests.integration.dbclient.appl.it;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonObject;

import io.helidon.tests.integration.dbclient.appl.it.tools.ApplClient;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class InitIT {

    private static final Logger LOGGER = Logger.getLogger(InitIT.class.getName());

    @Test
    @Order(1)
    public void setup() {
        LOGGER.fine(() -> String.format("Running InitIT.setup"));
        JsonObject data = ApplClient.data(ApplClient.test("Init", "setup"));
        ApplClient.logJsonObject(Level.FINER, data);
    }

    @Test
    @Order(2)
    public void testHealthCheck() {
        LOGGER.fine(() -> String.format("Running InitIT.testHealthCheck"));
        JsonObject data = ApplClient.data(ApplClient.test("Init", "testPing"));
        ApplClient.logJsonObject(Level.FINER, data);
    }

    @Test
    @Order(3)
    public void testInitSchema() {
        LOGGER.fine(() -> String.format("Running InitIT.testInitSchema"));
        JsonObject data = ApplClient.data(ApplClient.test("Init", "testInitSchema"));
        ApplClient.logJsonObject(Level.FINER, data);
    }

}
