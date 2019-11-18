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

package es.rafaco.inappdevtools.library.logic.log.reader;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.overlay.screens.console.Shell;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatLineAdapter;

public class LogcatReaderTask extends AsyncTask<Void, String, Void> {

    private final int BUFFER_SIZE = 1024;
    private final String commandScript;
    private final int id;
    private boolean isRunning = true;
    private Process logprocess = null;
    private BufferedReader reader = null;
    private LogcatLineAdapter adaptor;
    private int readCounter = 0;
    private int nullCounter = 0;
    private int sameCounter = 0;
    private int processedCounter = 0;
    private Runnable onCancelledCallback;
    private static int counter = -1;

    public LogcatReaderTask(LogcatLineAdapter adaptor, String commandScript) {
        this.adaptor = adaptor;
        this.commandScript = commandScript;
        this.id = counter++;
        Log.v(Iadt.TAG, "LogcatReaderTask " + id + " created:" + commandScript);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String[] bashCommand = Shell.formatBashCommand(commandScript);
            logprocess = Runtime.getRuntime().exec(bashCommand);
        }
        catch (Exception e) {
            FriendlyLog.logException("Exception", e);
            isRunning = false;
        }

        try {
            reader = new BufferedReader(new InputStreamReader(
                    logprocess.getInputStream()),BUFFER_SIZE);
        }
        catch(IllegalArgumentException e){
            FriendlyLog.logException("Exception", e);
            isRunning = false;
        }

        String line;
        try {
            while (isRunning && (line = reader.readLine())!= null) {
                readCounter ++;
                String[] lineArray = new String[1];
                lineArray[0] = line;
                publishProgress(lineArray);
            }
        }
        catch (IOException e) {
            FriendlyLog.logException("Exception", e);
            isRunning = false;
        }

        Log.v(Iadt.TAG, "LogcatReaderTask " + id + " finished doInBackground");
        return null;
    }

    @Override
    protected void onCancelled() {
        isRunning = false;
        if (logprocess != null) logprocess.destroy();
        Log.v(Iadt.TAG, "LogcatReaderTask " + id + " onCancelled");
        Log.v(Iadt.TAG, String.format("Printed %s of %s (%S) lines (filtered %s nulls and %s duplicated)",
                adaptor.getItemCount(), readCounter, processedCounter, nullCounter, sameCounter));

        if(onCancelledCallback !=null){
            onCancelledCallback.run();
            onCancelledCallback = null;
        }
        super.onCancelled();
        //stopTask();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Log.v(Iadt.TAG, "LogcatReaderTask " + id + " onPostExecute");
        Log.v(Iadt.TAG, String.format("Printed %s of %s lines (filtered %s nulls and %s duplicated)", readCounter, adaptor.getItemCount(), nullCounter, sameCounter));
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        processedCounter ++;

        //TODO: Research why there are too much nulls and duplicated

        String newLine = values[0];
        onLineRead(newLine);
    }

    private void onLineRead(String newLine) {
        if(TextUtils.isEmpty(newLine)) {
            nullCounter++;
            return;
        }

        //Remove duplicated
        if(adaptor.getItemCount()>0){
            String previousLine = adaptor.getItemByPosition(adaptor.getItemCount()-1)
                    .getOriginalLine();
            if(newLine.equals(previousLine)){
                //TODO: Add a multiplicity counter to LogcatLine and increment it
                sameCounter ++;
                return;
            }
        }
        adaptor.add(newLine, id);
    }

    public void stopTask(){
        isRunning = false;
        this.cancel(true);
    }

    public void stopTask(Runnable callback){
        onCancelledCallback = callback;
        stopTask();
    }
}
