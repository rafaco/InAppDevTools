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

//#ifdef ANDROIDX
//@import androidx.paging.DataSource;
//#else
import android.arch.paging.DataSource;
//#endif

import es.rafaco.inappdevtools.library.logic.log.filter.LogBackFilter;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;

public class LogDataSourceFactory extends DataSource.Factory {

    private LogBackFilter filter;
    private FriendlyDao dao;

    public LogDataSourceFactory(FriendlyDao dao, LogBackFilter filter) {
        this.dao = dao;
        this.filter = (filter != null) ? filter : new LogBackFilter();
    }

    @Override
    public DataSource<Integer, Friendly> create() {
        //Log.v(Iadt.TAG, "LogDataSource created");
        LogQueryHelper helper = new LogQueryHelper(getFilter());
        return dao.filterWithQuery(helper.getFilterQuery()).create();
    }

    public LogBackFilter getFilter(){
        return filter;
    }

    public void setFilter(LogBackFilter filter){
        this.filter = filter;
    }
}
