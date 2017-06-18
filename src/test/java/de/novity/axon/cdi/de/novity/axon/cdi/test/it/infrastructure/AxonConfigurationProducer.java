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

package de.novity.axon.cdi.de.novity.axon.cdi.test.it.infrastructure;

import de.novity.axon.cdi.CdiParameterResolverFactory;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.model.SimpleCommandHandler;
import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationParameterResolverFactory;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.messaging.annotation.ClasspathParameterResolverFactory;
import org.axonframework.messaging.annotation.MultiParameterResolverFactory;
import org.axonframework.messaging.annotation.ParameterResolverFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

public class AxonConfigurationProducer {
    @ApplicationScoped
    @Produces
    public Configuration configuration(CdiParameterResolverFactory cdiParameterResolverFactory) {
        final Configuration configuration = DefaultConfigurer
                .defaultConfiguration()
                .registerComponent(ParameterResolverFactory.class, config -> MultiParameterResolverFactory.ordered(
                        new ConfigurationParameterResolverFactory(config),
                        ClasspathParameterResolverFactory.forClass(getClass()),
                        cdiParameterResolverFactory
                ))
                .registerCommandHandler(config -> new SimpleCommandHandler())
                .buildConfiguration();

        configuration.start();

        return configuration;
    }

    public void shutdownConfiguration(@Disposes Configuration configuration) {
        configuration.shutdown();
    }
}
