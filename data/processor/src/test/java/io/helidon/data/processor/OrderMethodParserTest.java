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

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrderMethodParserTest {

    private static final Logger LOGGER = Logger.getLogger(OrderMethodParserTest.class.getName());

    // Test all possible ways to traverse from selection to order part of the query.


    // Test getOrderByName method name ("OrderBy" starts from root node of the selection projection parser)
    @Test
    public void testGetOrderByName() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getOrderByName", arguments);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET, DynamicFinderOrder.Order.Method.ASC, "name");
    }

    // Test findOrderByAge method name ("OrderBy" starts from root node of the selection projection parser)
    @Test
    public void testFindOrderByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findOrderByAge", arguments);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderOrder.Order.Method.ASC, "age");
    }

    // Test getCountOrderByName method name ("OrderBy" starts from root node of the selection properties parser)
    @Test
    public void testGetCountOrderByName() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getCountOrderByName", arguments);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.COUNT,
                DynamicFinderOrder.Order.Method.ASC, "name");
    }

    // Test findDistinctOrderByAge method name ("OrderBy" starts from root node of the selection properties parser)
    @Test
    public void testFindDistinctOrderByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findDistinctOrderByAge", arguments);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.DISTINCT,
                DynamicFinderOrder.Order.Method.ASC, "age");
    }

    // Helper method to evaluate use-cae with simple orderBy
    public void evaluateResultOrderByProperty(
            final DynamicFinder query,
            final DynamicFinderSelection.Method queryMathod,
            final DynamicFinderSelection.Projection.Method projectionMethod,
            final String projectionProperty,
            final DynamicFinderOrder.Order.Method orderMethod,
            final String orderProperty
    ) {
        // Selection
        assertThat(query.selection().method(), is(queryMathod));
        assertThat(query.selection().projection().isPresent(), is(true));
        assertThat(query.selection().property().isPresent(), is(true));
        assertThat(query.selection().property().get(), is(projectionProperty));
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

    // Test getCountNameOrderByAge method name ("OrderBy" starts from final node of the selection properties parser)
    @Test
    public void testGetCountNameOrderByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getCountNameOrderByAge", arguments);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.COUNT,
                "name", DynamicFinderOrder.Order.Method.ASC, "age");
    }

    // Test findMaxAgeOrderByName method name ("OrderBy" starts from final node of the selection properties parser)
    @Test
    public void testFindMaxAgeOrderByName() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findMaxAgeOrderByName", arguments);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.MAX,
                "age", DynamicFinderOrder.Order.Method.ASC, "name");
    }

    // Test all possible ways to traverse from criteria to order part of the query.

    // Test getByNameOrderByAge method name ("OrderBy" starts from final node of the criteria properties parser)
    @Test
    public void testGetByNameOrderByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByNameOrderByAge", arguments);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET, null,
                null, "name", "nameValue", DynamicFinderCriteria.Condition.Operator.EQUALS,
                DynamicFinderOrder.Order.Method.ASC, "age"
        );
    }

    // Test getByNameContainsOrderByAge method name ("OrderBy" starts from final node of the criteria operator parser)
    @Test
    public void testGetByNameContainsOrderByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByNameContainsOrderByAge", arguments);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET, null,
                null, "name", "nameValue", DynamicFinderCriteria.Condition.Operator.CONTAINS,
                DynamicFinderOrder.Order.Method.ASC, "age"
        );
    }

    // Test getByNameOrAgeOrderByAge method name ("OrderBy" starts from final node of the 2nd criteria properties parser)
    @Test
    public void testGetByNameOrAgeOrderByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue", "ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByNameOrAgeOrderByAge", arguments);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET, null, null,
                "name", "nameValue", DynamicFinderCriteria.Condition.Operator.EQUALS,
                "age", "ageValue", DynamicFinderCriteria.Condition.Operator.EQUALS,
                DynamicFinderCriteria.Compound.NextExpression.Operator.OR,
                DynamicFinderOrder.Order.Method.ASC, "age"
        );
    }

    // Test getByNameOrAgeLessThanOrderByAge method name ("OrderBy" starts from final node of the 2nd criteria operator parser)
    @Test
    public void testGetByNameOrAgeLessThanOrderByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue", "ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByNameOrAgeLessThanOrderByAge", arguments);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET, null, null,
                "name", "nameValue", DynamicFinderCriteria.Condition.Operator.EQUALS,
                "age", "ageValue", DynamicFinderCriteria.Condition.Operator.LESS_THAN,
                DynamicFinderCriteria.Compound.NextExpression.Operator.OR,
                DynamicFinderOrder.Order.Method.ASC, "age"
        );
    }

    // Test All orderings

    // Test getOrderByNameAsc
    @Test
    public void testGetOrderByNameAsc() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByNameAsc", Collections.EMPTY_LIST);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET, DynamicFinderOrder.Order.Method.ASC, "name");
    }

    // Test getOrderByNameDesc
    @Test
    public void testGetOrderByNameDesc() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByNameDesc", Collections.EMPTY_LIST);
        TestHelper.evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET, DynamicFinderOrder.Order.Method.DESC, "name");
    }

    // Helper method to evaluate use-cae with simple orderBy
    public void evaluateResultOrderByProperty(
            final DynamicFinder query,
            final DynamicFinderSelection.Method queryMathod,
            final DynamicFinderOrder.Order.Method orderMethod1,
            final String orderProperty1,
            final DynamicFinderOrder.Order.Method orderMethod2,
            final String orderProperty2
    ) {
        // Selection
        assertThat(query.selection().method(), is(queryMathod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria
        assertThat(query.criteria().isPresent(), is(false));
        // Order
        assertThat(query.order().isPresent(), is(true));
        assertThat(query.order().get().orders().size(), is(2));
        DynamicFinderOrder.Order order = query.order().get().orders().get(0);
        assertThat(order.method(), is(orderMethod1));
        assertThat(order.property(), is(orderProperty1));
        order = query.order().get().orders().get(1);
        assertThat(order.method(), is(orderMethod2));
        assertThat(order.property(), is(orderProperty2));
    }

    // Test getOrderByNameAndAge
    @Test
    public void testGetOrderByNameAndAge() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByNameAndAge", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.ASC, "name",
                DynamicFinderOrder.Order.Method.ASC, "age");
    }

    // Test getOrderByNameAscAndAge
    @Test
    public void testGetOrderByNameAscAndAge() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByNameAscAndAge", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.ASC, "name",
                DynamicFinderOrder.Order.Method.ASC, "age");
    }

    // Test getOrderByNameAscAndAgeAsc
    @Test
    public void testGetOrderByNameAscAndAgeAsc() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByNameAscAndAgeAsc", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.ASC, "name",
                DynamicFinderOrder.Order.Method.ASC, "age");
    }

    // Test getOrderByNameDescAndAge
    @Test
    public void testGetOrderByNameDescAndAge() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByNameDescAndAge", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.DESC, "name",
                DynamicFinderOrder.Order.Method.ASC, "age");
    }

    // Test getOrderByNameDescAndAgeAsc
    @Test
    public void testGetOrderByNameDescAndAgeAsc() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByNameDescAndAgeAsc", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.DESC, "name",
                DynamicFinderOrder.Order.Method.ASC, "age");
    }

    // Test getOrderByNameDescAndAgeDesc
    @Test
    public void testGetOrderByNameDescAndAgeDesc() {
        List<String> properties = List.of("name", "age");

        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByNameDescAndAgeDesc", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.DESC, "name",
                DynamicFinderOrder.Order.Method.DESC, "age");
    }

    // Helper method to evaluate use-cae with simple orderBy
    public void evaluateResultOrderByProperty(
            final DynamicFinder query,
            final DynamicFinderSelection.Method queryMathod,
            final DynamicFinderOrder.Order.Method orderMethod1,
            final String orderProperty1,
            final DynamicFinderOrder.Order.Method orderMethod2,
            final String orderProperty2,
            final DynamicFinderOrder.Order.Method orderMethod3,
            final String orderProperty3
    ) {
        // Selection
        assertThat(query.selection().method(), is(queryMathod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        // Criteria
        assertThat(query.criteria().isPresent(), is(false));
        // Order
        assertThat(query.order().isPresent(), is(true));
        assertThat(query.order().get().orders().size(), is(3));
        DynamicFinderOrder.Order order = query.order().get().orders().get(0);
        assertThat(order.method(), is(orderMethod1));
        assertThat(order.property(), is(orderProperty1));
        order = query.order().get().orders().get(1);
        assertThat(order.method(), is(orderMethod2));
        assertThat(order.property(), is(orderProperty2));
        order = query.order().get().orders().get(2);
        assertThat(order.method(), is(orderMethod3));
        assertThat(order.property(), is(orderProperty3));
    }

    // Test getOrderByFirstNameAndLastNameAndBirthDate
    @Test
    public void testGetOrderByFirstNameAndLastNameAndBirthDate() {
        List<String> properties = List.of("firstName", "lastName", "birthDate");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByFirstNameAndLastNameAndBirthDate", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.ASC, "firstName",
                DynamicFinderOrder.Order.Method.ASC, "lastName",
                DynamicFinderOrder.Order.Method.ASC, "birthDate");
    }


    // Test getOrderByFirstNameAndLastNameAscAndBirthDate
    @Test
    public void testGetOrderByFirstNameAndLastNameAscAndBirthDate() {
        List<String> properties = List.of("firstName", "lastName", "birthDate");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByFirstNameAndLastNameAscAndBirthDate", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.ASC, "firstName",
                DynamicFinderOrder.Order.Method.ASC, "lastName",
                DynamicFinderOrder.Order.Method.ASC, "birthDate");
    }

    // Test getOrderByFirstNameAndLastNameAscAndBirthDateAsc
    @Test
    public void testGetOrderByFirstNameAndLastNameAscAndBirthDateAsc() {
        List<String> properties = List.of("firstName", "lastName", "birthDate");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByFirstNameAndLastNameAscAndBirthDateAsc", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.ASC, "firstName",
                DynamicFinderOrder.Order.Method.ASC, "lastName",
                DynamicFinderOrder.Order.Method.ASC, "birthDate");
    }

    // Test getOrderByFirstNameAndLastNameDescAndBirthDateAsc
    @Test
    public void testGetOrderByFirstNameAndLastNameDescAndBirthDateAsc() {
        List<String> properties = List.of("firstName", "lastName", "birthDate");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByFirstNameAndLastNameDescAndBirthDateAsc", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.ASC, "firstName",
                DynamicFinderOrder.Order.Method.DESC, "lastName",
                DynamicFinderOrder.Order.Method.ASC, "birthDate");
    }

    // Test getOrderByFirstNameAndLastNameAscAndBirthDateDesc
    @Test
    public void testGetOrderByFirstNameAndLastNameAscAndBirthDateDesc() {
        List<String> properties = List.of("firstName", "lastName", "birthDate");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByFirstNameAndLastNameAscAndBirthDateDesc", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.ASC, "firstName",
                DynamicFinderOrder.Order.Method.ASC, "lastName",
                DynamicFinderOrder.Order.Method.DESC, "birthDate");
    }

    // Test getOrderByFirstNameAndLastNameDescAndBirthDateDesc
    @Test
    public void testGetOrderByFirstNameAndLastNameDescAndBirthDateDesc() {
        List<String> properties = List.of("firstName", "lastName", "birthDate");
        MethodParser parser = new MethodParserImpl(properties);
        @SuppressWarnings("unchecked")
        DynamicFinder query = parser.parse("getOrderByFirstNameAndLastNameDescAndBirthDateDesc", Collections.EMPTY_LIST);
        evaluateResultOrderByProperty(
                query, DynamicFinderSelection.Method.GET,
                DynamicFinderOrder.Order.Method.ASC, "firstName",
                DynamicFinderOrder.Order.Method.DESC, "lastName",
                DynamicFinderOrder.Order.Method.DESC, "birthDate");
    }

}
