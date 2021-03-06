/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.overlay.screens.network;

import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummaryDao;

//#ifdef ANDROIDX
//@import androidx.paging.DataSource;
//#else
import android.arch.paging.DataSource;
//#endif

public class NetDataSourceFactory extends DataSource.Factory {

    private long filter;
    private NetSummaryDao dao;

    public NetDataSourceFactory(NetSummaryDao dao, long sessionId) {
        this.dao = dao;
        this.filter = sessionId;
    }

    @Override
    public DataSource<Integer, NetSummary> create() {
        return dao.dataSourceBySessionId(filter).create();
    }

    public long getFilter(){
        return filter;
    }

    public void setFilter(long filter){
        this.filter = filter;
    }
}
