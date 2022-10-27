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

// Purpose of this class is just separation of public API from MethodParserImpl.
// It should be reduced to a single method which produces translated target platform query.
/**
 * Data repository query method parser.
 */
public abstract class MethodParser {

    /**
     * Parse data repository query method.
     *
     * @param entityName name of the data repository entity
     * @param methodName name of the data repository query method
     * @param methodArguments {@link List} of method arguments in the same order as in method prototype
     * @return dynamic finder statement translated to target query String
     */
    public DynamicFinderStatement parse(String entityName, String methodName, List<String> methodArguments) {
        DynamicFinder query = this.parse(methodName, methodArguments);
        final TransformQuery transform = new TransformQuery(query);
        transform.transform(entityName);
        return transform;
    }

    /**
     * Internal implementation of the parser.
     * This parser should analyze method prototype and produce abstract syntax tree model.
     *
     * @param methodName name of the data repository query method
     * @param methodArguments {@link List} of method arguments in the same order as in method prototype
     * @return dynamic finder query abstract syntax tree
     */
    abstract DynamicFinder parse(String methodName, List<String> methodArguments);

}
