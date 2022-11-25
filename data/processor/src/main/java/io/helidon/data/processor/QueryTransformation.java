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

import io.helidon.data.processor.DynamicFinderCriteria;
import io.helidon.data.processor.DynamicFinderOrder;
import io.helidon.data.processor.DynamicFinderSelection;

import java.util.List;

/**
 * Query Transformation API.
 * This interface defines dynamic finder query AST to target query language transformation.
 */
public interface QueryTransformation {

    /**
     * Return target query language statement.
     *
     * @return statement build from AST content
     */
    String statement();

    /**
     * Return list of query settings to be applied.
     *
     * @return query settings build from AST content
     */
    List<String> querySettings();

    /**
     * Initial event of the transformation.
     * This method is invoked once before the whole process starts.
     */
    default void start(String entityName) {
    };

    /**
     * Final event of the transformation.
     * This method is invoked once after the whole process is finished.
     */
    default void finish() {
    };

    /**
     * Initial event of the selection part of the Helidon dynamic finder query transformation.
     * Provides access to the whole selection AST subtree.
     *
     * @param selection selection AST subtree
     */
    default void startSelection(DynamicFinderSelection selection) {
    }

    /**
     * Optional event with selection property.
     * This method is called only when specific selection property was set in the AST.
     *
     * @param selectionPproperty specific selection property
     */
    default void selectionProperty(String selectionPproperty) {
    }

    /**
     * Event with selection method.
     * Known methods are {@code get} for single result and {@code find} for multiple results
     * to be returned by the query.
     *
     * @param selectionMethod selection method set, see {@link DynamicFinderSelection.Method}
     */
    default void selectionMethod(DynamicFinderSelection.Method selectionMethod) {
    }

    /**
     * Optional event with selection projection.
     * This method is called only when specific projection was set in the AST.
     *
     * @param projection projection definition of the selection, see {@link DynamicFinderSelection.Projection}
     */
    default void selectionProjection(DynamicFinderSelection.Projection projection) {
    }

    /**
     * Optional event with optional parameter of the selection projection method.
     * This method is called only when specific projection was set in the AST
     * and projection method has an argument, see {@link DynamicFinderSelection.Projection.Method}.
     *
     * @param parameter  optional parameter of the selection projection method
     */
    default void selectionProjectionMethodParameter(Object parameter) {
    }

    /**
     * Final event of selection part of the Helidon dynamic finder query transformation.
     * Provides access to the whole selection AST subtree.
     *
     * @param selection selection AST subtree
     */
    default void finishSelection(DynamicFinderSelection selection) {
    }

    /**
     * Initial optional event of the criteria part of the Helidon dynamic finder query transformation.
     * Provides access to the whole criteria AST subtree.
     * Whole criteria AST subtree is optional.
     *
     * @param criteria criteria AST subtree
     */
    default void startCriteria(DynamicFinderCriteria criteria) {
    }

    /**
     * Optional initial event of criteria condition.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param condition criteria condition
     */
    default void startCriteriaCondition(DynamicFinderCriteria.Condition condition) {
    }

    /**
     * Optional event of criteria condition negation.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param not criteria condition negation. Value of {@code true} when condition
     *            is negated or {@code false} otherwise
     */
    default void criteriaConditionNot(boolean not) {
    }

    /**
     * Optional event of criteria condition property.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param property criteria condition property
     */
    default void criteriaConditionProperty(String property) {
    }

    /**
     * Optional event of criteria condition operator.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param operator criteria condition operator
     */
    default void criteriaConditionOperator(DynamicFinderCriteria.Condition.Operator operator) {
    }

    /**
     * Optional event of criteria condition parameter as direct value.
     * This method is called always when optional criteria AST subtree exists.
     * Multiple condition values may exist.
     *
     * @param value criteria condition parameter as direct value
     */
    default void criteriaConditionValue(DynamicFinderCriteria.Condition.Parameter.Value<?> value) {
    }

