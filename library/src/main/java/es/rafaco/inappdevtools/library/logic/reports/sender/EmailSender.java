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

package es.rafaco.inappdevtools.library.logic.reports.sender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;

//#ifdef ANDROIDX
//@import androidx.core.content.FileProvider;
//#else
import android.support.v4.content.FileProvider;
//#endif

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.documents.Document;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.reports.ReportType;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;

public class EmailSender {

    private Context context;
    private Report report;
    //TODO:
    private boolean isHtml = false;

    public void sendReport(Context context, Report report, String body, List<String> filePaths){
        this.context = context;
        this.report = report;

        sendEmailIntent(context,
                getEmailTo(), "",
                getEmailSubject(),
                body,
                //getEmailBody(isHtml),
                filePaths, isHtml);

    }

    private String getEmailTo() {
        return IadtController.get().getConfig().getString(BuildConfig.EMAIL);
    }

    private String getEmailSubject(){
        String formatter = "%s report: %s";
        String currentType = "";
        if(report.getReportType().equals(ReportType.SESSION)){
            currentType = "session";
        }else if (report.getReportType().equals(ReportType.CRASH)){
            currentType = "crash";
        }else if (report.getReportType().equals(ReportType.CUSTOM)){
            currentType = "full";
        }
        AppInfoDocumentGenerator helper = (AppInfoDocumentGenerator) DocumentRepository.getGenerator(Document.APP_INFO);
        return String.format(formatter,
                helper.getFormattedAppLong(),
                report.getTitle());
    }

    @NonNull
    private String getEmailBody(boolean isHtml) {
        String emailbody;
        String userTextPlaceholder = "Hi devs,\n\n";
        String jump = "\n";

        if(!isHtml){
            emailbody = new  StringBuilder()
                    //.append(getEmailSubject())
                    .append(userTextPlaceholder)
                    //.append(jump)
                    .toString();
        }else{
            emailbody = new  StringBuilder()
                    .append("<h2><b>Iadt report!</b></h2>")
                    .append("<p><b>Some Content</b></p>")
                    .append("<small><p>More content</p></small>")
                    .append("<a href = \"https://example.com\">https://example.com</a>")
                    .toString();
        }
        return emailbody;
    }


    private static void sendEmailIntent(Context context,
                                       String emailTo, String emailCC,
                                       String subject, String emailText,
                                       List<String> filePaths, boolean isHtml) {

        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType(isHtml ? "text/html" : "text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{emailCC});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (isHtml){
            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(emailText).toString());
        }else{
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        }

        if (filePaths != null && filePaths.size()>0){
            ArrayList<Uri> uris = new ArrayList<>();
            for (String filePath : filePaths) {
                File file = new File(filePath);

                //TODO:??
                if (!file.exists() || !file.canRead()) {
                    Iadt.showMessage("Attachment Error");
                    //return;
                }

                Uri contentUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String authority = FileProviderUtils.getAuthority(context);
                    contentUri = FileProvider.getUriForFile(context, authority, file);
                } else {
                    contentUri = Uri.fromFile(file);
                }
                uris.add(contentUri);
            }
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }

        Intent chooserIntent = Intent.createChooser(emailIntent, "Send mail...");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooserIntent);
    }
}
