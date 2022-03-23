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

package org.inappdevtools.library.logic.log.datasource;

import java.util.List;

//#ifdef ANDROIDX
//@import androidx.sqlite.db.SimpleSQLiteQuery;
//#else
import android.arch.persistence.db.SimpleSQLiteQuery;
//#endif

import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.db.entities.AnalysisData;
import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.logic.log.filter.LogBackFilter;

public class LogAnalysisHelper {

    public LogAnalysisHelper() {
    }

    public List<AnalysisData> getSessionResult(){
        List<AnalysisData> analysisData = IadtDatabase.get().friendlyDao().analiseSession();
        return analysisData;
    }

    public List<AnalysisData> getSeverityResult(){
        List<AnalysisData> analysisData = IadtDatabase.get().friendlyDao().analiseSeverity();
        for (AnalysisData item: analysisData) {
            String name = item.getName();
            item.setName(FriendlyLog.convertCharToLongString(name) + " (" + name + ")");
        }
        return analysisData;
    }

    public List<AnalysisData> getCategoryResult(){
        List<AnalysisData> analysisData = IadtDatabase.get().friendlyDao().analiseEventCategory();
        return analysisData;
    }

    public List<AnalysisData> getLogcatTagResult(){
        List<AnalysisData> analysisData = IadtDatabase.get().friendlyDao().analiseLogcatTag();
        return analysisData;
    }

    public List<AnalysisData> getCurrentFilterOverview(LogBackFilter backFilter) {
        LogQueryHelper helper = new LogQueryHelper(backFilter);
        SimpleSQLiteQuery currentFilterSize = helper.getFilterSizeQuery();
        List<AnalysisData> analysisData = IadtDatabase.get().friendlyDao().analiseWithQuery(currentFilterSize);
        return analysisData;
    }

    public int getTotalLogSize() {
        return IadtDatabase.get().friendlyDao().count();
    }
}
