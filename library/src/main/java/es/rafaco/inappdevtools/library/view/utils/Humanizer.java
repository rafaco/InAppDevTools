package es.rafaco.inappdevtools.library.view.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.rafaco.inappdevtools.library.logic.utils.DateUtils;

public class Humanizer {

    public static String newLine(){
        return System.getProperty("line.separator");
    }

    public static String fullStop(){
        return newLine()+ newLine();
    }

    public static String toCapitalCase(String text){
        if (TextUtils.isEmpty(text)){
            return text;
        }
        String result = text.substring(0,1).toUpperCase();
        if (text.length()>1) {
            result += text.substring(1).toLowerCase();
        }
        return result;
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
            return 0;
        }

        return text.length() - text.replace(character, "").length();
    }

    public static String getLastPart(String text, String separator) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }

        //Matcher matcher = Pattern.compile(separator, Pattern.LITERAL).matcher(text);
        String[] parts = text.split("[" + separator + "]");
        if (parts == null || parts.length == 0)
            return text;

        //String lastPart = matcher.group(matcher.groupCount());
        String lastPart = parts[parts.length-1];
        if (lastPart != null
                && lastPart.length()>0)
            return lastPart;

        return text;
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
                        Iadt.getContext(),
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

    public static boolean isEven(int x){
        return (x % 2 == 0);
    }

    public static String ordinal(int position){
        if (position < 0){
            return String.valueOf(position);
        }

        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (position % 100) {
            case 11:
            case 12:
            case 13:
                return position + "th";
            default:
                return position + sufixes[position % 10];
        }
    }


    //region [ BYTE/KB ]

    public static String parseByte(long bytes) {
        return humanReadableByteCount(bytes, true);
    }

    public static String parseKb(long kb) {
        return humanReadableByteCount(kb*1000, true);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    //endregion
}
