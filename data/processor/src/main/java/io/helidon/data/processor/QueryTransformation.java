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
     * and projection method has an argument, see {@link DynamicFinderSelection.Projection.Method.TOP}
     * as an example of such method.
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
     * Optional initial event of first criteria expression.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param expression first criteria expression
     */
    default void startFirstCriteriaExpression(DynamicFinderCriteria.Expression expression) {
    }

    /**
     * Optional event of first criteria expression property.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param property first criteria expression property
     */
    default void firstCriteriaExpressionProperty(String property) {
    }

    /**
     * Optional event of first criteria expression condition negation.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param not value of {@code true} when criteria should be negated or {@code false} otherwise
     */
    default void firstCriteriaExpressionNot(boolean not) {
    }

    /**
     * Optional event of first criteria expression condition.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param condition first criteria expression condition
     */
    default void startFirstCriteriaExpressionCondition(DynamicFinderCriteria.Expression.Condition condition) {
    }

    /**
     * Optional event of first criteria expression condition operator.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param operator first criteria expression condition operator
     */
    default void firstCriteriaExpressionConditionOperator(DynamicFinderCriteria.Expression.Condition.Operator operator) {
    }

    /**
     * Optional event of first criteria expression condition value.
     * This method is called always when optional criteria AST subtree exists.
     * Multiple condition values may exist.
     *
     * @param value first criteria expression condition value
     */
    default void firstCriteriaExpressionConditionValue(String value) {
    }

    /**
     * Optional event of first criteria expression condition.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param condition first criteria expression condition
     */
    default void finishFirstCriteriaExpressionCondition(DynamicFinderCriteria.Expression.Condition condition) {
    }

    /**
     * Optional final event of first criteria expression.
     * This method is called always when optional criteria AST subtree exists.
     *
     * @param expression first criteria expression
     */
    default void finishFirstCriteriaExpression(DynamicFinderCriteria.Expression expression) {
    }

    /**
     * Optional initial event of next criteria expression.
     * This method is called when optional criteria AST subtree exists for each next expression.
     *
     * @param expression first criteria expression
     */
    default void startNextCriteriaExpression(DynamicFinderCriteria.Expression expression) {
    }

    /**
     * Optional event of next criteria expression logical operator.
     * This method is called when optional criteria AST subtree exists for each next expression.
     *
     * @param operator first criteria expression property
     */
    default void nextCriteriaExpressionOperator(DynamicFinderCriteria.NextExpression.Operator operator) {
    }

    /**
     * Optional event of next criteria expression property.
     * This method is called when optional criteria AST subtree exists for each next expression.
     *
     * @param property first criteria expression property
     */
    default void nextCriteriaExpressionProperty(String property) {
    }

    /**
     * Optional event of next criteria expression condition negation.
     * This method is called when optional criteria AST subtree exists for each next expression.
     *
     * @param not value of {@code true} when criteria should be negated or {@code false} otherwise
     */
    default void nextCriteriaExpressionNot(boolean not) {
    }

    /**
     * Optional event of next criteria expression condition.
     * This method is called when optional criteria AST subtree exists for each next expression.
     *
     * @param condition first criteria expression condition
     */
    default void startNextCriteriaExpressionCondition(DynamicFinderCriteria.Expression.Condition condition) {
    }

    /**
     * Optional event of next criteria expression condition operator.
     * This method is called when optional criteria AST subtree exists for each next expression.
     *
     * @param operator first criteria expression condition operator
     */
    default void nextCriteriaExpressionConditionOperator(DynamicFinderCriteria.Expression.Condition.Operator operator) {
    }

    /**
     * Optional event of next criteria expression condition value.
     * This method is called when optional criteria AST subtree exists for each next expression.
     * Multiple condition values may exist.
     *
     * @param value first criteria expression condition value
     */
    default void nextCriteriaExpressionConditionValue(String value) {
    }

    /**
     * Optional event of next criteria expression condition.
     * This method is called when optional criteria AST subtree exists for each next expression.
     *
     * @param condition first criteria expression condition
     */
    default void finishNextCriteriaExpressionCondition(DynamicFinderCriteria.Expression.Condition condition) {
    }

    /**
     * Optional final event of next criteria expression.
     * This method is called when optional criteria AST subtree exists for each next expression.
     *
     * @param expression first criteria expression
     */
    default void finishNextCriteriaExpression(DynamicFinderCriteria.Expression expression) {
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
