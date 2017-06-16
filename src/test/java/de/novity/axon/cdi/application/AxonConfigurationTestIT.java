package de.novity.axon.cdi.application;

import de.novity.axon.cdi.domain.DoItCommand;
import de.novity.axon.cdi.domain.TestCommandHandler;
import de.novity.axon.cdi.infrastructure.FirstBean;
import de.novity.axon.cdi.messaging.annotation.CdiParameterResolverFactory;
import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationParameterResolverFactory;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.messaging.annotation.ClasspathParameterResolverFactory;
import org.axonframework.messaging.annotation.MultiParameterResolverFactory;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AxonConfigurationTestIT {
    // Dependencies
    private WeldContainer container;
    private Configuration configuration;

    @BeforeEach
    private void setup() {
        container = new Weld()
                .containerId("CDI test environment")
                .disableDiscovery()
                .addBeanClass(FirstBean.class)
                .initialize();

        configuration = DefaultConfigurer
                .defaultConfiguration()
                .registerComponent(
                        ParameterResolverFactory.class,
                        config -> MultiParameterResolverFactory.ordered(
                                new CdiParameterResolverFactory(container.getBeanManager()),
                                ClasspathParameterResolverFactory.forClass(getClass()),
                                new ConfigurationParameterResolverFactory(configuration)
                        )
                )
                .registerCommandHandler(config -> new TestCommandHandler())
                .buildConfiguration();

        configuration.start();
    }

    @AfterEach
    private void teardown() {
        if (configuration != null) {
            configuration.shutdown();
        }

        if (container != null) {
            container.close();
        }
    }

    @Test
    public void cdiBeanCanBeResolved() {
        final DoItCommand command = new DoItCommand();

        configuration
                .commandGateway()
                .sendAndWait(command);
    }
}
