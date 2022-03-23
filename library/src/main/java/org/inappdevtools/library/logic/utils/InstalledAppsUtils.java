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

package org.inappdevtools.library.logic.utils;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.ApplicationInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;
import org.inappdevtools.library.view.utils.Humanizer;

public class InstalledAppsUtils {

    private InstalledAppsUtils() { throw new IllegalStateException("Utility class"); }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static int getCount() {
        return getList().size();
    }

    public static String getString(){
        String result = "";
        List<ActivityInfo> apps = getList();

        for(ActivityInfo app : apps){
            String packageName = app.applicationInfo.packageName;
            result += getAppName(packageName) + ": " + packageName + Humanizer.newLine();
        }

        return result;
    }

    public static List<ActivityInfo> getList(){
        List<ActivityInfo> result = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );

        List<ResolveInfo> resolveInfoList = getContext().getPackageManager().queryIntentActivities(intent,0);
        for(ResolveInfo resolveInfo : resolveInfoList){
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if(!isSystemPackage(resolveInfo)){
                result.add(activityInfo);
            }
        }

        return result;
    }

    public static boolean isSystemPackage(ResolveInfo resolveInfo){
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static Drawable getAppIconByPackageName(String ApkTempPackageName){
        Drawable drawable;

        try{
            drawable = getContext().getPackageManager().getApplicationIcon(ApkTempPackageName);
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_help_outline_white_24dp);
        }
        return drawable;
    }

    public static String getAppName(String ApkPackageName){
        String Name = "";
        ApplicationInfo applicationInfo;
        PackageManager packageManager = getContext().getPackageManager();

        try {
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0);
            if(applicationInfo!=null){
                Name = (String)packageManager.getApplicationLabel(applicationInfo);
            }

        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Name;
    }
}
