/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
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
package io.helidon.testing.junit5.suite.spi;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.helidon.testing.junit5.suite.Storage;

/**
 * Suite extension module service.
 * Module receives suite life-cycle events:<ul>
 *     <li><b>init</b> when module is being initialized and before {@link io.helidon.testing.junit5.suite.BeforeSuite}
 *                     methods are executed</li>
 *     <li><b>beforeTests</b> after {@link io.helidon.testing.junit5.suite.BeforeSuite} methods are executed and before
 *                            tests execution.</li>
 *     <li><b>afterTests</b> after tests execution and before {@link io.helidon.testing.junit5.suite.AfterSuite}
 *                           methods are executed</li>
 *     <li><b>close</b> when module is being closed and after {@link io.helidon.testing.junit5.suite.AfterSuite}
 *                      methods are executed</li></ul>
 *
 * There may exist multiple {@link SuiteProvider} instances in the tests. Life-cycle of the extension module is always bound
 * to the specific {@link SuiteProvider} instance which serves as a key for those events.
 * So for each {@link SuiteProvider} instance all life-cycle events are fired.
 */
public interface SuiteSupport {

    /**
     * Called during suite initialization phase to notify about initialization event
     * and provide {@link SuiteProvider} implementing class and internal suite jUnit5 storage.
     * Suite {@link io.helidon.testing.junit5.suite.BeforeSuite} methods are executed after
     * this method.
     *
     * @param provider the {@link SuiteProvider} implementing class
     * @param storage internal suite jUnit5 storage
     * @param callSupport suite parameter callSupport
     */
    void init(SuiteProvider provider, Storage storage, SuiteCallSupport callSupport);

    /**
     * Called after {@link io.helidon.testing.junit5.suite.BeforeSuite} methods are executed
     * and before tests execution.
     *
     * @param provider the {@link SuiteProvider} implementing class
     */
    void beforeTests(SuiteProvider provider);

    /**
     * Called after tests execution and before {@link io.helidon.testing.junit5.suite.AfterSuite}
     * methods are executed.
     *
     * @param provider the {@link SuiteProvider} implementing class
     */
    void afterTests(SuiteProvider provider);

    /**
     * Called during suite closing phase to notify about initialization event
     * and provide {@link SuiteProvider} implementing class and internal suite jUnit5 storage.
     * Suite {@link io.helidon.testing.junit5.suite.AfterSuite} methods are executed before
     * this method.
     *
     * @param provider the {@link SuiteProvider} implementing class
     */
    void close(SuiteProvider provider);

    /**
     * Called to check, whether extension module can resolve provided parameter {@link Type}.
     *
     * @param type type to be resolved
     * @return value of {@code true} when provided {@link Type} can be resolved or {@code false}
     *         otherwise
     */
    boolean supportsParameter(SuiteProvider provider, Type type);

    /**
     * Called to resolve supported type.
     * Will be called only when {@link #supportsParameter(SuiteProvider, Type)} returns
     * {@code true} for the same {@link Type} argument.
     *
     * @param type type to be resolved
     * @return resolved value
     */
    Object resolveParameter(SuiteProvider provider, Type type);

    /**
     * Suite parameter resolver and method call support.
     */
    interface SuiteCallSupport {

        /**
         * {@link Type} support check accessor.
         *
         * @return {@link Type} support check
         */
        Function<Type, Boolean> supports();

        /**
         * {@link Type} resolver accessor.
         *
         * @return {@link Type} resolver
         */
        Function<Type, Object> resolve();

        /**
         * Method executor
         */
        BiConsumer<Method, Object[]> invoke();

    }

}
