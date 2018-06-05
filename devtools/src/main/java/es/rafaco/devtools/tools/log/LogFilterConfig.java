package es.rafaco.devtools.tools.log;

import android.text.TextUtils;
import android.util.Log;

public class LogFilterConfig {

    String presetFilter;
    String logFilter;
    String textFilter;

    public LogFilterConfig(String presetFilter, String logFilter, String textFilter) {
        this.presetFilter = presetFilter;
        this.logFilter = logFilter;
        this.textFilter = textFilter.toLowerCase();
    }

    public Boolean onlyMyPid(){
        return presetFilter != "All";
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
        }else if(presetFilter.equals("DevTools")){
            return "DevTools";
        }else{
            return null;
        }
    }

    public Boolean validate(LogLine logLine) {
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

        //Filter by Level
        if (!LogLine.validateLevel(logLine.getLogLevel(), logFilter)) {
            return false;
        }

        //Filter by Text
        if (!TextUtils.isEmpty(textFilter)
                && logLine.getLogOutput().toLowerCase().contains(textFilter)) {
            return false;
        }

        return true;
    }


}
