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

package org.inappdevtools.plugin.config.parser


import org.gradle.api.Project

class ConfigParser {

    Project project
    def readers

    ConfigParser(org.inappdevtools.plugin.config.ConfigHelper configHelper) {
        this.project = configHelper.getProject()
        initReaders()
        configHelper.save(getAll())
    }

    /**
     * TODO: Define priorities and document it
     * https://tomgregory.com/gradle-project-properties-best-practices/
     *
     *  0. Runtime configuration provide thought our UI
     *  1. local.properties file in root folder: myPropName1=myPropValue1
     *  2. Gradle properties:
     *      1. Command line properties: ./gradlew <task-name> -PmyPropName1=myPropValue1
     *      2. Java system properties: ./gradlew <task-name> -Dorg.gradle.project.myPropName1=myPropValue1
     *      3. Env variables: ORG_GRADLE_PROJECT_myPropName1=myPropValue1
     *      4. User home gradle.properties: myPropName1=myPropValue1
     *      5. Project root gradle.properties: myPropName1=myPropValue1
     *  3. InAppDevTools extension in root project: inappdevtools { myPropName1=myPropValue1 }
     */
    private void initReaders(){
        readers = []
        // Following order is important as it set the priority
        readers += new EnvironmentConfigReader(project)            // Highest priority
        readers += new GradlePropertiesConfigReader(project)
        readers += new LocalPropertiesConfigReader(project)
        readers += new ExtensionConfigReader(project)       // Lowest priority
        readers += new DefaultConfigReader(project)         // Default values
    }

    boolean has(String key){
        for (def reader : readers){
            if (reader.has(key) && reader.hasValidValue(key))
                return true
        }
        return false
    }

    Object get(String key) {
        for (def reader : readers){
            if (reader.has(key) && reader.hasValidValue(key))
                return reader.get(key)
        }
        return null
    }

    String getName(String field) {
        for (def reader : readers){
            if (reader.has(field) && reader.hasValidValue(field)){
                return reader.getName()
            }
        }
        return null
    }

    String getKey(String field) {
        for (def reader : readers){
            if (reader.has(field) && reader.hasValidValue(key)){
                return reader.getKey()
            }
        }
        return null
    }

    void printResolution(String field) {
        println "IADT Configuration resolution info for '$field':"
        readers.each {
            println "IADT   ${it.getName()}: '${it.getKey(field)}' is " +
                    (it.has(field) ? "'${it.get(field)}'" : "not set")
        }
        println "IADT      Resolved value is '${get(field)}' (${getName(field)})"
    }

    Map getAll(){
        def allConfigFields = []
        org.inappdevtools.plugin.config.IadtConfigFields.declaredFields.findAll {!it.synthetic}.each {
            allConfigFields.add(it.name)
        }
        Map resolvedValues = [:]
        Map resolutionSources = [:]
        for (String fieldClassName : allConfigFields) {
            def field = org.inappdevtools.plugin.config.IadtConfigFields."$fieldClassName"
            if (has(field)){
                resolvedValues.put(field, get(field))
                resolutionSources.put(field, getName(field))
            }
        }
        resolvedValues.put("resolutions", resolutionSources)
        return resolvedValues
    }
}