# jUnit 5 Test Suite Design

## Suite

Test suite represents set of jUnit test classes with defined life-cycle.

### Suite life-cycle

Suite life-cycle consists of three stages:
- suite setup
- tests execution
- suite cleanup

Setup phase is responsible for testing environment initialization. This means
especially
- test clients (e.g. JDBC, DbClient) initialization and conbfiguration
   to be used with running contaienrs
- test containers (e.g. databases) startup

Cleanup phase si responsible for freeing all resources acquired by testing
environment. All testing clients must be closed and running containers must
be stopped and deleted.

Suite API is defined by `@Suite` annotation and `SuiteProvider` SPI interface.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(SuiteJuit5Extension.class)
@Inherited
public @interface Suite {

    /**
     * Suite provider class.
     *
     * @return the provider class
     */
    Class<? extends SuiteProvider> provider();

}
```

Annotation defines `SuiteProvider` implementing class. Value getter is called
`provider` to match `@TestContainer` annotation API, but it may be also called `value`
to simplify usage.

```java
public interface SuiteProvider extends Junit5ExtensionProvider {

  /**
   * Pass {@link Suite} context to the Junit 5 extension provider during initialization phase.
   * This method is called before any other initialization method of the provider.
   *
   * @param suiteContext the {@link Suite} context
   */
  void suiteContext(SuiteContext suiteContext);

}
```

`SuiteProvider` implementation class used as `@Suite` annotation should be annotated
with all required suite providers annotations. Those providers will be applied on all
test classes annotated with this `@Suite` annotation with the same provider argument.
The only mandatory method of `SuiteProvider` is `void suiteContext(SuiteContext suiteContext)`
to retrieve shared suite context.

Other optional methods are providers configuration hooks. Those methods are annotated
with corresponding setup annotation, e.g. `@SetUpContainer`.

```java
@SetUpContainer
void setupContainer(ContainerConfig.Builder builder) {
    // any builder calls
}
```

### Suite test classes grouping

One suite is defined by the value of the `@Suite` annotation `provider` value. This
value serves as suite unique ID. All test classes annotated with the same `provider`
form such a suite.
Each of the suites with unique `provider` has it's own life-cycle and share
the same set of <i>suite providers</i>.

## Suite with providers

Testing environment may consist of several providers. Some of those providers
were already mentioned
- testing clients (HTTP client, DbClient)
- Docker containers, for example databases

Each testing environment type requires specific handling of its life-cycle.
Suite providers represent tools to achieve this goal.

Suite provider is defined by <b>annotation</b> and <b>suite provider service</b>.

<i>Note: Suite provider annotation may be used directly on jUnit 5 test class. In such
a case annotated test class is a single test class suite and suite life-cycle
is bound just to this single class.</i>

### Test configuration provider

Simple provider that reads Config from the file specified in annotation
and optionally stores it into shared suite context under defined key.

```java
/**
 * Test that requires configuration.
 * <p>All tests in the {@link io.helidon.tests.integration.junit5.Suite} must be annotated
 * by this annotation when used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(Junit5ConfigExtension.class)
@Inherited
public @interface TestConfig {

    /**
     * Test config provider class.
     * Default value is {@code DefaultConfigProvider.class}
     *
     * @return config provider class
     */
    Class<? extends ConfigProvider> provider() default DefaultConfigProvider.class;


    /**
     * Test config provider configuration file.
     * default value is {@code "test.yaml"}
     *
     * @return config file to read from classpath
     */
    String file() default "test.yaml";

    /**
     * Define suite shared context key to store configuration.
     *
     * @return suite shared context key or {@code ""} when configuration shall not be stored
     */
    String key() default "";

}
```

Annotation defines `ConfigProvider` implementing class, source configuration file
and optional shared context key to store configuration for other providers.

```java
/**
 * Helidon integration tests configuration provider.
 */
public interface ConfigProvider extends Junit5ExtensionProvider {

    String STORE_KEY = "io.helidon.tests.ConfigProvider.key";

    /**
     * Pass {@link Suite} context to the Junit 5 extension provider during initialization phase.
     * This method is called before any other initialization method of the provider.
     *
     * @param suiteContext the {@link Suite} context
     */
    void suiteContext(SuiteContext suiteContext)

