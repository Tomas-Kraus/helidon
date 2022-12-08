package io.helidon.data.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class EntityModel {

    private static final Logger LOGGER = Logger.getLogger(ProcessorMain.class.getName());

    // Map of class FQN to class descriptor
    private final Map<String, EntityClass> classes;

    EntityModel(Map<String, EntityClass> classes) {
        this.classes = classes;
    }

    static Builder builder() {
        return new Builder();
    }

    // Building process has two stages:
    // - model preparation stage where class and field descriptors are being built
    // - field relation resolving stage where target entity for fields with mapping are being set
    final static class Builder {

        private final Map<String, EntityClass> classes;

        private final Relations relations;

        private Builder() {
            this.classes = new HashMap<>();
            this.relations = new Relations();
        }

        Builder entity(String fqn, EntityClass entity) {
            classes.put(fqn, entity);
            relations.relation(entity.type(), entity);
            return this;
        }

        EntityModel build() {
            // Fields relation resolving stage before final model instance is built.
            classes.values()
                    .forEach(this::resolveRelations);
            return new EntityModel(Collections.unmodifiableMap(classes));
        }

        // Fields relation resolving: walk through all entity fields
        private void resolveRelations(EntityClass entity) {
            LOGGER.log(Level.INFO, () -> String.format(" - resolving entity %s", entity.type().getName()));
            EntityClass.Builder.entityFields(entity)
                    .entrySet()
                    .forEach(this::updateRelation);
            EntityClass.Builder.resolvedFields(entity);
        }

        // Fields relation resolving: update entity relation for entity field if available
        private void updateRelation(Map.Entry<String, EntityField> entry) {
            entry.setValue(
                    EntityField.Builder.resolve(entry.getValue(), relations));
            LOGGER.log(
                    Level.INFO,
                    () -> String.format(
                            "   - field %s relation: %s",
                            entry.getKey(), entry.getValue().relation()[0].isEmpty()
                                    ? "N/A" : entry.getValue().relation()[0].get().type().getName()));
        }

        final static class Relations {

            private final Map<Class<?>, EntityClass> relations;

            Relations() {
                this.relations = new HashMap<>();
            }

            void relation(Class<?> entityClass, EntityClass descriptor) {
                relations.put(entityClass, descriptor);
            }

            EntityClass resolve(Class<?> entityClass) {
                return relations.get(entityClass);
            }

        }

    }

}
