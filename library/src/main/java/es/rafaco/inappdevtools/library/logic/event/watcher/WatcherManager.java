package es.rafaco.inappdevtools.library.logic.event.watcher;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.DevToolsConfig;
import es.rafaco.inappdevtools.library.logic.event.EventManager;
import es.rafaco.inappdevtools.library.logic.integrations.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.utils.ClassHelper;
import es.rafaco.inappdevtools.library.logic.event.watcher.crash.CrashHandler;

public class WatcherManager {

    private final Context context;
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

        initWatchers();
        startAllWatchers();

        PandoraBridge.init();
    }


    private void initWatchers() {
        initWatcher(ProcessWatcher.class);
        initWatcher(ForegroundWatcher.class);
        initWatcher(ActivityWatcher.class);
        initWatcher(FragmentWatcher.class);
        initWatcher(ActivityTouchWatcher.class);
        initWatcher(OrientationWatcher.class);
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
}
