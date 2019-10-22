/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip
import java.util.regex.Matcher
import java.util.regex.Pattern

class InAppDevToolsPlugin implements Plugin<Project> {

    static final TAG = 'inappdevtools'
    static final ASSETS_PATH = '/generated/assets'
    static final OUTPUT_PATH = ASSETS_PATH + '/' + TAG

    final PLUGINS_TASK = 'generatePluginList'
    final CLEAN_TASK = 'cleanGenerated'
    final CONFIG_TASK = 'generateConfigs'
    final SOURCES_TASK = 'packSources'
    final RESOURCES_TASK = 'packResources'

    void apply(Project project) {

        if(!isAndroidApplication(project) && !isAndroidLibrary(project) && !isAndroidFeature(project)){
            if (isRoot(project))
                println "IATD skipped for root project"
            else
                println "IATD skipped for ${project.name} project. " +
                        "Only Android application, library or feature project are currently allowed."
            return
        }

        //Init extension
        project.extensions.create(TAG, InAppDevToolsExtension)

        //Init output folder
        def outputFolder = project.file(getOutputPath(project))
        outputFolder.mkdirs()
        project.android.sourceSets.main.assets.srcDirs += "${project.buildDir}${ASSETS_PATH}"

        if (isAndroidApplication(project)){
            def manifest = new XmlSlurper().parse(project.file(project.android.sourceSets.main.manifest.srcFile))
            def internalPackage = manifest.@package.text()
            project.android.defaultConfig.resValue "string", "internal_package", "${internalPackage}"
            project.android.defaultConfig.buildConfigField("String", "INTERNAL_PACKAGE", "\"${internalPackage}\"")
        }

        // Add all our tasks to project
        addGenerateConfigTask(project)
        addPackSourcesTask(project, outputFolder)
        addPackResourcesTask(project, outputFolder)
        addCleanTask(project, outputFolder)

        // Selectively link our tasks to each BuildVariant
        project.tasks.whenTaskAdded { theTask ->
            if (theTask.name.contains("generate") & theTask.name.contains("ResValues")) {

                def buildVariant = extractBuildVariantFromResValuesTaskName(theTask)
                if ((!isReleaseTask(theTask) || (isReleaseTask(theTask) && isEnabledOnRelease(project)))
                        && isEnabled(project)
                        && isSourceInclusion(project)
                        && isSourceInspection(project)){

                    if (isDebug(project)){
                        println "${buildVariant} include sources"
                    }

                    theTask.dependsOn += [
                            project.tasks.getByName(SOURCES_TASK),
                            project.tasks.getByName(RESOURCES_TASK)]
                }
                else{
                    if (isDebug(project)){
                        println "${buildVariant} exclude sources"
                    }

                    theTask.dependsOn += [
                            project.tasks.getByName(CLEAN_TASK)]
                }

                //TODO: do we need it when disabled?
                // Link CONFIG_TASK
                theTask.dependsOn += [
                        project.tasks.getByName(CONFIG_TASK)]
            }
        }

        //Extend current project's Clean task
        project.tasks.clean {
            delete getOutputPath(project)
        }
    }

    private Task addGenerateConfigTask(Project project) {
        project.task(CONFIG_TASK, type: GenerateConfigsTask)
    }

    private Task addCleanTask(Project project, outputFolder) {
        project.task(CLEAN_TASK,
                description: 'Clean generated files',
                group: TAG,
                type: Delete) {

            doLast {
                project.delete outputFolder
                println "Deleted ${outputFolder} from ${project.name}"
            }
        }
    }

