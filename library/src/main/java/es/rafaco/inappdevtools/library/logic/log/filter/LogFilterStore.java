package es.rafaco.inappdevtools.library.logic.log.filter;

import com.google.gson.Gson;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class LogFilterStore {

    public static final String PREF_VALUE_KEY = "LOG_FILTER";

    public static LogUiFilter get(){
        Gson gson = new Gson();
        String string = DevToolsPrefs.getString(PREF_VALUE_KEY, null);
        if (string == null){
            return null;
        }
        return gson.fromJson(string, LogUiFilter.class);
    }

    public static void store(LogUiFilter filter){
        Gson gson = new Gson();
        String json = gson.toJson(filter);
        DevToolsPrefs.setString(PREF_VALUE_KEY, json);
    }
}
