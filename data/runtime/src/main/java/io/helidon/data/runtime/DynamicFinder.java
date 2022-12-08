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

import java.util.Optional;

/**
 * Helidon dynamic finder query abstract syntax tree.
 * Dynamic finder query is query based on finder method name. Name of such method consists of substrings
 * matching defined query builder rules: <ul>
 *
 * <li>{@code query ::= <selection> [<criteria>] [<order>]                                        }</li>
 * <li>{@code selection ::= <method> [<projection>] [ <property> ]                                }</li>
 * <li>{@code method ::= get | find                                                               }</li>
 * <li>{@code projection::= Count | CountDistinct | Distinct | Max | Min | Sum | Avg | <top>      }</li>
 * <li>{@code top ::= Top<number>                                                                 }</li>
 * <li>{@code criteria ::= By <expression> { <operator> <expression> }                            }</li>
 * <li>{@code operator ::= And | Or                                                               }</li>
 * <li>{@code expression ::=  <property> [ Not | IsNot ] <condition>                              }
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

    public static DynamicFinder build(DynamicFinderSelection selection, Optional<DynamicFinderCriteria> criteria, Optional<DynamicFinderOrder> order) {
        return new DynamicFinder(selection, criteria, order);
    }

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
    private DynamicFinder(DynamicFinderSelection selection, Optional<DynamicFinderCriteria> criteria, Optional<DynamicFinderOrder> order) {
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

}
