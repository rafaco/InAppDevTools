package es.rafaco.devtools.logic.watcher;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;
import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.DevToolsConfig;
import es.rafaco.devtools.logic.watcher.activityLog.ActivityLogManager;
import es.rafaco.devtools.logic.watcher.activityLog.ProcessLifecycleCallbacks;
import es.rafaco.devtools.logic.watcher.anr.AnrLogger;
import es.rafaco.devtools.logic.watcher.crash.CrashHandler;
import es.rafaco.devtools.logic.utils.AppUtils;
import es.rafaco.devtools.logic.steps.FriendlyLog;
import tech.linjiang.pandora.Pandora;

public class WatcherManager {

    private final Context context;

    private ActivityLogManager activityLogManager;
    private AnrLogger anrLogger;
    private ShakeWatcher shakeWatcher;
    private DeviceButtonsWatcher deviceButtonsWatcher;
    private ScreenChangeWatcher screenChangeWatcher;
    private ConnectivityChangeWatcher connectivityChangeWatcher;
    private AirplaneModeChangeWatcher airplaneModeChangeWatcher;

    public WatcherManager(Context context) {
        this.context = context;
    }

    public void init(DevToolsConfig config) {
        if (config.crashHandlerEnabled) startCrashHandler();
        if (config.anrLoggerEnabled) startAnrLogger();
        if (config.strictModeEnabled) startStrictMode();
        if (config.activityLoggerEnabled) startActivityLogger();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ProcessLifecycleCallbacks());
        startDeviceButtonsWatcher();
        startScreenChangeWatcher();
        startNetworkChangeWatcher();
        startAirplaneModeChangeWatcher();

        //if (config.invocationByShake)
        startShakeWatcher();
        //Config.setSHAKE_SWITCH(false);
        Pandora.get().open();
    }

    public void destroy() {
        anrLogger.destroy();
        deviceButtonsWatcher.stop();
        screenChangeWatcher.stop();
        connectivityChangeWatcher.stop();
        airplaneModeChangeWatcher.stop();
        shakeWatcher.stop();
    }



    public void start(String target){
        //TODO:
        Watcher watcher = getWatcher(target);
        watcher.start();
    }


    public void stop(String target){
        //TODO:
        Watcher watcher = getWatcher(target);
        watcher.stop();
    }

    private Watcher getWatcher(String target) {
        //TODO: store watchers at collection and retrieve them?
        return null;
    }






    private void startCrashHandler() {
        Thread.UncaughtExceptionHandler currentHanler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHanler != null || !currentHanler.getClass().isInstance(CrashHandler.class)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context.getApplicationContext(), context, currentHanler));
            Log.d(DevTools.TAG, "Exception handler added");
        }else{
            Log.d(DevTools.TAG, "Exception handler already attach on thread");
        }
    }

    private void startStrictMode() {
        AppUtils.startStrictMode();
    }

    private void startShakeWatcher() {
        shakeWatcher = new ShakeWatcher(context);
        shakeWatcher.setListener(new ShakeWatcher.InnerListener() {
            @Override
            public void onShake() {
                DevTools.openTools(false);
            }
        });
    }

    private void startActivityLogger() {
        activityLogManager = new ActivityLogManager(context);
    }

    private void startAnrLogger(){
        anrLogger = new AnrLogger();
    }

    private void startDeviceButtonsWatcher() {
        deviceButtonsWatcher = new DeviceButtonsWatcher(context);
        deviceButtonsWatcher.setListener(new DeviceButtonsWatcher.InnerListener() {
            @Override
            public void onHomePressed() {
                FriendlyLog.log("D", "User", "HomeKey", "Pressed home button");
            }
            @Override
            public void onRecentPressed() {
                FriendlyLog.log("D", "User", "RecentKey", "Pressed recent button");
            }

            @Override
            public void onDreamPressed() {
                FriendlyLog.log("D", "User", "DreamKey", "Pressed off button");
            }

            @Override
            public void onUnknownPressed(String info) {
                FriendlyLog.log("D", "User", "UnknownKey", "Pressed Unknown button: " + info);
            }
        });
        deviceButtonsWatcher.start();
    }

    private void startScreenChangeWatcher() {
        screenChangeWatcher = new ScreenChangeWatcher(context);
        screenChangeWatcher.setListener(new ScreenChangeWatcher.InnerListener() {
            @Override
            public void onScreenOff() {
                FriendlyLog.log("D", "Device", "ScreenOn", "Screen went ON");
            }

            @Override
            public void onScreenOn() {
                FriendlyLog.log("D", "Device", "ScreenOff", "Screen went OFF");
            }

            @Override
            public void onUserPresent() {
                FriendlyLog.log("D", "Device", "UserPresent", "User is now present");
            }
        });
        screenChangeWatcher.start();
    }

    private void startNetworkChangeWatcher() {
        connectivityChangeWatcher = new ConnectivityChangeWatcher(context);
        connectivityChangeWatcher.setListener(new ConnectivityChangeWatcher.InnerListener() {
            @Override
            public void onNetworkAvailable(String networkType) {
                FriendlyLog.log("D", "Network", "Connected", "Connected to a " + networkType + " network");
            }

            @Override
            public void onNetworkLost(String networkType) {
                FriendlyLog.log("D", "Network", "Disconnected", "Disconnected from " + networkType + " network");
            }
        });
        connectivityChangeWatcher.start();
    }

    private void startAirplaneModeChangeWatcher() {
        airplaneModeChangeWatcher = new AirplaneModeChangeWatcher(context);
        airplaneModeChangeWatcher.setListener(new AirplaneModeChangeWatcher.InnerListener() {
            @Override
            public void onAirplaneModeOn() {
                FriendlyLog.log("D", "Network", "AirplaneOn", "Airplane mode connected");
            }

            @Override
            public void onAirplaneModeOff() {
                FriendlyLog.log("D", "Network", "AirplaneOff", "Airplane mode disconnected");
            }
        });
        airplaneModeChangeWatcher.start();
    }

    public ActivityLogManager getActivityLogManager() {
        return activityLogManager;
    }
    public ShakeWatcher getShakeWatcher() {
        return shakeWatcher;
    }
}
