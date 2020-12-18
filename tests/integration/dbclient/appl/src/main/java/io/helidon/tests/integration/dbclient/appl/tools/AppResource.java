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

import javax.json.JsonObject;

import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

/**
 * REST Resource for test application.
 */
public class AppResource implements Service {

    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/{class}/{method}", this::test);
    }
                

    /**
     * Test invocation.
     *
     * @param request  the server request
     * @param response the server response
     * @return test result
     */
    public JsonObject test(final ServerRequest request, final ServerResponse response) {
        final String cls = request.path().param("class");
        final String method = request.path().param("method");
        Dispatcher.test(cls, method, response);
        return null;
    }
    
}
