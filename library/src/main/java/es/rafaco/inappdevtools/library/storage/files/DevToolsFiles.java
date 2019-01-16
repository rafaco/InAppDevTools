package es.rafaco.inappdevtools.library.storage.files;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.rafaco.inappdevtools.library.storage.db.entities.Crash;

public class DevToolsFiles {

    public static String storeCrashDetail(Crash crash, String report) {
        return FileCreator.withContent(
                "crash",
                "crash_"+crash.getUid()+"_detail.txt",
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

    public static File prepareSource(String target, String extension) {
        return FileCreator.prepare(
                "sources",
                target + "." + extension);
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

    public static File prepareScreen(long mImageTime, boolean fromCrash) {
        String first = "screen";
        String second = fromCrash ? "crash" : "user";
        String fileName = String.format(first + "_" + second + "_" + mImageTime);

        return FileCreator.prepare(
                "screen",
                fileName + ".jpg");
    }
}
