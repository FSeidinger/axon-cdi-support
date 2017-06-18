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

package de.novity.axon.cdi.de.novity.axon.cdi.test.it.model;

import de.novity.axon.cdi.de.novity.axon.cdi.test.it.api.AnotherDependency;
import de.novity.axon.cdi.de.novity.axon.cdi.test.it.api.SimpleCommand;
import org.axonframework.commandhandling.CommandHandler;

public class AnotherCommandHandler {
    @CommandHandler
    public void handle(final SimpleCommand command, final AnotherDependency anotherDependency) {
        anotherDependency.doSomething();
    }
}
