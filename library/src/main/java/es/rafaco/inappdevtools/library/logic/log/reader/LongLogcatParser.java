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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LongLogcatParser {

    // Pattern and some logic extracted from: com.android.ddmlib.logcat.LogCatMessageParser
    private static final Pattern logcatLongPattern = Pattern.compile(
            "^\\[\\s(\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d\\.\\d+)"
                    + "\\s+(\\d*):\\s*(\\S+)\\s([VDIWEAF])/(.*)\\]$");

    //private String mDataLine;
    private String mCurChannel = "?";
    private String mCurLogLevel = "?";
    private String mCurPid = "?";
    private String mCurTid = "?";
    private String mCurTag = "?";
    private String mCurTime = "?:??";
    private List<String> messages = new ArrayList<>();

    private String previousLine;
    private String beforePreviousLine;

    public LogcatLine parse(String line) {
        if (line == null) {
            return null;
        }

        if (line.startsWith("--------- beginning of")){
            //Keep channel
            mCurChannel = line.substring(line.lastIndexOf(" ")+1);
            updatePrevious(line);
            return null;
        }

        Matcher matcher = logcatLongPattern.matcher(line);
        if (matcher.matches()) {
            //Keep details
            //mDataLine = line;
            mCurTime = matcher.group(1);
            mCurPid = matcher.group(2);
            mCurTid = matcher.group(3);
            mCurLogLevel = matcher.group(4);
            mCurTag = matcher.group(5).trim();
        }
        else if (!line.isEmpty()) {
            //Keep a message line
            messages.add(line);
        }
        else {
            //Build and return a logLine with all messages
            LogcatLine logLine = buildLogcatLine();
            updatePrevious(line);
            return logLine;
        }

        updatePrevious(line);
        return null;
    }

    private void updatePrevious(String line) {
        beforePreviousLine = previousLine + "";
        previousLine = line;
    }

    public LogcatLine buildLogcatLine() {
        if (messages.isEmpty()){
            return null;
        }
        LogcatLine logLine = new LogcatLine();
        //logLine.setOriginalLine(mDataLine);
        logLine.setExpanded(false); //TODO: ???

        logLine.setTimestamp(mCurTime);
        try {
            logLine.setProcessId(Integer.parseInt(mCurPid));
            logLine.setThreadId(Integer.parseInt(mCurTid));
        }
        catch (NumberFormatException nex){
            logLine.setProcessId(-1);
        }
        logLine.setLogLevelChar(mCurLogLevel.charAt(0));
        logLine.setLogLevel(LogcatLine.convertCharToLogLevel(logLine.getLogLevelChar()));
        logLine.setTag(mCurTag);
        logLine.setChannel(mCurChannel);

        if (!messages.isEmpty()) {
            String fullMessage = messages.get(0);
            if (messages.size()>1){
                for (int i=1; i<messages.size(); i++){
                    fullMessage += Humanizer.newLine() + messages.get(i);
                }
            }
            logLine.setMessage(fullMessage);

            //invalidate messages
            messages = new ArrayList<>();
        }

        /*String pkgName = ""; //$NON-NLS-1$
        Integer pid = Ints.tryParse(mCurPid);
        if (pid != null && device != null) {
            pkgName = device.getClientName(pid);
        }*/
        return logLine;
    }
}
