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

import io.helidon.data.processor.QueryTransformation;
import io.helidon.data.runtime.DynamicFinderCriteria;
import io.helidon.data.runtime.DynamicFinderOrder;
import io.helidon.data.runtime.DynamicFinderSelection;
import io.helidon.data.runtime.DynamicFinderSelection.Projection;

import java.util.LinkedList;
import java.util.List;

/**
 * Query Transformation API.
 * This interface defines dynamic finder query AST to target query language transformation.
 */
public class JpaQueryTransformation implements QueryTransformation {

    // Target query language statement.
    private String statement;
    // Target query settings calls.
    private final List<String> querySettings;
    private final StringBuilder stmtBuilder;

    private String entityName;
    private char entityAlias;
    private DynamicFinderSelection.Projection projection;
    private Object projectionMethodParameter;
    // Target selection projection statement substring
    private String projectionStmt;
    private String criteriaExpressionProperty;
    private boolean criteriaExpressionNot;
    private DynamicFinderCriteria.Condition.Operator criteriaExpressionOperator;
    private int criteriaExpressionValueIndex;
    private boolean firstOrderRule;

    public JpaQueryTransformation() {
        this.stmtBuilder = new StringBuilder(128);
        this.querySettings = new LinkedList<>();
        this.projection = null;
        this.projectionMethodParameter = null;
        this.entityName = null;
        this.entityAlias = '\0';
        this.statement = null;
        this.projectionStmt = null;
        this.criteriaExpressionProperty = null;
        this.criteriaExpressionNot = false;
        this.criteriaExpressionOperator = null;
        this.criteriaExpressionValueIndex = 0;
        this.firstOrderRule = true;
    }

    @Override
    public String statement() {
        return statement;
    }

    @Override
    public List<String> querySettings() {
        return querySettings;
    }

    @Override
    public void start(String entityName) {
        // Store name of the entity
        this.entityName = entityName;
        // Build entity alias as first character of the entity nane. FQN is allowed here too.
        final int pos = entityName.lastIndexOf('.');
        if (pos < 0) {
            entityAlias = Character.toLowerCase(entityName.charAt(0));
        } else {
            if (entityName.length() < pos + 2) {
                throw new IllegalArgumentException("Entity fully qualified name ends with '.'");
            }
            entityAlias = Character.toLowerCase(entityName.charAt(pos + 1));
        }
    }

    @Override
    public void finish() {
        statement = stmtBuilder.toString();
    }

    @Override
    public void startSelection(DynamicFinderSelection selection) {
        stmtBuilder.append("SELECT");
        projectionStmt = "%s";
    }

    // First event to happen in selection part.
    @Override
    public void selectionProperty(String selectionPproperty) {
        // Append property name to projection argument
        projectionStmt = String.format("%%s.%s", selectionPproperty);
    }


    @Override
    public void selectionMethod(DynamicFinderSelection.Method selectionMethod) {
    }

    @Override
    public void selectionProjection(DynamicFinderSelection.Projection projection) {
        this.projection = projection;
        switch (projection.method()) {
            case COUNT -> projectionStmt = String.format("COUNT(%s)", projectionStmt);
            case COUNT_DISTINCT -> projectionStmt = String.format("COUNT(DISTINCT %s)", projectionStmt);
            case DISTINCT -> projectionStmt = String.format("DISTINCT %s", projectionStmt);
            case MAX -> projectionStmt = String.format("MAX(%s)", projectionStmt);
            case MIN -> projectionStmt = String.format("MIN(%s)", projectionStmt);
            case SUM -> projectionStmt = String.format("SUM(%s)", projectionStmt);
            case AVG -> projectionStmt = String.format("AVG(%s)", projectionStmt);
        }
    }

    @Override
    public void selectionProjectionMethodParameter(Object parameter) {
        projectionMethodParameter = parameter;
    }

    @Override
    public void finishSelection(DynamicFinderSelection selection) {
        stmtBuilder.append(' ');
        stmtBuilder.append(String.format(projectionStmt, entityAlias));
        stmtBuilder.append(String.format(" FROM %s %s", entityName, entityAlias));
        // TopNNN projection limits number of returned records. JPA supports this
        // by setting setMaxResults(Integer) on query.
        if (projection != null && projection.method() == DynamicFinderSelection.Projection.Method.TOP) {
            if (Projection.TOP_PARAM_CLASS.isInstance(projectionMethodParameter)) {
                final int limit = Projection.TOP_PARAM_CLASS.cast(projectionMethodParameter);
                querySettings.add(String.format("setMaxResults(%d)", limit));
            } else {
                // This should be never executed, but let's keep it here for future bugs.
                throw new IllegalStateException("Projection Top method parameter is not Integer.");
            }
        }
    }

