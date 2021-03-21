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
import es.rafaco.inappdevtools.config.IadtConfigFields
import es.rafaco.inappdevtools.tasks.BuildInfoTask
import es.rafaco.inappdevtools.tasks.DependencyTask
import es.rafaco.inappdevtools.tasks.DetectReactNativeTask
import es.rafaco.inappdevtools.tasks.EmptyBuildInfoTask
import es.rafaco.inappdevtools.utils.ProjectUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Zip
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log

class AddTasksJob extends Job {

    final CLEAN_TASK = 'iadtClean'
    final EMPTY_BUILD_INFO_TASK = 'iadtEmptyBuildInfo'
    final BUILD_INFO_TASK = 'iadtBuildInfo'
    final DEPENDENCIES_TASK = 'iadtDependencies'
    final SOURCES_TASK = 'iadtSrcPack'
    final GENERATED_TASK = 'iadtGenPack'
    final RESOURCES_TASK = 'iadtResPack'
    final REACT_DETECT_TASK = 'iadtReactDetect'
    final REACT_SOURCES_TASK = 'iadtReactSrcPack'

    boolean isFirstTask = true

    AddTasksJob(InAppDevToolsPlugin plugin, Project project) {
        super(plugin, project)
    }

    def 'do'(){
        addTasks()
        addHook()
    }

    // Add our tasks to the project (just add)
    private void addTasks() {
        addCleanTask(project)
        addEmptyBuildInfoTask(project)
        addBuildInfoTask(project)
        addSourcesTask(project)
        addReactSourcesTask(project)
        addResourcesTask(project)
        addGeneratedTask(project)
    }

    // Add a hook to detect when standard Android generate tasks get added
    private void addHook() {
        project.tasks.whenTaskAdded { theTask ->
            if (theTask.name.contains("generate") & theTask.name.contains("Assets")) {
                def buildVariant = theTask.name
                        .drop("generate".length()).reverse()
                        .drop("Assets".length()).reverse()
                onGenerateTaskAdded(theTask, buildVariant)
            }
        }
    }

    // Selectively link our tasks to each variant
    private void onGenerateTaskAdded(Task theTask, String buildVariant) {
        boolean isTest = buildVariant.toLowerCase().contains("test")
        boolean isNoop = configHelper.isNoopIncluded(buildVariant)
        boolean isDisabledByConfig = !configHelper.get(IadtConfigFields.ENABLED)
        boolean isDisabledByExclude = false
        boolean isEnabledByInclude = false

        if (configHelper.get(IadtConfigFields.EXCLUDE).length > 0) {
            configHelper.get(IadtConfigFields.EXCLUDE).each { exclusion ->
                if (buildVariant.toLowerCase().contains(exclusion))
                    isDisabledByExclude = true
            }
            configHelper.calculateInclude().each { inclusion ->
                if (buildVariant.toLowerCase().contains(inclusion))
                    isEnabledByInclude = true
            }
        }

        if (isDisabledByConfig){
            disableTasksForVariant(theTask, buildVariant, "Configuration 'enabled' is false")
        }
        else if (isTest){
            disableTasksForVariant(theTask, buildVariant, "Is for tests")
        }
        else if (isDisabledByExclude){
            disableTasksForVariant(theTask, buildVariant, "Include in 'exclude' list")
        }
        else if (!isEnabledByInclude){
            disableTasksForVariant(theTask, buildVariant, "Something went wrong")
        }
        else if (isNoop){
            Log.warn("WARNING $buildVariant was ENABLED by confifuration but it have noop artifact included.\n" +
                    " Tasks has not been added. Review your variantFilters for duplicated or missing values!")
            disableTasksForVariant(theTask, buildVariant, "Noop artifact detected!")
        }
        else {
            enableTasksForVariant(theTask, buildVariant)
        }
    }

    private void disableTasksForVariant(Task theTask, String buildVariant, String reason) {
        if (configHelper.get(IadtConfigFields.DEBUG)) {
            if(isFirstTask){
                println "IADT adding tasks:"
                isFirstTask = false
            }
            println "IADT   DISABLED for variant $buildVariant: $reason."
        }
        //TODO: Does this still have sense?
        //TODO: We are not suppose to have mixed artifacts with enabled configs
        //TODO: Does we need the empty build info?? When?
        //TODO: Clean can be improve removing entry from srcDirs
        Task emptyTask = project.tasks.getByName(EMPTY_BUILD_INFO_TASK)
        Task cleanTask = project.tasks.getByName(CLEAN_TASK)
        emptyTask.dependsOn += [cleanTask]
        theTask.dependsOn += [emptyTask]
    }

