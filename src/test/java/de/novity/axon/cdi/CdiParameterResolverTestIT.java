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

import de.novity.axon.cdi.test.it.api.AmbiguousDependency;
import de.novity.axon.cdi.test.it.api.AnotherDependency;
import de.novity.axon.cdi.test.it.api.SimpleDependency;
import de.novity.axon.cdi.test.it.infrastructure.AlternativeAmbiguousDependency;
import de.novity.axon.cdi.test.it.infrastructure.DefaultAmbiguousDependency;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Instance;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CdiParameterResolverTestIT {
    // Dependencies
    private WeldContainer container;

    @BeforeEach
    void setUp() {
        container = new Weld()
                .containerId("CDI test environment")
                .addBeanClass(SimpleDependency.class)
                .addBeanClass(DefaultAmbiguousDependency.class)
                .addBeanClass(AlternativeAmbiguousDependency.class)
                .disableDiscovery()
                .initialize();
    }

    @AfterEach
    void tearDown() {
        if (container != null) {
            container.close();
        }
    }

    @Test
    void resolverSucceedsWithResolvedInstance() {
        final Instance<SimpleDependency> simpleDependencyInstance = container.select(SimpleDependency.class);
        final CdiParameterResolver resolver = new CdiParameterResolver(simpleDependencyInstance);

        final SimpleDependency simpleDependency = (SimpleDependency) resolver.resolveParameterValue(null);
        assertNotNull(simpleDependency);
    }

    @Test
    void resolverFailsWithUnresolvedInstance() {
        final Instance<AnotherDependency> anotherDependencyInstance = container.select(AnotherDependency.class);
        final CdiParameterResolver resolver = new CdiParameterResolver(anotherDependencyInstance);

        final AnotherDependency anotherDependency = (AnotherDependency) resolver.resolveParameterValue(null);
        assertNull(anotherDependency);
    }

    @Test
    void resolverFailsWithAmbiguousInstance() {
        final Instance<AmbiguousDependency> ambiguousDependencyInstance = container.select(AmbiguousDependency.class);
        final CdiParameterResolver resolver = new CdiParameterResolver(ambiguousDependencyInstance);

        final AmbiguousDependency ambiguousDependency = (AmbiguousDependency) resolver.resolveParameterValue(null);
        assertNull(ambiguousDependency);
    }
}