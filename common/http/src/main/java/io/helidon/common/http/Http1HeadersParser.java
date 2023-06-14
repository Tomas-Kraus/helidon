/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
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

package io.helidon.common.http;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.helidon.common.buffers.Bytes;
import io.helidon.common.buffers.DataReader;
import io.helidon.common.buffers.LazyString;
import io.helidon.common.media.type.ParserMode;

/**
 * Used by both HTTP server and client to parse headers from {@link io.helidon.common.buffers.DataReader}.
 */
public final class Http1HeadersParser {

    private static final System.Logger LOGGER = System.getLogger(Http1HeadersParser.class.getName());

    // TODO expand set of fastpath headers
    private static final byte[] HD_HOST = (HeaderEnum.HOST.defaultCase() + ":").getBytes(StandardCharsets.UTF_8);
    private static final byte[] HD_ACCEPT = (HeaderEnum.ACCEPT.defaultCase() + ":").getBytes(StandardCharsets.UTF_8);
    private static final byte[] HD_CONNECTION =
            (HeaderEnum.CONNECTION.defaultCase() + ":").getBytes(StandardCharsets.UTF_8);

    private static final byte[] HD_USER_AGENT =
            (HeaderEnum.USER_AGENT.defaultCase() + ":").getBytes(StandardCharsets.UTF_8);

    private Http1HeadersParser() {
    }

    /**
     * Read headers from the provided reader.
     *
     * @param reader         reader to pull data from
     * @param maxHeadersSize maximal size of all headers, in bytes
     * @param parserMode     media type parsing mode
     * @param validate       whether to validate headers
     * @return a new mutable headers instance containing all headers parsed from reader
     */
    public static WritableHeaders<?> readHeaders(DataReader reader,
                                                 int maxHeadersSize,
                                                 ParserMode parserMode,
                                                 boolean validate) {
        WritableHeaders<?> headers = WritableHeaders.create();
        int maxLength = maxHeadersSize;

        while (true) {
            if (reader.startsWithNewLine()) { // new line found at 0
                reader.skip(2);
                return headers;
            }

            Http.HeaderName header = readHeaderName(reader, maxLength, validate);
            maxLength -= header.defaultCase().length() + 2;
            // Skip spaces after header name
            while (' ' == reader.lookup()) {
                reader.skip(1);
            }
            int eol = reader.findNewLine(maxLength);
            if (eol == maxLength) {
                throw new IllegalStateException("Header size exceeded");
            }
            // we do not need the string until somebody asks for this header (unless validation is on)
            LazyString value = reader.readLazyString(StandardCharsets.US_ASCII, eol);
            reader.skip(2);
            maxLength -= eol + 1;

            if (parserMode == ParserMode.RELAXED && header == HeaderEnum.CONTENT_TYPE) {
                String valueString = value.toString();
                Optional<String> maybeRelaxedMediaType = ParserMode.findRelaxedMediaType(valueString);
                if (maybeRelaxedMediaType.isPresent()) {
                    headers.add(Http.Header.create(header, maybeRelaxedMediaType.get()));
                    LOGGER.log(System.Logger.Level.WARNING,
                               () -> String.format("Invalid %s header value \"%s\" replaced with \"%s\"",
                                                   HeaderEnum.CONTENT_TYPE.defaultCase(),
                                                   valueString,
                                                   maybeRelaxedMediaType.get()));
                } else {
                    headers.add(Http.Header.create(header, valueString));
                }
            } else {
                headers.add(Http.Header.create(header, value));
            }
            if (maxLength < 0) {
                throw new IllegalStateException("Header size exceeded");
            }
        }
    }

    private static Http.HeaderName readHeaderName(DataReader reader,
                                                  int maxLength,
                                                  boolean validate) {
        switch (reader.lookup()) {
        case (byte) 'H' -> {
            if (reader.startsWith(HD_HOST)) {
                reader.skip(HD_HOST.length);
                return HeaderEnum.HOST;
            }
        }
        case (byte) 'A' -> {
            if (reader.startsWith(HD_ACCEPT)) {
                reader.skip(HD_ACCEPT.length);
                return HeaderEnum.ACCEPT;
            }
        }
        case (byte) 'C' -> {
            if (reader.startsWith(HD_CONNECTION)) {
                reader.skip(HD_CONNECTION.length);
                return HeaderEnum.CONNECTION;
            }
        }
        case (byte) 'U' -> {
            if (reader.startsWith(HD_USER_AGENT)) {
                reader.skip(HD_USER_AGENT.length);
                return HeaderEnum.USER_AGENT;
            }
        }
        default -> {
        }
        }
        int col = reader.findOrNewLine(Bytes.COLON_BYTE, maxLength);
        if (col == maxLength) {
            throw new IllegalStateException("Header size exceeded");
        } else if (col < 0) {
            throw new IllegalArgumentException("Invalid header, missing colon: " + reader.debugDataHex());
        }

        String headerName = reader.readAsciiString(col);
        if (validate) {
            HttpToken.validate(headerName);
        }
        Http.HeaderName header = Http.Header.create(headerName);
        reader.skip(1); // skip the colon character

        return header;
    }
}
