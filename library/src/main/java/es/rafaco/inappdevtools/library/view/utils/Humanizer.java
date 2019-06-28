package es.rafaco.inappdevtools.library.view.utils;

import android.text.TextUtils;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;

public class Humanizer {

    public static String capital(String text){
        if (TextUtils.isEmpty(text)){
            return text;
        }
        return text.substring(0,1).toUpperCase() + text.substring(1);
    }

    public static int countLines(String text) {
        if (TextUtils.isEmpty(text)){
            return 0;
        }
        return text.split(newLine()).length;
    }

    public static int countOcurrences(String text, String character) {
        if (TextUtils.isEmpty(text)
                || TextUtils.isEmpty(character)
                || character.length() > 1 ){
            return -1;
        }

        return text.length() - text.replace(character, "").length();
    }

    public static String newLine(){
        return System.getProperty("line.separator");
    }

    public static String fullStop(){
        return newLine()+ newLine();
    }

    public static String getElapsedTimeLowered(long oldTimeMillis){
        String elapsed = getElapsedTime(oldTimeMillis);
        elapsed = Character.toLowerCase(elapsed.charAt(0)) + elapsed.substring(1);
        return  elapsed;
    }


    public static String getElapsedTime(long oldTimeMillis, long newTimeMillis){
        return newTimeMillis-oldTimeMillis + "ms";
        //TODO: FIX IT
        /*CharSequence absoluteDate =
                android.text.format.DateUtils.formatDateRange(
                        Iadt.getAppContext(),
                        oldTimeMillis,
                        newTimeMillis,
                        android.text.format.DateUtils.FORMAT_ABBREV_ALL);
        return absoluteDate.toString();
        */
    }

    public static String getElapsedTime(long oldTimeMillis){
        if (DateUtils.getLong() - oldTimeMillis < 60*1000){
            return "Just now";
        }

        CharSequence relativeDate =
                android.text.format.DateUtils.getRelativeTimeSpanString(
                        oldTimeMillis,
                        DateUtils.getLong(),
                        android.text.format.DateUtils.MINUTE_IN_MILLIS,
                        android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE);
        return relativeDate.toString();
    }
}
