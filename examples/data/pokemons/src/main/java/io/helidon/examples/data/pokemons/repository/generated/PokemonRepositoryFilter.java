/*
 * Copyright (c) 2019, 2022 Oracle and/or its affiliates.
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
package io.helidon.examples.data.pokemons.repository.generated;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import io.helidon.data.processor.DynamicFinder;
import io.helidon.data.processor.DynamicFinderCriteria;
import io.helidon.data.processor.DynamicFinderOrder;
import io.helidon.data.repository.RepositoryFilter;

public class PokemonRepositoryFilter implements RepositoryFilter {

    // Parameter id
    private static final String ID = "id";
    // Parameter name
    private static final String NAME = "name";
    // Parameter type
    private static final String TYPE = "type";
    // Parameter type.id
    private static final String TYPE_ID = "type.id";
    // Parameter type.name
    private static final String TYPE_NAME = "type.name";
    private final DynamicFinder query;

    PokemonRepositoryFilter(Optional<Criteria> criteria, Optional<Order> order) {
        if (criteria.isEmpty() && order.isEmpty()) {
            throw new IllegalArgumentException("At least one from criteria and order arguments shall be non empty.");
        }
        // TODO: Build AST
        this.query = null;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for Pokemon entity class filtering rules.
     */
    public static class Builder implements io.helidon.common.Builder<Builder, PokemonRepositoryFilter> {

        private Criteria criteria;
        private Order order;

        private Builder() {
            this.criteria = null;
            this.order = null;
        }

        public Builder criteria(Criteria criteria) {
            this.criteria = criteria;
            return this;
        }

        public Builder order(Order order) {
            this.order = order;
            return this;
        }

        @Override
        public PokemonRepositoryFilter build() {
            return null;
        }
    }

    public static class Criteria implements RepositoryFilter.Criteria {

        // Criteria part of dynamic finder query. This shall be passed to DynamicFinder instance.
        private Optional<DynamicFinderCriteria> criteria;

        // TODO: Build AST in builder instead of keeping condition lists
        private final Optional<List<DynamicFinderCriteria.Expression>> id;
        private final Optional<List<DynamicFinderCriteria.Expression>> name;
        private final Optional<List<DynamicFinderCriteria.Expression>> type;
        // TODO: Build AST from condition lists in builder
        private Criteria(
                Optional<List<DynamicFinderCriteria.Expression>> id,
                Optional<List<DynamicFinderCriteria.Expression>> name,
                Optional<List<DynamicFinderCriteria.Expression>> type
        ) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder for Pokemon entity class filtering criteria.
         * Entity has following atributes:<ul>
         * <li>id</li>
         * <li>name</li>
         * <li>type</li>
         * </ul>
         */
        public static class Builder implements io.helidon.common.Builder<Builder, Criteria> {

             // Expressions joining logical operator
            DynamicFinderCriteria.NextExpression.Operator expressionOperator;
            private final List<DynamicFinderCriteria.Expression> idList;
            private final List<DynamicFinderCriteria.Expression> nameList;
            private final List<DynamicFinderCriteria.Expression> typeList;
            // Negate condition
            private boolean not;

            private Builder() {
                this.idList = new LinkedList<>();
                this.nameList = new LinkedList<>();
                this.typeList = new LinkedList<>();
                this.not = false;
                this.expressionOperator = null;
            }

            // Equals for id
            public Builder id(String valueParam) {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.EQUALS, valueParam);
                return this;
            }

            // Builder methods for numeric types: Equals, After, Before, GreaterThan, GreaterThanEqual, LessThan, LessThanEquals,
            //                                    InList, Between, IsNull

            // After for name
            public Builder idAfter(String valueParam) {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.AFTER, valueParam);
                return this;
            }

            // Before for name
            public Builder idBefore(String valueParam) {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.BEFORE, valueParam);
                return this;
            }

            // GreaterThan for id
            public Builder idGt(String valueParam) {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.GREATER_THAN, valueParam);
                return this;
            }

            // GreaterThanEqual for id
            public Builder idGte(String valueParam) {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.GREATER_THAN_EQUALS, valueParam);
                return this;
            }

            // LessThan for id
            public Builder idLt(String valueParam) {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.LESS_THAN, valueParam);
                return this;
            }

            // LessThanEquals for id
            public Builder idLte(String valueParam) {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.LESS_THAN_EQUALS, valueParam);
                return this;
            }

            // InList for id
            public Builder idIn(String valueParam) {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.IN, valueParam);
                return this;
            }

            // Between for id
            public Builder idBetween(String fromParam, String toParam) {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.BETWEEN, fromParam, toParam);
                return this;
            }

            // IsNull for id
            public Builder idIsNull() {
                add(idList, ID, DynamicFinderCriteria.Expression.Condition.Operator.NULL);
                return this;
            }

            // Equal for name
            public Builder name(String valueParam) {
                add(nameList, NAME, DynamicFinderCriteria.Expression.Condition.Operator.EQUALS, valueParam);
                return this;
            }

            // Builder methods for String type: Equal, Contains, StartsWith, EndsWith, Like, Ilike, InList, Between, IsNull

            // Contains for name
            public Builder nameContains(String valueParam) {
                add(nameList, NAME, DynamicFinderCriteria.Expression.Condition.Operator.CONTAINS, valueParam);
                return this;
            }

            // StartsWith for name
            public Builder nameStartsWith(String valueParam) {
                add(nameList, NAME, DynamicFinderCriteria.Expression.Condition.Operator.STARTS, valueParam);
                return this;
            }

            // EndsWith for name
            public Builder nameEndsWith(String valueParam) {
                add(nameList, NAME, DynamicFinderCriteria.Expression.Condition.Operator.ENDS, valueParam);
                return this;
            }

            // Like for name
            public Builder nameLike(String valueParam) {
                add(nameList, NAME, DynamicFinderCriteria.Expression.Condition.Operator.LIKE, valueParam);
                return this;
            }

            // Ilike for name
            public Builder nameIlike(String valueParam) {
                add(nameList, NAME, DynamicFinderCriteria.Expression.Condition.Operator.ILIKE, valueParam);
                return this;
            }

            // In for name
            public Builder nameIn(String valueParam) {
                add(nameList, NAME, DynamicFinderCriteria.Expression.Condition.Operator.IN, valueParam);
                return this;
            }

            // IsNull for name
            public Builder nameIsNull() {
                add(nameList, NAME, DynamicFinderCriteria.Expression.Condition.Operator.NULL);
                return this;
            }

            // IsNull for type
            public Builder typeIsNull() {
                add(typeList, TYPE, DynamicFinderCriteria.Expression.Condition.Operator.NULL);
                return this;
            }

            // Builder methods for ManyToOne relations: IsNull

            public Builder not() {
                this.not = true;
                return this;
            }

            public Builder or() {
                expressionOperator = DynamicFinderCriteria.NextExpression.Operator.OR;
                return this;
            }

            public Builder and() {
                expressionOperator = DynamicFinderCriteria.NextExpression.Operator.AND;
                return this;
            }

            @Override
            public Criteria build() {
                return new Criteria(
                        // Convert LinkedLists to unmodifiable ArrayLists when not empty.
                        idList.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(idList)),
                        nameList.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(nameList)),
                        typeList.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(typeList))
                );
            }

            // Add next expression to the expressions list.
            private void add(
                    List<DynamicFinderCriteria.Expression> list,
                    String property,
                    DynamicFinderCriteria.Expression.Condition.Operator criteriaOperator,
                    String... valueParam) {
                // 1st expression does not contain joining logical operator
                if (list.isEmpty()) {
                    idList.add(DynamicFinderCriteria.Expression.build(
                            property,
                            not,
                            DynamicFinderCriteria.Expression.Condition.build(criteriaOperator, valueParam)));
                    // 2nd and later expression contains joining logical operator
                } else {
                    idList.add(DynamicFinderCriteria.NextExpression.build(
                            // Default operator is AND
                            expressionOperator != null ? expressionOperator : DynamicFinderCriteria.NextExpression.Operator.AND,
                            property,
                            not,
                            DynamicFinderCriteria.Expression.Condition.build(criteriaOperator, valueParam)));
                }
                // Reset negation and joining logical operator
                this.not = false;
                this.expressionOperator = null;
            }
        }

    }

    public static class Order implements RepositoryFilter.Order {

        // Order part of the Helidon dynamic finder query. This shall be passed to DynamicFinder instance.
        private Optional<DynamicFinderOrder> order;

        private Order(Optional<DynamicFinderOrder> order) {
            this.order = order;
        }

        public static Order.Builder builder() {
            return new Order.Builder();
        }

        /**
         * Builder for Pokemon entity class filtering criteria.
         * Entity has following atributes:<ul>
         * <li>id</li>
         * <li>name</li>
         * <li>type</li>
         * </ul>
         */
        public static class Builder implements io.helidon.common.Builder<Order.Builder, Order> {

            private final List<DynamicFinderOrder.Order> orders;

            private Builder() {
                orders = new LinkedList<>();
            }
            public Builder idAsc() {
                orders.add(DynamicFinderOrder.Order.build(DynamicFinderOrder.Order.Method.ASC, ID));
                return this;
            }

            public Builder idDesc() {
                orders.add(DynamicFinderOrder.Order.build(DynamicFinderOrder.Order.Method.DESC, ID));
                return this;
            }

            public Builder nameAsc() {
                orders.add(DynamicFinderOrder.Order.build(DynamicFinderOrder.Order.Method.ASC, NAME));
                return this;
            }

            public Builder nameDesc() {
                orders.add(DynamicFinderOrder.Order.build(DynamicFinderOrder.Order.Method.DESC, NAME));
                return this;
            }

            public Builder typeIdAsc() {
                orders.add(DynamicFinderOrder.Order.build(DynamicFinderOrder.Order.Method.ASC, TYPE_ID));
                return this;
            }

            public Builder typeIdDesc() {
                orders.add(DynamicFinderOrder.Order.build(DynamicFinderOrder.Order.Method.DESC, TYPE_ID));
                return this;
            }

            public Builder typeNameAsc() {
                orders.add(DynamicFinderOrder.Order.build(DynamicFinderOrder.Order.Method.ASC, TYPE_NAME));
                return this;
            }

            public Builder typeNameDesc() {
                orders.add(DynamicFinderOrder.Order.build(DynamicFinderOrder.Order.Method.DESC, TYPE_NAME));
                return this;
            }

            @Override
            public Order build() {
                return new Order(
                        orders.isEmpty() ? Optional.empty() : Optional.of(DynamicFinderOrder.build(orders))
                );
            }

        }

    }

}
