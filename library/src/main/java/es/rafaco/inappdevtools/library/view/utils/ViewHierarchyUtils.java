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

import es.rafaco.inappdevtools.library.DevTools;

public class ViewHierarchyUtils {

    public static void logRootViews() {
        List<Pair<String, View>> rootViews = getRootViews(true);

        if(rootViews != null){
            for (Pair<String, View> rootView : rootViews){
                printViewHierarchy(rootView.second);
            }
        }
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
                Log.i(DevTools.TAG, "Found root view: " + viewName + ": " + rootView);
                rootViews.add(new Pair<>(viewName, rootView));

                if(print)
                    printViewHierarchy(rootView);
            }
            if (rootViews.size()==0)
                rootViews = null;
        } catch (Exception e) {
            e.printStackTrace();
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
            Log.d(DevTools.TAG, "TextView from hierarchy dumped: " + view.toString() + " with text: " + ((TextView) view).getText().toString() + " ,in Level: " + level);
        } else {
            Log.d(DevTools.TAG, "View from hierarchy dumped: " + view.toString() + " ,in Level: " + level);
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
