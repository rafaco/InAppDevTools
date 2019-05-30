package es.rafaco.inappdevtools.library.logic.event.watcher;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.event.Event;
import es.rafaco.inappdevtools.library.logic.event.EventManager;

public class ActivityTouchWatcher extends Watcher {

    public ActivityTouchWatcher(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void init() {
        eventManager.subscribe(Event.ACTIVITY_ON_RESUME, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                addTouchListener((Activity) param);
            }
        });
        eventManager.subscribe(Event.ACTIVITY_ON_PAUSE, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                removeTouchListener((Activity) param);
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private void addTouchListener(final Activity activity) {
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        if (decorView == null){
            Log.d(DevTools.TAG, "Resumed activity without decorView");
            return;
        }
        decorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(DevTools.TAG, "Click X:" + event.getX() + " Y:" + event.getY() + " at " + activity.getClass().getSimpleName() +
                        " - " + v.getClass().getSimpleName() + ": " + getResourceName(v, activity));
                return false;
            }
        });
    }

    private void removeTouchListener(Activity activity) {
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        if (decorView == null){
            Log.d(DevTools.TAG, "Paused activity without decorView");
            return;
        }
        decorView.setOnTouchListener(null);
    }

    private String getResourceName(View v, Activity activity) {
        try{
            return activity.getResources().getResourceName(v.getId());
        }catch (Resources.NotFoundException e){
            return "[ not set ]";
        }
    }
}
