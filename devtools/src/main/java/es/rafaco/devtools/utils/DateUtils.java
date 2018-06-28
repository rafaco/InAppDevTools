package es.rafaco.devtools.utils;

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
}
