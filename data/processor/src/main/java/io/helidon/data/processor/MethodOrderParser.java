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

import java.util.List;
import java.util.Set;

/**
 * Data repository query method parser of the order part of the method name.
 */
class MethodOrderParser extends MethodAbstractParser {

    @FunctionalInterface
    private interface OrderBy {
        DynamicFinderOrder.Builder orderBy(String property);
    }
    private static ParserState createFinalOrderPropertiesState() {
        ParserState state = new ParserState(ParserTransition.EmptyTransition.getInstance());
        state.setFinalState(ParserState.FinalState.ORDER_PROPERTY);
        return state;
    }

    private static ParserState createFinalOrderDirectionState() {
        ParserState state = new ParserState(ParserTransition.EmptyTransition.getInstance());
        state.setFinalState(ParserState.FinalState.ORDER_DIRECTIUON);
        return state;
    }

    private static ParserState createFinalOrderJoinState() {
        ParserState state = new ParserState(ParserTransition.EmptyTransition.getInstance());
        state.setFinalState(ParserState.FinalState.ORDER_JOIN);
        return state;
    }

    // Matched sequences:
    //  - Asc :: set ascending order, no additional ordering
    //  - Desc :: set descending order, no additional ordering
    //  - AscAnd :: set ascending order, additional ordering will follow
    //  - DescAnd :: set descending order, additional ordering will follow
    //  - And :: set default (ascending) order, additional ordering will follow
    // This state machine is build directly from known set of keywords. Any existing keywords change
    // will require this builder to be rewritten.
    private static ParserState buildDirectionJoinStateMachine(
            ParserTransition.Action ascEndAction,
            ParserTransition.Action descEndAction,
            ParserTransition.Action onlyAnd) {
        // Direction keywords are Asc and Desc. They will go in parallel branches until final state where
        // they'll merge with last characters.
        String ascKw = DynamicFinderOrder.Order.Method.ASC.keyword();
        String descKw = DynamicFinderOrder.Order.Method.DESC.keyword();
        ParserState ascState = new ParserState(ParserTransition.EmptyTransition.getInstance());
        ParserState descState = new ParserState(ParserTransition.EmptyTransition.getInstance());
        // Root state of the state machine.
        ParserState root = new ParserState(
                new ParserTransition.DualTransition(ascKw.charAt(0), ascState, descKw.charAt(0), descState));
        for (int i = 1; i < ascKw.length() - 1; i++) {
            ascState = ascState.addTransition(ascKw.charAt(i));
        }
        for (int i = 1; i < descKw.length() - 1; i++) {
            descState = descState.addTransition(descKw.charAt(i));
        }
        // Final direction keywords state (common for both direction keywords)
        ParserState lastDirectionState = createFinalOrderDirectionState();
        ParserTransition ascTransition = new ParserTransition.SingleTransition(ascKw.charAt(ascKw.length() - 1), lastDirectionState);
        ParserTransition descTransition = new ParserTransition.SingleTransition(descKw.charAt(descKw.length() - 1), lastDirectionState);
        ascTransition.setAction(ascKw.charAt(ascKw.length() - 1), ascEndAction);
        descTransition.setAction(descKw.charAt(descKw.length() - 1), descEndAction);
        ascState.setTransition(ascTransition);
        descState.setTransition(descTransition);
        // Join keyword should be added twice: To final direction keywords state and also to root state,
        // because direction keywords are not mandatory.
        // 1st add full And keyword matching states after final direction keywords state.
        ParserState andState = lastDirectionState.addTransition(DynamicFinderOrder.JOIN_OPERATOR.charAt(0));
        for (int i = 1; i < descKw.length() - 2; i++) {
            andState = andState.addTransition(DynamicFinderOrder.JOIN_OPERATOR.charAt(i));
        }
        ParserState lastJoinState = createFinalOrderJoinState();
        andState.setTransition(new ParserTransition.SingleTransition(
                DynamicFinderOrder.JOIN_OPERATOR.charAt(DynamicFinderOrder.JOIN_OPERATOR.length() - 1), lastJoinState));
        // 2nd connect root state with existing And keyword matching states:
        //  - transition for 'A' is already present in root state so transition for 'n' must be used to connect
        //    to already existing states.
        // State following root after 'A' transition.
        ParserState rootFollower = root.transition().next(DynamicFinderOrder.JOIN_OPERATOR.charAt(0));
        // State following final direction keywords state after 'A' + 'n' transition.
        ParserState finalFollower = lastDirectionState.transition().next(DynamicFinderOrder.JOIN_OPERATOR.charAt(0));
        finalFollower = finalFollower.transition().next(DynamicFinderOrder.JOIN_OPERATOR.charAt(1));
        // Join paths. Also set property with default order because there is no specific ordering keyword before And keyword
        ParserTransition joinTransition = rootFollower.transition().add(DynamicFinderOrder.JOIN_OPERATOR.charAt(1), finalFollower);
        joinTransition.setAction(DynamicFinderOrder.JOIN_OPERATOR.charAt(1), onlyAnd);
        rootFollower.setTransition(joinTransition);
        return root;
    }

