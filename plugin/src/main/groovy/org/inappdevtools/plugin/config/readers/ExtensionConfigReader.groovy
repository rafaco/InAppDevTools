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

import org.inappdevtools.plugin.InAppDevToolsExtension
import org.inappdevtools.plugin.InAppDevToolsPlugin
import org.inappdevtools.plugin.config.IadtConfigFields
import org.gradle.api.Project
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log

class ExtensionConfigReader implements IConfigReader {

    static final String NAME = 'Extension'

    InAppDevToolsExtension extension

    ExtensionConfigReader(Project project) {
        this.extension = project.rootProject.extensions.getByName(InAppDevToolsPlugin.TAG)
    }

    boolean has(String field){
        return extension &&
                extension.hasProperty(getKey(field)) &&
                extension[getKey(field)] != null
    }

    boolean hasValidValue(String field){
        // Always valid, the build will fails if found a value from wrong type
        def value = extension[getKey(field)]
        Class<?> targetClass = IadtConfigFields.getType(field)
        try{
            targetClass.cast(value)
        }catch(Exception e){
            Log.warn("IADT: Ignored an invalid config value in ${getName()}. '$field':'$value') ", e)
            return false
        }
        return true
    }

    Object get(String field) {
        if (!has(field))
            return null
        def value = extension[getKey(field)]
        Class<?> targetClass = IadtConfigFields.getType(field)
        return targetClass.cast(value)
    }

    String getName() {
        return NAME
    }

    String getKey(String field){
        return field
    }
}