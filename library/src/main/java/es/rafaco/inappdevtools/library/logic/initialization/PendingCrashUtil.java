package es.rafaco.inappdevtools.library.logic.initialization;

import android.content.Context;
import android.content.SharedPreferences;

import es.rafaco.inappdevtools.library.DevTools;

public class PendingCrashUtil {

    public static final String PREF_KEY = "inappdevtools";
    public static final String PENDING_CRASH_KEY = "PENDING_CRASH";

    public static boolean isPending(){
        Context context = DevTools.getAppContext();
        return isPending(context);
    }

    public static boolean isPending(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        return prefs.getBoolean(PENDING_CRASH_KEY, false);
    }

    public static void savePending(){
        storeBoolean(PREF_KEY, PENDING_CRASH_KEY, true);
    }

    public static void clearPending(){
        storeBoolean(PREF_KEY, PENDING_CRASH_KEY, false);
    }

    private static void storeBoolean(String prefKey, String key, boolean value){
        Context context = DevTools.getAppContext();
        SharedPreferences prefs = context.getSharedPreferences(
                prefKey, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }
}
