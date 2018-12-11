package es.rafaco.inappdevtools.library.logic.watcher;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class GestureWatcher extends Watcher {

    private InnerListener mListener;
    private GestureDetector mReceiver;


    public GestureWatcher(Context context) {
        super(context);
        mReceiver = new GestureDetector(context, new InnerReceiver());
    }

    @Override
    public void setListener(Object listener) {
        mListener = (InnerListener) listener;
    }

    @Override
    public void start() {
        //TODO?
    }

    @Override
    public void stop() {
        //TODO?
    }

    public GestureDetector getDetector() {
        return mReceiver;
    }


    class InnerReceiver extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            FriendlyLog.log("W", "UserTouch", "SingleTap", "User touch - onSingleTapConfirmed: " + e.toString());
            if (mListener!=null) mListener.onSingleTap("");
            return false;
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            //TODO: What is this? is not working
            FriendlyLog.log("W", "UserTouch", "ContextClick", "User touch - onContextClick: " + e.toString());
            if (mListener!=null) mListener.onContextClick("");
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            FriendlyLog.log("W", "UserTouch", "DoubleTap", "User touch - onDoubleTap: " + e.toString());
            if (mListener!=null) mListener.onDoubleTap("");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            FriendlyLog.log("W", "UserTouch", "LongPress", "User touch - onLongPress: " + e.toString());
            if (mListener!=null) mListener.onLongPress("");
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            FriendlyLog.log("W", "UserTouch", "Fling", "User touch - onFling");
            if (mListener!=null) mListener.onFling("");
            return false;
        }


        //region [ NOT USED ]

        @Override
        public boolean onDown(MotionEvent event) {
            //Notified when a tap occurs with the down {@link MotionEvent}
            //that triggered it. This will be triggered immediately for
            //every down event. All other events should be preceded by this.
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            //The user has performed a down and not performed a move or up yet.
            //This event is commonly used to provide visual feedback to the user to let them know
            // that their action has been recognized i.e. highlight an element.
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Notified when a tap occurs with the up, without waiting to detect double tap.
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            //Notified when an event within a double-tap gesture occurs, including
            //the down, move, and up events.
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //FriendlyLog.log("W", "UserTouch", "Scroll", "User touch - onScroll");
            return false;
        }

        //endregion
    }

    public interface InnerListener {
        boolean onSingleTap(String info);
        boolean onContextClick(String info);
        boolean onDoubleTap(String info);
        boolean onFling(String info);
        void onLongPress(String info);
    }
}