    private Task addPackResourcesTask(Project project, outputFolder) {
        project.task(RESOURCES_TASK,
                description: 'Generate a Zip file with the resources',
                group: TAG,
                type: Zip) {

            if (isAndroidApplication(project)){
                from ('src/main/') {
                    include 'AndroidManifest.xml'
                    into 'manifest'
                    //rename { 'manifest/AndroidManifest.xml' }
                }
                from ("${project.buildDir}/intermediates/merged_manifests/${getCurrentBuildVariant(project).uncapitalize()}") {
                    include 'AndroidManifest.xml'
                    into 'merged_manifest'
                }
            }
            from ('src/main/res') {
                excludes = ["raw/**"]
            }

            def outputName = "${project.name}_resources.zip"
            destinationDir project.file(outputFolder)
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (isDebug(project)) { println it.path }
            }
            doLast {
                println "Packed ${counter} files into ${outputName}"
                //println "Variant: ${getVariantName(project)}"
                //println "Flavor: ${getCurrentFlavor(project)}"
            }
        }
    }

    private Task addPackSourcesTask(Project project, outputFolder) {
        project.task(SOURCES_TASK,
                description: 'Generate a Jar file with all java sources, including generated ones',
                group: TAG,
                type: Jar) {

            def currentVariant = 'debug' //getVariantName(project)
            def outputName = "${project.name}_sources.jar"
            from project.android.sourceSets.main.java.srcDirs
            from("${project.buildDir}/generated/") {
                excludes = ["assets/**", "**/res/pngs/**"]
            }

            eachFile { fileDetails ->
                def filePath = fileDetails.path
                if (isDebug(project)) println "PROCESSED: " + filePath
                if (filePath.contains(currentVariant)) {
                    fileDetails.path = filePath.substring(filePath.indexOf(currentVariant), filePath.length())
                    if (isDebug(project)) println "RENAMED into " + fileDetails.path
                }
            }

            destinationDir project.file(outputFolder)
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (isDebug(project)) { println it.path }
            }
            doLast {
                println "Packed ${counter} files into ${outputName}"
            }
        }
    }

    static String getOutputPath(Project project){
        return "${project.buildDir}${OUTPUT_PATH}"
    }

    static InAppDevToolsExtension getExtension(Project project) {
        project.extensions.getByName(TAG)
    }

    static boolean isDebug(Project project){
        if (getExtension(project)!=null){
            return getExtension(project).debug
        }
        return false
    }

    static boolean isReleaseTask(Task task){
        return task.name.contains("Release")
    }

    static boolean isEnabledOnRelease(Project project){
        InAppDevToolsExtension extension = getExtension(project)
        if (extension!=null && extension.enabledOnRelease!=null){
            return extension.enabledOnRelease
        }
        return false
    }

    static boolean isEnabled(Project project){
        if (getExtension(project)!=null){
            return getExtension(project).enabled
        }
        return true
    }

    static boolean isSourceInclusion(Project project){
        InAppDevToolsExtension extension = getExtension(project)
        if (extension!=null && extension.sourceInclusion!=null){
            return extension.sourceInclusion
        }
        return true
    }

    static boolean isSourceInspection(Project project){
        InAppDevToolsExtension extension = getExtension(project)
        if (extension!=null && extension.sourceInspection!=null){
            return extension.sourceInspection
        }
        return true
    }

    static boolean isRoot(Project project){
        return project.name.equals(project.rootProject.name)
    }

    static boolean isAndroidApplication(Project project){
        return project.plugins.hasPlugin('com.android.application')
    }

    static boolean isAndroidLibrary(Project project){
        return project.plugins.hasPlugin('com.android.library')
    }

    static boolean isAndroidFeature(Project project){
        return project.plugins.hasPlugin('com.android.feature')
    }

    static String getCurrentBuildType(Project project) {
        Matcher matcher = getBuildVariantMatcher(project)

        if (matcher.find()) {
            String flavor = matcher.group(1)
            return flavor
        } else {
            println "getCurrentFlavor: cannot_find"
            return ""
        }
    }

    static String getCurrentFlavor(Project project) {
        Matcher matcher = getBuildVariantMatcher(project)

        if (matcher.find()) {
            String flavor = matcher.group(1)
            return flavor
        } else {
            println "getCurrentFlavor: cannot_find"
            return ""
        }
    }

    static String getCurrentBuildVariant(Project project) {
        Matcher matcher = getBuildVariantMatcher(project)

        if (matcher.find()) {
            String flavor = matcher.group(1)
            String buildType = matcher.group(2)
            String buildVariant = flavor + buildType
            return buildVariant
        } else {
            println "getCurrentBuildVariant: cannot_find"
            return ""
        }
    }

    private static Matcher getBuildVariantMatcher(Project project) {
        Gradle gradle = project.getGradle()
        String tskReqStr = gradle.getStartParameter().getTaskRequests().toString()
        Pattern pattern

        if (tskReqStr.contains("assemble"))
            pattern = Pattern.compile("assemble(\\w+)(Release|Debug)")
        else
            pattern = Pattern.compile("generate(\\w+)(Release|Debug)")

        Matcher matcher = pattern.matcher(tskReqStr)
        matcher
    }



    static String getCurrentApplicationId(Project project) {
        def outStr = ''
        def currFlavor = getCurrentFlavor(project)
        project.android.productFlavors.all{ flavor ->
            if( flavor.name==currFlavor )
                outStr=flavor.applicationId
        }

        return outStr
    }

    static String extractBuildVariantFromResValuesTaskName(Task task) {
        def buildName = task.name.drop(8)       //Remove head "generate"
                .reverse().drop(9).reverse()    //Remove tail "ResValues"
    }
}
