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

package es.rafaco.inappdevtools.library.storage.files;

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

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class FileCreator {

    @NonNull
    public static String withContent(final String subfolder, final String filename, final String content) {

        File file = prepare(subfolder, filename);
        if (file == null){
            return null;
        }

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(content);

            myOutWriter.close();
            fOut.flush();
            fOut.close();

            MediaScannerUtils.scan(file);
            return file.getPath();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        return null;
    }

    public static File prepare(String subfolder, String filename) {
        File file = new File(getSubfolder(subfolder), filename);
        //TODO: check if file exists and skip recreation
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            FriendlyLog.logException("Error preparing file " + subfolder + ":" + filename, e);
            FriendlyLog.logException("Exception", e);
            return null;
        }
    }

    public static File getSubfolder(String category){
        File categoryFolder = createDirIfNotExist(getLibDir() + "/" + category);
        return categoryFolder;
    }

    private static String getLibDir(){
        Context context = IadtController.get().getContext();
        return context.getFilesDir() + "/" + FileProviderUtils.ROOT_FOLDER;
    }

    private static File createDirIfNotExist(String path){
        File dir = new File(path);
        if( !dir.exists() ){
            dir.mkdirs();
        }
        return dir;
    }
}