    @Override
    public void startCriteria(DynamicFinderCriteria criteria) {
        stmtBuilder.append(" WHERE ");
    }

    @Override
    public void startCriteriaCondition(DynamicFinderCriteria.Condition condition) {
        // Internal condition values indexing, starts from 0.
        criteriaExpressionValueIndex = 0;
    }

    @Override
    public void criteriaConditionNot(boolean not) {
        criteriaExpressionNot = not;
    }

    @Override
    public void criteriaConditionProperty(String property) {
        criteriaExpressionProperty = String.format("%s.%s", entityAlias, property);
    }

    @Override
    public void criteriaConditionOperator(DynamicFinderCriteria.Condition.Operator operator) {
        // Left side of the condition
        switch(operator) {
        // Build left side of the condition for operators without values
        case NULL, EMPTY, TRUE, FALSE -> stmtBuilder.append(criteriaExpressionProperty);
        // Store operator for values processing for operators which have values
        default -> criteriaExpressionOperator = operator;
        }
        // Right side of the condition for operators without values
        switch(operator) {
        case NULL -> stmtBuilder.append(criteriaExpressionNot ? " IS NOT NULL" : " IS NULL");
        // This works for collections in JPA.
        // TODO: There is also an option to write query for Strings as NULL OR '' check.
        case EMPTY -> stmtBuilder.append(criteriaExpressionNot ? " IS NOT EMPTY" : " IS EMPTY");
        // Negation should be removed by optimization, but let's keep both options in the code.
        case TRUE -> stmtBuilder.append(criteriaExpressionNot ? " <> TRUE" : " = TRUE");
        // Negation should be removed by optimization, but let's keep both options in the code.
        case FALSE -> stmtBuilder.append(criteriaExpressionNot ? " <> FALSE" : " = FALSE");
        }
    }

