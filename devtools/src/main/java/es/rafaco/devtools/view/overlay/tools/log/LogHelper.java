package es.rafaco.devtools.view.overlay.tools.log;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import es.rafaco.devtools.DevTools;

public class LogHelper {

    private Context context;

    public LogHelper(Context context) {
        this.context = context;
    }

    public String saveLogcatToFile(){

        if(isExternalStorageWritable()){

            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/DevTools");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logcat_" + System.currentTimeMillis() + ".txt");

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -d -f " + logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return logFile.getPath();

        } else if(isExternalStorageReadable() ){
            // only readable
        } else{
            // not accessible
        }

        return null;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
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
}
