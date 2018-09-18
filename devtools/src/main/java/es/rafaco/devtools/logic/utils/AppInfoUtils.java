package es.rafaco.devtools.logic.utils;

import android.content.Context;
import android.provider.Settings;

public class AppInfoUtils {

    public static String getUUID(Context context){
        String uuid = Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return uuid;
    }
}
