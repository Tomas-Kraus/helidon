package io.helidon.data.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

// Default instance is used for fields with no relation.
/**
 * Entity field relational descriptor.
 */
class EntityField {

    private static final Logger LOGGER = Logger.getLogger(EntityField.class.getName());

    static Builder builder() {
        return new Builder();
    }

    private final String name;
    private final Class<?> type;
    private final Annotation[] annotations;
    private final Optional<EntityClass>[] relation;

    @SuppressWarnings("unchecked")
    EntityField(String name, Class<?> type, Annotation[] annotations, Optional<EntityClass> relation) {
        this.name = name;
        this.type = type;
        this.annotations = annotations;
        this.relation = new Optional[] {relation};
    }

    EntityField(String name, Class<?> type, Annotation[] annotations, Optional<EntityClass>[] relation) {
        this.name = name;
        this.type = type;
        this.annotations = annotations;
        this.relation = relation;
    }

    String name() {
        return name;
    }

    Class<?> type() {
        return type;
    }

    Annotation[] annotations() {
        return annotations;
    }

    Optional<EntityClass>[] relation() {
        return relation;
    }

    // Temporary EntityField instance only during building process.
    EntityField resolve(EntityModel.Builder.Relations relations) {
        throw new IllegalStateException("Entity field relation was already resolved.");
    }

    static class Builder {

        // Utility method to access EntityField temporary relation resolving ability.
        static final EntityField resolve(EntityField descriptor, EntityModel.Builder.Relations relations) {
            return descriptor.resolve(relations);
        }

        private String name;
        private Class<?> type;
        private Annotation[] annotations;

        private Type[] generics;
        private boolean collection;

        private Builder() {
            this.name = null;
            this.type = null;
            this.annotations = null;
            this.generics = null;
            this.collection = false;
        }

        Builder name(String name) {
            this.name = name;
            return this;
        }

        // Add type and analyze type specific features (Collection, Generics, ...)
        Builder type(Class<?> type) {
            this.type = type;
            if (Collection.class.isAssignableFrom(type)) {
                collection = true;
//                generics = ((ParameterizedType)type.getClass().getGenericSuperclass()).getActualTypeArguments();
            }
            if (Map.class.isAssignableFrom(type)) {
                collection = true;
                //generics = type.getGenericInterfaces();
            }
            return this;
        }

        Builder annotations(Annotation[] annotations) {
            this.annotations = annotations;
            return this;
        }

        // Builds temporary EntityField without relation being resolved yet.
        EntityField build() {
            return new BuilderField(name, type, annotations);
        }

        // Temporary EntityField without relation being resolved.
        class BuilderField extends EntityField {

            private BuilderField(String name, Class<?> type, Annotation[] annotations) {
                super(name, type, annotations, Optional.empty());
            }

            // Resolve field entity relation during building process.
            @Override
            EntityField resolve(EntityModel.Builder.Relations relations) {
                if (collection) {
                    LOGGER.log(Level.INFO, () -> String.format("   - collection %s", type.getName()));
//                    for (Type generic : generics) {
//                        LOGGER.log(Level.INFO, () -> String.format("   - collection %s generics %s", type.getName(), generic.getClass().getName()));
//                    }
                    return new EntityField(name, type, annotations, Optional.empty());
                } else {
                    LOGGER.log(Level.INFO, () -> String.format("   - single %s", type.getName()));
                    EntityClass entity = relations.resolve(type);
                    return entity == null
                            ? new EntityField(name, type, annotations, Optional.empty())
                            : new EntityField(name, type, annotations, Optional.of(entity));
                }
            }

        }

    }

}
