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

import de.novity.axon.cdi.test.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CdiParameterResolverFactoryIT {
    // Dependencies
    private WeldContainer container;
    private SimpleCommandHandler annotatedCommandHandler = new SimpleCommandHandler();
    private Executable commandHandler = findExecutable(annotatedCommandHandler);
    private Parameter[] parameters = commandHandler.getParameters();

    @AfterEach
    private void teardown() {
        if (container != null) {
            container.close();
        }
    }

    @Test
    public void factoryCanResolveValidParameter() throws Exception {
        container = new Weld()
                .containerId("CDI test environment")
                .disableDiscovery()
                .addBeanClass(CdiParameterResolverFactory.class)
                .addBeanClass(ASimpleDependency.class)
                .initialize();

        final CdiParameterResolverFactory factory = container.select(CdiParameterResolverFactory.class).get();

        ParameterResolver<SimpleDependency> resolver = factory.createInstance(commandHandler, parameters, 1);
        SimpleDependency dependency = resolver.resolveParameterValue(null);

        assertNotNull(resolver);
        assertNotNull(dependency);
    }

    @Test
    public void factoryFailsToResolveMissingParameter() {
        container = new Weld()
                .containerId("CDI test environment")
                .disableDiscovery()
                .addBeanClass(CdiParameterResolverFactory.class)
                .addBeanClass(AnotherDependency.class)
                .initialize();

        final CdiParameterResolverFactory factory = container.select(CdiParameterResolverFactory.class).get();

        ParameterResolver resolver = factory.createInstance(commandHandler, parameters, 1);
        assertNull(resolver);
    }

    @Test
    public void factoryFailsToResolveAmbiguousParameter() {
        container = new Weld()
                .containerId("CDI test environment")
                .disableDiscovery()
                .addBeanClass(CdiParameterResolverFactory.class)
                .addBeanClass(ASimpleDependency.class)
                .addBeanClass(BSimpleDependecy.class)
                .initialize();

        final CdiParameterResolverFactory factory = container.select(CdiParameterResolverFactory.class).get();

        ParameterResolver resolver = factory.createInstance(commandHandler, parameters, 1);
        assertNull(resolver);
    }

    private Executable findExecutable(Object target) {
        return Arrays.asList(target.getClass().getMethods())
                .stream()
                .filter(method -> method.getAnnotation(CommandHandler.class) != null)
                .findFirst()
                .get();
    }
}