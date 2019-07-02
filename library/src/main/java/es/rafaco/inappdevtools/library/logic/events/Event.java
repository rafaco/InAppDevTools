package es.rafaco.inappdevtools.library.logic.events;

import java.util.HashMap;
import java.util.Map;

public enum Event {

    APP_NEW_SESSION("AppNewSession"),
    APP_NEW_BUILD("AppNewBuild"),
    APP_START("AppStart"),

    CONFIG_CHANGED("ConfigChanged"),
    OVERLAY_NAVIGATION("OverlayNavigation"),
    OVERLAY_HIDDEN("OverlayHidden"),

    ERROR_HANDLED_EXCEPTION("ErrorHandledException"),
    ERROR_ANR("ErrorAnr"),
    ERROR_CRASH("ErrorCrash"),

    //Process
    PROCESS_ON_CREATE ("process_on_create"),
    PROCESS_ON_START("process_on_start"),
    PROCESS_ON_STOP("process_on_stop"),
    PROCESS_ON_RESUME("process_on_resume"),
    PROCESS_ON_PAUSE("process_on_pause"),
    PROCESS_ON_DESTROY("process_on_destroy"),

    //Activity
    ACTIVITY_OPEN("activity_open"),
    ACTIVITY_ON_CREATE ("activity_on_create"),
    ACTIVITY_ON_START("activity_on_start"),
    ACTIVITY_ON_STOP("activity_on_stop"),
    ACTIVITY_ON_RESUME("activity_on_resume"),
    ACTIVITY_ON_PAUSE("activity_on_pause"),
    ACTIVITY_ON_SAVE_INSTANCE("activity_on_save_instance"),
    ACTIVITY_ON_DESTROY("activity_on_destroy"),

    //Fragment
    FRAGMENT_PRE_ATTACHED("fragment_pre_attached"),
    FRAGMENT_ATTACHED ("fragment_attached"),
    FRAGMENT_CREATED("fragment_created"),
    FRAGMENT_ACTIVITY_CREATED("fragment_activity_created"),
    FRAGMENT_VIEW_CREATED("fragment_view_created"),
    FRAGMENT_STARTED("fragment_started"),
    FRAGMENT_RESUMED("fragment_resumed"),
    FRAGMENT_PAUSED("fragment_paused"),
    FRAGMENT_STOPPED("fragment_stopped"),
    FRAGMENT_SAVE_INSTANCE("fragment_save_instance"),
    FRAGMENT_VIEW_DESTROY("fragment_view_destroy"),
    FRAGMENT_DESTROY("fragment_destroy"),
    FRAGMENT_DETACHED("fragment_detached"),

    //ScreenChange
    ORIENTATION_PORTRAIT("orientation_portrait"),
    ORIENTATION_LANDSCAPE("orientation_landscape"),

    //ScreenChange
    SCREEN_OFF("ScreenOff"),
    SCREEN_ON("ScreenOn"),
    USER_PRESENT("UserPresent"),

    //ScreenChange
    IMPORTANCE_FOREGROUND("importance_foreground"),
    IMPORTANCE_BACKGROUND("importance_background"),

    //Shake
    SHAKE("Shake"),

    //DeviceButtons
    DEVICE_HOME_PRESSED("HomePressed"),
    DEVICE_RECENT_PRESSED("RecentPressed"),
    DEVICE_DREAM_PRESSED("DreamPressed"),
    DEVICE_UNKNOWN_PRESSED("UnknownPressed"),

    //Connectivity
    CONNECTIVITY_UP("NetworkAvailable"),
    CONNECTIVITY_DOWN("NetworkLost"),

    //Airplane mode
    AIRPLANE_MODE_UP("AirplaneModeUp"),
    AIRPLANE_MODE_DOWN("AirplaneModeDown"),

    //Gesture
    GESTURE_SINGLE_TAP("SingleTap"),
    GESTURE_CONTEXT_TAP("ContextClick"),
    GESTURE_DOUBLE_TAP("DoubleClick"),
    GESTURE_FLING_TAP("FlingClick"),
    GESTURE_LONG_PRESSED("LongPress");

    private final String name;

    Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    private static final Map<String, Event> ITEMS = new HashMap<>();

    static {
        for (Event event : values()) {
            ITEMS.put(event.name, event);
        }
    }

    public static Event getByName(String name) {
        return ITEMS.get(name);
    }

}
