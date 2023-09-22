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

import java.lang.reflect.Type;
import java.util.Optional;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.tests.integration.junit5.spi.ConfigProvider;

public class DefaultConfigProvider implements ConfigProvider, SuiteResolver {

    private String fileName;
    private SuiteContext suiteContext;
    private Config config;
    private Config.Builder builder;
    private Optional<String> key;

    public DefaultConfigProvider() {
        fileName = null;
        suiteContext = null;
        config = null;
        builder = null;
        key = Optional.empty();
    }

    @Override
    public void suiteContext(SuiteContext suiteContext) {
        this.suiteContext = suiteContext;
    }

    @Override
    public void file(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void key(Optional<String> key) {
        this.key = key;
    }

    @Override
    public void setup() {
        builder = Config.builder().addSource(ConfigSources.classpath(fileName));
    }

    @Override
    public Config.Builder builder() {
        return builder;
    }

    @Override
    public void start() {
        config = builder.build();
        if (key.isPresent()) {
            suiteContext.storage().put(key.get(), config);
        }
    }

    @Override
    public Config config() {
        if (key.isPresent()) {
            return suiteContext.storage().get(key.get(), Config.class);
        }
        return config;
    }

    @Override
    public <T extends Junit5ExtensionProvider> T as(Class<T> cls) {
        if (cls == ConfigProvider.class || cls == DefaultConfigProvider.class) {
            return cls.cast(this);
        }
        throw new IllegalArgumentException(
                String.format("Cannot cast this ConfigProvider implementation as %s", cls.getName()));
    }

    @Override
    public boolean supportsParameter(Type type) {
        return Config.class.isAssignableFrom((Class<?>) type);
    }

    @Override
    public Object resolveParameter(Type type) {
        if (Config.class.isAssignableFrom((Class<?>)type)) {
            return config();
        }
        throw new IllegalArgumentException(String.format("Cannot resolve parameter Type %s", type.getTypeName()));
    }

}
