package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.view.overlay.screens.ScreenHelper;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;


public class AnrHelper extends ScreenHelper {


    @Override
    public String getReportPath() {
        return null;
    }

    @Override
    public String getReportContent() {
        return null;
    }

    public InfoReport parseToInfoGroup(Anr data){
        InfoGroup basic = new InfoGroup.Builder("Anr info")
                .add("AnrId", data.getUid())
                .add("Date", DateUtils.format(data.getDate()))
                .add("Message", data.getMessage())
                .add("Cause", data.getCause())
                .build();

        InfoGroup stacktrace = new InfoGroup.Builder("Stacktrace")
                .add("", "")
                .add("", data.getStacktrace())
                .build();

        return new InfoReport.Builder("")
                .add(basic)
                .add(stacktrace)
                .build();
    }
}
