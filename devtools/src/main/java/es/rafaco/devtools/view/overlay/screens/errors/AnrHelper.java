package es.rafaco.devtools.view.overlay.screens.errors;

import es.rafaco.devtools.storage.db.entities.Anr;
import es.rafaco.devtools.tools.ToolHelper;
import es.rafaco.devtools.logic.utils.DateUtils;
import es.rafaco.devtools.view.overlay.screens.info.InfoCollection;
import es.rafaco.devtools.view.overlay.screens.info.InfoGroup;


public class AnrHelper extends ToolHelper {


    @Override
    public String getReportPath() {
        return null;
    }

    @Override
    public String getReportContent() {
        return null;
    }

    public InfoCollection parseToInfoGroup(Anr data){
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

        return new InfoCollection.Builder("")
                .add(basic)
                .add(stacktrace)
                .build();
    }
}
