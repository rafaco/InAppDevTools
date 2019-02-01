package es.rafaco.inappdevtools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip

import java.time.Duration
import java.time.Instant

class InAppDevToolsPlugin implements Plugin<Project> {

    final INAPPDEVTOOLS = 'inappdevtools'

    void apply(Project project) {
        def extension = project.extensions.create(INAPPDEVTOOLS, InAppDevToolsExtension)
        def startTime

        project.task('onStart',
                description: 'First task for initializations',
                group: INAPPDEVTOOLS){

            doFirst { startTime = Instant.now()}
        }

        project.task('packSources',
                description: 'Generate a Jar file with all java sources, including generated ones',
                group: INAPPDEVTOOLS,
                dependsOn: project.tasks.onStart,
                type: Jar){

            from project.android.sourceSets.main.java.srcDirs
            from ("${project.buildDir}/generated/"){
                excludes = ["**/res/pngs/**"]
            }
            archiveName = "${project.name}_sources.jar"
            eachFile { println it.path }
            includeEmptyDirs = false
        }

        project.task('packResources',
                description: 'Generate a Zip file with the resources',
                group: INAPPDEVTOOLS,
                dependsOn: project.tasks.onStart,
                type: Zip){

            from 'src/main/res'
            excludes = ["raw/**"]
            archiveName = "${project.name}_resources.zip"
            eachFile { println it.path }
            includeEmptyDirs = false
        }

        project.task('copyToRawResources',
                description: 'Copy the generated files into raw resources folder',
                group: INAPPDEVTOOLS,
                dependsOn: [ project.tasks.packSources, project.tasks.packResources],
                type: Copy) {

            def path1 = "${project.buildDir}/libs/${project.name}_sources.jar"
            def path2 = "${project.buildDir}/distributions/${project.name}_resources.zip"
            from project.files([path1, path2])
            into 'src/main/res/raw'
            eachFile {println it.path}
        }

        project.task('run',
                description: 'Last plugin task',
                group: INAPPDEVTOOLS,
                dependsOn: project.tasks.copyToRawResources ) {

            doLast {
                def duration = Duration.between(startTime, Instant.now()).toSeconds()
                println "   InAppDevTools plugin for ${project.name} took " + duration + " seconds"
                println "   Config: enabled=${extension.enabled}, email=${extension.email}"
            }
        }

        project.tasks.whenTaskAdded { theTask ->
            if (theTask.name.contains("generate") & theTask.name.contains("ResValues")) {
                println theTask.name
                theTask.dependsOn project.tasks.run
            }
        }

        project.tasks.clean {
            delete "src/main/res/raw/${project.name}_sources.jar"
            delete "src/main/res/raw/${project.name}_resources.zip"
        }
    }
}
