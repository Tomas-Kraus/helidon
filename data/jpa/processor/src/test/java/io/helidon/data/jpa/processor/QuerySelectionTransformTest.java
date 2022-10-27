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

public class QuerySelectionTransformTest {

    private static final String ENTITY_NAME = "Person";
    private static final List<String> ENTITY_PROPERTIES = List.of("name", "age", "salary");
    @SuppressWarnings("unchecked")
    private static final List<String> METHOD_ARGUMENTS = Collections.EMPTY_LIST;

    // Parse method prototype and transform AST to target platform query
    private static DynamicFinderStatement parseQuery(String methodName) {
        MethodParser parser = DynamicFinder.parser(QuerySelectionTransformTest.ENTITY_PROPERTIES);
        return parser.parse(ENTITY_NAME, methodName, METHOD_ARGUMENTS);
    }

    @Test
    public void testSelectionPropertyTransformation() {
        DynamicFinderStatement dfs = parseQuery("findName");
        String stmt = dfs.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT p.name FROM Person p"));
        assertThat(dfs.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionCountTransformation() {
        DynamicFinderStatement dfs = parseQuery("getCount");
        String stmt = dfs.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT COUNT(p) FROM Person p"));
        assertThat(dfs.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionDistinctNameTransformation() {
        DynamicFinderStatement dfs = parseQuery("findDistinctName");
        String stmt = dfs.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT DISTINCT p.name FROM Person p"));
        assertThat(dfs.querySettings().isEmpty(), is(true));
    }


    @Test
    public void testSelectionCountDistinctSalaryTransformation() {
        DynamicFinderStatement dfs = parseQuery("findCountDistinctSalary");
        String stmt = dfs.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT COUNT(DISTINCT p.salary) FROM Person p"));
        assertThat(dfs.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionMaxAgeTransformation() {
        DynamicFinderStatement dfs = parseQuery("findMaxAge");
        String stmt = dfs.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT MAX(p.age) FROM Person p"));
        assertThat(dfs.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionMinAgeTransformation() {
        DynamicFinderStatement dfs = parseQuery("findMinAge");
        String stmt = dfs.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT MIN(p.age) FROM Person p"));
        assertThat(dfs.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionSumSalaryTransformation() {
        DynamicFinderStatement dfs = parseQuery("findSumSalary");
        String stmt = dfs.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT SUM(p.salary) FROM Person p"));
        assertThat(dfs.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionAvgAgeTransformation() {
        DynamicFinderStatement dfs = parseQuery("findAvgAge");
        final String stmt = dfs.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT AVG(p.age) FROM Person p"));
        assertThat(dfs.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionTop10ransformation() {
        DynamicFinderStatement dfs = parseQuery("findTop10");
        final String stmt = dfs.statement();
        final List<String> settings = dfs.querySettings();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT p FROM Person p"));
        assertThat(settings.size(), is(1));
        System.out.println("QUERY SETTINGS: "+ settings.get(0));
        assertThat(settings.get(0), is("setMaxResults(10)"));
    }

}
