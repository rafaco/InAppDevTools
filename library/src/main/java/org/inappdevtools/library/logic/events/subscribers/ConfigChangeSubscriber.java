/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.events.subscribers;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.config.BuildConfigField;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.events.EventSubscriber;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class ConfigChangeSubscriber extends EventSubscriber {

    public ConfigChangeSubscriber(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.CONFIG_CHANGED, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                BuildConfigField target = (BuildConfigField) param;

                FriendlyLog.log("D", "Iadt", "BuildConfig",
                        "BuildConfig changed: " + target.getKey() + " to " + IadtController.get().getConfig().get(target));

                /*if (target.getKey().equals(BuildConfig.ENABLED.getKey())){
                    //TODO: restart library instead of app
                    Iadt.showMessage("Restart needed");
                    IadtController.get().restartApp(false);
                }*/
            }
        });
    }
}
