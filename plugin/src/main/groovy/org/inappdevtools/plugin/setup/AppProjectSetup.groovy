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


import org.gradle.api.Project
import org.inappdevtools.plugin.config.ConfigHelper
import org.inappdevtools.plugin.config.IadtConfigFields
import org.inappdevtools.plugin.utils.PluginUtils
import org.inappdevtools.plugin.utils.ProjectUtils
import org.inappdevtools.plugin.workers.*

class AppProjectSetup {

    Project project
    ProjectUtils projectUtils
    ConfigHelper configHelper

    AppProjectSetup(Project project) {
        this.project = project
        this.projectUtils = new ProjectUtils(project)
        this.configHelper = new ConfigHelper(project)
    }

    void beforeEvaluate() {
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
        println "IADT InAppDevTools ${PluginUtils.getVersion()}"

        printEnabledInfo()
        if (!configHelper.get(IadtConfigFields.ENABLED) && !configHelper.get(IadtConfigFields.USE_NOOP)) {
            if (configHelper.get(IadtConfigFields.DEBUG)) {
                println "IADT Skipping everything (disabled and don't use noop)"
            }
            return
        }
        printDebugInfo()

        initOutputFolder(project)
        applyToAndroidModule(project)
    }

    private void printDebugInfo() {
        if (configHelper.get(IadtConfigFields.DEBUG)) {
            projectUtils.printProjectType()
            projectUtils.printDimensions()
            //projectUtils.printBuildTypes()
            //projectUtils.printFlavors()
            println "IADT prepare project:"
        }
    }

    private void printEnabledInfo() {
        if (configHelper.get(IadtConfigFields.ENABLED)) {
            ArrayList<String> excludeConfig = configHelper.get(IadtConfigFields.EXCLUDE)
            String opMode = projectUtils.useAndroidX() ? "ANDROIDX artifact" : "SUPPORT artifact"
            String noopMode = configHelper.get(IadtConfigFields.USE_NOOP) ? "NOOP artifact" : "Nothing"
            if (excludeConfig != null && excludeConfig.size > 0) {
                println "IADT   ENABLED for ${configHelper.calculateInclude()} builds --> $opMode"
                println "IADT   DISABLED for ${excludeConfig} builds --> $noopMode"
            } else {
                println "IADT   ENABLED for ALL builds --> $opMode"
            }
        } else {
            println "IADT   DISABLED for ALL builds --> $noopMode"
        }
    }

    private void initOutputFolder(Project project) {
        if (configHelper.get(IadtConfigFields.DEBUG)) {
            println "IADT   init output folder."
        }

        // Prepare output folder
        def outputFolder = projectUtils.getOutputDir()
        outputFolder.mkdirs()

        // Include output folder in source sets
        //TODO: Is variant filter needed? I believe they get exclude but I don't remember where
        project.android.sourceSets.main.assets.srcDirs += outputFolder.getParent()

        // Include output folder in standard clean task
        project.tasks.clean {
            delete projectUtils.getOutputPath()
        }
    }

    private void applyToAndroidModule(Project project) {
        if (projectUtils.isAndroidApplication()) {
            new RecordInternalPackageJob(project).do()
            new AddPluginsJob(project).do()
            new AddRepositoriesJob(project).do()
            new AddDependenciesJob(project).do()
        }
        new AddTasksJob(project).do()
    }
}
