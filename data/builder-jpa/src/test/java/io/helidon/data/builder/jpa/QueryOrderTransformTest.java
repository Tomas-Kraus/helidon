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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class QueryOrderTransformTest {


    @Test
    public void testOrderByNameTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .orderBy("name")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name"));
    }

    @Test
    public void testOrderByNameAscTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .orderBy("name")
                .asc()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name"));
    }

    @Test
    public void testOrderByNameDescTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .orderBy("name")
                .desc()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name DESC"));
    }

    @Test
    public void testOrderByNameAndAgeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .orderBy("name")
                .and("age")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name, age"));
    }

    @Test
    public void testOrderByNameAndAgeAscTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .orderBy("name")
                .and("age")
                .asc()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name, age"));
    }

    @Test
    public void testOrderByNameAndAgeDescTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .orderBy("name")
                .and("age")
                .desc()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name, age DESC"));
    }

    @Test
    public void testOrderByNameDescAndAgeDescTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .orderBy("name")
                .desc()
                .and("age")
                .desc()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        System.out.println("OrderByName: " + stmt);
        assertThat(stmt, is("SELECT p FROM Person p ORDER BY name DESC, age DESC"));
    }

}
