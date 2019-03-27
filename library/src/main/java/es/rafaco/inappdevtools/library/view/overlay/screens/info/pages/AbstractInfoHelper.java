package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;

import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;

public abstract class AbstractInfoHelper {

    protected Context context;

    public AbstractInfoHelper(Context context) {
        this.context = context;
    }

    public String getFullReport(){
        return getOverview() + "\n" + getInfoReport().toString();
    }

    public abstract String getOverview();
    public abstract InfoReport getInfoReport();
}
