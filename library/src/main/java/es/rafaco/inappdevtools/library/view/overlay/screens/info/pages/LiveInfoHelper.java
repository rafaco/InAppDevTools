package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Debug;
import android.util.Log;

//#ifdef MODERN
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.StopWatch;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LiveInfoHelper extends AbstractInfoHelper {

    public LiveInfoHelper(Context context) {
        super(context);
    }

    @Override
    public String getOverview() {
        ActivityEventDetector activityWatcher = (ActivityEventDetector) Iadt.getEventDetector(ActivityEventDetector.class);
        String result = getTopActivity() + " on " + (activityWatcher.isInBackground() ? "Background" : "Foreground");
        return result;
    }

    @Override
    public InfoReport getInfoReport() {
        return new InfoReport.Builder("")
                .add(getActivityInfo())
                .add()
                .add(getRunningInfo())
                .build();
    }

    public InfoGroup getActivityInfo() {
        ActivityEventDetector activityWatcher = (ActivityEventDetector) Iadt.getEventDetector(ActivityEventDetector.class);
        return new InfoGroup.Builder("")
                .add("App on " + (activityWatcher.isInBackground() ? "Background" : "Foreground"))
                .add("Top activity is " + getTopActivity())
                .build();
    }

    public InfoGroup getRunningInfo() {
        return new InfoGroup.Builder("")
                .add("Tasks", getRunningTasks())
                .add("Services", getRunningServices())
                .add("Providers", getRunningProviders())
                .add("Memory", getRunningMemory())
                .add("Processes", getRunningProcesses())
                .add("Threads", getRunningThreads())
                .build();
    }

    public String getRunningThreads() {

        StringBuilder result = new StringBuilder(Humanizer.newLine());
        StopWatch counter = new StopWatch("getAllThreads()");
        Thread[] allThreads = ThreadUtils.getAllThreads();
        counter.step("Process result");

//        StopWatch counter2 = new StopWatch("getAllStacktraces()");
//        Set<Thread> allStacktraces = ThreadUtils.getAllStacktraces();
//        counter2.step("Process result");


        ThreadGroup previousGroup = null;
        int previousGroupStart = 0;
        int nullCount = 0;

        for (Thread info : allThreads) {
            if (info == null){
                //result.append("null" + Humanizer.newLine());
                nullCount++;
            }
            else{

                String currentName = (info.getThreadGroup()==null) ? "" : info.getThreadGroup().getName();
                if (previousGroup == null
                        || !previousGroup.getName().equals(currentName)) {
                    if (previousGroup != null){
                        result.insert(previousGroupStart,
                                Humanizer.newLine()
                                        + ThreadUtils.formatGroup(previousGroup)
                                        + (nullCount!=0 ? " (" + nullCount + " nulls)" : "")
                                        + Humanizer.newLine());
                        nullCount = 0;
                    }
                    previousGroup = info.getThreadGroup();
                    previousGroupStart = result.length();
                }

                result  .append(formatThreadId(info))
                        .append(" ")
                        .append(formatThreadDescription(info))
                        //.append(info.getName())
                        .append(" ")
                        .append(info.getState())
                        .append(Humanizer.newLine());
            }
        }

        if (previousGroup != null){
            result.insert(previousGroupStart,
                    Humanizer.newLine()
                            + ThreadUtils.formatGroup(previousGroup)
                            + (nullCount!=0 ? " (" + nullCount + " nulls)" : "")
                            + Humanizer.newLine());
        }

        Log.d(Iadt.TAG, counter.finish());
        return result.toString();
    }

    private String formatThreadId(Thread info){
        String id = String.valueOf(info.getId());
        while(id.length()<4){
            id = "  " + id;
        }
        return id;
    }

    private String formatThreadDescription(Thread info){
        String standard = info.toString();
        return standard.replaceFirst("Thread", "");
    }

    public String getRunningProcesses() {
        StringBuilder result = new StringBuilder("\n");
        String packageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : manager.getRunningAppProcesses()) {

            String importance = info.importance + " ";
            if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                importance += "Foreground";
            }else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
                importance += "Service";
            }
            else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                importance += "Cached";
            }
            else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE) {
                importance += "Gone";
            }
            else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                importance += "Cached";
            }
            result
                    .append(info.pid)
                    .append(" - ")
                    .append(info.processName)
                    .append("\n")
                    .append("  importance: ")
                    .append(importance)
                    //.append(" uid: ")
                    //.append(info.uid)
                    .append("\n");
            for (String pkg : info.pkgList) {
                result.append("  pkg: ").append(pkg).append("\n");
            }

            Debug.MemoryInfo[] processMemoryInfo;
            try {
                processMemoryInfo = manager.getProcessMemoryInfo(new int[]{info.pid});
                if (processMemoryInfo!=null && processMemoryInfo.length>0){
                    result.append(getMemoryInfoFormatted(processMemoryInfo[0]));
                }
            } catch (Exception e) {
                //TODO: research why is always null
                result.append("No Info " + e.getMessage()).append("\n");
            }

            //result.append(getRunningServices(info.pid)).append("\n");
            //result.append(getRunningProviders(info.processName, info.uid)).append("\n");
        }
        return result.toString();
    }

    public String getRunningProviders() {
        String result = "\n";
        String packageName = context.getPackageName();

        for (PackageInfo pack: context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)) {
            if (pack.packageName.equals(packageName)){
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo info: providers) {
                        result += info.authority + "\n";// + info.name  + "\n\n";
                    }
                }
            }
        }
        return result;
    }

    public String getRunningServices(int pid) {
        String result = "\n";

        String packageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (info.service.getPackageName().equals(packageName) && info.pid==pid) {
                result += getServiceString(info);
            }
        }
        return result;
    }

    public String getRunningServices() {
        String result = "\n";

        String packageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (info.service.getPackageName().equals(packageName)) {
                result += getServiceString(info);
            }
        }
        return result;
    }

    @NonNull
    private String getServiceString(ActivityManager.RunningServiceInfo info) {
        String className = info.service.getShortClassName();
        String name = className.substring(className.lastIndexOf(".")+1);
        long startTimeMillis = Calendar.getInstance().getTimeInMillis() - info.activeSince;
        String elapsed = Humanizer.getElapsedTimeLowered(startTimeMillis);
        String date = DateUtils.format(startTimeMillis);
        String result = name + ". " + info.crashCount + " crashes since " + elapsed;// + " (" + date + ")";
        return result + "\n";
    }

    private String getRunningTasks() {
        String output = "\n";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(10);
        Iterator<ActivityManager.RunningTaskInfo> itr = runningTaskInfoList.iterator();
        while(itr.hasNext()){
            ActivityManager.RunningTaskInfo runningTaskInfo = itr.next();
            int id = runningTaskInfo.id;
            int numOfActivities = runningTaskInfo.numActivities;
            String topActivity = runningTaskInfo.topActivity.getShortClassName();
            String text = String.valueOf(id) + " - " + topActivity + " top of " + numOfActivities;
            output += text + "\n";
        }
        return output;
    }

    private String getRunningMemory() {
        String output = "\n";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //output += "--> ActivityManager.memoryClass: max ram allowed per app" + "\n";
        int memoryClass = manager.getMemoryClass();
        output += String.format("  %s Mb allowed per app",
                memoryClass) + "\n";

        //output += "--> ActivityManager.memoryInfo: Total device memory" + "\n";
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        output += String.format("  Device: %s / %s total (%s threshold)%s",
                Humanizer.parseByte(memoryInfo.availMem),
                Humanizer.parseByte(memoryInfo.totalMem),
                Humanizer.parseByte(memoryInfo.threshold),
                memoryInfo.lowMemory? " LOW!" : ""
        ) + "\n";

        //output += "--> Runtime data: dalvik process" + "\n";
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        String totalMemory = Humanizer.humanReadableByteCount(runtime.totalMemory(), true);
        String freeMemory = Humanizer.humanReadableByteCount(runtime.freeMemory(), true);
        output += String.format("  Runtime: %s / %s (%s processors) ",
                freeMemory,
                totalMemory,
                processors) + "\n";

        //output += "--> Debug data: system wide" + "\n";
        String nativeHeapSize = Humanizer.humanReadableByteCount(Debug.getNativeHeapSize(), true);
        //String nativeHeapAllocatedSize = OSInfoHelper.humanReadableByteCount(Debug.getNativeHeapAllocatedSize(), true);
        String nativeHeapFreeSize = Humanizer.humanReadableByteCount(Debug.getNativeHeapFreeSize(), true);
        output += String.format("  NativeHeap: %s / %s", nativeHeapFreeSize, nativeHeapSize) + "\n";


        /*
        //output += " - Debug.MemoryInfo" + "\n";
        Debug.MemoryInfo debugMemoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(debugMemoryInfo);
        output += getMemoryInfoFormatted(debugMemoryInfo);
        */

        return output;
    }

    @NonNull
    private String getMemoryInfoFormatted(Debug.MemoryInfo debugMemoryInfo) {
        String result;
        result = String.format("  Dalvik: %s pss, %s shared, %s private",
                Humanizer.parseKb(debugMemoryInfo.dalvikPss),
                Humanizer.parseKb(debugMemoryInfo.dalvikSharedDirty),
                Humanizer.parseKb(debugMemoryInfo.dalvikPrivateDirty)
                ) + "\n";
        result += String.format("  Native: %s pss, %s shared, %s private",
                Humanizer.parseKb(debugMemoryInfo.nativePss),
                Humanizer.parseKb(debugMemoryInfo.nativeSharedDirty),
                Humanizer.parseKb(debugMemoryInfo.nativePrivateDirty)
                ) + "\n";
        result += String.format("  Other: %s pss, %s shared, %s private",
                Humanizer.parseKb(debugMemoryInfo.otherPss),
                Humanizer.parseKb(debugMemoryInfo.otherSharedDirty),
                Humanizer.parseKb(debugMemoryInfo.otherPrivateDirty)
                ) + "\n";
        return result;
    }

    private String getTopActivity() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo firstTaskInfo = runningTaskInfoList.get(0);
        String shortClassName = firstTaskInfo.topActivity.getShortClassName();
        int lastDot = shortClassName.lastIndexOf(".");
        if (lastDot > 0){
            return shortClassName.substring(lastDot + 1);
        }
        return shortClassName;
    }
}
