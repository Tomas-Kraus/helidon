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
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CriteriaMethodParserTest {

    /** Local logger instance. */
    private static final Logger LOGGER = Logger.getLogger(CriteriaMethodParserTest.class.getName());

    // Test all possible ways to traverse from selection to criteria part of the query.
    // "By" keyword is connected to:
    //  - root node of the selection projection parser
    //  - root node of the selection properties parser
    //  - all final nodes of the selection properties parser

    // Test getByName method name ("By" starts from root node of the selection projection parser)
    @Test
    public void testGetByName() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByName", arguments);
        TestHelper.evaluateResultBy(
                query, DynamicFinderSelection.Method.GET, "name", "nameValue");
    }

    // Test findByAge method name ("By" starts from root node of the selection projection parser)
    @Test
    public void testFindByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findByAge", arguments);
        TestHelper.evaluateResultBy(
                query, DynamicFinderSelection.Method.FIND, "age", "ageValue");
    }

    // Test getCountByName method name ("By" starts from root node of the selection properties parser)
    @Test
    public void testGetCountByName() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getCountByName", arguments);
        TestHelper.evaluateResultProjectionBy(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.COUNT,
                null, "name", "nameValue");
    }

    // Test findDistinctByAge method name ("By" starts from root node of the selection properties parser)
    @Test
    public void testFindDistinctByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findDistinctByAge", arguments);
        TestHelper.evaluateResultProjectionBy(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.DISTINCT,
                null, "age", "ageValue");
    }

    // Test getCountNameByAge method name ("By" starts from final node of the selection properties parser)
    @Test
    public void testGetCountNameByAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getCountNameByAge", arguments);
        TestHelper.evaluateResultProjectionBy(
                query, DynamicFinderSelection.Method.GET, DynamicFinderSelection.Projection.Method.COUNT,
                "name", "age", "ageValue");
    }

    // Test findMaxAgeByName method name ("By" starts from final node of the selection properties parser)
    @Test
    public void testFindMaxAgeByName() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findMaxAgeByName", arguments);
        TestHelper.evaluateResultProjectionBy(
                query, DynamicFinderSelection.Method.FIND, DynamicFinderSelection.Projection.Method.MAX,
                "age", "name", "nameValue");
    }

    // Test various combinations of properties List content

    // Test the shortest initial substring of property names: getByFirst
    @Test
    public void testGetByFirst() {
        List<String> properties = List.of("first", "firstName", "firstJob", "firstNameOfPet");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByFirst", arguments);
        TestHelper.evaluateResultBy(
                query, DynamicFinderSelection.Method.GET, "first", "value");
    }

    // Test middle length initial substring of property names: getByFirstName
    @Test
    public void testGetByFirstName() {
        List<String> properties = List.of("first", "firstName", "firstJob", "firstNameOfPet");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByFirstName", arguments);
        TestHelper.evaluateResultBy(
                query, DynamicFinderSelection.Method.GET, "firstName", "value");
    }

    // Test middle length initial substring of property names: getByFirstJob
    @Test
    public void testGetByFirstJob() {
        List<String> properties = List.of("first", "firstName", "firstJob", "firstNameOfPet");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByFirstJob", arguments);
        TestHelper.evaluateResultBy(
                query, DynamicFinderSelection.Method.GET, "firstJob", "value");
    }

    // Test the longest length initial substring of property names: getByFirstNameOfPet
    @Test
    public void testGetByFirstNameOfPet() {
        List<String> properties = List.of("first", "firstName", "firstJob", "firstNameOfPet");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByFirstNameOfPet", arguments);
        TestHelper.evaluateResultBy(
                query, DynamicFinderSelection.Method.GET, "firstNameOfPet", "value");
    }

    // Test clash with 1st letter of By keyword after final state: findByFirstName
    @Test
    public void testFindByFirstName() {
        List<String> properties = List.of("first", "firstName", "firstNameB");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findByFirstName", arguments);
        TestHelper.evaluateResultBy(
                query, DynamicFinderSelection.Method.FIND, "firstName", "value");
    }

    // Test clash with 1st letter of By keyword after final state: getByFirstNameB
    @Test
    public void testGetByFirstNameB() {
        List<String> properties = List.of("first", "firstName", "firstNameB");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByFirstNameB", arguments);
        TestHelper.evaluateResultBy(
                query, DynamicFinderSelection.Method.GET, "firstNameB", "value");
    }

    // Test full "By" keyword conflict: getByFirstNameBy
    // In criteria part of the query "By" is not recognized as keyword so this test shall pass.
    @Test
    public void testGetByFirstNameByConflict() {
        List<String> properties = List.of("first", "firstName", "firstNameBy");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("getByFirstNameBy", arguments);
        TestHelper.evaluateResultBy(
                query, DynamicFinderSelection.Method.GET, "firstNameBy", "value");
    }

    // Test full "OrderBy" keyword conflict: getByFirstNameOrderBy with firstNameOrderBy entity property
    // This test shall throw MethodParserException during state machine build phase.
    @Test
    public void testGetByFirstNameOrderByConflict() {
        final List<String> properties = List.of("first", "firstName", "firstNameOrderBy");
        Assertions.assertThrows(MethodParserException.class, () -> {
            new MethodParserImpl(properties);
        });
    }

    // Test All criteria conditions

    // Test findByNameAfter
    @Test
    public void testFindByNameAfter() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "After";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.AFTER, "value"
            );
        }
    }

    // Test findByNameBefore
    @Test
    public void testFindByNameBefore() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "Before";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.BEFORE, "value"
            );
        }
    }

    // Test findByNameContains
    @Test
    public void testFindByNameContains() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "Contains";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.CONTAINS, "value"
            );
        }
    }

    // Test findByNameStartsWith
    @Test
    public void testFindByNameStartsWith() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "StartsWith";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.STARTS, "value"
            );
        }
    }

    // Test findByNameStartingWith
    @Test
    public void testFindByNameStartingWith() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "StartingWith";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.STARTS, "value"
            );
        }
    }

    // Test findByNameEndsWith
    @Test
    public void testFindByNameEndsWith() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "EndsWith";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.ENDS, "value"
            );
        }
    }

    // Test findByNameEndingWith
    @Test
    public void testFindByNameEndingWith() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "EndingWith";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.ENDS, "value"
            );
        }
    }

    // Test findByNameEqual
    @Test
    public void testFindByNameEqual() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "Equal";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.EQUALS, "value"
            );
        }
    }

    // Test findByNameEquals
    @Test
    public void testFindByNameEquals() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "Equals";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.EQUALS, "value"
            );
        }
    }

    // Test findByNameGreaterThan
    @Test
    public void testFindByNameGreaterThan() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "GreaterThan";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.GREATER_THAN, "value"
            );
        }
    }

    // Test findByNameGreaterThanEqual
    @Test
    public void testFindByNameGreaterThanEqual() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "GreaterThanEqual";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.GREATER_THAN_EQUALS, "value"
            );
        }
    }

    // Test findByNameGreaterThanEquals
    @Test
    public void testFindByNameGreaterThanEquals() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "GreaterThanEquals";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.GREATER_THAN_EQUALS, "value"
            );
        }
    }

    // Test findByNameLessThan
    @Test
    public void testFindByNameLessThan() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "LessThan";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.LESS_THAN, "value"
            );
        }
    }

    // Test findByNameLessThanEqual
    @Test
    public void testFindByNameLessThanEqual() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "LessThanEqual";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.LESS_THAN_EQUALS, "value"
            );
        }
    }

    // Test findByNameLessThanEquals
    @Test
    public void testFindByNameLessThanEquals() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "LessThanEquals";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.LESS_THAN_EQUALS, "value"
            );
        }
    }

    // Test findByNameLike
    @Test
    public void testFindByNameLike() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "Like";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.LIKE, "value"
            );
        }
    }

    // Test findByNameIlike
    @Test
    public void testFindByNameIlike() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "Ilike";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.ILIKE, "value"
            );
        }
    }

    // Test findByNameIn
    @Test
    public void testFindByNameIn() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "In";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.IN, "value"
            );
        }
    }

    // Test findByNameInList
    @Test
    public void testFindByNameInList() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("value");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "InList";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.IN, "value"
            );
        }
    }

    // Test findByNameBetween
    @Test
    public void testFindByNameBetween() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("from", "to");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "Between";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0,
                    DynamicFinderCriteria.Condition.Operator.BETWEEN, new String[] {"from", "to"}
            );
        }
    }

    // Test findByNameInRange
    @Test
    public void testFindByNameInRange() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("from", "to");
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "InRange";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0,
                    DynamicFinderCriteria.Condition.Operator.BETWEEN, new String[] {"from", "to"}
            );
        }
    }

    // Test findByNameNull
    @Test
    public void testFindByNameNull() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of();
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "Null";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.NULL
            );
        }
    }

    // Test findByNameIsNull
    @Test
    public void testFindByNameIsNull() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of();
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "IsNull";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.NULL
            );
        }
    }

    // Test findByNameEmpty
    @Test
    public void testFindByNameEmpty() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of();
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "Empty";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.EMPTY
            );
        }
    }

    // Test findByNameIsEmpty
    @Test
    public void testFindByNameIsEmpty() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of();
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "IsEmpty";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            TestHelper.evaluateResultByCondition(
                    query, "name", i > 0, DynamicFinderCriteria.Condition.Operator.EMPTY
            );
        }
    }

    // Test findByNameTrue
    @Test
    public void testFindByNameTrue() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of();
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "True";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            // Not True changes target value to FALSE
            TestHelper.evaluateResultByCondition(
                    query, "name", false,
                    i < 1   ? DynamicFinderCriteria.Condition.Operator.TRUE
                            : DynamicFinderCriteria.Condition.Operator.FALSE
            );
        }
    }

    // Test findByNameIsTrue
    @Test
    public void testFindByNameIsTrue() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of();
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "IsTrue";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            // Not True changes target value to FALSE
            TestHelper.evaluateResultByCondition(
                    query, "name", false,
                    i < 1   ? DynamicFinderCriteria.Condition.Operator.TRUE
                            : DynamicFinderCriteria.Condition.Operator.FALSE
            );
        }
    }

    // Test findByNameFalse
    @Test
    public void testFindByNameFalse() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of();
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "False";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            // Not False changes target value to TRUE
            TestHelper.evaluateResultByCondition(
                    query, "name", false,
                    i < 1   ? DynamicFinderCriteria.Condition.Operator.FALSE
                            : DynamicFinderCriteria.Condition.Operator.TRUE
            );
        }
    }

    // Test findByNameIsFalse
    @Test
    public void testFindByNameIsFalse() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of();
        MethodParser parser = new MethodParserImpl(properties);
        final String[] negation = new String[] {"", "Not", "IsNot"};
        for (int i = 0; i < 3; i++) {
            String methodName = "findByName" + negation[i] + "IsFalse";
            LOGGER.info(() -> String.format("Testing criteria property and condition: %s", methodName));
            DynamicFinder query = parser.parse(methodName, arguments);
            // Not False changes target value to TRUE
            TestHelper.evaluateResultByCondition(
                    query, "name", false,
                    i < 1   ? DynamicFinderCriteria.Condition.Operator.FALSE
                            : DynamicFinderCriteria.Condition.Operator.TRUE
            );
        }
    }


    // Test findByNameAndAge
    @Test
    public void testFindByNameAndAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue", "ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findByNameAndAge", arguments);
        TestHelper.evaluateResultBy(query, DynamicFinderSelection.Method.FIND,
                "name", "nameValue", "age", "ageValue",
                DynamicFinderCriteria.Compound.NextExpression.Operator.AND
        );
    }

    // Test findByNameOrAge
    @Test
    public void testFindByNameOrAge() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue", "ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findByNameOrAge", arguments);
        TestHelper.evaluateResultBy(query, DynamicFinderSelection.Method.FIND,
                "name", "nameValue", "age", "ageValue",
                DynamicFinderCriteria.Compound.NextExpression.Operator.OR
        );
    }

    // Test logical operator between expressions

    // Test findByNameAfterOrAgeBefore
    @Test
    public void testFindByNameAfterOrAgeBefore() {
        List<String> properties = List.of("name", "age");
        List<String> arguments = List.of("nameValue", "ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findByNameAfterOrAgeBefore", arguments);
        TestHelper.evaluateResultBy(query, DynamicFinderSelection.Method.FIND,
                DynamicFinderCriteria.Condition.Operator.AFTER,
                "name", "nameValue",
                DynamicFinderCriteria.Condition.Operator.BEFORE,
                "age", "ageValue",
                DynamicFinderCriteria.Compound.NextExpression.Operator.OR
        );
    }

    // Test logical operator between expressions on 3 expressions criteria part

    // Test findByNameAfterOrAgeBeforeAndMarried
    @Test
    public void testFindByNameAfterOrAgeBeforeAndMarried() {
        List<String> properties = List.of("name", "age", "married");
        List<String> arguments = List.of("nameValue", "ageValue", "marriedValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findByNameAfterOrAgeBeforeAndMarried", arguments);
        TestHelper.evaluateResultBy(query, DynamicFinderSelection.Method.FIND,
                DynamicFinderCriteria.Condition.Operator.AFTER,
                "name", "nameValue",
                DynamicFinderCriteria.Condition.Operator.BEFORE,
                "age", "ageValue",
                DynamicFinderCriteria.Compound.NextExpression.Operator.OR,
                DynamicFinderCriteria.Condition.Operator.EQUALS,
                "married", "marriedValue",
                DynamicFinderCriteria.Compound.NextExpression.Operator.AND
        );
    }

    // Test findByNameAfterOrAgeBeforeAndMarriedTrue
    @Test
    public void testFindByNameAfterOrAgeBeforeAndMarriedTrue() {
        List<String> properties = List.of("name", "age", "married");
        List<String> arguments = List.of("nameValue", "ageValue");
        MethodParser parser = new MethodParserImpl(properties);
        DynamicFinder query = parser.parse("findByNameAfterOrAgeBeforeAndMarriedTrue", arguments);
        TestHelper.evaluateResultBy(query, DynamicFinderSelection.Method.FIND,
                DynamicFinderCriteria.Condition.Operator.AFTER,
                "name", "nameValue",
                DynamicFinderCriteria.Condition.Operator.BEFORE,
                "age", "ageValue",
                DynamicFinderCriteria.Compound.NextExpression.Operator.OR,
                DynamicFinderCriteria.Condition.Operator.TRUE,
                "married", null,
                DynamicFinderCriteria.Compound.NextExpression.Operator.AND
        );
    }

}
