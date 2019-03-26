package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Logcat;
import es.rafaco.inappdevtools.library.storage.db.entities.Screen;
import es.rafaco.inappdevtools.library.tools.ToolHelper;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoReport;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoGroup;

public class CrashHelper extends ToolHelper{

    public static final String CAUSED_BY = "Caused by:";

    @Override
    public String getReportPath() {
        return null;
    }

    @Override
    public String getReportContent() {
        return null;
    }

    public InfoReport parseToInfoGroup(Crash data){

        InfoGroup status = new InfoGroup.Builder("App status")
                //.add("When", DateUtils.getElapsedTime(data.getDate())) //TODO: no an app status
                .add("AppStatus", data.isForeground() ? "Foreground" : "Background")
                .add("LastActivity", data.getLastActivity())
                .build();

        InfoGroup basic = new InfoGroup.Builder("Crash info")
                .add("CrashId", data.getUid())
                .add("Date", DateUtils.format(data.getDate()))
                .add("AppStatus", data.isForeground() ? "Foreground" : "Background")
                .add("LastActivity", data.getLastActivity())
                .add("Exception", data.getException())
                .add("Message", data.getMessage())
                .add("ExceptionAt", data.getExceptionAt())
                .add("CauseException", data.getCauseException())
                .add("CauseMessage", data.getCauseMessage())
                .add("CauseAt", data.getCauseExceptionAt())
                .build();

        InfoGroup thread = new InfoGroup.Builder("Thread info")
                .add("Thread ID", data.getThreadId())
                .add("Name", data.getThreadName())
                .add("Group", data.getThreadGroupName())
                .add("isMain", data.isMainThread())
                .build();

        InfoGroup links = new InfoGroup.Builder("Linked info")
                .add("ReportPath", String.valueOf(data.getReportPath()))
                .add("LogcatId", String.valueOf(data.getLogcatId()))
                .add("ScreenId", String.valueOf(data.getScreenId()))
                .build();

        InfoGroup stacktrace = new InfoGroup.Builder("Stacktrace")
                .add("", data.getStacktrace())
                .build();

        return new InfoReport.Builder("")
                .add(status)
                .add(basic)
                .add(thread)
                .add(links)
                .add(stacktrace)
                .build();
    }

    public List<String> getReportPaths(final Crash crash) {
        List<String> filePaths = new ArrayList<>();
        addCrashDetailFile(crash, filePaths);
        addLogcatFile(crash, filePaths);
        addScreenFile(crash, filePaths);

        return filePaths;
    }

    private void addCrashDetailFile(Crash crash, List<String> filePaths) {
        if (!TextUtils.isEmpty(crash.getReportPath())) {
            filePaths.add(crash.getReportPath());
        }
    }

    private void addScreenFile(Crash crash, List<String> filePaths) {
        Screen screen = DevTools.getDatabase().screenDao().findById(crash.getScreenId());
        String filePath = screen.getPath();

        if (!TextUtils.isEmpty(filePath)) {
            filePaths.add(filePath);
        }
    }

    private void addLogcatFile(Crash crash, List<String> filePaths) {
        Logcat logcat = DevTools.getDatabase().logcatDao().findById(crash.getLogcatId());
        String filePath = logcat.getPath();

        if (!TextUtils.isEmpty(filePath)) {
            filePaths.add(filePath);
        }
    }

    //region [ TEXT FORMATTERS ]

    public String getFormattedAt(Crash data) {
        String[] split = data.getStacktrace().split("\n\t");
        return formatAt(split[1]);
    }

    public String getCaused(Crash data) {
        String[] split = data.getStacktrace().split("\n\t");
        for (int i=0; i<split.length; i++){
            String line = split[i];
            if (line.contains(CAUSED_BY)){
                return line.substring(line.indexOf(CAUSED_BY));
            }
        }
        return null;
    }

    public String getCausedAt(Crash data) {
        String[] split = data.getStacktrace().split("\n\t");
        for (int i=0; i<split.length; i++){
            String line = split[i];
            if (line.contains(CAUSED_BY)){
                return formatAt(split[i+1]);
            }
        }
        return null;
    }

    private String formatAt(String text){
        return text.replace("(", " (");
    }

    //endregion
}
