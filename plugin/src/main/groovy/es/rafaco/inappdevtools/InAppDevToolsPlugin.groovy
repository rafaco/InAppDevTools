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

package es.rafaco.inappdevtools

import es.rafaco.inappdevtools.config.ConfigHelper
import es.rafaco.inappdevtools.tasks.TasksHelper
import es.rafaco.inappdevtools.utils.AndroidPluginUtils
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ProjectReportsPlugin
import es.rafaco.inappdevtools.utils.ProjectUtils
import org.gradle.plugins.ide.eclipse.internal.AfterEvaluateHelper
import tech.linjiang.pandora.gradle.PandoraPlugin

class InAppDevToolsPlugin implements Plugin<Project> {

    static final TAG = 'inappdevtools'
    static final ASSETS_PATH = '/generated/assets'
    static final OUTPUT_PATH = ASSETS_PATH + '/iadt'

    InAppDevToolsExtension extension
    ProjectUtils projectUtils
    ConfigHelper configHelper
    File outputFolder

    void apply(Project project) {
        projectUtils = new ProjectUtils(project)
        configHelper = new ConfigHelper(project)

        //println "IADT apply for $project"
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
                //TODO: Filter modules from configuration

                if (projectUtils.isRoot()){
                    afterEvaluateRoot(project)
                }
                else if (projectUtils.isAndroidApplication()) {
                    //TODO: Unlock for any Android Modules
                    afterEvaluateAndroidModule(project)
                }
                else{
                    if (configHelper.isDebug()){
                        println "IADT skipped for ${project} project. " +
                                "Only Android application module is currently supported."
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
            subproject.getPluginManager().apply('es.rafaco.inappdevtools')
        }
    }

    private void afterEvaluateRoot(Project project) {

        if (configHelper.isDebug()) {
            def gradleVersion = project.gradle.gradleVersion
            def androidPluginVersion = new AndroidPluginUtils(projectUtils.getProject()).getVersion()
            println "IADT InAppDevTools ${getPluginVersion()}"
            println "IADT Build info:"
            println "IADT   Gradle $gradleVersion"
            println "IADT   Android Gradle Plugin $androidPluginVersion"
            println "IADT   Start task " + project.getGradle().getStartParameter().taskRequests[0].getArgs()[0]
            println "IADT Configuration:"
            println "IADT   enabled: " + configHelper.isEnabled()
            println "IADT   variantFilter: " + configHelper.isVariantFilter()
            println "IADT   variantFilterIn: " + configHelper.getVariantFilterIn()
            println "IADT   variantFilterOut: " + configHelper.getVariantFilterOut()
            println "IADT   noopEnabled: " + configHelper.isNoopEnabled()
            //TODO: remove or ad more
            println "IADT   teamName: " + configHelper.getTeamName()
        }
    }

    private void afterEvaluateAndroidModule(Project project) {
        println "IADT ENABLED for module $project.name:"
        if (configHelper.isDebug()) {
            AfterEvaluateHelper.afterEvaluateOrExecute(project, new Action<Project>() {
                @Override
                void execute(Project project2) {
                    projectUtils.printProjectType()
                    projectUtils.printBuildTypes()
                    projectUtils.printFlavors()
                    //projectUtils.printVariants()
                }
            })
        }

        initOutputFolder(project)
        if (projectUtils.isAndroidApplication()) {
            injectInternalPackage(project)
            applySafePlugins(project)

            injectRepositories(project)
            //if (!projectUtils.isLocalDev()){
                injectDependencies(project)
            //}
        }

        new TasksHelper(this, project).initTasks()
    }

    //region [ INIT PLUGIN ]

    private void initOutputFolder(Project project) {
        println "IADT prepare output folder"

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

    //Inject internal package from host app manifest into resValue
    private void injectInternalPackage(Project project) {
        //TODO: Incremental issue: check if already set before perform to avoid updating modified date
        def internalPackage = getInternalPackageFromManifest()
        project.android.defaultConfig.resValue "string", "internal_package", "${internalPackage}"
        //project.android.defaultConfig.buildConfigField("String", "INTERNAL_PACKAGE", "\"${internalPackage}\"")
    }

    //Apply external plugins required for Iadt (safe to be applied even if we dont use them)
    private void applySafePlugins(Project project) {
        project.getPluginManager().apply(ProjectReportsPlugin.class)
        //TODO: research other reports (tasks, properties, dashboard,...)
        // project.getPluginManager().apply(org.gradle.api.reporting.plugins.BuildDashboardPlugin.class)
    }

    //Apply Pandora plugin. It cause a crash on startup when using noop (no Pandora libraries)
    private void applyPandoraPlugins(Project project){
        project.getPluginManager().apply(PandoraPlugin.class)
    }

    private void injectRepositories(Project project) {
        // Add JitPack repository for transitive dependencies
        println "IADT add repositories:"
        println "IADT   maven { url \"https://jitpack.io\"}"
        project.repositories {
            maven { url "https://jitpack.io" }
        }

    }

    private void injectDependencies(Project project) {
        println "IADT add dependencies:"

        if (configHelper.isEnabled()){
            if (configHelper.isVariantFilter()){
                configHelper.getVariantFilterIn().each { filterIn ->
                    addDependency(project, filterIn,
                            "es.rafaco.inappdevtools", "library")
                }
                if (configHelper.isNoopEnabled()) {
                    configHelper.getVariantFilterOut().each { filterOut ->
                        addDependency(project, filterOut,
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
            if (configHelper.isNoopEnabled()){
                addDependency(project, "",
                        "es.rafaco.inappdevtools", "noop")
            }
        }
    }

    private void addDependency(Project project, String configuration, String group, String id) {
        def isLocal = projectUtils.isLocalDev()
        String configName = configuration + "Implementation" //(isLocal ? "Api" : "Implementation")
        configName = configName[0].toLowerCase() + configName.substring(1)
        String configValue

        if (isLocal){
            String modulePath = ":" + id
            configValue = "project([path: \"$modulePath\")"
            project.dependencies.add(configName,
                    project.dependencies.project([path: modulePath]))
        } else {
            String extendedId = (id != "library") ? id :
                    projectUtils.useAndroidX() ? "androidx" : "support"
            configValue = group + ":" + extendedId + ":" + getPluginVersion()
            project.dependencies.add(configName, configValue)
        }
        println "IADT   ${configName} ${configValue}"
    }

    //endregion

    //region [ STATIC ACCESS TO PLUGIN ]

    static boolean isDebug(Project project) {
        if (getExtension(project)!=null){
            return getExtension(project).debug
        }
        return false
    }

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

    //region [ PROPERTY EXTRACTORS ]

    String getPluginName() {
        this.getClass().getPackage().getSpecificationTitle()
    }

    String getPluginVersion() {
        this.getClass().getPackage().getSpecificationVersion()
    }

    String getInternalPackageFromManifest() {
        String manifestPath = projectUtils.getProject().android.sourceSets.main.manifest.srcFile
        File manifestFile = projectUtils.getFile(manifestPath)
        GPathResult parsedManifest = new XmlSlurper().parse(manifestFile)
        parsedManifest.@package.text()
    }

    //endregion
}
