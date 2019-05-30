package es.rafaco.inappdevtools.library.logic.event.watcher;

import android.view.GestureDetector;
import android.view.MotionEvent;

import es.rafaco.inappdevtools.library.logic.event.Event;
import es.rafaco.inappdevtools.library.logic.event.EventManager;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class GestureWatcher extends Watcher {

    private GestureDetector mReceiver;

    public GestureWatcher(EventManager manager) {
        super(manager);
        mReceiver = new GestureDetector(getContext(), new InnerReceiver());
    }

    @Override
    public void init() {
        eventManager.subscribe(Event.GESTURE_SINGLE_TAP, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "UserTouch", "SingleTap",
                        "User touch - onSingleTapConfirmed: " + param);
            }
        });

        eventManager.subscribe(Event.GESTURE_CONTEXT_TAP, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "UserTouch", "ContextClick",
                        "User touch - onContextClick: " + param);
            }
        });

        eventManager.subscribe(Event.GESTURE_DOUBLE_TAP, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "UserTouch", "DoubleTap",
                        "User touch - onDoubleTap: " + param);
            }
        });

        eventManager.subscribe(Event.GESTURE_LONG_PRESSED, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "UserTouch", "LongPress",
                        "User touch - onLongPress: " + param);
            }
        });

        eventManager.subscribe(Event.GESTURE_FLING_TAP, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "UserTouch", "Fling", (String)param);
            }
        });
    }

    @Override
    public boolean onlyForeground() {
        return true;
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

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        private static final int FLING_THRESHOLD = 200;
        private static final int FLING_VELOCITY_THRESHOLD = 200;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            eventManager.fire(Event.GESTURE_SINGLE_TAP, e.toString());
            return false;
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            eventManager.fire(Event.GESTURE_CONTEXT_TAP, e.toString());
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            eventManager.fire(Event.GESTURE_DOUBLE_TAP, e.toString());
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            eventManager.fire(Event.GESTURE_LONG_PRESSED, e.toString());
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();

            String extra = String.format("From %s,%s to %s,%s at %s,%s -> %s,%s",
                        e1.getX(), e1.getY(),
                        e2.getX(), e2.getY(),
                        velocityX, velocityY,
                        diffX, diffY);

            String message = "";

            try {
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    message = "Horizontal ";
                    if (Math.abs(diffX) > FLING_THRESHOLD && Math.abs(velocityX) > FLING_VELOCITY_THRESHOLD) {
                        message += "fling ";
                    } else if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        message += "swipe ";
                    }else{
                        message += "drag ";
                    }

                    if (diffX > 0) {
                        message += "to right";
                    } else {
                        message += "to left";
                    }
                }
                else{
                    message = "Vertical ";
                    if (Math.abs(diffY) > FLING_THRESHOLD && Math.abs(velocityY) > FLING_VELOCITY_THRESHOLD) {
                        message += "fling ";
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        message += "swipe ";
                    }else{
                        message += "drag ";
                    }

                    if (diffY > 0) {
                        message += "to bottom";
                    } else {
                        message += "to top";
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            message = "User touch: " + message;

            eventManager.fire(Event.GESTURE_FLING_TAP, message + " : " + extra);

            return false;
        }


        //region [ NOT CURRENTLY USED ]

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
}
