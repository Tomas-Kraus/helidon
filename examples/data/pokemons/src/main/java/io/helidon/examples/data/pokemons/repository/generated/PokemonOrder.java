package io.helidon.examples.data.pokemons.repository.generated;

import java.util.Optional;

import io.helidon.data.runtime.DynamicFinderOrder;
import io.helidon.data.runtime.OrderBuilder;

public interface PokemonOrder {

    Optional<DynamicFinderOrder> order();

    interface Builder<B extends Builder<B, T>, T extends PokemonOrder> extends io.helidon.common.Builder<B, T>, OrderBuilder<B> {
        B orderById(String order);
        B orderById(DynamicFinderOrder.Order.Method order);
        B orderByName(String order);
        B orderByName(DynamicFinderOrder.Order.Method order);
        B orderByType(TypeOrder order);
    }

    @SuppressWarnings("rawtypes")
    static Builder<? extends Builder, ? extends PokemonOrder> builder() {
        return new PokemonFilterImpl.BuilderImpl();
    }

}
