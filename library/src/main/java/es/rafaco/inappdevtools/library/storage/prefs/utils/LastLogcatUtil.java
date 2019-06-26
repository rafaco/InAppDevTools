package es.rafaco.inappdevtools.library.storage.prefs.utils;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class LastLogcatUtil {

    public static final String PREF_VALUE_KEY = "LAST_LOGCAT";

    public static Long get(){
        return DevToolsPrefs.getLong(PREF_VALUE_KEY, -1L);
    }

    public static void set(long value){
        DevToolsPrefs.setLong(PREF_VALUE_KEY, value);
    }
}
