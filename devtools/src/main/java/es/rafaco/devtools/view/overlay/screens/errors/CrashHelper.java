package es.rafaco.devtools.view.overlay.screens.errors;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.errors.Logcat;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.logic.tools.ToolHelper;
import es.rafaco.devtools.utils.DateUtils;
import es.rafaco.devtools.utils.FileUtils;
import es.rafaco.devtools.view.overlay.screens.log.LogHelper;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreenHelper;

public class CrashHelper extends ToolHelper{

    @Override
    public String getReportPath() {
        return null;
    }

    @Override
    public String getReportContent() {
        return null;
    }

    public List<String> buildReport(Crash crash) {
        List<String> filePaths = new ArrayList<>();

        addCrashInfo(crash, filePaths);
        addLogcatFile(crash, filePaths);
        addScreen(crash, filePaths);

        return filePaths;
    }

    private void addCrashInfo(Crash crash, List<String> filePaths) {

        String report = new  StringBuilder()
                .append("UID: " + crash.getUid())
                .append("Time: " + DateUtils.formatToDateAndTimeString(crash))
                .append("Exception: " + crash.getException())
                .append("Message: " + crash.getMessage())
                .append("Stacktrace: " + crash.getStacktrace())
                .toString();

        String filePath = FileUtils.createFileWithContent("crash",
                "crash_info" + System.currentTimeMillis() + ".txt",
                report);

        if (!TextUtils.isEmpty(filePath)) {
            filePaths.add(filePath);
        }
    }

    private void addScreen(Crash crash, List<String> filePaths) {
        String filePath = "";
        if (crash.getRawScreen() != null){
            filePath = new ScreenHelper().storeByteArray(crash);
        }
        else{
            Screen screen = DevTools.getDatabase().screenDao().findById(crash.getScreenId());
            filePath = screen.getPath();
        }

        if (!TextUtils.isEmpty(filePath)) {
            filePaths.add(filePath);
        }
    }

    private void addLogcatFile(Crash crash, List<String> filePaths) {
        String filePath = "";
        if (crash.getRawLogcat() != null){
            filePath = new LogHelper().undoRawReport(crash);
        }
        else{
            Logcat logcat = DevTools.getDatabase().logcatDao().findById(crash.getLogcatId());
            filePath = logcat.getPath();
        }

        if (!TextUtils.isEmpty(filePath)) {
            filePaths.add(filePath);
        }
    }
}
