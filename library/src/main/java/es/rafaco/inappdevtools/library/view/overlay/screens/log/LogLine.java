package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

//#ifdef MODERN
import androidx.core.content.ContextCompat;
//#else
//@import android.support.v4.content.ContextCompat;
//#endif


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.rafaco.inappdevtools.library.R;


public class LogLine {

    private static final int TIMESTAMP_LENGTH = 19;
    public static final int LOG_WTF = 100;

    private static Pattern logPattern = Pattern.compile(
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
    private String timestamp;
    private boolean expanded = false;
    private boolean highlighted = false;

    public static LogLine newLogLine(String originalLine, boolean expanded) {

        LogLine logLine = new LogLine();
        logLine.setOriginalLine(originalLine);
        logLine.setExpanded(expanded);

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

        Matcher matcher = logPattern.matcher(originalLine);

        if (matcher.find(startIdx)) {
            logLine.setLogLevelChar(matcher.group(1).charAt(0));
            logLine.setLogLevel(convertCharToLogLevel(logLine.getLogLevelChar()));
            logLine.setTag(matcher.group(2));
            logLine.setProcessId(Integer.parseInt(matcher.group(3)));

            logLine.setLogOutput(originalLine.substring(matcher.end()));

        } else {
            //Log.d(DevTools.TAG, String.format("Line doesn't match pattern: %s", originalLine));
            logLine.setLogOutput(originalLine);
            logLine.setLogLevel(-1);
        }

        return logLine;

    }

    private static int convertCharToLogLevel(char logLevelChar) {

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
            case Log.VERBOSE:
                return 'V';
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
        }
        return ' ';
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
            //Log.w("RAFA", "Originalname problem");
            //06-07 23:01:37.069 E/AndroidRuntime( 7640): FATAL EXCEPTION: main
            //06-07 23:01:37.069 E/AndroidRuntime(7640): FATAL EXCEPTION: main
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

    public void setLogOutput(String logOutput) {
        this.logOutput = logOutput;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
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
}
