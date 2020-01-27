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
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.reports.ReportHelper;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.db.entities.SessionAnalysis;
import es.rafaco.inappdevtools.library.storage.db.entities.SessionAnalysisRaw;
import es.rafaco.inappdevtools.library.storage.db.entities.SessionDao;
import es.rafaco.inappdevtools.library.storage.prefs.utils.FirstStartUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.NewBuildUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class SessionManager {

    private final Context context;
    Session session;

    public SessionManager(Context context) {
        this.context = context;

        //Tracking session per process
        startNew(DateUtils.getLong());
    }

    public Session getCurrent() {
        return session;
    }

    public void updateCurrent(Session updated) {
        getDao().update(updated);
        session = updated;
    }

    public long startNew(Long date) {
        int pid = ThreadUtils.myPid();
        long detectionDate = date;

        session = new Session();
        session.setDate(detectionDate);
        session.setDetectionDate(detectionDate);
        session.setPid(pid);

        if (FirstStartUtil.isFirstStart()){
            FirstStartUtil.saveFirstStart();
            session.setFirstStart(true);
        }else{
            session.setFirstStart(false);
        }

        if (NewBuildUtil.isNewBuild()){
            session.setNewBuild(true);
        }else{
            session.setNewBuild(false);
        }

        if (PendingCrashUtil.isPending()){
            session.setPendingCrash(true);
        }else{
            session.setPendingCrash(false);
        }

        //TODO: calculate finishDate for previous session and update it in db
        //session.setFinishDate();

        long newSessionId = getDao().insert(session);
        session.setUid(newSessionId);

        return newSessionId;
    }

    public void improveStartTime() {
        Session currentSession = getCurrent();
        if (currentSession.getDate() != currentSession.getDetectionDate()){
            return;
        }

        long detectionDate = currentSession.getDetectionDate();
        int pid = ThreadUtils.myPid();
        String threadFilter = "%" + "Process: " + pid + "%";
        Friendly firstSessionLog = getFriendlyDao().getFirstSessionLog(threadFilter, detectionDate);

        if (firstSessionLog != null
                && firstSessionLog.getDate()<detectionDate){

            //Update session item
            long improvedDate = firstSessionLog.getDate();
            currentSession.setDate(improvedDate);
            updateCurrent(currentSession);

            //Update 'new session' log item
            String message = "Session " + currentSession.getUid() +" started";
            Friendly newSessionLog = getFriendlyDao().getNewSessionLog(message);
            if (newSessionLog != null
                    && improvedDate < newSessionLog.getDate()){
                newSessionLog.setDate(improvedDate);
                getFriendlyDao().update(newSessionLog);

                if (IadtController.get().isDebug())
                    Log.i(Iadt.TAG, "Improved session start time by "
                            + Humanizer.getDuration(detectionDate - improvedDate));
            }
            else{
                if (IadtController.get().isDebug())
                    Log.w(Iadt.TAG, "Unable to improve session start time");
            }
        }
        else{
            Log.w(Iadt.TAG, "Unable to improve session start time");
        }
    }

    public long updateFinishTime(long uid) {
        Session target = getDao().findById(uid);
        if (target.getFinishDate() > 0) {
            return target.getFinishDate();
        }

        Session next = getDao().findById(uid+1);
        //TODO: if (next.getDate() != next.getDetectionDate()){
        long firstNextDate = next.getDate();
        Friendly lastSessionLog = getFriendlyDao().getLastSessionLog(firstNextDate);

        long finishDate = lastSessionLog.getDate();
        target.setFinishDate(finishDate);
        getDao().update(target);

        return finishDate;
    }

    public List<Session> getSessionsWithOverview() {

        List<Session> sessions = getDao().getAll();

        //Current session
        Session current = sessions.get(0);
        current.setAnalysis(calculateCurrentSessionAnalysis());

        //Previous sessions
        for (int i = 1; i<sessions.size(); i++){
            Session target = sessions.get(i);
            updateFinishedSession(target);

        }
        return sessions;
    }

    private void updateFinishedSession(Session target) {
        //Calculate finish date if needed
        if (target.getFinishDate()<=0){
            long finishDate = updateFinishTime(target.getUid());
            target.setFinishDate(finishDate);
        }

        //Calculate and store analysis if needed
        if (TextUtils.isEmpty(target.getAnalysisJson())){
            target.setAnalysis(calculateFinishedSessionAnalysis(target));
            getDao().update(target);
        }
    }

    public SessionAnalysis calculateCurrentSessionAnalysis() {
        long startDate = getCurrent().getDetectionDate();
        List<SessionAnalysisRaw> logcatResume = getDao().analiseLiveSessionLogcat(startDate);
        List<SessionAnalysisRaw> eventResume = getDao().analiseLiveSessionEvents(startDate);
        SessionAnalysis analysis = SessionAnalysis.buildFromRaw(logcatResume, eventResume);
        return analysis;
    }

    public SessionAnalysis calculateFinishedSessionAnalysis(Session session) {
        long startDate = session.getDate();
        long finishDate = session.getFinishDate();
        List<SessionAnalysisRaw> logcatResume = getDao().analiseFinishedSessionLogcat(startDate, finishDate);
        List<SessionAnalysisRaw> eventResume = getDao().analiseFinishedSessionEvents(startDate, finishDate);
        SessionAnalysis analysis = SessionAnalysis.buildFromRaw(logcatResume, eventResume);
        return analysis;
    }

    public Session getSessionWithOverview(int sessionUid) {
        if (getCurrent().getUid() == sessionUid){
            getCurrent().setAnalysis(calculateCurrentSessionAnalysis());
            return getCurrent();
        }

        //Not the current Session
        Session target = getDao().findById(sessionUid);
        updateFinishedSession(target);
        return target;
    }


    private SessionDao getDao() {
        return DevToolsDatabase.getInstance().sessionDao();
    }

    private FriendlyDao getFriendlyDao() {
        return DevToolsDatabase.getInstance().friendlyDao();
    }

    public Context getContext() {
        return context;
    }

    public void destroy() {
        //TODO
    }

    public void storeInfoDocuments() {
        ReportHelper.getInfoDocuments(getCurrent().getUid());
    }
}
