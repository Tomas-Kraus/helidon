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
package io.helidon.data.jpa.processor;

import io.helidon.data.processor.DynamicFinder;
import io.helidon.data.processor.DynamicFinderStatement;
import io.helidon.data.processor.MethodParser;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class QueryOrderTransformTest {

    private static final String ENTITY_NAME = "Person";
    private static final List<String> ENTITY_PROPERTIES = List.of("name", "age", "salary");
    @SuppressWarnings("unchecked")
    private static final List<String> METHOD_ARGUMENTS = Collections.EMPTY_LIST;

    // Parse method prototype and transform AST to target platform query
    private static DynamicFinderStatement parseQuery(String methodName) {
        MethodParser parser = DynamicFinder.parser(ENTITY_PROPERTIES);
        return parser.parse(ENTITY_NAME, methodName, METHOD_ARGUMENTS);
    }

    @Test
    public void testOrderByNameTransformation() {
        DynamicFinderStatement dfs = parseQuery("getOrderByName");
        String stmt = dfs.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name"));
    }

    @Test
    public void testOrderByNameAscTransformation() {
        DynamicFinderStatement dfs = parseQuery("getOrderByNameAsc");
        String stmt = dfs.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name"));
    }

    @Test
    public void testOrderByNameDescTransformation() {
        DynamicFinderStatement dfs = parseQuery("getOrderByNameDesc");
        String stmt = dfs.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name DESC"));
    }

    @Test
    public void testOrderByNameAndAgeTransformation() {
        DynamicFinderStatement dfs = parseQuery("getOrderByNameAndAge");
        String stmt = dfs.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name, age"));
    }

    @Test
    public void testOrderByNameAndAgeAscTransformation() {
        DynamicFinderStatement dfs = parseQuery("getOrderByNameAndAgeAsc");
        String stmt = dfs.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name, age"));
    }

    @Test
    public void testOrderByNameAndAgeDescTransformation() {
        DynamicFinderStatement dfs = parseQuery("getOrderByNameAndAgeDesc");
        String stmt = dfs.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name, age DESC"));
    }

    @Test
    public void testOrderByNameDescAndAgeDescTransformation() {
        DynamicFinderStatement dfs = parseQuery("getOrderByNameDescAndAgeDesc");
        String stmt = dfs.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name DESC, age DESC"));
    }

}
