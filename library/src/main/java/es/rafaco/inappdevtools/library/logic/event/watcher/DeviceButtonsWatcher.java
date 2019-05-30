package es.rafaco.inappdevtools.library.logic.event.watcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.event.Event;
import es.rafaco.inappdevtools.library.logic.event.EventManager;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class DeviceButtonsWatcher extends Watcher {

    private IntentFilter mFilter;
    private InnerReceiver mReceiver;

    public DeviceButtonsWatcher(EventManager manager) {
        super(manager);

        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void init() {
        eventManager.subscribe(Event.DEVICE_HOME_PRESSED, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "HomeKey", "Pressed home button");
            }
        });

        eventManager.subscribe(Event.DEVICE_RECENT_PRESSED, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "RecentKey", "Pressed recent button");
            }
        });

        eventManager.subscribe(Event.DEVICE_DREAM_PRESSED, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "DreamKey", "Pressed off button");
            }
        });

        eventManager.subscribe(Event.DEVICE_UNKNOWN_PRESSED, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "UnknownKey", "Pressed Unknown button: " + param);
            }
        });
    }@Override
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
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String SYSTEM_DIALOG_REASON_DREAM_KEY = "dream";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    Log.v(DevTools.TAG, "DeviceButtonsWatcher - action:" + action + ", reason:" + reason);

                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        eventManager.fire(Event.DEVICE_HOME_PRESSED);
                    }
                    else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        eventManager.fire(Event.DEVICE_RECENT_PRESSED);
                    }
                    else if (reason.equals(SYSTEM_DIALOG_REASON_DREAM_KEY)) {
                        eventManager.fire(Event.DEVICE_DREAM_PRESSED);
                    }
                    else{
                        eventManager.fire(Event.DEVICE_UNKNOWN_PRESSED, reason);
                    }
                }
            }
        }
    }
}
