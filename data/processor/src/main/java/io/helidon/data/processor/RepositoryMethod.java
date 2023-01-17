package io.helidon.data.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RepositoryMethod {


    private final String name;
    private final Class<?> type;
//    private final Annotation[] annotations;

    private final List<RepositoryParameter> parameters;

    private RepositoryMethod(String name, Class<?> type, List<RepositoryParameter> parameters) {
        this.name = name;
        this.type = type;
        this.parameters = parameters;
    }
    String name() {
        return name;
    }

    Class<?> type() {
        return type;
    }

    List<RepositoryParameter> parameters() {
        return parameters;
    }

   static Builder builder() {
        return new Builder();
    }

    final static class Builder {

        private String name;
        private Class<?> type;
        private List<RepositoryParameter> parameters;

        Builder() {
            this.name = null;
            this.type = null;
            this.parameters = new LinkedList<>();
        }

        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder type(Class<?> type) {
            this.type = type;
            return this;
        }

        Builder parameter(RepositoryParameter parameter) {
            parameters.add(parameter);
            return this;
        }

        RepositoryMethod build() {
            return new RepositoryMethod(name, type, List.copyOf(parameters));
        }

    }



}
