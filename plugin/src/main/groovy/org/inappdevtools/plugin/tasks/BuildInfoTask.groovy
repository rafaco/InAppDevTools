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
import org.inappdevtools.plugin.config.ConfigHelper
import org.inappdevtools.plugin.config.IadtConfigFields
import org.gradle.api.tasks.TaskAction
import org.inappdevtools.plugin.utils.AndroidPluginUtils
import org.inappdevtools.plugin.utils.FileExporter
import org.inappdevtools.plugin.utils.ProjectUtils

import java.time.Duration
import java.time.Instant
import java.util.regex.Matcher
import java.util.regex.Pattern

class BuildInfoTask extends IadtBaseTask {

    @Internal
    ProjectUtils projectUtils

    @Internal
    FileExporter configExporter

    BuildInfoTask() {
        this.description = "Generate config files (build_config, build_info, git_config,...)"
    }

    @TaskAction
    void perform() {
        def configStartTime = Instant.now()
        projectUtils = new ProjectUtils(project)
        configExporter = new FileExporter(project)

        generateCompileConfig()
        generateBuildInfo()
        generateGitInfo()
        generatePluginsList()

        boolean isDebug = new ConfigHelper(project).get(IadtConfigFields.DEBUG)
        if (isDebug) {
            def duration = Duration.between(configStartTime, Instant.now()).toSeconds()
            println "   BuildInfoTask took " + duration + " secs for ${project.name}"
        }
    }

    private void generateCompileConfig() {
        ConfigHelper configHelper= new ConfigHelper(project)
        Map allConfigurations = configHelper.getAll()
        File file = projectUtils.getFile("${outputPath}/build_config.json")
        configExporter.writeMap(file, allConfigurations)
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
        configExporter.writeMap(file, propertiesMap)
    }

    private void generatePluginsList() {
        def plugins = ""
        project.rootProject.buildscript.configurations.classpath.each { plugins += it.name + "\n" }
        File pluginsFile = new File("${outputPath}/gradle_plugins.txt")
        configExporter.writeString(pluginsFile, plugins)
    }

