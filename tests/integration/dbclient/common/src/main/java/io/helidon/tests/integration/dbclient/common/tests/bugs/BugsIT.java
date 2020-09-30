/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates.
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
package io.helidon.tests.integration.dbclient.common.tests.bugs;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.helidon.tests.integration.dbclient.common.AbstractIT.DB_CLIENT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for reported issues.
 */
public class BugsIT {

    /* Local logger instance. */
    private static final Logger LOGGER = Logger.getLogger(BugsIT.class.getName());

    /**
 * Issue 2286: DbClient - NullPointerException inTransaction with failing statement
 * https://github.com/oracle/helidon/issues/2286
 */
    @Test
    public void testIssue2286() {
        try {
        // Shall never throw java.util.concurrent.ExecutionException with NPE as a cause
            DB_CLIENT.execute(exec -> exec.createGet("select syntax error 1").execute()).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NullPointerException) {
                LOGGER.log(Level.WARNING, "NullPointerException in statement execution: ", cause);
            }
            assertThat(cause, is(not(instanceOf(NullPointerException.class))));
        } catch (InterruptedException e) {
            Assertions.fail("Statement execution was interrupted");
        }
        try {
        // Shall never throw java.util.concurrent.ExecutionException with NPE as a cause
            DB_CLIENT.inTransaction(tx -> tx.createGet("select syntax error 1").execute()).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NullPointerException) {
                LOGGER.log(Level.WARNING, "NullPointerException in statement execution: ", cause);
            }
            assertThat(cause, is(not(instanceOf(NullPointerException.class))));
        } catch (InterruptedException e) {
            Assertions.fail("Statement execution was interrupted");
        }
    }

    
}
