/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2021 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.config

import org.gradle.api.Project

class LocalPropertiesConfigReader {

    static final String NAME = 'local.properties'
    static final String PREFIX = 'iadt.'

    Properties localProperties

    LocalPropertiesConfigReader(Project project) {
        def propertiesFile = project.rootProject.file('local.properties')
        if (propertiesFile.exists()) {
            localProperties = new Properties()
            localProperties.load(propertiesFile.newDataInputStream())
        }
    }

    String getName() {
        return NAME
    }

    boolean hasConfig(String field){
        return localProperties && localProperties.containsKey(getKey(field))
    }

    Object getConfig(String field) {
        if (!hasConfig(field))
            return null
        return localProperties[getKey(field)]
    }

    String getKey(String field){
        return PREFIX + field
    }
}