    private void generateGitInfo() {
        def canShellGit = configExporter.canShell("git --version")
        if (!canShellGit){
            println "DISABLED GIT INFO: Unable to reach git command, check your PATH!"
            generateEmptyGitInfo()
            return
        }

        def isGitFolder = configExporter.shell('git rev-parse --is-inside-git-dir')
        if (!isGitFolder){
            println "DISABLED GIT INFO: Project folder is not in a Git repository. Run 'git init' to initialise."
            generateEmptyGitInfo()
            return
        }

        def gitPath = configExporter.shell('git rev-parse --show-toplevel')
        //def localBranch = configUtils.shell("git name-rev --name-only HEAD")
        def localBranch = configExporter.shell("git rev-parse --abbrev-ref HEAD")
        def remoteName = configExporter.shell('git config --get branch.' + localBranch + '.remote')
        def remoteUrl = configExporter.shell('git config remote.' + remoteName + '.url')
        def remoteBranchFull = remoteName + '/' + localBranch //TODO: trackingBranch
        def remoteHeadFull = configExporter.shell('git rev-parse --abbrev-ref ' + remoteName + '/HEAD')

        //def tag = configUtils.shell('git describe --tags --abbrev=0')
        def tagDescription = configExporter.shell('git describe --tags --always --dirty')
        Matcher tagDescriptionMatcher = getTagMatcher(tagDescription)
        def tagParser = new TagParser(tagDescription);

        def remoteBranchDistance = configExporter.shell('git rev-list --count ' + remoteBranchFull + '..HEAD')
        def localBranchCount = configExporter.shell('git rev-list --count HEAD')
        def localBranchLog = configExporter.shell('git log ' + localBranch +' -' + remoteBranchDistance)

        def localBranchGraph = configExporter.shell('git log --graph --oneline ' + remoteBranchFull + '..HEAD')
        def remoteBranchGraph = configExporter.shell('git log --graph --oneline ' + remoteHeadFull + '..' + remoteBranchFull)
        def localChangesDiff = configExporter.shell('git diff HEAD')
        def localChangesTxt = configExporter.shell('git status --short')
        def localCommitsDiff = configExporter.shell('git log ' + remoteBranchFull + '..HEAD -p')
        def localUntracked = configExporter.shell('git ls-files -o --exclude-standard')
        def localCommits = configExporter.shell('git cherry -v')

        def localTotalCount = countLines(localChangesTxt)
        def localUntrackedCount = countLines(localUntracked)
        def localTrackedCount =  localTotalCount - localUntrackedCount

        File fetchFile = new File(gitPath + "/.git/FETCH_HEAD");

        Map propertiesMap = [
                ENABLED         : true,

                VERSION         : configExporter.shell('git --version').minus("git version "),
                PATH            : gitPath,
                USER_NAME       : configExporter.shell('git config user.name'),
                USER_EMAIL      : configExporter.shell('git config user.email'),

                HAS_REMOTE     : !remoteName.isEmpty(),
                REMOTE_NAME     : remoteName,
                REMOTE_URL      : remoteUrl,
                REMOTE_HEAD     : remoteHeadFull,
                REMOTE_HEAD_COUNT      : configExporter.shell('git rev-list --count ' + remoteName),
                REMOTE_HEAD_DISTANCE     : configExporter.shell('git rev-list --count ' + remoteName + '..HEAD'),
                REMOTE_BRANCH   : remoteBranchFull,
                REMOTE_BRANCH_COUNT    : configExporter.shell('git rev-list --count ' + remoteBranchFull),
                REMOTE_BRANCH_DISTANCE   : remoteBranchDistance,
                REMOTE_BRANCH_GRAPH   : remoteBranchGraph,
                REMOTE_LAST_FETCH_TIME      : fetchFile.lastModified(),
                REMOTE_LAST_COMMIT     : configExporter.shell('git log ' + remoteBranchFull +' -1'),

                HAS_TAG     : !tagParser.getName().isEmpty(),
                TAG_DESCRIPTION : tagDescription,
                TAG_NAME    : tagParser.getName(),
                TAG_DISTANCE    : tagParser.getDistance(),
                TAG_LAST_COMMIT : tagParser.getCommit(),
                TAG_DIRTY       : tagParser.isDirty(),

                LOCAL_BRANCH    : localBranch,
                LOCAL_BRANCH_COUNT  : localBranchCount.toInteger(),
                LOCAL_BRANCH_GRAPH  : localBranchGraph,
                HAS_LOCAL_COMMITS   : localCommits != '',
                LOCAL_COMMITS       : localCommits,

                HAS_LOCAL_CHANGES   : localChangesTxt != '',
                LOCAL_UNTRACKED_COUNT : localUntrackedCount,
                LOCAL_UNTRACKED : localUntracked,
                LOCAL_TRACKED_COUNT : localTrackedCount,
                LOCAL_TOTAL_COUNT :  localTotalCount,
                LOCAL_UNSTAGED_STATS :  configExporter.shell('git diff --shortstat'),
                LOCAL_STAGED_STATS :  configExporter.shell('git diff --shortstat --cached'),
                LOCAL_TRACKED_STATS :  configExporter.shell('git diff --shortstat HEAD'),

                FIRST_COMMIT_TIME      : configExporter.shell('git log --reverse --format=%cd || head -1'),
                LAST_COMMIT_TIME      : configExporter.shell('git log -1 --format=%cd'),
        ]

        File file = projectUtils.getFile("${outputPath}/git_info.json")
        configExporter.writeMap(file, propertiesMap)

        def localCommitsLong = configExporter.shell('git log -p' + remoteBranchFull + '..HEAD')
        File commitsFile = new File("${outputPath}/local_commits.txt")
        configExporter.writeString(commitsFile, localCommitsLong)

        File remoteBranchGraphFile = new File("${outputPath}/git_remote_branch.txt")
        configExporter.writeString(remoteBranchGraphFile, remoteBranchGraph)

        File localBranchGraphFile = new File("${outputPath}/git_local_branch.txt")
        configExporter.writeString(localBranchGraphFile, localBranchGraph)

        File localCommitsDiffFile = new File("${outputPath}/git_local_branch.diff")
        configExporter.writeString(localCommitsDiffFile, localCommitsDiff)

        File localChangesTxtFile = new File("${outputPath}/git_local_changes.txt")
        configExporter.writeString(localChangesTxtFile, localChangesTxt)

        File localChangesDiffFile = new File("${outputPath}/git_local_changes.diff")
        configExporter.writeString(localChangesDiffFile, localChangesDiff)
    }

    private void generateEmptyGitInfo() {
        Map propertiesMap = [ ENABLED : false ]
        File file = projectUtils.getFile("${outputPath}/git_info.json")
        configExporter.writeMap(file, propertiesMap)

        new File("${outputPath}/local_commits.txt").delete()
        new File("${outputPath}/git_local_changes.diff").delete()
        new File("${outputPath}/git_local_changes.txt").delete()
        new File("${outputPath}/git_local_branch.diff").delete()
        new File("${outputPath}/git_local_branch.txt").delete()
        new File("${outputPath}/git_remote_branch.txt").delete()
    }

    private int countLines(String multiLineString){
        if (multiLineString.isEmpty()) return 0
        multiLineString.split('\n').size()
    }

    private Matcher getTagMatcher(String tagDescription){
        Pattern pattern = Pattern.compile("([^-]+)?[-]?(\\d+)?[-]?(\\w+)[-]?(dirty)?")
        Matcher matcher = pattern.matcher(tagDescription)
        matcher.find()
        matcher
    }

    class TagParser {
        String tagDescription
        String[] split
        boolean hasTag

        TagParser(String tagDescription) {
            this.tagDescription = tagDescription
            split = tagDescription.split('-')
            this.hasTag = split.size() > 2
        }

        public String getName(){
            if (hasTag && split.size()>0) split[0]
            else ""
        }
        public String getDistance(){
            if (hasTag && split.size()>1) split[1]
            else ""
        }
        public String getCommit(){
            if (hasTag && split.size()>2) split[2]
            else if (split.size()>0)  split[0]
        }
        public boolean isDirty(){
            String dirtyText = ""
            if (hasTag && split.size()==4)
                dirtyText = split[3]
            else if (split.size()==2)
                dirtyText = split[1]
            dirtyText == "dirty"
        }
    }
}


