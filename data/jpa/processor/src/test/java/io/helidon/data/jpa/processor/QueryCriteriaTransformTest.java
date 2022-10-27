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

public class QueryCriteriaTransformTest {

    private static final String PERSON_ENTITY_NAME = "Person";
    private static final List<String> PERSON_ENTITY_PROPERTIES = List.of("name", "age", "salary");
    @SuppressWarnings("unchecked")
    private static final List<String> EMPTY_METHOD_ARGUMENTS = Collections.EMPTY_LIST;

    // Parse method prototype and transform AST to target platform query
    private static DynamicFinderStatement parseQuery(
            String entityName,
            List<String> entityProperties,
            String methodName,
            List<String> methodArguments) {
        MethodParser parser = DynamicFinder.parser(entityProperties);
        return parser.parse(entityName, methodName, methodArguments);
    }

    private void evaluateSingleValueTransformation(
            List<String> settings,
            String stmt,
            String stmtCheck,
            String settingsCheck) {
        assertThat(stmt, is(stmtCheck));
        assertThat(settings.size(), is(1));
        assertThat(settings.get(0), is(settingsCheck));
    }

    @Test
    public void testCriteriaAfterTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeAfter", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("After: " + stmt);
        System.out.println("After: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age > :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotAfterTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeNotAfter", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotAfter: " + stmt);
        System.out.println("NotAfter: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age <= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaBeforeTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeBefore", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("Before: " + stmt);
        System.out.println("Before: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age < :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotBeforeTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeNotBefore", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotBefore: " + stmt);
        System.out.println("NotBefore: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age >= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaContainsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameContains", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("Contains: " + stmt);
        System.out.println("Contains: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name LIKE CONCAT('%', :nameValue, '%')",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotContainsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameNotContains", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotContains: " + stmt);
        System.out.println("NotContains: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT LIKE CONCAT('%', :nameValue, '%')",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaStartsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameStartsWith", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("Starts: " + stmt);
        System.out.println("Starts: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name LIKE CONCAT(:nameValue, '%')",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotStartsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameNotStartsWith", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotStarts: " + stmt);
        System.out.println("NotStarts: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT LIKE CONCAT(:nameValue, '%')",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaEndsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameEndsWith", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("Ends: " + stmt);
        System.out.println("Ends: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name LIKE CONCAT('%', :nameValue)",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotEndsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameNotEndsWith", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotEnds: " + stmt);
        System.out.println("NotEnds: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT LIKE CONCAT('%', :nameValue)",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaEqualsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeEquals", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("EqualsT: " + stmt);
        System.out.println("EqualsT: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age = :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotEqualsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeNotEquals", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotEqualsT: " + stmt);
        System.out.println("NotEqualsT: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age <> :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    // Default operation is Equals
    @Test
    public void testCriteriaDefaultTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAge", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("Default: " + stmt);
        System.out.println("Default: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age = :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaGreaterThanTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeGreaterThan", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("GreaterThan: " + stmt);
        System.out.println("GreaterThan: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age > :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotGreaterThanTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeNotGreaterThan", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotGreaterThan: " + stmt);
        System.out.println("NotGreaterThan: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age <= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaGreaterThanEqualsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeGreaterThanEquals", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("GreaterThanEquals: " + stmt);
        System.out.println("GreaterThanEquals: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age >= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotGreaterThanEqualsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeNotGreaterThanEquals", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotGreaterThanEquals: " + stmt);
        System.out.println("NotGreaterThanEquals: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age < :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaLessThanTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeLessThan", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("LessThan: " + stmt);
        System.out.println("LessThan: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age < :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotLessThanTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeNotLessThan", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotLessThan: " + stmt);
        System.out.println("NotLessThan: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age >= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaLessThanEqualsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeLessThanEquals", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("LessThanEquals: " + stmt);
        System.out.println("LessThanEquals: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age <= :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaNotLessThanEqualsTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeNotLessThanEquals", List.of("ageValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotLessThanEquals: " + stmt);
        System.out.println("NotLessThanEquals: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age > :ageValue",
                "setParameter(\"ageValue\", ageValue)");
    }

    @Test
    public void testCriteriaLikeTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameLike", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("Like: " + stmt);
        System.out.println("Like: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name LIKE :nameValue",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotLikeTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameNotLike", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotLike: " + stmt);
        System.out.println("NotLike: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT LIKE :nameValue",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaIlikeTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameIlike", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("Ilike: " + stmt);
        System.out.println("Ilike: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE LOWER(p.name) LIKE LOWER(:nameValue)",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotIlikeTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameNotIlike", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotIlike: " + stmt);
        System.out.println("NotIlike: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE LOWER(p.name) NOT LIKE LOWER(:nameValue)",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaInTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameIn", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("In: " + stmt);
        System.out.println("In: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IN :nameValue",
                "setParameter(\"nameValue\", nameValue)");
    }

    @Test
    public void testCriteriaNotInTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameNotIn", List.of("nameValue"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotIn: " + stmt);
        System.out.println("NotIn: "+ settings.get(0));
        evaluateSingleValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name NOT IN :nameValue",
                "setParameter(\"nameValue\", nameValue)");
    }

    private void evaluateDualValueTransformation(
            List<String> settings,
            String stmt,
            String stmtCheck,
            String settingsCheck1,
            String settingsCheck2) {
        assertThat(stmt, is(stmtCheck));
        assertThat(settings.size(), is(2));
        assertThat(settings.get(0), is(settingsCheck1));
        assertThat(settings.get(1), is(settingsCheck2));
    }

    @Test
    public void testCriteriaBetweenTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeBetween", List.of("min", "max"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
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
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByAgeNotBetween", List.of("min", "max"));
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotBetween: " + stmt);
        System.out.println("NotBetween: "+ settings.get(0));
        System.out.println("NotBetween: "+ settings.get(1));
        evaluateDualValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.age NOT BETWEEN :min AND :max",
                "setParameter(\"min\", min)",
                "setParameter(\"max\", max)");
    }

    private void evaluateNoValueTransformation(List<String> settings, String stmt, String stmtCheck) {
        assertThat(stmt, is(stmtCheck));
        assertThat(settings.size(), is(0));
    }

    @Test
    public void testCriteriaNullTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameIsNull", EMPTY_METHOD_ARGUMENTS);
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("Null: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IS NULL");
    }

    @Test
    public void testCriteriaNotNullTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameIsNotNull", EMPTY_METHOD_ARGUMENTS);
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotNull: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IS NOT NULL");
    }

    @Test
    public void testCriteriaEmptyTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameIsEmpty", EMPTY_METHOD_ARGUMENTS);
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("Empty: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IS EMPTY");
    }

    @Test
    public void testCriteriaNotEmptyTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameIsNotEmpty", EMPTY_METHOD_ARGUMENTS);
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotEmpty: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT p FROM Person p WHERE p.name IS NOT EMPTY");
    }

    @Test
    public void testCriteriaTrueTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                "SchrodingerCat", List.of("alive"), "getByAliveIsTrue", EMPTY_METHOD_ARGUMENTS);
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("True: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT s FROM SchrodingerCat s WHERE s.alive = TRUE");
    }

    @Test
    public void testCriteriaNotTrueTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                "SchrodingerCat", List.of("alive"), "getByAliveIsNotTrue", EMPTY_METHOD_ARGUMENTS);
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotTrue: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT s FROM SchrodingerCat s WHERE s.alive = FALSE");
    }

    @Test
    public void testCriteriaFalseTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                "SchrodingerCat", List.of("alive"), "getByAliveIsFalse", EMPTY_METHOD_ARGUMENTS);
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("False: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT s FROM SchrodingerCat s WHERE s.alive = FALSE");
    }

    @Test
    public void testCriteriaNotFalseTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                "SchrodingerCat", List.of("alive"), "getByAliveIsNotFalse", EMPTY_METHOD_ARGUMENTS);
        String stmt = dfs.statement();
        List<String> settings = dfs.querySettings();
        System.out.println("NotFalse: " + stmt);
        evaluateNoValueTransformation(settings, stmt,
                "SELECT s FROM SchrodingerCat s WHERE s.alive = TRUE");
    }

    @Test
    public void testCriteriaSimpleNameAndAgeTransformation() {
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES, "getByNameAndAge", List.of("nameValue", "ageValue"));
        final String stmt = dfs.statement();
        final List<String> settings = dfs.querySettings();
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
        DynamicFinderStatement dfs = parseQuery(
                PERSON_ENTITY_NAME, PERSON_ENTITY_PROPERTIES,
                "getByNameAndAgeOrSalary", List.of("nameValue", "ageValue", "salaryValue"));
        final String stmt = dfs.statement();
        final List<String> settings = dfs.querySettings();
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
