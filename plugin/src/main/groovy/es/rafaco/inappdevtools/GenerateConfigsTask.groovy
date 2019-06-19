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
        if (extension.enabled){
            generateGitConfig(project)
        }

        if (isDebug()) {
            def duration = Duration.between(configStartTime, Instant.now()).toSeconds()
            println "   GenerateConfigs took " + duration + " secs for ${project.name}"
        }
    }

    private void generateCompileConfig(Project project) {
        Map propertiesMap = [
                BUILD_TIME    : new Date().getTime(),
                BUILD_TIME_UTC: new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC")),
        ]

        if (extension.enabled!=null)
            propertiesMap.put("ENABLED", extension.enabled)

        if (extension.debug!=null)
            propertiesMap.put("DEBUG", extension.debug)

        if (extension.email!=null)
            propertiesMap.put("EMAIL", extension.email)

        if (extension.overlayEnabled!=null)
            propertiesMap.put("OVERLAY_ENABLED", extension.overlayEnabled)

        if (extension.overlayIconEnabled!=null)
            propertiesMap.put("OVERLAY_ICON_ENABLED", extension.overlayIconEnabled)

        if (extension.notificationEnabled!=null)
            propertiesMap.put("NOTIFICATION_ENABLED", extension.notificationEnabled)

        if (extension.crashHandlerEnabled!=null)
            propertiesMap.put("CRASH_HANDLER_ENABLED", extension.crashHandlerEnabled)

        if (extension.callDefaultCrashHandler!=null)
            propertiesMap.put("CALL_DEFAULT_CRASH_HANDLER", extension.callDefaultCrashHandler)

        if (extension.stickyService!=null)
            propertiesMap.put("STICKY_SERVICE", extension.stickyService)

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
                    ENABLED         : true,
                    REMOTE          : shell('git config --get remote.origin.url'),
                    INFO            : shell('git describe --tags --always --dirty'),
                    BRANCH          : (shell('git branch') =~ /(?m)\* (.*)$/)[0][1],
                    TAG             : shell('git describe --tags --abbrev=0'),
                    TAG_DISTANCE    : shell('git rev-list ' + shell('git describe --tags --abbrev=0') + ' --count'),
                    LAST_COMMIT     : [
                            ISCLEAN : gitDiff == '',
                            MESSAGE : shell('git log -1 --pretty=%B'),
                            SHORT   : shell('git log --oneline -1'),
                            LONG    : shell('git log -1')
                    ],
                    LOCAL_COMMITS   : shell('git cherry -v'),
                    LOCAL_CHANGES: [
                            ISDIRTY : gitDiff != '',
                            STATUS  : shell('git status --short'),
                    ]
            ]
        }

        File file = getFile(project, "${outputPath}/git_config.json")
        saveConfigMap(propertiesMap, file)

        File diffFile = new File("${outputPath}/git.diff")
        if (gitDiff != null && gitDiff != '') {
            if (isDebug()) {  println "Generated: " + diffFile.getPath() }
            diffFile.text = gitDiff
        }else{
            if (diffFile.exists()) { diffFile.delete() }
        }
    }

    private void saveConfigMap(Map map, File file) {
        String extensionJson
        if (isDebug()) {
            extensionJson = JsonOutput.prettyPrint(JsonOutput.toJson(map))
            println "Generated: " + file.getPath()
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
