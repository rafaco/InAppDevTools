package es.rafaco.inappdevtools.library.logic.events.detectors.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class AirplaneModeChangeEventDetector extends EventDetector {

    private IntentFilter mFilter;
    private InnerReceiver mReceiver;


    public AirplaneModeChangeEventDetector(EventManager manager) {
        super(manager);
        mFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.AIRPLANE_MODE_UP, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Network", "AirplaneOn",
                        "Airplane mode connected");
            }
        });

        eventManager.subscribe(Event.AIRPLANE_MODE_DOWN, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Network", "AirplaneOff",
                        "Airplane mode disconnected");
            }
        });
    }

    @Override
    public void start() {
        if (mReceiver != null) {
            getContext().registerReceiver(mReceiver, mFilter);
        }
    }

    @Override
    public void stop() {
        if (mReceiver != null) {
            getContext().unregisterReceiver(mReceiver);
        }
    }

   class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_AIRPLANE_MODE_CHANGED)){
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                if(isAirplaneModeOn){
                    eventManager.fire(Event.AIRPLANE_MODE_UP);
                } else {
                    eventManager.fire(Event.AIRPLANE_MODE_DOWN);
                }
            }
        }
    }
}
