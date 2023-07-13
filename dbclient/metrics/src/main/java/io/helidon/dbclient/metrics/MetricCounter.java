/*
 * Copyright (c) 2019, 2023 Oracle and/or its affiliates.
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
package io.helidon.dbclient.metrics;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;

/**
 * {@link MetricService} implementation for {@link Counter}.
 */
final class MetricCounter extends MetricService<Counter> {

    private MetricCounter(Builder builder) {
        super(builder);
    }

    /**
     * Create a new fluent API builder to create a new counter metric.
     * @return a new builder instance
     */
    static Builder builder() {
        return new Builder();
    }

    @Override
    protected void executeMetric(Counter metric, CompletionStage<Void> future) {
        future.thenRun(() -> {
            if (measureSuccess()) {
                metric.inc();
            }
        }).exceptionally(throwable -> {
            if (measureErrors()) {
                metric.inc();
            }
            return null;
        });
    }

    @Override
    protected MetricType metricType() {
        return MetricType.COUNTER;
    }

    @Override
    protected Counter metric(MetricRegistry registry, Metadata meta) {
        return registry.counter(meta);
    }

    @Override
    protected String defaultNamePrefix() {
        return "db.counter.";
    }

    /**
     * Fluent API builder for {@link MetricCounter}.
     */
    static class Builder extends DbClientMetricBuilder<Builder, MetricCounter> {

        @Override
        public MetricCounter build() {
            return new MetricCounter(this);
        }
    }
}
