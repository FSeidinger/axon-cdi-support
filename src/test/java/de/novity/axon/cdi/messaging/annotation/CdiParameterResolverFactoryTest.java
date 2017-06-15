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

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CdiParameterResolverFactoryTest {
    @Tested
    private CdiParameterResolverFactory factory;

    @Injectable
    private BeanManager manager;

    @Mocked
    private Bean mockedBean;

    @Mocked
    private Bean anotherMockedBean;

    @Test
    public void factoryResolvesParameterType() throws Exception {
        final SimpleTarget target = new SimpleTarget();
        final Executable targetMethod = target.getClass().getMethods()[0];
        final Parameter parameters[] = targetMethod.getParameters();
        final Set<Bean> beans = new HashSet<>(Arrays.asList(mockedBean));

        new Expectations() {{
            manager.getBeans(withInstanceOf(Type.class), withAny(new Annotation[0]));
            result = beans;

            manager.getReference(withInstanceOf(Bean.class), withInstanceOf(Type.class), withInstanceOf(CreationalContext.class));
            result = new FirstBean();
        }};

        final ParameterResolver<SimpleBean> resolver = factory.createInstance(targetMethod, parameters, 0);
        final SimpleBean simpleBean = resolver.resolveParameterValue(null);

        assertNotNull(resolver);
        assertNotNull(simpleBean);
    }

    @Test
    public void factoryRejectsAmbiguousParameterType() throws Exception {
        final SimpleTarget target = new SimpleTarget();
        final Executable targetMethod = target.getClass().getMethods()[0];
        final Parameter parameters[] = targetMethod.getParameters();

        new Expectations() {{
            manager.getBeans(withInstanceOf(Type.class), withAny(new Annotation[0]));
            result = Collections.EMPTY_SET;

            manager.resolve(withInstanceOf(Set.class));
            result = null;
        }};

        final ParameterResolver resolver = factory.createInstance(targetMethod, parameters, 0);

        assertNull(resolver);
    }

    @Test
    public void factoryRejectsUnknownParameterType() throws Exception {
        final SimpleTarget target = new SimpleTarget();
        final Executable targetMethod = target.getClass().getMethods()[0];
        final Parameter parameters[] = targetMethod.getParameters();
        final Set<Bean> beans = new HashSet<>(Arrays.asList(mockedBean, anotherMockedBean));

        new Expectations() {{
            manager.getBeans(withInstanceOf(Type.class), withAny(new Annotation[0]));
            result = beans;

            manager.resolve(withInstanceOf(Set.class));
            result = null;
        }};

        final ParameterResolver resolver = factory.createInstance(targetMethod, parameters, 0);

        assertNull(resolver);
    }
}