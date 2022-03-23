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

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.view.utils.Humanizer;
import org.inappdevtools.library.view.utils.PathUtils;

public class InternalFileReader {

    public String getContent(String internalPath) {
        StringBuilder builder = new StringBuilder();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader bufferedReader = null;
        try {
            String path = PathUtils.join(FileCreator.getIadtFolder(), internalPath);
            File target = new File(path);
            fis = new FileInputStream(target.getAbsolutePath());
            isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            bufferedReader = new BufferedReader(isr);
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
        finally {
            IOUtil.closeQuietly(fis, isr, bufferedReader);
        }
        return builder.toString();
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

    public static String getTotalSizeFormatted(){
        File target = IadtController.get().getContext().getFilesDir().getParentFile();
        return getFolderSizeFormatted(target);
    }

    public static String getFolderSizeFormatted(File dir) {
        long size = getFolderSize(dir);
        return Humanizer.humanReadableByteCount(size, false);
    }

    public static long getFolderSize(File dir) {
        File[] entries = dir.listFiles();
        boolean isEmpty = entries==null || entries.length==0;
        if (isEmpty) {
            return 0;
        }

        long size = 0;
        for (File file : entries) {
            if (file.isFile()) {
                size += file.length();
            }
            else
                size += getFolderSize(file);
        }
        return size;
    }
}
