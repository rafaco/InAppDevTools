package es.rafaco.inappdevtools.library.logic.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private DateUtils() { throw new IllegalStateException("Utility class"); }

    public static long getLong() {
        return System.currentTimeMillis();
    }

    public static String getElapsedTimeLowered(long oldTimeMillis){
        String elapsed = getElapsedTime(oldTimeMillis);
        elapsed = Character.toLowerCase(elapsed.charAt(0)) + elapsed.substring(1);
        return  elapsed;
    }

    public static String getElapsedTime(long oldTimeMillis){
        if (getLong() - oldTimeMillis < 60*1000){
            return "Just now";
        }

        CharSequence relativeDate =
                android.text.format.DateUtils.getRelativeTimeSpanString(
                        oldTimeMillis,
                        getLong(),
                        android.text.format.DateUtils.MINUTE_IN_MILLIS,
                        android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE);
        return relativeDate.toString();
    }

    public static String formatNow() {
        return format(getLong());
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


    public static long parseLogcatDate(String text){
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String dateTimePattern = "yyyy-MM-dd HH:mm:ss.SSS";
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimePattern);
        Date date = null;
        try {
            date = sdf.parse(year + "-" + text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String formatLogcatDate(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        return simpleDateFormat.format(timeMillis);
    }
}
