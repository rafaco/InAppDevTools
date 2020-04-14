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

import es.rafaco.inappdevtools.utils.ConfigUtils
import es.rafaco.inappdevtools.utils.ProjectUtils
import es.rafaco.inappdevtools.utils.AndroidPluginUtils
import org.gradle.api.tasks.TaskAction

import java.time.Duration
import java.time.Instant

class BuildInfoTask extends IadtBaseTask {

    ProjectUtils projectUtils
    ConfigUtils configUtils

    BuildInfoTask() {
        this.description = "Generate config files (build_config, build_info, git_config,...)"
    }

    @TaskAction
    void perform() {
        def configStartTime = Instant.now()
        projectUtils = new ProjectUtils(project)
        configUtils = new ConfigUtils(project)

        generateCompileConfig()
        generateBuildInfo()
        generateGitInfo()
        generatePluginsList()

        if (isDebug()) {
            def duration = Duration.between(configStartTime, Instant.now()).toSeconds()
            println "   BuildInfoTask took " + duration + " secs for ${project.name}"
        }
    }

    private void generateCompileConfig() {
        Map propertiesMap = [:]

        //TODO: Validate and throw new InvalidUserDataException()

        if (extension.enabled!=null)
            propertiesMap.put("enabled", extension.enabled)

        if (extension.enabledOnRelease!=null)
            propertiesMap.put("enabledOnRelease", extension.enabledOnRelease)

        if (extension.debug!=null)
            propertiesMap.put("debug", extension.debug)

        if (extension.notes!=null)
            propertiesMap.put("notes", extension.notes)

        if (extension.teamName!=null)
            propertiesMap.put("teamName", extension.teamName)

        if (extension.teamEmail!=null)
            propertiesMap.put("teamEmail", extension.teamEmail)

        if (extension.teamDesc!=null)
            propertiesMap.put("teamDesc", extension.teamDesc)

        if (extension.teamLinks!=null)
            propertiesMap.put("teamLinks", extension.teamLinks)

        if (extension.overlayEnabled!=null)
            propertiesMap.put("overlayEnabled", extension.overlayEnabled)

        if (extension.sourceInclusion!=null)
            propertiesMap.put("sourceInclusion", extension.sourceInclusion)

        if (extension.sourceInspection!=null)
            propertiesMap.put("sourceInspection", extension.sourceInspection)

        if (extension.invocationByIcon!=null)
            propertiesMap.put("invocationByIcon", extension.invocationByIcon)

        if (extension.invocationByShake!=null)
            propertiesMap.put("invocationByShake", extension.invocationByShake)

        if (extension.invocationByNotification!=null)
            propertiesMap.put("invocationByNotification", extension.invocationByNotification)

        if (extension.callDefaultCrashHandler!=null)
            propertiesMap.put("callDefaultCrashHandler", extension.callDefaultCrashHandler)

        if (extension.injectEventsOnLogcat!=null)
            propertiesMap.put("injectEventsOnLogcat", extension.injectEventsOnLogcat)

        File file = projectUtils.getFile("${outputPath}/build_config.json")
        configUtils.writeMap(file, propertiesMap)
    }

    private void generateBuildInfo() {
        Map propertiesMap = [
                BUILD_TIME      : new Date().getTime(),
                BUILD_TIME_UTC  : new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC")),

                HOST_NAME       : InetAddress.localHost.hostName,
                HOST_ADDRESS    : InetAddress.localHost.hostAddress,
                HOST_OS         : "${System.properties['os.name']} ${System.properties['os.arch']} ${System.properties['os.version']}",
                HOST_USER       : System.properties['user.name'],

                IADT_PLUGIN_VERSION     : this.getClass().getPackage().getImplementationVersion(),
                ANDROID_PLUGIN_VERSION  : new AndroidPluginUtils(projectUtils.getProject()).getVersion(),
                GRADLE_VERSION  : project.gradle.gradleVersion,
                JAVA_VERSION    : "${System.properties['java.version']}",
                JAVA_VENDOR     : "${System.properties['java.vendor']} ${System.properties['java.vm.version']}",
                //TODO: KOTLIN_VERSION
        ]

        File file = projectUtils.getFile("${outputPath}/build_info.json")
        configUtils.writeMap(file, propertiesMap)
    }

    private void generatePluginsList() {
        def plugins = ""
        project.rootProject.buildscript.configurations.classpath.each { plugins += it.name + "\n" }
        File pluginsFile = new File("${outputPath}/gradle_plugins.txt")
        configUtils.writeString(pluginsFile, plugins)
    }

    private void generateGitInfo() {
        def canShellGit = configUtils.canShell("git --version")
        if (!canShellGit){
            println "DISABLED GIT INFO: Unable to reach git command, check your PATH!"
            generateEmptyGitInfo()
            return
        }

        def isGitFolder = configUtils.shell('git rev-parse --is-inside-git-dir')
        if (!isGitFolder){
            println "DISABLED GIT INFO: Project folder is not in a Git repository. Run 'git init' to initialise."
            generateEmptyGitInfo()
            return
        }

        //def localBranch = configUtils.shell("git name-rev --name-only HEAD")
        def localBranch = configUtils.shell("git rev-parse --abbrev-ref HEAD")
        def trackingRemote = configUtils.shell('git config --get branch.' + localBranch + '.remote')
        def trackingFull = trackingRemote + '/' + localBranch //TODO: trackingBranch
        def remoteUrl = configUtils.shell('git config remote.' + trackingRemote + '.url')
        def tag = configUtils.shell('git describe --tags --abbrev=0')
        def gitDiff = configUtils.shell('git diff HEAD')
        def localCommits = configUtils.shell('git cherry -v')

        Map propertiesMap = [
                ENABLED         : true,
                VERSION         : configUtils.shell('git --version').minus("git version "),
                USER_NAME       : configUtils.shell('git config user.name'),
                USER_EMAIL      : configUtils.shell('git config user.email'),
                REMOTE_NAME     : trackingRemote,
                REMOTE_URL      : remoteUrl,
                REMOTE_LAST     : configUtils.shell('git log ' + trackingFull +' -1'),
                TAG_LAST        : tag,
                TAG_INFO        : configUtils.shell('git describe --tags --always --dirty'),
                TAG_DISTANCE    : configUtils.shell('git rev-list ' + tag + ' --count'),
                LOCAL_BRANCH        : localBranch,
                HAS_LOCAL_COMMITS   : localCommits != '',
                LOCAL_COMMITS       : localCommits,
                HAS_LOCAL_CHANGES   : gitDiff != '',
                LOCAL_CHANGES       : configUtils.shell('git status --short'),
        ]

        File file = projectUtils.getFile("${outputPath}/git_info.json")
        configUtils.writeMap(file, propertiesMap)

        def localCommitsLong = configUtils.shell('git log ' + trackingFull + '..HEAD')
        File commitsFile = new File("${outputPath}/local_commits.txt")
        configUtils.writeString(commitsFile, localCommitsLong)

        File diffFile = new File("${outputPath}/local_changes.diff")
        configUtils.writeString(diffFile, gitDiff)
    }

    private void generateEmptyGitInfo() {
        Map propertiesMap = [ ENABLED : false ]
        File file = projectUtils.getFile("${outputPath}/git_info.json")
        configUtils.writeMap(file, propertiesMap)

        new File("${outputPath}/local_commits.txt").delete()
        new File("${outputPath}/local_changes.diff").delete()
    }
}
