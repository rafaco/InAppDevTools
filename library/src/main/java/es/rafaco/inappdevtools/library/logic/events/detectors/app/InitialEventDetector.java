package es.rafaco.inappdevtools.library.logic.events.detectors.app;

import android.util.Log;

import java.util.Date;

import es.rafaco.inappdevtools.library.Iadt;
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
import es.rafaco.inappdevtools.library.view.overlay.screens.console.Shell;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatLine;
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

                //TODO: delay after App started
                long improveStartTime = improveStartTime(detectionDate, pid);

                Session session = new Session();
                session.setDate(improveStartTime);
                session.setDetectionDate(detectionDate);
                session.setPid(pid);

                //TODO: calculate finishDate for previous session and update it in db
                //session.setFinishDate();

                long id = DevToolsDatabase.getInstance().sessionDao().insert(session);

                FriendlyLog.log(improveStartTime, "I", "Iadt", "Init",
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
    }

    @Override
    public void start() {
        Log.w("SESSION", "New session start");
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

    public long improveStartTime(long detectionDate, int pid){
        Shell exe = new Shell();
        String command = "logcat -v time -m 5 --pid=" + pid + " *:V";
        String output = exe.run(exe.formatBashCommand(command));
        String[] lines = output.split("[" + Humanizer.newLine() + "]");
        long firstLogcatLineTime = 0;
        if (lines.length>0) {
            for (String line : lines) {
                firstLogcatLineTime = LogcatLine.extractDate(line);
                if (firstLogcatLineTime != 0)
                    break;
            }
        }
        if (firstLogcatLineTime != 0 && firstLogcatLineTime < detectionDate){
            //Real start is for sure before the first logcat line
            //LogScreen will show before the first line
            return firstLogcatLineTime - 1;
        }else{
            Log.w(Iadt.TAG, "Unable to improve start session time");
            return detectionDate;
        }
    }
}
