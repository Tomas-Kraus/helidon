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

import io.helidon.data.runtime.DynamicFinderSelection;

/**
  * Data repository query method parser of the selection part of the method name.
  */
class MethodSelectionParser extends MethodAbstractParser {

    ///////////////////////////////////////////////////////////////////////////
    // State machines builders.                                              //
    ///////////////////////////////////////////////////////////////////////////

    // Build DynamicFinderSelection.Method keywords parsing state machine
    private static ParserState buildSelectionMethodsStateMachine(MethodSelectionParser parser) {
        ParserState root = buildSelectionMethodsRootState();
        buildSelectionMethodStates(parser, root);
        return root;
    }

    // Initialize selection root state and transitions for 1st letters of "get" and "find" keywords.
    // We have exactly 2 methods now, but this may change in the future so this code is independent
    // on methods count.
    private static ParserState buildSelectionMethodsRootState() {
        DynamicFinderSelection.Method[] selectionMethods = DynamicFinderSelection.Method.values();
        ParserTransition transition;
        switch (DynamicFinderSelection.Method.LENGTH) {
            // This shall never happen, but one never knows.
            case 0 -> throw new IllegalStateException("No query selection methods defined");
            // Direct creation of SingleTransition instance.
            case 1 -> transition = new ParserTransition.SingleTransition(
                    selectionMethods[0].keyword().charAt(0), new ParserState());
            // Direct creation of DualTransition instance.
            case 2 -> transition = new ParserTransition.DualTransition(
                    selectionMethods[0].keyword().charAt(0), new ParserState(),
                    selectionMethods[1].keyword().charAt(0), new ParserState());
            // Direct creation of MultiTransition instance.
            default -> {
                transition = new ParserTransition.MultiTransition(DynamicFinderSelection.Method.LENGTH);
                for (DynamicFinderSelection.Method method : selectionMethods) {
                    transition.add(method.keyword().charAt(0));
                }
            }
        }
        return new ParserState(transition);
    }

    // Build rest of DynamicFinderSelection.Method keywords parsing state machine
    private static void buildSelectionMethodStates(MethodSelectionParser parser, ParserState root) {
        DynamicFinderSelection.Method[] selectionMethods = DynamicFinderSelection.Method.values();
        // Final state of DynamicFinderSelection.Method keywords parsing
        ParserState finalState = new ParserState(ParserTransition.EmptyTransition.getInstance());
        // Mark final state of DynamicFinderSelection.Method state as final
        finalState.setFinalState(ParserState.FinalState.METHOD);
        for (DynamicFinderSelection.Method method : selectionMethods) {
            String keyword = method.keyword();
            ParserState state = root.transition().next(keyword.charAt(0));
            // Build transitions from 2nd to before last letters of keywords
            for (int i = 1; i < keyword.length() - 1; i++) {
                state = state.addTransition(keyword.charAt(i));
            }
            // Build and add transition for last letter of keyword
            char lastKeywordChar = keyword.charAt(keyword.length() - 1);
            ParserTransition transition = new ParserTransition.SingleTransition(
                    lastKeywordChar, finalState);
            state.setTransition(transition);
            // Set final letter transition action to call AST builder. This code depends
            // on current DynamicFinderSelection.Method content and DynamicFinder.Builder API
            // so it must be updated with every change in those classes.
            switch(method) {
                case GET -> transition.setAction(lastKeywordChar, parser::getAction);
                case FIND -> transition.setAction(lastKeywordChar, parser::findAction);
            }
        }
    }

