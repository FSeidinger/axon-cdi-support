/*
 * Copyright 2017 novity Software-Consulting
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

package de.novity.axon.cdi;

import de.novity.axon.cdi.app.domain.model.SimpleCommand;
import de.novity.axon.cdi.app.domain.model.SimpleCommandHandler;
import de.novity.axon.cdi.test.ASimpleDependency;
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
                .addBeanClass(ASimpleDependency.class)
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
                .registerCommandHandler(config -> new SimpleCommandHandler())
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
        final SimpleCommand command = new SimpleCommand();

        configuration
                .commandGateway()
                .sendAndWait(command);
    }
}
