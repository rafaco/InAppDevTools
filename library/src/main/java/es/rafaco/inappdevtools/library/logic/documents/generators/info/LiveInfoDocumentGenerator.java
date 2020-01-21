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

package es.rafaco.inappdevtools.library.logic.documents.generators.info;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Process;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.Document;
import es.rafaco.inappdevtools.library.logic.events.detectors.device.OrientationEventDetector;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.RunningProcessesUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningProvidersUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningServicesUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningThreadsUtils;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.InspectViewScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LiveInfoDocumentGenerator extends AbstractDocumentGenerator {

    private final long sessionId;

    public LiveInfoDocumentGenerator(Context context, Document report, long param) {
        super(context, report, param);
        this.sessionId = param;
    }

    @Override
    public String getTitle() {
        return getDocument().getName() + " Info from Session " + sessionId;
    }

    @Override
    public String getSubfolder() {
        return "session/" + sessionId;
    }

    @Override
    public String getFilename() {
        return "info_" + getDocument().getName().toLowerCase() + "_" + sessionId + ".txt";
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
    public DocumentData getData() {
        return new DocumentData.Builder(getTitle())
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

    public DocumentSectionData getActivityInfo() {
        DocumentSectionData.Builder builder = new DocumentSectionData.Builder("View")
                .setIcon(R.string.gmd_view_carousel)
                .setOverview(RunningTasksUtils.getTopActivity())
                .add("App on " + RunningTasksUtils.getTopActivityStatus())
                .add("Orientation is " + OrientationEventDetector.getOrientationString())
                .add()
                .add("Top activity is " + RunningTasksUtils.getTopActivity())
                .add(RunningTasksUtils.getTopActivityInfo());

        //TODO: when multiple buttons supported -> Add inspect source
        builder.addButton(new RunButton("Inspect View",
                new Runnable() {
                        @Override
                        public void run() { OverlayService.performNavigation(InspectViewScreen.class);
                        }
                    }));
        return builder.build();
    }

    public DocumentSectionData getTaskInfo() {
        return new DocumentSectionData.Builder("Tasks")
                .setIcon(R.string.gmd_layers)
                .setOverview(RunningTasksUtils.getCount() + "")
                .add(RunningTasksUtils.getString())
                .build();
    }

    public DocumentSectionData getServicesInfo() {
        return new DocumentSectionData.Builder("Services")
                .setIcon(R.string.gmd_store)
                .setOverview(RunningServicesUtils.getCount() + "")
                .add(RunningServicesUtils.getString())
                .build();
    }

    public DocumentSectionData getProvidersInfo() {
        return new DocumentSectionData.Builder("Provider")
                .setIcon(R.string.gmd_local_convenience_store)
                .setOverview(RunningProvidersUtils.getCount() + "")
                .add(RunningProvidersUtils.getString())
                .build();
    }

    @SuppressLint("NewApi")
    public DocumentSectionData getProcessesInfo() {
        return new DocumentSectionData.Builder("Processes")
                .setIcon(R.string.gmd_developer_board)
                .setOverview(RunningProcessesUtils.getCount() + "")
                .add("myPid", Process.myPid())
                .add("myTid", Process.myTid())
                .add("myUid", Process.myUid())
                .add(RunningProcessesUtils.getString())
                .build();
    }

    public DocumentSectionData getThreadsInfo() {
        return new DocumentSectionData.Builder("Threads")
                .setIcon(R.string.gmd_line_style)
                .setOverview(RunningThreadsUtils.getCount() + "")
                .add(RunningThreadsUtils.getString())
                .build();
    }

    public DocumentSectionData getMemoryInfo() {
        return new DocumentSectionData.Builder("Memory")
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
        //String nativeHeapAllocatedSize = OSInfoDocumentGenerator.humanReadableByteCount(Debug.getNativeHeapAllocatedSize(), true);
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
