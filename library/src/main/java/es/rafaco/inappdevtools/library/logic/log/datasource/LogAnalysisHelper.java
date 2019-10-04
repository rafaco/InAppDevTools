package es.rafaco.inappdevtools.library.logic.log.datasource;

import java.util.List;

//#ifdef ANDROIDX
//@import androidx.sqlite.db.SimpleSQLiteQuery;
//#else
import android.arch.persistence.db.SimpleSQLiteQuery;
//#endif

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.log.filter.LogBackFilter;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisItem;

public class LogAnalysisHelper {

    public LogAnalysisHelper() {
    }

    public List<AnalysisItem> getSessionResult(){
        List<AnalysisItem> analysisItems = IadtController.getDatabase().friendlyDao().analiseSession();
        return analysisItems;
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

    public List<AnalysisItem> getCurrentFilterOverview(LogBackFilter backFilter) {
        LogQueryHelper helper = new LogQueryHelper(backFilter);
        SimpleSQLiteQuery currentFilterSize = helper.getFilterSizeQuery();
        List<AnalysisItem> analysisItems = IadtController.getDatabase().friendlyDao().analiseWithQuery(currentFilterSize);
        return analysisItems;
    }

    public int getTotalLogSize() {
        return IadtController.getDatabase().friendlyDao().count();
    }
}
