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

package es.rafaco.inappdevtools.config

import es.rafaco.inappdevtools.utils.ProjectUtils
import org.gradle.api.Project
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log

class ConfigHelper {

    Project project

    ConfigHelper(Project project) {
        this.project = project
    }

    void save(Map resolvedConfig) {
        project.rootProject.ext.iadt_resolved_config = resolvedConfig
        /*project.subprojects.each {
            project.ext.iadt_resolved_config = resolvedConfig
        }*/
    }

    Object get(String key) {
        if (project.rootProject.ext.iadt_resolved_config == null){
            Log.error("IADT resolved configuration don't exists. Ensure to use it after evaluate")
            return
        }
        project.rootProject.ext.iadt_resolved_config[key]
    }

    Map getAll() {
        project.rootProject.ext.iadt_resolved_config
    }

    //region [ COMPUTED PROPERTIES ]

    //TODO: DELETE AFTER PARSING CORRECTLY from Config Parser
    // If so, add to standard has() or generalize to hasArrayWithValues()
    boolean hasExcludeWithValues(){
        if (has(IadtConfigFields.EXCLUDE) &&
                get(IadtConfigFields.EXCLUDE).getClass().getName() == "[Ljava.lang.String;"){
            return  get(IadtConfigFields.EXCLUDE).length>0
        }
        return false
    }

    String[] calculateInclude() {
        String[] result = []
        String[] exclude = get(IadtConfigFields.EXCLUDE)
        if (exclude==null || exclude.length<1){
            // Warning: validate hasExcludeWithValues() before
            // We can not solve the dimension without exclusions
            return result
        }
        def dimensions = new ProjectUtils(project).getDimensions()
        def usedDimensionName = getDimensionUsedInExclude(dimensions, exclude)
        def usedDimensionValues = dimensions[usedDimensionName]
        usedDimensionValues.each {
            if (!exclude.contains(it)){
                result += it
            }
        }
        return result
    }

    private String getDimensionUsedInExclude(def dimensions, def exclude) {
        String result
        dimensions.each{ name, values ->
            if (values!=null && values.size()>0){
                if (values.contains(exclude[0])){
                    result = name
                    return result
                }
            }
        }
        return result
    }

    boolean isNoopIncluded(String variantName) {
        ProjectUtils projectUtils = new ProjectUtils(project)
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
}
