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
package io.helidon.data.builder.query.transform;

/*
 * Whole AST walk through process is written as recursive descend to keep good
 * human readability of the code.
 */

import io.helidon.data.builder.query.DynamicFinder;
import io.helidon.data.builder.query.DynamicFinderCriteria;
import io.helidon.data.builder.query.DynamicFinderSelection;

import java.util.List;

/**
 * Run the AST to target query String transformation process.
 */
public class TransformQuery {

    private final DynamicFinder model;
    private final QueryTransformation transformation;

    /**
     * Creates an instance of the AST to target query String transformation process.
     *
     * @param model Helidon dynamic finder query AST
     */
    public TransformQuery(DynamicFinder model) {
        this.model = model;
        this.transformation = QueryTransformManager.provider().newInstance();
    }

    /**
     * Return target query language statement.
     *
     * @return statement build from AST content.
     */
    public String statement() {
        return transformation.statement();
    }

    /**
     * Return list of query settings to be applied.
     *
     * @return query settings build from AST content
     */
    public List<String> querySettings() {
        return transformation.querySettings();
    }

    /**
     * Do the transformation process.
     * Descend trough whole AST and call events.
     * TODO: whole mapping should be passed when available
     * @param entityName name of the entity
     */
    public void transform(final String entityName) {
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

    private void transformCriteria() {
        model.criteria().ifPresent(
                criteria -> {
                    transformation.startCriteria(criteria);
                    // First expression is always present.
                    transformation.startFirstCriteriaExpression(criteria.first());
                    final DynamicFinderCriteria.Expression first =  criteria.first();
                    transformation.firstCriteriaExpressionProperty(first.property());
                    transformation.firstCriteriaExpressionNot(first.not());
                    transformation.startFirstCriteriaExpressionCondition(first.condition());
                    transformation.firstCriteriaExpressionConditionOperator(first.condition().operator());
                    first.condition().values().forEach(
                            transformation::firstCriteriaExpressionConditionValue);
                    transformation.finishFirstCriteriaExpressionCondition(first.condition());
                    transformation.finishFirstCriteriaExpression(criteria.first());
                    criteria.next().forEach(
                            next -> {
                                transformation.startNextCriteriaExpression(next);
                                transformation.nextCriteriaExpressionOperator(next.operator());
                                transformation.nextCriteriaExpressionProperty(next.property());
                                transformation.nextCriteriaExpressionNot(next.not());
                                transformation.startNextCriteriaExpressionCondition(next.condition());
                                transformation.nextCriteriaExpressionConditionOperator(next.condition().operator());
                                next.condition().values().forEach(
                                        transformation::nextCriteriaExpressionConditionValue);
                                transformation.finishNextCriteriaExpressionCondition(next.condition());
                                transformation.finishNextCriteriaExpression(next);
                            }
                    );
                    transformation.finishCriteria(criteria);
                });
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
