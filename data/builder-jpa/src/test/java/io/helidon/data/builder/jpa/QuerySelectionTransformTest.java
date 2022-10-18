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
package io.helidon.data.builder.jpa;

import io.helidon.data.builder.query.DynamicFinder;
import io.helidon.data.builder.query.transform.TransformQuery;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class QuerySelectionTransformTest {

    @Test
    public void testSelectionPropertyTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .find()
                .property("name")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT p.name FROM Person p"));
        assertThat(transform.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionCountTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .find()
                .count()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT COUNT(p) FROM Person p"));
        assertThat(transform.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionDistinctNameTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .find()
                .distinct()
                .property("name")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT DISTINCT p.name FROM Person p"));
        assertThat(transform.querySettings().isEmpty(), is(true));
    }


    @Test
    public void testSelectionCountDistinctSalaryTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .find()
                .countDistinct()
                .property("salary")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT COUNT(DISTINCT p.salary) FROM Person p"));
        assertThat(transform.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionMaxAgeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .find()
                .max()
                .property("age")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT MAX(p.age) FROM Person p"));
        assertThat(transform.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionMinAgeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .find()
                .min()
                .property("age")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT MIN(p.age) FROM Person p"));
        assertThat(transform.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionSumSalaryTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .find()
                .sum()
                .property("salary")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT SUM(p.salary) FROM Person p"));
        assertThat(transform.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionAvgAgeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .find()
                .avg()
                .property("age")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT AVG(p.age) FROM Person p"));
        assertThat(transform.querySettings().isEmpty(), is(true));
    }

    @Test
    public void testSelectionTop10ransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .find()
                .top(10)
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("STATEMENT: "+ stmt);
        assertThat(stmt, is("SELECT p FROM Person p"));
        assertThat(settings.size(), is(1));
        System.out.println("QUERY SETTINGS: "+ settings.get(0));
        assertThat(settings.get(0), is("setMaxResults(10)"));
    }

}
