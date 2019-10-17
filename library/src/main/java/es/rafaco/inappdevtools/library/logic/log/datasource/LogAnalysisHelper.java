/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
