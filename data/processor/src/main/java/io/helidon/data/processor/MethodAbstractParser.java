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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

class MethodAbstractParser {

    /**
     * Builds state machine from list of Strings sorted by length in descending order.
     *
     * @param sortedStrings source Strings sorted by length in descending order
     * @param finalStateCreator final state instance supplier
     * @param firstChar first string character processing action
     * @param lastChar last string character processing action
     * @param rootStateEnhancer root state enhancer called at the end of the state machine building process
     * @param finalStatesEnhancer final states enhancer called for each of the states at the end
     *                            of the state machine building process
     * @return root state of the state machine graph
     */
    static final ParserState buildStateMachineFromSortedList(
            List<String> sortedStrings,
            Supplier<ParserState> finalStateCreator,
            ParserState.FinalState finalStateType,
            ParserTransition.Action firstChar,
            ParserTransition.Action lastChar,
            Consumer<ParserState> rootStateEnhancer,
            Consumer<ParserState> finalStatesEnhancer) {
        ParserState finalState = finalStateCreator.get();
        List<ParserState> finalStates = new LinkedList<>();
        finalStates.add(finalState);
        ParserState root = new ParserState(
                // Init 1st state transition to be large enough to hold all selection properties.
                new ParserTransition.MultiTransition(sortedStrings.size() + 2));
        // Properties are sorted from the longest to the shortest (mandatory, this builder won't work without that).
        // Final state for prefix of already existing property will always exist as non-final state first.
        for (String property : sortedStrings) {
            ParserState state = root;
            for (int i = 0; i < property.length(); i++) {
                char c = property.charAt(i);
                // Build transitions from 1st to before last letters of keywords.
                if (i < property.length() - 1) {
                    if (i == 0) {
                        // 1st character of the property should be upper case.
                        state = state.addTransition(Character.toUpperCase(c));
                        if (firstChar != null) {
                            root.transition().setAction(Character.toUpperCase(c), firstChar);
                        }
                    } else {
                        state = state.addTransition(c);
                    }
                    // Build and add transition for last letter of property.
                } else {
                    ParserState next = state.transition().next(c);
                    // Mark already existing last state as final.
                    if (next != null) {
                        if (next.finalState() != null) {
                            throw new IllegalStateException(String.format(
                                    "Property %s was already added to state machine.", property));
                        }
                        next.setFinalState(finalStateType);
                        finalStates.add(next);
                        // Add transition to shared final state. There won't be another longer String.
                    } else {
                        ParserTransition transition = new ParserTransition.SingleTransition(c, finalState);
                        state.setTransition(transition);
                    }
                    if (lastChar != null) {
                        state.transition().setAction(c, lastChar);
                    }
                }
            }
        }
        if (rootStateEnhancer != null) {
            rootStateEnhancer.accept(root);
        }
        if (finalStatesEnhancer != null) {
            finalStates.forEach(state -> finalStatesEnhancer.accept(state));
        }
        return root;
    }

    ParserState.FinalState executeStateMachine(
            ParserContext context,
            ParserState initialState,
            Set<ParserState.FinalState> finalStates,
            ParserState.FinalAction finalAction,
            Supplier<DynamicFinder> queryBuilder,
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
                    if (!finalStates.contains(state.finalState())) {
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
        if (finalAction != null) {
            finalAction.process(context);
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
                if (finalStates.contains(state.finalState()) && !context.hasNext()) {
                    context.setQuery(queryBuilder.get());
                    return ParserState.FinalState.END;
                }
            }
        }
        return state.finalState();
    }

    ParserState.FinalState executeStateMachineWithStepBack(
            ParserContext context,
            ParserState initialState,
            Set<ParserState.FinalState> finalStates,
            ParserState.FinalAction finalAction,
            Supplier<DynamicFinder> queryBuilder,
            boolean mandatory,
            String noMoreTransitionsException,
            String illegalFinalStateException) {
        int begPos = context.pos();
        ParserState state = initialState;
        ParserState lastFinal = null;
        while (context.hasNext()) {
            ParserState next = state.transition().next(context.character());
            // No more transitions available
            if (next == null) {
                if (state.finalState() == null) {
                    // State machine can't match current text position, but there is previous final state stored.
                    // Rollback to this stored final state and use it as parsing result.
                    if (lastFinal != null) {
                        context.rollback();
                        state = lastFinal;
                        break;
                    }
                    throw new MethodParserException(String.format(noMoreTransitionsException,
                            context.parsedText(begPos), context.pos(), context.text()));
                } else {
                    if (!finalStates.contains(state.finalState())) {
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
                // Found final state but input characters and transitions are still available
                if (state.finalState() != null) {
                    lastFinal = state;
                    context.store();
                }
            }
        }
        if (finalAction != null) {
            finalAction.process(context);
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
                if (finalStates.contains(state.finalState()) && !context.hasNext()) {
                    context.setQuery(queryBuilder.get());
                    return ParserState.FinalState.END;
                }
            }
        }
        return state.finalState();
    }

}
