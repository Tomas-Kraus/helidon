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

import java.util.Optional;

/**
 * Helidon dynamic finder query.
 * Dynamic finder query is query based on finder method name. Name of such method consists of substrings
 * matching defined query builder rules: <ul>
 *
 * <li>{@code query ::= <selection> <criteria> [<order>]                                          }</li>
 * <li>{@code selection ::= <method> [<projection>]                                               }</li>
 * <li>{@code method ::= get | find                                                               }</li>
 * <li>{@code projection::= Count | CountDistinct | Distinct | Max | Min | Sum | Avg | <top>      }</li>
 * <li>{@code top ::= Top <number>                                                                }</li>
 * <li>{@code criteria ::= By <expression> { <operator> <expression> }                            }</li>
 * <li>{@code operator ::= And | Or                                                               }</li>
 * <li>{@code expression ::=  <property> [ Not | IsNot ] <condition>                                }
 * <li>{@code condition ::= After | Before | Contains | StartsWith | StartingWith                 }
 *     {@code               | EndsWith | EndingWith | Equals | Equal                              }
 *     {@code               | GreaterThan | GreaterThanEqual | GreaterThanEquals | LessThan       }
 *     {@code               | LessThanEqual | LessThanEquals | Like | Ilike | In | InList         }
 *     {@code               | Between | InRange | IsNull | Null | IsEmpty | Empty                 }
 *     {@code               | True | IsTrue | False | IsFalse                                     }</li>
 * <li>{@code order ::= OrderBy <property> [ Asc | Desc ] { And <property> [ Asc | Desc ] }</li>
 * <li>{@code property ::= } name of entity property</li>
 * <li>{@code number ::= } decimal number</li>
 * </ul>
 */
public class DynamicFinder {

    // Selection part of dynamic finder query.
    private final DynamicFinderSelection selection;
    // Criteria part of dynamic finder query.
    private final Optional<DynamicFinderCriteria> criteria;
    // Order part of dynamic finder query.
    private final Optional<DynamicFinderOrder> order;

    /**
     * Creates an instance of dynamic finder query.
     *
     * @param selection selection part of dynamic finder query
     * @param criteria criteria part of dynamic finder query
     */
    private DynamicFinder(final DynamicFinderSelection selection, final Optional<DynamicFinderCriteria> criteria, final Optional<DynamicFinderOrder> order) {
        this.selection = selection;
        this.criteria = criteria;
        this.order = order;
    }

    /**
     * Query selection.
     *
     * @return selection part of the dynamic finder query
     */
    public DynamicFinderSelection selection() {
        return selection;
    }

    /**
     * Query order.
     *
     * @return order part of the dynamic finder query
     */
    public Optional<DynamicFinderOrder> order() {
        return order;
    }

    /**
     * Query criteria.
     *
     * @return criteria part of the dynamic finder query
     */
    public Optional<DynamicFinderCriteria> criteria() {
        return criteria;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Helidon dynamic finder query builder.
     */
    public static class Builder {

        // Selection part of dynamic finder query.
        private DynamicFinderSelection selection;
        // Criteria part of dynamic finder query.
        private DynamicFinderCriteria criteria;
        // Criteria part of dynamic finder query.
        private DynamicFinderOrder order;


        private Builder() {
            this.criteria = null;
            this.selection = null;
            this.order = null;
        }

        /**
         * Select dynamic finder query with single result.
         *
         * @return builder with single result query
         */
        public DynamicFinderSelection.Builder get() {
            return new DynamicFinderSelection.Builder(this).get();
        }

        /**
         * Select dynamic finder query with multiple results.
         *
         * @return builder with multiple results query
         */
        public DynamicFinderSelection.Builder find() {
            return new DynamicFinderSelection.Builder(this).find();
        }

        /**
         * Internal: Build Helidon dynamic finder query.
         *
         * @return new instance of Helidon dynamic finder query.
         */
        DynamicFinder build() {
            return new DynamicFinder(
                    selection,
                    criteria != null ? Optional.of(criteria) : Optional.empty(),
                    order != null ? Optional.of(order) : Optional.empty()
            );
        }

        /**
         * Internal: Setter for selection from {@link DynamicFinderSelection.Builder}.
         *
         * @param selection selection part of dynamic finder query
         */
        void setSelection(final DynamicFinderSelection selection) {
            this.selection = selection;
        }

        /**
         * Internal: Setter for criteria from {@link DynamicFinderCriteria.Builder}.
         *
         * @param criteria selection part of dynamic finder query
         */
        void setCriteria(final DynamicFinderCriteria criteria) {
            this.criteria = criteria;
        }

        /**
         * Internal: Setter for order from {@link DynamicFinderOrder.Builder}.
         *
         * @param order order part of dynamic finder query
         */
        void setOrder(final DynamicFinderOrder order) {
            this.order = order;
        }

    }

}
