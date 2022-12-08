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

import io.helidon.data.runtime.DynamicFinder;

/**
 * Context of data repository query method parser.
 */
class ParserContext {

    // String being parsed.
    private final String text;
    // Current parser position in parsed String.
    private int pos;
    // Store position to allow partial rollback
    private int storedPos;
    // Target query.
    private DynamicFinder query;

    /**
     * Creates new instance of data repository query method parser.
     * @param text text to be parsed.
     */
    ParserContext(String text) {
        this.text = text;
        this.pos = 0;
        this.storedPos = 0;
        this.query = null;
    }

    /**
     * Move to the next character to be parsed.
     */
    void next() {
        pos++;
    }

    /**
     * Whether there are more characters to be parsed in the String.
     *
     * @return value of {@code true} when more characters are available
     *         or {@code false} otherwise
     */
    boolean hasNext() {
        return pos < text.length();
    }

    /**
     * Return current position in the String.
     *
     * @return parsed string index (from 0 to length() - 1)
     */
    int pos() {
        return pos;
    }

    /**
     * Store current position in the String for possible rollback.
     */
    void store() {
        storedPos = pos;
    }

    /**
     * Rollback current position in the String to previously stored position.
     */
    void rollback() {
        pos = storedPos;
    }

    /**
     * Return current character in the String.
     *
     * @return character at current position
     */
    char character() {
        return text.charAt(pos);
    }

    /**
     * Return character at the provided position in the String.
     *
     * @return character at the provided position
     */
    char character(int pos) {
        return text.charAt(pos);
    }

    /**
     * Return method name being parsed.
     *
     * @return String being parsed
     */
    String text() {
        return text;
    }

    /**
     * Return method name substring that was already parsed.
     *
     * @return method name substring that was already parsed
     */
    String parsedText(int beg) {
        return text.substring(beg, pos + 1);
    }

    /**
     * Set dynamic finder query AST.
     *
     * @param query dynamic finder query AST
     */
    void setQuery(DynamicFinder query) {
        this.query = query;
    }

    /**
     * Return dynamic finder query AST.
     *
     * @return dynamic finder query AST
     */
    DynamicFinder query() {
        return query;
    }

}
