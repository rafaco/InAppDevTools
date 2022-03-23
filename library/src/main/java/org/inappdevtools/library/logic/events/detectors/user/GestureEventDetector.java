/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inappdevtools.library.logic.events.detectors.user;

import android.view.GestureDetector;
import android.view.MotionEvent;

import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class GestureEventDetector extends EventDetector {

    private GestureDetector mReceiver;

    public GestureEventDetector(EventManager manager) {
        super(manager);
        mReceiver = new GestureDetector(getContext(), new InnerReceiver());
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.GESTURE_SINGLE_TAP, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Touch", "SingleTap",
                        "User touch - onSingleTapConfirmed: " + param);
            }
        });

        eventManager.subscribe(Event.GESTURE_CONTEXT_TAP, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Touch", "ContextClick",
                        "User touch - onContextClick: " + param);
            }
        });

        eventManager.subscribe(Event.GESTURE_DOUBLE_TAP, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Touch", "DoubleTap",
                        "User touch - onDoubleTap: " + param);
            }
        });

        eventManager.subscribe(Event.GESTURE_LONG_PRESSED, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Touch", "LongPress",
                        "User touch - onLongPress: " + param);
            }
        });

        eventManager.subscribe(Event.GESTURE_FLING_TAP, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Touch", "Fling", (String)param);
            }
        });
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
            //FriendlyLog.log("W", "Touch", "Scroll", "User touch - onScroll");
            return false;
        }

        //endregion
    }
}
