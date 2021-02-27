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


import es.rafaco.inappdevtools.tasks.DependencyTask
import es.rafaco.inappdevtools.tasks.EmptyBuildInfoTask
import es.rafaco.inappdevtools.tasks.DetectReactNativeTask
import es.rafaco.inappdevtools.utils.AndroidPluginUtils
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ProjectReportsPlugin
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Zip
import es.rafaco.inappdevtools.tasks.BuildInfoTask
import es.rafaco.inappdevtools.utils.ProjectUtils
import org.gradle.plugins.ide.eclipse.internal.AfterEvaluateHelper
import tech.linjiang.pandora.gradle.PandoraPlugin

class InAppDevToolsPlugin implements Plugin<Project> {

    static final TAG = 'inappdevtools'
    static final ASSETS_PATH = '/generated/assets'
    static final OUTPUT_PATH = ASSETS_PATH + '/iadt'

    final CLEAN_TASK = 'iadtClean'
    final EMPTY_BUILD_INFO_TASK = 'iadtEmptyBuildInfo'
    final BUILD_INFO_TASK = 'iadtBuildInfo'
    final DEPENDENCIES_TASK = 'iadtDependencies'
    final SOURCES_TASK = 'iadtSrcPack'
    final GENERATED_TASK = 'iadtGenPack'
    final RESOURCES_TASK = 'iadtResPack'
    final REACT_DETECT_TASK = 'iadtReactDetect'
    final REACT_SOURCES_TASK = 'iadtReactSrcPack'

    InAppDevToolsExtension extension
    ProjectUtils projectUtils
    File outputFolder

