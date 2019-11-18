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

package es.rafaco.inappdevtools.library.view.notifications;

import android.app.NotificationManager;

public enum IadtChannel {

    CHANNEL_PRIORITY("es.rafaco.iadt.priority", "Iadt Priority", NotificationManager.IMPORTANCE_HIGH,
            "Notifications pop up over other screens"),
    CHANNEL_STANDARD("es.rafaco.iadt.standard", "Iadt Standard", NotificationManager.IMPORTANCE_DEFAULT,
            "Non intrusive notifications, they use device settings for sound and vibrate"),
    CHANNEL_SILENT("es.rafaco.iadt.silent", "Iadt Silent", NotificationManager.IMPORTANCE_LOW,
            "Non intrusive and always silent notifications");

    private String id;
    private String name;
    private String description;
    private int priority;

    IadtChannel(String id, String name, int priority, String description) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }
}
