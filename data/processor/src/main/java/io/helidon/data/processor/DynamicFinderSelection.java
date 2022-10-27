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
package io.helidon.data.processor;

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
        static final int LENGTH = values().length;

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
        String keyword() {
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
            static final int LENGTH = values().length;

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
            String keyword() {
                return keyword;
            }

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

    // Helidon dynamic finder query builder is not public API.
    /**
     * Helidon dynamic finder query selection builder.
     */
    static class Builder {

        // Parent class builder where all parts are put together.
        private final DynamicFinder.Builder builder;
        // Query selection method.
        private Method method;
        // Query selection projection.
        private Projection projection;
        // Query selection property.
        private String property;

        // Creqates an instanceof query selection builder.
        Builder(DynamicFinder.Builder builder) {
            this.builder = builder;
            this.method = null;
            this.projection = null;
            this.property = null;
        }

        /**
         * Select query projection: {@code Count}.
         *
         * @return builder with query projection set as {@code Count}
         */
        DynamicFinderSelection.Builder count() {
            projection = new Projection(
                    Projection.Method.COUNT, Optional.empty());
            return this;
        }

        /**
         * Select query projection: {@code CountDistinct}.
         *
         * @return builder with query projection set as {@code CountDistinct}
         */
        DynamicFinderSelection.Builder countDistinct() {
            projection = new Projection(
                    Projection.Method.COUNT_DISTINCT, Optional.empty());
            return this;
        }

        /**
         * Select query projection: {@code Distinct}.
         *
         * @return builder with query projection set as {@code Distinct}
         */
        DynamicFinderSelection.Builder distinct() {
            projection = new Projection(
                    Projection.Method.DISTINCT, Optional.empty());
            return this;
        }

        /**
         * Select query projection: {@code Max}.
         *
         * @return builder with query projection set as {@code Max}
         */
        DynamicFinderSelection.Builder max() {
            projection = new Projection(
                    Projection.Method.MAX, Optional.empty());
            return this;
        }

        /**
         * Select query projection: {@code Min}.
         *
         * @return builder with query projection set as {@code Min}
         */
        DynamicFinderSelection.Builder min() {
            projection = new Projection(
                    Projection.Method.MIN, Optional.empty());
            return this;
        }

        /**
         * Select query projection: {@code Sum}.
         *
         * @return builder with query projection set as {@code Sum}
         */
        DynamicFinderSelection.Builder sum() {
            projection = new Projection(
                    Projection.Method.SUM, Optional.empty());
            return this;
        }

        /**
         * Select query projection: {@code Avg}.
         *
         * @return builder with query projection set as {@code Avg}
         */
        DynamicFinderSelection.Builder avg() {
            projection = new Projection(
                    Projection.Method.AVG, Optional.empty());
            return this;
        }

        /**
         * Select query projection: {@code Top(Integer)}.
         *
         * @param count number of results to return
         * @return builder with query projection set as {@code Top(Integer)}
         */
        DynamicFinderSelection.Builder top(int count) {
            projection = new Projection(
                    Projection.Method.TOP, Optional.of(count));
            return this;
        }

        /**
         * Set query property
         *
         * @param property name of the entity property
         * @return builder with query property set
         */
        DynamicFinderSelection.Builder property(String property) {
            if (property == null) {
                throw new IllegalArgumentException("Query selection property shall not be null");
            }
            if (property.isEmpty()) {
                throw new IllegalArgumentException("Query selection property shall not be empty value");
            }
            this.property = property;
            return this;
        }

        /**
         * Build Helidon dynamic finder query criteria.
         *
         * @param property criteria expression parameter: Entity property name
         */
        DynamicFinderCriteria.Builder by(String property) {
            // Finalize selection first.
            builder.setSelection(
                    new DynamicFinderSelection(
                            this.method,
                            this.projection != null ? Optional.of(this.projection) : Optional.empty(),
                            this.property != null ? Optional.of(this.property) : Optional.empty()));
            // Return criteria builder.
            return new DynamicFinderCriteria.Builder(builder).by(property);
        }

        /**
         * Build Helidon dynamic finder query criteria.
         * This is a shortcut to add default {@link DynamicFinderCriteria.Expression.Condition.Operator.EQUALS} condition
         * for provided property.
         *
         * @param property       criteria expression parameter: Entity property name
         * @param conditionValue condition property value: used in {@code setParameter(property, conditionValue)} call.
         */
        DynamicFinderCriteria.Builder by(String property, String conditionValue) {
            // Finalize selection first.
            builder.setSelection(
                    new DynamicFinderSelection(
                            this.method,
                            this.projection != null ? Optional.of(this.projection) : Optional.empty(),
                            this.property != null ? Optional.of(this.property) : Optional.empty()));
            // Return criteria builder.
            return new DynamicFinderCriteria.Builder(builder).by(property, conditionValue);
        }

        /**
         * Build Helidon dynamic finder query order.
         *
         * @param property criteria expression parameter: Entity property name
         */
        DynamicFinderOrder.Builder orderBy(String property) {
            // Finalize selection first.
            builder.setSelection(
                    new DynamicFinderSelection(
                            this.method,
                            this.projection != null ? Optional.of(this.projection) : Optional.empty(),
                            this.property != null ? Optional.of(this.property) : Optional.empty()));
            // Return order builder.
            return new DynamicFinderOrder.Builder(builder).orderBy(property);
        }

        /**
         * Build Helidon dynamic finder query.
         *
         * @return new instance of Helidon dynamic finder query.
         */
        DynamicFinder build() {
            // Finalize selection first.
            builder.setSelection(
                    new DynamicFinderSelection(
                            method,
                            projection != null ? Optional.of(projection) : Optional.empty(),
                            property != null ? Optional.of(property) : Optional.empty()));
            // Return finished AST.
            return builder.build();
        }

        /**
         * Internal: Select dynamic finder query with single result.
         *
         * @return builder with single result query
         */
        DynamicFinderSelection.Builder get() {
            method = Method.GET;
            return this;
        }

        /**
         * Internal: Select dynamic finder query with multiple results.
         *
         * @return builder with multiple results query
         */
        DynamicFinderSelection.Builder find() {
            method = Method.FIND;
            return this;
        }

    }

}
