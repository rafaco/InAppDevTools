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

package es.rafaco.inappdevtools.config.parser

import org.gradle.api.Project

class EnvironmentConfigReader extends StringConfigReader {

    static final String NAME = 'Environment'
    static final String PREFIX = 'IADT_'

    EnvironmentConfigReader(Project project) {
        super(project)
        data = System.getenv()
    }

    String getName() {
        return NAME
    }

    String getKey(String field){
        return (PREFIX + toSnakeCase(field)).toUpperCase()
    }

    String toSnakeCase(String text) {
        text.replaceAll( /([A-Z])/, /_$1/ ).toLowerCase().replaceAll( /^_/, '' )
    }
}