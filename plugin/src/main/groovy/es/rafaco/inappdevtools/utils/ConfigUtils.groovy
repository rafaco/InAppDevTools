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

    void saveConfigMap(Map map, File file) {
        String extensionJson
        extensionJson = JsonOutput.prettyPrint(JsonOutput.toJson(map))
        if (isDebug()) {
            println "Generated: " + file.getPath()
            println extensionJson
        }
        file.write extensionJson
    }

    String shell(String command) {
        //TODO: print output error
        //TODO: research why it lock the builds sometimes, like with long diffs or paged result.
        String result = null
        try {
            if (isDebug()) println ("Shell command: " + command)
            result = command.execute([], project.rootProject.rootDir).text.trim()
        }
        catch (java.io.IOException e) {
            println getTag() + "[WARNING]: " + "Unable to reach git command, check your PATH!"
            if (isDebug()) {
                e.printStackTrace()
            }
        }
        catch (Exception e) {
            println getTag() + "[WARNING]: " + "Unable to get git info"
            if (isDebug()) {
                e.printStackTrace()
            }
        }
        result
    }
}
