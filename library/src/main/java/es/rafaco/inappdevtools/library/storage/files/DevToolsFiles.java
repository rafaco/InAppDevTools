package es.rafaco.inappdevtools.library.storage.files;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DevToolsFiles {

    public static String storeCrashDetail(long crashId, String report) {
        return FileCreator.withContent(
                "crash",
                "crash_" + crashId + "_detail.txt",
                report);
    }

    public static String storeCrashLog(long crashId, String report) {
        return FileCreator.withContent(
                "crash",
                "crash_" + crashId + "_logcat.txt",
                report);
    }

    public static String storeInfo(String report, long timeMillis) {
        return FileCreator.withContent(
                "info",
                "info_" + timeMillis + ".txt",
                report);
    }

    public static String storeSources(String report, long timeMillis) {
        return FileCreator.withContent(
                "sources",
                "sources_" + timeMillis + ".txt",
                report);
    }

    public static File prepareLogcat(long timeMillis) {
        return FileCreator.prepare(
                "logcat",
                "logcat_" + timeMillis + ".txt");
    }

    public static File prepareDatabase(String dbName, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        String formattedTime = sdf.format(new Date(time));
        return FileCreator.prepare(
                "db",
                "db_" + dbName + "_" + formattedTime + ".csv");
    }

    public static File prepareScreen(long id, boolean fromCrash) {
        String subfolder = fromCrash ? "crash" : "screenshots";
        String filename = fromCrash ?
                "crash_" + id + "_screen" :
                "screenshots" + id;

        return FileCreator.prepare(
                subfolder,
                filename + ".jpg");
    }
}
