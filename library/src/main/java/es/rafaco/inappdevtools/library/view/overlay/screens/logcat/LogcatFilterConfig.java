package es.rafaco.inappdevtools.library.view.overlay.screens.logcat;

import android.text.TextUtils;

public class LogcatFilterConfig {

    String presetFilter;
    String logFilter;
    String textFilter;

    public LogcatFilterConfig(String presetFilter, String logFilter, String textFilter) {
        this.presetFilter = presetFilter;
        this.logFilter = logFilter;
        this.textFilter = textFilter.toLowerCase();
    }

    public Boolean onlyMyPid(){
        return !presetFilter.equals("All");
    }

    public int pid(){
        if (onlyMyPid()){
            return android.os.Process.myPid();
        }
        return -1;
    }

    public String tag(){
        if(presetFilter.equals("Cordova")){
            return "chromium";
        }else if(presetFilter.equals("Iadt")){
            return "Iadt";
        }else{
            return null;
        }
    }

    public Boolean validate(LogcatLine logLine) {

        //Filter by Level
        if (!LogcatLine.validateLevel(logLine.getLogLevel(), logFilter)) {
            return false;
        }

        //Filter by PID
        if (onlyMyPid()
                && logLine.getProcessId() != pid()) {
            return false;
        }
        //Filter by Tag
        if (tag() != null
                && !logLine.getTag().equals(tag())) {
            return false;
        }

        //Filter by text
        if (!TextUtils.isEmpty(textFilter)
                && !logLine.getLogOutput().toLowerCase().contains(textFilter)) {
            return false;
        }

        return true;
    }


}
