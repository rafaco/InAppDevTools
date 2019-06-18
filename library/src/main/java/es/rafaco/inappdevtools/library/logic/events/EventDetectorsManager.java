package es.rafaco.inappdevtools.library.logic.events;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.DevToolsConfig;
import es.rafaco.inappdevtools.library.logic.events.detectors.*;
import es.rafaco.inappdevtools.library.logic.integrations.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.utils.ClassHelper;
import es.rafaco.inappdevtools.library.logic.events.detectors.crash.CrashHandler;

public class EventDetectorsManager {

    private final Context context;
    private final EventManager eventManager;
    private List<EventDetector> eventDetectors = new ArrayList<>();

    public EventDetectorsManager(EventManager eventManager) {
        this.eventManager = eventManager;
        this.context = eventManager.getContext();
        init(DevTools.getConfig());
    }

    public void init(DevToolsConfig config) {
        if (config.crashHandlerEnabled) startCrashHandler();

        initDetectors();
        startAll();

        PandoraBridge.init();
    }


    private void initDetectors() {
        initDetector(InitialEventDetector.class);

        initDetector(ProcessEventDetector.class);
        initDetector(ForegroundEventDetector.class);
        initDetector(ActivityEventDetector.class);
        initDetector(FragmentEventDetector.class);
        initDetector(ActivityTouchEventDetector.class);
        initDetector(OrientationEventDetector.class);
        initDetector(ErrorAnrEventDetector.class);
        initDetector(GestureEventDetector.class);
        initDetector(DeviceButtonsEventDetector.class);
        initDetector(ScreenChangeEventDetector.class);
        initDetector(ConnectivityChangeEventDetector.class);
        initDetector(AirplaneModeChangeEventDetector.class);
        initDetector(ShakeEventDetector.class);
    }

    private void initDetector(Class<? extends EventDetector> className) {
        EventDetector eventDetector = new ClassHelper<EventDetector>().createClass(className,
                EventManager.class, eventManager);
        if (eventDetector != null){
            eventDetectors.add(eventDetector);
        }
    }

    private void startAll() {
        for (EventDetector eventDetector : eventDetectors) {
            Log.d(DevTools.TAG, "EventDetector started " + eventDetector.getClass().getSimpleName());
            eventDetector.start();
        }
    }

    private void stopAll() {
        for (EventDetector eventDetector : eventDetectors) {
            Log.d(DevTools.TAG, "EventDetector stopped " + eventDetector.getClass().getSimpleName());
            eventDetector.stop();
        }
    }

    public EventDetector get(Class<? extends EventDetector> className) {
        for (EventDetector eventDetector : eventDetectors) {
            if (eventDetector.getClass().equals(className)){
                return eventDetector;
            }
        }
        return  null;
    }

    public void destroy() {
        stopAll();
        eventDetectors = null;
    }



    //TODO: Refactor into an isolated watcher
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
