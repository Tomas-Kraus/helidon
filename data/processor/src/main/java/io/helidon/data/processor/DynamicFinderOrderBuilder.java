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
import java.util.LinkedList;
import java.util.List;

import io.helidon.data.runtime.DynamicFinder;
import io.helidon.data.runtime.DynamicFinderOrder;

/**
 * Helidon dynamic finder query order builder.
 */
class DynamicFinderOrderBuilder {

    private static class OrderBuilder {

        // Entity property name.
        private final String property;
        // Query order method.
        private DynamicFinderOrder.Order.Method method;

        private OrderBuilder(String property) {
            this.property = property;
            this.method = DynamicFinderOrder.Order.Method.ASC;
        }

        private void asc() {
            this.method = DynamicFinderOrder.Order.Method.ASC;
        }

        private void desc() {
            this.method = DynamicFinderOrder.Order.Method.DESC;
        }

        private DynamicFinderOrder.Order build() {
            return DynamicFinderOrder.Order.build(method, property);
        }
    }

    // Parent class builder where all parts are put together.
    private final DynamicFinderBuilder builder;

    // Query order builder.
    DynamicFinderOrderBuilder.OrderBuilder orderBuilder;

    // List of ordering properties with ordering methods.
    private final List<DynamicFinderOrder.Order> orders;

    // Creqates an instanceof query selection builder.
    DynamicFinderOrderBuilder(DynamicFinderBuilder builder) {
        this.builder = builder;
        this.orderBuilder = null;
        orders = new LinkedList<>();
    }

    /**
     * Select ascending order.
     *
     * @return builder with ascending order set for current property
     */
    DynamicFinderOrderBuilder asc() {
        orderBuilder.asc();
        return this;
    }

    /**
     * Select descending order.
     *
     * @return builder with descending order set for current property
     */
    DynamicFinderOrderBuilder desc() {
        orderBuilder.desc();
        return this;
    }

    /**
     * Select descending order.
     *
     * @return builder with descending order set for current property
     */
    DynamicFinderOrderBuilder and(String property) {
        orders.add(orderBuilder.build());
        orderBuilder = new DynamicFinderOrderBuilder.OrderBuilder(property);
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
        builder.setOrder(DynamicFinderOrder.build(new ArrayList<>(orders)));
        // Return finished AST.
        return builder.build();
    }

    /**
     * Build Helidon dynamic finder query order.
     *
     * @param property order parameter: Entity property name
     */
    DynamicFinderOrderBuilder orderBy(String property) {
        orderBuilder = new DynamicFinderOrderBuilder.OrderBuilder(property);
        return this;
    }

}
