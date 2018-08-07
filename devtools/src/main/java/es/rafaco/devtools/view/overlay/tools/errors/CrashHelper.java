package es.rafaco.devtools.view.overlay.tools.errors;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.errors.Logcat;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.utils.DateUtils;
import es.rafaco.devtools.utils.FileUtils;

public class CrashHelper {
    private final Context context;

    public CrashHelper(Context context) {
        this.context = context;
    }

    public List<String> buildReport(Crash crash) {
        List<String> filePaths = new ArrayList<>();

        String crashFile = buildCrashReport(crash);
        if (TextUtils.isEmpty(crashFile)) {
            filePaths.add(crashFile);
        }

        Logcat logcat = DevTools.getDatabase().logcatDao().findById(crash.getLogcatId());
        if (logcat!= null)
            filePaths.add(logcat.getPath());

        Screen screen = DevTools.getDatabase().screenDao().findById(crash.getScreenId());
        if (screen!= null)
            filePaths.add(screen.getPath());

        return filePaths;
    }

    public String buildCrashReport(Crash crash){

        String report = new  StringBuilder()
                .append("UID: " + crash.getUid())
                .append("Time: " + DateUtils.formatToDateAndTimeString(crash))
                .append("Exception: " + crash.getException())
                .append("Message: " + crash.getMessage())
                .append("Stacktrace: " + crash.getStacktrace())
                .toString();

        String path = FileUtils.createFileWithContent("crash",
                "crash_" + System.currentTimeMillis() + ".txt",
                report);

        return path;
    }
}
