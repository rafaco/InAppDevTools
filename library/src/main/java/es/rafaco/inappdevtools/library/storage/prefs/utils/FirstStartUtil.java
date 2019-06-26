package es.rafaco.inappdevtools.library.storage.prefs.utils;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class FirstStartUtil {

    public static final String PREF_VALUE_KEY = "IS_FIRST_START";

    public static boolean isFirstStart(){
        return DevToolsPrefs.getBoolean(PREF_VALUE_KEY, true);
    }

    public static void saveFirstStart(){
        DevToolsPrefs.setBoolean(PREF_VALUE_KEY, false);
    }
}
