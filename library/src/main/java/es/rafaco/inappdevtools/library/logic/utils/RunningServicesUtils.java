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

package es.rafaco.inappdevtools.library.logic.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RunningServicesUtils {

    private RunningServicesUtils() { throw new IllegalStateException("Utility class"); }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static int getCount() {
        List<ActivityManager.RunningServiceInfo> services = getList();
        return services.size();
    }

    public static String getString() {
        String result = Humanizer.newLine();

        List<ActivityManager.RunningServiceInfo> services = getList();
        for(ActivityManager.RunningServiceInfo service : services){
            result += getServiceString(service);
        }
        return result;
    }

    private static List<ActivityManager.RunningServiceInfo> getList() {
        List<ActivityManager.RunningServiceInfo> services = new ArrayList<>();

        String packageName = getContext().getPackageName();
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (info.service.getPackageName().equals(packageName)) {
                services.add(info);
            }
        }
        return services;
    }

    private static String getServiceString(ActivityManager.RunningServiceInfo info) {
        String className = info.service.getShortClassName();
        String name = className.substring(className.lastIndexOf(".")+1);
        long startTimeMillis = Calendar.getInstance().getTimeInMillis() - info.activeSince;
        String elapsed = Humanizer.getElapsedTimeLowered(startTimeMillis);
        String date = DateUtils.format(startTimeMillis);
        String result = name + ". " + info.crashCount + " crashes since " + elapsed;// + " (" + date + ")";
        return result + Humanizer.newLine();
    }

    public static String getRunningServices(int pid) {
        String result = Humanizer.newLine();

        String packageName = getContext().getPackageName();
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (info.service.getPackageName().equals(packageName) && info.pid==pid) {
                result += getServiceString(info);
            }
        }
        return result;
    }
}
