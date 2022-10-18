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
package io.helidon.data.builder.query;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Criteria part of the Helidon dynamic finder query.
 */
public class DynamicFinderCriteria {

    /**
     * Query criteria expression.
     */
    public static class Expression {

        /**
         * Expression condition.
         */
        public static class Condition {

            /**
             * Condition operators with supported keywords.
             * Numbers of parameters are used to assign method parameters to statement {@code setParameter} calls.
             * Keywords are used to initialize related part of dynamic finder query method name parser.
             */
            public enum Operator {
                AFTER(1, "After"),
                BEFORE(1, "Before"),
                CONTAINS(1, "Contains"),
                STARTS(1, "StartsWith", "StartingWith"),
                ENDS(1, "EndsWith", "EndingWith"),
                EQUALS(1, "Equal", "Equals"),
                GREATER_THAN(1, "GreaterThan"),
                GREATER_THAN_EQUALS(1, "GreaterThanEqual", "GreaterThanEquals"),
                LESS_THAN(1, "LessThan"),
                LESS_THAN_EQUALS(1, "LessThanEqual", "LessThanEquals"),
                LIKE(1, "Like"),
                ILIKE(1, "Ilike"),
                IN(1, "In", "InList"),
                BETWEEN(2, "Between", "InRange"),
                NULL(0, "Null", "IsNull"),
                EMPTY(0, "Empty", "IsEmpty"),
                TRUE(0, "True", "IsTrue"),
                FALSE(0, "False", "IsFalse");

                // Number of method parameters
                private int paramCount;
                // Supported method keywords
                private String[] keywords;

                // Creates an instance of
                private Operator(final int paramCount, final String... keywords) {
                    if (keywords == null || keywords.length == 0) {
                        throw new IllegalArgumentException("At least one keyword is required!");
                    }
                    this.paramCount = paramCount;
                    this.keywords = keywords;
                }

                /**
                 * Supported keywords.
                 *
                 * @return keywords supported by the condition method.
                 */
                public String[] keywords() {
                    return keywords;
                }

                /**
                 * Number of operator parameters.
                 *
                 * @return number of operator parameters required from dynamic finder query method
                 */
                public int paramCount() {
                    return paramCount;
                }

            }

            // Condition operator.
            private final Operator operator;
            // Condition values: dynamic finder query method parameters names assigned to condition
            private final List<String> values;

            // Creates an instance of expression condition.
            private Condition(final Operator operator, final List<String> values) {
                this.operator = operator;
                this.values = values;
            }

            /**
             * Condition operator.
             *
             * @return method of the expression condition
             */
            public Operator operator() {
                return operator;
            }

            /**
             * Condition values.
             * Values are passed as dynamic finder query method arguments.
             *
             * @return values of the expression condition
             */
            public List<String> values() {
                return values;
            }

        }

        // Criteria expression property.
        private final String property;
        // Negated expression.
        private final boolean not;
        // Criteria expression condition.
        private final Condition condition;

        // Creates an instance of query criteria expression.
        private Expression(final String property, final boolean not, final Condition condition) {
            if (condition == null) {
                throw new IllegalArgumentException("Expression condition shal not be null");
            }
            this.property = property;
            this.not = not;
            this.condition = condition;
        }

        /**
         * Criteria expression property.
         *
         * @return name of the property
         */
        public String property() {
            return property;
        }

        /**
         * Negated expression.
         *
         * @return value of {@code true} when expression is negated or {@code false} otherwise
         */
        public boolean not() {
            return not;
        }

        /**
         * Criteria expression condition.
         *
         * @return condition of the expression
         */
        public Condition condition() {
            return condition;
        }

    }

    /**
     * Query criteria expression with logical operator.
     */
    public static class NextExpression extends Expression {

        /**
         * Expression logical operators.
         * Used to connect with previous expression in the query criteria.
         */
        public enum Operator {
            AND,
            OR
        }

        // Expression logical operator.
        private final Operator operator;

        // Creates an instance of query criteria expression.
        private NextExpression(
                final Operator operator, final String parameter, final boolean not, final Condition condition) {
            super(parameter, not, condition);
            this.operator = operator;
        }

        /**
         * Expression logical operator.
         *
         * @return logical operator to connect with previous expression in the query criteria.
         */
        public Operator operator() {
            return operator;
        }

    }

    // Query criteria first expression.
    private final Expression first;
    // Query criteria expressions following the first one.
    private final List<NextExpression> next;

