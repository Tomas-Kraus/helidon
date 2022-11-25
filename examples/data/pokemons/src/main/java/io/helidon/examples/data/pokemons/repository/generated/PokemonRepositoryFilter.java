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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.helidon.data.processor.DynamicFinder;
import io.helidon.data.processor.DynamicFinderCriteria;
import io.helidon.data.processor.DynamicFinderOrder;
import io.helidon.data.repository.RepositoryFilter;

public class PokemonRepositoryFilter implements RepositoryFilter {

    // Entity name.
    private static final String ENTITY = "Pokemon";
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

    // Entity attributes case-insensitive matching Map.
    private static final Map<String, String> ENTITY_ATTRS = initEntityAttrs();
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

    // Initialize entity attributes case-insensitive matching Map.
    private static Map<String, String> initEntityAttrs() {
        Map<String, String> map = new HashMap<>();
        map.put(ID.toLowerCase(), ID);
        map.put(NAME.toLowerCase(), NAME);
        map.put(TYPE.toLowerCase(), TYPE);
        map.put(TYPE_ID.toLowerCase(), TYPE_ID);
        map.put(TYPE_NAME.toLowerCase(), TYPE_NAME);
        return map;
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

        private final Optional<DynamicFinderCriteria> criteria;

        private Criteria(Optional<DynamicFinderCriteria> criteria) {
            this.criteria = criteria;
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

            private final List<DynamicFinderCriteria.Condition> idList;
            private final List<DynamicFinderCriteria.Condition> nameList;
            private final List<DynamicFinderCriteria.Condition> typeNameList;

            private Builder() {
                this.idList = new LinkedList<>();
                this.nameList = new LinkedList<>();
                this.typeNameList = new LinkedList<>();
            }

            // Equal for id
            public Builder id(int id) {
                add(idList, NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, Integer.class, id);
                return this;
            }

            // Equal for ids
            public Builder id(Iterable<Integer> ids) {
                ids.forEach(id -> add(idList, NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, Integer.class, id));
                return this;
            }

            // Equal for name
            public Builder name(String name) {
                add(nameList, NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, String.class, name);
                return this;
            }

            // Equal for names
            public Builder name(Iterable<String> names) {
                names.forEach(name -> add(nameList, NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, String.class, name));
                return this;
            }

            // Equal for type.name
            public Builder typeName(String typeName) {
                add(typeNameList, TYPE_NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, String.class, typeName);
                return this;
            }

            // Equal for type.name
            public Builder typeName(Iterable<String> names) {
                names.forEach(name -> add(typeNameList, TYPE_NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, String.class, name));
                return this;
            }

            @Override
            public Criteria build() {
                DynamicFinderCriteria.Expression idExpr = expressionFromList(idList);
                DynamicFinderCriteria.Expression nameExpr = expressionFromList(nameList);
                DynamicFinderCriteria.Expression typeNameExpr = expressionFromList(typeNameList);
                List<DynamicFinderCriteria.Expression> expressions = List.of(idExpr, nameExpr, typeNameExpr);
                switch (expressions.size()) {
                    case 0:
                        return new Criteria(Optional.empty());
                    case 1:
                        return new Criteria(Optional.of(DynamicFinderCriteria.build(expressions.get(0))));
                    default:
                        return new Criteria(Optional.of(
                                DynamicFinderCriteria.build(
                                        DynamicFinderCriteria.Compound.build(
                                                expressions.get(0),
                                                buildNextExpressionList(
                                                        expressions,
                                                        DynamicFinderCriteria.Compound.NextExpression.Operator.AND)))));
                }
            }

            // Those static helpers should be moved to data Runtime module or even do DynamicFinder AST classes as factory methods

            // Builds next expression list starting from 2nd item in the list.
            private static List<DynamicFinderCriteria.Compound.NextExpression> buildNextExpressionList(
                    List<? extends DynamicFinderCriteria.Expression> list,
                    DynamicFinderCriteria.Compound.NextExpression.Operator operator
            ) {
                List<DynamicFinderCriteria.Compound.NextExpression> nextList = new ArrayList<>(list.size() - 1);
                boolean first = true;
                for (DynamicFinderCriteria.Expression expression : list) {
                    // Skip 1st item in the list
                    if (first) {
                        first = false;
                    } else {
                        nextList.add(DynamicFinderCriteria.Compound.buildExpression(operator, expression));
                    }
                }
                return nextList;
            }

            // Returns null when provided list is empty.
            private static DynamicFinderCriteria.Expression expressionFromList(List<DynamicFinderCriteria.Condition> list) {
                if (list.isEmpty()) {
                    return null;
                }
                // Only one condition exists, return it as an expression.
                if (list.size() == 1) {
                    return list.get(0);
                // Build compound expression from more than one expression.
                } else {
                    return DynamicFinderCriteria.Compound.build(
                            list.get(0),
                            buildNextExpressionList(list, DynamicFinderCriteria.Compound.NextExpression.Operator.OR));
                }
            }


            // Add next expression to the expressions list.
            private static <T> void add(List<DynamicFinderCriteria.Condition> list,
                             String property,
                             DynamicFinderCriteria.Condition.Operator criteriaOperator,
                             Class<T> valueClass,
                             T... value) {
                int size = value == null ? 0 : value.length;
                @SuppressWarnings("unchecked")
                DynamicFinderCriteria.Condition.Parameter<T>[] values
                        = new DynamicFinderCriteria.Condition.Parameter[size];
                for (int i = 0; i < size; i++) {
                    values[i] = DynamicFinderCriteria.Condition.Parameter.Value.build(valueClass, value[i]);
                }
                list.add(DynamicFinderCriteria.Condition.build(property, criteriaOperator, values));
            }
        }

    }

    public static class Order implements RepositoryFilter.Order {

        // Order part of the Helidon dynamic finder query. This shall be passed to DynamicFinder instance.
        private final Optional<DynamicFinderOrder> order;

        private Order(Optional<DynamicFinderOrder> order) {
            this.order = order;
        }

        public static Order.Builder builder() {
            return new Order.Builder();
        }

        /**
         * Builder for Pokemon entity class filtering ordering.
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

            /**
             * Set ordering rule for provided entity attribute.
             *
             * @param name  name of the entity attribute
             * @param order ordering keyword
             * @return ordering builder
             */
            public Builder order(String name, String order) {
                Objects.requireNonNull(name, "Name of entity attribute is null");
                Objects.requireNonNull(order, "Ordering keyword is null.");
                return order(name, DynamicFinderOrder.Order.Method.parse(order));
            }

            /**
             * Set ordering rule for provided entity attribute.
             *
             * @param name  name of the entity attribute
             * @param order ordering method
             * @return ordering builder
             */
            public Builder order(String name, DynamicFinderOrder.Order.Method order) {
                Objects.requireNonNull(name, "Name of entity attribute is null");
                Objects.requireNonNull(order, "Ordering method is null.");
                // Case-insensitive entity attribute matching.
                String attributeName = ENTITY_ATTRS.get(name);
                if (attributeName == null) {
                    throw new IllegalArgumentException(String.format("Attribute %s was not found in entity %s.", name, ENTITY));
                }
                orders.add(DynamicFinderOrder.Order.build(order, attributeName));
                return this;
            }

            @Override
            public Order build() {
                return new Order(orders.isEmpty() ? Optional.empty() : Optional.of(DynamicFinderOrder.build(orders)));
            }

        }

    }

}
