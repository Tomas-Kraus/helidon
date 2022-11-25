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

/*
 * Whole AST walk through process is written as recursive descend to keep good
 * human readability of the code.
 */

import java.util.List;
import java.util.function.Consumer;

/**
 * Run the AST to target query String transformation process.
 */
class TransformQuery implements DynamicFinderStatement {

    private final DynamicFinder model;
    private final QueryTransformation transformation;

    /**
     * Creates an instance of the AST to target query String transformation process.
     *
     * @param model Helidon dynamic finder query AST
     */
    TransformQuery(DynamicFinder model) {
        this.model = model;
        this.transformation = QueryTransformManager.provider().newInstance();
    }

    /**
     * Return target query language statement.
     *
     * @return statement build from AST content.
     */
    @Override
    public String statement() {
        return transformation.statement();
    }

    /**
     * Return list of query settings to be applied.
     *
     * @return query settings build from AST content
     */
    @Override
    public List<String> querySettings() {
        return transformation.querySettings();
    }

    /**
     * Do the transformation process.
     * Descend trough whole AST and call events.
     * TODO: whole mapping should be passed when available
     * @param entityName name of the entity
     */
    void transform(String entityName) {
        transformation.start(entityName);
        transformSelect();
        transformCriteria();
        transformOrder();
        transformation.finish();
    }

    // This traverse order is optimized for SQL languages family.
    // If other implementations would require different traverse, an interface may be added to allow
    // implementation to specify own overrides.

    // Descend trough selection subtree of the AST and call selection related events.
    private void transformSelect() {
        final DynamicFinderSelection selection = model.selection();
        transformation.startSelection(selection);
        selection.property().ifPresent(
                transformation::selectionProperty);
        transformation.selectionMethod(selection.method());
        selection.projection().ifPresent(
                projection -> {
                    transformation.selectionProjection(projection);
                    projection.parameter().ifPresent(
                            transformation::selectionProjectionMethodParameter);
                });
        transformation.finishSelection(selection);
    }

    // Descend trough criteria subtree of the AST and call criteria related events.
    private void transformCriteria() {
        model.criteria().ifPresent(
                criteria -> {
                    transformation.startCriteria(criteria);
                    transformExpression(criteria.expression());
                    transformation.finishCriteria(criteria);
                });
    }

    // Descend trough criteria expression subtree.
    private void transformExpression(DynamicFinderCriteria.Expression expression) {
        switch (expression.type()) {
            // Leaf node of the expression subtree
            case CONDITION -> {
                DynamicFinderCriteria.Condition condition = expression.as(DynamicFinderCriteria.Condition.class);
                transformation.startCriteriaCondition(condition);
                transformation.criteriaConditionNot(condition.not());
                transformation.criteriaConditionProperty(condition.property());
                transformation.criteriaConditionOperator(condition.operator());
                condition.values().forEach(
                        value -> transformCriteriaExpressionConditionParameter(
                                value,
                                transformation::criteriaConditionValue,
                                transformation::criteriaConditionArgument
                        ));
                transformation.finishCriteriaCondition(condition);
            }
            case COMPOUND -> {
                DynamicFinderCriteria.Compound compound = expression.as(DynamicFinderCriteria.Compound.class);
                transformation.startCriteriaCompoundExpression(compound);
                transformation.startFirstCriteriaCompoundExpression(compound.first());
                transformExpression(compound.first());
                transformation.finishFirstCriteriaCompoundExpression(compound.first());
                compound.next().forEach(
                        next -> {
                            transformation.criteriaCompoundExpressionOperator(next.operator());
                            transformation.startNextCriteriaCompoundExpression(next);
                            transformExpression(next);
                            transformation.finishNextCriteriaCompoundExpression(next);
                        });
                transformation.finishCriteriaCompoundExpression(compound);
            }
        }
    }

    // Call criteria expression condition parameter handler for proper child class of the parameter
    private static void transformCriteriaExpressionConditionParameter(
            DynamicFinderCriteria.Condition.Parameter<?> parameter,
            Consumer<DynamicFinderCriteria.Condition.Parameter.Value<?>> valueAction,
            Consumer<DynamicFinderCriteria.Condition.Parameter.Argument<?>> argumentAction
    ) {
        switch (parameter.type()) {
        case VALUE -> valueAction.accept(
                (DynamicFinderCriteria.Condition.Parameter.Value<?>) parameter);
        case ARGUMENT -> argumentAction.accept(
                (DynamicFinderCriteria.Condition.Parameter.Argument<?>) parameter);
        }
    }

    private void transformOrder() {
        model.order().ifPresent(
                order -> {
                    transformation.startOrder(order);
                    order.orders().forEach(
                            rule -> {
                                transformation.startOrderRule(rule);
                                transformation.orderRuleProperty(rule.property());
                                transformation.orderRuleMethod(rule.method());
                                transformation.finishOrderRule(rule);
                            }
                    );
                    transformation.finishOrder(order);
                }
        );
    }

}
