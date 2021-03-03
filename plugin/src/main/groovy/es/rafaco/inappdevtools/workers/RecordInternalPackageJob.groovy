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
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Project

class RecordInternalPackageJob extends Job {

    RecordInternalPackageJob(InAppDevToolsPlugin plugin, Project project) {
        super(plugin, project)
    }

    def 'do'(){
        //Inject internal package from host app manifest into resValue
        println "IADT   record internal package."
        //TODO: Incremental issue: check if already set before perform to avoid updating modified date
        def internalPackage = getInternalPackageFromManifest()
        project.android.defaultConfig.resValue "string", "internal_package", "${internalPackage}"
        //project.android.defaultConfig.buildConfigField("String", "INTERNAL_PACKAGE", "\"${internalPackage}\"")
    }

    String getInternalPackageFromManifest() {
        String manifestPath = project.android.sourceSets.main.manifest.srcFile
        File manifestFile = projectUtils.getFile(manifestPath)
        GPathResult parsedManifest = new XmlSlurper().parse(manifestFile)
        parsedManifest.@package.text()
    }
}
