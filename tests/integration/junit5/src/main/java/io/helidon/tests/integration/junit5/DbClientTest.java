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
package io.helidon.tests.integration.junit5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.helidon.tests.integration.junit5.spi.DbClientProvider;

/**
 * Test that requires {@link io.helidon.dbclient.DbClient}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface DbClientTest {

    /**
     * {@link io.helidon.dbclient.DbClient} provider class.
     * Default value is {@code DefaultSetupProvider.class}
     *
     * @return setup provider class
     */
    Class<? extends DbClientProvider> provider() default DefaultDbClientProvider.class;

    /**
     * Define suite shared context storage key to store configuration.
     *
     * @return suite shared context storage key or {@code ""} when configuration shall not be stored
     */
    String configKey() default "";

    /**
     * Define suite shared context storage key to store database container mapped port.
     *
     * @return suite shared context storage key or {@code ""} when configuration shall not be stored
     */
    String portKey() default "";

}
