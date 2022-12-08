package io.helidon.examples.data.pokemons.repository.generated;

import java.util.Optional;

import io.helidon.data.runtime.DynamicFinderCriteria;

public interface TypeCriteria {
    public Optional<DynamicFinderCriteria> criteria();

    interface Builder<B extends Builder<B, T>, T extends TypeCriteria> extends io.helidon.common.Builder<B, T> {
        B id(int id);
        B id(Iterable<Integer> ids);
        B name(String name);
        B name(Iterable<String> names);
    }

    @SuppressWarnings("rawtypes")
    static Builder<? extends Builder, ? extends TypeCriteria> builder() {
        return new TypeFilterImpl.BuilderImpl();
    }

}
