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

import de.novity.axon.cdi.test.unit.SimpleDependency;
import mockit.*;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CdiParameterResolverFactoryTest {
    @Tested
    private CdiParameterResolverFactory factory;

    @Mocked
    private CDI<Object> cdi;

    private Method simpleCommandHandlerMethod;
    private Parameter[] simpleCommandHandlerMethodParameters;

    @Mocked
    private Instance<SimpleDependency> simpleInstance;

    @BeforeEach
    void setup() throws Exception {
        new MockUp<CDI<Object>>() {
            @Mock
            CDI<Object> current() {
                return cdi;
            }
        };

        simpleCommandHandlerMethod = getClass().getMethod("handle", SimpleDependency.class);
        simpleCommandHandlerMethodParameters = simpleCommandHandlerMethod.getParameters();
    }

    @Test
    void factoryResolvesParameterType() throws Exception {
        final Parameter parameter = simpleCommandHandlerMethodParameters[0];
        final Annotation[] qualifiers = parameter.getAnnotationsByType(Qualifier.class);

        new Expectations() {{
            cdi.select(parameter.getType(), qualifiers);
            result = simpleInstance;
        }};

        final ParameterResolver<?> resolver = factory.createInstance(simpleCommandHandlerMethod, simpleCommandHandlerMethodParameters, 0);

        assertNotNull(resolver);

        new Verifications() {{
            //noinspection ConstantConditions
            cdi.select((Class<?>) any, (Annotation[]) any);
            times = 1;
        }};
    }

    @SuppressWarnings({"EmptyMethod", "WeakerAccess"})
    public void handle(SimpleDependency dependency) {
    }
}