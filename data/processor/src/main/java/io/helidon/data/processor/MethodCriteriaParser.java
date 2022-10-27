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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Data repository query method parser of the criteria part of the method name.
 */
class MethodCriteriaParser extends MethodAbstractParser {

    private static ParserState createFinalCriteriaPropertiesState() {
        ParserState state = new ParserState(ParserTransition.EmptyTransition.getInstance());
        state.setFinalState(ParserState.FinalState.CRITERIA_PROPERTY);
        return state;
    }

    private static ParserState createFinalCriteriaOperatorState() {
        ParserState state = new ParserState(ParserTransition.EmptyTransition.getInstance());
        state.setFinalState(ParserState.FinalState.CRITERIA_OPERATOR);
        return state;
    }

    // Adds OrderBy, And, Or keywords to provided final parser state.
    // TODO/PERF: Currently full graph of OrderBy/And/Or keywords is being added to each of the final states.
    //            this may be optimized to add full graph just once and initial states for 'O' and 'A' connecting
    //            other states to this full graph.
    private static void buildFinalParserStates(
            ParserState root,
            ParserTransition.Action finalAndAction,
            ParserTransition.Action finalOrAction) {
        ParserState state = root.addTransition(DynamicFinder.ORDER_BY_KEYWORD.charAt(0));
        // Add OrderBy
        for (int i = 1; i < DynamicFinder.ORDER_BY_KEYWORD.length() - 1; i++) {
            state = state.addTransition(DynamicFinder.ORDER_BY_KEYWORD.charAt(i));
        }
        if (!state.transition().isEmpty()) {
            throw new MethodParserException("Keyword OrderBy is already present in state machine.");
        }
        ParserState finalState = new ParserState(ParserTransition.EmptyTransition.getInstance());
        finalState.setFinalState(ParserState.FinalState.ORDER_BY);
        state.setTransition(
                new ParserTransition.SingleTransition(
                        DynamicFinder.ORDER_BY_KEYWORD.charAt(DynamicFinder.ORDER_BY_KEYWORD.length() - 1), finalState));
        // Add And joining logical operator
        String andKw = DynamicFinderCriteria.NextExpression.Operator.AND.keyword();
        state = root.addTransition(andKw.charAt(0));
        for (int i = 1; i < andKw.length() - 1; i++) {
            state = state.addTransition(andKw.charAt(i));
        }
        if (!state.transition().isEmpty()) {
            throw new MethodParserException("Keyword And is already present in state machine.");
        }
        ParserState finalAndOrState = new ParserState(ParserTransition.EmptyTransition.getInstance());
        finalAndOrState.setFinalState(ParserState.FinalState.CRITERIA_JOIN);
        ParserTransition transition = new ParserTransition.SingleTransition(andKw.charAt(andKw.length() - 1), finalAndOrState);
        transition.setAction(andKw.charAt(andKw.length() - 1), finalAndAction);
        state.setTransition(transition);
        // Add Or joining logical operator
        String orKw = DynamicFinderCriteria.NextExpression.Operator.OR.keyword();
        state = root.addTransition(orKw.charAt(0));
        for (int i = 1; i < orKw.length() - 1; i++) {
            state = state.addTransition(orKw.charAt(i));
        }
        // Or is prefix of already existing OrderBy keyword so final state must be set
        // in the middle of existing state machine
        ParserState finalOrState = state.addTransition(orKw.charAt(orKw.length() - 1));
        state.transition().setAction(orKw.charAt(orKw.length() - 1), finalOrAction);
        finalOrState.setFinalState(ParserState.FinalState.CRITERIA_JOIN);
    }

    private static String[] createMethodArguments(List<String> methodArguments) {
        if (methodArguments == null) {
            return new String[0];
        }
        String[] arguments = new String[methodArguments.size()];
        int i = 0;
        for (String argument : methodArguments) {
            arguments[i++] = argument;
        }
        return arguments;
    }

    // Confition negation operator keyword: Not
    private static final String NOT_KEYWORD = "Not";
    // Confition negation operator keyword: IsNot
    private static final String IS_NOT_KEYWORD = "IsNot";

