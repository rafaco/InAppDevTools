package es.rafaco.devtools.utils;

import android.content.Context;
import android.content.SharedPreferences;

import es.rafaco.devtools.DevTools;

public class FirstStartUtil {

    public static final String SHARED_PREFS_KEY = "es.rafaco.devtools";
    public static final String PREF_KEY = "IS_FIRST_START";

    public static boolean isFirstStart(){
        Context context = DevTools.getAppContext();
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_KEY, true);
    }

    public static void saveFirstStart(){
        storeBoolean(SHARED_PREFS_KEY, PREF_KEY, false);
    }

    private static void storeBoolean(String prefKey, String key, boolean value){
        Context context = DevTools.getAppContext();
        SharedPreferences prefs = context.getSharedPreferences(
                prefKey, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }
}