    // TODO: Building process can be simplified a bit when DynamicFinderSelection.Projection.Method values
    //       list will be sorted from the longest to the shortest keyword (Count as beginning of CountDistinct
    //       won't be an exception in the process).
    //       Already did it in entity properties state machine builder.
    private static ParserState buildProjectionMethodsStateMachine(MethodSelectionParser parser) {
        ParserState finalState = createFinalProjectionMethodsState();
        ParserState root = new ParserState(
                // Init 1st state transition to be large enough to hold all projection methods keywords.
                new ParserTransition.MultiTransition(DynamicFinderSelection.Projection.Method.LENGTH + 2));
        for (DynamicFinderSelection.Projection.Method projectionMethod : DynamicFinderSelection.Projection.Method.values()) {
            String keyword = projectionMethod.keyword();
            ParserState state = root;
            for (int i = 0; i < keyword.length(); i++) {
                // Build transitions from 1st to before last letters of keywords.
                if (i < keyword.length() - 1) {
                    state = state.addTransition(keyword.charAt(i));
                // Build and add transition for last letter of keyword
                } else {
                    ParserTransition transition;
                    switch(projectionMethod) {
                        // Most of the keywords can be terminated in a single state that is already prepared.
                        default ->{
                            transition = new ParserTransition.SingleTransition(keyword.charAt(i), finalState);
                            state.setTransition(transition);
                        }
                        // Count keyword may be final, but also may not (part of CountDistinct).
                        // This case requires separate final state.
                        case COUNT -> {
                            ParserState newFinalState = createFinalProjectionMethodsState();
                            transition = new ParserTransition.SingleTransition(keyword.charAt(i), newFinalState);
                            state.setTransition(transition);
                        }
                        // Top keyword shall be followed by integer.
                        // This case requires separate final state and additional transitions.
                        case TOP -> {
                            ParserState newFinalState = createFinalProjectionMethodsState();
                            ParserTransition.MultiTransition firstDigitTransition = new ParserTransition.MultiTransition(10);
                            for (char c : new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'}) {
                                firstDigitTransition.add(c, newFinalState);
                                firstDigitTransition.setAction(c, parser::topFirstDigitAction);
                                newFinalState.setTransition(newFinalState.transition().add(c, newFinalState));
                                newFinalState.transition().setAction(c, parser::topNextDigitAction);
                            }
                            ParserState firstDigitState = new ParserState(firstDigitTransition);
                            transition = new ParserTransition.SingleTransition(keyword.charAt(i), firstDigitState);
                            state.setTransition(transition);
                        }
                    }
                    // Call actions for last known keyword final letter.
                    switch(projectionMethod) {
                        case COUNT -> transition.setAction(keyword.charAt(i), parser::countAction);
                        case COUNT_DISTINCT -> transition.setAction(keyword.charAt(i), parser::countDistinctAction);
                        case DISTINCT -> transition.setAction(keyword.charAt(i), parser::distinctAction);
                        case MAX -> transition.setAction(keyword.charAt(i), parser::maxAction);
                        case MIN -> transition.setAction(keyword.charAt(i), parser::minAction);
                        case SUM -> transition.setAction(keyword.charAt(i), parser::sumAction);
                        case AVG -> transition.setAction(keyword.charAt(i), parser::avgAction);
                        case TOP -> transition.setAction(keyword.charAt(i), parser::topAction);
                    }
                }
            }
        }
        buildFinalParserStates(root, null);
        return root;
    }

    private static ParserState createFinalProjectionMethodsState() {
        ParserState state = new ParserState(ParserTransition.EmptyTransition.getInstance());
        state.setFinalState(ParserState.FinalState.PROJECTION);
        return state;
    }

    private static ParserState createFinalSelectionPropertiesState() {
        ParserState state = new ParserState(ParserTransition.EmptyTransition.getInstance());
        state.setFinalState(ParserState.FinalState.SELECTION_PROPERTY);
        return state;
    }

    // Append states to parse 'By' and 'OrderBy' keywords after provided root state.
    // Used to extend existing state machines with additional keywords processing.
    // PERF: Currently full 'By' and 'OrderBy' keywords parsing state machine is appended to provided state.
    //       This may be done better in the future.
    private static void buildFinalParserStates(ParserState root, ParserTransition.Action action) {
        // Add By keyword
        ParserState state = root.addTransition(DynamicFinderBuilder.BY_KEYWORD.charAt(0));
        ParserState finalState = new ParserState(ParserTransition.EmptyTransition.getInstance());
        finalState.setFinalState(ParserState.FinalState.BY);
        ParserTransition finalTransition = new ParserTransition.SingleTransition(DynamicFinderBuilder.BY_KEYWORD.charAt(1), finalState);
        state.setTransition(finalTransition);
        if (action != null) {
            finalTransition.setAction(DynamicFinderBuilder.BY_KEYWORD.charAt(1), action);
        }
        // Add OrderBy keyword
        state = root.addTransition(DynamicFinderBuilder.ORDER_BY_KEYWORD.charAt(0));
        for (int i = 1; i < DynamicFinderBuilder.ORDER_BY_KEYWORD.length() - 1; i++) {
            state = state.addTransition(DynamicFinderBuilder.ORDER_BY_KEYWORD.charAt(i));
        }
        finalState = new ParserState(ParserTransition.EmptyTransition.getInstance());
        finalState.setFinalState(ParserState.FinalState.ORDER_BY);
        finalTransition = new ParserTransition.SingleTransition(
                DynamicFinderBuilder.ORDER_BY_KEYWORD.charAt(DynamicFinderBuilder.ORDER_BY_KEYWORD.length() - 1), finalState);
        state.setTransition(finalTransition);
        if (action != null) {
            finalTransition.setAction(
                    DynamicFinderBuilder.ORDER_BY_KEYWORD.charAt(DynamicFinderBuilder.ORDER_BY_KEYWORD.length() - 1), action);
        }
    }

