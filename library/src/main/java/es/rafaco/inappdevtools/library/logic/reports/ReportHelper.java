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
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.documents.DocumentFormatter;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.StopWatch;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.files.utils.FileCreator;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;
import es.rafaco.inappdevtools.library.storage.files.utils.ZipUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashHelper;

public class ReportHelper {

    public static final String EMAIL = "EMAIL";
    public static final String SEND = "SEND";
    public static final String VIEW = "VIEW";

    Context context;
    Report report;
    ReportType type;
    Object target;

    public void send(Report report) {
        this.context = IadtController.get().getContext();
        this.report = report;

        List<String> generatedFiles = generateFiles();

        List<String> filesToSend;
        if (generatedFiles.size() <= 1) {
            filesToSend = generatedFiles;
        }
        else{
            String compressedFile = compressFiles(generatedFiles);
            updateReportCompressed(compressedFile);
            filesToSend = Arrays.asList(compressedFile);
        }

        if (report.getSenderType().equals(EMAIL)) {
            sendByEmail(filesToSend);
        }
        else if (report.getSenderType().equals(SEND)){
            senExternally(filesToSend);
        }
        else if (report.getSenderType().equals(VIEW)){
            viewExternally(filesToSend);
        }
        updateReportSent();
    }

    private void updateReportCompressed(String filePath) {
        report.setZipPath(filePath);
        IadtController.getDatabase().reportDao().update(report);
    }

    private void updateReportSent() {

    }

    private String compressFiles(List<String> filePaths) {
        File outputFile = FileCreator.prepare("report",
                "report_" + report.getUid() + ".zip");
        ZipUtils zipManager = new ZipUtils();
        zipManager.zip(filePaths, outputFile.getAbsolutePath());

        return outputFile.getAbsolutePath();
    }

    private List<String> generateFiles() {
        List<String> filePaths = new ArrayList<>();

        if (report.getReportType().equals(ReportType.SESSION)
            || report.getReportType().equals(ReportType.CRASH)){

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

            //Session details
            filePaths.add(DocumentRepository.getDocumentPath(DocumentType.SESSION, session));
            filePaths.add(DocumentRepository.getDocumentPath(DocumentType.SESSION_STEPS, session));
            filePaths.add(DocumentRepository.getDocumentPath(DocumentType.SESSION_LOGS, session));

            //Crash??
            if (session.getCrashId()>0){
                Crash crash = IadtController.get().getDatabase().crashDao()
                        .findById(session.getCrashId());
                filePaths.add(crash.getReportPath());

                //TODO: crash screenshots
                //TODO: crash stacktrace with source snapshot! Include few source lines around
                // the traced one... Nice idea, buddy!!
            }

            filePaths.addAll(getInfoDocuments(session.getUid()));

            //TODO: screenshots

            //TODO: sources????
        }

        return filePaths;
    }

    //Generate Info overview and individual pages
    public static List<String> getInfoDocuments(long sessionId) {
        List<String> filePaths = new ArrayList<>();
        StopWatch watch = null;
        if (IadtController.get().isDebug()){
            watch = new StopWatch("GenerateInfoDocs");
            watch.step("Overview");
        }
        filePaths.add(DocumentRepository.getDocumentPath(DocumentType.INFO_OVERVIEW, sessionId));
        DocumentType[] values = DocumentType.getInfoValues();
        for (DocumentType documentType : values){
            if (IadtController.get().isDebug())
                watch.step(documentType.getName());
            filePaths.add(DocumentRepository.getDocumentPath(documentType, sessionId));
        }
        if (IadtController.get().isDebug())
            FriendlyLog.log(watch.finish());
        return filePaths;
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

    private void senExternally(List<String> filesToSend) {
        DocumentFormatter formatter = DocumentRepository.getFormatter();
        String body = formatter.formatEmailBody(report);
        FileProviderUtils.sendExternally(body, filesToSend.get(0));
    }








    //TODO: Remove following, is old!
    //
    public void send(ReportType type, Object target) {
        this.context = IadtController.get().getContext();
        this.type = type;
        this.target = target;

        List<String> filesPaths = getFilePaths();
        sendByEmail(filesPaths);
    }


    @NonNull
    private List<String> getFilePaths() {
        List<String> filePaths = new ArrayList<>();
        //filePaths.add(new InfoHelper().getReportPath());

        if(type.equals(ReportType.SESSION)){

            //filePaths.add(new LogcatUtils().getReportPath());

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
