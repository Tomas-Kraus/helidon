package io.helidon.data.processor;

import java.util.Optional;

import io.helidon.data.runtime.DynamicFinder;
import io.helidon.data.runtime.DynamicFinderCriteria;
import io.helidon.data.runtime.DynamicFinderOrder;
import io.helidon.data.runtime.DynamicFinderSelection;

/**
 * Helidon dynamic finder query selection builder.
 */
class DynamicFinderSelectionBuilder {

    // Parent class builder where all parts are put together.
    private final DynamicFinderBuilder builder;
    // Query selection method.
    private DynamicFinderSelection.Method method;
    // Query selection projection.
    private DynamicFinderSelection.Projection projection;
    // Query selection property.
    private String property;

    // Creqates an instanceof query selection builder.
    DynamicFinderSelectionBuilder(DynamicFinderBuilder builder) {
        this.builder = builder;
        this.method = null;
        this.projection = null;
        this.property = null;
    }

    /**
     * Select query projection: {@code Count}.
     *
     * @return builder with query projection set as {@code Count}
     */
    DynamicFinderSelectionBuilder count() {
        projection = DynamicFinderSelection.Projection.build(
                DynamicFinderSelection.Projection.Method.COUNT, Optional.empty());
        return this;
    }

    /**
     * Select query projection: {@code CountDistinct}.
     *
     * @return builder with query projection set as {@code CountDistinct}
     */
    DynamicFinderSelectionBuilder countDistinct() {
        projection = DynamicFinderSelection.Projection.build(
                DynamicFinderSelection.Projection.Method.COUNT_DISTINCT, Optional.empty());
        return this;
    }

    /**
     * Select query projection: {@code Distinct}.
     *
     * @return builder with query projection set as {@code Distinct}
     */
    DynamicFinderSelectionBuilder distinct() {
        projection = DynamicFinderSelection.Projection.build(
                DynamicFinderSelection.Projection.Method.DISTINCT, Optional.empty());
        return this;
    }

    /**
     * Select query projection: {@code Max}.
     *
     * @return builder with query projection set as {@code Max}
     */
    DynamicFinderSelectionBuilder max() {
        projection = DynamicFinderSelection.Projection.build(
                DynamicFinderSelection.Projection.Method.MAX, Optional.empty());
        return this;
    }

    /**
     * Select query projection: {@code Min}.
     *
     * @return builder with query projection set as {@code Min}
     */
    DynamicFinderSelectionBuilder min() {
        projection = DynamicFinderSelection.Projection.build(
                DynamicFinderSelection.Projection.Method.MIN, Optional.empty());
        return this;
    }

    /**
     * Select query projection: {@code Sum}.
     *
     * @return builder with query projection set as {@code Sum}
     */
    DynamicFinderSelectionBuilder sum() {
        projection = DynamicFinderSelection.Projection.build(
                DynamicFinderSelection.Projection.Method.SUM, Optional.empty());
        return this;
    }

    /**
     * Select query projection: {@code Avg}.
     *
     * @return builder with query projection set as {@code Avg}
     */
    DynamicFinderSelectionBuilder avg() {
        projection = DynamicFinderSelection.Projection.build(
                DynamicFinderSelection.Projection.Method.AVG, Optional.empty());
        return this;
    }

    /**
     * Select query projection: {@code Top(Integer)}.
     *
     * @param count number of results to return
     * @return builder with query projection set as {@code Top(Integer)}
     */
    DynamicFinderSelectionBuilder top(int count) {
        projection = DynamicFinderSelection.Projection.build(
                DynamicFinderSelection.Projection.Method.TOP, Optional.of(count));
        return this;
    }

    /**
     * Set query property
     *
     * @param property name of the entity property
     * @return builder with query property set
     */
    DynamicFinderSelectionBuilder property(String property) {
        if (property == null) {
            throw new IllegalArgumentException("Query selection property shall not be null");
        }
        if (property.isEmpty()) {
            throw new IllegalArgumentException("Query selection property shall not be empty value");
        }
        this.property = property;
        return this;
    }

    /**
     * Build Helidon dynamic finder query criteria.
     *
     * @param property criteria expression parameter: Entity property name
     */
    DynamicFinderCriteriaBuilder by(String property) {
        // Finalize selection first.
        builder.setSelection(
                DynamicFinderSelection.build(
                        this.method,
                        this.projection != null ? Optional.of(this.projection) : Optional.empty(),
                        this.property != null ? Optional.of(this.property) : Optional.empty()));
        // Return criteria builder.
        return new DynamicFinderCriteriaBuilder(builder).by(property);
    }

    /**
     * Build Helidon dynamic finder query criteria.
     * This is a shortcut to add default {@code EQUALS} condition
     * for provided property.
     *
     * @param property       criteria expression parameter: Entity property name
     * @param conditionValue condition property value: used in {@code setParameter(property, conditionValue)} call.
     */
    DynamicFinderCriteriaBuilder by(String property, String conditionValue) {
        // Finalize selection first.
        builder.setSelection(
                DynamicFinderSelection.build(
                        this.method,
                        this.projection != null ? Optional.of(this.projection) : Optional.empty(),
                        this.property != null ? Optional.of(this.property) : Optional.empty()));
        // Return criteria builder.
        return new DynamicFinderCriteriaBuilder(builder).by(property, conditionValue);
    }

    /**
     * Build Helidon dynamic finder query order.
     *
     * @param property criteria expression parameter: Entity property name
     */
    DynamicFinderOrderBuilder orderBy(String property) {
        // Finalize selection first.
        builder.setSelection(
                DynamicFinderSelection.build(
                        this.method,
                        this.projection != null ? Optional.of(this.projection) : Optional.empty(),
                        this.property != null ? Optional.of(this.property) : Optional.empty()));
        // Return order builder.
        return new DynamicFinderOrderBuilder(builder).orderBy(property);
    }

    /**
     * Build Helidon dynamic finder query.
     *
     * @return new instance of Helidon dynamic finder query.
     */
    DynamicFinder build() {
        // Finalize selection first.
        builder.setSelection(
                DynamicFinderSelection.build(
                        method,
                        projection != null ? Optional.of(projection) : Optional.empty(),
                        property != null ? Optional.of(property) : Optional.empty()));
        // Return finished AST.
        return builder.build();
    }

    /**
     * Internal: Select dynamic finder query with single result.
     *
     * @return builder with single result query
     */
    DynamicFinderSelectionBuilder get() {
        method = DynamicFinderSelection.Method.GET;
        return this;
    }

    /**
     * Internal: Select dynamic finder query with multiple results.
     *
     * @return builder with multiple results query
     */
    DynamicFinderSelectionBuilder find() {
        method = DynamicFinderSelection.Method.FIND;
        return this;
    }

}
