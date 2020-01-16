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

package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.os.Build;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.documents.Document;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.view.overlay.screens.ScreenHelper;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class InfoHelper extends ScreenHelper {

    @Override
    public String getReportPath() {
        String filePath = DevToolsFiles.storeInfo(getReportContent(), System.currentTimeMillis());
        return filePath;
    }

    @Override
    public String getReportContent() {
        String result = "Generated report from info pages\n" + "//TODO:";

        Document[] values = Document.getInfoDocuments();
        for (Document report : values){
            DocumentData reportData = IadtController.get().getDocumentManager().getDocumentData(report);
            result += reportData.toString();
            result += Humanizer.newLine();
        }
        return result;
    }


    //region [ PROPERTY EXTRACTORS ]

    private Boolean isVirtual() {
        return Build.FINGERPRINT.contains("generic") ||
                Build.PRODUCT.contains("sdk");
    }


    public static String getLinuxInfo() {
        ArrayList<String> commandLine = new ArrayList<String>();
        commandLine.add("cat");
        commandLine.add("/proc/version");
        return runCommandLine(commandLine);
    }

    public static String getMemInfo() {
        ArrayList<String> commandLine = new ArrayList<String>();
        commandLine.add("cat");
        commandLine.add("/proc/meminfo");
        return runCommandLine(commandLine);
    }

    public static String getProcStat() {
        ArrayList<String> commandLine = new ArrayList<String>();
        commandLine.add("cat");
        commandLine.add("/proc/stat");
        //commandLine.add("/proc/pid/stat");
        //commandLine.add("adb top -n 1");
        //In adb shell: top -n 1
        return runCommandLine(commandLine);
    }

    @NonNull
    private static String runCommandLine(ArrayList<String> commandLine) {
        StringBuilder meminfo = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                meminfo.append(line);
                meminfo.append("\n");
            }
        } catch (IOException e) {
            Log.e(Iadt.TAG, "Could not read /proc/meminfo", e);
        }

        return meminfo.toString();
    }
    //endregion
}
