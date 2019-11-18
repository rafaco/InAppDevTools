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

package es.rafaco.inappdevtools.library.view.overlay.screens.logcat;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LogcatLine {

    private static final int TIMESTAMP_LENGTH = 19;
    public static final int LOG_WTF = 100;

    private static Pattern defaultPattern = Pattern.compile(
            // Line sample: 2019-06-30 01:03:07.079 1090-1237/es.rafaco.inappdevtools.app D/OpenGLRenderer:
            // log level
            "(\\w)" +
            "/" +
            // tag
            "([^(]+)" +
            "\\(\\s*" +
            // pid
            "(\\d+)" +
            // optional weird number that only occurs on ZTE blade
            "(?:\\*\\s*\\d+)?" +
            "\\): ");

    private String originalLine;
    private int logLevel;
    private Character logLevelChar;
    private String tag;
    private String logOutput;
    private int processId = -1;
    private int threadId = -1;
    private String timestamp;
    private boolean expanded = false;
    private boolean highlighted = false;
    private String channel;

    public static LogcatLine newLogLine(String originalLine, boolean expanded) {

        if (originalLine.isEmpty()) {
            return null;
        }

        LogcatLine logLine = new LogcatLine();
        logLine.setOriginalLine(originalLine);
        logLine.setExpanded(expanded); //TODO: ???

        int startIdx = 0;

        // if the first char is a digit, then this starts out with a timestamp
        // otherwise, it's a legacy log or the beginning of the log output or something
        if (!TextUtils.isEmpty(originalLine)
                && Character.isDigit(originalLine.charAt(0))
                && originalLine.length() >= TIMESTAMP_LENGTH) {
            String timestamp = originalLine.substring(0, TIMESTAMP_LENGTH - 1);
            logLine.setTimestamp(timestamp);
            startIdx = TIMESTAMP_LENGTH; // cut off timestamp
        }

        Matcher matcher = defaultPattern.matcher(originalLine);

        if (matcher.find(startIdx)) {
            logLine.setLogLevelChar(matcher.group(1).charAt(0));
            logLine.setLogLevel(convertCharToLogLevel(logLine.getLogLevelChar()));
            logLine.setTag(matcher.group(2).trim());
            logLine.setProcessId(Integer.parseInt(matcher.group(3)));
            logLine.setMessage(originalLine.substring(matcher.end()));
        } else {
            //Log.d(Iadt.TAG, String.format("Line doesn't match pattern: %s", originalLine));
            logLine.setMessage(originalLine);
            logLine.setLogLevel(-1);
        }

        return logLine;
    }

    public static void addMessage(LogcatLine logcatLine, String message) {
        if (TextUtils.isEmpty(logcatLine.getMessage())){
            logcatLine.setMessage(message);
        }
        else{
            logcatLine.setMessage(logcatLine.getMessage() + Humanizer.newLine() + message);
        }
    }

    public static int convertCharToLogLevel(char logLevelChar) {

        switch (logLevelChar) {
            case 'V':
                return Log.VERBOSE;
            case 'D':
                return Log.DEBUG;
            case 'I':
                return Log.INFO;
            case 'W':
                return Log.WARN;
            case 'E':
                return Log.ERROR;
            case 'F':
                return LOG_WTF; // 'F' actually stands for 'WTF', which is a real Android log level in 2.2
        }
        return -1;
    }

    private static char convertLogLevelToChar(int logLevel) {

        switch (logLevel) {
            case Log.DEBUG:
                return 'D';
            case Log.ERROR:
                return 'E';
            case Log.INFO:
                return 'I';
            case Log.WARN:
                return 'W';
            case LOG_WTF:
                return 'F';
            case Log.VERBOSE:
            default:
                return 'V';
        }
    }

    public static boolean validateLevel(int logLevel, String logLevelLimit) {
        if (logLevel == -1) {
            //TODO: // starter lines like "begin of log etc. etc."
            return true;
        }

        return logLevel >= convertCharToLogLevel(logLevelLimit.charAt(0));
    }

    public static int getLogColor(Context context, String type) {
        int color = Color.WHITE;
        if(type.equals("V") || type.equals("D")){
            color = R.color.rally_blue; //Color.rgb(0, 0, 200);
        }
        else if(type.equals("I")){
            color = R.color.rally_green; //Color.rgb(0, 128, 0);
        }
        else if(type.equals("W")){
            color = R.color.rally_yellow; //Color.parseColor("#827717");//rgb(255, 234, 0);
        }
        else if(type.equals("E") || type.equals("F")){
            color = R.color.rally_orange; //Color.rgb(255, 0, 0);
        }

        if (color == Color.WHITE){
            return Color.WHITE;
        }else{

            return ContextCompat.getColor(context, color);
        }
    }

    public int getLogColor(Context context){
        return getLogColor(context, String.valueOf(logLevelChar));
    }

    public String getOriginalLine() {
        String previousImp = getOriginalLine_OLD();
        if (!previousImp.equals(originalLine)){
        }
        return originalLine;
    }

    public String getOriginalLine_OLD() {

        if (logLevel == -1) { // starter line like "begin of log etc. etc."
            return logOutput;
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (timestamp != null) {
            stringBuilder.append(timestamp).append(' ');
        }

        stringBuilder.append(convertLogLevelToChar(logLevel))
                .append('/')
                .append(tag)
                .append('(')
                .append(processId)
                .append("): ")
                .append(logOutput);

        return stringBuilder.toString();
    }

    public String getLogLevelText() {
        return Character.toString(convertLogLevelToChar(logLevel));
    }

    public void setOriginalLine(String originalLine) {
        this.originalLine = originalLine;
    }

    public Character getLogLevelChar() {
        return logLevelChar;
    }

    public void setLogLevelChar(Character logLevelChar) {
        this.logLevelChar = logLevelChar;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLogOutput() {
        return logOutput;
    }

    public void setMessage(String logOutput) {
        this.logOutput = logOutput;
    }

    public String getMessage() {
        return logOutput;
    }



    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public Friendly parseToFriendly() {
        Friendly parsed = new Friendly();
        parsed.setCategory("Logcat");
        parsed.setSubcategory(!TextUtils.isEmpty(getTag()) ? getTag() : "-");
        if (!TextUtils.isEmpty(timestamp)){
            parsed.setDate(DateUtils.parseLogcatDate(timestamp));
        }else{
            Log.d("LOGCAT", "Ignored without date: " + originalLine);
        }
        parsed.setSeverity(""+convertLogLevelToChar(logLevel));
        parsed.setMessage(getLogOutput());

        String extra = "";
        extra += "Date: " + DateUtils.format(parsed.getDate()) + Humanizer.newLine();
        extra += "Source: " + parsed.getCategory() + Humanizer.newLine();
        extra += "Channel: " + channel + Humanizer.newLine();
        extra += "Tag: " + tag + Humanizer.newLine();
        extra += "Process: " + processId + Humanizer.newLine();
        extra += "Thread: " + threadId;
        parsed.setExtra(extra);

        return parsed;
    }

    public static long extractDate(String line){
        if (!TextUtils.isEmpty(line)
                && Character.isDigit(line.charAt(0))
                && line.length() >= TIMESTAMP_LENGTH) {
            String timestamp = line.substring(0, TIMESTAMP_LENGTH - 1);
            return DateUtils.parseLogcatDate(timestamp);
        }
        else{
            return 0;
        }
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
