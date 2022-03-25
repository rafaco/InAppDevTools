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

package org.inappdevtools.plugin.config.readers


import org.gradle.api.Project
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log
import org.inappdevtools.plugin.config.IadtConfigFields

class DefaultConfigReader implements IConfigReader {

    static final String NAME = 'Default'

    DefaultConfigReader(Project project) {
    }

    boolean has(String field){
        return true
    }

    boolean hasValidValue(String field){
        // Should be always valid, but we ensure it exists in the default values list
        if (IadtConfigFields.getDefault(field) == null){
            Log.error("IADT: Ignored an invalid config value in ${getName()}. '$field' not found) ")
            return false
        }
        return true
    }

    Object get(String field) {
        return IadtConfigFields.getDefault(field)
    }

    String getName() {
        return NAME
    }

    String getKey(String field){
        return field
    }
}