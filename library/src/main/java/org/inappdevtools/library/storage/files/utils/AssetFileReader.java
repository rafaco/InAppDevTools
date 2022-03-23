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

package org.inappdevtools.library.storage.files.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.zip.ZipFile;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.view.utils.Humanizer;
import org.inappdevtools.library.view.utils.PathUtils;

public class AssetFileReader {

    private final Context context;

    public AssetFileReader(Context context) {
        this.context = context;
    }

    public ZipFile getZipFile(String target) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(copyToCache(target));
        } catch (IOException e) {
            FriendlyLog.logException("Exception", e);
        }
        return zip;
    }

    private File copyToCache(String target ){
        if(!target.startsWith("/")){
            target = "/" + target;
        }
        File f = new File(context.getCacheDir() + target);

        if (!f.exists()){
            createLocalFileFromAsset(target, f);
        }
        return f;
    }

    public File copyToInternal(String origin, String destinationFolder ){
        if(origin.startsWith("/")){
            origin = origin.substring(1);
        }
        String fileNameWithExtension = PathUtils.getFileNameWithExtension(origin);
        File destination = FileCreator.prepare(destinationFolder, fileNameWithExtension);
        if (!exists(origin)){
            return null;
        }
        createLocalFileFromAsset(origin, destination);
        return destination;
    }

    private void createLocalFileFromAsset(String target, File f) {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            f.getParentFile().mkdirs();
            if (target.startsWith("/assets/")){
                target = target.substring("/assets/".length());
            }
            is = context.getAssets().open(target);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            fos = new FileOutputStream(f);
            fos.write(buffer);
            is.close();
        }
        catch (Exception e) {
            FriendlyLog.logException("SourceReader exception", e);
        }
        finally {
            IOUtil.closeQuietly(fos, is);
        }
    }

    public String getFileContents(String pathInAssetsDir) {
        StringBuilder builder = null;
        InputStream stream = null;
        BufferedReader in = null;

        try {
            stream = context.getAssets().open(pathInAssetsDir);
            in = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
            builder = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str + Humanizer.newLine());
            }
        }
        catch (IOException e) {
            Log.e(Iadt.TAG, "Unable to read config at '" + pathInAssetsDir + "'" + Log.getStackTraceString(e));
            return null;
        }
        finally {
            IOUtil.closeQuietly(stream, in);
        }

        return builder.toString();
    }

    public boolean exists(String pathInAssetsDir){
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(pathInAssetsDir);
            if(null != inputStream ) {
                return true;
            }
        }
        catch(IOException e) {
            //Intentionally empty
        }
        finally {
            IOUtil.closeQuietly(inputStream);
        }
        return false;
    }
}