    // Creates an instance of criteria part of the Helidon dynamic finder query.
    private DynamicFinderCriteria(final Expression first, List<NextExpression> next) {
        if (first == null) {
            throw new IllegalArgumentException("First expression Optional<Expression> argument shall not be null!");
        }
        if (next == null) {
            throw new IllegalArgumentException("Next expressions List<NextExpression> argument shall not be null!");
        }
        this.first = first;
        this.next = next;
    }

    /**
     * Query criteria first expression.
     * Single expression does not need logical operator.
     *
     * @return first (mandatory) expression of the query criteria, never returns {@code null}.
     */
    public Expression first() {
        return first;
    }

    /**
     * Query criteria expressions following the first one.
     * Contains logical operator to apply as connection with previous expression.
     * Should return empty list when no next expression is available, never returns {@code null}.
     *
     * @return list of expressions following the first one, never returns {@code null}.
     */
    public List<NextExpression> next() {
        return next;
    }

    /**
     * Helidon dynamic finder query criteria builder.
     */
    public static class Builder implements BuilderBy, BuilderNextOperator, BuilderNext {

        // Criteria expression builder.
        private static class ExpressionBuilder {

            // Criteria expression property.
            final String property;
            // Negated expression.
            boolean not;
            // Criteria expression condition.
            Expression.Condition condition;

            // Creates an instance of expression builder.
            private ExpressionBuilder(final String property) {
                this.property = property;
                this.not = false;
                this.condition = null;
            }

            private void not() {
                not = true;
            }

            void condition(final Expression.Condition condition) {
                this.condition = condition;
            }

            private Expression build() {
                // Optimize logical values on AST building level.
                if (not) {
                    switch (condition.operator) {
                        // Convert NotTrue to False
                        case TRUE:
                            return new Expression(property, false,
                                    new Expression.Condition(Expression.Condition.Operator.FALSE, condition.values));
                        // Convert NotFalse to True
                        case FALSE:
                            return new Expression(property, false,
                                    new Expression.Condition(Expression.Condition.Operator.TRUE, condition.values));
                    }
                }
                return new Expression(property, not, condition);
            }

        }

        // Criteria next expression builder.
        private static class NextExpressionBuilder extends ExpressionBuilder {

            private final NextExpression.Operator operator;

            // Creates an instance of expression builder.
            private NextExpressionBuilder(final NextExpression.Operator operator, final String parameter) {
                super(parameter);
                this.operator = operator;
            }

            private NextExpression build() {
                return new NextExpression(operator, property, not, condition);
            }

        }

        // Parent class builder where all parts are put together.
        private final DynamicFinder.Builder builder;
        // Query criteria first expression.
        private Expression first;
        // Query criteria expressions following the first one.
        private final List<NextExpression> next;
        // Criteria expression builder.
        // Contains nextExpressionBuilder instance after first expression is finished.
        private ExpressionBuilder expressionBuilder;
        // Criteria next expression builder.
        private NextExpressionBuilder nextExpressionBuilder;

        // Creqates an instanceof query criteria builder.
        Builder(final DynamicFinder.Builder builder) {
            this.builder = builder;
            this.first = null;
            this.next = new LinkedList<>();
            this.expressionBuilder = null;
            this.nextExpressionBuilder = null;
        }

        /**
         * Build Helidon dynamic finder query criteria.
         *
         * @param property criteria expression property: Entity property name
         */
        BuilderNext by(final String property) {
            expressionBuilder = new ExpressionBuilder(property);
            return this;
        }

