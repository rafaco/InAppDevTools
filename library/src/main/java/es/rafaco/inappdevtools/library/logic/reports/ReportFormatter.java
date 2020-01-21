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

import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ReportFormatter {

    public static String getShortDescription(Report report) {
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

    public static String getLongDescription(Report report) {
        String result = getShortDescription(report);
        result += " report";
        return result;
    }
}
