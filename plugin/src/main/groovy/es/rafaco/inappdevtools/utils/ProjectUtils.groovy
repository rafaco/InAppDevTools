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

package es.rafaco.inappdevtools.utils

import es.rafaco.inappdevtools.InAppDevToolsPlugin
import org.gradle.api.Project
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

    boolean isDebug(){
        InAppDevToolsPlugin.isDebug(project)
    }


    //region [ MODULE CLASSIFIER ]

    boolean isRoot(){
        return project.name == project.rootProject.name
    }

    boolean isAndroidModule(){
        return isAndroidApplication() || isAndroidLibrary() || isAndroidFeature()
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

    //endregion

    //region [ VARIANT/FLAVOR EXTRACTORS ]

    String getCurrentBuildType() {
        Matcher matcher = getBuildVariantMatcher()

        if (matcher.find()) {
            String flavor = matcher.group(1)
            return flavor
        } else {
            if (isDebug()) println "getCurrentFlavor: cannot_find"
            return ""
        }
    }

    String getCurrentFlavor() {
        Matcher matcher = getBuildVariantMatcher()

        if (matcher.find()) {
            String flavor = matcher.group(1)
            return flavor
        } else {
            if (isDebug()) println "getCurrentFlavor: cannot_find"
            return ""
        }
    }

    String getCurrentBuildVariant() {
        Matcher matcher = getBuildVariantMatcher()

        if (matcher.find()) {
            String flavor = matcher.group(1)
            String buildType = matcher.group(2)
            String buildVariant = flavor + buildType
            return buildVariant
        } else {
            if (isDebug()) println "getCurrentBuildVariant: cannot_find"
            return ""
        }
    }

    String getBuildVariantFolders() {
        Matcher matcher = getBuildVariantMatcher()

        if (matcher.find()) {
            String flavor = matcher.group(1).toLowerCase()
            String buildType = matcher.group(2).toLowerCase()
            String buildVariantFolder = buildType + '/' + flavor + '/'
            return buildVariantFolder
        } else {
            if (isDebug()) println "getBuildVariantFolders: cannot_find"
            return ""
        }
    }

    private Matcher getBuildVariantMatcher() {
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
        def currFlavor = getCurrentFlavor(project)
        project.android.productFlavors.all{ flavor ->
            if( flavor.name==currFlavor )
                outStr=flavor.applicationId
        }

        return outStr
    }

    //endregion

    void printConfigurations(){
        println 'Configurations for project ' + project.getName()
        project.configurations.each { println " - " + it.name }
    }
}
