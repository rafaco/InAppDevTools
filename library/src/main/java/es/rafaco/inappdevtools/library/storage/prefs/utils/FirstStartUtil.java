package es.rafaco.inappdevtools.library.storage.prefs.utils;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class FirstStartUtil {

    public static final String PREF_VALUE_KEY = "IS_FIRST_START";
    private static Boolean isSessionFromFirstStart;

    public static boolean isFirstStart(){
        if (isSessionFromFirstStart == null){
            return isSessionFromFirstStart();
        }
        return DevToolsPrefs.getBoolean(PREF_VALUE_KEY, true);
    }

    public static boolean isSessionFromFirstStart() {
        if (isSessionFromFirstStart == null){
            isSessionFromFirstStart = DevToolsPrefs.getBoolean(PREF_VALUE_KEY, true);
        }
        return isSessionFromFirstStart;
    }

    public static void saveFirstStart(){
        DevToolsPrefs.setBoolean(PREF_VALUE_KEY, false);
    }
}
