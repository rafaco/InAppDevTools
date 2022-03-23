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

package org.inappdevtools.library.logic.log.reader;

import android.util.Log;

import org.inappdevtools.library.logic.utils.DateUtils;
import org.inappdevtools.library.storage.files.utils.FileCreator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.view.overlay.screens.device.Shell;

public class LogcatUtils {

    private LogcatUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void clearBuffer() {
        String[] fullCommand = Shell.formatBashCommand("logcat -c");
        Process process = null;
        try {
            process = new ProcessBuilder()
                    .command(fullCommand)
                    .redirectErrorStream(true)
                    .start();
            FriendlyLog.log("D", "Iadt", "Delete","Logcat buffer deleted by user");
        }
        catch (IOException e) {
            Log.e(Iadt.TAG, "LogcatBuffer showPlaceholder has failed :(");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTraceString = sw.toString();
            Log.e(Iadt.TAG, stackTraceString);
        }
        finally {
            if (process!=null){
                process.destroy();
            }
        }
    }

    public static String getLogs() {
        File file = FileCreator.prepare(
                "logcat",
                "logcat_" + DateUtils.getLong() + ".txt");
        if (file == null)
            return null;

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("logcat -d -f " + file);
            process.waitFor();
            return file.getPath();

        }
        catch (IOException e) {
            FriendlyLog.logException("Exception", e);
        }
        catch (InterruptedException e) {
            FriendlyLog.logException("Exception", e);
            Thread.currentThread().interrupt();
        }
        finally {
            if (process!=null){
                process.destroy();
            }
        }
        return null;
    }
}
