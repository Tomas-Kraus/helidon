/*
 * Copyright (c) 2022 Oracle and/or its affiliates.
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
package io.helidon.data.runtime;

import java.util.Optional;

/**
 * Selection part of the Helidon dynamic finder query.
 */
public class DynamicFinderSelection {

    /**
     * Query selection methods.
     */
    public enum Method {
        /**
         * Return single result.
         */
        GET("get"),
        /**
         * Return multiple results.
         */
        FIND("find");

        /**
         * Query selection methods enumeration length.
         */
        public static final int LENGTH = values().length;

        //  Supported method keyword.
        private final String keyword;

        Method(String keyword) {
            this.keyword = keyword;
        }

        /**
         * Supported method keyword.
         *
         * @return selection method keyword
         */
        public String keyword() {
            return keyword;
        }

    }

    /**
     * Query selection projection.
     */
    public static class Projection {

        /**
         * Class constant for TOP method parameter.
         */
        public static final Class<Integer> TOP_PARAM_CLASS = Integer.class;

        /**
         * Projection methods.
         */
        public enum Method {
            /**
             * Return count of the values.
             */
            COUNT("Count", Void.class),
            /**
             * Return count of the distinct values.
             */
            COUNT_DISTINCT("CountDistinct", Void.class),
            /**
             * Return the distinct values.
             */
            DISTINCT("Distinct", Void.class),
            /**
             * Return the maximum value.
             */
            MAX("Max", Void.class),
            /**
             * Return the minimum value.
             */
            MIN("Min", Void.class),
            /**
             * Return the summary of all values.
             */
            SUM("Sum", Void.class),
            /**
             * Return the average of all values.
             */
            AVG("Avg", Void.class),
            /**
             * Return first N of all values. N is integer value.
             */
            TOP("Top", TOP_PARAM_CLASS);

            //  Supported method keyword.
            public final String keyword;
            // Type of the projection parameter.
            public final Class<?> type;

            /**
             * Query projection methods enumeration length.
             */
            public static final int LENGTH = values().length;

            // Creates an instance of projection method.
            Method(String keyword, Class<?> type) {
                this.keyword = keyword;
                this.type = type;
            }

            /**
             * Supported method keyword.
             *
             * @return projection method keyword
             */
            public String keyword() {
                return keyword;
            }

        }

        public static Projection build(Method method, Optional<?> parameter) {
            return new Projection(method, parameter);
        }

        // Projection method.
        private final Method method;
        // Projection method parameter.
        private final Optional<?> parameter;

        // Creates an instance of query selection projection.
        private Projection(Method method, Optional<?> parameter) {
            this.method = method;
            this.parameter = parameter;
        }

        /**
         * Projection method.
         *
         * @return method of the finder query projection
         */
        public Method method() {
            return method;
        }

        /**
         * Projection method parameter.
         *
         * @param <T> type of the finder query projection method parameter, matches {@link Method#type}
         * @return optional finder query projection method parameter
         */
        @SuppressWarnings("unchecked")
        public <T> Optional<T> parameter() {
            return (Optional<T>) parameter;
        }

    }

    public static DynamicFinderSelection build(Method method, Optional<Projection> projection, Optional<String> property) {
        return new DynamicFinderSelection(method, projection, property);
    }

    // Query selection method.
    private final Method method;
    // Query selection projection.
    private final Optional<Projection> projection;
    // Query selection property.
    final Optional<String> property;

    // Creates an instance of selection part of the Helidon dynamic finder query.
    private DynamicFinderSelection(Method method, Optional<Projection> projection, Optional<String> property) {
        this.method = method;
        this.projection = projection;
        this.property = property;
    }

    /**
     * Query selection method.
     *
     * @return method of the query selection
     */
    public Method method() {
        return method;
    }

    /**
     * Query selection projection.
     *
     * @return projection of the query selection
     */
    public Optional<Projection> projection() {
        return projection;
    }

    /**
     * Query selection property.
     *
     * @return property of the query selection
     */
    public Optional<String> property() {
        return property;
    }

}
