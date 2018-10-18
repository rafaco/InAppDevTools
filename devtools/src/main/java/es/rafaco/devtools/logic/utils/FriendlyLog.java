package es.rafaco.devtools.logic.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Date;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.storage.db.entities.Friendly;

public class FriendlyLog {
    public static String TAG = "FriendlyLog";

    public static void log(String severity, String category, String type, String message) {
        log(new Date().getTime(), severity, category, type, message);
    }

    public static void log(long date, String severity, String category, String type, String message) {
        final Friendly log = new Friendly();
        log.setDate(date);
        log.setSeverity(severity);
        log.setCategory(category);
        log.setType(type);
        log.setMessage(message);

        logAtLogcat(log);
        insertOnBackground(log);
    }

    private static void logAtLogcat(Friendly log) {
        String text = getFormatted(log);
        switch (log.getSeverity()){
            case "D":
                Log.d(TAG, text);
                break;
            case "I":
                Log.i(TAG, text);
                break;
            case "V":
                Log.v(TAG, text);
                break;
            case "W":
                Log.w(TAG, text);
                break;
            case "E":
                Log.e(TAG, text);
                break;
            case "F":
                Log.wtf(TAG, text);
                break;
        }

    }

    private static void insertOnBackground(final Friendly log) {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                DevTools.getDatabase().friendlyDao().insert(log);
            }
        });
    }

    @NonNull
    private static String getFormatted(Friendly log) {
        return String.format("%s-%s: %s", log.getCategory(), log.getType(), log.getMessage());
    }

    public static int getColor(Friendly log) {
        String type = log.getSeverity();
        int color = Color.WHITE;
        if(type.equals("V")){
            color = R.color.rally_blue; //Color.rgb(0, 0, 200);
        }
        if(type.equals("D")){
            color = R.color.rally_blue_med; //Color.rgb(0, 0, 200);
        }
        else if(type.equals("I")){
            color = R.color.rally_green; //Color.rgb(0, 128, 0);
        }
        else if(type.equals("W")){
            color = R.color.rally_yellow; //Color.parseColor("#827717");//rgb(255, 234, 0);
        }
        else if(type.equals("E") || type.equals("F")){
            color = R.color.rally_orange; //Color.rgb(255, 0, 0);
        }

        return color;
    }

    public static int getIcon(Friendly log) {
        if (log.getCategory().equals("App")){
            if (log.getType().equals("Foreground")){
                return R.drawable.ic_flip_to_front_white_24dp;
            }
            else if (log.getType().equals("Background")){
                return R.drawable.ic_flip_to_back_white_24dp;
            }else{
                return R.drawable.ic_flag_white_24dp;
            }
        }
        return -1;
    }
}
