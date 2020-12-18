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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.webclient.WebClient;

import org.junit.jupiter.api.Assertions;

/**
 * Web client to access application.
 */
public class ApplClient {

    private static final Logger LOGGER = Logger.getLogger(ApplClient.class.getName());

    private static String BASE_URL = null;
    private static String TEST_URL = null;

    private static WebClient ROOT_CLIENT = null;

    private static WebClient TEST_CLIENT = null;

    public static final void init(final int port) {
        BASE_URL = "http://localhost:" + String.valueOf(port);
        TEST_URL = BASE_URL + "/test";
        ROOT_CLIENT = WebClient.builder()
            .baseUri(BASE_URL)
            .addMediaSupport(JsonpSupport.create())
            .build();
        TEST_CLIENT = WebClient.builder()
            .baseUri(TEST_URL)
            .addMediaSupport(JsonpSupport.create())
            .build();
    } 


    public static WebClient root() {
        return ROOT_CLIENT;
    }


    public static JsonObject testDispatcher(final String cls, final String method) {
        final StringBuilder sb = new StringBuilder(cls.length() + method.length() + 2);
        sb.append('/');
        sb.append(cls);
        sb.append('/');
        sb.append(method);
        try {
            return TEST_CLIENT.get().path(sb.toString()).submit().get().content().as(JsonObject.class).get();
        } catch (InterruptedException ie) {
            LOGGER.log(Level.WARNING, ie, () -> String.format("Thread was interrupted: %s", ie.getMessage()));
            Assertions.fail(String.format("Thread was interrupted: %s", ie.getMessage()), ie);
        } catch (ExecutionException ex) {
            LOGGER.log(Level.WARNING, ex, () -> String.format("Web client exception: %s", ex.getMessage()));
            Assertions.fail(String.format("Web client exception: %s", ex.getMessage()), ex);
        }
        return null;
    }

    
    public static JsonObject test(final String cls, final String method) {
        final StringBuilder sb = new StringBuilder(cls.length() + method.length() + 2);
        sb.append('/');
        sb.append(cls);
        sb.append('/');
        sb.append(method);
        try {
            return ROOT_CLIENT.get().path(sb.toString()).submit().get().content().as(JsonObject.class).get();
        } catch (InterruptedException ie) {
            LOGGER.log(Level.WARNING, ie, () -> String.format("Thread was interrupted: %s", ie.getMessage()));
            Assertions.fail(String.format("Thread was interrupted: %s", ie.getMessage()), ie);
        } catch (ExecutionException ex) {
            LOGGER.log(Level.WARNING, ex, () -> String.format("Web client exception: %s", ex.getMessage()));
            Assertions.fail(String.format("Web client exception: %s", ex.getMessage()), ex);
        } 
        return null;
    }

    public static JsonObject data(final JsonObject response) {
        String status = response.getString("status");
        switch (status) {
            case "OK":
                return response.getJsonObject("data");
            case "exception":
                Assertions.fail(logStackTrace(response));
            default:
                Assertions.fail("Unknown response content: " + response.toString());
        }
        return null;
    }
    
    private static String logStackTrace(final JsonObject response) {
        JsonArray stacktrace = response.getJsonArray("stacktrace");
        List<JsonObject> tracesList = stacktrace.getValuesAs(JsonObject.class);
        String logMsg = null;
        for (JsonObject trace : tracesList) {
            String message = trace.getString("message");
            LOGGER.warning(() -> String.format("%s: %s", trace.getString("class"), trace.getString("message")));
            JsonArray lines = trace.getJsonArray("trace");
            List<JsonObject> linesList = lines.getValuesAs(JsonObject.class);
            for (JsonObject line : linesList) {
                LOGGER.warning(() -> String.format("    at %s$%s (%s:%d)",
                        line.getString("class"), line.getString("method"), line.getString("file"), line.getInt("line")));
            }
            if (logMsg == null) {
                logMsg = message;
            }
        }
        return logMsg;
    }
    

    public static void logJsonObject(final Level level, final JsonObject data) {
        for (Map.Entry<String, JsonValue> entry : data.entrySet()) {
            LOGGER.log(level, () -> String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
        }
    }

}
