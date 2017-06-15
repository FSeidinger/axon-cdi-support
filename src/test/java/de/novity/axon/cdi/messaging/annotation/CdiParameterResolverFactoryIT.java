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

package de.novity.axon.cdi.messaging.annotation;

import org.axonframework.messaging.annotation.ParameterResolver;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CdiParameterResolverFactoryIT {
    // SUT
    private CdiParameterResolverFactory factory;

    // Dependencies
    private Weld weld;
    private WeldContainer container;

    @BeforeEach
    private void setup() {
        weld = new Weld()
                .containerId("CDI test environment")
                .disableDiscovery();
    }

    @AfterEach
    private void teardown() {
        if (container != null) {
            container.close();
        }
    }

    @Test
    public void factoryCanResolveValidParameter() throws Exception {
        container = weld
                .addBeanClass(SimpleBean.class)
                .addBeanClass(FirstBean.class)
                .initialize();
        factory = new CdiParameterResolverFactory(container.getBeanManager());

        final SimpleTarget target = new SimpleTarget();
        final Executable targetMethod = target.getClass().getMethods()[0];
        final Parameter parameters[] = targetMethod.getParameters();

        ParameterResolver<SimpleBean> resolver = factory.createInstance(targetMethod, parameters, 0);
        SimpleBean simpleBean = resolver.resolveParameterValue(null);

        assertNotNull(resolver);
        assertNotNull(simpleBean);
    }

    @Test
    public void factoryFailsToResolveMissingParameter() {
        container = weld
                .addBeanClass(ThirdBean.class)
                .initialize();
        factory = new CdiParameterResolverFactory(container.getBeanManager());

        final SimpleTarget target = new SimpleTarget();
        final Executable targetMethod = target.getClass().getMethods()[0];
        final Parameter parameters[] = targetMethod.getParameters();

        ParameterResolver resolver = factory.createInstance(targetMethod, parameters, 0);
        assertNull(resolver);
    }

    @Test
    public void factoryFailsToResolveAmbiguousParameter() {
        container = weld
                .addBeanClass(FirstBean.class)
                .addBeanClass(SecondBean.class)
                .initialize();
        factory = new CdiParameterResolverFactory(container.getBeanManager());

        final SimpleTarget target = new SimpleTarget();
        final Executable targetMethod = target.getClass().getMethods()[0];
        final Parameter parameters[] = targetMethod.getParameters();

        ParameterResolver resolver = factory.createInstance(targetMethod, parameters, 0);
        assertNull(resolver);
    }
}