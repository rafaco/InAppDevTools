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

package org.inappdevtools.plugin.utils

import org.inappdevtools.plugin.config.ConfigHelper
import org.inappdevtools.plugin.config.IadtConfigFields
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.invocation.Gradle

import java.util.regex.Matcher
import java.util.regex.Pattern

class ProjectUtils {

    Project project

    ProjectUtils(Project project) {
        this.project = project
    }

    Project getProject() {
        project
    }


    File getFile(String path) {
        def file = project.file(path)
        file.parentFile.mkdirs()
        file
    }

    boolean existsTask(String target){
        try{
            project.tasks.getByName(target)
            return true
        }
        catch (Exception e){}
        return false
    }

    boolean addTaskDependency(Task dependentTask, Task dependsOnTask){
        if (existsTask(dependentTask) && existsTask(dependsOnTask)){
            dependentTask.dependsOn(dependsOnTask)
            return true
        }
        return false
    }


    //region [ MODULE CLASSIFIER ]

    boolean isRoot(){
        return project.name == project.rootProject.name
    }

    boolean isAndroidModule(){
        return isAndroidApplication() || isAndroidLibrary() || isAndroidFeature()
        /*TODO:
        return project.getPlugins().hasPlugin('com.android.application') ||
                project.getPlugins().hasPlugin('com.android.library') ||
                project.getPlugins().hasPlugin('com.android.feature') ||
                project.getPlugins().hasPlugin('com.android.dynamic-feature') ||
                project.getPlugins().hasPlugin('com.android.instantapp') ||
                project.getPlugins().hasPlugin('android') ||
                project.getPlugins().hasPlugin('android-library') */
    }

    //TODO: KOTLIN!
    boolean isKotlin() {
        return project.getPlugins().hasPlugin('kotlin') ||
                project.getPlugins().hasPlugin('kotlin-platform-common') ||
                project.getPlugins().hasPlugin('kotlin-platform-jvm') ||
                project.getPlugins().hasPlugin('kotlin-platform-js') ||
                project.getPlugins().hasPlugin('org.jetbrains.kotlin') ||
                project.getPlugins().hasPlugin('org.jetbrains.kotlin.jvm') ||
                project.getPlugins().hasPlugin('org.jetbrains.kotlin.js') ||
                project.getPlugins().hasPlugin('kotlin2js') ||
                project.getPlugins().hasPlugin('kotlin-android') ||
                project.getPlugins().hasPlugin('kotlin-android-extensions')
    }

    boolean isAndroidApplication(){
        return project.plugins.hasPlugin('com.android.application')
    }

    boolean isAndroidLibrary(){
        return project.plugins.hasPlugin('com.android.library')
    }

    boolean isAndroidFeature(){
        return project.plugins.hasPlugin('com.android.feature')
    }

    boolean isLocalDev(){
        return project.rootProject.gradle.hasProperty('iadtIsLocalDev') &&
                project.rootProject.gradle.ext.iadtIsLocalDev
    }

    boolean useAndroidX(){
        return project.rootProject.hasProperty('android.useAndroidX') &&
                project.rootProject.properties['android.useAndroidX'] == 'true'
    }

    boolean enableJetifier(){
        return project.rootProject.hasProperty('android.enableJetifier') &&
                project.rootProject.properties['android.enableJetifier'] == 'true'
    }

    //endregion



    //TODO: REPLACE USAGES BY NEW METHODS
    //region [ VARIANT/FLAVOR EXTRACTORS ]

    String getCurrentBuildType() {
        String tskReqStr = project.getGradle().getStartParameter().getTaskRequests().toString()
        if (tskReqStr.contains("Debug"))
            return "Debug"
        else if (tskReqStr.contains("Release"))
            return "Release"
        else{
            println "Unable to get current Build Type"
            return ""
        }
    }

    String getCurrentBuildFlavor() {
        //TODO: this is ugly but is going to be removed soon
        def isDebug = new ConfigHelper(project).get(IadtConfigFields.DEBUG)
        Matcher matcher = getVariantMatcher()
        if (matcher.find()) {
            return matcher.group(1)
        } else {
            if (isDebug) println "There are not Flavors"
            return ""
        }
    }

