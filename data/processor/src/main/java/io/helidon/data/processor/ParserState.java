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

/**
 * Represents state in finite state machine model.
 */
class ParserState {


    // Action to be done in final state
    @FunctionalInterface
    interface FinalAction {
        void process(ParserContext ctx);
    }

    /**
     * The state machine final states.
     */
    enum FinalState {
        // Finished DynamicFinderSelection.Method keyword parsing
        METHOD,
        // Finished DynamicFinderSelection.Projection.Method keyword parsing
        PROJECTION,
        // Finished projection property parsing
        SELECTION_PROPERTY,
        // Finished criteria property parsing
        CRITERIA_PROPERTY,
        // Finished criteria Not/IsNot parsing
        CRITERIA_NOT,
        // Finished criteria operator parsing
        CRITERIA_OPERATOR,
        // Finished criteria joining logical operator parsing
        CRITERIA_JOIN,
        // Finished order property parsing
        ORDER_PROPERTY,
        // Finished order direction parsing
        ORDER_DIRECTIUON,
        // Finished order joining keyword (And) parsing
        ORDER_JOIN,
        //
        BY,
        //
        ORDER_BY,
        // Reached end of the parsed method name
        END,
        // Indicate parsing error
        ERROR;
    }

    private ParserTransition transition;
    private FinalState finalState;

    // Creates an instance of leaf node.
    ParserState() {
        this.transition = ParserTransition.EmptyTransition.getInstance();
        this.finalState = null;
    }

    // Creates an instance of state with specific transition.
    ParserState(ParserTransition transition) {
        if (transition == null) {
            throw new IllegalArgumentException("transition shall not be null.");
        }
        this.transition = transition;
        this.finalState = null;
    }

    void setTransition(ParserTransition transition) {
        if (transition == null) {
            throw new IllegalArgumentException("transition shall not be null.");
        }
        this.transition = transition;
    }

    // Adds transition for specified character
    // This does not handle final states in the middle of state machine.
    ParserState addTransition(char character) {
        // Transition already exists, target state is returned
        ParserState next = transition.next(character);
        if (next != null) {
            return next;
        }
        // New transition depends on current transition instance.
        transition = transition.add(character);
        return transition.next(character);
    }

    void setFinalState(FinalState finalState) {
        this.finalState = finalState;
    }

    ParserTransition transition() {
        return transition;
    }

    FinalState finalState() {
        return finalState;
    }

}
