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

import de.novity.axon.cdi.de.novity.axon.cdi.test.it.App;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.api.SimpleCommand;
import org.axonframework.config.Configuration;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.spi.CDI;

class AxonConfigurationTestIT {
    private WeldContainer container;

    @BeforeEach
    void setup() {
        container = new Weld()
                .containerId("CDI test environment")
                .disableDiscovery()
                .addBeanClass(CdiParameterResolverFactory.class)
                .addPackages(true, App.class)
                .initialize();
    }

    @AfterEach
    void teardown() {
        if (container != null) {
            container.close();
        }
    }

    @Test
    void cdiBeanCanBeResolved() throws Exception {
        final SimpleCommand command = new SimpleCommand();

        final Configuration configuration = lookupConfiguration(container);
        configuration
                .commandGateway()
                .sendAndWait(command);
    }

    private Configuration lookupConfiguration(CDI<Object> container) {
        return container.select(Configuration.class).get();
    }
}
