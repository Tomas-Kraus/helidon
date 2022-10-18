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
package io.helidon.data.builder.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Order part of the Helidon dynamic finder query.
 */
public class DynamicFinderOrder {

    /**
     * Query order.
     */
    public static class Order {

        /**
         * Query ordering methods.
         */
        public enum Method {
            /** Ascending order. Default ordering method. */
            ASC,
            /** Descending order. */
            DESC
        }

        // Query order method.
        private final Order.Method method;
        // Entity property name used to sort query result.
        private final String property;

        private Order(final Order.Method method, final String property) {
            this.method = method;
            this.property = property;
        }

        /**
         * Query order method.
         *
         * @return method of the property ordering
         */
        public Order.Method method() {
            return method;
        }

        /**
         * Entity property name.
         *
         * @return name of the entity property used to sort query result
         */
        public String property() {
            return property;
        }

    }

    // List of ordering properties with ordering methods.
    private final List<Order> orders;

    private DynamicFinderOrder(final List<Order> orders) {
        this.orders = orders;
    }

    /**
     * Ordering properties with ordering methods.
     *
     * @return {@link List} of ordering properties with ordering methods
     */
    public List<Order> orders() {
        return orders;
    }

    /**
     * Helidon dynamic finder query order builder.
     */
    public static class Builder implements BuilderOrder {

        private static class OrderBuilder {

            // Entity property name.
            private final String property;
            // Query order method.
            private Order.Method method;

            private OrderBuilder(final String property) {
                this.property = property;
                this.method = Order.Method.ASC;
            }

            private void asc() {
                this.method = Order.Method.ASC;
            }

            private void desc() {
                this.method = Order.Method.DESC;
            }

            private Order build() {
                return new Order(method, property);
            }
        }

        // Parent class builder where all parts are put together.
        private final DynamicFinder.Builder builder;

        // Query order builder.
        OrderBuilder orderBuilder;

        // List of ordering properties with ordering methods.
        private final List<Order> orders;

        // Creqates an instanceof query selection builder.
        Builder(final DynamicFinder.Builder builder) {
            this.builder = builder;
            this.orderBuilder = null;
            orders = new LinkedList<>();
        }

        /**
         * Select ascending order.
         *
         * @return builder with ascending order set for current property
         */
        public DynamicFinderOrder.BuilderOrder asc() {
            orderBuilder.asc();
            return this;
        }

        /**
         * Select descending order.
         *
         * @return builder with descending order set for current property
         */
        public DynamicFinderOrder.BuilderOrder desc() {
            orderBuilder.desc();
            return this;
        }

        /**
         * Select descending order.
         *
         * @return builder with descending order set for current property
         */
        public DynamicFinderOrder.Builder and(final String property) {
            orders.add(orderBuilder.build());
            orderBuilder = new OrderBuilder(property);
            return this;
        }

        /**
         * Build Helidon dynamic finder query.
         *
         * @return new instance of Helidon dynamic finder query.
         */
        public DynamicFinder build() {
            // Finalize order first.
            orders.add(orderBuilder.build());
            orderBuilder = null;
            builder.setOrder(new DynamicFinderOrder(new ArrayList<>(orders)));
            // Return finished AST.
            return builder.build();
        }

        /**
         * Build Helidon dynamic finder query order.
         *
         * @param property order parameter: Entity property name
         */
        DynamicFinderOrder.Builder orderBy(final String property) {
            orderBuilder = new OrderBuilder(property);
            return this;
        }

    }

    /**
     * Helidon dynamic finder query order builder: token after {@code OrderBy<property>Asc|Desc}.
     * Reduces offered method calls to disable setting ordering method again.
     */
    public interface BuilderOrder {
        DynamicFinderOrder.Builder and(final String property);
        DynamicFinder build();
    }

}
