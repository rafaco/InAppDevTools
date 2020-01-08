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

import android.net.Uri;
import android.os.Build;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.tiagohm.Language;
import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.info.reporters.AppInfoReporter;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.view.overlay.screens.ScreenHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.EmailUtils;

public class ReportHelper extends ScreenHelper {

    @Override
    public String getReportPath() {
        return null;
    }

    @Override
    public String getReportContent() {
        return null;
    }

    public enum ReportType {
        CRASH(1), SESSION(2), CUSTOM(3),ISSUE(4);

        public int code;
        private static final Map<Integer, ReportType> TYPES = new HashMap<>();
        ReportType(int code) {
            this.code = code;
        }
        static {
            for (ReportType value : values()) {
                TYPES.put(value.code, value);
            }
        }
        public int getCode() {
            return code;
        }
        public static ReportType getByCode(int code) {
            return TYPES.get(code);
        }
    }

    ReportType type;
    Object target;

    public void start(ReportType type, Object target) {
        this.type = type;
        this.target = target;
        boolean isHtml = false;
        List<String> filesPaths = getFilePaths();

        EmailUtils.sendEmailIntent(context,
                getEmailTo(),
                "",
                getEmailSubject(),
                getEmailBody(isHtml),
                filesPaths,
                false);
    }


    @NonNull
    private List<String> getFilePaths() {
        List<String> filePaths = new ArrayList<>();
        filePaths.add(new InfoHelper().getReportPath());

        if(type.equals(ReportType.SESSION)){

            filePaths.add(new LogcatHelper().getReportPath());

            //Include only the last one
            //filePaths.add(new ScreenshotHelper().getReportPath());

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


    @NonNull
    private String getEmailTo() {
        return IadtController.get().getConfig().getString(BuildConfig.EMAIL);
    }

    private String getEmailSubject(){
        AppInfoReporter helper = new AppInfoReporter(context);
        String formatter = "%s %s report from %s %s";
        String currentType = "";
        if(type.equals(ReportType.SESSION)){
            currentType = "session";
        }else if (type.equals(ReportType.CRASH)){
            currentType = "crash";
        }else if (type.equals(ReportType.CUSTOM)){
            currentType = "full";
        }
        return String.format(formatter,
                helper.getAppName(), currentType,
                Build.BRAND, Build.MODEL);
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
}
