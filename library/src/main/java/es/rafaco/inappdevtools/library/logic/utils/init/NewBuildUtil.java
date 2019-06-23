package es.rafaco.inappdevtools.library.logic.utils.init;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class NewBuildUtil {

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

        long lastBuildTime = DevToolsPrefs.getLong(PREF_KEY, -1);
        long currentBuildTime = Iadt.getConfig().getLong(Config.BUILD_TIME);

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
        DevToolsPrefs.setLong(PREF_KEY, buildTimeOnMemory);
    }
}
