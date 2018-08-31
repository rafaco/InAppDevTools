package es.rafaco.devtools.view.overlay.screens.log;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.errors.Logcat;
import es.rafaco.devtools.logic.tools.ToolHelper;
import es.rafaco.devtools.utils.FileUtils;
import es.rafaco.devtools.view.overlay.screens.commands.ShellExecuter;

public class LogHelper extends ToolHelper{

    @Override
    public String getReportPath() {
        if(FileUtils.isExternalStorageWritable()){
            File file = FileUtils.createNewFile("log",
                    "logcat_" + System.currentTimeMillis() + ".txt");
            try {
                Process process = Runtime.getRuntime().exec("logcat -d -f " + file);
                process.waitFor();
                return file.getPath();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if(FileUtils.isExternalStorageReadable() ){
            // only readable
        } else{
            // not accessible
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
            Log.i(DevTools.TAG, "LogcatBuffer cleared");
        } catch (IOException e) {
            Log.e(DevTools.TAG, "LogcatBuffer clear has failed :(");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTraceString = sw.toString();
            Log.e(DevTools.TAG, stackTraceString);
            if (process != null &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    process.isAlive()) {
                process.destroy();
            }
        }
    }

    public String buildRawReport(){
        ShellExecuter exe = new ShellExecuter();
        String command = "logcat -d -t 1000 *:V";
        String output = exe.Executer(command);
        return  output;
    }

    public String undoRawReport(Crash crash){
        String rawLog = crash.getRawLogcat();

        if (rawLog == null){
            return "";
        }

        try {
            File file = FileUtils.createNewFile("crash",
                    "crash_logcat_" + crash.getDate() + ".txt");

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(rawLog.getBytes());
            outputStream.close();

            //TODO: update MediaStore
            String filePath = file.getAbsolutePath();

            Logcat logcat = new Logcat();
            logcat.setDate(crash.getDate());
            logcat.setPath(filePath);
            long logcatId = DevTools.getDatabase().logcatDao().insert(logcat);

            crash.setRawLogcat(null);
            crash.setLogcatId(logcatId);
            DevTools.getDatabase().crashDao().update(crash);

            return filePath;

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
            return "";
        }
    }

    /*public long buildCrashReport(){

        if(FileUtils.isExternalStorageWritable()){
            long logcatId = -1;
            File file = FileUtils.createNewFile("crash",
                    "crash_" + System.currentTimeMillis() + ".txt");
            try {
                Process process = Runtime.getRuntime().exec("logcat -d -t 100 -f " + file);
                process.waitFor();

                Logcat logcat = new Logcat();
                logcat.setDate(new Date().getTime());
                logcat.setPath(file.getPath());
                logcatId = DevTools.getDatabase().logcatDao().insert(logcat);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return logcatId;

        } else if(FileUtils.isExternalStorageReadable() ){
            // only readable
        } else{
            // not accessible
        }

        return -1;
    }*/
}
