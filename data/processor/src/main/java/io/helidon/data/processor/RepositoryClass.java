package io.helidon.data.processor;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.helidon.data.repository.CrudRepository;
import io.helidon.data.repository.GenericRepository;

public class RepositoryClass {

    enum Interface {
        /** Interface {@link CrudRepository}. */
        CRUD
        ;

        static final int LENGTH = values().length;

        private static final Map<Class<?>, Interface> DECODE_MAP = initParseMap();

        private static final Map<Class<?>, Interface> initParseMap() {
            Map<Class<?>, Interface> map = new HashMap<>(LENGTH);
            map.put(CrudRepository.class, CRUD);
            return map;
        }

        /**
         * Decode repository interfaces.
         *
         * @param iface interface class to check
         * @return interface identifier for known interface or {@code null} otherwise.
         */
        static Interface decode(Class<?> iface) {
            Objects.requireNonNull(iface, "Interface class shall not be null.");
            return DECODE_MAP.get(iface);
        }

     }

    private final Class<?> type;

    private final Set<Interface> interfaces;
//    private final List<Annotation> annotations;

    private final List<RepositoryMethod> methods;


    private RepositoryClass(Class<?> type, Set<Interface> interfaces, List<RepositoryMethod> methods) {
        this.type = type;
        this.interfaces = interfaces;
        this.methods = methods;
    }

    Class<?> type() {
        return type;
    }

    static Builder builder() {
        return new Builder();
    }

    final static class Builder {

        private Class<?> type;
        private final Set<Interface> interfaces;
        private final List<RepositoryMethod> methods;

        private Builder() {
            this.interfaces = new HashSet<>();
            this.methods = new LinkedList<>();
            this.type = null;
        }

        Builder type(Class<?> type) {
            this.type = type;
            return this;
        }

        Builder iface(Interface iface) {
            interfaces.add(iface);
            return this;
        }

        Builder method(RepositoryMethod method) {
            methods.add(method);
            return this;
        }

        RepositoryClass build() {
            return new RepositoryClass(type, Set.copyOf(interfaces), List.copyOf(methods));
        }

    }




}
