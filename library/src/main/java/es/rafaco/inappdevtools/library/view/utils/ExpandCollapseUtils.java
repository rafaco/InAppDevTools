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

package es.rafaco.inappdevtools.library.view.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import es.rafaco.inappdevtools.library.Iadt;

public class ExpandCollapseUtils {

    private ExpandCollapseUtils() { throw new IllegalStateException("Utility class"); }


    public static void start(final View view, final Runnable midCallback, final Runnable endCallback){

        final Animation.AnimationListener expandListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Intentionally empty
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (endCallback != null)
                    endCallback.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //Intentionally empty
            }
        };

        Animation.AnimationListener collapseListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Intentionally empty
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (midCallback != null)
                    midCallback.run();
                expand(view, expandListener);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //Intentionally empty
            }
        };

        collapse(view, collapseListener);
    }

    public static void collapse(final View v, Animation.AnimationListener listener) {
        if (v==null){
            Log.d(Iadt.TAG, "Unable to collapse an empty view");
            listener.onAnimationEnd(null);
            return;
        }

        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        if (listener != null){
            a.setAnimationListener(listener);
        }
        v.startAnimation(a);
    }

    public static void expand(final View v, Animation.AnimationListener listener) {
        if (v==null){
            Log.d(Iadt.TAG, "Unable to expand an empty view");
            listener.onAnimationEnd(null);
            return;
        }

        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        if (listener != null){
            a.setAnimationListener(listener);
        }
        v.startAnimation(a);
    }
}
