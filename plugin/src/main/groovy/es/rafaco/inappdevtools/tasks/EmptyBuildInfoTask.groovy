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

package es.rafaco.inappdevtools.tasks

import es.rafaco.inappdevtools.config.ConfigHelper
import es.rafaco.inappdevtools.config.IadtConfigFields
import es.rafaco.inappdevtools.utils.FileExporter
import es.rafaco.inappdevtools.utils.ProjectUtils
import org.gradle.api.tasks.TaskAction

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
