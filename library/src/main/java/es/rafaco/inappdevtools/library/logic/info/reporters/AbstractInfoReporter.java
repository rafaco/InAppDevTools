package es.rafaco.inappdevtools.library.logic.info.reporters;

import android.content.Context;

import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;

public abstract class AbstractInfoReporter {

    protected Context context;
    private final InfoReport report;

    public AbstractInfoReporter(Context context, InfoReport report) {
        this.context = context;
        this.report = report;
    }

    protected InfoReport getReport() {
        return report;
    }
    public abstract InfoReportData getData();
    public abstract String getOverview();
}
