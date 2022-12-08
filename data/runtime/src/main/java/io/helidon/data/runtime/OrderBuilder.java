package io.helidon.data.runtime;

public interface OrderBuilder<B extends OrderBuilder> {

    /**
     * Set ordering rule for provided entity attribute.
     *
     * @param name  name of the entity attribute
     * @param order ordering keyword
     * @return ordering builder
     */
    B orderBy(String name, String order);


    /**
     * Set ordering rule for provided entity attribute.
     *
     * @param name  name of the entity attribute
     * @param order ordering method
     * @return ordering builder
     */
    B orderBy(String name, DynamicFinderOrder.Order.Method order);

}
