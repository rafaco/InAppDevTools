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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.builds.BuildFilesRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentFormatter;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.files.utils.FileCreator;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;
import es.rafaco.inappdevtools.library.storage.files.utils.InternalFileReader;
import es.rafaco.inappdevtools.library.storage.files.utils.ZipUtils;

public class ReportSender {

    Context context;
    Report report;

    public void send(Report report) {
        this.context = IadtController.get().getContext();
        this.report = report;

        Map<String, List<String>> reportFilesMap = gatherReportFiles();
        List<String> filesToSend = compressIfNeeded(reportFilesMap);

        if (filesToSend == null) return;
        performSent(report, filesToSend);
        updateReportSent();
    }

    private void updateReportCompressed(String filePath) {
        report.setZipPath(filePath);
        IadtController.getDatabase().reportDao().update(report);
    }

    private void updateReportSent() {
        //TODO: use a real sent! user can cancel the intent chooser
        report.setDateSent(DateUtils.getLong());
        IadtController.getDatabase().reportDao().update(report);
    }


    //region [ GATHER FILES ]

    private Map<String, List<String>> gatherReportFiles() {
        Map<String, List<String>> reportMap = new HashMap<>();

        if (report.getReportType().equals(ReportType.SESSION)
            || report.getReportType().equals(ReportType.CRASH)){

            Session session = getSessionObject();

            addSessionFiles(reportMap, session);
            addBuildFiles(reportMap, session);
            addCrashDocument(reportMap, session);
            addInfoDocuments(reportMap, session);
            addScreenshots(reportMap, session);

            //TODO: network requests
            //TODO: Re-enable database dump
            //addDbDump(reportMap, session);
            //TODO: New shared preferences dump
        }

        return reportMap;
    }

    private Session getSessionObject() {
        //Fill sessionId if only crashId was provided (crash reports)
        if (report.getSessionId()<1 && report.getCrashId()>0){
            Crash crash = IadtController.get().getDatabase().crashDao()
                    .findById(report.getCrashId());
            report.setSessionId(crash.getSessionId());
        }

        Session session;
        if (report.getSessionId()>0) {
            session = IadtController.get().getDatabase().sessionDao()
                    .findById(report.getSessionId());
        }else {
            session = IadtController.get().getDatabase().sessionDao()
                    .getLast();
        }
        return session;
    }

    private void addSessionFiles(Map<String, List<String>> reportMap, Session session) {
        List<String> sessionFiles = new ArrayList<>();
        sessionFiles.add(DocumentRepository.getDocumentPath(DocumentType.SESSION, session));
        sessionFiles.add(DocumentRepository.getDocumentPath(DocumentType.SESSION_STEPS, session));
        sessionFiles.add(DocumentRepository.getDocumentPath(DocumentType.SESSION_LOGS, session));
        reportMap.put("session", sessionFiles);
    }

    private void addBuildFiles(Map<String, List<String>> reportMap, Session session) {
        List<String> buildFiles = new ArrayList<>();
        String buildSubfolder = BuildFilesRepository.getSubfolderForBuild(session.getUid());
        InternalFileReader reader = new InternalFileReader(context);
        File buildFolder = FileCreator.getSubfolder(buildSubfolder);
        buildFiles.addAll(reader.getFilesAtFolder(buildFolder.getAbsolutePath()));
        reportMap.put("build", buildFiles);
    }

    private void addCrashDocument(Map<String, List<String>> reportMap, Session session) {
        if (session.getCrashId()>0){
            List<String> crashFiles = new ArrayList<>();
            Crash crash = IadtController.get().getDatabase().crashDao()
                    .findById(session.getCrashId());
            //Add crash report only
            crashFiles.add(crash.getReportPath());
            reportMap.put("crash", crashFiles);

            // Old add all: filePaths.addAll(new CrashHelper().getReportPaths(crash));
            //TODO: crash screenshots
            //TODO: crash stacktrace with source snapshot! Include few source lines around
            // the traced one... Nice idea, buddy!!
        }
    }

    private void addScreenshots(Map<String, List<String>> reportMap, Session session) {
        List<String> screenFiles = new ArrayList<>();
        List<Screenshot> screens = IadtController.getDatabase().screenshotDao().getAllBySessionId(session.getUid());
        for (Screenshot screen : screens) {
            screenFiles.add(screen.getPath());
        }
        if (!screenFiles.isEmpty()){
            reportMap.put("screen", screenFiles);
        }
    }

    private void addInfoDocuments(Map<String, List<String>> reportMap, Session session) {
        List<String> infoFiles = IadtController.get().getSessionManager().getInfoDocuments(session.getUid());
        reportMap.put("info", infoFiles);
    }

    private void addDbDump(Map<String, List<String>> reportMap, Session session) {
        /*try {
            SupportSQLiteDatabase db = IadtController.get().getDatabase().getOpenHelper().getReadableDatabase();
            String name = IadtController.get().getDatabase().getOpenHelper().getDatabaseName();
            reportMap.put("database", SqliteExporter.export(name, db));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    //endregion



    //region [ COMPRESS ]

    private List<String> compressIfNeeded(Map<String, List<String>> reportFilesMap) {
        List<String> filesToSend;
        if (reportFilesMap.isEmpty()) {
            //TODO: No report files
            Iadt.showError("Unable to send an empty report");
            return null;
        }
        else if(reportFilesMap.size() == 1
                && ((List<String>)reportFilesMap.values().toArray()[0]).size() == 1){
            //TODO: Single file report
            filesToSend = new ArrayList<>();
            filesToSend.add(((List<String>)reportFilesMap.values().toArray()[0]).get(0));
        }
        else{
            String compressedFile = compressFiles(reportFilesMap);
            updateReportCompressed(compressedFile);
            filesToSend = Arrays.asList(compressedFile);
        }
        return filesToSend;
    }

    private String compressFiles(Map<String, List<String>> filePaths) {
        File outputFile = FileCreator.prepare("report",
                "report_" + report.getUid() + ".zip");

        ZipUtils zipManager = new ZipUtils();
        zipManager.zip(filePaths, outputFile.getAbsolutePath());

        return outputFile.getAbsolutePath();
    }

    //endregion

    //region [ SENT ]

    private void performSent(Report report, List<String> filesToSend) {
        if (report.getSenderTypeCode().equals(ReportSenderType.EMAIL.getCode())) {
            sendByEmail(filesToSend);
        }
        else if (report.getSenderTypeCode().equals(ReportSenderType.SEND.getCode())){
            sendExternally(filesToSend);
        }
        else if (report.getSenderTypeCode().equals(ReportSenderType.VIEW.getCode())){
            viewExternally(filesToSend);
        }
    }

    private void sendByEmail(List<String> filesPaths) {
        DocumentFormatter formatter = DocumentRepository.getFormatter();
        String body = formatter.formatEmailBody(report);
        String subject = formatter.formatEmailSubject(report);
        String to = formatter.formatEmailTo(report);
        String cc = formatter.formatEmailCc(report);

        FileProviderUtils.sendEmail(to, cc, subject, body, filesPaths);
    }

    private void viewExternally(List<String> filesToSend) {
        DocumentFormatter formatter = DocumentRepository.getFormatter();
        String body = formatter.formatEmailBody(report);
        FileProviderUtils.viewExternally(body, filesToSend.get(0));
    }

    private void sendExternally(List<String> filesToSend) {
        DocumentFormatter formatter = DocumentRepository.getFormatter();
        String body = formatter.formatEmailBody(report);
        FileProviderUtils.sendExternally(body, filesToSend.get(0));
    }

    //endregion
}
