package es.rafaco.inappdevtools.library.logic.watcher.activityLog;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class ProcessLifecycleCallbacks {

    public static void onCreate() {
        FriendlyLog.log("D", "Process", "Create", "Process created");
    }

    public static void onStart() {
        FriendlyLog.log("D", "Process", "Start", "Process started");
    }

    public static void onStop() {
        FriendlyLog.log("D", "Process", "Stop", "Process stopped");
    }

    public static void onResume() {
        FriendlyLog.log("D", "Process", "Resume", "Process resumed");
    }

    public static void onPause() {
        FriendlyLog.log("D", "Process", "Pause", "Process paused");
    }

    public static void onDestroy() {
        FriendlyLog.log("I", "Process", "Destroy", "Process destroyed");
    }
}
