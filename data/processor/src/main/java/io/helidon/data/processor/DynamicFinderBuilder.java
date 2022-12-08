package io.helidon.data.processor;

import java.util.Optional;

import io.helidon.data.runtime.DynamicFinder;
import io.helidon.data.runtime.DynamicFinderCriteria;
import io.helidon.data.runtime.DynamicFinderOrder;
import io.helidon.data.runtime.DynamicFinderSelection;

/**
 * Helidon dynamic finder query builder.
 */
class DynamicFinderBuilder {

    /** Criteria part of dynamic finder query initial keyword. */
    static final String BY_KEYWORD = "By";
    /** Order part of dynamic finder query initial keyword. */
    static final String ORDER_BY_KEYWORD = "OrderBy";

    static DynamicFinderBuilder builder() {
        return new DynamicFinderBuilder();
    }

    // Selection part of dynamic finder query.
    private DynamicFinderSelection selection;
    // Criteria part of dynamic finder query.
    private DynamicFinderCriteria criteria;
    // Criteria part of dynamic finder query.
    private DynamicFinderOrder order;

    // Creates an instance of Helidon dynamic finder query builder.
    private DynamicFinderBuilder() {
        this.criteria = null;
        this.selection = null;
        this.order = null;
    }

    /**
     * Select dynamic finder query with single result.
     *
     * @return builder with single result query
     */
    DynamicFinderSelectionBuilder get() {
        return new DynamicFinderSelectionBuilder(this).get();
    }

    /**
     * Select dynamic finder query with multiple results.
     *
     * @return builder with multiple results query
     */
    DynamicFinderSelectionBuilder find() {
        return new DynamicFinderSelectionBuilder(this).find();
    }

    /**
     * Internal: Build Helidon dynamic finder query.
     *
     * @return new instance of Helidon dynamic finder query.
     */
    DynamicFinder build() {
        return DynamicFinder.build(
                selection,
                criteria != null ? Optional.of(criteria) : Optional.empty(),
                order != null ? Optional.of(order) : Optional.empty()
        );
    }

    /**
     * Internal: Setter for selection from {@link DynamicFinderSelectionBuilder}.
     *
     * @param selection selection part of dynamic finder query
     */
    void setSelection(final DynamicFinderSelection selection) {
        this.selection = selection;
    }

    /**
     * Internal: Setter for criteria from {@link DynamicFinderCriteriaBuilder}.
     *
     * @param criteria selection part of dynamic finder query
     */
    void setCriteria(final DynamicFinderCriteria criteria) {
        this.criteria = criteria;
    }

    /**
     * Internal: Setter for order from {@link DynamicFinderOrderBuilder}.
     *
     * @param order order part of dynamic finder query
     */
    void setOrder(final DynamicFinderOrder order) {
        this.order = order;
    }

}
