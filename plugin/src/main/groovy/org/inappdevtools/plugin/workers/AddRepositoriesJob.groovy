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

import java.net.URI;
import org.gradle.api.Project

// Add repositories needed for transitive dependencies (google, mavencentral, jitpack and jcenter)
class AddRepositoriesJob extends Job {

    AddRepositoriesJob(org.inappdevtools.plugin.InAppDevToolsPlugin plugin, Project project) {
        super(plugin, project)
    }

    def 'do'(){
        if (configHelper.get(org.inappdevtools.plugin.config.IadtConfigFields.DEBUG)) {
            println "IADT add repositories:"
        }
        safeAddMavenRepository("https://dl.google.com/dl/android/maven2/")
        safeAddMavenRepository("https://repo.maven.apache.org/maven2/")
        safeAddMavenRepository("https://jcenter.bintray.com/")
        safeAddMavenRepository("https://jitpack.io")
    }

    private Boolean safeAddMavenRepository(String repoUrl) {
        if (!repositoriesContainsUrl(repoUrl)){
            project.repositories{
                maven { url repoUrl }
            }
            if (configHelper.get(org.inappdevtools.plugin.config.IadtConfigFields.DEBUG)) {
                println "IADT   maven { url \"" + repoUrl + "\"}"
            }
        }
    }

    private Boolean repositoriesContainsUrl(String repoUrl) {
        Boolean founded = false
        URI uri = new URI(repoUrl);
        project.repositories.each {
            if (it.url == uri){
                founded = true
            }
        }
        return founded
    }
}