    void apply(Project project) {
        projectUtils = new ProjectUtils(project)

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
                    if (isDebug()){
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

        if (isDebug()) {
            def gradleVersion = project.gradle.gradleVersion
            def androidPluginVersion = new AndroidPluginUtils(projectUtils.getProject()).getVersion()
            println "IADT -> Gradle $gradleVersion"
            println "IADT -> Android Gradle Plugin $androidPluginVersion"
        }

        /*project.subprojects { subproject ->
            println "IADT root: subproject ${subproject}"
            //TODO: Open for other application modules
            //TODO: Filter modules from configuration
            def isEnabledForModule = subproject.plugins.hasPlugin('com.android.application')
            if (isEnabledForModule){

                // Add JitPack repository for dependencies
                println "IADT root: add repositories to ${subproject}"
                subproject.repositories {
                    maven { url "https://jitpack.io" }
                }

                // Add dependencies
                println "IADT root: add dependencies to ${subproject}"
            }
        }*/
    }

    private void afterEvaluateAndroidModule(Project project) {
        println("IADT -> InAppDevTools ${getPluginVersion()} enabled for $project")
        if (isDebug()) {
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
        initTasks(project)

        if (projectUtils.isAndroidApplication()) {
            injectInternalPackage(project)
            applySafePlugins(project)

            injectRepositories(project)
            injectDependencies(project)
        }
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

    //endregion

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

    //region [ INIT TASKS ]

    private void initTasks(Project project) {
        // Add our tasks to the project (just add)
        addCleanTask(project)
        addEmptyBuildInfoTask(project)
        addBuildInfoTask(project)
        addSourcesTask(project)
        addReactSourcesTask(project)
        addResourcesTask(project)
        addGeneratedTask(project)

        // Selectively link our tasks to each variant base on configuration
        project.tasks.whenTaskAdded { theTask ->
            if (theTask.name.contains("generate") & theTask.name.contains("Assets")) {

                def buildVariant = theTask.name.drop("generate".length()).reverse()
                        .drop("Assets".length()).reverse()

                boolean isNoop = isNoopIncluded(buildVariant)
                if (isNoop) {
                    println "Iadt DISABLED for $buildVariant (noop artifact)"
                } else if (!isPluginEnabled(theTask)) {
                    if (!isEnabled())
                        println "Iadt DISABLED for $buildVariant (configuration)"
                    else
                        println "Iadt DISABLED for ${buildVariant}. Auto-disabled for Release, use noop to reduce apk size."
                } else {
                    println "Iadt ENABLED for $buildVariant"
                }

                if (isNoop || !isPluginEnabled(theTask)){
                    Task emptyTask = project.tasks.getByName(EMPTY_BUILD_INFO_TASK)
                    Task cleanTask = project.tasks.getByName(CLEAN_TASK)
                    emptyTask.dependsOn += [ cleanTask ]
                    theTask.dependsOn += [ emptyTask ]
                    return
                }

                if (isNetworkInterceptor()){
                    applyPandoraPlugins(project)
                }

                theTask.dependsOn += [project.tasks.getByName(BUILD_INFO_TASK)]
                if (projectUtils.isAndroidApplication()) {
                    addAndLinkDependencyReportTask(project, theTask, buildVariant)
                    addAndLinkDetectReactNativeTask(project, theTask, buildVariant)
                }
                if (shouldIncludeSources(theTask)) {
                    theTask.dependsOn += [
                            project.tasks.getByName(SOURCES_TASK),
                            project.tasks.getByName(REACT_SOURCES_TASK),
                            project.tasks.getByName(RESOURCES_TASK),
                            project.tasks.getByName(GENERATED_TASK)]
                }
            }
        }
    }

    private Task addEmptyBuildInfoTask(Project project) {
        project.task(EMPTY_BUILD_INFO_TASK, type: EmptyBuildInfoTask)
    }

    private Task addCleanTask(Project project) {
        project.task(CLEAN_TASK,
                description: 'Clean generated files',
                group: TAG,
                type: Delete) {

            doLast {
                project.delete getOutputDir(project)
                if (isDebug()) println "Deleted ${getOutputDir(project)} from ${project.name}"
            }
        }
    }

    private void addAndLinkDependencyReportTask(Project project, Task superTask, String buildVariant ) {
        DependencyTask dependencyTask = project.task(DEPENDENCIES_TASK + buildVariant, type: DependencyTask) {
            variantName = buildVariant
        }
        //Manual skip comparing lastModified don't work inside the task but other skips are working.
        //It seems that the parent class delete the output file in constructor, we always receive
        //outputFile.lastModified() = 0 and outputfile.exists() = false inside the task
        //TODO: it doesn't recalculate per variant
        if (dependencyTask.outputFile.lastModified() < dependencyTask.inputFile.lastModified()){
            superTask.dependsOn += [dependencyTask]
        }
    }

    private void addAndLinkDetectReactNativeTask(Project project, Task superTask, String buildVariant ) {
        DetectReactNativeTask reactTask = project.task(REACT_DETECT_TASK + buildVariant, type: DetectReactNativeTask)
        superTask.dependsOn += [reactTask]
        DependencyTask dependencyTask = project.tasks.getByName(DEPENDENCIES_TASK + buildVariant)
        reactTask.dependsOn += [dependencyTask]
    }

    private Task addBuildInfoTask(Project project) {
        project.task(BUILD_INFO_TASK, type: BuildInfoTask)
    }

    private Task addResourcesTask(Project project) {
        project.task(RESOURCES_TASK,
                description: 'Generate a Zip file with the resources',
                group: TAG,
                type: Zip) {

            from ('src/main/res') {
                excludes = ["raw/**"]
            }

            def outputName = "${project.name}_resources.zip"
            destinationDir project.file(getOutputDir(project))
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (isDebug()) { println it.path }
            }
            doLast {
                if (isDebug())
                    println "Packed ${counter} files into ${getOutputDir(project)}\\${outputName}"
            }
        }
    }

    private Task addSourcesTask(Project project) {
        project.task(SOURCES_TASK,
                description: 'Generate a Zip file with all java sources',
                group: TAG,
                type: Zip) {

            def outputName = "${project.name}_sources.zip"
            destinationDir project.file(getOutputDir(project))
            archiveName = outputName
            includeEmptyDirs = false

            if (projectUtils.isAndroidApplication()){
                from project.android.sourceSets.main.manifest.srcFile
                if (isDebug()) println "Added sourceSets: ${project.android.sourceSets.main.manifest.srcFile}"
            }

            def counter = 0
            eachFile {
                counter++
                if (isDebug()) { println it.path }
            }

            doFirst {
                if (projectUtils.isAndroidApplication()) {
                    if (isDebug()) println "Added sourceSets: ${project.android.sourceSets.main.manifest.srcFile}"
                }

                def sourceSets = project.android.sourceSets
                String [] targetNames = ["main", projectUtils.getCurrentBuildType().uncapitalize()]
                def buildFlavor = projectUtils.getCurrentBuildFlavor().uncapitalize()
                if (!buildFlavor.isEmpty()){
                    targetNames += [ buildFlavor, projectUtils.getCurrentVariant().uncapitalize()]
                }
                sourceSets.all { sourceSet ->
                    if(sourceSet.name in targetNames) {
                        for (File file : sourceSet.java.getSrcDirs())
                            if (file.exists()) {
                                if (isDebug()) println "Added sourceSets: ${file}"
                                from file
                            }
                    }
                }
            }

            doLast {
                if (isDebug())
                    println "Packed ${counter} files into ${getOutputDir(project)}\\${outputName}"
            }
        }
    }

    private Task addGeneratedTask(Project project) {
        project.task(GENERATED_TASK,
                description: 'Generate a Zip file with generated sources',
                group: TAG,
                type: Zip) {

            def outputName = "${project.name}_generated.zip"

            from("${project.buildDir}/generated/") {
                excludes = ["assets/**", "**/res/pngs/**"]
            }

            if (projectUtils.isAndroidApplication()){
                def variantName = projectUtils.getCurrentVariant().uncapitalize()
                from ("${project.buildDir}/intermediates/merged_manifests/${variantName}") {
                    include 'AndroidManifest.xml'
                    into 'merged_manifests'
                }
            }

            // File postprocessing
            // Used to simplify folders of generated sources.
            // Broke nodes navigation, could be due to empty folders
            /*eachFile { fileDetails ->
                def filePath = fileDetails.path
                if (isDebug()) println "PROCESSED: " + filePath

                def currentVariantFolders = getBuildVariantFolders(project)
                if (filePath.contains('/r/')) {
                    fileDetails.path = filePath.substring(filePath.indexOf('/r/')
                            + '/r/'.size(), filePath.length())
                    if (isDebug()) println "RENAMED into " + fileDetails.path
                }
                else if (filePath.contains(currentVariantFolders)) {
                    fileDetails.path = filePath.substring(filePath.indexOf(currentVariantFolders)
                            + currentVariantFolders.size(), filePath.length())
                    if (isDebug()) println "RENAMED into " + fileDetails.path
                }
            }*/

            destinationDir project.file(getOutputDir(project))
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (isDebug()) { println it.path }
            }
            doLast {
                if (isDebug())
                    println "Packed ${counter} files into ${getOutputDir(project)}${ProjectUtils.getFolderSeparator()}${outputName}"
            }
        }
    }

    Task addReactSourcesTask(Project project) {
        Task srcTask = project.task(REACT_SOURCES_TASK,
                description: 'Generate a Zip file with ReactNative js sources from parent',
                group: TAG,
                type: Zip) {

            def outputName = "${project.name}_react_sources.zip"
            destinationDir project.file(InAppDevToolsPlugin.getOutputDir(project))
            archiveName outputName
            includeEmptyDirs = false

            String rootPath = new File('').getAbsolutePath()
            int lastFolderIndex = rootPath.lastIndexOf(ProjectUtils.getFolderSeparator())
            if (lastFolderIndex == -1){
                println "Unable to get parent folder. Disabled react native detector"
                File configFile = InAppDevToolsPlugin.getOutputFile(project, 'react_config.json')
                if (configFile.exists()) configFile.delete()
                return
            }
            String parentPath = rootPath.substring(0, lastFolderIndex)
            //TODO: Check react on parentPath
            if(true) {
                from(parentPath) {
                    include "*.js"
                    exclude ".*"
                }
            }

            String sourcesPath = parentPath + ProjectUtils.getFolderSeparator() + "src"
            def existsInternals = new File(sourcesPath).exists()
            if(existsInternals) {
                from(sourcesPath) {
                    include "**/*.js"
                    exclude ".*"
                    into 'src'
                }
            }

            def counter = 0
            eachFile {
                counter++
                if (isDebug()) { println it.path }
            }

            doLast {
                if (isDebug())
                    println "Packed ${counter} files into ${getOutputDir(project)}\\${outputName}"
            }
        }

        srcTask.onlyIf {
            InAppDevToolsPlugin.getOutputFile(project, 'react_config.json').exists()
        }
    }

    //endregion

    //region [ CONFIGURATION ]

    InAppDevToolsExtension getExtension() {
        getExtension(projectUtils.getProject())
    }

    boolean isDebug(){
        isDebug(projectUtils.getProject())
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

    //region [ COMPUTED CONFIGURATION ]

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

    //region [ STATIC ACCESS TO PLUGIN ]

    static InAppDevToolsExtension getExtension(Project project) {
        project.rootProject.extensions.getByName(TAG)
    }

    static boolean isDebug(Project project){
        if (getExtension(project)!=null){
            return getExtension(project).debug
        }
        return false
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
