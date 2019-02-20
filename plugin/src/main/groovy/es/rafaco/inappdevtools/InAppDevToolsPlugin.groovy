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

    void apply(Project project) {
        def startTime

        def extension = project.extensions.create(TAG, InAppDevToolsExtension)

        def outputPath = "${project.buildDir}/generated/assets/inappdevtools"
        def outputFolder = project.file(outputPath)
        outputFolder.parentFile.mkdirs()
        project.android.sourceSets.main.assets.srcDirs += "${project.buildDir}/generated/assets"

        project.afterEvaluate({
            Map configMap = [
                    BUILD_TIME:  new Date().getTime(),
                    BUILD_TIME_UTC:  new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC")),
                    EXT_EMAIL : extension.email,
                    EXT_ENABLED : extension.enabled,
                    EXT_DEBUG : extension.debug
            ]

            //def gitSha = 'git rev-parse --short HEAD'.execute([], project.rootDir).text.trim()
            //project.android.defaultConfig.buildConfigField "String", "GIT_SHA", "\"${gitSha}\""
            //println "GIT_SHA" + " = " + "\"${gitSha}\""

            File file = getFile(project, "${outputPath}/compile_config.json")
            String extensionJson
            if (!extension.debug) {
                extensionJson = JsonOutput.toJson(configMap)
            }else{
                extensionJson = JsonOutput.prettyPrint(JsonOutput.toJson(configMap))
                println "CompileConfig: " + file.getPath()
                println extensionJson
            }
            file.write extensionJson
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
                if (extension.debug){  println it.path }
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
                println "InAppDevTools: Added task run before " + theTask.name
                theTask.dependsOn project.tasks.run
            }
        }

        project.tasks.clean {
            delete "src/main/res/raw/${project.name}_sources.jar"
            delete "src/main/res/raw/${project.name}_resources.zip"
        }
    }

    private File getFile(Project project, String path) {
        def file = project.file(path)
        file.parentFile.mkdirs()
        file
    }
}
