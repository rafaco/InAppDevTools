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

package org.inappdevtools.plugin.utils

import org.gradle.api.Project

class DependencyUtils {

    Project project

    DependencyUtils(Project project) {
        this.project = project
    }

    String getCurrentVersion(File gradleDependenciesTxt, String artifactKeyword) {
        String rawLine = findLine(gradleDependenciesTxt, artifactKeyword)
        if (rawLine.isEmpty()){
            return ""
        }
        return extractMaxVersion(cleanHead(rawLine))
    }

    String findLine(File file, String key) {
        def lines = file.readLines()
        def found = ""
        lines.find {
            if (it.contains(key)) {
                found = it
                return true //break
            }
            return false    //keep looping
        }
        return found
    }

    private String cleanHead(String line) {
        return line.drop(line.indexOf("--- ") + "--- ".length())
    }

    private String extractMaxVersion(String line) {
        String temp = line.drop(line.lastIndexOf(":") + ":".length())
        boolean isOverride = temp.indexOf(" -> ") > 0
        if (isOverride){
            return temp.drop(temp.indexOf(" -> ") + " -> ".length())
        }
        boolean isMax = temp.indexOf(" (*)") > 0
        if (isMax){
            return temp.reverse().drop(" (*)").reverse()
        }
        return temp
    }
}
