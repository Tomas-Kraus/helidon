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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents parser state transition for specified letter or set of letters.
 */
abstract class ParserTransition {

    // Action to be done on transition
    @FunctionalInterface
    interface Action {
        void process(ParserContext ctx);
    }

    abstract Optional<Action> action(char character);

    abstract void setAction(char character, Action action);

    /**
     * Return next state for provided character.
     * This method executes state transition in the model.
     *
     * @param character character of the transition
     * @return next state if transition is defined for provided character or {@code null} otherwise
     */
    abstract ParserState next(char character);

    /**
     * Add new state transition for provided character.
     *
     * @param character character of the transition
     * @return new target state
     */
    abstract ParserTransition add(char character);

    /**
     * Add transition for provided character and state.
     *
     * @param character character of the transition
     * @param state target state of the transition
     * @return new target state
     */
    abstract ParserTransition add(char character, ParserState state);

    /**
     * Returns whether current transition contains at least one transition to next state.
     *
     * @return value of {@code true} when at least one transition is present or {@code false} otherwise.
     */
    abstract boolean isEmpty();

    ParserTransition() {
    }

    /**
     * Empty transition node on leaf nodes of the state machine model.
     */
    static class EmptyTransition extends ParserTransition {

        private static final EmptyTransition INSTANCE = new EmptyTransition();

        static EmptyTransition getInstance() {
            return  INSTANCE;
        }

        private EmptyTransition() {
        }

        // Next state always returns null for leaf node.
        @Override
        ParserState next(char c) {
            return null;
        }

        @Override
        ParserTransition add(char character) {
            return add(character, new ParserState(ParserTransition.EmptyTransition.getInstance()));
        }

        @Override
        ParserTransition add(char character, ParserState state) {
            if (next(character) != null) {
                throw new IllegalArgumentException("Transition already exists for character " + character);
            }
            return new SingleTransition(character, state);
        }

        @Override
        Optional<Action> action(char c) {
            return Optional.empty();
        }

        @Override
        void setAction(char c, Action action) {
            throw new IllegalStateException("Transition action can't be set for empty transition");
        }

        @Override
        boolean isEmpty() {
            return true;
        }

    }

    /**
     * Single character transition node.
     * This is the type of majority of internal (non leaf) nodes of the state machine model.
     */
    static class SingleTransition extends ParserTransition {

        private char character;
        private ParserState next;

        private Optional<Action> action;

        SingleTransition(char character, ParserState next) {
            this.character = character;
            this.next = next;
            this.action = Optional.empty();
        }

        // Next state returns stored state when provided character matches stored character
        // or null otherwise.
        @Override
        ParserState next(char character) {
            if (this.character == character) {
                return next;
            } else {
                return null;
            }
        }

        @Override
        ParserTransition add(char character) {
            return add(character, new ParserState(ParserTransition.EmptyTransition.getInstance()));
        }

        @Override
        ParserTransition add(char character, ParserState state) {
            if (next(character) != null) {
                throw new IllegalArgumentException("Transition already exists for character " + character);
            }
            if (character == '\0' && next == null) {
                this.character = character;
                this.next = state;
                return this;
            } else {
                return new DualTransition(this, character, state);
            }
        }


        @Override
        Optional<Action> action(char c) {
            if (this.character == c) {
                return this.action;
            }
            return Optional.empty();
        }

        @Override
        void setAction(char c, Action action) {
            if (this.character == c) {
                this.action = Optional.of(action);
            } else {
                throw new IllegalStateException("Transition action can't be set for unknown character " + c);
            }
        }

        @Override
        boolean isEmpty() {
            return character == '\0' && next == null;
        }

    }

    /**
     * Dual character transition node.
     * Significant amount of nodes of the state machine model.
     * PERF: For dual transition node two simple if statements still make sense when compared to tiny hashtable.
     */
    static class DualTransition extends ParserTransition {

        private final char[] character;
        private final ParserState[] next;
        private final Optional<Action>[] action;

        // Creates an instance of dual character transition node from single character transition node
        // and another transition to be added.
        @SuppressWarnings("unchecked")
        DualTransition(SingleTransition source, char character, ParserState next) {
            this.character = new char[] {source.character, character};
            this.next = new ParserState[] {source.next, next};
            this.action = new Optional[] {source.action, Optional.empty()};
        }

