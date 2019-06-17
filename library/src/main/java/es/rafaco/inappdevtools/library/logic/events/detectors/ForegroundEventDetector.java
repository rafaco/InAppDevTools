package es.rafaco.inappdevtools.library.logic.events.detectors;

import android.os.Handler;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class ForegroundEventDetector extends EventDetector {

    private boolean mInBackground = true;
    private static final long BACKGROUND_DELAY = 500;
    private final Handler mBackgroundDelayHandler = new Handler();
    private Runnable mBackgroundTransition;

    public ForegroundEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.ACTIVITY_ON_RESUME, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                updateOnActivityResumed();
            }
        });
        eventManager.subscribe(Event.ACTIVITY_ON_PAUSE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                updateOnActivityPaused();
            }
        });

        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I","App", "Foreground",
                        "App to foreground");
            }
        });
        eventManager.subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I","App", "Background",
                        "App to background");
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private void updateOnActivityResumed() {
        if (mBackgroundTransition != null) {
            mBackgroundDelayHandler.removeCallbacks(mBackgroundTransition);
            mBackgroundTransition = null;
        }

        if (mInBackground) {
            mInBackground = false;
            eventManager.fire(Event.IMPORTANCE_FOREGROUND);
        }
    }

    private void updateOnActivityPaused() {
        if (!mInBackground && mBackgroundTransition == null) {
            mBackgroundTransition = new Runnable() {
                @Override
                public void run() {
                    mInBackground = true;
                    eventManager.fire(Event.IMPORTANCE_BACKGROUND);
                    mBackgroundTransition = null;
                }
            };
            mBackgroundDelayHandler.postDelayed(mBackgroundTransition, BACKGROUND_DELAY);
        }
    }
}
