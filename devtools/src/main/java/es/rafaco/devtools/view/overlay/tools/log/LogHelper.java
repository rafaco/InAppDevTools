package es.rafaco.devtools.view.overlay.tools.log;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import es.rafaco.devtools.DevTools;

public class LogHelper {

    private Context context;

    public LogHelper(Context context) {
        this.context = context;
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
