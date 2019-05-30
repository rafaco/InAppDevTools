package es.rafaco.inappdevtools.library.logic.event.watcher;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.event.Event;
import es.rafaco.inappdevtools.library.logic.event.EventManager;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class ShakeWatcher extends Watcher {

    private final SensorManager sensorManager;
    private final Sensor sensor;
    private InnerReceiver mReceiver;

    public ShakeWatcher(EventManager manager) {
        super(manager);

        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void init() {
        eventManager.subscribe(Event.SHAKE, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "Shake", "Shake detected");
                DevTools.openTools(false);
            }
        });

        eventManager.subscribe(Event.ACTIVITY_ON_RESUME, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                start();
            }
        });
        eventManager.subscribe(Event.ACTIVITY_ON_PAUSE, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                stop();
            }
        });
    }@Override
    public void start() {
        sensorManager.registerListener(mReceiver, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void stop() {
        sensorManager.unregisterListener(mReceiver);
    }

    class InnerReceiver implements SensorEventListener {

        private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
        private static final int SHAKE_SLOP_TIME_MS = 500;
        private long mShakeTimestamp;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }
                mShakeTimestamp = now;
                Log.d(DevTools.TAG, "RAFA - Shake action detected!");

                eventManager.fire(Event.SHAKE);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
