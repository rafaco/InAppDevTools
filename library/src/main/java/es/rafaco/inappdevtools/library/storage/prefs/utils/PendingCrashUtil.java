package es.rafaco.inappdevtools.library.storage.prefs.utils;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class PendingCrashUtil {

    public static final String PREF_VALUE_KEY = "PENDING_CRASH";
    private static Boolean iSessionFromPending;

    public static void savePending(){
        DevToolsPrefs.setBoolean(PREF_VALUE_KEY, true);
    }

    public static void clearPending(){
        DevToolsPrefs.setBoolean(PREF_VALUE_KEY, false);
    }

    public static boolean isPending(){
        if (iSessionFromPending == null){
            isSessionFromPending();
        }
        return DevToolsPrefs.getBoolean(PREF_VALUE_KEY, false);
    }

    public static boolean isSessionFromPending() {
        if (iSessionFromPending == null){
            iSessionFromPending = DevToolsPrefs.getBoolean(PREF_VALUE_KEY, false);
        }
        return iSessionFromPending;
    }
}
