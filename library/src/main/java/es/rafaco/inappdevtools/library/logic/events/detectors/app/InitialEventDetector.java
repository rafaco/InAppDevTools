package es.rafaco.inappdevtools.library.logic.events.detectors.app;

import android.util.Log;

import java.util.Date;

import es.rafaco.inappdevtools.library.IadtController;
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

public class InitialEventDetector extends EventDetector {

    public InitialEventDetector(EventManager eventManager) {
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
                if (PendingCrashUtil.isSessionFromPending()) {
                    FriendlyLog.log(new Date().getTime(), "W", "App", "Restart",
                            "App restarted after a crash");
                } else if (FirstStartUtil.isFirstStart()) {
                    FriendlyLog.log(new Date().getTime(), "I", "App", "FirstStart",
                            "App first start (no data)");
                } else if (NewBuildUtil.isNewBuild()) {
                    FriendlyLog.log(new Date().getTime(), "I", "App", "Start",
                            "App started (new compilation over old data)");
                } else {
                    FriendlyLog.log(new Date().getTime(), "I", "App", "Start",
                            "App started");
                }
            }
        });
    }

    @Override
    public void start() {
        if (IadtController.get().isDebug())
            Log.d("SESSION", "New session start");
        eventManager.fire(Event.APP_NEW_SESSION, DateUtils.getLong());

        if (NewBuildUtil.isNewBuild()){
            eventManager.fire(Event.APP_NEW_BUILD, NewBuildUtil.getBuildTime());
        }

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
