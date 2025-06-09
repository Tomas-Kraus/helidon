package io.helidon.testing.junit5.suite.container;

import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.service.registry.Service;
import io.helidon.testing.junit5.suite.container.spi.SuiteContainerSupport;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Weight(Weighted.DEFAULT_WEIGHT - 50)
@Service.Singleton
public class GenericContainerSupport implements SuiteContainerSupport {

    @Override
    @SuppressWarnings("rawtypes")
    public boolean supports(Class<? extends GenericContainer> containerClass) {
        return containerClass == GenericContainer.class;
    }

    @Override
    public GenericContainer<?> create(String image) {
        return new GenericContainer<>(DockerImageName.parse(image));
    }

}
