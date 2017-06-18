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

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.axonframework.messaging.GenericMessage;
import org.axonframework.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Instance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CdiParameterResolverTest {
    private CdiParameterResolver resolver;

    @Mocked
    private Instance<Object> instance;

    @BeforeEach
    void setup() {
        resolver = new CdiParameterResolver(instance);
    }

    @Test
    void resolverAcceptsNullMessage() {
        assertTrue(resolver.matches(null));
    }

    @Test
    void resolverAcceptsSimpleMessage() {
        final Object payload  = new Object();
        final Message<Object> simpleMessage = new GenericMessage<>(payload);

        assertTrue(resolver.matches(simpleMessage));
    }

    @Test
    void resolverResolvesParameter() {
        final Object value = new Object();

        new Expectations() {{
            instance.get();
            result = value;
        }};

        assertEquals(resolver.resolveParameterValue(null), value);

        new Verifications() {{
            instance.get();
            times = 1;
        }};
    }
}