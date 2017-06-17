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

import org.axonframework.common.Assert;
import org.axonframework.messaging.annotation.FixedValueParameterResolver;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Set;

@ApplicationScoped
public class CdiParameterResolverFactory implements ParameterResolverFactory {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final BeanManager manager;

    @Inject
    public CdiParameterResolverFactory(BeanManager manager) {
        Assert.notNull(manager, () -> "The bean manager must not be null");
        this.manager = manager;
    }

    @Override
    public ParameterResolver createInstance(Executable executable, Parameter[] parameters, int parameterIndex) {
        final Class<?> parameterClass = parameters[parameterIndex].getType();
        final Annotation[] parameterQualifiers = parameterClass.getAnnotationsByType(javax.inject.Qualifier.class);
        final Set<Bean<?>> beanCandidates = manager.getBeans(parameterClass, parameterQualifiers);

        try {
            final Bean<?> bean = manager.resolve(beanCandidates);

            if (bean != null) {
                final CreationalContext<?> context = manager.createCreationalContext(bean);
                final Object value = manager.getReference(bean, parameterClass, context);
                return new FixedValueParameterResolver(value);
            } else {
                logger.warn("No bean resolution for parameter type {}", parameterClass);
            }
        } catch (AmbiguousResolutionException e) {
            logger.warn("Ambiguous bean resolution for parameter type {}\n  {}", parameterClass, e.getMessage());
        }

        return null;
    }
}
