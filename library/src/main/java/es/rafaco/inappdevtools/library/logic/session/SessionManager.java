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

import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.db.entities.SessionDao;
import es.rafaco.inappdevtools.library.storage.prefs.utils.FirstStartUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.NewBuildUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;

public class SessionManager {

    private final Context context;
    Session session;

    public SessionManager(Context context) {
        this.context = context;

        //Tracking session per process
        startNewSession(DateUtils.getLong());
    }

    public Session getCurrent() {
        return session;
    }

    public void updateCurrent(Session updated) {
        getDao().update(updated);
        session = updated;
    }

    public long startNewSession(Long date) {
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


    public Context getContext() {
        return context;
    }

    private SessionDao getDao() {
        return DevToolsDatabase.getInstance().sessionDao();
    }

    public void destroy() {
        //TODO
    }
}
