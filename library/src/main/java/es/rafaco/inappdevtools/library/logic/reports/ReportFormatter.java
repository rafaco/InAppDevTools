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

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.db.IadtDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ReportFormatter {

    public static String getShortTitle(Report report) {
        String result = Humanizer.toCapitalCase(report.getReportType().name());
        if (report.getReportType() == ReportType.CRASH
                && report.getCrashId()>0){
            result += " " + report.getCrashId();
        }
        else if (report.getReportType() == ReportType.SESSION
                && report.getSessionId()>0){
            result += " " + report.getSessionId();
        }
        else if (report.getReportType() == ReportType.ISSUE){
            //TODO
        }
        return result;
    }

    public static String getLongTitle(Report report) {
        String result = getShortTitle(report);
        result += " report";
        return result;
    }

    public static int getStatusColor(Report report) {
        if (report.getReportType() == ReportType.CRASH){
            return R.color.rally_orange;
        }
        else if (report.getReportType() == ReportType.SESSION){
            if (report.getSessionId() == IadtController.get().getSessionManager().getCurrentUid()){
                return R.color.rally_blue;
            }
            else if (report.getCrashId() > 0){
                return R.color.rally_orange;
            }
            else{
                return R.color.rally_green;
            }
        }
        else{
            return R.color.rally_white;
        }
    }

    //TODO: RELOCATE: this don't belong here
    public static String getSessionStatusString(Report report) {
        String status;
        if (report.getReportType() == ReportType.CRASH){
            status = "Crashed";
        }
        else if (report.getReportType() == ReportType.SESSION){
            if (report.getSessionId() == IadtController.get()
                    .getSessionManager().getCurrentUid()){
                status = "Running";
            }
            else if (report.getSessionId() > 0 &&
                    IadtDatabase.get().sessionDao()
                            .findById(report.getSessionId()).getCrashId() > 0){
                status = "Crashed";
            }
            else{
                status = "Finished";
            }
        }
        else{
            status = "N/A";
        }
        return status;
    }

    public static String getSessionStatusString(Session session) {
        String status;
        if (session.getUid() == IadtController.get()
                .getSessionManager().getCurrentUid()){
            status = "Running";
        }
        else if (session.getCrashId() > 0){
            status = "Crashed";
        }
        else{
            status = "Finished";
        }
        return status;
    }

    public static String getTitle(Report report) {
        return getLongTitle(report) + " (" + getSessionStatusString(report) + ")";
    }

    public static String getAttachmentDescription(Report report) {
        String result = "";
        if (report.getReportType() != ReportType.ISSUE){
            if (report.getCrashId() > 0){
                result += "- Crash details and screenshot" + Humanizer.newLine();
            }
            result += "- Session details, repro steps and logs" + Humanizer.newLine();
            result += "- Screenshots taken during session" + Humanizer.newLine();
            result += "- Build details and build files" + Humanizer.newLine();
        }

        result += "- Basic info (Device, OS, App..)";
        return result;
    }
}
