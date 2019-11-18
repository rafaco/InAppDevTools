/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.logic.events;

public abstract class EventDetector extends EventSubscriber {

    public EventDetector(EventManager eventManager) {
        super(eventManager);
    }

    /**
     * Called after creation
     */
    public abstract void subscribe();

    /**
     * Called by EventDetectorsManager before all initializations.
     * Allow to start the watcher manually. Useful after calling {@link #stop()}.
     */
    public abstract void start();

    /**
     * Called by EventDetectorsManager on destroy.
     * Allow to stop the watcher manually. Use {@link #start()} to restart it.
     */
    public abstract void stop();

}
