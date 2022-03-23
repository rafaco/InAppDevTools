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

package org.inappdevtools.plugin.utils

import org.gradle.api.Project

// Idea extracted from https://www.camnter.com/the-gradle-plugin-distinguishes-agp-versions/
//
// There is a simpler way from the app build.gradle file, but it doesn't work from a plugin
// def version = com.android.builder.model.Version.ANDROID_GRADLE_PLUGIN_VERSION
//
class AndroidPluginUtils {

    static final String VERSION_3_ZERO_FIELD = 'com.android.builder.Version'
    static final String VERSION_3_ONE_FIELD = 'com.android.builder.model.Version'
    static final String AGP_VERSION_FIELD = 'ANDROID_GRADLE_PLUGIN_VERSION'

    static final String ARTIFACT_GROUP = 'com.android.tools.build'
    static final String ARTIFACT_NAME = 'gradle'

    Project project

    AndroidPluginUtils(Project project) {
        this.project = project
    }

    String getVersion(){
        String gradlePluginVersion = getByMethod1()
        if (gradlePluginVersion != "") {
            return gradlePluginVersion
        }
        return getByMethod2()
    }

    String getByMethod1() {
        String gradlePluginVersion = ''
        try {
            gradlePluginVersion = Class.forName(VERSION_3_ZERO_FIELD)
                    .getDeclaredField(AGP_VERSION_FIELD)
                    .get(this)
                    .toString()
        } catch (Exception e) {
            // Intentionally empty
        }

        try {
            gradlePluginVersion = Class.forName(VERSION_3_ONE_FIELD)
                    .getDeclaredField(AGP_VERSION_FIELD)
                    .get(this)
                    .toString()
        } catch (Exception e) {
            // Intentionally empty
        }
        gradlePluginVersion
    }

    String getByMethod2() {
        String version = ''
        project.rootProject
                .buildscript
                .configurations
                .classpath
                .resolvedConfiguration
                .firstLevelModuleDependencies.
                each {
                    if (it.moduleGroup == ARTIFACT_GROUP && it.moduleName == ARTIFACT_NAME){
                        version = it.moduleVersion
                    }
                }
        version
    }


    String isolateVersion(String moduleVersion){
        if (moduleVersion.contains("-")) {
            def versionArray = moduleVersion.split("-")
            return versionArray[0]
        }
        return moduleVersion
    }

    String isolateAlpha(String moduleVersion){
        if (moduleVersion.contains("-")) {
            def versionArray = moduleVersion.split("-")
            return versionArray[1]
        }
        return ''
    }
}
