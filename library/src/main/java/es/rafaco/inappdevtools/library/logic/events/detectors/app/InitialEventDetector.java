package es.rafaco.inappdevtools.library.logic.events.detectors.app;

import java.util.Date;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.utils.init.FirstStartUtil;
import es.rafaco.inappdevtools.library.logic.utils.init.NewBuildUtil;
import es.rafaco.inappdevtools.library.logic.utils.init.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.files.CacheUtils;

public class InitialEventDetector extends EventDetector {

    public InitialEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.LIBRARY_START, new EventManager.OneShotListener(){
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Iadt", "Init",
                        "Iadt started");
            }
        });

        eventManager.subscribe(Event.APP_START, new EventManager.Listener(){
            @Override
            public void onEvent(Event event, Object param) {
                if (PendingCrashUtil.isPending()) {
                    FriendlyLog.log(new Date().getTime(), "W", "App", "Restart",
                            "App restarted after a crash");
                } else if (FirstStartUtil.isFirstStart()) {
                    FriendlyLog.log(new Date().getTime(), "I", "App", "FirstStart",
                            "App first start (no data)");
                } else if (NewBuildUtil.isNewBuild()) {
                    FriendlyLog.log(new Date().getTime(), "W", "App", "Start",
                            "App started (new compilation over old data)");
                } else {
                    FriendlyLog.log(new Date().getTime(), "I", "App", "Start",
                            "App started");
                }
            }
        });

        eventManager.subscribe(Event.NEW_BUILD, new EventManager.Listener(){
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("V", "Iadt", "NewBuild",
                        "New compilation from " + DateUtils.getElapsedTimeLowered((long)param));

                CacheUtils.deleteAll(getContext());
            }
        });
    }

    @Override
    public void start() {
        eventManager.fire(Event.LIBRARY_START);

        if (NewBuildUtil.isNewBuild()){
            eventManager.fire(Event.NEW_BUILD, NewBuildUtil.getBuildTime());
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
