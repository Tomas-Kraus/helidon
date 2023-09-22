package io.helidon.tests.integration.dbclient.mysql;

import java.sql.DriverManager;

import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbStatementType;
import io.helidon.dbclient.metrics.DbClientMetrics;
import io.helidon.tests.integration.dbclient.common.utils.InitUtils;
import io.helidon.tests.integration.junit5.AfterSuite;
import io.helidon.tests.integration.junit5.BeforeSuite;
import io.helidon.tests.integration.junit5.ContainerTest;
import io.helidon.tests.integration.junit5.DbClientTest;
import io.helidon.tests.integration.junit5.MySqlContainer;
import io.helidon.tests.integration.junit5.SetUpDbClient;
import io.helidon.tests.integration.junit5.Suite;
import io.helidon.tests.integration.junit5.TestConfig;
import io.helidon.tests.integration.junit5.spi.SuiteProvider;

@TestConfig(key = MySqlSuite.CONFIG_KEY)
@ContainerTest(provider  = MySqlContainer.class,
               image     = "mysql:8",
               configKey = MySqlSuite.CONFIG_KEY,
               portKey   = MySqlSuite.PORT_KEY)
@DbClientTest(configKey = MySqlSuite.CONFIG_KEY,
              portKey = MySqlSuite.PORT_KEY)
public class MySqlSuite implements SuiteProvider {

    static final String CONFIG_KEY = "MySQLSuite.config.key";
    static final String PORT_KEY = "MySQLSuite.port.key";
    private static final int STARTUP_TIMEOUT = 60;
    private static final int CONNECTION_CHECK_TIMEOUT = 1;

    @SetUpDbClient
    public void setupDbClient(DbClient.Builder builder) {
        builder.addService(DbClientMetrics.counter()
                                   .statementNames("select-pokemons", "insert-pokemon"))
               .addService(DbClientMetrics.timer()
                                   .statementTypes(DbStatementType.INSERT));
    }

    @BeforeSuite
    public void beforeSuite(DbClient dbClient, Config config) {
        InitUtils.waitForStart(
                () -> DriverManager.getConnection(config.get("db.connection.url").asString().get(),
                                                  config.get("db.connection.username").asString().get(),
                                                  config.get("db.connection.password").asString().get()),
                STARTUP_TIMEOUT,
                CONNECTION_CHECK_TIMEOUT);
        InitUtils.initSchema(dbClient);
        InitUtils.initData(dbClient);
    }

    @AfterSuite
    public void afterSuite(DbClient dbClient) {
        InitUtils.dropSchema(dbClient);
    }

    @Suite(provider = MySqlSuite.class)
    public static class ExceptionalStmtIT extends io.helidon.tests.integration.dbclient.common.tests.ExceptionalStmtIT {

        public ExceptionalStmtIT(DbClient dbClient) {
            super(dbClient);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class FlowControlIT extends io.helidon.tests.integration.dbclient.common.tests.FlowControlIT {
        public FlowControlIT(DbClient dbClient) {
            super(dbClient);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class GetStatementIT extends io.helidon.tests.integration.dbclient.common.tests.GetStatementIT {

        public GetStatementIT(DbClient dbClient) {
            super(dbClient);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class HealthCheckIT extends io.helidon.tests.integration.dbclient.common.tests.HealthCheckIT {

        public HealthCheckIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class InterceptorIT extends io.helidon.tests.integration.dbclient.common.tests.InterceptorIT {

        public InterceptorIT(Config config) {
            super(config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class MapperIT extends io.helidon.tests.integration.dbclient.common.tests.MapperIT {

        public MapperIT(DbClient dbClient) {
            super(dbClient);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class QueryStatementIT extends io.helidon.tests.integration.dbclient.common.tests.QueryStatementIT {

        public QueryStatementIT(DbClient dbClient) {
            super(dbClient);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class ServerHealthCheckIT extends io.helidon.tests.integration.dbclient.common.tests.ServerHealthCheckIT {

        public ServerHealthCheckIT(DbClient dbClient) {
            super(dbClient);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class ServerMetricsCheckIT extends io.helidon.tests.integration.dbclient.common.tests.ServerMetricsCheckIT {

        public ServerMetricsCheckIT(DbClient dbClient) {
            super(dbClient);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class SimpleDeleteIT extends io.helidon.tests.integration.dbclient.common.tests.SimpleDeleteIT {

        public SimpleDeleteIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class SimpleDmlIT extends io.helidon.tests.integration.dbclient.common.tests.SimpleDmlIT {

        public SimpleDmlIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class SimpleGetIT extends io.helidon.tests.integration.dbclient.common.tests.SimpleGetIT {

        public SimpleGetIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class SimpleInsertIT extends io.helidon.tests.integration.dbclient.common.tests.SimpleInsertIT {

        public SimpleInsertIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class SimpleQueriesIT extends io.helidon.tests.integration.dbclient.common.tests.SimpleQueriesIT {

        public SimpleQueriesIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class SimpleUpdateIT extends io.helidon.tests.integration.dbclient.common.tests.SimpleUpdateIT {

        public SimpleUpdateIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class StatementDmlIT extends io.helidon.tests.integration.dbclient.common.tests.StatementDmlIT {

        public StatementDmlIT(DbClient dbClient) {
            super(dbClient);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class TransactionDeleteIT extends io.helidon.tests.integration.dbclient.common.tests.TransactionDeleteIT {

        public TransactionDeleteIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class TransactionExceptionalStmtIT extends io.helidon.tests.integration.dbclient.common.tests.TransactionExceptionalStmtIT {

        public TransactionExceptionalStmtIT(DbClient dbClient) {
            super(dbClient);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class TransactionGetIT extends io.helidon.tests.integration.dbclient.common.tests.TransactionGetIT {

        public TransactionGetIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class TransactionInsertIT extends io.helidon.tests.integration.dbclient.common.tests.TransactionInsertIT {

        public TransactionInsertIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class TransactionQueriesIT extends io.helidon.tests.integration.dbclient.common.tests.TransactionQueriesIT {

        public TransactionQueriesIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

    @Suite(provider = MySqlSuite.class)
    public static class TransactionUpdateIT extends io.helidon.tests.integration.dbclient.common.tests.TransactionUpdateIT {

        public TransactionUpdateIT(DbClient dbClient, Config config) {
            super(dbClient, config);
        }

    }

}
