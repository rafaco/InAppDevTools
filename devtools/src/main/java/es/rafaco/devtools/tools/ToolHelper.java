package es.rafaco.devtools.tools;

import android.content.Context;

import java.util.List;

import es.rafaco.devtools.DevTools;

public abstract class ToolHelper {

    public final Context context;

    public ToolHelper() {
        this.context = DevTools.getAppContext();
    }

    public abstract String getReportPath();
    public List<String> getReportPaths() { return null; }
    public abstract String getReportContent();

}
