/*
 * Copyright (c) 2022, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.nima.webserver.http1;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import io.helidon.common.buffers.DataReader;
import io.helidon.config.Config;
import io.helidon.nima.http.encoding.ContentEncodingContext;
import io.helidon.nima.webserver.ConnectionContext;
import io.helidon.nima.webserver.Router;
import io.helidon.nima.webserver.ServerContext;
import io.helidon.nima.webserver.WebServer;
import io.helidon.nima.webserver.spi.ServerConnectionSelector;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionConfigTest {

    @Test
    void testConnectionConfig()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // This will pick up application.yaml from the classpath as default configuration file
        Config config = Config.create();

        // Builds LoomServer instance including connectionProviders list.
        WebServer.Builder wsBuilder = WebServer.builder()
                .config(config.get("server"));

        // Call wsBuilder.connectionProviders() trough reflection
        Method connectionProviders
                = WebServer.Builder.class.getDeclaredMethod("connectionProviders", (Class<?>[]) null);
        connectionProviders.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ServerConnectionSelector> providers
                = (List<ServerConnectionSelector>) connectionProviders.invoke(wsBuilder, (Object[]) null);

        // Check whether at least one Http2ConnectionProvider was found
        boolean haveHttp1Provider = false;

        for (ServerConnectionSelector provider : providers) {
            if (provider instanceof Http1ConnectionSelector) {
                haveHttp1Provider = true;
                Http1Connection conn = (Http1Connection) provider.connection(mockContext());
                // Verify values to be updated from configuration file
                assertThat(conn.config().maxPrologueLength(), is(4096));
                assertThat(conn.config().maxHeadersSize(), is(8192));
                assertThat(conn.config().validatePath(), is(false));
                assertThat(conn.config().validateHeaders(), is(false));
            }
        }
        assertThat("No Http12ConnectionProvider was found", haveHttp1Provider, is(true));
    }
    @Test
    void testConnectionProvidersDisabled()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // This will pick up application.yaml from the classpath as default configuration file
        Config config = Config.create();

        // Builds LoomServer instance including connectionProviders list using server2 node.
        WebServer.Builder wsBuilder = WebServer.builder()
                .config(config.get("server2"));

        // Call wsBuilder.connectionProviders() trough reflection
        Method connectionProviders
                = WebServer.Builder.class.getDeclaredMethod("connectionProviders", (Class<?>[]) null);
        connectionProviders.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ServerConnectionSelector> providers
                = (List<ServerConnectionSelector>) connectionProviders.invoke(wsBuilder, (Object[]) null);

        // No providers shall be loaded with ServiceLoader disabled for connection providers.
        assertThat(providers, notNullValue());
        assertThat(providers, is(empty()));
    }

    // Check that WebServer ContentEncodingContext is disabled when disable is present in config
    @Test
    void testContentEncodingConfig() throws NoSuchFieldException, IllegalAccessException {
        // This will pick up application.yaml from the classpath as default configuration file
        Config config = Config.create();

        // Builds LoomServer instance including connectionProviders list.
        WebServer.Builder wsBuilder = WebServer.builder()
                .config(config.get("server"));

        // Access WebServer.Builder.contentEncodingContext trough reflection
        Field contentEncodingContextField = WebServer.Builder.class.getDeclaredField("contentEncodingContext");
        contentEncodingContextField.setAccessible(true);
        ContentEncodingContext contentEncodingContext = (ContentEncodingContext) contentEncodingContextField.get(wsBuilder);
        // helidon-nima-http-encoding-gzip is on classpath so disabling ServiceLoader shall remove them
        assertThat(contentEncodingContext.contentEncodingEnabled(), is(true));
        assertThat(contentEncodingContext.contentDecodingEnabled(), is(true));
        failsWith(() -> contentEncodingContext.decoder("gzip"), NoSuchElementException.class);
        failsWith(() -> contentEncodingContext.decoder("gzip"), NoSuchElementException.class);
        failsWith(() -> contentEncodingContext.encoder("gzip"), NoSuchElementException.class);
        failsWith(() -> contentEncodingContext.decoder("x-gzip"), NoSuchElementException.class);
        failsWith(() -> contentEncodingContext.encoder("x-gzip"), NoSuchElementException.class);
    }

    // Verify that provided task throws an exception
    private static void failsWith(Runnable task, Class<? extends Exception> exception) {
        try {
            task.run();
            // Fail the test when no Exception was thrown
            fail(String.format("Exception %s was not thrown", exception.getName()));
        } catch (Exception ex) {
            if (!exception.isAssignableFrom(ex.getClass())) {
                throw ex;
            }
        }
    }

    private static ConnectionContext mockContext() {
        ConnectionContext ctx = mock(ConnectionContext.class);
        when(ctx.dataReader()).thenReturn(mock(DataReader.class));
        when(ctx.router()).thenReturn(Router.empty());
        when(ctx.serverContext()).thenReturn(mock(ServerContext.class));
        return ctx;
    }

}
