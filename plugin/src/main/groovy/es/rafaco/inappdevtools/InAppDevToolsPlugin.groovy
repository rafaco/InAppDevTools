package es.rafaco.inappdevtools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip

class InAppDevToolsPlugin implements Plugin<Project> {

    static final TAG = 'inappdevtools'
    static final ASSETS_PATH = '/generated/assets'
    static final OUTPUT_PATH = ASSETS_PATH + '/' + TAG

    final CONFIG_TASK = 'generateConfigs'
    final SOURCES_TASK = 'packSources'
    final RESOURCES_TASK = 'packResources'

    void apply(Project project) {
        //Init extension
        project.extensions.create(TAG, InAppDevToolsExtension)

        //Init output
        def outputFolder = project.file(getOutputPath(project))
        outputFolder.mkdirs()
        project.android.sourceSets.main.assets.srcDirs += "${project.buildDir}${ASSETS_PATH}"

        //Add tasks
        project.task(CONFIG_TASK, type:GenerateConfigsTask)
        addPackSourcesTask(project, outputFolder)
        addPackResourcesTask(project, outputFolder)

        // Link tasks on project
        project.tasks.whenTaskAdded { theTask ->
            if (theTask.name.contains("generate") & theTask.name.contains("ResValues")) {
                if (isDebug(project)){ println "InAppDevTools: Added tasks before " + theTask.name }
                theTask.dependsOn += [
                        project.tasks.getByName(SOURCES_TASK),
                        project.tasks.getByName(RESOURCES_TASK),
                        project.tasks.getByName(CONFIG_TASK)]
            }
        }

        //Extend current project's Clean task
        project.tasks.clean {
            delete getOutputPath(project)
        }
    }

    private Task addPackResourcesTask(Project project, outputFolder) {
        project.task('packResources',
                description: 'Generate a Zip file with the resources',
                group: TAG,
                type: Zip) {

            def outputName = "${project.name}_resources.zip"
            from 'src/main/res'
            excludes = ["raw/**"]
            destinationDir project.file(outputFolder)
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (isDebug(project)) { println it.path }
            }
            doLast {
                println "Packed ${counter} files into ${outputName}"
            }
        }
    }

    private Task addPackSourcesTask(Project project, outputFolder) {
        project.task('packSources',
                description: 'Generate a Jar file with all java sources, including generated ones',
                group: TAG,
                type: Jar) {

            def outputName = "${project.name}_sources.jar"
            from project.android.sourceSets.main.java.srcDirs
            from("${project.buildDir}/generated/") {
                excludes = ["assets/**", "**/res/pngs/**"]
            }
            destinationDir project.file(outputFolder)
            archiveName = outputName
            includeEmptyDirs = false

            def counter = 0
            eachFile {
                counter++
                if (isDebug(project)) { println it.path }
            }
            doLast {
                println "Packed ${counter} files into ${outputName}"
            }
        }
    }

    static String getOutputPath(Project project){
        return "${project.buildDir}${OUTPUT_PATH}"
    }

    static InAppDevToolsExtension getExtension(Project project) {
        project.extensions.getByName(TAG)
    }

    static boolean isDebug(Project project){
        if (getExtension(project)!=null){
            return getExtension(project).debug
        }
        return false
    }
}
