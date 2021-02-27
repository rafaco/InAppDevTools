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
import org.gradle.api.GradleException
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

        if (projectUtils.isRoot()) {
            onApplyToRoot(project)
        }

        //TODO: Check isEnabled? IMPORTANT!

        AfterEvaluateHelper.afterEvaluateOrExecute(project, new Action<Project>() {
            @Override
            void execute(Project project2) {

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
            println "IADT -> Gradle $gradleVersion"
            println "IADT -> Android Gradle Plugin $androidPluginVersion"
        }
    }

    private void afterEvaluateAndroidModule(Project project) {
        println("IADT -> InAppDevTools ${getPluginVersion()} enabled for $project")
        if (configHelper.isDebug()) {
            AfterEvaluateHelper.afterEvaluateOrExecute(project, new Action<Project>() {
                @Override
                void execute(Project project2) {
                    projectUtils.printBuildTypes()
                    projectUtils.printFlavors()
                    projectUtils.printVariants()
                }
            })
        }

        initOutputFolder(project)
        if (projectUtils.isAndroidApplication()) {
            injectInternalPackage(project)
            applySafePlugins(project)

            injectRepositories(project)
            injectDependencies(project)
        }

        new TasksHelper(this, project).initTasks()
    }

    //region [ INIT PLUGIN ]

    private void initOutputFolder(Project project) {
        println "IADT initOutputFolder()"

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
        println "IADT add repositories to ${project}"
        project.repositories {
            maven { url "https://jitpack.io" }
        }

    }

    private void injectDependencies(Project project) {
        if (projectUtils.isLocalDev()){
            println "IADT dependencies skipped, local development"
            /*project.dependencies.add("debugApi",
                    project.dependencies.project([path : ":library"]))
            project.dependencies.add("releaseApi",
                    project.dependencies.project([path : ":noop"]))*/
        } else {
            println "IADT remote dependencies from version ${getPluginVersion()}"
            //TODO: Filter by variant from configuration!!
            if (projectUtils.useAndroidX()){
                if (!projectUtils.enableJetifier()) {
                    //TODO: Fail the build or fallback to noop?
                    throw new GradleException("InAppDevTools require Jetifier enabled if you " +
                            "use AndroidX.\n" +
                            "- Set 'android.enableJetifier' to true in the gradle.properties " +
                            "file and retry.\n" +
                            "- Sorry, all my sources are migrated but there still a incompatible " +
                            "transitional dependency. Working on it.")
                }
                project.dependencies.add("debugImplementation",
                        "es.rafaco.inappdevtools:androidx:${getPluginVersion()}")
            } else {
                project.dependencies.add("debugImplementation",
                        "es.rafaco.inappdevtools:support:${getPluginVersion()}")
            }
            project.dependencies.add("releaseImplementation",
                    "es.rafaco.inappdevtools:noop:${getPluginVersion()}")

        }
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
