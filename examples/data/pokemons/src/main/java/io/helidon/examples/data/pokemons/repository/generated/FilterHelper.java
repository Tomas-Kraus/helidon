package io.helidon.examples.data.pokemons.repository.generated;

import java.util.ArrayList;
import java.util.List;

import io.helidon.data.runtime.DynamicFinderCriteria;

// Those static helpers should be moved to data Runtime module or even do DynamicFinder AST classes as factory methods
public class FilterHelper {

    /**
     * Builds list of expressions from condition lists for entity parameters.
     *
     * @param conditionLists condition lists for entity parameters
     * @return list of expressions
     */
    @SafeVarargs
    public static List<DynamicFinderCriteria.Expression> buildExpressions(List<DynamicFinderCriteria.Condition>... conditionLists) {
        List<DynamicFinderCriteria.Expression> expressions = new ArrayList<>(conditionLists.length);
        for (List<DynamicFinderCriteria.Condition> conditionList : conditionLists) {
            expressions.add(expressionFromList(conditionList));
        }
        return expressions;
    }

    public static DynamicFinderCriteria.Compound buildCompound(
            List<DynamicFinderCriteria.Expression> expressions,
            DynamicFinderCriteria.Compound.NextExpression.Operator operator) {
        return DynamicFinderCriteria.Compound.build(
                expressions.get(0),
                buildNextExpressionList(expressions, operator));
    }

    // Builds next expression list starting from 2nd item in the list.
    private static List<DynamicFinderCriteria.Compound.NextExpression> buildNextExpressionList(
            List<? extends DynamicFinderCriteria.Expression> list,
            DynamicFinderCriteria.Compound.NextExpression.Operator operator) {
        List<DynamicFinderCriteria.Compound.NextExpression> nextList = new ArrayList<>(list.size() - 1);
        boolean first = true;
        for (DynamicFinderCriteria.Expression expression : list) {
            // Skip 1st item in the list
            if (first) {
                first = false;
            } else {
                nextList.add(DynamicFinderCriteria.Compound.buildExpression(operator, expression));
            }
        }
        return nextList;
    }

    // Returns null when provided list is empty.
    private static DynamicFinderCriteria.Expression expressionFromList(List<DynamicFinderCriteria.Condition> list) {
        if (list.isEmpty()) {
            return null;
        }
        // Only one condition exists, return it as an expression.
        if (list.size() == 1) {
            return list.get(0);
            // Build compound expression from more than one expression.
        } else {
            return DynamicFinderCriteria.Compound.build(
                    list.get(0),
                    buildNextExpressionList(list, DynamicFinderCriteria.Compound.NextExpression.Operator.OR));
        }
    }

}
