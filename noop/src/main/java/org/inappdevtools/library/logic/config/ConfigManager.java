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

package org.inappdevtools.library.logic.config;

import android.content.Context;

public class ConfigManager {

    public ConfigManager(Context context) {}

    public Object get(BuildConfigField config) {
        return null;
    }

    public void set(BuildConfigField config, Object value) {}

    public boolean getBoolean(BuildConfigField config) {
        return false;
    }

    public void setBoolean(BuildConfigField config, boolean value) {}

    public String getString(BuildConfigField config) {
        return "";
    }

    public void setString(BuildConfigField config, String value) {}

    public long getLong(BuildConfigField config) {
        return -1;
    }

    public void setLong(BuildConfigField config, long value) {}
}
