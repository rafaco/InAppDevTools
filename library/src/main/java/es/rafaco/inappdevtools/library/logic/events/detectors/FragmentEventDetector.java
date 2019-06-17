package es.rafaco.inappdevtools.library.logic.events.detectors;

import android.app.Activity;

//#ifdef MODERN
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.fragment.app.FragmentManager;
//#else
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
//#endif

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class FragmentEventDetector extends EventDetector {

    private SupportFragmentLifecycleCallbacks supportFragmentCallbacks;
    //private FragmentLifecycleCallbacks fragmentCallbacks;

    public FragmentEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.ACTIVITY_ON_CREATE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                registerFragmentLifecycleCallbacks((Activity)param);
            }
        });

        eventManager.subscribe(Event.ACTIVITY_ON_DESTROY, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                unregisterFragmentLifecycleCallbacks((Activity)param);
            }
        });
    }

    @Override
    public void start() {
        //Intentionally empty
    }

    @Override
    public void stop() {
        //Intentionally empty.
    }

    //region [ DETECTOR ]

    private void registerFragmentLifecycleCallbacks(Activity activity) {
        if (activity instanceof AppCompatActivity){
            if (supportFragmentCallbacks==null)
                this.supportFragmentCallbacks = new SupportFragmentLifecycleCallbacks();

            FragmentManager supportFragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            supportFragmentManager.registerFragmentLifecycleCallbacks(supportFragmentCallbacks, true);

            String message = "FragmentCallbacks registered: "
                    + activity.getClass().getSimpleName()
                    + " - " + supportFragmentManager.toString();
            FriendlyLog.log("D", "Fragment", "CallbacksRegistered", message);
        }
        else{
            FriendlyLog.log("W", "Fragment", "CallbacksRegistered", "Unregistered: not an AppCompatActivity");
        }

        //TODO
        /*else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (fragmentCallbacks==null)
                this.fragmentCallbacks = new FragmentLifecycleCallbacks();

            activity.getFragmentManager()
                    .registerFragmentLifecycleCallbacks(fragmentCallbacks, true);
        }*/
    }

    private void unregisterFragmentLifecycleCallbacks(Activity activity){
        if (activity instanceof AppCompatActivity){
            FragmentManager supportFragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            supportFragmentManager.unregisterFragmentLifecycleCallbacks(supportFragmentCallbacks);

            String message = "FragmentCallbacks unregistered: "
                    + activity.getClass().getSimpleName()
                    + " - " + supportFragmentManager.toString();
            FriendlyLog.log("V", "Fragment", "CallbacksUnregistered", message);
        }

        //TODO
        /*else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           activity.getFragmentManager()
                    .unregisterFragmentLifecycleCallbacks(fragmentCallbacks);
        }*/
    }

    //endregion
}
