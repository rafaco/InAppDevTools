package es.rafaco.inappdevtools.library.logic.initialization;

import android.content.Context;
import android.content.SharedPreferences;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfig;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfigFields;

public class NewBuildUtil {

    public static final String SHARED_PREFS_KEY = "inappdevtools";
    public static final String PREF_KEY = "LAST_BUILD_TIME";
    public static Boolean isNewBuildOnMemory;
    public static Long buildTimeOnMemory;

    public static boolean isNewBuild(){
        if (isNewBuildOnMemory == null){
            update();
        }
        return isNewBuildOnMemory;
    }

    public static long getBuildTime(){
        if (buildTimeOnMemory == null){
            update();
        }
        return buildTimeOnMemory;
    }



    private static void update(){

        Context context = DevTools.getAppContext();
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        long lastBuildTime = prefs.getLong(PREF_KEY, -1);
        CompileConfig buildConfig = new CompileConfig(context);
        long currentBuildTime = buildConfig.getLong(CompileConfigFields.BUILD_TIME);

        if (lastBuildTime<0){
            //First start
            isNewBuildOnMemory = false;
            buildTimeOnMemory = currentBuildTime;
            storeBuildTime();
        }
        else if (lastBuildTime == currentBuildTime) {
            isNewBuildOnMemory = false;
            buildTimeOnMemory = currentBuildTime;
        }
        else{
            isNewBuildOnMemory = true;
            buildTimeOnMemory = currentBuildTime;
            storeBuildTime();
        }
    }

    private static void storeBuildTime(){
        Context context = DevTools.getAppContext();
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        prefs.edit().putLong(PREF_KEY, buildTimeOnMemory).apply();
    }
}
