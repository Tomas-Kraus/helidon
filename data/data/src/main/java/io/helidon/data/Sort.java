/*
 * Copyright (c) 2022, 2023 Oracle and/or its affiliates.
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
package io.helidon.data;

import java.util.List;
import java.util.Objects;

/**
 * Sorted objects.
 * Instances are immutable. Any operation that mutates this instance returns a new one.
 */
public interface Sort {

    /**
     * Objects sorted by the provided property name.
     *
     * @param propertyName the property name to order by
     * @return new sorted objects instance
     */
    Sort order(String propertyName);

    /**
     * Adds the provided ordering.
     *
     * @param order the ordering to add
     * @return new sorted objects instance
     */
    Sort order(Sort.Order order);

    /**
     * Objects sorted by the provided property name and direction.
     *
     * @param propertyName the property name to order by
     * @param direction the direction of the ordering
     * @return new sorted objects instance
     */
    Sort order(String propertyName, Order.Direction direction);

    /**
     * Whether sorting is applied.
     *
     * @return Value of {@code true} when sorting is applied or {@code false} otherwise
     */
    boolean sorted();

    /**
     * The ordering definition of this sorted objects instance
     *
     * @return the ordering definition
     */
    List<Order> orderBy();

    /**
     * Sorted objects ordering.
     */
    class Order {
        private final String property;
        private final Direction direction;
        private final boolean ignoreCase;

        private Order(String property, Direction direction, boolean ignoreCase) {
            Objects.requireNonNull(direction, "The direction is null");
            Objects.requireNonNull(property, "The property is null");
            this.direction = direction;
            this.property = property;
            this.ignoreCase = ignoreCase;
        }

        /**
         * Get the property name to order by.
         *
         * @return the property name to order by
         */
        public String property() {
            return property;
        }

        /**
         * Get the case sensitivity of the sorting.
         *
         * @return Value of {@code true} when case is being ignored or {@code false} otherwise
         */
        public boolean ignoreCase() {
            return ignoreCase;
        }

        /**
         * Get the direction of the ordering.
         *
         * @return the direction of the ordering
         */
        public Direction direction() {
            return direction;
        }

        /**
         * Whether the ordering is ascending.
         *
         * @return Value of {@code true} when ordering is ascending or {@code false} otherwise
         */
        public boolean ascending() {
            return direction == Direction.ASC;
        }

        /**
         * Whether the ordering is descending.
         *
         * @return Value of {@code true} when ordering is descending or {@code false} otherwise
         */
        public boolean descending() {
            return direction == Direction.DESC;
        }

        @Override
        public boolean equals(Object order) {
            if (this == order) {
                return true;
            }
            if (order == null || getClass() != order.getClass()) {
                return false;
            }
            return direction == ((Order) order).direction
                    && ignoreCase == ((Order) order).ignoreCase
                    && property.equals(((Order) order).property);
        }

        @Override
        public int hashCode() {
            return Objects.hash(property, direction, ignoreCase);
        }

        /**
         * Creates instance of sorted objects ordering in ascending order for the given property name.
         *
         * @param property the property name to order by
         * @param ignoreCase whether case is being ignored
         * @return new instance of sorted objects ordering
         */
        public static Order asc(String property, boolean ignoreCase) {
            return new Order(property, Direction.ASC, ignoreCase);
        }

        /**
         * Creates instance of sorted objects ordering in ascending order for the given property name.
         * Ordering is case-sensitive.
         *
         * @param property the property name to order by
         * @return new instance of sorted objects ordering
         */
        public static Order asc(String property) {
            return asc(property, false);
        }

        /**
         * Creates instance of sorted objects ordering in descending order for the given property name.
         *
         * @param property the property name to order by
         * @param ignoreCase whether case is being ignored
         * @return new instance of sorted objects ordering
         */
        public static Order desc(String property, boolean ignoreCase) {
            return new Order(property, Direction.ASC, ignoreCase);
        }

        /**
         * Creates instance of sorted objects ordering in descending order for the given property name.
         * Ordering is case-sensitive.
         *
         * @param property the property name to order by
         * @return new instance of sorted objects ordering
         */
        public static Order desc(String property) {
            return desc(property, false);
        }

        /**
         * Creates an instance of sorted objects ordering by the given property.
         *
         * @param property the property name to order by
         * @param direction the direction of the ordering
         * @param ignoreCase whether case is being ignored
         * @return new instance of sorted objects ordering
         */
        public static Order create(String property, Order.Direction direction, boolean ignoreCase) {
            return new Order(property, direction, ignoreCase);
        }

        /**
         * Creates an instance of sorted objects ordering by the given property.
         * Direction of the ordering is ascending. Ordering is case-sensitive.
         *
         * @param property the property name to order by
         * @return new instance of sorted objects ordering
         */
        public static Order create(String property) {
            return create(property, Direction.ASC, false);
        }

        /**
         * The direction of the sorted objects ordering.
         */
        public enum Direction {
            /** Ascending direction of the ordering. */
            ASC,
            /** Descending direction of the ordering. */
            DESC
        }

    }

}
