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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.helidon.data.runtime.DynamicFinder;
import io.helidon.data.runtime.DynamicFinderCriteria;

class DynamicFinderCriteriaBuilder {

    // Parent class builder where all parts are put together.
    private final DynamicFinderBuilder builder;
    // Query criteria first expression.
    private DynamicFinderCriteria.Expression first;
    // Query criteria expressions following the first one.
    private final List<DynamicFinderCriteria.Compound.NextExpression> next;
    // Criteria expression builder.
    // Contains nextExpressionBuilder instance after first expression is finished.
    private ConditionBuilder conditionBuilder;
    // Criteria next expression builder.
    private DynamicFinderCriteria.Compound.NextExpression.Builder nextExpressionBuilder;

    // Creqates an instanceof query criteria builder.
    DynamicFinderCriteriaBuilder(DynamicFinderBuilder builder) {
        this.builder = builder;
        this.first = null;
        this.next = new LinkedList<>();
        this.conditionBuilder = null;
        this.nextExpressionBuilder = null;
    }

    /**
     * Build Helidon dynamic finder query criteria.
     *
     * @param property criteria expression property: Entity property name
     */
    DynamicFinderCriteriaBuilder by(String property) {
        conditionBuilder = new ConditionBuilder();
        conditionBuilder.property(property);
        return this;
    }

