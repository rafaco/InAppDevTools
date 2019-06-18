package es.rafaco.inappdevtools.library.logic.events.detectors;

import java.util.Date;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.initialization.FirstStartUtil;
import es.rafaco.inappdevtools.library.logic.initialization.NewBuildUtil;
import es.rafaco.inappdevtools.library.logic.initialization.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.files.CacheUtils;

public class InitialEventDetector extends EventDetector {

    public InitialEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.NEW_BUILD, new EventManager.OneShotListener(){

            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "DevTools", "NewBuild",
                        "New build from " + DateUtils.getElapsedTimeLowered((long)param));

                new CacheUtils().deleteAll(getContext());
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_CREATE, new EventManager.Listener(){
            @Override
            public void onEvent(Event event, Object param) {
                if (PendingCrashUtil.isPending())
                    FriendlyLog.log(new Date().getTime(), "W", "App", "Restart",
                            "App restarted after a crash");
                else if (FirstStartUtil.isFirstStart())
                    FriendlyLog.log(new Date().getTime(), "I", "App", "FirstStart",
                            "App started for first time");
                else
                    FriendlyLog.log(new Date().getTime(), "I", "App", "Start",
                            "App started");
            }
        });
    }

    @Override
    public void start() {
        if (NewBuildUtil.isNewBuild()){
            eventManager.fire(Event.NEW_BUILD, NewBuildUtil.getBuildTime());
        }
    }

    @Override
    public void stop() {
        //Intentionally empty
    }
}
