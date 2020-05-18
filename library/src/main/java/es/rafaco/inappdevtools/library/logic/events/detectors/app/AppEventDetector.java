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

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.prefs.utils.NewBuildUtil;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.files.utils.CacheUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class AppEventDetector extends EventDetector {

    public AppEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.APP_NEW_SESSION, new EventManager.OneShotListener(){
            @Override
            public void onEvent(Event event, Object param) {
                if (NewBuildUtil.isNewBuild()) {
                    eventManager.fire(Event.APP_NEW_BUILD, NewBuildUtil.getBuildTime());
                }

                //TODO: relocate to session manager?
                Session session = IadtController.get().getSessionManager().getCurrent();
                FriendlyLog.logSessionStart(session.getUid());
            }
        });

        eventManager.subscribe(Event.APP_NEW_BUILD, new EventManager.Listener(){
            @Override
            public void onEvent(Event event, Object param) {
                long buildId = IadtController.get().getBuildManager().getCurrentId();
                long sessionId = IadtController.get().getSessionManager().getCurrentUid();
                FriendlyLog.logBuildStart(buildId, sessionId, (long)param);
                CacheUtils.deleteAll(getContext());
            }
        });

        eventManager.subscribe(Event.APP_START, new EventManager.Listener(){
            @Override
            public void onEvent(Event event, Object param) {
                Session currentSession = IadtController.get().getSessionManager().getCurrent();

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

        //Fired at OverlayService
        eventManager.subscribe(Event.APP_TASK_REMOVED, new EventManager.Listener(){
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I", "App", "TaskRemoved",
                        "App closed (task removed)");
            }
        });

        //Fired by ActivityTracker when tracking ACTIVITY_ON_RESUME
        //TODO: fragment navigation
        eventManager.subscribe(Event.APP_NAVIGATION, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I", "App", "Navigation",
                        "Navigation to " + param);
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
