package es.rafaco.inappdevtools.library.logic.info.reporters;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.logic.utils.RunningProcessesUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningProvidersUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningServicesUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningThreadsUtils;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LiveInfoReporter extends AbstractInfoReporter {

    public LiveInfoReporter(Context context) {
        this(context, InfoReport.LIVE);
    }

    public LiveInfoReporter(Context context, InfoReport report) {
        super(context, report);
    }

    @Override
    public String getOverview() {
        String result = RunningTasksUtils.getTopActivity() + " on " + RunningTasksUtils.getTopActivityStatus() + Humanizer.newLine()
                + RunningTasksUtils.getCount() + " tasks with " + RunningTasksUtils.getActivitiesCount() + " activities" + Humanizer.newLine()
                + RunningServicesUtils.getCount() + " services and " + RunningProvidersUtils.getCount() + " providers" + Humanizer.newLine()
                + RunningProcessesUtils.getCount() + " processes with " + RunningThreadsUtils.getCount() + " threads";
        return result;
    }

    @Override
    public InfoReportData getData() {
        return new InfoReportData.Builder(getReport())
                .setOverview(getOverview())
                .add(getActivityInfo())
                .add(getTaskInfo())
                .add(getServicesInfo())
                .add(getProvidersInfo())
                .add(getProcessesInfo())
                .add(getThreadsInfo())
                .add(getMemoryInfo())
                .build();
    }

    public InfoGroupData getActivityInfo() {
        return new InfoGroupData.Builder("View")
                .setIcon(R.string.gmd_view_carousel)
                .setOverview(RunningTasksUtils.getTopActivity())
                .add("App on " + RunningTasksUtils.getTopActivityStatus())
                .add("Top activity is " + RunningTasksUtils.getTopActivity())
                .build();
    }

    public InfoGroupData getTaskInfo() {
        return new InfoGroupData.Builder("Tasks")
                .setIcon(R.string.gmd_layers)
                .setOverview(RunningTasksUtils.getCount() + "")
                .add(RunningTasksUtils.getString())
                .build();
    }

    public InfoGroupData getServicesInfo() {
        return new InfoGroupData.Builder("Services")
                .setIcon(R.string.gmd_store)
                .setOverview(RunningServicesUtils.getCount() + "")
                .add(RunningServicesUtils.getString())
                .build();
    }

    public InfoGroupData getProvidersInfo() {
        return new InfoGroupData.Builder("Provider")
                .setIcon(R.string.gmd_local_convenience_store)
                .setOverview(RunningProvidersUtils.getCount() + "")
                .add(RunningProvidersUtils.getString())
                .build();
    }

    public InfoGroupData getProcessesInfo() {
        return new InfoGroupData.Builder("Processes")
                .setIcon(R.string.gmd_developer_board)
                .setOverview(RunningProcessesUtils.getCount() + "")
                .add(RunningProcessesUtils.getString())
                .build();
    }

    public InfoGroupData getThreadsInfo() {
        return new InfoGroupData.Builder("Threads")
                .setIcon(R.string.gmd_line_style)
                .setOverview(RunningThreadsUtils.getCount() + "")
                .add(RunningThreadsUtils.getString())
                .build();
    }

    public InfoGroupData getMemoryInfo() {
        return new InfoGroupData.Builder("Memory")
                .setIcon(R.string.gmd_memory)
                .add(getRunningMemory())
                .build();
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
        //String nativeHeapAllocatedSize = OSInfoReporter.humanReadableByteCount(Debug.getNativeHeapAllocatedSize(), true);
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

}
