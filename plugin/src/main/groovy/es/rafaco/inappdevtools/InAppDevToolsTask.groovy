package es.rafaco.inappdevtools

import org.gradle.api.DefaultTask
import org.gradle.api.Project

class InAppDevToolsTask extends DefaultTask{

    final TAG = InAppDevToolsPlugin.TAG
    InAppDevToolsExtension extension
    String outputPath

    InAppDevToolsTask() {
        this.group = TAG
        extension = InAppDevToolsPlugin.getExtension(project)
        outputPath = InAppDevToolsPlugin.getOutputPath(project)
    }

    protected File getFile(Project project, String path) {
        def file = project.file(path)
        file.parentFile.mkdirs()
        file
    }

    protected boolean isDebug() {
        return extension.debug
    }
}
