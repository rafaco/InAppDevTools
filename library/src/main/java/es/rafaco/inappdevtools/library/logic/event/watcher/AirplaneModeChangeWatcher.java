package es.rafaco.inappdevtools.library.logic.event.watcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import es.rafaco.inappdevtools.library.logic.event.Event;
import es.rafaco.inappdevtools.library.logic.event.EventManager;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class AirplaneModeChangeWatcher extends Watcher {

    private IntentFilter mFilter;
    private InnerReceiver mReceiver;


    public AirplaneModeChangeWatcher(EventManager manager) {
        super(manager);
        mFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void init() {
        eventManager.subscribe(Event.CONNECTIVITY_UP, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Network", "AirplaneOn", "Airplane mode connected");
            }
        });

        eventManager.subscribe(Event.CONNECTIVITY_UP, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Network", "AirplaneOff", "Airplane mode disconnected");
            }
        });
    }

    @Override
    public boolean onlyForeground() {
        return true;
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
