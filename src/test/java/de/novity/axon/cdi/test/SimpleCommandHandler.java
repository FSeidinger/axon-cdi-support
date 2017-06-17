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

package de.novity.axon.cdi.test;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.Assert;

public class SimpleCommandHandler {
    @CommandHandler
    public void handl(final SimpleCommand command, final SimpleDependency dependency) {
        Assert.notNull(command, () -> "Command must not be null");
        Assert.notNull(dependency, () -> "Dependecy must not be null");

        dependency.doSomething();
    }
}
