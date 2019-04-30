package es.rafaco.inappdevtools.library.view.utils;

import android.text.TextUtils;

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
}
