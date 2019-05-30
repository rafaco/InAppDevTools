package es.rafaco.inappdevtools.library.logic.event.watcher;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.DevToolsConfig;
import es.rafaco.inappdevtools.library.logic.event.EventManager;
import es.rafaco.inappdevtools.library.logic.integrations.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.utils.ClassHelper;
import es.rafaco.inappdevtools.library.logic.watcher.activityLog.ActivityLogManager;
import es.rafaco.inappdevtools.library.logic.watcher.crash.CrashHandler;

public class WatcherManager {

    private final Context context;

    private ActivityLogManager activityLogManager;
    private List<Watcher> watchers = new ArrayList<>();

    public WatcherManager(Context context) {
        this.context = context;
        init(DevTools.getConfig());
    }

    protected EventManager getEventManager(){
        return DevTools.getEventManager();
    }

    public void init(DevToolsConfig config) {
        if (config.crashHandlerEnabled) startCrashHandler();
        if (config.activityLoggerEnabled) startActivityLogger();

        initWatchers();
        startAllWatchers();
        PandoraBridge.init();
    }


    private void initWatchers() {
        initWatcher(ProcessLifecycleWatcher.class);
        initWatcher(ErrorAnrWatcher.class);
        initWatcher(GestureWatcher.class);
        initWatcher(DeviceButtonsWatcher.class);
        initWatcher(ScreenChangeWatcher.class);
        initWatcher(ConnectivityChangeWatcher.class);
        initWatcher(AirplaneModeChangeWatcher.class);
        initWatcher(ShakeWatcher.class);
    }

    private void initWatcher(Class<? extends Watcher> className) {
        Watcher watcher = new ClassHelper<Watcher>().createClass(className,
                EventManager.class, getEventManager());
        if (watcher!= null){
            watcher.init();
            watchers.add(watcher);
        }
    }

    private void startAllWatchers() {
        for (Watcher watcher : watchers) {
            Log.d("WATCHER", "Watcher started " + watcher.getClass().getSimpleName());
            watcher.start();
        }
    }

    private void startForegroundWatchers() {
        for (Watcher watcher : watchers) {
            if (watcher.onlyForeground()){
                return;
            }
            watcher.start();
        }
    }

    private void stopForegroundWatchers() {
        for (Watcher watcher : watchers) {
            if (watcher.onlyForeground()){
                watcher.stop();
            }
        }
    }

    private void stopAllWatchers() {
        for (Watcher watcher : watchers) {
            Log.d("WATCHER", "Watcher stopped " + watcher.getClass().getSimpleName());
            watcher.stop();
        }
    }

    public Watcher getWatcher(Class<? extends Watcher> className) {
        for (Watcher watcher : watchers) {
            if (watcher.getClass().equals(className)){
                return watcher;
            }
        }
        return  null;
    }

    public void destroy() {
        stopAllWatchers();
        watchers = null;
    }




    private void startCrashHandler() {
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler != null && !currentHandler.getClass().isInstance(CrashHandler.class)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context, currentHandler));
            Log.d(DevTools.TAG, "Exception handler added");
        }else{
            Log.d(DevTools.TAG, "Exception handler already attach on thread");
        }
    }

    private void startActivityLogger() {
        activityLogManager = new ActivityLogManager(context);
    }

    public ActivityLogManager getActivityLogManager() {
        return activityLogManager;
    }

    public ShakeWatcher getShakeWatcher() {
        return (ShakeWatcher) getWatcher(ShakeWatcher.class);
    }

    public GestureDetector getGestureDetector() {
        GestureWatcher watcher = (GestureWatcher) getWatcher(GestureWatcher.class);
        return watcher.getDetector();
    }
}
