package io.helidon.data.processor;

public class RepositoryParameter {

    private final Class<?> type;
    private final String name;

    private RepositoryParameter(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    Class<?> type() {
        return type;
    }

    String name() {
        return name;
    }

    static Builder builder() {
        return new Builder();
    }

    final static class Builder {

        private Class<?> type;
        private String name;

        private Builder() {
            this.type = null;
            this.name = null;
        }

        Builder type(Class<?> type) {
            this.type = type;
            return this;
        }

        Builder name(String name) {
            this.name = name;
            return this;
        }

        RepositoryParameter build() {
            return new RepositoryParameter(name, type);
        }



    }

}
