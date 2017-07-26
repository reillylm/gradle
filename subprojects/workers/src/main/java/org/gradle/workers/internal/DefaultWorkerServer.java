/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.workers.internal;

import org.gradle.api.internal.AsmBackedClassGenerator;
import org.gradle.api.internal.DefaultInstantiatorFactory;
import org.gradle.internal.reflect.Instantiator;

public class DefaultWorkerServer implements WorkerServer<ActionExecutionSpec> {
    private static final Instantiator DEFAULT_INSTANTIATOR = new DefaultInstantiatorFactory(new AsmBackedClassGenerator()).inject();

    @Override
    public DefaultWorkResult execute(ActionExecutionSpec spec) {
        return execute(spec, null);
    }

    @Override
    public DefaultWorkResult execute(ActionExecutionSpec spec, Instantiator instantiator) {
        try {
            Class<? extends Runnable> implementationClass = spec.getImplementationClass();
            if (instantiator == null) {
                instantiator = DEFAULT_INSTANTIATOR;
            }
            Runnable runnable = instantiator.newInstance(implementationClass, spec.getParams(implementationClass.getClassLoader()));
            runnable.run();
            return new DefaultWorkResult(true, null);
        } catch (Throwable t) {
            return new DefaultWorkResult(true, t);
        }
    }


    @Override
    public String toString() {
        return "DefaultWorkerServer{}";
    }
}