        /**
         * Build Helidon dynamic finder query criteria.
         * This is a shortcut to add default {@link Expression.Condition.Operator.EQUALS} condition
         * for provided property.
         *
         * @param property criteria expression parameter: Entity property name
         * @param conditionValue condition property value: used in {@code setParameter(property, conditionValue)} call.
         */
        BuilderBy by(final String property, final String conditionValue) {
            expressionBuilder = new ExpressionBuilder(property);
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.EQUALS,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderBy not() {
            expressionBuilder.not();
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator after(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.AFTER,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator before(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.BEFORE,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator contains(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.CONTAINS,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator starts(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.STARTS,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator ends(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.ENDS,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator eq(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.EQUALS,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator gt(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.GREATER_THAN,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator gte(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.GREATER_THAN_EQUALS,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator lt(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.LESS_THAN,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator lte(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.LESS_THAN_EQUALS,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator like(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.LIKE,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator iLike(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.ILIKE,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator in(final String conditionValue) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.IN,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator between(final String conditionFrom, final String conditionTo) {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.BETWEEN,
                    Collections.unmodifiableList(Arrays.asList(conditionFrom, conditionTo))
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator isNull() {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.NULL,
                    Collections.emptyList()
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator empty() {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.EMPTY,
                    Collections.emptyList()
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator isTrue() {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.TRUE,
                    Collections.emptyList()
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNextOperator isFalse() {
            expressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.FALSE,
                    Collections.emptyList()
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderNext and(final String property) {
            finishCurrentBuilder();
            expressionBuilder = nextExpressionBuilder = new NextExpressionBuilder(
                    NextExpression.Operator.AND, property);
            return this;
        }

        public DynamicFinderCriteria.BuilderNext or(final String property) {
            finishCurrentBuilder();
            expressionBuilder = nextExpressionBuilder = new NextExpressionBuilder(
                    NextExpression.Operator.OR, property);
            return this;
        }

        public DynamicFinderCriteria.BuilderBy and(final String property, final String conditionValue) {
            finishCurrentBuilder();
            nextExpressionBuilder = new NextExpressionBuilder(
                    NextExpression.Operator.AND, property);
            nextExpressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.EQUALS,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        public DynamicFinderCriteria.BuilderBy or(final String property, final String conditionValue) {
            finishCurrentBuilder();
            nextExpressionBuilder = new NextExpressionBuilder(
                    NextExpression.Operator.OR, property);
            nextExpressionBuilder.condition(new Expression.Condition(
                    Expression.Condition.Operator.EQUALS,
                    Collections.singletonList(conditionValue)
            ));
            return this;
        }

        /**
         * Build Helidon dynamic finder query order.
         *
         * @param property criteria expression parameter: Entity property name
         */
        public DynamicFinderOrder.Builder orderBy(final String property) {
            // Finalize criteria first.
            finishCurrentBuilder();
            expressionBuilder = nextExpressionBuilder = null;
            builder.setCriteria(new DynamicFinderCriteria(first, next));
            return new DynamicFinderOrder.Builder(builder).orderBy(property);
        }

        public DynamicFinder build() {
            // Finalize criteria first.
            finishCurrentBuilder();
            expressionBuilder = nextExpressionBuilder = null;
            builder.setCriteria(new DynamicFinderCriteria(first, next));
            // Return finished AST.
            return builder.build();
        }

        private void finishCurrentBuilder() {
            if (first == null) {
                first = expressionBuilder.build();
            } else {
                next.add(nextExpressionBuilder.build());
            }
        }

    }

    /**
     * Helidon dynamic finder query criteria builder: token after {@code By<property>}.
     * Reduces offered method calls to allowed set.
     * Condition is always required. Equal should be used as default.
     */
    public interface BuilderBy extends BuilderByNot, Negation, BuilderNextOperator {
    }

    /**
     * Helidon dynamic finder query criteria builder: token after {@code By<property>Not}.
     * Reduces offered method calls to allowed set.
     */
    public interface BuilderByNot {
        DynamicFinderCriteria.BuilderNextOperator after(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator before(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator contains(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator starts(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator ends(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator eq(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator gt(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator gte(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator lt(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator lte(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator like(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator iLike(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator in(final String conditionValue);
        DynamicFinderCriteria.BuilderNextOperator between(final String conditionFrom, final String conditionTo);
        DynamicFinderCriteria.BuilderNextOperator isNull();
        DynamicFinderCriteria.BuilderNextOperator empty();
        DynamicFinderCriteria.BuilderNextOperator isTrue();
        DynamicFinderCriteria.BuilderNextOperator isFalse();
    }

    /**
     * Helidon dynamic finder query criteria builder: token after {@code By<property>Not<condition>}.
     * Reduces offered method calls to allowed set.
     */
    public interface BuilderNextOperator extends Buildable {
        DynamicFinderCriteria.BuilderNext and(final String parameter);
        DynamicFinderCriteria.BuilderBy and(final String property, final String conditionValue);
        DynamicFinderCriteria.BuilderNext or(final String parameter);
        DynamicFinderCriteria.BuilderBy or(final String property, final String conditionValue);
    }

    /**
     * Helidon dynamic finder query criteria builder: token after {@code And<property> || Or<property>}.
     * Reduces offered method calls to allowed set.
     * Condition is always required. Equal should be used as default.
     */
    public interface BuilderNext extends BuilderByNot, Negation {
    }

    /**
     * Helidon dynamic finder query criteria builder: marks current interface (builder stage) as buildable.
     */
    public interface Buildable {

        DynamicFinderOrder.Builder orderBy(final String property);
        DynamicFinder build();
    }

    /**
     * Helidon dynamic finder query criteria builder: allows condition negation to interface (builder stage).
     */
    public interface Negation {
        BuilderBy not();
    }
}
