package es.rafaco.inappdevtools.library.logic.events.detectors.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class ScreenChangeEventDetector extends EventDetector {

    private IntentFilter mFilter;
    private InnerReceiver mReceiver;

    public boolean isScreenOn = true;

    public ScreenChangeEventDetector(EventManager manager) {
        super(manager);

        mFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mFilter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.SCREEN_OFF, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Device", "ScreenOff", "Screen went OFF");
            }
        });

        eventManager.subscribe(Event.SCREEN_ON, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Device", "ScreenOn", "Screen went ON");
            }
        });

        eventManager.subscribe(Event.USER_PRESENT, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Device", "UserPresent", "User is now present");
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
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOn = false;
                eventManager.fire(Event.SCREEN_OFF);
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOn = true;
                eventManager.fire(Event.SCREEN_ON);
            }
            else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
                eventManager.fire(Event.USER_PRESENT);
            }
        }
    }
}
