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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Order part of the Helidon dynamic finder query.
 */
public class DynamicFinderOrder {

    public static final String JOIN_OPERATOR = "And";

    /**
     * Query order.
     */
    public static class Order {

        /**
         * Query ordering methods.
         */
        public enum Method {
            /** Ascending order. Default ordering method. */
            ASC("Asc"),
            /** Descending order. */
            DESC("Desc");

            // Case-insensitive keywords searching map
            public static final Map<String, Method> IKEYWORDS = initIKeywords();

            /** Query ordering methods enumeration length. */
            public static final int LENGTH = values().length;

            // Initialize case-insensitive keywords searching map.
            private static Map<String, Method> initIKeywords() {
                Map<String, Method> map = new HashMap<>(6);
                map.put(ASC.keyword.toLowerCase(), ASC);
                map.put("Ascend".toLowerCase(), ASC);
                map.put("Ascending".toLowerCase(), ASC);
                map.put(DESC.keyword.toLowerCase(), DESC);
                map.put("Descend".toLowerCase(), DESC);
                map.put("Descending".toLowerCase(), DESC);
                return map;
            }

            /**
             * Get query ordering method matching provided keyword.
             * Keyword matching is case-insensitive.
             *
             * @param kw keyword to search for
             * @return matching query ordering method or empty value when no matching keyword was found
             */
            public static Method parse(String kw) {
                Method method = IKEYWORDS.get(kw);
                if (method == null) {
                    throw new IllegalArgumentException(String.format("Unknown ordering keyword %s.", kw));
                }
                return method;
            }

            // Query ordering methods keyword
            private final String keyword;

            // Creates an instance of query ordering method.
            Method(String keyword) {
                this.keyword = keyword;
            }

            String keyword() {
                return keyword;
            }

        }

        public static Order build(Order.Method method, String property) {
            return new Order(method, property);
        }

        // Query order method.
        private final Order.Method method;
        // Entity property name used to sort query result.
        private final String property;

        private Order(Order.Method method, String property) {
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

    public static DynamicFinderOrder build(List<Order> orders) {
        return new DynamicFinderOrder(orders);
    }

    // List of ordering properties with ordering methods.
    private final List<Order> orders;

    private DynamicFinderOrder(List<Order> orders) {
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
    static class Builder {

        private static class OrderBuilder {

            // Entity property name.
            private final String property;
            // Query order method.
            private Order.Method method;

            private OrderBuilder(String property) {
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
        Builder(DynamicFinder.Builder builder) {
            this.builder = builder;
            this.orderBuilder = null;
            orders = new LinkedList<>();
        }

        /**
         * Select ascending order.
         *
         * @return builder with ascending order set for current property
         */
        DynamicFinderOrder.Builder asc() {
            orderBuilder.asc();
            return this;
        }

        /**
         * Select descending order.
         *
         * @return builder with descending order set for current property
         */
        DynamicFinderOrder.Builder desc() {
            orderBuilder.desc();
            return this;
        }

        /**
         * Select descending order.
         *
         * @return builder with descending order set for current property
         */
        DynamicFinderOrder.Builder and(String property) {
            orders.add(orderBuilder.build());
            orderBuilder = new OrderBuilder(property);
            return this;
        }

        /**
         * Build Helidon dynamic finder query.
         *
         * @return new instance of Helidon dynamic finder query.
         */
        DynamicFinder build() {
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
        DynamicFinderOrder.Builder orderBy(String property) {
            orderBuilder = new OrderBuilder(property);
            return this;
        }

    }

}
