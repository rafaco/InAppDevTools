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

package es.rafaco.inappdevtools.library.logic.info;

import android.content.Context;
import android.util.Log;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.logic.info.reporters.AbstractInfoReporter;

public class InfoManager {

    private final Context context;

    public InfoManager(Context context) {
        this.context = context;
    }


    public InfoReportData getReportData(InfoReport report) {
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "Generating " + report.getTitle() + "InfoReport");
        AbstractInfoReporter helper = report.getReporter();
        return helper.getData();
    }


    public InfoReportData getReportData(int infoReportIndex) {
        InfoReport infoReport = getInfoReport(infoReportIndex);
        return getReportData(infoReport);
    }

    public InfoReport getInfoReport(int infoReportIndex) {
        InfoReport[] infoReports = InfoReport.values();
        return infoReports[infoReportIndex];
    }
}
