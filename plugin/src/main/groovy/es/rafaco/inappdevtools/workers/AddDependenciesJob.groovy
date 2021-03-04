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

package es.rafaco.inappdevtools.workers

import es.rafaco.inappdevtools.InAppDevToolsPlugin
import org.gradle.api.Project

class AddDependenciesJob extends Job {

    AddDependenciesJob(InAppDevToolsPlugin plugin, Project project) {
        super(plugin, project)
    }

    def 'do'(){
        println "IADT add dependencies:"

        if (configHelper.isEnabled()){
            if (configHelper.hasExclude()){
                String[] exclude = configHelper.getExclude()
                String[] include = configHelper.calculateInclude()
                include.each {
                    addDependency(project, it,
                            "es.rafaco.inappdevtools", "library")
                }
                if (configHelper.isUseNoop()) {
                    exclude.each {
                        addDependency(project, it,
                                "es.rafaco.inappdevtools", "noop")
                    }
                }
                return
            }
            //All enabled
            addDependency(project, "",
                    "es.rafaco.inappdevtools", "library")
        }
        else {
            //All disable
            if (configHelper.isUseNoop()){
                addDependency(project, "",
                        "es.rafaco.inappdevtools", "noop")
            }
        }
    }

    private void addDependency(Project project, String configuration, String group, String id) {
        def isLocal = projectUtils.isLocalDev()
        String configName = configuration + (isLocal ? "Api" : "Implementation")
        configName = configName[0].toLowerCase() + configName.substring(1)
        String configValue

        if (isLocal){
            String modulePath = ":" + id
            configValue = "project([path: \"$modulePath\"])"
            /*project.dependencies.add(configName,
                    project.dependencies.project([path: modulePath]))*/
        } else {
            String extendedId = (id != "library") ? id :
                    projectUtils.useAndroidX() ? "androidx" : "support"
            configValue = group + ":" + extendedId + ":" + plugin.getPluginVersion()
            project.dependencies.add(configName, configValue)
        }
        println "IADT   ${configName} ${configValue}"
    }

}
