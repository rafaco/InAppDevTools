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

package org.inappdevtools.library.logic.events;

import org.inappdevtools.library.logic.events.subscribers.ConfigChangeSubscriber;
import org.inappdevtools.library.logic.events.subscribers.OverlayNavigationSubscriber;
import org.inappdevtools.library.logic.events.subscribers.OverlayRestoreSubscriber;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.logic.events.subscribers.*;
import org.inappdevtools.library.logic.utils.ClassHelper;

public class EventSubscribersManager {

    private final EventManager eventManager;
    private List<EventSubscriber> items = new ArrayList<>();

    public EventSubscribersManager(EventManager eventManager) {
        this.eventManager = eventManager;
        initItems();
    }

    private void initItems() {
        initItem(OverlayNavigationSubscriber.class);
        initItem(OverlayRestoreSubscriber.class);
        initItem(ConfigChangeSubscriber.class);
    }

    private void initItem(Class<? extends EventSubscriber> className) {
        EventSubscriber subscriber = new ClassHelper<EventSubscriber>().createClass(className,
                EventManager.class, eventManager);
        if (subscriber != null){
            items.add(subscriber);
        }
    }

    public EventSubscriber get(Class<? extends EventSubscriber> className) {
        for (EventSubscriber subscriber : items) {
            if (subscriber.getClass().equals(className)){
                return subscriber;
            }
        }
        return  null;
    }

    public void destroy() {
        items = null;
    }
}
