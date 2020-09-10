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

package es.rafaco.inappdevtools.library.logic.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import java.util.Iterator;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RunningBroadcastReceiversUtils {

    private RunningBroadcastReceiversUtils() { throw new IllegalStateException("Utility class"); }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static int getCount() {
        List<ResolveInfo> providers = getList();
        return providers.size();
    }

    public static List<ResolveInfo> getList() {
        List<ResolveInfo> result;
        String packageName = getContext().getPackageName();
        PackageManager packageManager = getContext().getPackageManager();
        Intent intent = new Intent();
        intent.setPackage(packageName);
        result = packageManager.queryBroadcastReceivers(intent, PackageManager.GET_RESOLVED_FILTER);
        return result;
    }

    public static String getTitle(ResolveInfo info) {
        return Humanizer.getLastPart(info.activityInfo.name, ".");
    }

    public static String getClassName(ResolveInfo info) {
        return info.activityInfo.name;
    }

    public static String getContent(ResolveInfo resolveInfo) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        //TODO: resolveInfo

        StringBuilder contentBuffer = new StringBuilder();

        contentBuffer.append("Exported: " + activityInfo.exported);
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Name: " + activityInfo.name);
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Actions:");
        Iterator<String> actionsIterator = resolveInfo.filter.actionsIterator();
        while(actionsIterator.hasNext()) {
            contentBuffer.append(Humanizer.newLine());
            contentBuffer.append("  - " + actionsIterator.next());
        }
        contentBuffer.append(Humanizer.newLine());

        if (activityInfo.metaData!=null) {
            Bundle bundle = activityInfo.metaData;
            contentBuffer.append("MetaData: ");
            for (String key: bundle.keySet()) {
                contentBuffer.append(Humanizer.newLine());
                contentBuffer.append(key + ": " + bundle.get(key));
            }
            contentBuffer.append(Humanizer.newLine());
        }

        return contentBuffer.toString();
    }
}
