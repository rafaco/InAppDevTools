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

package es.rafaco.inappdevtools.library.logic.documents;

import android.text.TextUtils;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfigFields;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.ToolsInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.reports.ReportFormatter;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class DocumentFormatter {

    private String constantHeader;

    private void buildConstantHeader() {
        AppInfoDocumentGenerator app = (AppInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.APP_INFO);
        BuildInfoDocumentGenerator build = (BuildInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.BUILD_INFO);
        DeviceInfoDocumentGenerator device = (DeviceInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.DEVICE_INFO);
        ToolsInfoDocumentGenerator tools = (ToolsInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.TOOLS_INFO);

        constantHeader = "Device: " + device.getOneLineOverview() + Humanizer.newLine();
        constantHeader += "App: " + app.getAppNameAndVersions() + Humanizer.newLine();

        //TODO: Calculate correctly for previous builds (Store builds on DB?)
        constantHeader += "Build: " + build.getShortOverview() + Humanizer.newLine();
        constantHeader += "Sources: " + build.getShortOverviewSources() + Humanizer.newLine();
        constantHeader += Humanizer.newLine();
        constantHeader += "Generated with InAppDevTools " + tools.getShortOverview()
                + Humanizer.newLine();
        constantHeader += "https://github.com/rafaco/InAppDevTools"
                + Humanizer.newLine();
    }

    public String formatDocument(String docName, String content) {
        if (TextUtils.isEmpty(constantHeader)){
            buildConstantHeader();
        }

        String result = "Document: %s" + Humanizer.newLine()
                + constantHeader;
        result = Humanizer.multiLineComment(result) + Humanizer.newLine();
        result += "%s" + Humanizer.newLine();

        return String.format(result, docName, content);
    }


    public String formatEmailBody(Report report) {
        if (TextUtils.isEmpty(constantHeader)){
            buildConstantHeader();
        }
        String defaultValue = "-";

        String result = "Report: " + ReportFormatter.getShortTitle(report);
        result += " at " + DateUtils.formatShortDate(report.getDate())
                + Humanizer.newLine();
        result += "Reason: " + report.getFormattedReason()
                + Humanizer.newLine();
        result += "Reporter: " + Humanizer.unavailable(report.getEmail(), defaultValue)
                + Humanizer.newLine();
        result += "Title: " + Humanizer.unavailable(report.getTitle(), defaultValue)
                + Humanizer.newLine();
        result += "Description: " + Humanizer.unavailable(report.getDescription(), defaultValue)
                + Humanizer.fullStop();
        result += constantHeader;
        
        return result;
    }

    public String formatEmailSubject(Report report){
        String formatter = "Report from %s: %s";
        AppInfoDocumentGenerator helper = (AppInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.APP_INFO);
        return String.format(formatter,
                helper.getFormattedAppLong(),
                Humanizer.unavailable(report.getTitle(), "No title"));
    }

    public String formatEmailTo(Report report) {
        return IadtController.get().getConfig().getString(BuildConfigFields.EMAIL);
    }

    public String formatEmailCc(Report report) {
        return "";
    }
}
