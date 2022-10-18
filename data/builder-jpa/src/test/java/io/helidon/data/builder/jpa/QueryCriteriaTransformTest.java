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

public class QueryCriteriaTransformTest {

    private void evaluateSingleValueTransformation(
            final List<String> settings, final String stmt, final String stmtCheck, final String settingsCheck) {
        assertThat(stmt, is(stmtCheck));
        assertThat(settings.size(), is(1));
        assertThat(settings.get(0), is(settingsCheck));
    }

    @Test
    public void testCriteriaAfterTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .after("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("After: " + stmt);
        System.out.println("After: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age > :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotAfterTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .not()
                .after("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotAfter: " + stmt);
        System.out.println("NotAfter: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age <= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaBeforeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .before("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Before: " + stmt);
        System.out.println("Before: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age < :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotBeforeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .not()
                .before("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotBefore: " + stmt);
        System.out.println("NotBefore: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age >= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaContainsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .contains("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Contains: " + stmt);
        System.out.println("Contains: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name LIKE CONCAT('%', :nameValue, '%')",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotContainsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .contains("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotContains: " + stmt);
        System.out.println("NotContains: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT LIKE CONCAT('%', :nameValue, '%')",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaStartsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .starts("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Starts: " + stmt);
        System.out.println("Starts: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name LIKE CONCAT(:nameValue, '%')",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotStartsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .starts("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotStarts: " + stmt);
        System.out.println("NotStarts: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT LIKE CONCAT(:nameValue, '%')",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaEndsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .ends("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Ends: " + stmt);
        System.out.println("Ends: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name LIKE CONCAT('%', :nameValue)",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotEndsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .ends("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotEnds: " + stmt);
        System.out.println("NotEnds: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT LIKE CONCAT('%', :nameValue)",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaEqualsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .eq("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("EqualsT: " + stmt);
        System.out.println("EqualsT: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age = :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotEqualsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .not()
                .eq("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotEqualsT: " + stmt);
        System.out.println("NotEqualsT: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age <> :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    // Default operation is Equals
    @Test
    public void testCriteriaDefaultTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age", "ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Default: " + stmt);
        System.out.println("Default: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age = :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    // Default operation is Equals
    @Test
    public void testCriteriaNotDefaultTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age", "ageValue")
                .not()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotDefault: " + stmt);
        System.out.println("NotDefault: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age <> :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaGreaterThanTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .gt("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("GreaterThan: " + stmt);
        System.out.println("GreaterThan: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age > :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotGreaterThanTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .not()
                .gt("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotGreaterThan: " + stmt);
        System.out.println("NotGreaterThan: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age <= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaGreaterThanEqualsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .gte("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("GreaterThanEquals: " + stmt);
        System.out.println("GreaterThanEquals: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age >= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotGreaterThanEqualsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .not()
                .gte("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotGreaterThanEquals: " + stmt);
        System.out.println("NotGreaterThanEquals: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age < :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaLessThanTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .lt("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("LessThan: " + stmt);
        System.out.println("LessThan: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age < :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotLessThanTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .not()
                .lt("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotLessThan: " + stmt);
        System.out.println("NotLessThan: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age >= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaLessThanEqualsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .lte("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("LessThanEquals: " + stmt);
        System.out.println("LessThanEquals: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age <= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotLessThanEqualsTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .not()
                .lte("ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotLessThanEquals: " + stmt);
        System.out.println("NotLessThanEquals: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age > :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaLikeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .like("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Like: " + stmt);
        System.out.println("Like: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name LIKE :nameValue",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotLikeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .like("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotLike: " + stmt);
        System.out.println("NotLike: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT LIKE :nameValue",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaIlikeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .iLike("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Ilike: " + stmt);
        System.out.println("Ilike: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE LOWER(p.name) LIKE LOWER(:nameValue)",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotIlikeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .iLike("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotIlike: " + stmt);
        System.out.println("NotIlike: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE LOWER(p.name) NOT LIKE LOWER(:nameValue)",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaInTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .in("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("In: " + stmt);
        System.out.println("In: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IN :nameValue",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotInTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .in("nameValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotIn: " + stmt);
        System.out.println("NotIn: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT IN :nameValue",
                "setParameter(\"nameValue\", nameValue)");
    }

    private void evaluateDualValueTransformation(
            final List<String> settings, final String stmt, final String stmtCheck, final String settingsCheck1, final String settingsCheck2) {
        assertThat(stmt, is(stmtCheck));
        assertThat(settings.size(), is(2));
        assertThat(settings.get(0), is(settingsCheck1));
        assertThat(settings.get(1), is(settingsCheck2));
    }

    @Test
    public void testCriteriaBetweenTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .between("min", "max")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Between: " + stmt);
        System.out.println("Between: "+ settings.get(0));
        System.out.println("Between: "+ settings.get(1));
        evaluateDualValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age BETWEEN :min AND :max",
                "setParameter(\"min\", min)",
                "setParameter(\"max\", max)");
    }

    @Test
    public void testCriteriaNotBetweenTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("age")
                .not()
                .between("min", "max")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotBetween: " + stmt);
        System.out.println("NotBetween: "+ settings.get(0));
        System.out.println("NotBetween: "+ settings.get(1));
        evaluateDualValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age NOT BETWEEN :min AND :max",
                "setParameter(\"min\", min)",
                "setParameter(\"max\", max)");
    }

    private void evaluateNoValueTransformation(
            final List<String> settings, final String stmt, final String stmtCheck) {
        assertThat(stmt, is(stmtCheck));
        assertThat(settings.size(), is(0));
    }

    @Test
    public void testCriteriaNullTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .isNull()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Null: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IS NULL");
    }

    @Test
    public void testCriteriaNotNullTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .isNull()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotNull: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IS NOT NULL");
    }

    @Test
    public void testCriteriaEmptyTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .empty()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("Empty: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IS EMPTY");
    }

    @Test
    public void testCriteriaNotEmptyTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name")
                .not()
                .empty()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotEmpty: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IS NOT EMPTY");
    }

    @Test
    public void testCriteriaTrueTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("alive")
                .isTrue()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("SchrodingerCat");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("True: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT s FROM SchrodingerCat s WHERE s.alive = TRUE");
    }

    @Test
    public void testCriteriaNotTrueTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("alive")
                .not()
                .isTrue()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("SchrodingerCat");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotTrue: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT s FROM SchrodingerCat s WHERE s.alive = FALSE");
    }

    @Test
    public void testCriteriaFalseTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("alive")
                .isFalse()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("SchrodingerCat");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("False: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT s FROM SchrodingerCat s WHERE s.alive = FALSE");
    }

    @Test
    public void testCriteriaNotFalseTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("alive")
                .not()
                .isFalse()
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("SchrodingerCat");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NotFalse: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT s FROM SchrodingerCat s WHERE s.alive = TRUE");
    }

    @Test
    public void testCriteriaSimpleNameAndAgeTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name", "nameValue")
                .and("age", "ageValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NameAndAge: " + stmt);
        System.out.println("NameAndAge: "+ settings.get(0));
        System.out.println("NameAndAge: "+ settings.get(1));
        evaluateDualValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE (p.name = :nameValue) AND (p.age = :ageValue)",
                "setParameter(\"nameValue\", nameValue)",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaSimpleNameAndAgeOrSalaryTransformation() {
        final DynamicFinder query = DynamicFinder.builder()
                .get()
                .by("name", "nameValue")
                .and("age", "ageValue")
                .or("salary", "salaryValue")
                .build();
        final TransformQuery transform = new TransformQuery(query);
        transform.transform("Person");
        final String stmt = transform.statement();
        final List<String> settings = transform.querySettings();
        System.out.println("NameAndAge: " + stmt);
        System.out.println("NameAndAge: "+ settings.get(0));
        System.out.println("NameAndAge: "+ settings.get(1));
        System.out.println("NameAndAge: "+ settings.get(2));
        assertThat(stmt, is("SELECT p FROM Person p WHERE (p.name = :nameValue) AND (p.age = :ageValue) OR (p.salary = :salaryValue)"));
        assertThat(settings.size(), is(3));
        assertThat(settings.get(0), is("setParameter(\"nameValue\", nameValue)"));
        assertThat(settings.get(1), is("setParameter(\"ageValue\", ageValue)"));
        assertThat(settings.get(2), is("setParameter(\"salaryValue\", salaryValue)"));
    }

}
