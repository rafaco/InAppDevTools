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
import org.gradle.api.Project
import org.gradle.api.plugins.ProjectReportsPlugin
import tech.linjiang.pandora.gradle.PandoraPlugin

class AddPluginsJob extends Job {

    AddPluginsJob(InAppDevToolsPlugin plugin, Project project) {
        super(plugin, project)
    }

    def 'do'(){
        if (configHelper.isNetworkInterceptor()) {
            applyPandoraPlugin(project)
        }
        applyReportPlugin(project)
    }

    //Apply external plugins required for Iadt (safe to be applied even if we don't use them)
    private void applyReportPlugin(Project project) {
        if (configHelper.isDebug()) {
            println "IADT   apply ProjectReportsPlugin."
        }
        project.getPluginManager().apply(ProjectReportsPlugin.class)
        //TODO: research other reports (tasks, properties, dashboard,...)
        // project.getPluginManager().apply(org.gradle.api.reporting.plugins.BuildDashboardPlugin.class)
    }

    //Apply Pandora plugin. It cause a crash on startup when using noop (no Pandora libraries)
    private void applyPandoraPlugin(Project project){
        if (configHelper.isDebug()) {
            println "IADT   apply PandoraPlugin."
        }
        project.getPluginManager().apply(PandoraPlugin.class)
    }
}
