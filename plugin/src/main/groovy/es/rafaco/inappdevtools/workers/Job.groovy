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
import es.rafaco.inappdevtools.config.ConfigHelper
import es.rafaco.inappdevtools.utils.ProjectUtils
import org.gradle.api.Project

abstract class Job {
    InAppDevToolsPlugin plugin
    Project project
    ConfigHelper configHelper
    ProjectUtils projectUtils

    Job(InAppDevToolsPlugin plugin, Project project) {
        this.plugin = plugin
        this.project = project
        this.configHelper = plugin.configHelper
        this.projectUtils = plugin.projectUtils
    }

    abstract def 'do'()

    def concreteMethod() {
        println 'concrete'
    }
}