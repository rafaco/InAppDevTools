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

package org.inappdevtools.plugin

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.inappdevtools.plugin.config.ConfigHelper
import org.inappdevtools.plugin.setup.AppProjectSetup
import org.inappdevtools.plugin.setup.OtherProjectSetup
import org.inappdevtools.plugin.setup.RootProjectSetup
import org.inappdevtools.plugin.utils.ProjectUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.inappdevtools.plugin.utils.PluginUtils

class InAppDevToolsPlugin implements Plugin<Settings> {

    static final TAG = 'inappdevtools'

    ProjectUtils projectUtils
    ConfigHelper configHelper

    void apply(Settings settings) {
        println "IADT apply for settings $settings"
        println "IADT InAppDevTools ${PluginUtils.getVersion()} from Settings"

        //settings.getPluginManager().apply('org.inappdevtools')

        settings.getGradle().addBuildListener(new BuildListener() {
            @Override
            void settingsEvaluated(Settings evaluatedSettings) {
                println "IADT settingsEvaluated $evaluatedSettings"
            }

            @Override
            void projectsLoaded(Gradle gradle) {
                println "IADT projectsLoaded $gradle"

            }

            @Override
            void projectsEvaluated(Gradle gradle) {
                println "IADT projectsEvaluated $gradle"
            }

            @Override
            void buildFinished(BuildResult buildResult) {
                println "IADT buildFinished $buildResult"
            }
        })

        settings.getGradle().addProjectEvaluationListener(new ProjectEvaluationListener() {
            @Override
            void beforeEvaluate(Project project) {
                println "IADT beforeEvaluate $project"
                if (new ProjectUtils(project).isRoot()) {
                    new RootProjectSetup(project).beforeEvaluate()
                }
            }

            @Override
            void afterEvaluate(Project project, ProjectState projectState) {
                println "IADT afterEvaluate $project"
                projectUtils = new ProjectUtils(project)

                //TODO: Check isEnabled? IMPORTANT!
                //  optionally if not enabled globally:
                //   - stop linking output folder (project.android.sourceSets.main.assets.srcDirs += outputFolder.getParent())
                //   - if noopEnabled -> afterEvaluate add noop dependency, skip repository
                //   - add Noop tasks from TaskHelper... or direct cleanup bypassing tasks

                if (projectUtils.isRoot()) {
                    new RootProjectSetup(project).afterEvaluate()
                }
                else if (projectUtils.isAndroidApplication()) {
                    new AppProjectSetup(project).afterEvaluate()
                }
                else {
                    new OtherProjectSetup(project).afterEvaluate()
                }
            }
        })
    }
}
