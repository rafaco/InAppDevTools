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

package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;

public class ForegroundInitSubscriber extends EventSubscriber {

    public ForegroundInitSubscriber(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.USER_PRESENT, new EventManager.OneShotListener() {
            @Override
            public void onEvent(Event event, Object param) {
                IadtController.get().initForegroundIfPending();
            }
        });

        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.OneShotListener() {
            @Override
            public void onEvent(Event event, Object param) {
                IadtController.get().initForegroundIfPending();
            }
        });
    }
}
