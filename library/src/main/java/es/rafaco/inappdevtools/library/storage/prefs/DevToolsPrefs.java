package es.rafaco.inappdevtools.library.storage.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import es.rafaco.inappdevtools.library.Iadt;

public class DevToolsPrefs {

    public static final String SHARED_PREFS_KEY = "iadt";

    public static boolean contains(String key){
        return getPrefs().contains(key);
    }

    public static String getString(String key, String defValue){
        return getPrefs().getString(key, defValue);
    }

    public static void setString(String key, String value){
        getPrefs().edit().putString(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue){
        return getPrefs().getBoolean(key, defValue);
    }

    public static void setBoolean(String key, boolean value){
        getPrefs().edit().putBoolean(key, value).apply();
    }

    public static long getLong(String key, long defValue){
        return getPrefs().getLong(key, defValue);
    }

    public static void setLong(String key, long value){
        getPrefs().edit().putLong(key, value).apply();
    }

    private static SharedPreferences getPrefs() {
        return getContext().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }

    private static Context getContext() {
        return Iadt.getAppContext();
    }
}
