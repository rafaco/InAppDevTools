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

package es.rafaco.inappdevtools.library.view.overlay.screens.console;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class Shell {

    public static final String BASH_PATH = "/system/bin/sh";
    public static final String BASH_ARGS = "-c";

    Process process;
    private boolean isCancelled = false;

    public Shell() {
    }

    public String run(String command) {
        return run(new String[] { command });
    }

    public String run(String[] command) {
        String response = "";

        if (IadtController.get().isDebug()){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < command.length; i++) {
                stringBuilder.append(command[i] + " ");
            }
            ThreadUtils.printOverview("Shell");
            Log.v(Iadt.TAG, "Shell running: " + stringBuilder.toString());
        }

        try {
            process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer output = new StringBuffer();
            String line;
            while (!isCancelled && (line = reader.readLine())!= null) {
                output.append(line + Humanizer.newLine());
            }
            String outputString = output.toString();
            if (IadtController.get().isDebug())
                Log.v(Iadt.TAG, "Shell result: " + outputString);

            String errorString = null;
            InputStream errorStream = process.getErrorStream();
            if (errorStream!=null){
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                StringBuffer errorOutput = new StringBuffer();
                String errorLine;
                while (!isCancelled && (errorLine = errorReader.readLine())!= null) {
                    errorOutput.append(errorLine + Humanizer.newLine());
                }
                errorString = errorOutput.toString();

                if (IadtController.get().isDebug())
                    Log.w(Iadt.TAG, "Shell error: " + errorString);
            }

            if (!TextUtils.isEmpty(outputString))
                response = "OUTPUT: " + outputString;

            if (!TextUtils.isEmpty(errorString)){
                if (!TextUtils.isEmpty(outputString)){
                    response += Humanizer.newLine();
                }
                response += "ERROR: " + errorString;
            }

        } catch (Exception e) {
            FriendlyLog.logException("Exception", e);
        }

        return response;
    }

    public void cancel(){
        isCancelled = true;
    }

    public void destroy(){
        cancel();
        if (process != null) process.destroy();
    }

    public static String[] formatBashCommand(String command){
        return new String[] { BASH_PATH, BASH_ARGS, command};
    }

    /* // Another quick way to run commands

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
    }*/
}