        // Creates an instance of dual character transition node from specific transitions.
        @SuppressWarnings("unchecked")
        DualTransition(char character0, ParserState next0, char character1, ParserState next1) {
            this.character = new char[] {character0, character1};
            this.next = new ParserState[] {next0, next1};
            this.action = new Optional[] {Optional.empty(), Optional.empty()};
        }

        // Next state returns stored state when provided character matches one of stored characters
        // or null otherwise.
        @Override
        ParserState next(char character) {
            if (this.character[0] == character) {
                return next[0];
            } else if (this.character[1] == character) {
                return next[1];
            } else {
                return null;
            }
        }

        @Override
        ParserTransition add(char character) {
            return add(character, new ParserState(ParserTransition.EmptyTransition.getInstance()));
        }

        @Override
        ParserTransition add(char character, ParserState state) {
            if (next(character) != null) {
                throw new IllegalArgumentException("Transition already exists for character " + character);
            }
            return new MultiTransition(this, character, state);
        }

        @Override
        Optional<Action> action(char c) {
            if (this.character[0] == c) {
                return this.action[0];
            } else if (this.character[1] == c) {
                return this.action[1];
            }
            return Optional.empty();
        }

        @Override
        void setAction(char c, Action action) {
            if (this.character[0] == c) {
                this.action[0] = Optional.of(action);
            } else if (this.character[1] == c) {
                this.action[1] = Optional.of(action);
            } else {
                throw new IllegalStateException("Transition action can't be set for unknown character " + c);
            }
        }

        @Override
        boolean isEmpty() {
            return character[0] == '\0' && next[0] == null && character[1] == '\0' && next[1] == null;
        }

    }

    /**
     * Multiple characters transition node.
     * Only few internal nodes will be of this type. Usually it's the type of the root node.
     */
    static class MultiTransition extends ParserTransition {

        private final Map<Character, ParserState> characters;
        private final Map<Character, Optional<Action>> actions;

        // Creates an instance of multiple characters transition node from dual character transition node
        // and another transition to be added.
        MultiTransition(DualTransition source, char character, ParserState next) {
            characters = new HashMap<>(4);
            // Transfer characters
            characters.put(source.character[0], source.next[0]);
            characters.put(source.character[1], source.next[1]);
            characters.put(character, next);
            actions = new HashMap<>(4);
            // Transfer actions
            for (int i = 0; i < 2; i++) {
                final char c = source.character[i];
                final Optional<Action> maybeAction = source.action[i];
                maybeAction.ifPresent(action -> actions.put(c, maybeAction));
            }
        }

        // Creates an instance of empty multiple characters transition node with internal hash table
        // initialized to final transitions count.
        MultiTransition(int targetSize) {
            characters = new HashMap<>(targetSize);
            actions = new HashMap<>(targetSize);
        }

        // Next state returns stored state when provided character matches one of stored characters
        // or null otherwise.
        @Override
        ParserState next(char character) {
            return characters.get(character);
        }

        @Override
        ParserTransition add(char c) {
            if (characters.containsKey(c)) {
                throw new IllegalArgumentException("Transition already exists for character " + c);
            }
            ParserState newState = new ParserState(ParserTransition.EmptyTransition.getInstance());
            characters.put(c, newState);
            return this;
        }

        ParserTransition add(char c, ParserState state) {
            if (characters.containsKey(c)) {
                throw new IllegalArgumentException("Transition already exists for character " + c);
            }
            characters.put(c, state);
            return this;
        }

        @Override
        Optional<Action> action(char c) {
            return actions.getOrDefault(c, Optional.empty());
        }

        @Override
        void setAction(char c, Action action) {
            if (characters.containsKey(c)) {
                actions.put(c, Optional.of(action));
            } else {
                throw new IllegalStateException("Transition action can't be set for unknown character " + c);
            }
        }

        @Override
        boolean isEmpty() {
            return characters.isEmpty();
        }

        void copyActions(MultiTransition source) {
            for (Map.Entry<Character, Optional<Action>> srcEntry : source.actions.entrySet()) {
                if (characters.containsKey(srcEntry.getKey()) && !actions.containsKey(srcEntry.getKey())) {
                    actions.put(srcEntry.getKey(), srcEntry.getValue());
                }
            }
        }

    }

}
