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

package org.inappdevtools.plugin.setup


import org.gradle.api.Project
import org.inappdevtools.plugin.config.ConfigHelper
import org.inappdevtools.plugin.config.IadtConfigFields
import org.inappdevtools.plugin.utils.ProjectUtils

class OtherProjectSetup {

    Project project
    ProjectUtils projectUtils
    ConfigHelper configHelper

    OtherProjectSetup(Project project) {
        this.project = project
        this.projectUtils = new ProjectUtils(project)
        this.configHelper = new ConfigHelper(project)
    }

    void beforeEvaluate() {
    }

    void afterEvaluate() {
        if (configHelper.get(IadtConfigFields.DEBUG)) {
            //TODO: Unlock for other Android Modules
            println "IADT skipped for ${project} project. " +
                    "Only Android Application modules are currently supported."
        }
    }
}
