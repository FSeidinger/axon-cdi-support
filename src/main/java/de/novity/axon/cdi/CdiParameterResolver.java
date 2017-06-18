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

import org.axonframework.messaging.Message;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.UnsatisfiedResolutionException;

@SuppressWarnings("WeakerAccess")
public class CdiParameterResolver implements ParameterResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Instance<?> parameterInstance;

    public CdiParameterResolver(Instance<?> parameterInstance) {
        this.parameterInstance = parameterInstance;
    }

    @Override
    public Object resolveParameterValue(Message message) {
        try {
            return parameterInstance.get();
        } catch (UnsatisfiedResolutionException | AmbiguousResolutionException e) {
            logger.warn(e.getMessage());
        }

        return null;
    }

    @Override
    public boolean matches(Message message) {
        return true;
    }
}
