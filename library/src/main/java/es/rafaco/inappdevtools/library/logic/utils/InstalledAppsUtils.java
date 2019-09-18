package es.rafaco.inappdevtools.library.logic.utils;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.ApplicationInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class InstalledAppsUtils {

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
