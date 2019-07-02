package es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog;

import android.arch.persistence.db.SimpleSQLiteQuery;

import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisItem;

public class FriendlyLogAnalysis {

    public FriendlyLogAnalysis() {

    }

    public List<AnalysisItem> getSeverityResult(){
        List<AnalysisItem> analysisItems = IadtController.getDatabase().friendlyDao().analiseSeverity();
        for (AnalysisItem item: analysisItems) {
            String name = item.getName();
            item.setName(FriendlyLog.convertCharToLongString(name) + " (" + name + ")");
        }
        return analysisItems;
    }

    public List<AnalysisItem> getCategoryResult(){
        List<AnalysisItem> analysisItems = IadtController.getDatabase().friendlyDao().analiseEventCategory();
        return analysisItems;
    }

    public List<AnalysisItem> getLogcatTagResult(){
        List<AnalysisItem> analysisItems = IadtController.getDatabase().friendlyDao().analiseLogcatTag();
        return analysisItems;
    }

    public List<AnalysisItem> getCurrentResult(FriendlyLogDataSourceFactory dataSourceFactory) {
        FriendlyLogQueryHelper helper = new FriendlyLogQueryHelper(dataSourceFactory);
        SimpleSQLiteQuery currentFilterSize = helper.getCurrentFilterSize();
        List<AnalysisItem> analysisItems = IadtController.getDatabase().friendlyDao().analiseWithQuery(currentFilterSize);
        return analysisItems;
    }
}
