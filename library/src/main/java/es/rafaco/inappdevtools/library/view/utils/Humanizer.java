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

package es.rafaco.inappdevtools.library.view.utils;

import android.text.TextUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import es.rafaco.inappdevtools.library.logic.utils.DateUtils;

public class Humanizer {

    private Humanizer() {
        throw new IllegalStateException("Utility class");
    }

    //region [ TEXT ]

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

    public static String removeHead(String text, String head) {
        if (!TextUtils.isEmpty(text)
                && text.startsWith(head)) {
            return text.substring(head.length());
        }
        return text;
    }

    public static String removeTail(String text, String tail) {
        if (!TextUtils.isEmpty(text)
                && text.endsWith(tail)) {
            return text.substring(0, text.length() - tail.length());
        }
        return text;
    }

    public static String removeTailStartingWith(String text, String tailStart) {
        if (!TextUtils.isEmpty(text)
                && text.contains(tailStart)) {
            return text.substring(0, text.indexOf(tailStart));
        }
        return text;
    }

    public static String truncate(String text, int maxLength) {
        if (!TextUtils.isEmpty(text) && text.length()>3-1 && text.length()>maxLength){
            return text.substring(0, maxLength-3-1) + "...";
        }
        return text;
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

    //endregion

    //region [ MULTILINE TEXT ]

    public static String newLine(){
        return System.getProperty("line.separator");
    }

    public static String fullStop(){
        return newLine()+ newLine();
    }

    public static int countLines(String text) {
        if (TextUtils.isEmpty(text)){
            return 0;
        }
        return text.split(newLine()).length;
    }

    public static String prependLines(String paragraph, String prefix){
        if (TextUtils.isEmpty(paragraph)){
            return paragraph;
        }
        StringBuilder resultBuilder = new StringBuilder();
        String[] lines = paragraph.split(Humanizer.newLine());
        for (int i = 0; i<lines.length; i++){
            resultBuilder.append(prefix + lines[i] + Humanizer.newLine());
        }
        return resultBuilder.toString();
    }

    public static String multiLineComment(String paragraph){
        if (TextUtils.isEmpty(paragraph)){
            return paragraph;
        }
        String tokenStart = "/*";
        String tokenMiddle = " * ";
        String tokenEnd = " */";
        String result = tokenStart + Humanizer.newLine();
        result += prependLines(paragraph, tokenMiddle);
        result += tokenEnd + Humanizer.newLine();
        return result;
    }

    public static String trimNewlines(String text) {
        String result = text;
        String newLine = newLine();
        while (result.startsWith(newLine)) {
            result = removeHead(result, newLine);
        }
        while (result.endsWith(newLine)) {
            result = removeTail(result, newLine);
        }
        return result;
    }

    //endregion

    //region [ TIME ]

    public static String getElapsedTimeLowered(long oldTimeMillis){
        String elapsed = getElapsedTime(oldTimeMillis);
        elapsed = Character.toLowerCase(elapsed.charAt(0)) + elapsed.substring(1);
        return  elapsed;
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

    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    public static final long MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;
    public static final long MILLIS_PER_MONTH = 30 * MILLIS_PER_DAY;
    public static final long MILLIS_PER_YEAR = 365 * MILLIS_PER_DAY;

    public static String getDuration(long durationMillis) {
        String humanDuration = null;
        if (durationMillis >= MILLIS_PER_YEAR) {
            long years = (durationMillis / MILLIS_PER_YEAR);
            long months = (durationMillis - (years * MILLIS_PER_YEAR))/MILLIS_PER_MONTH;
            humanDuration = String.format("%sw %sd", years, months);
        }
        else if (durationMillis >= MILLIS_PER_MONTH) {
            long months = (durationMillis / MILLIS_PER_MONTH);
            long days = (durationMillis - (months * MILLIS_PER_MONTH))/MILLIS_PER_WEEK;
            humanDuration = String.format("%sm %sd", months, days);
        }
        else if (durationMillis >= MILLIS_PER_DAY) {
            long days = (durationMillis / MILLIS_PER_DAY);
            long hours = (durationMillis - (days * MILLIS_PER_DAY))/MILLIS_PER_HOUR;
            humanDuration = String.format("%sd %sh", days, hours);
        }
        else if (durationMillis >= MILLIS_PER_HOUR) {
            long hours = (durationMillis / MILLIS_PER_HOUR);
            long minutes = (durationMillis - (hours * MILLIS_PER_HOUR)) / MILLIS_PER_MINUTE;
            humanDuration = String.format("%sh %smin", hours, minutes);
        }
        else if (durationMillis >= MILLIS_PER_MINUTE) {
            long minutes = (durationMillis / MILLIS_PER_MINUTE);
            long seconds = (durationMillis - (minutes * MILLIS_PER_MINUTE)) / MILLIS_PER_SECOND;
            humanDuration = String.format("%smin %ss", minutes, seconds);
        }
        else if (durationMillis >= MILLIS_PER_SECOND) {
            int seconds = (int) (durationMillis / MILLIS_PER_SECOND);
            long milliseconds = (durationMillis - (seconds * MILLIS_PER_SECOND));
            humanDuration = String.format("%ss %sms", seconds, milliseconds);
        }
        else {
            long milliseconds = durationMillis;
            humanDuration = String.format("%sms", milliseconds);
        }
        return humanDuration;
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

    //endregion

    //region [ BYTE/KB ]

    public static String parseByte(long bytes) {
        //TODO: verify correctness of base1000
        return humanReadableByteCount(bytes, true);
    }

    public static String parseKb(long kb) {
        //TODO: verify correctness of base1000
        return humanReadableByteCount(kb*1000, true);
    }

    public static String humanReadableByteCount(long bytes, boolean base1000) {
        int unit = base1000 ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (base1000 ? "kMGTPE" : "KMGTPE").charAt(exp-1) + "";

        // We normally used kB when it should be KiB according to ISO
        // Find out more at: https://es.wikipedia.org/wiki/Kilobyte
        boolean strictIsoNaming = false;
        if (strictIsoNaming && !base1000) pre += "i";

        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static long countBytes(String text){
        if (TextUtils.isEmpty(text))
            return 0;

        byte[] utf8Bytes = null;
        utf8Bytes = text.getBytes(Charset.forName("UTF-8"));
        if (utf8Bytes == null){
            return 0;
        }
        return utf8Bytes.length;
    }

    public static String getStringSize(String text){
        long stringBytes = countBytes(text);
        return humanReadableByteCount(stringBytes, false);
    }

    //endregion

    public static String unavailable(String text, String defaultValue) {
        if (TextUtils.isEmpty(text)){
            return defaultValue;
        }
        return text;
    }

    public static String join(String separator, String...strings) {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, strings);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            sb.append(s);
            if (i + 1 < list.size())
                sb.append(separator);
        }
        return sb.toString();
    }

    public static String plural(int count, String singularName){
        boolean isSingular = count == 1;
        if (isSingular)
            return count + " " + singularName;

        String[] esPluralSuffixes = {"s", "ss", "sh", "ch", "x", "z"};
        boolean isEsPlural = false;
        for (String esPluralSuffix: esPluralSuffixes) {
            if (singularName.endsWith(esPluralSuffix)) {
                isEsPlural = true;
                break;
            }
        }
        return count + " " + singularName + (isEsPlural ? "es" : "s");
    }
}

