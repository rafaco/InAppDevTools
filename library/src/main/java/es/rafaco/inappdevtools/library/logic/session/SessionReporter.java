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

package es.rafaco.inappdevtools.library.logic.session;

import android.content.Context;

import java.text.SimpleDateFormat;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.db.entities.SessionAnalysis;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;


public class SessionReporter {

    private final Context context;
    private final Session session;
    private SessionAnalysis analysis;

    public SessionReporter(Context context, Session session) {
        this.context = context;
        this.session = session;
    }

    public DocumentData getData() {
        return new DocumentData.Builder(getTitle())
                .setIcon(R.string.gmd_timeline)
                .setOverview(getOverview())
                .add(getFlagsInfo())
                .add(getDurationInfo())
                .add(getLogInfo())
        .build();
    }

    public String getTitle() {
        return Humanizer.ordinal((int) session.getUid()) + " Session";
    }

    public String getOverview() {
        return getStartOverview()+ Humanizer.newLine()
                + getFinishOverview() + Humanizer.newLine()
                + getLogsOverview();
    }

    private String getFinishOverview() {
        String flagsLine = "";
        if (session.isCurrent()){
            flagsLine += "Running for ";
        }
        else if (session.getCrashId()>0){
            flagsLine += "Crashed in ";
        }
        else {
            flagsLine += "Closed after ";
        }
        return flagsLine + Humanizer.getDuration(session.getDuration());
    }

    private String getStartOverview() {
        String flagsLine = "";

        if (session.isFirstStart()){
            flagsLine += "First start ";
        }
        else if (session.isNewBuild()){
            flagsLine += "New build ";
        }
        else {
            flagsLine += "Started ";
        }
        return flagsLine + Humanizer.getElapsedTimeLowered(session.getDate());
    }

    private String getLogsOverview() {
        return String.format("%s repro steps and %s logs",
                getReproStepCount(),
                getAnalysis().getTotal());
    }

    private DocumentSectionData getDurationInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("mm-dd HH:mm:ss");
        DocumentSectionData group = new DocumentSectionData.Builder("Dates")
                .setIcon(R.string.gmd_timer)
                .add("Duration", Humanizer.getDuration(session.getDuration()))
                .add("Start", Humanizer.getElapsedTime(session.getDate())
                        +"  "+ formatter.format(session.getDate()))
                .add("End", session.isCurrent() ? " - " :
                        Humanizer.getElapsedTime(session.getFinishDate())
                                +"  "+ formatter.format(session.getFinishDate()))
                .build();
        return group;
    }

    public DocumentSectionData getFlagsInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Flags")
                .setIcon(R.string.gmd_flag)
                .add("isFirstStart", session.isFirstStart())
                .add("isNewBuild", session.isNewBuild())
                .add("isFromCrash", session.isPendingCrash())
                .add("hasCrash", session.getCrashId()>0)
                .build();
        return group;
    }

    public DocumentSectionData getLogInfo() {

        DocumentSectionData group = new DocumentSectionData.Builder("Logs")
                .setIcon(R.string.gmd_format_align_left)
                .setOverview(analysis.getTotal() + "")
                .add(getAnalysis().getTotal() + " logs in Total")
                .add()
                .add(getAnalysis().getEventTotal() + " Event logs")
                .add("Fatal", getAnalysis().getEventFatal())
                .add("Error", getAnalysis().getEventError())
                .add("Warning", getAnalysis().getEventWarning())
                .add("Info", getAnalysis().getEventInfo())
                .add("Debug", getAnalysis().getEventDebug())
                .add("Verbose", getAnalysis().getEventVerbose())
                .add()
                .add(getAnalysis().getLogcatTotal() + " Logcat logs")
                .add("Fatal", getAnalysis().getLogcatFatal())
                .add("Error", getAnalysis().getLogcatError())
                .add("Warning", getAnalysis().getLogcatWarning())
                .add("Info", getAnalysis().getLogcatInfo())
                .add("Debug", getAnalysis().getLogcatDebug())
                .add("Verbose", getAnalysis().getLogcatVerbose())
                .build();
        return group;
    }

    public SessionAnalysis getAnalysis(){
        if (analysis != null){
            return analysis;
        }

        if (session.isCurrent()){
            analysis = IadtController.get().getSessionManager().calculateCurrentSessionAnalysis();
        }else{
            analysis = session.getAnalysis();
        }

        return analysis;
    }

    public int getReproStepCount() {
        return getAnalysis().getEventInfo()
                + getAnalysis().getEventWarning()
                + getAnalysis().getEventError()
                + getAnalysis().getEventFatal();
    }

    private String getFlagsLine() {
        String flagsLine = "";
        if (session.isCurrent()){
            flagsLine += "Current";
        }
        else if (session.getCrashId()>0){
            flagsLine += "Crashed!";
        }
        else {
            flagsLine += "Finished OK";
        }

        if (session.isFirstStart()){
            flagsLine += "  (first start)";
        }
        else if (session.isNewBuild()){
            flagsLine += "  (new build)";
        }
        return flagsLine;
    }
}
