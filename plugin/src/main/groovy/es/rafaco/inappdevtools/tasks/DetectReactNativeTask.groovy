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

package es.rafaco.inappdevtools.tasks

import es.rafaco.inappdevtools.InAppDevToolsPlugin
import es.rafaco.inappdevtools.utils.FileExporter
import es.rafaco.inappdevtools.utils.DependencyUtils
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class DetectReactNativeTask extends IadtBaseTask {

    DetectReactNativeTask() {
        this.description = "Generate a react_config file if ReactNative present in dependencies"
    }

    @TaskAction
    void perform() {
        File dependencies = InAppDevToolsPlugin.getOutputFile(getProject(),
                'gradle_dependencies.txt')
        String reactString = new DependencyUtils(getProject()).getCurrentVersion(dependencies,
                "com.facebook.react:react-native")

        boolean isReact = !reactString.isEmpty()
        if (isReact){
            Map config = [
                    enabled : isReact,
                    version : reactString ]
            saveConfiguration(project, config)
        }
        else{
            cleanConfiguration(project)
        }
    }

    private void saveConfiguration(Project project, Map config) {
        File configFile = InAppDevToolsPlugin.getOutputFile(project, 'react_config.json')
        FileExporter configUtils = new FileExporter(project)
        configUtils.writeMap(configFile, config)
    }

    private void cleanConfiguration(Project project) {
        File configFile = InAppDevToolsPlugin.getOutputFile(project, 'react_config.json')
        if (configFile.exists()){
            configFile.delete()
        }
    }
}
