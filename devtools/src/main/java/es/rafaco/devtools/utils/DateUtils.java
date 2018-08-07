package es.rafaco.devtools.utils;

import java.text.SimpleDateFormat;

import es.rafaco.devtools.db.errors.Crash;

public class DateUtils {

    public static String getElapsedTimeString(long oldTimeMillis){
        CharSequence relativeDate =
                android.text.format.DateUtils.getRelativeTimeSpanString(
                        oldTimeMillis,
                        System.currentTimeMillis(),
                        android.text.format.DateUtils.MINUTE_IN_MILLIS,
                        android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE);
        return relativeDate.toString();
    }


    public static String formatToDateAndTimeString(Crash crash) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return simpleDateFormat.format(crash.getDate());
    }
}
