package es.rafaco.inappdevtools.library.logic.utils;

import android.content.Context;

import java.text.SimpleDateFormat;

public class DateUtils {

    public static String getElapsedTimeLowered(long oldTimeMillis){
        String elapsed = getElapsedTime(oldTimeMillis);
        elapsed = Character.toLowerCase(elapsed.charAt(0)) + elapsed.substring(1);
        return  elapsed;
    }

    public static String getElapsedTime(long oldTimeMillis){
        if (System.currentTimeMillis() - oldTimeMillis < 60*1000){
            return "Just now";
        }

        CharSequence relativeDate =
                android.text.format.DateUtils.getRelativeTimeSpanString(
                        oldTimeMillis,
                        System.currentTimeMillis(),
                        android.text.format.DateUtils.MINUTE_IN_MILLIS,
                        android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE);
        return relativeDate.toString();
    }


    public static String format(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return simpleDateFormat.format(timeMillis);
    }

    public static String formatPrecisionTime(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        return simpleDateFormat.format(timeMillis);
    }

    public static String localDateTimeCustomFormat(Context context, long timeMillis) {
        java.text.DateFormat localDateFormatter = android.text.format.DateFormat.getMediumDateFormat(context);
        java.text.DateFormat localTimeFormatter = android.text.format.DateFormat.getTimeFormat(context);

        return localTimeFormatter.format(timeMillis)+ ", " + localDateFormatter.format(timeMillis);
    }

    public static String localDateTimeFormat(Context context, long timeMillis) {
        return android.text.format.DateUtils.formatDateTime(context, timeMillis,
                android.text.format.DateUtils.FORMAT_SHOW_DATE |
                        android.text.format.DateUtils.FORMAT_NUMERIC_DATE |
                        android.text.format.DateUtils.FORMAT_SHOW_TIME);
    }
}
