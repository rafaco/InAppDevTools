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


import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log
import org.inappdevtools.plugin.config.IadtConfigFields

abstract class StringConfigReader implements IConfigReader {

    def data

    StringConfigReader(Project project) {
    }

    boolean has(String field){
        return data && data.containsKey(getKey(field))
    }

    boolean hasValidValue(String field){
        def isValid
        def value = data[getKey(field)]
        Class<?> targetClass = IadtConfigFields.getType(field)
        switch (targetClass) {
            case Boolean:
                isValid = value != null && ["true", "false"].contains(value.toLowerCase())
                break
            case String:
            case Map:
            case Class.forName("[Ljava.lang.String;"):
                //TODO: validate Map and List
                isValid = value != null
                break
            default:
                isValid = false
                break
        }
        if (!isValid){
            Log.warn("IADT: Ignored an invalid config value in ${getName()}. '$field':'$value') ")
            return false
        }
        return true
    }

    Object get(String field) {
        if (!has(field))
            return null
        def value = data[getKey(field)]
        Class<?> targetClass = IadtConfigFields.getType(field)
        switch (targetClass) {
            case Boolean:
                return value.toBoolean()
            case String:
                return value
            case Class.forName("[Ljava.lang.String;"):
                def parsed = new JsonSlurper().parseText(value)
                return parsed as String[]
            case Map:
                def parsed = new JsonSlurper().parseText(value)
                return parsed as Map
            default:
                return value.asType(targetClass)
        }
    }
}