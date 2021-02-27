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

import es.rafaco.inappdevtools.InAppDevToolsExtension
import es.rafaco.inappdevtools.InAppDevToolsPlugin
import es.rafaco.inappdevtools.utils.ProjectUtils
import org.gradle.api.Project
import org.gradle.api.Task

class ConfigHelper {

    Project project

    ConfigHelper(Project project) {
        this.project = project
    }

    Project getProject() {
        project
    }

    InAppDevToolsExtension getExtension() {
        project.rootProject.extensions.getByName(InAppDevToolsPlugin.TAG)
    }

    //region [ DIRECT PROPERTIES ]

    boolean isDebug(){
        if (getExtension()!=null){
            return getExtension().debug
        }
        return false
    }

    boolean isEnabled(){
        if (getExtension()!=null){
            return getExtension().enabled
        }
        return true
    }

    boolean isEnabledOnRelease(){
        InAppDevToolsExtension extension = getExtension()
        if (extension!=null && extension.enabledOnRelease!=null){
            return extension.enabledOnRelease
        }
        return false
    }

    boolean isSourceInclusion(){
        InAppDevToolsExtension extension = getExtension()
        if (extension!=null && extension.sourceInclusion!=null){
            return extension.sourceInclusion
        }
        return true
    }

    boolean isSourceInspection(){
        InAppDevToolsExtension extension = getExtension()
        if (extension!=null && extension.sourceInspection!=null){
            return extension.sourceInspection
        }
        return true
    }

    boolean isNetworkInterceptor(){
        InAppDevToolsExtension extension = getExtension()
        if (extension!=null && extension.networkInterceptor!=null){
            return extension.networkInterceptor
        }
        return true
    }

    //endregion

    //region [ COMPUTED PROPERTIES ]

    boolean isPluginEnabled(Task buildTask) {
        return isEnabled() &&
                (!isReleaseTask(buildTask) ||
                        (isReleaseTask(buildTask) && isEnabledOnRelease()))
    }

    boolean isReleaseTask(Task task){
        return task.name.contains("Release")
    }

    boolean shouldIncludeSources(Task task) {
        return isPluginEnabled(task) &&
                isSourceInclusion() &&
                isSourceInspection()
    }

    boolean isNoopIncluded(String variantName) {
        ProjectUtils projectUtils = new ProjectUtils(project)
        def variantConfigurationName = variantName.uncapitalize() + 'Runtime' + 'Classpath'
        if (!projectUtils.existsConfiguration(variantConfigurationName)) {
            if (isDebug) println "Skipped by error: configuration not found ${variantConfigurationName}"
            return
        }
        boolean isNoop = false
        def currentConfiguration = projectUtils.getConfigurations().getByName(variantConfigurationName)
        currentConfiguration.allDependencies.each {dep ->
            //println "Iadt Plugin3 $currentConfiguration.name > ${dep.group}:${dep.name}:${dep.version}"
            if (dep.group=='inappdevtools' && dep.name=='noop'){
                //InAppDevTools project
                isNoop = true
            }else if (dep.group=='es.rafaco.inappdevtools' && dep.name=='noop') {
                //External projects
                isNoop = true
            }
        }
        isNoop
    }

    //endregion
}