    private OrderBy builder;
    private DynamicFinderOrder.Builder orderBuilder;
    // Root (starting) node of the order properties parser.
    private final ParserState propertiesRoot;
    // Root (starting) node of the ordering direction parser.
    // Contains And keyword to connect more orderings.
    private final ParserState directionRoot;
    private int firstPropertyChar;
    private String property;

    MethodOrderParser(List<String> entityProperties) {
        this.propertiesRoot = buildStateMachineFromSortedList(
                entityProperties, MethodOrderParser::createFinalOrderPropertiesState,
                ParserState.FinalState.CRITERIA_PROPERTY, this::firstPropertyChar, this::lastPropertyChar,
                null, null);
        this.directionRoot = buildDirectionJoinStateMachine(this::ascProperty, this::descProperty, this::buildProperty);
        this.builder = null;
        this.orderBuilder = null;
        this.firstPropertyChar = 0;
        this.property = null;
    }

    // Reset parser to be used for another method name parsing
    void reset() {
        this.builder = null;
        this.orderBuilder = null;
        this.firstPropertyChar = 0;
        this.property = null;
    }

    ParserState.FinalState parse(DynamicFinderSelection.Builder builder, ParserContext context) {
        // Set builder instance retrieved from selection part parser.
        this.builder = builder::orderBy;
        return parse(context);
    }

    ParserState.FinalState parse(DynamicFinderCriteria.Builder builder, ParserContext context) {
        // Set selection builder instance retrieved from criteria part parser.
        this.builder = builder::orderBy;
        return parse(context);
    }

    private ParserState.FinalState parse(ParserContext context) {
        ParserState.FinalState finalState = null;
        while (finalState != ParserState.FinalState.END) {
            // Ordering property is mandatory for each ordering expression.
            finalState = executeStateMachineWithStepBack(context, propertiesRoot,
                    Set.of(ParserState.FinalState.ORDER_PROPERTY),
                    this::finalPropertyState, () -> orderBuilder.build(), true,
                    "Unknown ordering property %s found at position %d when parsing %s.",
                    "Illegal final state %s when parsing ordering property");
            if (finalState == ParserState.FinalState.END) {
                return finalState;
            }
            finalState = executeStateMachine(context, directionRoot,
                    Set.of(ParserState.FinalState.ORDER_DIRECTIUON, ParserState.FinalState.ORDER_JOIN),
                    null, () -> orderBuilder.build(), true,
                    "Unknown keyword %s found at position %d when parsing %s.",
                    "Illegal final state %s when parsing ordering keywords");
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
    void buildProperty(ParserContext ctx) {
        if (orderBuilder == null) {
            orderBuilder = builder.orderBy(property);
        } else {
            orderBuilder.and(property);
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

    // Store property and set ascending order.
    void ascProperty(ParserContext ctx) {
        buildProperty(ctx);
        orderBuilder.asc();
    }

    // Store property and set descending order.
    void descProperty(ParserContext ctx) {
        buildProperty(ctx);
        orderBuilder.desc();
    }

}
