package es.rafaco.inappdevtools.library.logic.log.filter;

import com.google.gson.Gson;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class LogFilterStore {

    public static final String PREF_VALUE_KEY = "LOG_FILTER";

    public static LogFilter get(){
        Gson gson = new Gson();
        String string = DevToolsPrefs.getString(PREF_VALUE_KEY, null);
        if (string == null){
            return new LogFilter();
        }

        return gson.fromJson(string, LogFilter.class);
    }

    public static void store(LogFilter filter){
        Gson gson = new Gson();
        String json = gson.toJson(filter);
        DevToolsPrefs.setString(PREF_VALUE_KEY, json);
    }
}
