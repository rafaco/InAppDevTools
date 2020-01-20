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

package es.rafaco.inappdevtools.library.storage.files.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class AssetFileReader {

    private final Context context;

    public AssetFileReader(Context context) {
        this.context = context;
    }

    public ZipFile getZipFile(String target) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(getLocalFile(target));
        } catch (IOException e) {
            FriendlyLog.logException("Exception", e);
        }
        return zip;
    }

    private File getLocalFile(String target ){
        if(!target.startsWith("/")){
            target = "/" + target;
        }
        File f = new File(context.getCacheDir() + target);

        if (!f.exists()){
            createLocalFromAsset(target, f);
        }
        return f;
    }

    private void createLocalFromAsset(String target, File f) {
        FileOutputStream fos = null;
        try {
            f.getParentFile().mkdirs();
            if (target.startsWith("/assets/")){
                target = target.substring("/assets/".length());
            }
            InputStream is = context.getAssets().open(target);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            fos = new FileOutputStream(f);
            fos.write(buffer);
        } catch (Exception e) {
            FriendlyLog.logException("SourceReader exception", e);
        }finally {
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    FriendlyLog.logException("Exception", e);
                }
            }
        }
    }
}
