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


import org.inappdevtools.plugin.config.parser.ConfigParser
import org.inappdevtools.plugin.config.IadtConfigFields
import org.inappdevtools.plugin.config.ConfigHelper
import org.inappdevtools.plugin.workers.AddPluginsJob
import org.inappdevtools.plugin.workers.AddRepositoriesJob
import org.inappdevtools.plugin.workers.AddTasksJob
import org.inappdevtools.plugin.workers.RecordInternalPackageJob
import groovy.json.JsonOutput
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.inappdevtools.plugin.utils.PluginUtils
import org.gradle.plugins.ide.eclipse.internal.AfterEvaluateHelper

class InAppDevToolsPlugin implements Plugin<Project> {

    static final TAG = 'inappdevtools'
    static final ASSETS_PATH = '/generated/assets'
    static final OUTPUT_PATH = ASSETS_PATH + '/iadt'

    InAppDevToolsExtension extension
    org.inappdevtools.plugin.utils.ProjectUtils projectUtils
    ConfigHelper configHelper
    File outputFolder

    void apply(Project project) {
        //println "IADT apply for $project"
        projectUtils = new org.inappdevtools.plugin.utils.ProjectUtils(project)

        if (projectUtils.isRoot()) {
            onApplyToRoot(project)
        }

        //TODO: Check isEnabled? IMPORTANT!
        //  optionally if not enabled globally:
        //   - stop linking output folder (project.android.sourceSets.main.assets.srcDirs += outputFolder.getParent())
        //   - if noopEnabled -> afterEvaluate add noop dependency, skip repository
        //   - add Noop tasks from TaskHelper... or direct cleanup bypassing tasks

        AfterEvaluateHelper.afterEvaluateOrExecute(project, new Action<Project>() {
            @Override
            void execute(Project project2) {
                //println "IADT Apply after evaluate for $project"

                configHelper = new ConfigHelper(project)

                if (projectUtils.isRoot()){
                    new ConfigParser(configHelper)
                    afterEvaluateRoot(project)
                }
                else if (projectUtils.isAndroidApplication()) {
                    //TODO: Unlock for any Android Modules
                    afterEvaluateAndroidModule(project)
                }
                else{
                    if (configHelper.get(IadtConfigFields.DEBUG)){
                        println "IADT skipped for ${project} project. " +
                                "Only Android Application modules are currently supported."
                    }
                }
            }
        })
    }

    private void onApplyToRoot(Project project) {
        // Init configuration extension
        extension = project.extensions.create(TAG, InAppDevToolsExtension)

        // Apply to all submodules, we will filter them afterEvaluate
        project.subprojects { subproject ->
            //TODO: Filter modules from configuration?
            //println "IADT root: apply plugin to ${subproject}"
            subproject.getPluginManager().apply('org.inappdevtools')
        }
    }

    private void afterEvaluateRoot(Project project) {

        if (configHelper.get(IadtConfigFields.DEBUG)) {
            def gradleVersion = project.gradle.gradleVersion
            def androidPluginVersion = new org.inappdevtools.plugin.utils.AndroidPluginUtils(projectUtils.getProject()).getVersion()
            println "IADT InAppDevTools ${PluginUtils.getVersion(this)}"
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

    private void afterEvaluateAndroidModule(Project project) {
        println "IADT InAppDevTools ${PluginUtils.getVersion(this)}"
        String opMode = projectUtils.useAndroidX() ? "ANDROIDX artifact" : "SUPPORT artifact"
        String noopMode = configHelper.get(IadtConfigFields.USE_NOOP) ? "NOOP artifact" : "Nothing"
        if (configHelper.get(IadtConfigFields.ENABLED)) {
            if (configHelper.get(IadtConfigFields.EXCLUDE).length>0) {
                println "IADT   ENABLED for ${configHelper.calculateInclude()} builds --> $opMode"
                println "IADT   DISABLED for ${configHelper.get(IadtConfigFields.EXCLUDE)} builds --> $noopMode"
            }
            else{
                println "IADT   ENABLED for ALL builds --> $opMode"
            }
        }
        else {
            println "IADT   DISABLED for ALL builds --> $noopMode"
        }
        if (!configHelper.get(IadtConfigFields.ENABLED) && !configHelper.get(IadtConfigFields.USE_NOOP)) {
            if (configHelper.get(IadtConfigFields.DEBUG)) {
                println "IADT Skipping everything (disabled and don't use noop)"
            }
            return
        }

        if (configHelper.get(IadtConfigFields.DEBUG)) {
            projectUtils.printProjectType()
            projectUtils.printDimensions()
            //projectUtils.printBuildTypes()
            //projectUtils.printFlavors()
            println "IADT prepare project:"
        }
        applyToAndroidModule(project)
    }

    private void applyToAndroidModule(Project project) {
        initOutputFolder(project)
        if (projectUtils.isAndroidApplication()) {
            new RecordInternalPackageJob(this, project).do()
            new AddPluginsJob(this, project).do()
            new AddRepositoriesJob(this, project).do()
            new org.inappdevtools.plugin.workers.AddDependenciesJob(this, project).do()
        }
        new AddTasksJob(this, project).do()
    }

    //region [ INIT PLUGIN ]

    private void initOutputFolder(Project project) {
        if (configHelper.get(IadtConfigFields.DEBUG)) {
            println "IADT   init output folder."
        }

        // Prepare output folder
        outputFolder = getOutputDir(project)
        outputFolder.mkdirs()

        // Include output folder in source sets
        //TODO: Is variant filter needed? I believe they get exclude but I don't remember where
        project.android.sourceSets.main.assets.srcDirs += outputFolder.getParent()

        // Include output folder in standard clean task
        project.tasks.clean {
            delete getOutputPath(project)
        }
    }

    //endregion

    //region [ STATIC ACCESS TO PLUGIN ]

    static InAppDevToolsExtension getExtension(Project project) {
        project.rootProject.extensions.getByName(TAG)
    }

    static String getOutputPath(Project project){
        "${project.buildDir}${OUTPUT_PATH}"
    }

    static File getOutputDir(Project project){
        project.file(getOutputPath(project))
    }

    static File getOutputFile(Project project, String filename){
        project.file("${getOutputPath(project)}/${filename}")
    }

    //endregion
}
