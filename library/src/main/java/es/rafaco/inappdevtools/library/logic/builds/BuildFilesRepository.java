/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.logic.builds;

import android.content.Context;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.storage.db.entities.Build;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.storage.files.utils.AssetFileReader;
import es.rafaco.inappdevtools.library.storage.files.utils.FileCreator;
import es.rafaco.inappdevtools.library.storage.files.utils.InternalFileReader;
import es.rafaco.inappdevtools.library.storage.files.utils.JsonHelper;

public class BuildFilesRepository {

    public static JsonHelper getBuildInfoHelper(long sessionId){
        long buildId = getBuildIdForSession(sessionId);
        String buildFolder = getSubfolderForBuild(buildId);
        String buildFile = buildFolder + "/" + IadtPath.BUILD_INFO_FILE;
        String content = new InternalFileReader().getContent(buildFile);
        return new JsonHelper(content);
    }

    public static JsonHelper getBuildConfigHelper(long sessionId){
        long buildId = getBuildIdForSession(sessionId);
        String buildFolder = getSubfolderForBuild(buildId);
        String buildFile = buildFolder + "/" + IadtPath.BUILD_CONFIG_FILE;
        String content = new InternalFileReader().getContent(buildFile);
        return new JsonHelper(content);
    }

    public static JsonHelper getGitConfigHelper(long sessionId){
        long buildId = getBuildIdForSession(sessionId);
        String buildFolder = getSubfolderForBuild(buildId);
        String buildFile = buildFolder + "/" + IadtPath.GIT_CONFIG_FILE;
        String content = new InternalFileReader().getContent(buildFile);
        return new JsonHelper(content);
    }

    public static JsonHelper getAppBuildConfigHelper(long sessionId){
        long buildId = getBuildIdForSession(sessionId);
        String buildFolder = getSubfolderForBuild(buildId);
        String buildFile = buildFolder + "/" + IadtPath.APP_BUILD_CONFIG_FILE;
        String content = new InternalFileReader().getContent(buildFile);
        return new JsonHelper(content);
    }

    public static String getBuildFile(long sessionId, String fileName){
        long buildId = getBuildIdForSession(sessionId);
        String buildFolder = getSubfolderForBuild(buildId);
        String buildFile = buildFolder + "/" + fileName;
        return buildFile;
    }

    public static void saveCurrentBuildFiles(long buildId){
        FriendlyLog.logDebug("Saving build files");
        String destinationFolder = getSubfolderForBuild(buildId);

        AssetFileReader copier = new AssetFileReader(getContext());
        copier.copyToInternal(IadtPath.BUILD_INFO, destinationFolder);
        copier.copyToInternal(IadtPath.BUILD_CONFIG, destinationFolder);
        copier.copyToInternal(IadtPath.GIT_CONFIG, destinationFolder);
        copier.copyToInternal(IadtPath.LOCAL_CHANGES, destinationFolder);
        copier.copyToInternal(IadtPath.LOCAL_COMMITS, destinationFolder);
        copier.copyToInternal(IadtPath.PLUGIN_LIST, destinationFolder);
        copier.copyToInternal(IadtPath.DEPENDENCIES, destinationFolder);

        String jsonValues = AppBuildConfig.toJson(getContext());
        FileCreator.withContent(destinationFolder, IadtPath.APP_BUILD_CONFIG_FILE, jsonValues);
    }

    public static Context getContext(){
        return IadtController.get().getContext();
    }

    public static long getBuildIdForSession(long sessionId){
        return getBuildForSession(sessionId).getUid();
    }

    public static Build getBuildForSession(long sessionId){
        return IadtController.getDatabase().buildDao().findBySessionId(sessionId);
    }

    public static String getSubfolderForBuild(long buildId){
        return "build/" + buildId;
    }
}
