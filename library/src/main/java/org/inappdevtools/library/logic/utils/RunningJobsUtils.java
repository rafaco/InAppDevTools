/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.os.Build;

//#ifdef ANDROIDX
//@import androidx.annotation.RequiresApi;
//#else
import android.support.annotation.RequiresApi;
//#endif

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.view.utils.Humanizer;

public class RunningJobsUtils {

    private RunningJobsUtils() { throw new IllegalStateException("Utility class"); }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static int getCount() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return getList().size();
        }
        else{
            return 0;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<JobInfo> getList() {
        List<JobInfo> items = new ArrayList<>();
        JobScheduler scheduler = (JobScheduler) getContext().getSystemService( Context.JOB_SCHEDULER_SERVICE );
        String packageName = getContext().getPackageName();
        for (JobInfo jobInfo : scheduler.getAllPendingJobs() ) {
            if (jobInfo.getService().getPackageName().equals(packageName)) {
                items.add(jobInfo);
            }
        }
        return items;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String getServiceTitle(JobInfo info) {
        return info.toString() + " " + info.getService().toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String getServiceContent(JobInfo info) {
        StringBuilder contentBuffer = new StringBuilder();

        contentBuffer.append("Id: " + info.getId());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("IsPeriodic: " + info.isPeriodic());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("IsPersisted: " + info.isPersisted());
        contentBuffer.append(Humanizer.newLine());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            contentBuffer.append("IsImportantWhileForeground: " + info.isImportantWhileForeground());
            contentBuffer.append(Humanizer.newLine());
        }

        contentBuffer.append("IntervalMillis: " + info.getIntervalMillis());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Package: " + info.getService().getPackageName());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Class: " + info.getService().getClassName());
        contentBuffer.append(Humanizer.newLine());

        return contentBuffer.toString();
    }
}
