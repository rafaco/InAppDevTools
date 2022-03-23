/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.documents.generators.detail;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.db.entities.NetContent;
import org.inappdevtools.library.storage.db.entities.NetSummary;

import java.util.List;

import org.inappdevtools.library.R;
import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.data.DocumentData;
import org.inappdevtools.library.logic.documents.data.DocumentSectionData;
import org.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import org.inappdevtools.library.logic.utils.DateUtils;
import org.inappdevtools.library.view.overlay.screens.network.NetFormatter;
import org.inappdevtools.library.view.utils.Humanizer;
import tech.linjiang.pandora.util.FormatUtil;

public class NetItemDocumentGenerator extends AbstractDocumentGenerator {

    private final NetSummary netSummary;
    private final NetContent netContent;
    private final NetFormatter formatter;

    public NetItemDocumentGenerator(Context context, DocumentType report, NetSummary param) {
        super(context, report, param);
        this.netSummary = param;
        this.netContent = IadtDatabase.get().netContentDao()
                .findByCompositeId(netSummary.sessionId, netSummary.pandoraId);
        this.formatter = new NetFormatter(netSummary);
    }

    @Override
    public String getTitle() {
        return "Network " + netSummary.uid;
    }

    @Override
    public String getSubfolder() {
        return "network/" + netSummary.uid;
    }

    @Override
    public String getFilename() {
        return "summary_" + netSummary.uid + "_overview" + ".txt";
    }

    @Override
    public String getOverview() {
        return netSummary.url + Humanizer.newLine()
            + netSummary.host + Humanizer.newLine()
                + getStatusLine();
    }

    private String getStatusLine() {
        String result = formatter.getStatusString();
        if (netSummary.status != NetSummary.Status.REQUESTING){
            result += " in " + formatter.getDurationString();
        }
        return result;
    }

    @Override
    public DocumentData getData() {
        DocumentData.Builder data = new DocumentData.Builder(getDocumentType())
                .setTitle(getTitle())
                .setOverview(getOverview())
                .add(getGeneralInfo())
                .add(getRequestHeader())
                .add(getRequestBody());

        if (netSummary.status == NetSummary.Status.ERROR){
            data.add(getError());
        }
        else if (netSummary.status == NetSummary.Status.COMPLETE){
            data.add(getResponseHeader());
            data.add(getResponseBody());
        }
        else{
            data.add(getWaitingSection());
        }
        return data.build();
    }

    public DocumentSectionData getGeneralInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("General")
                .setIcon(R.string.gmd_info)
                .add("Host", netSummary.host)
                .add("Url", netSummary.url)
                .add("Query", netSummary.query)
                .add("")
                .add("Method", netSummary.method)
                .add("Protocol", netSummary.protocol)
                .add("Ssl", String.valueOf(netSummary.ssl))
                .add("")
                .add("Request Time", DateUtils.formatFull(netSummary.start_time))
                .add("Request Content-Type", netSummary.request_content_type)
                .add("Request Size", formatter.getRequestSize())
                .add("")
                .add("Response Code", netSummary.code)
                .add("Response Time", DateUtils.formatFull(netSummary.end_time))
                .add("Response Content-Type", netSummary.response_content_type)
                .add("Response Size", formatter.getResponseSize())
                .add("")
                .add("Total Duration", formatter.getDurationString())
                .add("Status", formatter.getStatusString())

                .build();
        return group;
    }

    public DocumentSectionData getRequestHeader() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Request Header")
                .setIcon(R.string.gmd_cloud_upload);

        List<Pair<String, String>> pairs = FormatUtil.parseHeaders(netSummary.request_header);
        if (pairs != null && pairs.size() > 0) {
            group.setOverview(pairs.size() + " items");
            for (Pair<String, String> pair : pairs) {
                group.add(pair.first, pair.second);
            }
        }
        else{
            group.setOverview("Empty");
            group.setExpandable(false);
        }

        return group.build();
    }

    public DocumentSectionData getRequestBody() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Request Body")
                .setIcon(R.string.gmd_receipt);

        if (netContent != null && !TextUtils.isEmpty(netContent.requestBody)){
            group.setOverview(formatter.getRequestSize());
            group.add(netContent.requestBody);
        }else{
            group.setOverview("Empty");
            group.setExpandable(false);
        }
        return group.build();
    }

    public DocumentSectionData getResponseHeader() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Response Header")
                .setIcon(R.string.gmd_cloud_download);

        List<Pair<String, String>> pairs = FormatUtil.parseHeaders(netSummary.response_header);
        if (pairs != null && pairs.size() > 0) {
            group.setOverview(pairs.size() + " items");
            for (Pair<String, String> pair : pairs) {
                group.add(pair.first, pair.second);
            }
        }
        else{
            group.setOverview("Empty");
            group.setExpandable(false);
        }

        return group.build();
    }

    public DocumentSectionData getResponseBody() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Response Body")
                .setIcon(R.string.gmd_receipt);

        if (netContent != null && !TextUtils.isEmpty(netContent.responseBody)){
            group.setOverview(formatter.getResponseSize());
            group.add(netContent.responseBody);
        }else{
            group.setOverview(netSummary.status == NetSummary.Status.REQUESTING ? "Waiting" : "Empty");
            group.setExpandable(false);
        }
        return group.build();
    }

    public DocumentSectionData getError() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Error")
                .setIcon(R.string.gmd_receipt)
                .add(netContent.responseBody);
        return group.build();
    }

    private DocumentSectionData getWaitingSection() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Waiting response...")
                .setIcon(R.string.gmd_hourglass_empty)
                .setExpandable(false);
        return group.build();
    }
}