    @Override
    public void criteriaConditionArgument(DynamicFinderCriteria.Condition.Parameter.Argument<?> argument) {
        // Verify values limit first. This condition won't pass only because of serious bug in the code.
        if (criteriaExpressionValueIndex > criteriaExpressionOperator.paramCount() - 1) {
            throw new IllegalStateException(String.format("Condition values limit exceeded for method %s.", criteriaExpressionOperator.keywords()[0]));
        }
        // Left side of the condition
        switch (criteriaExpressionOperator) {
        case ILIKE -> stmtBuilder.append("LOWER(")
                .append(criteriaExpressionProperty)
                .append(")");
        // JPQL BETWEEN has 2 values. Left side of the condition is built with 1st one.
        case BETWEEN -> {
            if (criteriaExpressionValueIndex == 0) {
                stmtBuilder.append(criteriaExpressionProperty);
            }
        }
        default -> stmtBuilder.append(criteriaExpressionProperty);
        }
        // Condition operator
        switch(criteriaExpressionOperator) {
        case AFTER -> stmtBuilder.append(criteriaExpressionNot
                                                 ? " <= "  // Negated AFTER
                                                 : " > "); // Regular AFTER
        case BEFORE -> stmtBuilder.append(criteriaExpressionNot
                                                  ? " >= "  // Negated BEFORE
                                                  : " < "); // Regular BEFORE
        case CONTAINS, STARTS, ENDS, LIKE, ILIKE -> stmtBuilder.append(criteriaExpressionNot
                                                                               ? " NOT LIKE " // Negated CONTAINS, STARTS, ENDS, LIKE, ILIKE
                                                                               : " LIKE ");   // Regular CONTAINS, STARTS, ENDS, LIKE, ILIKE
        case EQUALS -> stmtBuilder.append(criteriaExpressionNot
                                                  ? " <> "  // Negated EQUALS
                                                  : " = "); // Regular EQUALS
        case GREATER_THAN -> stmtBuilder.append(criteriaExpressionNot
                                                        ? " <= "  // Negated GREATER_THAN
                                                        : " > "); // Regular GREATER_THAN
        case GREATER_THAN_EQUALS -> stmtBuilder.append(criteriaExpressionNot
                                                               ? " < "    // Negated GREATER_THAN_EQUALS
                                                               : " >= "); // Regular GREATER_THAN_EQUALS
        case LESS_THAN -> stmtBuilder.append(criteriaExpressionNot
                                                     ? " >= "  // Negated LESS_THAN
                                                     : " < "); // Regular LESS_THAN
        case LESS_THAN_EQUALS -> stmtBuilder.append(criteriaExpressionNot
                                                            ? " > "    // Negated LESS_THAN_EQUALS
                                                            : " <= "); // Regular LESS_THAN_EQUALS
        case IN -> stmtBuilder.append(criteriaExpressionNot
                                              ? " NOT IN " // Negated IN
                                              : " IN ");   // Regular IN
        // JPQL BETWEEN has 2 values. Operator part of the condition depends on value order.
        case BETWEEN -> {
            switch (criteriaExpressionValueIndex) {
            case 0 -> stmtBuilder.append(criteriaExpressionNot
                                                 ? " NOT BETWEEN " // Negated BETWEEN
                                                 : " BETWEEN ");   // Regular BETWEEN
            case 1 -> stmtBuilder.append(" AND ");
            }
        }
        }
        // Right side of the condition
        String value = argument.name();
        switch(criteriaExpressionOperator) {
        case AFTER, BEFORE, EQUALS,
                GREATER_THAN, GREATER_THAN_EQUALS,
                LESS_THAN, LESS_THAN_EQUALS,
                LIKE, IN, BETWEEN -> stmtBuilder.append(":")
                .append(value);
        case CONTAINS -> stmtBuilder.append("CONCAT('%', :")
                .append(value)
                .append(", '%')");
        case STARTS -> stmtBuilder.append("CONCAT(:")
                .append(value)
                .append(", '%')");
        case ENDS -> stmtBuilder.append("CONCAT('%', :")
                .append(value)
                .append(")");
        case ILIKE -> stmtBuilder.append("LOWER(:")
                .append(value)
                .append(")");
        }
        querySettings.add(String.format("setParameter(\"%s\", %s)", value, value));
        criteriaExpressionValueIndex++;
    }

    @Override
    public void startFirstCriteriaCompoundExpression(DynamicFinderCriteria.Expression expression) {
        // Enclose multiple values in brackets when multiple expressions exist
        stmtBuilder.append("(");
    }

    @Override
    public void startNextCriteriaCompoundExpression(DynamicFinderCriteria.Compound.NextExpression expression) {
        // Enclose multiple values in brackets when multiple expressions exist
        stmtBuilder.append("(");
    }

    @Override
    public void finishFirstCriteriaCompoundExpression(DynamicFinderCriteria.Expression expression) {
        // Enclose multiple values in brackets when multiple expressions exist
        stmtBuilder.append(")");
    }

    @Override
    public void finishNextCriteriaCompoundExpression(DynamicFinderCriteria.Compound.NextExpression expression) {
        // Enclose multiple values in brackets when multiple expressions exist
        stmtBuilder.append(")");
    }

    // Opening bracket delayed to next expression logical operator, which is mandatory
    @Override
    public void criteriaCompoundExpressionOperator(DynamicFinderCriteria.Compound.NextExpression.Operator operator) {
        switch(operator) {
        case AND -> stmtBuilder.append(" AND ");
        case OR -> stmtBuilder.append(" OR ");
        }
    }
    @Override
    public void startOrder(DynamicFinderOrder order) {
        stmtBuilder.append(" ORDER BY");
        firstOrderRule = true;
    }

    @Override
    public void startOrderRule(DynamicFinderOrder.Order rule) {
        if (firstOrderRule) {
            firstOrderRule = false;
        } else {
            stmtBuilder.append(",");
        }
    }

    @Override
    public void orderRuleProperty(String property) {
        stmtBuilder.append(" ");
        stmtBuilder.append(property);
    }

    @Override
    public void orderRuleMethod(DynamicFinderOrder.Order.Method method) {
        // Ascending order is default in SQL/JPQL so only adds descending order explicitly.
        if (method == DynamicFinderOrder.Order.Method.DESC) {
            stmtBuilder.append(" DESC");
        }
    }

}
