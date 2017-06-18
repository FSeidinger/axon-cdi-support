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

import org.axonframework.messaging.annotation.ParameterResolver;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

@ApplicationScoped
public class CdiParameterResolverFactory implements ParameterResolverFactory {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final CDI<Object> cdi = CDI.current();

    @Override
    public ParameterResolver createInstance(Executable executable, Parameter[] parameters, int parameterIndex) {
        final Class<?> parameterClass = parameters[parameterIndex].getType();
        final Annotation[] parameterQualifiers = parameterClass.getAnnotationsByType(javax.inject.Qualifier.class);

        final Instance<?> cdiInstance = cdi.select(parameterClass, parameterQualifiers);

        return new CdiParameterResolver(cdiInstance);
    }
}
