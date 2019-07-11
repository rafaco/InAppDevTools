package es.rafaco.inappdevtools.library.logic.log;

public class FriendlyLog {

    public static final String TAG = "FriendlyLog";

    public enum LEVEL { V, D, I, W, E, F, WTF }

    public static void log(String message){}

    public static void log(String severity, String category, String type, String message) {}

    public static void log(long date, String severity, String category, String type, String message) {}

    public static void log(String severity, String category, String type, String message, String extra) {}

    public static void log(long date, String severity, String category, String type, String message, String extra) {}

    public static void logException(String message, Throwable e) { }
}
