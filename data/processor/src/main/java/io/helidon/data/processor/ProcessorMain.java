package io.helidon.data.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedList;
import java.util.List;
import java.lang.System.Logger.Level;

public class ProcessorMain {
    private static final System.Logger LOGGER = System.getLogger(ProcessorMain.class.getName());

    public static void main(String[] args) {
        List<Class<?>> repositories = new LinkedList<>();
        try {
            EntityModel.Builder entityModelBuilder = EntityModel.builder();
            for (String arg : args) {
                Class<?> cls = null;
                cls = Class.forName(arg);
                if (cls.isAnnotationPresent(jakarta.persistence.Entity.class)) {
                    EntityClass ec = scanEntity(cls);
                    entityModelBuilder.entity(ec.type().getName(), ec);
                }
                // Need entity model before processing repository interfaces
                if (cls.isAnnotationPresent(io.helidon.data.annotation.Repository.class)) {
                    repositories.add(cls);
                }
            }
            EntityModel entityModel = entityModelBuilder.build();
            RepositoryModel.Builder repositoryModelBuilder = RepositoryModel.builder();
            for (Class<?> repository : repositories) {
                RepositoryClass rc = scanRepository(repository);
                repositoryModelBuilder.repository(rc.type().getName(), rc);
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

    private static RepositoryClass scanRepository(Class<?> repository) {
        LOGGER.log(Level.INFO, () -> String.format("Scanning repository class %s:", repository.getName()));
        RepositoryClass.Builder builder = RepositoryClass.builder();
        builder.type(repository);
        Class<?>[] interfaces = repository.getInterfaces();
        for (Class<?> interf : interfaces) {
            RepositoryClass.Interface iface = RepositoryClass.Interface.decode(interf);
            if (iface != null) {
                builder.iface(iface);
                LOGGER.log(Level.INFO, () -> String.format(" - interface %s as %s", interf.getName(), iface.name()));
            }
        }
        Method[] methods = repository.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            Class<?> returnType = method.getReturnType();
            Parameter[] parameters = method.getParameters();
            Type[] parTypes = method.getGenericParameterTypes();
            RepositoryMethod.Builder methodBuilder = RepositoryMethod
                    .builder()
                    .name(name)
                    .type(returnType);
            LOGGER.log(Level.INFO, () -> String.format(" - method %s of type %s", name, returnType.getName()));
            int paramNameCount = 1;
            int i = 0;
            for (Parameter parameter : parameters) {
                Class<?> parType = resolveGenerics(parTypes[i++]);
                if (parameter.isNamePresent()) {
                    methodBuilder.parameter(
                            RepositoryParameter.builder()
                                    .name(parameter.getName())
                                    .type(parType)
                                    .build());
                    LOGGER.log(Level.INFO, () -> String.format("   - parameter %s of type %s", parameter.getName(), parType.getName()));
                } else {
                    String paramName = String.format("par%02d", paramNameCount++);
                    LOGGER.log(Level.INFO, () -> String.format("   - parameter %s of type %s", paramName, parType.getName()));
                    methodBuilder.parameter(
                            RepositoryParameter.builder()
                                    .name(paramName)
                                    .type(parameter.getType())
                                    .build());
                }
            }
            Annotation[] methodAnnotations = method.getAnnotations();
            builder.method(methodBuilder.build());
        }
        return builder.build();
    }

    private static Class<?> resolveGenerics(Type type) {
        Class<?> res;
        if (type instanceof Class) {
            res = (Class) type;
        } else if (type instanceof ParameterizedType) {
            res = resolveGenerics(((ParameterizedType) type).getRawType());
        } else if (type instanceof TypeVariable<?>) {
            String name = ((TypeVariable) type).getName();
            Type[] bounds = ((TypeVariable) type).getBounds();
            res = resolveGenerics(bounds[0]);
            //res = (Class) type;
//            res = resolveGenerics(
//                    generics.get(((TypeVariable) type).getName()), generics);
        } else if (type instanceof WildcardType) {
            final Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            res = resolveGenerics(upperBounds[0]);
        } else {
            final Class arrayType = resolveGenerics(((GenericArrayType) type).getGenericComponentType());
            try {
                // returning complete array class with resolved type
                if (arrayType.isArray()) {
                    res = Class.forName("[" + arrayType.getName());
                } else {
                    res = Class.forName("[L" + arrayType.getName() + ";");
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(
                        "Failed to create array class", e);
            }
        }
        return res;
    }


}
