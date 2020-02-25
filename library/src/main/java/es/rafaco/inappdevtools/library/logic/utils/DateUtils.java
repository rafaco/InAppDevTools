/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.logic.utils;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.rafaco.inappdevtools.library.Iadt;

public class DateUtils {

    private DateUtils() { throw new IllegalStateException("Utility class"); }

    public static long getLong() {
        return System.currentTimeMillis();
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
            Log.e(Iadt.TAG, "Error parsing logcat date: " + text);
            e.printStackTrace();
            return -1;
        }
        return date.getTime();
    }

    public static String formatLogcatDate(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
        return simpleDateFormat.format(timeMillis);
    }

    public static String formatFull(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        return simpleDateFormat.format(timeMillis);
    }

    public static String formatShortDate(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        return simpleDateFormat.format(timeMillis);
    }

    public static String formatTimeWithMillis(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault());
        return simpleDateFormat.format(timeMillis);
    }
}
