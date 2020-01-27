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
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;

public class ViewHierarchyUtils {

    private ViewHierarchyUtils() { throw new IllegalStateException("Utility class"); }

    public static void logRootViews() {
        List<Pair<String, View>> rootViews = getRootViews(true);

        if(rootViews != null){
            for (Pair<String, View> rootView : rootViews){
                printViewHierarchy(rootView.second);
            }
        }
    }

    public static Pair<String, View> getAppRootView() {
        List<Pair<String, View>> rootViews = getRootViews(false);
        //Exclude our overlay windows
        for (Pair<String, View> root : rootViews) {
            //TODO: we return the first one from the app
            //We could also use view tags to identify our windows
            if (!(root.second.getContext() instanceof OverlayService)){
                return root;
            }
        }
        return null;
    }

    public static List<Pair<String, View>> getRootViews(Boolean print) {
        List<Pair<String, View>> rootViews;
        try {
            rootViews = new ArrayList<>();
            Class wmgClass = Class.forName("android.view.WindowManagerGlobal");
            Object wmgInstnace = wmgClass.getMethod("getInstance").invoke(null, (Object[])null);

            Method getViewRootNames = wmgClass.getMethod("getViewRootNames");
            Method getRootView = wmgClass.getMethod("getRootView", String.class);
            String[] rootViewNames = (String[])getViewRootNames.invoke(wmgInstnace, (Object[])null);

            for(String viewName : rootViewNames) {
                View rootView = (View)getRootView.invoke(wmgInstnace, viewName);
                //Log.i(Iadt.TAG, "Found root view: " + viewName + ": " + rootView);
                rootViews.add(new Pair<>(viewName, rootView));

                if(print)
                    printViewHierarchy(rootView);
            }
            if (rootViews.isEmpty())
                rootViews = null;
        } catch (Exception e) {
            FriendlyLog.logException("Exception at  getRootViews", e);
            rootViews = null;
        }
        return rootViews;
    }

    //Functions to get hierarchy
    private static void printViewHierarchy(View view) {
        //This is how I start recursion to get view hierarchy
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            dumpViewHierarchyWithProperties(group, 0);
        } else {
            dumpViewWithProperties(view, 0);
        }
    }

    private static void dumpViewHierarchyWithProperties(ViewGroup group, int level) {
        //TODO: Is this needed? (it was a pointless bugout highlighted by sonar)
        dumpViewWithProperties(group, level);

        final int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = group.getChildAt(i);
            if (view instanceof ViewGroup) {
                dumpViewHierarchyWithProperties((ViewGroup) view, level + 1);
            } else {
                dumpViewWithProperties(view, level + 1);
            }
        }
    }

    private static boolean dumpViewWithProperties(View view, int level) {
        //Add to view Hierarchy.
        if (view instanceof TextView) {
            Log.d(Iadt.TAG, "TextView from hierarchy dumped: " + view.toString() + " with text: " + ((TextView) view).getText().toString() + " ,in Level: " + level);
        } else {
            Log.d(Iadt.TAG, "View from hierarchy dumped: " + view.toString() + " ,in Level: " + level);
        }
        return true;
    }

    public static String getWindowName(View root) {
        return  root.getClass().getName() + '@' + Integer.toHexString(root.hashCode());
    }

    public static String getActivityNameFromRootView(View selectedView) {
        WindowManager.LayoutParams wlp = (WindowManager.LayoutParams) selectedView.getLayoutParams();
        String windowTitle = wlp.getTitle().toString();
        String activityName = windowTitle.substring(windowTitle.lastIndexOf('.') + 1);
        //TODO: fallback with another extractor

        return activityName;
    }
}
