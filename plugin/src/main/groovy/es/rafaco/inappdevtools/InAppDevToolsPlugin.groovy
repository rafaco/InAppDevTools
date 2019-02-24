package es.rafaco.inappdevtools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip

import java.time.Duration
import java.time.Instant

import groovy.json.JsonOutput

class InAppDevToolsPlugin implements Plugin<Project> {

    final TAG = 'inappdevtools'
    def project
    def extension
    def outputPath
    def outputFolder

    void apply(Project project) {
        def startTime
        this.project = project
        this.extension = project.extensions.create(TAG, InAppDevToolsExtension)
        this.outputPath = "${project.buildDir}/generated/assets/inappdevtools"
        this.outputFolder = project.file(outputPath)

        outputFolder.parentFile.mkdirs()
        project.android.sourceSets.main.assets.srcDirs += "${project.buildDir}/generated/assets"

        project.afterEvaluate({
            def configStartTime = Instant.now()
            generateCompileConfig(project)
            generateGitConfig(project)
            def duration = Duration.between(configStartTime, Instant.now()).toSeconds()
            println "   InAppDevToolsPlugin:generateConfigs for ${project.name} took " + duration + " seconds"
        })

        project.task('onStart',
                description: 'First task for initializations',
                group: TAG){

            doFirst { startTime = Instant.now() }
        }

        project.task('packSources',
                description: 'Generate a Jar file with all java sources, including generated ones',
                group: TAG,
                dependsOn: project.tasks.onStart,
                type: Jar){

            def outputName = "${project.name}_sources.jar"
            from project.android.sourceSets.main.java.srcDirs
            from ("${project.buildDir}/generated/"){
                excludes = ["assets/**", "**/res/pngs/**"]
            }
            destinationDir outputFolder
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (extension.debug){  println it.path }
            }
            doLast{
                println "Packed ${counter} files into ${outputName}"
            }
        }

        project.task('packResources',
                description: 'Generate a Zip file with the resources',
                group: TAG,
                dependsOn: project.tasks.onStart,
                type: Zip){

            def outputName = "${project.name}_resources.zip"
            from 'src/main/res'
            excludes = ["raw/**"]
            destinationDir outputFolder
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (extension.debug){  println it.path }
            }
            doLast{
                println "Packed ${counter} files into ${outputName}"
            }
        }

        project.task('copyToRawResources',
                description: 'Copy the generated files into raw resources folder',
                group: TAG,
                dependsOn: [ project.tasks.packSources, project.tasks.packResources],
                type: Copy) {

            def outputName = 'src/main/res/raw'
            def path1 = "${project.buildDir}/libs/${project.name}_sources.jar"
            def path2 = "${project.buildDir}/distributions/${project.name}_resources.zip"
            from project.files([path1, path2])
            into outputName

            def counter = 0
            eachFile {
                counter++
                if (extension.debug){ println it.path }
            }
            doLast{
                println "Copied ${counter} files into ${outputName}"
            }
        }

        project.task('run',
                description: 'Last plugin task',
                group: TAG,
                dependsOn: project.tasks.copyToRawResources ) {

            doLast {
                def duration = Duration.between(startTime, Instant.now()).toSeconds()
                println "   InAppDevTools plugin for ${project.name} took " + duration + " seconds"
            }
        }

        project.tasks.whenTaskAdded { theTask ->
            if (theTask.name.contains("generate") & theTask.name.contains("ResValues")) {
                if (extension.debug){ println "InAppDevTools: Added task run before " + theTask.name }
                theTask.dependsOn project.tasks.run
            }
        }

        project.tasks.clean {
            delete outputPath
        }
    }

    private File getFile(Project project, String path) {
        def file = project.file(path)
        file.parentFile.mkdirs()
        file
    }

    private void generateCompileConfig(Project project) {
        Map propertiesMap = [
                BUILD_TIME:  new Date().getTime(),
                BUILD_TIME_UTC:  new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC")),
                EXT_EMAIL : extension.email,
                EXT_ENABLED : extension.enabled,
                EXT_DEBUG : extension.debug
        ]

        File file = getFile(project, "${outputPath}/compile_config.json")
        saveConfigMap(propertiesMap, file)
    }

    private void generateGitConfig(Project project) {

        Map propertiesMap
        def gitDiff = shell('git diff HEAD')

        if (gitDiff == null){
            println TAG + ": " + "Unable to reach git command, check your PATH!"
            propertiesMap = [
                    ENABLED:  false
            ]
        }else{
            propertiesMap = [
                    ENABLED:  true,
                    INFO: shell('git describe --tags --always --dirty'),
                    BRANCH: (shell('git branch') =~ /(?m)\* (.*)$/)[0][1],
                    SHA: shell('git rev-parse --short HEAD'),
                    TAG : shell('git describe --tags --abbrev=0'),
                    LAST_COMMIT : [
                            ISCLEAN: gitDiff == '',
                            MESSAGE: shell('git log -1 --pretty=%B'),
                            SHORT: shell('git log --oneline -1'),
                            LONG: shell('git log -1')
                    ],
                    LOCAL_CHANGES: [
                            ISDIRTY: gitDiff != '',
                            STATUS: shell('git status --short'),
                    ]
            ]
        }

        File file = getFile(project, "${outputPath}/git_config.json")
        saveConfigMap(propertiesMap, file)

        if (gitDiff != null && gitDiff != ''){
            new File("${outputPath}/git.diff").text = gitDiff
        }
    }

    private void saveConfigMap(Map map, File file) {
        String extensionJson
        if (!extension.debug) {
            extensionJson = JsonOutput.toJson(map)
        } else {
            extensionJson = JsonOutput.prettyPrint(JsonOutput.toJson(map))
            println "Generated config: " + file.getPath()
            println extensionJson
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
            if (extension.debug) { e.printStackTrace() }
        }
        catch (Exception e) {
            println TAG + "[WARNING]: " + "Unable to get git info"
            if (extension.debug) { e.printStackTrace() }
        }
        result
    }
}