    // Prepend Not and IsNot keywords states to existing condition operators parser state machine.
    private static ParserState buildNotParserStates(ParserState root, MethodCriteriaParser parser) {
        ParserState newRoot = createRootCriteriaNotState();
        // Add IsNot and Not keywords
        for (String keyword : new String[]{IS_NOT_KEYWORD, NOT_KEYWORD}) {
            ParserState state = newRoot.addTransition(keyword.charAt(0));
            for (int i = 1; i < keyword.length() - 1; i++) {
                state = state.addTransition(keyword.charAt(i));
            }
            ParserTransition transition = new ParserTransition.SingleTransition(keyword.charAt(keyword.length() - 1), root);
            transition.setAction(keyword.charAt(keyword.length() - 1), parser::notCriteriaCondition);
            state.setTransition(transition);
        }
        // Now all condition operators keywords must be joined from new root state
        // because Not and IsNot keywords are optional.
        keywords:
        for (String keyword : DynamicFinderCriteria.Expression.Condition.Operator.allKeywordsInDescLength()) {
            int i = 0;
            ParserState newLast = newRoot;
            ParserState newNext = newRoot.transition().next(keyword.charAt(i));
            ParserState last = root;
            ParserState next = root.transition().next(keyword.charAt(i));
            if (newNext == next) {
                continue;
            }
            while(newNext != null) {
                i++;
                newLast = newNext;
                newNext = newNext.transition().next(keyword.charAt(i));
                last = next;
                next = next.transition().next(keyword.charAt(i));
                if (newNext == next) {
                    continue keywords;
                }
            }
            ParserTransition transition = newLast.transition().add(keyword.charAt(i), next);
            // Action must be copied with transition too.
            Optional<ParserTransition.Action> action = last.transition().action(keyword.charAt(i));
            if (action.isPresent()) {
                transition.setAction(keyword.charAt(i), action.get());
            }
            newLast.setTransition(transition);
        }
        // Copy all root state actions to new root state.
        // TODO: Make this dirty hacking piece of code look better
        ((ParserTransition.MultiTransition)newRoot.transition()).copyActions(
                (ParserTransition.MultiTransition) root.transition());
        return newRoot;
    }

    private static ParserState createRootCriteriaNotState() {
        return new ParserState(ParserTransition.EmptyTransition.getInstance());
    }

    private DynamicFinderSelection.Builder builder;
    private DynamicFinderCriteria.Builder criteriaBuilder;
    // Root (starting) node of the criteria properties parser.
    private final ParserState propertiesRoot;
    // Root (starting) node of the criteria operator parser.
    private final ParserState operatorRoot;
    // Array of method arguments in the same order as in method prototype.
    private String[] methodArguments;
    // Current argument position;
    private int argumentPos;
    private int firstPropertyChar;
    private String property;
    private int firstOperatorChar;
    boolean notOperator;
    private DynamicFinderCriteria.Expression.Condition.Operator operator;
    private DynamicFinderCriteria.NextExpression.Operator joinOperator;

    MethodCriteriaParser(List<String> entityProperties) {
        this.propertiesRoot = buildStateMachineFromSortedList(
                entityProperties, MethodCriteriaParser::createFinalCriteriaPropertiesState,
                ParserState.FinalState.CRITERIA_PROPERTY, this::firstPropertyChar, this::lastPropertyChar,
                null, (root) -> buildFinalParserStates(root, this::propertyAndOperator, this::propertyOrOperator));
        ParserState conditionPperators = buildStateMachineFromSortedList(
                Arrays.asList(DynamicFinderCriteria.Expression.Condition.Operator.allKeywordsInDescLength()),
                MethodCriteriaParser::createFinalCriteriaOperatorState,
                ParserState.FinalState.CRITERIA_OPERATOR, this::firstOperatorChar, this::lastOperatorChar,
                null, (root) -> buildFinalParserStates(root, this::criteriaAndOperator, this::criteriaOrOperator));
        this.operatorRoot = buildNotParserStates(conditionPperators, this);
        this.methodArguments = null;
        this.argumentPos = 0;
        this.builder = null;
        this.criteriaBuilder = null;
        this.firstPropertyChar = 0;
        this.property = null;
        this.firstOperatorChar = 0;
        this.notOperator = false;
        this.operator = null;
        this.joinOperator = null;
    }

    // Reset parser to be used for another method name parsing
    void reset() {
        this.methodArguments = null;
        this.argumentPos = 0;
        this.builder = null;
        this.criteriaBuilder = null;
        this.firstPropertyChar = 0;
        this.property = null;
        this.firstOperatorChar = 0;
        this.notOperator = false;
        this.operator = null;
        this.joinOperator = null;
    }

