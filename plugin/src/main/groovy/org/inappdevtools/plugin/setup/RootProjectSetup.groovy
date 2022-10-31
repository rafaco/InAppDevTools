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

package org.inappdevtools.plugin.setup

import groovy.json.JsonOutput
import org.gradle.api.Project
import org.inappdevtools.plugin.InAppDevToolsExtension
import org.inappdevtools.plugin.InAppDevToolsPlugin
import org.inappdevtools.plugin.config.ConfigHelper
import org.inappdevtools.plugin.config.ConfigParser
import org.inappdevtools.plugin.config.IadtConfigFields
import org.inappdevtools.plugin.utils.AndroidPluginUtils
import org.inappdevtools.plugin.utils.PluginUtils
import org.inappdevtools.plugin.utils.ProjectUtils

class RootProjectSetup {

    Project project
    ProjectUtils projectUtils
    ConfigHelper configHelper

    RootProjectSetup(Project project) {
        this.project = project
        this.projectUtils = new ProjectUtils(project)
        this.configHelper = new ConfigHelper(project)
    }

    void beforeEvaluate() {
        println "IADT beforeEvaluate $project"
        // Init configuration extension
        project.extensions.create(InAppDevToolsPlugin.TAG, InAppDevToolsExtension)
        // onApplyToRoot(project)
    }

    private void onApplyToRoot(Project project) {
        println "IADT onApplyToRoot $project"

        // Apply to all submodules, we will filter them afterEvaluate
        project.subprojects { subproject ->
            //TODO: Filter modules from configuration?
            //println "IADT root: apply plugin to ${subproject}"
            //subproject.getPluginManager().apply('org.inappdevtools')
        }
    }

    void afterEvaluate() {
        new ConfigParser(configHelper)

        if (configHelper.get(IadtConfigFields.DEBUG)) {
            def gradleVersion = project.gradle.gradleVersion
            def androidPluginVersion = new AndroidPluginUtils(projectUtils.getProject()).getVersion()
            println "IADT InAppDevTools ${PluginUtils.getVersion()}"
            println "IADT Build info:"
            println "IADT   Gradle $gradleVersion"
            println "IADT   Android Gradle Plugin $androidPluginVersion"
            println "IADT   Start task " + project.getGradle().getStartParameter().taskRequests[0].getArgs()[0]
            println "IADT Configurations affecting build:"
            println "IADT   enabled: " + configHelper.get(IadtConfigFields.ENABLED)
            println "IADT   exclude: " + configHelper.get(IadtConfigFields.EXCLUDE)
            println "IADT   useNoop: " + configHelper.get(IadtConfigFields.USE_NOOP)
            println "IADT   debug: " + configHelper.get(IadtConfigFields.DEBUG)
            println("IADT Configurations all: ")
            println(JsonOutput.prettyPrint(JsonOutput.toJson(configHelper.getAll())))
        }
    }
}
