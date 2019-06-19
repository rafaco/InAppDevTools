package es.rafaco.inappdevtools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip
import java.util.regex.Matcher
import java.util.regex.Pattern

class InAppDevToolsPlugin implements Plugin<Project> {

    static final TAG = 'inappdevtools'
    static final ASSETS_PATH = '/generated/assets'
    static final OUTPUT_PATH = ASSETS_PATH + '/' + TAG

    final CONFIG_TASK = 'generateConfigs'
    final CLEAN_TASK = 'cleanGenerated'
    final SOURCES_TASK = 'packSources'
    final RESOURCES_TASK = 'packResources'

    void apply(Project project) {

        if(!isAndroidApplication(project) && !isAndroidLibrary(project)){
            return
        }

        //Init extension
        project.extensions.create(TAG, InAppDevToolsExtension)

        //Init output folder
        def outputFolder = project.file(getOutputPath(project))
        outputFolder.mkdirs()
        project.android.sourceSets.main.assets.srcDirs += "${project.buildDir}${ASSETS_PATH}"

        if (isAndroidApplication(project)){
            def manifest = new XmlSlurper().parse(project.file(project.android.sourceSets.main.manifest.srcFile))
            def internalPackage = manifest.@package.text()
            project.android.defaultConfig.resValue "string", "internal_package", "${internalPackage}"
            project.android.defaultConfig.buildConfigField("String", "INTERNAL_PACKAGE", "\"${internalPackage}\"")
        }

        //Add tasks
        project.task(CONFIG_TASK, type:GenerateConfigsTask)
        addPackSourcesTask(project, outputFolder)
        addPackResourcesTask(project, outputFolder)
        addCleanTask(project, outputFolder)

        // Link tasks on project
        project.tasks.whenTaskAdded { theTask ->
            if (theTask.name.contains("generate") & theTask.name.contains("ResValues")) {

                if (isEnabled(project)){
                    if (isDebug(project)){ println "InAppDevTools: Added tasks before " + theTask.name }
                    theTask.dependsOn += [
                            project.tasks.getByName(SOURCES_TASK),
                            project.tasks.getByName(RESOURCES_TASK),
                            project.tasks.getByName(CONFIG_TASK)]
                }
                else{
                    if (isDebug(project)){ println "InAppDevTools: Removed generated assets"}
                    //project.android.sourceSets.main.assets.srcDirs -= "${project.buildDir}${ASSETS_PATH}"
                    //delete(project.file("${project.buildDir}\\generated\\assets\\inappdevtools"))
                    theTask.dependsOn += [
                            project.tasks.getByName(CLEAN_TASK),
                            project.tasks.getByName(CONFIG_TASK)]
                }
            }
        }

        //Extend current project's Clean task
        project.tasks.clean {
            delete getOutputPath(project)
        }
    }

    private Task addCleanTask(Project project, outputFolder) {
        project.task(CLEAN_TASK,
                description: 'Clean generated files',
                group: TAG,
                type: Delete) {

            doLast {
                project.delete outputFolder
                println "Deleted ${outputFolder} from ${project.name}"
            }

        }
    }

    private Task addPackResourcesTask(Project project, outputFolder) {
        project.task(RESOURCES_TASK,
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
                println "Variant: " + getVariantName(project)
            }
        }
    }

    private Task addPackSourcesTask(Project project, outputFolder) {
        project.task(SOURCES_TASK,
                description: 'Generate a Jar file with all java sources, including generated ones',
                group: TAG,
                type: Jar) {

            def currentVariant = 'debug' //getVariantName(project)
            def outputName = "${project.name}_sources.jar"
            from project.android.sourceSets.main.java.srcDirs
            from("${project.buildDir}/generated/") {
                excludes = ["assets/**", "**/res/pngs/**"]
            }

            eachFile { fileDetails ->
                def filePath = fileDetails.path
                if (isDebug(project)) println "PROCESSED: " + filePath
                if (filePath.contains(currentVariant)) {
                    fileDetails.path = filePath.substring(filePath.indexOf(currentVariant), filePath.length())
                    if (isDebug(project))  println "RENAMED into " + fileDetails.path
                }
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

    static boolean isEnabled(Project project){
        if (getExtension(project)!=null){
            return getExtension(project).enabled
        }
        return false
    }

    static boolean isAndroidApplication(Project project){
        return project.plugins.hasPlugin('com.android.application')
    }

    static boolean isAndroidLibrary(Project project){
        return project.plugins.hasPlugin('com.android.library')
    }

    static boolean isAndroidFeature(Project project){
        return project.plugins.hasPlugin('com.android.feature')
    }

    static String getVariantName(Project project){
        String buildType
        if (isAndroidApplication(project)){
            project.android.applicationVariants.all { variant ->
                    buildType = variant.buildType.name
                    println "variant: " + buildType + " dir: " + variant.dirName
            }
        }
        return buildType
    }

    static String getCurrentFlavor(Project project) {
        Gradle gradle = project.getGradle()
        String  tskReqStr = gradle.getStartParameter().getTaskRequests().toString()
        Pattern pattern

        if( tskReqStr.contains( "assemble" ) )
            pattern = Pattern.compile("assemble(\\w+)(Release|Debug)")
        else
            pattern = Pattern.compile("generate(\\w+)(Release|Debug)")

        Matcher matcher = pattern.matcher( tskReqStr )

        if (matcher.find()) {
            String flavor = matcher.group(1).toLowerCase()
            println "getCurrentFlavor: " + flavor
            return flavor
        } else {
            println "getCurrentFlavor: cannot_find_current_flavor"
            return ""
        }
    }

    static String getCurrentApplicationId(Project project) {
        def outStr = ''
        def currFlavor = getCurrentFlavor(project)
        project.android.productFlavors.all{ flavor ->
            if( flavor.name==currFlavor )
                outStr=flavor.applicationId
        }

        return outStr
    }
}