    /**
     * Config file name from {@link io.helidon.tests.integration.junit5.TestConfig}
     * annotation.
     *
     * @param file config file name to read from classpath
     */
    void file(String file);

    /**
     * Shared suite context storage key if defined.
     * @param key suite context storage key
     */
    void key(Optional<String> key);

    /**
     * Build configuration builder based on provided file value.
     */
    void setup();

    /**
     * Provide config builder to be used in setup hook.
     *
     * @return configuration builder with values from provided file.
     */
    Config.Builder builder();

    /**
     * Start the existence of Config.
     */
    void start();

    /**
     * Provide root {@link Config} instance for the tests.
     */
    Config config();

}
```

`ConfigProvider` defines base container life-cycle:
- `void suiteContext(SuiteContext suiteContext)` called during `ConfigProvider`
  instance initialization to gain access to shared suite context
- `void file(String file)` passes config file name from annotation
- `void setup()` builds initial configuration before calling optional suite
  provider `@SetUpConfig` annotated method
- `Config.Builder builder()` is initial configuration builder accessor, must
  return instance built by `setup` method
- `void start()` starts config active life by creating its instance and putting
  it into suite shared context. Method name is not optimal for this use-case,
  but matches other interfaces
- `Config config()` returns `Config` instance for the tests


### Test container provider

Test container provider handles life-cycle of Docker container. This provider
is responsible for
- container startup
- providing configuration for related client (e.g. database URL for DbClient)
- container cleanup

Container provider API is defined by `@TestContainer` annotation
and `ContainerProvider` SPI interface.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(Junit5ContainerExtension.class)
@Inherited
public @interface ContainerTest {

    /**
     * Container provider class.
     *
     * @return container provider class
     */
    Class<? extends ContainerProvider> provider();

    /**
     * Container image, may contain label, e.g. {@code "mysql"}, {@code "mysql:8.0"}.
     *
     * @return container image
     */
    String image() default "";

}
```

Annotation defines `ContainerProvider` implementing class and optional Docker
image name of the container.

```java
/**
 * Helidon Database Client integration tests Docker container provider interface.
 */
public interface ContainerProvider extends Junit5ExtensionProvider {

    /**
     * Pass {@link Suite} context to the Junit 5 extension provider during initialization phase.
     * This method is called before any other initialization method of the provider.
     *
     * @param suiteContext the {@link Suite} context
     */
    void suiteContext(SuiteContext suiteContext)

    /**
     * Docker image from {@link io.helidon.tests.integration.junit5.ContainerTest} annotation.
     * This method is called during {@link ContainerProvider} initialization phase.
     * Implementing class must store this value and handle it properly.
     *
     * @param image name of the Docker image including label or {@link Optional#empty()} when not defined
     */
    void image(Optional<String> image);

    /**
      * Build docker container configuration.
      * Default container configuration must be set in this method.
      *
      * @return docker container configuration builder with default configuration set
      */
     void setup();

    /**
     * Docker container configuration builder with default configuration set.
     * This is the {@link ContainerConfig.Builder} instance passed
     * to {@link io.helidon.tests.integration.junit5.SetUpContainer} annotated method
     * in related {@link SuiteProvider} implementing class.
     *
     * @return container configuration builder with default configuration
     */
    ContainerConfig.Builder builder();

    /**
     * Start Docker container.
     * Calling this method may change provided value of Docker container configuration.
     */
    void start();

    /**
     * Stop Docker container.
     */
    void stop();

}
```

`ContainerProvider` defines base container life-cycle:
- `void suiteContext(SuiteContext suiteContext)` called during `ContainerProvider`
  instance initialization to gain access to shared suite context
- `void image(Optional<String> image)` passes Docker image name from annotation
- `void setup()` builds initial docker container configuration
  before calling optional suite provider `@SetUpContainer` annotated method
- `ContainerConfig.Builder builder()` is initial docker container configuration
  accessor, must return instance built by `setup` method
- `void start()` starts the container and stores DbClient access URL into
  shared suite context
- `void stop()` stops and deletes the container

### Test DbClient provider

This provider adds DbClient life-cycle handling. Provider responsibility
is to properly configure and initialize DbClient for the suite. Also synchronization
with the container startup and database initialization is required.

DbClient provider API is defined by `@DbClientTest` annotation
and `DbClientProvider` SPI interface.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(Junit5ContainerExtension.class)
@Inherited
public @interface DbClientTest {

