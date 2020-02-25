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

package es.rafaco.inappdevtools.library.logic.integrations;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.entities.NetContent;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import tech.linjiang.pandora.cache.Content;
import tech.linjiang.pandora.cache.Summary;
import tech.linjiang.pandora.network.NetStateListener;
import tech.linjiang.pandora.util.SimpleTask;

public class PandoraListener implements NetStateListener {
    
    private final long sessionId;

    public PandoraListener() {
        sessionId = IadtController.get().getSessionManager().getCurrentUid();
    }

    @Override
    public void onRequestStart(long id) {
        refreshSingleData(true, id);
    }

    @Override
    public void onRequestEnd(long id) {
        refreshSingleData(false, id);
    }

    private void refreshSingleData(final boolean isNew, final long pandoraId) {
        new SimpleTask<>(new SimpleTask.Callback<Void, Summary>() {
            @Override
            public Summary doInBackground(Void[] params) {
                return Summary.query(pandoraId);
            }

            @Override
            public void onPostExecute(Summary pandoraSummary) {
                if (pandoraSummary == null) {
                    return;
                }
                Content pandoraContent = Content.query(pandoraId);
                NetSummary iadtSummary = null;
                NetContent iadtContent = null;

                if (isNew) {
                    iadtSummary = migrateSummary(null, pandoraSummary);
                    iadtContent = migrateContent(null, pandoraContent);

                    long summaryId = IadtController.getDatabase().netSummaryDao().insert(iadtSummary);
                    IadtController.getDatabase().netContentDao().insert(iadtContent);

                    iadtSummary.uid = summaryId;
                    FriendlyLog.logNetStart(iadtSummary);
                }
                else{
                    iadtSummary = IadtController.getDatabase().netSummaryDao()
                            .findByCompositeId(sessionId, pandoraId);
                    iadtContent = IadtController.getDatabase().netContentDao()
                            .findByCompositeId(sessionId, pandoraId);

                    iadtSummary = migrateSummary(iadtSummary, pandoraSummary);
                    iadtContent = migrateContent(iadtContent, pandoraContent);
                    iadtSummary = updateSummarySizes(iadtSummary, iadtContent);

                    IadtController.getDatabase().netSummaryDao().update(iadtSummary);
                    IadtController.getDatabase().netContentDao().update(iadtContent);

                    FriendlyLog.logNetEnd(iadtSummary);
                }
            }
        }).execute();
    }

    private NetSummary migrateSummary(NetSummary iadtData, Summary pandoraData){
        if (iadtData==null)
            iadtData = new NetSummary();

        iadtData.sessionId = sessionId;
        iadtData.pandoraId = pandoraData.id;
        iadtData.status = pandoraData.status;
        iadtData.code = pandoraData.code;
        iadtData.url = pandoraData.url;
        iadtData.query = pandoraData.query;
        iadtData.host = pandoraData.host;
        iadtData.method = pandoraData.method;
        iadtData.protocol = pandoraData.protocol;
        iadtData.ssl = pandoraData.ssl;
        iadtData.start_time = pandoraData.start_time;
        iadtData.end_time = pandoraData.end_time;
        iadtData.request_content_type = pandoraData.request_content_type;
        iadtData.response_content_type = pandoraData.response_content_type;
        iadtData.request_size = pandoraData.request_size;
        iadtData.response_size = pandoraData.response_size;
        iadtData.request_header = pandoraData.requestHeader;
        iadtData.response_header = pandoraData.responseHeader;

        return iadtData;
    }

    private NetContent migrateContent(NetContent iadtData, Content pandoraData){
        if (iadtData==null)
            iadtData = new NetContent();

        iadtData.sessionId = sessionId;
        iadtData.pandoraId = pandoraData.id;
        iadtData.requestBody = pandoraData.requestBody;
        iadtData.responseBody = pandoraData.responseBody;

        return iadtData;
    }

    private NetSummary updateSummarySizes(NetSummary summary, NetContent content) {
        //Original Pandora size calculator fails when data can not be parsed into JSON
        if (content!=null){
            summary.request_size = Humanizer.countBytes(content.requestBody);
            summary.response_size = Humanizer.countBytes(content.responseBody);
        }
        return summary;
    }
}