    /**
     * Optional event of criteria condition parameter as method argument reference.
     * This method is called always when optional criteria AST subtree exists.
     * Multiple condition values may exist.
     *
     * @param argument criteria condition parameter as method argument reference
     */
    default void criteriaConditionArgument(DynamicFinderCriteria.Condition.Parameter.Argument<?> argument) {
    }

    /**
     * Optional final event of criteria condition.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param condition criteria condition
     */
    default void finishCriteriaCondition(DynamicFinderCriteria.Condition condition) {
    }

    /**
     * Optional initial event of criteria compound expression.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param expression criteria compound expression
     */
    default void startCriteriaCompoundExpression(DynamicFinderCriteria.Compound expression) {
    }

    /**
     * Optional initial event of first criteria compound expression item.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param expression first item of criteria compound expression
     */
    default void startFirstCriteriaCompoundExpression(DynamicFinderCriteria.Expression expression) {
    }

    /**
     * Optional final event of first criteria compound expression item.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param expression first item of criteria compound expression
     */
    default void finishFirstCriteriaCompoundExpression(DynamicFinderCriteria.Expression expression) {
    }

    /**
     * Optional initial event of next criteria compound expression item.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param expression next item of criteria compound expression
     */
    default void startNextCriteriaCompoundExpression(DynamicFinderCriteria.Compound.NextExpression expression) {
    }

    /**
     * Optional final event of next criteria compound expression item.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param expression next item of criteria compound expression
     */
    default void finishNextCriteriaCompoundExpression(DynamicFinderCriteria.Compound.NextExpression expression) {
    }

    /**
     * Optional event of next criteria compound expression joining logical operator.
     * This event is called between two joined expressions processing.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param operator compound expression joining logical operator
     */
    default void criteriaCompoundExpressionOperator(DynamicFinderCriteria.Compound.NextExpression.Operator operator) {
    }

    /**
     * Optional final event of criteria compound expression.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param expression criteria compound expression
     */
    default void finishCriteriaCompoundExpression(DynamicFinderCriteria.Compound expression) {
    }

    /**
     * Final optional event of the criteria part of the Helidon dynamic finder query transformation.
     * Provides access to the whole criteria AST subtree.
     * Whole criteria AST subtree is optional.
     *
     * @param criteria criteria AST subtree
     */
    default void finishCriteria(DynamicFinderCriteria criteria) {
    }

    /**
     * Initial optional event of the order part of the Helidon dynamic finder query transformation.
     * Provides access to the whole order AST subtree.
     * Whole order AST subtree is optional.
     *
     * @param order order AST subtree
     */
    default void startOrder(DynamicFinderOrder order) {
    }

    /**
     * Initial optional event of the order rule of the Helidon dynamic finder query transformation.
     * Whole order AST subtree is optional.
     *
     * @param rule ordering rule
     */
    default void startOrderRule(DynamicFinderOrder.Order rule) {
    }

    /**
     * Optional event of the order rule property of the Helidon dynamic finder query transformation.
     * Whole order AST subtree is optional.
     *
     * @param property ordering rule property
     */
    default void orderRuleProperty(String property) {
    }

    /**
     * Optional event of the order rule method of the Helidon dynamic finder query transformation.
     * Whole order AST subtree is optional.
     *
     * @param method ordering rule method, see {@link DynamicFinderOrder.Order.Method}
     */
    default void orderRuleMethod(DynamicFinderOrder.Order.Method method) {
    }

    /**
     * Final optional event of the order rule of the Helidon dynamic finder query transformation.
     * Whole order AST subtree is optional.
     *
     * @param rule ordering rule
     */
    default void finishOrderRule(DynamicFinderOrder.Order rule) {
    }

    /**
     * Final optional event of the order part of the Helidon dynamic finder query transformation.
     * Provides access to the whole order AST subtree.
     * Whole order AST subtree is optional.
     *
     * @param order order AST subtree
     */
    default void finishOrder(DynamicFinderOrder order) {
    }

}
