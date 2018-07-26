package es.rafaco.devtools.view.overlay.tools.log;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.errors.Logcat;
import es.rafaco.devtools.utils.FileUtils;

public class LogHelper {

    private Context context;

    public LogHelper(Context context) {
        this.context = context;
    }

    public String buildReport(){

        if(FileUtils.isExternalStorageWritable()){
            File file = FileUtils.createNewFile("log", "logcat_" + System.currentTimeMillis() + ".txt");
            try {
                Process process = Runtime.getRuntime().exec("logcat -d -f " + file);
                process.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return file.getPath();

        } else if(FileUtils.isExternalStorageReadable() ){
            // only readable
        } else{
            // not accessible
        }

        return null;
    }

    public long buildCrashReport(){

        if(FileUtils.isExternalStorageWritable()){
            long logcatId = -1;
            File file = FileUtils.createNewFile("crash", "crash_" + System.currentTimeMillis() + ".txt");
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
