package es.rafaco.inappdevtools.library.storage.prefs.utils;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class PendingCrashUtil {

    public static final String PREF_VALUE_KEY = "PENDING_CRASH";

    public static boolean isPending(){
        return DevToolsPrefs.getBoolean(PREF_VALUE_KEY, false);
    }

    public static void savePending(){
        DevToolsPrefs.setBoolean(PREF_VALUE_KEY, true);
    }

    public static void clearPending(){
        DevToolsPrefs.setBoolean(PREF_VALUE_KEY, false);
    }
}