    private void enableTasksForVariant(Task theTask, String buildVariant) {
        if(isFirstTask){
            println "IADT adding tasks:"
            isFirstTask = false
        }
        println "IADT   ENABLED for variant $buildVariant"
        theTask.dependsOn += [project.tasks.getByName(BUILD_INFO_TASK)]
        if (projectUtils.isAndroidApplication()) {
            addAndLinkDependencyReportTask(project, theTask, buildVariant)
            addAndLinkDetectReactNativeTask(project, theTask, buildVariant)
        }
        boolean shouldIncludeSources = configHelper.get(IadtConfigFields.SOURCE_INCLUSION) &&
                configHelper.get(IadtConfigFields.SOURCE_INSPECTION)
        if (shouldIncludeSources) {
            theTask.dependsOn += [
                    project.tasks.getByName(SOURCES_TASK),
                    project.tasks.getByName(REACT_SOURCES_TASK),
                    project.tasks.getByName(RESOURCES_TASK),
                    project.tasks.getByName(GENERATED_TASK)]
        }
    }

    //region [ ADD TASKS ]

    private Task addEmptyBuildInfoTask(Project project) {
        project.task(EMPTY_BUILD_INFO_TASK, type: EmptyBuildInfoTask)
    }

    private Task addCleanTask(Project project) {
        project.task(CLEAN_TASK,
                description: 'Clean generated files',
                group: plugin.TAG,
                type: Delete) {

            doLast {
                project.delete plugin.getOutputDir(project)
                if (configHelper.get(IadtConfigFields.DEBUG))
                    println "Deleted ${plugin.getOutputDir(project)} from ${project.name}"
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
                group: plugin.TAG,
                type: Zip) {

            from ('src/main/res') {
                excludes = ["raw/**"]
            }

            def outputName = "${project.name}_resources.zip"
            destinationDir project.file(plugin.getOutputDir(project))
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (configHelper.get(IadtConfigFields.DEBUG)) {
                    println it.path
                }
            }
            doLast {
                if (configHelper.get(IadtConfigFields.DEBUG))
                    println "Packed ${counter} files into ${plugin.getOutputDir(project)}\\${outputName}"
            }
        }
    }

    private Task addSourcesTask(Project project) {
        project.task(SOURCES_TASK,
                description: 'Generate a Zip file with all java sources',
                group: plugin.TAG,
                type: Zip) {

            def outputName = "${project.name}_sources.zip"
            destinationDir project.file(plugin.getOutputDir(project))
            archiveName = outputName
            includeEmptyDirs = false

            if (projectUtils.isAndroidApplication()){
                from project.android.sourceSets.main.manifest.srcFile
                if (configHelper.get(IadtConfigFields.DEBUG))
                    println "Added sourceSets: ${project.android.sourceSets.main.manifest.srcFile}"
            }

            def counter = 0
            eachFile {
                counter++
                if (configHelper.get(IadtConfigFields.DEBUG))
                    println it.path
            }

            doFirst {
                if (projectUtils.isAndroidApplication()) {
                    if (configHelper.get(IadtConfigFields.DEBUG))
                        println "Added sourceSets: ${project.android.sourceSets.main.manifest.srcFile}"
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
                                if (configHelper.get(IadtConfigFields.DEBUG))
                                    println "Added sourceSets: ${file}"
                                from file
                            }
                    }
                }
            }

            doLast {
                if (configHelper.get(IadtConfigFields.DEBUG))
                    println "Packed ${counter} files into ${plugin.getOutputDir(project)}\\${outputName}"
            }
        }
    }

    private Task addGeneratedTask(Project project) {
        project.task(GENERATED_TASK,
                description: 'Generate a Zip file with generated sources',
                group: plugin.TAG,
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

            destinationDir project.file(plugin.getOutputDir(project))
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (configHelper.get(IadtConfigFields.DEBUG)) {
                    println it.path
                }
            }
            doLast {
                if (configHelper.get(IadtConfigFields.DEBUG))
                    println "Packed ${counter} files into ${plugin.getOutputDir(project)}${projectUtils.getFolderSeparator()}${outputName}"
            }
        }
    }

    Task addReactSourcesTask(Project project) {
        Task srcTask = project.task(REACT_SOURCES_TASK,
                description: 'Generate a Zip file with ReactNative js sources from parent',
                group: plugin.TAG,
                type: Zip) {

            def outputName = "${project.name}_react_sources.zip"
            destinationDir project.file(plugin.getOutputDir(project))
            archiveName outputName
            includeEmptyDirs = false

            String rootPath = new File('').getAbsolutePath()
            int lastFolderIndex = rootPath.lastIndexOf(projectUtils.getFolderSeparator())
            if (lastFolderIndex == -1){
                println "Unable to get parent folder. Disabled react native detector"
                File configFile = plugin.getOutputFile(project, 'react_config.json')
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
                if (configHelper.get(IadtConfigFields.DEBUG)) {
                    println it.path
                }
            }

            doLast {
                if (configHelper.get(IadtConfigFields.DEBUG))
                    println "Packed ${counter} files " +
                            "into ${plugin.getOutputDir(project)}\\${outputName}"
            }
        }

        srcTask.onlyIf {
            plugin.getOutputFile(project, 'react_config.json').exists()
        }
    }

    //endregion
}
