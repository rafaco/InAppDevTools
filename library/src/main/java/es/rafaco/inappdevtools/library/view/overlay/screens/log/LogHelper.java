package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Logcat;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.tools.ToolHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.commands.ShellExecuter;

public class LogHelper extends ToolHelper{

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
        String[] fullCommand = new String[]{LogReaderTask.BASH_PATH, LogReaderTask.BASH_ARGS, "logcat -c"};
        Process process = null;
        try {
            process = new ProcessBuilder()
                    .command(fullCommand)
                    .redirectErrorStream(true)
                    .start();
            FriendlyLog.log("D", "DevTools", "Delete","Logcat buffer deleted by user");
        } catch (IOException e) {
            Log.e(DevTools.TAG, "LogcatBuffer showPlaceholder has failed :(");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTraceString = sw.toString();
            Log.e(DevTools.TAG, stackTraceString);
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
        ShellExecuter exe = new ShellExecuter();
        String command = "logcat -d -t 1000 *:V";
        String output = exe.Executer(command);
        return  output;
    }
}