    String getCurrentVariant() {
        String buildType = getCurrentBuildType()
        String buildFlavor = getCurrentBuildFlavor()
        String buildVariant = buildFlavor + buildType
        return buildVariant.uncapitalize()
    }

    String getBuildVariantFolders() {
        String buildType = getCurrentBuildType()
        String buildFlavor = getCurrentBuildFlavor()
        String resultFolder = buildType + '/'
        if (!buildFlavor.isEmpty()){
            resultFolder += buildFlavor + '/'
        }
        return resultFolder
    }

    private Matcher getVariantMatcher() {
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

    String getCurrentApplicationId() {
        def outStr = ''
        def currFlavor = getCurrentBuildFlavor(project)
        project.android.productFlavors.all{ flavor ->
            if( flavor.name==currFlavor )
                outStr=flavor.applicationId
        }

        return outStr
    }

    //endregion


    //region [ CONFIGURATIONS LISTS]

    Set getConfigurations() {
        project.configurations
    }

    boolean existsConfiguration(String target){
        try{
            getConfigurations().getByName(target)
            return true
        }
        catch (Exception e){}
        return false
    }

    void printConfigurations(){
        println 'Configurations for project ' + project.getName() + " -> " + getConfigurations().size()
        getConfigurations().each { println " - " + it.name }
    }

    //TODO: remove
    void printDependencyCandidates() {
        printArray("Dependency candidates", getDependencyConfigurations())
    }

    //TODO: remove
    String[] getDependencyConfigurations() {
        if (!isAndroidModule()) return results
        def results = []
        project.configurations.each {
            if (it.isCanBeResolved()
                    && it.name.contains("Classpath")
                    && it.name.toLowerCase().contains("runtime")
                    && !it.name.toLowerCase().contains("test"))
                results.add(it.name)
        }
        return results
    }

    String[] getBuildTypes() {
        if (!isAndroidModule()) return []
        def results = []
        project.android.buildTypes.each {
            results.add(it.name)
        }
        return results
    }

    boolean hasFlavors(){
        if (!isAndroidModule()) return false
        return !project.android.productFlavors.isEmpty()
    }

    def getFlavors() {
        if (!isAndroidModule()) return []
        def results = [:]
        project.android.flavorDimensionList.each{
            results[(it)]=[]
        }
        project.android.productFlavors.each {
            def values = results.get(it.dimension)
            values.add(it.name)
        }
        return results
    }

    def getDimensions() {
        def results = getFlavors()
        results["buildType"] = getBuildTypes()
        return results
    }

    String[] getVariants() {
        def variantCollection = getVariantsCollection()
        if (variantCollection.isEmpty()) return []

        def results = []
        variantCollection.each {
            results.add(it.name)
        }
        return results
    }

    Set getVariantsCollection() {
        if (isAndroidApplication()) return project.android.applicationVariants
        else if (isAndroidLibrary()) return project.android.libraryVariants
        else return []
    }

    void printProjectType() {
        print "IADT   Module: "

        if (isAndroidApplication()){
            print "Android Application"
        }
        else if (isAndroidLibrary()){
            print "Android Library"
        }
        else if (isAndroidFeature()){
            print "Android Feature"
        }
        println()
    }

    void printDimensions() {
        printArray("IADT   Dimensions", getDimensions())
    }

    void printBuildTypes() {
        printArray("IADT   buildTypes", getBuildTypes())
    }

    void printFlavors() {
        printArray("IADT   flavors", getFlavors())
    }

    void printVariants() {
        printArray("IADT   variants", getVariants())
    }

    void printArray(String title, array) {
        print "$title: "
        print array
        println()
    }

    //endregion

    static String getFolderSeparator() {
        String osName = System.properties['os.name']
        boolean isWindows = osName.toLowerCase().contains("windows")
        return isWindows ? "\\" : "/"
    }
}