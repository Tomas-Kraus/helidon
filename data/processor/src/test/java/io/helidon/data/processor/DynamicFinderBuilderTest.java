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

import io.helidon.data.processor.DynamicFinder;
import io.helidon.data.processor.DynamicFinderCriteria;
import io.helidon.data.processor.DynamicFinderOrder;
import io.helidon.data.processor.DynamicFinderSelection;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DynamicFinderBuilderTest {

    // Helper method to evaluate use-cae with result and projection selected.
    public static void evaluateResultOnly(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod
    ) {
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        assertThat(query.criteria().isEmpty(), is(true));
    }

    // Minimal builder use-cae with single result query:
    // - only get method is selected
    // - no projection is set
    // - no criteria is set
    @Test
    @Order(1)
    public void testGetSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .build();
        evaluateResultOnly(query, DynamicFinderSelection.Method.GET);
    }

    // Minimal builder use-cae with multiple results query:
    // - only find method is selected
    // - no projection is set
    // - no criteria is set
    @Test
    @Order(1)
    public void testFindSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .build();
        evaluateResultOnly(query, DynamicFinderSelection.Method.FIND);
    }

    // Helper method to evaluate use-cae with result and projection selected.
    public static void evaluateResultAndProjectionOnly(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            final DynamicFinderSelection.Projection.Method projectionMethod
            ) {
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isPresent(), is(true));
        assertThat(query.selection().projection().get().method(), is(projectionMethod));
        assertThat(query.selection().projection().get().parameter().isEmpty(), is(true));
        assertThat(query.criteria().isEmpty(), is(true));
    }

    // Builder use-cae with single result query and Count projection:
    // - get method is selected
    // - projection is set to COUNT which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testGetCountSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .count()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.COUNT);
    }

    // Builder use-cae with multiple results query and Count projection:
    // - find method is selected
    // - projection is set to COUNT which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testFindCountSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .count()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.COUNT);
    }

    // Builder use-cae with single result query and CountDistinct projection:
    // - get method is selected
    // - projection is set to COUNT_DISTINCT which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testGetCountDistinctSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .countDistinct()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.COUNT_DISTINCT);
    }

    // Builder use-cae with multiple results query and CountDistinct projection:
    // - find method is selected
    // - projection is set to COUNT_DISTINCT which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testFindCountDistinctSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .countDistinct()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.COUNT_DISTINCT);
    }

    // Builder use-cae with single result query and Distinct projection:
    // - get method is selected
    // - projection is set to DISTINCT which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testGetDistinctSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .distinct()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.DISTINCT);
    }

    // Builder use-cae with multiple results query and Distinct projection:
    // - find method is selected
    // - projection is set to DISTINCT which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testFindDistinctSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .distinct()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.DISTINCT);
    }

    // Builder use-cae with single result query and Max projection:
    // - get method is selected
    // - projection is set to MAX which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testGetMaxSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .max()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.MAX);
    }

    // Builder use-cae with multiple results query and Max projection:
    // - find method is selected
    // - projection is set to MAX which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testFindMaxSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .max()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.MAX);
    }

    // Builder use-cae with single result query and Min projection:
    // - get method is selected
    // - projection is set to MIN which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testGetMinSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .min()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.MIN);
    }

    // Builder use-cae with multiple results query and Min projection:
    // - find method is selected
    // - projection is set to MIN which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testFindMinSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .min()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.MIN);
    }

    // Builder use-cae with single result query and Sum projection:
    // - get method is selected
    // - projection is set to SUM which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testGetSumSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .sum()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.SUM);
    }

    // Builder use-cae with multiple results query and Sum projection:
    // - find method is selected
    // - projection is set to SUM which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testFindSumSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .sum()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.SUM);
    }

    // Builder use-cae with single result query and Avg projection:
    // - get method is selected
    // - projection is set to AVG which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testGetAvgSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .avg()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.AVG);
    }

    // Builder use-cae with multiple results query and Avg projection:
    // - find method is selected
    // - projection is set to AVG which has no parameters in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testFindAvgSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .avg()
                .build();
        evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.AVG);
    }

    // Helper method to evaluate use-cae with result and projection Top(Integer) selected.
    public static void evaluateResultAndProjectionTopOnly(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            final int projectionParameter
    ) {
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isPresent(), is(true));
        assertThat(query.selection().projection().get().method(), is(DynamicFinderSelection.Projection.Method.TOP));
        Optional<Integer> parameter = query.selection().projection().get().parameter();
        assertThat(parameter.isPresent(), is(true));
        assertThat(parameter.get(), is(projectionParameter));
        assertThat(query.criteria().isEmpty(), is(true));
    }

    // Builder use-cae with single result query and Top(Integer) projection:
    // - get method is selected
    // - projection is set to TOP which has integer in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testGetTopSelection() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .top(10)
                .build();
        evaluateResultAndProjectionTopOnly(
                query, DynamicFinderSelection.Method.GET, 10);
    }

    // Builder use-cae with multiple results query and Top(Integer) projection:
    // - find method is selected
    // - projection is set to TOP which has integer in method name
    // - no criteria is set
    @Test
    @Order(2)
    public void testFindTopelSection() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .top(10)
                .build();
        evaluateResultAndProjectionTopOnly(
                query, DynamicFinderSelection.Method.FIND, 10);
    }

    // Helper method to evaluate use-cae with result and property only criteria.
    public static void evaluateResultBy(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            final String parameter,
            final String parameterValue
    ) {
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        assertThat(query.criteria().isPresent(), is(true));
        assertThat(query.criteria().get().first(), is(notNullValue()));
        assertThat(query.criteria().get().first().property(), is(parameter));
        assertThat(query.criteria().get().first().not(), is(false));
        assertThat(
                query.criteria().get().first().condition().operator(),
                is(DynamicFinderCriteria.Expression.Condition.Operator.EQUALS));
        assertThat(query.criteria().get().first().condition().values().get(0), is(parameterValue));
        assertThat(query.criteria().get().next().isEmpty(), is(true));
    }

    // Builder use-cae with single result query and property only criteria:
    // - get method is selected
    // - no projection is set
    // - criteria contains first expression only
    //   - not set to false
    //   - no condition is set
    @Test
    @Order(3)
    public void testGetBy() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name", "nameValue")
                .build();
        evaluateResultBy(query, DynamicFinderSelection.Method.GET, "name", "nameValue");
    }

    // Builder use-cae with multiple results query and property only criteria:
    // - find method is selected
    // - no projection is set
    // - criteria contains first expression only
    //   - not set to false
    //   - no condition is set
    @Test
    @Order(3)
    public void testFindBy() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name", "nameValue")
                .build();
        evaluateResultBy(query, DynamicFinderSelection.Method.FIND, "name", "nameValue");
    }

    // Rest of criteria builder tests will contain simple single result query with no projection

    // Helper method to evaluate use-cae criteria with single condition with single value.
    public static void evaluateResultByCondition(
            final DynamicFinder query,
            String parameter,
            final boolean not,
            final DynamicFinderCriteria.Expression.Condition.Operator conditionOperator,
            final String conditionValue
    ) {
        assertThat(query.criteria().isPresent(), is(true));
        assertThat(query.criteria().get().first(), is(notNullValue()));
        assertThat(query.criteria().get().first().property(), is(parameter));
        assertThat(query.criteria().get().first().not(), is(not));
        assertThat(query.criteria().get().first().condition().operator(), is(conditionOperator));
        List<String> values = query.criteria().get().first().condition().values();
        assertThat(values.size(), is(1));
        assertThat(values.get(0), is(conditionValue));
        assertThat(query.criteria().get().next().isEmpty(), is(true));
    }

    // Builder use-cae with After condition
    @Test
    @Order(4)
    public void testGetByAfter() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .after("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.AFTER, "nameValue");
    }

    // Builder use-cae with negated After condition
    @Test
    @Order(4)
    public void testGetByNotAfter() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .after("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.AFTER, "nameValue");
    }

    // Builder use-cae with Before condition
    @Test
    @Order(4)
    public void testGetByBefore() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .before("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.BEFORE, "nameValue");
    }

    // Builder use-cae with negated Before condition
    @Test
    @Order(4)
    public void testGetByNotBefore() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .before("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.BEFORE, "nameValue");
    }

    // Builder use-cae with Contains condition
    @Test
    @Order(4)
    public void testGetByContains() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .contains("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.CONTAINS, "nameValue");
    }

    // Builder use-cae with negated Contains condition
    @Test
    @Order(4)
    public void testGetByNotContains() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .contains("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.CONTAINS, "nameValue");
    }

    // Builder use-cae with Starts condition
    @Test
    @Order(4)
    public void testGetByStarts() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .starts("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.STARTS, "nameValue");
    }

    // Builder use-cae with negated Starts condition
    @Test
    @Order(4)
    public void testGetByNotStarts() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .starts("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.STARTS, "nameValue");
    }

    // Builder use-cae with Ends condition
    @Test
    @Order(4)
    public void testGetByEnds() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .ends("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.ENDS, "nameValue");
    }

    // Builder use-cae with negated Ends condition
    @Test
    @Order(4)
    public void testGetByNotEnds() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .ends("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.ENDS, "nameValue");
    }

    // Builder use-cae with Equals condition
    @Test
    @Order(4)
    public void testGetByEq() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .eq("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.EQUALS, "nameValue");
    }

    // Builder use-cae with negated Equals condition
    @Test
    @Order(4)
    public void testGetByNotEq() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .eq("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.EQUALS, "nameValue");
    }

    // Builder use-cae with GreaterThan condition
    @Test
    @Order(4)
    public void testGetByGt() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .gt("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.GREATER_THAN, "nameValue");
    }

    // Builder use-cae with negated GreaterThan condition
    @Test
    @Order(4)
    public void testGetByNotGt() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .gt("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.GREATER_THAN, "nameValue");
    }
    // Builder use-cae with GreaterThanEquals condition
    @Test
    @Order(4)
    public void testGetByGte() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .gte("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.GREATER_THAN_EQUALS, "nameValue");
    }

    // Builder use-cae with negated GreaterThanEquals condition
    @Test
    @Order(4)
    public void testGetByNotGte() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .gte("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.GREATER_THAN_EQUALS, "nameValue");
    }

    // Builder use-cae with LessThan condition
    @Test
    @Order(4)
    public void testGetByLt() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .lt("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.LESS_THAN, "nameValue");
    }

    // Builder use-cae with negated LessThan condition
    @Test
    @Order(4)
    public void testGetByNotLt() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .lt("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.LESS_THAN, "nameValue");
    }
    // Builder use-cae with LessThanEquals condition
    @Test
    @Order(4)
    public void testGetByLte() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .lte("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.LESS_THAN_EQUALS, "nameValue");
    }

    // Builder use-cae with negated LessThanEquals condition
    @Test
    @Order(4)
    public void testGetByNotLte() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .lte("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.LESS_THAN_EQUALS, "nameValue");
    }

    @Test
    @Order(4)
    public void testGetByLike() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .like("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.LIKE, "nameValue");
    }

    // Builder use-cae with negated Like condition
    @Test
    @Order(4)
    public void testGetByNotLike() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .like("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.LIKE, "nameValue");
    }

    // Builder use-cae with Ilike condition
    @Test
    @Order(4)
    public void testGetByIlike() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .iLike("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.ILIKE, "nameValue");
    }

    // Builder use-cae with negated Ilike condition
    @Test
    @Order(4)
    public void testGetByNotIlike() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .iLike("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.ILIKE, "nameValue");
    }

    // Builder use-cae with In condition
    @Test
    @Order(4)
    public void testGetByIn() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .in("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.IN, "nameValue");
    }

    // Builder use-cae with negated In condition
    @Test
    @Order(4)
    public void testGetByNotIn() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .in("nameValue")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.IN, "nameValue");
    }

    // Helper method to evaluate use-cae criteria with single condition with two values.
    public static void evaluateResultByCondition(
            final DynamicFinder query,
            String parameter,
            final boolean not,
            final DynamicFinderCriteria.Expression.Condition.Operator conditionOperator,
            final String[] conditionValues
    ) {
        assertThat(query.criteria().isPresent(), is(true));
        assertThat(query.criteria().get().first(), is(notNullValue()));
        assertThat(query.criteria().get().first().property(), is(parameter));
        assertThat(query.criteria().get().first().not(), is(not));
        assertThat(query.criteria().get().first().condition().operator(), is(conditionOperator));
        List<String> values = query.criteria().get().first().condition().values();
        assertThat(values.size(), is(2));
        assertThat(values.get(0), is(conditionValues[0]));
        assertThat(values.get(1), is(conditionValues[1]));
        assertThat(query.criteria().get().next().isEmpty(), is(true));
    }

    // Builder use-cae with Between condition
    @Test
    @Order(4)
    public void testGetByBetween() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .between("from", "to")
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.BETWEEN, new String[] {"from", "to"});
    }

    // Builder use-cae with negated Between condition
    @Test
    @Order(4)
    public void testGetByNotBetween() {
        DynamicFinder query = DynamicFinder.builder()
                .find()
                .by("name")
                .not()
                .between("from", "to")
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.BETWEEN, new String[] {"from", "to"});
    }

    // Helper method to evaluate use-cae criteria with single condition with no values.
    public static void evaluateResultByCondition(
            final DynamicFinder query,
            String parameter,
            final boolean not,
            final DynamicFinderCriteria.Expression.Condition.Operator conditionOperator
    ) {
        assertThat(query.criteria().isPresent(), is(true));
        assertThat(query.criteria().get().first(), is(notNullValue()));
        assertThat(query.criteria().get().first().property(), is(parameter));
        assertThat(query.criteria().get().first().not(), is(not));
        assertThat(query.criteria().get().first().condition().operator(), is(conditionOperator));
        List<String> values = query.criteria().get().first().condition().values();
        assertThat(values.size(), is(0));
        assertThat(query.criteria().get().next().isEmpty(), is(true));
    }

    // Builder use-cae with Null condition
    @Test
    @Order(4)
    public void testGetByNull() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .isNull()
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.NULL);
    }

    // Builder use-cae with negated Null condition
    @Test
    @Order(4)
    public void testGetByNotNull() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .isNull()
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.NULL);
    }

    // Builder use-cae with Empty condition
    @Test
    @Order(4)
    public void testGetByEmpty() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .empty()
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.EMPTY);
    }

    // Builder use-cae with negated Empty condition
    @Test
    @Order(4)
    public void testGetByNotEmpty() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .empty()
                .build();
        evaluateResultByCondition(
                query, "name", true,
                DynamicFinderCriteria.Expression.Condition.Operator.EMPTY);
    }

    // Builder use-cae with True condition
    @Test
    @Order(4)
    public void testGetByTrue() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .isTrue()
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.TRUE);
    }

    // Builder use-cae with negated True condition
    // - negated logical value shall be optimized on Expression builder level
    @Test
    @Order(4)
    public void testGetByNotTrue() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .isTrue()
                .build();
        // Not True must be optimized as False
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.FALSE);
    }

    // Builder use-cae with False condition
    @Test
    @Order(4)
    public void testGetByFalse() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .isFalse()
                .build();
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.FALSE);
    }

    // Builder use-cae with negated False condition
    // - negated logical value shall be optimized on Expression builder level
    @Test
    @Order(4)
    public void testGetByNotFalse() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .isFalse()
                .build();
        // Not False must be optimized as True
        evaluateResultByCondition(
                query, "name", false,
                DynamicFinderCriteria.Expression.Condition.Operator.TRUE);
    }

    // Builder use-cae with multiple expressions
    @Test
    @Order(5)
    public void testGetByPropertyAndLikeOrTrue() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .eq("nameValue")
                .and("nick")
                .not()
                .like("nickPattern")
                .or("age")
                .between("fromValue", "toValue")
                .build();
        assertThat(query.selection().method(), is(DynamicFinderSelection.Method.GET));
        assertThat(query.selection().projection().isEmpty(), is(true));
        assertThat(query.criteria().isPresent(), is(true));
        DynamicFinderCriteria criteria = query.criteria().get();
        // 1st expression
        assertThat(criteria.first(), is(notNullValue()));
        assertThat(criteria.first().property(), is("name"));
        assertThat(criteria.first().not(), is(false));
        assertThat(
                criteria.first().condition().operator(),
                is(DynamicFinderCriteria.Expression.Condition.Operator.EQUALS));
        List<String> firstValues = criteria.first().condition().values();
        assertThat(firstValues.size(), is(1));
        assertThat(firstValues.get(0), is("nameValue"));
        // Check that next expressions exist.
        assertThat(criteria.next().size(), is(2));
        // 2nd expression
        DynamicFinderCriteria.Expression expression2 = criteria.next().get(0);
        assertThat(expression2, is(notNullValue()));
        assertThat(expression2.property(), is("nick"));
        assertThat(expression2.not(), is(true));
        assertThat(
                expression2.condition().operator(),
                is(DynamicFinderCriteria.Expression.Condition.Operator.LIKE));
        List<String> secondValues = expression2.condition().values();
        assertThat(secondValues.size(), is(1));
        assertThat(secondValues.get(0), is("nickPattern"));
        // 3rd expression
        DynamicFinderCriteria.Expression expression3 = criteria.next().get(1);
        assertThat(expression3, is(notNullValue()));
        assertThat(expression3.property(), is("age"));
        assertThat(expression3.not(), is(false));
        assertThat(
                expression3.condition().operator(),
                is(DynamicFinderCriteria.Expression.Condition.Operator.BETWEEN));
        List<String> thirdValues = expression3.condition().values();
        assertThat(thirdValues.size(), is(2));
        assertThat(thirdValues.get(0), is("fromValue"));
        assertThat(thirdValues.get(1), is("toValue"));
    }

    // Helper method to evaluate use-cae with simple orderBy
    public void evaluateResultGetOrderByProperty(
            final DynamicFinder query,
            final String criteriaProperty,
            final String criteriaValue,
            final DynamicFinderOrder.Order.Method orderMethod,
            final String orderProperty
    ) {
        // Selection
        assertThat(query.selection().method(), is(DynamicFinderSelection.Method.GET));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria
        assertThat(query.criteria().isPresent(), is(true));
        DynamicFinderCriteria criteria = query.criteria().get();
        assertThat(criteria.first(), is(notNullValue()));
        assertThat(criteria.first().property(), is(criteriaProperty));
        assertThat(criteria.first().not(), is(false));
        assertThat(
                criteria.first().condition().operator(),
                is(DynamicFinderCriteria.Expression.Condition.Operator.EQUALS));
        assertThat(criteria.first().condition().values().get(0), is(criteriaValue));
        // Order
        assertThat(query.order().isPresent(), is(true));
        assertThat(query.order().get().orders().size(), is(1));
        DynamicFinderOrder.Order order = query.order().get().orders().get(0);
        assertThat(order.method(), is(orderMethod));
        assertThat(order.property(), is(orderProperty));
    }

    // Builder use-cae with simple orderBy
    @Test
    @Order(6)
    public void testGetOrderByTime() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name", "nameValue")
                .orderBy("time")
                .build();
        evaluateResultGetOrderByProperty(
                query, "name", "nameValue",
                DynamicFinderOrder.Order.Method.ASC, "time");
    }

    // Builder use-cae with simple orderBy with explicit ascending order
    @Test
    @Order(6)
    public void testGetOrderByTimeAsc() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name", "nameValue")
                .orderBy("time")
                .asc()
                .build();
        evaluateResultGetOrderByProperty(
                query, "name", "nameValue",
                DynamicFinderOrder.Order.Method.ASC, "time");
    }

    // Builder use-cae with simple orderBy with explicit descending order
    @Test
    @Order(6)
    public void testGetOrderByTimeDesc() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name", "nameValue")
                .orderBy("time")
                .desc()
                .build();
        evaluateResultGetOrderByProperty(
                query, "name", "nameValue",
                DynamicFinderOrder.Order.Method.DESC, "time");
    }

    // Builder use-cae with orderBy with multiple orderings
    @Test
    @Order(6)
    public void testGetOrderByTimeDescAndName() {
        DynamicFinder query = DynamicFinder.builder()
                .get()
                .orderBy("time")
                .desc()
                .and("name")
                .build();
        // Selection - minimal
        assertThat(query.selection().method(), is(DynamicFinderSelection.Method.GET));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria - none
        assertThat(query.criteria().isEmpty(), is(true));
        // Order
        assertThat(query.order().isPresent(), is(true));
        assertThat(query.order().get().orders().size(), is(2));
        DynamicFinderOrder.Order order1 = query.order().get().orders().get(0);
        assertThat(order1.method(), is(DynamicFinderOrder.Order.Method.DESC));
        assertThat(order1.property(), is("time"));
        DynamicFinderOrder.Order order2 = query.order().get().orders().get(1);
        assertThat(order2.method(), is(DynamicFinderOrder.Order.Method.ASC));
        assertThat(order2.property(), is("name"));
    }

}
