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
package org.inappdevtools.plugin.workers


import org.inappdevtools.plugin.InAppDevToolsPlugin
import org.inappdevtools.plugin.config.ConfigHelper
import org.gradle.api.Project

abstract class Job {
    InAppDevToolsPlugin plugin
    Project project
    ConfigHelper configHelper
    org.inappdevtools.plugin.utils.ProjectUtils projectUtils

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