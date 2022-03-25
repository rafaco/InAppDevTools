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

package org.inappdevtools.plugin.tasks


import org.inappdevtools.plugin.config.ConfigHelper
import org.inappdevtools.plugin.config.IadtConfigFields
import org.gradle.api.tasks.TaskAction
import org.inappdevtools.plugin.utils.FileExporter
import org.inappdevtools.plugin.utils.ProjectUtils

class EmptyBuildInfoTask extends IadtBaseTask {

    EmptyBuildInfoTask() {
        this.description = "Generate a build_config file for disabled Iadt (only contains enabled = false)"
    }

    @TaskAction
    void perform() {
        Map propertiesMap = [ enabled : false ]

        ProjectUtils projectUtils = new ProjectUtils(project)
        FileExporter fileExporter = new FileExporter(project)
        File file = projectUtils.getFile("${outputPath}/build_config.json")
        fileExporter.writeMap(file, propertiesMap)

        boolean isDebug = new ConfigHelper(project).get(IadtConfigFields.DEBUG)
        if (isDebug)
            println "   Generated empty build_config for Iadt disabled"
    }
}
