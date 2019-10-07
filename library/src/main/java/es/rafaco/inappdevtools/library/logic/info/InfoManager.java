package es.rafaco.inappdevtools.library.logic.info;

import android.content.Context;
import android.util.Log;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.logic.info.reporters.AbstractInfoReporter;

public class InfoManager {

    private final Context context;

    public InfoManager(Context context) {
        this.context = context;
    }

    public InfoReportData getReportData(int infoReportIndex) {
        InfoReport infoReport = getInfoReport(infoReportIndex);
        return getReportData(infoReport);
    }

    public InfoReportData getReportData(InfoReport report) {
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "Generating " + report.getTitle() + "InfoReport");
        AbstractInfoReporter helper = report.getReporter();
        return helper.getData();
    }

    public InfoReport getInfoReport(int infoReportIndex) {
        InfoReport[] infoReports = InfoReport.values();
        return infoReports[infoReportIndex];
    }
}
