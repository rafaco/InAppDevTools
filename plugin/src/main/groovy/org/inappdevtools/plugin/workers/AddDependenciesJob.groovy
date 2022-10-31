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

package org.inappdevtools.plugin.workers

import org.inappdevtools.plugin.config.IadtConfigFields
import org.inappdevtools.plugin.utils.PluginUtils
import org.gradle.api.Project

class AddDependenciesJob extends Job {

    AddDependenciesJob(Project project) {
        super(project)
    }

    def 'do'(){
        println "IADT add dependencies:"

        if (configHelper.get(IadtConfigFields.ENABLED)){
            if (configHelper.get(IadtConfigFields.EXCLUDE).size()>0){
                String[] exclude = configHelper.get(IadtConfigFields.EXCLUDE)
                String[] include = configHelper.calculateInclude()
                include.each {
                    addDependency(project, it,
                            "org.inappdevtools", "library")
                }
                if (configHelper.get(IadtConfigFields.USE_NOOP)) {
                    exclude.each {
                        addDependency(project, it,
                                "org.inappdevtools", "noop")
                    }
                }
                return
            }
            //All enabled
            addDependency(project, "",
                    "org.inappdevtools", "library")
        }
        else {
            //All disable
            if (configHelper.get(IadtConfigFields.USE_NOOP)){
                addDependency(project, "",
                        "org.inappdevtools", "noop")
            }
        }
    }

    private void addDependency(Project project, String configuration, String group, String id) {
        def isLocal = projectUtils.isLocalDev()
        String configName = configuration + (isLocal ? "Api" : "Implementation")
        configName = configName[0].toLowerCase() + configName.substring(1)
        String localConfigValue = "project([path: \":$id\"])"
        String externalId = (id != "library") ? id :
                projectUtils.useAndroidX() ? "androidx" : "support"
        String externalConfigValue = group + ":" + externalId + ":" + PluginUtils.getVersion()

        if (isLocal){
            println "IADT   ${configName} '${externalConfigValue}' SKIPPED (isLocalDev)"
            //println "IADT   ${configName} ${localConfigValue} (isLocalDev)"
            //project.dependencies.add(configName, project.dependencies.project([path: localConfigValue]))
        } else {
            println "IADT   ${configName} '${externalConfigValue}'"
            project.dependencies.add(configName, externalConfigValue)
        }
    }

}
