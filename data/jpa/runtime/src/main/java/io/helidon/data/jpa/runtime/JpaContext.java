package io.helidon.data.jpa.runtime;

import java.util.Map;
import java.util.Objects;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// Needs public visibility to access EntityManager from generated classes.
// This instance is passed to generated code. It contains JPA context required
// for statements execution.
/**
 * JPA context class.
 * This class is passed to Repository instances to allow statement execution via thread local
 * {@link jakarta.persistence.EntityManager}.
 */
public class JpaContext {

    // Override ThreadLocal to set initial value with no EntityManager
    private static final class LocalContext extends ThreadLocal<ThreadContext> {

        @Override
        public ThreadContext initialValue() {
            return new ThreadContext();
        }

    }

    // ThreadLocal JPA context.
    private static final class ThreadContext {

        // Keep singleton instance in inner class to initialize it lazily.
        private static final LocalContext INSTANCE = new LocalContext();

        private static ThreadLocal<ThreadContext> getInstance() {
            return INSTANCE;
        }

        // thread local EntityManager
        private EntityManager entityManager;

        private ThreadContext() {
            entityManager = null;
        }

        private EntityManager entityManager(EntityManagerFactory factory) {
            // Also refresh closed entity manager.
            if (entityManager == null || !entityManager.isOpen()) {
                entityManager = factory.createEntityManager();
            }
            return entityManager;
        }

        private void closeEntityManager() {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
                // Remove reference and pass it to GC.
                entityManager = null;
            }
        }

    }

    // JPA EntityManagerFactory bound with selected persistence unit.
    private final EntityManagerFactory factory;

    /**
     * Creates an instance of JPA context.
     *
     * @param puName persistence unit name
     * @param properties persistence unit properties
     */
    JpaContext(String puName, Map<String, String> properties) {
        Objects.requireNonNull(puName, "Name of the JPA persistence unit is null.");
        Objects.requireNonNull(puName, "JPA persistence unit properties Map is null.");
        factory = Persistence.createEntityManagerFactory(puName, properties);
    }

    /**
     * Returns thread local instance of JPA {@link jakarta.persistence.EntityManager} for current persistence unit.
     */
    public EntityManager entityManager() {
        return ThreadContext.getInstance().get().entityManager(factory);
    }

    /**
     * Closes entity manager in current thread local context.
     */
    public void closeEntityManager() {
        ThreadContext.getInstance().get().closeEntityManager();
    }

}
