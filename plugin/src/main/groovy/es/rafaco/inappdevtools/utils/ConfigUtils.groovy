/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.utils

import es.rafaco.inappdevtools.InAppDevToolsPlugin
import groovy.json.JsonOutput
import org.gradle.api.Project

class ConfigUtils {

    Project project

    ConfigUtils(Project project) {
        this.project = project
    }

    private boolean isDebug(){
        return InAppDevToolsPlugin.isDebug(project)
    }

    private String getTag(){
        return InAppDevToolsPlugin.TAG
    }

    void writeMap(File file, Map map) {
        if (file.exists() && (map == null || map.size() == 0)){
            file.delete()
            return
        }
        String extensionJson
        extensionJson = JsonOutput.prettyPrint(JsonOutput.toJson(map))
        if (file.exists() && file.text == extensionJson){
            return
        }
        file.write extensionJson
        if (isDebug()) {
            println "Generated: " + file.getPath()
            println extensionJson
        }
    }

    void writeString(File file, String content) {
        if (content == null || content == ''){
            if (file.exists()) file.delete()
            return
        }
        if (file.exists() && file.text == content){
            return
        }
        file.write content
        if (isDebug()) println "Generated: " + file.getPath()
    }

    String shell(String command) {
        String result = null
        try {
            if (isDebug()) println ("Shell command: " + command)
            result = command.execute([], project.rootProject.rootDir).text.trim()
        }
        catch (Exception e) {
            if (isDebug()) e.printStackTrace()
        }
        result
    }

    boolean canShell(String command) {
        try {
            command.execute([], project.rootProject.rootDir)
        }
        catch (Exception e) {
            e.printStackTrace()
            return false
        }
        return true
    }
}
