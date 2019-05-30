package es.rafaco.inappdevtools.library.logic.event;

import java.util.HashMap;
import java.util.Map;


public enum Event {

    //Process
    PROCESS_ON_CREATE ("process_on_create"),
    PROCESS_ON_START("process_on_start"),
    PROCESS_ON_STOP("process_on_stop"),
    PROCESS_ON_RESUME("process_on_resume"),
    PROCESS_ON_PAUSE("process_on_pause"),
    PROCESS_ON_DESTROY("process_on_destroy"),

    //ScreenChange
    SCREEN_OFF("ScreenOff"),
    SCREEN_ON("ScreenOn"),
    USER_PRESENT("UserPresent"),

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
    GESTURE_LONG_PRESSED("LongPress"),

    ERROR_HANDLED_EXCEPTION("ErrorHandledException"),
    ERROR_ANR("ErrorAnr"),
    ERROR_CRASH("ErrorCrash");

    private static final Map<String, Event> EVENTS = new HashMap<>();
    private final String name;

    static {
        for (Event event : values()) {
            EVENTS.put(event.name, event);
        }
    }

    Event(String name) {
        this.name = name;
    }

    public static Event getByName(String name) {
        return EVENTS.get(name);
    }

    public String getName() {
        return name;
    }

}
