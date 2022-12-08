package io.helidon.data.processor;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

class EntityClass {

    private final Class<?> type;
    private final Annotation[] annotations;
    // This instance can't be immutable because Map is replaced with resolved fields in final build phase.
    // But there is no setter method available to modify this value from another class. Only builder can do it.
    private Map<String, EntityField> fields;

    // Limit builder fields access
    private boolean fieldsAccess;

    EntityClass(Class<?> type, Annotation[] annotations, Map<String, EntityField> fields) {
        this.type = type;
        this.annotations = annotations;
        this.fields = fields;
    }

    Class<?> type() {
        return type;
    }

    Annotation[] annotations() {
        return annotations;
    }

    EntityField field(String name) {
        return fields.get(name);
    }

    Set<String> fieldNames() {
       return fields.keySet();
    }

    // Creates an instance of EntityClass builder. Shall not be used outside EntityModel.Builder class!
    static Builder builder() {
        return new Builder();
    }

    final static class Builder {

        // Utility method to get EntityClass fields during build process.
        static Map<String, EntityField> entityFields(EntityClass descriptor) {
            return descriptor.fields;
        }

        // Utility method to update EntityClass fields to immutable Map after fields resolving is done.
        static void resolvedFields(EntityClass descriptor) {
            descriptor.fields = Collections.unmodifiableMap(descriptor.fields);
        }

        private Class<?> type;
        private final List<Annotation> annotations;
        private final Map<String, EntityField> fields;

        private Builder() {
            this.type = null;
            this.annotations = new LinkedList<>();
            this.fields = new HashMap<>();
        }

        Builder type(Class<?> type) {
            this.type = type;
            return this;
        }

        Builder annotation(Annotation annotation) {
            annotations.add(annotation);
            return this;
        }

        Builder field(String name, EntityField field) {
            fields.put(name, field);
            return this;
        }

        EntityClass build() {
            return new EntityClass(
                    type,
                    annotations.toArray(new Annotation[annotations.size()]),
                    // Map entries will be updated with resolved fields. Immutable Map will be set later after fields resolving is done.
                    fields);
        }

    }

}
