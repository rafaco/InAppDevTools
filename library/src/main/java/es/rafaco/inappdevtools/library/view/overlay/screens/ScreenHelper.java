package es.rafaco.inappdevtools.library.view.overlay.screens;

import android.content.Context;

import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;

public abstract class ScreenHelper {

    public final Context context;

    public ScreenHelper() {
        this.context = IadtController.get().getContext();
    }

    public abstract String getReportPath();
    public List<String> getReportPaths() { return null; }
    public abstract String getReportContent();

}