    void methodArguments(List<String> methodArguments) {
        this.methodArguments = createMethodArguments(methodArguments);
        this.argumentPos = 0;
    }

    DynamicFinderCriteria.Builder criteriaBuilder() {
        return criteriaBuilder;
    }

    ParserState.FinalState parse(DynamicFinderSelection.Builder builder, ParserContext context, List<String> methodArguments) {
        // Set selection builder instance retrieved from selection part parser.
        this.builder = builder;
        // Set method arguments to be consumed by parser.
        methodArguments(methodArguments);
        // Parse criteria property (mandatory)
        ParserState.FinalState finalState = executeStateMachineWithStepBack(context, propertiesRoot,
                Set.of(ParserState.FinalState.CRITERIA_PROPERTY, ParserState.FinalState.CRITERIA_JOIN, ParserState.FinalState.ORDER_BY),
                this::finalPropertyState, () -> criteriaBuilder.build(), true,
                "Unknown criteria property %s found at position %d when parsing %s.",
                "Illegal final state %s when parsing criteria property");
        switch(finalState) {
            case END, ORDER_BY -> {
                return finalState;
            }
        }
        if (finalState != ParserState.FinalState.CRITERIA_JOIN) {
            // Parse criteria operator
            finalState = executeStateMachine(context, operatorRoot,
                    Set.of(ParserState.FinalState.CRITERIA_OPERATOR, ParserState.FinalState.CRITERIA_JOIN, ParserState.FinalState.ORDER_BY),
                    this::finalOperatorState, () -> criteriaBuilder.build(), true,
                    "Unknown criteria operator %s found at position %d when parsing %s.",
                    "Illegal final state %s when parsing criteria operator");
            if (finalState == ParserState.FinalState.END) {
                return finalState;
            }
        }
        while(finalState == ParserState.FinalState.CRITERIA_JOIN) {
            // Parse criteria property (mandatory)
            finalState = executeStateMachineWithStepBack(context, propertiesRoot,
                    Set.of(ParserState.FinalState.CRITERIA_PROPERTY, ParserState.FinalState.CRITERIA_JOIN, ParserState.FinalState.ORDER_BY),
                    this::finalPropertyState, () -> criteriaBuilder.build(), true,
                    "Unknown criteria property %s found at position %d when parsing %s.",
                    "Illegal final state %s when parsing criteria property");
            switch(finalState) {
                case END, ORDER_BY -> {
                    return finalState;
                }
            }
            if (finalState != ParserState.FinalState.CRITERIA_JOIN) {
                // Parse criteria operator
                finalState = executeStateMachine(context, operatorRoot,
                        Set.of(ParserState.FinalState.CRITERIA_OPERATOR, ParserState.FinalState.CRITERIA_JOIN, ParserState.FinalState.ORDER_BY),
                        this::finalOperatorState, () -> criteriaBuilder.build(), true,
                        "Unknown criteria operator %s found at position %d when parsing %s.",
                        "Illegal final state %s when parsing criteria operator");
                if (finalState == ParserState.FinalState.END) {
                    return finalState;
                }
            }
        }

        return finalState;
    }

    // Final property parsing state action
    void finalPropertyState(ParserContext ctx) {
        if (!ctx.hasNext()) {
            buildProperty(ctx);
        }
    }

    // Internal property builder call
    // - as final property parsing state action (no more characters in input)
    // - as final expression parsing state action before logical operator announcing next expression
    // - TODO: as final expression parsing state action before OrderBy keyword
    void buildProperty(ParserContext ctx) {
        if (argumentPos >= methodArguments.length) {
            throw new MethodParserException(String.format(
                    "Missing method argument for argument property %s at position %d when parsing %s.",
                    property, ctx.pos(), ctx.text()));
        }
        if (joinOperator == null) {
            criteriaBuilder = (DynamicFinderCriteria.Builder) builder
                    .by(property, methodArguments[argumentPos++]);
        } else {
            switch (joinOperator) {
                case AND -> criteriaBuilder.and(property, methodArguments[argumentPos++]);
                case OR -> criteriaBuilder.or(property, methodArguments[argumentPos++]);
            }
        }
    }

