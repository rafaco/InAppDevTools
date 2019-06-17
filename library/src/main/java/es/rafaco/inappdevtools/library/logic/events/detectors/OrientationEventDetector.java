package es.rafaco.inappdevtools.library.logic.events.detectors;

import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

import static android.content.Context.SENSOR_SERVICE;

public class OrientationEventDetector extends EventDetector implements SensorEventListener{

    SensorManager sm;
    private int previousOrientation = -1;

    public OrientationEventDetector(EventManager eventManager) {
        super(eventManager);
        sm = (SensorManager) DevTools.getAppContext().getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.ACTIVITY_ON_RESUME, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                start();
            }
        });
        eventManager.subscribe(Event.ACTIVITY_ON_PAUSE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                stop();
            }
        });

        //Log initial orientation
        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (previousOrientation == -1){
                    previousOrientation = getOrientation();
                    String orientation = getOrientationString();
                    FriendlyLog.log("I", "Device", orientation,
                            "Orientation is " + orientation.toLowerCase());
                }
            }
        });

        //Log orientation changes
        eventManager.subscribe(Event.ORIENTATION_PORTRAIT, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I", "Device", "Portrait",
                        "Orientation changed to portrait");
            }
        });
        eventManager.subscribe(Event.ORIENTATION_LANDSCAPE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I", "Device", "Landscape",
                        "Orientation changed to landscape");
            }
        });
    }

    @Override
    public void start() {
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void stop() {
        sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int detectedOrientation = getOrientation();
        if (detectedOrientation == Configuration.ORIENTATION_PORTRAIT
                && previousOrientation != detectedOrientation) {

            previousOrientation = detectedOrientation;
            eventManager.fire(Event.ORIENTATION_PORTRAIT);
        }
        else if (detectedOrientation == Configuration.ORIENTATION_LANDSCAPE
                && previousOrientation != detectedOrientation) {

            previousOrientation = detectedOrientation;
            eventManager.fire(Event.ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public static int getOrientation(){
        return DevTools.getAppContext().getResources().getConfiguration().orientation;
    }

    public static String getOrientationString(){
        return (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) ? "Landscape" : "Portrait";
    }
}
