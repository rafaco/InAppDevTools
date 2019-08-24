package es.rafaco.inappdevtools.library.logic.events;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.detectors.app.ErrorAnrEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.app.ForegroundEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.app.InitialEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.device.AirplaneModeChangeEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.device.ConnectivityChangeEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.device.DeviceButtonsEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.device.OrientationEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.FragmentEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ProcessEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.user.ActivityTouchEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.user.GestureEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.user.ScreenChangeEventDetector;
import es.rafaco.inappdevtools.library.logic.events.detectors.user.ShakeEventDetector;
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

        //TODO: Refactor into a detector
        startCrashHandler();

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
            if (IadtController.get().isDebug())
                Log.d(Iadt.TAG, "EventDetector started " + eventDetector.getClass().getSimpleName());
            eventDetector.start();
        }
    }

    private void stopAll() {
        for (EventDetector eventDetector : eventDetectors) {
            if (IadtController.get().isDebug())
                Log.d(Iadt.TAG, "EventDetector stopped " + eventDetector.getClass().getSimpleName());
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
            if (IadtController.get().isDebug())
                Log.d(Iadt.TAG, "Exception handler added");
        }else{
            Log.w(Iadt.TAG, "Exception handler already attach on thread");
        }
    }
}
