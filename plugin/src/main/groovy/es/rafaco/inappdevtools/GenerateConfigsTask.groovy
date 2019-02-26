package es.rafaco.inappdevtools

import groovy.json.JsonOutput
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import java.time.Duration
import java.time.Instant

class GenerateConfigsTask extends InAppDevToolsTask {


    GenerateConfigsTask() {
        this.description = "Generate config files (compile_config, git_config,...)"
    }

    @TaskAction
    void perform() {
        def configStartTime = Instant.now()
        generateCompileConfig(project)
        generateGitConfig(project)
        def duration = Duration.between(configStartTime, Instant.now()).toSeconds()
        println "   InAppDevToolsPlugin:generateConfigs for ${project.name} took " + duration + " seconds"
    }

    private void generateCompileConfig(Project project) {
        Map propertiesMap = [
                BUILD_TIME    : new Date().getTime(),
                BUILD_TIME_UTC: new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC")),
                EXT_EMAIL     : extension.email,
                EXT_ENABLED   : extension.enabled,
                EXT_DEBUG     : extension.debug
        ]

        File file = getFile(project, "${outputPath}/compile_config.json")
        saveConfigMap(propertiesMap, file)
    }

    private void generateGitConfig(Project project) {

        Map propertiesMap
        def gitDiff = shell('git diff HEAD')

        if (gitDiff == null) {
            println TAG + ": " + "Unable to reach git command, check your PATH!"
            propertiesMap = [
                    ENABLED: false
            ]
        } else {
            propertiesMap = [
                    ENABLED      : true,
                    INFO         : shell('git describe --tags --always --dirty'),
                    BRANCH       : (shell('git branch') =~ /(?m)\* (.*)$/)[0][1],
                    SHA          : shell('git rev-parse --short HEAD'),
                    TAG          : shell('git describe --tags --abbrev=0'),
                    LAST_COMMIT  : [
                            ISCLEAN: gitDiff == '',
                            MESSAGE: shell('git log -1 --pretty=%B'),
                            SHORT  : shell('git log --oneline -1'),
                            LONG   : shell('git log -1')
                    ],
                    LOCAL_CHANGES: [
                            ISDIRTY: gitDiff != '',
                            STATUS : shell('git status --short'),
                    ]
            ]
        }

        File file = getFile(project, "${outputPath}/git_config.json")
        saveConfigMap(propertiesMap, file)

        if (gitDiff != null && gitDiff != '') {
            File diffFile = new File("${outputPath}/git.diff")
            if (isDebug()) {
                println "Generated config: " + diffFile.getPath()
            }
            diffFile.text = gitDiff
        }
    }

    private void saveConfigMap(Map map, File file) {
        String extensionJson
        if (isDebug()) {
            extensionJson = JsonOutput.prettyPrint(JsonOutput.toJson(map))
            println "Generated config: " + file.getPath()
            println extensionJson
        } else {
            extensionJson = JsonOutput.toJson(map)
        }
        file.write extensionJson
    }

    private String shell(String cmd) {
        String result = null
        try {
            result = cmd.execute([], project.rootDir).text.trim()
        }
        catch (java.io.IOException e) {
            println TAG + "[WARNING]: " + "Unable to reach git command, check your PATH!"
            if (isDebug()) {
                e.printStackTrace()
            }
        }
        catch (Exception e) {
            println TAG + "[WARNING]: " + "Unable to get git info"
            if (isDebug()) {
                e.printStackTrace()
            }
        }
        result
    }
}
