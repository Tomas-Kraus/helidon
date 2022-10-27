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

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SelectionMethodParserTest {

    @SuppressWarnings("unchecked")
    private static final List<String> METHOD_ARGUMENTS = Collections.EMPTY_LIST;

    // Test minimal get method name
    // "get" shall be translated to "SELECT e FROM Entity e" query.
    @Test
    public void testMinimalGetMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("get", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultOnly(query, DynamicFinderSelection.Method.GET);
    }

    // Test minimal find method name
    // "find" shall be translated to "SELECT e FROM Entity e" query.
    @Test
    public void testMinimalFindMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("find", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultOnly(query, DynamicFinderSelection.Method.FIND);
    }

    // Test getCount method name
    @Test
    public void testGetCountMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getCount", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.COUNT);
    }

    // Test findCount method name
    @Test
    public void testFindCountMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findCount", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.COUNT);
    }

    // Test getCountDistinct method name
    @Test
    public void testGetCountDistinctMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getCountDistinct", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.COUNT_DISTINCT);
    }

    // Test findCountDistinct method name
    @Test
    public void testFindCountDistinctMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findCountDistinct", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.COUNT_DISTINCT);
    }

    // Test getCount method name
    @Test
    public void testGetDistinctMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getDistinct", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.DISTINCT);
    }

    // Test findMaxmethod name
    @Test
    public void testFindDistinctMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findDistinct", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.DISTINCT);
    }

    // Test getCount method name
    @Test
    public void testGetMaxMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getMax", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.MAX);
    }

    // Test findMax method name
    @Test
    public void testFindMaxMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findMax", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.MAX);
    }

    // Test getMin method name
    @Test
    public void testGetMinMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getMin", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.MIN);
    }

    // Test findMin method name
    @Test
    public void testFindMinMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findMin", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.MIN);
    }

    // Test getSum method name
    @Test
    public void testGetSumMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getSum", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.SUM);
    }

    // Test findSum method name
    @Test
    public void testFindSumMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findSum", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.SUM);
    }

    // Test getAvg method name
    @Test
    public void testGetAvgMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getAvg", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.AVG);
    }

    // Test findAvg method name
    @Test
    public void testFindAvgMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findAvg", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionOnly(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.AVG);
    }

    // Test getTop1 method name
    @Test
    public void testGetTop1Method() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getTop1", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionTopOnly(
                query, DynamicFinderSelection.Method.GET, 1);
    }

    // Test findTop1 method name
    @Test
    public void testFindTop1Method() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findTop1", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionTopOnly(
                query, DynamicFinderSelection.Method.FIND, 1);
    }

    // Test getTop12 method name
    @Test
    public void testGetTop12Method() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getTop12", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionTopOnly(
                query, DynamicFinderSelection.Method.GET, 12);
    }

    // Test findTop12 method name
    @Test
    public void testFindTop12Method() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findTop12", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionTopOnly(
                query, DynamicFinderSelection.Method.FIND, 12);
    }

    // Test getTop123 method name
    @Test
    public void testGetTop123Method() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getTop123", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionTopOnly(
                query, DynamicFinderSelection.Method.GET, 123);
    }

    // Test findTop123 method name
    @Test
    public void testFindTop123Method() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findTop123", METHOD_ARGUMENTS);
        DynamicFinderBuilderTest.evaluateResultAndProjectionTopOnly(
                query, DynamicFinderSelection.Method.FIND, 123);
    }

    // Helper method to evaluate use-cae with result and projection selected.
    public static void evaluateResultPropertyOnly(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            final String property
    ) {
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isEmpty(), is(true));
        assertThat(query.selection().property().isPresent(), is(true));
        assertThat(query.selection().property().get(), is(property));
        assertThat(query.criteria().isEmpty(), is(true));
    }

    // Test getName method name
    @Test
    public void testGetNameMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getName", METHOD_ARGUMENTS);
        evaluateResultPropertyOnly(
                query, DynamicFinderSelection.Method.GET, "name");
    }

    // Test findAge method name
    @Test
    public void testFindAgeMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findAge", METHOD_ARGUMENTS);
        evaluateResultPropertyOnly(
                query, DynamicFinderSelection.Method.FIND, "age");
    }

    // Helper method to evaluate use-cae with result and projection selected.
    public static void evaluateResultProjectionAndProperty(
            final DynamicFinder query,
            final DynamicFinderSelection.Method selectionMethod,
            final DynamicFinderSelection.Projection.Method projectionMethod,
            final String property
    ) {
        assertThat(query.selection().method(), is(selectionMethod));
        assertThat(query.selection().projection().isPresent(), is(true));
        assertThat(query.selection().projection().get().method(), is(projectionMethod));
        assertThat(query.selection().projection().get().parameter().isEmpty(), is(true));
        assertThat(query.selection().property().isPresent(), is(true));
        assertThat(query.selection().property().get(), is(property));
        assertThat(query.criteria().isEmpty(), is(true));
    }

    // Test getCountName method name
    @Test
    public void testGetCountNameMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getCountName", METHOD_ARGUMENTS);
        evaluateResultProjectionAndProperty(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.COUNT, "name");
    }

    // Test getCountName method name
    @Test
    public void testGetMaxAgeMethod() {
        List<String> properties = List.of("name", "age");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getMaxAge", METHOD_ARGUMENTS);
        evaluateResultProjectionAndProperty(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.MAX, "age");
    }

}
