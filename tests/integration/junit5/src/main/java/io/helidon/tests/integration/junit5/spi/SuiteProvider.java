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
package io.helidon.tests.integration.junit5.spi;

import io.helidon.tests.integration.junit5.Junit5ExtensionProvider;

/**
 * Helidon integration tests suite provider.
 */
public interface SuiteProvider extends Junit5ExtensionProvider {

    /**
     * Cast {@link Junit5ExtensionProvider} as {@link SuiteProvider}.
     * Implementing class should override this method and add itself.
     *
     * @param cls {@link Junit5ExtensionProvider} child interface or implementing class
     * @return this instance cast to {@link SuiteProvider} and optionally implementing class
     * @param <T> target casting type
     */
    @Override
    default <T extends Junit5ExtensionProvider> T as(Class<T> cls) {
        if (cls == SuiteProvider.class) {
            return cls.cast(this);
        }
        throw new IllegalArgumentException(
                String.format("Cannot cast this SuiteProvider implementation as %s", cls.getName()));
    }

}
