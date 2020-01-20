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

package es.rafaco.inappdevtools.library.logic.reports;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.documents.Document;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.ToolsInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.reports.sender.EmailSender;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.files.utils.FileCreator;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatHelper;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ReportHelper {

    Context context;
    Report report;
    ReportType type;
    Object target;
    private String reportHeader;

    public void start(Report report) {
        this.context = IadtController.get().getContext();
        this.report = report;

        //Todo: zip files?
        List<String> filesPaths = generateFiles();
        String body = formatEmailBody();
        sendByEmail(body, filesPaths);
    }

    private List<String> generateFiles() {
        List<String> filePaths = new ArrayList<>();
        if (report.getSessionId()>0){

            Session session = IadtController.get().getDatabase().sessionDao()
                    .findById(report.getSessionId());

            //Session details
            filePaths.add(DocumentRepository.saveDocument(Document.SESSION, session));
            filePaths.add(DocumentRepository.saveDocument(Document.SESSION_STEPS, session));
            filePaths.add(DocumentRepository.saveDocument(Document.SESSION_LOGS, session));

            //Info pages
            filePaths.add(DocumentRepository.saveDocument(Document.INFO_OVERVIEW, session.getUid()));
            Document[] values = Document.getInfoValues();
            for (Document document : values){
                filePaths.add(DocumentRepository.saveDocument(document, session.getUid()));
            }


            //TODO: crashes

            //TODO: screenshots

            //TODO: sources????
        }

        return filePaths;
    }

    private void addDocument(List<String> filePaths, String subFolder, String fileName,
                             String documentName, String content) {
        String filePath = FileCreator.withContent(subFolder,
                fileName,
                formatDocument(documentName, content));
        filePaths.add(filePath);
    }

    private void buildReportHeader() {
        AppInfoDocumentGenerator app = (AppInfoDocumentGenerator) DocumentRepository.getGenerator(Document.APP_INFO);
        BuildInfoDocumentGenerator build = (BuildInfoDocumentGenerator) DocumentRepository.getGenerator(Document.BUILD_INFO);
        DeviceInfoDocumentGenerator device = (DeviceInfoDocumentGenerator) DocumentRepository.getGenerator(Document.DEVICE_INFO);
        ToolsInfoDocumentGenerator tools = (ToolsInfoDocumentGenerator) DocumentRepository.getGenerator(Document.TOOLS_INFO);

        reportHeader  = "App: " + app.getAppNameAndVersions() + Humanizer.newLine();
        reportHeader += "Build: " + build.getShortOverview() + Humanizer.newLine();
        reportHeader += "Branch: " + build.getShortOverviewSources() + Humanizer.newLine();
        reportHeader += "Changes: " + build.getShortOverviewChanges() + Humanizer.newLine();
        reportHeader += "Device: " + device.getOneLineOverview() + Humanizer.newLine();
        reportHeader += Humanizer.newLine();
        reportHeader += "This file has been generated with InAppDevTools " + tools.getShortOverview()
                + Humanizer.newLine();
        reportHeader += "Find out more at https://github.com/rafaco/InAppDevTools."
                + Humanizer.newLine();
    }

    private String formatDocument(String docName, String content) {
        if (TextUtils.isEmpty(reportHeader)){
            buildReportHeader();
        }

        String result = "Document: %s" + Humanizer.newLine()
                + reportHeader;
        result = Humanizer.multiLineComment(result) + Humanizer.newLine();
        result += "%s" + Humanizer.newLine();

        return String.format(result, docName, content);
    }

    private String formatEmailBody() {
        if (TextUtils.isEmpty(reportHeader)){
            buildReportHeader();
        }

        String result = "Report: " + getShortReportDescription(report);
        result += " at " + DateUtils.formatShortDate(report.getDate()) + Humanizer.newLine();
        result += "Reason: " + report.getFormattedReason() + Humanizer.fullStop();
        result += "Reporter: " + Humanizer.unavailable(report.getEmail()) + Humanizer.newLine();
        result += "Description:" + Humanizer.newLine() + Humanizer.unavailable(report.getTitle()) + Humanizer.fullStop();
        result += "Details:" + Humanizer.newLine() + Humanizer.unavailable(report.getDescription()) + Humanizer.fullStop();
        result += reportHeader + Humanizer.newLine();
        result += Humanizer.newLine();
        return result;
    }

    public static String getShortReportDescription(Report report) {
        String result = Humanizer.toCapitalCase(report.getReportType().name());
        if (report.getReportType() == ReportType.CRASH
                && report.getCrashId()>0){
            result += " " + report.getCrashId();
        }
        else if (report.getReportType() == ReportType.SESSION
                && report.getSessionId()>0){
            result += " " + report.getSessionId();
        }
        else if (report.getReportType() == ReportType.CUSTOM){
            //TODO
        }
        else if (report.getReportType() == ReportType.ISSUE){
            //TODO
        }
        return result;
    }

    public static String getLongReportDescription(Report report) {
        String result = getShortReportDescription(report);
        result += " report";
        return result;
    }

    private void sendByEmail(String body, List<String> filesPaths) {
        new EmailSender().sendReport(context, report, body, filesPaths);
    }





    public void start(ReportType type, Object target) {
        this.context = IadtController.get().getContext();
        this.type = type;
        this.target = target;

        List<String> filesPaths = getFilePaths();
        sendByEmail("Hi devs.... old report method", filesPaths);
    }


    @NonNull
    private List<String> getFilePaths() {
        List<String> filePaths = new ArrayList<>();
        //filePaths.add(new InfoHelper().getReportPath());

        if(type.equals(ReportType.SESSION)){

            filePaths.add(new LogcatHelper().getReportPath());

            //Include only the last one
            //filePaths.add(new ScreenshotHelper().saveReport());

            try{
                ArrayList<Uri> screens = (ArrayList<Uri>)target;
                if (screens != null && screens.size()>0){
                    for (Uri screen : screens) {
                        filePaths.add(screen.getPath());
                    }
                }
            }catch (Exception e){
                Log.e(Iadt.TAG, "Exception parsing screens for report");
            }

            /* TODO: Re-enable db dump
            try {
                SupportSQLiteDatabase db = IadtController.get().getDatabase().getOpenHelper().getReadableDatabase();
                String name = IadtController.get().getDatabase().getOpenHelper().getDatabaseName();
                filePaths.add(SqliteExporter.export(name, db));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        else if (type.equals(ReportType.CRASH)){
            Crash crash = (Crash) target;
            filePaths.addAll(new CrashHelper().getReportPaths(crash));
        }
        return filePaths;
    }
}
