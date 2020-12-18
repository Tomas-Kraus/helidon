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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import io.helidon.dbclient.DbRow;
import io.helidon.webserver.ServerResponse;

/**
 * Test methods dispatcher.
 * Each test method is identified by class name and method name.
 */
public class Dispatcher {

    private static final class TestClass {

        private final Map<String, TestRunner> methods;
        private final Object instance;
    
        TestClass(final Object instance) {
            this.methods = new HashMap<>();
            this.instance = instance;
        }
    
        Map<String, TestRunner> methods() {
            return methods;
        }

        Object instance() {
            return instance;
        }

    }

    private static final Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());

    private static final Dispatcher DISPATCHER = new Dispatcher();

    /* Tests references. */
    private final Map<String, TestClass> tests = new HashMap<>();


    /**
     * Add test class with custom name and its annotated methods.
     *
     * @param name test class custom name, {@code cls.getSimpleName()} is used when {@code null}
     * @param cls test class
     */ 
    public static void add(final String name, Class<?> cls) {
        DISPATCHER.register(name, cls);
    }

    /**
     * Add test class with default name and its annotated methods.
     *
     * @param cls test class
     */    
    public static void add(Class<?> cls) {
        DISPATCHER.register(null, cls);
    }

    /**
     * Test execution.
     * Executes test method and returns execution status as JSON object.
     * Returned value contains execution status and data or error message.<br>
     * <b>Statuses:</b><ul>
     * <li><b>OK</b> when method completed successfully, object contains returned data</li>
     * <li><b>exception</b> when method threw an exception, object contains stack trace</li>
     * <li><b>noClass</b> when test class was not found, object contains missing class name</li>
     * <li><b>noMethod</b> when test class was not found, object contains error message</li></ul>
     * 
     * @param className name (key) of class containing test method
     * @param methodName name of method in the class to me invoked
     */
    public static void test(final String className, final String methodName, final ServerResponse response) {
        DISPATCHER.runTest(className, methodName, response);
    }

    private void register(final String name, Class<?> cls) {
        final MethodHandles.Lookup mhl = MethodHandles.publicLookup();
        final MethodType mt = MethodType.methodType(CompletableFuture.class, ServerResponse.class);
        final MethodType ct = MethodType.methodType(void.class);
        final String className = name != null ? name : cls.getSimpleName();
        final Method[] methods = cls.getDeclaredMethods();
        Map<String, TestRunner> classTests = null;
        boolean first = true;
        for (final Method method : methods) {
            if (method.isAnnotationPresent(AppTestOptionalDbRow.class)) {
                Class<?> rt = method.getReturnType();
                if (first) try {
                    final MethodHandle mh = mhl.findConstructor(cls, ct);
                    try {
                        Object instance = mh.invoke();
                        TestClass tc = new TestClass(instance);
                        tests.put(className, tc);
                        classTests = tc.methods();
                    } catch (Throwable t) {
                        LOGGER.log(Level.WARNING, t, () -> String.format("%s constructor failed: %s - %s", cls.getName(), t.getClass().getName(), t.getMessage()));
                        break;
                    }
                    LOGGER.finer(() -> String.format("Registered class %s with key %s in Dispatcher", cls.getSimpleName(), className));
                } catch (NoSuchMethodException ex) {
                    LOGGER.warning(() -> String.format("Method %s was not found in class %s: %s", method.getName(), className, ex.getMessage()));
                    break;
                } catch (IllegalAccessException ex) {
                    LOGGER.warning(() -> String.format("Method %s in class %s could not be accessed: %s", method.getName(), className, ex.getMessage()));
                    break;
                }
                try {
                    final String methodName = method.getName();
                    final MethodHandle mh = mhl.findVirtual(cls, methodName, mt);
                    classTests.put(methodName, new TestRunnerOptionalDbRow(mh));
                    LOGGER.finer(() -> String.format("Registered method %s of class with key %s in Dispatcher", methodName, className));
                } catch (NoSuchMethodException ex) {
                    LOGGER.warning(() -> String.format("Method %s was not found in class %s: %s", method.getName(), className, ex.getMessage()));
                } catch (IllegalAccessException ex) {
                    LOGGER.warning(() -> String.format("Method %s in class %s could not be accessed: %s", method.getName(), className, ex.getMessage()));
                }
            }
        }
        
    }

    private static abstract class TestRunner {

        final MethodHandle mh;

        TestRunner(final MethodHandle mh) {
            this.mh = mh;
        }

        abstract void runTest(final Object instance, final ServerResponse response);

    }

    private static final class TestRunnerOptionalDbRow extends TestRunner {

        TestRunnerOptionalDbRow(final MethodHandle mh) {
            super(mh);
        }

        @Override
        void runTest(final Object instance, final ServerResponse response) {
            try {
                CompletableFuture<Optional<DbRow>> future = (CompletableFuture<Optional<DbRow>>) mh.invoke(instance, response);
                future
                        .thenAccept(data -> data.ifPresentOrElse(
                        row -> response.send(AppResponse.okStatus(row.as(JsonObject.class))),
                        () -> response.send(AppResponse.okStatus(JsonObject.EMPTY_JSON_OBJECT))))
                        .exceptionally(t -> {
                            response.send(AppResponse.exceptionStatus(t));
                            return null;
                        });
            } catch (Throwable t) {
                response.send(AppResponse.exceptionStatus(t));
            }
        }

    }

    private void runTest(final String className, final String methodName, final ServerResponse response) {
        final TestClass tc = tests.get(className);
        JsonObjectBuilder job = Json.createObjectBuilder();
        if (tc != null) {
            TestRunner runner = tc.methods().get(methodName);
            if (runner != null) {
                runner.runTest(tc.instance(), response);
            } else {
                response.send(AppResponse.noMethodStatus(job, className, methodName));
            }
        } else {
            response.send(AppResponse.noClassStatus(job, className));
        }
    }

}