    // Final property parsing state action
    void finalOperatorState(ParserContext ctx) {
        if (!ctx.hasNext()) {
            buildOperatorState(ctx);
        }
    }
    void buildOperatorState(ParserContext ctx) {
        if (joinOperator == null) {
            criteriaBuilder = (DynamicFinderCriteria.Builder) builder.by(property);
        } else {
            switch (joinOperator) {
                case AND -> criteriaBuilder.and(property);
                case OR -> criteriaBuilder.or(property);
            }
        }
        if (notOperator) {
            criteriaBuilder.not();
        }
        switch (operator) {
            case AFTER -> criteriaBuilder.after(methodArguments[argumentPos++]);
            case BEFORE -> criteriaBuilder.before(methodArguments[argumentPos++]);
            case CONTAINS -> criteriaBuilder.contains(methodArguments[argumentPos++]);
            case STARTS -> criteriaBuilder.starts(methodArguments[argumentPos++]);
            case ENDS -> criteriaBuilder.ends(methodArguments[argumentPos++]);
            case EQUALS -> criteriaBuilder.eq(methodArguments[argumentPos++]);
            case GREATER_THAN -> criteriaBuilder.gt(methodArguments[argumentPos++]);
            case GREATER_THAN_EQUALS -> criteriaBuilder.gte(methodArguments[argumentPos++]);
            case LESS_THAN -> criteriaBuilder.lt(methodArguments[argumentPos++]);
            case LESS_THAN_EQUALS -> criteriaBuilder.lte(methodArguments[argumentPos++]);
            case LIKE -> criteriaBuilder.like(methodArguments[argumentPos++]);
            case ILIKE -> criteriaBuilder.iLike(methodArguments[argumentPos++]);
            case IN -> criteriaBuilder.in(methodArguments[argumentPos++]);
            case BETWEEN -> criteriaBuilder.between(methodArguments[argumentPos++], methodArguments[argumentPos++]);
            case NULL -> criteriaBuilder.isNull();
            case EMPTY -> criteriaBuilder.empty();
            case TRUE -> criteriaBuilder.isTrue();
            case FALSE -> criteriaBuilder.isFalse();
        }
    }

    // Store position of the first character of the property.
    void firstPropertyChar(ParserContext ctx) {
        firstPropertyChar = ctx.pos();
    }

    // Build property name from parsed text. First letter must be turned to lowercase.
    void lastPropertyChar(ParserContext ctx) {
        StringBuilder property = new StringBuilder(ctx.pos() - firstPropertyChar);
        property.append(Character.toLowerCase(ctx.character(firstPropertyChar)));
        property.append(ctx.parsedText(firstPropertyChar + 1));
        this.property = property.toString();
    }

    // Store position of the first character of the operator.
    void firstOperatorChar(ParserContext ctx) {
        firstOperatorChar = ctx.pos();
    }

    // Build operator name from parsed text and resolve enumeration value.
    void lastOperatorChar(ParserContext ctx) {
        final String keyword = ctx.parsedText(firstOperatorChar);
        operator = DynamicFinderCriteria.Expression.Condition.Operator.kwToOperator(keyword);
        if (operator == null) {
            throw new IllegalStateException(String.format(
                    "Keyword %s does not match any known criteria operator at position %d when parsing %s.",
                    keyword, ctx.pos(), ctx.text()));
        }
    }

    // Finish property build and set joining logical operator to AND
    void propertyAndOperator(ParserContext ctx) {
        buildProperty(ctx);
        joinOperator = DynamicFinderCriteria.NextExpression.Operator.AND;
    }

    // Finish property build and set joining logical operator to OR
    void propertyOrOperator(ParserContext ctx) {
        buildProperty(ctx);
        joinOperator = DynamicFinderCriteria.NextExpression.Operator.OR;
    }

    // Set joining logical operator to AND
    void criteriaAndOperator(ParserContext ctx) {
        buildOperatorState(ctx);
        joinOperator = DynamicFinderCriteria.NextExpression.Operator.AND;
    }

    // Set joining logical operator to OR
    void criteriaOrOperator(ParserContext ctx) {
        buildOperatorState(ctx);
        joinOperator = DynamicFinderCriteria.NextExpression.Operator.OR;
    }

    // Set the negation of criteria condition operator to true
    void notCriteriaCondition(ParserContext ctx) {
        notOperator = true;
    }

}
