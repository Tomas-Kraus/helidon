package io.helidon.data.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessorMain {
    private static final Logger LOGGER = Logger.getLogger(ProcessorMain.class.getName());

    public static void main(String[] args) {
        List<Class<?>> repositories = new LinkedList<>();
        try {
            EntityModel.Builder modelBuilder = EntityModel.builder();
            for (String arg : args) {
                Class<?> cls = null;
                cls = Class.forName(arg);
                if (cls.isAnnotationPresent(jakarta.persistence.Entity.class)) {
                    EntityClass ec = scanEntity(cls);
                    modelBuilder.entity(ec.type().getName(), ec);
                }
                // Need entity model before processing repository interfaces
                if (cls.isAnnotationPresent(io.helidon.data.annotation.Repository.class)) {
                    repositories.add(cls);
                }
            }
            EntityModel model = modelBuilder.build();
            for (Class<?> repository : repositories) {
                scanRepository(repository);
            }
            // Generate filtering builders
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private static EntityClass scanEntity(Class<?> entity) {
        LOGGER.log(Level.INFO, () -> String.format("Scanning entity class %s:", entity.getName()));
        EntityClass.Builder classBuilder = EntityClass.builder().type(entity);
        Annotation[] entityAnnotations = entity.getAnnotations();
        for (Annotation annotation : entityAnnotations) {
            LOGGER.log(Level.INFO, () -> String.format(" - annotation: %s", annotation.annotationType().getName()));
            classBuilder.annotation(annotation);
        }
        Field[] fields = entity.getDeclaredFields();
        for (Field field : fields) {
            classBuilder.field(field.getName(),
                          EntityField.builder()
                                  .name(field.getName())
                                  .type(field.getType())
                                  // TODO: Store only annotations we need, skip @Transient fields
                                  .annotations(field.getAnnotations())
                                  .build());
            LOGGER.log(Level.INFO, () -> String.format(" - field: %s :: %s", field.getName(), field.getType().getName()));
        }
        return classBuilder.build();
    }

    private static void scanRepository(Class<?> repository) {
        LOGGER.log(Level.INFO, () -> String.format("Scanning repository class %s:", repository.getName()));
    }

}
