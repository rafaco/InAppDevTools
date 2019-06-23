package es.rafaco.inappdevtools.library.view.overlay.screens;

import android.content.Context;

import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;

public abstract class OverlayScreenHelper {

    public final Context context;

    public OverlayScreenHelper() {
        this.context = Iadt.getAppContext();
    }

    public abstract String getReportPath();
    public List<String> getReportPaths() { return null; }
    public abstract String getReportContent();

}
