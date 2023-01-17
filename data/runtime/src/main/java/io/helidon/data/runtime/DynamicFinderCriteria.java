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
package io.helidon.data.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class DynamicFinderCriteria {

    /**
     * Query criteria expression.
     * Expression is a logical expression syntax tree where internal node represents single criteria
     * or multiple expressions joined by logical operator.
     */
    public interface Expression {

        enum Type {
            /** Simple criteria expression. */
            CONDITION,
            /** Compound expression. */
            COMPOUND
        }


        /**
         * Type of the expression.
         * Get {@link Type} enumeration value to detect current implementation of expression.
         *
         * @return {@link Type} of the condition expression
         */
        Type type();

        <T extends Expression> T as(Class<T> cls);

        /**
         * Add property prefix to the whole expression tree.
         *
         * @param prefix property prefix to add
         * @param expression expression to be modified
         * @return new modified expression
         */
        static DynamicFinderCriteria.Expression propertyPrefix(String prefix, DynamicFinderCriteria.Expression expression) {
            switch (expression.type()) {
            case CONDITION -> {
                DynamicFinderCriteria.Condition src = expression.as(DynamicFinderCriteria.Condition.class);
                return DynamicFinderCriteria.Condition.build(
                        src.not(),
                        new StringBuilder(prefix.length() + src.property().length() + 1)
                                .append(prefix)
                                .append('.')
                                .append(src.property())
                                .toString(),
                        src.operator(),
                        src.values());
            }
            case COMPOUND -> {
                DynamicFinderCriteria.Compound src = expression.as(DynamicFinderCriteria.Compound.class);
                List<DynamicFinderCriteria.Compound.NextExpression> nextList = new ArrayList<>(src.next().size());
                src.next().forEach(
                        next -> nextList.add(
                                DynamicFinderCriteria.Compound.NextExpression.builder()
                                        .operator(next.operator())
                                        .expression(propertyPrefix(prefix, next.expression()))
                                        .build()));
                return DynamicFinderCriteria.Compound.builder()
                        .first(propertyPrefix(prefix, src.first()))
                        .next(nextList)
                        .build();
            }
            default -> throw new IllegalStateException(String.format("Unknown expression type %s", expression.type().name()));
            }
        }

    }

    /**
     * Compound expression.
     * Two or more expressions joined by logical operators.
     */
    public static class Compound implements Expression {

        /**
         * Compound expression element following joining logical operator.
         * Joining logical operator is included.
         */
        public interface NextExpression extends Expression {

            /**
             * Expression logical operator.
             * Used to connect with previous expression in the query criteria.
             */
            enum Operator {
                /** Logical operator {@code And}. */
                AND("And"),
                /** Logical operator {@code Or}. */
                OR("Or");

                /** Expression logical operators enumeration length. */
                public static final int LENGTH = values().length;

                // Logical operator keyword
                private final String keyword;

                // Creates an instance of criteria expression joining logical operator.
                Operator(String keyword) {
                    this.keyword = keyword;
                }

                public String keyword() {
                    return keyword;
                }

            }

            /**
             * Expression logical operator.
             *
             * @return logical operator to connect with previous expression in the query criteria.
             */
            Operator operator();

            /**
             * Expression joined by logical operator.
             *
             * @return expression on the right side of the logical operator.
             */
            Expression expression();

            static Builder builder() {
                return new Builder();
            }

            class Builder implements io.helidon.common.Builder<Builder, NextExpression> {

                private NextExpression.Operator operator;

                private Expression expression;

                private Builder() {
                    this.operator = null;
                    this.expression = null;
                }

                public Builder operator(NextExpression.Operator operator) {
                    Objects.requireNonNull(operator, "Expression operator shall not be null.");
                    this.operator = operator;
                    return this;
                }

                public Builder expression(Expression expression) {
                    Objects.requireNonNull(expression, "Expression shall not be null.");
                    this.expression = expression;
                    return this;
                }

                public NextExpression build() {
                    return Compound.buildExpression(operator, expression);
                }
            }

        }

        // Internal implementation of Compound expression element following joining logical operator.
        private static final class NextExpressionImpl implements NextExpression {

            private final Operator operator;

            private final Expression expression;

            private NextExpressionImpl(Operator operator, Expression expression) {
                Objects.requireNonNull(expression, "Expression shall not be null.");
                Objects.requireNonNull(operator, "Expression operator shall not be null.");
                this.operator = operator;
                this.expression = expression;
            }

            @Override
            public Type type() {
                return expression.type();
            }

            @Override
            public <T extends Expression> T as(Class<T> cls) {
                if (cls.isInstance(NextExpression.class)) {
                    return cls.cast(this);
                }
                return expression.as(cls);
            }

            @Override
            public Operator operator() {
                return operator;
            }

            @Override
            public Expression expression() {
                return expression;
            }

        }

        public static NextExpression buildExpression(NextExpression.Operator operator, Expression expression) {
            Objects.requireNonNull(expression, "Expression shall not be null.");
            Objects.requireNonNull(operator, "Expression operator shall not be null.");
            return new NextExpressionImpl(operator, expression);
        }

        public static Compound build(Expression first, List<NextExpression> next) {
            Objects.requireNonNull(first, "First expression shall not be null.");
            Objects.requireNonNull(next, "Next expression list shall not be null.");
            return new Compound(first, next);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder implements io.helidon.common.Builder<Builder, Compound> {

            private Expression first;

            private List<Compound.NextExpression> next;

            private Builder() {
                this.first = null;
                this.next = null;
            }

            public Builder first(Expression expression) {
                Objects.requireNonNull(expression, "Expression shall not be null.");
                if (first != null) {
                    throw new IllegalStateException("First expression was already set.");
                }
                this.first = expression;
                return this;
            }

            public Builder next(List<Compound.NextExpression> next) {
                Objects.requireNonNull(next, "Expression list shall not be null.");
                if (first == null) {
                    throw new IllegalStateException("No first expression was set.");
                }
                this.next = next;
                return this;
            }

            public Compound build() {
                if (first == null) {
                    throw new IllegalStateException("No first expression was set.");
                }
                if (next == null) {
                    throw new IllegalStateException("No next expression list was set.");
                }
                if (next.isEmpty()) {
                    throw new IllegalStateException("No next expression was set.");
                }
                return Compound.build(first, List.copyOf(next));
            }

        }

        // First expression (with no logical operator).
        private final Expression first;
        // Next expression following joining logical operator.
        private final List<NextExpression> next;

        private Compound(Expression first, List<NextExpression> next) {
            this.first = first;
            this.next = next;
        }

        @Override
        public Expression.Type type() {
            return Type.COMPOUND;
        }

        @Override
        public <T extends Expression> T as(Class<T> cls) {
            if (cls != Compound.class) {
                throw new IllegalArgumentException(String.format("Class %s is not supported", cls.getSimpleName()));
            }
            return cls.cast(this);
        }

        /**
         * First expression (with no logical operator) of the compound expression.
         *
         * @return first expression of the compound expression
         */
        public Expression first() {
            return first;
        }

        /**
         * Next expression (following joining logical operator) of the compound expression.
         *
         * @return list of next expressions with preceding logical operators
         */
        public List<NextExpression> next() {
            return next;
        }


    }

    /**
     * Expression as condition.
     * Leaf node of the expression syntax tree.
     */
    public static class Condition implements Expression {

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

            /** Condition operators enumeration length. */
            static final int LENGTH = values().length;

            private static final Map<String, Operator> KEYWORD_TO_OPERATOR_MAP = initKeywordToOperatorMap();

            private static final String[] ALL_KEYWORDS_IN_DESCENDING_LENGTH = initAllKeywordsInDescendingLength();

            // Build keyword to operator Map
            private static Map<String, Operator> initKeywordToOperatorMap() {
                Map<String, Operator> map = new HashMap<>();
                for (Operator operator : values()) {
                    for (String kw : operator.keywords) {
                        map.put(kw, operator);
                    }
                }
                return map;
            }

            // Build an array of all operators keywords sorted by descending length
            private static String[] initAllKeywordsInDescendingLength() {
                Set<String> kwSet = KEYWORD_TO_OPERATOR_MAP.keySet();
                @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
                String[] keywords = kwSet.toArray(new String[kwSet.size()]);
                Arrays.sort(keywords, Comparator.comparingInt(String::length).reversed());
                return keywords;
            }

            /**
             * Get operator for provided keyword.
             *
             * @return operator for provided keyword or {@code null} if keyword is not known.
             */
            public static Operator kwToOperator(String kw) {
                return KEYWORD_TO_OPERATOR_MAP.get(kw);
            }

            /**
             * Return an array of all {@link Operator}s keywords sorted by descending length.
             *
             * @return all operator keywords (sorted by descending length).
             */
            public static String[] allKeywordsInDescLength() {
                return ALL_KEYWORDS_IN_DESCENDING_LENGTH;
            }

            // Number of operator parameters
            private final int paramCount;
            // Supported operator keywords
            private final String[] keywords;

            // Creates an instance of condition operators
            Operator(int paramCount, String... keywords) {
                if (keywords == null || keywords.length == 0) {
                    throw new IllegalArgumentException("At least one keyword is required!");
                }
                this.paramCount = paramCount;
                this.keywords = keywords;
            }

            // TODO: Remove public access to keywords and paramCount methods
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

        /**
         * Condition parameter.
         *
         * @param <T> class of the value
         */
        public static abstract class Parameter<T> {

            /**
             * Condition parameter type.
             * This enumeration is used to distinguish all existing implementations of condition parameter.
             */
            public enum Type {
                /**
                 * Parameter is specified as direct valie.
                 */
                VALUE,
                /**
                 * Parameter is specified as method argument reference.
                 */
                ARGUMENT
            }

            /**
             * Type of the condition parameter.
             * Get {@link Type} enumeration value to detect current implementation of condition parameter.
             *
             * @return {@link Type} of the condition parameter
             */
            public abstract Type type();

            /**
             * Condition parameter as direct value.
             *
             * @param <T> class of the value
             */
            public static class Value<T> extends Parameter<T> {

                public static <T> Value<T> build(Class<T> valueClass, T value) {
                    return new Value<>(valueClass, value);
                }

                // Value of the parameter
                private final T value;
                // Class of the parameter
                private final Class<T> valueClass;

                private Value(Class<T> valueClass, T value) {
                    Objects.requireNonNull(value, "Value of the parameter is null");
                    Objects.requireNonNull(valueClass, "Class of the parameter is null");
                    this.value = value;
                    this.valueClass = valueClass;
                }

                @Override
                public Type type() {
                    return Type.VALUE;
                }

                public T value() {
                    return value;
                }

            }

            /**
             * Condition parameter as method argument reference.
             *
             * @param <T> class of the method argument value
             */
            public static class Argument<T> extends Parameter<T> {

                public static <T> Argument<T> build(Class<T> valueClass, String name) {
                    return new Argument<>(valueClass, name);
                }

                // Name of the method argument
                private final String name;
                // Class of the parameter
                private final Class<T> valueClass;

                private Argument(Class<T> valueClass, String name) {
                    Objects.requireNonNull(name, "Name of the method argument is null");
                    Objects.requireNonNull(valueClass, "Class of the parameter is null");
                    this.name = name;
                    this.valueClass = valueClass;
                }

                @Override
                public Type type() {
                    return Type.ARGUMENT;
                }

                public String name() {
                    return name;
                }

                public Class<T> valueClass() {
                    return valueClass;
                }

            }

        }

        /**
         * Creates an instance of criteria expression condition.
         *
         * @param not whether the condition is negated
         * @param property name of the entity property in the condition
         * @param operator condition operator
         * @param values condition values
         * @return new instance of criteria expression condition
         */
        public static Condition build(boolean not, String property, Operator operator, Parameter<?>... values) {
            Objects.requireNonNull(property, "Condition property shall not be null.");
            Objects.requireNonNull(operator, "Condition operator shall not be null.");
            Objects.requireNonNull(values, "Condition values array shall not be null.");
            int paramCount = values != null ? values.length : 0;
            if (paramCount != operator.paramCount()) {
                throw new IllegalArgumentException(String.format("Number of parameters must be %d for operator %s.", operator.paramCount(), operator.name()));
            }
            return new Condition(
                    not,
                    property,
                    operator,
                    (values != null && values.length > 0) ? Arrays.asList(values) : Collections.emptyList());
        }

        /**
         * Creates an instance of criteria expression condition.
         *
         * @param not whether the condition is negated
         * @param property name of the entity property in the condition
         * @param operator condition operator
         * @param values condition values
         * @return new instance of criteria expression condition
         */
        public static Condition build(boolean not, String property, Operator operator, List<? extends Parameter<?>> values) {
            Objects.requireNonNull(property, "Condition property shall not be null.");
            Objects.requireNonNull(operator, "Condition operator shall not be null.");
            Objects.requireNonNull(values, "Condition values list shall not be null.");
            int paramCount = values.size();
            if (paramCount != operator.paramCount()) {
                throw new IllegalArgumentException(String.format("Number of parameters must be %d for operator %s.", operator.paramCount(), operator.name()));
            }
            return new Condition(not, property, operator, List.copyOf(values));
        }

        /**
         * Creates an instance of criteria expression condition.
         * Condition won't be negated.
         *
         * @param property name of the entity property in the condition
         * @param operator condition operator
         * @param values condition values
         * @return new instance of criteria expression condition
         */
        public static Condition build(String property, Operator operator, Parameter<?>... values) {
            return build(false, property, operator, values);
        }

        // Condition property.
        private final String property;
        // Condition operator.
        private final Operator operator;
        // Condition values: dynamic finder query method parameters names assigned to condition.
        private final List<? extends Parameter<?>> values;
        // Whether condition is negated.
        private final boolean not;

        // Creates an instance of expression condition with possible negation.
        private Condition(boolean not, String property, Operator operator, List<? extends Parameter<?>> values) {
            this.property = property;
            this.operator = operator;
            this.values = values;
            this.not = not;
        }

        @Override
        public Expression.Type type() {
            return Type.CONDITION;
        }

        @Override
        public <T extends Expression> T as(Class<T> cls) {
            if (cls != Condition.class) {
                throw new IllegalArgumentException(String.format("Class %s is not supported", cls.getSimpleName()));
            }
            return cls.cast(this);
        }

        /**
         * Condition property.
         *
         * @return name of the property
         */
        public String property() {
            return property;
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
        public List<? extends Parameter<?>> values() {
            return values;
        }

        /**
         * Negated condition.
         *
         * @return value of {@code true} when condition is negated or {@code false} otherwise
         */
        public boolean not() {
            return not;
        }

    }

    public static DynamicFinderCriteria build(Expression expression) {
        return new DynamicFinderCriteria(expression);
    }

    // Rood node of criteria expression
    private final Expression expression;

    // Creates an instance of criteria part of the Helidon dynamic finder query.
    private DynamicFinderCriteria(Expression expression) {
        Objects.requireNonNull(expression, "Criteria expression shall not be null.");
        this.expression = expression;
    }

    public Expression expression() {
        return expression;
    }

}
