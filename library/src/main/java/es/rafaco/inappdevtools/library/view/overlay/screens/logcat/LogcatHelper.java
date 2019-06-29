package es.rafaco.inappdevtools.library.view.overlay.screens.logcat;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.entities.Logcat;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreenHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.console.Shell;

public class LogcatHelper extends OverlayScreenHelper {

    @Override
    public String getReportPath() {
        File file = DevToolsFiles.prepareLogcat(System.currentTimeMillis());
        if (file == null)
            return null;

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("logcat -d -f " + file);
            process.waitFor();
            return file.getPath();

        } catch (IOException e) {
            FriendlyLog.logException("Exception", e);
        } catch (InterruptedException e) {
            FriendlyLog.logException("Exception", e);
            if (process!=null){
                process.destroy();
            }
            Thread.currentThread().interrupt();
        }

        return null;
    }

    @Override
    public String getReportContent() {
        return null;
    }

    public static void clearLogcatBuffer() {
        String[] fullCommand = Shell.formatBashCommand("logcat -c");
        Process process = null;
        try {
            process = new ProcessBuilder()
                    .command(fullCommand)
                    .redirectErrorStream(true)
                    .start();
            FriendlyLog.log("D", "Iadt", "Delete","Logcat buffer deleted by user");
        } catch (IOException e) {
            Log.e(Iadt.TAG, "LogcatBuffer showPlaceholder has failed :(");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTraceString = sw.toString();
            Log.e(Iadt.TAG, stackTraceString);
        }
    }

    public Logcat buildCrashReport(long crashId){
        String raw = getCrashReport();
        String filePath = DevToolsFiles.storeCrashLog(crashId, raw);

        Logcat logcat = new Logcat();
        logcat.setDate(new Date().getTime());
        logcat.setPath(filePath);
        logcat.setCrash(true);
        return logcat;
    }

    public String getCrashReport(){
        Shell exe = new Shell();
        String command = "logcat -d -t 1000 *:V";
        String output = exe.run(command);
        return  output;
    }
}
