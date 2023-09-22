package io.helidon.tests.integration.junit5;

/**
 * Helidon integration tests Junit 5 extension provider.
 * Common SPI extension base.
 */
public interface Junit5ExtensionProvider {

    /**
     * Cast Junit 5 extension provider to its implementing class.
     * @param cls Junit 5 extension provider implementing class
     * @return Junit 5 extension provider as its implementing class
     * @param <T> Junit 5 extension provider implementing class
     * @throws java.lang.IllegalArgumentException when {@code cls} parameter does not match
     *         class implemented by the provider.
     */
    <T extends Junit5ExtensionProvider> T as(Class <T> cls);

    /**
     * Pass {@link Suite} context to the Junit 5 extension provider during initialization phase.
     * This method is called before any other initialization method of the provider.
     *
     * @param suiteContext the {@link Suite} context
     */
    default void suiteContext(SuiteContext suiteContext) {
    }

}
