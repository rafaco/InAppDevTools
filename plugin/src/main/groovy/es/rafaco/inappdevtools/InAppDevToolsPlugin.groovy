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

import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Zip
import es.rafaco.inappdevtools.tasks.GenerateConfigsTask
import es.rafaco.inappdevtools.utils.ProjectUtils
import org.gradle.plugins.ide.eclipse.internal.AfterEvaluateHelper
import tech.linjiang.pandora.gradle.PandoraPlugin;

class InAppDevToolsPlugin implements Plugin<Project> {

    static final TAG = 'inappdevtools'
    static final ASSETS_PATH = '/generated/assets'
    static final OUTPUT_PATH = ASSETS_PATH + '/iadt'

    final CLEAN_TASK = 'cleanGenerated'
    final DEPENDENCY_REPORT_TASK = 'iadtDependencyReport'
    final CONFIG_TASK = 'generateConfigs'
    final SOURCES_TASK = 'packSources'
    final GENERATED_TASK = 'packGenerated'
    final RESOURCES_TASK = 'packResources'

    InAppDevToolsExtension extension
    ProjectUtils projectUtils
    File outputFolder

    void apply(Project project) {

        projectUtils = new ProjectUtils(project)

        if (projectUtils.isRoot()){
            println "IATD skipped for root project"
            return
        }
        else if (!projectUtils.isAndroidModule()){
            println "IATD skipped for ${project.name} project. " +
                    "Only Android application, library or feature project are currently allowed."
            return
        }

        println("Configuring " + getPluginNameAndVersion())
        AfterEvaluateHelper.afterEvaluateOrExecute( project, new Action<Project>() {
            @Override
            void execute(Project project2) {
                projectUtils.printBuildTypes()
                projectUtils.printFlavors()
                projectUtils.printVariants()
            }
        })

        extension = project.extensions.create(TAG, InAppDevToolsExtension)
        outputFolder = getOutputDir(project)
        outputFolder.mkdirs()
        project.android.sourceSets.main.assets.srcDirs += outputFolder.getParent()

        if (projectUtils.isAndroidApplication()){
            //Inject internal package from manifest into resValue
            //TODO: Incremental issue: check if already set before perform to avoid updating modified date
            def internalPackage = getInternalPackageFromManifest()
            project.android.defaultConfig.resValue "string", "internal_package", "${internalPackage}"
            //project.android.defaultConfig.buildConfigField("String", "INTERNAL_PACKAGE", "\"${internalPackage}\"")

            //Add report plugin required for DEPENDENCY_REPORT_TASK
            //TODO: research other reports (tasks, properties, dashboard,...)
            project.getPluginManager().apply(org.gradle.api.plugins.ProjectReportsPlugin.class)
            project.getPluginManager().apply(PandoraPlugin.class)
            // project.getPluginManager().apply(org.gradle.api.reporting.plugins.BuildDashboardPlugin.class)
        }

        // Add our tasks to the project
        addGenerateConfigTask(project)
        addPackSourcesTask(project)
        addPackResourcesTask(project)
        addPackGeneratedTask(project)
        addCleanTask(project)

        // Selectively link our tasks to each BuildVariant
        project.tasks.whenTaskAdded { theTask ->

            if (theTask.name.contains("generate") & theTask.name.contains("ResValues")) {
                def buildVariant = theTask.name.drop("generate".length()).reverse()
                        .drop("ResValues".length()).reverse()

                if (shouldIncludeSources(theTask)){
                    if (isDebug()) println "${buildVariant} include sources"
                    theTask.dependsOn += [
                            project.tasks.getByName(SOURCES_TASK),
                            project.tasks.getByName(RESOURCES_TASK) ]

                    if (projectUtils.isAndroidApplication()){
                        addAndLinkDependencyReportTask(project, theTask, buildVariant)
                    }
                }

                // Link CONFIG_TASK
                // Always performed, if disabled it only add a static config = { enabled=false }
                theTask.dependsOn += [project.tasks.getByName(CONFIG_TASK)]
            }

            if (theTask.name.contains("generate") & theTask.name.contains("Assets")) {
                def buildVariant = theTask.name.drop("generate".length()).reverse()
                        .drop("Assets".length()).reverse()

                if(shouldIncludeSources(theTask)){
                    if (isDebug()) println "${buildVariant} include generated sources"
                    theTask.dependsOn += [project.tasks.getByName(GENERATED_TASK)]
                }
                else{
                    if (isDebug()) println "${buildVariant} without any sources"
                    theTask.dependsOn += [project.tasks.getByName(CLEAN_TASK)]
                }
            }
        }

        //Extend current project's Clean task to clear our outputPath
        //TODO: reuse our clean task
        project.tasks.clean {
            delete getOutputPath(project)
        }
    }

    //region [ ADD TASKS ]

    private Task addAndLinkDependencyReportTask(Project project, Task superTask, String buildVariant ) {
        Task dependencyTask = project.task(DEPENDENCY_REPORT_TASK + buildVariant, type: DependencyTask) {
            variantName = buildVariant
        }
        superTask.dependsOn += [ dependencyTask ]
        dependencyTask
    }

    private Task addGenerateConfigTask(Project project) {
        project.task(CONFIG_TASK, type: GenerateConfigsTask)
    }

