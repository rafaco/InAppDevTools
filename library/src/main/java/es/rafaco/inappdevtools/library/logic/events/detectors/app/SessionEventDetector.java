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

package es.rafaco.inappdevtools.library.logic.events.detectors.app;

import java.util.Date;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.prefs.utils.FirstStartUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.NewBuildUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.files.CacheUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class SessionEventDetector extends EventDetector {

    public SessionEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.APP_NEW_SESSION, new EventManager.OneShotListener(){
            @Override
            public void onEvent(Event event, Object param) {
                int pid = ThreadUtils.myPid();
                long detectionDate = (Long) param;

                Session session = new Session();
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
                    eventManager.fire(Event.APP_NEW_BUILD, NewBuildUtil.getBuildTime());
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

                long id = DevToolsDatabase.getInstance().sessionDao().insert(session);

                FriendlyLog.log("I", "Iadt", "Init",
                        "Session " + id + " started");
            }
        });

        eventManager.subscribe(Event.APP_NEW_BUILD, new EventManager.Listener(){
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("V", "Iadt", "NewBuild",
                        "New compilation from " + Humanizer.getElapsedTimeLowered((long)param));

                CacheUtils.deleteAll(getContext());
            }
        });

        eventManager.subscribe(Event.APP_START, new EventManager.Listener(){
            @Override
            public void onEvent(Event event, Object param) {
                Session currentSession = DevToolsDatabase.getInstance().sessionDao().getLast();

                if (currentSession.isPendingCrash()) {
                    FriendlyLog.log(new Date().getTime(), "I", "App", "Restart",
                            "App restarted (previous session crashed)");
                }
                else if (currentSession.isFirstStart()) {
                    FriendlyLog.log(new Date().getTime(), "I", "App", "FirstStart",
                            "App first start (no local data)");
                }
                else if (currentSession.isNewBuild()) {
                    FriendlyLog.log(new Date().getTime(), "I", "App", "Start",
                            "App started (new compilation over old data)");
                }
                else {
                    FriendlyLog.log(new Date().getTime(), "I", "App", "Start",
                            "App started");
                }
            }
        });
    }

    @Override
    public void start() {
        eventManager.fire(Event.APP_NEW_SESSION, DateUtils.getLong());

        eventManager.subscribe(Event.PROCESS_ON_CREATE, new EventManager.OneShotListener() {
            @Override
            public void onEvent(Event event, Object param) {
                eventManager.fire(Event.APP_START);
            }
        });
    }

    @Override
    public void stop() {
        //Intentionally empty
    }
}
