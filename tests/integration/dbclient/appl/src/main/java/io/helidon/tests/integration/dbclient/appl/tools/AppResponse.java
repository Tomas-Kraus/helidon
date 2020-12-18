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

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import io.helidon.dbclient.DbRow;
import io.helidon.webserver.ServerResponse;

/**
 *
 */
public class AppResponse {

    public static void sendDmlResponse(final ServerResponse response, final Supplier<CompletableFuture<Long>> test) {
        CompletableFuture<Long> future = test.get();
        future
                .thenAccept(result -> response.send(okStatus(result(result))))
                .exceptionally(t -> {
                    response.send(exceptionStatus(t));
                    return null;
                });
    }

    public static void sendResponse(final ServerResponse response, final Supplier<CompletableFuture<Optional<DbRow>>> test) {
        CompletableFuture<Optional<DbRow>> future = test.get();
        future
                .thenAccept(data -> data.ifPresentOrElse(
                    row -> response.send(okStatus(row.as(JsonObject.class))),
                    () -> response.send(okStatus(JsonObject.EMPTY_JSON_OBJECT))))
                .exceptionally(t -> {
                    response.send(exceptionStatus(t));
                    return null;
                });
    }

    public static JsonObject okStatus(final JsonObject data) {
        final JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("status", "OK");
        job.add("data", data != null ? data : JsonValue.NULL);
        return job.build();
    }

    public static JsonObject result(long value) {
        final JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("result", value);
        return job.build();
    }

    public static JsonObject exceptionStatus(final Throwable t) {
        final JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("status", "exception");
        final JsonArrayBuilder jabSt = Json.createArrayBuilder();
        Throwable current = t;
        while (current != null) {
            jabSt.add(buildStackTrace(current));
            current = current.getCause();
        }
        job.add("stacktrace", jabSt.build());
        return job.build();
    }

    private static JsonObject buildStackTrace(final Throwable t) {
        JsonObjectBuilder jobSt = Json.createObjectBuilder();
        jobSt.add("class", t.getClass().getName());
        jobSt.add("message", t.getMessage());
        final JsonArrayBuilder jab = Json.createArrayBuilder();
        final StackTraceElement[] elements = t.getStackTrace();
        if (elements != null) {
            for (final StackTraceElement element : elements) {
                JsonObjectBuilder jobElement = Json.createObjectBuilder();
                jobElement.add("loader", element.getClassLoaderName() != null ? Json.createValue(element.getClassLoaderName()) : JsonValue.NULL);
                jobElement.add("file", element.getFileName() != null ? Json.createValue(element.getFileName()) : JsonValue.NULL);
                jobElement.add("line", element.getLineNumber());
                jobElement.add("module", element.getModuleName() != null ? Json.createValue(element.getModuleName()) : JsonValue.NULL);
                jobElement.add("modVersion", element.getModuleVersion() != null ? Json.createValue(element.getModuleVersion()) : JsonValue.NULL);
                jobElement.add("loader", element.getClassLoaderName() != null ? Json.createValue(element.getClassLoaderName()) : JsonValue.NULL);
                jobElement.add("class", element.getClassName() != null ? Json.createValue(element.getClassName()) : JsonValue.NULL);
                jobElement.add("method", element.getMethodName() != null ? Json.createValue(element.getMethodName()) : JsonValue.NULL);
                jab.add(jobElement.build());
            }
        }
        jobSt.add("trace", jab.build());
        return jobSt.build();
    }

    public static JsonObject noClassStatus(final JsonObjectBuilder job, final String className) {
        JsonObjectBuilder jobSt = Json.createObjectBuilder();
        job.add("status", "noClass");
        job.add("class", className);
        return jobSt.build();
    }

    public static JsonObject noMethodStatus(final JsonObjectBuilder job, final String className, final String methodName) {
        JsonObjectBuilder jobSt = Json.createObjectBuilder();
        job.add("status", "noClass");
        job.add("class", className);
        job.add("method", methodName);
        return jobSt.build();
    }

}
