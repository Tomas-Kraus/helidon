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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class TestHelper {

    // Get criteria expression condition parameter as String.
    static String criteriaConditionValue(DynamicFinderCriteria.Condition condition, int index) {
        DynamicFinderCriteria.Condition.Parameter<?> parameter = condition.values().get(index);
        return switch (parameter.type()) {
            case VALUE -> ((DynamicFinderCriteria.Condition.Parameter.Value<?>) parameter).value().toString();
            case ARGUMENT -> ((DynamicFinderCriteria.Condition.Parameter.Argument<?>) parameter).name();
            default -> null;
        };
    }

    // Collect criteria expression condition parameters as List<String>.
    static List<String> criteriaConditionValues(DynamicFinderCriteria.Condition condition) {
        return condition.values().stream().map(val -> {
            return switch (val.type()) {
                case VALUE -> ((DynamicFinderCriteria.Condition.Parameter.Value<?>) val).value().toString();
                case ARGUMENT -> ((DynamicFinderCriteria.Condition.Parameter.Argument<?>) val).name();
                default -> null;
            };
        }).toList();
    }

    // Helper method to evaluate use-cae with result and property only criteria.
    public static void evaluateResultBy(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            final String parameter,
            final String parameterValue
    ) {
        // Selection check
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // First condition (in single expression criteria)
        DynamicFinderCriteria.Condition first = query.criteria().get().expression().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(parameter));
        assertThat(first.not(), is(false));
        assertThat(
                first.operator(),
                is(DynamicFinderCriteria.Condition.Operator.EQUALS));
        assertThat(criteriaConditionValue(first, 0), is(parameterValue));
    }

    // Helper method to evaluate use-cae criteria with single condition with single value
    public static void evaluateResultByCondition(
            final DynamicFinder query,
            String parameter,
            final boolean not,
            final DynamicFinderCriteria.Condition.Operator conditionOperator,
            final String conditionValue
    ) {
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // First condition (in single expression criteria)
        DynamicFinderCriteria.Condition first = query.criteria().get().expression().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(parameter));
        assertThat(first.not(), is(not));
        assertThat(first.operator(), is(conditionOperator));
        List<String> values = criteriaConditionValues(first);
        assertThat(values.size(), is(1));
        assertThat(values.get(0), is(conditionValue));
    }

    // Helper method to evaluate use-cae criteria with single condition with two values.
    public static void evaluateResultByCondition(
            final DynamicFinder query,
            String parameter,
            final boolean not,
            final DynamicFinderCriteria.Condition.Operator conditionOperator,
            final String[] conditionValues
    ) {
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // First condition (in single expression criteria)
        DynamicFinderCriteria.Condition first = query.criteria().get().expression().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(parameter));
        assertThat(first.not(), is(not));
        assertThat(first.operator(), is(conditionOperator));
        List<String> values = criteriaConditionValues(first);
        assertThat(values.size(), is(2));
        assertThat(values.get(0), is(conditionValues[0]));
        assertThat(values.get(1), is(conditionValues[1]));
    }

    // Helper method to evaluate use-cae criteria with single condition with no values.
    public static void evaluateResultByCondition(
            final DynamicFinder query,
            String parameter,
            final boolean not,
            final DynamicFinderCriteria.Condition.Operator conditionOperator
    ) {
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // First condition (in single expression criteria)
        DynamicFinderCriteria.Condition first = query.criteria().get().expression().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(parameter));
        assertThat(first.not(), is(not));
        assertThat(first.operator(), is(conditionOperator));
        List<String> values = criteriaConditionValues(first);
        assertThat(values.size(), is(0));
    }

    // Helper method to evaluate use-cae with simple orderBy
    public static void evaluateResultGetOrderByProperty(
            final DynamicFinder query,
            final String criteriaProperty,
            final String criteriaValue,
            final DynamicFinderOrder.Order.Method orderMethod,
            final String orderProperty
    ) {
        // Selection check
        assertThat(query.selection().method(), is(DynamicFinderSelection.Method.GET));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // First condition (in single expression criteria)
        DynamicFinderCriteria.Condition first = query.criteria().get().expression().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(criteriaProperty));
        assertThat(first.not(), is(false));
        assertThat(
                first.operator(),
                is(DynamicFinderCriteria.Condition.Operator.EQUALS));
        assertThat(criteriaConditionValue(first, 0), is(criteriaValue));
        // Order
        assertThat(query.order().isPresent(), is(true));
        assertThat(query.order().get().orders().size(), is(1));
        DynamicFinderOrder.Order order = query.order().get().orders().get(0);
        assertThat(order.method(), is(orderMethod));
        assertThat(order.property(), is(orderProperty));
    }

    // Helper method to evaluate use-cae with result and property only criteria.
    static void evaluateResultBy(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            final String parameter1, final String parameterValue1,
            final String parameter2, final String parameterValue2,
            final DynamicFinderCriteria.Compound.NextExpression.Operator logicalOperator
    ) {
        // Selection check
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // At least two conditions in compound expression are expected to exist.
        DynamicFinderCriteria.Compound compound = query.criteria().get().expression().as(DynamicFinderCriteria.Compound.class);
        // First condition
        DynamicFinderCriteria.Condition first = compound.first().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(parameter1));
        assertThat(first.not(), is(false));
        assertThat(
                first.operator(),
                is(DynamicFinderCriteria.Condition.Operator.EQUALS));
        assertThat(criteriaConditionValue(first, 0), is(parameterValue1));
        // 2nd condition
        assertThat(compound.next().size(), is(1));
        DynamicFinderCriteria.Compound.NextExpression nextItem = compound.next().get(0);
        assertThat(nextItem.operator(), is(logicalOperator));
        DynamicFinderCriteria.Condition next = nextItem.as(DynamicFinderCriteria.Condition.class);
        assertThat(next.property(), is(parameter2));
        assertThat(next.not(), is(false));
        assertThat(
                next.operator(),
                is(DynamicFinderCriteria.Condition.Operator.EQUALS));
        assertThat(criteriaConditionValue(next, 0), is(parameterValue2));
    }

    // Helper method to evaluate use-cae with result and property only criteria.
    static void evaluateResultBy(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            DynamicFinderCriteria.Condition.Operator operator1,
            final String parameter1, final String parameterValue1,
            DynamicFinderCriteria.Condition.Operator operator2,
            final String parameter2, final String parameterValue2,
            final DynamicFinderCriteria.Compound.NextExpression.Operator logicalOperator
    ) {
        // Selection check
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // At least two conditions in compound expression are expected to exist.
        DynamicFinderCriteria.Compound compound = query.criteria().get().expression().as(DynamicFinderCriteria.Compound.class);
        // First condition
        DynamicFinderCriteria.Condition first = compound.first().as(DynamicFinderCriteria.Condition.class);
        assertThat(query.criteria().isPresent(), is(true));
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(parameter1));
        assertThat(first.not(), is(false));
        assertThat(
                first.operator(),
                is(operator1));
        assertThat(criteriaConditionValue(first, 0), is(parameterValue1));
        // 2nd condition
        assertThat(compound.next().size(), is(1));
        DynamicFinderCriteria.Compound.NextExpression nextItem = compound.next().get(0);
        assertThat(nextItem.operator(), is(logicalOperator));
        DynamicFinderCriteria.Condition next = nextItem.as(DynamicFinderCriteria.Condition.class);
        assertThat(next.property(), is(parameter2));
        assertThat(next.not(), is(false));
        assertThat(
                next.operator(),
                is(operator2));
        assertThat(criteriaConditionValue(next, 0), is(parameterValue2));
        assertThat(nextItem.operator(), is(logicalOperator));
    }

    // Helper method to evaluate use-cae with result and property only criteria.
    static void evaluateResultBy(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            DynamicFinderCriteria.Condition.Operator operator1,
            final String parameter1, final String parameterValue1,
            DynamicFinderCriteria.Condition.Operator operator2,
            final String parameter2, final String parameterValue2,
            final DynamicFinderCriteria.Compound.NextExpression.Operator logicalOperator1,
            DynamicFinderCriteria.Condition.Operator operator3,
            final String parameter3, final String parameterValue3,
            final DynamicFinderCriteria.Compound.NextExpression.Operator logicalOperator2
    ) {
        // Selection check
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // At least two conditions in compound expression are expected to exist.
        DynamicFinderCriteria.Compound compound = query.criteria().get().expression().as(DynamicFinderCriteria.Compound.class);
        // First condition
        DynamicFinderCriteria.Condition first = compound.first().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(parameter1));
        assertThat(first.not(), is(false));
        assertThat(
                first.operator(),
                is(operator1));
        assertThat(criteriaConditionValue(first, 0), is(parameterValue1));
        // 2nd condition
        assertThat(compound.next().size(), is(2));
        DynamicFinderCriteria.Compound.NextExpression nextItem1 = compound.next().get(0);
        assertThat(nextItem1.operator(), is(logicalOperator1));
        DynamicFinderCriteria.Condition next1 = nextItem1.as(DynamicFinderCriteria.Condition.class);
        assertThat(next1.property(), is(parameter2));
        assertThat(next1.not(), is(false));
        assertThat(
                next1.operator(),
                is(operator2));
        assertThat(criteriaConditionValue(next1, 0), is(parameterValue2));
        // 3rd condition
        DynamicFinderCriteria.Compound.NextExpression nextItem2 = compound.next().get(1);
        assertThat(nextItem2.operator(), is(logicalOperator2));
        DynamicFinderCriteria.Condition next2 = nextItem2.as(DynamicFinderCriteria.Condition.class);
        assertThat(next2.property(), is(parameter3));
        assertThat(next2.not(), is(false));
        assertThat(
                next2.operator(),
                is(operator3));
        if (parameterValue3 == null) {
            assertThat(next2.values().isEmpty(), is(true));
        } else {
            assertThat(criteriaConditionValue(next2, 0), is(parameterValue3));
        }
    }

    static void evaluateResultProjectionBy(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            final DynamicFinderSelection.Projection.Method projectionMethod,
            final String projectionParameter,
            final String parameter,
            final String parameterValue
    ) {
        // Selection check
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isPresent(), is(true));
        assertThat(query.selection().projection().get().method(), is(projectionMethod));
        assertThat(query.selection().projection().get().parameter().isEmpty(), is(true));
        if (projectionParameter == null) {
            assertThat(query.selection().property().isEmpty(), is(true));
        } else {
            assertThat(query.selection().property().isPresent(), is(true));
            assertThat(query.selection().property().get(), is(projectionParameter));
        }
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // First condition (in single expression criteria)
        DynamicFinderCriteria.Condition first = query.criteria().get().expression().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(parameter));
        assertThat(first.not(), is(false));
        assertThat(
                first.operator(),
                is(DynamicFinderCriteria.Condition.Operator.EQUALS));
        assertThat(criteriaConditionValue(first, 0), is(parameterValue));
    }

    // Helper method to evaluate use-cae with simple orderBy
    static void evaluateResultOrderByProperty(
            final DynamicFinder query,
            final DynamicFinderSelection.Method queryMathod,
            final DynamicFinderOrder.Order.Method orderMethod,
            final String orderProperty
    ) {
        // Selection
        assertThat(query.selection().method(), is(queryMathod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria
        assertThat(query.criteria().isPresent(), is(false));
        // Order
        assertThat(query.order().isPresent(), is(true));
        assertThat(query.order().get().orders().size(), is(1));
        DynamicFinderOrder.Order order = query.order().get().orders().get(0);
        assertThat(order.method(), is(orderMethod));
        assertThat(order.property(), is(orderProperty));
    }

    // Helper method to evaluate use-cae with simple orderBy
    static void evaluateResultOrderByProperty(
            final DynamicFinder query,
            final DynamicFinderSelection.Method queryMathod,
            final DynamicFinderSelection.Projection.Method projectionMethod,
            final DynamicFinderOrder.Order.Method orderMethod,
            final String orderProperty
    ) {
        // Selection
        assertThat(query.selection().method(), is(queryMathod));
        assertThat(query.selection().projection().isPresent(), is(true));
        assertThat(query.selection().property().isEmpty(), is(true));
        assertThat(query.selection().projection().get().method(), is(projectionMethod));
        // Criteria
        assertThat(query.criteria().isPresent(), is(false));
        // Order
        assertThat(query.order().isPresent(), is(true));
        assertThat(query.order().get().orders().size(), is(1));
        DynamicFinderOrder.Order order = query.order().get().orders().get(0);
        assertThat(order.method(), is(orderMethod));
        assertThat(order.property(), is(orderProperty));
    }

    // Helper method to evaluate use-cae with simple orderBy
    static void evaluateResultOrderByProperty(
            final DynamicFinder query,
            final DynamicFinderSelection.Method queryMethod,
            final DynamicFinderSelection.Projection.Method projectionMethod,
            final String projectionProperty,
            final String criteriaProperty,
            final String criteriaValue,
            final DynamicFinderCriteria.Condition.Operator criteriaOperator,
            final DynamicFinderOrder.Order.Method orderMethod,
            final String orderProperty
    ) {
        // Selection
        assertThat(query.selection().method(), is(queryMethod));
        if (projectionProperty == null) {
            assertThat(query.selection().property().isPresent(), is(false));
        } else {
            assertThat(query.selection().property().isPresent(), is(true));
            assertThat(query.selection().property().get(), is(projectionProperty));
        }
        if (projectionMethod == null) {
            assertThat(query.selection().projection().isPresent(), is(false));
        } else {
            assertThat(query.selection().projection().isPresent(), is(true));
            assertThat(query.selection().projection().get().method(), is(projectionMethod));
        }
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // First condition (in single expression criteria)
        DynamicFinderCriteria.Condition first = query.criteria().get().expression().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(criteriaProperty));
        assertThat(first.not(), is(false));
        assertThat(first.operator(), is(criteriaOperator));
        assertThat(criteriaConditionValue(first, 0), is(criteriaValue));
        // Order
        assertThat(query.order().isPresent(), is(true));
        assertThat(query.order().get().orders().size(), is(1));
        DynamicFinderOrder.Order order = query.order().get().orders().get(0);
        assertThat(order.method(), is(orderMethod));
        assertThat(order.property(), is(orderProperty));
    }

    // Helper method to evaluate use-cae with simple orderBy
    static void evaluateResultOrderByProperty(
            final DynamicFinder query,
            final DynamicFinderSelection.Method queryMethod,
            final DynamicFinderSelection.Projection.Method projectionMethod,
            final String projectionProperty,
            final String criteriaProperty1,
            final String criteriaValue1,
            final DynamicFinderCriteria.Condition.Operator criteriaOperator1,
            final String criteriaProperty2,
            final String criteriaValue2,
            final DynamicFinderCriteria.Condition.Operator criteriaOperator2,
            final DynamicFinderCriteria.Compound.NextExpression.Operator criteriaJoinOperator,
            final DynamicFinderOrder.Order.Method orderMethod,
            final String orderProperty
    ) {
        // Selection
        assertThat(query.selection().method(), is(queryMethod));
        if (projectionProperty == null) {
            assertThat(query.selection().property().isPresent(), is(false));
        } else {
            assertThat(query.selection().property().isPresent(), is(true));
            assertThat(query.selection().property().get(), is(projectionProperty));
        }
        if (projectionMethod == null) {
            assertThat(query.selection().projection().isPresent(), is(false));
        } else {
            assertThat(query.selection().projection().isPresent(), is(true));
            assertThat(query.selection().projection().get().method(), is(projectionMethod));
        }
        // Criteria check
        assertThat(query.criteria().isPresent(), is(true));
        // At least two conditions in compound expression are expected to exist.
        DynamicFinderCriteria.Compound compound = query.criteria().get().expression().as(DynamicFinderCriteria.Compound.class);
        // First condition
        DynamicFinderCriteria.Condition first = compound.first().as(DynamicFinderCriteria.Condition.class);
        assertThat(first, is(notNullValue()));
        assertThat(first.property(), is(criteriaProperty1));
        assertThat(first.not(), is(false));
        assertThat(first.operator(), is(criteriaOperator1));
        assertThat(criteriaConditionValue(first, 0), is(criteriaValue1));
        // 2nd condition
        assertThat(compound.next().size(), is(1));
        DynamicFinderCriteria.Compound.NextExpression nextItem = compound.next().get(0);
        assertThat(nextItem.operator(), is(criteriaJoinOperator));
        DynamicFinderCriteria.Condition next = nextItem.as(DynamicFinderCriteria.Condition.class);
        assertThat(next.property(), is(criteriaProperty2));
        assertThat(next.not(), is(false));
        assertThat(next.operator(), is(criteriaOperator2));
        assertThat(criteriaConditionValue(next, 0), is(criteriaValue2));
        assertThat(nextItem.operator(), is(criteriaJoinOperator));
        // Order
        assertThat(query.order().isPresent(), is(true));
        assertThat(query.order().get().orders().size(), is(1));
        DynamicFinderOrder.Order order = query.order().get().orders().get(0);
        assertThat(order.method(), is(orderMethod));
        assertThat(order.property(), is(orderProperty));
    }

    // Provides static methods only
    private TestHelper() {
        throw new UnsupportedOperationException("Instances of TestHelperMethods are not allowed");
    }

}
