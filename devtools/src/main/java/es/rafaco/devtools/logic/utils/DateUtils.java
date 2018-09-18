package es.rafaco.devtools.logic.utils;

import java.text.SimpleDateFormat;

public class DateUtils {

    public static String getElapsedTimeLowered(long oldTimeMillis){
        String elapsed = getElapsedTime(oldTimeMillis);
        elapsed = Character.toLowerCase(elapsed.charAt(0)) + elapsed.substring(1);
        return  elapsed;
    }

    public static String getElapsedTime(long oldTimeMillis){
        CharSequence relativeDate =
                android.text.format.DateUtils.getRelativeTimeSpanString(
                        oldTimeMillis,
                        System.currentTimeMillis(),
                        android.text.format.DateUtils.MINUTE_IN_MILLIS,
                        android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE);
        return relativeDate.toString();
    }


    public static String format(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return simpleDateFormat.format(timeMillis);
    }
}
