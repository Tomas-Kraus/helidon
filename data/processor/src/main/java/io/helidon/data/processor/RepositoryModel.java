package io.helidon.data.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RepositoryModel {

    // Map of class FQN to class descriptor
    private final Map<String, RepositoryClass> classes;

    private RepositoryModel(Map<String, RepositoryClass> classes) {
        this.classes = classes;
    }

    static Builder builder() {
        return new Builder();
    }

    final static class Builder {

        private final Map<String, RepositoryClass> classes;

        private Builder() {
            this.classes = new HashMap<>();
        }

        Builder repository(String fqn, RepositoryClass repository) {
            classes.put(fqn, repository);
            return this;
        }

        RepositoryModel build() {
            return new RepositoryModel(Collections.unmodifiableMap(classes));
        }

    }




}
