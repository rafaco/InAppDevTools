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
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class InternalFileReader {

    private final Context context;

    public InternalFileReader(Context context) {
        this.context = context;
    }

    public String getContent(String internalPath) {
        StringBuilder builder = null;
        FileInputStream fis;
        try {
            File target = new File(FileCreator.getIadtFolder() + "/" + internalPath);
            fis = new FileInputStream(target.getAbsolutePath());
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line + Humanizer.newLine());
            }
        }
        catch (IOException e) {
            Log.e(Iadt.TAG, "Unable to read content from internal '"
                    + internalPath + "'" + Log.getStackTraceString(e));
            return null;
        }

        String result = (builder != null) ? builder.toString() : null;
        try {
            fis.close();
        }
        catch(Exception e){
            Log.e(Iadt.TAG, "Unable to close stream from internal '"
                    + internalPath + "'" + Log.getStackTraceString(e));
        }

        return result;
    }

    public List<String> getFilesAtFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        List<String> result = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile()){
                result.add(file.getAbsolutePath());
            }
        }
        return result;
    }
}