    private DynamicFinderBuilder builder;
    private DynamicFinderSelectionBuilder selectionBuilder;

    // Root (starting) node of the selection method parser.
    private final ParserState selectionRoot;
    // Root (starting) node of the selection projection parser.
    private final ParserState projectionRoot;
    // Root (starting) node of the selection properties parser.
    private final ParserState propertiesRoot;
    // Root (starting) node of the possible terminal By and OrderBy keywords
    DynamicFinderSelection.Projection.Method projectionMethod;
    int projectionMethodNUmberParam;
    private int firstSelectionPropertyChar;
    private String selectionProperty;

    MethodSelectionParser(DynamicFinderBuilder builder, List<String> entityProperties) {
        this.selectionRoot = buildSelectionMethodsStateMachine(this);
        this.projectionRoot = buildProjectionMethodsStateMachine(this);
        this.propertiesRoot = buildStateMachineFromSortedList(
                entityProperties, MethodSelectionParser::createFinalSelectionPropertiesState,
                ParserState.FinalState.SELECTION_PROPERTY, this::firstPropertyChar, this::lastPropertyChar,
                (root) -> buildFinalParserStates(root, null), (root) -> buildFinalParserStates(root, this::finalPropertyState));
        this.builder = builder;
        this.selectionBuilder = null;
        this.projectionMethod = null;
        this.projectionMethodNUmberParam = 0;
        this.firstSelectionPropertyChar = 0;
        this.selectionProperty = null;
    }

    // Reset parser to be used for another method name parsing
    void reset(DynamicFinderBuilder builder) {
        this.builder = builder;
        this.selectionBuilder = null;
        this.projectionMethod = null;
        this.projectionMethodNUmberParam = 0;
        this.firstSelectionPropertyChar = 0;
        this.selectionProperty = null;
    }

    DynamicFinderSelectionBuilder selectionBuilder() {
        return selectionBuilder;
    }

    ParserState.FinalState parse(ParserContext context) {
        // Parse selection method
        ParserState.FinalState finalState = executeStateMachine(context, selectionRoot,
                ParserState.FinalState.METHOD, null, true,
                "Unknown selection method %s found at position %d when parsing %s.",
                "Illegal final state %s when parsing selection method");
        if (finalState == ParserState.FinalState.END) {
            return finalState;
        }
        // Parse selection projection (optional state machine, may not find matching result).
        context.store();
        finalState = executeStateMachine(context, projectionRoot,
                ParserState.FinalState.PROJECTION, this::finalProjectionState, false,
                "Unknown projection method %s found at position %d when parsing %s.",
                "Illegal final state %s when parsing selection projection");
        switch(finalState) {
            case END, BY, ORDER_BY -> {
                return finalState;
            }
            case ERROR -> context.rollback();
        }
        // Parse selection properties
        finalState = executeStateMachine(context, propertiesRoot,
                ParserState.FinalState.SELECTION_PROPERTY, this::finalPropertyState, true,
                "Unknown selection property %s found at position %d when parsing %s.",
                "Illegal final state %s when parsing selection property");
        return finalState;
    }

