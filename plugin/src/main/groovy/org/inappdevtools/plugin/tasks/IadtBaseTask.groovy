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

package org.inappdevtools.plugin.tasks

import org.gradle.api.tasks.Internal
import org.inappdevtools.plugin.InAppDevToolsPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Project

class IadtBaseTask extends DefaultTask{

    @Internal
    final TAG = InAppDevToolsPlugin.TAG

    @Internal
    String outputPath

    IadtBaseTask() {
        this.group = TAG
        outputPath = InAppDevToolsPlugin.getOutputPath(project)
    }

    protected File getFile(Project project, String path) {
        def file = project.file(path)
        file.parentFile.mkdirs()
        file
    }
}