    /**
     * Build Helidon dynamic finder query criteria.
     * This is a shortcut to add default {@code EQUALS} condition for provided property.
     *
     * @param property criteria expression parameter: Entity property name
     * @param conditionValue condition property value: used in {@code setParameter(property, conditionValue)} call.
     */
    DynamicFinderCriteriaBuilder by(String property, String conditionValue) {
        conditionBuilder = new ConditionBuilder();
        conditionBuilder.property(property)
                .operator(DynamicFinderCriteria.Condition.Operator.EQUALS)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder not() {
        conditionBuilder.not();
        return this;
    }

    DynamicFinderCriteriaBuilder after(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.AFTER)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder before(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.BEFORE)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder contains(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.CONTAINS)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder starts(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.STARTS)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder ends(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.ENDS)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder eq(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.EQUALS)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder gt(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.GREATER_THAN)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder gte(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.GREATER_THAN_EQUALS)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder lt(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.LESS_THAN)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder lte(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.LESS_THAN_EQUALS)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder like(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.LIKE)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder iLike(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.ILIKE)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder in(String conditionValue) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.IN)
                .argument(Object.class, conditionValue);
        return this;
    }

    DynamicFinderCriteriaBuilder between(final String conditionFrom, final String conditionTo) {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.BETWEEN)
                .argument(Object.class, conditionFrom)
                .argument(Object.class, conditionTo);
        return this;
    }

    DynamicFinderCriteriaBuilder isNull() {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.NULL);
        return this;
    }

    DynamicFinderCriteriaBuilder empty() {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.EMPTY);
        return this;
    }

    DynamicFinderCriteriaBuilder isTrue() {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.TRUE);
        return this;
    }

    DynamicFinderCriteriaBuilder isFalse() {
        conditionBuilder.operator(DynamicFinderCriteria.Condition.Operator.FALSE);
        return this;
    }

    DynamicFinderCriteriaBuilder and(String property) {
        finishCurrentBuilder();
        conditionBuilder = conditionBuilder()
                .property(property);
        nextExpressionBuilder = expressionBuilder()
                .operator(DynamicFinderCriteria.Compound.NextExpression.Operator.AND);
        return this;
    }

    DynamicFinderCriteriaBuilder or(String property) {
        finishCurrentBuilder();
        conditionBuilder = conditionBuilder()
                .property(property);
        nextExpressionBuilder = expressionBuilder()
                .operator(DynamicFinderCriteria.Compound.NextExpression.Operator.OR);
        return this;
    }

    DynamicFinderCriteriaBuilder and(String property, String conditionValue) {
        finishCurrentBuilder();
        conditionBuilder = conditionBuilder()
                .property(property)
                .operator(DynamicFinderCriteria.Condition.Operator.EQUALS)
                .argument(Object.class, conditionValue);
        nextExpressionBuilder = expressionBuilder()
                .operator(DynamicFinderCriteria.Compound.NextExpression.Operator.AND);
        return this;
    }

    DynamicFinderCriteriaBuilder or(String property, String conditionValue) {
        finishCurrentBuilder();
        conditionBuilder = conditionBuilder()
                .property(property)
                .operator(DynamicFinderCriteria.Condition.Operator.EQUALS)
                .argument(Object.class, conditionValue);
        nextExpressionBuilder = expressionBuilder()
                .operator(DynamicFinderCriteria.Compound.NextExpression.Operator.OR);
        return this;
    }

    /**
     * Build Helidon dynamic finder query order.
     *
     * @param property criteria expression parameter: Entity property name
     */
    DynamicFinderOrderBuilder orderBy(String property) {
        // Finalize criteria first.
        finishCurrentBuilder();
        conditionBuilder = null;
        nextExpressionBuilder = null;
        builder.setCriteria(buildCriteria());
        return new DynamicFinderOrderBuilder(builder).orderBy(property);
    }

    DynamicFinder build() {
        // Finalize criteria first.
        finishCurrentBuilder();
        conditionBuilder = null;
        nextExpressionBuilder = null;
        builder.setCriteria(buildCriteria());
        // Return finished AST.
        return builder.build();
    }

    private void finishCurrentBuilder() {
        if (first == null) {
            first = conditionBuilder.build();
        } else {
            next.add(nextExpressionBuilder.expression(conditionBuilder.build()).build());
        }
    }

    // Helper method to build proper expression from stored conditions and logical operators.
    private DynamicFinderCriteria buildCriteria() {
        if (first == null) {
            throw new IllegalStateException("No criteria expression condition was set.");
        }
        // Only single condition exists, make it whole expression.
        if (next.isEmpty()) {
            return DynamicFinderCriteria.build(first);
            // Build compound expression when more than one condition exist.
        } else {
            DynamicFinderCriteria.Compound expression = compoundBuilder().first(first).next(next).build();
            return DynamicFinderCriteria.build(expression);
        }
    }

    static ConditionBuilder conditionBuilder() {
        return new ConditionBuilder();
    }
    static DynamicFinderCriteria.Compound.NextExpression.Builder expressionBuilder() {
        return DynamicFinderCriteria.Compound.NextExpression.builder();
    }

    static DynamicFinderCriteria.Compound.Builder compoundBuilder() {
        return DynamicFinderCriteria.Compound.builder();
    }

    private static class ConditionBuilder {

        // Condition property.
        private String property;
        // Condition operator.
        private DynamicFinderCriteria.Condition.Operator operator;
        // Condition values: dynamic finder query method parameters names assigned to condition.
        private List<DynamicFinderCriteria.Condition.Parameter<?>> values;
        // Whether condition is negated.
        private boolean not;

        private ConditionBuilder() {
            this.property = null;
            this.operator = null;
            this.values = new LinkedList<>();
            this.not = false;
        }

        private ConditionBuilder property(String property) {
            Objects.requireNonNull(property, "Condition property shall not be null.");
            this.property = property;
            return this;
        }

        private ConditionBuilder operator(DynamicFinderCriteria.Condition.Operator operator) {
            Objects.requireNonNull(operator, "Condition operator shall not be null.");
            this.operator = operator;
            return this;
        }

        private ConditionBuilder value(Class<Object> valueClass, Object value) {
            values.add(DynamicFinderCriteria.Condition.Parameter.Value.build(valueClass, value));
            return this;
        }

        private ConditionBuilder argument(Class<Object> valueClass, String name) {
            values.add(DynamicFinderCriteria.Condition.Parameter.Argument.build(valueClass, name));
            return this;
        }

        private ConditionBuilder not() {
            this.not = true;
            return this;
        }

        private DynamicFinderCriteria.Condition build() {
            // Optimize logical values on AST building level.
            if (not) {
                switch (operator) {
                // Convert NotTrue to False
                case TRUE:
                    return DynamicFinderCriteria.Condition.build(false, property, DynamicFinderCriteria.Condition.Operator.FALSE, values);
                // Convert NotFalse to True
                case FALSE:
                    return DynamicFinderCriteria.Condition.build(false, property, DynamicFinderCriteria.Condition.Operator.TRUE, values);
                }
            }
            return DynamicFinderCriteria.Condition.build(not, property, operator, values);
        }

    }

}