    /**
     * Test setup provider class.
     * Default value is {@code DefaultSetupProvider.class}
     *
     * @return setup provider class
     */
    Class<? extends DbClientProvider> provider() default DefaultdbClientProvider.class;

}
```
Annotation defines `DbClientProvider` implementing class. Value getter is called
`provider` to match `@TestContainer` annotation API, but it may be also called `value`
to simplify usage.

```java
/**
 * Helidon Database Client integration tests configuration provider interface.
 */
public interface DbClientProvider extends Junit5ExtensionProvider, DbClientSupplier {

    String STORE_KEY = "io.helidon.tests.dbclient.DbClientProvider.key";

    /**
     * Pass {@link Suite} context to the Junit 5 extension provider during initialization phase.
     * This method is called before any other initialization method of the provider.
     *
     * @param suiteContext the {@link Suite} context
     */
    void suiteContext(SuiteContext suiteContext)

    /**
     * Build {@link io.helidon.dbclient.DbClient} configuration.
     * Default client configuration must be set in this method.
     *
     * @return {@link io.helidon.dbclient.DbClient} configuration builder with default configuration set
     */
    void setup();

    /**
     * {@link io.helidon.dbclient.DbClient} builder with default configuration set.
     * This is the {@link io.helidon.tests.integration.junit5.ContainerConfig.Builder} instance passed
     * to {@code SetUpDbClient} annotated method
     * in related {@link io.helidon.tests.integration.junit5.spi.SuiteProvider} implementing class.
     *
     * @return {@link io.helidon.dbclient.DbClient} builder with default configuration
     */
    DbClient.Builder builder();

    /**
     * Create {@link io.helidon.dbclient.DbClient} instance and initialize the database.
     * This method must also ensure that related docker container is ready to accept connections.
     */
    void start();

    /**
     * Return {@link io.helidon.dbclient.DbClient} instance for the tests.
     */
    DbClient dbClient();

}
```

`DbClientProvider` defines the `DbClient` instance life-cycle for the whole suite:
- `void suiteContext(SuiteContext suiteContext)` called during `ContainerProvider`
  instance initialization to gain access to shared suite context
- `void setup()` builds initial `DbClient` configuration
  before calling optional suite provider `@SetUpDbClient` annotated method
- `DbClient.Builder builder()` is initial `DbClient` configuration
  accessor, must return instance built by `setup` method
- `void start()` ensures that related container is up and accepts connections,
  creates `DbClient` instance for the tests and initializes the database
  (relational schema, data)
- `DbClient dbClient()` returns `DbClient` instance for the tests

### Passing container mapped port to the DbClientTest

`ContainerProvider` must store updated database URL into the shared suite context
under key known also to `DbClientProvider`.

This requires execution of both provider methods to be synchronized:
1. providers initialization phase
- `ConfigProvider.suiteContext`, `ContainerProvider.suiteContext` and `DbClientProvider.suiteContext`
- `ConfigProvider.file`, `ConfigProvider.key`
- `ContainerProvider.image`

2. configuration initialization phase
- `ConfigProvider.setup`
- `ConfigProvider.builder` and suite `@SetUpConfig` annotated method
- `ConfigProvider.start` creates and stores config

3. container configuration and startup
- `ContainerProvider.setup`
- `ContainerProvider.builder` and suite `@SetUpContainer` annotated method
- `ContainerProvider.start` stores updated database URL

4. `DbClient` configuration and startup
- `DbClientProvider.setup` retrieves updated database URL
- `DbClientProvider.builder` and suite `@SetUpDbClient` annotated method
- `DbClientProvider.start`

5. tests execution
- `DbClientProvider.dbClient`

6. suite Cleanup
- `DbClientProvider.stop` and `ContainerProvider.stop`

### Annotated test classes

```
@Suite(provider = MySQLSuite.class)
public class SimpleDmlIT extends io.helidon.tests.integration.dbclient.common.tests.SimpleDmlIT {

    public SimpleDmlIT(DbClient dbClient, Config config) {
        super(dbClient, config);
    }

}
```

Test class is annotated with `@Suite` annotation only. Provider `MySQLSuite` contains
both `@TestContainer` and `@DbClientTest` annotations.
