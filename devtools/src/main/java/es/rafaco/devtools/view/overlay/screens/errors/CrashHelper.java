package es.rafaco.devtools.view.overlay.screens.errors;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.errors.Logcat;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.logic.tools.ToolHelper;
import es.rafaco.devtools.utils.DateUtils;
import es.rafaco.devtools.utils.FileUtils;
import es.rafaco.devtools.view.overlay.screens.info.InfoCollection;
import es.rafaco.devtools.view.overlay.screens.info.InfoGroup;
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

        addCrashDetailFile(crash, filePaths);
        addLogcatFile(crash, filePaths);
        addScreenFile(crash, filePaths);

        return filePaths;
    }

    public InfoCollection parseToInfoGroup(Crash data){

        InfoGroup basic = new InfoGroup.Builder("Crash info")
                .add("CrashId", data.getUid())
                .add("Date", DateUtils.format(data.getDate()))
                .add("Exception", data.getException())
                .add("Message", data.getMessage())
                .build();

        InfoGroup thread = new InfoGroup.Builder("Thread info")
                .add("Thread ID", data.getThreadId())
                .add("Name", data.getThreadName())
                .add("Group", data.getThreadGroupName())
                .add("isMain", data.isMainThread())
                .build();

        InfoGroup links = new InfoGroup.Builder("Linked info")
                .add("LogcatId", String.valueOf(data.getLogcatId()))
                .add("ScreenId", String.valueOf(data.getScreenId()))
                .add("LogcatId", String.valueOf(data.getScreenId()))
                .build();

        InfoGroup stacktrace = new InfoGroup.Builder("Stacktrace")
                .add("", "")
                .add("", data.getStacktrace())
                .build();

        return new InfoCollection.Builder("")
                .add(basic)
                .add(thread)
                .add(links)
                .add(stacktrace)
                .build();
    }

    private void addCrashDetailFile(Crash crash, List<String> filePaths) {

        String report = parseToInfoGroup(crash).toString();

        String filePath = FileUtils.createFileWithContent("crash",
                "crash_info" + System.currentTimeMillis() + ".txt",
                report);

        if (!TextUtils.isEmpty(filePath)) {
            filePaths.add(filePath);
        }
    }

    private void addScreenFile(Crash crash, List<String> filePaths) {
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
