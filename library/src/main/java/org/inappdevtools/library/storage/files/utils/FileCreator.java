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

package org.inappdevtools.library.storage.files.utils;

import android.content.Context;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.view.utils.PathUtils;

public class FileCreator {

    private FileCreator() {
        throw new IllegalStateException("Utility class");
    }

    @NonNull
    public static String withContent(final String subfolder, final String filename, final String content){

        File file = prepare(subfolder, filename);
        if (file == null){
            return "";
        }
        FileOutputStream fOut = null;
        OutputStreamWriter myOutWriter = null;
        try {
            fOut = new FileOutputStream(file);
            myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(content);

            myOutWriter.close();
            fOut.flush();
            fOut.close();

            if (IadtController.get().isDebug()) {
                Log.v(Iadt.TAG, "Document stored: " + subfolder + "/" + filename);
            }
            return file.getPath();
        } catch (IOException e) {
            FriendlyLog.logException("Error creating file", e);
        } finally {
            IOUtil.closeQuietly(fOut, myOutWriter);
        }
        return "";
    }

    public static File prepare(String subfolder, String filename) {
        File file = new File(getSubfolder(subfolder), filename);
        //TODO: check if file exists and skip recreation
        try {
            boolean created = file.createNewFile();
            if (created){
                return file;
            }
        }
        catch (IOException e) {
            FriendlyLog.logException("Error preparing file " + subfolder + ":" + filename, e);
            FriendlyLog.logException("Exception", e);
        }
        return null;
    }

    public static File getSubfolder(String category){
        return createDirIfNotExist(PathUtils.join(getIadtFolder(), category));
    }

    public static String getIadtFolder(){
        Context context = IadtController.get().getContext();
        return PathUtils.join(context.getFilesDir().toString(), FileProviderUtils.ROOT_FOLDER);
    }

    public static boolean exists(String subfolder, String filename){
        String filePath = PathUtils.join(getIadtFolder(), subfolder, filename);
        File file = new File(filePath);
        return file.exists();
    }

    private static File createDirIfNotExist(String path){
        File dir = new File(path);
        if( !dir.exists() ){
            dir.mkdirs();
        }
        return dir;
    }

    public static String getPath(String subfolder, String filename) {
        return PathUtils.join(getIadtFolder(), subfolder, filename);
    }
}