    private Task addCleanTask(Project project) {
        project.task(CLEAN_TASK,
                description: 'Clean generated files',
                group: TAG,
                type: Delete) {

            doLast {
                project.delete getOutputDir(project)
                println "Deleted ${getOutputDir(project)} from ${project.name}"
            }
        }
    }

    private Task addPackResourcesTask(Project project) {
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
                println "Packed ${counter} files into ${getOutputDir(project)}\\${outputName}"
            }
        }
    }

    private Task addPackSourcesTask(Project project) {
        project.task(SOURCES_TASK,
                description: 'Generate a Zip file with all java sources',
                group: TAG,
                type: Zip) {

            def outputName = "${project.name}_sources.zip"
            from project.android.sourceSets.main.java.srcDirs

            if (projectUtils.isAndroidApplication()){
                from project.android.sourceSets.main.manifest.srcFile
            }

            destinationDir project.file(getOutputDir(project))
            archiveName = outputName
            includeEmptyDirs = true

            def counter = 0
            eachFile {
                counter++
                if (isDebug()) { println it.path }
            }
            doLast {
                println "Packed ${counter} files into ${getOutputDir(project)}\\${outputName}"
            }
        }
    }

    private Task addPackGeneratedTask(Project project) {
        project.task(GENERATED_TASK,
                description: 'Generate a Zip file with generated sources',
                group: TAG,
                type: Zip) {

            def outputName = "${project.name}_generated.zip"

            from("${project.buildDir}/generated/") {
                excludes = ["assets/**", "**/res/pngs/**"]
            }

            if (projectUtils.isAndroidApplication()){
                def variantName = projectUtils.getCurrentBuildVariant().uncapitalize()
                from ("${project.buildDir}/intermediates/merged_manifests/${variantName}") {
                    include 'AndroidManifest.xml'
                    into 'merged_manifests'
                }
            }

            eachFile { fileDetails ->
                def filePath = fileDetails.path
                if (isDebug()) println "PROCESSED: " + filePath

                /* Simplify folders of generated sources.
                // Seems brokening nodes, could be empty folders
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
                }*/
            }

            destinationDir project.file(getOutputDir(project))
            archiveName = outputName
            includeEmptyDirs = true

            def counter = 0
            eachFile {
                counter++
                if (isDebug()) { println it.path }
            }
            doLast {
                println "Packed ${counter} files into ${getOutputDir(project)}\\${outputName}"
            }
        }
    }

    //TODO: REMOVE after validate new implementation with addAndLinkDependencyReportTask()
    //final VARIANT_DETECTOR_TASK = 'iadtVariantDetectorTask'
    //final TEST_VARIANT_DETECTOR_TASK = 'iadtTestVariantDetectorTask'
    /*private Task addAndLinkVariantDetector(Project project) {
        project.ext.currentVariant = "initial"

        Task variantDetector = project.task(VARIANT_DETECTOR_TASK,
                //dependsOn: 'installDebug',
                description: 'Prepare all variants in this project to fill ext.currentVariant property',
                group: 'Iadt-VariantDetector') {

            def variantCollection= new ProjectUtils(project).getVariantsCollection()
            variantCollection.all { variant ->
                variant.outputs.each { output ->
                    def variantName = variant.name.capitalize()
                    def taskName = "store${variantName}Variant"
                    project.task("$taskName",
                            description: 'Fills ext.currentVariant property with the name of the currently running variant',
                            group: 'Iadt-VariantDetector')  {
                        doLast{
                            println "VariantDetector: running $taskName task to save currentVariant=$variantName"
                            project.ext.set("currentVariant", variantName)
                        }
                    }
                    println "VariantDetector: $variantName output.assemble dependsOn $taskName task"
                    output.assemble.dependsOn taskName
                }
            }
        }

        println "before get variants"
        def variants = projectUtils.getVariants()
        println "after get variants " + variants.size()
        variants.each {
            println "inside variants.each"
            def installVariantTaskName = 'install' + it.capitalize()
            if (projectUtils.existsTask(installVariantTaskName)) {
                println "VariantDetectorTask dependsOn $installVariantTaskName task"
                def installTask = project.tasks.getByName(installVariantTaskName)
                variantDetector.dependsOn installTask
            }
        }

        println "after variants.each"

        *//*project.task(TEST_VARIANT_DETECTOR_TASK,
                dependsOn: VARIANT_DETECTOR_TASK,
                description: 'Print ext.currentVariant property (for testing VariantDetector)',
                group: 'Iadt-VariantDetector')  {
            doLast {
                println("currentVariant is $project.ext.currentVariant")
            }
        }*//*
    }*/

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

    //endregion

    //region [ STATIC ACCESS TO PLUGIN ]

    static InAppDevToolsExtension getExtension(Project project) {
        project.extensions.getByName(TAG)
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

    String getPluginNameAndVersion() {
        String pluginName = this.getClass().getPackage().getSpecificationTitle()
        String pluginVersion = this.getClass().getPackage().getSpecificationVersion()
        "${pluginName} ${pluginVersion}"
    }

    String getInternalPackageFromManifest() {
        String manifestPath = projectUtils.getProject().android.sourceSets.main.manifest.srcFile
        File manifestFile = projectUtils.getFile(manifestPath)
        GPathResult parsedManifest = new XmlSlurper().parse(manifestFile)
        parsedManifest.@package.text()
    }

    //endregion
}
