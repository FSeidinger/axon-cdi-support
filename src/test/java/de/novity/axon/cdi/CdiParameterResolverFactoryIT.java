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

import de.novity.axon.cdi.de.novity.axon.cdi.test.it.api.AmbiguousDependency;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.api.AnotherDependency;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.api.SimpleCommand;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.api.SimpleDependency;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.infrastructure.AlternativeAmbiguousDependency;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.infrastructure.DefaultAmbiguousDependency;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.model.AmbiguousCommandHandler;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.model.AnotherCommandHandler;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.model.SimpleCommandHandler;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("uncheced")
class CdiParameterResolverFactoryIT {
    // SUT
    private CdiParameterResolverFactory factory;

    // Dependencies
    private WeldContainer container;
    private Method handlingSimpleBeanMethod;
    private Method handlingAnotherSimpleBeanMethod;
    private Method handlingAmbiguousBeanMethod;

    @BeforeEach
    void setup() throws Exception {
        container = new Weld()
                .containerId("CDI test environment")
                .addBeanClass(CdiParameterResolverFactory.class)
                .addBeanClass(SimpleDependency.class)
                .addBeanClass(DefaultAmbiguousDependency.class)
                .addBeanClass(AlternativeAmbiguousDependency.class)
                .disableDiscovery()
                .initialize();

        handlingSimpleBeanMethod = SimpleCommandHandler.class.getMethod("handle", SimpleCommand.class, SimpleDependency.class);
        handlingAnotherSimpleBeanMethod = AnotherCommandHandler.class.getMethod("handle", SimpleCommand.class, AnotherDependency.class);
        handlingAmbiguousBeanMethod = AmbiguousCommandHandler.class.getMethod("handle", SimpleCommand.class, AmbiguousDependency.class);
        factory = container.select(CdiParameterResolverFactory.class).get();
    }

    @AfterEach
    void teardown() {
        if (container != null) {
            container.close();
        }
    }

    @Test
    void factoryCanResolveValidParameter() throws Exception {
        final ParameterResolver<?> resolver = factory.createInstance(handlingSimpleBeanMethod, handlingSimpleBeanMethod.getParameters(), 1);
        final Object value = resolver.resolveParameterValue((Message<?>) null);

        assertNotNull(resolver);
        assertNotNull(value);
    }

    @Test
    void factoryFailsToResolveUnknownParameter() {
        final ParameterResolver<?> resolver = factory.createInstance(handlingAnotherSimpleBeanMethod, handlingAnotherSimpleBeanMethod.getParameters(), 1);
        final Object value = resolver.resolveParameterValue((Message<?>) null);

        assertNotNull(resolver);
        assertNull(value);
    }

    @Test
    void factoryFailsToResolveAmbiguousParameter() {
        final ParameterResolver<?> resolver = factory.createInstance(handlingAmbiguousBeanMethod, handlingAmbiguousBeanMethod.getParameters(), 1);
        final Object value = resolver.resolveParameterValue((Message<?>) null);

        assertNotNull(resolver);
        assertNull(value);
    }
}