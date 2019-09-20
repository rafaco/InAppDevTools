package es.rafaco.inappdevtools.library.storage.prefs.utils;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.config.BuildInfo;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class NewBuildUtil {

    public static final String PREF_VALUE_KEY = "LAST_BUILD_TIME";
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

        long lastBuildTime = DevToolsPrefs.getLong(PREF_VALUE_KEY, -1);
        JsonAssetHelper buildInfo = new JsonAssetHelper(IadtController.get().getContext(), JsonAsset.BUILD_INFO);
        long currentBuildTime = buildInfo.getLong(BuildInfo.BUILD_TIME);

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
        DevToolsPrefs.setLong(PREF_VALUE_KEY, buildTimeOnMemory);
    }
}