    private ParserState.FinalState executeStateMachine(
            ParserContext context,
            ParserState initialState,
            ParserState.FinalState finalState,
            Runnable finalStateAction,
            boolean mandatory,
            String noMoreTransitionsException,
            String illegalFinalStateException) {
        int begPos = context.pos();
        ParserState state = initialState;
        while (context.hasNext()) {
            ParserState next = state.transition().next(context.character());
            // No more transitions available
            if (next == null) {
                if (state.finalState() == null) {
                    if (mandatory) {
                        throw new MethodParserException(String.format(noMoreTransitionsException,
                                context.parsedText(begPos), context.pos(), context.text()));
                    } else {
                        // TODO: Log debug message about failed string matching
                        return ParserState.FinalState.ERROR;
                    }
                } else {
                    if (state.finalState() != finalState && state.finalState() != ParserState.FinalState.BY
                            && state.finalState() != ParserState.FinalState.ORDER_BY) {
                        throw new IllegalStateException(String.format(
                                illegalFinalStateException, state.finalState().name()));
                    } else {
                        break;
                    }
                }
            } else {
                state.transition().action(context.character()).ifPresent(
                        action -> action.process(context)
                );
                state = next;
                context.next();
            }
        }
        if (finalStateAction != null && state.finalState() == finalState) {
            finalStateAction.run();
        }
        switch (state.finalState()) {
            case BY -> {
                if (!context.hasNext()) {
                    throw new MethodParserException(String.format(
                            "Missing criteria after keyword By at position %d when parsing %s.",
                            context.pos(), context.text()));
                }
            }
            case ORDER_BY -> {
                if (!context.hasNext()) {
                    throw new MethodParserException(String.format(
                            "Missing ordering after keyword OrderBy at position %d when parsing %s.",
                            context.pos(), context.text()));
                }
            }
            default -> {
                if (state.finalState() == finalState && !context.hasNext()) {
                    context.setQuery(selectionBuilder.build());
                    return ParserState.FinalState.END;
                }
            }
        }
        return state.finalState();
    }

    void finalProjectionState() {
        if (projectionMethod != null) {
            switch (projectionMethod) {
                case COUNT -> selectionBuilder.count();
                case COUNT_DISTINCT -> selectionBuilder.countDistinct();
                case DISTINCT -> selectionBuilder.distinct();
                case MAX -> selectionBuilder.max();
                case MIN -> selectionBuilder.min();
                case SUM -> selectionBuilder.sum();
                case AVG -> selectionBuilder.avg();
                case TOP -> selectionBuilder.top(projectionMethodNUmberParam);
            }
        }
    }

    void finalPropertyState() {
        selectionBuilder.property(selectionProperty);
    }

    // Process when entering final By or OrderBy keyword state
    void finalPropertyState(ParserContext ctx) {
        finalPropertyState();
    }

    // Builder is called with last letter of the "get" keyword.
    void getAction(ParserContext ctx) {
        selectionBuilder = builder.get();
    }

    // Builder is called with last letter of the "find" keyword.
    void findAction(ParserContext ctx) {
        selectionBuilder = builder.find();
    }

    // Some keywords need special handling. We can't call builder with last
    // DynamicFinderSelection.Projection.Method keyword letter. But we can set internal identifier
    // to last retrieved keyword.

    void countAction(ParserContext ctx) {
        projectionMethod = DynamicFinderSelection.Projection.Method.COUNT;
    }

    void countDistinctAction(ParserContext ctx) {
        projectionMethod = DynamicFinderSelection.Projection.Method.COUNT_DISTINCT;
    }

    void distinctAction(ParserContext ctx) {
        projectionMethod = DynamicFinderSelection.Projection.Method.DISTINCT;
    }

    void maxAction(ParserContext ctx) {
        projectionMethod = DynamicFinderSelection.Projection.Method.MAX;
    }

    void minAction(ParserContext ctx) {
        projectionMethod = DynamicFinderSelection.Projection.Method.MIN;
    }

    void sumAction(ParserContext ctx) {
        projectionMethod = DynamicFinderSelection.Projection.Method.SUM;
    }

    void avgAction(ParserContext ctx) {
        projectionMethod = DynamicFinderSelection.Projection.Method.AVG;
    }

    void topAction(ParserContext ctx) {
        projectionMethod = DynamicFinderSelection.Projection.Method.TOP;
    }

    void topFirstDigitAction(ParserContext ctx) {
        projectionMethodNUmberParam = ctx.character() - '0';
    }

    void topNextDigitAction(ParserContext ctx) {
        projectionMethodNUmberParam = projectionMethodNUmberParam * 10 + ctx.character() - '0';
    }

    void firstPropertyChar(ParserContext ctx) {
        firstSelectionPropertyChar = ctx.pos();
    }

    void lastPropertyChar(ParserContext ctx) {
        StringBuilder property = new StringBuilder(ctx.pos() - firstSelectionPropertyChar);
        property.append(Character.toLowerCase(ctx.character(firstSelectionPropertyChar)));
        property.append(ctx.parsedText(firstSelectionPropertyChar + 1));
        selectionProperty = property.toString();
    }

}
